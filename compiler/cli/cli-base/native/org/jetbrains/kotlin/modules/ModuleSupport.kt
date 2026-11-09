/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("PackageDirectoryMismatch")

package org.jetbrains.kotlin.modules

data class JavaRootPath(val path: String, val packagePrefix: String? = null)

interface Module {
    fun getModuleName(): String
    fun getModuleType(): String
    fun getOutputDirectory(): String
    fun getFriendPaths(): List<String>
    fun getSourceFiles(): List<String>
    fun getCommonSourceFiles(): List<String>
    fun getClasspathRoots(): List<String>
    fun getJavaSourceRoots(): List<JavaRootPath>
    val modularJdkRoot: String?
}
