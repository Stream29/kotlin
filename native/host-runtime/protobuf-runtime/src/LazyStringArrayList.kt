/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Copyright 2008 Google Inc.
 * Use of this source code is governed by the Apache 2.0 and Protobuf BSD licenses.
 */

package org.jetbrains.kotlin.protobuf

class LazyStringArrayList(private val list: MutableList<Any> = ArrayList()) :
    AbstractMutableList<String>(), LazyStringList {

    constructor(from: LazyStringList) : this(ArrayList<Any>(from.size)) {
        for (i in 0 until from.size) {
            list.add(from.getUnderlying(i))
        }
    }

    constructor(from: List<String>) : this(ArrayList<Any>(from))

    override val size: Int get() = list.size

    override fun get(index: Int): String {
        val item = list[index]
        if (item is String) return item
        val bs = item as ByteString
        val s = bs.toStringUtf8()
        if (bs.isValidUtf8()) {
            list[index] = s
        }
        return s
    }

    override fun getUnderlying(index: Int): Any = list[index]

    override fun getByteString(index: Int): ByteString {
        val item = list[index]
        if (item is ByteString) return item
        val s = item as String
        val bs = ByteString.copyFromUtf8(s)
        list[index] = bs
        return bs
    }

    override fun add(index: Int, element: String) {
        list.add(index, element)
    }

    override fun add(element: ByteString) {
        list.add(element)
    }

    override fun removeAt(index: Int): String {
        val s = get(index)
        list.removeAt(index)
        return s
    }

    override fun set(index: Int, element: String): String {
        val prev = get(index)
        list[index] = element
        return prev
    }

    override fun asByteStringList(): List<ByteString> =
        (0 until size).map { getByteString(it) }

    override fun getUnmodifiableView(): LazyStringList = UnmodifiableLazyStringList(this)

    companion object {
        val EMPTY: LazyStringList = UnmodifiableLazyStringList(LazyStringArrayList())
    }

    private class UnmodifiableLazyStringList(private val underlying: LazyStringList) :
        AbstractList<String>(), LazyStringList {
        override val size: Int get() = underlying.size
        override fun get(index: Int): String = underlying[index]
        override fun getUnderlying(index: Int): Any = underlying.getUnderlying(index)
        override fun getByteString(index: Int): ByteString = underlying.getByteString(index)
        override fun asByteStringList(): List<ByteString> = underlying.asByteStringList()
        override fun add(element: ByteString) {
            throw UnsupportedOperationException("List is unmodifiable")
        }
        override fun getUnmodifiableView(): LazyStringList = this
    }
}
