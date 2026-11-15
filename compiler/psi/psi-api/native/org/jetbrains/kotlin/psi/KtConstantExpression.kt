/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.stubs.KotlinConstantExpressionStub
import org.jetbrains.kotlin.psi.utils.toConstantExpressionElementType

open class KtConstantExpression : KtExpressionImplStub<KotlinConstantExpressionStub> {
    constructor(node: ASTNode) : super(node)
    constructor(stub: KotlinConstantExpressionStub) : super(stub, stub.kind.toConstantExpressionElementType())

    override fun <R, D> accept(visitor: KtVisitor<R, D>, data: D): R =
        visitor.visitConstantExpression(this, data)

    override val text: String
        get() {
            val stub = greenStub
            if (stub != null) return stub.value
            return super<KtExpressionImplStub>.text
        }
}
