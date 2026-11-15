/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.KtNodeTypes



open class KtConstructorDelegationCall : KtElementImpl, KtCallElement {
constructor(node: ASTNode) : super(node) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitConstructorDelegationCall(this,data)
}

override fun getValueArgumentList(): KtValueArgumentList? {

return (findChildByType(KtNodeTypes.VALUE_ARGUMENT_LIST) as KtValueArgumentList?)
}

override fun getValueArguments(): List<out ValueArgument> {

var list: KtValueArgumentList? = getValueArgumentList()
return (if (list != null) list.getArguments() else emptyList<KtValueArgument>())
}

override fun getLambdaArguments(): List<KtLambdaArgument> {

return emptyList()
}

override fun getTypeArguments(): List<KtTypeProjection> {

return emptyList()
}

override fun getTypeArgumentList(): KtTypeArgumentList? {

return null
}

override fun getCalleeExpression(): KtConstructorDelegationReferenceExpression? {

return findChildByClass(KtConstructorDelegationReferenceExpression ::class)
}

open fun isImplicit(): Boolean {

var callee: KtConstructorDelegationReferenceExpression? = getCalleeExpression()
return callee != null && callee.firstChild == null
}

open fun isCallToThis(): Boolean {

var callee: KtConstructorDelegationReferenceExpression? = getCalleeExpression()
return callee != null && callee.isThis()
}
}
