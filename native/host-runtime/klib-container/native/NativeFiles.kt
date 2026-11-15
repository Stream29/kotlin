@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package nativekotlin.klib

import kotlinx.cinterop.*
import platform.posix.*
import platform.zlib.*
import nativekotlin.files.nk_openat
import nativekotlin.files.nk_clear_errno

private fun checkFd(fd: Int): Int {
    check(fd >= 0) { "File operation failed (errno=$errno)" }
    return fd
}

private fun fileMode(fd: Int): Int = memScoped {
    val info = alloc<stat>()
    check(fstat(fd, info.ptr) == 0)
    info.st_mode.toInt() and S_IFMT
}

private fun readFd(fd: Int, limit: Int): ByteArray = memScoped {
    val info = alloc<stat>()
    check(fstat(fd, info.ptr) == 0)
    require(info.st_mode.toInt() and S_IFMT == S_IFREG) { "Only regular files are supported" }
    require(info.st_size >= 0 && info.st_size <= limit.toLong()) { "File exceeds memory budget" }
    val size = info.st_size.toInt()
    val result = ByteArray(size)
    if (size > 0) {
        result.usePinned { pinned ->
            var offset = 0
            while (offset < size) {
                val count = read(fd, pinned.addressOf(offset), (size - offset).convert())
                if (count < 0 && errno == EINTR) continue
                check(count > 0) { "File changed or read failed" }
                offset += count.toInt()
            }
        }
    }
    val extra = alloc<ByteVar>()
    var count: Long
    do { count = read(fd, extra.ptr, 1.convert()) } while (count < 0 && errno == EINTR)
    check(count == 0L) { "File grew or read failed" }
    result
}

class DirectoryKlib(path: String) : KlibContainer {
    private var root = checkFd(open(path, O_RDONLY or O_DIRECTORY or O_CLOEXEC or O_NOFOLLOW))

    private fun openEntry(path: String): Int {
        validateEntryPath(path)
        check(root >= 0) { "Container closed" }
        var current = checkFd(dup(root))
        try {
            var segStart = 0
            val len = path.length
            while (segStart < len) {
                val slash = path.indexOf('/', segStart)
                val isLast = slash == -1
                val name = if (segStart == 0 && isLast) path else path.substring(segStart, if (isLast) len else slash)
                val flags = O_RDONLY or O_CLOEXEC or O_NOFOLLOW or O_NONBLOCK or
                    (if (!isLast) O_DIRECTORY else 0)
                val next = checkFd(nk_openat(current, name, flags))
                close(current)
                current = next
                if (isLast) break
                segStart = slash + 1
            }
            return current.also { current = -1 }
        } finally { if (current >= 0) close(current) }
    }

    override fun read(path: String): ByteArray {
        val fd = openEntry(path)
        return try { readFd(fd, MAX_ENTRY_BYTES) } finally { close(fd) }
    }

    private fun walkEntries(collectDirs: Boolean): List<String> {
        check(root >= 0)
        val result = mutableListOf<String>()
        var visited = 0
        fun walk(fd: Int, prefix: String, depth: Int) {
            require(depth <= 64) { "Directory nesting exceeds budget" }
            val duplicate = checkFd(nk_openat(fd, ".", O_RDONLY or O_DIRECTORY or O_CLOEXEC))
            val directory = fdopendir(duplicate)
            if (directory == null) { close(duplicate); error("Cannot enumerate directory") }
            try {
                while (true) {
                    nk_clear_errno()
                    val item = readdir(directory)
                    if (item == null) { check(errno == 0); break }
                    val name = item.pointed.d_name.toKString()
                    if (name == "." || name == "..") continue
                    require(++visited <= MAX_ENTRIES) { "Directory entry count exceeds budget" }
                    val relative = prefix + name
                    validateEntryPath(relative)
                    val child = checkFd(nk_openat(fd, name, O_RDONLY or O_NOFOLLOW or O_CLOEXEC or O_NONBLOCK))
                    try {
                        when (fileMode(child)) {
                            S_IFDIR -> {
                                if (collectDirs) {
                                    require(result.size < MAX_ENTRIES)
                                    result.add(relative)
                                }
                                walk(child, "$relative/", depth + 1)
                            }
                            S_IFREG -> {
                                if (!collectDirs) {
                                    require(result.size < MAX_ENTRIES)
                                    result.add(relative)
                                }
                            }
                            else -> error("Unsupported file type")
                        }
                    } finally { close(child) }
                }
            } finally { closedir(directory) }
        }
        walk(root, "", 0)
        return result.sorted()
    }

