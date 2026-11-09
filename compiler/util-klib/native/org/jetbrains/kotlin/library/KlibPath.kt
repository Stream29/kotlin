/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package org.jetbrains.kotlin.library

import kotlinx.cinterop.*
import nativekotlin.files.nk_clear_errno
import nativekotlin.host.files.NativeFile
import org.jetbrains.kotlin.io.NativeZipFileSystem
import org.jetbrains.kotlin.io.NativeZipPath
import platform.posix.*
import kotlin.random.Random

open class KlibIoException(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)

class KlibZipException(message: String? = null, cause: Throwable? = null) : KlibIoException(message, cause)

class InvalidKlibPathException(val input: String, reason: String) : IllegalArgumentException("$reason: $input")

enum class KlibFileVisitResult {
    CONTINUE,
    TERMINATE,
    SKIP_SUBTREE,
    SKIP_SIBLINGS,
}

class KlibBasicFileAttributes(
    val isDirectory: Boolean,
    val isRegularFile: Boolean,
    val size: Long,
)

open class KlibSimpleFileVisitor<T> {
    open fun preVisitDirectory(dir: T, attrs: KlibBasicFileAttributes): KlibFileVisitResult = KlibFileVisitResult.CONTINUE
    open fun visitFile(file: T, attrs: KlibBasicFileAttributes): KlibFileVisitResult = KlibFileVisitResult.CONTINUE
    open fun visitFileFailed(file: T, exc: KlibIoException): KlibFileVisitResult = throw exc
    open fun postVisitDirectory(dir: T, exc: KlibIoException?): KlibFileVisitResult {
        if (exc != null) throw exc
        return KlibFileVisitResult.CONTINUE
    }
}

internal fun currentWorkingDirectory(): String {
    val buf = getcwd(null, 0) ?: throw KlibIoException("getcwd failed (errno=$errno)")
    return try {
        buf.toKString().replace('\\', '/')
    } finally {
        free(buf)
    }
}

private fun isDrivePrefix(s: String): Boolean =
    s.length >= 2 && s[1] == ':' && (s.length == 2 || s[2] == '/')

private fun isDriveRoot(s: String): Boolean =
    (s.length == 2 && s[1] == ':') || (s.length == 3 && s[1] == ':' && s[2] == '/')

private fun normalizeUnixSpelling(rawInput: String): String {
    if ('\u0000' in rawInput) {
        throw InvalidKlibPathException(rawInput, "Nul character not allowed")
    }
    if (rawInput.isEmpty()) return ""
    val input = rawInput.replace('\\', '/')
    val sb = StringBuilder(input.length)
    var prevSlash = false
    for (c in input) {
        if (c == '/') {
            if (!prevSlash) sb.append('/')
            prevSlash = true
        } else {
            sb.append(c)
            prevSlash = false
        }
    }
    if (sb.length == 2 && sb[1] == ':') {
        sb.append('/')
    } else if (sb.length > 1 && !(sb.length == 3 && sb[1] == ':' && sb[2] == '/') && sb[sb.length - 1] == '/') {
        sb.setLength(sb.length - 1)
    }
    return sb.toString()
}

private fun normalizeLexicalSegments(segments: List<String>, isAbsolute: Boolean): List<String> {
    val out = ArrayList<String>(segments.size)
    for (seg in segments) {
        when (seg) {
            "", "." -> {}
            ".." -> {
                if (out.isNotEmpty() && out.last() != "..") {
                    out.removeAt(out.lastIndex)
                } else if (!isAbsolute) {
                    out.add("..")
                }
            }
            else -> out.add(seg)
        }
    }
    return out
}

internal data class PosixFileKey(val dev: ULong, val ino: ULong)

sealed class KlibPath : Comparable<KlibPath> {
    abstract val pathString: String
    abstract val name: String
    val extension: String
        get() = name.substringAfterLast('.', "")
    val nameWithoutExtension: String
        get() = name.substringBeforeLast(".")
    abstract val parent: KlibPath?
    abstract val root: KlibPath?
    abstract val isAbsolute: Boolean
    abstract val nameCount: Int

