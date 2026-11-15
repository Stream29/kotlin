/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import org.jetbrains.kotlin.KtNodeTypes

import com.intellij.lang.ASTNode
import com.intellij.psi.tree.IElementType
import com.intellij.util.ArrayFactory
import com.intellij.platform.native.NativeReferenceArrays
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub
import org.jetbrains.kotlin.resolution.KtResolvable



open class KtSuperTypeListEntry : KtElementImplStub<KotlinPlaceHolderStub<out KtSuperTypeListEntry>>, KtResolvable {
constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinPlaceHolderStub<out KtSuperTypeListEntry>, nodeType: IElementType) : super(stub,nodeType) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitSuperTypeListEntry(this,data)
}

open fun getTypeReference(): KtTypeReference? {

return getStubOrPsiChild(KtNodeTypes.TYPE_REFERENCE, KtTypeReference::class)
}

open fun getTypeAsUserType(): KtUserType? {

var reference: KtTypeReference? = getTypeReference()
if (reference != null) {
var element: KtTypeElement? = reference.typeElement
if (element is KtUserType) {
return ((element as KtUserType))
}
}
return null
}

companion object {
@Suppress("UNCHECKED_CAST")
val EMPTY_ARRAY: Array<KtSuperTypeListEntry> = NativeReferenceArrays.newArray(KtSuperTypeListEntry::class, 0) as Array<KtSuperTypeListEntry>

@Suppress("UNCHECKED_CAST")
var ARRAY_FACTORY: ArrayFactory<KtSuperTypeListEntry> = ArrayFactory { count ->
    if (count == 0) EMPTY_ARRAY as Array<KtSuperTypeListEntry?> else NativeReferenceArrays.newArray(KtSuperTypeListEntry::class, count)
}
}
}
