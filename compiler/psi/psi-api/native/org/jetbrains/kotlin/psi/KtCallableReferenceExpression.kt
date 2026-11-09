/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.resolution.KtResolvableCall



open class KtCallableReferenceExpression : KtExpressionImpl, KtDoubleColonExpression, KtResolvableCall {
constructor(node: ASTNode) : super(node) {


}

open fun getCallableReference(): KtSimpleNameExpression {

var psi: PsiElement? = doubleColonTokenReference
while (psi != null) {
if (psi is KtSimpleNameExpression) {
return (psi as KtSimpleNameExpression)
}
psi = psi.nextSibling
}
throw IllegalStateException("Callable reference simple name shouldn't be parsed to null")
}

override fun findColonColon(): PsiElement? {

return findChildByType(KtTokens.COLONCOLON)
}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitCallableReferenceExpression(this,data)
}
}
