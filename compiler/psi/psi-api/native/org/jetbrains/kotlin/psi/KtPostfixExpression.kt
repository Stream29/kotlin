/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.util.PsiTreeUtil



open class KtPostfixExpression : KtUnaryExpression {
constructor(node: ASTNode) : super(node) {


}

override fun getBaseExpression(): KtExpression? {

return PsiTreeUtil.getPrevSiblingOfType(getOperationReference(),KtExpression ::class)
}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitPostfixExpression(this,data)
}
}
