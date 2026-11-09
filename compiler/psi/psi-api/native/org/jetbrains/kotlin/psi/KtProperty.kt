/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import org.jetbrains.kotlin.psi.psiUtil.containingClassOrScript as p1kOriginal_containingClassOrScript

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.navigation.ItemPresentationProviders
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.KtStubBasedElementTypes
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.stubs.KotlinPropertyStub
import org.jetbrains.kotlin.KtNodeTypes.Companion.PROPERTY_DELEGATE
import org.jetbrains.kotlin.lexer.KtTokens.Companion.EQ
import org.jetbrains.kotlin.psi.psiUtil.isKtFile



open class KtProperty : KtTypeParameterListOwnerStub<KotlinPropertyStub>, KtVariableDeclaration {
constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinPropertyStub) : super(stub,KtStubBasedElementTypes.PROPERTY) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitProperty(this,data)
}

override val isVar: Boolean
get() {

var stub: KotlinPropertyStub? = greenStub
if (stub != null) {
return stub.isVar
}
return node.findChildByType(KtTokens.VAR_KEYWORD) != null
}

open fun isLocal(): Boolean {

return ! isTopLevel && ! isMember()
}

open fun isMember(): Boolean {

return (this).p1kOriginal_containingClassOrScript != null
}

open val isTopLevel: Boolean
get() {

var stub: KotlinPropertyStub? = greenStub
if (stub != null) {
return stub.isTopLevel
}
return isKtFile(parent)
}

override fun getValueParameterList(): KtParameterList? {

return null
}

override fun getValueParameters(): List<KtParameter> {

return emptyList()
}

override fun getReceiverTypeReference(): KtTypeReference? {

var stub: KotlinPropertyStub? = greenStub
if (stub != null) {
if (! stub.isExtension) {
return null
}
else {
return getStubOrPsiChild(KtStubBasedElementTypes.TYPE_REFERENCE)
}
}
return getReceiverTypeRefByTree()
}

private fun getReceiverTypeRefByTree(): KtTypeReference? {

var node: ASTNode? = node.firstChildNode
while (node != null) {
var tt: IElementType? = node.elementType
if (tt == KtTokens.COLON) break
if (tt == KtNodeTypes.TYPE_REFERENCE) {
return (node.psi as KtTypeReference)
}
node = node.treeNext
}
return null
}

override fun getTypeReference(): KtTypeReference? {

var stub: KotlinPropertyStub? = greenStub
if (stub != null) {
if (! stub.hasReturnTypeRef) {
return null
}
else {
var typeReferences: List<KtTypeReference> = getStubOrPsiChildrenAsList(KtStubBasedElementTypes.TYPE_REFERENCE)
var returnTypeRefPositionInPsi: Int = (if (stub.isExtension) 1 else 0)
if (typeReferences.size <= returnTypeRefPositionInPsi) {
LOG.error("Invalid stub structure built for property:\n" + text)
return null
}
return typeReferences.get(returnTypeRefPositionInPsi)
}
}
return org.jetbrains.kotlin.psi.typeRefHelpers.getTypeReference(this)
}

override fun setTypeReference(typeRef: KtTypeReference?): KtTypeReference? {

return KtPsiMutationService.getInstance().setPropertyTypeReference(this,typeRef)
}

override fun getColon(): PsiElement? {

return findChildByType(KtTokens.COLON)
}

open fun getEqualsToken(): PsiElement? {

return findChildByType(KtTokens.EQ)
}

open fun getAccessors(): List<KtPropertyAccessor> {

return getStubOrPsiChildrenAsList(KtStubBasedElementTypes.PROPERTY_ACCESSOR)
}

open fun getGetter(): KtPropertyAccessor? {

for (accessor in getAccessors()) {
if (accessor.isGetter) return accessor
}
return null
}

open fun getSetter(): KtPropertyAccessor? {

for (accessor in getAccessors()) {
if (accessor.isSetter) return accessor
}
return null
}

open fun getFieldDeclaration(): KtBackingField? {

for (field in getStubOrPsiChildrenAsList(KtStubBasedElementTypes.BACKING_FIELD)) {
return field
}
return null
}

open fun hasDelegate(): Boolean {

var stub: KotlinPropertyStub? = greenStub
if (stub != null) {
return stub.hasDelegate
}
return delegate != null
}

open val delegate: KtPropertyDelegate?
get() {

var stub: KotlinPropertyStub? = greenStub
if (stub != null && ! stub.hasDelegate) {
return null
}
return (findChildByType<PsiElement>(PROPERTY_DELEGATE) as KtPropertyDelegate?)
}

open fun hasDelegateExpression(): Boolean {

var stub: KotlinPropertyStub? = greenStub
if (stub != null) {
return stub.hasDelegateExpression
}
return getDelegateExpression() != null
}

open fun getDelegateExpression(): KtExpression? {

var stub: KotlinPropertyStub? = greenStub
if (stub != null && ! stub.hasDelegateExpression) {
return null
}
var delegate: KtPropertyDelegate? = delegate
if (delegate != null) {
return delegate.getExpression()
}
return null
}

override fun hasInitializer(): Boolean {

var stub: KotlinPropertyStub? = greenStub
if (stub != null) {
return stub.hasInitializer
}
return getInitializer() != null
}

override fun getInitializer(): KtExpression? {

var stub: KotlinPropertyStub? = greenStub
if (stub != null) {
if (! stub.hasInitializer) {
return null
}
}
return PsiTreeUtil.getNextSiblingOfType(findChildByType(EQ),KtExpression ::class)
}

open fun hasDelegateExpressionOrInitializer(): Boolean {

return hasDelegateExpression() || hasInitializer()
}

open fun setInitializer(initializer: KtExpression?): KtExpression? {

return KtPsiMutationService.getInstance().setPropertyInitializer(this,initializer)
}

open fun getDelegateExpressionOrInitializer(): KtExpression? {

var expression: KtExpression? = getDelegateExpression()
if (expression == null) {
return getInitializer()
}
return expression
}

override fun getValOrVarKeyword(): PsiElement {

var element: PsiElement? = findChildByType(KtTokens.VAL_VAR)
assert(element != null) { "Val or var should always exist for property" + this.text }
return element!!
}

override val presentation: ItemPresentation?
get() {

return ItemPresentationProviders.getItemPresentation(this)
}

open fun shouldChangeModificationCount(place: PsiElement): Boolean {

return false
}

open fun hasBody(): Boolean {

if (hasDelegateExpressionOrInitializer()) return true
var getter: KtPropertyAccessor? = getGetter()
if (getter != null && getter.hasBody()) {
return true
}
var setter: KtPropertyAccessor? = getSetter()
if (setter != null && setter.hasBody()) {
return true
}
return false
}

companion object {
private val LOG: Logger = Logger.getInstance(KtProperty ::class)
}
}
