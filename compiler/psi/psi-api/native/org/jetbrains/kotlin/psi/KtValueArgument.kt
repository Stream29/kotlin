/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.tree.TokenSet
import org.jetbrains.kotlin.KtStubBasedElementTypes
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub
import org.jetbrains.kotlin.psi.stubs.KotlinValueArgumentStub
import org.jetbrains.kotlin.psi.stubs.elements.KtTokenSets



open class KtValueArgument : KtElementImplStub<KotlinValueArgumentStub<out KtValueArgument>>, ValueArgument {
constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinValueArgumentStub<KtValueArgument>) : super(stub,KtStubBasedElementTypes.VALUE_ARGUMENT) {


}

protected constructor(stub: KotlinValueArgumentStub<out KtValueArgument>, nodeType: IStubElementType<*, *>) : super(stub,nodeType) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitArgument(this,data)
}

override fun getArgumentExpression(): KtExpression? {

var stub: KotlinPlaceHolderStub<out KtValueArgument>? = stub
if (stub != null) {
var constantExpressions: Array<KtExpression?> = stub.getChildrenByType(KtTokenSets.CONSTANT_EXPRESSIONS,KtExpression.EMPTY_ARRAY)
if (constantExpressions.size != 0) {
return constantExpressions[0]
}
}
return findChildByClass(KtExpression ::class)
}

open fun getStringTemplateExpression(): KtStringTemplateExpression? {

var stub: KotlinPlaceHolderStub<out KtValueArgument>? = stub
var expression: KtExpression?
if (stub != null) {
var stringTemplateExpressions: Array<KtExpression?> = stub.getChildrenByType(STRING_TEMPLATE_EXPRESSIONS_TYPES,KtExpression.EMPTY_ARRAY)
expression = (if (stringTemplateExpressions.size != 0) stringTemplateExpressions[0] else null)
}
else {
expression = findChildByClass(KtExpression ::class)
}
return (if (expression is KtStringTemplateExpression) (expression as KtStringTemplateExpression) else null)
}

override fun getArgumentName(): KtValueArgumentName? {

return getStubOrPsiChild(KtStubBasedElementTypes.VALUE_ARGUMENT_NAME)
}

open fun getEqualsToken(): PsiElement? {

return findChildByType(KtTokens.EQ)
}

override fun isNamed(): Boolean {

return getArgumentName() != null
}

override fun asElement(): KtElement {

return this
}

override fun getSpreadElement(): LeafPsiElement? {

var stub: KotlinValueArgumentStub<*>? = stub
if (stub != null && ! stub.isSpread) {
return null
}
var node: ASTNode? = node.findChildByType(KtTokens.MUL)
return (if (node == null) null else (node.psi as LeafPsiElement))
}

override val isSpread: Boolean
get() {

var stub: KotlinValueArgumentStub<*>? = greenStub
if (stub != null) {
return stub.isSpread
}
return getSpreadElement() != null
}

override fun isExternal(): Boolean {

return false
}

companion object {
private val STRING_TEMPLATE_EXPRESSIONS_TYPES: TokenSet = TokenSet.create(KtStubBasedElementTypes.STRING_TEMPLATE)
}
}
