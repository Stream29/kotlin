/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.common.fir

import org.jetbrains.kotlin.KtInMemoryTextSourceFile
import org.jetbrains.kotlin.KtIoFileSourceFile
import org.jetbrains.kotlin.KtPsiSourceFile
import org.jetbrains.kotlin.KtVirtualFileSourceFile
import org.jetbrains.kotlin.SourceFileReader as InputStreamReader
import org.jetbrains.kotlin.cli.common.messages.*
import org.jetbrains.kotlin.cli.common.renderDiagnosticInternalName
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.MessageCollectorAccess
import org.jetbrains.kotlin.config.messageCollector
import org.jetbrains.kotlin.diagnostics.*
import org.jetbrains.kotlin.diagnostics.impl.BaseDiagnosticsCollector
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirErrors
import kotlin.AutoCloseable as Closeable

object FirDiagnosticsCompilerResultsReporter {
    @OptIn(MessageCollectorAccess::class)
    fun reportToMessageCollector(
        diagnosticsCollector: BaseDiagnosticsCollector,
        configuration: CompilerConfiguration,
    ): Boolean {
        return reportToMessageCollector(diagnosticsCollector, configuration.messageCollector, configuration.renderDiagnosticInternalName)
    }

    fun reportToMessageCollector(
        diagnosticsCollector: BaseDiagnosticsCollector,
        messageCollector: MessageCollector,
        renderDiagnosticName: Boolean,
    ): Boolean {
        return reportByFile(diagnosticsCollector) { diagnostic, location ->
            reportDiagnosticToMessageCollector(diagnostic, location, messageCollector, renderDiagnosticName)
        }.also {
            reportSpecialErrors(
                diagnosticsCollector.diagnostics.any { it.factory == FirErrors.INCOMPATIBLE_CLASS },
                diagnosticsCollector.diagnostics.any { it.factory == FirErrors.PRE_RELEASE_CLASS },
                diagnosticsCollector.diagnostics.any { it.factory == FirErrors.IR_WITH_UNSTABLE_ABI_COMPILED_CLASS },
                messageCollector,
            )
        }
    }

    private fun reportSpecialErrors(
        hasIncompatibleClasses: Boolean,
        hasPrereleaseClasses: Boolean,
        hasUnstableClasses: Boolean,
        messageCollector: MessageCollector,
    ) {
        if (hasIncompatibleClasses) {
            messageCollector.report(
                CompilerMessageSeverity.ERROR,
                "Incompatible classes were found in dependencies. " +
                        "Remove them from the classpath or use '-Xskip-metadata-version-check' to suppress errors"
            )
        }

        if (hasPrereleaseClasses) {
            messageCollector.report(
                CompilerMessageSeverity.ERROR,
                "Pre-release declarations were found in dependencies. Please exclude the dependencies with such declarations " +
                        "and recompile with a release compiler, or use '-Xskip-prerelease-check' to suppress errors. " +
                        "Note that in the latter case the compiled declarations will also be marked as pre-release."
            )
        }

        if (hasUnstableClasses) {
            messageCollector.report(
                CompilerMessageSeverity.ERROR,
                "Classes compiled by an unstable version of the Kotlin compiler were found in dependencies. " +
                        "Remove them from the classpath or use '-Xallow-unstable-dependencies' to suppress errors"
            )
        }
    }

    fun throwFirstErrorAsException(
        diagnosticsCollector: BaseDiagnosticsCollector, messageRenderer: MessageRenderer = MessageRenderer.PLAIN_RELATIVE_PATHS
    ): Boolean {
        return reportByFile(diagnosticsCollector) { diagnostic, location ->
            throwErrorDiagnosticAsException(diagnostic, location, messageRenderer)
        }
    }

