/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Copyright 2008 Google Inc.
 * Use of this source code is governed by the Apache 2.0 and Protobuf BSD licenses.
 */

package org.jetbrains.kotlin.protobuf

object Internal {
    interface EnumLite {
        val number: Int
    }

    interface EnumLiteMap<T : EnumLite> {
        fun findValueByNumber(number: Int): T?
    }

    fun stringDefaultValue(bytes: String): String = bytes

    fun bytesDefaultValue(bytes: String): ByteString = ByteString.copyFromUtf8(bytes)

    fun isValidUtf8(bytes: ByteArray): Boolean = isValidUtf8(bytes, 0, bytes.size)

    fun isValidUtf8(bytes: ByteArray, offset: Int, length: Int): Boolean {
        var index = offset
        val end = offset + length
        while (index < end) {
            val byte1 = bytes[index++].toInt() and 0xFF
            if (byte1 < 0x80) continue
            if (byte1 in 0xC2..0xDF) {
                if (index >= end) return false
                val byte2 = bytes[index++].toInt() and 0xFF
                if (byte2 !in 0x80..0xBF) return false
            } else if (byte1 in 0xE0..0xEF) {
                if (index + 1 >= end) return false
                val byte2 = bytes[index++].toInt() and 0xFF
                if (byte2 !in 0x80..0xBF) return false
                if (byte1 == 0xE0 && byte2 < 0xA0) return false
                if (byte1 == 0xED && byte2 >= 0xA0) return false
                val byte3 = bytes[index++].toInt() and 0xFF
                if (byte3 !in 0x80..0xBF) return false
            } else if (byte1 in 0xF0..0xF4) {
                if (index + 2 >= end) return false
                val byte2 = bytes[index++].toInt() and 0xFF
                if (byte2 !in 0x80..0xBF) return false
                if (byte1 == 0xF0 && byte2 < 0x90) return false
                if (byte1 == 0xF4 && byte2 >= 0x90) return false
                val byte3 = bytes[index++].toInt() and 0xFF
                if (byte3 !in 0x80..0xBF) return false
                val byte4 = bytes[index++].toInt() and 0xFF
                if (byte4 !in 0x80..0xBF) return false
            } else {
                return false
            }
        }
        return true
    }
}