    abstract fun subpath(beginIndex: Int, endIndex: Int): KlibPath
    abstract fun startsWith(other: KlibPath): Boolean
    abstract fun startsWith(other: String): Boolean
    abstract fun endsWith(other: KlibPath): Boolean
    abstract fun endsWith(other: String): Boolean
    abstract fun resolve(other: String): KlibPath
    abstract fun resolve(other: KlibPath): KlibPath
    abstract fun normalize(): KlibPath
    abstract fun toAbsolutePath(): KlibPath
    fun absolute(): KlibPath = toAbsolutePath()
    fun absolutePathString(): String = toAbsolutePath().pathString
    abstract fun toRealPath(): KlibPath
    abstract fun relativize(other: KlibPath): KlibPath

    abstract fun exists(): Boolean
    abstract fun isDirectory(): Boolean
    abstract fun isRegularFile(): Boolean
    abstract fun readBytes(): ByteArray
    abstract fun writeBytes(array: ByteArray)
    fun writeText(text: CharSequence) = writeBytes(text.toString().encodeToByteArray())
    abstract fun createDirectories(): KlibPath
    abstract fun listDirectoryEntries(): List<KlibPath>
    abstract fun deleteIfExists(): Boolean
    abstract fun deleteRecursively()
    abstract fun copyTo(target: KlibPath, overwrite: Boolean = false): KlibPath
    abstract fun copyToRecursively(target: KlibPath, followLinks: Boolean = false, overwrite: Boolean = false): KlibPath
    abstract fun toFile(): NativeFile
    abstract fun fileKey(): Any

    override fun toString(): String = pathString

    class Physical(rawInput: String) : KlibPath() {
        val rawPath: String = normalizeUnixSpelling(rawInput)

        override val pathString: String
            get() = rawPath

        internal val drivePrefix: String
            get() = if (isDrivePrefix(rawPath)) rawPath.substring(0, 2) else ""

        override val isAbsolute: Boolean
            get() = rawPath.startsWith('/') || isDrivePrefix(rawPath)

        internal val segments: List<String>
            get() = when {
                rawPath == "/" || isDriveRoot(rawPath) -> emptyList()
                rawPath.isEmpty() -> listOf("")
                drivePrefix.isNotEmpty() -> rawPath.substring(2).trimStart('/').split('/')
                else -> rawPath.trimStart('/').split('/')
            }

        override val name: String
            get() = if (rawPath == "/" || isDriveRoot(rawPath)) "" else rawPath.substringAfterLast('/')

        override val parent: KlibPath?
            get() {
                if (rawPath == "/" || rawPath.isEmpty() || isDriveRoot(rawPath)) return null
                val idx = rawPath.lastIndexOf('/')
                if (idx < 0) return null
                if (idx == 0) return Physical("/")
                if (idx == 2 && rawPath[1] == ':') return Physical(rawPath.substring(0, 3))
                return Physical(rawPath.substring(0, idx))
            }

        override val root: KlibPath?
            get() = when {
                drivePrefix.isNotEmpty() -> Physical("$drivePrefix/")
                isAbsolute -> Physical("/")
                else -> null
            }

        override val nameCount: Int
            get() = segments.size

        override fun subpath(beginIndex: Int, endIndex: Int): KlibPath {
            val segs = segments
            require(beginIndex >= 0 && beginIndex < segs.size && endIndex > beginIndex && endIndex <= segs.size) {
                "Invalid subpath($beginIndex, $endIndex) for $rawPath"
            }
            return Physical(segs.subList(beginIndex, endIndex).joinToString("/"))
        }

        override fun startsWith(other: KlibPath): Boolean {
            if (other !is Physical) return false
            if (isAbsolute != other.isAbsolute) return false
            if (!drivePrefix.equals(other.drivePrefix, ignoreCase = true)) return false
            if (other.rawPath == "/" || isDriveRoot(other.rawPath)) return isAbsolute
            if (other.rawPath.isEmpty()) return rawPath.isEmpty()
            val mySegs = segments
            val otherSegs = other.segments
            return mySegs.size >= otherSegs.size && mySegs.subList(0, otherSegs.size) == otherSegs
        }

