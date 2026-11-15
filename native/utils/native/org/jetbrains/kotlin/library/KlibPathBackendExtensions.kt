/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package org.jetbrains.kotlin.library

import platform.posix.rename
import platform.posix.symlink
import platform.posix.unlink

fun Path(path: String): KlibPath = KlibPaths.get(path)

fun Path(base: String, vararg subpaths: String): KlibPath = KlibPaths.get(base, *subpaths)

fun KlibPath.readText(): String = readBytes().decodeToString()

fun KlibPath.readLines(): List<String> {
    val text = readText()
    if (text.isEmpty()) return emptyList()
    val lines = text.lines()
    return if (text.endsWith("\n") || text.endsWith("\r")) lines.dropLast(1) else lines
}

inline fun <T> KlibPath.useLines(block: (Sequence<String>) -> T): T =
    block(readLines().asSequence())

fun KlibPath.forEachLine(action: (String) -> Unit) {
    for (line in readLines()) action(line)
}

fun KlibPath.writeLines(lines: Iterable<CharSequence>, lineSeparator: CharSequence = "\n"): KlibPath = apply {
    val sb = StringBuilder()
    for (line in lines) {
        sb.append(line).append(lineSeparator)
    }
    writeText(sb.toString())
}

fun KlibPath.writeLines(lines: Sequence<CharSequence>, lineSeparator: CharSequence = "\n"): KlibPath =
    writeLines(lines.asIterable(), lineSeparator)

fun KlibPath.appendText(text: CharSequence): KlibPath = apply {
    val existing = if (exists()) readBytes() else ByteArray(0)
    val added = text.toString().encodeToByteArray()
    writeBytes(existing + added)
}

fun KlibPath.appendLines(lines: Iterable<CharSequence>, lineSeparator: CharSequence = "\n"): KlibPath = apply {
    val sb = StringBuilder()
    for (line in lines) {
        sb.append(line).append(lineSeparator)
    }
    appendText(sb.toString())
}

fun KlibPath.appendLines(lines: Sequence<CharSequence>, lineSeparator: CharSequence = "\n"): KlibPath =
    appendLines(lines.asIterable(), lineSeparator)

fun KlibPath.createFile(): KlibPath = apply {
    if (!exists()) {
        parent?.createDirectories()
        writeBytes(ByteArray(0))
    }
}

fun KlibPath.createParentDirectories(): KlibPath = apply {
    parent?.createDirectories()
}

fun KlibPath.fileSize(): Long = KlibFiles.size(this)

fun KlibPath.moveTo(target: KlibPath, overwrite: Boolean = false): KlibPath {
    if (target.exists()) {
        if (!overwrite) throw KlibIoException("Target already exists: ${target.pathString}")
        target.deleteRecursively()
    }
    target.parent?.createDirectories()
    if (this is KlibPath.Physical && target is KlibPath.Physical) {
        if (rename(this.toFile().path, target.toFile().path) == 0) {
            return target
        }
    }
    copyToRecursively(target, followLinks = false, overwrite = true)
    deleteRecursively()
    return target
}

fun KlibPath.ensureSymbolicLinkTo(target: KlibPath) {
    parent?.createDirectories()
    unlink(pathString)
    if (symlink(target.pathString, pathString) != 0) {
        throw KlibIoException("Failed to create symlink $pathString -> ${target.pathString}")
    }
}

fun KlibPath.listDirectoryEntries(glob: String): List<KlibPath> {
    if (glob == "*") return listDirectoryEntries()
    val regexPattern = buildString {
        append("^")
        var i = 0
        while (i < glob.length) {
            when (val c = glob[i]) {
                '*' -> append(".*")
                '?' -> append(".")
                '.', '+', '(', ')', '[', ']', '{', '}', '^', '$', '|', '\\' -> append("\\").append(c)
                else -> append(c)
            }
            i++
        }
        append("$")
    }
    val regex = Regex(regexPattern)
    return listDirectoryEntries().filter { regex.matches(it.name) }
}

fun createTempDirectory(prefix: String? = null): KlibPath =
    KlibFiles.createTempDirectory(prefix)

fun createTempDirectory(directory: KlibPath?, prefix: String? = null): KlibPath =
    if (directory == null) KlibFiles.createTempDirectory(prefix) else KlibFiles.createTempDirectory(directory, prefix)

fun createTempFile(prefix: String? = null, suffix: String? = null): KlibPath =
    KlibFiles.createTempFile(prefix, suffix)

fun createTempFile(directory: KlibPath?, prefix: String? = null, suffix: String? = null): KlibPath {
    if (directory == null) return KlibFiles.createTempFile(prefix, suffix)
    val dir = KlibFiles.createTempDirectory(directory, prefix)
    val file = dir.resolve("${(prefix ?: "klib").replace('/', '_')}${suffix ?: ".tmp"}")
    file.writeBytes(ByteArray(0))
    return file
}
