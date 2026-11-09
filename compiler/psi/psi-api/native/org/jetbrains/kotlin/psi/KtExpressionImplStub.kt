/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import org.jetbrains.kotlin.psi.psiUtil.parentSubstitute as p1kOriginal_parentSubstitute

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import com.intellij.util.IncorrectOperationException



abstract class KtExpressionImplStub<T : StubElement<*>> : KtElementImplStub<T>, KtExpression {
constructor(stub: T, nodeType: IStubElementType<*, *>) : super(stub,nodeType) {


}

constructor(node: ASTNode) : super(node) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitExpression(this,data)
}

override fun replace(newElement: PsiElement): PsiElement {

return KtPsiMutationService.getInstance().replaceExpression(this,newElement,true,this :: rawReplace)
}

open fun rawReplace(newElement: PsiElement): PsiElement {

return super<KtElementImplStub>.replace(newElement)
}

override val parent: PsiElement?
get() {

var substitute: PsiElement? = (this).p1kOriginal_parentSubstitute
return (if (substitute != null) substitute else super<KtElementImplStub>.parent)
}
}
