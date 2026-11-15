/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.common.messages

interface MessageRenderer {
    fun renderPreamble(): String
    fun render(severity: CompilerMessageSeverity, message: String, location: CompilerMessageSourceLocation?): String
    fun renderUsage(usage: String): String
    fun renderConclusion(): String
    fun getName(): String

    companion object {
        val PLAIN_FULL_PATHS: MessageRenderer = PlainTextMessageRenderer(false)
        val PLAIN_RELATIVE_PATHS: MessageRenderer = PlainTextMessageRenderer(true)
        val WITHOUT_PATHS: MessageRenderer = object : PlainTextMessageRenderer(false) {
            override fun getPath(location: CompilerMessageSourceLocation): String? = null
            override fun getName(): String = "WithoutPaths"
        }
    }
}

private open class PlainTextMessageRenderer(private val relativePaths: Boolean) : MessageRenderer {
    override fun renderPreamble(): String = ""

    override fun render(
        severity: CompilerMessageSeverity,
        message: String,
        location: CompilerMessageSourceLocation?,
    ): String = buildString {
        val path = location?.let { getPath(it) }
        if (path != null) {
            append(path)
            if (location.line > 0) {
                append(":${location.line}")
                if (location.column > 0) {
                    append(":${location.column}")
                }
            }
            append(": ")
        }
        append(severity.presentableName)
        append(": ")
        append(message)
    }

    protected open fun getPath(location: CompilerMessageSourceLocation): String? = location.path

    override fun renderUsage(usage: String): String = usage

    override fun renderConclusion(): String = ""

    override fun getName(): String = if (relativePaths) "PlainRelativePaths" else "PlainFullPaths"
}
