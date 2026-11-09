/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin.lexer


import nativekotlin.host.text.jdkCharacterType



internal fun lexerCodePointAt(text: CharSequence, index: Int): Int {
    val high = text[index].code
    if (high in 0xD800..0xDBFF && index + 1 < text.length) {
        val low = text[index + 1].code
        if (low in 0xDC00..0xDFFF) return ((high - 0xD800) shl 10) + low - 0xDC00 + 0x10000
    }
    return high
}

internal fun lexerCodePointBefore(text: CharSequence, index: Int): Int {
    val low = text[index - 1].code
    if (low in 0xDC00..0xDFFF && index > 1) {
        val high = text[index - 2].code
        if (high in 0xD800..0xDBFF) return ((high - 0xD800) shl 10) + low - 0xDC00 + 0x10000
    }
    return low
}

internal fun lexerCharCount(codePoint: Int): Int = if (codePoint >= 0x10000) 2 else 1

internal fun lexerOffsetByCodePoints(text: CharSequence, index: Int, codePointOffset: Int): Int {
    if (index < 0 || index > text.length) throw IndexOutOfBoundsException()
    var result = index
    if (codePointOffset >= 0) {
        repeat(codePointOffset) {
            if (result >= text.length) throw IndexOutOfBoundsException()
            result += lexerCharCount(lexerCodePointAt(text, result))
        }
    } else {
        var remaining = codePointOffset
        while (remaining < 0) {
            if (result <= 0) throw IndexOutOfBoundsException()
            result -= lexerCharCount(lexerCodePointBefore(text, result))
            remaining++
        }
    }
    return result
}

internal fun lexerIsWhitespace(char: Char): Boolean {
    val code = char.code
    if (code in 9..13 || code in 28..31) return true
    return when (jdkCharacterType(code)) {
        12 -> code != 0xA0 && code != 0x2007 && code != 0x202F
        13, 14 -> true
        else -> false
    }
}

/** Only the generated KDoc look-ahead scratch bit set; not a java.util substitute. */
internal class LexerBitSet(requestedSize: Int) {
    private val bits = BooleanArray(((requestedSize + 63) / 64) * 64)
    val size: Int get() = bits.size
    operator fun get(index: Int): Boolean = index < bits.size && bits[index]
    fun set(index: Int) { bits[index] = true }
    fun set(index: Int, value: Boolean) { bits[index] = value }
    fun clear(index: Int) { bits[index] = false }
    fun clear() { bits.fill(false) }
}
