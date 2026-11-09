/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.KtStubBasedElementTypes
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub



open class KtSuperTypeCallEntry : KtSuperTypeListEntry, KtCallElement {
constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinPlaceHolderStub<out KtSuperTypeListEntry>) : super(stub,KtStubBasedElementTypes.SUPER_TYPE_CALL_ENTRY) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitSuperTypeCallEntry(this,data)
}

override fun getCalleeExpression(): KtConstructorCalleeExpression {

return getRequiredStubOrPsiChild(KtStubBasedElementTypes.CONSTRUCTOR_CALLEE)
}

override fun getValueArgumentList(): KtValueArgumentList? {

return getStubOrPsiChild(KtStubBasedElementTypes.VALUE_ARGUMENT_LIST)
}

override fun getValueArguments(): List<out ValueArgument> {

var list: KtValueArgumentList? = getValueArgumentList()
return (if (list != null) list.getArguments() else emptyList<KtValueArgument>())
}

override fun getLambdaArguments(): List<KtLambdaArgument> {

return emptyList()
}

override fun getTypeReference(): KtTypeReference? {

return getCalleeExpression().getTypeReference()
}

override fun getTypeArguments(): List<KtTypeProjection> {

var typeArgumentList: KtTypeArgumentList? = getTypeArgumentList()
if (typeArgumentList == null) {
return emptyList()
}
return typeArgumentList.getArguments()
}

override fun getTypeArgumentList(): KtTypeArgumentList? {

var userType: KtUserType? = getTypeAsUserType()
return (if (userType != null) userType.getTypeArgumentList() else null)
}
}
