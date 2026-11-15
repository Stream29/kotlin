/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.lexer.KtTokens



open class KtTryExpression : KtExpressionImpl {
constructor(node: ASTNode) : super(node) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitTryExpression(this,data)
}

open fun getTryBlock(): KtBlockExpression {

return (findChildByType<PsiElement>(KtNodeTypes.BLOCK) as KtBlockExpression)
}

open fun getCatchClauses(): List<KtCatchClause> {

return findChildrenByType(KtNodeTypes.CATCH)
}

open fun getFinallyBlock(): KtFinallySection? {

return (findChildByType(KtNodeTypes.FINALLY) as KtFinallySection?)
}

open fun getTryKeyword(): PsiElement? {

return findChildByType(KtTokens.TRY_KEYWORD)
}
}
