/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.KtStubBasedElementTypes
import org.jetbrains.kotlin.psi.stubs.KotlinUserTypeStub
import org.jetbrains.kotlin.resolution.KtResolvable



open class KtUserType : KtElementImplStub<KotlinUserTypeStub>, KtTypeElement, KtResolvable {
constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinUserTypeStub) : super(stub,KtStubBasedElementTypes.USER_TYPE) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitUserType(this,data)
}

open fun getTypeArgumentList(): KtTypeArgumentList? {

return getStubOrPsiChild(KtStubBasedElementTypes.TYPE_ARGUMENT_LIST)
}

open fun getTypeArguments(): List<KtTypeProjection> {

var typeArgumentList: KtTypeArgumentList? = getTypeArgumentList()
return (if (typeArgumentList == null) emptyList() else typeArgumentList.getArguments())
}

override fun getTypeArgumentsAsTypes(): List<KtTypeReference?> {

var result: MutableList<KtTypeReference?> = ArrayList()
for (projection in getTypeArguments()) {
result.add(projection.getTypeReference())
}
return result
}

open fun getReferenceExpression(): KtSimpleNameExpression? {

var nameRefExpr: KtNameReferenceExpression? = getStubOrPsiChild(KtStubBasedElementTypes.REFERENCE_EXPRESSION)
return (if (nameRefExpr != null) nameRefExpr else getStubOrPsiChild(KtStubBasedElementTypes.ENUM_ENTRY_SUPERCLASS_REFERENCE_EXPRESSION))
}

open fun getQualifier(): KtUserType? {

return getStubOrPsiChild(KtStubBasedElementTypes.USER_TYPE)
}

open fun deleteQualifier(): Unit {

KtPsiMutationService.getInstance().removeQualifier(this)
}

open fun getReferencedName(): String? {

var referenceExpression: KtSimpleNameExpression? = getReferenceExpression()
return (if (referenceExpression == null) null else referenceExpression.referencedName)
}
}
