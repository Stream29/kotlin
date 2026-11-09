/*
 * Copyright 2010-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin.descriptors

import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.mpp.FunctionSymbolMarker
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeSubstitution
import org.jetbrains.kotlin.types.TypeSubstitutor

// Translated-unverified.
interface FunctionDescriptor : CallableMemberDescriptor, FunctionSymbolMarker {
    override fun getContainingDeclaration(): DeclarationDescriptor
    override fun getOriginal(): FunctionDescriptor
    override fun substitute(substitutor: TypeSubstitutor): FunctionDescriptor?
    /** May force lazy substitution; prefer original.overriddenDescriptors when sufficient. */
    override fun getOverriddenDescriptors(): Collection<FunctionDescriptor>
    fun getInitialSignatureDescriptor(): FunctionDescriptor?
    fun isHiddenToOvercomeSignatureClash(): Boolean
    override fun copy(newOwner: DeclarationDescriptor, modality: Modality, visibility: DescriptorVisibility,
                      kind: CallableMemberDescriptor.Kind, copyOverrides: Boolean): FunctionDescriptor
    fun isOperator(): Boolean
    fun isInfix(): Boolean
    fun isInline(): Boolean
    fun isTailrec(): Boolean
    fun isHiddenForResolutionEverywhereBesideSupercalls(): Boolean
    fun isSuspend(): Boolean
    override fun newCopyBuilder(): CopyBuilder<out FunctionDescriptor?>

    interface CopyBuilder<D : FunctionDescriptor?> : CallableMemberDescriptor.CopyBuilder<D> {
        override fun setOwner(owner: DeclarationDescriptor): CopyBuilder<D>
        override fun setModality(modality: Modality): CopyBuilder<D>
        override fun setVisibility(visibility: DescriptorVisibility): CopyBuilder<D>
        override fun setKind(kind: CallableMemberDescriptor.Kind): CopyBuilder<D>
        override fun setCopyOverrides(copyOverrides: Boolean): CopyBuilder<D>
        override fun setName(name: Name): CopyBuilder<D>
        fun setValueParameters(parameters: List<ValueParameterDescriptor>): CopyBuilder<D>
        override fun setTypeParameters(parameters: List<TypeParameterDescriptor>): CopyBuilder<D>
        override fun setReturnType(type: KotlinType): CopyBuilder<D>
        fun setContextReceiverParameters(contextReceiverParameters: List<ReceiverParameterDescriptor>): CopyBuilder<D>
        fun setExtensionReceiverParameter(extensionReceiverParameter: ReceiverParameterDescriptor?): CopyBuilder<D>
        override fun setDispatchReceiverParameter(dispatchReceiverParameter: ReceiverParameterDescriptor?): CopyBuilder<D>
        override fun setOriginal(original: CallableMemberDescriptor?): CopyBuilder<D>
        fun setSignatureChange(): CopyBuilder<D>
        override fun setPreserveSourceElement(): CopyBuilder<D>
        fun setDropOriginalInContainingParts(): CopyBuilder<D>
        fun setHiddenToOvercomeSignatureClash(): CopyBuilder<D>
        fun setHiddenForResolutionEverywhereBesideSupercalls(): CopyBuilder<D>
        fun setAdditionalAnnotations(additionalAnnotations: Annotations): CopyBuilder<D>
        override fun setSubstitution(substitution: TypeSubstitution): CopyBuilder<D>
        fun <V> putUserData(userDataKey: CallableDescriptor.UserDataKey<V>, value: V): CopyBuilder<D>
        override fun build(): D?
    }
}

val FunctionDescriptor.original: FunctionDescriptor get() = getOriginal()
val FunctionDescriptor.overriddenDescriptors: Collection<FunctionDescriptor> get() = getOverriddenDescriptors()
val FunctionDescriptor.initialSignatureDescriptor: FunctionDescriptor? get() = getInitialSignatureDescriptor()
val FunctionDescriptor.isHiddenToOvercomeSignatureClash: Boolean get() = isHiddenToOvercomeSignatureClash()
val FunctionDescriptor.isOperator: Boolean get() = isOperator()
val FunctionDescriptor.isInfix: Boolean get() = isInfix()
val FunctionDescriptor.isInline: Boolean get() = isInline()
val FunctionDescriptor.isTailrec: Boolean get() = isTailrec()
val FunctionDescriptor.isHiddenForResolutionEverywhereBesideSupercalls: Boolean get() = isHiddenForResolutionEverywhereBesideSupercalls()
val FunctionDescriptor.isSuspend: Boolean get() = isSuspend()
