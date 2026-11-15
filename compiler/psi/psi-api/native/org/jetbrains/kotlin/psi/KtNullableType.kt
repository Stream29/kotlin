/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub
import org.jetbrains.kotlin.psi.stubs.elements.KtTokenSets
import org.jetbrains.kotlin.resolution.KtResolvable

open class KtNullableType : KtModifierListOwnerStub<KotlinPlaceHolderStub<KtNullableType>>, KtTypeElement, KtResolvable {
    constructor(node: ASTNode) : super(node)
    constructor(stub: KotlinPlaceHolderStub<KtNullableType>) : super(stub, KtNodeTypes.NULLABLE_TYPE)

    open fun getQuestionMarkNode(): ASTNode =
        node.findChildByType(KtTokens.QUEST)!!

    override fun getTypeArgumentsAsTypes(): List<KtTypeReference?> {
        val innerType = getInnerType()
        return if (innerType == null) emptyList() else innerType.getTypeArgumentsAsTypes()
    }

    override fun <R, D> accept(visitor: KtVisitor<R, D>, data: D): R =
        visitor.visitNullableType(this, data)

    open fun getInnerType(): KtTypeElement? =
        KtStubbedPsiUtil.getStubOrPsiChild(this, KtTokenSets.TYPE_ELEMENT_TYPES, KtTypeElement.ARRAY_FACTORY)
}
