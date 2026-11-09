/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.java.enhancement

import org.jetbrains.kotlin.fir.FirAnnotationContainer
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import kotlin.collections.AbstractList

/**
 * This class provides unified entry point for enhanced Java annotations.
 *
 * All FIR Java declarations delegates [org.jetbrains.kotlin.fir.FirAnnotationContainer.annotations] to this class.
 *
 * TODO: the lazy annotations is a workaround for KT-55387, some non-lazy solution should probably be used instead
 *
 * @see FirDelegatedJavaAnnotationList
 * @see FirEmptyJavaAnnotationList
 */
interface FirJavaAnnotationList : List<FirAnnotation>

class FirDelegatedJavaAnnotationList(annotationsOwner: FirAnnotationContainer) :
    FirJavaAnnotationList,
    List<FirAnnotation> by annotationsOwner.annotations

object FirEmptyJavaAnnotationList : FirJavaAnnotationList, AbstractList<FirAnnotation>() {
    override val size: Int get() = 0
    override fun isEmpty(): Boolean = true
    override fun contains(element: FirAnnotation): Boolean = false
    override fun iterator(): Iterator<FirAnnotation> = emptyList<FirAnnotation>().iterator()
    override fun containsAll(elements: Collection<FirAnnotation>): Boolean = elements.isEmpty()
    override fun get(index: Int): FirAnnotation = throw IndexOutOfBoundsException("Index $index out of bounds")
    override fun listIterator(): ListIterator<FirAnnotation> = emptyList<FirAnnotation>().listIterator()
}
