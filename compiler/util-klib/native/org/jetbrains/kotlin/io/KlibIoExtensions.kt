/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.io

import nativekotlin.host.files.NativeFile
import nativekotlin.klib.DirectoryKlib
import nativekotlin.klib.packKlib
import org.jetbrains.kotlin.konan.properties.Properties
import org.jetbrains.kotlin.library.KlibIoException
import org.jetbrains.kotlin.library.KlibPath
import org.jetbrains.kotlin.library.KlibZipException
import org.jetbrains.kotlin.library.createDirectories

fun KlibPath.canonicalPathString(): String = toRealPath().pathString

fun KlibPath.fileKey(): Any = this.fileKey()

fun NativeZipFileSystem.klibPath(path: String): KlibPath = KlibPath.Archive(getPath(path))

fun <T> ZipFileSystemAccessor.withZipFileSystem(
    zipFile: KlibPath,
    action: (NativeZipFileSystem) -> T,
): T {
    val file = try {
        zipFile.toFile()
    } catch (e: KlibIoException) {
        throw e
    } catch (e: Throwable) {
        throw KlibIoException(e.message, e)
    }
    if (!file.exists) {
        throw KlibIoException("ZIP archive does not exist: ${file.path}")
    }
    if (!file.isFile) {
        throw KlibIoException("ZIP archive is not a regular file: ${file.path}")
    }
    var enteredAction = false
    return try {
        withZipFileSystem(file) { fs ->
            enteredAction = true
            action(fs)
        }
    } catch (e: KlibIoException) {
        throw e
    } catch (e: Throwable) {
        if (!enteredAction) {
            throw KlibZipException(e.message ?: "Invalid ZIP archive: ${file.path}", e)
        }
        throw e
    }
}

fun <T> KlibPath.withZipFileSystem(
    accessor: ZipFileSystemAccessor = ZipFileSystemInPlaceAccessor,
    action: (NativeZipFileSystem) -> T,
): T = accessor.withZipFileSystem(this, action)

fun KlibPath.zipDirAs(zipFilePath: KlibPath) {
    val sourceFile = toFile()
    if (!sourceFile.exists || !sourceFile.isDirectory) {
        throw KlibIoException("Cannot zip non-directory path: $pathString")
    }
    val zipBytes = try {
        val dirKlib = DirectoryKlib(sourceFile.path)
        try {
            packKlib(dirKlib, dirKlib.directoryPaths())
        } finally {
            dirKlib.close()
        }
    } catch (e: KlibIoException) {
        throw e
    } catch (e: Throwable) {
        throw KlibIoException("Failed to pack directory $pathString as ZIP: ${e.message}", e)
    }
    zipFilePath.parent.createDirectories()
    zipFilePath.writeBytes(zipBytes)
}

fun KlibPath.readProperties(): Properties = Properties().apply {
    load(readBytes())
}

fun KlibPath.writeProperties(properties: Properties) {
    parent.createDirectories()
    writeBytes(properties.toByteArray())
}

fun NativeFile.loadProperties(): Properties = Properties().apply {
    try {
        load(readBytes())
    } catch (e: Throwable) {
        throw KlibIoException("Failed to load properties from $path: ${e.message}", e)
    }
}

fun Properties.saveToFile(file: NativeFile) {
    try {
        file.parentFile?.mkdirs()
        file.writeBytes(toByteArray())
    } catch (e: Throwable) {
        throw KlibIoException("Failed to save properties to ${file.path}: ${e.message}", e)
    }
}

fun parseSpaceSeparatedArgs(argsString: String): List<String> {
    val parsedArgs = mutableListOf<String>()
    var inQuotes = false
    var currentCharSequence = StringBuilder()
    fun saveArg(wasInQuotes: Boolean) {
        if (wasInQuotes || currentCharSequence.isNotBlank()) {
            parsedArgs.add(currentCharSequence.toString())
            currentCharSequence = StringBuilder()
        }
    }
    argsString.forEach { char ->
        if (char == '"') {
            inQuotes = !inQuotes
            // Save value which was in quotes.
            if (!inQuotes) {
                saveArg(true)
            }
        } else if (char.isWhitespace() && !inQuotes) {
            // Space is separator.
            saveArg(false)
        } else {
            currentCharSequence.append(char)
        }
    }
    if (inQuotes) {
        error("No close-quote was found in $currentCharSequence.")
    }
    saveArg(false)
    return parsedArgs
}

private fun String.suffix(suffix: String?): String =
    if (!suffix.isNullOrEmpty()) "$this.$suffix" else this

fun Properties.propertyList(key: String, suffix: String? = null, escapeInQuotes: Boolean = false): List<String> {
    val value: String? = (getProperty(key.suffix(suffix)) ?: getProperty(key))?.trim(Char::isWhitespace)
    return when {
        value.isNullOrEmpty() -> emptyList()
        escapeInQuotes -> parseSpaceSeparatedArgs(value)
        else -> value.split(Regex("\\s+"))
    }
}
