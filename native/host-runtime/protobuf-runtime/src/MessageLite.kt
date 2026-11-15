/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Copyright 2008 Google Inc.
 * Use of this source code is governed by the Apache 2.0 and Protobuf BSD licenses.
 */

package org.jetbrains.kotlin.protobuf

interface MessageLite : MessageLiteOrBuilder {
    fun writeTo(output: CodedOutputStream)
    val serializedSize: Int
    fun newBuilderForType(): Builder
    fun toBuilder(): Builder
    fun toByteArray(): ByteArray
    fun toByteString(): ByteString

    interface Builder : MessageLiteOrBuilder {
        fun clear(): Builder
        fun mergeFrom(input: CodedInputStream): Builder
        fun mergeFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Builder
        fun mergeFrom(data: ByteArray): Builder
        fun build(): MessageLite
        fun buildPartial(): MessageLite
        fun clone(): Builder
    }
}
