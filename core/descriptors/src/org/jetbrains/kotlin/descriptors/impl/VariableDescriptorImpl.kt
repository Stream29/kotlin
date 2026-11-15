/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.descriptors.impl

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.shouldBeUpdated

// Translated-unverified.
abstract class VariableDescriptorImpl(
    containingDeclaration: DeclarationDescriptor, annotations: Annotations, name: Name,
    protected var outType: KotlinType?, source: SourceElement
) : DeclarationDescriptorNonRootImpl(containingDeclaration, annotations, name, source), VariableDescriptor {
    override fun getType(): KotlinType = outType!!
    open fun setOutType(outType: KotlinType?) {
        assert(this.outType == null || this.outType!!.shouldBeUpdated())
        this.outType = outType
    }
    override fun getOriginal(): VariableDescriptor = super.getOriginal() as VariableDescriptor
    override fun getValueParameters(): List<ValueParameterDescriptor> = emptyList()
    override fun hasStableParameterNames(): Boolean = false
    override fun hasSynthesizedParameterNames(): Boolean = false
    override fun getOverriddenDescriptors(): Collection<CallableDescriptor> = emptySet()
    override fun getTypeParameters(): List<TypeParameterDescriptor> = emptyList()
    override fun getContextReceiverParameters(): List<ReceiverParameterDescriptor> = emptyList()
    override fun getExtensionReceiverParameter(): ReceiverParameterDescriptor? = null
    override fun getDispatchReceiverParameter(): ReceiverParameterDescriptor? = null
    override fun getReturnType(): KotlinType = getType()
    override fun isConst(): Boolean = false
    override fun <V> getUserData(key: CallableDescriptor.UserDataKey<V>): V? = null
}

val VariableDescriptorImpl.original: VariableDescriptor get() = getOriginal()
val VariableDescriptorImpl.returnType: KotlinType get() = getReturnType()
