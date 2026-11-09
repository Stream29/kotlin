/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.KtStubBasedElementTypes
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub



open class KtParameterList : KtElementImplStub<KotlinPlaceHolderStub<KtParameterList>> {
constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinPlaceHolderStub<KtParameterList>) : super(stub,KtStubBasedElementTypes.VALUE_PARAMETER_LIST) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitParameterList(this,data)
}

open fun getParameters(): List<KtParameter> {

return getStubOrPsiChildrenAsList(KtStubBasedElementTypes.VALUE_PARAMETER)
}

open fun addParameter(parameter: KtParameter): KtParameter {

return KtPsiMutationService.getInstance().appendParameter(this,parameter)
}

open fun addParameterBefore(parameter: KtParameter, anchor: KtParameter?): KtParameter {

return KtPsiMutationService.getInstance().insertParameterBefore(this,parameter,anchor)
}

open fun addParameterAfter(parameter: KtParameter, anchor: KtParameter?): KtParameter {

return KtPsiMutationService.getInstance().insertParameterAfter(this,parameter,anchor)
}

open fun removeParameter(parameter: KtParameter): Unit {

KtPsiMutationService.getInstance().deleteParameter(this,parameter)
}

open fun removeParameter(index: Int): Unit {

KtPsiMutationService.getInstance().deleteParameter(this,index)
}

open fun getOwnerFunction(): KtDeclarationWithBody? {

var parent: PsiElement? = parentByStub
if (!(parent is KtDeclarationWithBody)) return null
return (parent as KtDeclarationWithBody)
}

open fun getRightParenthesis(): PsiElement? {

return findChildByType(KtTokens.RPAR)
}

open fun getLeftParenthesis(): PsiElement? {

return findChildByType(KtTokens.LPAR)
}

open fun getFirstComma(): PsiElement? {

return findChildByType(KtTokens.COMMA)
}

open fun getTrailingComma(): PsiElement? {

var parentElement: PsiElement? = parent
if (parentElement is KtFunctionLiteral) {
return org.jetbrains.kotlin.psi.psiUtil.getTrailingCommaByElementsList(this)
}
else {
return org.jetbrains.kotlin.psi.psiUtil.getTrailingCommaByClosingElement(getRightParenthesis())
}
}
}
