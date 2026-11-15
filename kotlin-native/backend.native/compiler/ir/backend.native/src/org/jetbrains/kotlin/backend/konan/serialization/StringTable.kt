/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.konan.serialization

internal class ByteArrayStream(val buf: ByteArray) {
    private var offset = 0

    fun hasData() = offset < buf.size

    fun readInt(): Int {
        val b0 = buf[offset++].toInt() and 0xFF
        val b1 = buf[offset++].toInt() and 0xFF
        val b2 = buf[offset++].toInt() and 0xFF
        val b3 = buf[offset++].toInt() and 0xFF
        return b0 or (b1 shl 8) or (b2 shl 16) or (b3 shl 24)
    }

    fun writeInt(value: Int) {
        buf[offset++] = (value and 0xFF).toByte()
        buf[offset++] = ((value ushr 8) and 0xFF).toByte()
        buf[offset++] = ((value ushr 16) and 0xFF).toByte()
        buf[offset++] = ((value ushr 24) and 0xFF).toByte()
    }

    fun readString(length: Int): String {
        val chars = CharArray(length) {
            val lo = buf[offset++].toInt() and 0xFF
            val hi = buf[offset++].toInt() and 0xFF
            ((hi shl 8) or lo).toChar()
        }
        return chars.concatToString()
    }

    fun writeString(string: String) {
        for (i in 0 until string.length) {
            val c = string[i].code
            buf[offset++] = (c and 0xFF).toByte()
            buf[offset++] = ((c ushr 8) and 0xFF).toByte()
        }
    }

    fun readIntArray(): IntArray {
        val size = readInt()
        return IntArray(size) { readInt() }
    }

    fun writeIntArray(array: IntArray) {
        writeInt(array.size)
        for (v in array) {
            writeInt(v)
        }
    }
}

internal class StringTableBuilder {
    private val indices = mutableMapOf<String, Int>()
    private var index = 0

    operator fun String.unaryPlus() {
        this@StringTableBuilder.indices.getOrPut(this) { index++ }
    }

    fun build() = StringTable(indices)
}

internal inline fun buildStringTable(block: StringTableBuilder.() -> Unit): StringTable {
    val builder = StringTableBuilder()
    builder.block()
    return builder.build()
}

internal class StringTable(val indices: Map<String, Int>) {
    val sizeBytes: Int get() = Int.SIZE_BYTES + indices.keys.sumOf { Int.SIZE_BYTES + it.length * Char.SIZE_BYTES }

    fun serialize(stream: ByteArrayStream) {
        val lengths = IntArray(indices.size)
        val strings = Array(indices.size) { "" }
        indices.forEach { [string, index] ->
            lengths[index] = string.length
            strings[index] = string
        }
        stream.writeIntArray(lengths)
        strings.forEach { stream.writeString(it) }
    }

    companion object {
        fun deserialize(stream: ByteArrayStream): Array<String> {
            val lengths = stream.readIntArray()
            return Array(lengths.size) { stream.readString(lengths[it]) }
        }
    }
}