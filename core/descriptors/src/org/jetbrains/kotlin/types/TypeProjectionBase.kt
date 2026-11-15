/*
 * Copyright 2010-2016 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.types

// Translated-unverified.
abstract class TypeProjectionBase : TypeProjection {
    override fun toString(): String {
        if (isStarProjection()) return "*"
        if (getProjectionKind() == Variance.INVARIANT) return getType().toString()
        return "${getProjectionKind()} ${getType()}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TypeProjection) return false
        if (isStarProjection() != other.isStarProjection()) return false
        if (getProjectionKind() != other.getProjectionKind()) return false
        if (getType() != other.getType()) return false
        return true
    }

    override fun hashCode(): Int {
        val result = getProjectionKind().hashCode()
        return 31 * result + if (TypeUtils.noExpectedType(getType())) 19
        else if (isStarProjection()) 17 else getType().hashCode()
    }
}
