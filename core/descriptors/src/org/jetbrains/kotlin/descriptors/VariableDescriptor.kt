/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.descriptors

import org.jetbrains.kotlin.resolve.constants.ConstantValue
import org.jetbrains.kotlin.types.TypeSubstitutor

// Translated-unverified.
interface VariableDescriptor : ValueDescriptor {
    override fun substitute(substitutor: TypeSubstitutor): VariableDescriptor?
    fun isVar(): Boolean
    fun getCompileTimeInitializer(): ConstantValue<*>?
    /** IDE-only cache maintenance. */
    fun cleanCompileTimeInitializerCache()
    /** Const flags do not guarantee a non-null compile-time initializer. */
    fun isConst(): Boolean
    fun isLateInit(): Boolean
}

val VariableDescriptor.isVar: Boolean get() = isVar()
val VariableDescriptor.compileTimeInitializer: ConstantValue<*>? get() = getCompileTimeInitializer()
val VariableDescriptor.isConst: Boolean get() = isConst()
val VariableDescriptor.isLateInit: Boolean get() = isLateInit()
