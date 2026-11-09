/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.platform.native.NativeReferenceArrays

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderWithTextStub



abstract class KtStringTemplateEntry : KtElementImplStub<KotlinPlaceHolderWithTextStub<out KtStringTemplateEntry>> {
constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinPlaceHolderWithTextStub<out KtStringTemplateEntry>, elementType: IStubElementType<*, *>) : super(stub,elementType) {


}

open fun getExpression(): KtExpression? {

var stub: KotlinPlaceHolderStub<*>? = stub
if (stub != null) {
var childrenStubs: List<StubElement<*>> = stub.childrenStubs
for (element in childrenStubs) {
var psiElement: PsiElement? = element.psi
if (psiElement is KtExpression) {
return (psiElement as KtExpression)
}
}
}
return findChildByClass(KtExpression ::class)
}

override val text: String
get() {

var stub: KotlinPlaceHolderWithTextStub<*>? = greenStub
if (stub != null) {
return stub.text
}
return super<KtElementImplStub>.text
}

companion object {
@Suppress("UNCHECKED_CAST")
val EMPTY_ARRAY: Array<KtStringTemplateEntry> = NativeReferenceArrays.newArray(KtStringTemplateEntry::class, 0) as Array<KtStringTemplateEntry>
}
}
