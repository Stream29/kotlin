/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.descriptors

// Translated-unverified.
interface PropertySetterDescriptor : PropertyAccessorDescriptor {
    override fun getOriginal(): PropertySetterDescriptor
    override fun getOverriddenDescriptors(): Collection<PropertySetterDescriptor>
}

val PropertySetterDescriptor.original: PropertySetterDescriptor get() = getOriginal()
val PropertySetterDescriptor.overriddenDescriptors: Collection<PropertySetterDescriptor> get() = getOverriddenDescriptors()
