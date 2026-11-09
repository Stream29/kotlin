/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.descriptors.impl

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.storage.StorageManager

// Translated-unverified.
abstract class ClassDescriptorBase protected constructor(
    storageManager: StorageManager, containingDeclaration: DeclarationDescriptor, name: Name,
    source: SourceElement, isExternal: Boolean
) : AbstractClassDescriptor(storageManager, name) {
    private val containingDescriptor = containingDeclaration
    private val sourceElement = source
    private val external = isExternal
    override fun isExternal(): Boolean = external
    override fun getContainingDeclaration(): DeclarationDescriptor = containingDescriptor
    override fun getSource(): SourceElement = sourceElement
}
