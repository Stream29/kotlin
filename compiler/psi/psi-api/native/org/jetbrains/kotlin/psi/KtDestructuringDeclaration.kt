/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.KtStubBasedElementTypes
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.stubs.KotlinDestructuringDeclarationStub
import org.jetbrains.kotlin.lexer.KtTokens.Companion.EQ
import org.jetbrains.kotlin.lexer.KtTokens.Companion.LBRACKET
import org.jetbrains.kotlin.lexer.KtTokens.Companion.LPAR
import org.jetbrains.kotlin.lexer.KtTokens.Companion.RBRACKET
import org.jetbrains.kotlin.lexer.KtTokens.Companion.RPAR
import org.jetbrains.kotlin.lexer.KtTokens.Companion.VAL_VAR
import org.jetbrains.kotlin.lexer.KtTokens.Companion.VAR_KEYWORD



open class KtDestructuringDeclaration : KtDeclarationStub<KotlinDestructuringDeclarationStub>, KtValVarKeywordOwner, KtDeclarationWithInitializer, KtDeclarationWithReturnType {
constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinDestructuringDeclarationStub) : super(stub,KtStubBasedElementTypes.DESTRUCTURING_DECLARATION) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitDestructuringDeclaration(this,data)
}

open fun getEntries(): List<KtDestructuringDeclarationEntry> {

var result: MutableList<KtDestructuringDeclarationEntry> = ArrayList()
run {
var child: ASTNode? = node.firstChildNode
forLoop1@ while (child != null) {
forBody2@ do {
if (child.elementType == KtNodeTypes.DESTRUCTURING_DECLARATION_ENTRY) {
result.add((child.psi as KtDestructuringDeclarationEntry))
}
else if (child.elementType == TokenType.ERROR_ELEMENT) {
run {
var errorChild: ASTNode? = child.firstChildNode
forLoop3@ while (errorChild != null) {
forBody4@ do {
if (errorChild.elementType == KtNodeTypes.DESTRUCTURING_DECLARATION_ENTRY) {
result.add((errorChild.psi as KtDestructuringDeclarationEntry))
}
} while (false)
errorChild = errorChild.treeNext
}
}
}
} while (false)
child = child.treeNext
}
}
return result
}

override fun getInitializer(): KtExpression? {

var stub: KotlinDestructuringDeclarationStub? = greenStub
if (stub != null && ! stub.hasInitializer) {
return null
}
var eqNode: ASTNode? = node.findChildByType(EQ)
if (eqNode == null) {
return null
}
return PsiTreeUtil.getNextSiblingOfType(eqNode.psi,KtExpression ::class)
}

override fun hasInitializer(): Boolean {

var stub: KotlinDestructuringDeclarationStub? = greenStub
if (stub != null) {
return stub.hasInitializer
}
return getInitializer() != null
}

open val isVar: Boolean
get() {

var stub: KotlinDestructuringDeclarationStub? = greenStub
if (stub != null) {
return stub.isVar
}
return node.findChildByType(KtTokens.VAR_KEYWORD) != null
}

override fun getValOrVarKeyword(): PsiElement? {

return findChildByType(VAL_VAR)
}

open fun isFullForm(): Boolean {

var entries: List<KtDestructuringDeclarationEntry> = getEntries()
return ! entries.isEmpty() && entries.get(0).getOwnValOrVarKeyword() != null
}

open fun getRPar(): PsiElement? {

return findChildByType(CLOSING_BRACES)
}

open fun getLPar(): PsiElement? {

return findChildByType(OPENING_BRACES)
}

open fun hasSquareBrackets(): Boolean {

return node.findChildByType(LBRACKET) != null
}

open fun getTrailingComma(): PsiElement? {

return org.jetbrains.kotlin.psi.psiUtil.getTrailingCommaByClosingElement(getRPar())
}

override fun getTypeReference(): KtTypeReference? {

return null
}

companion object {
private val OPENING_BRACES: TokenSet = TokenSet.create(LPAR,LBRACKET)

private val CLOSING_BRACES: TokenSet = TokenSet.create(RPAR,RBRACKET)
}
}
