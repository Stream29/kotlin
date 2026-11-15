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

import org.jetbrains.kotlin.utils.WrappedValues
import org.jetbrains.kotlin.utils.isProcessCanceledException
import org.jetbrains.kotlin.utils.rethrow
import kotlin.concurrent.Volatile

open class LockBasedStorageManager(
    private val debugText: String,
    private val exceptionHandlingStrategy: ExceptionHandlingStrategy,
    protected val lock: SimpleLock
) : StorageManager {

    interface ExceptionHandlingStrategy {
        fun handleException(throwable: Throwable): RuntimeException

        companion object {
            val THROW: ExceptionHandlingStrategy = object : ExceptionHandlingStrategy {
                override fun handleException(throwable: Throwable): RuntimeException {
                    throw rethrow(throwable)
                }
            }
        }
    }

    companion object {
        val NO_LOCKS: StorageManager = object : LockBasedStorageManager("NO_LOCKS", ExceptionHandlingStrategy.THROW, EmptySimpleLock) {
            override fun <K, V> recursionDetectedDefault(source: String, input: K?): RecursionDetectedResult<V> {
                return RecursionDetectedResult.fallThrough()
            }
        }

        fun createWithExceptionHandling(
            debugText: String,
            exceptionHandlingStrategy: ExceptionHandlingStrategy
        ): LockBasedStorageManager = createWithExceptionHandling(debugText, exceptionHandlingStrategy, null, null)

        fun createWithExceptionHandling(
            debugText: String,
            exceptionHandlingStrategy: ExceptionHandlingStrategy,
            checkCancelled: Runnable?,
            interruptedExceptionHandler: ((InterruptedException) -> Unit)?
        ): LockBasedStorageManager {
            return LockBasedStorageManager(
                debugText,
                exceptionHandlingStrategy,
                SimpleLock.simpleLock(checkCancelled, interruptedExceptionHandler)
            )
        }

        private fun <K> createConcurrentHashMap(): ConcurrentMap<K, Any> {
            return ConcurrentHashMap()
        }

        private fun <T : Throwable> sanitizeStackTrace(throwable: T): T {
            return throwable
        }
    }

    constructor(debugText: String) : this(debugText, null as Runnable?, null)

    constructor(
        debugText: String,
        checkCancelled: Runnable?,
        interruptedExceptionHandler: ((InterruptedException) -> Unit)?
    ) : this(
        debugText,
        ExceptionHandlingStrategy.THROW,
        SimpleLock.simpleLock(checkCancelled, interruptedExceptionHandler)
    )

    override fun toString(): String = "${this::class.simpleName ?: "LockBasedStorageManager"}@${hashCode().toUInt().toString(16)} ($debugText)"

    open fun replaceExceptionHandling(
        debugText: String,
        exceptionHandlingStrategy: ExceptionHandlingStrategy
    ): LockBasedStorageManager = LockBasedStorageManager(debugText, exceptionHandlingStrategy, lock)

    override fun <K, V : Any> createMemoizedFunction(compute: (K) -> V): MemoizedFunctionToNotNull<K, V> {
        return createMemoizedFunction(compute, createConcurrentHashMap())
    }

    override fun <K, V : Any> createMemoizedFunction(
        compute: (K) -> V,
        onRecursiveCall: (K, Boolean) -> V
    ): MemoizedFunctionToNotNull<K, V> {
        return createMemoizedFunction(compute, onRecursiveCall, createConcurrentHashMap())
    }

    override fun <K, V : Any> createMemoizedFunction(
        compute: (K) -> V,
        map: ConcurrentMap<K, Any>
    ): MemoizedFunctionToNotNull<K, V> {
        return MapBasedMemoizedFunctionToNotNull(this, map, compute)
    }

    override fun <K, V : Any> createMemoizedFunction(
        compute: (K) -> V,
        onRecursiveCall: (K, Boolean) -> V,
        map: ConcurrentMap<K, Any>
    ): MemoizedFunctionToNotNull<K, V> {
        return object : MapBasedMemoizedFunctionToNotNull<K, V>(this, map, compute) {
            override fun recursionDetected(input: K, firstTime: Boolean): RecursionDetectedResult<V> {
                return RecursionDetectedResult.value(onRecursiveCall(input, firstTime))
            }
        }
    }

    override fun <K, V : Any> createMemoizedFunctionWithNullableValues(compute: (K) -> V?): MemoizedFunctionToNullable<K, V> {
        return createMemoizedFunctionWithNullableValues(compute, createConcurrentHashMap())
    }

    override fun <K, V : Any> createMemoizedFunctionWithNullableValues(
        compute: (K) -> V?,
        map: ConcurrentMap<K, Any>
    ): MemoizedFunctionToNullable<K, V> {
        return object : MapBasedMemoizedFunction<K, V>(this, map, compute), MemoizedFunctionToNullable<K, V> {}
    }

    override fun <T : Any> createLazyValue(computable: () -> T): NotNullLazyValue<T> {
        return LockBasedNotNullLazyValue(this, computable)
    }

    override fun <T : Any> createLazyValue(
        computable: () -> T,
        onRecursiveCall: (Boolean) -> T
    ): NotNullLazyValue<T> {
        return object : LockBasedNotNullLazyValue<T>(this, computable) {
            override fun recursionDetected(firstTime: Boolean): RecursionDetectedResult<T> {
                return RecursionDetectedResult.value(onRecursiveCall(firstTime))
            }
        }
    }

    override fun <T : Any> createRecursionTolerantLazyValue(computable: () -> T, onRecursiveCall: T): NotNullLazyValue<T> {
        return object : LockBasedNotNullLazyValue<T>(this, computable) {
            override fun recursionDetected(firstTime: Boolean): RecursionDetectedResult<T> {
                return RecursionDetectedResult.value(onRecursiveCall)
            }

            override fun presentableName(): String = "RecursionTolerantLazyValue"
        }
    }

    override fun <T : Any> createLazyValueWithPostCompute(
        computable: () -> T,
        onRecursiveCall: ((Boolean) -> T)?,
        postCompute: (T) -> Unit
    ): NotNullLazyValue<T> {
        return object : LockBasedNotNullLazyValueWithPostCompute<T>(this, computable) {
            override fun recursionDetected(firstTime: Boolean): RecursionDetectedResult<T> {
                if (onRecursiveCall == null) {
                    return super.recursionDetected(firstTime)
                }
                return RecursionDetectedResult.value(onRecursiveCall(firstTime))
            }

            override fun doPostCompute(value: T) {
                postCompute(value)
            }

            override fun presentableName(): String = "LockBasedNotNullLazyValueWithPostCompute"
        }
    }

    override fun <T : Any> createNullableLazyValue(computable: () -> T?): NullableLazyValue<T> {
        return object : LockBasedLazyValue<T?>(this, computable), NullableLazyValue<T> {}
    }

    override fun <T : Any> createRecursionTolerantNullableLazyValue(computable: () -> T?, onRecursiveCall: T?): NullableLazyValue<T> {
        return object : LockBasedLazyValue<T?>(this, computable), NullableLazyValue<T> {
            override fun recursionDetected(firstTime: Boolean): RecursionDetectedResult<T?> {
                return RecursionDetectedResult.value(onRecursiveCall)
            }

            override fun presentableName(): String = "RecursionTolerantNullableLazyValue"
        }
    }

    override fun <T : Any> createNullableLazyValueWithPostCompute(
        computable: () -> T?,
        postCompute: (T?) -> Unit
    ): NullableLazyValue<T> {
        return object : LockBasedLazyValueWithPostCompute<T?>(this, computable), NullableLazyValue<T> {
            override fun doPostCompute(value: T?) {
                postCompute(value)
            }

            override fun presentableName(): String = "NullableLazyValueWithPostCompute"
        }
    }

    override fun <T> compute(computable: () -> T): T {
        lock.lock()
        try {
            return computable()
        } catch (throwable: Throwable) {
            throw exceptionHandlingStrategy.handleException(throwable)
        } finally {
            lock.unlock()
        }
    }

    protected open fun <K, V> recursionDetectedDefault(source: String, input: K?): RecursionDetectedResult<V> {
        throw sanitizeStackTrace(
            AssertionError("Recursion detected $source${if (input == null) "" else "on input: $input"} under $this")
        )
    }

    protected class RecursionDetectedResult<T> private constructor(
        private val value: T?,
        val isFallThrough: Boolean
    ) {
        companion object {
            fun <T> value(value: T): RecursionDetectedResult<T> = RecursionDetectedResult(value, false)
            fun <T> fallThrough(): RecursionDetectedResult<T> = RecursionDetectedResult(null, true)
        }

        val resultValue: T
            get() {
                assert(!isFallThrough) { "A value requested from FALL_THROUGH" }
                @Suppress("UNCHECKED_CAST")
                return value as T
            }

        override fun toString(): String = if (isFallThrough) "FALL_THROUGH" else value.toString()
    }

    private enum class NotValue {
        NOT_COMPUTED,
        COMPUTING,
        RECURSION_WAS_DETECTED
    }

    private open class LockBasedLazyValue<T>(
        private val storageManager: LockBasedStorageManager,
        private val computable: () -> T
    ) {
        @Volatile
        private var value: Any? = NotValue.NOT_COMPUTED

        open fun isComputed(): Boolean = value != NotValue.NOT_COMPUTED && value != NotValue.COMPUTING

        open fun isComputing(): Boolean = value == NotValue.COMPUTING

        @Suppress("UNCHECKED_CAST")
        open operator fun invoke(): T {
            var _value = value
            if (_value !is NotValue) return WrappedValues.unescapeThrowable<T>(_value) as T

            storageManager.lock.lock()
            try {
                _value = value
                if (_value !is NotValue) return WrappedValues.unescapeThrowable<T>(_value) as T

                if (_value == NotValue.COMPUTING) {
                    value = NotValue.RECURSION_WAS_DETECTED
                    val result = recursionDetected(true)
                    if (!result.isFallThrough) {
                        return result.resultValue
                    }
                }

                if (_value == NotValue.RECURSION_WAS_DETECTED) {
                    val result = recursionDetected(false)
                    if (!result.isFallThrough) {
                        return result.resultValue
                    }
                }

                value = NotValue.COMPUTING
                try {
                    val typedValue = computable()
                    postCompute(typedValue)
                    value = typedValue
                    return typedValue
                } catch (throwable: Throwable) {
                    if (throwable.isProcessCanceledException()) {
                        value = NotValue.NOT_COMPUTED
                        throw throwable
                    }

                    if (value == NotValue.COMPUTING) {
                        value = WrappedValues.escapeThrowable(throwable)
                    }
                    throw storageManager.exceptionHandlingStrategy.handleException(throwable)
                }
            } finally {
                storageManager.lock.unlock()
            }
        }

        protected open fun recursionDetected(firstTime: Boolean): RecursionDetectedResult<T> {
            return storageManager.recursionDetectedDefault("in a lazy value", null)
        }

        protected open fun postCompute(value: T) {}

        open fun renderDebugInformation(): String = "${presentableName()}, storageManager=$storageManager"

        protected open fun presentableName(): String = this::class.simpleName ?: "LockBasedLazyValue"
    }

    private abstract class LockBasedLazyValueWithPostCompute<T>(
        storageManager: LockBasedStorageManager,
        computable: () -> T
    ) : LockBasedLazyValue<T>(storageManager, computable) {
        @Volatile
        private var valuePostCompute: SingleThreadValue<T>? = null

        override fun invoke(): T {
            val postComputeCache = valuePostCompute
            if (postComputeCache != null && postComputeCache.hasValue()) {
                return postComputeCache.getValue()
            }
            return super.invoke()
        }

        override fun postCompute(value: T) {
            valuePostCompute = SingleThreadValue(value)
            try {
                doPostCompute(value)
            } finally {
                valuePostCompute = null
            }
        }

        protected abstract fun doPostCompute(value: T)
    }

    private abstract class LockBasedNotNullLazyValueWithPostCompute<T : Any>(
        storageManager: LockBasedStorageManager,
        computable: () -> T
    ) : LockBasedLazyValueWithPostCompute<T>(storageManager, computable), NotNullLazyValue<T> {
        override fun renderDebugInformation(): String = super<LockBasedLazyValueWithPostCompute>.renderDebugInformation()
        override fun invoke(): T {
            val result = super.invoke()
            assert(result != null) { "compute() returned null" }
            return result!!
        }
    }

    private open class LockBasedNotNullLazyValue<T : Any>(
        storageManager: LockBasedStorageManager,
        computable: () -> T
    ) : LockBasedLazyValue<T>(storageManager, computable), NotNullLazyValue<T> {
        override fun renderDebugInformation(): String = super<LockBasedLazyValue>.renderDebugInformation()
        override fun invoke(): T {
            val result = super.invoke()
            assert(result != null) { "compute() returned null" }
            return result!!
        }
    }

    private open class MapBasedMemoizedFunction<K, V>(
        private val storageManager: LockBasedStorageManager,
        private val cache: ConcurrentMap<K, Any>,
        private val compute: (K) -> V?
    ) {

        open operator fun invoke(input: K): V? {
            var value = cache[input]
            if (value != null && value != NotValue.COMPUTING) {
                return WrappedValues.unescapeExceptionOrNull(value)
            }

            storageManager.lock.lock()
            try {
                value = cache[input]

                if (value == NotValue.COMPUTING) {
                    value = NotValue.RECURSION_WAS_DETECTED
                    val result = recursionDetected(input, true)
                    if (!result.isFallThrough) {
                        return result.resultValue
                    }
                }

                if (value == NotValue.RECURSION_WAS_DETECTED) {
                    val result = recursionDetected(input, false)
                    if (!result.isFallThrough) {
                        return result.resultValue
                    }
                }

                if (value != null) return WrappedValues.unescapeExceptionOrNull(value)

                var error: AssertionError? = null
                try {
                    cache[input] = NotValue.COMPUTING
                    val typedValue = compute(input)
                    val oldValue = cache.put(input, WrappedValues.escapeNull(typedValue))

                    if (oldValue != NotValue.COMPUTING) {
                        error = raceCondition(input, oldValue)
                        throw error
                    }

                    return typedValue
                } catch (throwable: Throwable) {
                    if (throwable.isProcessCanceledException()) {
                        val remove: Any?
                        try {
                            remove = cache.remove(input)
                        } catch (e: Throwable) {
                            throw unableToRemoveKey(input, e)
                        }
                        if (remove != NotValue.COMPUTING) {
                            throw inconsistentComputingKey(input, remove)
                        }
                        throw throwable
                    }
                    if (throwable === error) {
                        try {
                            cache.remove(input)
                        } catch (e: Throwable) {
                            throw unableToRemoveKey(input, e)
                        }
                        throw storageManager.exceptionHandlingStrategy.handleException(throwable)
                    }

                    val oldValue = cache.put(input, WrappedValues.escapeThrowable(throwable))
                    if (oldValue != NotValue.COMPUTING) {
                        throw raceCondition(input, oldValue)
                    }

                    throw storageManager.exceptionHandlingStrategy.handleException(throwable)
                }
            } finally {
                storageManager.lock.unlock()
            }
        }

        protected open fun recursionDetected(input: K, firstTime: Boolean): RecursionDetectedResult<V> {
            return storageManager.recursionDetectedDefault("", input)
        }

        private fun raceCondition(input: K, oldValue: Any?): AssertionError = sanitizeStackTrace(
            AssertionError("Race condition detected on input $input. Old value is $oldValue under $storageManager")
        )

        private fun inconsistentComputingKey(input: K, oldValue: Any?): AssertionError = sanitizeStackTrace(
            AssertionError("Inconsistent key detected. ${NotValue.COMPUTING} is expected, was: $oldValue, most probably race condition detected on input $input under $storageManager")
        )

        private fun unableToRemoveKey(input: K, throwable: Throwable): AssertionError = sanitizeStackTrace(
            AssertionError("Unable to remove $input under $storageManager", throwable)
        )

        open fun isComputed(key: K): Boolean {
            val value = cache[key]
            return value != null && value != NotValue.COMPUTING
        }

        protected fun getStorageManager(): LockBasedStorageManager = storageManager
    }

    private open class MapBasedMemoizedFunctionToNotNull<K, V : Any>(
        storageManager: LockBasedStorageManager,
        map: ConcurrentMap<K, Any>,
        compute: (K) -> V
    ) : MapBasedMemoizedFunction<K, V>(storageManager, map, compute), MemoizedFunctionToNotNull<K, V> {
        override fun invoke(input: K): V {
            val result = super.invoke(input)
            assert(result != null) { "compute() returned null under ${getStorageManager()}" }
            return result!!
        }
    }

    override fun <K, V : Any> createCacheWithNullableValues(): CacheWithNullableValues<K, V> {
        return CacheWithNullableValuesBasedOnMemoizedFunction(this, createConcurrentHashMap())
    }

    private open class CacheWithNullableValuesBasedOnMemoizedFunction<K, V : Any>(
        storageManager: LockBasedStorageManager,
        map: ConcurrentMap<KeyWithComputation<K, V>, Any>
    ) : MapBasedMemoizedFunction<KeyWithComputation<K, V>, V>(
        storageManager,
        map,
        { computation -> computation.computation() }
    ), CacheWithNullableValues<K, V> {
        override fun computeIfAbsent(key: K, computation: () -> V?): V? {
            return invoke(KeyWithComputation(key, computation))
        }
    }

    override fun <K, V : Any> createCacheWithNotNullValues(): CacheWithNotNullValues<K, V> {
        return CacheWithNotNullValuesBasedOnMemoizedFunction(this, createConcurrentHashMap())
    }

    private class CacheWithNotNullValuesBasedOnMemoizedFunction<K, V : Any>(
        storageManager: LockBasedStorageManager,
        map: ConcurrentMap<KeyWithComputation<K, V>, Any>
    ) : CacheWithNullableValuesBasedOnMemoizedFunction<K, V>(storageManager, map), CacheWithNotNullValues<K, V> {
        override fun computeIfAbsent(key: K, computation: () -> V): V {
            val result = super.computeIfAbsent(key, computation)
            assert(result != null) { "computeIfAbsent() returned null under ${getStorageManager()}" }
            return result!!
        }
    }

    private class KeyWithComputation<K, V>(
        private val key: K,
        val computation: () -> V?
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || other !is KeyWithComputation<*, *>) return false
            return key == other.key
        }

        override fun hashCode(): Int = key?.hashCode() ?: 0
    }
}

