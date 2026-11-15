/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub

open class KtPrefixExpression : KtUnaryExpression {
    constructor(node: ASTNode) : super(node)
    constructor(stub: KotlinPlaceHolderStub<KtPrefixExpression>) : super(stub, KtNodeTypes.PREFIX_EXPRESSION)

    override fun <R, D> accept(visitor: KtVisitor<R, D>, data: D): R =
        visitor.visitPrefixExpression(this, data)

    override fun getBaseExpression(): KtExpression? {
        val operationReference = getOperationReference()
        val stubBasedOperand = operationReference.stubBasedOperandAfter
        if (stubBasedOperand != null) return stubBasedOperand
        return PsiTreeUtil.getNextSiblingOfType(operationReference, KtExpression::class)
    }
}
