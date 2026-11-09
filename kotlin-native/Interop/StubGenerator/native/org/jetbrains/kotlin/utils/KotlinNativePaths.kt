/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import nativekotlin.host.files.NativeFile
import org.jetbrains.kotlin.konan.file.absoluteFile
import platform.posix.getenv

object KotlinNativePaths {
    @OptIn(ExperimentalForeignApi::class)
    val homePath: NativeFile
        get() {
            val raw = getenv("KONAN_HOME")?.toKString()?.takeIf { it.isNotEmpty() }
                ?: getenv("KOTLIN_NATIVE_HOME")?.toKString()?.takeIf { it.isNotEmpty() }
                ?: "."
            return NativeFile(raw).absoluteFile
        }
}
