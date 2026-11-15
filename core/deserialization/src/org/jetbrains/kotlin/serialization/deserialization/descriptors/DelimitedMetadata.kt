/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin.serialization.deserialization.descriptors

import org.jetbrains.kotlin.protobuf.ByteString
import org.jetbrains.kotlin.protobuf.CodedInputStream
import org.jetbrains.kotlin.protobuf.CodedOutputStream
import org.jetbrains.kotlin.protobuf.ExtensionRegistryLite
import org.jetbrains.kotlin.protobuf.MessageLite
import org.jetbrains.kotlin.protobuf.Parser

// Native adaptation of the in-memory writeDelimitedTo/parseDelimitedFrom pair.
// Each message retains its varint length prefix; the parser gets only that message's bytes.
internal fun Collection<MessageLite>.packDelimitedMetadata(): ByteArray {
    val bytes = ByteString.newOutput()
    val output = CodedOutputStream.newInstance(bytes)
    for (message in this) {
        output.writeRawVarint32(message.serializedSize)
        message.writeTo(output)
    }
    output.flush()
    return bytes.toByteArray()
}

internal fun <M : MessageLite> Parser<M>.readDelimitedMetadata(
    input: CodedInputStream,
    extensionRegistry: ExtensionRegistryLite
): M? {
    if (input.isAtEnd()) return null
    val size = input.readRawVarint32()
    return parseFrom(input.readRawBytes(size), extensionRegistry)
}
