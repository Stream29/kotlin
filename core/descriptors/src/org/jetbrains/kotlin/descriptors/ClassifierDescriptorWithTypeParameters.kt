/*
 * Copyright 2010-2016 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.descriptors

import org.jetbrains.kotlin.mpp.ClassLikeSymbolMarker
import org.jetbrains.kotlin.mpp.ClassifierSymbolMarker

// Translated-unverified.
interface ClassifierDescriptorWithTypeParameters :
    ClassifierDescriptor, DeclarationDescriptorWithVisibility, MemberDescriptor,
    Substitutable<ClassifierDescriptorWithTypeParameters>, ClassLikeSymbolMarker, ClassifierSymbolMarker {
    /** Whether this class captures its outer instance (unlike a static nested class). */
    fun isInner(): Boolean
    fun getDeclaredTypeParameters(): List<TypeParameterDescriptor>
}

val ClassifierDescriptorWithTypeParameters.isInner: Boolean get() = isInner()
val ClassifierDescriptorWithTypeParameters.declaredTypeParameters: List<TypeParameterDescriptor>
    get() = getDeclaredTypeParameters()
