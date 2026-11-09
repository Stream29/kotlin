/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package org.jetbrains.kotlin.konan.host

import kotlinx.cinterop.*
import platform.posix.*

val NativeFile.nameWithoutExtension: String
    get() = name.substringBeforeLast('.')

val NativeFile.isFile: Boolean
    get() = memScoped {
        val st = alloc<stat>()
        stat(path, st.ptr) == 0 && (st.st_mode.toUInt() and S_IFMT.toUInt()) == S_IFREG.toUInt()
    }

val NativePath.isAbsolute: Boolean
    get() = toString().let { it.startsWith("/") || (it.length >= 2 && it[1] == ':') }

fun NativePath.absolutePathString(): String = toAbsolutePath().toString()

fun NativePaths.get(first: String, vararg more: String): NativePath =
    more.fold(NativePath(first)) { acc, part -> acc.resolve(part) }

fun <K, V> MutableMap<K, V>.putIfAbsent(key: K, value: V): V? {
    val existing = this[key]
    if (existing == null) {
        this[key] = value
    }
    return existing
}

private fun normalizeUnixGlobSubject(raw: String): String {
    if (raw.isEmpty()) return ""
    val sb = StringBuilder(raw.length)
    var prevSlash = false
    for (c in raw.replace('\\', '/')) {
        if (c == '/') {
            if (!prevSlash) sb.append('/')
            prevSlash = true
        } else {
            sb.append(c)
            prevSlash = false
        }
    }
    if (sb.length > 1 && sb[sb.length - 1] == '/') {
        sb.setLength(sb.length - 1)
    }
    return sb.toString()
}

fun globToRegex(glob: String): Regex {
    val regex = StringBuilder("^")
    var i = 0
    var inGroup = 0
    while (i < glob.length) {
        when (val c = glob[i++]) {
            '\\' -> {
                if (i == glob.length) {
                    throw IllegalArgumentException("No character to escape at end of glob: $glob")
                }
                val next = glob[i++]
                if (next in "\\^$+.()|[]{}*?") {
                    regex.append('\\')
                }
                regex.append(next)
            }
            '/' -> regex.append('/')
            '.', '(', ')', '+', '|', '^', '$', '@', '%' -> {
                regex.append('\\').append(c)
            }
            '?' -> regex.append("[^/]")
            '*' -> {
                if (i < glob.length && glob[i] == '*') {
                    while (i < glob.length && glob[i] == '*') {
                        i++
                    }
                    regex.append(".*")
                } else {
                    regex.append("[^/]*")
                }
            }
            '[' -> {
                regex.append("[[^/]&&[")
                if (i < glob.length && glob[i] == '^') {
                    regex.append("\\^")
                    i++
                } else {
                    if (i < glob.length && glob[i] == '!') {
                        regex.append('^')
                        i++
                    }
                    if (i < glob.length && glob[i] == '-') {
                        regex.append('-')
                        i++
                    }
                }
                var hasLeft = false
                var last = 0.toChar()
                while (i < glob.length) {
                    val ch = glob[i++]
                    if (ch == ']') {
                        break
                    }
                    if (ch == '/') {
                        throw IllegalArgumentException("Explicit '/' not allowed in character class: $glob")
                    }
                    if (ch == '\\' || ch == '[' || (ch == '&' && i < glob.length && glob[i] == '&')) {
                        regex.append('\\')
                    }
                    regex.append(ch)
                    if (ch == '-') {
                        if (!hasLeft) {
                            throw IllegalArgumentException("Invalid range in character class: $glob")
                        }
                        if (i >= glob.length || glob[i] == ']') {
                            break
                        }
                        val right = glob[i++]
                        if (right < last) {
                            throw IllegalArgumentException("Invalid range $last-$right in glob: $glob")
                        }
                        regex.append(right)
                        hasLeft = false
                    } else {
                        hasLeft = true
                        last = ch
                    }
                }
                regex.append("]]")
            }
            '{' -> {
                if (inGroup > 0) {
                    throw IllegalArgumentException("Cannot nest groups in glob: $glob")
                }
                regex.append("(?:(?:")
                inGroup++
            }
            '}' -> {
                if (inGroup > 0) {
                    inGroup--
                    regex.append("))")
                } else {
                    regex.append("\\}")
                }
            }
            ',' -> {
                if (inGroup > 0) {
                    regex.append(")|(?:")
                } else {
                    regex.append(',')
                }
            }
            else -> regex.append(c)
        }
    }
    if (inGroup > 0) {
        throw IllegalArgumentException("Missing '}' in glob: $glob")
    }
    regex.append('$')
    return Regex(regex.toString())
}

fun String.matchesUnixGlob(glob: String): Boolean =
    globToRegex(glob).matches(normalizeUnixGlobSubject(this))

fun NativePath.matchesUnixGlob(glob: String): Boolean =
    toString().matchesUnixGlob(glob)
