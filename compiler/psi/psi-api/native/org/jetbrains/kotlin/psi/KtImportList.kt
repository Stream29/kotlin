/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.KtStubBasedElementTypes
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub



open class KtImportList : KtElementImplStub<KotlinPlaceHolderStub<KtImportList>> {
constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinPlaceHolderStub<KtImportList>) : super(stub,KtStubBasedElementTypes.IMPORT_LIST) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitImportList(this,data)
}

open fun getImports(): List<KtImportDirective> {

return getStubOrPsiChildrenAsList(KtStubBasedElementTypes.IMPORT_DIRECTIVE)
}
}
