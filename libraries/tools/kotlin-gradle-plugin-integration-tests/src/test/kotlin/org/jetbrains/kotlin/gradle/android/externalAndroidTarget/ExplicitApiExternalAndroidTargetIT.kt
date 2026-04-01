/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.android.externalAndroidTarget

import com.android.build.api.dsl.androidLibrary
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
                .copy(
                    androidVersion = androidVersion,
                    // disabled for stable test run with js target
                    configurationCache = BuildOptions.ConfigurationCacheValue.DISABLED,
                    isolatedProjects = BuildOptions.IsolatedProjectsMode.DISABLED,
                ),
            buildJdk = jdkVersion.location,
        ) {
            plugins {
                kotlin("multiplatform")
                id("com.android.kotlin.multiplatform.library")
            }
            buildScriptInjection {
                kotlinMultiplatform.apply {
                    androidLibrary {
                        compileSdk = 34
                        namespace = "org.jetbrains.sample"
                        withHostTest { }
                    }
                    js {
                        nodejs()
                    }
                    explicitApiWarning()
                }
            }
            injectSourcesImplicitVisibility()
            build(":compileKotlinMetadata", ":compileAndroidMain") {
                assertTasksExecuted(":compileAndroidMain")
                assertCompilerArgument(":compileAndroidMain", "-Xexplicit-api=warning", LogLevel.INFO)
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
                .copy(
                    androidVersion = androidVersion,
                    configurationCache = BuildOptions.ConfigurationCacheValue.DISABLED,
                    isolatedProjects = BuildOptions.IsolatedProjectsMode.DISABLED,
                ),
            buildJdk = jdkVersion.location,
        ) {
            plugins {
                kotlin("multiplatform")
                id("com.android.kotlin.multiplatform.library")
            }
            buildScriptInjection {
                kotlinMultiplatform.apply {
                    androidLibrary {
                        compileSdk = 34
                        namespace = "org.jetbrains.sample"
                        withHostTest { }
                    }
                    js {
                        nodejs()
                    }
                    explicitApi()
                }
            }
            injectSourcesImplicitVisibility()
            buildAndFail(":compileKotlinMetadata", ":compileAndroidMain", forwardBuildOutput = true) {
                assertCompilerArgument(":compileAndroidMain", "-Xexplicit-api=strict", LogLevel.INFO)
                assertOutputContains("Visibility must be specified in explicit API mode")
                assertOutputContains("Return type must be specified in explicit API mode")
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
                .copy(
                    androidVersion = androidVersion,
                    configurationCache = BuildOptions.ConfigurationCacheValue.DISABLED,
                    isolatedProjects = BuildOptions.IsolatedProjectsMode.DISABLED,
                ),
            buildJdk = jdkVersion.location,
        ) {
            plugins {
                kotlin("multiplatform")
                id("com.android.kotlin.multiplatform.library")
            }
            buildScriptInjection {
                kotlinMultiplatform.apply {
                    androidLibrary {
                        compileSdk = 34
                        namespace = "org.jetbrains.sample"
                        withHostTest { }
                    }
                    js {
                        nodejs()
                    }
                    explicitApiWarning()
                }
            }
            injectSourcesExplicitVisibility()
            build(":compileAndroidMain") {
                assertTasksExecuted(":compileAndroidMain")
                assertCompilerArgument(":compileAndroidMain", "-Xexplicit-api=warning", LogLevel.INFO)
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
                .copy(
                    androidVersion = androidVersion,
                    configurationCache = BuildOptions.ConfigurationCacheValue.DISABLED,
                    isolatedProjects = BuildOptions.IsolatedProjectsMode.DISABLED,
                ),
            buildJdk = jdkVersion.location,
        ) {
            plugins {
                kotlin("multiplatform")
                id("com.android.kotlin.multiplatform.library")
            }
            buildScriptInjection {
                kotlinMultiplatform.apply {
                    androidLibrary {
                        compileSdk = 34
                        namespace = "org.jetbrains.sample"
                        withHostTest { }
                    }
                    js {
                        nodejs()
                    }
                    explicitApi()
                }
            }
            injectSourcesExplicitVisibility()
            build(":compileAndroidMain") {
                assertTasksExecuted(":compileAndroidMain")
                assertCompilerArgument(":compileAndroidMain", "-Xexplicit-api=strict", LogLevel.INFO)
            }
        }
    }

    private fun TestProject.injectSourcesImplicitVisibility() = buildScriptInjection {
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
        kotlinMultiplatform.sourceSets.getByName("commonMain").compileSource(
            """
            public val version = 1
            public fun compute() = version + 1
            open class Base {
                open fun foo(): String = "base"
            }
            class Child : Base() {
                override fun foo() = super.foo()
            }
            """.trimIndent()
        )
        val androidMain = kotlinMultiplatform.sourceSets.getByName("androidMain")
        androidMain.compileSource(
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
    }

    private fun TestProject.injectSourcesExplicitVisibility() = buildScriptInjection {
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
        kotlinMultiplatform.sourceSets.getByName("commonMain").compileSource(
            """
            public val version: Int = 1
            public fun compute(): Int = version + 1
            public open class Base {
                public open fun foo(): String = "base"
            }
            public class Child : Base() {
                public override fun foo(): String = super.foo()
            }
            """.trimIndent()
        )
        val androidMain = kotlinMultiplatform.sourceSets.getByName("androidMain")
        androidMain.compileSource(
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
