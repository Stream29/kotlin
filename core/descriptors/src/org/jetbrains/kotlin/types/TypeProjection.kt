/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.types

import org.jetbrains.kotlin.types.checker.KotlinTypeRefiner
import org.jetbrains.kotlin.types.model.TypeArgumentMarker

// Translated-unverified.
interface TypeProjection : TypeArgumentMarker {
    fun getProjectionKind(): Variance
    fun getType(): KotlinType
    fun isStarProjection(): Boolean
    @TypeRefinement
    fun refine(kotlinTypeRefiner: KotlinTypeRefiner): TypeProjection
    fun replaceType(type: KotlinType): TypeProjection
}

val TypeProjection.projectionKind: Variance get() = getProjectionKind()
val TypeProjection.type: KotlinType get() = getType()
val TypeProjection.isStarProjection: Boolean get() = isStarProjection()
