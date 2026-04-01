/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.android.externalAndroidTarget

import com.android.build.api.dsl.androidLibrary
import org.gradle.kotlin.dsl.kotlin
import org.gradle.util.GradleVersion
import org.jetbrains.kotlin.gradle.testbase.*
import java.util.zip.ZipFile
import kotlin.test.assertNotNull

@AndroidTestVersions(minVersion = TestVersions.AGP.AGP_813)
@AndroidGradlePluginTests
class AndroidLibraryWithJavaIT : KGPBaseTest() {

    @GradleAndroidTest
    fun `test - androidLibrary - withJava enabled`(
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
                        withJava()
                    }
                }
            }

            val javaSrcFile = projectPath.resolve("src/androidMain/java/sample/JavaClass.java")
            javaSrcFile.parent.toFile().mkdirs()
            javaSrcFile.toFile().writeText(
                """
                package sample;
                public class JavaClass {
                    public String ping() {
                        return "java";
                    }
                    public String callKotlin() {
                        return new KotlinClass().ping() + ":" + ping();
                    }
                }
                """.trimIndent()
            )

            val kotlinSrcFile = projectPath.resolve("src/androidMain/kotlin/sample/KotlinClass.kt")
            kotlinSrcFile.parent.toFile().mkdirs()
            kotlinSrcFile.toFile().writeText(
                """
                package sample
                class KotlinClass {
                    fun ping(): String = "kotlin"
                    fun callJava(): String = JavaClass().ping()
                }
                """.trimIndent()
            )

            build("assemble") {
                assertTasksExecuted(":compileAndroidMainJavaWithJavac")
                assertFileInProjectExists("build/outputs/aar/empty.aar")
                assertAarContainsClass("build/outputs/aar/empty.aar", "sample/JavaClass.class")
                assertAarContainsClass("build/outputs/aar/empty.aar", "sample/KotlinClass.class")
            }
        }
    }

    @GradleAndroidTest
    fun `test - androidLibrary - withJava disabled`(
        gradleVersion: GradleVersion, androidVersion: String, jdkVersion: JdkVersions.ProvidedJdk,
    ) {
        project(
            "empty",
            gradleVersion = gradleVersion,
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
                        namespace = "org.jetbrains.sample.options"
                    }
                }
            }

            val javaSrcFile = projectPath.resolve("src/androidMain/java/sample/JavaClass.java")
            javaSrcFile.parent.toFile().mkdirs()
            javaSrcFile.toFile().writeText("package sample; public class JavaClass {}")

            val kotlinSrcFile = projectPath.resolve("src/androidMain/kotlin/sample/KotlinClass.kt")
            kotlinSrcFile.parent.toFile().mkdirs()
            kotlinSrcFile.toFile().writeText(
                """
                package sample
                class KotlinClass {
                    fun useJava(): String = JavaClass().toString()
                }
                """.trimIndent()
            )

            buildAndFail("assemble") {
                assertTasksFailed(":compileAndroidMain")
                assertTasksAreNotInTaskGraph(":compileAndroidMainJavaWithJavac")
                assertFileInProjectNotExists("build/outputs/aar/empty.aar")
            }
        }
    }

    @GradleAndroidTest
    fun `test - withJava enabled without Java sources`(
        gradleVersion: GradleVersion, androidVersion: String, jdkVersion: JdkVersions.ProvidedJdk,
    ) {
        project(
            "empty",
            gradleVersion = gradleVersion,
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
                        namespace = "org.jetbrains.sample.nojava"
                        withJava()
                    }
                }
            }

            val ktAndroid = projectPath.resolve("src/androidMain/kotlin/sample/OnlyKotlin.kt")
            ktAndroid.parent.toFile().mkdirs()
            ktAndroid.toFile().writeText(
                """
                package sample
                class OnlyKotlin { fun ok() = "ok" }
                """.trimIndent()
            )

            build("assemble") {
                assertFileInProjectExists("build/outputs/aar/empty.aar")
                assertTasksNoSource(":compileAndroidMainJavaWithJavac")
                assertAarContainsClass("build/outputs/aar/empty.aar", "sample/OnlyKotlin.class")
            }
        }
    }

    private fun TestProject.assertAarContainsClass(aarPath: String, classPath: String) {
        val aarFile = projectPath.resolve(aarPath).toFile()
        check(aarFile.exists()) { "AAR file does not exist: $aarPath" }

        ZipFile(aarFile).use { aarZip ->
            val classesJarEntry = aarZip.getEntry("classes.jar")
            check(classesJarEntry != null) { "classes.jar not found inside AAR: $aarPath" }

            val tempJar = kotlin.io.path.createTempFile(suffix = ".jar").toFile().apply {
                deleteOnExit()
            }

            aarZip.getInputStream(classesJarEntry).use { input ->
                tempJar.outputStream().use { output -> input.copyTo(output) }
            }

            ZipFile(tempJar).use { classesJar ->
                val classEntry = classesJar.getEntry(classPath)
                assertNotNull(
                    classEntry,
                    "Class $classPath not found inside classes.jar of $aarPath"
                )
            }
        }
    }
}