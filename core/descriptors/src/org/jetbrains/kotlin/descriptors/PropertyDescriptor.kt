/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.descriptors

import org.jetbrains.kotlin.mpp.PropertySymbolMarker
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeSubstitutor

// Translated-unverified. Kotlin-origin getter/setter properties retain their property contract.
interface PropertyDescriptor : VariableDescriptorWithAccessors, CallableMemberDescriptor, PropertySymbolMarker {
    fun getGetter(): PropertyGetterDescriptor?
    fun getSetter(): PropertySetterDescriptor?
    override val getter: PropertyGetterDescriptor? get() = getGetter()
    override val setter: PropertySetterDescriptor? get() = getSetter()
    /** For example, the setter of Tr<out String>.v: T is projected out. */
    fun isSetterProjectedOut(): Boolean
    fun getAccessors(): List<PropertyAccessorDescriptor>
    override fun getOriginal(): PropertyDescriptor
    override fun getOverriddenDescriptors(): Collection<PropertyDescriptor>
    fun getBackingField(): FieldDescriptor?
    fun getDelegateField(): FieldDescriptor?
    override fun substitute(substitutor: TypeSubstitutor): PropertyDescriptor?
    override fun newCopyBuilder(): CallableMemberDescriptor.CopyBuilder<out PropertyDescriptor>
    fun getInType(): KotlinType?
}

val PropertyDescriptor.isSetterProjectedOut: Boolean get() = isSetterProjectedOut()
val PropertyDescriptor.accessors: List<PropertyAccessorDescriptor> get() = getAccessors()
val PropertyDescriptor.original: PropertyDescriptor get() = getOriginal()
val PropertyDescriptor.overriddenDescriptors: Collection<PropertyDescriptor> get() = getOverriddenDescriptors()
val PropertyDescriptor.backingField: FieldDescriptor? get() = getBackingField()
val PropertyDescriptor.delegateField: FieldDescriptor? get() = getDelegateField()
val PropertyDescriptor.inType: KotlinType? get() = getInType()
