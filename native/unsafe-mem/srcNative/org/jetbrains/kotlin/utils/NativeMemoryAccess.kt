/*
 * Copyright 2026 NativeKotlin contributors.
 * SPDX-License-Identifier: Apache-2.0
 */
@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package org.jetbrains.kotlin.utils

import kotlinx.cinterop.*
import platform.posix.free
import platform.posix.malloc
import platform.posix.memcpy
import platform.posix.memset

/** Native addresses are never dereferenced through an unaligned typed pointer. */
internal object NativeMemoryAccess : UnsafeMemoryAccess {
    // Free-list heads are atomic, but their in-chunk next/size words are not.
    // Serialize those word accesses to preserve JVM's non-tearing int semantics.
    private val intAccessLock = NativeMemoryLock()

    override fun allocateMemory(size: Long): Long {
        require(size > 0) { "allocateMemory size must be positive, but was $size" }
        return (malloc(size.toULong()) ?: throw OutOfMemoryError("malloc($size) returned null")).rawValue.toLong()
    }

    override fun freeMemory(address: Long) = free(address.toCPointer<ByteVar>())

    override fun getByte(address: Long): Byte = address.toCPointer<ByteVar>()!!.pointed.value
    override fun putByte(address: Long, value: Byte) { address.toCPointer<ByteVar>()!!.pointed.value = value }

    override fun getShort(address: Long): Short = memScoped {
        val value = alloc<ShortVar>()
        memcpy(value.ptr, address.toCPointer<ByteVar>(), 2uL)
        value.value
    }

    override fun putShort(address: Long, value: Short) = memScoped {
        val temporary = alloc<ShortVar>()
        temporary.value = value
        memcpy(address.toCPointer<ByteVar>(), temporary.ptr, 2uL)
        Unit
    }

    override fun getInt(address: Long): Int = intAccessLock.withLock {
        memScoped {
            val value = alloc<IntVar>()
            memcpy(value.ptr, address.toCPointer<ByteVar>(), 4uL)
            value.value
        }
    }

    override fun putInt(address: Long, value: Int) = intAccessLock.withLock {
        memScoped {
            val temporary = alloc<IntVar>()
            temporary.value = value
            memcpy(address.toCPointer<ByteVar>(), temporary.ptr, 4uL)
            Unit
        }
    }

    override fun getLong(address: Long): Long = memScoped {
        val value = alloc<LongVar>()
        memcpy(value.ptr, address.toCPointer<ByteVar>(), 8uL)
        value.value
    }

    override fun putLong(address: Long, value: Long) = memScoped {
        val temporary = alloc<LongVar>()
        temporary.value = value
        memcpy(address.toCPointer<ByteVar>(), temporary.ptr, 8uL)
        Unit
    }

    override fun getFloat(address: Long): Float = Float.fromBits(getInt(address))
    override fun putFloat(address: Long, value: Float) = putInt(address, value.toRawBits())
    override fun getDouble(address: Long): Double = Double.fromBits(getLong(address))
    override fun putDouble(address: Long, value: Double) = putLong(address, value.toRawBits())

    override fun zeroMemory(address: Long, length: Long) {
        require(length >= 0) { "zeroMemory length must be nonnegative, but was $length" }
        if (length == 0L) return
        memset(address.toCPointer<ByteVar>(), 0, length.toULong())
    }

    override fun copyFromByteArray(src: ByteArray, destAddress: Long, length: Int) {
        require(length >= 0) { "copyFromByteArray length must be nonnegative, but was $length" }
        if (length == 0) return
        src.usePinned { memcpy(destAddress.toCPointer<ByteVar>(), it.addressOf(0), length.toULong()) }
    }

    override fun copyToByteArray(srcAddress: Long, dest: ByteArray, length: Int) {
        require(length >= 0) { "copyToByteArray length must be nonnegative, but was $length" }
        if (length == 0) return
        dest.usePinned { memcpy(it.addressOf(0), srcAddress.toCPointer<ByteVar>(), length.toULong()) }
    }

    override fun copyFromCharArray(src: CharArray, destAddress: Long, lengthInChars: Int) {
        require(lengthInChars >= 0) { "copyFromCharArray length must be nonnegative, but was $lengthInChars" }
        if (lengthInChars == 0) return
        src.usePinned {
            memcpy(destAddress.toCPointer<ByteVar>(), it.addressOf(0), lengthInChars.toULong() * 2uL)
        }
    }
}

internal object UnsafeMemoryAccessProvider {
    fun getImplementation(): UnsafeMemoryAccess = NativeMemoryAccess
}
