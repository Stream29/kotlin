/*
 * Native host boundary for original per-instance Java ThreadLocal cells in FIR.
 * Licensed under Apache 2.0; see porting/F1/license/LICENSE.txt.
 */

@file:OptIn(kotlin.experimental.ExperimentalNativeApi::class)

package org.jetbrains.kotlin.fir.platform

import kotlin.native.concurrent.ThreadLocal
import kotlin.native.ref.WeakReference

private class Entry(val owner: WeakReference<FirThreadLocal<*>>, var value: Any?)

@ThreadLocal
private object ThreadCells {
    val entries: MutableList<Entry> = mutableListOf()

    fun find(owner: FirThreadLocal<*>): Entry? {
        var found: Entry? = null
        val iterator = entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            val key = entry.owner.get()
            if (key == null) iterator.remove()
            else if (key === owner) found = entry
        }
        return found
    }
}

class FirThreadLocal<T> private constructor(private val initializer: () -> T) {
    companion object {
        fun <T> withInitial(initializer: () -> T): FirThreadLocal<T> = FirThreadLocal(initializer)
    }

    @Suppress("UNCHECKED_CAST")
    fun get(): T {
        val entry = ThreadCells.find(this)
        if (entry != null) return entry.value as T
        return initializer().also { set(it) }
    }

    fun set(value: T) {
        val entry = ThreadCells.find(this)
        if (entry == null) ThreadCells.entries.add(Entry(WeakReference<FirThreadLocal<*>>(this), value))
        else entry.value = value
    }

    fun remove() {
        ThreadCells.find(this)?.let { ThreadCells.entries.remove(it) }
    }
}
