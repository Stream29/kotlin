/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Copyright 2008 Google Inc.
 * Use of this source code is governed by the Apache 2.0 and Protobuf BSD licenses.
 */

package org.jetbrains.kotlin.protobuf

class ByteString private constructor(private val bytes: ByteArray) : Iterable<Byte> {

    fun size(): Int = bytes.size
    val size: Int get() = bytes.size
    fun isEmpty(): Boolean = bytes.isEmpty()

    fun byteAt(index: Int): Byte = bytes[index]

    fun toByteArray(): ByteArray = bytes.copyOf()

    fun toStringUtf8(): String = if (bytes.isEmpty()) "" else decodeUtf8(bytes, 0, bytes.size)

    fun isValidUtf8(): Boolean = isValidUtf8(bytes, 0, bytes.size)

    fun concat(other: ByteString): ByteString {
        if (other.bytes.isEmpty()) return this
        if (this.bytes.isEmpty()) return other
        require(other.bytes.size <= Int.MAX_VALUE - this.bytes.size) { "ByteString size overflow" }
        val combined = ByteArray(this.bytes.size + other.bytes.size)
        this.bytes.copyInto(combined, 0, 0, this.bytes.size)
        other.bytes.copyInto(combined, this.bytes.size, 0, other.bytes.size)
        return ByteString(combined)
    }

    fun newCodedInput(): CodedInputStream = CodedInputStream.newInstance(bytes)

    fun writeTo(output: CodedOutputStream) {
        if (bytes.isNotEmpty()) {
            output.writeRawBytes(bytes, 0, bytes.size)
        }
    }

