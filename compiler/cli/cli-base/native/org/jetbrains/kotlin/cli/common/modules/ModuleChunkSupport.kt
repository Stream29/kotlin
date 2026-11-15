/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("PackageDirectoryMismatch")

package org.jetbrains.kotlin.cli.common.modules

import org.jetbrains.kotlin.modules.Module

class ModuleChunk(val modules: List<Module>) {
    companion object {
        val EMPTY = ModuleChunk(emptyList())
    }
}
