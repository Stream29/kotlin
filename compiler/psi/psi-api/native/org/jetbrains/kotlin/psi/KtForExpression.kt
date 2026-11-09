/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.resolution.KtResolvableCall



open class KtForExpression : KtLoopExpression, KtResolvableCall {
constructor(node: ASTNode) : super(node) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitForExpression(this,data)
}

open fun getLoopParameter(): KtParameter? {

return (findChildByType(KtNodeTypes.VALUE_PARAMETER) as KtParameter?)
}

open fun getDestructuringDeclaration(): KtDestructuringDeclaration? {

var loopParameter: KtParameter? = getLoopParameter()
if (loopParameter == null) return null
return loopParameter.getDestructuringDeclaration()
}

open fun getLoopRange(): KtExpression? {

return findExpressionUnder(KtNodeTypes.LOOP_RANGE)
}

open fun getInKeyword(): PsiElement? {

return findChildByType(KtTokens.IN_KEYWORD)
}

open fun getForKeyword(): PsiElement {

return findChildByType<PsiElement>(KtTokens.FOR_KEYWORD)!!
}
}
