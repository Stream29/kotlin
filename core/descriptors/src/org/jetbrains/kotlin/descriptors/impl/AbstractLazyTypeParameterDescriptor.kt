/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.descriptors.impl

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.SupertypeLoopChecker
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.types.Variance

// Translated-unverified.
abstract class AbstractLazyTypeParameterDescriptor(
    storageManager: StorageManager, containingDeclaration: DeclarationDescriptor, annotations: Annotations, name: Name,
    variance: Variance, isReified: Boolean, index: Int, source: SourceElement, supertypeLoopChecker: SupertypeLoopChecker
) : AbstractTypeParameterDescriptor(storageManager, containingDeclaration, annotations, name, variance, isReified, index, source, supertypeLoopChecker) {
    // No descriptor renderer: preserve laziness.
    override fun toString(): String =
        (if (isReified()) "reified " else "") + (if (getVariance() == Variance.INVARIANT) "" else "${getVariance()} ") + getName()
}
