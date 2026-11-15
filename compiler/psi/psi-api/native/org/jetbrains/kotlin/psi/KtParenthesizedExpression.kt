/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub

open class KtParenthesizedExpression : KtExpressionImplStub<KotlinPlaceHolderStub<KtParenthesizedExpression>> {
    constructor(node: ASTNode) : super(node)
    constructor(stub: KotlinPlaceHolderStub<KtParenthesizedExpression>) : super(stub, KtNodeTypes.PARENTHESIZED)

    override fun <R, D> accept(visitor: KtVisitor<R, D>, data: D): R =
        visitor.visitParenthesizedExpression(this, data)

    open fun getExpression(): KtExpression? {
        val fromStub = getExpressionFromStub()
        if (fromStub != null) return fromStub
        return findChildByClass(KtExpression::class)
    }
}
