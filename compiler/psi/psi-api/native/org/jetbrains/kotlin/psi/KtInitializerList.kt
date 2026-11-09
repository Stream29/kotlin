/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import org.jetbrains.kotlin.util.NativeArrays as Arrays

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.KtStubBasedElementTypes
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub
import org.jetbrains.kotlin.psi.stubs.elements.KtTokenSets



open class KtInitializerList : KtElementImplStub<KotlinPlaceHolderStub<KtInitializerList>> {
constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinPlaceHolderStub<KtInitializerList>) : super(stub,KtStubBasedElementTypes.INITIALIZER_LIST) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitInitializerList(this,data)
}

@Suppress("UNCHECKED_CAST")
open fun getInitializers(): List<KtSuperTypeListEntry> {

return Arrays.asList(getStubOrPsiChildren(KtTokenSets.SUPER_TYPE_LIST_ENTRIES,KtSuperTypeListEntry.ARRAY_FACTORY)) as List<KtSuperTypeListEntry>
}
}
