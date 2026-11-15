/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub

open class KtObjectLiteralExpression : KtExpressionImplStub<KotlinPlaceHolderStub<KtObjectLiteralExpression>> {
    constructor(node: ASTNode) : super(node)
    constructor(stub: KotlinPlaceHolderStub<KtObjectLiteralExpression>) : super(stub, KtNodeTypes.OBJECT_LITERAL)

    override fun <R, D> accept(visitor: KtVisitor<R, D>, data: D): R =
        visitor.visitObjectLiteralExpression(this, data)

    open fun getObjectDeclaration(): KtObjectDeclaration =
        getStubOrPsiChild(KtNodeTypes.OBJECT_DECLARATION, KtObjectDeclaration::class)!!
}
