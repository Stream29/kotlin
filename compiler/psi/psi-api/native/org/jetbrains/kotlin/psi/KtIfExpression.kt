/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.lexer.KtTokens



open class KtIfExpression : KtExpressionImpl {
constructor(node: ASTNode) : super(node) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitIfExpression(this,data)
}

open fun getCondition(): KtExpression? {

return findExpressionUnder(KtNodeTypes.CONDITION)
}

open fun getLeftParenthesis(): PsiElement? {

return findChildByType(KtTokens.LPAR)
}

open fun getRightParenthesis(): PsiElement? {

return findChildByType(KtTokens.RPAR)
}

open fun getThen(): KtExpression? {

return findExpressionUnder(KtNodeTypes.THEN)
}

open fun getElse(): KtExpression? {

return findExpressionUnder(KtNodeTypes.ELSE)
}

open fun getElseKeyword(): PsiElement? {

return findChildByType(KtTokens.ELSE_KEYWORD)
}

open fun getIfKeyword(): PsiElement {

return findChildByType<PsiElement>(KtTokens.IF_KEYWORD)!!
}
}
