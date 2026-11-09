@file:OptIn(kotlin.concurrent.atomics.ExperimentalAtomicApi::class,
    kotlin.native.concurrent.ObsoleteWorkersApi::class, kotlinx.cinterop.ExperimentalForeignApi::class)

package org.jetbrains.kotlin.konan.host

import kotlin.concurrent.atomics.AtomicInt
import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import platform.posix.sched_yield

class NativeCache<K, V> {
    private val lock = AtomicInt(0)
    private val values = mutableMapOf<K, V>()
    fun getOrPut(key: K, compute: () -> V): V {
        while (!lock.compareAndSet(0, 1)) sched_yield()
        return try { values.getOrPut(key, compute) } finally { lock.store(0) }
    }
}

private class ParallelWork<T, R>(val values: List<T>, val transform: (T) -> R)

/** Bounded Native replacement of the original parallel stream, preserving result order. */
fun <T, R> List<T>.nativeParallelMap(transform: (T) -> R): List<R> {
    if (size < 2) return map(transform)
    val workers = List(minOf(2, size)) { Worker.start() }
    return try {
        val chunkSize = (size + workers.size - 1) / workers.size
        val futures = chunked(chunkSize).mapIndexed { i, chunk ->
            workers[i].execute(TransferMode.SAFE, { ParallelWork(chunk, transform) }) { work ->
                work.values.map(work.transform)
            }
        }
        futures.flatMap { it.result }
    } finally {
        workers.forEach { it.requestTermination().result }
    }
}
