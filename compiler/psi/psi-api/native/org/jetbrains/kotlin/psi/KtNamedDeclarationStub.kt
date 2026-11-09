/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.platform.native.NativeReferenceArrays

import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject as p1kOriginal_containingClassOrObject

import com.intellij.lang.ASTNode
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.search.SearchScope
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.IncorrectOperationException
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.stubs.KotlinStubWithFqName



abstract class KtNamedDeclarationStub<T : KotlinStubWithFqName<*>> : KtDeclarationStub<T>, KtNamedDeclaration {
constructor(stub: T, nodeType: IStubElementType<*, *>) : super(stub,nodeType) {


}

constructor(node: ASTNode) : super(node) {


}

override val name: String?
get() {

var stub: T? = greenStub
if (stub != null) {
return stub.name
}
var identifier: PsiElement? = nameIdentifier
if (identifier != null) {
var text: String? = identifier.text
return (if (text != null) KtPsiUtil.unquoteIdentifier(text) else null)
}
else {
return null
}
}

override fun getNameAsName(): Name? {

var name: String? = name
return (if (name != null) Name.identifier(name) else null)
}

override fun getNameAsSafeName(): Name {

return KtPsiUtil.safeName(name)
}

override val nameIdentifier: PsiElement?
get() {

return findChildByType(KtTokens.IDENTIFIER)
}

override fun setName(name: String): PsiElement? {

return KtPsiMutationService.getInstance().setNamedDeclarationStubName(this,name)
}

override val textOffset: Int
get() {

var identifier: PsiElement? = nameIdentifier
return (if (identifier != null) identifier.textRange!!.startOffset else textRange.startOffset)
}

override val useScope: SearchScope
get() {

var enclosingBlock: KtElement? = KtPsiUtil.getEnclosingElementForLocalDeclaration(this,false)
if (enclosingBlock != null) {
var enclosingParent: PsiElement? = enclosingBlock.parent
var containingObject: KtObjectDeclaration? = PsiTreeUtil.getParentOfType(this,KtObjectDeclaration ::class)
if (containingObject != null && containingObject.isObjectLiteral) {
var declaration: KtDeclarationWithInitializer? = PsiTreeUtil.getParentOfType(containingObject,KtDeclarationWithInitializer ::class)
if (declaration != null && declaration.hasModifier(KtTokens.PRIVATE_KEYWORD)) {
if (enclosingParent is KtFile) {
return LocalSearchScope(enclosingParent)
}
if (enclosingParent is KtObjectDeclaration && ((enclosingParent as KtObjectDeclaration)).isCompanion()) {
return LocalSearchScope(enclosingParent.parent!!)
}
}
}
if (enclosingParent is KtContainerNode) {
enclosingParent = enclosingParent.parent
}
if (enclosingBlock is KtBlockExpression && enclosingParent is KtDoWhileExpression) {
var condition: KtExpression? = ((enclosingParent as KtDoWhileExpression)).getCondition()
if (condition != null) return LocalSearchScope(arrayOf<PsiElement>(enclosingBlock,condition).also {
NativeReferenceArrays.register(it,PsiElement::class)
})
}
return LocalSearchScope(enclosingBlock)
}
var parent: PsiElement? = parent
var grandParent: PsiElement? = (if (parent != null) parent.parent else null)
if (parent is KtBlockExpression && grandParent is KtScript) {
return LocalSearchScope(containingFile!!)
}
if (hasModifier(KtTokens.PRIVATE_KEYWORD)) {
var containingClass: KtElement? = PsiTreeUtil.getParentOfType(this,KtClassOrObject ::class)
if (containingClass is KtObjectDeclaration && ((containingClass as KtObjectDeclaration)).isCompanion()) {
var companionObjectClass: KtElement? = PsiTreeUtil.getParentOfType(containingClass,KtClassOrObject ::class)
if (companionObjectClass != null) {
containingClass = companionObjectClass
}
}
if (containingClass != null) {
return LocalSearchScope(containingClass)
}
var ktFile: KtFile = getContainingKtFile()
if (this is KtClassOrObject) {
var superScope: SearchScope = super<KtDeclarationStub>.useScope
return LocalSearchScope(ktFile).intersectWith(superScope)
}
else {
return LocalSearchScope(ktFile)
}
}
var scope: SearchScope = super<KtDeclarationStub>.useScope
var classOrObject: KtClassOrObject? = (this).p1kOriginal_containingClassOrObject
if (classOrObject != null) {
scope = scope.intersectWith(classOrObject.useScope)
}
return scope
}

override fun getFqName(): FqName? {

var stub: T? = greenStub
if (stub != null) {
return stub.fqName
}
return KtNamedDeclarationUtil.getFQName(this)
}
}
