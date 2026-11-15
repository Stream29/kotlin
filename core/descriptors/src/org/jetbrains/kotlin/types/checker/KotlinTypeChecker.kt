/*
 * Copyright 2010-2016 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.types.checker

import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeConstructor

// Translated-unverified.
interface KotlinTypeChecker {
    fun interface TypeConstructorEquality {
        fun equals(a: TypeConstructor, b: TypeConstructor): Boolean
    }
    fun isSubtypeOf(subtype: KotlinType, supertype: KotlinType): Boolean
    fun equalTypes(a: KotlinType, b: KotlinType): Boolean

    companion object {
        val DEFAULT: KotlinTypeChecker = NewKotlinTypeChecker.Default
    }
}
