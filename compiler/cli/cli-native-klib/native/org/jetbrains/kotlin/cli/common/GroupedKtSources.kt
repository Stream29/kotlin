/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.common

import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileVisitor
import org.jetbrains.kotlin.KtSourceFile
import org.jetbrains.kotlin.KtVirtualFileSourceFile
import org.jetbrains.kotlin.cli.CliDiagnostics
import org.jetbrains.kotlin.cli.common.config.KotlinSourceRoot
import org.jetbrains.kotlin.cli.common.config.kotlinSourceRoots
import org.jetbrains.kotlin.cli.jvm.compiler.VfsBasedProjectEnvironment
import org.jetbrains.kotlin.cli.report
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.konan.file.File
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImplementationDetail
import org.jetbrains.kotlin.psi.hmppModuleName
import org.jetbrains.kotlin.psi.isCommonSource

class GroupedKtSources(
    val platformSources: Collection<KtSourceFile>,
    val commonSources: Collection<KtSourceFile>,
    val sourcesByModuleName: Map<String?, Set<KtSourceFile>>,
) {
    fun isEmpty(): Boolean = platformSources.isEmpty() && commonSources.isEmpty()
}

fun GroupedKtSources.isCommonSourceForLt(sourceFile: KtSourceFile): Boolean {
    return sourceFile in commonSources
}

fun GroupedKtSources.fileBelongsToModuleForLt(sourceFile: KtSourceFile, moduleName: String): Boolean {
    return sourceFile in sourcesByModuleName[moduleName].orEmpty()
}

@OptIn(KtImplementationDetail::class)
val isCommonSourceForPsi: (KtFile) -> Boolean = { it.isCommonSource == true }

@OptIn(KtImplementationDetail::class)
val fileBelongsToModuleForPsi: (KtFile, String) -> Boolean = { file, moduleName -> file.hmppModuleName == moduleName }

fun collectSources(
    compilerConfiguration: CompilerConfiguration,
    projectEnvironment: VfsBasedProjectEnvironment,
): GroupedKtSources {
    val platformSources = linkedSetOf<KtSourceFile>()
    val commonSources = linkedSetOf<KtSourceFile>()
    val sourcesByModuleName = mutableMapOf<String?, MutableSet<KtSourceFile>>()

    getSourceFilesFromSourceRoots(
        compilerConfiguration,
        projectEnvironment,
        compilerConfiguration.kotlinSourceRoots,
    ) { virtualFile, isCommon, moduleName ->
        val file = KtVirtualFileSourceFile(virtualFile)
        if (isCommon) {
            commonSources.add(file)
        } else {
            platformSources.add(file)
        }
        sourcesByModuleName.getOrPut(moduleName) { mutableSetOf() }.add(file)
    }
    return GroupedKtSources(platformSources, commonSources, sourcesByModuleName)
}

fun getSourceFilesFromSourceRoots(
    configuration: CompilerConfiguration,
    projectEnvironment: VfsBasedProjectEnvironment,
    sourceRoots: List<KotlinSourceRoot>,
    reportLocation: Boolean = false,
    processor: (VirtualFile, isCommon: Boolean, moduleName: String?) -> Unit,
) {
    val localFileSystem = projectEnvironment.localFileSystem
    val processedFiles = mutableSetOf<VirtualFile>()
    for ([sourceRootPath, isCommon, hmppModuleName] in sourceRoots) {
        val vFile = localFileSystem.findFileByPath(File(sourceRootPath).path)
        if (vFile == null) {
            configuration.report(CliDiagnostics.ROOTS_RESOLUTION_ERROR, "Source file or directory not found: $sourceRootPath")
            continue
        }
        if (!vFile.isDirectory && vFile.fileType != KotlinFileType.INSTANCE) {
            configuration.report(CliDiagnostics.ROOTS_RESOLUTION_ERROR, "Source entry is not a Kotlin file: $sourceRootPath")
            continue
        }

        VfsUtilCore.visitChildrenRecursively(
            vFile,
            object : VirtualFileVisitor<Any?>() {
                override fun visitFile(file: VirtualFile): Boolean {
                    if (!file.isDirectory && file.fileType == KotlinFileType.INSTANCE && processedFiles.add(file)) {
                        processor(file, isCommon, hmppModuleName)
                    }
                    return true
                }
            }
        )
    }
}