        override fun startsWith(other: String): Boolean = startsWith(Physical(other))

        override fun endsWith(other: KlibPath): Boolean {
            if (other !is Physical) return false
            if (other.isAbsolute) return this == other
            if (other.rawPath.isEmpty()) return rawPath.isEmpty()
            val mySegs = segments
            val otherSegs = other.segments
            return mySegs.size >= otherSegs.size &&
                mySegs.subList(mySegs.size - otherSegs.size, mySegs.size) == otherSegs
        }

        override fun endsWith(other: String): Boolean = endsWith(Physical(other))

        override fun resolve(other: String): KlibPath = resolve(Physical(other))

        override fun resolve(other: KlibPath): KlibPath {
            require(other is Physical) { "Provider mismatch: cannot resolve Archive path against Physical path" }
            if (other.isAbsolute) return other
            if (other.rawPath.isEmpty()) return this
            if (rawPath.isEmpty()) return other
            return Physical(if (rawPath.endsWith("/")) "$rawPath${other.rawPath}" else "$rawPath/${other.rawPath}")
        }

        override fun normalize(): KlibPath {
            if (rawPath.isEmpty() || rawPath == "/" || isDriveRoot(rawPath)) return this
            val out = normalizeLexicalSegments(segments, isAbsolute)
            val joined = out.joinToString("/")
            val prefix = if (drivePrefix.isNotEmpty()) "$drivePrefix/" else if (isAbsolute) "/" else ""
            return Physical("$prefix$joined")
        }

        override fun toAbsolutePath(): KlibPath {
            if (isAbsolute) return this
            val cwd = currentWorkingDirectory()
            return if (rawPath.isEmpty()) Physical(cwd) else Physical("$cwd/$rawPath")
        }

        override fun toRealPath(): KlibPath {
            val target = if (rawPath.isEmpty()) "." else rawPath
            val resolved = _fullpath(null, target, 0u)
                ?: throw KlibIoException("Cannot resolve real path for '$rawPath' (errno=$errno)")
            return try {
                val res = Physical(resolved.toKString())
                if (!res.exists()) throw KlibIoException("Real path does not exist: '$rawPath'")
                res
            } finally {
                free(resolved)
            }
        }

        override fun relativize(other: KlibPath): KlibPath {
            require(other is Physical && isAbsolute == other.isAbsolute && drivePrefix.equals(other.drivePrefix, ignoreCase = true)) {
                "Cannot relativize incompatible paths: '$this' and '$other'"
            }
            val left = (normalize() as Physical).let {
                if (it.rawPath.isEmpty() || it.rawPath == "/" || isDriveRoot(it.rawPath)) emptyList() else it.segments
            }
            val right = (other.normalize() as Physical).let {
                if (it.rawPath.isEmpty() || it.rawPath == "/" || isDriveRoot(it.rawPath)) emptyList() else it.segments
            }
            var common = 0
            while (common < minOf(left.size, right.size) && left[common] == right[common]) common++
            val rel = (List(left.size - common) { ".." } + right.drop(common)).joinToString("/")
            return Physical(rel)
        }

        private val posixTarget: String
            get() = if (rawPath.isEmpty()) "." else rawPath

        override fun exists(): Boolean = memScoped {
            if (isDriveRoot(posixTarget)) return true
            val st = alloc<stat>()
            stat(posixTarget, st.ptr) == 0
        }

        override fun isDirectory(): Boolean = memScoped {
            if (isDriveRoot(posixTarget)) return true
            val st = alloc<stat>()
            stat(posixTarget, st.ptr) == 0 && (st.st_mode.toUInt() and S_IFMT.toUInt()) == S_IFDIR.toUInt()
        }

        override fun isRegularFile(): Boolean = memScoped {
            val st = alloc<stat>()
            stat(posixTarget, st.ptr) == 0 && (st.st_mode.toUInt() and S_IFMT.toUInt()) == S_IFREG.toUInt()
        }

