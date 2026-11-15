/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.resolution.KtResolvableCall



open class KtArrayAccessExpression : KtExpressionImpl, KtReferenceExpression, KtResolvableCall {
constructor(node: ASTNode) : super(node) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitArrayAccessExpression(this,data)
}

open fun getArrayExpression(): KtExpression? {

return findChildByClass(KtExpression ::class)
}

open fun getIndexExpressions(): List<KtExpression> {

return PsiTreeUtil.getChildrenOfTypeAsList(getIndicesNode(),KtExpression ::class)
}

open fun getIndicesNode(): KtContainerNode {

var indicesNode: KtContainerNode? = findChildByType(KtNodeTypes.INDICES)
assert(indicesNode != null) { "Can't be null because of parser" }
return indicesNode!!
}

@Suppress("UNCHECKED_CAST")
open fun getBracketRanges(): List<TextRange> {

var lBracket: PsiElement? = getLeftBracket()
var rBracket: PsiElement? = getRightBracket()
if (lBracket == null || rBracket == null) {
return emptyList()
}
return arrayListOf<TextRange?>(lBracket.textRange,rBracket.textRange) as List<TextRange>
}

open fun getLeftBracket(): PsiElement? {

return getIndicesNode().findChildByType(KtTokens.LBRACKET)
}

open fun getRightBracket(): PsiElement? {

return getIndicesNode().findChildByType(KtTokens.RBRACKET)
}

open fun getTrailingComma(): PsiElement? {

return org.jetbrains.kotlin.psi.psiUtil.getTrailingCommaByClosingElement(getRightBracket())
}
}
