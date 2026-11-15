/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import org.jetbrains.kotlin.KtNodeTypes

import org.jetbrains.kotlin.psi.psiUtil.collectAnnotationEntriesFromStubOrPsi as p1kOriginal_collectAnnotationEntriesFromStubOrPsi

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub



open class KtTypeConstraint : KtElementImplStub<KotlinPlaceHolderStub<KtTypeConstraint>>, KtAnnotated, KtAnnotationsContainer {
constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinPlaceHolderStub<KtTypeConstraint>) : super(stub,KtNodeTypes.TYPE_CONSTRAINT) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitTypeConstraint(this,data)
}

open fun getSubjectTypeParameterName(): KtSimpleNameExpression? {

return getStubOrPsiChild(KtNodeTypes.REFERENCE_EXPRESSION, KtNameReferenceExpression::class)
}

open fun getBoundTypeReference(): KtTypeReference? {

return getStubOrPsiChild(KtNodeTypes.TYPE_REFERENCE, KtTypeReference::class)
}

override fun getAnnotations(): List<KtAnnotation> {

return getStubOrPsiChildren(KtNodeTypes.ANNOTATION, KtAnnotation.EMPTY_ARRAY).asList()
}

override fun getAnnotationEntries(): List<KtAnnotationEntry> {

return (this).p1kOriginal_collectAnnotationEntriesFromStubOrPsi()
}

companion object {
val EMPTY_ARRAY: Array<KtTypeConstraint> = emptyArray()
}
}
