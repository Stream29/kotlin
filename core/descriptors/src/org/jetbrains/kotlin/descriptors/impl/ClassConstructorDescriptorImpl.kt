/*
 * Copyright 2010-2017 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.descriptors.impl

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor.Kind
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeSubstitutor

// Translated-unverified.
open class ClassConstructorDescriptorImpl protected constructor(
    containingDeclaration: ClassDescriptor, original: ConstructorDescriptor?, annotations: Annotations,
    protected val primary: Boolean, kind: Kind, source: SourceElement
) : FunctionDescriptorImpl(containingDeclaration, original, annotations, SpecialNames.INIT, kind, source), ClassConstructorDescriptor {
    open fun initialize(unsubstitutedValueParameters: List<ValueParameterDescriptor>, visibility: DescriptorVisibility,
                        typeParameterDescriptors: List<TypeParameterDescriptor>): ClassConstructorDescriptorImpl {
        super.initialize(null, calculateDispatchReceiverParameter(), calculateContextReceiverParameters(),
                         typeParameterDescriptors, unsubstitutedValueParameters, null, Modality.FINAL, visibility)
        return this
    }
    open fun initialize(unsubstitutedValueParameters: List<ValueParameterDescriptor>, visibility: DescriptorVisibility): ClassConstructorDescriptorImpl {
        initialize(unsubstitutedValueParameters, visibility, getContainingDeclaration().getDeclaredTypeParameters())
        return this
    }
    open fun calculateDispatchReceiverParameter(): ReceiverParameterDescriptor? {
        val classDescriptor = getContainingDeclaration()
        if (classDescriptor.isInner()) {
            val classContainer = classDescriptor.getContainingDeclaration()
            if (classContainer is ClassDescriptor) return classContainer.getThisAsReceiverParameter()
        }
        return null
    }
    private fun calculateContextReceiverParameters(): List<ReceiverParameterDescriptor> {
        val classDescriptor = getContainingDeclaration()
        if (classDescriptor.getContextReceivers().isNotEmpty()) return classDescriptor.getContextReceivers()
        return emptyList()
    }
    override fun getContainingDeclaration(): ClassDescriptor = super.getContainingDeclaration() as ClassDescriptor
    override fun getReturnType(): KotlinType = super.getReturnType()!!
    override fun getConstructedClass(): ClassDescriptor = getContainingDeclaration()
    override fun getOriginal(): ClassConstructorDescriptor = super.getOriginal() as ClassConstructorDescriptor
    override fun substitute(substitutor: TypeSubstitutor): ClassConstructorDescriptor? = super.substitute(substitutor) as ClassConstructorDescriptor?
    override fun <R, D> accept(visitor: DeclarationDescriptorVisitor<R, D>, data: D): R? = visitor.visitConstructorDescriptor(this, data)
    override fun isPrimary(): Boolean = primary
    override fun getOverriddenDescriptors(): Collection<FunctionDescriptor> = emptySet()
    override fun setOverriddenDescriptors(overriddenDescriptors: Collection<CallableMemberDescriptor?>) {
        assert(overriddenDescriptors.isEmpty()) { "Constructors cannot override anything" }
    }
    override fun createSubstitutedCopy(
        newOwner: DeclarationDescriptor, original: FunctionDescriptor?, kind: Kind, newName: Name?,
        annotations: Annotations, source: SourceElement
    ): ClassConstructorDescriptorImpl {
        if (kind != Kind.DECLARATION && kind != Kind.SYNTHESIZED)
            throw IllegalStateException("Attempt at creating a constructor that is not a declaration: \ncopy from: $this\nnewOwner: $newOwner\nkind: $kind")
        assert(newName == null) { "Attempt to rename constructor: $this" }
        return ClassConstructorDescriptorImpl(newOwner as ClassDescriptor, this, annotations, primary, Kind.DECLARATION, source)
    }
    override fun copy(newOwner: DeclarationDescriptor, modality: Modality, visibility: DescriptorVisibility, kind: Kind, copyOverrides: Boolean): ClassConstructorDescriptor =
        super.copy(newOwner, modality, visibility, kind, copyOverrides) as ClassConstructorDescriptor
    companion object {
        fun create(containingDeclaration: ClassDescriptor, annotations: Annotations, isPrimary: Boolean, source: SourceElement): ClassConstructorDescriptorImpl =
            ClassConstructorDescriptorImpl(containingDeclaration, null, annotations, isPrimary, Kind.DECLARATION, source)
        fun createSynthesized(containingDeclaration: ClassDescriptor, annotations: Annotations, isPrimary: Boolean, source: SourceElement): ClassConstructorDescriptorImpl =
            ClassConstructorDescriptorImpl(containingDeclaration, null, annotations, isPrimary, Kind.SYNTHESIZED, source)
    }
}

var ClassConstructorDescriptorImpl.returnType: KotlinType
    get() = getReturnType()
    set(value) = setReturnType(value)
