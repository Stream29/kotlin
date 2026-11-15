/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin

import org.jetbrains.kotlin.serialization.deserialization.InputStream
import com.intellij.platform.native.NativeCharset



/**
 * UTF-8 host boundary for the original source-line mapping algorithm.
 * Offsets are UTF-16 code units, as in the original InputStreamReader consumer.
 */
class SourceFileReader(private val stream: InputStream) : AutoCloseable {
    private val text by lazy { NativeCharset.UTF_8.decode(stream.readBytes()) }
    private var position = 0
    private var closed = false

    fun read(buffer: CharArray): Int {
        check(!closed) { "Stream closed" }
        if (buffer.isEmpty()) return 0
        if (position == text.length) return -1
        val count = minOf(buffer.size, text.length - position)
        for (i in 0 until count) buffer[i] = text[position + i]
        position += count
        return count
    }

    override fun close() {
        if (!closed) {
            closed = true
            stream.close()
        }
    }
}
