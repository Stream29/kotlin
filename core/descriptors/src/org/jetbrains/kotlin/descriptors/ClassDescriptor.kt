/*
 * Copyright 2000-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin.descriptors

import org.jetbrains.kotlin.mpp.RegularClassSymbolMarker
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.types.SimpleType
import org.jetbrains.kotlin.types.TypeProjection
import org.jetbrains.kotlin.types.TypeSubstitution

// Translated-unverified.
interface ClassDescriptor : ClassifierDescriptorWithTypeParameters, ClassOrPackageFragmentDescriptor, RegularClassSymbolMarker {
    fun getMemberScope(typeArguments: List<TypeProjection>): MemberScope
    fun getMemberScope(typeSubstitution: TypeSubstitution): MemberScope
    fun getUnsubstitutedMemberScope(): MemberScope
    fun getUnsubstitutedInnerClassesScope(): MemberScope
    fun getStaticScope(): MemberScope
    fun getConstructors(): Collection<ClassConstructorDescriptor>
    override fun getContainingDeclaration(): DeclarationDescriptor
    override fun getDefaultType(): SimpleType
    fun getCompanionObjectDescriptor(): ClassDescriptor?
    fun getKind(): ClassKind
    override fun getModality(): Modality
    override fun getVisibility(): DescriptorVisibility
    fun isCompanionObject(): Boolean
    fun isData(): Boolean
    fun isInline(): Boolean
    fun isFun(): Boolean
    fun isValue(): Boolean
    fun getThisAsReceiverParameter(): ReceiverParameterDescriptor
    fun getContextReceivers(): List<ReceiverParameterDescriptor>
    fun getUnsubstitutedPrimaryConstructor(): ClassConstructorDescriptor?
    /** Unlike constructor parameters, excludes parameters captured from an outer declaration. */
    override fun getDeclaredTypeParameters(): List<TypeParameterDescriptor>
    fun getSealedSubclasses(): Collection<ClassDescriptor>
    fun getValueClassRepresentation(): ValueClassRepresentation<SimpleType>?
    override fun getOriginal(): ClassDescriptor
    fun getDefaultFunctionTypeForSamInterface(): SimpleType?
    /** A fast negative check, not an exact SAM classification. */
    fun isDefinitelyNotSamInterface(): Boolean
}

val ClassDescriptor.unsubstitutedMemberScope: MemberScope get() = getUnsubstitutedMemberScope()
val ClassDescriptor.unsubstitutedInnerClassesScope: MemberScope get() = getUnsubstitutedInnerClassesScope()
val ClassDescriptor.staticScope: MemberScope get() = getStaticScope()
val ClassDescriptor.constructors: Collection<ClassConstructorDescriptor> get() = getConstructors()
val ClassDescriptor.companionObjectDescriptor: ClassDescriptor? get() = getCompanionObjectDescriptor()
val ClassDescriptor.kind: ClassKind get() = getKind()
val ClassDescriptor.isCompanionObject: Boolean get() = isCompanionObject()
val ClassDescriptor.isData: Boolean get() = isData()
val ClassDescriptor.isInline: Boolean get() = isInline()
val ClassDescriptor.isFun: Boolean get() = isFun()
val ClassDescriptor.isValue: Boolean get() = isValue()
val ClassDescriptor.thisAsReceiverParameter: ReceiverParameterDescriptor get() = getThisAsReceiverParameter()
val ClassDescriptor.contextReceivers: List<ReceiverParameterDescriptor> get() = getContextReceivers()
val ClassDescriptor.unsubstitutedPrimaryConstructor: ClassConstructorDescriptor? get() = getUnsubstitutedPrimaryConstructor()
val ClassDescriptor.sealedSubclasses: Collection<ClassDescriptor> get() = getSealedSubclasses()
val ClassDescriptor.valueClassRepresentation: ValueClassRepresentation<SimpleType>? get() = getValueClassRepresentation()
val ClassDescriptor.original: ClassDescriptor get() = getOriginal()
val ClassDescriptor.defaultFunctionTypeForSamInterface: SimpleType? get() = getDefaultFunctionTypeForSamInterface()
val ClassDescriptor.isDefinitelyNotSamInterface: Boolean get() = isDefinitelyNotSamInterface()
