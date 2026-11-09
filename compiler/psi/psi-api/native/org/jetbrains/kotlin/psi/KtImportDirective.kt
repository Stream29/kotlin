/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.KtStubBasedElementTypes
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.stubs.KotlinImportDirectiveStub
import org.jetbrains.kotlin.psi.KtImportInfo.ImportContent
import org.jetbrains.kotlin.psi.stubs.elements.KtTokenSets
import org.jetbrains.kotlin.resolve.ImportPath
import kotlin.concurrent.Volatile



open class KtImportDirective : KtElementImplStub<KotlinImportDirectiveStub>, KtImportInfo {
constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinImportDirectiveStub) : super(stub,KtStubBasedElementTypes.IMPORT_DIRECTIVE) {


}

@Volatile private var importedFqNameCache: FqName? = null

protected override fun copyKotlinPsiStateTo(clone: com.intellij.psi.impl.PsiElementBase) {
super.copyKotlinPsiStateTo(clone)
(clone as KtImportDirective).importedFqNameCache = importedFqNameCache
}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitImportDirective(this,data)
}

open fun getImportedReference(): KtExpression? {

var references: Array<KtExpression?> = getStubOrPsiChildren(KtTokenSets.INSIDE_DIRECTIVE_EXPRESSIONS,KtExpression.ARRAY_FACTORY)
if (references.size > 0) {
return references[0]
}
return null
}

open fun getAlias(): KtImportAlias? {

return getStubOrPsiChild(KtStubBasedElementTypes.IMPORT_ALIAS)
}

override val aliasName: String?
get() {

var alias: KtImportAlias? = getAlias()
return (if (alias != null) alias.name else null)
}

override val isAllUnder: Boolean
get() {

var stub: KotlinImportDirectiveStub? = greenStub
if (stub != null) {
return stub.isAllUnder
}
return node.findChildByType(KtTokens.MUL) != null
}

override val importContent: ImportContent?
get() {

var reference: KtExpression? = getImportedReference()
if (reference == null) return null
return ImportContent.ExpressionBased(reference)
}

override val importedFqName: FqName?
get() {

var stub: KotlinImportDirectiveStub? = greenStub
if (stub != null) {
return stub.importedFqName
}
var importedFqName: FqName? = this.importedFqNameCache
if (importedFqName != null) return importedFqName
var importedReference: KtExpression? = getImportedReference()
if (importedReference == null) return null
importedFqName = fqNameFromExpression(importedReference)
this.importedFqNameCache = importedFqName
return importedFqName
}

open fun getImportPath(): ImportPath? {

var importFqn: FqName? = importedFqName
if (importFqn == null) {
return null
}
var alias: Name? = null
var aliasName: String? = aliasName
if (aliasName != null) {
alias = Name.identifier(aliasName)
}
return ImportPath(importFqn,isAllUnder,alias)
}

open fun isValidImport(): Boolean {

var stub: KotlinImportDirectiveStub? = greenStub
if (stub != null) {
return stub.isValid
}
return ! PsiTreeUtil.hasErrorElements(this)
}

override fun subtreeChanged(): Unit {

super.subtreeChanged()
importedFqNameCache = null
}

companion object {
private fun fqNameFromExpression(expression: KtExpression?): FqName? {

if (expression == null) {
return null
}
if (expression is KtDotQualifiedExpression) {
var dotQualifiedExpression: KtDotQualifiedExpression = (expression as KtDotQualifiedExpression)
var parentFqn: FqName? = fqNameFromExpression(dotQualifiedExpression.receiverExpression)
var child: Name? = nameFromExpression(dotQualifiedExpression.selectorExpression)
if (child == null) {
return parentFqn
}
if (parentFqn != null) {
return parentFqn.child(child)
}
return null
}
else if (expression is KtSimpleNameExpression) {
var simpleNameExpression: KtSimpleNameExpression = (expression as KtSimpleNameExpression)
return FqName.topLevel(simpleNameExpression.getReferencedNameAsName())
}
else {
throw IllegalArgumentException("Can't construct fqn for: " + (expression!!)::class.toString())
}
}

private fun nameFromExpression(expression: KtExpression?): Name? {

if (expression == null) {
return null
}
if (expression is KtSimpleNameExpression) {
return ((expression as KtSimpleNameExpression)).getReferencedNameAsName()
}
else {
throw IllegalArgumentException("Can't construct name for: " + (expression!!)::class.toString())
}
}
}
}
