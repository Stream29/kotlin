/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.descriptors.impl

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.AnnotatedImpl
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.renderer.DescriptorRenderer
import kotlin.native.identityHashCode

// Translated-unverified.
abstract class DeclarationDescriptorImpl(annotations: Annotations, name: Name) : AnnotatedImpl(annotations), DeclarationDescriptor {
    private val descriptorName = name
    override fun getName(): Name = descriptorName
    override fun getOriginal(): DeclarationDescriptor = this
    override fun acceptVoid(visitor: DeclarationDescriptorVisitor<Nothing?, Nothing?>) {
        accept(visitor, null)
    }
    override fun toString(): String = toString(this)

    companion object {
        fun toString(descriptor: DeclarationDescriptor): String = try {
            DescriptorRenderer.DEBUG_TEXT.render(descriptor) +
                "[${descriptor::class.simpleName}@${descriptor.identityHashCode().toUInt().toString(16)}]"
        } catch (e: Throwable) {
            // Upstream diagnostic fallback for a descriptor that is not completely initialized.
            "${descriptor::class.simpleName} ${descriptor.getName()}"
        }
    }
}
