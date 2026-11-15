/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import org.jetbrains.kotlin.KtNodeTypes

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.navigation.ItemPresentationProviders
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.stubs.KotlinAnnotationEntryStub



open class KtAnnotationEntry : KtElementImplStub<KotlinAnnotationEntryStub>, KtCallElement {
constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinAnnotationEntryStub) : super(stub,KtNodeTypes.ANNOTATION_ENTRY) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitAnnotationEntry(this,data)
}

open fun getTypeReference(): KtTypeReference? {

var calleeExpression: KtConstructorCalleeExpression? = getCalleeExpression()
if (calleeExpression == null) {
return null
}
return calleeExpression.getTypeReference()
}

override fun getCalleeExpression(): KtConstructorCalleeExpression? {

return getStubOrPsiChild(KtNodeTypes.CONSTRUCTOR_CALLEE, KtConstructorCalleeExpression::class)
}

override fun getValueArgumentList(): KtValueArgumentList? {

return getStubOrPsiChild(KtNodeTypes.VALUE_ARGUMENT_LIST, KtValueArgumentList::class)
}

override fun getValueArguments(): List<out ValueArgument> {

var stub: KotlinAnnotationEntryStub? = stub
if (stub != null && ! stub.hasValueArguments) {
return emptyList<KtValueArgument>()
}
var list: KtValueArgumentList? = getValueArgumentList()
return (if (list != null) list.getArguments() else emptyList<KtValueArgument>())
}

override fun getLambdaArguments(): List<KtLambdaArgument> {

return emptyList()
}

override fun getTypeArguments(): List<KtTypeProjection> {

var typeArgumentList: KtTypeArgumentList? = getTypeArgumentList()
if (typeArgumentList == null) {
return emptyList()
}
return typeArgumentList.getArguments()
}

override fun getTypeArgumentList(): KtTypeArgumentList? {

var typeReference: KtTypeReference? = getTypeReference()
if (typeReference == null) {
return null
}
var typeElement: KtTypeElement? = typeReference.typeElement
if (typeElement is KtUserType) {
var userType: KtUserType = (typeElement as KtUserType)
return userType.getTypeArgumentList()
}
return null
}

open fun getAtSymbol(): PsiElement? {

return findChildByType(KtTokens.AT)
}

open val useSiteTarget: KtAnnotationUseSiteTarget?
get() {

var target: KtAnnotationUseSiteTarget? = getStubOrPsiChild(KtNodeTypes.ANNOTATION_TARGET, KtAnnotationUseSiteTarget::class)
if (target == null) {
var parent: PsiElement? = parentByStub
if (parent is KtAnnotation) {
return ((parent as KtAnnotation)).useSiteTarget
}
}
return target
}

open val shortName: Name?
get() {

var stub: KotlinAnnotationEntryStub? = greenStub
if (stub != null) {
var shortName: String? = stub.shortName
if (shortName != null) {
return Name.identifier(shortName)
}
return null
}
var typeReference: KtTypeReference? = getTypeReference()
assert(typeReference != null) { "Annotation entry hasn't typeReference " + text }
var typeElement: KtTypeElement? = typeReference!!.typeElement
if (typeElement is KtUserType) {
var userType: KtUserType = (typeElement as KtUserType)
var shortName: String? = userType.referencedName
if (shortName != null) {
return Name.identifier(shortName)
}
}
return null
}

override val presentation: ItemPresentation?
get() {

return ItemPresentationProviders.getItemPresentation(this)
}

companion object {
val EMPTY_ARRAY: Array<KtAnnotationEntry> = emptyArray()
}
}
