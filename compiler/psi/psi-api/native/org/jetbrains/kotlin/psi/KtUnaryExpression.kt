/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.resolution.KtResolvableCall



abstract class KtUnaryExpression : KtExpressionImpl, KtOperationExpression, KtResolvableCall {
constructor(node: ASTNode) : super(node) {


}

abstract fun getBaseExpression(): KtExpression?

override fun getOperationReference(): KtSimpleNameExpression {

return findChildByType<KtSimpleNameExpression>(KtNodeTypes.OPERATION_REFERENCE)!!
}

open fun getOperationToken(): IElementType {

return getOperationReference().getReferencedNameElementType()
}
}
