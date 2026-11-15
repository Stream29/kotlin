@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package nativekotlin.host

import kotlinx.cinterop.*
import platform.posix.*

/** Read one regular source file through a stable descriptor; do not block on devices/FIFOs. */
fun readSourceFile(path: String, maxBytes: Int = 16 * 1024 * 1024): String =
    readRegularFileBytes(path, maxBytes).decodeToString(throwOnInvalidSequence = true)

fun readRegularFileBytes(path: String, maxBytes: Int = 16 * 1024 * 1024): ByteArray {
    require('\u0000' !in path && maxBytes >= 0)
    val fd = open(path, O_RDONLY or O_CLOEXEC or O_NONBLOCK)
    check(fd >= 0) { "Cannot open source file: $path (errno=$errno)" }
    try {
        return memScoped {
            val info = alloc<stat>()
            check(fstat(fd, info.ptr) == 0) { "Cannot stat source: $path" }
            require(info.st_mode.toInt() and S_IFMT == S_IFREG) { "Not a regular source file: $path" }
            require(info.st_size in 0..maxBytes.toLong()) { "Source exceeds $maxBytes bytes: $path" }
            val bytes = ByteArray(info.st_size.toInt())
            bytes.usePinned { pinned ->
                var offset = 0
                while (offset < bytes.size) {
                    val count = read(fd, pinned.addressOf(offset), (bytes.size - offset).convert())
                    if (count < 0 && errno == EINTR) continue
                    check(count > 0) { "Source changed or read failed: $path" }
                    offset += count.toInt()
                }
            }
            val extra = alloc<ByteVar>()
            var count: Long
            do { count = read(fd, extra.ptr, 1.convert()) } while (count < 0 && errno == EINTR)
            check(count == 0L) { "Source grew or read failed: $path" }
            bytes
        }
    } finally {
        close(fd)
    }
}
