/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("PackageDirectoryMismatch")

package com.intellij.util.containers

import org.jetbrains.kotlin.utils.SmartList

open class MultiMap<K, V>(
    private val map: MutableMap<K, MutableCollection<V>> = LinkedHashMap()
) {
    protected open fun createCollection(): MutableCollection<V> = SmartList()

    fun putValue(key: K, value: V) {
        getModifiable(key).add(value)
    }

    fun putValues(key: K, values: Collection<V>) {
        if (values.isNotEmpty()) {
            getModifiable(key).addAll(values)
        }
    }

    operator fun get(key: K): Collection<V> = map[key] ?: emptyList()

    fun getModifiable(key: K): MutableCollection<V> = map.getOrPut(key) { createCollection() }

    fun remove(key: K): Collection<V>? = map.remove(key)

    fun remove(key: K, value: V): Boolean {
        val values = map[key] ?: return false
        val removed = values.remove(value)
        if (values.isEmpty()) {
            map.remove(key)
        }
        return removed
    }

    fun clear() {
        map.clear()
    }

    fun isEmpty(): Boolean = map.values.all { it.isEmpty() }

    val size: Int
        get() = map.size

    fun keySet(): Set<K> = map.keys

    fun values(): Collection<V> = map.values.flatten()

    fun entrySet(): Set<Map.Entry<K, Collection<V>>> = map.entries

    fun containsKey(key: K): Boolean = map.containsKey(key)

    companion object {
        fun <K, V> create(): MultiMap<K, V> = MultiMap()
        fun <K, V> createLinked(): MultiMap<K, V> = MultiMap(LinkedHashMap())
        fun <K, V> createSet(): MultiMap<K, V> = object : MultiMap<K, V>() {
            override fun createCollection(): MutableCollection<V> = LinkedHashSet()
        }
        fun <K, V> empty(): MultiMap<K, V> = MultiMap()
    }
}