        override fun readBytes(): ByteArray {
            try {
                return NativeFile(posixTarget).readBytes()
            } catch (e: Throwable) {
                if (e is KlibIoException) throw e
                throw KlibIoException("Failed to read '$rawPath': ${e.message}", e)
            }
        }

        override fun writeBytes(array: ByteArray) {
            try {
                NativeFile(posixTarget).writeBytes(array)
            } catch (e: Throwable) {
                if (e is KlibIoException) throw e
                throw KlibIoException("Failed to write '$rawPath': ${e.message}", e)
            }
        }

        override fun createDirectories(): KlibPath {
            if (!NativeFile(posixTarget).mkdirs()) {
                throw KlibIoException("Failed to create directories for '$rawPath' (errno=$errno)")
            }
            return this
        }

        override fun listDirectoryEntries(): List<KlibPath> {
            val dir = opendir(posixTarget)
                ?: throw KlibIoException("Cannot open directory '$rawPath' (errno=$errno)")
            try {
                val entries = ArrayList<KlibPath>()
                while (true) {
                    nk_clear_errno()
                    val entry = readdir(dir)
                    if (entry == null) {
                        if (errno != 0) throw KlibIoException("Error reading directory '$rawPath' (errno=$errno)")
                        break
                    }
                    val entryName = entry.pointed.d_name.toKString()
                    if (entryName != "." && entryName != "..") {
                        entries.add(resolve(entryName))
                    }
                }
                return entries.sortedBy { it.name }
            } finally {
                closedir(dir)
            }
        }

        override fun deleteIfExists(): Boolean = memScoped {
            val st = alloc<stat>()
            if (stat(posixTarget, st.ptr) != 0) {
                if (errno == ENOENT) return false
                throw KlibIoException("Cannot stat '$rawPath' (errno=$errno)")
            }
            val isDir = (st.st_mode.toUInt() and S_IFMT.toUInt()) == S_IFDIR.toUInt()
            val rc = if (isDir) rmdir(posixTarget) else remove(posixTarget)
            if (rc != 0) {
                throw KlibIoException("Cannot delete '$rawPath' (errno=$errno)")
            }
            true
        }

        override fun deleteRecursively() {
            val isDir = memScoped {
                val st = alloc<stat>()
                if (stat(posixTarget, st.ptr) != 0) {
                    if (errno == ENOENT) return
                    throw KlibIoException("Cannot stat '$rawPath' (errno=$errno)")
                }
                val mode = st.st_mode.toUInt() and S_IFMT.toUInt()
                if (mode == S_IFDIR.toUInt()) {
                    for (child in listDirectoryEntries()) {
                        child.deleteRecursively()
                    }
                    true
                } else {
                    false
                }
            }
            val rc = if (isDir) rmdir(posixTarget) else remove(posixTarget)
            if (rc != 0 && errno != ENOENT) {
                throw KlibIoException("Cannot delete '$rawPath' (errno=$errno)")
            }
        }

        override fun copyTo(target: KlibPath, overwrite: Boolean): KlibPath {
            require(target is Physical) { "Cannot copy Physical path to non-Physical target: $target" }
            if (!isRegularFile()) {
                if (isDirectory()) {
                    if (target.exists()) {
                        if (!overwrite) throw KlibIoException("Destination exists: $target")
                        if (!target.isDirectory()) target.deleteIfExists()
                    }
                    target.createDirectories()
                    return target
                }
                throw KlibIoException("Source file does not exist: $rawPath")
            }
            if (target.exists() && !overwrite) {
                throw KlibIoException("Destination exists: $target")
            }
            target.parent?.createDirectories()
            target.writeBytes(readBytes())
            return target
        }

        override fun copyToRecursively(target: KlibPath, followLinks: Boolean, overwrite: Boolean): KlibPath {
            require(target is Physical) { "Cannot copy Physical path to non-Physical target: $target" }
            if (isDirectory()) {
                target.createDirectories()
                for (child in listDirectoryEntries()) {
                    child.copyToRecursively(target.resolve(child.name), followLinks, overwrite)
                }
            } else {
                copyTo(target, overwrite)
            }
            return target
        }

        override fun toFile(): NativeFile = NativeFile(rawPath)

