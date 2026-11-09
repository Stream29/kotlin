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



open class KtValueArgumentList : KtElementImplStub<KotlinPlaceHolderStub<KtValueArgumentList>> {
constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinPlaceHolderStub<KtValueArgumentList>) : super(stub,KtStubBasedElementTypes.VALUE_ARGUMENT_LIST) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitValueArgumentList(this,data)
}

open fun getArguments(): List<KtValueArgument> {

return getStubOrPsiChildrenAsList(KtStubBasedElementTypes.VALUE_ARGUMENT)
}

open fun getRightParenthesis(): PsiElement? {

return findChildByType(KtTokens.RPAR)
}

open fun getLeftParenthesis(): PsiElement? {

return findChildByType(KtTokens.LPAR)
}

open fun addArgument(argument: KtValueArgument): KtValueArgument {

return KtPsiMutationService.getInstance().appendValueArgument(this,argument)
}

open fun addArgumentAfter(argument: KtValueArgument, anchor: KtValueArgument?): KtValueArgument {

return KtPsiMutationService.getInstance().insertValueArgumentAfter(this,argument,anchor)
}

open fun addArgumentBefore(argument: KtValueArgument, anchor: KtValueArgument?): KtValueArgument {

return KtPsiMutationService.getInstance().insertValueArgumentBefore(this,argument,anchor)
}

open fun removeArgument(argument: KtValueArgument): Unit {

KtPsiMutationService.getInstance().deleteValueArgument(this,argument)
}

open fun removeArgument(index: Int): Unit {

KtPsiMutationService.getInstance().deleteValueArgument(this,index)
}

open fun getTrailingComma(): PsiElement? {

return org.jetbrains.kotlin.psi.psiUtil.getTrailingCommaByClosingElement(getRightParenthesis())
}
}
