/*
 * Copyright 2010-2016 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.types.checker

import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeConstructor

// Translated-unverified.
open class KotlinTypeCheckerImpl protected constructor(private val procedure: TypeCheckingProcedure) : KotlinTypeChecker {
    override fun isSubtypeOf(subtype: KotlinType, supertype: KotlinType): Boolean = procedure.isSubtypeOf(subtype, supertype)
    override fun equalTypes(a: KotlinType, b: KotlinType): Boolean = procedure.equalTypes(a, b)

    companion object {
        fun withAxioms(equalityAxioms: KotlinTypeChecker.TypeConstructorEquality): KotlinTypeChecker =
            KotlinTypeCheckerImpl(TypeCheckingProcedure(object : TypeCheckerProcedureCallbacksImpl() {
                override fun assertEqualTypeConstructors(a: TypeConstructor, b: TypeConstructor): Boolean =
                    a == b || equalityAxioms.equals(a, b)
            }))
    }
}
