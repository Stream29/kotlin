/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.lexer.KtTokens



open class KtWhenConditionIsPattern : KtWhenCondition {
constructor(node: ASTNode) : super(node) {


}

open fun isNegated(): Boolean {

return node.findChildByType(KtTokens.NOT_IS) != null
}

open fun getTypeReference(): KtTypeReference? {

return (findChildByType(KtNodeTypes.TYPE_REFERENCE) as KtTypeReference?)
}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitWhenConditionIsPattern(this,data)
}
}
