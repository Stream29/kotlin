package nativekotlin.klib

const val MAX_ENTRY_BYTES: Int = 64 * 1024 * 1024
const val MAX_ARCHIVE_BYTES: Int = 256 * 1024 * 1024
const val MAX_ENTRIES: Int = 60000

interface KlibContainer : AutoCloseable {
    fun paths(): List<String>
    fun read(path: String): ByteArray
}

fun validateEntryPath(path: String) {
    val len = path.length
    require(len in 1..4096 && path[0] != '/') { "Unsafe KLIB path" }
    var segStart = 0
    for (i in 0..len) {
        if (i == len || path[i] == '/') {
            val segLen = i - segStart
            require(segLen > 0) { "Unsafe KLIB path" }
            require(!(segLen == 1 && path[segStart] == '.')) { "Unsafe KLIB path" }
            require(!(segLen == 2 && path[segStart] == '.' && path[segStart + 1] == '.')) { "Unsafe KLIB path" }
            segStart = i + 1
        } else {
            val c = path[i]
            require(c != '\\' && c != '\u0000') { "Unsafe KLIB path" }
        }
    }
}

private val crcTable = IntArray(256) { value ->
    var crc = value
    repeat(8) { crc = (crc ushr 1) xor (if (crc and 1 != 0) 0xedb88320u.toInt() else 0) }
    crc
}

fun crc32(bytes: ByteArray): UInt {
    var crc = -1
    for (byte in bytes) {
        crc = (crc ushr 8) xor crcTable[(crc xor byte.toInt()) and 255]
    }
    return crc.inv().toUInt()
}

