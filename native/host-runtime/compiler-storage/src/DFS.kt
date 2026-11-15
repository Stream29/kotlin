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

object DFS {
    fun <N, R> dfs(
        nodes: Collection<N>,
        neighbors: Neighbors<N>,
        visited: Visited<N>,
        handler: NodeHandler<N, R>
    ): R {
        for (node in nodes) {
            doDfs(node, neighbors, visited, handler)
        }
        return handler.result()
    }

    fun <N, R> dfs(
        nodes: Collection<N>,
        neighbors: Neighbors<N>,
        handler: NodeHandler<N, R>
    ): R {
        return dfs(nodes, neighbors, VisitedWithSet(), handler)
    }

    fun <N> ifAny(
        nodes: Collection<N>,
        neighbors: Neighbors<N>,
        predicate: (N) -> Boolean
    ): Boolean {
        var result = false
        dfs(nodes, neighbors, object : AbstractNodeHandler<N, Boolean>() {
            override fun beforeChildren(current: N): Boolean {
                if (predicate(current)) {
                    result = true
                }
                return !result
            }

            override fun result(): Boolean = result
        })
        return result
    }

    fun <N, R> dfsFromNode(
        node: N,
        neighbors: Neighbors<N>,
        visited: Visited<N>,
        handler: NodeHandler<N, R>
    ): R {
        doDfs(node, neighbors, visited, handler)
        return handler.result()
    }

    fun <N> dfsFromNode(
        node: N,
        neighbors: Neighbors<N>,
        visited: Visited<N>
    ) {
        dfsFromNode(node, neighbors, visited, object : AbstractNodeHandler<N, Unit>() {
            override fun result() = Unit
        })
    }

    fun <N> topologicalOrder(
        nodes: Iterable<N>,
        neighbors: Neighbors<N>,
        visited: Visited<N>
    ): List<N> {
        val handler = TopologicalOrder<N>()
        for (node in nodes) {
            doDfs(node, neighbors, visited, handler)
        }
        return handler.result()
    }

    fun <N> topologicalOrder(
        nodes: Iterable<N>,
        neighbors: Neighbors<N>
    ): List<N> {
        return topologicalOrder(nodes, neighbors, VisitedWithSet())
    }

    fun <N> doDfs(
        current: N,
        neighbors: Neighbors<N>,
        visited: Visited<N>,
        handler: NodeHandler<N, *>
    ) {
        if (!visited.checkAndMarkVisited(current)) return
        if (!handler.beforeChildren(current)) return

        for (neighbor in neighbors.getNeighbors(current)) {
            doDfs(neighbor, neighbors, visited, handler)
        }
        handler.afterChildren(current)
    }

    interface NodeHandler<N, R> {
        fun beforeChildren(current: N): Boolean
        fun afterChildren(current: N)
        fun result(): R
    }

    fun interface Neighbors<N> {
        fun getNeighbors(current: N): Iterable<N>
    }

    fun interface Visited<N> {
        fun checkAndMarkVisited(current: N): Boolean
    }

    abstract class AbstractNodeHandler<N, R> : NodeHandler<N, R> {
        override fun beforeChildren(current: N): Boolean = true
        override fun afterChildren(current: N) {}
    }

    class VisitedWithSet<N>(private val visited: MutableSet<N> = HashSet()) : Visited<N> {
        override fun checkAndMarkVisited(current: N): Boolean = visited.add(current)
    }

    abstract class CollectingNodeHandler<N, R, C : Iterable<R>>(protected val result: C) : AbstractNodeHandler<N, C>() {
        override fun result(): C = result
    }

    abstract class NodeHandlerWithListResult<N, R> : CollectingNodeHandler<N, R, MutableList<R>>(ArrayList())

    class TopologicalOrder<N> : NodeHandlerWithListResult<N, N>() {
        override fun afterChildren(current: N) {
            result.add(0, current)
        }
    }
}
