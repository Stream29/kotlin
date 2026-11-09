/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.search.SearchScope
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.KtStubBasedElementTypes
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.stubs.KotlinTypeParameterStub
import org.jetbrains.kotlin.types.Variance



open class KtTypeParameter : KtNamedDeclarationStub<KotlinTypeParameterStub> {
constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinTypeParameterStub) : super(stub,KtStubBasedElementTypes.TYPE_PARAMETER) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitTypeParameter(this,data)
}

open fun getVariance(): Variance {

var modifierList: KtModifierList? = getModifierList()
if (modifierList == null) return Variance.INVARIANT
if (modifierList.hasModifier(KtTokens.OUT_KEYWORD)) return Variance.OUT_VARIANCE
if (modifierList.hasModifier(KtTokens.IN_KEYWORD)) return Variance.IN_VARIANCE
return Variance.INVARIANT
}

open fun setExtendsBound(typeReference: KtTypeReference?): KtTypeReference? {

return KtPsiMutationService.getInstance().setTypeParameterExtendsBound(this,typeReference)
}

open fun getExtendsBound(): KtTypeReference? {

return getStubOrPsiChild(KtStubBasedElementTypes.TYPE_REFERENCE)
}

override val useScope: SearchScope
get() {

var owner: KtTypeParameterListOwner? = PsiTreeUtil.getParentOfType(this,KtTypeParameterListOwner ::class)
return LocalSearchScope((if(owner != null) owner else this))
}
}
