/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir

/*
    Lock protecting accesses to global IR structures during by-file lowerings.
 */
class IrLock {
    @PublishedApi
    internal val nativeLock = org.jetbrains.kotlin.storage.ReentrantLock()
}

inline fun <T> synchronized(lock: IrLock, block: () -> T): T {
    lock.nativeLock.lock()
    try {
        return block()
    } finally {
        lock.nativeLock.unlock()
    }
}
