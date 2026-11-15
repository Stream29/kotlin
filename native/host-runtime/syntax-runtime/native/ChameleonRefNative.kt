// Copyright 2000-2025 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.platform.syntax.impl.builder

import kotlin.native.ref.WeakReference
import kotlinx.cinterop.*
import platform.posix.*
import com.intellij.platform.syntax.extensions.impl.PosixRecursiveLock

/**
 * Native implementation of [ChameleonRef] backed by [kotlin.native.ref.WeakReference]
 * and thread-safe realization under POSIX mutex.
 */
private class NativeChameleonRefImpl(
    initial: AstMarkersChameleon? = null,
) : ChameleonRef {
    private val lock = PosixRecursiveLock()
    private var ref: WeakReference<AstMarkersChameleon>? = initial?.let { WeakReference(it) }

    override val value: AstMarkersChameleon?
        get() = lock.withLock { ref?.value }

    override fun set(value: AstMarkersChameleon) {
        lock.withLock {
            ref = WeakReference(value)
        }
    }

    override fun realize(func: () -> AstMarkersChameleon): AstMarkersChameleon =
        lock.withLock {
            ref?.value ?: func().also { ref = WeakReference(it) }
        }

    override fun toString(): String = value?.toString() ?: "null"
}

internal fun ChameleonRefNative(): ChameleonRef = NativeChameleonRefImpl()

internal fun ChameleonRefNative(chameleon: AstMarkersChameleon): ChameleonRef =
    NativeChameleonRefImpl(chameleon)
