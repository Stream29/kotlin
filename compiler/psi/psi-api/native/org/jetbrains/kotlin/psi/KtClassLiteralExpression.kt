/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import org.jetbrains.kotlin.KtStubBasedElementTypes
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.stubs.KotlinClassLiteralExpressionStub



open class KtClassLiteralExpression : KtElementImplStub<KotlinClassLiteralExpressionStub>, KtDoubleColonExpression {
constructor(stub: KotlinClassLiteralExpressionStub) : super(stub,KtStubBasedElementTypes.CLASS_LITERAL_EXPRESSION) {


}

constructor(node: ASTNode) : super(node) {


}

override val receiverExpression: KtExpression?
get() {

var stub: KotlinClassLiteralExpressionStub? = stub
if (stub != null) {
var expressions: Array<KtExpression?> = stub.getChildrenByType(CLASS_REFS,KtExpression.EMPTY_ARRAY)
if (expressions.size == 1) {
return expressions[0]
}
}
return super<KtDoubleColonExpression>.receiverExpression
}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitClassLiteralExpression(this,data)
}

override fun findColonColon(): PsiElement? {

var child: ASTNode? = node.findChildByType(KtTokens.COLONCOLON)
return (if (child != null) child.psi else null)
}

companion object {
private val CLASS_REFS: TokenSet = TokenSet.create(KtStubBasedElementTypes.REFERENCE_EXPRESSION,KtStubBasedElementTypes.DOT_QUALIFIED_EXPRESSION)
}
}
