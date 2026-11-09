/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.KtStubBasedElementTypes
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub



open class KtDelegatedSuperTypeEntry : KtSuperTypeListEntry {
constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinPlaceHolderStub<out KtSuperTypeListEntry>) : super(stub,KtStubBasedElementTypes.DELEGATED_SUPER_TYPE_ENTRY) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitDelegatedSuperTypeEntry(this,data)
}

open fun getDelegateExpression(): KtExpression? {

return findChildByClass(KtExpression ::class)
}

open fun getByKeywordNode(): ASTNode? {

return node.findChildByType(KtTokens.BY_KEYWORD)
}
}
