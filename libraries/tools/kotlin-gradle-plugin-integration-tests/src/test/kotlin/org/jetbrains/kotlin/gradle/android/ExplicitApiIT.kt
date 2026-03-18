package org.jetbrains.kotlin.gradle.android

import org.gradle.util.GradleVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilerExecutionStrategy
import org.jetbrains.kotlin.gradle.testbase.*
import kotlin.io.path.moveTo
import kotlin.io.path.writeText
import kotlin.test.fail

@AndroidTestVersions(
    maxVersion = TestVersions.AGP.AGP_813,
    additionalVersions = [
        TestVersions.AGP.AGP_83,
        TestVersions.AGP.AGP_84,
        TestVersions.AGP.AGP_85,
        TestVersions.AGP.AGP_86,
        TestVersions.AGP.AGP_87,
        TestVersions.AGP.AGP_88,
        TestVersions.AGP.AGP_89,
        TestVersions.AGP.AGP_810,
        TestVersions.AGP.AGP_811,
        TestVersions.AGP.AGP_812,
    ],
)
@AndroidGradlePluginTests
class ExplicitApiIT : KGPBaseTest() {

    @GradleAndroidTest
    fun `test - explicit API - warning builds`(
        gradleVersion: GradleVersion, androidVersion: String, jdkVersion: JdkVersions.ProvidedJdk,
    ) {
        project(
            "externalAndroidTarget-simple",
            gradleVersion,
            buildOptions = defaultBuildOptions
                .copy(androidVersion = androidVersion)
                .copy(configurationCache = BuildOptions.ConfigurationCacheValue.DISABLED)
                .copy(useDaemonFallbackStrategy = true)
                .copy(compilerExecutionStrategy = KotlinCompilerExecutionStrategy.IN_PROCESS)
                .copy(customKotlinDaemonRunFilesDirectory = workingDir.resolve("kotlin-daemon-run-files/explicit-warning-negative").toFile())
                .copy(freeArgs = listOf(
                    "-Pkotlin.build.internal.gradle.setup=false"
                )),
            buildJdk = jdkVersion.location,
        ) {
            modifyProjectForAGPVersion(androidVersion)
            projectPath.resolve("src/commonMain/kotlin/CommonMain.kt").writeText(
                """
                object CommonMain {
                    val greeting = "Hello"
                    fun greet(name: String) = "${'$'}greeting, ${'$'}name"
                    override fun toString(): String {
                        return "CommonMain"
                    }
                }
                """.trimIndent()
            )
            projectPath.resolve("src/androidMain/kotlin/AndroidMain.kt").writeText(
                """
                import android.content.Context
                import android.util.Log
                
                class AndroidMain(val context: Context) {
                    val counter = 0
                    fun increment() = counter + 1
                    fun useContext() {
                        context.getSystemService(Context.LOCATION_SERVICE)
                    }
                
                    fun useLog() {
                        Log.d("test", CommonMain.toString())
                    }
                
                    companion object {
                        fun useCommonMain() {
                            println("useCommonMain: ${'$'}{CommonMain}")
                        }
                    }
                }
                """.trimIndent()
            )
            buildGradleKts.modify {
                it.replace(
                    "kotlin {",
                    """
                    |kotlin {
                    |       explicitApiWarning()
                """.trimMargin()
                )
            }
            build(":compileCommonMainKotlinMetadata", ":compileAndroidMain", forwardBuildOutput = true) {
                assertTasksExecuted(":compileCommonMainKotlinMetadata", ":compileAndroidMain")
                val relevant = output.lineSequence()
                    .filter { it.contains("CommonMain.kt") || it.contains("AndroidMain.kt") }
                    .toList()
                if (relevant.none { it.contains("explicit API") }) {
                    fail("Expected explicit API diagnostics for CommonMain.kt/AndroidMain.kt")
                }
            }
        }
    }

