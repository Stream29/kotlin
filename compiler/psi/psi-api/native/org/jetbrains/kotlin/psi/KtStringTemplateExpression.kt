/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.ContributedReferenceHost
import com.intellij.psi.LiteralTextEscaper
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.tree.TokenSet
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.psiUtil.getContentRange
import org.jetbrains.kotlin.psi.psiUtil.replaceStringTemplateContent
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub

open class KtStringTemplateExpression : KtExpressionImplStub<KotlinPlaceHolderStub<KtStringTemplateExpression>>, PsiLanguageInjectionHost, ContributedReferenceHost {
    constructor(node: ASTNode) : super(node)
    constructor(stub: KotlinPlaceHolderStub<KtStringTemplateExpression>) : super(stub, KtNodeTypes.STRING_TEMPLATE)

    override fun <R, D> accept(visitor: KtVisitor<R, D>, data: D): R =
        visitor.visitStringTemplateExpression(this, data)

    open fun getInterpolationPrefix(): KtStringInterpolationPrefix? =
        getStubOrPsiChild(KtNodeTypes.STRING_INTERPOLATION_PREFIX, KtStringInterpolationPrefix::class)

    @Suppress("UNCHECKED_CAST")
    open fun getEntries(): Array<KtStringTemplateEntry> =
        getStubOrPsiChildren(STRING_ENTRIES_TYPES, KtStringTemplateEntry.EMPTY_ARRAY) as Array<KtStringTemplateEntry>

    override val isValidHost: Boolean
        get() = node.getChildren(CLOSE_QUOTE_TOKEN_SET).size != 0

    override fun updateText(text: String): KtStringTemplateExpression {
        val mutationService = KtPsiMutationService.getInstanceOrNull()
        if (mutationService != null) return mutationService.updateStringTemplateText(this, text) as KtStringTemplateExpression
        val newExpression = KtPsiFactory(project).createExpressionIfPossible(text)
        if (newExpression is KtStringTemplateExpression) return replace(newExpression) as KtStringTemplateExpression
        return replaceStringTemplateContent(this, getContentRange(), text)!!
    }

    override fun createLiteralTextEscaper(): LiteralTextEscaper<out PsiLanguageInjectionHost> =
        KotlinStringLiteralTextEscaper(this)

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
        private val STRING_ENTRIES_TYPES: TokenSet = TokenSet.create(
            KtNodeTypes.LONG_STRING_TEMPLATE_ENTRY,
            KtNodeTypes.SHORT_STRING_TEMPLATE_ENTRY,
            KtNodeTypes.LITERAL_STRING_TEMPLATE_ENTRY,
            KtNodeTypes.ESCAPE_STRING_TEMPLATE_ENTRY,
        )
    }
}