    override fun paths(): List<String> = walkEntries(collectDirs = false)

    fun directoryPaths(): List<String> = walkEntries(collectDirs = true)

    override fun close() {
        if (root >= 0) { close(root); root = -1 }
    }
}

fun crc32Native(bytes: ByteArray): UInt {
    val initCrc = platform.zlib.crc32(0u.convert(), null, 0u)
    if (bytes.isEmpty()) return initCrc.toUInt()
    return bytes.usePinned { pinned ->
        platform.zlib.crc32(initCrc, pinned.addressOf(0).reinterpret(), bytes.size.toUInt()).toUInt()
    }
}

fun inflateRawSliceNative(input: ByteArray, offset: Int, compressedSize: Int, expectedSize: Int): ByteArray = memScoped {
    require(expectedSize in 0..MAX_ENTRY_BYTES)
    require(offset >= 0 && compressedSize >= 0 && offset <= input.size - compressedSize)
    val output = ByteArray(expectedSize)
    val dummyOut = if (expectedSize == 0) alloc<UByteVar>() else null
    val stream = alloc<z_stream>()
    stream.zalloc = null; stream.zfree = null; stream.opaque = null
    check(inflateInit2_(stream.ptr, -15, zlibVersion(), sizeOf<z_stream>().toInt()) == Z_OK)
    try {
        if (compressedSize == 0) {
            stream.next_in = null
            stream.avail_in = 0u
            if (expectedSize == 0) {
                stream.next_out = dummyOut!!.ptr
                stream.avail_out = 1u
                val status = inflate(stream.ptr, Z_FINISH)
                require(status == Z_STREAM_END && stream.total_in.toLong() == 0L && stream.total_out.toLong() == 0L) {
                    "Invalid deflated ZIP entry"
                }
            } else {
                output.usePinned { dst ->
                    stream.next_out = dst.addressOf(0).reinterpret()
                    stream.avail_out = expectedSize.toUInt()
                    val status = inflate(stream.ptr, Z_FINISH)
                    require(status == Z_STREAM_END && stream.total_in.toLong() == 0L &&
                        stream.total_out.toLong() == expectedSize.toLong()) { "Invalid deflated ZIP entry" }
                }
            }
        } else {
            input.usePinned { src ->
                stream.next_in = src.addressOf(offset).reinterpret()
                stream.avail_in = compressedSize.toUInt()
                if (expectedSize == 0) {
                    stream.next_out = dummyOut!!.ptr
                    stream.avail_out = 1u
                    val status = inflate(stream.ptr, Z_FINISH)
                    require(status == Z_STREAM_END && stream.total_in.toLong() == compressedSize.toLong() &&
                        stream.total_out.toLong() == 0L) { "Invalid deflated ZIP entry" }
                } else {
                    output.usePinned { dst ->
                        stream.next_out = dst.addressOf(0).reinterpret()
                        stream.avail_out = expectedSize.toUInt()
                        val status = inflate(stream.ptr, Z_FINISH)
                        require(status == Z_STREAM_END && stream.total_in.toLong() == compressedSize.toLong() &&
                            stream.total_out.toLong() == expectedSize.toLong()) { "Invalid deflated ZIP entry" }
                    }
                }
            }
        }
    } finally { inflateEnd(stream.ptr) }
    output
}

fun inflateRawNative(input: ByteArray, expectedSize: Int): ByteArray =
    inflateRawSliceNative(input, 0, input.size, expectedSize)

fun openKlib(path: String): KlibContainer {
    val fd = checkFd(open(path, O_RDONLY or O_NOFOLLOW or O_CLOEXEC or O_NONBLOCK))
    return try {
        if (fileMode(fd) == S_IFDIR) DirectoryKlib(path)
        else ZipKlib(readFd(fd, MAX_ARCHIVE_BYTES), ::inflateRawSliceNative, ::crc32Native)
    } finally { close(fd) }
}

fun writeNewFile(path: String, bytes: ByteArray) {
    val fd = checkFd(open(path, O_WRONLY or O_CREAT or O_EXCL or O_NOFOLLOW or O_CLOEXEC, 384))
    try {
        if (bytes.isNotEmpty()) {
            bytes.usePinned { pinned ->
                var offset = 0
                while (offset < bytes.size) {
                    val written = write(fd, pinned.addressOf(offset), (bytes.size - offset).convert())
                    if (written < 0 && errno == EINTR) continue
                    check(written > 0) { "Write failed; output may be incomplete" }
                    offset += written.toInt()
                }
            }
        }
    } finally { close(fd) }
}
