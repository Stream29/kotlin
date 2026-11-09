/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.KtStubBasedElementTypes
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub



open class KtTypeConstraintList : KtElementImplStub<KotlinPlaceHolderStub<KtTypeConstraintList>> {
constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinPlaceHolderStub<KtTypeConstraintList>) : super(stub,KtStubBasedElementTypes.TYPE_CONSTRAINT_LIST) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitTypeConstraintList(this,data)
}

open fun getConstraints(): List<KtTypeConstraint> {

return getStubOrPsiChildrenAsList(KtStubBasedElementTypes.TYPE_CONSTRAINT)
}
}
