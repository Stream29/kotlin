/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import org.jetbrains.kotlin.util.NativeArrays as Arrays

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.resolution.KtResolvableCall



open class KtBinaryExpression : KtExpressionImpl, KtOperationExpression, KtResolvableCall {
constructor(node: ASTNode) : super(node) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitBinaryExpression(this,data)
}

open fun getLeft(): KtExpression? {

var node: ASTNode? = getOperationReference().node.treePrev
while (node != null) {
var psi: PsiElement? = node.psi
if (psi is KtExpression) {
return (psi as KtExpression)
}
node = node.treePrev
}
return null
}

open fun getRight(): KtExpression? {

var node: ASTNode? = getOperationReference().node.treeNext
while (node != null) {
var psi: PsiElement? = node.psi
if (psi is KtExpression) {
return (psi as KtExpression)
}
node = node.treeNext
}
return null
}

override fun getOperationReference(): KtOperationReferenceExpression {

var operationReference: PsiElement? = findChildByType(KtNodeTypes.OPERATION_REFERENCE)
if (operationReference == null) {
throw NullPointerException("No operation reference for binary expression: " + Arrays.toString(children))
}
return (operationReference as KtOperationReferenceExpression)
}

open fun getOperationToken(): IElementType {

return getOperationReference().getReferencedNameElementType()
}
}
