/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.descriptors

import org.jetbrains.kotlin.mpp.TypeParameterSymbolMarker
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeConstructor
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.kotlin.types.model.TypeParameterMarker

// Translated-unverified.
interface TypeParameterDescriptor : ClassifierDescriptor, TypeParameterMarker, TypeParameterSymbolMarker {
    fun isReified(): Boolean
    fun getVariance(): Variance
    fun getUpperBounds(): List<KotlinType>
    override fun getTypeConstructor(): TypeConstructor
    override fun getOriginal(): TypeParameterDescriptor
    fun getIndex(): Int
    /** Captured copies keep the original's type constructor but belong to the inner declaration. */
    fun isCapturedFromOuterDeclaration(): Boolean
    fun getStorageManager(): StorageManager
}

val TypeParameterDescriptor.isReified: Boolean get() = isReified()
val TypeParameterDescriptor.variance: Variance get() = getVariance()
val TypeParameterDescriptor.upperBounds: List<KotlinType> get() = getUpperBounds()
val TypeParameterDescriptor.original: TypeParameterDescriptor get() = getOriginal()
val TypeParameterDescriptor.index: Int get() = getIndex()
val TypeParameterDescriptor.isCapturedFromOuterDeclaration: Boolean get() = isCapturedFromOuterDeclaration()
val TypeParameterDescriptor.storageManager: StorageManager get() = getStorageManager()
