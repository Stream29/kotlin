/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.jvm

import nativekotlin.host.text.jdkJavaIdentifierPart
import nativekotlin.host.text.jdkJavaIdentifierStart

private val JAVA_KEYWORDS = setOf(
    "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
    "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float",
    "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native",
    "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp",
    "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void",
    "volatile", "while",
    "true", "false", "null",
    "_",
)

private fun codePointAt(text: String, index: Int): Int {
    val high = text[index]
    if (high.isHighSurrogate() && index + 1 < text.length) {
        val low = text[index + 1]
        if (low.isLowSurrogate()) {
            return ((high.code - 0xD800) shl 10) + (low.code - 0xDC00) + 0x10000
        }
    }
    return high.code
}

private fun charCount(codePoint: Int): Int = if (codePoint >= 0x10000) 2 else 1

fun isValidJavaIdentifier(name: String): Boolean {
    if (name.isEmpty()) return false
    if (name in JAVA_KEYWORDS) return false

    var index = 0
    val firstCodePoint = codePointAt(name, index)
    if (!jdkJavaIdentifierStart(firstCodePoint)) return false
    index += charCount(firstCodePoint)

    while (index < name.length) {
        val codePoint = codePointAt(name, index)
        if (!jdkJavaIdentifierPart(codePoint)) return false
        index += charCount(codePoint)
    }
    return true
}
