/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.search.SearchScope
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolution.KtResolvableCall
import org.jetbrains.kotlin.lexer.KtTokens.Companion.EQ



open class KtDestructuringDeclarationEntry : KtNamedDeclarationNotStubbed, KtVariableDeclaration, KtResolvableCall {
constructor(node: ASTNode) : super(node) {


}

override fun getTypeReference(): KtTypeReference? {

return org.jetbrains.kotlin.psi.typeRefHelpers.getTypeReference(this)
}

override fun setTypeReference(typeRef: KtTypeReference?): KtTypeReference? {

return KtPsiMutationService.getInstance().setDestructuringDeclarationEntryTypeReference(this,typeRef)
}

override fun getColon(): PsiElement? {

return findChildByType(KtTokens.COLON)
}

override fun getValueParameterList(): KtParameterList? {

return null
}

override fun getValueParameters(): List<KtParameter> {

return emptyList()
}

override fun getReceiverTypeReference(): KtTypeReference? {

return null
}

override fun getTypeParameterList(): KtTypeParameterList? {

return null
}

override fun getTypeConstraintList(): KtTypeConstraintList? {

return null
}

override fun getTypeConstraints(): List<KtTypeConstraint> {

return emptyList()
}

override fun getTypeParameters(): List<KtTypeParameter> {

return emptyList()
}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitDestructuringDeclarationEntry(this,data)
}

override val isVar: Boolean
get() {

return findChildByType<PsiElement>(KtTokens.VAR_KEYWORD) != null || getParentNode().findChildByType(KtTokens.VAR_KEYWORD) != null
}

override fun getInitializer(): KtNameReferenceExpression? {

return PsiTreeUtil.getNextSiblingOfType(getEqualsToken(),KtNameReferenceExpression ::class)
}

open fun getEqualsToken(): PsiElement? {

return findChildByType(EQ)
}

override fun hasInitializer(): Boolean {

return getInitializer() != null
}

private fun getParentNode(): ASTNode {

var parent: ASTNode = node.treeParent!!
if (parent.elementType == TokenType.ERROR_ELEMENT) {
parent = parent.treeParent!!
}
assert(parent.elementType == KtNodeTypes.DESTRUCTURING_DECLARATION) { "parent is " + parent.elementType }
return parent
}

override fun getValOrVarKeyword(): PsiElement? {

var node: ASTNode? = getParentNode().findChildByType(KtTokens.VAL_VAR)
if (node == null) return null
return node.psi
}

open fun getOwnValOrVarKeyword(): PsiElement? {

return findChildByType(KtTokens.VAL_VAR)
}

override fun getFqName(): FqName? {

return null
}

override val useScope: SearchScope
get() {

var enclosingBlock: KtElement? = KtPsiUtil.getEnclosingElementForLocalDeclaration(this,false)
if (enclosingBlock is KtParameter) {
enclosingBlock = KtPsiUtil.getEnclosingElementForLocalDeclaration((enclosingBlock as KtParameter),false)
}
if (enclosingBlock != null) return LocalSearchScope(enclosingBlock)
return super<KtNamedDeclarationNotStubbed>.useScope
}
}
