// Copyright 2000-2025 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.platform.syntax.extensions.impl

import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.usleep
import kotlin.concurrent.AtomicReference
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
internal val syntaxCurrentThreadToken: Any = Any()

@OptIn(ExperimentalForeignApi::class)
internal class PosixRecursiveLock {
    private val owner = AtomicReference<Any?>(null)
    private var holdCount = 0

    fun lock() {
        val me = syntaxCurrentThreadToken
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

    fun unlock() {
        val me = syntaxCurrentThreadToken
        check(owner.value === me) { "PosixRecursiveLock.unlock called by non-owner thread" }
        val next = holdCount - 1
        if (next == 0) {
            holdCount = 0
            owner.value = null
        } else {
            holdCount = next
        }
    }

    inline fun <R> withLock(block: () -> R): R {
        lock()
        try {
            return block()
        } finally {
            unlock()
        }
    }
}

/**
 * Real thread-safe ConcurrentMap implementation for Kotlin/Native backed by an atomic recursive lock.
 */
internal class NativeConcurrentMap<K : Any, V : Any> : AbstractMutableMap<K, V>(), ConcurrentMap<K, V> {
    private val lock = PosixRecursiveLock()
    private val delegate = LinkedHashMap<K, V>()
    private var computing: HashSet<K>? = null

    override val size: Int
        get() = lock.withLock { delegate.size }

    override fun isEmpty(): Boolean =
        lock.withLock { delegate.isEmpty() }

    override fun containsKey(key: K): Boolean =
        lock.withLock { delegate.containsKey(key) }

    override fun containsValue(value: V): Boolean =
        lock.withLock { delegate.containsValue(value) }

    override fun get(key: K): V? =
        lock.withLock { delegate[key] }

    override fun put(key: K, value: V): V? =
        lock.withLock { delegate.put(key, value) }

    override fun remove(key: K): V? =
        lock.withLock { delegate.remove(key) }

    override fun putAll(from: Map<out K, V>): Unit =
        lock.withLock { delegate.putAll(from) }

    override fun clear(): Unit =
        lock.withLock { delegate.clear() }

    private fun snapshotIterator(): MutableIterator<K> {
        val snapshot = lock.withLock { delegate.keys.toList() }.iterator()
        var last: K? = null
        var canRemove = false
        return object : MutableIterator<K> {
            override fun hasNext(): Boolean = snapshot.hasNext()
            override fun next(): K = snapshot.next().also { last = it; canRemove = true }
            override fun remove() {
                check(canRemove) { "next() must precede remove()" }
                this@NativeConcurrentMap.remove(last!!)
                canRemove = false
            }
        }
    }

    override val keys: MutableSet<K> = object : AbstractMutableSet<K>() {
        override val size: Int get() = this@NativeConcurrentMap.size
        override fun contains(element: K): Boolean = containsKey(element)
        override fun add(element: K): Boolean = throw UnsupportedOperationException()
        override fun remove(element: K): Boolean = this@NativeConcurrentMap.remove(element) != null
        override fun clear() = this@NativeConcurrentMap.clear()
        override fun iterator(): MutableIterator<K> = snapshotIterator()
    }

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>> =
        object : AbstractMutableSet<MutableMap.MutableEntry<K, V>>() {
            override val size: Int get() = this@NativeConcurrentMap.size
            override fun clear() = this@NativeConcurrentMap.clear()
            override fun add(element: MutableMap.MutableEntry<K, V>): Boolean =
                put(element.key, element.value) == null
            override fun contains(element: MutableMap.MutableEntry<K, V>): Boolean = get(element.key) == element.value
            override fun remove(element: MutableMap.MutableEntry<K, V>): Boolean = lock.withLock {
                if (delegate[element.key] != element.value) false
                else { delegate.remove(element.key); true }
            }
            override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> {
                val snapshot = lock.withLock { delegate.toList() }.iterator()
                var last: K? = null
                var canRemove = false
                return object : MutableIterator<MutableMap.MutableEntry<K, V>> {
                    override fun hasNext(): Boolean = snapshot.hasNext()
                    override fun next(): MutableMap.MutableEntry<K, V> {
                        val (key, initial) = snapshot.next()
                        last = key
                        canRemove = true
                        return object : MutableMap.MutableEntry<K, V> {
                            private var currentValue = initial
                            override val key: K = key
                            override val value: V get() = currentValue
                            override fun setValue(newValue: V): V {
                                val old = value
                                put(key, newValue)
                                currentValue = newValue
                                return old
                            }
                            override fun equals(other: Any?): Boolean =
                                other is Map.Entry<*, *> && key == other.key && value == other.value
                            override fun hashCode(): Int = key.hashCode() xor value.hashCode()
                        }
                    }
                    override fun remove() {
                        check(canRemove)
                        this@NativeConcurrentMap.remove(last!!)
                        canRemove = false
                    }
                }
            }
        }

    override val values: MutableCollection<V> = object : AbstractMutableCollection<V>() {
        override val size: Int get() = this@NativeConcurrentMap.size
        override fun add(element: V): Boolean = throw UnsupportedOperationException()
        override fun contains(element: V): Boolean = containsValue(element)
        override fun clear() = this@NativeConcurrentMap.clear()
        override fun iterator(): MutableIterator<V> {
            val iterator = entries.iterator()
            return object : MutableIterator<V> {
                override fun hasNext(): Boolean = iterator.hasNext()
                override fun next(): V = iterator.next().value
                override fun remove() = iterator.remove()
            }
        }
    }

    override fun computeIfAbsent(key: K, f: (K) -> V): V =
        lock.withLock {
            delegate[key] ?: run {
                val active = computing ?: HashSet<K>(4).also { computing = it }
                check(active.add(key)) { "Recursive update" }
                try { f(key).also { delegate[key] = it } }
                finally { active.remove(key) }
            }
        }
}

internal fun <K : Any, V : Any> newConcurrentMapNative(): ConcurrentMap<K, V> = NativeConcurrentMap()
