/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin.util

private fun immutableMutation(): Nothing = throw UnsupportedOperationException()

open class NativeUnmodifiableCollection<E>(protected val backingCollection: Collection<E>) : AbstractMutableCollection<E>() {
    override val size: Int get() = backingCollection.size
    override fun contains(element: E): Boolean = backingCollection.contains(element)
    override fun containsAll(elements: Collection<E>): Boolean = backingCollection.containsAll(elements)
    override fun iterator(): MutableIterator<E> {
        val source = backingCollection.iterator()
        return object : MutableIterator<E> {
            override fun hasNext(): Boolean = source.hasNext()
            override fun next(): E = source.next()
            override fun remove(): Unit = immutableMutation()
        }
    }
    final override fun add(element: E): Boolean = immutableMutation()
    final override fun addAll(elements: Collection<E>): Boolean = immutableMutation()
    final override fun remove(element: E): Boolean = immutableMutation()
    final override fun removeAll(elements: Collection<E>): Boolean = immutableMutation()
    final override fun retainAll(elements: Collection<E>): Boolean = immutableMutation()
    final override fun clear(): Unit = immutableMutation()
}

open class NativeUnmodifiableSet<E>(private val source: Set<E>) : NativeUnmodifiableCollection<E>(source), MutableSet<E> {
    override fun equals(other: Any?): Boolean = other === this || source == other
    override fun hashCode(): Int = source.hashCode()
}

open class NativeUnmodifiableList<E>(private val source: List<E>) : AbstractMutableList<E>() {
    override val size: Int get() = source.size
    override fun get(index: Int): E = source[index]
    override fun set(index: Int, element: E): E = immutableMutation()
    override fun add(index: Int, element: E): Unit = immutableMutation()
    override fun addAll(elements: Collection<E>): Boolean = immutableMutation()
    override fun addAll(index: Int, elements: Collection<E>): Boolean = immutableMutation()
    override fun removeAt(index: Int): E = immutableMutation()
    override fun remove(element: E): Boolean = immutableMutation()
    override fun removeAll(elements: Collection<E>): Boolean = immutableMutation()
    override fun retainAll(elements: Collection<E>): Boolean = immutableMutation()
    override fun clear(): Unit = immutableMutation()
    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> =
        NativeCollections.unmodifiableList(source.subList(fromIndex, toIndex))
}

private class NativeUnmodifiableRandomAccessList<E>(source: List<E>) : NativeUnmodifiableList<E>(source), RandomAccess

private class NativeUnmodifiableEntry<K, V>(private val source: Map.Entry<K, V>) : MutableMap.MutableEntry<K, V> {
    override val key: K get() = source.key
    override val value: V get() = source.value
    override fun setValue(newValue: V): V = immutableMutation()
    override fun equals(other: Any?): Boolean = other is Map.Entry<*, *> && key == other.key && value == other.value
    override fun hashCode(): Int = (key?.hashCode() ?: 0) xor (value?.hashCode() ?: 0)
    override fun toString(): String = "$key=$value"
}

private class NativeUnmodifiableEntries<K, V>(private val source: Set<Map.Entry<K, V>>) :
    AbstractMutableSet<MutableMap.MutableEntry<K, V>>() {
    override val size: Int get() = source.size
    override fun contains(element: MutableMap.MutableEntry<K, V>): Boolean = source.contains(element)
    override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> {
        val iterator = source.iterator()
        return object : MutableIterator<MutableMap.MutableEntry<K, V>> {
            override fun hasNext(): Boolean = iterator.hasNext()
            override fun next(): MutableMap.MutableEntry<K, V> = NativeUnmodifiableEntry(iterator.next())
            override fun remove(): Unit = immutableMutation()
        }
    }
    override fun add(element: MutableMap.MutableEntry<K, V>): Boolean = immutableMutation()
    override fun addAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean = immutableMutation()
    override fun remove(element: MutableMap.MutableEntry<K, V>): Boolean = immutableMutation()
    override fun removeAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean = immutableMutation()
    override fun retainAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean = immutableMutation()
    override fun clear(): Unit = immutableMutation()
}

open class NativeUnmodifiableMap<K, V>(private val source: Map<K, V>) : AbstractMutableMap<K, V>() {
    override val size: Int get() = source.size
    override fun get(key: K): V? = source[key]
    override fun containsKey(key: K): Boolean = source.containsKey(key)
    override fun containsValue(value: V): Boolean = source.containsValue(value)
    private val entryView by lazy { NativeUnmodifiableEntries(source.entries) }
    private val keyView by lazy { NativeUnmodifiableSet(source.keys) }
    private val valueView by lazy { NativeUnmodifiableCollection(source.values) }
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>> get() = entryView
    override val keys: MutableSet<K> get() = keyView
    override val values: MutableCollection<V> get() = valueView
    final override fun put(key: K, value: V): V? = immutableMutation()
    final override fun putAll(from: Map<out K, V>): Unit = immutableMutation()
    final override fun remove(key: K): V? = immutableMutation()
    final override fun clear(): Unit = immutableMutation()
}

