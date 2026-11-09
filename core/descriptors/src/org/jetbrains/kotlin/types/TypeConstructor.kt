/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.types

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.types.checker.KotlinTypeRefiner
import org.jetbrains.kotlin.types.model.TypeConstructorMarker

// Translated-unverified.
interface TypeConstructor : TypeConstructorMarker {
    /** Includes parameters captured from outer declarations of an inner class. */
    fun getParameters(): List<TypeParameterDescriptor>
    fun getSupertypes(): Collection<KotlinType>
    fun isFinal(): Boolean
    /** False for internal-only types such as intersections. */
    fun isDenotable(): Boolean
    fun getDeclarationDescriptor(): ClassifierDescriptor?
    fun getBuiltIns(): KotlinBuiltIns
    /** Refines existing supertypes; does not add platform-declared supertypes. */
    @TypeRefinement
    fun refine(kotlinTypeRefiner: KotlinTypeRefiner): TypeConstructor
}

val TypeConstructor.parameters: List<TypeParameterDescriptor> get() = getParameters()
val TypeConstructor.supertypes: Collection<KotlinType> get() = getSupertypes()
val TypeConstructor.isFinal: Boolean get() = isFinal()
val TypeConstructor.isDenotable: Boolean get() = isDenotable()
val TypeConstructor.declarationDescriptor: ClassifierDescriptor? get() = getDeclarationDescriptor()
val TypeConstructor.builtIns: KotlinBuiltIns get() = getBuiltIns()
