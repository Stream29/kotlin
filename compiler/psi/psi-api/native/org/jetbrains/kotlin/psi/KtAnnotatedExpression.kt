/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import org.jetbrains.kotlin.psi.psiUtil.collectAnnotationEntriesFromStubOrPsi as p1kOriginal_collectAnnotationEntriesFromStubOrPsi

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.KtNodeTypes



open class KtAnnotatedExpression : KtExpressionImpl, KtAnnotated, KtAnnotationsContainer {
constructor(node: ASTNode) : super(node) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitAnnotatedExpression(this,data)
}

open fun getBaseExpression(): KtExpression? {

return findChildByClass(KtExpression ::class)
}

override fun getAnnotations(): List<KtAnnotation> {

return findChildrenByType(KtNodeTypes.ANNOTATION)
}

override fun getAnnotationEntries(): List<KtAnnotationEntry> {

return (this).p1kOriginal_collectAnnotationEntriesFromStubOrPsi()
}
}
