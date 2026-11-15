/*
* Copyright 2010-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.common.arguments

import org.jetbrains.kotlin.konan.file.File
import org.jetbrains.kotlin.konan.file.absolutePath
import org.jetbrains.kotlin.konan.file.toPath

const val ARGFILE_ARGUMENT = "@"

private const val SINGLE_QUOTE = '\''
private const val DOUBLE_QUOTE = '"'
private const val BACKSLASH = '\\'

private class CharSeqReader(private val text: String) {
    private var index = 0
    fun nextChar(): Char? = if (index < text.length) text[index++] else null
}

fun preprocessCommandLineArguments(args: List<String>, errors: Lazy<ArgumentParseErrors>): List<String> =
    args.flatMap { arg ->
        if (arg.isArgfileArgument) {
            File(arg.argfilePath).expand(errors.value)
        } else {
            listOf(arg)
        }
    }

fun readArgumentsFromArgFile(content: String): List<String> {
    val reader = CharSeqReader(content)
    return generateSequence { reader.parseNextArgument() }.toList()
}

private fun File.expand(errors: ArgumentParseErrors): List<String> {
    val klibPath = toPath()
    if (!klibPath.exists()) {
        errors.argfileErrors += "Argfile not found: $absolutePath"
        return emptyList()
    }
    return try {
        val content = klibPath.readBytes().decodeToString()
        readArgumentsFromArgFile(content)
    } catch (e: Throwable) {
        errors.argfileErrors += "Error while reading argfile: $e"
        emptyList()
    }
}

private fun CharSeqReader.parseNextArgument(): String? {
    val sb = StringBuilder()

    var r = nextChar()
    while (r != null && r.isWhitespace()) {
        r = nextChar()
    }

    while (r != null) {
        if (r.isWhitespace()) break

        if (r == DOUBLE_QUOTE || r == SINGLE_QUOTE) {
            consumeRestOfQuotedSequence(sb, r)
            return sb.toString()
        }

        sb.append(r)
        r = nextChar()
    }

    return sb.toString().takeIf { it.isNotEmpty() }
}

private fun CharSeqReader.consumeRestOfQuotedSequence(sb: StringBuilder, quote: Char) {
    var ch = nextChar()
    while (ch != null && ch != quote) {
        if (ch == BACKSLASH) nextChar()?.let { sb.append(it) } else sb.append(ch)
        ch = nextChar()
    }
}

private val String.argfilePath: String
    get() = removePrefix(ARGFILE_ARGUMENT)

private val String.isArgfileArgument: Boolean
    get() = startsWith(ARGFILE_ARGUMENT)
