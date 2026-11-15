/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.descriptors

import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeSubstitution

// Translated-unverified.
interface CallableMemberDescriptor : CallableDescriptor, MemberDescriptor {
    override fun getOverriddenDescriptors(): Collection<CallableMemberDescriptor>
    override fun getOriginal(): CallableMemberDescriptor
    fun setOverriddenDescriptors(overriddenDescriptors: Collection<CallableMemberDescriptor?>)

    enum class Kind {
        DECLARATION, FAKE_OVERRIDE, DELEGATION, SYNTHESIZED;
        fun isReal(): Boolean = this != FAKE_OVERRIDE
    }

    fun getKind(): Kind
    fun copy(newOwner: DeclarationDescriptor, modality: Modality, visibility: DescriptorVisibility,
             kind: Kind, copyOverrides: Boolean): CallableMemberDescriptor
    fun newCopyBuilder(): CopyBuilder<out CallableMemberDescriptor?>

    interface CopyBuilder<D : CallableMemberDescriptor?> {
        fun setOwner(owner: DeclarationDescriptor): CopyBuilder<D>
        fun setModality(modality: Modality): CopyBuilder<D>
        fun setVisibility(visibility: DescriptorVisibility): CopyBuilder<D>
        fun setKind(kind: Kind): CopyBuilder<D>
        fun setTypeParameters(parameters: List<TypeParameterDescriptor>): CopyBuilder<D>
        fun setDispatchReceiverParameter(dispatchReceiverParameter: ReceiverParameterDescriptor?): CopyBuilder<D>
        fun setSubstitution(substitution: TypeSubstitution): CopyBuilder<D>
        fun setCopyOverrides(copyOverrides: Boolean): CopyBuilder<D>
        fun setName(name: Name): CopyBuilder<D>
        fun setOriginal(original: CallableMemberDescriptor?): CopyBuilder<D>
        fun setPreserveSourceElement(): CopyBuilder<D>
        fun setReturnType(type: KotlinType): CopyBuilder<D>
        fun build(): D?
    }
}

var CallableMemberDescriptor.overriddenDescriptors: Collection<CallableMemberDescriptor>
    get() = getOverriddenDescriptors()
    set(value) = setOverriddenDescriptors(value)
val CallableMemberDescriptor.original: CallableMemberDescriptor get() = getOriginal()
val CallableMemberDescriptor.kind: CallableMemberDescriptor.Kind get() = getKind()
val CallableMemberDescriptor.Kind.isReal: Boolean get() = isReal()
