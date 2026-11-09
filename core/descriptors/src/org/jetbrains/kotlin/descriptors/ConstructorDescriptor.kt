/*
 * Copyright 2010-2017 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.descriptors

import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeSubstitutor

// Translated-unverified.
interface ConstructorDescriptor : FunctionDescriptor {
    override fun getTypeParameters(): List<TypeParameterDescriptor>
    override fun getReturnType(): KotlinType
    override fun getContainingDeclaration(): ClassifierDescriptorWithTypeParameters
    fun getConstructedClass(): ClassDescriptor
    override fun getOriginal(): ConstructorDescriptor
    override fun substitute(substitutor: TypeSubstitutor): ConstructorDescriptor?
    override fun getName(): Name
    override fun copy(newOwner: DeclarationDescriptor, modality: Modality, visibility: DescriptorVisibility,
                      kind: CallableMemberDescriptor.Kind, copyOverrides: Boolean): ConstructorDescriptor
    fun isPrimary(): Boolean
}

val ConstructorDescriptor.returnType: KotlinType get() = getReturnType()
val ConstructorDescriptor.containingDeclaration: ClassifierDescriptorWithTypeParameters get() = getContainingDeclaration()
val ConstructorDescriptor.constructedClass: ClassDescriptor get() = getConstructedClass()
val ConstructorDescriptor.original: ConstructorDescriptor get() = getOriginal()
val ConstructorDescriptor.isPrimary: Boolean get() = isPrimary()
