/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.common.messages

import org.jetbrains.kotlin.konan.file.*
import org.jetbrains.kotlin.util.capitalizeDecapitalize.decapitalizeAsciiOnly

val MessageRenderer.Companion.PROPERTY_KEY: String
    get() = "org.jetbrains.kotlin.cliMessageRenderer"

val MessageRenderer.Companion.GRADLE_STYLE: MessageRenderer
    get() = GradleStyleMessageRendererInstance

val MessageRenderer.Companion.XCODE_STYLE: MessageRenderer
    get() = XcodeStyleMessageRendererInstance

val MessageRenderer.Companion.XML: MessageRenderer
    get() = XmlMessageRendererInstance

class GradleStyleMessageRenderer : MessageRenderer {
    override fun render(
        severity: CompilerMessageSeverity,
        message: String,
        location: CompilerMessageSourceLocation?,
    ): String {
        val prefix = when (severity) {
            CompilerMessageSeverity.WARNING, CompilerMessageSeverity.STRONG_WARNING, CompilerMessageSeverity.FIXED_WARNING -> "w"
            CompilerMessageSeverity.ERROR, CompilerMessageSeverity.EXCEPTION -> "e"
            CompilerMessageSeverity.LOGGING, CompilerMessageSeverity.OUTPUT -> "v"
            CompilerMessageSeverity.INFO -> "i"
        }

        return buildString {
            append("$prefix: ")

            location?.apply {
                val absPath = File(path).absolutePath.replace('\\', '/')
                val fileUri = if (absPath.startsWith("/")) "file://$absPath" else "file:///$absPath"
                append(fileUri)
                if (line > 0 && column > 0) {
                    append(":$line:$column")
                }
                append(' ')
            }

            append(message)
        }
    }

    override fun renderPreamble(): String = ""

    override fun renderUsage(usage: String): String = usage

    override fun renderConclusion(): String = ""

    override fun getName(): String = "GradleStyle"
}

class XcodeStyleMessageRenderer : MessageRenderer {
    override fun render(
        severity: CompilerMessageSeverity,
        message: String,
        location: CompilerMessageSourceLocation?,
    ): String {
        val xcodeSeverity = when (severity) {
            CompilerMessageSeverity.WARNING, CompilerMessageSeverity.STRONG_WARNING, CompilerMessageSeverity.FIXED_WARNING -> "warning"
            CompilerMessageSeverity.ERROR, CompilerMessageSeverity.EXCEPTION -> "error"
            CompilerMessageSeverity.LOGGING, CompilerMessageSeverity.OUTPUT, CompilerMessageSeverity.INFO -> "note"
        }

        return buildString {
            location?.apply {
                append("$path:$line:$column: ")
            }

            append("$xcodeSeverity: $message")
        }
    }

    override fun renderPreamble(): String = ""

    override fun renderUsage(usage: String): String = usage

    override fun renderConclusion(): String = ""

    override fun getName(): String = "XcodeStyle"
}

class XmlMessageRenderer : MessageRenderer {
    override fun renderPreamble(): String = "<MESSAGES>"

    override fun render(
        severity: CompilerMessageSeverity,
        message: String,
        location: CompilerMessageSourceLocation?,
    ): String = buildString {
        val tagName = severity.presentableName
        append("<").append(tagName)
        if (location != null) {
            append(" path=\"").append(escapeXml(location.path)).append("\"")
            append(" line=\"").append(location.line).append("\"")
            append(" column=\"").append(location.column).append("\"")
        }
        append(">")
        append(escapeXml(message))
        append("</").append(tagName).append(">")
    }

    override fun renderUsage(usage: String): String =
        render(CompilerMessageSeverity.STRONG_WARNING, usage, null)

    override fun renderConclusion(): String = "</MESSAGES>"

    override fun getName(): String = "XML"

    private fun escapeXml(value: String): String = buildString(value.length) {
        for (ch in value) {
            when (ch) {
                '&' -> append("&amp;")
                '<' -> append("&lt;")
                '>' -> append("&gt;")
                '"' -> append("&quot;")
                '\'' -> append("&apos;")
                else -> append(ch)
            }
        }
    }
}

private val GradleStyleMessageRendererInstance = GradleStyleMessageRenderer()
private val XcodeStyleMessageRendererInstance = XcodeStyleMessageRenderer()
private val XmlMessageRendererInstance = XmlMessageRenderer()

