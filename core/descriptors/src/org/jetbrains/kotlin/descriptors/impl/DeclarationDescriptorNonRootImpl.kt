/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.descriptors.impl

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.name.Name

// Translated-unverified.
abstract class DeclarationDescriptorNonRootImpl protected constructor(
    containingDeclaration: DeclarationDescriptor, annotations: Annotations, name: Name, source: SourceElement
) : DeclarationDescriptorImpl(annotations, name), DeclarationDescriptorNonRoot {
    private val containingDescriptor = containingDeclaration
    private val sourceElement = source
    override fun getOriginal(): DeclarationDescriptorWithSource = super.getOriginal() as DeclarationDescriptorWithSource
    override fun getContainingDeclaration(): DeclarationDescriptor = containingDescriptor
    override fun getSource(): SourceElement = sourceElement
    override fun validate() {
        containingDescriptor.validate()
    }
}
