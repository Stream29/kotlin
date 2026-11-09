/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.common

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.KtSourceElement
import org.jetbrains.kotlin.KtSourceFileLinesMapping
import org.jetbrains.kotlin.backend.common.CompilationException
import org.jetbrains.kotlin.cli.CliDiagnostics
import org.jetbrains.kotlin.cli.CliDiagnostics.KOTLIN_PACKAGE_USAGE
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocationWithRange
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.cli.common.messages.MessageUtil
import org.jetbrains.kotlin.cli.report
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.declarations.FirFile
import org.jetbrains.kotlin.fir.packageFqName
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.isSubpackageOf
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.platform.isCommon
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.text
import org.jetbrains.kotlin.util.PerformanceManager

fun <F> checkKotlinPackageUsage(
    configuration: CompilerConfiguration,
    files: Collection<F>,
    getPackage: (F) -> FqName,
    getMessageLocation: (F) -> CompilerMessageSourceLocation?,
): Boolean {
    if (configuration.getBoolean(CLIConfigurationKeys.ALLOW_KOTLIN_PACKAGE)) {
        return true
    }
    val kotlinPackage = FqName("kotlin")
    for (file in files) {
        if (getPackage(file).isSubpackageOf(kotlinPackage)) {
            configuration.report(
                KOTLIN_PACKAGE_USAGE,
                "Only the Kotlin standard library is allowed to use the 'kotlin' package",
                getMessageLocation(file),
            )
            return false
        }
    }
    return true
}

fun checkKotlinPackageUsageForPsi(
    configuration: CompilerConfiguration,
    files: Collection<KtFile>,
): Boolean {
    return checkKotlinPackageUsage(
        configuration, files,
        getPackage = { it.packageFqName },
        getMessageLocation = { MessageUtil.psiElementToMessageLocation(it.packageDirective!!) },
    )
}

fun checkKotlinPackageUsageForLightTree(
    configuration: CompilerConfiguration,
    files: Collection<FirFile>,
): Boolean {
    return checkKotlinPackageUsage(
        configuration, files,
        getPackage = { it.packageFqName },
        getMessageLocation = { it.packageDirective.source?.getLocationWithin(it) },
    )
}

private fun KtSourceElement.getLocationWithin(file: FirFile): CompilerMessageLocationWithRange? {
    val sourceFile = file.sourceFile ?: return null
    val [startLine, startColumn] = file.getLineAndColumnStartingWithOnesAt(startOffset) ?: return null
    val [endLine, endColumn] = file.getLineAndColumnStartingWithOnesAt(endOffset) ?: return null
    return CompilerMessageLocationWithRange.create(sourceFile.path, startLine, startColumn, endLine, endColumn, text?.toString())
}

private fun FirFile.getLineAndColumnStartingWithOnesAt(offset: Int?): Pair<Int, Int>? {
    return offset?.let { sourceFileLinesMapping?.getLineAndColumnByOffsetStartingWithOnes(it) }
}

private fun KtSourceFileLinesMapping.getLineAndColumnByOffsetStartingWithOnes(startOffset: Int): Pair<Int, Int> {
    val [line, column] = getLineAndColumnByOffset(startOffset)
    return line + 1 to column + 1
}

fun createPerformanceManagerFor(platform: TargetPlatform): PerformanceManager =
    object : PerformanceManager(platform, "Kotlin to ${if (platform.isCommon()) "Metadata" else platform.first().platformName} compiler") {}

fun disposeRootInWriteAction(disposable: Disposable) {
    val app = ApplicationManager.getApplicationOrNull()
    if (app != null) {
        app.runWriteAction {
            Disposer.dispose(disposable)
        }
    } else {
        Disposer.dispose(disposable)
    }
}

fun CompilerConfiguration.reportCompilationException(e: CompilationException) {
    report(
        CliDiagnostics.COMPILER_EXCEPTION,
        e.stackTraceToString(),
        CompilerMessageLocation.create(
            path = e.path,
            line = e.line,
            column = e.column,
            lineContent = e.content
        )
    )
}
