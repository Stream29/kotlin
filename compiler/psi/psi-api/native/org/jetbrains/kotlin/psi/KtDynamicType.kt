/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.KtStubBasedElementTypes
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub



open class KtDynamicType : KtElementImplStub<KotlinPlaceHolderStub<KtDynamicType>>, KtTypeElement {
constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinPlaceHolderStub<KtDynamicType>) : super(stub,KtStubBasedElementTypes.DYNAMIC_TYPE) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitDynamicType(this,data)
}

override fun getTypeArgumentsAsTypes(): List<KtTypeReference> {

return emptyList()
}
}
