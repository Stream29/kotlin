/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.KtStubBasedElementTypes
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub
import org.jetbrains.kotlin.resolution.KtResolvableCall



open class KtConstructorCalleeExpression : KtExpressionImplStub<KotlinPlaceHolderStub<KtConstructorCalleeExpression>>, KtResolvableCall {
constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinPlaceHolderStub<KtConstructorCalleeExpression>) : super(stub,KtStubBasedElementTypes.CONSTRUCTOR_CALLEE) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitConstructorCalleeExpression(this,data)
}

open fun getTypeReference(): KtTypeReference? {

return getStubOrPsiChild(KtStubBasedElementTypes.TYPE_REFERENCE)
}

open fun getConstructorReferenceExpression(): KtSimpleNameExpression? {

var typeReference: KtTypeReference? = getTypeReference()
if (typeReference == null) {
return null
}
var typeElement: KtTypeElement? = typeReference.typeElement
if (!(typeElement is KtUserType)) {
return null
}
return ((typeElement as KtUserType)).getReferenceExpression()
}
}
