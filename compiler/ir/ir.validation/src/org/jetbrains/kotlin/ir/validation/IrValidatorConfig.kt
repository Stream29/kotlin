/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.validation

import org.jetbrains.kotlin.ir.validation.checkers.IrChecker
import org.jetbrains.kotlin.ir.validation.checkers.IrNestedOffsetRangeChecker
import org.jetbrains.kotlin.ir.validation.checkers.IrOffsetsChecker
import org.jetbrains.kotlin.ir.validation.checkers.declaration.*
import org.jetbrains.kotlin.ir.validation.checkers.expression.*
import org.jetbrains.kotlin.ir.validation.checkers.symbol.IrVisibilityChecker
import org.jetbrains.kotlin.ir.validation.checkers.type.IrTypeParameterScopeChecker

data class IrValidatorConfig(
    val checkTreeConsistency: Boolean = false,
    val checkUnboundSymbols: Boolean = false,
    val checkers: Set<IrChecker> = emptySet(),
) {
    fun withCheckers(vararg checkers: IrChecker) = copy(checkers = this.checkers + checkers)
    fun withoutCheckers(vararg checkers: IrChecker) = copy(checkers = this.checkers - checkers.toSet())

    fun withCheckersByName(include: List<String>, candidateCheckers: List<IrChecker>): IrValidatorConfig = copy(
        checkers = this.checkers + candidateCheckers.filter { checker ->
            include.intersect(getCheckerFilteringKeys(checker)).isNotEmpty()
        }
    )

    fun withoutCheckersByName(exclude: List<String>) = copy(
        checkers = checkers.filterNot { checker ->
            exclude.intersect(getCheckerFilteringKeys(checker)).isNotEmpty()
        }.toSet()
    )

    companion object {
        private val TYPE_CHECKERS = setOf(
            IrConstTypeChecker,
            IrStringConcatenationTypeChecker,
            IrGetObjectValueTypeChecker,
            IrGetValueTypeChecker,
            IrUnitTypeExpressionChecker,
            IrNothingTypeExpressionChecker,
            IrGetFieldTypeChecker,
            IrCallTypeChecker,
            IrTypeOperatorTypeChecker,
            IrDynamicTypeFieldAccessChecker,
        )
        private val VARARG_CHECKERS = setOf(
            IrVarargTypesChecker,
            IrValueParameterVarargTypesChecker,
        )
        private fun getCheckerFilteringKeys(checker: IrChecker): Set<String> = buildSet {
            val simpleName = checker::class.simpleName
            if (simpleName != null) add(simpleName)
            if (checker in TYPE_CHECKERS) add("IrTypeCheckers")
            if (checker in VARARG_CHECKERS) add("IrVarargCheckers")
        }
    }
}

fun IrValidatorConfig.withBasicChecks() = withCheckers(
    IrFunctionDispatchReceiverChecker, IrConstructorReceiverChecker, IrFunctionParametersChecker,
    IrPropertyAccessorsChecker, IrFunctionPropertiesChecker,
    IrSetValueAssignabilityChecker,
    IrTypeOperatorTypeOperandChecker,
    IrPrivateDeclarationOverrideChecker,
    IrPropertyCompanionExtensionChecker,
    IrFunctionCompanionExtensionChecker,
    IrOffsetsChecker,
)

fun IrValidatorConfig.withTypeChecks() = withCheckers(
    IrConstTypeChecker,
    IrStringConcatenationTypeChecker,
    IrGetObjectValueTypeChecker,
    IrGetValueTypeChecker,
    IrUnitTypeExpressionChecker,
    IrNothingTypeExpressionChecker,
    IrGetFieldTypeChecker,
    IrCallTypeChecker,
    IrTypeOperatorTypeChecker,
    IrDynamicTypeFieldAccessChecker,
)

fun IrValidatorConfig.withVarargChecks() = withCheckers(
    IrVarargTypesChecker,
    IrValueParameterVarargTypesChecker,
)

fun IrValidatorConfig.withInlineFunctionCallsiteCheck(checkInlineFunctionUseSites: InlineFunctionUseSiteChecker?) =
    if (checkInlineFunctionUseSites != null) {
        withCheckers(IrNoInlineUseSitesChecker(checkInlineFunctionUseSites))
    } else this

fun IrValidatorConfig.withAllChecks() = withBasicChecks()
    .withVarargChecks()
    .withTypeChecks()
    .withCheckers(
        IrCallValueArgumentCountChecker,
        IrCallTypeArgumentCountChecker,
        IrVisibilityChecker.Strict,
        IrValueAccessScopeChecker,
        IrTypeParameterScopeChecker,
        IrCrossFileFieldUsageChecker,
        IrFieldVisibilityChecker,
        IrExpressionBodyInFunctionChecker,
        IrNestedOffsetRangeChecker,
        IrClassSuperTypesChecker,
    )