/** Bounded ZIP32 reader. Directory names are accepted but never extracted. */
class ZipKlib(
    private val bytes: ByteArray,
    private val inflateSlice: (ByteArray, Int, Int, Int) -> ByteArray,
    private val crc32Fn: (ByteArray) -> UInt = ::crc32,
) : KlibContainer {
    constructor(
        bytes: ByteArray,
        inflateRaw: (ByteArray, Int) -> ByteArray,
    ) : this(
        bytes = bytes,
        inflateSlice = { src, offset, length, expectedSize ->
            inflateRaw(src.copyOfRange(offset, offset + length), expectedSize)
        },
        crc32Fn = ::crc32,
    )

    private data class Entry(val offset: Int, val compressed: Int, val size: Int, val method: Int, val crc: UInt)
    private val entries = LinkedHashMap<String, Entry>()
    private val directories = LinkedHashSet<String>()

    private fun range(offset: Int, count: Int) {
        require(offset >= 0 && count >= 0 && offset <= bytes.size && count <= bytes.size - offset) { "Truncated ZIP" }
    }
    private fun u16(offset: Int): Int {
        range(offset, 2)
        return (bytes[offset].toInt() and 255) or ((bytes[offset + 1].toInt() and 255) shl 8)
    }
    private fun u32(offset: Int): UInt {
        range(offset, 4)
        return u16(offset).toUInt() or (u16(offset + 2).toUInt() shl 16)
    }
    private fun size32(offset: Int): Int {
        val value = u32(offset)
        require(value <= Int.MAX_VALUE.toUInt()) { "ZIP64 or oversized field is unsupported" }
        return value.toInt()
    }
    private fun regionEquals(offA: Int, offB: Int, length: Int): Boolean {
        for (i in 0 until length) {
            if (bytes[offA + i] != bytes[offB + i]) return false
        }
        return true
    }

    init {
        require(bytes.size in 22..MAX_ARCHIVE_BYTES) { "Invalid ZIP size" }
        val lower = maxOf(0, bytes.size - 65557)
        val end = (bytes.size - 22 downTo lower).firstOrNull {
            u32(it) == 0x06054b50u && it + 22 + u16(it + 20) == bytes.size
        } ?: error("ZIP central directory not found")
        require(u16(end + 4) == 0 && u16(end + 6) == 0) { "Split ZIP is unsupported" }
        val count = u16(end + 10)
        require(count == u16(end + 8) && count <= MAX_ENTRIES) { "Invalid ZIP entry count" }
        val directorySize = size32(end + 12)
        val directoryStart = size32(end + 16)
        range(directoryStart, directorySize)
        require(directoryStart <= end && directorySize == end - directoryStart) { "Invalid central directory bounds" }
        var cursor = directoryStart
        var totalSize = 0L
        val names = HashSet<String>(count * 2)
        val localRanges = ArrayList<Pair<Int, Int>>(count)
        repeat(count) {
            range(cursor, 46)
            require(u32(cursor) == 0x02014b50u) { "Invalid central directory entry" }
            val flags = u16(cursor + 8)
            val method = u16(cursor + 10)
            require(flags and 1 == 0 && flags and 0x40 == 0) { "Encrypted ZIP is unsupported" }
            require(method == 0 || method == 8) { "ZIP compression method $method is unsupported" }
            val compressed = size32(cursor + 20)
            val size = size32(cursor + 24)
            require(size <= MAX_ENTRY_BYTES) { "ZIP entry exceeds memory budget" }
            totalSize += size
            require(totalSize <= MAX_ARCHIVE_BYTES) { "ZIP expanded size exceeds budget" }
            val nameLength = u16(cursor + 28)
            val extraLength = u16(cursor + 30)
            val commentLength = u16(cursor + 32)
            require(u16(cursor + 34) == 0)
            val externalMode = (u32(cursor + 38) shr 16).toInt()
            require(externalMode and 0xf000 != 0xa000) { "ZIP symlinks are unsupported" }
            val local = size32(cursor + 42)
            range(cursor + 46, nameLength + extraLength + commentLength)
            if (flags and 0x800 == 0) {
                for (i in 0 until nameLength) {
                    require(bytes[cursor + 46 + i] >= 0) { "Non-UTF8 ZIP filename is unsupported" }
                }
            }
            val name = bytes.decodeToString(cursor + 46, cursor + 46 + nameLength, throwOnInvalidSequence = true)
            val isDirectory = name.endsWith('/')
            val entryPath = if (isDirectory) name.dropLast(1) else name
            validateEntryPath(entryPath)
            require(names.add(name)) { "Duplicate ZIP entry" }
            range(local, 30)
            require(u32(local) == 0x04034b50u && u16(local + 8) == method && u16(local + 6) == flags)
            val localNameLength = u16(local + 26)
            val localExtraLength = u16(local + 28)
            range(local + 30, localNameLength + localExtraLength)
            require(localNameLength == nameLength && regionEquals(local + 30, cursor + 46, nameLength))
            val start = local + 30 + localNameLength + localExtraLength
            require(start <= directoryStart && compressed <= directoryStart - start) { "ZIP payload overlaps directory" }
            if (flags and 8 == 0) {
                require(u32(local + 14) == u32(cursor + 16) && size32(local + 18) == compressed && size32(local + 22) == size)
            }
            localRanges.add(local to start + compressed)
            if (isDirectory) {
                require(size == 0) { "Non-empty ZIP directory entry: $name" }
                directories.add(entryPath)
            } else {
                entries[name] = Entry(start, compressed, size, method, u32(cursor + 16))
            }
            cursor += 46 + nameLength + extraLength + commentLength
            require(cursor <= end)
        }
        require(cursor == end)
        localRanges.sortBy { it.first }
        for (i in 1 until localRanges.size) require(localRanges[i - 1].second <= localRanges[i].first) { "Overlapping ZIP entries" }
    }

    override fun paths(): List<String> = entries.keys.sorted()
    fun directoryPaths(): List<String> = directories.sorted()
    fun entrySize(path: String): Long {
        validateEntryPath(path)
        entries[path]?.let { return it.size.toLong() }
        if (path in directories) return 0L
        error("KLIB entry not found: $path")
    }
    override fun read(path: String): ByteArray {
        validateEntryPath(path)
        val entry = entries[path] ?: error("KLIB entry not found: $path")
        val result = if (entry.method == 0) {
            bytes.copyOfRange(entry.offset, entry.offset + entry.compressed)
        } else {
            inflateSlice(bytes, entry.offset, entry.compressed, entry.size)
        }
        require(result.size == entry.size && crc32Fn(result) == entry.crc) { "ZIP length or CRC mismatch: $path" }
        return result
    }
    override fun close() {}
}

