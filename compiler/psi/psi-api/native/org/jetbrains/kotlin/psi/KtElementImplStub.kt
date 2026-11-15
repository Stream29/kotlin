/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import org.jetbrains.kotlin.util.NativeArrays as Arrays

import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.lang.ASTNode
import com.intellij.lang.Language
import com.intellij.psi.*
import com.intellij.psi.impl.source.PsiFileImpl
import com.intellij.psi.impl.PsiElementBase
import com.intellij.psi.tree.IElementType
import com.intellij.psi.stubs.StubElement
import com.intellij.util.IncorrectOperationException
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.stubs.elements.KtTokenSets
import org.jetbrains.kotlin.psi.impl.NativePsiCloneFactories



open class KtElementImplStub<T : StubElement<*>> : StubBasedPsiElementBase<T>, KtElement, StubBasedPsiElement<T> {
@Suppress("UNCHECKED_CAST")
protected open fun copyKotlinPsiStateTo(clone: PsiElementBase) {
copyStubStateTo(clone as StubBasedPsiElementBase<T>)
}

@Suppress("UNCHECKED_CAST")
protected override fun createUnlinkedUserDataClone(): KtElementImplStub<T> =
NativePsiCloneFactories.copy(this) { copyKotlinPsiStateTo(it) } as KtElementImplStub<T>

constructor(stub: T, nodeType: IElementType) : super(stub,nodeType) {


}

constructor(node: ASTNode) : super(node) {


}

override val language: Language
get() {

return KotlinLanguage.INSTANCE
}

override fun toString(): String {

return iElementType.toString()
}

override val iElementType: IElementType
get() {
return elementTypeImpl
}

final override fun accept(visitor: PsiElementVisitor): Unit {

if (visitor is KtVisitor<*, *>) {
accept((visitor as KtVisitor<Any?, Any?>),null)
}
else {
visitor.visitElement(this)
}
}

override fun getContainingKtFile(): KtFile {

var file: PsiFile? = containingFile
if (!(file is KtFile)) {
var fileString: String = ""
if (file!!.isValid) {
try {
fileString = " " + file.text
}
catch (e: Exception) {

}
}
var nodeString: String = ((if(file is PsiFileImpl)(" node = " + node) else ""))
throw IllegalStateException("KtElement not inside KtFile: " + file + fileString + " of type " + (file!!)::class + " for element " + this + " of type " + this::class + nodeString)
}
return (file as KtFile)
}

override fun <D> acceptChildren(visitor: KtVisitor<Nothing?,D>, data: D): Unit {

var child: PsiElement? = firstChild
while (child != null) {
if (child is KtElement) {
((child as KtElement)).accept(visitor,data)
}
child = child.nextSibling
}
}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitKtElement(this,data)
}

override fun delete(): Unit {

deleteWithMutationService(this) { it.deleteElement(this) }
}

override fun rawDelete(): Unit {

super<StubBasedPsiElementBase>.delete()
}

override val reference: PsiReference?
get() {

var references: Array<PsiReference> = references
return (if ((references.size > 0)) references[0] else null)
}

override val references: Array<PsiReference>
get() {

return KotlinReferenceProvidersService.getReferencesFromProviders(this)
}

@Suppress("UNCHECKED_CAST")
protected fun getExpressionsFromStub(): Array<KtExpression> {
val stub: T? = stub
if (stub == null) return KtExpression.EMPTY_ARRAY
return stub.getChildrenByType(KtTokenSets.STUBBED_EXPRESSIONS, KtExpression.EMPTY_ARRAY) as Array<KtExpression>
}

protected fun getExpressionFromStub(): KtExpression? {
val expressions: Array<KtExpression> = getExpressionsFromStub()
return if (expressions.size != 0) expressions[0] else null
}

override fun getPsiOrParent(): KtElement {

return this
}
}
