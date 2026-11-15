/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import org.jetbrains.kotlin.KtNodeTypes

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub



open class KtTypeParameterList : KtElementImplStub<KotlinPlaceHolderStub<KtTypeParameterList>> {
constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinPlaceHolderStub<KtTypeParameterList>) : super(stub,KtNodeTypes.TYPE_PARAMETER_LIST) {


}

open fun getParameters(): List<KtTypeParameter> {

return getStubOrPsiChildren(KtNodeTypes.TYPE_PARAMETER, KtTypeParameter.EMPTY_ARRAY).asList()
}

open fun addParameter(typeParameter: KtTypeParameter): KtTypeParameter {

return KtPsiMutationService.getInstance().appendTypeParameter(this,typeParameter)
}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitTypeParameterList(this,data)
}

open fun getTrailingComma(): PsiElement? {

return org.jetbrains.kotlin.psi.psiUtil.getTrailingCommaByClosingElement(findChildByType(KtTokens.GT))
}
}
