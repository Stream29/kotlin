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

package org.jetbrains.kotlin.storage

interface ConcurrentMap<K, V> : MutableMap<K, V> {
    fun putIfAbsent(key: K, value: V): V?
    fun remove(key: K, value: V): Boolean
    fun replace(key: K, value: V): V?
    fun replace(key: K, oldValue: V, newValue: V): Boolean
}

interface StorageManager {
    /**
     * Given a function compute: K -> V create a memoized version of it that computes a value only once for each key
     * @param compute the function to be memoized
     */
    fun <K, V : Any> createMemoizedFunction(compute: (K) -> V): MemoizedFunctionToNotNull<K, V>

    fun <K, V : Any> createMemoizedFunction(compute: (K) -> V, onRecursiveCall: (K, Boolean) -> V): MemoizedFunctionToNotNull<K, V>

    fun <K, V : Any> createMemoizedFunctionWithNullableValues(compute: (K) -> V?): MemoizedFunctionToNullable<K, V>

    fun <K, V : Any> createCacheWithNullableValues(): CacheWithNullableValues<K, V>
    fun <K, V : Any> createCacheWithNotNullValues(): CacheWithNotNullValues<K, V>

    fun <K, V : Any> createMemoizedFunction(compute: (K) -> V, map: ConcurrentMap<K, Any>): MemoizedFunctionToNotNull<K, V>

    fun <K, V : Any> createMemoizedFunction(compute: (K) -> V, onRecursiveCall: (K, Boolean) -> V, map: ConcurrentMap<K, Any>): MemoizedFunctionToNotNull<K, V>

    fun <K, V : Any> createMemoizedFunctionWithNullableValues(compute: (K) -> V?, map: ConcurrentMap<K, Any>): MemoizedFunctionToNullable<K, V>

    fun <T : Any> createLazyValue(computable: () -> T): NotNullLazyValue<T>

    fun <T : Any> createLazyValue(computable: () -> T, onRecursiveCall: (Boolean) -> T): NotNullLazyValue<T>

    fun <T : Any> createRecursionTolerantLazyValue(computable: () -> T, onRecursiveCall: T): NotNullLazyValue<T>

    /**
     * @param onRecursiveCall is called if the computation calls itself recursively.
     *                        The parameter to it is `true` for the first call, `false` otherwise.
     *                        If `onRecursiveCall` is `null`, an exception will be thrown on a recursive call,
     *                        otherwise it's executed and its result is returned
     *
     * @param postCompute is called after the value is computed AND published.
     */
    fun <T : Any> createLazyValueWithPostCompute(computable: () -> T, onRecursiveCall: ((Boolean) -> T)?, postCompute: (T) -> Unit): NotNullLazyValue<T>

    fun <T : Any> createNullableLazyValue(computable: () -> T?): NullableLazyValue<T>

    fun <T : Any> createRecursionTolerantNullableLazyValue(computable: () -> T?, onRecursiveCall: T?): NullableLazyValue<T>

    /**
     * See javadoc for createLazyValueWithPostCompute
     */
    fun <T : Any> createNullableLazyValueWithPostCompute(computable: () -> T?, postCompute: (T?) -> Unit): NullableLazyValue<T>

    fun <T> compute(computable: () -> T): T
}
