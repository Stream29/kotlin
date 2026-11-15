/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Copyright 2008 Google Inc.
 * Use of this source code is governed by the Apache 2.0 and Protobuf BSD licenses.
 */

package org.jetbrains.kotlin.protobuf

object WireFormat {
    const val WIRETYPE_VARINT = 0
    const val WIRETYPE_FIXED64 = 1
    const val WIRETYPE_LENGTH_DELIMITED = 2
    const val WIRETYPE_START_GROUP = 3
    const val WIRETYPE_END_GROUP = 4
    const val WIRETYPE_FIXED32 = 5

    const val TAG_TYPE_BITS = 3
    const val TAG_TYPE_MASK = (1 shl TAG_TYPE_BITS) - 1

    const val MESSAGE_SET_ITEM = 1
    const val MESSAGE_SET_TYPE_ID = 2
    const val MESSAGE_SET_MESSAGE = 3

    val MESSAGE_SET_ITEM_TAG = makeTag(MESSAGE_SET_ITEM, WIRETYPE_START_GROUP)
    val MESSAGE_SET_ITEM_END_TAG = makeTag(MESSAGE_SET_ITEM, WIRETYPE_END_GROUP)
    val MESSAGE_SET_TYPE_ID_TAG = makeTag(MESSAGE_SET_TYPE_ID, WIRETYPE_VARINT)
    val MESSAGE_SET_MESSAGE_TAG = makeTag(MESSAGE_SET_MESSAGE, WIRETYPE_LENGTH_DELIMITED)

    fun getTagWireType(tag: Int): Int = tag and TAG_TYPE_MASK
    fun getTagFieldNumber(tag: Int): Int = tag ushr TAG_TYPE_BITS
    fun makeTag(fieldNumber: Int, wireType: Int): Int = (fieldNumber shl TAG_TYPE_BITS) or wireType

    enum class JavaType {
        INT, LONG, FLOAT, DOUBLE, BOOLEAN, STRING, BYTE_STRING, ENUM, MESSAGE
    }

    enum class FieldType(val javaType: JavaType, val wireType: Int) {
        DOUBLE(JavaType.DOUBLE, WIRETYPE_FIXED64),
        FLOAT(JavaType.FLOAT, WIRETYPE_FIXED32),
        INT64(JavaType.LONG, WIRETYPE_VARINT),
        UINT64(JavaType.LONG, WIRETYPE_VARINT),
        INT32(JavaType.INT, WIRETYPE_VARINT),
        FIXED64(JavaType.LONG, WIRETYPE_FIXED64),
        FIXED32(JavaType.INT, WIRETYPE_FIXED32),
        BOOL(JavaType.BOOLEAN, WIRETYPE_VARINT),
        STRING(JavaType.STRING, WIRETYPE_LENGTH_DELIMITED),
        GROUP(JavaType.MESSAGE, WIRETYPE_START_GROUP),
        MESSAGE(JavaType.MESSAGE, WIRETYPE_LENGTH_DELIMITED),
        BYTES(JavaType.BYTE_STRING, WIRETYPE_LENGTH_DELIMITED),
        UINT32(JavaType.INT, WIRETYPE_VARINT),
        ENUM(JavaType.ENUM, WIRETYPE_VARINT),
        SFIXED32(JavaType.INT, WIRETYPE_FIXED32),
        SFIXED64(JavaType.LONG, WIRETYPE_FIXED64),
        SINT32(JavaType.INT, WIRETYPE_VARINT),
        SINT64(JavaType.LONG, WIRETYPE_VARINT);

        open val isPackable: Boolean
            get() = this != STRING && this != GROUP && this != MESSAGE && this != BYTES
    }
}
