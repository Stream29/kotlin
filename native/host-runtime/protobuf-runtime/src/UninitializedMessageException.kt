/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Copyright 2008 Google Inc.
 * Use of this source code is governed by the Apache 2.0 and Protobuf BSD licenses.
 */

package org.jetbrains.kotlin.protobuf

class UninitializedMessageException(val missingFields: List<String> = emptyList()) :
    RuntimeException("Message was missing required fields: " + missingFields.joinToString(", ")) {

    fun asInvalidProtocolBufferException(): InvalidProtocolBufferException =
        InvalidProtocolBufferException(message ?: "Missing required fields")
}
