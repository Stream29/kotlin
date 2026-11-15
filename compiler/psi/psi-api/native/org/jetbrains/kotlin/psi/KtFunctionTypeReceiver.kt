/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import org.jetbrains.kotlin.KtNodeTypes

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub



open class KtFunctionTypeReceiver : KtElementImplStub<KotlinPlaceHolderStub<KtFunctionTypeReceiver>> {
constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinPlaceHolderStub<KtFunctionTypeReceiver>) : super(stub,KtNodeTypes.FUNCTION_TYPE_RECEIVER) {


}

open fun getTypeReference(): KtTypeReference {

return getRequiredStubOrPsiChild(KtNodeTypes.TYPE_REFERENCE, KtTypeReference::class)
}
}