    @GradleAndroidTest
    fun `test - explicit API - strict fails`(
        gradleVersion: GradleVersion, androidVersion: String, jdkVersion: JdkVersions.ProvidedJdk,
    ) {
        project(
            "externalAndroidTarget-simple",
            gradleVersion,
            buildOptions = defaultBuildOptions
                .copy(androidVersion = androidVersion)
                .copy(configurationCache = BuildOptions.ConfigurationCacheValue.DISABLED)
                .copy(useDaemonFallbackStrategy = true)
                .copy(compilerExecutionStrategy = KotlinCompilerExecutionStrategy.IN_PROCESS)
                .copy(customKotlinDaemonRunFilesDirectory = workingDir.resolve("kotlin-daemon-run-files/explicit-strict-negative").toFile())
                .copy(freeArgs = listOf(
                    "-Pkotlin.build.internal.gradle.setup=false"
                )),
            buildJdk = jdkVersion.location,
        ) {
            modifyProjectForAGPVersion(androidVersion)
            projectPath.resolve("src/commonMain/kotlin/CommonMain.kt").writeText(
                """
                object CommonMain {
                    val greeting = "Hello"
                    fun greet(name: String) = "${'$'}greeting, ${'$'}name"
                    override fun toString(): String {
                        return "CommonMain"
                    }
                }
                """.trimIndent()
            )
            projectPath.resolve("src/androidMain/kotlin/AndroidMain.kt").writeText(
                """
                import android.content.Context
                import android.util.Log
                
                class AndroidMain(val context: Context) {
                    val counter = 0
                    fun increment() = counter + 1
                    fun useContext() {
                        context.getSystemService(Context.LOCATION_SERVICE)
                    }
                
                    fun useLog() {
                        Log.d("test", CommonMain.toString())
                    }
                
                    companion object {
                        fun useCommonMain() {
                            println("useCommonMain: ${'$'}{CommonMain}")
                        }
                    }
                }
                """.trimIndent()
            )
            buildGradleKts.modify {
                it.replace(
                    "kotlin {",
                    """
                    |kotlin {
                    |       explicitApi()
                """.trimMargin()
                )
            }
            buildAndFail(":compileCommonMainKotlinMetadata", ":compileAndroidMain", forwardBuildOutput = true) {
                val relevant = output.lineSequence()
                    .filter { it.contains("CommonMain.kt") || it.contains("AndroidMain.kt") }
                    .toList()
                if (relevant.none { it.contains("explicit API") }) {
                    fail("Expected explicit API diagnostics for CommonMain.kt/AndroidMain.kt")
                }
            }
        }
    }

    @GradleAndroidTest
    fun `test - explicit API - positive warning builds`(
        gradleVersion: GradleVersion, androidVersion: String, jdkVersion: JdkVersions.ProvidedJdk,
    ) {
        project(
            "externalAndroidTarget-simple",
            gradleVersion,
            buildOptions = defaultBuildOptions
                .copy(androidVersion = androidVersion)
                .copy(configurationCache = BuildOptions.ConfigurationCacheValue.DISABLED)
                .copy(useDaemonFallbackStrategy = true)
                .copy(compilerExecutionStrategy = KotlinCompilerExecutionStrategy.IN_PROCESS)
                .copy(customKotlinDaemonRunFilesDirectory = workingDir.resolve("kotlin-daemon-run-files/explicit-warning-positive").toFile())
                .copy(freeArgs = listOf(
                    "-Pkotlin.build.internal.gradle.setup=false"
                )),
            buildJdk = jdkVersion.location,
        ) {
            modifyProjectForAGPVersion(androidVersion)
            projectPath.resolve("src/commonMain/kotlin/CommonMain.kt").writeText(
                """
                public object CommonMain {
                    public val greeting: String = "Hello"
                    public fun greet(name: String): String = "${'$'}greeting, ${'$'}name"
                    public override fun toString(): String {
                        return "CommonMain"
                    }
                }
                """.trimIndent()
            )
            projectPath.resolve("src/androidMain/kotlin/AndroidMain.kt").writeText(
                """
                import android.content.Context
                import android.util.Log
                
                public class AndroidMain(public val context: Context) {
                    public val counter: Int = 0
                    public fun increment(): Int = counter + 1
                    public fun useContext(): Unit {
                        context.getSystemService(Context.LOCATION_SERVICE)
                    }
                
                    public fun useLog(): Unit {
                        Log.d("test", CommonMain.toString())
                    }
                
                    public companion object {
                        public fun useCommonMain(): Unit {
                            println("useCommonMain: ${'$'}{CommonMain}")
                        }
                    }
                }
                """.trimIndent()
            )
            buildGradleKts.modify {
                it.replace(
                    "kotlin {",
                    """
                    |kotlin {
                    |       explicitApiWarning()
                """.trimMargin()
                )
            }
            build(":compileCommonMainKotlinMetadata", ":compileAndroidMain", forwardBuildOutput = true) {
                assertTasksExecuted(":compileCommonMainKotlinMetadata", ":compileAndroidMain")
                val relevant = output.lineSequence()
                    .filter { it.contains("CommonMain.kt") || it.contains("AndroidMain.kt") }
                    .toList()
                if (relevant.any { it.contains("explicit API") }) {
                    fail("Unexpected explicit API diagnostics for CommonMain.kt/AndroidMain.kt")
                }
            }
        }
    }

