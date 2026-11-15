/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the upstream util.kt.
 */
package org.jetbrains.kotlin.load.java.descriptors

import org.jetbrains.kotlin.K1Deprecation
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.load.java.lazy.descriptors.LazyJavaStaticClassScope
import org.jetbrains.kotlin.load.kotlin.JvmPackagePartSource
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperClassNotAny
import org.jetbrains.kotlin.resolve.jvm.JvmClassName
import org.jetbrains.kotlin.serialization.deserialization.descriptors.DescriptorWithContainerSource

// Moved unchanged from util.kt so value-parameter enhancement does not link the Java class loader.
@K1Deprecation
fun ClassDescriptor.getParentJavaStaticClassScope(): LazyJavaStaticClassScope? {
    val superClassDescriptor = getSuperClassNotAny() ?: return null

    return superClassDescriptor.staticScope as? LazyJavaStaticClassScope ?: superClassDescriptor.getParentJavaStaticClassScope()
}

@K1Deprecation
fun DescriptorWithContainerSource.getImplClassNameForDeserialized(): JvmClassName? =
    (containerSource as? JvmPackagePartSource)?.className
