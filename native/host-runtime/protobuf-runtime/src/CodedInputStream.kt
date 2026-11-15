/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Copyright 2008 Google Inc.
 * Use of this source code is governed by the Apache 2.0 and Protobuf BSD licenses.
 */

package org.jetbrains.kotlin.protobuf

class CodedInputStream private constructor(
    private val buffer: ByteArray,
    private val offset: Int,
    private val length: Int
) {
    private var bufferPos = offset
    private var bufferSize = offset + length
    private var lastTag = 0
    private var currentLimit = bufferSize

    private var recursionDepth = 0
    private var recursionLimit = DEFAULT_RECURSION_LIMIT
    private var sizeLimit = DEFAULT_SIZE_LIMIT

    val totalBytesRead: Int get() = bufferPos - offset

    private fun requireAvailable(size: Int) {
        if (size < 0) throw InvalidProtocolBufferException.negativeSize()
        if (size > currentLimit - bufferPos) throw InvalidProtocolBufferException.truncatedMessage()
        if (size > sizeLimit - totalBytesRead) throw InvalidProtocolBufferException.sizeLimitExceeded()
    }

    fun readTag(): Int {
        if (bufferPos >= currentLimit) {
            lastTag = 0
            return 0
        }
        val first = buffer[bufferPos].toInt()
        if (first >= 0 && totalBytesRead < sizeLimit) {
            bufferPos++
            if ((first ushr 3) == 0) {
                throw InvalidProtocolBufferException.invalidTag()
            }
            lastTag = first
            return first
        }
        lastTag = readRawVarint32()
        if (WireFormat.getTagFieldNumber(lastTag) == 0) {
            throw InvalidProtocolBufferException.invalidTag()
        }
        return lastTag
    }

    fun checkLastTagWas(value: Int) {
        if (lastTag != value) {
            throw InvalidProtocolBufferException.invalidEndTag()
        }
    }

    fun getLastTag(): Int = lastTag

    fun skipField(tag: Int): Boolean {
        when (WireFormat.getTagWireType(tag)) {
            WireFormat.WIRETYPE_VARINT -> {
                readInt64()
                return true
            }
            WireFormat.WIRETYPE_FIXED64 -> {
                readRawLittleEndian64()
                return true
            }
            WireFormat.WIRETYPE_LENGTH_DELIMITED -> {
                val size = readRawVarint32()
                skipRawBytes(size)
                return true
            }
            WireFormat.WIRETYPE_START_GROUP -> {
                skipMessage()
                checkLastTagWas(
                    WireFormat.makeTag(
                        WireFormat.getTagFieldNumber(tag),
                        WireFormat.WIRETYPE_END_GROUP
                    )
                )
                return true
            }
            WireFormat.WIRETYPE_END_GROUP -> return false
            WireFormat.WIRETYPE_FIXED32 -> {
                readRawLittleEndian32()
                return true
            }
            else -> throw InvalidProtocolBufferException.invalidWireType()
        }
    }

    fun skipField(tag: Int, output: CodedOutputStream): Boolean {
        when (WireFormat.getTagWireType(tag)) {
            WireFormat.WIRETYPE_VARINT -> {
                val value = readRawVarint64()
                output.writeRawVarint32(tag)
                output.writeRawVarint64(value)
                return true
            }
            WireFormat.WIRETYPE_FIXED64 -> {
                val value = readRawLittleEndian64()
                output.writeRawVarint32(tag)
                output.writeRawLittleEndian64(value)
                return true
            }
            WireFormat.WIRETYPE_LENGTH_DELIMITED -> {
                val size = readRawVarint32()
                requireAvailable(size)
                output.writeRawVarint32(tag)
                output.writeRawVarint32(size)
                output.writeRawBytes(buffer, bufferPos, size)
                bufferPos += size
                return true
            }
            WireFormat.WIRETYPE_START_GROUP -> {
                output.writeRawVarint32(tag)
                skipMessage(output)
                val endTag = WireFormat.makeTag(
                    WireFormat.getTagFieldNumber(tag),
                    WireFormat.WIRETYPE_END_GROUP
                )
                checkLastTagWas(endTag)
                output.writeRawVarint32(endTag)
                return true
            }
            WireFormat.WIRETYPE_END_GROUP -> return false
            WireFormat.WIRETYPE_FIXED32 -> {
                val value = readRawLittleEndian32()
                output.writeRawVarint32(tag)
                output.writeRawLittleEndian32(value)
                return true
            }
            else -> throw InvalidProtocolBufferException.invalidWireType()
        }
    }

    private fun skipMessage() {
        if (recursionDepth >= recursionLimit) throw InvalidProtocolBufferException.recursionLimitExceeded()
        recursionDepth++
        try {
            while (true) {
                val tag = readTag()
                if (tag == 0 || !skipField(tag)) break
            }
        } finally { recursionDepth-- }
    }

    private fun skipMessage(output: CodedOutputStream) {
        if (recursionDepth >= recursionLimit) throw InvalidProtocolBufferException.recursionLimitExceeded()
        recursionDepth++
        try {
            while (true) {
                val tag = readTag()
                if (tag == 0 || !skipField(tag, output)) break
            }
        } finally { recursionDepth-- }
    }

    fun readDouble(): Double = Double.fromBits(readRawLittleEndian64())
    fun readFloat(): Float = Float.fromBits(readRawLittleEndian32())
    fun readUInt64(): Long = readRawVarint64()
    fun readInt64(): Long = readRawVarint64()
    fun readInt32(): Int = readRawVarint32()
    fun readFixed64(): Long = readRawLittleEndian64()
    fun readFixed32(): Int = readRawLittleEndian32()
    fun readBool(): Boolean = readRawVarint64() != 0L
    fun readUInt32(): Int = readRawVarint32()
    fun readEnum(): Int = readRawVarint32()
    fun readSFixed32(): Int = readRawLittleEndian32()
    fun readSFixed64(): Long = readRawLittleEndian64()
    fun readSInt32(): Int = decodeZigZag32(readRawVarint32())
    fun readSInt64(): Long = decodeZigZag64(readRawVarint64())

    fun readString(): String {
        val size = readRawVarint32()
        requireAvailable(size)
        if (size == 0) return ""
        val result = ByteString.decodeUtf8(buffer, bufferPos, size)
        bufferPos += size
        return result
    }

    fun readGroup(fieldNumber: Int, builder: MessageLite.Builder, extensionRegistry: ExtensionRegistryLite) {
        if (recursionDepth >= recursionLimit) throw InvalidProtocolBufferException.recursionLimitExceeded()
        recursionDepth++
        try {
            builder.mergeFrom(this, extensionRegistry)
            checkLastTagWas(WireFormat.makeTag(fieldNumber, WireFormat.WIRETYPE_END_GROUP))
        } finally { recursionDepth-- }
    }

    fun readMessage(builder: MessageLite.Builder, extensionRegistry: ExtensionRegistryLite) {
        val length = readRawVarint32()
        if (length < 0) throw InvalidProtocolBufferException.negativeSize()
        if (recursionDepth >= recursionLimit) throw InvalidProtocolBufferException.recursionLimitExceeded()
        val oldLimit = pushLimit(length)
        recursionDepth++
        try {
            builder.mergeFrom(this, extensionRegistry)
            checkLastTagWas(0)
        } finally {
            recursionDepth--
            popLimit(oldLimit)
        }
    }

    fun <T : MessageLite> readMessage(parser: Parser<T>, extensionRegistry: ExtensionRegistryLite): T {
        val length = readRawVarint32()
        if (length < 0) throw InvalidProtocolBufferException.negativeSize()
        if (recursionDepth >= recursionLimit) throw InvalidProtocolBufferException.recursionLimitExceeded()
        val oldLimit = pushLimit(length)
        recursionDepth++
        try {
            return parser.parsePartialFrom(this, extensionRegistry).also { checkLastTagWas(0) }
        } finally {
            recursionDepth--
            popLimit(oldLimit)
        }
    }

    fun readBytes(): ByteString {
        val size = readRawVarint32()
        requireAvailable(size)
        if (size == 0) return ByteString.EMPTY
        val result = ByteString.copyFrom(buffer, bufferPos, size)
        bufferPos += size
        return result
    }

    fun readRawVarint32(): Int {
        val avail = currentLimit - bufferPos
        if (avail >= 5 && sizeLimit - totalBytesRead >= 5) {
            var pos = bufferPos
            var b = buffer[pos++].toInt()
            if (b >= 0) {
                bufferPos = pos
                return b
            }
            var result = b and 0x7F
            b = buffer[pos++].toInt()
            if (b >= 0) {
                bufferPos = pos
                return result or (b shl 7)
            }
            result = result or ((b and 0x7F) shl 7)
            b = buffer[pos++].toInt()
            if (b >= 0) {
                bufferPos = pos
                return result or (b shl 14)
            }
            result = result or ((b and 0x7F) shl 14)
            b = buffer[pos++].toInt()
            if (b >= 0) {
                bufferPos = pos
                return result or (b shl 21)
            }
            result = result or ((b and 0x7F) shl 21)
            b = buffer[pos++].toInt()
            result = result or (b shl 28)
            bufferPos = pos
            if (b >= 0) return result
            for (i in 0 until 5) {
                requireAvailable(1)
                if (buffer[bufferPos++].toInt() >= 0) return result
            }
            throw InvalidProtocolBufferException.malformedVarint()
        }
        requireAvailable(1)
        var b = buffer[bufferPos++].toInt()
        if (b >= 0) return b
        var result = b and 0x7F
        requireAvailable(1)
        b = buffer[bufferPos++].toInt()
        if (b >= 0) {
            result = result or (b shl 7)
        } else {
            result = result or ((b and 0x7F) shl 7)
            requireAvailable(1)
            b = buffer[bufferPos++].toInt()
            if (b >= 0) {
                result = result or (b shl 14)
            } else {
                result = result or ((b and 0x7F) shl 14)
                requireAvailable(1)
                b = buffer[bufferPos++].toInt()
                if (b >= 0) {
                    result = result or (b shl 21)
                } else {
                    result = result or ((b and 0x7F) shl 21)
                    requireAvailable(1)
                    b = buffer[bufferPos++].toInt()
                    result = result or (b shl 28)
                    if (b < 0) {
                        for (i in 0 until 5) {
                            requireAvailable(1)
                            if (buffer[bufferPos++].toInt() >= 0) return result
                        }
                        throw InvalidProtocolBufferException.malformedVarint()
                    }
                }
            }
        }
        return result
    }

    fun readRawVarint64(): Long {
        var shift = 0
        var result = 0L
        while (shift < 64) {
            requireAvailable(1)
            val b = buffer[bufferPos++].toLong()
            result = result or ((b and 0x7FL) shl shift)
            if ((b and 0x80L) == 0L) return result
            shift += 7
        }
        throw InvalidProtocolBufferException.malformedVarint()
    }

    fun readRawLittleEndian32(): Int {
        requireAvailable(4)
        var pos = bufferPos
        val b0 = buffer[pos++].toInt() and 0xFF
        val b1 = buffer[pos++].toInt() and 0xFF
        val b2 = buffer[pos++].toInt() and 0xFF
        val b3 = buffer[pos++].toInt() and 0xFF
        bufferPos = pos
        return b0 or (b1 shl 8) or (b2 shl 16) or (b3 shl 24)
    }

    fun readRawLittleEndian64(): Long {
        requireAvailable(8)
        var pos = bufferPos
        val b0 = buffer[pos++].toLong() and 0xFFL
        val b1 = buffer[pos++].toLong() and 0xFFL
        val b2 = buffer[pos++].toLong() and 0xFFL
        val b3 = buffer[pos++].toLong() and 0xFFL
        val b4 = buffer[pos++].toLong() and 0xFFL
        val b5 = buffer[pos++].toLong() and 0xFFL
        val b6 = buffer[pos++].toLong() and 0xFFL
        val b7 = buffer[pos++].toLong() and 0xFFL
        bufferPos = pos
        return b0 or (b1 shl 8) or (b2 shl 16) or (b3 shl 24) or
            (b4 shl 32) or (b5 shl 40) or (b6 shl 48) or (b7 shl 56)
    }

    fun pushLimit(byteLimit: Int): Int {
        if (byteLimit < 0) throw InvalidProtocolBufferException.negativeSize()
        val oldLimit = currentLimit
        if (byteLimit > oldLimit - bufferPos) {
            throw InvalidProtocolBufferException.truncatedMessage()
        }
        currentLimit = bufferPos + byteLimit
        return oldLimit
    }

    fun popLimit(oldLimit: Int) {
        require(oldLimit >= currentLimit && oldLimit <= bufferSize)
        currentLimit = oldLimit
    }

    fun getBytesUntilLimit(): Int {
        if (currentLimit == Int.MAX_VALUE) return -1
        return currentLimit - bufferPos
    }

    fun isAtEnd(): Boolean = bufferPos >= currentLimit

    fun setRecursionLimit(limit: Int): Int {
        if (limit < 0) throw IllegalArgumentException("Recursion limit cannot be negative: $limit")
        val old = recursionLimit
        recursionLimit = limit
        return old
    }

    fun setSizeLimit(limit: Int): Int {
        if (limit < 0) throw IllegalArgumentException("Size limit cannot be negative: $limit")
        val old = sizeLimit
        sizeLimit = limit
        return old
    }

    fun skipRawBytes(size: Int) {
        requireAvailable(size)
        bufferPos += size
    }

    fun readRawBytes(size: Int): ByteArray {
        requireAvailable(size)
        val bytes = buffer.copyOfRange(bufferPos, bufferPos + size)
        bufferPos += size
        return bytes
    }

    companion object {
        const val DEFAULT_RECURSION_LIMIT = 64
        const val DEFAULT_SIZE_LIMIT = 64 * 1024 * 1024

        fun decodeZigZag32(n: Int): Int = (n ushr 1) xor -(n and 1)
        fun decodeZigZag64(n: Long): Long = (n ushr 1) xor -(n and 1L)

        fun newInstance(buf: ByteArray, off: Int = 0, len: Int = buf.size): CodedInputStream {
            require(off >= 0 && len >= 0 && off <= buf.size - len)
            return CodedInputStream(buf, off, len)
        }
    }
}