        override fun fileKey(): Any = memScoped {
            val st = alloc<stat>()
            if (stat(posixTarget, st.ptr) == 0 && st.st_ino.toULong() != 0uL) {
                PosixFileKey(st.st_dev.toULong(), st.st_ino.toULong())
            } else {
                toAbsolutePath().normalize().pathString
            }
        }

        override fun compareTo(other: KlibPath): Int {
            require(other is Physical) { "Cannot compare Physical path with Archive path" }
            return rawPath.compareTo(other.rawPath)
        }

        override fun equals(other: Any?): Boolean = other is Physical && rawPath == other.rawPath
        override fun hashCode(): Int = rawPath.hashCode()
    }

    class Archive(
        val zipPath: NativeZipPath,
        private val relativeSpelling: String? = null,
    ) : KlibPath() {
        internal val entryPath: String
            get() = zipPath.toString().trim('/')

        override val isAbsolute: Boolean
            get() = relativeSpelling == null

        override val pathString: String
            get() = relativeSpelling ?: if (entryPath.isEmpty()) "/" else "/$entryPath"

        internal val segments: List<String>
            get() = when {
                relativeSpelling != null -> if (relativeSpelling.isEmpty()) listOf("") else relativeSpelling.split('/')
                entryPath.isEmpty() -> emptyList()
                else -> entryPath.split('/')
            }

        override val name: String
            get() = if (relativeSpelling != null) {
                relativeSpelling.substringAfterLast('/')
            } else {
                zipPath.name
            }

        override val parent: KlibPath?
            get() {
                if (relativeSpelling != null) {
                    val idx = relativeSpelling.lastIndexOf('/')
                    return if (idx < 0) null else Archive(zipPath.fileSystem.getPath("/"), relativeSpelling.substring(0, idx))
                }
                if (entryPath.isEmpty()) return null
                val parentEntry = entryPath.substringBeforeLast('/', "")
                return Archive(zipPath.fileSystem.getPath("/$parentEntry"))
            }

        override val root: KlibPath?
            get() = if (isAbsolute) Archive(zipPath.fileSystem.getPath("/")) else null

        override val nameCount: Int
            get() = segments.size

        override fun subpath(beginIndex: Int, endIndex: Int): KlibPath {
            val segs = segments
            require(beginIndex >= 0 && beginIndex < segs.size && endIndex > beginIndex && endIndex <= segs.size) {
                "Invalid subpath($beginIndex, $endIndex) for $pathString"
            }
            val sub = segs.subList(beginIndex, endIndex).joinToString("/")
            return Archive(zipPath.fileSystem.getPath("/"), relativeSpelling = sub)
        }

        override fun startsWith(other: KlibPath): Boolean {
            if (other !is Archive || other.zipPath.fileSystem !== zipPath.fileSystem) return false
            if (isAbsolute != other.isAbsolute) return false
            if (other.pathString == "/") return isAbsolute
            if (other.pathString.isEmpty()) return pathString.isEmpty()
            val mySegs = segments
            val otherSegs = other.segments
            return mySegs.size >= otherSegs.size && mySegs.subList(0, otherSegs.size) == otherSegs
        }

        override fun startsWith(other: String): Boolean =
            if (other.startsWith('/')) startsWith(Archive(zipPath.fileSystem.getPath(other)))
            else startsWith(Archive(zipPath.fileSystem.getPath("/"), relativeSpelling = other))

        override fun endsWith(other: KlibPath): Boolean {
            val otherSegs: List<String>
            val otherAbs: Boolean
            when (other) {
                is Archive -> {
                    if (other.zipPath.fileSystem !== zipPath.fileSystem) return false
                    otherAbs = other.isAbsolute
                    otherSegs = other.segments
                    if (otherAbs) return this == other
                }
                is Physical -> {
                    otherAbs = other.isAbsolute
                    if (otherAbs) return false
                    otherSegs = other.segments
                }
            }
            val mySegs = segments
            return mySegs.size >= otherSegs.size &&
                mySegs.subList(mySegs.size - otherSegs.size, mySegs.size) == otherSegs
        }

        override fun endsWith(other: String): Boolean =
            if (other.startsWith('/')) endsWith(Archive(zipPath.fileSystem.getPath(other)))
            else endsWith(Archive(zipPath.fileSystem.getPath("/"), relativeSpelling = other))

        override fun resolve(other: String): KlibPath {
            if ('\u0000' in other) throw InvalidKlibPathException(other, "Nul character not allowed")
            if (other.isEmpty()) return this
            val cleanOther = normalizeUnixSpelling(other)
            if (cleanOther.startsWith('/')) {
                val normalized = normalizeLexicalSegments(cleanOther.trimStart('/').split('/'), isAbsolute = true)
                return Archive(zipPath.fileSystem.getPath("/" + normalized.joinToString("/")))
            }
            if (relativeSpelling != null) {
                val combined = if (relativeSpelling.isEmpty()) cleanOther else "$relativeSpelling/$cleanOther"
                return Archive(zipPath.fileSystem.getPath("/"), relativeSpelling = combined)
            }
            val baseSegs = if (entryPath.isEmpty()) emptyList() else entryPath.split('/')
            val combinedSegs = normalizeLexicalSegments(baseSegs + cleanOther.split('/'), isAbsolute = true)
            return Archive(zipPath.fileSystem.getPath("/" + combinedSegs.joinToString("/")))
        }

        override fun resolve(other: KlibPath): KlibPath = when (other) {
            is Archive -> {
                require(other.zipPath.fileSystem === zipPath.fileSystem) {
                    "Provider mismatch: cannot resolve across different ZIP file systems"
                }
                if (other.isAbsolute) other else resolve(other.pathString)
            }
            is Physical -> {
                require(!other.isAbsolute) {
                    "Provider mismatch: cannot resolve absolute Physical path against Archive path"
                }
                resolve(other.pathString)
            }
        }

        override fun normalize(): KlibPath {
            if (relativeSpelling == null) return this
            val norm = normalizeLexicalSegments(segments, isAbsolute = false).joinToString("/")
            return Archive(zipPath.fileSystem.getPath("/"), relativeSpelling = norm)
        }

        override fun toAbsolutePath(): KlibPath {
            if (relativeSpelling == null) return this
            val norm = normalizeLexicalSegments(segments, isAbsolute = true).joinToString("/")
            return Archive(zipPath.fileSystem.getPath("/$norm"))
        }

        override fun toRealPath(): KlibPath {
            val abs = toAbsolutePath() as Archive
            try {
                if (!abs.exists()) {
                    throw KlibIoException("No such ZIP entry: ${abs.pathString}")
                }
            } catch (e: Throwable) {
                if (e is KlibIoException) throw e
                throw KlibIoException("ZIP access failed for '${abs.pathString}': ${e.message}", e)
            }
            return abs
        }

        override fun relativize(other: KlibPath): KlibPath {
            require(other is Archive && other.zipPath.fileSystem === zipPath.fileSystem && isAbsolute == other.isAbsolute) {
                "Cannot relativize incompatible Archive paths: '$this' and '$other'"
            }
            val left = (normalize() as Archive).let {
                if (it.pathString.isEmpty() || it.pathString == "/") emptyList() else it.segments
            }
            val right = (other.normalize() as Archive).let {
                if (it.pathString.isEmpty() || it.pathString == "/") emptyList() else it.segments
            }
            var common = 0
            while (common < minOf(left.size, right.size) && left[common] == right[common]) common++
            val rel = (List(left.size - common) { ".." } + right.drop(common)).joinToString("/")
            return Archive(zipPath.fileSystem.getPath("/"), relativeSpelling = rel)
        }

        internal fun resolvedZipPath(): NativeZipPath = (toAbsolutePath() as Archive).zipPath

        override fun exists(): Boolean = try {
            resolvedZipPath().exists()
        } catch (e: Throwable) {
            throw KlibIoException("ZIP access failed for '$pathString': ${e.message}", e)
        }

        override fun isDirectory(): Boolean = try {
            resolvedZipPath().isDirectory()
        } catch (e: Throwable) {
            throw KlibIoException("ZIP access failed for '$pathString': ${e.message}", e)
        }

        override fun isRegularFile(): Boolean = try {
            resolvedZipPath().isRegularFile()
        } catch (e: Throwable) {
            throw KlibIoException("ZIP access failed for '$pathString': ${e.message}", e)
        }

        override fun readBytes(): ByteArray = try {
            resolvedZipPath().readBytes()
        } catch (e: Throwable) {
            throw KlibIoException("Failed to read ZIP entry '$pathString': ${e.message}", e)
        }

        override fun writeBytes(array: ByteArray): Nothing {
            throw UnsupportedOperationException("ZIP archive paths are read-only: $pathString")
        }

        override fun createDirectories(): Nothing =
            throw UnsupportedOperationException("ZIP archive paths are read-only: $pathString")

        override fun listDirectoryEntries(): List<KlibPath> = try {
            resolvedZipPath().children().map { Archive(it) }.sortedBy { it.name }
        } catch (e: Throwable) {
            throw KlibIoException("Failed to list ZIP directory '$pathString': ${e.message}", e)
        }

        override fun deleteIfExists(): Nothing =
            throw UnsupportedOperationException("ZIP archive paths are read-only: $pathString")

        override fun deleteRecursively(): Nothing =
            throw UnsupportedOperationException("ZIP archive paths are read-only: $pathString")

        override fun copyTo(target: KlibPath, overwrite: Boolean): KlibPath {
            require(target is Physical) { "Cannot copy Archive path to non-Physical target: $target" }
            if (!isRegularFile()) {
                if (isDirectory()) {
                    if (target.exists()) {
                        if (!overwrite) throw KlibIoException("Destination exists: $target")
                        if (!target.isDirectory()) target.deleteIfExists()
                    }
                    target.createDirectories()
                    return target
                }
                throw KlibIoException("Source ZIP entry does not exist: $pathString")
            }
            if (target.exists() && !overwrite) {
                throw KlibIoException("Destination exists: $target")
            }
            target.parent?.createDirectories()
            target.writeBytes(readBytes())
            return target
        }

        override fun copyToRecursively(target: KlibPath, followLinks: Boolean, overwrite: Boolean): KlibPath {
            require(target is Physical) { "Cannot copy Archive path to non-Physical target: $target" }
            if (isDirectory()) {
                target.createDirectories()
                for (child in listDirectoryEntries()) {
                    child.copyToRecursively(target.resolve(child.name), followLinks, overwrite)
                }
            } else {
                copyTo(target, overwrite)
            }
            return target
        }

        override fun toFile(): Nothing =
            throw UnsupportedOperationException("Archive path '$pathString' is not on the default POSIX file system")

        override fun fileKey(): Any = zipPath

        override fun compareTo(other: KlibPath): Int {
            require(other is Archive && other.zipPath.fileSystem === zipPath.fileSystem) {
                "Cannot compare Archive path with different provider: $other"
            }
            return pathString.compareTo(other.pathString)
        }

        override fun equals(other: Any?): Boolean =
            other is Archive && zipPath.fileSystem === other.zipPath.fileSystem && pathString == other.pathString

        override fun hashCode(): Int = 31 * zipPath.fileSystem.hashCode() + pathString.hashCode()
    }
}

