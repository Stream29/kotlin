/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("PackageDirectoryMismatch")

package org.jetbrains.kotlin.cli.common.messages

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import nativekotlin.host.files.NativeFile as File
import org.jetbrains.kotlin.diagnostics.PsiDiagnosticUtils

class FilteringMessageCollector(
    private val messageCollector: MessageCollector,
    private val decline: (CompilerMessageSeverity) -> Boolean,
) : MessageCollectorWithDiagnosticId {
    override fun clear() {
        messageCollector.clear()
    }

    override fun report(
        severity: CompilerMessageSeverity,
        message: String,
        location: CompilerMessageSourceLocation?,
    ) {
        report(severity, message, location, null)
    }

    override fun report(
        severity: CompilerMessageSeverity,
        message: String,
        location: CompilerMessageSourceLocation?,
        diagnosticId: String?,
    ) {
        if (!decline(severity)) {
            messageCollector.report(severity, message, location, diagnosticId)
        }
    }

    override fun hasErrors(): Boolean = messageCollector.hasErrors()
}

class GroupingMessageCollector(
    private val delegate: MessageCollector,
    private val treatWarningsAsErrors: Boolean,
    private val reportAllWarnings: Boolean,
) : MessageCollectorWithDiagnosticId {
    private val groupedMessages = linkedMapOf<CompilerMessageSourceLocation?, LinkedHashSet<Message>>()

    override fun clear() {
        groupedMessages.clear()
    }

    override fun report(
        severity: CompilerMessageSeverity,
        message: String,
        location: CompilerMessageSourceLocation?,
    ) {
        report(severity, message, location, null)
    }

    override fun report(
        severity: CompilerMessageSeverity,
        message: String,
        location: CompilerMessageSourceLocation?,
        diagnosticId: String?,
    ) {
        if (severity == CompilerMessageSeverity.OUTPUT || CompilerMessageSeverity.VERBOSE.contains(severity)) {
            delegate.report(severity, message, location, diagnosticId)
        } else {
            groupedMessages.getOrPut(location) { linkedSetOf() }.add(Message(severity, message, location, diagnosticId))
        }
    }

    override fun hasErrors(): Boolean =
        delegate.hasErrors() || hasExplicitErrors() || (treatWarningsAsErrors && hasRegularWarnings())

    private fun hasExplicitErrors(): Boolean =
        groupedMessages.values.any { set -> set.any { it.severity.isError } }

    private fun hasRegularWarnings(): Boolean =
        groupedMessages.values.any { set -> set.any { it.severity.isRegularWarning } }

    fun flush() {
        val hasExplicitErrors = hasExplicitErrors()

        if (treatWarningsAsErrors && !hasExplicitErrors && hasRegularWarnings()) {
            report(CompilerMessageSeverity.ERROR, "warnings found and -Werror specified", null)
        }

        val sortedKeys = groupedMessages.keys.sortedWith(nullsFirst(CompilerMessageLocationComparator))
        for (location in sortedKeys) {
            for (message in groupedMessages[location].orEmpty()) {
                if (!hasExplicitErrors || reportAllWarnings || message.severity.isError || message.severity == CompilerMessageSeverity.STRONG_WARNING) {
                    delegate.report(message.severity, message.message, message.location, message.diagnosticId)
                }
            }
        }

        groupedMessages.clear()
    }

    private object CompilerMessageLocationComparator : Comparator<CompilerMessageSourceLocation> {
        override fun compare(o1: CompilerMessageSourceLocation, o2: CompilerMessageSourceLocation): Int {
            if (o1.column == -1 && o2.column != -1) return -1
            if (o1.column != -1 && o2.column == -1) return 1

            if (o1.line == -1 && o2.line != -1) return -1
            if (o1.line != -1 && o2.line == -1) return 1

            return o1.path.compareTo(o2.path)
        }
    }

    private class Message(
        val severity: CompilerMessageSeverity,
        val message: String,
        val location: CompilerMessageSourceLocation?,
        val diagnosticId: String?,
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Message) return false
            if (severity != other.severity) return false
            if (message != other.message) return false
            return location == other.location
        }

        override fun hashCode(): Int {
            var result = severity.hashCode()
            result = 31 * result + message.hashCode()
            result = 31 * result + (location?.hashCode() ?: 0)
            return result
        }

        override fun toString(): String =
            "[$severity] $message" + (if (location != null) " (at $location)" else " (no location)")
    }
}

