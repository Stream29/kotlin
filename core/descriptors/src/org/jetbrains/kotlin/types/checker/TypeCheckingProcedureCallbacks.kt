/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.types.checker

import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeConstructor
import org.jetbrains.kotlin.types.TypeProjection

// Translated-unverified. True continues checking; false fails it.
interface TypeCheckingProcedureCallbacks {
    fun assertEqualTypes(a: KotlinType, b: KotlinType, typeCheckingProcedure: TypeCheckingProcedure): Boolean
    fun assertEqualTypeConstructors(a: TypeConstructor, b: TypeConstructor): Boolean
    fun assertSubtype(subtype: KotlinType, supertype: KotlinType, typeCheckingProcedure: TypeCheckingProcedure): Boolean
    fun capture(type: KotlinType, typeProjection: TypeProjection): Boolean
    fun noCorrespondingSupertype(subtype: KotlinType, supertype: KotlinType): Boolean
}
