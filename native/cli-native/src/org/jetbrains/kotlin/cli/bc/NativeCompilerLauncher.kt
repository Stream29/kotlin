/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin.cli.bc

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import org.jetbrains.kotlin.cli.utilities.main as cliUtilitiesMain
import platform.posix.getenv

@OptIn(kotlin.native.runtime.NativeRuntimeApi::class, ExperimentalForeignApi::class)
fun configureNativeCompilerGc(lowMemMode: Boolean = false) {
    val preset = getenv("KONAN_GC_PRESET")?.toKString()
    if (preset == "stock") return
    if (preset == "lowmem" || lowMemMode) {
        kotlin.native.runtime.GC.targetHeapUtilization = 0.60
        kotlin.native.runtime.GC.heapTriggerCoefficient = 0.90
        kotlin.native.runtime.GC.minHeapBytes = 64L * 1024 * 1024
        kotlin.native.runtime.GC.targetHeapBytes = 128L * 1024 * 1024
        kotlin.native.runtime.GC.pauseOnTargetHeapOverflow = false
    } else {
        kotlin.native.runtime.GC.targetHeapUtilization = 0.35
        kotlin.native.runtime.GC.heapTriggerCoefficient = 0.90
        kotlin.native.runtime.GC.minHeapBytes = 256L * 1024 * 1024
        kotlin.native.runtime.GC.targetHeapBytes = 512L * 1024 * 1024
        kotlin.native.runtime.GC.pauseOnTargetHeapOverflow = false
    }
    getenv("KONAN_GC_UTIL")?.toKString()?.toDoubleOrNull()?.let {
        kotlin.native.runtime.GC.targetHeapUtilization = it
    }
    getenv("KONAN_GC_TRIGGER")?.toKString()?.toDoubleOrNull()?.let {
        kotlin.native.runtime.GC.heapTriggerCoefficient = it
    }
    getenv("KONAN_GC_MIN_MB")?.toKString()?.toLongOrNull()?.let {
        kotlin.native.runtime.GC.minHeapBytes = it * 1024L * 1024L
    }
    getenv("KONAN_GC_TARGET_MB")?.toKString()?.toLongOrNull()?.let {
        kotlin.native.runtime.GC.targetHeapBytes = it * 1024L * 1024L
    }
}

fun nativeCompilerCliMain(args: Array<String>) {
    configureNativeCompilerGc()
    cliUtilitiesMain(args)
}