    @GradleAndroidTest
    fun `test - explicit API - positive strict builds`(
        gradleVersion: GradleVersion, androidVersion: String, jdkVersion: JdkVersions.ProvidedJdk,
    ) {
        project(
            "externalAndroidTarget-simple",
            gradleVersion,
            buildOptions = defaultBuildOptions
                .copy(androidVersion = androidVersion)
                .copy(configurationCache = BuildOptions.ConfigurationCacheValue.DISABLED)
                .copy(useDaemonFallbackStrategy = true)
                .copy(compilerExecutionStrategy = KotlinCompilerExecutionStrategy.IN_PROCESS)
                .copy(customKotlinDaemonRunFilesDirectory = workingDir.resolve("kotlin-daemon-run-files/explicit-strict-positive").toFile())
                .copy(freeArgs = listOf(
                    "-Pkotlin.build.internal.gradle.setup=false"
                )),
            buildJdk = jdkVersion.location,
        ) {
            modifyProjectForAGPVersion(androidVersion)
            projectPath.resolve("src/commonMain/kotlin/CommonMain.kt").writeText(
                """
                public object CommonMain {
                    public val greeting: String = "Hello"
                    public fun greet(name: String): String = "${'$'}greeting, ${'$'}name"
                    public override fun toString(): String {
                        return "CommonMain"
                    }
                }
                """.trimIndent()
            )
            projectPath.resolve("src/androidMain/kotlin/AndroidMain.kt").writeText(
                """
                import android.content.Context
                import android.util.Log
                
                public class AndroidMain(public val context: Context) {
                    public val counter: Int = 0
                    public fun increment(): Int = counter + 1
                    public fun useContext(): Unit {
                        context.getSystemService(Context.LOCATION_SERVICE)
                    }
                
                    public fun useLog(): Unit {
                        Log.d("test", CommonMain.toString())
                    }
                
                    public companion object {
                        public fun useCommonMain(): Unit {
                            println("useCommonMain: ${'$'}{CommonMain}")
                        }
                    }
                }
                """.trimIndent()
            )
            buildGradleKts.modify {
                it.replace(
                    "kotlin {",
                    """
                    |kotlin {
                    |       explicitApi()
                """.trimMargin()
                )
            }
            build(":compileCommonMainKotlinMetadata", ":compileAndroidMain", forwardBuildOutput = true) {
                assertTasksExecuted(":compileCommonMainKotlinMetadata", ":compileAndroidMain")
                val relevant = output.lineSequence()
                    .filter { it.contains("CommonMain.kt") || it.contains("AndroidMain.kt") }
                    .toList()
                if (relevant.any { it.contains("explicit API") }) {
                    fail("Unexpected explicit API diagnostics for CommonMain.kt/AndroidMain.kt")
                }
            }
        }
    }

    private fun TestProject.modifyProjectForAGPVersion(androidVersion: String) {
        val agpVersion = TestVersions.AgpCompatibilityMatrix.fromVersion(androidVersion)
        buildGradleKts.modify {
            val withAndroidTestMethod = when {
                agpVersion >= TestVersions.AgpCompatibilityMatrix.AGP_88 -> "withHostTest {}"
                else -> "withAndroidTestOnJvm {}"
            }
            val androidTestSourceSetName = when {
                agpVersion >= TestVersions.AgpCompatibilityMatrix.AGP_88 -> "androidHostTest"
                else -> "androidTestOnJvm"
            }
            it.replace("<host-test-dsl>", withAndroidTestMethod)
                .replace("<host-test-source-set-name>", androidTestSourceSetName)
        }

        if (agpVersion >= TestVersions.AgpCompatibilityMatrix.AGP_88) {
            projectPath.resolve("src/androidTestOnJvm")
                .moveTo(projectPath.resolve("src/androidHostTest"))
        }
    }
}
