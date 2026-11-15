/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.konan.library

import org.jetbrains.kotlin.library.Klib
import org.jetbrains.kotlin.library.klibFlag
import org.jetbrains.kotlin.library.loader.KlibLoader

/**
 * Indicates whether this [Klib] belongs to the Kotlin/Native distribution.
 */
var Klib.isFromKotlinNativeDistribution: Boolean by klibFlag()

/**
 * Indicates whether this [Klib] was implicitly loaded from the Kotlin/Native distribution.
 */
var Klib.isImplicitlyLoadedFromKotlinNativeDistribution: Boolean by klibFlag()

/**
 * Indicates whether this [Klib] is explicitly specified by the user in compiler's CLI arguments.
 * The opposite to [isImplicitlyLoadedFromKotlinNativeDistribution].
 */
val Klib.isExplicitlySpecifiedByUserInCLIArgument: Boolean
    get() = !isImplicitlyLoadedFromKotlinNativeDistribution
