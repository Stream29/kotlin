/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.lang.Language
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiReference
import org.jetbrains.kotlin.psi.impl.NativePsiCloneFactories
import com.intellij.util.IncorrectOperationException
import org.jetbrains.kotlin.idea.KotlinLanguage



open class KtElementImpl : ASTWrapperPsiElement, KtElement {
protected override fun createUnlinkedUserDataClone(): KtElementImpl =
NativePsiCloneFactories.copy(this) { copyAstWrapperStateTo(it as KtElementImpl) } as KtElementImpl

constructor(node: ASTNode) : super(node) {


}

override val language: Language
get() {

return KotlinLanguage.INSTANCE
}

override fun toString(): String {

return node.elementType.toString()
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
var fileString: String? = (if ((file != null && file.isValid)) (" " + file.text) else "")
throw IllegalStateException("KtElement not inside KtFile: " + file + fileString + " for element " + this + " of type " + this::class + " node = " + node)
}
return (file as KtFile)
}

override fun <D> acceptChildren(visitor: KtVisitor<Nothing?,D>, data: D): Unit {

KtPsiUtil.visitChildren(this,visitor,data)
}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitKtElement(this,data)
}

override fun delete(): Unit {

KtPsiMutationService.getInstance().deleteElement(this)
}

override fun rawDelete(): Unit {

super<ASTWrapperPsiElement>.delete()
}

override val reference: PsiReference?
get() {

var references: Array<PsiReference> = references
if (references.size == 1) return references[0]
else return null
}

override val references: Array<PsiReference>
get() {

return KotlinReferenceProvidersService.getReferencesFromProviders(this)
}

override fun getPsiOrParent(): KtElement {

return this
}
}
