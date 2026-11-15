package org.jetbrains.kotlin.ir.util

import org.jetbrains.kotlin.ir.IrLock
import org.jetbrains.kotlin.ir.synchronized

// The private TypeTranslator stack previously inherited Vector's per-operation monitor.
class NativeIrStack<T> {
    private val lock = IrLock()
    private val elements = ArrayDeque<T>()

    fun push(value: T): T = synchronized(lock) {
        elements.addLast(value)
        value
    }

    fun pop(): T = synchronized(lock) { elements.removeLast() }

    fun isNotEmpty(): Boolean = synchronized(lock) { elements.isNotEmpty() }

    operator fun contains(value: Any?): Boolean = synchronized(lock) {
        elements.any { value == it }
    }
}
