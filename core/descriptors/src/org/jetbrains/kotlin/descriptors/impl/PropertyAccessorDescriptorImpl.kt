/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.descriptors.impl

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor.Kind
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.TypeSubstitutor

// Translated-unverified.
abstract class PropertyAccessorDescriptorImpl(
    modality: Modality, visibility: DescriptorVisibility, correspondingProperty: PropertyDescriptor,
    annotations: Annotations, name: Name, isDefault: Boolean, isExternal: Boolean, isInline: Boolean,
    kind: Kind, source: SourceElement
) : DeclarationDescriptorNonRootImpl(correspondingProperty.getContainingDeclaration(), annotations, name, source), PropertyAccessorDescriptor {
    private var defaultFlag = isDefault
    private val externalFlag = isExternal
    private val accessorModality = modality
    private val property = correspondingProperty
    private val inlineFlag = isInline
    private val accessorKind = kind
    private var accessorVisibility = visibility
    private var initialSignature: FunctionDescriptor? = null
    override fun isDefault(): Boolean = defaultFlag
    open fun setDefault(aDefault: Boolean) { defaultFlag = aDefault }
    override fun getKind(): Kind = accessorKind
    override fun isOperator(): Boolean = false
    override fun isInfix(): Boolean = false
    override fun isExternal(): Boolean = externalFlag
    override fun isInline(): Boolean = inlineFlag
    override fun isTailrec(): Boolean = false
    override fun isSuspend(): Boolean = false
    override fun isExpect(): Boolean = false
    override fun isActual(): Boolean = false
    // Upstream uses accessor originals in the backend; copying belongs to the property.
    override fun substitute(substitutor: TypeSubstitutor): FunctionDescriptor = this
    override fun getTypeParameters(): List<TypeParameterDescriptor> = emptyList()
    override fun hasStableParameterNames(): Boolean = false
    override fun hasSynthesizedParameterNames(): Boolean = false
    override fun getModality(): Modality = accessorModality
    override fun getVisibility(): DescriptorVisibility = accessorVisibility
    open fun setVisibility(visibility: DescriptorVisibility) { accessorVisibility = visibility }
    override val correspondingVariable: VariableDescriptorWithAccessors get() = property
    override fun getCorrespondingProperty(): PropertyDescriptor = property
    override fun getContextReceiverParameters(): List<ReceiverParameterDescriptor> = getCorrespondingProperty().getContextReceiverParameters()
    override fun getExtensionReceiverParameter(): ReceiverParameterDescriptor? = getCorrespondingProperty().getExtensionReceiverParameter()
    override fun getDispatchReceiverParameter(): ReceiverParameterDescriptor? = getCorrespondingProperty().getDispatchReceiverParameter()
    override fun newCopyBuilder(): FunctionDescriptor.CopyBuilder<out FunctionDescriptor> =
        throw UnsupportedOperationException("Accessors must be copied by the corresponding property")
    override fun copy(newOwner: DeclarationDescriptor, modality: Modality, visibility: DescriptorVisibility, kind: Kind, copyOverrides: Boolean): PropertyAccessorDescriptor =
        throw UnsupportedOperationException("Accessors must be copied by the corresponding property")
    protected open fun getOverriddenDescriptors(isGetter: Boolean): Collection<PropertyAccessorDescriptor> {
        val result = ArrayList<PropertyAccessorDescriptor>(0)
        for (overriddenProperty in getCorrespondingProperty().getOverriddenDescriptors()) {
            val accessor = if (isGetter) overriddenProperty.getGetter() else overriddenProperty.getSetter()
            if (accessor != null) result.add(accessor)
        }
        return result
    }
    override fun setOverriddenDescriptors(overriddenDescriptors: Collection<CallableMemberDescriptor?>) {
        assert(overriddenDescriptors.isEmpty()) { "Overridden accessors should be empty" }
    }
    abstract override fun getOriginal(): PropertyAccessorDescriptor
    override fun getInitialSignatureDescriptor(): FunctionDescriptor? = initialSignature
    open fun setInitialSignatureDescriptor(initialSignatureDescriptor: FunctionDescriptor?) { initialSignature = initialSignatureDescriptor }
    override fun isHiddenToOvercomeSignatureClash(): Boolean = false
    override fun isHiddenForResolutionEverywhereBesideSupercalls(): Boolean = false
    override fun <V> getUserData(key: CallableDescriptor.UserDataKey<V>): V? = null
}

var PropertyAccessorDescriptorImpl.isDefault: Boolean get() = isDefault(); set(value) = setDefault(value)
var PropertyAccessorDescriptorImpl.visibility: DescriptorVisibility get() = getVisibility(); set(value) = setVisibility(value)
var PropertyAccessorDescriptorImpl.initialSignatureDescriptor: FunctionDescriptor?
    get() = getInitialSignatureDescriptor()
    set(value) = setInitialSignatureDescriptor(value)