fun KlibPath(path: String): KlibPath = KlibPath.Physical(path)

fun KlibPath(first: String, vararg more: String): KlibPath {
    var result: KlibPath = KlibPath.Physical(first)
    for (part in more) {
        if ('\u0000' in part) throw InvalidKlibPathException(part, "Nul character not allowed")
        if (part.isNotEmpty()) {
            result = result.resolve(part)
        }
    }
    return result
}

fun KlibPath?.createDirectories(): KlibPath =
    checkNotNull(this) { "Cannot create directories for null parent path" }.createDirectories()

object KlibPaths {
    fun get(first: String, vararg more: String): KlibPath = KlibPath(first, *more)
}

object KlibFiles {
    fun exists(path: KlibPath): Boolean = path.exists()

    fun isDirectory(path: KlibPath): Boolean = path.isDirectory()

    fun isRegularFile(path: KlibPath): Boolean = path.isRegularFile()

    fun size(path: KlibPath): Long = when (path) {
        is KlibPath.Physical -> memScoped {
            val target = if (path.rawPath.isEmpty()) "." else path.rawPath
            val st = alloc<stat>()
            if (stat(target, st.ptr) != 0) {
                throw KlibIoException("Cannot stat '${path.rawPath}' (errno=$errno)")
            }
            st.st_size.toLong()
        }
        is KlibPath.Archive -> try {
            path.resolvedZipPath().size()
        } catch (e: Throwable) {
            throw KlibIoException("Cannot stat ZIP entry '${path.pathString}': ${e.message}", e)
        }
    }

