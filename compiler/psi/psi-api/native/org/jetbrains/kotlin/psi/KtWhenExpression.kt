/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.lexer.KtTokens



open class KtWhenExpression : KtExpressionImpl {
constructor(node: ASTNode) : super(node) {


}

open fun getEntries(): List<KtWhenEntry> {

return findChildrenByType(KtNodeTypes.WHEN_ENTRY)
}

open fun getSubjectVariable(): KtProperty? {

return findChildByClass(KtProperty ::class)
}

open fun getSubjectExpression(): KtExpression? {

return findChildByClass(KtExpression ::class)
}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitWhenExpression(this,data)
}

open fun getWhenKeyword(): PsiElement {

return findChildByType<PsiElement>(KtTokens.WHEN_KEYWORD)!!
}

open fun getCloseBrace(): PsiElement? {

return findChildByType(KtTokens.RBRACE)
}

open fun getOpenBrace(): PsiElement? {

return findChildByType(KtTokens.LBRACE)
}

open fun getLeftParenthesis(): PsiElement? {

return findChildByType(KtTokens.LPAR)
}

open fun getRightParenthesis(): PsiElement? {

return findChildByType(KtTokens.RPAR)
}

open fun getElseExpression(): KtExpression? {

for (entry in getEntries()) {
if (entry.isElse()) {
return entry.getExpression()
}
}
return null
}
}
