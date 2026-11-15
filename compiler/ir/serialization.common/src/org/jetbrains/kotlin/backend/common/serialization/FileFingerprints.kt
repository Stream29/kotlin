/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.common.serialization

import org.jetbrains.kotlin.library.KotlinLibrary
import org.jetbrains.kotlin.library.SerializedIrFile
import org.jetbrains.kotlin.library.components.KlibIrComponent
import org.jetbrains.kotlin.library.components.irOrFail
import nativekotlin.host.files.NativeFile as File

value class FingerprintHash(val hash: Hash128Bits) {
    override fun toString(): String {
        return "${hash.lowBytes.toString(36)}$HASH_SEPARATOR${hash.highBytes.toString(36)}"
    }

    fun toByteArray(): ByteArray {
        return ByteArray(Long.SIZE_BYTES * 2) { index ->
            val value = if (index < Long.SIZE_BYTES) hash.lowBytes else hash.highBytes
            (value shr ((Long.SIZE_BYTES - 1 - index % Long.SIZE_BYTES) * 8)).toByte()
        }
    }

    companion object {
        private const val HASH_SEPARATOR = "."

        fun fromString(s: String): FingerprintHash? {
            val hashes = s.split(HASH_SEPARATOR).mapNotNull { it.toULongOrNull(36) }
            return hashes.takeIf { it.size == 2 }?.let { FingerprintHash(Hash128Bits(lowBytes = it[0], highBytes = it[1])) }
        }

        fun fromByteArray(bytes: ByteArray): FingerprintHash {
            if (bytes.size < Long.SIZE_BYTES * 2) throw IndexOutOfBoundsException()
            fun getLong(offset: Int): ULong = (offset until offset + Long.SIZE_BYTES).fold(0uL) { result, index ->
                (result shl 8) or bytes[index].toUByte().toULong()
            }
            val lowBytes = getLong(0)
            val highBytes = getLong(Long.SIZE_BYTES)
            return FingerprintHash(Hash128Bits(lowBytes, highBytes))
        }
    }
}

value class SerializedIrFileFingerprint private constructor(val fileFingerprint: FingerprintHash) {
    companion object {
        fun fromString(s: String): SerializedIrFileFingerprint? {
            return FingerprintHash.fromString(s)?.let { SerializedIrFileFingerprint(it) }
        }

        private fun calculateFileFingerprint(file: SerializedIrFile): FingerprintHash {
            val fileDataHash = cityHash128(file.fileData)
            val withTypesHash = cityHash128WithSeed(fileDataHash, file.types)
            val withSignaturesHash = cityHash128WithSeed(withTypesHash, file.signatures)
            val withStringsHash = cityHash128WithSeed(withSignaturesHash, file.strings)
            val withBodiesHash = cityHash128WithSeed(withStringsHash, file.bodies)
            val withFileEntriesHash = file.fileEntries?.let { cityHash128WithSeed(withBodiesHash, it) } ?: withBodiesHash
            return FingerprintHash(cityHash128WithSeed(withFileEntriesHash, file.declarations))
        }

        private fun calculateFileFingerprint(ir: KlibIrComponent, fileIndex: Int): FingerprintHash {
            val fileDataHash = cityHash128(ir.irFile(fileIndex))
            val withTypesHash = cityHash128WithSeed(fileDataHash, ir.types(fileIndex))
            val withSignaturesHash = cityHash128WithSeed(withTypesHash, ir.signatures(fileIndex))
            val withStringsHash = cityHash128WithSeed(withSignaturesHash, ir.stringLiterals(fileIndex))
            val withBodiesHash = cityHash128WithSeed(withStringsHash, ir.bodies(fileIndex))
            val withFileEntriesHash = ir.irFileEntries(fileIndex)?.let { cityHash128WithSeed(withBodiesHash, it) } ?: withBodiesHash
            return FingerprintHash(cityHash128WithSeed(withFileEntriesHash, ir.declarations(fileIndex)))
        }
    }

    constructor(file: SerializedIrFile) : this(calculateFileFingerprint(file))

    constructor(lib: KotlinLibrary, fileIndex: Int) : this(calculateFileFingerprint(lib.irOrFail, fileIndex))

    override fun toString(): String {
        return fileFingerprint.toString()
    }
}

value class SerializedKlibFingerprint(val klibFingerprint: FingerprintHash) {
    companion object {
        private fun List<SerializedIrFileFingerprint>.calculateKlibFingerprint(): FingerprintHash {
            val combinedHash = fold(Hash128Bits(size.toULong())) { acc, x ->
                acc.combineWith(x.fileFingerprint.hash)
            }
            return FingerprintHash(combinedHash)
        }

        // File.calculateKlibHash() and List<SerializedIrFileFingerprint>.calculateKlibFingerprint()
        // give different hashes for same klibs, it is ok
        private fun File.calculateKlibHash(prefix: String = "", seed: Hash128Bits = Hash128Bits()): Hash128Bits {
            var combinedHash = seed

            if (isDirectory) {
                serializationListFiles()!!.sortedBy { it.name }.forEach { f ->
                    val filePrefix = "$prefix${f.name}/"
                    combinedHash = f.calculateKlibHash(filePrefix, cityHash128WithSeed(combinedHash, filePrefix.serializationUtf8Bytes()))
                }
            } else {
                serializationForEachBlock { buffer, bufferSize ->
                    combinedHash = cityHash128WithSeed(combinedHash, buffer, 0, bufferSize)
                }
            }

            return combinedHash
        }

        fun fromString(s: String): SerializedKlibFingerprint? {
            return FingerprintHash.fromString(s)?.let { SerializedKlibFingerprint(it) }
        }
    }

    constructor(fileFingerprints: List<SerializedIrFileFingerprint>) : this(fileFingerprints.calculateKlibFingerprint())

    constructor(klibFile: File) : this(FingerprintHash(klibFile.calculateKlibHash()))

    override fun toString(): String {
        return klibFingerprint.toString()
    }
}
