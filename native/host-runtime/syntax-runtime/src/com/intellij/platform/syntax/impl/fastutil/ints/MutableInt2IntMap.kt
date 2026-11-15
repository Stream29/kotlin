package com.intellij.platform.syntax.impl.fastutil.ints


interface MutableInt2IntMap: Int2IntMap {
    fun put(key: Int, value: Int): Int
    fun remove(key: Int): Int

    operator fun set(key: Int, value: Int): Int = put(key, value)
}