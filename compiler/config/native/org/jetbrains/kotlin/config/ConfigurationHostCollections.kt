/*
 * Copyright 2026 NativeKotlin contributors.
 * Licensed under the Apache License, Version 2.0.
 */

package org.jetbrains.kotlin.config

// Key equality deliberately remains Any's identity equality, irrespective of name.
internal class ConfigurationIdentityKey<T>(private val name: String) {
    override fun toString(): String = name
}

private fun immutable(): Nothing = throw UnsupportedOperationException()

private fun <T> immutableIterator(source: Iterator<T>): MutableIterator<T> =
    object : MutableIterator<T> {
        override fun hasNext(): Boolean = source.hasNext()
        override fun next(): T = source.next()
        override fun remove(): Unit = immutable()
    }

internal class ConfigurationCollectionView<T>(private val source: Collection<T>) : AbstractMutableCollection<T>() {
    override val size: Int get() = source.size
    override fun isEmpty(): Boolean = source.isEmpty()
    override fun contains(element: T): Boolean = source.contains(element)
    override fun containsAll(elements: Collection<T>): Boolean = source.containsAll(elements)
    override fun toString(): String = source.toString()
    override fun iterator(): MutableIterator<T> = immutableIterator(source.iterator())
    override fun add(element: T): Boolean = immutable()
    override fun addAll(elements: Collection<T>): Boolean = immutable()
    override fun remove(element: T): Boolean = immutable()
    override fun removeAll(elements: Collection<T>): Boolean = immutable()
    override fun retainAll(elements: Collection<T>): Boolean = immutable()
    override fun clear(): Unit = immutable()
}

internal class ConfigurationSetView<T>(private val source: Set<T>) : AbstractMutableSet<T>() {
    override val size: Int get() = source.size
    override fun isEmpty(): Boolean = source.isEmpty()
    override fun contains(element: T): Boolean = source.contains(element)
    override fun containsAll(elements: Collection<T>): Boolean = source.containsAll(elements)
    override fun equals(other: Any?): Boolean = this === other || source == other
    override fun hashCode(): Int = source.hashCode()
    override fun toString(): String = source.toString()
    override fun iterator(): MutableIterator<T> = immutableIterator(source.iterator())
    override fun add(element: T): Boolean = immutable()
    override fun addAll(elements: Collection<T>): Boolean = immutable()
    override fun remove(element: T): Boolean = immutable()
    override fun removeAll(elements: Collection<T>): Boolean = immutable()
    override fun retainAll(elements: Collection<T>): Boolean = immutable()
    override fun clear(): Unit = immutable()
}

internal class ConfigurationListView<T>(private val source: List<T>) : AbstractMutableList<T>() {
    override val size: Int get() = source.size
    override fun isEmpty(): Boolean = source.isEmpty()
    override fun contains(element: T): Boolean = source.contains(element)
    override fun containsAll(elements: Collection<T>): Boolean = source.containsAll(elements)
    override fun indexOf(element: T): Int = source.indexOf(element)
    override fun lastIndexOf(element: T): Int = source.lastIndexOf(element)
    override fun equals(other: Any?): Boolean = this === other || source == other
    override fun hashCode(): Int = source.hashCode()
    override fun toString(): String = source.toString()
    override fun get(index: Int): T = source[index]
    override fun add(element: T): Boolean = immutable()
    override fun add(index: Int, element: T): Unit = immutable()
    override fun addAll(elements: Collection<T>): Boolean = immutable()
    override fun addAll(index: Int, elements: Collection<T>): Boolean = immutable()
    override fun set(index: Int, element: T): T = immutable()
    override fun remove(element: T): Boolean = immutable()
    override fun removeAt(index: Int): T = immutable()
    override fun removeAll(elements: Collection<T>): Boolean = immutable()
    override fun retainAll(elements: Collection<T>): Boolean = immutable()
    override fun clear(): Unit = immutable()
    override fun iterator(): MutableIterator<T> = immutableIterator(source.iterator())
    override fun listIterator(): MutableListIterator<T> = listIterator(0)
    override fun listIterator(index: Int): MutableListIterator<T> {
        val iterator = source.listIterator(index)
        return object : MutableListIterator<T> {
            override fun hasNext(): Boolean = iterator.hasNext()
            override fun hasPrevious(): Boolean = iterator.hasPrevious()
            override fun next(): T = iterator.next()
            override fun previous(): T = iterator.previous()
            override fun nextIndex(): Int = iterator.nextIndex()
            override fun previousIndex(): Int = iterator.previousIndex()
            override fun add(element: T): Unit = immutable()
            override fun set(element: T): Unit = immutable()
            override fun remove(): Unit = immutable()
        }
    }
    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> =
        ConfigurationListView(source.subList(fromIndex, toIndex))
}

internal class ConfigurationMapView<K, V>(private val source: Map<K, V>) : AbstractMutableMap<K, V>() {
    override val size: Int get() = source.size
    override fun isEmpty(): Boolean = source.isEmpty()
    override fun equals(other: Any?): Boolean = this === other || source == other
    override fun hashCode(): Int = source.hashCode()
    override fun toString(): String = source.toString()
    override fun get(key: K): V? = source[key]
    override fun containsKey(key: K): Boolean = source.containsKey(key)
    override fun containsValue(value: V): Boolean = source.containsValue(value)
    override fun put(key: K, value: V): V? = immutable()
    override fun putAll(from: Map<out K, V>): Unit = immutable()
    override fun remove(key: K): V? = immutable()
    override fun clear(): Unit = immutable()
    override val keys: MutableSet<K> get() = ConfigurationSetView(source.keys)
    override val values: MutableCollection<V> get() = ConfigurationCollectionView(source.values)
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = ConfigurationSetView(object : AbstractSet<MutableMap.MutableEntry<K, V>>() {
            override val size: Int get() = source.size
            override fun iterator(): Iterator<MutableMap.MutableEntry<K, V>> {
                val iterator = source.entries.iterator()
                return object : Iterator<MutableMap.MutableEntry<K, V>> {
                    override fun hasNext(): Boolean = iterator.hasNext()
                    override fun next(): MutableMap.MutableEntry<K, V> {
                        val entry = iterator.next()
                        return object : MutableMap.MutableEntry<K, V> {
                            override val key: K get() = entry.key
                            override val value: V get() = entry.value
                            override fun setValue(newValue: V): V = immutable()
                            override fun equals(other: Any?): Boolean =
                                other is Map.Entry<*, *> && key == other.key && value == other.value
                            override fun hashCode(): Int = (key?.hashCode() ?: 0) xor (value?.hashCode() ?: 0)
                            override fun toString(): String = "$key=$value"
                        }
                    }
                }
            }
        })
}

// EnumSet iterates in declaration order and permits mutation, unlike setOf.
internal class ConfigurationEnumSet<E : Enum<E>>(
    private val universe: List<E>,
    initial: Set<E>,
) : AbstractMutableSet<E>() {
    private val members = initial.toMutableSet()
    override val size: Int get() = members.size
    override fun contains(element: E): Boolean = element in members
    override fun add(element: E): Boolean = members.add(element)
    override fun remove(element: E): Boolean = members.remove(element)
    override fun clear() = members.clear()
    override fun iterator(): MutableIterator<E> {
        val snapshot = universe.filter { it in members }.iterator()
        var last: E? = null
        return object : MutableIterator<E> {
            override fun hasNext(): Boolean = snapshot.hasNext()
            override fun next(): E = snapshot.next().also { last = it }
            override fun remove() {
                val element = last ?: throw IllegalStateException()
                members.remove(element)
                last = null
            }
        }
    }
}
