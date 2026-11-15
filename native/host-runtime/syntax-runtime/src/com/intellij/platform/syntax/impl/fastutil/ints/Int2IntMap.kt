package com.intellij.platform.syntax.impl.fastutil.ints

import fleet.com.intellij.multiplatform.util.fastutil.ints.IntIterator

interface Int2IntMap {
    val size: Int
    val keys: IntIterator
    val values: IntIterator
    val entries: Iterator<Int2IntEntry>

    operator fun get(key: Int): Int
}