/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Copyright 2008 Google Inc.
 * Use of this source code is governed by the Apache 2.0 and Protobuf BSD licenses.
 */

package org.jetbrains.kotlin.protobuf

abstract class AbstractParser<MessageType : MessageLite> : Parser<MessageType> {

    private fun checkMessageInitialized(message: MessageType): MessageType {
        if (!message.isInitialized()) {
            throw UninitializedMessageException().asInvalidProtocolBufferException().setUnfinishedMessage(message)
        }
        return message
    }

    override fun parseFrom(input: CodedInputStream): MessageType =
        parseFrom(input, ExtensionRegistryLite.getEmptyRegistry())

    override fun parseFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): MessageType =
        checkMessageInitialized(parsePartialFrom(input, extensionRegistry))

    override fun parseFrom(data: ByteArray): MessageType =
        parseFrom(data, ExtensionRegistryLite.getEmptyRegistry())

    override fun parseFrom(data: ByteArray, extensionRegistry: ExtensionRegistryLite): MessageType =
        checkMessageInitialized(parsePartialFrom(data, extensionRegistry))

    override fun parsePartialFrom(data: ByteArray, extensionRegistry: ExtensionRegistryLite): MessageType {
        val input = CodedInputStream.newInstance(data)
        val message = parsePartialFrom(input, extensionRegistry)
        input.checkLastTagWas(0)
        return message
    }
}
