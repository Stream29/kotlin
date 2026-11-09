/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.config

import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.fputs
import platform.posix.fputc
import platform.posix.stderr

interface LoggingContext {

    /**
     * Whether logging is enabled for the current [CompilerPhase] by the `-Xverbose-phases` CLI option.
     */
    var inVerbosePhase: Boolean

    /**
     * If [inVerbosePhase] is `true`, prints [message] to the standard error stream.
     */
    @OptIn(ExperimentalForeignApi::class)
    fun log(message: () -> String) {
        if (inVerbosePhase) {
            val text = message()
            for (part in text.split('\u0000').withIndex()) {
                if (part.index != 0) fputc(0, stderr)
                fputs(part.value, stderr)
            }
            fputc(10, stderr)
        }
    }
}