internal abstract class CliPlainTextMessageRenderer : MessageRenderer {
    override fun renderPreamble(): String = ""

    override fun render(
        severity: CompilerMessageSeverity,
        message: String,
        location: CompilerMessageSourceLocation?,
    ): String {
        val result = StringBuilder()

        val line = location?.line ?: -1
        val column = location?.column ?: -1
        val lineEnd = location?.lineEnd ?: -1
        val columnEnd = location?.columnEnd ?: -1
        val lineContent = location?.lineContent

        val path = if (location != null) getPath(location) else null
        if (path != null) {
            result.append(path)
            result.append(":")
            if (line > 0) {
                result.append(line).append(":")
                if (column > 0) {
                    result.append(column).append(":")
                }
            }
            result.append(" ")
        }

        result.append(severity.presentableName)
        result.append(": ")
        result.append(decapitalizeIfNeeded(message))

        if (lineContent != null && 1 <= column && column <= lineContent.length + 1) {
            result.append('\n')
            result.append(lineContent)
            result.append('\n')
            result.append(" ".repeat(column - 1))
            if (lineEnd > line) {
                result.append("^".repeat(lineContent.length - column + 1))
            } else if (lineEnd == line && columnEnd > column) {
                result.append("^".repeat(columnEnd - column))
            } else {
                result.append("^")
            }
        }

        return result.toString()
    }

    protected abstract fun getPath(location: CompilerMessageSourceLocation): String?

    override fun renderUsage(usage: String): String = usage

    override fun renderConclusion(): String = ""

    companion object {
        private fun decapitalizeIfNeeded(message: String): String {
            if (message.startsWith("Java") || message.startsWith("Kotlin")) {
                return message
            }
            if (message.length >= 2 && message[0].isUpperCase() && message[1].isUpperCase()) {
                return message
            }
            return message.decapitalizeAsciiOnly()
        }
    }
}

internal object CliPlainFullPathsMessageRenderer : CliPlainTextMessageRenderer() {
    override fun getPath(location: CompilerMessageSourceLocation): String = location.path
    override fun getName(): String = "FullPath"
}

internal object CliPlainRelativePathsMessageRenderer : CliPlainTextMessageRenderer() {
    override fun getPath(location: CompilerMessageSourceLocation): String {
        val cwd = File(".").absoluteFile
        val cwdPath = cwd.toPath().normalize()
        val file = File(location.path)
        val filePath = file.absoluteFile.toPath().normalize()
        return if (filePath.startsWith(cwdPath)) {
            filePath.toFile().toRelativeString(cwdPath.toFile())
        } else {
            location.path
        }
    }

    override fun getName(): String = "RelativePath"
}

internal object CliWithoutPathsMessageRenderer : CliPlainTextMessageRenderer() {
    override fun getPath(location: CompilerMessageSourceLocation): String? = null
    override fun getName(): String = "Pathless"
}

fun normalizeCliMessageRenderer(renderer: MessageRenderer): MessageRenderer = when {
    renderer === MessageRenderer.PLAIN_FULL_PATHS -> CliPlainFullPathsMessageRenderer
    renderer === MessageRenderer.PLAIN_RELATIVE_PATHS -> CliPlainRelativePathsMessageRenderer
    renderer === MessageRenderer.WITHOUT_PATHS -> CliWithoutPathsMessageRenderer
    else -> renderer
}

class PrintingMessageCollector(
    private val errStream: Appendable,
    messageRenderer: MessageRenderer,
    private val verbose: Boolean,
) : MessageCollector {
    private val messageRenderer: MessageRenderer = normalizeCliMessageRenderer(messageRenderer)
    private var hasErrors: Boolean = false

    override fun clear() {
        // Do nothing, messages are already reported
    }

    override fun report(
        severity: CompilerMessageSeverity,
        message: String,
        location: CompilerMessageSourceLocation?,
    ) {
        if (!verbose && CompilerMessageSeverity.VERBOSE.contains(severity)) return

        hasErrors = hasErrors || severity.isError

        errStream.append(messageRenderer.render(severity, message, location)).append('\n')
    }

    override fun hasErrors(): Boolean = hasErrors
}
