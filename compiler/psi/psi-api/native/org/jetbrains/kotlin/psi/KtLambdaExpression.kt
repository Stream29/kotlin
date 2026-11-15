/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@file:OptIn(kotlin.native.internal.InternalForKotlinNative::class)

package org.jetbrains.kotlin.psi

import org.jetbrains.kotlin.psi.psiUtil.getContainingKtFile as p1kOriginal_getContainingKtFile

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.source.tree.LazyParseablePsiElement
import com.intellij.util.IncorrectOperationException
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.lexer.KtTokens



open class KtLambdaExpression : LazyParseablePsiElement, KtExpression {
protected override fun createUnlinkedClone(): KtLambdaExpression {
check(this::class == KtLambdaExpression::class) { "Missing concrete Kotlin lambda clone hook: ${this::class}" }
return kotlin.native.internal.createUninitializedInstance<KtLambdaExpression>().also { copyLazyStateTo(it) }
}

constructor(text: CharSequence?) : super(KtNodeTypes.LAMBDA_EXPRESSION,text) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitLambdaExpression(this,data)
}

open fun getFunctionLiteral(): KtFunctionLiteral {

return findChildByType(KtNodeTypes.FUNCTION_LITERAL)!!.getPsi(KtFunctionLiteral ::class)!!
}

open fun getValueParameters(): List<KtParameter> {

return getFunctionLiteral().getValueParameters()
}

open fun getBodyExpression(): KtBlockExpression? {

return getFunctionLiteral().getBodyExpression()
}

open fun hasDeclaredReturnType(): Boolean {

return getFunctionLiteral().getTypeReference() != null
}

open fun asElement(): KtElement {

return this
}

open fun getLeftCurlyBrace(): ASTNode {

return getFunctionLiteral().node.findChildByType(KtTokens.LBRACE)!!
}

open fun getRightCurlyBrace(): ASTNode? {

return getFunctionLiteral().node.findChildByType(KtTokens.RBRACE)
}

override fun getContainingKtFile(): KtFile {

return (this).p1kOriginal_getContainingKtFile()
}

override fun <D> acceptChildren(visitor: KtVisitor<Nothing?,D>, data: D): Unit {

KtPsiUtil.visitChildren(this,visitor,data)
}

final override fun accept(visitor: PsiElementVisitor): Unit {

if (visitor is KtVisitor<*, *>) {
accept((visitor as KtVisitor<Any?, Any?>),null)
}
else {
visitor.visitElement(this)
}
}

override fun toString(): String {

return node.elementType.toString()
}

override fun getPsiOrParent(): KtElement {

return this
}

override fun rawDelete(): Unit {

super<LazyParseablePsiElement>.delete()
}

open fun shouldChangeModificationCount(place: PsiElement): Boolean {

return false
}

fun isTrailingLambdaOnNewLine(): Boolean {

var parent: PsiElement? = parent
if (parent is KtLabeledExpression) {
parent = parent.parent
}
if (parent is KtLambdaArgument) {
var prevSibling: PsiElement? = parent.prevSibling
while (prevSibling != null && !(prevSibling is KtElement)) {
if (prevSibling is PsiWhiteSpace && prevSibling.textContains('\n')) {
return true
}
prevSibling = prevSibling.prevSibling
}
}
return false
}
}
