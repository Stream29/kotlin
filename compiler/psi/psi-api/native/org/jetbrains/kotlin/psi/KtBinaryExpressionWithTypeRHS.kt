/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.KtNodeTypes



open class KtBinaryExpressionWithTypeRHS : KtExpressionImpl, KtOperationExpression {
constructor(node: ASTNode) : super(node) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitBinaryWithTypeRHSExpression(this,data)
}

open fun getLeft(): KtExpression {

var left: KtExpression? = findChildByClass(KtExpression ::class)
assert(left != null)
return left!!
}

open fun getRight(): KtTypeReference? {

var node: ASTNode? = getOperationReference().node
while (node != null) {
var psi: PsiElement? = node.psi
if (psi is KtTypeReference) {
return (psi as KtTypeReference)
}
node = node.treeNext
}
return null
}

override fun getOperationReference(): KtSimpleNameExpression {

return (findChildByType<PsiElement>(KtNodeTypes.OPERATION_REFERENCE) as KtSimpleNameExpression)
}
}
