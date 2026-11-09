/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli

import org.jetbrains.kotlin.KtSourceFile
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.config.MessageCollectorAccess
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.config.messageCollector
import org.jetbrains.kotlin.diagnostics.DiagnosticContext
import org.jetbrains.kotlin.diagnostics.KtDiagnostic
import org.jetbrains.kotlin.diagnostics.KtSourcelessDiagnosticFactory
import org.jetbrains.kotlin.diagnostics.Severity

fun CompilerConfiguration.report(
    factory: KtSourcelessDiagnosticFactory,
    message: String,
    location: CompilerMessageSourceLocation? = null,
) {
    val currentLanguageVersionSettings = this.languageVersionSettings
    val context = object : DiagnosticContext {
        override val containingFile: KtSourceFile?
            get() = null

        override fun isDiagnosticSuppressed(diagnostic: KtDiagnostic): Boolean = false

        override val languageVersionSettings: LanguageVersionSettings
            get() = currentLanguageVersionSettings
    }
    val diagnostic = factory.create(message, location, context) ?: return
    val diagnosticsCollector = this.get(CLIConfigurationKeys.DIAGNOSTICS_COLLECTOR)
    if (diagnosticsCollector != null) {
        diagnosticsCollector.report(diagnostic, context)
    } else {
        val severity = when (diagnostic.severity) {
            Severity.ERROR -> CompilerMessageSeverity.ERROR
            Severity.WARNING -> CompilerMessageSeverity.WARNING
            Severity.INFO -> CompilerMessageSeverity.INFO
            else -> CompilerMessageSeverity.STRONG_WARNING
        }
        @OptIn(MessageCollectorAccess::class)
        this.messageCollector.report(severity, diagnostic.renderMessage(), location)
    }
}
