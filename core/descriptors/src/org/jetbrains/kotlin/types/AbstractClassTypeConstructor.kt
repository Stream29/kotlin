/*
 * Copyright 2010-2016 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.types

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
import org.jetbrains.kotlin.storage.StorageManager

// Translated-unverified.
abstract class AbstractClassTypeConstructor(storageManager: StorageManager) : AbstractTypeConstructor(storageManager), TypeConstructor {
    abstract override fun getDeclarationDescriptor(): ClassDescriptor
    final override fun isFinal(): Boolean {
        val descriptor = getDeclarationDescriptor()
        return descriptor.isFinalClass && !descriptor.isExpect()
    }
    override fun getBuiltIns(): KotlinBuiltIns = getDeclarationDescriptor().builtIns
    override fun isSameClassifier(classifier: ClassifierDescriptor): Boolean =
        classifier is ClassDescriptor && areFqNamesEqual(getDeclarationDescriptor(), classifier)

    override fun getAdditionalNeighboursInSupertypeGraph(useCompanions: Boolean): Collection<KotlinType> {
        val containingDeclaration = getDeclarationDescriptor().getContainingDeclaration()
        if (containingDeclaration !is ClassDescriptor) return emptyList()
        val additionalNeighbours = ArrayList<KotlinType>()
        // Nested -> containing and host -> companion edges detect the visibility loops in KT-21515.
        additionalNeighbours.add(containingDeclaration.getDefaultType())
        val companion = containingDeclaration.getCompanionObjectDescriptor()
        if (useCompanions && companion != null) additionalNeighbours.add(companion.getDefaultType())
        return additionalNeighbours
    }

    override fun defaultSupertypeIfEmpty(): KotlinType? =
        if (KotlinBuiltIns.isSpecialClassWithNoSupertypes(getDeclarationDescriptor())) null
        else getBuiltIns().getAnyType()
}

val AbstractClassTypeConstructor.declarationDescriptor: ClassDescriptor get() = getDeclarationDescriptor()
