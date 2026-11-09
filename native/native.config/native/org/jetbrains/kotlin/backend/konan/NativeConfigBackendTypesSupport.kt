/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("PackageDirectoryMismatch")

package org.jetbrains.kotlin.backend.konan

import org.jetbrains.kotlin.konan.file.File
import org.jetbrains.kotlin.konan.file.absolutePath
import org.jetbrains.kotlin.konan.target.HostManager

enum class AllocationMode {
    STD,
    CUSTOM
}

enum class TestRunnerKind {
    NONE,
    MAIN_THREAD,
    WORKER,
    MAIN_THREAD_NO_EXIT
}

sealed class LlvmVariant {
    object User : LlvmVariant() {
        override fun getKonanPropertiesEntry(): Pair<String, String> =
            konanPropertiesKey to "\$llvm.${HostManager.hostName}.user"
    }

    object Dev : LlvmVariant() {
        override fun getKonanPropertiesEntry(): Pair<String, String> =
            konanPropertiesKey to "\$llvm.${HostManager.hostName}.dev"
    }

    object DevWithAsserts : LlvmVariant() {
        override fun getKonanPropertiesEntry(): Pair<String, String> =
            konanPropertiesKey to "\$llvm.${HostManager.hostName}.dev-with-asserts"
    }

    class Custom(val path: File) : LlvmVariant() {
        override fun getKonanPropertiesEntry(): Pair<String, String> =
            konanPropertiesKey to path.absolutePath
    }

    abstract fun getKonanPropertiesEntry(): Pair<String, String>

    companion object {
        private val konanPropertiesKey: String by lazy {
            "llvmHome.${HostManager.hostName}"
        }
    }
}
