/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.contracts.description

import org.jetbrains.kotlin.contracts.description.expressions.*
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ContractProvider
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.storage.StorageManager

internal val FunctionDescriptor.valueParameters: List<ValueParameterDescriptor>
    get() = getValueParameters()

interface ContractDescriptionElement {
    fun <R, D> accept(contractDescriptionVisitor: ContractDescriptionVisitor<R, D>, data: D): R
}

interface BooleanExpression : ContractDescriptionElement

interface EffectDeclaration : ContractDescriptionElement

interface ContractDescriptionVisitor<out R, in D> {
    fun visitContractDescriptionElement(contractDescriptionElement: ContractDescriptionElement, data: D): R =
        throw IllegalStateException("Unexpected contract description element: $contractDescriptionElement")

    fun visitEffectDeclaration(effectDeclaration: EffectDeclaration, data: D): R =
        visitContractDescriptionElement(effectDeclaration, data)

    fun visitConditionalEffectDeclaration(conditionalEffect: ConditionalEffectDeclaration, data: D): R =
        visitEffectDeclaration(conditionalEffect, data)

    fun visitReturnsEffectDeclaration(returnsEffect: ReturnsEffectDeclaration, data: D): R =
        visitEffectDeclaration(returnsEffect, data)

    fun visitCallsEffectDeclaration(callsEffect: CallsEffectDeclaration, data: D): R =
        visitEffectDeclaration(callsEffect, data)

    fun visitReturnsResultOfEffectDeclaration(returnsResultOfEffect: ReturnsResultOfEffectDeclaration, data: D): R =
        visitEffectDeclaration(returnsResultOfEffect, data)

    fun visitBooleanExpression(booleanExpression: BooleanExpression, data: D): R =
        visitContractDescriptionElement(booleanExpression, data)

    fun visitLogicalOr(logicalOr: LogicalOr, data: D): R =
        visitBooleanExpression(logicalOr, data)

    fun visitLogicalAnd(logicalAnd: LogicalAnd, data: D): R =
        visitBooleanExpression(logicalAnd, data)

    fun visitLogicalNot(logicalNot: LogicalNot, data: D): R =
        visitBooleanExpression(logicalNot, data)

    fun visitIsInstancePredicate(isInstancePredicate: IsInstancePredicate, data: D): R =
        visitBooleanExpression(isInstancePredicate, data)

    fun visitIsNullPredicate(isNullPredicate: IsNullPredicate, data: D): R =
        visitBooleanExpression(isNullPredicate, data)

    fun visitValue(value: ContractDescriptionValue, data: D): R =
        visitContractDescriptionElement(value, data)

    fun visitConstantDescriptor(constantReference: ConstantReference, data: D): R =
        visitValue(constantReference, data)

    fun visitBooleanConstantDescriptor(booleanConstantDescriptor: BooleanConstantReference, data: D): R =
        visitConstantDescriptor(booleanConstantDescriptor, data)

    fun visitVariableReference(variableReference: VariableReference, data: D): R =
        visitValue(variableReference, data)

    fun visitBooleanVariableReference(booleanVariableReference: BooleanVariableReference, data: D): R =
        visitVariableReference(booleanVariableReference, data)
}

class ContractDescription(
    val effects: List<EffectDeclaration>,
    val ownerFunction: FunctionDescriptor,
    val storageManager: StorageManager
)

abstract class AbstractContractProvider : ContractProvider {
    abstract fun getContractDescription(): ContractDescription?
}

class ContractProviderImpl(private val contractDescription: ContractDescription?) : AbstractContractProvider() {
    override fun getContractDescription(): ContractDescription? = contractDescription
}

object ContractProviderKey : CallableDescriptor.UserDataKey<AbstractContractProvider>

class ConditionalEffectDeclaration(
    val effect: EffectDeclaration,
    val condition: BooleanExpression
) : EffectDeclaration {
    override fun <R, D> accept(contractDescriptionVisitor: ContractDescriptionVisitor<R, D>, data: D): R =
        contractDescriptionVisitor.visitConditionalEffectDeclaration(this, data)
}

class ReturnsEffectDeclaration(val value: ConstantReference) : EffectDeclaration {
    override fun <R, D> accept(contractDescriptionVisitor: ContractDescriptionVisitor<R, D>, data: D): R =
        contractDescriptionVisitor.visitReturnsEffectDeclaration(this, data)
}

class CallsEffectDeclaration(
    val variableReference: VariableReference,
    val kind: EventOccurrencesRange
) : EffectDeclaration {
    override fun <R, D> accept(contractDescriptionVisitor: ContractDescriptionVisitor<R, D>, data: D): R =
        contractDescriptionVisitor.visitCallsEffectDeclaration(this, data)
}

class ReturnsResultOfEffectDeclaration(val variableReference: VariableReference) : EffectDeclaration {
    override fun <R, D> accept(contractDescriptionVisitor: ContractDescriptionVisitor<R, D>, data: D): R =
        contractDescriptionVisitor.visitReturnsResultOfEffectDeclaration(this, data)
}