    fun reportByFile(
        diagnosticsCollector: BaseDiagnosticsCollector, report: (KtDiagnostic, CompilerMessageSourceLocation?) -> Unit
    ): Boolean {
        var hasErrors = false
        for (sourceFile in diagnosticsCollector.diagnosticsByFile.keys) {
            val positionFinder = lazy {
                when (sourceFile) {
                    is KtVirtualFileSourceFile,
                    is KtInMemoryTextSourceFile,
                    is KtPsiSourceFile,
                        -> SequentialCloseablePositionFinder(InputStreamReader(sourceFile.getContentsAsStream()))
                    is KtIoFileSourceFile ->
                        if (sourceFile.file.isFile)
                            SequentialCloseablePositionFinder(InputStreamReader(sourceFile.getContentsAsStream()))
                        else null
                    null -> null
                    else -> error("Unexpected source file type: $sourceFile")
                }
            }

            try {
                val diagnosticList = diagnosticsCollector.diagnosticsByFile[sourceFile].orEmpty()

                // Precomputing positions of the offsets in the ascending order of the offsets
                val offsetsToPositions = positionFinder.value?.let { finder ->
                    val sortedOffsets = mutableSetOf<Int>().apply {
                        for (diagnostic in diagnosticList) {
                            if (diagnostic is KtDiagnosticWithSource && diagnostic !is KtPsiDiagnostic) {
                                val range = diagnostic.firstRange
                                add(range.startOffset)
                                add(range.endOffset)
                            }
                        }
                    }.sorted()
                    sortedOffsets.associateWith { finder.findNextPosition(it) }
                }

                for (diagnostic in diagnosticList.sortedWith(InFileDiagnosticsComparator)) {
                    val location = when (diagnostic) {
                        is KtDiagnosticWithoutSource -> diagnostic.location
                        is KtDiagnosticWithSource -> when (diagnostic) {
                            is KtPsiDiagnostic -> {
                                val file = diagnostic.element.psi.containingFile!!
                                val document = file.viewProvider.document
                                val lineAndColumnRange = if (document != null && diagnostic.textRanges.isNotEmpty()) {
                                    val firstRange = diagnostic.textRanges.minBy { it.startOffset }
                                    PsiDiagnosticUtils.LineAndColumnRange(
                                        PsiDiagnosticUtils.offsetToLineAndColumn(document, firstRange.startOffset),
                                        PsiDiagnosticUtils.offsetToLineAndColumn(document, firstRange.endOffset),
                                    )
                                } else {
                                    PsiDiagnosticUtils.LineAndColumnRange.NONE
                                }
                                MessageUtil.psiFileToMessageLocation(
                                    file,
                                    file.name,
                                    lineAndColumnRange
                                )
                            }
                            else -> {
                                offsetsToPositions?.let {
                                    val range = diagnostic.firstRange
                                    val start = offsetsToPositions[range.startOffset]!!
                                    val end = offsetsToPositions[range.endOffset]!!
                                    MessageUtil.createMessageLocation(
                                        sourceFile?.path, start.lineContent, start.line, start.column, end.line, end.column
                                    )
                                }
                            }
                        }
                    }
                    report(diagnostic, location)
                    hasErrors = hasErrors || diagnostic.severity == Severity.ERROR
                }
            } finally {
                if (positionFinder.isInitialized()) {
                    positionFinder.value?.close()
                }
            }
        }
        return hasErrors
    }

    fun reportDiagnosticToMessageCollector(
        diagnostic: KtDiagnostic,
        location: CompilerMessageSourceLocation?,
        reporter: MessageCollector,
        renderDiagnosticName: Boolean
    ) {
        val severity = diagnostic.severity.toCompilerMessageSeverity()
        val message = diagnostic.renderMessage()
        val diagnosticId = diagnostic.factory.name
        val textToRender = when (renderDiagnosticName) {
            true -> "[$diagnosticId] $message"
            false -> message
        }

        reporter.report(severity, textToRender, location, diagnosticId)
    }

