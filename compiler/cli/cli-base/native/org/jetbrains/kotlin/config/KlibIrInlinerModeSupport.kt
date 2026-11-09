/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.config

enum class KlibIrInlinerMode(val modeState: String) {
    INTRA_MODULE(modeState = "intra-module"),
    FULL(modeState = "full"),
    DISABLED(modeState = "disabled"),
    DEFAULT(modeState = "default");

    companion object {
        fun fromString(string: String?): KlibIrInlinerMode? =
            entries.find { it.modeState == string }

        fun availableValues(): String =
            entries.joinToString(prefix = "{", postfix = "}") { it.modeState }
    }
}
