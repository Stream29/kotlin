/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.elements

import com.intellij.lang.ASTNode
import com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.stubs.StubUtils



open class KtDotQualifiedExpressionElementType : KtPlaceHolderStubElementType<KtDotQualifiedExpression> {
constructor(debugName: String) : super(debugName,KtDotQualifiedExpression ::class) {


}

override fun shouldCreateStub(node: ASTNode): Boolean {

var treeParent: ASTNode? = node.treeParent
if (treeParent == null) return false
var parentElementType: IElementType? = treeParent.elementType
if (parentElementType == KtStubElementTypes.IMPORT_DIRECTIVE || parentElementType == KtStubElementTypes.PACKAGE_DIRECTIVE || parentElementType == KtStubElementTypes.DOT_QUALIFIED_EXPRESSION || StubUtils.isDeclaredInsideValueArgument(node)) {
return super.shouldCreateStub(node)
}
return false
}
}
