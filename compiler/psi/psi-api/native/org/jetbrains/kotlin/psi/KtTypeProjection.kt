/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.KtStubBasedElementTypes
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.stubs.KotlinTypeProjectionStub



open class KtTypeProjection : KtModifierListOwnerStub<KotlinTypeProjectionStub> {
constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinTypeProjectionStub) : super(stub,KtStubBasedElementTypes.TYPE_PROJECTION) {


}

open val projectionKind: KtProjectionKind
get() {

var stub: KotlinTypeProjectionStub? = greenStub
if (stub != null) {
return stub.projectionKind
}
var projectionToken: PsiElement? = getProjectionToken()
var token: IElementType? = (if (projectionToken != null) projectionToken.node!!.elementType else null)
for (projectionKind in KtProjectionKind.values()) {
if (projectionKind.getToken() == token) {
return projectionKind
}
}
throw IllegalStateException(projectionToken!!.text)
}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitTypeProjection(this,data)
}

open fun getTypeReference(): KtTypeReference? {

return getStubOrPsiChild(KtStubBasedElementTypes.TYPE_REFERENCE)
}

open fun getProjectionToken(): PsiElement? {

var star: PsiElement? = findChildByType(KtTokens.MUL)
if (star != null) {
return star
}
var modifierList: KtModifierList? = getModifierList()
if (modifierList != null) {
var element: PsiElement? = modifierList.getModifier(KtTokens.IN_KEYWORD)
if (element != null) return element
element = modifierList.getModifier(KtTokens.OUT_KEYWORD)
if (element != null) return element
}
return null
}
}