object MessageCollectorUtil {
    fun reportException(messageCollector: MessageCollector, exception: Throwable) {
        messageCollector.report(CompilerMessageSeverity.EXCEPTION, OutputMessageUtil.renderException(exception), null)
    }
}

object MessageUtil {
    fun psiElementToMessageLocation(element: PsiElement?): CompilerMessageSourceLocation? {
        if (element == null) return null
        val file = element.containingFile ?: return null
        val textRange = element.textRange ?: return null
        val document = file.viewProvider.document
        val range = PsiDiagnosticUtils.LineAndColumnRange(
            PsiDiagnosticUtils.offsetToLineAndColumn(document, textRange.startOffset),
            PsiDiagnosticUtils.offsetToLineAndColumn(document, textRange.endOffset),
        )
        return psiFileToMessageLocation(file, "<no path>", range)
    }

    fun psiFileToMessageLocation(
        file: PsiFile,
        defaultValue: String?,
        range: PsiDiagnosticUtils.LineAndColumnRange,
    ): CompilerMessageSourceLocation? {
        val virtualFile = file.virtualFile
        val path = if (virtualFile != null) virtualFileToPath(virtualFile) else defaultValue
        val start = range.getStart()
        val end = range.getEnd()
        return createMessageLocation(path, start.getLineContent(), start.getLine(), start.getColumn(), end.getLine(), end.getColumn())
    }

    fun createMessageLocation(
        path: String?,
        lineContent: String?,
        line: Int,
        column: Int,
        endLine: Int,
        endColumn: Int,
    ): CompilerMessageLocationWithRange? =
        CompilerMessageLocationWithRange.create(path, line, column, endLine, endColumn, lineContent)

    fun virtualFileToPath(virtualFile: VirtualFile): String =
        virtualFile.path
}

object OutputMessageUtil {
    private const val SOURCE_FILES_PREFIX = "Sources:"
    private const val OUTPUT_FILES_PREFIX = "Output:"

    fun renderException(e: Throwable): String = e.stackTraceToString()

    fun formatOutputMessage(sourceFiles: Collection<File>, outputFile: File): String =
        "$OUTPUT_FILES_PREFIX\n${outputFile.path}\n$SOURCE_FILES_PREFIX\n${sourceFiles.joinToString("\n") { it.path }}"

    fun formatOutputMessage(sourceFilePaths: Collection<String>, outputFilePath: String): String =
        "$OUTPUT_FILES_PREFIX\n$outputFilePath\n$SOURCE_FILES_PREFIX\n${sourceFilePaths.joinToString("\n")}"

    fun parseOutputMessage(message: String): Output? {
        val strings = message.split("\n")
        if (strings.size <= 2) return null
        if (OUTPUT_FILES_PREFIX != strings[0]) return null
        return if (SOURCE_FILES_PREFIX == strings[1]) {
            Output(parseSourceFiles(strings, 2), null)
        } else {
            val outputFile = File(strings[1])
            if (SOURCE_FILES_PREFIX != strings[2]) return null
            Output(parseSourceFiles(strings, 3), outputFile)
        }
    }

    private fun parseSourceFiles(strings: List<String>, start: Int): Collection<File> {
        val sourceFiles = ArrayList<File>()
        for (i in start until strings.size) {
            sourceFiles.add(File(strings[i]))
        }
        return sourceFiles
    }

    class Output(val sourceFiles: Collection<File>, val outputFile: File?)
}
