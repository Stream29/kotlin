/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.IStubElementType
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderWithTextStub



abstract class KtStringTemplateEntryWithExpression : KtStringTemplateEntry {
constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinPlaceHolderWithTextStub<out KtStringTemplateEntryWithExpression>, elementType: IStubElementType<*, *>) : super(stub,elementType) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitStringTemplateEntryWithExpression(this,data)
}

open fun getExpressions(): List<KtExpression> {

var expression: KtExpression? = getExpression()
return (if (expression != null) listOf(expression) else emptyList())
}
}
