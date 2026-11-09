/*
 * Copyright 2010-2016 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.descriptors

import org.jetbrains.kotlin.mpp.ClassifierSymbolMarker
import org.jetbrains.kotlin.types.SimpleType
import org.jetbrains.kotlin.types.TypeConstructor

// Translated-unverified.
interface ClassifierDescriptor : DeclarationDescriptorNonRoot, ClassifierSymbolMarker {
    fun getTypeConstructor(): TypeConstructor
    fun getDefaultType(): SimpleType
    override fun getOriginal(): ClassifierDescriptor
}

val ClassifierDescriptor.typeConstructor: TypeConstructor get() = getTypeConstructor()
val ClassifierDescriptor.defaultType: SimpleType get() = getDefaultType()
val ClassifierDescriptor.original: ClassifierDescriptor get() = getOriginal()
