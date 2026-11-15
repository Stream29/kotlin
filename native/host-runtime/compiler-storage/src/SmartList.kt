/*
 * Copyright 2010-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.utils

/**
 * A List which is optimised for the sizes of 0 and 1,
 * in which cases it would not allocate array at all.
 */
class SmartList<E> : AbstractMutableList<E>, RandomAccess {
    private var mySize: Int = 0
    private var myElem: Any? = null

    constructor()

    constructor(element: E) {
        add(element)
    }

    constructor(elements: Collection<E>) {
        val size = elements.size
        if (size == 1) {
            val element = if (elements is List<E>) elements[0] else elements.iterator().next()
            add(element)
        } else if (size > 0) {
            mySize = size
            val array = arrayOfNulls<Any?>(size)
            var i = 0
            for (e in elements) {
                array[i++] = e
            }
            myElem = array
        }
    }

    constructor(vararg elements: E) {
        if (elements.size == 1) {
            add(elements[0])
        } else if (elements.isNotEmpty()) {
            mySize = elements.size
            val array = arrayOfNulls<Any?>(mySize)
            for (i in 0 until mySize) {
                array[i] = elements[i]
            }
            myElem = array
        }
    }

    override val size: Int
        get() = mySize

    @Suppress("UNCHECKED_CAST")
    override fun get(index: Int): E {
        if (index < 0 || index >= mySize) {
            throw IndexOutOfBoundsException("Index: $index, Size: $mySize")
        }
        if (mySize == 1) {
            return myElem as E
        }
        return (myElem as Array<Any?>)[index] as E
    }

    override fun add(element: E): Boolean {
        if (mySize == 0) {
            myElem = element
        } else if (mySize == 1) {
            val array = arrayOfNulls<Any?>(2)
            array[0] = myElem
            array[1] = element
            myElem = array
        } else {
            @Suppress("UNCHECKED_CAST")
            var array = myElem as Array<Any?>
            val oldCapacity = array.size
            if (mySize >= oldCapacity) {
                var newCapacity = oldCapacity * 3 / 2 + 1
                val minCapacity = mySize + 1
                if (newCapacity < minCapacity) {
                    newCapacity = minCapacity
                }
                val newArray = arrayOfNulls<Any?>(newCapacity)
                array.copyInto(newArray, 0, 0, oldCapacity)
                myElem = newArray
                array = newArray
            }
            array[mySize] = element
        }
        mySize++
        modCount++
        return true
    }

    override fun add(index: Int, element: E) {
        if (index < 0 || index > mySize) {
            throw IndexOutOfBoundsException("Index: $index, Size: $mySize")
        }
        if (mySize == 0) {
            myElem = element
        } else if (mySize == 1 && index == 0) {
            val array = arrayOfNulls<Any?>(2)
            array[0] = element
            array[1] = myElem
            myElem = array
        } else {
            val array = arrayOfNulls<Any?>(mySize + 1)
            if (mySize == 1) {
                array[0] = myElem
            } else {
                @Suppress("UNCHECKED_CAST")
                val oldArray = myElem as Array<Any?>
                oldArray.copyInto(array, 0, 0, index)
                oldArray.copyInto(array, index + 1, index, mySize)
            }
            array[index] = element
            myElem = array
        }
        mySize++
        modCount++
    }

    override fun clear() {
        myElem = null
        mySize = 0
        modCount++
    }

    @Suppress("UNCHECKED_CAST")
    override fun set(index: Int, element: E): E {
        if (index < 0 || index >= mySize) {
            throw IndexOutOfBoundsException("Index: $index, Size: $mySize")
        }
        val oldValue: E
        if (mySize == 1) {
            oldValue = myElem as E
            myElem = element
        } else {
            @Suppress("UNCHECKED_CAST")
            val array = myElem as Array<Any?>
            oldValue = array[index] as E
            array[index] = element
        }
        return oldValue
    }

    @Suppress("UNCHECKED_CAST")
    override fun removeAt(index: Int): E {
        if (index < 0 || index >= mySize) {
            throw IndexOutOfBoundsException("Index: $index, Size: $mySize")
        }
        val oldValue: E
        if (mySize == 1) {
            oldValue = myElem as E
            myElem = null
        } else {
            @Suppress("UNCHECKED_CAST")
            val array = myElem as Array<Any?>
            oldValue = array[index] as E
            if (mySize == 2) {
                myElem = array[1 - index]
            } else {
                val numMoved = mySize - index - 1
                if (numMoved > 0) {
                    array.copyInto(array, index, index + 1, mySize)
                }
                array[mySize - 1] = null
            }
        }
        mySize--
        modCount++
        return oldValue
    }

    fun getModificationCount(): Int = modCount

    fun sort(comparator: Comparator<in E>) {
        if (mySize >= 2) {
            @Suppress("UNCHECKED_CAST")
            val array = myElem as Array<Any?>
            val subList = ArrayList<E>(mySize)
            for (i in 0 until mySize) {
                subList.add(array[i] as E)
            }
            subList.sortWith(comparator)
            for (i in 0 until mySize) {
                array[i] = subList[i]
            }
        }
    }

    fun trimToSize() {
        if (mySize < 2) return
        @Suppress("UNCHECKED_CAST")
        val array = myElem as Array<Any?>
        if (mySize < array.size) {
            modCount++
            val newArray = arrayOfNulls<Any?>(mySize)
            array.copyInto(newArray, 0, 0, mySize)
            myElem = newArray
        }
    }

    override fun iterator(): MutableIterator<E> {
        if (mySize == 0) return EmptyIterator.getInstance()
        if (mySize == 1) return SingletonIterator()
        return super.iterator()
    }

    private class EmptyIterator<T> private constructor() : MutableIterator<T> {
        companion object {
            private val INSTANCE = EmptyIterator<Any?>()
            @Suppress("UNCHECKED_CAST")
            fun <T> getInstance(): EmptyIterator<T> = INSTANCE as EmptyIterator<T>
        }
        override fun hasNext(): Boolean = false
        override fun next(): T = throw NoSuchElementException()
        override fun remove() = throw IllegalStateException()
    }

    private inner class SingletonIterator : MutableIterator<E> {
        private val myInitialModCount = modCount
        private var myVisited = false

        override fun hasNext(): Boolean = !myVisited

        @Suppress("UNCHECKED_CAST")
        override fun next(): E {
            if (myVisited) throw NoSuchElementException()
            myVisited = true
            checkCoModification()
            return myElem as E
        }

        private fun checkCoModification() {
            if (modCount != myInitialModCount) {
                throw ConcurrentModificationException("ModCount: $modCount; expected: $myInitialModCount")
            }
        }

        override fun remove() {
            checkCoModification()
            clear()
        }
    }
}
