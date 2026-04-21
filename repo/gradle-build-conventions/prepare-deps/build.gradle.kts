@file:Suppress("PropertyName", "HasPlatformType", "UnstableApiUsage")

import org.gradle.internal.os.OperatingSystem
import java.io.Closeable
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.UncheckedIOException
import java.net.URI
import java.nio.file.Files
import java.nio.file.attribute.FileTime
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.xml.stream.XMLOutputFactory

plugins {
    base
    id("org.jetbrains.kotlin.jvm")
}

fun Project.getBooleanProperty(name: String): Boolean? = this.findProperty(name)?.let {
    val v = it.toString()
    if (v.isBlank()) true
    else v.toBoolean()
}

project.apply {
    from(rootProject.file("../../gradle/versions.gradle.kts"))
}

val intellijSeparateSdks by extra(project.getBooleanProperty("intellijSeparateSdks") ?: false)
val intellijReleaseType: String by extra {
    when {
        extra["versions.intellijSdk"]?.toString()?.contains("-EAP-") == true -> "snapshots"
        extra["versions.intellijSdk"]?.toString()?.endsWith("SNAPSHOT") == true -> "nightly"
        else -> "releases"
    }
}
val customDepsOrg: String by extra("kotlin.build")

val intellijVersion = project.extra["versions.intellijSdk"] as String
val intellijVersionForIde = rootProject.intellijSdkVersionForIde()
val asmVersion = project.findProperty("versions.jar.asm-all") as String?
val androidStudioRelease = project.findProperty("versions.androidStudioRelease") as String?
val androidStudioBuild = project.findProperty("versions.androidStudioBuild") as String?

fun checkIntellijVersion(intellijVersion: String) {
    val intellijVersionDelimiterIndex = intellijVersion.indexOfAny(charArrayOf('.', '-'))
    if (intellijVersionDelimiterIndex == -1) {
        error("Invalid IDEA version $intellijVersion")
    }
}
checkIntellijVersion(intellijVersion)
intellijVersionForIde?.let { checkIntellijVersion(it) }

logger.info("intellijVersion: $intellijVersion")
logger.info("intellijVersionForIde: $intellijVersionForIde")
logger.info("androidStudioRelease: $androidStudioRelease")
logger.info("androidStudioBuild: $androidStudioBuild")
logger.info("intellijSeparateSdks: $intellijSeparateSdks")

val androidStudioOs by lazy {
    when {
        OperatingSystem.current().isWindows -> "windows"
        OperatingSystem.current().isMacOsX -> "mac"
        OperatingSystem.current().isLinux -> "linux"
        else -> {
            logger.error("Unknown operating system for android tools: ${OperatingSystem.current().name}")
            ""
        }
    }
}

repositories {
    if (androidStudioRelease != null) {
        ivy {
            url = URI("https://dl.google.com/dl/android/studio/ide-zips/$androidStudioRelease")

            patternLayout {
                artifact("[artifact]-[revision]-$androidStudioOs.[ext]")
            }

            metadataSources {
                artifact()
            }
        }
    }

    maven("https://www.jetbrains.com/intellij-repository/$intellijReleaseType")
    maven("https://plugins.jetbrains.com/maven")
    maven("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies")
}

val intellij by configurations.creating
val intellijForIde by configurations.creating
val androidStudio by configurations.creating
val sources by configurations.creating
val sourcesForIde by configurations.creating
val jpsStandalone by configurations.creating
val jpsStandaloneForIde by configurations.creating
val intellijCore by configurations.creating
val intellijCoreForIde by configurations.creating

/**
 * Special repository for annotations.jar required for idea runtime only.
 *
 * See IntellijDependenciesKt.intellijRuntimeAnnotations for more details.
 */
val intellijRuntimeAnnotations = "intellij-runtime-annotations"

val dependenciesDir = (findProperty("kotlin.build.dependencies.dir") as String?)?.let(::File)
    ?: rootProject.gradle.gradleUserHomeDir.resolve("kotlin-build-dependencies")

val customDepsRepoDir = dependenciesDir.resolve("repo")

val repoDir = File(customDepsRepoDir, customDepsOrg)

dependencies {
    if (androidStudioRelease != null) {
        val extension = if (androidStudioOs == "linux")
            "tar.gz"
        else
            "zip"

        androidStudio("google:android-studio-ide:$androidStudioBuild@$extension")
    } else {
        intellij("com.jetbrains.intellij.idea:ideaIC:$intellijVersion")
        intellijVersionForIde?.let { intellijForIde("com.jetbrains.intellij.idea:ideaIC:$it") }
    }

    if (asmVersion != null) {
        sources("org.jetbrains.intellij.deps:asm-all:$asmVersion:sources@jar")
    }

    sources("com.jetbrains.intellij.idea:ideaIC:$intellijVersion:sources@jar")
    intellijVersionForIde?.let { sourcesForIde("com.jetbrains.intellij.idea:ideaIC:$it:sources@jar") }
    jpsStandalone("com.jetbrains.intellij.idea:jps-standalone:$intellijVersion")
    intellijVersionForIde?.let { jpsStandaloneForIde("com.jetbrains.intellij.idea:jps-standalone:$it") }
    intellijCore("com.jetbrains.intellij.idea:intellij-core:$intellijVersion")
    intellijVersionForIde?.let { intellijCoreForIde("com.jetbrains.intellij.idea:intellij-core:$it") }
}