private class ZipOutput(initialCapacity: Int = 65536) {
    private var data = ByteArray(initialCapacity)
    var size: Int = 0
        private set
    private fun reserve(count: Int) {
        require(count >= 0 && count <= MAX_ARCHIVE_BYTES - size) { "Output ZIP exceeds budget" }
        if (size + count > data.size) data = data.copyOf(maxOf(size + count, minOf(data.size * 2, MAX_ARCHIVE_BYTES)))
    }
    fun raw(bytes: ByteArray) { reserve(bytes.size); bytes.copyInto(data, size); size += bytes.size }
    fun u16(value: Int) { reserve(2); data[size++] = value.toByte(); data[size++] = (value ushr 8).toByte() }
    fun u32(value: UInt) { u16(value.toInt()); u16((value shr 16).toInt()) }
    fun result() = if (size == data.size) data else data.copyOf(size)
}

/** Store-only ZIP writer: contents are preserved; timestamps are fixed to 1980-01-01. */
fun packKlib(container: KlibContainer): ByteArray = packKlib(container, emptyList())

fun packKlib(container: KlibContainer, directoryPaths: Iterable<String>): ByteArray =
    packKlibWithCrc(container, directoryPaths, ::crc32)

fun packKlibWithCrc(
    container: KlibContainer,
    directoryPaths: Iterable<String>,
    crc32Fn: (ByteArray) -> UInt,
): ByteArray {
    val out = ZipOutput()
    data class Index(val name: ByteArray, val crc: UInt, val size: Int, val offset: Int)
    val dirs = directoryPaths.map { it.trim('/') }.filter { it.isNotEmpty() }.distinct().sorted()
    val paths = container.paths().sorted()
    val index = ArrayList<Index>(dirs.size + paths.size)
    require(dirs.size + paths.size <= MAX_ENTRIES && paths.size == paths.toSet().size)
    val fileSet = paths.toSet()
    for (dir in dirs) {
        validateEntryPath(dir)
        require(dir !in fileSet) { "Directory/file collision in KLIB ZIP: $dir" }
        val name = "$dir/".encodeToByteArray()
        require(name.size <= 65535)
        index.add(Index(name, 0u, 0, out.size))
        out.u32(0x04034b50u); out.u16(20); out.u16(0x800); out.u16(0); out.u16(0); out.u16(0x21)
        out.u32(0u); out.u32(0u); out.u32(0u); out.u16(name.size); out.u16(0)
        out.raw(name)
    }
    for (path in paths) {
        validateEntryPath(path)
        val name = path.encodeToByteArray()
        require(name.size <= 65535)
        val data = container.read(path)
        require(data.size <= MAX_ENTRY_BYTES)
        val crc = crc32Fn(data)
        index.add(Index(name, crc, data.size, out.size))
        out.u32(0x04034b50u); out.u16(20); out.u16(0x800); out.u16(0); out.u16(0); out.u16(0x21)
        out.u32(crc); out.u32(data.size.toUInt()); out.u32(data.size.toUInt()); out.u16(name.size); out.u16(0)
        out.raw(name); out.raw(data)
    }
    val start = out.size
    for (entry in index) {
        out.u32(0x02014b50u); out.u16(20); out.u16(20); out.u16(0x800); out.u16(0); out.u16(0); out.u16(0x21)
        out.u32(entry.crc); out.u32(entry.size.toUInt()); out.u32(entry.size.toUInt())
        out.u16(entry.name.size); out.u16(0); out.u16(0); out.u16(0); out.u16(0); out.u32(0u); out.u32(entry.offset.toUInt())
        out.raw(entry.name)
    }
    val directorySize = out.size - start
    out.u32(0x06054b50u); out.u16(0); out.u16(0); out.u16(index.size); out.u16(index.size)
    out.u32(directorySize.toUInt()); out.u32(start.toUInt()); out.u16(0)
    return out.result()
}
