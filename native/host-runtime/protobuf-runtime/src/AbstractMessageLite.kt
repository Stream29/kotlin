/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Copyright 2008 Google Inc.
 * Use of this source code is governed by the Apache 2.0 and Protobuf BSD licenses.
 */

package org.jetbrains.kotlin.protobuf

abstract class AbstractMessageLite : MessageLite {

    override fun toByteArray(): ByteArray {
        val size = serializedSize
        if (size == 0) return EMPTY_BYTE_ARRAY
        val bytes = ByteArray(size)
        val output = CodedOutputStream.newInstance(bytes)
        writeTo(output)
        output.checkNoSpaceLeft()
        return bytes
    }

    override fun toByteString(): ByteString = ByteString.wrap(toByteArray())

    abstract class Builder<BuilderType : Builder<BuilderType>> : MessageLite.Builder {
        abstract override fun mergeFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): BuilderType

        override fun mergeFrom(data: ByteArray): BuilderType =
            mergeFrom(data, 0, data.size)

        fun mergeFrom(data: ByteArray, off: Int, len: Int): BuilderType {
            val input = CodedInputStream.newInstance(data, off, len)
            mergeFrom(input, ExtensionRegistryLite.getEmptyRegistry())
            input.checkLastTagWas(0)
            @Suppress("UNCHECKED_CAST")
            return this as BuilderType
        }

        override fun mergeFrom(input: CodedInputStream): BuilderType =
            mergeFrom(input, ExtensionRegistryLite.getEmptyRegistry())

        abstract override fun clone(): BuilderType

        companion object {
            fun <T> addAll(values: Iterable<T>, list: MutableList<in T>) {
                if (values is Collection<T>) {
                    for (v in values) {
                        if (v == null) throw NullPointerException("Element cannot be null")
                    }
                    list.addAll(values)
                    return
                }
                for (v in values) {
                    if (v == null) throw NullPointerException("Element cannot be null")
                    list.add(v)
                }
            }
        }
    }

    companion object {
        private val EMPTY_BYTE_ARRAY = ByteArray(0)
    }
}