private fun touchFileByWritingEmptyByteArray(file: File) {
    try {
        FileOutputStream(file).use { it.write(ByteArray(0)) }
    } catch (e: IOException) {
        throw UncheckedIOException("Could not update timestamp for $file", e)
    }
}

private fun touchExistingFile(file: File) {
    try {
        if (file.exists()) {
            Files.setLastModifiedTime(file.toPath(), FileTime.fromMillis(System.currentTimeMillis()))
        }
    } catch (e: IOException) {
        if (file.isFile && file.length() == 0L) {
            // On Linux, users cannot touch files they don't own but have write access to
            // because the JDK uses futimes() instead of futimens() [note the 'n'!]
            // see https://github.com/gradle/gradle/issues/7873
            touchFileByWritingEmptyByteArray(file)
        } else {
            throw UncheckedIOException("Could not update timestamp for $file", e)
        }
    }
}

tasks.named<Delete>("clean") {
    delete(customDepsRepoDir)
}

fun writeIvyXml(
    organization: String,
    moduleName: String,
    version: String,
    fileName: String,
    baseDir: File,
    artifactDir: File,
    targetDir: File,
    vararg sourcesJar: File,
    allowAnnotations: Boolean = false
) {
    fun shouldIncludeIntellijJar(jar: File) =
        jar.isFile
                && jar.extension == "jar"
                && !jar.name.startsWith("kotlin-")
                && (allowAnnotations || jar.name != "annotations.jar") // see comments for [intellijAnnotations] above

    val ivyFile = targetDir.resolve("$fileName.ivy.xml")
    ivyFile.parentFile.mkdirs()
    with(XMLWriter(ivyFile.writer())) {
        document("UTF-8", "1.0") {
            element("ivy-module") {
                attribute("version", "2.0")
                attribute("xmlns:m", "https://ant.apache.org/ivy/maven")

                emptyElement("info") {
                    attributes(
                        "organisation" to organization,
                        "module" to moduleName,
                        "revision" to version,
                        "publication" to SimpleDateFormat("yyyyMMddHHmmss").format(Date())
                    )
                }

                element("configurations") {
                    listOf("default", "sources").forEach { configurationName ->
                        emptyElement("conf") {
                            attributes("name" to configurationName, "visibility" to "public")
                        }
                    }
                }

                element("publications") {
                    artifactDir.listFiles()
                        ?.filter(::shouldIncludeIntellijJar)
                        ?.sortedBy { it.name.lowercase() }
                        ?.forEach { jarFile ->
                            val relativeName = jarFile.toRelativeString(baseDir).removeSuffix(".jar")
                            emptyElement("artifact") {
                                attributes(
                                    "name" to relativeName,
                                    "type" to "jar",
                                    "ext" to "jar",
                                    "conf" to "default"
                                )
                            }
                        }

                    sourcesJar.forEach { jarFile ->
                        emptyElement("artifact") {
                            val sourcesArtifactName = jarFile.name.substringBefore("-$version")
                            attributes(
                                "name" to sourcesArtifactName,
                                "type" to "jar",
                                "ext" to "jar",
                                "conf" to "sources",
                                "m:classifier" to "sources"
                            )
                        }
                    }
                }
            }
        }

        close()
    }
}

fun skipToplevelDirectory(path: String) = path.substringAfter('/')

fun skipContentsDirectory(path: String) = path.substringAfter("Contents/")

fun Project.intellijSdkVersionForIde(): String? {
    val majorVersion = kotlinBuildProperties.stringProperty("attachedIntellijVersion").orNull ?: return null
    return rootProject.findProperty("versions.intellijSdk.forIde.$majorVersion") as? String
}

class XMLWriter(private val outputStreamWriter: OutputStreamWriter) : Closeable {

    private val xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStreamWriter)

    private var depth = 0
    private val indent = "  "

    fun document(encoding: String, version: String, init: XMLWriter.() -> Unit) = apply {
        xmlStreamWriter.writeStartDocument(encoding, version)

        init()

        xmlStreamWriter.writeEndDocument()
    }

    fun element(name: String, init: XMLWriter.() -> Unit) = apply {
        writeIndent()
        xmlStreamWriter.writeStartElement(name)
        depth += 1

        init()

        depth -= 1
        writeIndent()
        xmlStreamWriter.writeEndElement()
    }

    fun emptyElement(name: String, init: XMLWriter.() -> Unit) = apply {
        writeIndent()
        xmlStreamWriter.writeEmptyElement(name)
        init()
    }

    fun attribute(name: String, value: String): Unit = xmlStreamWriter.writeAttribute(name, value)

    fun attributes(vararg attributes: Pair<String, String>) {
        attributes.forEach { attribute(it.first, it.second) }
    }

    private fun writeIndent() {
        xmlStreamWriter.writeCharacters("\n")
        repeat(depth) {
            xmlStreamWriter.writeCharacters(indent)
        }
    }

    override fun close() {
        xmlStreamWriter.flush()
        xmlStreamWriter.close()
        outputStreamWriter.close()
    }
}

project.configurations.named(org.jetbrains.kotlin.gradle.plugin.PLUGIN_CLASSPATH_CONFIGURATION_NAME + "Main") {
    resolutionStrategy {
        eachDependency {
            if (this.requested.group == "org.jetbrains.kotlin") useVersion(libs.versions.kotlin.`for`.gradle.plugins.compilation.get())
        }
    }
}
