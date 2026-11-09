/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@file:OptIn(kotlin.native.internal.InternalForKotlinNative::class)

package org.jetbrains.kotlin.psi

import org.jetbrains.kotlin.util.NativeArrays as Arrays

import org.jetbrains.kotlin.psi.psiUtil.getContainingKtFile as p1kOriginal_getContainingKtFile

import com.intellij.lang.Language
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.impl.source.tree.CompositeElement
import com.intellij.psi.impl.source.tree.LazyParseablePsiElement
import com.intellij.psi.util.PsiUtilCore
import com.intellij.util.IncorrectOperationException
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.KtNodeTypes.Companion.BLOCK



open class KtBlockExpression : LazyParseablePsiElement, KtElement, KtExpression, KtStatementExpression {
protected override fun createUnlinkedClone(): KtBlockExpression {
check(this::class == KtBlockExpression::class) { "Missing concrete Kotlin block clone hook: ${this::class}" }
return kotlin.native.internal.createUninitializedInstance<KtBlockExpression>().also { copyLazyStateTo(it) }
}

constructor(text: CharSequence?) : super(BLOCK,text) {


}

open fun shouldChangeModificationCount(place: PsiElement): Boolean {

return false
}

override val language: Language
get() {

return KotlinLanguage.INSTANCE
}

override fun toString(): String {

return node.elementType.toString()
}

override fun getContainingKtFile(): KtFile {

return (this).p1kOriginal_getContainingKtFile()
}

override fun <D> acceptChildren(visitor: KtVisitor<Nothing?,D>, data: D): Unit {

KtPsiUtil.visitChildren(this,visitor,data)
}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitBlockExpression(this,data)
}

final override fun accept(visitor: PsiElementVisitor): Unit {

if (visitor is KtVisitor<*, *>) {
accept((visitor as KtVisitor<Any?, Any?>),null)
}
else {
visitor.visitElement(this)
}
}

override fun delete(): Unit {

KtPsiMutationService.getInstance().deleteBlockExpression(this)
}

override fun rawDelete(): Unit {

super<LazyParseablePsiElement>.delete()
}

override val children: Array<PsiElement>
get() {

var psiChild: PsiElement? = firstChild
var result: MutableList<PsiElement>? = null
while (psiChild != null) {
if (psiChild.node is CompositeElement) {
if (result == null) result = ArrayList()
result.add(psiChild)
}
psiChild = psiChild.nextSibling
}
return (if (result == null) PsiElement.EMPTY_ARRAY else PsiUtilCore.toPsiElementArray(result))
}

override fun getPsiOrParent(): KtElement {

return this
}

open fun getFirstStatement(): KtExpression? {

return findChildByClass(KtExpression ::class)
}

open fun getStatements(): List<KtExpression> {

return Arrays.asList(findChildrenByClass(KtExpression ::class))
}

open fun getLastBracketRange(): TextRange? {

var rBrace: PsiElement? = getRBrace()
return (if (rBrace != null) rBrace.textRange else null)
}

open fun getRBrace(): PsiElement? {

return findPsiChildByType(KtTokens.RBRACE)
}

open fun getLBrace(): PsiElement? {

return findPsiChildByType(KtTokens.LBRACE)
}
}
