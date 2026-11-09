/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.descriptors

import org.jetbrains.kotlin.descriptors.annotations.Annotated
import org.jetbrains.kotlin.mpp.DeclarationSymbolMarker

// Translated-unverified. Methods remain the virtual contract; extensions are one-way aliases.
interface DeclarationDescriptor : Annotated, Named, ValidateableDescriptor, DeclarationSymbolMarker {
    /** The original declaration, or this descriptor when it has not been substituted. */
    fun getOriginal(): DeclarationDescriptor
    fun getContainingDeclaration(): DeclarationDescriptor?
    fun <R, D> accept(visitor: DeclarationDescriptorVisitor<R, D>, data: D): R?
    fun acceptVoid(visitor: DeclarationDescriptorVisitor<Nothing?, Nothing?>)
}

val DeclarationDescriptor.original: DeclarationDescriptor get() = getOriginal()
val DeclarationDescriptor.containingDeclaration: DeclarationDescriptor? get() = getContainingDeclaration()
