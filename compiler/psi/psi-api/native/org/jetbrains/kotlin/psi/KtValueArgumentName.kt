/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.KtStubBasedElementTypes
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub



open class KtValueArgumentName : KtElementImplStub<KotlinPlaceHolderStub<KtValueArgumentName>>, ValueArgumentName {
constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinPlaceHolderStub<KtValueArgumentName>) : super(stub,KtStubBasedElementTypes.VALUE_ARGUMENT_NAME) {


}

override val referenceExpression: KtSimpleNameExpression
get() {

return getStubOrPsiChild(KtStubBasedElementTypes.REFERENCE_EXPRESSION)!!
}

override val asName: Name
get() {

return referenceExpression.getReferencedNameAsName()
}
}
