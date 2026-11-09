/*
 * Copyright 2026 NativeKotlin contributors.
 * Use of this source code is governed by the Apache 2.0 license in porting/R1/LICENSE.txt.
 */
@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package org.jetbrains.kotlin.backend.common.serialization

import kotlinx.cinterop.*
import nativekotlin.files.nk_clear_errno
import nativekotlin.host.files.NativeFile
import platform.posix.*

/** Original File.forEachBlock default is 4096, not the unrelated 8192-byte helper. */
internal fun NativeFile.serializationForEachBlock(action: (ByteArray, Int) -> Unit) {
    val input = fopen(path, "rb") ?: error("Cannot open $path (errno=$errno)")
    var failure: Throwable? = null
    try {
        val buffer = ByteArray(4096)
        buffer.usePinned { pinned ->
            while (true) {
                val count = fread(pinned.addressOf(0), 1.convert(), buffer.size.convert(), input).toInt()
                val readFailed = ferror(input) != 0
                val readErrno = errno
                if (count > 0) action(buffer, count)
                if (readFailed) {
                    if (readErrno == EINTR) {
                        clearerr(input)
                        continue
                    }
                    error("Cannot read $path (errno=$readErrno)")
                }
                if (count < buffer.size) {
                    break
                }
            }
        }
    } catch (exception: Throwable) {
        failure = exception
        throw exception
    } finally {
        if (fclose(input) != 0) {
            val closeFailure = IllegalStateException("Cannot close $path (errno=$errno)")
            if (failure != null) failure.addSuppressed(closeFailure) else throw closeFailure
        }
    }
}

internal fun NativeFile.serializationListFiles(): Array<NativeFile>? {
    val directory = opendir(path) ?: return null
    try {
        val children = ArrayList<NativeFile>()
        while (true) {
            nk_clear_errno()
            val entry = readdir(directory)
            if (entry == null) {
                if (errno != 0) return null
                return children.toTypedArray()
            }
            val name = entry.pointed.d_name.toKString()
            if (name != "." && name != "..") children += NativeFile(this, name)
        }
    } finally {
        closedir(directory)
    }
}

/** Same CR/LF/CRLF and final-line boundaries as BufferedReader.readLine. */
internal fun NativeFile.serializationForEachLine(action: (String) -> Unit) {
    var line = ByteArray(4096)
    var size = 0
    var skipLineFeed = false
    serializationForEachBlock { buffer, count ->
        for (index in 0 until count) {
            val byte = buffer[index]
            if (skipLineFeed && byte == 10.toByte()) {
                skipLineFeed = false
                continue
            }
            skipLineFeed = false
            if (byte == 10.toByte() || byte == 13.toByte()) {
                action(line.decodeToString(endIndex = size))
                size = 0
                skipLineFeed = byte == 13.toByte()
            } else {
                if (size == line.size) {
                    check(line.size <= Int.MAX_VALUE / 2) { "Source line is too large: $path" }
                    line = line.copyOf(line.size * 2)
                }
                line[size++] = byte
            }
        }
    }
    if (size > 0) action(line.decodeToString(endIndex = size))
}

fun NativeFile.serializationAbsoluteFile(): NativeFile {
    if (path.startsWith('/') || (path.length >= 2 && path[1] == ':')) return this
    val current = getcwd(null, 0) ?: error("getcwd failed (errno=$errno)")
    return try {
        NativeFile(current.toKString(), path)
    } finally {
        free(current)
    }
}

internal fun getFileName(path: String): String {
    if (path.isEmpty()) return ""
    var end = path.length
    while (end > 0 && (path[end - 1] == '/' || path[end - 1] == '\\')) {
        end--
    }
    if (end == 0) return path.substring(0, 1)
    val start = maxOf(path.lastIndexOf('/', end - 1), path.lastIndexOf('\\', end - 1))
    return path.substring(start + 1, end)
}
