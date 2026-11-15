/*
 * Copyright 2010-2015 JetBrains s.r.o.
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

import kotlin.concurrent.Volatile

object WrappedValues {
    private val NULL_VALUE = object {
        override fun toString(): String = "NULL_VALUE"
    }

    @Volatile
    var throwWrappedProcessCanceledException: Boolean = false

    private class ThrowableWrapper(val throwable: Throwable) {
        override fun toString(): String = throwable.toString()
    }

    @Suppress("UNCHECKED_CAST")
    fun <V> unescapeNull(value: Any): V? {
        if (value === NULL_VALUE) return null
        return value as V
    }

    fun <V> escapeNull(value: V?): Any {
        return value ?: NULL_VALUE
    }

    fun escapeThrowable(throwable: Throwable): Any {
        return ThrowableWrapper(throwable)
    }

    fun <V> unescapeExceptionOrNull(value: Any): V? {
        return unescapeNull(unescapeThrowable(value))
    }

    @Suppress("UNCHECKED_CAST")
    fun <V> unescapeThrowable(value: Any?): V {
        if (value is ThrowableWrapper) {
            val originThrowable = value.throwable

            if (throwWrappedProcessCanceledException && originThrowable.isProcessCanceledException()) {
                throw WrappedProcessCanceledException(originThrowable)
            }

            throw rethrow(originThrowable)
        }

        return value as V
    }

    class WrappedProcessCanceledException(cause: Throwable?) : RuntimeException("Rethrow stored exception", cause)
}
