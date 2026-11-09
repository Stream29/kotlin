package org.jetbrains.kotlin.ir.util

@Suppress("UNCHECKED_CAST")
internal fun <K, V> Map<K, V>.getOrDefault(key: K, defaultValue: V): V {
    val value = this[key]
    return if (value != null || containsKey(key)) value as V else defaultValue
}
