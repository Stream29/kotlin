/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlinx.cinterop

const val callbacksLibName = "callbacks"

inline fun <T> usingJvmCInteropCallbacks(konanHome: String? = null, block: () -> T): T = block()

fun loadKonanLibrary(name: String, konanHome: String? = null) {}