    @OptIn(MessageCollectorAccess::class)
    fun reportDiagnosticToConfiguration(
        diagnostic: KtDiagnostic,
        location: CompilerMessageSourceLocation?,
        configuration: CompilerConfiguration,
        renderDiagnosticName: Boolean
    ) {
        reportDiagnosticToMessageCollector(diagnostic, location, configuration.messageCollector, renderDiagnosticName)
    }

    private fun throwErrorDiagnosticAsException(
        diagnostic: KtDiagnostic,
        location: CompilerMessageSourceLocation?,
        messageRenderer: MessageRenderer
    ) {
        if (diagnostic.severity == Severity.ERROR) {
            val severity = diagnostic.severity.toCompilerMessageSeverity()
            val message = diagnostic.renderMessage()
            val diagnosticText = messageRenderer.render(severity, message, location)
            throw IllegalStateException("${diagnostic.factory.name}: $diagnosticText")
        }
    }

    private object InFileDiagnosticsComparator : Comparator<KtDiagnostic> {
        override fun compare(o1: KtDiagnostic, o2: KtDiagnostic): Int {
            val range1 = o1.firstRange
            val range2 = o2.firstRange

            return if (range1 != range2) {
                DiagnosticRangeUtils.TEXT_RANGE_COMPARATOR.compare(range1, range2)
            } else o1.factory.name.compareTo(o2.factory.name)
        }
    }
}

fun BaseDiagnosticsCollector.reportToMessageCollector(messageCollector: MessageCollector, renderDiagnosticName: Boolean) {
    FirDiagnosticsCompilerResultsReporter.reportToMessageCollector(this, messageCollector, renderDiagnosticName)
}

class KtSourceFileDiagnosticPos(val line: Int, val column: Int, val lineContent: String?) {
    override fun toString(): String = if (line < 0) "(offset: $column line unknown)" else "($line,$column)"

    companion object {
        val NONE = KtSourceFileDiagnosticPos(-1, -1, null)
    }
}

private class SequentialCloseablePositionFinder(private val reader: InputStreamReader) : Closeable, SequentialPositionFinder(reader) {
    override fun close() {
        reader.close()
    }
}


open class SequentialPositionFinder(private val reader: InputStreamReader) {
    private var currentLineContent: String? = null
    private val buffer = CharArray(255)
    private var bufLength = -1
    private var bufPos = 0
    private var endOfStream = false
    private var skipNextLf = false

    private var charsRead = 0
    private var currentLine = 0

    fun findNextPosition(offset: Int, withLineContents: Boolean = true): KtSourceFileDiagnosticPos {
        fun posInCurrentLine(): KtSourceFileDiagnosticPos? {
            val col = offset - (charsRead - currentLineContent!!.length - 1) + 1
            assert(col > 0)
            return if (col <= currentLineContent!!.length + 1)
                KtSourceFileDiagnosticPos(currentLine, col, if (withLineContents) currentLineContent else null)
            else null
        }

        if (offset < charsRead) {
            return posInCurrentLine()!!
        }

        while (true) {
            if (currentLineContent == null) {
                currentLineContent = readNextLine()
            }

            posInCurrentLine()?.let { return@findNextPosition it }

            if (endOfStream) return KtSourceFileDiagnosticPos(-1, offset, if (withLineContents) currentLineContent else null)

            currentLineContent = null
        }
    }

    private fun readNextLine() = buildString {
        while (true) {
            if (bufPos >= bufLength) {
                bufLength = reader.read(buffer)
                bufPos = 0
                if (bufLength < 0) {
                    endOfStream = true
                    currentLine++
                    charsRead++
                    break
                }
            } else {
                val c = buffer[bufPos++]
                charsRead++
                when {
                    c == '\n' && skipNextLf -> {
                        charsRead--
                        skipNextLf = false
                    }
                    c == '\n' || c == '\r' -> {
                        currentLine++
                        skipNextLf = c == '\r'
                        break
                    }
                    else -> {
                        append(c)
                        skipNextLf = false
                    }
                }
            }
        }
    }
}
