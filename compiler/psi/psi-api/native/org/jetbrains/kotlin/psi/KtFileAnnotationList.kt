/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import org.jetbrains.kotlin.psi.psiUtil.collectAnnotationEntriesFromStubOrPsi as p1kOriginal_collectAnnotationEntriesFromStubOrPsi

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.KtStubBasedElementTypes
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub



open class KtFileAnnotationList : KtElementImplStub<KotlinPlaceHolderStub<KtFileAnnotationList>>, KtAnnotationsContainer {
constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinPlaceHolderStub<KtFileAnnotationList>) : super(stub,KtStubBasedElementTypes.FILE_ANNOTATION_LIST) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitFileAnnotationList(this,data)
}

override fun getAnnotations(): List<KtAnnotation> {

return getStubOrPsiChildrenAsList(KtStubBasedElementTypes.ANNOTATION)
}

override fun getAnnotationEntries(): List<KtAnnotationEntry> {

return (this).p1kOriginal_collectAnnotationEntriesFromStubOrPsi()
}
}
