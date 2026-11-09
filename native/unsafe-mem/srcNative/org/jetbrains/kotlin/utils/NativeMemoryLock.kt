/*
 * Copyright 2026 NativeKotlin contributors.
 * SPDX-License-Identifier: Apache-2.0
 */
@file:OptIn(kotlin.concurrent.atomics.ExperimentalAtomicApi::class, kotlinx.cinterop.ExperimentalForeignApi::class)

package org.jetbrains.kotlin.utils

import kotlin.concurrent.atomics.AtomicLong
import platform.posix.pthread_self
import platform.posix.sched_yield

/** Reentrant linux_x64 monitor replacement, with no native allocation to dispose. */
internal class NativeMemoryLock {
    private val owner = AtomicLong(0L)
    private var depth = 0

    fun <T> withLock(block: () -> T): T {
        val thread = pthread_self().toLong()
        check(thread != 0L)
        if (owner.load() != thread) {
            while (!owner.compareAndSet(0L, thread)) sched_yield()
        }
        depth++
        return try {
            block()
        } finally {
            if (--depth == 0) owner.store(0L)
        }
    }
}
