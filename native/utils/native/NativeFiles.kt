@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package org.jetbrains.kotlin.konan.host

import kotlinx.cinterop.*
import nativekotlin.c1i.posix.c1i_delete_on_exit
import nativekotlin.c1i.posix.c1i_is_symlink
import nativekotlin.c1i.posix.c1i_mkstemps
import nativekotlin.c1i.posix.c1i_clear_errno
import platform.posix.*

fun environment(name: String): String? = getenv(name)?.toKString()

private fun ioFailure(operation: String): Nothing =
    error("$operation: ${strerror(errno)?.toKString()} (errno=$errno)")

class NativePath(private val value: String) {
    init { require('\u0000' !in value) }
    private val absolute get() = value.startsWith("/")
    private fun parts() = value.split('/').filter { it.isNotEmpty() }
    fun toFile() = NativeFile(value)
    fun toAbsolutePath() = NativePath(toFile().absolutePath)
    fun normalize(): NativePath {
        val result = mutableListOf<String>()
        for (part in parts()) when (part) {
            "." -> {}
            ".." -> if (result.isNotEmpty() && result.last() != "..") result.removeAt(result.lastIndex)
                    else if (!absolute) result.add(part)
            else -> result.add(part)
        }
        return NativePath((if (absolute) "/" else "") + result.joinToString("/"))
    }
    fun startsWith(other: NativePath): Boolean =
        absolute == other.absolute && parts().take(other.parts().size) == other.parts()
    fun relativize(other: NativePath): NativePath {
        require(absolute == other.absolute) { "Cannot relativize absolute and relative paths" }
        val left = normalize().parts()
        val right = other.normalize().parts()
        var common = 0
        while (common < minOf(left.size, right.size) && left[common] == right[common]) common++
        return NativePath((List(left.size - common) { ".." } + right.drop(common)).joinToString("/"))
    }
    fun resolve(other: NativePath): NativePath = resolve(other.toString())
    fun resolve(other: String) = NativePath(
        if (other.startsWith("/") || value.isEmpty()) other else if (other.isEmpty()) value
        else if (value.endsWith("/")) "$value$other" else "$value/$other"
    )
    fun resolveSibling(other: String): NativePath {
        if (other.startsWith("/")) return NativePath(other)
        val current = value.trimEnd('/')
        val separator = current.lastIndexOf('/')
        if (separator < 0) return NativePath(other)
        val parent = when {
            separator == 0 -> "/"
            separator == 1 && current.startsWith("//") -> "//"
            else -> current.substring(0, separator)
        }
        return NativePath(parent).resolve(other)
    }
    override fun toString() = value
    override fun equals(other: Any?) = other is NativePath && value == other.value
    override fun hashCode() = value.hashCode()
}

object NativePaths {
    fun get(value: String) = NativePath(value)
}

