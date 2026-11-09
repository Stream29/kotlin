/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.KtNodeTypes



open class KtSuperExpression : KtInstanceExpressionWithLabel, KtStatementExpression {
constructor(node: ASTNode) : super(node) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitSuperExpression(this,data)
}

open fun getSuperTypeQualifier(): KtTypeReference? {

return (findChildByType(KtNodeTypes.TYPE_REFERENCE) as KtTypeReference?)
}
}
