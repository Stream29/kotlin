/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.lexer.KtTokens



open class KtIsExpression : KtExpressionImpl, KtOperationExpression {
constructor(node: ASTNode) : super(node) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitIsExpression(this,data)
}

open fun getLeftHandSide(): KtExpression {

return findChildByClass(KtExpression ::class)!!
}

open fun getTypeReference(): KtTypeReference? {

return (findChildByType(KtNodeTypes.TYPE_REFERENCE) as KtTypeReference?)
}

override fun getOperationReference(): KtSimpleNameExpression {

return (findChildByType<com.intellij.psi.PsiElement>(KtNodeTypes.OPERATION_REFERENCE) as KtSimpleNameExpression)
}

open fun isNegated(): Boolean {

return getOperationReference().getReferencedNameElementType() == KtTokens.NOT_IS
}
}
