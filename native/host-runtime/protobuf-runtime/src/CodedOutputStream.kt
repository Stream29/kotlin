/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Copyright 2008 Google Inc.
 * Use of this source code is governed by the Apache 2.0 and Protobuf BSD licenses.
 */

package org.jetbrains.kotlin.protobuf

class CodedOutputStream private constructor(
    private val buffer: ByteArray?,
    private val offset: Int,
    private val length: Int,
    private val output: ByteString.Output?
) {
    private var position = offset
    private val limit = offset + length

    val spaceLeft: Int get() = if (buffer != null) limit - position else Int.MAX_VALUE

    fun checkNoSpaceLeft() {
        if (buffer != null && spaceLeft != 0) {
            throw IllegalStateException("Did not write as much data as expected, space left: $spaceLeft")
        }
    }

    fun flush() {
        // No-op for direct byte array and ByteString.Output
    }

    fun writeTag(fieldNumber: Int, wireType: Int) {
        writeRawVarint32(WireFormat.makeTag(fieldNumber, wireType))
    }

    fun writeInt32(fieldNumber: Int, value: Int) {
        writeTag(fieldNumber, WireFormat.WIRETYPE_VARINT)
        writeInt32NoTag(value)
    }

    fun writeInt32NoTag(value: Int) {
        if (value >= 0) {
            writeRawVarint32(value)
        } else {
            writeRawVarint64(value.toLong())
        }
    }

    fun writeInt64(fieldNumber: Int, value: Long) {
        writeTag(fieldNumber, WireFormat.WIRETYPE_VARINT)
        writeRawVarint64(value)
    }

    fun writeUInt64(fieldNumber: Int, value: Long) {
        writeTag(fieldNumber, WireFormat.WIRETYPE_VARINT)
        writeRawVarint64(value)
    }

    fun writeUInt32(fieldNumber: Int, value: Int) {
        writeTag(fieldNumber, WireFormat.WIRETYPE_VARINT)
        writeRawVarint32(value)
    }

    fun writeFixed64(fieldNumber: Int, value: Long) {
        writeTag(fieldNumber, WireFormat.WIRETYPE_FIXED64)
        writeRawLittleEndian64(value)
    }

    fun writeFixed32(fieldNumber: Int, value: Int) {
        writeTag(fieldNumber, WireFormat.WIRETYPE_FIXED32)
        writeRawLittleEndian32(value)
    }

    fun writeBool(fieldNumber: Int, value: Boolean) {
        writeTag(fieldNumber, WireFormat.WIRETYPE_VARINT)
        writeRawByte(if (value) 1 else 0)
    }

    fun writeString(fieldNumber: Int, value: String) {
        writeTag(fieldNumber, WireFormat.WIRETYPE_LENGTH_DELIMITED)
        writeStringNoTag(value)
    }

    fun writeStringNoTag(value: String) {
        val buf = buffer
        if (buf != null) {
            val utf8Size = ByteString.computeUtf8Size(value)
            writeRawVarint32(utf8Size)
            if (utf8Size > limit - position) {
                throw OutOfSpaceException()
            }
            position = ByteString.encodeUtf8Into(value, buf, position)
        } else {
            val bytes = ByteString.encodeUtf8(value)
            writeRawVarint32(bytes.size)
            writeRawBytes(bytes, 0, bytes.size)
        }
    }

    fun writeGroup(fieldNumber: Int, value: MessageLite) {
        writeTag(fieldNumber, WireFormat.WIRETYPE_START_GROUP)
        value.writeTo(this)
        writeTag(fieldNumber, WireFormat.WIRETYPE_END_GROUP)
    }

    fun writeMessage(fieldNumber: Int, value: MessageLite) {
        writeTag(fieldNumber, WireFormat.WIRETYPE_LENGTH_DELIMITED)
        writeRawVarint32(value.serializedSize)
        value.writeTo(this)
    }

    fun writeMessageNoTag(value: MessageLite) {
        writeRawVarint32(value.serializedSize)
        value.writeTo(this)
    }

    fun writeBytes(fieldNumber: Int, value: ByteString) {
        writeTag(fieldNumber, WireFormat.WIRETYPE_LENGTH_DELIMITED)
        writeRawVarint32(value.size())
        value.writeTo(this)
    }

    fun writeBytesNoTag(value: ByteString) {
        writeRawVarint32(value.size())
        value.writeTo(this)
    }

    fun writeByteArray(fieldNumber: Int, value: ByteArray) {
        writeTag(fieldNumber, WireFormat.WIRETYPE_LENGTH_DELIMITED)
        writeRawVarint32(value.size)
        writeRawBytes(value, 0, value.size)
    }

    fun writeEnum(fieldNumber: Int, value: Int) {
        writeTag(fieldNumber, WireFormat.WIRETYPE_VARINT)
        writeInt32NoTag(value)
    }

    fun writeSFixed32(fieldNumber: Int, value: Int) {
        writeTag(fieldNumber, WireFormat.WIRETYPE_FIXED32)
        writeRawLittleEndian32(value)
    }

    fun writeSFixed64(fieldNumber: Int, value: Long) {
        writeTag(fieldNumber, WireFormat.WIRETYPE_FIXED64)
        writeRawLittleEndian64(value)
    }

    fun writeSInt32(fieldNumber: Int, value: Int) {
        writeTag(fieldNumber, WireFormat.WIRETYPE_VARINT)
        writeRawVarint32(encodeZigZag32(value))
    }

    fun writeSInt64(fieldNumber: Int, value: Long) {
        writeTag(fieldNumber, WireFormat.WIRETYPE_VARINT)
        writeRawVarint64(encodeZigZag64(value))
    }

    fun writeFloat(fieldNumber: Int, value: Float) {
        writeTag(fieldNumber, WireFormat.WIRETYPE_FIXED32)
        writeRawLittleEndian32(value.toRawBits())
    }

    fun writeDouble(fieldNumber: Int, value: Double) {
        writeTag(fieldNumber, WireFormat.WIRETYPE_FIXED64)
        writeRawLittleEndian64(value.toRawBits())
    }

    fun writeRawByte(value: Byte) {
        writeRawByte(value.toInt())
    }

    fun writeRawByte(value: Int) {
        val buf = buffer
        if (buf != null) {
            if (position >= limit) {
                throw OutOfSpaceException()
            }
            buf[position++] = value.toByte()
        } else {
            output?.write(value)
        }
    }

    fun writeRawBytes(bytes: ByteArray, offset: Int = 0, length: Int = bytes.size) {
        require(offset >= 0 && length >= 0 && offset <= bytes.size - length)
        if (length == 0) return
        val buf = buffer
        if (buf != null) {
            if (length > limit - position) {
                throw OutOfSpaceException()
            }
            bytes.copyInto(buf, position, offset, offset + length)
            position += length
        } else {
            output?.write(bytes, offset, length)
        }
    }

    fun writeRawVarint32(value: Int) {
        val buf = buffer
        if (buf != null && limit - position >= 5) {
            var v = value
            if ((v and 0x7F.inv()) == 0) {
                buf[position++] = v.toByte()
                return
            }
            buf[position++] = ((v and 0x7F) or 0x80).toByte()
            v = v ushr 7
            if ((v and 0x7F.inv()) == 0) {
                buf[position++] = v.toByte()
                return
            }
            buf[position++] = ((v and 0x7F) or 0x80).toByte()
            v = v ushr 7
            if ((v and 0x7F.inv()) == 0) {
                buf[position++] = v.toByte()
                return
            }
            buf[position++] = ((v and 0x7F) or 0x80).toByte()
            v = v ushr 7
            if ((v and 0x7F.inv()) == 0) {
                buf[position++] = v.toByte()
                return
            }
            buf[position++] = ((v and 0x7F) or 0x80).toByte()
            v = v ushr 7
            buf[position++] = v.toByte()
            return
        }
        var v = value
        while (true) {
            if ((v and 0x7F.inv()) == 0) {
                writeRawByte(v)
                return
            } else {
                writeRawByte((v and 0x7F) or 0x80)
                v = v ushr 7
            }
        }
    }

    fun writeRawVarint64(value: Long) {
        val buf = buffer
        if (buf != null && limit - position >= 10) {
            var v = value
            while (true) {
                if ((v and 0x7FL.inv()) == 0L) {
                    buf[position++] = v.toInt().toByte()
                    return
                } else {
                    buf[position++] = (((v and 0x7FL) or 0x80L).toInt()).toByte()
                    v = v ushr 7
                }
            }
        }
        var v = value
        while (true) {
            if ((v and 0x7FL.inv()) == 0L) {
                writeRawByte(v.toInt())
                return
            } else {
                writeRawByte(((v and 0x7FL) or 0x80L).toInt())
                v = v ushr 7
            }
        }
    }

    fun writeRawLittleEndian32(value: Int) {
        val buf = buffer
        if (buf != null && limit - position >= 4) {
            buf[position++] = (value and 0xFF).toByte()
            buf[position++] = ((value ushr 8) and 0xFF).toByte()
            buf[position++] = ((value ushr 16) and 0xFF).toByte()
            buf[position++] = ((value ushr 24) and 0xFF).toByte()
            return
        }
        writeRawByte(value and 0xFF)
        writeRawByte((value ushr 8) and 0xFF)
        writeRawByte((value ushr 16) and 0xFF)
        writeRawByte((value ushr 24) and 0xFF)
    }

    fun writeRawLittleEndian64(value: Long) {
        val buf = buffer
        if (buf != null && limit - position >= 8) {
            buf[position++] = (value and 0xFFL).toInt().toByte()
            buf[position++] = ((value ushr 8) and 0xFFL).toInt().toByte()
            buf[position++] = ((value ushr 16) and 0xFFL).toInt().toByte()
            buf[position++] = ((value ushr 24) and 0xFFL).toInt().toByte()
            buf[position++] = ((value ushr 32) and 0xFFL).toInt().toByte()
            buf[position++] = ((value ushr 40) and 0xFFL).toInt().toByte()
            buf[position++] = ((value ushr 48) and 0xFFL).toInt().toByte()
            buf[position++] = ((value ushr 56) and 0xFFL).toInt().toByte()
            return
        }
        writeRawByte((value and 0xFFL).toInt())
        writeRawByte(((value ushr 8) and 0xFFL).toInt())
        writeRawByte(((value ushr 16) and 0xFFL).toInt())
        writeRawByte(((value ushr 24) and 0xFFL).toInt())
        writeRawByte(((value ushr 32) and 0xFFL).toInt())
        writeRawByte(((value ushr 40) and 0xFFL).toInt())
        writeRawByte(((value ushr 48) and 0xFFL).toInt())
        writeRawByte(((value ushr 56) and 0xFFL).toInt())
    }

    class OutOfSpaceException : RuntimeException("CodedOutputStream was writing to a flat byte array and ran out of space.")

    companion object {
        fun encodeZigZag32(n: Int): Int = (n shl 1) xor (n shr 31)
        fun encodeZigZag64(n: Long): Long = (n shl 1) xor (n shr 63)

        fun newInstance(bytes: ByteArray, offset: Int = 0, length: Int = bytes.size): CodedOutputStream {
            require(offset >= 0 && length >= 0 && offset <= bytes.size - length)
            return CodedOutputStream(bytes, offset, length, null)
        }

        fun newInstance(output: ByteString.Output): CodedOutputStream =
            CodedOutputStream(null, 0, 0, output)

        fun computeTagSize(fieldNumber: Int): Int =
            computeRawVarint32Size(WireFormat.makeTag(fieldNumber, 0))

        fun computeInt32Size(fieldNumber: Int, value: Int): Int =
            computeTagSize(fieldNumber) + computeInt32SizeNoTag(value)

        fun computeInt32SizeNoTag(value: Int): Int =
            if (value >= 0) computeRawVarint32Size(value) else 10

        fun computeInt64Size(fieldNumber: Int, value: Long): Int =
            computeTagSize(fieldNumber) + computeRawVarint64Size(value)

        fun computeUInt64Size(fieldNumber: Int, value: Long): Int =
            computeTagSize(fieldNumber) + computeRawVarint64Size(value)

        fun computeUInt32Size(fieldNumber: Int, value: Int): Int =
            computeTagSize(fieldNumber) + computeRawVarint32Size(value)

        fun computeFixed64Size(fieldNumber: Int, value: Long): Int =
            computeTagSize(fieldNumber) + 8

        fun computeFixed32Size(fieldNumber: Int, value: Int): Int =
            computeTagSize(fieldNumber) + 4

        fun computeBoolSize(fieldNumber: Int, value: Boolean): Int =
            computeTagSize(fieldNumber) + 1

        fun computeStringSize(fieldNumber: Int, value: String): Int =
            computeTagSize(fieldNumber) + computeStringSizeNoTag(value)

        fun computeStringSizeNoTag(value: String): Int {
            val length = ByteString.computeUtf8Size(value)
            return computeRawVarint32Size(length) + length
        }

        fun computeGroupSize(fieldNumber: Int, value: MessageLite): Int =
            computeTagSize(fieldNumber) * 2 + value.serializedSize

        fun computeMessageSize(fieldNumber: Int, value: MessageLite): Int {
            val size = value.serializedSize
            return computeTagSize(fieldNumber) + computeRawVarint32Size(size) + size
        }

        fun computeMessageSizeNoTag(value: MessageLite): Int {
            val size = value.serializedSize
            return computeRawVarint32Size(size) + size
        }

        fun computeBytesSize(fieldNumber: Int, value: ByteString): Int =
            computeTagSize(fieldNumber) + computeRawVarint32Size(value.size()) + value.size()

        fun computeBytesSizeNoTag(value: ByteString): Int =
            computeRawVarint32Size(value.size()) + value.size()

        fun computeByteArraySize(fieldNumber: Int, value: ByteArray): Int =
            computeTagSize(fieldNumber) + computeRawVarint32Size(value.size) + value.size

        fun computeEnumSize(fieldNumber: Int, value: Int): Int =
            computeTagSize(fieldNumber) + computeInt32SizeNoTag(value)

        fun computeSFixed32Size(fieldNumber: Int, value: Int): Int =
            computeTagSize(fieldNumber) + 4

        fun computeSFixed64Size(fieldNumber: Int, value: Long): Int =
            computeTagSize(fieldNumber) + 8

        fun computeSInt32Size(fieldNumber: Int, value: Int): Int =
            computeTagSize(fieldNumber) + computeRawVarint32Size(encodeZigZag32(value))

        fun computeSInt64Size(fieldNumber: Int, value: Long): Int =
            computeTagSize(fieldNumber) + computeRawVarint64Size(encodeZigZag64(value))

        fun computeFloatSize(fieldNumber: Int, value: Float): Int =
            computeTagSize(fieldNumber) + 4

        fun computeDoubleSize(fieldNumber: Int, value: Double): Int =
            computeTagSize(fieldNumber) + 8

        fun computeRawVarint32Size(value: Int): Int {
            if ((value and (0.inv() shl 7)) == 0) return 1
            if ((value and (0.inv() shl 14)) == 0) return 2
            if ((value and (0.inv() shl 21)) == 0) return 3
            if ((value and (0.inv() shl 28)) == 0) return 4
            return 5
        }

        fun computeRawVarint64Size(value: Long): Int {
            if ((value and (0L.inv() shl 7)) == 0L) return 1
            if ((value and (0L.inv() shl 14)) == 0L) return 2
            if ((value and (0L.inv() shl 21)) == 0L) return 3
            if ((value and (0L.inv() shl 28)) == 0L) return 4
            if ((value and (0L.inv() shl 35)) == 0L) return 5
            if ((value and (0L.inv() shl 42)) == 0L) return 6
            if ((value and (0L.inv() shl 49)) == 0L) return 7
            if ((value and (0L.inv() shl 56)) == 0L) return 8
            if ((value and (0L.inv() shl 63)) == 0L) return 9
            return 10
        }
    }
}
