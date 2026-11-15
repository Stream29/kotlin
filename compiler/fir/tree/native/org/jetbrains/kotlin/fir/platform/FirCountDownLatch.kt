/*
 * Native host boundary for the original FIR resolve-state barriers.
 * Licensed under Apache 2.0; see porting/F1/license/LICENSE.txt.
 */

@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class, kotlin.experimental.ExperimentalNativeApi::class)

package org.jetbrains.kotlin.fir.platform

import kotlinx.cinterop.*
import platform.posix.*
import org.jetbrains.kotlin.storage.TimeUnit
import kotlin.native.ref.createCleaner

class FirCountDownLatch(initialCount: Int) {
    private class Resources {
        val mutex = nativeHeap.alloc<pthread_mutex_t>().ptr
        val condition = nativeHeap.alloc<pthread_cond_t>().ptr

        init {
            var mutexReady = false
            try {
                check(pthread_mutex_init(mutex, null) == 0)
                mutexReady = true
                memScoped {
                    val attr = alloc<pthread_condattr_t>()
                    check(pthread_condattr_init(attr.ptr) == 0)
                    try {
                        check(pthread_condattr_setclock(attr.ptr, CLOCK_MONOTONIC) == 0)
                        check(pthread_cond_init(condition, attr.ptr) == 0)
                    } finally {
                        pthread_condattr_destroy(attr.ptr)
                    }
                }
            } catch (failure: Throwable) {
                if (mutexReady) pthread_mutex_destroy(mutex)
                nativeHeap.free(condition)
                nativeHeap.free(mutex)
                throw failure
            }
        }

        fun dispose() {
            pthread_cond_destroy(condition)
            pthread_mutex_destroy(mutex)
            nativeHeap.free(condition)
            nativeHeap.free(mutex)
        }
    }

    private var remaining: Long = initialCount.toLong().also { require(it >= 0) }
    private val resources = Resources()
    @Suppress("unused")
    private val cleaner = createCleaner(resources) { it.dispose() }

    val count: Long
        get() {
            check(pthread_mutex_lock(resources.mutex) == 0)
            return try { remaining } finally { check(pthread_mutex_unlock(resources.mutex) == 0) }
        }

    fun countDown() {
        check(pthread_mutex_lock(resources.mutex) == 0)
        try {
            if (remaining > 0) {
                remaining--
                if (remaining == 0L) check(pthread_cond_broadcast(resources.condition) == 0)
            }
        } finally {
            check(pthread_mutex_unlock(resources.mutex) == 0)
        }
    }

    fun await() {
        check(pthread_mutex_lock(resources.mutex) == 0)
        try {
            while (remaining > 0) check(pthread_cond_wait(resources.condition, resources.mutex) == 0)
        } finally {
            check(pthread_mutex_unlock(resources.mutex) == 0)
        }
    }

    fun await(timeout: Long, unit: TimeUnit): Boolean {
        val multiplier = when (unit) {
            TimeUnit.NANOSECONDS -> 1L
            TimeUnit.MICROSECONDS -> 1_000L
            TimeUnit.MILLISECONDS -> 1_000_000L
            TimeUnit.SECONDS -> 1_000_000_000L
            TimeUnit.MINUTES -> 60_000_000_000L
            TimeUnit.HOURS -> 3_600_000_000_000L
            TimeUnit.DAYS -> 86_400_000_000_000L
        }
        val nanos = if (timeout <= 0) 0L else if (timeout > Long.MAX_VALUE / multiplier) Long.MAX_VALUE else timeout * multiplier
        return memScoped {
            val deadline = alloc<timespec>()
            check(clock_gettime(CLOCK_MONOTONIC, deadline.ptr) == 0)
            deadline.tv_sec += nanos / 1_000_000_000L
            val nanoPart = deadline.tv_nsec + nanos % 1_000_000_000L
            deadline.tv_sec += nanoPart / 1_000_000_000L
            deadline.tv_nsec = nanoPart % 1_000_000_000L
            check(pthread_mutex_lock(resources.mutex) == 0)
            try {
                while (remaining > 0) {
                    if (nanos == 0L) return@memScoped false
                    val result = pthread_cond_timedwait(resources.condition, resources.mutex, deadline.ptr)
                    if (result == ETIMEDOUT) return@memScoped remaining == 0L
                    check(result == 0) { "pthread_cond_timedwait failed: $result" }
                }
                true
            } finally {
                check(pthread_mutex_unlock(resources.mutex) == 0)
            }
        }
    }
}
