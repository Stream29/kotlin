/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@file:OptIn(kotlin.native.internal.InternalForKotlinNative::class, KtImplementationDetail::class)

package org.jetbrains.kotlin.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.KtNodeTypes

class KtTypeCodeFragment : KtCodeFragment {
    constructor(
        project: Project,
        name: String,
        text: CharSequence,
        context: PsiElement?,
    ) : super(project, name, text, null, KtNodeTypes.TYPE_CODE_FRAGMENT, context)

    override fun createUnlinkedFileClone(): KtTypeCodeFragment =
        kotlin.native.internal.createUninitializedInstance<KtTypeCodeFragment>().also { copyKtCodeFragmentStateTo(it) }

    override fun getContentElement() = findChildByClass(KtTypeReference::class)
}
