/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.utils

import org.jetbrains.kotlin.storage.DefaultSimpleLock
import org.jetbrains.kotlin.storage.guarded

class CallOnceFunction<F, T>(private val defaultValue: T, delegate: Function1<F, T>) : Function1<F, T> {
    private val lock = DefaultSimpleLock()
    private var function: Function1<F, T>? = delegate

    override fun invoke(p1: F): T {
        val selected = lock.guarded { function.also { function = null } }
        return selected?.invoke(p1) ?: defaultValue
    }
}
