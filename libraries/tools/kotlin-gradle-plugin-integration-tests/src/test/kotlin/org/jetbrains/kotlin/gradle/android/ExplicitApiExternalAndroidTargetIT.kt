package org.jetbrains.kotlin.gradle.android

import org.gradle.util.GradleVersion
import org.jetbrains.kotlin.gradle.testbase.*
import kotlin.io.path.writeText
import kotlin.test.fail

@AndroidTestVersions(minVersion = TestVersions.AGP.AGP_813)
@AndroidGradlePluginTests
class ExplicitApiExternalAndroidTargetIT : KGPBaseTest() {

    @GradleAndroidTest
    fun `test - explicit API - warning builds`(
        gradleVersion: GradleVersion, androidVersion: String, jdkVersion: JdkVersions.ProvidedJdk,
    ) {
        project(
            "externalAndroidTarget-simple",
            gradleVersion,
            buildOptions = defaultBuildOptions
                .copy(androidVersion = androidVersion),
            buildJdk = jdkVersion.location,
        ) {
            configureAndroidHostTest()
            writeCommonSource(publicApi = false)
            writeAndroidSource(publicApi = false)
            buildScriptInjection {
                kotlinMultiplatform.explicitApiWarning()
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
                .copy(androidVersion = androidVersion),
            buildJdk = jdkVersion.location,
        ) {
            configureAndroidHostTest()
            writeCommonSource(publicApi = false)
            writeAndroidSource(publicApi = false)
            buildScriptInjection {
                kotlinMultiplatform.explicitApi()
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
                .copy(androidVersion = androidVersion),
            buildJdk = jdkVersion.location,
        ) {
            configureAndroidHostTest()
            writeCommonSource(publicApi = true)
            writeAndroidSource(publicApi = true)
            buildScriptInjection {
                kotlinMultiplatform.explicitApiWarning()
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
                .copy(androidVersion = androidVersion),
            buildJdk = jdkVersion.location,
        ) {
            configureAndroidHostTest()
            writeCommonSource(publicApi = true)
            writeAndroidSource(publicApi = true)
            buildScriptInjection {
                kotlinMultiplatform.explicitApi()
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

    private fun TestProject.configureAndroidHostTest() {
        buildGradleKts.modify {
            it.replace("<host-test-dsl>", "withHostTest {}")
                .replace("<host-test-source-set-name>", "androidHostTest")
        }
    }

    private fun TestProject.writeCommonSource(publicApi: Boolean) {
        val content = if (!publicApi) {
            """
            object CommonMain {
                val greeting = "Hello"
                fun greet(name: String) = "${'$'}greeting, ${'$'}name"
                override fun toString(): String {
                    return "CommonMain"
                }
            }
            """.trimIndent()
        } else {
            """
            public object CommonMain {
                public val greeting: String = "Hello"
                public fun greet(name: String): String = "${'$'}greeting, ${'$'}name"
                public override fun toString(): String {
                    return "CommonMain"
                }
            }
            """.trimIndent()
        }
        projectPath.resolve("src/commonMain/kotlin/CommonMain.kt").writeText(content)
    }

    private fun TestProject.writeAndroidSource(publicApi: Boolean) {
        val content = if (!publicApi) {
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
        } else {
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
        }
        projectPath.resolve("src/androidMain/kotlin/AndroidMain.kt").writeText(content)
    }

}
