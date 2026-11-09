/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.search.SearchScope
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.SpecialNames



open class KtFunctionLiteral : KtFunctionNotStubbed {
constructor(node: ASTNode) : super(node) {


}

override fun hasBlockBody(): Boolean {

return false
}

override val name: String
get() {

return SpecialNames.ANONYMOUS_STRING
}

override val nameIdentifier: PsiElement?
get() {

return null
}

open fun hasParameterSpecification(): Boolean {

return findChildByType<PsiElement>(KtTokens.ARROW) != null
}

override fun getBodyExpression(): KtBlockExpression {

return (super.getBodyExpression() as KtBlockExpression)
}

override fun getEqualsToken(): PsiElement? {

return null
}

open fun getLBrace(): PsiElement {

return findChildByType<PsiElement>(KtTokens.LBRACE)!!
}

open fun getRBrace(): PsiElement? {

return findChildByType(KtTokens.RBRACE)
}

open fun getArrow(): PsiElement? {

return findChildByType(KtTokens.ARROW)
}

override fun getFqName(): FqName? {

return null
}

override fun hasBody(): Boolean {

return getBodyExpression() != null
}

override val useScope: SearchScope
get() {

return LocalSearchScope(this)
}
}
