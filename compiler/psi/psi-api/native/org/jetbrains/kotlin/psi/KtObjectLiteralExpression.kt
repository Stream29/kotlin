/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.KtNodeTypes



open class KtObjectLiteralExpression : KtExpressionImpl {
constructor(node: ASTNode) : super(node) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitObjectLiteralExpression(this,data)
}

open fun getObjectDeclaration(): KtObjectDeclaration {

return (findChildByType<com.intellij.psi.PsiElement>(KtNodeTypes.OBJECT_DECLARATION) as KtObjectDeclaration)
}
}
