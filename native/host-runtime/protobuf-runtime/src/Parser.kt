/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Copyright 2008 Google Inc.
 * Use of this source code is governed by the Apache 2.0 and Protobuf BSD licenses.
 */

package org.jetbrains.kotlin.protobuf

interface Parser<MessageType> {
    fun parseFrom(input: CodedInputStream): MessageType
    fun parseFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): MessageType
    fun parsePartialFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): MessageType

    fun parseFrom(data: ByteArray): MessageType
    fun parseFrom(data: ByteArray, extensionRegistry: ExtensionRegistryLite): MessageType
    fun parsePartialFrom(data: ByteArray, extensionRegistry: ExtensionRegistryLite): MessageType
}