/** Exact selected immutable-table boundary, not a com.google.common class. */
class NativeImmutableBiMap<K : Any, V : Any> private constructor(source: Map<K, V>) : NativeUnmodifiableMap<K, V>(source) {
    private var inverseMap: NativeImmutableBiMap<V, K>? = null
    fun inverse(): NativeImmutableBiMap<V, K> = inverseMap!!
    override val values: MutableSet<V> get() = inverse().keys

    class Builder<K : Any, V : Any> {
        private val entries = ArrayList<Pair<K, V>>()
        fun put(key: K?, value: V?): Builder<K, V> {
            if (key == null || value == null) throw NullPointerException()
            entries.add(key to value)
            return this
        }
        fun putAll(values: Map<out K?, V?>): Builder<K, V> {
            for ((key, value) in values) put(key, value)
            return this
        }
        fun build(): NativeImmutableBiMap<K, V> {
            if (entries.isEmpty()) return empty()
            val forward = LinkedHashMap<K, V>()
            val backward = LinkedHashMap<V, K>()
            for ((key, value) in entries) {
                require(!forward.containsKey(key)) { "Multiple entries with same key: $key" }
                require(!backward.containsKey(value)) { "Multiple entries with same value: $value" }
                forward[key] = value
                backward[value] = key
            }
            val result = NativeImmutableBiMap(forward)
            val inverse = NativeImmutableBiMap(backward)
            result.inverseMap = inverse
            inverse.inverseMap = result
            return result
        }
    }

    companion object {
        private val EMPTY: NativeImmutableBiMap<Any, Any> =
            NativeImmutableBiMap<Any, Any>(emptyMap()).also { it.inverseMap = it }
        @Suppress("UNCHECKED_CAST")
        private fun <K : Any, V : Any> empty(): NativeImmutableBiMap<K, V> = EMPTY as NativeImmutableBiMap<K, V>
        fun <K : Any, V : Any> builder(): Builder<K, V> = Builder()
        @Suppress("UNCHECKED_CAST")
        fun <K : Any, V : Any> copyOf(source: Map<out K?, V?>): NativeImmutableBiMap<K, V> =
            if (source is NativeImmutableBiMap<*, *>) source as NativeImmutableBiMap<K, V>
            else builder<K, V>().putAll(source).build()
    }
}

class NativeImmutableSet<E : Any> private constructor(source: Set<E>) : NativeUnmodifiableSet<E>(source) {
    companion object {
        private val EMPTY = NativeImmutableSet<Any>(emptySet())
        fun <E : Any> of(vararg elements: E?): NativeImmutableSet<E> = copyOf(elements.asIterable())
        @Suppress("UNCHECKED_CAST")
        fun <E : Any> copyOf(elements: Iterable<E?>): NativeImmutableSet<E> {
            if (elements is NativeImmutableSet<*>) return elements as NativeImmutableSet<E>
            val result = LinkedHashSet<E>()
            for (element in elements) {
                if (element == null) throw NullPointerException()
                result.add(element)
            }
            if (result.isEmpty()) return EMPTY as NativeImmutableSet<E>
            return NativeImmutableSet(result)
        }
    }
}

object NativeCollections {
    @Suppress("UNCHECKED_CAST")
    fun <K, V> unmodifiableMap(map: Map<K, V>): MutableMap<K, V> =
        if (map::class == NativeUnmodifiableMap::class) map as MutableMap<K, V> else NativeUnmodifiableMap(map)
    @Suppress("UNCHECKED_CAST")
    fun <E> unmodifiableSet(set: Set<E>): MutableSet<E> =
        if (set::class == NativeUnmodifiableSet::class) set as MutableSet<E> else NativeUnmodifiableSet(set)
    @Suppress("UNCHECKED_CAST")
    fun <E> unmodifiableList(list: List<E>): MutableList<E> = when {
        list is NativeUnmodifiableList<*> -> list as MutableList<E>
        list is RandomAccess -> NativeUnmodifiableRandomAccessList(list)
        else -> NativeUnmodifiableList(list)
    }
    fun <E> reverse(list: MutableList<E>) {
        for (index in 0 until list.size / 2) {
            val other = list.lastIndex - index
            val previous = list[index]
            list[index] = list[other]
            list[other] = previous
        }
    }
}

object NativeArrays {
    fun <E> asList(array: Array<E>): MutableList<E> = object : AbstractMutableList<E>() {
        override val size: Int get() = array.size
        override fun get(index: Int): E = array[index]
        override fun set(index: Int, element: E): E = array[index].also { array[index] = element }
        override fun add(index: Int, element: E): Unit = immutableMutation()
        override fun removeAt(index: Int): E = immutableMutation()
    }
    fun toString(array: Array<out Any?>?): String = array?.joinToString(", ", "[", "]") ?: "null"
}
