/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.KtStubBasedElementTypes
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub
import org.jetbrains.kotlin.psi.stubs.elements.KtTokenSets
import org.jetbrains.kotlin.resolution.KtResolvable



open class KtNullableType : KtElementImplStub<KotlinPlaceHolderStub<KtNullableType>>, KtTypeElement, KtResolvable {
constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinPlaceHolderStub<KtNullableType>) : super(stub,KtStubBasedElementTypes.NULLABLE_TYPE) {


}

open fun getQuestionMarkNode(): ASTNode {

return node.findChildByType(KtTokens.QUEST)!!
}

override fun getTypeArgumentsAsTypes(): List<KtTypeReference?> {

var innerType: KtTypeElement? = getInnerType()
return (if (innerType == null) emptyList() else innerType.getTypeArgumentsAsTypes())
}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitNullableType(this,data)
}

open fun getInnerType(): KtTypeElement? {

return KtStubbedPsiUtil.getStubOrPsiChild(this,KtTokenSets.TYPE_ELEMENT_TYPES,KtTypeElement.ARRAY_FACTORY)
}

open fun getModifierList(): KtModifierList? {

return getStubOrPsiChild(KtStubBasedElementTypes.MODIFIER_LIST)
}

open fun getAnnotationEntries(): List<KtAnnotationEntry> {

var modifierList: KtModifierList? = getModifierList()
return (if (modifierList != null) modifierList.getAnnotationEntries() else emptyList())
}
}