    private fun tempBaseDir(): String =
        (getenv("TMPDIR")?.toKString()?.takeIf { it.isNotEmpty() }
            ?: getenv("TEMP")?.toKString()?.takeIf { it.isNotEmpty() }
            ?: getenv("TMP")?.toKString()?.takeIf { it.isNotEmpty() }
            ?: "/tmp").replace('\\', '/').trimEnd('/')

    private fun sanitizePrefix(prefix: String?): String =
        (prefix ?: "klib").replace('/', '_').replace('\\', '_')

    private fun createTempDirInBase(base: String, prefix: String?): KlibPath {
        val cleanBase = base.replace('\\', '/').trimEnd('/')
        NativeFile(cleanBase).mkdirs()
        val pfx = sanitizePrefix(prefix)
        for (attempt in 0 until 1000) {
            val suffix = Random.Default.nextBytes(6).joinToString("") { (it.toInt() and 0xff).toString(16).padStart(2, '0') }
            val candidate = "$cleanBase/$pfx$suffix"
            if (mkdir(candidate) == 0) {
                return KlibPath.Physical(candidate)
            }
        }
        throw KlibIoException("Failed to create temp directory in '$cleanBase' (errno=$errno)")
    }

    fun createTempDirectory(prefix: String? = null): KlibPath =
        createTempDirInBase(tempBaseDir(), prefix)

