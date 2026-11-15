/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.lexer.KtTokens



open class KtWhenEntry : KtElementImpl {
constructor(node: ASTNode) : super(node) {


}

open fun isElse(): Boolean {

return getElseKeyword() != null && getGuard() == null
}

open fun getElseKeyword(): PsiElement? {

return findChildByType(KtTokens.ELSE_KEYWORD)
}

open fun getExpression(): KtExpression? {

return findChildByClass(KtExpression ::class)
}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitWhenEntry(this,data)
}

open fun getConditions(): Array<KtWhenCondition> {

return findChildrenByClass(KtWhenCondition ::class)
}

open fun getGuard(): KtWhenEntryGuard? {

return findChildByClass(KtWhenEntryGuard ::class)
}

open fun getTrailingComma(): PsiElement? {

return org.jetbrains.kotlin.psi.psiUtil.getTrailingCommaByClosingElement(getArrow())
}

open fun getArrow(): PsiElement? {

return findChildByType(KtTokens.ARROW)
}
}
