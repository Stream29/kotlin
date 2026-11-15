/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Copyright 2008 Google Inc.
 * Use of this source code is governed by the Apache 2.0 and Protobuf BSD licenses.
 */

package org.jetbrains.kotlin.protobuf

open class InvalidProtocolBufferException(message: String) : Exception(message) {
    var unfinishedMessage: MessageLite? = null
        private set

    fun setUnfinishedMessage(unfinishedMessage: MessageLite?): InvalidProtocolBufferException {
        this.unfinishedMessage = unfinishedMessage
        return this
    }

    companion object {
        fun truncatedMessage(): InvalidProtocolBufferException =
            InvalidProtocolBufferException("While parsing a protocol message, the input ended unexpectedly in the middle of a field. This could mean either less than the input has been truncated or that an embedded message misreported its own length.")

        fun negativeSize(): InvalidProtocolBufferException =
            InvalidProtocolBufferException("CodedInputStream encountered an embedded string or message which claimed to have negative size.")

        fun malformedVarint(): InvalidProtocolBufferException =
            InvalidProtocolBufferException("CodedInputStream encountered a malformed varint.")

        fun invalidTag(): InvalidProtocolBufferException =
            InvalidProtocolBufferException("Protocol message contained an invalid tag (zero).")

        fun invalidEndTag(): InvalidProtocolBufferException =
            InvalidProtocolBufferException("Protocol message end-group tag did not match expected tag.")

        fun invalidWireType(): InvalidProtocolBufferException =
            InvalidProtocolBufferException("Protocol message tag had invalid wire type.")

        fun recursionLimitExceeded(): InvalidProtocolBufferException =
            InvalidProtocolBufferException("Protocol message had too many levels of nesting. May be malicious. Use CodedInputStream.setRecursionLimit() to increase the depth limit.")

        fun sizeLimitExceeded(): InvalidProtocolBufferException =
            InvalidProtocolBufferException("Protocol message was too large. May be malicious. Use CodedInputStream.setSizeLimit() to increase the size limit.")
    }
}
