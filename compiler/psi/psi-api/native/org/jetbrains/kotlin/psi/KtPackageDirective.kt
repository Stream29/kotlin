/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import org.jetbrains.kotlin.util.NativeCollections as Collections

import org.jetbrains.kotlin.psi.psiUtil.getQualifiedElementSelector as p1kOriginal_getQualifiedElementSelector

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.KtStubBasedElementTypes
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub
import org.jetbrains.kotlin.psi.stubs.elements.KtTokenSets



open class KtPackageDirective : KtModifierListOwnerStub<KotlinPlaceHolderStub<KtPackageDirective>> {
private var qualifiedNameCache: String? = null

protected override fun copyKotlinPsiStateTo(clone: com.intellij.psi.impl.PsiElementBase) {
super.copyKotlinPsiStateTo(clone)
(clone as KtPackageDirective).qualifiedNameCache = qualifiedNameCache
}

constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinPlaceHolderStub<KtPackageDirective>) : super(stub,KtStubBasedElementTypes.PACKAGE_DIRECTIVE) {


}

open fun getPackageNameExpression(): KtExpression? {

return KtStubbedPsiUtil.getStubOrPsiChild(this,KtTokenSets.INSIDE_DIRECTIVE_EXPRESSIONS,KtExpression.ARRAY_FACTORY)
}

open fun getPackageNames(): List<KtSimpleNameExpression> {

var nameExpression: KtExpression? = getPackageNameExpression()
if (nameExpression == null) return emptyList()
var packageNames: MutableList<KtSimpleNameExpression> = ArrayList()
while (nameExpression is KtQualifiedExpression) {
var qualifiedExpression: KtQualifiedExpression = (nameExpression as KtQualifiedExpression)
var selector: KtExpression? = qualifiedExpression.selectorExpression
if (selector is KtSimpleNameExpression) {
packageNames.add((selector as KtSimpleNameExpression))
}
nameExpression = qualifiedExpression.receiverExpression
}
if (nameExpression is KtSimpleNameExpression) {
packageNames.add((nameExpression as KtSimpleNameExpression))
}
Collections.reverse(packageNames)
return packageNames
}

open fun getLastReferenceExpression(): KtSimpleNameExpression? {

var nameExpression: KtExpression? = getPackageNameExpression()
if (nameExpression == null) return null
return ((nameExpression).p1kOriginal_getQualifiedElementSelector() as KtSimpleNameExpression?)
}

open val nameIdentifier: PsiElement?
get() {

var lastPart: KtSimpleNameExpression? = getLastReferenceExpression()
return (if (lastPart != null) lastPart.getIdentifier() else null)
}

override val name: String
get() {

var nameIdentifier: PsiElement? = nameIdentifier
return (if (nameIdentifier == null) "" else nameIdentifier.text)
}

open fun getNameAsName(): Name {

var nameIdentifier: PsiElement? = nameIdentifier
return (if (nameIdentifier == null) SpecialNames.ROOT_PACKAGE else Name.identifier(nameIdentifier.text))
}

open fun isRoot(): Boolean {

return name.length == 0
}

open fun getFqName(): FqName {

var qualifiedName: String = getQualifiedName()
return (if (qualifiedName.isEmpty()) FqName.ROOT else FqName(qualifiedName))
}

open fun getFqName(nameExpression: KtSimpleNameExpression): FqName {

return FqName(getQualifiedNameOf(nameExpression))
}

open fun setFqName(fqName: FqName): Unit {

KtPsiMutationService.getInstance().setPackageFqName(this,fqName)
}

open fun getQualifiedName(): String {

if (qualifiedNameCache == null) {
qualifiedNameCache = getQualifiedNameOf(null)
}
return qualifiedNameCache!!
}

private fun getQualifiedNameOf(nameExpression: KtSimpleNameExpression?): String {

var builder: StringBuilder = StringBuilder()
for (e in getPackageNames()) {
if (builder.length > 0) {
builder.append(".")
}
builder.append(e.referencedName)
if (e == nameExpression) break
}
return builder.toString()
}

open fun getPackageKeyword(): PsiElement? {

return findChildByType(KtTokens.PACKAGE_KEYWORD)
}

override fun subtreeChanged(): Unit {

qualifiedNameCache = null
}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitPackageDirective(this,data)
}
}
