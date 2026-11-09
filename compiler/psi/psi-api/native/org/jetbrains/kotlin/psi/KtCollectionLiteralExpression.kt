/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import org.jetbrains.kotlin.util.NativeArrays as Arrays

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.KtStubBasedElementTypes
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.stubs.KotlinCollectionLiteralExpressionStub
import org.jetbrains.kotlin.psi.stubs.elements.KtTokenSets
import org.jetbrains.kotlin.resolution.KtResolvableCall
import org.jetbrains.kotlin.psi.psiUtil.getTrailingCommaByClosingElement



open class KtCollectionLiteralExpression : KtElementImplStub<KotlinCollectionLiteralExpressionStub>, KtReferenceExpression, KtResolvableCall {
constructor(stub: KotlinCollectionLiteralExpressionStub) : super(stub,KtStubBasedElementTypes.COLLECTION_LITERAL_EXPRESSION) {


}

constructor(node: ASTNode) : super(node) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitCollectionLiteralExpression(this,data)
}

open fun getLeftBracket(): PsiElement? {

var astNode: ASTNode? = node.findChildByType(KtTokens.LBRACKET)
return (if (astNode != null) astNode.psi else null)
}

open fun getRightBracket(): PsiElement? {

var astNode: ASTNode? = node.findChildByType(KtTokens.RBRACKET)
return (if (astNode != null) astNode.psi else null)
}

open fun getTrailingComma(): PsiElement? {

var rightBracket: PsiElement? = getRightBracket()
return getTrailingCommaByClosingElement(rightBracket)
}

@Suppress("UNCHECKED_CAST")
open fun getInnerExpressions(): List<KtExpression> {

var stub: KotlinCollectionLiteralExpressionStub? = stub
if (stub != null) {
var expressionsCount: Int = stub.innerExpressionCount
if (expressionsCount == 0) {
return emptyList()
}
var constantExpressions: Array<KtExpression?> = stub.getChildrenByType(KtTokenSets.CONSTANT_EXPRESSIONS,KtExpression.EMPTY_ARRAY)
if (constantExpressions.size == expressionsCount) {
return Arrays.asList(constantExpressions) as List<KtExpression>
}
}
return PsiTreeUtil.getChildrenOfTypeAsList(this,KtExpression ::class)
}
}
