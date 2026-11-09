/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.util.IncorrectOperationException
import org.jetbrains.kotlin.psi.stubs.KotlinConstantExpressionStub
import org.jetbrains.kotlin.psi.utils.toConstantExpressionElementType



open class KtConstantExpression : KtElementImplStub<KotlinConstantExpressionStub>, KtExpression {
constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinConstantExpressionStub) : super(stub,stub.kind.toConstantExpressionElementType()) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitConstantExpression(this,data)
}

override fun replace(newElement: PsiElement): PsiElement {

return KtPsiMutationService.getInstance().replaceExpression(this,newElement,true) { super<KtElementImplStub>.replace(it) }
}

override val text: String
get() {

var stub: KotlinConstantExpressionStub? = greenStub
if (stub != null) {
return stub.value
}
return super<KtElementImplStub>.text
}
}
