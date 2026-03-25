package org.jetbrains.kotlin.gradle.android

import org.gradle.api.logging.LogLevel
import org.gradle.kotlin.dsl.kotlin
import org.gradle.util.GradleVersion
import org.jetbrains.kotlin.gradle.testbase.*

@AndroidTestVersions(minVersion = TestVersions.AGP.AGP_813)
@AndroidGradlePluginTests
class ExplicitApiExternalAndroidTargetIT : KGPBaseTest() {

    @GradleAndroidTest
    fun `test - explicit API - warning builds`(
        gradleVersion: GradleVersion, androidVersion: String, jdkVersion: JdkVersions.ProvidedJdk,
    ) {
        project(
            "empty",
            gradleVersion,
            buildOptions = defaultBuildOptions
                .copy(androidVersion = androidVersion, logLevel = LogLevel.DEBUG),
            buildJdk = jdkVersion.location,
        ) {
            addAgpToBuildScriptCompilationClasspath(androidVersion)
            addKgpToBuildScriptCompilationClasspath()
            plugins {
                kotlin("multiplatform")
                id("com.android.kotlin.multiplatform.library")
            }
            configureKmpAndroidLibrary()
            injectSources(publicApi = false)
            buildScriptInjection {
                kotlinMultiplatform.explicitApiWarning()
            }
            build(":compileKotlinMetadata", ":compileAndroidMain", forwardBuildOutput = true) {
                assertTasksExecuted(":compileAndroidMain")
                assertCompilerArgument(":compileAndroidMain", "-Xexplicit-api=warning")
                assertOutputContains(Regex("explicit api mode", RegexOption.IGNORE_CASE))
            }
        }
    }

    @GradleAndroidTest
    fun `test - explicit API - strict fails`(
        gradleVersion: GradleVersion,
        androidVersion: String,
        jdkVersion: JdkVersions.ProvidedJdk,
    ) {
        project(
            "empty",
            gradleVersion,
            buildOptions = defaultBuildOptions
                .copy(androidVersion = androidVersion, logLevel = LogLevel.DEBUG),
            buildJdk = jdkVersion.location,
        ) {
            addAgpToBuildScriptCompilationClasspath(androidVersion)
            addKgpToBuildScriptCompilationClasspath()
            plugins {
                kotlin("multiplatform")
                id("com.android.kotlin.multiplatform.library")
            }
            configureKmpAndroidLibrary()
            injectSources(publicApi = false)
            buildScriptInjection {
                kotlinMultiplatform.explicitApi()
            }
            buildAndFail(":compileKotlinMetadata", ":compileAndroidMain", forwardBuildOutput = true) {
                assertCompilerArgument(":compileAndroidMain", "-Xexplicit-api=strict")
                assertOutputContains(Regex("explicit api mode", RegexOption.IGNORE_CASE))
            }
        }
    }

    @GradleAndroidTest
    fun `test - explicit API - positive warning builds`(
        gradleVersion: GradleVersion, androidVersion: String, jdkVersion: JdkVersions.ProvidedJdk,
    ) {
        project(
            "empty",
            gradleVersion,
            buildOptions = defaultBuildOptions
                .copy(androidVersion = androidVersion),
            buildJdk = jdkVersion.location,
        ) {
            addAgpToBuildScriptCompilationClasspath(androidVersion)
            addKgpToBuildScriptCompilationClasspath()
            plugins {
                kotlin("multiplatform")
                id("com.android.kotlin.multiplatform.library")
            }
            configureKmpAndroidLibrary()
            injectSources(publicApi = true)
            buildScriptInjection {
                kotlinMultiplatform.explicitApiWarning()
            }
            build(":compileAndroidMain", forwardBuildOutput = true) {
                assertTasksExecuted(":compileAndroidMain")
                assertOutputDoesNotContain(Regex("explicit api mode", RegexOption.IGNORE_CASE))
            }
        }
    }

    @GradleAndroidTest
    fun `test - explicit API - positive strict builds`(
        gradleVersion: GradleVersion, androidVersion: String, jdkVersion: JdkVersions.ProvidedJdk,
    ) {
        project(
            "empty",
            gradleVersion,
            buildOptions = defaultBuildOptions
                .copy(androidVersion = androidVersion),
            buildJdk = jdkVersion.location,
        ) {
            addAgpToBuildScriptCompilationClasspath(androidVersion)
            addKgpToBuildScriptCompilationClasspath()
            plugins {
                kotlin("multiplatform")
                id("com.android.kotlin.multiplatform.library")
            }
            configureKmpAndroidLibrary()
            injectSources(publicApi = true)
            buildScriptInjection {
                kotlinMultiplatform.explicitApi()
            }
            build(":compileAndroidMain", forwardBuildOutput = true) {
                assertTasksExecuted(":compileAndroidMain")
                assertOutputDoesNotContain(Regex("explicit api mode", RegexOption.IGNORE_CASE))
            }
        }
    }

    private fun TestProject.configureKmpAndroidLibrary() = buildScriptInjection {
        val target = kotlinMultiplatform.targets.getByName("android")
        val klass = target::class.java.classLoader.loadClass(
            "com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension"
        )
        val setCompileSdk = klass.getMethod("setCompileSdk", Int::class.javaObjectType)
        setCompileSdk.invoke(target, 34)
        val setNamespace = klass.getMethod("setNamespace", String::class.java)
        setNamespace.invoke(target, "org.jetbrains.sample")
        runCatching {
            val withHostTest = klass.getMethod("withHostTest")
            withHostTest.invoke(target)
        }
    }

    private fun TestProject.injectSources(publicApi: Boolean) = buildScriptInjection {
        // Inject sources with/without explicit API
        if (!publicApi) {
            kotlinMultiplatform.sourceSets.getByName("commonMain").compileSource(
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
            kotlinMultiplatform.sourceSets.getByName("androidMain").compileSource(
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
        } else {
            kotlinMultiplatform.sourceSets.getByName("commonMain").compileSource(
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
            kotlinMultiplatform.sourceSets.getByName("androidMain").compileSource(
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
        }
    }

}
