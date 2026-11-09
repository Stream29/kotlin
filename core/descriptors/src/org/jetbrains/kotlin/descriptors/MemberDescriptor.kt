/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.descriptors

// Translated-unverified.
interface MemberDescriptor : DeclarationDescriptorNonRoot, DeclarationDescriptorWithVisibility {
    fun getModality(): Modality
    override fun getVisibility(): DescriptorVisibility
    fun isExpect(): Boolean
    fun isActual(): Boolean
    fun isExternal(): Boolean
}

val MemberDescriptor.modality: Modality get() = getModality()
val MemberDescriptor.isExpect: Boolean get() = isExpect()
val MemberDescriptor.isActual: Boolean get() = isActual()
val MemberDescriptor.isExternal: Boolean get() = isExternal()