class NativeFile(val path: String) {
    init { require('\u0000' !in path) }
    constructor(parent: NativeFile, child: String) : this(parent.toPath().resolve(child).toString())
    companion object { const val separator = "/" }
    val name get() = path.trimEnd('/').substringAfterLast('/')
    val parentFile: NativeFile get() = NativeFile(absolutePath.substringBeforeLast('/').ifEmpty { "/" })
    val absolutePath: String get() {
        if (path.startsWith("/")) return path
        val cwd = getcwd(null, 0u) ?: ioFailure("getcwd")
        return try { "${cwd.toKString()}/$path" } finally { free(cwd) }
    }
    val canonicalPath: String get() {
        val resolved = realpath(absolutePath, null)
        if (resolved != null) return try { resolved.toKString() } finally { free(resolved) }
        if (errno != ENOENT && errno != ENOTDIR) ioFailure("realpath($path)")
        val unresolved = absolutePath.trimEnd('/')
        val parent = NativeFile(unresolved.substringBeforeLast('/').ifEmpty { "/" }).canonicalPath
        return when (val tail = unresolved.substringAfterLast('/')) {
            "." -> parent
            ".." -> NativeFile(parent).parentFile.path
            else -> parent.trimEnd('/') + "/" + tail
        }
    }
    val canonicalFile get() = NativeFile(canonicalPath)
    fun toPath() = NativePath(path)
    fun resolve(child: String) = NativeFile(this, child)
    fun exists(): Boolean = access(path, F_OK) == 0
    val isDirectory: Boolean get() = memScoped {
        val st = alloc<stat>()
        stat(path, st.ptr) == 0 && (st.st_mode and S_IFMT.toUInt()) == S_IFDIR.toUInt()
    }
    fun length(): Long = memScoped {
        val st = alloc<stat>()
        if (stat(path, st.ptr) != 0) 0L else st.st_size
    }
    fun mkdirs(): Boolean {
        if (isDirectory) return true
        if (absolutePath != "/" && !parentFile.mkdirs()) return false
        return mkdir(path, 0x1ffu) == 0 || isDirectory
    }
    fun delete() = remove(path) == 0
    fun deleteOnExit() { check(c1i_delete_on_exit(absolutePath) == 0) { "deleteOnExit($path)" } }
    fun children(): List<NativeFile> {
        val directory = opendir(path) ?: ioFailure("opendir($path)")
        return try {
            buildList {
                while (true) {
                    c1i_clear_errno()
                    val entry = readdir(directory) ?: run {
                        if (errno != 0) ioFailure("readdir($path)")
                        break
                    }
                    val name = entry.pointed.d_name.toKString()
                    if (name != "." && name != "..") add(resolve(name))
                }
            }
        } finally { closedir(directory) }
    }
    fun walkTopDown(): Sequence<NativeFile> = sequence {
        yield(this@NativeFile)
        if (isDirectory) for (child in children()) yieldAll(child.walkTopDown())
    }
    fun deleteRecursively(): Boolean {
        var result = true
        if (isDirectory && c1i_is_symlink(path) == 0) for (child in children()) result = child.deleteRecursively() && result
        return delete() && result
    }
    fun forEachBlock(block: (ByteArray, Int) -> Unit) {
        val file = fopen(path, "rb") ?: ioFailure("fopen($path)")
        try {
            val buffer = ByteArray(8192)
            buffer.usePinned { pinned ->
                while (true) {
                    val count = fread(pinned.addressOf(0), 1u, buffer.size.convert(), file).toInt()
                    if (count > 0) block(buffer, count)
                    if (count < buffer.size) {
                        if (ferror(file) != 0) ioFailure("fread($path)")
                        break
                    }
                }
            }
        } finally { fclose(file) }
    }
    fun readBytes(): ByteArray {
        val chunks = mutableListOf<ByteArray>()
        var size = 0
        forEachBlock { bytes, count -> chunks.add(bytes.copyOf(count)); size += count }
        val result = ByteArray(size)
        var offset = 0
        for (chunk in chunks) { chunk.copyInto(result, offset); offset += chunk.size }
        return result
    }
    fun readText() = readBytes().decodeToString()
    fun readLines(): List<String> = readText().let {
        if (it.isEmpty()) emptyList() else it.split(Regex("\\r\\n|\\n|\\r")).let { lines ->
            if (lines.last().isEmpty()) lines.dropLast(1) else lines
        }
    }
    fun writeBytes(bytes: ByteArray) {
        val file = fopen(path, "wb") ?: ioFailure("fopen($path)")
        try { writeAll(file, bytes) } finally { if (fclose(file) != 0) ioFailure("fclose($path)") }
    }
    fun writeText(text: String) = writeBytes(text.encodeToByteArray())
    fun bufferedWriter() = NativeWriter(path)
    fun copyTo(target: NativeFile, overwrite: Boolean = false): NativeFile {
        check(overwrite || !target.exists()) { "Target exists: $target" }
        val file = fopen(target.path, "wb") ?: ioFailure("fopen($target)")
        try { forEachBlock { bytes, size -> writeAll(file, bytes.copyOf(size)) } }
        finally { if (fclose(file) != 0) ioFailure("fclose($target)") }
        return target
    }
    override fun toString() = path
    override fun equals(other: Any?) = other is NativeFile && path == other.path
    override fun hashCode() = path.hashCode()
}

private fun writeAll(file: CPointer<FILE>, bytes: ByteArray) {
    if (bytes.isEmpty()) return
    bytes.usePinned { pinned ->
        var offset = 0
        while (offset < bytes.size) {
            val count = fwrite(pinned.addressOf(offset), 1u, (bytes.size - offset).convert(), file).toInt()
            if (count <= 0) ioFailure("fwrite")
            offset += count
        }
    }
}

class NativeWriter(path: String) : Appendable, AutoCloseable {
    private var file: CPointer<FILE>? = fopen(path, "wb") ?: ioFailure("fopen($path)")
    override fun append(value: CharSequence?): Appendable = apply {
        writeAll(checkNotNull(file), (value?.toString() ?: "null").encodeToByteArray())
    }
    override fun append(value: CharSequence?, startIndex: Int, endIndex: Int): Appendable =
        append((value ?: "null").subSequence(startIndex, endIndex))
    override fun append(value: Char): Appendable = append(value.toString())
    override fun close() {
        val current = file ?: return
        file = null
        if (fclose(current) != 0) ioFailure("fclose")
    }
}

object NativeFiles {
    private fun pattern(prefix: String?, suffix: String) =
        "${environment("TMPDIR") ?: "/tmp"}/${prefix ?: "cinterop"}XXXXXX$suffix".encodeToByteArray() + byteArrayOf(0)
    fun createTempFile(prefix: String?, suffix: String?): NativePath {
        val ending = suffix ?: ".tmp"
        val bytes = pattern(prefix, ending)
        return bytes.usePinned {
            check(c1i_mkstemps(it.addressOf(0), ending.encodeToByteArray().size) == 0) { "mkstemps failed" }
            NativePath(it.addressOf(0).toKString())
        }
    }
    fun createTempDirectory(prefix: String): NativePath {
        val bytes = pattern(prefix, "")
        return bytes.usePinned {
            val path = mkdtemp(it.addressOf(0)) ?: ioFailure("mkdtemp")
            NativePath(path.toKString())
        }
    }
    fun isDirectory(path: NativePath) = path.toFile().isDirectory
    fun write(path: NativePath, bytes: ByteArray): NativePath = path.also { it.toFile().writeBytes(bytes) }
}
