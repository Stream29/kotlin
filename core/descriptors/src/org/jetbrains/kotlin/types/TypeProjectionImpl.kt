/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.types

import org.jetbrains.kotlin.types.checker.KotlinTypeRefiner

// Translated-unverified.
open class TypeProjectionImpl(
    private val projection: Variance,
    private val projectedType: KotlinType
) : TypeProjectionBase() {
    constructor(type: KotlinType) : this(Variance.INVARIANT, type)
    override fun replaceType(type: KotlinType): TypeProjectionBase = TypeProjectionImpl(projection, type)
    override fun getProjectionKind(): Variance = projection
    override fun getType(): KotlinType = projectedType
    override fun isStarProjection(): Boolean = false
    @TypeRefinement
    override fun refine(kotlinTypeRefiner: KotlinTypeRefiner): TypeProjection =
        TypeProjectionImpl(projection, kotlinTypeRefiner.refineType(projectedType))
}
