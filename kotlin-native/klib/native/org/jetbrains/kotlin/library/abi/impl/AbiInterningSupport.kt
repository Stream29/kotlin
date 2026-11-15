package org.jetbrains.kotlin.library.abi.impl

internal fun <T : Any> HashSet<T>.addOrGet(element: T): T {
    if (add(element)) return element
    return first { it == element }
}
