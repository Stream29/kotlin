/*
 * Copyright 2026 NativeKotlin contributors.
 * Use of this source code is governed by the Apache 2.0 license in porting/R1/LICENSE.txt.
 */
package org.jetbrains.kotlin.backend.common.serialization

/** Preserve JVM UTF-8 replacement ('?') before delegating well-formed encoding to Native. */
internal fun String.serializationUtf8Bytes(): ByteArray {
    val sanitized = StringBuilder(length)
    var index = 0
    while (index < length) {
        val character = this[index++]
        when (character.code) {
            in 0xD800..0xDBFF -> {
                if (index < length && this[index].code in 0xDC00..0xDFFF) {
                    sanitized.append(character)
                    sanitized.append(this[index++])
                } else {
                    sanitized.append('?')
                }
            }
            in 0xDC00..0xDFFF -> sanitized.append('?')
            else -> sanitized.append(character)
        }
    }
    return sanitized.toString().encodeToByteArray()
}
