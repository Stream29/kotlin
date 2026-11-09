/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin.psi.impl

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import nativekotlin.host.psi.NativePsiContextClassifiers
import nativekotlin.host.psi.PsiContextClassifier
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDeclarationWithBody
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtPackageDirective
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtScript

/** The original compiler-util context queries, bound to real Kotlin PSI types. */
object KotlinPsiContextClassifier : PsiContextClassifier {
    override fun matches(element: PsiElement, originalClassSimpleName: String): Boolean = when (originalClassSimpleName) {
        "KtImportDirective" -> element is KtImportDirective
        "KtPackageDirective" -> element is KtPackageDirective
        "KtDeclarationWithBody" -> element is KtDeclarationWithBody
        "KtClassOrObject" -> element is KtClassOrObject
        "KtScript" -> element is KtScript
        "KtProperty" -> element is KtProperty
        else -> throw IllegalArgumentException("Unknown original Kotlin context query: $originalClassSimpleName")
    }
    override fun isLeafPsiElement(element: PsiElement): Boolean = element is LeafPsiElement
    fun install() = NativePsiContextClassifiers.install(this)
}
