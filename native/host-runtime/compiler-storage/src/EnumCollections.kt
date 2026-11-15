package org.jetbrains.kotlin.utils.addToStdlib

class EnumMap<K : Enum<K>, V>(map: Map<K, V>) : AbstractMutableMap<K, V>() {
    private val delegate = LinkedHashMap<K, V>()

    init {
        require(map.isNotEmpty()) { "Specified map is empty" }
        putAll(map)
    }

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>> get() = delegate.entries
    override fun put(key: K, value: V): V? {
        val present = delegate.containsKey(key)
        val previous = delegate.put(key, value)
        if (!present) {
            val ordered = delegate.entries.sortedBy { it.key.ordinal }.map { it.key to it.value }
            delegate.clear()
            ordered.forEach { delegate[it.first] = it.second }
        }
        return previous
    }
}

class EnumSet<E : Enum<E>> private constructor() : AbstractMutableSet<E>() {
    private val delegate = LinkedHashSet<E>()
    override val size: Int get() = delegate.size
    override fun iterator(): MutableIterator<E> = delegate.iterator()
    override fun add(element: E): Boolean {
        if (!delegate.add(element)) return false
        val ordered = delegate.sortedBy { it.ordinal }
        delegate.clear()
        delegate.addAll(ordered)
        return true
    }

    companion object {
        fun <E : Enum<E>> of(element: E, vararg elements: E): EnumSet<E> =
            EnumSet<E>().apply { add(element); addAll(elements) }
    }
}