    override fun iterator(): ByteIterator = object : ByteIterator() {
        private var index = 0
        override fun hasNext(): Boolean = index < bytes.size
        override fun nextByte(): Byte = bytes[index++]
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ByteString) return false
        if (bytes.size != other.bytes.size) return false
        for (i in bytes.indices) {
            if (bytes[i] != other.bytes[i]) return false
        }
        return true
    }

    private var hash = 0
    override fun hashCode(): Int {
        var h = hash
        if (h == 0) {
            val size = bytes.size
            h = size
            for (i in 0 until size) {
                h = h * 31 + bytes[i].toInt()
            }
            if (h == 0) h = 1
            hash = h
        }
        return h
    }

    override fun toString(): String =
        "<ByteString@${hashCode()} size=${bytes.size}>"

    class Output(initialCapacity: Int = 32) {
        private var buffer = ByteArray(initialCapacity)
        private var count = 0

        private fun ensureCapacity(extra: Int) {
            require(extra >= 0 && extra <= Int.MAX_VALUE - count) { "ByteString size overflow" }
            if (count + extra > buffer.size) {
                buffer = buffer.copyOf(maxOf(minOf(buffer.size.toLong() * 2, Int.MAX_VALUE.toLong()).toInt(), count + extra))
            }
        }

        fun write(b: Int) {
            ensureCapacity(1)
            buffer[count++] = b.toByte()
        }

        fun write(b: ByteArray, off: Int = 0, len: Int = b.size) {
            require(off >= 0 && len >= 0 && off <= b.size - len)
            if (len == 0) return
            ensureCapacity(len)
            b.copyInto(buffer, count, off, off + len)
            count += len
        }

        fun size(): Int = count

        fun toByteString(): ByteString = if (count == 0) EMPTY else ByteString(buffer.copyOf(count))

        fun toByteArray(): ByteArray = buffer.copyOf(count)

        fun reset() {
            count = 0
        }
    }

    companion object {
        val EMPTY = ByteString(ByteArray(0))

        internal fun wrap(bytes: ByteArray): ByteString =
            if (bytes.isEmpty()) EMPTY else ByteString(bytes)

        fun copyFrom(bytes: ByteArray): ByteString =
            if (bytes.isEmpty()) EMPTY else ByteString(bytes.copyOf())

        fun copyFrom(bytes: ByteArray, offset: Int, size: Int): ByteString {
            require(offset >= 0 && size >= 0 && offset <= bytes.size - size)
            if (size == 0) return EMPTY
            return ByteString(bytes.copyOfRange(offset, offset + size))
        }

        fun copyFromUtf8(text: String): ByteString =
            if (text.isEmpty()) EMPTY else ByteString(encodeUtf8(text))

        fun newOutput(initialCapacity: Int = 32): Output = Output(initialCapacity)

        internal fun computeUtf8Size(text: String): Int {
            val len = text.length
            var utf8Length = len
            var i = 0
            while (i < len && text[i].code < 0x80) {
                i++
            }
            while (i < len) {
                val c = text[i].code
                if (c < 0x80) {
                    i++
                } else if (c < 0x800) {
                    utf8Length += 1
                    i++
                } else if (c in 0xd800..0xdfff) {
                    if (c <= 0xdbff && i + 1 < len && text[i + 1].code in 0xdc00..0xdfff) {
                        utf8Length += 2
                        i += 2
                    } else {
                        i++
                    }
                } else {
                    utf8Length += 2
                    i++
                }
            }
            require(utf8Length >= len) { "UTF-8 length overflow" }
            return utf8Length
        }

        internal fun encodeUtf8Into(text: String, dst: ByteArray, offset: Int): Int {
            val len = text.length
            var pos = offset
            var i = 0
            while (i < len) {
                val c = text[i].code
                if (c >= 0x80) break
                dst[pos++] = c.toByte()
                i++
            }
            if (i == len) return pos
            while (i < len) {
                val c = text[i++].code
                if (c < 0x80) {
                    dst[pos++] = c.toByte()
                } else if (c < 0x800) {
                    dst[pos++] = (0xc0 or (c ushr 6)).toByte()
                    dst[pos++] = (0x80 or (c and 0x3f)).toByte()
                } else if (c in 0xd800..0xdfff) {
                    if (c <= 0xdbff && i < len) {
                        val low = text[i].code
                        if (low in 0xdc00..0xdfff) {
                            i++
                            val codePoint = 0x10000 + ((c - 0xd800) shl 10) + (low - 0xdc00)
                            dst[pos++] = (0xf0 or (codePoint ushr 18)).toByte()
                            dst[pos++] = (0x80 or ((codePoint ushr 12) and 0x3f)).toByte()
                            dst[pos++] = (0x80 or ((codePoint ushr 6) and 0x3f)).toByte()
                            dst[pos++] = (0x80 or (codePoint and 0x3f)).toByte()
                            continue
                        }
                    }
                    dst[pos++] = '?'.code.toByte()
                } else {
                    dst[pos++] = (0xe0 or (c ushr 12)).toByte()
                    dst[pos++] = (0x80 or ((c ushr 6) and 0x3f)).toByte()
                    dst[pos++] = (0x80 or (c and 0x3f)).toByte()
                }
            }
            return pos
        }

        internal fun encodeUtf8(text: String): ByteArray {
            if (text.isEmpty()) return ByteArray(0)
            val size = computeUtf8Size(text)
            val out = ByteArray(size)
            encodeUtf8Into(text, out, 0)
            return out
        }

        internal fun isValidUtf8(bytes: ByteArray, start: Int, size: Int): Boolean {
            val end = start + size
            var i = start
            while (i < end) {
                val b1 = bytes[i].toInt()
                if (b1 >= 0) {
                    i++
                    continue
                }
                val lead = b1 and 0xff
                if (lead < 0xc2) return false
                if (lead <= 0xdf) {
                    if (i + 1 >= end) return false
                    val b2 = bytes[i + 1].toInt() and 0xff
                    if (b2 !in 0x80..0xbf) return false
                    i += 2
                } else if (lead <= 0xef) {
                    if (i + 2 >= end) return false
                    val b2 = bytes[i + 1].toInt() and 0xff
                    val b3 = bytes[i + 2].toInt() and 0xff
                    if (b2 !in 0x80..0xbf || b3 !in 0x80..0xbf) return false
                    if (lead == 0xe0 && b2 < 0xa0) return false
                    if (lead == 0xed && b2 >= 0xa0) return false
                    i += 3
                } else if (lead <= 0xf4) {
                    if (i + 3 >= end) return false
                    val b2 = bytes[i + 1].toInt() and 0xff
                    val b3 = bytes[i + 2].toInt() and 0xff
                    val b4 = bytes[i + 3].toInt() and 0xff
                    if (b2 !in 0x80..0xbf || b3 !in 0x80..0xbf || b4 !in 0x80..0xbf) return false
                    if (lead == 0xf0 && b2 < 0x90) return false
                    if (lead == 0xf4 && b2 > 0x8f) return false
                    i += 4
                } else {
                    return false
                }
            }
            return true
        }

        internal fun decodeUtf8(bytes: ByteArray, start: Int, size: Int): String {
            if (size == 0) return ""
            val end = start + size
            var asciiCheck = start
            while (asciiCheck < end && bytes[asciiCheck] >= 0) {
                asciiCheck++
            }
            if (asciiCheck == end) {
                val chars = CharArray(size)
                for (k in 0 until size) {
                    chars[k] = bytes[start + k].toInt().toChar()
                }
                return chars.concatToString()
            }
            return buildString(size) {
                for (k in start until asciiCheck) {
                    append(bytes[k].toInt().toChar())
                }
                var i = asciiCheck
                fun byte(index: Int) = bytes[index].toInt() and 255
                outer@ while (i < end) {
                    val lead = byte(i)
                    if (lead < 128) {
                        append(lead.toChar())
                        i++
                        continue
                    }
                    val width = when (lead) {
                        in 0xc2..0xdf -> 2
                        in 0xe0..0xef -> 3
                        in 0xf0..0xf4 -> 4
                        else -> 0
                    }
                    if (width == 0) {
                        append('\ufffd')
                        i++
                        continue
                    }
                    var code = lead and (127 shr width)
                    for (part in 1 until width) {
                        if (i + part >= end) {
                            append('\ufffd')
                            i = end
                            continue@outer
                        }
                        val b = byte(i + part)
                        val badSecond = part == 1 && (
                            (lead == 0xe0 && b < 0xa0) || (lead == 0xf0 && b < 0x90) || (lead == 0xf4 && b > 0x8f)
                        )
                        if (b !in 0x80..0xbf || badSecond) {
                            append('\ufffd')
                            i += part
                            continue@outer
                        }
                        code = (code shl 6) or (b and 63)
                    }
                    when {
                        code in 0xd800..0xdfff -> append('\ufffd')
                        code <= 0xffff -> append(code.toChar())
                        else -> {
                            append((0xd800 + ((code - 0x10000) shr 10)).toChar())
                            append((0xdc00 + ((code - 0x10000) and 1023)).toChar())
                        }
                    }
                    i += width
                }
            }
        }
    }
}
