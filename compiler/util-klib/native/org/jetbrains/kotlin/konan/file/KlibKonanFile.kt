/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package org.jetbrains.kotlin.konan.file

import kotlinx.cinterop.staticCFunction
import nativekotlin.host.files.NativeFile
import org.jetbrains.kotlin.library.KlibFiles
import org.jetbrains.kotlin.library.KlibPath
import org.jetbrains.kotlin.library.KlibPaths
import platform.posix.atexit

typealias File = NativeFile
typealias ZipFileSystemAccessor = org.jetbrains.kotlin.io.ZipFileSystemAccessor

fun File(path: KlibPath): File = path.toFile()

fun File(parent: File, child: File): File = NativeFile(parent, child.path)

fun String.File(): File = NativeFile(this)

fun NativeFile.toPath(): KlibPath = KlibPaths.get(path)

val NativeFile.javaPath: KlibPath
    get() = toPath()

val NativeFile.isAbsolute: Boolean
    get() = toPath().isAbsolute

val NativeFile.absolutePath: String
    get() = toPath().toAbsolutePath().pathString

val NativeFile.absoluteFile: NativeFile
    get() = NativeFile(absolutePath)

val NativeFile.canonicalPath: String
    get() = toPath().toRealPath().pathString

val NativeFile.canonicalFile: NativeFile
    get() = NativeFile(canonicalPath)

val NativeFile.nameSegments: List<String>
    get() = path.split('/').filter { it.isNotEmpty() }

val NativeFile.listFiles: List<NativeFile>
    get() = listFilesOrEmpty

val NativeFile.listFilesOrEmpty: List<NativeFile>
    get() = toPath().listDirectoryEntries().map { it.toFile() }

fun NativeFile.child(name: String): NativeFile = NativeFile(this, name)

fun NativeFile.resolve(name: String): NativeFile = NativeFile(this, name)

fun NativeFile.toRelativeString(base: NativeFile): String {
    val thisPath = toPath().normalize()
    val basePath = base.toPath().normalize()
    if (thisPath.isAbsolute != basePath.isAbsolute) {
        throw IllegalArgumentException("this and base files have different roots: $this and $base.")
    }
    if (thisPath.pathString == basePath.pathString) return ""
    val baseSegments = basePath.pathString.trim('/').split('/').filter { it.isNotEmpty() && it != "." }
    val thisSegments = thisPath.pathString.trim('/').split('/').filter { it.isNotEmpty() && it != "." }
    var common = 0
    while (common < baseSegments.size && common < thisSegments.size && baseSegments[common] == thisSegments[common]) {
        common++
    }
    if (common < baseSegments.size && baseSegments[common] == "..") {
        throw IllegalArgumentException("Unable to calculate relative path: $this to $base")
    }
    val up = List(baseSegments.size - common) { ".." }
    val down = thisSegments.subList(common, thisSegments.size)
    return (up + down).joinToString("/")
}

object KlibTempDeleteOnExitRegistry {
    private val registeredPaths = linkedSetOf<String>()
    private var hookInstalled = false

    fun register(path: KlibPath) {
        val normalized = path.toAbsolutePath().normalize().pathString
        if (!hookInstalled) {
            hookInstalled = true
            atexit(staticCFunction<Unit> { KlibTempDeleteOnExitRegistry.cleanupAll() })
        }
        registeredPaths.add(normalized)
    }

    fun cleanupAll() {
        val snapshot = registeredPaths.toList().asReversed()
        registeredPaths.clear()
        for (raw in snapshot) {
            try {
                val p = KlibPaths.get(raw)
                if (p.exists()) {
                    p.deleteRecursively()
                }
            } catch (_: Throwable) {
            }
        }
    }
}

fun NativeFile.deleteOnExit(): NativeFile {
    KlibTempDeleteOnExitRegistry.register(toPath())
    return this
}

fun KlibPath.deleteOnExit(): KlibPath {
    if (this is KlibPath.Physical) {
        KlibTempDeleteOnExitRegistry.register(this)
    }
    return this
}

val NativeFile.Companion.userDir: NativeFile
    get() = KlibPaths.get("").toAbsolutePath().toFile()

fun createTempDir(prefix: String): NativeFile =
    KlibFiles.createTempDirectory(prefix).toFile()

fun createTempFile(prefix: String, suffix: String? = null): NativeFile =
    KlibFiles.createTempFile(prefix, suffix).toFile()
