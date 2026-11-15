/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.storage

import kotlinx.cinterop.*
import platform.posix.usleep
import kotlin.concurrent.AtomicReference
import kotlin.time.TimeSource

enum class TimeUnit {
    NANOSECONDS,
    MICROSECONDS,
    MILLISECONDS,
    SECONDS,
    MINUTES,
    HOURS,
    DAYS
}

fun interface Runnable {
    fun run()
}

open class InterruptedException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)

interface Lock {
    fun lock()
    fun unlock()
    fun tryLock(): Boolean
    fun tryLock(time: Long, unit: TimeUnit): Boolean
}

@OptIn(ExperimentalForeignApi::class)
open class ReentrantLock : Lock {
    private val owner = AtomicReference<Any?>(null)
    private var holdCount = 0

    override fun lock() {
        val me = currentStorageThreadToken
        if (owner.value === me) {
            holdCount++
            return
        }
        if (owner.compareAndSet(null, me)) {
            holdCount = 1
            return
        }
        lockContended(me)
    }

    private fun lockContended(me: Any) {
        while (true) {
            var spins = 0
            while (spins < 64) {
                if (owner.value == null && owner.compareAndSet(null, me)) {
                    holdCount = 1
                    return
                }
                spins++
            }
            usleep(50u)
        }
    }

    override fun unlock() {
        val me = currentStorageThreadToken
        check(owner.value === me) { "ReentrantLock.unlock called by non-owner thread" }
        val next = holdCount - 1
        if (next == 0) {
            holdCount = 0
            owner.value = null
        } else {
            holdCount = next
        }
    }

    override fun tryLock(): Boolean {
        val me = currentStorageThreadToken
        if (owner.value === me) {
            holdCount++
            return true
        }
        if (owner.compareAndSet(null, me)) {
            holdCount = 1
            return true
        }
        return false
    }

    override fun tryLock(time: Long, unit: TimeUnit): Boolean {
        if (tryLock()) return true
        if (time <= 0) return false
        val multiplier = when (unit) {
            TimeUnit.NANOSECONDS -> 1L
            TimeUnit.MICROSECONDS -> 1_000L
            TimeUnit.MILLISECONDS -> 1_000_000L
            TimeUnit.SECONDS -> 1_000_000_000L
            TimeUnit.MINUTES -> 60_000_000_000L
            TimeUnit.HOURS -> 3_600_000_000_000L
            TimeUnit.DAYS -> 86_400_000_000_000L
        }
        val timeoutNanos = if (time > Long.MAX_VALUE / multiplier) Long.MAX_VALUE else time * multiplier
        val start = TimeSource.Monotonic.markNow()
        val me = currentStorageThreadToken
        while (true) {
            var spins = 0
            while (spins < 32) {
                if (owner.value == null && owner.compareAndSet(null, me)) {
                    holdCount = 1
                    return true
                }
                spins++
            }
            val elapsed = start.elapsedNow().inWholeNanoseconds
            if (elapsed >= timeoutNanos) return false
            val sleepMicros = minOf(500L, maxOf(1L, (timeoutNanos - elapsed) / 1_000L)).toUInt()
            usleep(sleepMicros)
            if (owner.value == null && owner.compareAndSet(null, me)) {
                holdCount = 1
                return true
            }
        }
    }
}

private const val CHECK_CANCELLATION_PERIOD_MS: Long = 50

interface SimpleLock {
    fun lock()

    fun unlock()

    companion object {
        fun simpleLock(checkCancelled: Runnable? = null, interruptedExceptionHandler: ((InterruptedException) -> Unit)? = null): SimpleLock =
            if (checkCancelled != null && interruptedExceptionHandler != null) {
                CancellableSimpleLock(checkCancelled, interruptedExceptionHandler)
            } else {
                DefaultSimpleLock()
            }
    }
}

inline fun <T> SimpleLock.guarded(crossinline computable: () -> T): T {
    lock()
    return try {
        computable()
    } finally {
        unlock()
    }
}

object EmptySimpleLock : SimpleLock {
    override fun lock() {
    }

    override fun unlock() {
    }
}

open class DefaultSimpleLock(protected val lock: Lock = ReentrantLock()) : SimpleLock {

    override fun lock() = lock.lock()

    override fun unlock() = lock.unlock()

}

class CancellableSimpleLock(
    lock: Lock,
    private val checkCancelled: Runnable,
    private val interruptedExceptionHandler: (InterruptedException) -> Unit
) : DefaultSimpleLock(lock) {
    constructor(checkCancelled: Runnable, interruptedExceptionHandler: (InterruptedException) -> Unit) : this(
        checkCancelled = checkCancelled,
        lock = ReentrantLock(),
        interruptedExceptionHandler = interruptedExceptionHandler
    )

    override fun lock() {
        try {
            while (!lock.tryLock(CHECK_CANCELLATION_PERIOD_MS, TimeUnit.MILLISECONDS)) {
                checkCancelled.run()
            }
        } catch (e: InterruptedException) {
            interruptedExceptionHandler(e)
        }
    }
}
