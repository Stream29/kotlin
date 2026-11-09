/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.konan.util

import org.jetbrains.kotlin.konan.file.*
import org.jetbrains.kotlin.konan.target.Distribution
import org.jetbrains.kotlin.konan.target.KonanTarget

val Distribution.systemCacheRootDirectory: File
    get() = File(konanHome).child("klib/cache")

val Distribution.compilerFingerprint: String
    get() = File(konanHome).child("konan/compiler.fingerprint").let {
        if (it.exists) it.readBytes().decodeToString().trim() else "native-k2-2.3.20"
    }

fun Distribution.runtimeFingerprint(target: KonanTarget): String =
    File(konanHome).child("konan/targets/$target/runtime.fingerprint").let {
        if (it.exists) it.readBytes().decodeToString().trim() else "native-runtime-2.3.20"
    }