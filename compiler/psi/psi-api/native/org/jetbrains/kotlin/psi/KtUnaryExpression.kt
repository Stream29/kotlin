/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub
import org.jetbrains.kotlin.resolution.KtResolvableCall

abstract class KtUnaryExpression : KtExpressionImplStub<KotlinPlaceHolderStub<out KtUnaryExpression>>, KtOperationExpression, KtResolvableCall {
    constructor(node: ASTNode) : super(node)
    protected constructor(stub: KotlinPlaceHolderStub<out KtUnaryExpression>, nodeType: IElementType) : super(stub, nodeType)

    abstract fun getBaseExpression(): KtExpression?

    override fun getOperationReference(): KtOperationReferenceExpression =
        getStubOrPsiChild(KtNodeTypes.OPERATION_REFERENCE, KtOperationReferenceExpression::class)!!

    open fun getOperationToken(): IElementType =
        getOperationReference().getReferencedNameElementType()
}
