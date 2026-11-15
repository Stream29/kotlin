/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@file:OptIn(kotlin.native.internal.InternalForKotlinNative::class, KtImplementationDetail::class)

package org.jetbrains.kotlin.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.KtNodeTypes

class KtBlockCodeFragment : KtCodeFragment {
    constructor(
        viewProvider: FileViewProvider,
        imports: String?,
        context: PsiElement?,
    ) : super(viewProvider, imports, KtNodeTypes.BLOCK_CODE_FRAGMENT, context)

    constructor(
        project: Project,
        name: String,
        text: CharSequence,
        imports: String?,
        context: PsiElement?,
    ) : this(
        createFileViewProviderForLightFile(project, name, text),
        imports,
        context,
    )

    override fun createUnlinkedFileClone(): KtBlockCodeFragment =
        kotlin.native.internal.createUninitializedInstance<KtBlockCodeFragment>().also { copyKtCodeFragmentStateTo(it) }

    override fun getContentElement() = findChildByClass(KtBlockExpression::class)
        ?: throw IllegalStateException("Block expression should be parsed for BlockCodeFragment")
}
