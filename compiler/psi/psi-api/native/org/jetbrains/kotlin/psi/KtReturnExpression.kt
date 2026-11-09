/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.resolution.KtResolvable



open class KtReturnExpression : KtExpressionWithLabel, KtStatementExpression, KtResolvable {
constructor(node: ASTNode) : super(node) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitReturnExpression(this,data)
}

open fun getReturnedExpression(): KtExpression? {

return findChildByClass(KtExpression ::class)
}

open fun getReturnKeyword(): PsiElement {

return findChildByType<PsiElement>(KtTokens.RETURN_KEYWORD)!!
}

open fun getLabeledExpression(): PsiElement? {

return findChildByType(KtNodeTypes.LABEL_QUALIFIER)
}
}
