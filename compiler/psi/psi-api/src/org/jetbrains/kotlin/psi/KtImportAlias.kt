/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.search.LocalSearchScope
import org.jetbrains.kotlin.KtStubBasedElementTypes
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import org.jetbrains.kotlin.psi.stubs.KotlinImportAliasStub



/**
 * Represents an import alias that provides an alternative name for an imported declaration.
 *
 * ### Example:
 *
 * ```kotlin
 * import java.util.Date as JavaDate
 * //                    ^^^^^^^^^^^
 * ```
 */
class KtImportAlias : KtElementImplStub<KotlinImportAliasStub>, PsiNameIdentifierOwner {
    @Suppress("unused")
    constructor(node: ASTNode) : super(node)

    @Suppress("unused")
    constructor(stub: KotlinImportAliasStub) : super(stub, KtStubBasedElementTypes.IMPORT_ALIAS)

    override fun <R : Any?, D : Any?> accept(visitor: KtVisitor<R, D>, data: D): R {
        return visitor.visitImportAlias(this, data)
    }

    val importDirective: KtImportDirective?
        get() = parent as? KtImportDirective

    override val name: String?
    get() = greenStub?.name ?: nameIdentifier?.text

    @OptIn(KtNonPublicApi::class)
    override fun setName(name: String): PsiElement = KtPsiMutationService.getInstance().setImportAliasName(this, name)

    override val nameIdentifier: PsiElement?
    get() = findChildByType(KtTokens.IDENTIFIER)

    override val textOffset: Int get() = nameIdentifier?.textOffset ?: startOffset

    override val useScope: LocalSearchScope get() = LocalSearchScope(containingFile!!)
}
