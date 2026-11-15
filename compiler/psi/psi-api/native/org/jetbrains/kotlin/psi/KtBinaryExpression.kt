/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import org.jetbrains.kotlin.util.NativeArrays as Arrays
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub
import org.jetbrains.kotlin.resolution.KtResolvableCall
import org.jetbrains.kotlin.utils.KotlinExceptionWithAttachments

open class KtBinaryExpression : KtExpressionImplStub<KotlinPlaceHolderStub<KtBinaryExpression>>, KtOperationExpression, KtResolvableCall {
    constructor(node: ASTNode) : super(node)
    constructor(stub: KotlinPlaceHolderStub<KtBinaryExpression>) : super(stub, KtNodeTypes.BINARY_EXPRESSION)

    override fun <R, D> accept(visitor: KtVisitor<R, D>, data: D): R =
        visitor.visitBinaryExpression(this, data)

    open fun getLeft(): KtExpression? {
        val operationReference = getOperationReference()
        val stubBasedOperand = operationReference.stubBasedOperandBefore
        if (stubBasedOperand != null) return stubBasedOperand
        var node: ASTNode? = operationReference.node.treePrev
        while (node != null) {
            val psi: PsiElement? = node.psi
            if (psi is KtExpression) return psi
            node = node.treePrev
        }
        return null
    }

    open fun getRight(): KtExpression? {
        val operationReference = getOperationReference()
        val stubBasedOperand = operationReference.stubBasedOperandAfter
        if (stubBasedOperand != null) return stubBasedOperand
        var node: ASTNode? = operationReference.node.treeNext
        while (node != null) {
            val psi: PsiElement? = node.psi
            if (psi is KtExpression) return psi
            node = node.treeNext
        }
        return null
    }

    override fun getOperationReference(): KtOperationReferenceExpression {
        val operationReference = getOperationReferenceOrNull()
        if (operationReference == null) {
            val exception = KotlinExceptionWithAttachments("No operation reference for binary expression: " + Arrays.toString(children))
            exception.withPsiAttachment("expression.kt", this)
            throw exception
        }
        return operationReference
    }

    open fun getOperationTokenOrNull(): IElementType? =
        getOperationReferenceOrNull()?.getReferencedNameElementType()

    private fun getOperationReferenceOrNull(): KtOperationReferenceExpression? =
        getStubOrPsiChild(KtNodeTypes.OPERATION_REFERENCE, KtOperationReferenceExpression::class)

    open fun getOperationToken(): IElementType {
        val tokenOrNull = getOperationTokenOrNull()
        if (tokenOrNull == null) {
            val exception = KotlinExceptionWithAttachments("No operation token for binary expression: " + Arrays.toString(children))
            exception.withPsiAttachment("expression.kt", this)
            throw exception
        }
        return tokenOrNull
    }
}
