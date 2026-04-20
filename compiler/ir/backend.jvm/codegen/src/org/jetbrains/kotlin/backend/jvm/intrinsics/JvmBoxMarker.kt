/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.jvm.intrinsics

import org.jetbrains.kotlin.backend.jvm.codegen.BlockInfo
import org.jetbrains.kotlin.backend.jvm.codegen.ExpressionCodegen
import org.jetbrains.kotlin.backend.jvm.codegen.MaterialValue
import org.jetbrains.kotlin.backend.jvm.codegen.PromisedValue
import org.jetbrains.kotlin.backend.jvm.codegen.materialize
import org.jetbrains.kotlin.backend.jvm.mapping.asSpecTypeParameterUsage
import org.jetbrains.kotlin.ir.expressions.*

object JvmBoxMarker : IntrinsicMethod() {
    override fun invoke(
        expression: IrFunctionAccessExpression,
        codegen: ExpressionCodegen,
        data: BlockInfo,
    ): PromisedValue {
        val type = expression.typeArguments[0]!!
        val typeUsage = type.asSpecTypeParameterUsage()!!

        val value = expression.arguments[0]!!.accept(codegen, data)
        value.materialize()

        codegen.mv.invokestatic(
            "kotlin/jvm/internal/Intrinsics",
            "boxMarker${typeUsage.encode()}",
            "(Ljava/lang/Object;)Lkotlin/jvm/internal/SpecBoxedDecoy${typeUsage.encode()};",
            false,
        )

        return MaterialValue(codegen, value.type, value.irType)
    }
}
