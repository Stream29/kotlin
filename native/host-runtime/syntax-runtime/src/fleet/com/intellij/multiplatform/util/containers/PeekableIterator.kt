// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package fleet.com.intellij.multiplatform.util.containers


/**
 * An iterator with additional ability to [.peek] the current element without moving the cursor.
 *
 *
 * Consider using [com.google.common.collect.PeekingIterator] instead.
 */
interface PeekableIterator<T> : Iterator<T> {
  /**
   * @return the current element.
   * Upon iterator creation should return the first element.
   * After [.hasNext] returned false might throw [NoSuchElementException].
   */
  fun peek(): T

  companion object {
    val EMPTY: PeekableIterator<*> = object : PeekableIterator<Any?> {
      override fun peek(): Any? {
        throw NoSuchElementException()
      }

      override fun hasNext(): Boolean {
        return false
      }

      override fun next(): Any? {
        return null
      }
    }
  }
}
