/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.utils

class BitSet(initialBits: Int = 64) {
    private var words: LongArray = LongArray(((initialBits.coerceAtLeast(0) + 63) ushr 6).coerceAtLeast(1))
    private var wordsInUse: Int = 0

    private fun ensureCapacity(wordIndex: Int) {
        if (wordIndex >= words.size) {
            val newSize = maxOf(words.size * 2, wordIndex + 1)
            words = words.copyOf(newSize)
        }
    }

    private fun recalculateWordsInUse() {
        var i = wordsInUse - 1
        while (i >= 0 && words[i] == 0L) {
            i--
        }
        wordsInUse = i + 1
    }

    operator fun get(bitIndex: Int): Boolean {
        require(bitIndex >= 0) { "bitIndex < 0: $bitIndex" }
        val wordIndex = bitIndex ushr 6
        return wordIndex < wordsInUse && (words[wordIndex] and (1L shl (bitIndex and 63))) != 0L
    }

    fun set(bitIndex: Int) {
        require(bitIndex >= 0) { "bitIndex < 0: $bitIndex" }
        val wordIndex = bitIndex ushr 6
        ensureCapacity(wordIndex)
        if (wordIndex >= wordsInUse) {
            wordsInUse = wordIndex + 1
        }
        words[wordIndex] = words[wordIndex] or (1L shl (bitIndex and 63))
    }

    fun set(bitIndex: Int, value: Boolean) {
        if (value) set(bitIndex) else clear(bitIndex)
    }

    fun clear(bitIndex: Int) {
        require(bitIndex >= 0) { "bitIndex < 0: $bitIndex" }
        val wordIndex = bitIndex ushr 6
        if (wordIndex >= wordsInUse) return
        words[wordIndex] = words[wordIndex] and (1L shl (bitIndex and 63)).inv()
        recalculateWordsInUse()
    }

    fun clear() {
        for (i in 0 until wordsInUse) {
            words[i] = 0L
        }
        wordsInUse = 0
    }

    fun or(other: BitSet) {
        if (this === other) return
        val wordsInCommon = minOf(wordsInUse, other.wordsInUse)
        if (wordsInUse < other.wordsInUse) {
            ensureCapacity(other.wordsInUse - 1)
            wordsInUse = other.wordsInUse
        }
        for (i in 0 until wordsInCommon) {
            words[i] = words[i] or other.words[i]
        }
        if (wordsInCommon < other.wordsInUse) {
            for (i in wordsInCommon until other.wordsInUse) {
                words[i] = other.words[i]
            }
        }
        recalculateWordsInUse()
    }

    fun andNot(other: BitSet) {
        for (i in minOf(wordsInUse, other.wordsInUse) - 1 downTo 0) {
            words[i] = words[i] and other.words[i].inv()
        }
        recalculateWordsInUse()
    }

    fun nextSetBit(fromIndex: Int): Int {
        require(fromIndex >= 0) { "fromIndex < 0: $fromIndex" }
        var u = fromIndex ushr 6
        if (u >= wordsInUse) return -1
        var word = words[u] and (-1L shl (fromIndex and 63))
        while (true) {
            if (word != 0L) {
                return (u * 64) + word.countTrailingZeroBits()
            }
            u++
            if (u == wordsInUse) return -1
            word = words[u]
        }
    }

    fun copyFrom(other: BitSet) {
        words = other.words.copyOf(other.wordsInUse.coerceAtLeast(1))
        wordsInUse = other.wordsInUse
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BitSet) return false
        if (wordsInUse != other.wordsInUse) return false
        for (i in 0 until wordsInUse) {
            if (words[i] != other.words[i]) return false
        }
        return true
    }

    override fun hashCode(): Int {
        var h = 1234L
        for (i in wordsInUse - 1 downTo 0) {
            h = h xor (words[i] * (i + 1))
        }
        return ((h shr 32) xor h).toInt()
    }
}

fun BitSet.copy(): BitSet = BitSet().apply { copyFrom(this@copy) }

inline fun BitSet.forEachBit(block: (Int) -> Unit) {
    var i = this.nextSetBit(0)
    while (i >= 0) {
        block(i)
        i = this.nextSetBit(i + 1)
    }
}
