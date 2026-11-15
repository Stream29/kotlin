/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.descriptors.impl

import org.jetbrains.kotlin.descriptors.*

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.constants.ConstantValue
import org.jetbrains.kotlin.storage.NullableLazyValue
import org.jetbrains.kotlin.types.KotlinType

// Translated-unverified.
abstract class VariableDescriptorWithInitializerImpl(
    containingDeclaration: DeclarationDescriptor, annotations: Annotations, name: Name,
    outType: KotlinType?, isVar: Boolean, source: SourceElement
) : VariableDescriptorImpl(containingDeclaration, annotations, name, outType, source) {
    private val mutable = isVar
    protected var compileTimeInitializer: NullableLazyValue<ConstantValue<*>>? = null
    protected var compileTimeInitializerFactory: (() -> NullableLazyValue<ConstantValue<*>>)? = null
    override fun isVar(): Boolean = mutable
    override fun getCompileTimeInitializer(): ConstantValue<*>? = compileTimeInitializer?.invoke()
    open fun setCompileTimeInitializerFactory(compileTimeInitializerFactory: () -> NullableLazyValue<ConstantValue<*>>) {
        assert(!isVar()) { "Constant value for variable initializer should be recorded only for final variables: ${getName()}" }
        setCompileTimeInitializer(null, compileTimeInitializerFactory)
    }
    open fun setCompileTimeInitializer(
        compileTimeInitializer: NullableLazyValue<ConstantValue<*>>?,
        compileTimeInitializerFactory: () -> NullableLazyValue<ConstantValue<*>>
    ) {
        assert(!isVar()) { "Constant value for variable initializer should be recorded only for final variables: ${getName()}" }
        this.compileTimeInitializerFactory = compileTimeInitializerFactory
        this.compileTimeInitializer = compileTimeInitializer ?: compileTimeInitializerFactory()
    }
    override fun cleanCompileTimeInitializerCache() {
        compileTimeInitializer = compileTimeInitializerFactory!!()
    }
}
