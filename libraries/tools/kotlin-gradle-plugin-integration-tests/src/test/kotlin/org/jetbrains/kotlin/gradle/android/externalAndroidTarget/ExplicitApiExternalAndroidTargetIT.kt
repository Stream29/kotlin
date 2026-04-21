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
    fun `test - warning - warns with implicit declarations`(
        gradleVersion: GradleVersion, androidVersion: String, jdkVersion: JdkVersions.ProvidedJdk,
    ) {
        project(
            "empty",
            gradleVersion,
            buildOptions = defaultBuildOptions.copy(androidVersion = androidVersion),
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
                    }
                    iosArm64()
                    explicitApiWarning()
                }
            }
            buildScriptInjection {
                val commonMain = kotlinMultiplatform.sourceSets.getByName("commonMain")
                commonMain.compileSource(
                    """
                    public val version = 1
                    public fun compute() = version + 1
                    """.trimIndent()
                )
                val androidMain = kotlinMultiplatform.sourceSets.getByName("androidMain")
                androidMain.compileSource(
                    """
                    class AndroidMain {
                        val counter = 0
                        fun increment() = counter + 1
                    }
                    """.trimIndent()
                )
            }
            build(":compileKotlinMetadata", ":compileAndroidMain") {
                assertTasksExecuted(":compileAndroidMain")
                assertCompilerArgument(":compileAndroidMain", "-Xexplicit-api=warning", LogLevel.INFO)
                assertOutputContains("Visibility must be specified in explicit API mode")
                assertOutputContains("Return type must be specified in explicit API mode")
            }
        }
    }

    @GradleAndroidTest
    fun `test - strict - fails with implicit declarations`(
        gradleVersion: GradleVersion,
        androidVersion: String,
        jdkVersion: JdkVersions.ProvidedJdk,
    ) {
        project(
            "empty",
            gradleVersion,
            buildOptions = defaultBuildOptions.copy(androidVersion = androidVersion),
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
                    }
                    iosArm64()
                    explicitApi()
                }
            }
            buildScriptInjection {
                val commonMain = kotlinMultiplatform.sourceSets.getByName("commonMain")
                commonMain.compileSource(
                    """
                    public val version = 1
                    public fun compute() = version + 1
                    """.trimIndent()
                )
                val androidMain = kotlinMultiplatform.sourceSets.getByName("androidMain")
                androidMain.compileSource(
                    """
                    class AndroidMain {
                        val counter = 0
                        fun increment() = counter + 1
                    }
                    """.trimIndent()
                )
            }
            buildAndFail(":compileCommonMainKotlinMetadata", ":compileAndroidMain") {
                assertCompilerArgument(":compileAndroidMain", "-Xexplicit-api=strict", LogLevel.INFO)
                assertOutputContains("Visibility must be specified in explicit API mode")
                assertOutputContains("Return type must be specified in explicit API mode")
            }
        }
    }

    @GradleAndroidTest
    fun `test - disabled - builds with implicit declarations`(
        gradleVersion: GradleVersion, androidVersion: String, jdkVersion: JdkVersions.ProvidedJdk,
    ) {
        project(
            "empty",
            gradleVersion,
            buildOptions = defaultBuildOptions.copy(androidVersion = androidVersion),
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
                    }
                    iosArm64()
                }
            }
            buildScriptInjection {
                val androidMain = kotlinMultiplatform.sourceSets.getByName("androidMain")
                androidMain.compileSource(
                    """
                    import android.content.Context
                    
                    class AndroidMain(val context: Context) {
                        val counter = 0
                        fun increment() = counter + 1
                    }
                    """.trimIndent()
                )
            }
            build(":compileKotlinMetadata", ":compileAndroidMain") {
                assertTasksExecuted(":compileAndroidMain")
                assertNoCompilerArgument(":compileAndroidMain", "-Xexplicit-api=", LogLevel.INFO)
            }
        }
    }

    @GradleAndroidTest
    fun `test - strict - fails with androidMain missing explicit types`(
        gradleVersion: GradleVersion, androidVersion: String, jdkVersion: JdkVersions.ProvidedJdk,
    ) {
        project(
            "empty",
            gradleVersion,
            buildOptions = defaultBuildOptions.copy(androidVersion = androidVersion),
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
                    }
                    iosArm64()
                    explicitApi()
                }
            }
            buildScriptInjection {
                val commonMain = kotlinMultiplatform.sourceSets.getByName("commonMain")
                commonMain.compileSource(
                    """
                    public object CommonMain {
                        public val greeting: String = "Hello"
                        public fun greet(name: String): String = "${'$'}greeting, ${'$'}name"
                    }
                    """.trimIndent()
                )
                val androidMain = kotlinMultiplatform.sourceSets.getByName("androidMain")
                androidMain.compileSource(
                    """
                    import android.content.Context
                    
                    class AndroidMain(val context: Context) {
                        fun increment() = 1
                    }
                    """.trimIndent()
                )
            }
            buildAndFail(":compileKotlinMetadata", ":compileAndroidMain") {
                assertTasksFailed(":compileAndroidMain")
                assertCompilerArgument(":compileAndroidMain", "-Xexplicit-api=strict", LogLevel.INFO)
                assertOutputContains("Visibility must be specified in explicit API mode")
                assertOutputContains("Return type must be specified in explicit API mode")
            }
        }
    }

    @GradleAndroidTest
    fun `test - strict - fails with commonMain missing explicit types`(
        gradleVersion: GradleVersion, androidVersion: String, jdkVersion: JdkVersions.ProvidedJdk,
    ) {
        project(
            "empty",
            gradleVersion,
            buildOptions = defaultBuildOptions.copy(androidVersion = androidVersion),
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
                    }
                    iosArm64()
                    explicitApi()
                }
            }
            buildScriptInjection {
                val commonMain = kotlinMultiplatform.sourceSets.getByName("commonMain")
                commonMain.compileSource(
                    """
                    public val version = 1
                    public fun compute() = version + 1
                    """.trimIndent()
                )
                val androidMain = kotlinMultiplatform.sourceSets.getByName("androidMain")
                androidMain.compileSource(
                    """
                    import android.content.Context
                    
                    public class AndroidMain(public val context: Context) {
                        public fun increment(): Int = 1
                    }
                    """.trimIndent()
                )
            }
            buildAndFail(":compileCommonMainKotlinMetadata", ":compileAndroidMain") {
                assertCompilerArgument(":compileAndroidMain", "-Xexplicit-api=strict", LogLevel.INFO)
                assertOutputContains("Return type must be specified in explicit API mode")
            }
        }
    }

    @GradleAndroidTest
    fun `test - warning - builds without warnings - positive case`(
        gradleVersion: GradleVersion, androidVersion: String, jdkVersion: JdkVersions.ProvidedJdk,
    ) {
        project(
            "empty",
            gradleVersion,
            buildOptions = defaultBuildOptions.copy(androidVersion = androidVersion),
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
                    }
                    iosArm64()
                    explicitApiWarning()
                }
            }
            buildScriptInjection {
                val commonMain = kotlinMultiplatform.sourceSets.getByName("commonMain")
                commonMain.compileSource(
                    """
                    public object CommonMain {
                        public fun greet(name: String): String = name
                    }
                    """.trimIndent()
                )
                val androidMain = kotlinMultiplatform.sourceSets.getByName("androidMain")
                androidMain.compileSource(
                    """
                    public class AndroidMain {
                        public fun increment(): Int = 1
                    }
                    """.trimIndent()
                )
            }
            build(":compileKotlinMetadata", ":compileAndroidMain") {
                assertTasksExecuted(":compileAndroidMain")
                assertCompilerArgument(":compileAndroidMain", "-Xexplicit-api=warning", LogLevel.INFO)
                assertOutputDoesNotContain("Visibility must be specified in explicit API mode")
                assertOutputDoesNotContain("Return type must be specified in explicit API mode")
            }
        }
    }

    @GradleAndroidTest
    fun `test - strict - builds - positive case`(
        gradleVersion: GradleVersion, androidVersion: String, jdkVersion: JdkVersions.ProvidedJdk,
    ) {
        project(
            "empty",
            gradleVersion,
            buildOptions = defaultBuildOptions.copy(androidVersion = androidVersion),
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
                    }
                    iosArm64()
                    explicitApi()
                }
            }
            buildScriptInjection {
                val commonMain = kotlinMultiplatform.sourceSets.getByName("commonMain")
                commonMain.compileSource(
                    """
                    public object CommonMain {
                        public fun greet(name: String): String = name
                    }
                    """.trimIndent()
                )
                val androidMain = kotlinMultiplatform.sourceSets.getByName("androidMain")
                androidMain.compileSource(
                    """
                    public class AndroidMain {
                        public fun increment(): Int = 1
                    }
                    """.trimIndent()
                )
            }
            build(":compileKotlinMetadata", ":compileAndroidMain") {
                assertTasksExecuted(":compileAndroidMain")
                assertCompilerArgument(":compileAndroidMain", "-Xexplicit-api=strict", LogLevel.INFO)
                assertOutputDoesNotContain("Visibility must be specified in explicit API mode")
                assertOutputDoesNotContain("Return type must be specified in explicit API mode")
            }
        }
    }
}
