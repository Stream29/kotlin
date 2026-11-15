// Copyright 2000-2025 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.platform.syntax.impl.builder

/**
 * Native stack trace adaptation.
 * In Kotlin/Native, stack traces are immutable and runtime-managed; returns the original [th].
 */
internal fun makeStackTraceRelativeNative(th: Throwable, relativeTo: Throwable): Throwable {
    return th
}
