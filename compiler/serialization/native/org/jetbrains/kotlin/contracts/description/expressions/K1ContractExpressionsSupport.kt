/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.contracts.description.expressions

import org.jetbrains.kotlin.contracts.description.BooleanExpression
import org.jetbrains.kotlin.contracts.description.ContractDescriptionElement
import org.jetbrains.kotlin.contracts.description.ContractDescriptionVisitor
import org.jetbrains.kotlin.descriptors.ParameterDescriptor
import org.jetbrains.kotlin.types.KotlinType

interface ContractDescriptionValue : ContractDescriptionElement {
    override fun <R, D> accept(contractDescriptionVisitor: ContractDescriptionVisitor<R, D>, data: D): R =
        contractDescriptionVisitor.visitValue(this, data)
}

class LogicalOr(val left: BooleanExpression, val right: BooleanExpression) : BooleanExpression {
    override fun <R, D> accept(contractDescriptionVisitor: ContractDescriptionVisitor<R, D>, data: D): R =
        contractDescriptionVisitor.visitLogicalOr(this, data)
}

class LogicalAnd(val left: BooleanExpression, val right: BooleanExpression) : BooleanExpression {
    override fun <R, D> accept(contractDescriptionVisitor: ContractDescriptionVisitor<R, D>, data: D): R =
        contractDescriptionVisitor.visitLogicalAnd(this, data)
}

class LogicalNot(val arg: BooleanExpression) : BooleanExpression {
    override fun <R, D> accept(contractDescriptionVisitor: ContractDescriptionVisitor<R, D>, data: D): R =
        contractDescriptionVisitor.visitLogicalNot(this, data)
}

class IsInstancePredicate(
    val arg: VariableReference,
    val type: KotlinType,
    val isNegated: Boolean
) : BooleanExpression {
    override fun <R, D> accept(contractDescriptionVisitor: ContractDescriptionVisitor<R, D>, data: D): R =
        contractDescriptionVisitor.visitIsInstancePredicate(this, data)

    fun negated(): IsInstancePredicate = IsInstancePredicate(arg, type, !isNegated)
}

class IsNullPredicate(
    val arg: VariableReference,
    val isNegated: Boolean
) : BooleanExpression {
    override fun <R, D> accept(contractDescriptionVisitor: ContractDescriptionVisitor<R, D>, data: D): R =
        contractDescriptionVisitor.visitIsNullPredicate(this, data)

    fun negated(): IsNullPredicate = IsNullPredicate(arg, !isNegated)
}

open class ConstantReference(val name: String) : ContractDescriptionValue {
    override fun <R, D> accept(contractDescriptionVisitor: ContractDescriptionVisitor<R, D>, data: D): R =
        contractDescriptionVisitor.visitConstantDescriptor(this, data)

    companion object {
        val NULL = ConstantReference("NULL")
        val NOT_NULL = ConstantReference("NOT_NULL")
        val WILDCARD = ConstantReference("WILDCARD")
    }
}

class BooleanConstantReference private constructor(name: String) : ConstantReference(name), BooleanExpression {
    override fun <R, D> accept(contractDescriptionVisitor: ContractDescriptionVisitor<R, D>, data: D): R =
        contractDescriptionVisitor.visitBooleanConstantDescriptor(this, data)

    companion object {
        val TRUE = BooleanConstantReference("TRUE")
        val FALSE = BooleanConstantReference("FALSE")
    }
}

open class VariableReference(val descriptor: ParameterDescriptor) : ContractDescriptionValue {
    override fun <R, D> accept(contractDescriptionVisitor: ContractDescriptionVisitor<R, D>, data: D): R =
        contractDescriptionVisitor.visitVariableReference(this, data)
}

class BooleanVariableReference(descriptor: ParameterDescriptor) : VariableReference(descriptor), BooleanExpression {
    override fun <R, D> accept(contractDescriptionVisitor: ContractDescriptionVisitor<R, D>, data: D): R =
        contractDescriptionVisitor.visitBooleanVariableReference(this, data)
}
