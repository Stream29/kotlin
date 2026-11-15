/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.common.diagnostics

import org.jetbrains.kotlin.library.KlibIoException

/**
 * Pure Kotlin/Native implementation of the JAR `META-INF/MANIFEST.MF` main-attributes parser
 * used by `LibrarySpecialCompatibilityChecker` when inspecting JAR files on the classpath.
 *
 * Implements the JAR Manifest specification:
 * - Physical line length limit of 512 bytes (72-byte standard line length).
 * - Continuation lines starting with a single ASCII space (`0x20`) concatenated at the byte level
 *   before UTF-8 decoding so multibyte UTF-8 sequences split across continuation boundaries remain intact.
 * - Main section ends at the first blank line (`\r\n`, `\n`, or `\r`).
 * - Header syntax `Name: Value` with ASCII space after colon (`: `) and valid attribute name
 *   characters `[A-Za-z0-9_-]{1,70}`, matched case-insensitively.
 * - Duplicate attributes in the main section overwrite earlier values.
 */
class Attributes {
    private val valuesByLowerName = linkedMapOf<String, String>()

    fun getValue(name: String): String? {
        validateAttributeName(name)
        return valuesByLowerName[name.lowercase()]
    }

    internal fun putValue(name: String, value: String) {
        validateAttributeName(name)
        valuesByLowerName[name.lowercase()] = value
    }

    companion object {
        internal fun isValidAttributeChar(ch: Char): Boolean =
            ch in 'A'..'Z' || ch in 'a'..'z' || ch in '0'..'9' || ch == '_' || ch == '-'

        internal fun validateAttributeName(name: String) {
            if (name.isEmpty() || name.length > 70 || !name.all(::isValidAttributeChar)) {
                throw IllegalArgumentException("Invalid manifest attribute name: '$name'")
            }
        }
    }
}

class Manifest() {
    val mainAttributes: Attributes = Attributes()

    constructor(bytes: ByteArray) : this() {
        read(bytes)
    }

    fun read(bytes: ByteArray) {
        val physicalLines = splitPhysicalLines(bytes)
        val logicalLines = mutableListOf<ByteArray>()

        for (rawLine in physicalLines) {
            if (rawLine.size > 512) {
                throw KlibIoException("Manifest line too long (${rawLine.size} > 512 bytes)")
            }
            if (rawLine.isEmpty()) {
                // First blank line terminates the main attributes section.
                break
            }
            if (rawLine[0] == ' '.code.toByte()) {
                if (logicalLines.isEmpty()) {
                    throw KlibIoException("Manifest continuation line before any header")
                }
                val prev = logicalLines.removeAt(logicalLines.lastIndex)
                val cont = rawLine.copyOfRange(1, rawLine.size)
                val combined = ByteArray(prev.size + cont.size)
                prev.copyInto(combined, 0)
                cont.copyInto(combined, prev.size)
                logicalLines.add(combined)
            } else {
                logicalLines.add(rawLine)
            }
        }

        for (lineBytes in logicalLines) {
            val line = try {
                lineBytes.decodeToString(throwOnInvalidSequence = true)
            } catch (e: Throwable) {
                throw KlibIoException("Invalid UTF-8 in manifest header: ${e.message}", e)
            }
            val colonIndex = line.indexOf(':')
            if (colonIndex <= 0 || colonIndex + 1 >= line.length || line[colonIndex + 1] != ' ') {
                throw KlibIoException("Invalid manifest header syntax: '$line'")
            }
            val name = line.substring(0, colonIndex)
            if (name.length > 70 || !name.all(Attributes::isValidAttributeChar)) {
                throw KlibIoException("Invalid manifest header name: '$name'")
            }
            val value = line.substring(colonIndex + 2)
            mainAttributes.putValue(name, value)
        }
    }

    private fun splitPhysicalLines(bytes: ByteArray): List<ByteArray> {
        val lines = mutableListOf<ByteArray>()
        var start = 0
        var i = 0
        while (i < bytes.size) {
            val b = bytes[i].toInt() and 0xFF
            if (b == '\r'.code || b == '\n'.code) {
                lines.add(bytes.copyOfRange(start, i))
                if (b == '\r'.code && i + 1 < bytes.size && (bytes[i + 1].toInt() and 0xFF) == '\n'.code) {
                    i += 2
                } else {
                    i += 1
                }
                start = i
            } else {
                i += 1
            }
        }
        if (start < bytes.size) {
            lines.add(bytes.copyOfRange(start, bytes.size))
        }
        return lines
    }
}
