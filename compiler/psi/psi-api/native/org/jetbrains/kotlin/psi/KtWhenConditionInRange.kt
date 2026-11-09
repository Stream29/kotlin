/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.resolution.KtResolvableCall



open class KtWhenConditionInRange : KtWhenCondition, KtResolvableCall {
constructor(node: ASTNode) : super(node) {


}

open fun isNegated(): Boolean {

return getOperationReference().node.findChildByType(KtTokens.NOT_IN) != null
}

open fun getRangeExpression(): KtExpression? {

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

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitWhenConditionInRange(this,data)
}

open fun getOperationReference(): KtOperationReferenceExpression {

return (findChildByType<PsiElement>(KtNodeTypes.OPERATION_REFERENCE) as KtOperationReferenceExpression)
}
}
