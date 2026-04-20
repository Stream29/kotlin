/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.jvm.mapping

import org.jetbrains.kotlin.backend.jvm.InlineClassAbi
import org.jetbrains.kotlin.backend.jvm.JvmBackendContext
import org.jetbrains.kotlin.codegen.signature.BothSignatureWriter
import org.jetbrains.kotlin.codegen.util.inlinecodegen.LightIrType
import org.jetbrains.kotlin.codegen.util.inlinecodegen.SpecTypeParametersUsages
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrScriptSymbol
import org.jetbrains.kotlin.ir.symbols.IrTypeParameterSymbol
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrStarProjection
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.IrTypeProjection
import org.jetbrains.kotlin.ir.types.isMarkedNullable
import org.jetbrains.kotlin.ir.types.withNullability
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.genericTypeParameterIndex
import org.jetbrains.kotlin.ir.util.isJvmSpecialized
import org.jetbrains.kotlin.ir.util.isJvmSpecializedGeneric
import org.jetbrains.kotlin.ir.util.render
import org.jetbrains.kotlin.types.Variance

class IrSpecializationTypeMap {
    /**
     * Generic type index -> Specialized to type
     */
    val map: HashMap<Int, LightIrType> = HashMap()

    val callee: IrSimpleFunction

    var isSpecialized = false
        private set

    constructor(callExpression: IrCall, context: JvmBackendContext) {
        callee = callExpression.symbol.owner
        for ((typeParameterIndex, genericAndActual) in (callee.typeParameters zip callExpression.typeArguments).withIndex()) {
            val (generic, actual) = genericAndActual
            if (!generic.isJvmSpecialized) continue
            if (actual == null) error("specialized type is null in ${callExpression.render()}")
            isSpecialized = true
            map[typeParameterIndex] = actual.toLightIrType(context) ?: error("could not convert to light type: ${actual.render()}")
        }
    }
}

fun IrType.toLightIrType(context: JvmBackendContext): LightIrType? {
    val simpleType = this as? IrSimpleType ?: return null
    val sw = BothSignatureWriter(BothSignatureWriter.Mode.TYPE)

    val classifier = when (val classifier = simpleType.classifier) {
        is IrClassSymbol -> LightIrType.Classifier.Clazz(
            classifier.owner.fqNameWhenAvailable?.asString() ?: return null,
            InlineClassAbi.unboxType(simpleType.withNullability(false))?.let { context.defaultTypeMapper.mapType(it).toString() },
            InlineClassAbi.unboxType(simpleType.withNullability(true))?.let { context.defaultTypeMapper.mapType(it).toString() },
            context.defaultTypeMapper.mapTypeParameter(simpleType, sw).internalName,
        )
        is IrTypeParameterSymbol -> LightIrType.Classifier.TypeParameter(
            classifier.owner.name.asString(),
            classifier.owner.index,
            classifier.owner.variance.toLightIrTreeChar(),
            classifier.owner.isReified,
            classifier.owner.isJvmSpecialized,
            context.defaultTypeMapper.mapTypeParameter(simpleType, sw).internalName,
        )
        is IrScriptSymbol -> TODO("IrScriptSymbol classifiers are not supported yet")
    }

    val arguments = simpleType.arguments.map {
        when (it) {
            is IrStarProjection -> LightIrType.TypeArgument.StarProjection()
            is IrTypeProjection -> LightIrType.TypeArgument.TypeProjection(
                it.type.toLightIrType(context) ?: return null,
                it.variance.toLightIrTreeChar()
            )
        }
    }

    return LightIrType(classifier, arguments, isMarkedNullable())
}

private fun Variance.toLightIrTreeChar(): Char = when (this) {
    Variance.INVARIANT -> '-'
    Variance.IN_VARIANCE -> 'I'
    Variance.OUT_VARIANCE -> 'O'
}

fun IrType.asSpecTypeParameterUsage(): SpecTypeParametersUsages.Usage? =
    if (isJvmSpecializedGeneric) SpecTypeParametersUsages.Usage(genericTypeParameterIndex!!, isMarkedNullable()) else null

fun IrFunction.specTypeParametersUsages(): SpecTypeParametersUsages {
    return SpecTypeParametersUsages(
        buildMap {
            for ((parameterIndex, parameter) in parameters.withIndex()) {
                parameter.type.asSpecTypeParameterUsage()?.let {
                    put(parameterIndex, it)
                }
            }
        },
        returnType.asSpecTypeParameterUsage(),
    )
}
