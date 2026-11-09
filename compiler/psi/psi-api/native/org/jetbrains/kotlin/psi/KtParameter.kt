/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.navigation.ItemPresentationProviders
import com.intellij.psi.PsiElement
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.search.SearchScope
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.KtStubBasedElementTypes
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.stubs.KotlinParameterStub



open class KtParameter : KtNamedDeclarationStub<KotlinParameterStub>, KtCallableDeclaration, KtValVarKeywordOwner {
constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinParameterStub) : super(stub,KtStubBasedElementTypes.VALUE_PARAMETER) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitParameter(this,data)
}

override fun getTypeReference(): KtTypeReference? {

return getStubOrPsiChild(KtStubBasedElementTypes.TYPE_REFERENCE)
}

override fun setTypeReference(typeRef: KtTypeReference?): KtTypeReference? {

return KtPsiMutationService.getInstance().setParameterTypeReference(this,typeRef)
}

override fun getColon(): PsiElement? {

return findChildByType(KtTokens.COLON)
}

open fun getEqualsToken(): PsiElement? {

return findChildByType(KtTokens.EQ)
}

open fun hasDefaultValue(): Boolean {

var stub: KotlinParameterStub? = greenStub
if (stub != null) {
return stub.hasDefaultValue
}
return getDefaultValue() != null
}

open fun getDefaultValue(): KtExpression? {

var stub: KotlinParameterStub? = greenStub
if (stub != null) {
if (! stub.hasDefaultValue) {
return null
}
}
var equalsToken: PsiElement? = getEqualsToken()
return (if (equalsToken != null) PsiTreeUtil.getNextSiblingOfType(equalsToken,KtExpression ::class) else null)
}

open val isMutable: Boolean
get() {

var stub: KotlinParameterStub? = greenStub
if (stub != null) {
return stub.isMutable
}
return findChildByType<PsiElement>(KtTokens.VAR_KEYWORD) != null
}

open fun isVarArg(): Boolean {

var modifierList: KtModifierList? = getModifierList()
return modifierList != null && modifierList.hasModifier(KtTokens.VARARG_KEYWORD)
}

open fun hasValOrVar(): Boolean {

var stub: KotlinParameterStub? = greenStub
if (stub != null) {
return stub.hasValOrVar
}
return getValOrVarKeyword() != null
}

override fun getValOrVarKeyword(): PsiElement? {

var stub: KotlinParameterStub? = stub
if (stub != null && ! stub.hasValOrVar) {
return null
}
return findChildByType(KtTokens.VAL_VAR)
}

open fun getDestructuringDeclaration(): KtDestructuringDeclaration? {

if (stub != null) return null
return findChildByType(KtNodeTypes.DESTRUCTURING_DECLARATION)
}

override val presentation: ItemPresentation?
get() {

return ItemPresentationProviders.getItemPresentation(this)
}

open fun isLoopParameter(): Boolean {

return parent is KtForExpression
}

private fun <T : PsiElement> checkParentOfParentType(klass: kotlin.reflect.KClass<T>): Boolean {

var parent: PsiElement? = parent
if (parent == null) {
return false
}
return klass.isInstance(parent.parent)
}

open fun isCatchParameter(): Boolean {

return checkParentOfParentType(KtCatchClause ::class)
}

open fun isContextParameter(): Boolean {

return parent is KtContextParameterList
}

open fun isLambdaParameter(): Boolean {

return checkParentOfParentType(KtFunctionLiteral ::class)
}

open fun isFunctionTypeParameter(): Boolean {

return checkParentOfParentType(KtFunctionType ::class)
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

open fun getOwnerFunction(): KtDeclarationWithBody? {

var parent: PsiElement? = parentByStub
if (!(parent is KtParameterList)) return null
return ((parent as KtParameterList)).getOwnerFunction()
}

open fun getOwnerDeclaration(): KtDeclaration? {

var parent: PsiElement? = parent
if (parent is KtParameterList) {
return ((parent as KtParameterList)).getOwnerFunction()
}
if (parent is KtContextParameterList) {
return ((parent as KtContextParameterList)).ownerDeclaration
}
return null
}

override val useScope: SearchScope
get() {

var owner: KtExpression? = getOwnerFunction()
if (owner is KtPrimaryConstructor) {
if (hasValOrVar()) return super<KtNamedDeclarationStub>.useScope
owner = ((owner as KtPrimaryConstructor)).getContainingClassOrObject()
}
if (owner == null) {
owner = PsiTreeUtil.getParentOfType(this,KtExpression ::class)
}
return LocalSearchScope((if(owner != null) owner else this))
}

companion object {
val VAL_VAR_TOKEN_SET: TokenSet = KtTokens.VAL_VAR
}
}
