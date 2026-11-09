/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.descriptors

// Translated-unverified.
interface PropertyAccessorDescriptor : VariableAccessorDescriptor {
    fun isDefault(): Boolean
    override fun getOriginal(): PropertyAccessorDescriptor
    override fun getOverriddenDescriptors(): Collection<PropertyAccessorDescriptor>
    fun getCorrespondingProperty(): PropertyDescriptor
    override fun copy(newOwner: DeclarationDescriptor, modality: Modality, visibility: DescriptorVisibility,
                      kind: CallableMemberDescriptor.Kind, copyOverrides: Boolean): PropertyAccessorDescriptor
}

val PropertyAccessorDescriptor.isDefault: Boolean get() = isDefault()
val PropertyAccessorDescriptor.original: PropertyAccessorDescriptor get() = getOriginal()
val PropertyAccessorDescriptor.overriddenDescriptors: Collection<PropertyAccessorDescriptor> get() = getOverriddenDescriptors()
val PropertyAccessorDescriptor.correspondingProperty: PropertyDescriptor get() = getCorrespondingProperty()
