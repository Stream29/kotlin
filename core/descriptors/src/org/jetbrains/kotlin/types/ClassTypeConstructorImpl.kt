/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.types

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.SupertypeLoopChecker
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.storage.StorageManager

// Translated-unverified.
open class ClassTypeConstructorImpl(
    private val classDescriptor: ClassDescriptor,
    parameters: List<TypeParameterDescriptor>,
    supertypes: Collection<KotlinType>,
    storageManager: StorageManager
) : AbstractClassTypeConstructor(storageManager), TypeConstructor {
    private val constructorParameters = object : AbstractList<TypeParameterDescriptor>() {
        private val snapshot = ArrayList(parameters)
        override val size: Int get() = snapshot.size
        override fun get(index: Int): TypeParameterDescriptor = snapshot[index]
    }
    // A read-only live view, not a snapshot: matches unmodifiableCollection(supertypes).
    private val constructorSupertypes = object : AbstractCollection<KotlinType>() {
        override val size: Int get() = supertypes.size
        override fun iterator(): Iterator<KotlinType> = object : Iterator<KotlinType> {
            private val delegate = supertypes.iterator()
            override fun hasNext(): Boolean = delegate.hasNext()
            override fun next(): KotlinType = delegate.next()
        }
    }
    override fun getParameters(): List<TypeParameterDescriptor> = constructorParameters
    override fun toString(): String = DescriptorUtils.getFqName(classDescriptor).asString()
    override fun isDenotable(): Boolean = true
    override fun getDeclarationDescriptor(): ClassDescriptor = classDescriptor
    override fun computeSupertypes(): Collection<KotlinType> = constructorSupertypes
    override val supertypeLoopChecker: SupertypeLoopChecker get() = SupertypeLoopChecker.EMPTY
}
