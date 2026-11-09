/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.resolve.calls.tower

import org.jetbrains.kotlin.fir.resolve.calls.candidate.CandidateCollector
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume

class TowerResolveManager private constructor(private val shouldStopAtTheLevel: (TowerGroup) -> Boolean) {

    constructor(collector: CandidateCollector) : this(collector::shouldStopAtTheGroup)

    private class TaskPriorityQueue {
        private val heap = ArrayList<SuspendedResolverTask>()

        fun clear() {
            heap.clear()
        }

        fun isNotEmpty(): Boolean = heap.isNotEmpty()

        fun peek(): SuspendedResolverTask? = heap.firstOrNull()

        operator fun plusAssign(x: SuspendedResolverTask) {
            var k = heap.size
            heap.add(x)
            while (k > 0) {
                val parent = (k - 1) ushr 1
                val e = heap[parent]
                if (x >= e) break
                heap[k] = e
                k = parent
            }
            heap[k] = x
        }

        fun poll(): SuspendedResolverTask {
            val result = heap[0]
            val x = heap.removeAt(heap.lastIndex)
            if (heap.isNotEmpty()) {
                var k = 0
                val half = heap.size ushr 1
                while (k < half) {
                    var child = (k shl 1) + 1
                    var c = heap[child]
                    val right = child + 1
                    if (right < heap.size && c > heap[right]) {
                        child = right
                        c = heap[child]
                    }
                    if (x <= c) break
                    heap[k] = c
                    k = child
                }
                heap[k] = x
            }
            return result
        }
    }

    private val queue = TaskPriorityQueue()

    fun reset() {
        queue.clear()
    }

    private suspend fun suspendResolverTask(group: TowerGroup) =
        suspendCoroutineUninterceptedOrReturn<Unit> {
            queue += SuspendedResolverTask(it, group)
            COROUTINE_SUSPENDED
        }

    suspend fun requestGroup(requested: TowerGroup) {
        if (shouldStopAtTheLevel(requested)) {
            stopResolverTask()
        }
        val peeked = queue.peek()

        // Task ordering should be FIFO
        // Here, if peeked have equal group it means that it came first to this function, so should be processed before us
        if (peeked != null && peeked.group <= requested) {
            suspendResolverTask(requested)
        }
    }

    private suspend fun stopResolverTask(): Nothing = suspendCoroutineUninterceptedOrReturn { COROUTINE_SUSPENDED }

    private data class SuspendedResolverTask(
        val continuation: Continuation<Unit>,
        val group: TowerGroup
    ) : Comparable<SuspendedResolverTask> {
        override fun compareTo(other: SuspendedResolverTask): Int {
            return group.compareTo(other.group)
        }
    }

    fun enqueueResolverTask(group: TowerGroup = TowerGroup.Start, task: suspend () -> Unit) {
        val continuation = task.createCoroutineUnintercepted(
            object : Continuation<Unit> {
                override val context: CoroutineContext
                    get() = EmptyCoroutineContext

                override fun resumeWith(result: Result<Unit>) {
                    result.getOrThrow()
                }
            }
        )

        queue += SuspendedResolverTask(continuation, group)
    }

    private fun resumeTask(task: SuspendedResolverTask) {
        if (shouldStopAtTheLevel(task.group)) {
            return
        }
        task.continuation.resume(Unit)
    }

    fun runTasks() {
        while (queue.isNotEmpty()) {
            val current = queue.poll()
            resumeTask(current)
        }
    }
}
