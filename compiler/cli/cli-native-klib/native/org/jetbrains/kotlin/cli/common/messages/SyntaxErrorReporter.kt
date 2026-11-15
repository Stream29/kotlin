/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.common.messages

import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.KtPsiSourceFile
import org.jetbrains.kotlin.KtRealPsiSourceElement
import org.jetbrains.kotlin.KtSourceFile
import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.config.LanguageVersionSettingsImpl
import org.jetbrains.kotlin.diagnostics.DiagnosticContext
import org.jetbrains.kotlin.diagnostics.InternalDiagnosticFactoryMethod
import org.jetbrains.kotlin.diagnostics.KtDiagnostic
import org.jetbrains.kotlin.diagnostics.impl.BaseDiagnosticsCollector
import org.jetbrains.kotlin.fir.builder.FirSyntaxErrors
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

object SyntaxErrorReporter {
    class SyntaxErrorReport(val isHasErrors: Boolean, val isAllErrorsAtEof: Boolean)

    fun reportSyntaxErrors(file: PsiElement, diagnosticCollector: BaseDiagnosticsCollector): SyntaxErrorReport {
        return reportSyntaxErrors(file) { element, message ->
            @OptIn(InternalDiagnosticFactoryMethod::class)
            val diagnostic = FirSyntaxErrors.SYNTAX.on(
                KtRealPsiSourceElement(element),
                message,
                positioningStrategy = null,
                DiagnosticContext.Default,
            )
            val context = object : DiagnosticContext {
                override val containingFile: KtSourceFile
                    get() = KtPsiSourceFile(file.containingFile!!)

                override fun isDiagnosticSuppressed(diagnostic: KtDiagnostic): Boolean {
                    return false
                }

                override val languageVersionSettings: LanguageVersionSettings
                    get() = LanguageVersionSettingsImpl.DEFAULT
            }
            diagnosticCollector.report(diagnostic, context)
        }
    }

    private fun reportSyntaxErrors(
        file: PsiElement,
        createAndReportSyntaxError: (PsiErrorElement, message: String) -> Unit,
    ): SyntaxErrorReport {
        class ErrorReportingVisitor : KtTreeVisitorVoid() {
            var hasErrors = false
            var allErrorsAtEof = true

            private fun PsiElement.isAtEof(): Boolean {
                var element = this
                while (true) {
                    element = element.nextSibling ?: return true
                    if (element !is PsiWhiteSpace || element !is PsiComment) return false
                }
            }

            override fun visitErrorElement(element: PsiErrorElement) {
                val description = element.errorDescription
                if (allErrorsAtEof && !element.isAtEof()) {
                    allErrorsAtEof = false
                }
                hasErrors = true
                createAndReportSyntaxError(
                    element,
                    if (StringUtil.isEmpty(description)) "Syntax error" else description
                )
            }
        }

        val visitor = ErrorReportingVisitor()
        file.accept(visitor)
        return SyntaxErrorReport(visitor.hasErrors, visitor.allErrorsAtEof)
    }
}
