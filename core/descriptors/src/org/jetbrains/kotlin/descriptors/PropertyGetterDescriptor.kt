/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.descriptors

// Translated-unverified.
interface PropertyGetterDescriptor : PropertyAccessorDescriptor {
    override fun getOriginal(): PropertyGetterDescriptor
    override fun getOverriddenDescriptors(): Collection<PropertyGetterDescriptor>
}

val PropertyGetterDescriptor.original: PropertyGetterDescriptor get() = getOriginal()
val PropertyGetterDescriptor.overriddenDescriptors: Collection<PropertyGetterDescriptor> get() = getOverriddenDescriptors()
