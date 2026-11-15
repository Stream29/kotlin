/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.descriptors.impl

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor.Kind
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
import org.jetbrains.kotlin.types.KotlinType

// Translated-unverified.
open class PropertySetterDescriptorImpl(
    correspondingProperty: PropertyDescriptor, annotations: Annotations, modality: Modality, visibility: DescriptorVisibility,
    isDefault: Boolean, isExternal: Boolean, isInline: Boolean, kind: Kind, original: PropertySetterDescriptor?, source: SourceElement
) : PropertyAccessorDescriptorImpl(modality, visibility, correspondingProperty, annotations,
    Name.special("<set-${correspondingProperty.getName()}>"), isDefault, isExternal, isInline, kind, source), PropertySetterDescriptor {
    private var parameter: ValueParameterDescriptor? = null
    private val originalDescriptor = original ?: this
    open fun initialize(parameter: ValueParameterDescriptor) {
        assert(this.parameter == null)
        this.parameter = parameter
    }
    open fun initializeDefault() { initialize(createSetterParameter(this, getCorrespondingProperty().getType(), Annotations.EMPTY)) }
    @Suppress("UNCHECKED_CAST")
    override fun getOverriddenDescriptors(): Collection<PropertySetterDescriptor> =
        super.getOverriddenDescriptors(false) as Collection<PropertySetterDescriptor>
    override fun getValueParameters(): List<ValueParameterDescriptor> = listOf(parameter ?: throw IllegalStateException())
    override fun getReturnType(): KotlinType = builtIns.getUnitType()
    override fun <R, D> accept(visitor: DeclarationDescriptorVisitor<R, D>, data: D): R? = visitor.visitPropertySetterDescriptor(this, data)
    override fun getOriginal(): PropertySetterDescriptor = originalDescriptor
    companion object {
        fun createSetterParameter(setterDescriptor: PropertySetterDescriptor, type: KotlinType, annotations: Annotations): ValueParameterDescriptorImpl =
            ValueParameterDescriptorImpl(setterDescriptor, null, 0, annotations, SpecialNames.IMPLICIT_SET_PARAMETER, type,
                                         false, false, false, null, SourceElement.NO_SOURCE)
    }
}
