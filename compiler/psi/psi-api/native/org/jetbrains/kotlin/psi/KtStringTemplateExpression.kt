/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.*
import com.intellij.psi.tree.TokenSet
import com.intellij.util.IncorrectOperationException
import org.jetbrains.kotlin.KtStubBasedElementTypes
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub



open class KtStringTemplateExpression : KtElementImplStub<KotlinPlaceHolderStub<KtStringTemplateExpression>>, KtExpression, PsiLanguageInjectionHost, ContributedReferenceHost {
constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinPlaceHolderStub<KtStringTemplateExpression>) : super(stub,KtStubBasedElementTypes.STRING_TEMPLATE) {


}

override fun replace(newElement: PsiElement): PsiElement {

return KtPsiMutationService.getInstance().replaceExpression(this,newElement,true) { super<KtElementImplStub>.replace(it) }
}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitStringTemplateExpression(this,data)
}

open fun getInterpolationPrefix(): KtStringInterpolationPrefix? {

return getStubOrPsiChild(KtStubBasedElementTypes.STRING_INTERPOLATION_PREFIX)
}

@Suppress("UNCHECKED_CAST")
open fun getEntries(): Array<KtStringTemplateEntry> {

return getStubOrPsiChildren(STRING_ENTRIES_TYPES,KtStringTemplateEntry.EMPTY_ARRAY) as Array<KtStringTemplateEntry>
}

override val isValidHost: Boolean
get() {

return node.getChildren(CLOSE_QUOTE_TOKEN_SET).size != 0
}

override fun updateText(text: String): PsiLanguageInjectionHost {

return KtPsiMutationService.getInstance().updateStringTemplateText(this,text)
}

override fun createLiteralTextEscaper(): LiteralTextEscaper<out PsiLanguageInjectionHost> {

return KotlinStringLiteralTextEscaper(this)
}

open fun hasInterpolation(): Boolean {

for (child in children) {
if (child is KtSimpleNameStringTemplateEntry || child is KtBlockStringTemplateEntry) {
return true
}
}
return false
}

companion object {
private val CLOSE_QUOTE_TOKEN_SET: TokenSet = TokenSet.create(KtTokens.CLOSING_QUOTE)

private val STRING_ENTRIES_TYPES: TokenSet = TokenSet.create(KtStubBasedElementTypes.LONG_STRING_TEMPLATE_ENTRY,KtStubBasedElementTypes.SHORT_STRING_TEMPLATE_ENTRY,KtStubBasedElementTypes.LITERAL_STRING_TEMPLATE_ENTRY,KtStubBasedElementTypes.ESCAPE_STRING_TEMPLATE_ENTRY)
}
}
