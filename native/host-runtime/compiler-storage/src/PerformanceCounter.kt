/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.util

import kotlinx.cinterop.*
import org.jetbrains.kotlin.storage.DefaultSimpleLock
import org.jetbrains.kotlin.storage.guarded
import platform.posix.*

abstract class PerformanceCounter protected constructor(val name: String) {
    companion object {
        private val countersLock = DefaultSimpleLock()
        private val allCounters = arrayListOf<PerformanceCounter>()

        private var enabled = false

        @OptIn(ExperimentalForeignApi::class)
        fun currentTime(): Long = memScoped {
            val ts = alloc<timespec>()
            clock_gettime(CLOCK_MONOTONIC, ts.ptr)
            ts.tv_sec * 1_000_000_000L + ts.tv_nsec
        }

        fun report(consumer: (String) -> Unit) {
            val countersCopy = countersLock.guarded {
                allCounters.toTypedArray()
            }
            countersCopy.forEach { it.report(consumer) }
        }

        fun report(consumer: (String, Int, Long) -> Unit) {
            val countersCopy = countersLock.guarded {
                allCounters.toTypedArray()
            }
            countersCopy.forEach { it.report(consumer) }
        }

        val numberOfCounters: Int
            get() = countersLock.guarded { allCounters.size }

        fun setTimeCounterEnabled(enable: Boolean) {
            enabled = enable
        }

        fun resetAllCounters() {
            countersLock.guarded {
                allCounters.forEach(PerformanceCounter::reset)
            }
        }

        fun create(name: String, reenterable: Boolean = false): PerformanceCounter {
            return if (reenterable)
                ReenterableCounter(name)
            else
                SimpleCounter(name)
        }

        fun create(name: String, vararg excluded: PerformanceCounter): PerformanceCounter = CounterWithExclude(name, *excluded)

        internal inline fun <T> getOrPut(threadLocal: ThreadLocal<T>, default: () -> T): T =
            threadLocal.get() ?: default().also { threadLocal.set(it) }
    }

    internal val excludedFrom: MutableList<CounterWithExclude> = ArrayList()

    private var count: Int = 0
    private var totalTimeNanos: Long = 0

    init {
        countersLock.guarded {
            allCounters.add(this)
        }
    }

    fun increment() {
        count++
    }

    fun <T> time(block: () -> T): T {
        count++
        if (!enabled) return block()

        excludedFrom.forEach { it.enterExcludedMethod() }
        try {
            return countTime(block)
        } finally {
            excludedFrom.forEach { it.exitExcludedMethod() }
        }
    }

    fun reset() {
        count = 0
        totalTimeNanos = 0
    }

    protected fun incrementTime(delta: Long) {
        totalTimeNanos += delta
    }

    protected abstract fun <T> countTime(block: () -> T): T

    fun report(consumer: (String) -> Unit) {
        if (totalTimeNanos == 0L) {
            consumer("$name performed $count times")
        } else {
            val millis = totalTimeNanos / 1_000_000L
            consumer("$name performed $count times, total time $millis ms")
        }
    }

    fun report(consumer: (String, Int, Long) -> Unit) =
        consumer(name, count, totalTimeNanos)
}

@OptIn(ExperimentalForeignApi::class)
internal class ThreadLocal<T> {
    private val key: pthread_key_t = memScoped {
        val slot = alloc<pthread_key_tVar>()
        check(pthread_key_create(slot.ptr, staticCFunction { pointer ->
            pointer?.asStableRef<Any>()?.dispose()
        }) == 0)
        slot.value
    }

    @Suppress("UNCHECKED_CAST")
    fun get(): T? = pthread_getspecific(key)?.asStableRef<Any>()?.get() as T?

    fun set(value: T?) {
        val previous = pthread_getspecific(key)
        val replacement = value?.let { StableRef.create(it as Any) }
        if (pthread_setspecific(key, replacement?.asCPointer()) != 0) {
            replacement?.dispose()
            error("pthread_setspecific failed")
        }
        previous?.asStableRef<Any>()?.dispose()
    }

    fun remove() = set(null)
}

internal class Stack<T> {
    private val list = mutableListOf<T>()
    fun push(item: T) { list.add(item) }
    fun pop(): T = if (list.isEmpty()) throw NoSuchElementException() else list.removeAt(list.size - 1)
    fun peek(): T = if (list.isEmpty()) throw NoSuchElementException() else list[list.size - 1]
    fun isEmpty(): Boolean = list.isEmpty()
}

private class SimpleCounter(name: String) : PerformanceCounter(name) {
    override fun <T> countTime(block: () -> T): T {
        val startTime = PerformanceCounter.currentTime()
        try {
            return block()
        } finally {
            incrementTime(PerformanceCounter.currentTime() - startTime)
        }
    }
}

private class ReenterableCounter(name: String) : PerformanceCounter(name) {
    companion object {
        private val enteredCounters = ThreadLocal<MutableSet<ReenterableCounter>>()

        private fun enterCounter(counter: ReenterableCounter) = PerformanceCounter.getOrPut(enteredCounters) { HashSet() }.add(counter)

        private fun leaveCounter(counter: ReenterableCounter) {
            enteredCounters.get()?.remove(counter)
        }
    }

    override fun <T> countTime(block: () -> T): T {
        val startTime = PerformanceCounter.currentTime()
        val needTime = enterCounter(this)
        try {
            return block()
        } finally {
            if (needTime) {
                incrementTime(PerformanceCounter.currentTime() - startTime)
                leaveCounter(this)
            }
        }
    }
}

internal class CounterWithExclude(name: String, vararg excludedCounters: PerformanceCounter) : PerformanceCounter(name) {
    companion object {
        private val counterToCallStackMapThreadLocal = ThreadLocal<MutableMap<CounterWithExclude, CallStackWithTime>>()

        private fun getCallStack(counter: CounterWithExclude) =
            PerformanceCounter.getOrPut(counterToCallStackMapThreadLocal) { HashMap() }.getOrPut(counter) { CallStackWithTime() }
    }

    init {
        excludedCounters.forEach { it.excludedFrom.add(this) }
    }

    private val callStack: CallStackWithTime
        get() = getCallStack(this)

    override fun <T> countTime(block: () -> T): T {
        incrementTime(callStack.push(true))
        try {
            return block()
        } finally {
            incrementTime(callStack.pop(true))
        }
    }

    fun enterExcludedMethod() {
        incrementTime(callStack.push(false))
    }

    fun exitExcludedMethod() {
        incrementTime(callStack.pop(false))
    }

    private class CallStackWithTime {
        private val callStack = Stack<Boolean>()
        private var intervalStartTime: Long = 0

        fun Stack<Boolean>.peekOrFalse() = if (isEmpty()) false else peek()

        private fun intervalUsefulTime(callStackUpdate: Stack<Boolean>.() -> Unit): Long {
            val delta = if (callStack.peekOrFalse()) PerformanceCounter.currentTime() - intervalStartTime else 0
            callStack.callStackUpdate()

            intervalStartTime = PerformanceCounter.currentTime()
            return delta
        }

        fun push(usefulCall: Boolean): Long {
            if (!isEnteredCounter() && !usefulCall) return 0

            return intervalUsefulTime { push(usefulCall) }
        }

        fun pop(usefulCall: Boolean): Long {
            if (!isEnteredCounter()) return 0

            assert(callStack.peek() == usefulCall)
            return intervalUsefulTime { pop() }
        }

        fun isEnteredCounter(): Boolean = !callStack.isEmpty()
    }
}
