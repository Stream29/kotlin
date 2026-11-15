/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.lazy

import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclaration

interface AbstractFir2IrLazyDeclaration<out F : Any> : IrDeclaration {
    val fir: F
    val containerSourceElement: SourceElement?
        get() = null
}

abstract class Fir2IrLazyClass : IrClass(), AbstractFir2IrLazyDeclaration<Any>
