// Copyright 2000-2025 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.platform.syntax.extensions.impl

import com.intellij.platform.syntax.extensions.ExtensionSupport
import kotlin.native.concurrent.ThreadLocal

internal fun instantiateExtensionRegistryNative(): ExtensionSupport = ExtensionRegistryImpl()

@ThreadLocal
private var currentThreadRegistry: ExtensionSupport? = null

/**
 * Thread-local registry holder backed by Kotlin/Native [@ThreadLocal].
 * Guarantees per-thread extension isolation without C interop or StableRef allocations.
 */
internal object NativeThreadLocalRegistryHolder : RegistryHolder {
    override val registry: ExtensionSupport?
        get() = currentThreadRegistry

    override fun installRegistry(registry: ExtensionSupport) {
        currentThreadRegistry = registry
    }

    override fun clearRegistry() {
        currentThreadRegistry = null
    }
}

internal fun instantiateThreadLocalRegistryNative(): RegistryHolder = NativeThreadLocalRegistryHolder
