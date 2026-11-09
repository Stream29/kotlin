/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.KtNodeTypes



open class KtCatchClause : KtElementImpl {
constructor(node: ASTNode) : super(node) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitCatchSection(this,data)
}

open fun getParameterList(): KtParameterList? {

return (findChildByType(KtNodeTypes.VALUE_PARAMETER_LIST) as KtParameterList?)
}

open fun getCatchParameter(): KtParameter? {

var list: KtParameterList? = getParameterList()
if (list == null) return null
var parameters: List<KtParameter> = list.getParameters()
return (if (parameters.size == 1) parameters.get(0) else null)
}

open fun getCatchBody(): KtExpression? {

return findChildByClass(KtExpression ::class)
}
}
