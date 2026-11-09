/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import org.jetbrains.kotlin.psi.psiUtil.collectAnnotationEntriesFromStubOrPsi as p1kOriginal_collectAnnotationEntriesFromStubOrPsi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.tree.TokenSet
import org.jetbrains.kotlin.KtStubBasedElementTypes
import org.jetbrains.kotlin.lexer.KtModifierKeywordToken
import org.jetbrains.kotlin.psi.stubs.KotlinModifierListStub



abstract class KtModifierList : KtElementImplStub<KotlinModifierListStub>, KtAnnotationsContainer {
constructor(stub: KotlinModifierListStub, nodeType: IStubElementType<*, *>) : super(stub,nodeType) {


}

constructor(node: ASTNode) : super(node) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitModifierList(this,data)
}

override fun getAnnotations(): List<KtAnnotation> {

return getStubOrPsiChildrenAsList(KtStubBasedElementTypes.ANNOTATION)
}

open fun getContextParameterList(): KtContextParameterList? {

return getStubOrPsiChild(KtStubBasedElementTypes.CONTEXT_PARAMETER_LIST)
}

open fun getContextReceiverList(): KtContextReceiverList? {

return (getContextParameterList() as KtContextReceiverList?)
}

open fun getContextParameterLists(): List<KtContextParameterList> {

return getStubOrPsiChildrenAsList(KtStubBasedElementTypes.CONTEXT_PARAMETER_LIST)
}

open fun getContextReceiverLists(): List<KtContextReceiverList> {

return (getContextParameterLists() as List<KtContextReceiverList>)
}

override fun getAnnotationEntries(): List<KtAnnotationEntry> {

return (this).p1kOriginal_collectAnnotationEntriesFromStubOrPsi()
}

open fun hasModifier(tokenType: KtModifierKeywordToken): Boolean {

var stub: KotlinModifierListStub? = stub
if (stub != null) {
return stub.hasModifier(tokenType)
}
return getModifier(tokenType) != null
}

open fun getModifier(tokenType: KtModifierKeywordToken): PsiElement? {

return findChildByType(tokenType)
}

open fun getModifier(tokenTypes: TokenSet): PsiElement? {

return findChildByType(tokenTypes)
}

open fun getOwner(): PsiElement? {

return parentByStub
}

override fun deleteChildInternal(child: ASTNode): Unit {

super.deleteChildInternal(child)
if (firstChild == null) {
delete()
}
}
}