    fun createTempDirectory(dir: KlibPath, prefix: String? = null): KlibPath =
        createTempDirInBase(dir.toFile().path, prefix)

    fun createTempFile(prefix: String? = null, suffix: String? = null): KlibPath {
        val actualSuffix = suffix ?: ".tmp"
        val baseDir = createTempDirectory(sanitizePrefix(prefix))
        val fileName = "${sanitizePrefix(prefix)}$actualSuffix"
        val file = baseDir.resolve(fileName)
        file.writeBytes(ByteArray(0))
        return file
    }

    fun walkFileTree(start: KlibPath, visitor: KlibSimpleFileVisitor<KlibPath>): KlibPath {
        fun walk(current: KlibPath): KlibFileVisitResult {
            val isDir = current.isDirectory()
            val isReg = !isDir && current.isRegularFile()
            val sz = if (isReg) try { size(current) } catch (_: Throwable) { 0L } else 0L
            val attrs = KlibBasicFileAttributes(isDirectory = isDir, isRegularFile = isReg, size = sz)
            if (isDir) {
                when (val pre = visitor.preVisitDirectory(current, attrs)) {
                    KlibFileVisitResult.TERMINATE -> return KlibFileVisitResult.TERMINATE
                    KlibFileVisitResult.SKIP_SUBTREE, KlibFileVisitResult.SKIP_SIBLINGS -> return pre
                    KlibFileVisitResult.CONTINUE -> {}
                }
                var dirExc: KlibIoException? = null
                try {
                    for (child in current.listDirectoryEntries()) {
                        val childRes = walk(child)
                        if (childRes == KlibFileVisitResult.TERMINATE) return KlibFileVisitResult.TERMINATE
                        if (childRes == KlibFileVisitResult.SKIP_SIBLINGS) break
                    }
                } catch (e: KlibIoException) {
                    dirExc = e
                }
                return visitor.postVisitDirectory(current, dirExc)
            } else {
                return visitor.visitFile(current, attrs)
            }
        }
        if (start.exists()) {
            walk(start)
        }
        return start
    }
}