class ConcurrentHashMap<K, V> : AbstractMutableMap<K, V>(), ConcurrentMap<K, V> {
    private val lock = DefaultSimpleLock()
    private val delegate = HashMap<K, V>()
    private var computing: HashSet<K>? = null

    override val size: Int get() = lock.guarded { delegate.size }
    override fun isEmpty(): Boolean = lock.guarded { delegate.isEmpty() }
    override fun containsKey(key: K): Boolean = lock.guarded { delegate.containsKey(key) }
    override fun containsValue(value: V): Boolean = lock.guarded { delegate.containsValue(value) }
    override fun get(key: K): V? = lock.guarded { delegate[key] }
    override fun put(key: K, value: V): V? = lock.guarded {
        requireNotNull(key)
        requireNotNull(value)
        delegate.put(key, value)
    }
    override fun remove(key: K): V? = lock.guarded { delegate.remove(key) }
    override fun putAll(from: Map<out K, V>) {
        val copy = from.toMap()
        lock.guarded { copy.forEach { (key, value) -> put(key, value) } }
    }
    override fun clear() = lock.guarded { delegate.clear() }
    override fun putIfAbsent(key: K, value: V): V? = lock.guarded {
        delegate[key] ?: put(key, value)
    }
    fun computeIfAbsent(key: K, mapping: (K) -> V): V = lock.guarded {
        delegate[key] ?: run {
            val active = computing ?: HashSet<K>(4).also { computing = it }
            check(active.add(key)) { "Recursive computation for key $key" }
            try { mapping(key).also { put(key, it) } }
            finally { active.remove(key) }
        }
    }
    override fun remove(key: K, value: V): Boolean = lock.guarded {
        if (delegate[key] != value || !delegate.containsKey(key)) false
        else { delegate.remove(key); true }
    }
    override fun replace(key: K, value: V): V? = lock.guarded {
        if (delegate.containsKey(key)) put(key, value) else null
    }
    override fun replace(key: K, oldValue: V, newValue: V): Boolean = lock.guarded {
        if (delegate[key] != oldValue || !delegate.containsKey(key)) false
        else { put(key, newValue); true }
    }

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>> =
        object : AbstractMutableSet<MutableMap.MutableEntry<K, V>>() {
            override val size: Int get() = this@ConcurrentHashMap.size
            override fun clear() = this@ConcurrentHashMap.clear()
            override fun add(element: MutableMap.MutableEntry<K, V>): Boolean =
                throw UnsupportedOperationException("Map entry views do not support add")
            override fun contains(element: MutableMap.MutableEntry<K, V>): Boolean = lock.guarded {
                delegate.containsKey(element.key) && delegate[element.key] == element.value
            }
            override fun remove(element: MutableMap.MutableEntry<K, V>): Boolean =
                this@ConcurrentHashMap.remove(element.key, element.value)
            override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> {
                val snapshot = lock.guarded { delegate.map { it.key to it.value } }.iterator()
                return object : MutableIterator<MutableMap.MutableEntry<K, V>> {
                    private var last: MutableMap.MutableEntry<K, V>? = null
                    override fun hasNext(): Boolean = snapshot.hasNext()
                    override fun next(): MutableMap.MutableEntry<K, V> {
                        val pair = snapshot.next()
                        return object : MutableMap.MutableEntry<K, V> {
                            override val key: K = pair.first
                            override var value: V = pair.second
                                private set
                            override fun setValue(newValue: V): V {
                                val previous = value
                                put(key, newValue)
                                value = newValue
                                return previous
                            }
                            override fun equals(other: Any?): Boolean =
                                other is Map.Entry<*, *> && key == other.key && value == other.value
                            override fun hashCode(): Int = (key?.hashCode() ?: 0) xor (value?.hashCode() ?: 0)
                            override fun toString(): String = "$key=$value"
                        }.also { last = it }
                    }
                    override fun remove() {
                        val entry = checkNotNull(last) { "Call next before remove" }
                        this@ConcurrentHashMap.remove(entry.key)
                        last = null
                    }
                }
            }
        }
}
