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



open class KtTypeArgumentList : KtElementImplStub<KotlinPlaceHolderStub<KtTypeArgumentList>> {
constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinPlaceHolderStub<KtTypeArgumentList>) : super(stub,KtNodeTypes.TYPE_ARGUMENT_LIST) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitTypeArgumentList(this,data)
}

open fun getArguments(): List<KtTypeProjection> {

return getStubOrPsiChildren(KtNodeTypes.TYPE_PROJECTION, KtTypeProjection.EMPTY_ARRAY).asList()
}

open fun addArgument(typeArgument: KtTypeProjection): KtTypeProjection {

return KtPsiMutationService.getInstance().appendTypeArgument(this,typeArgument)
}

open fun getTrailingComma(): PsiElement? {

return org.jetbrains.kotlin.psi.psiUtil.getTrailingCommaByClosingElement(findChildByType(KtTokens.GT))
}
}
