/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@file:OptIn(kotlin.native.internal.InternalForKotlinNative::class)

package org.jetbrains.kotlin.psi

import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassOwner
import com.intellij.psi.tree.TokenSet
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.psi.stubs.elements.KtTokenSets

@SubclassOptInRequired(KtImplementationDetail::class)
open class KtFile(viewProvider: FileViewProvider, isCompiled: Boolean) : @Suppress("DEPRECATION") KtCommonFile(viewProvider, isCompiled),
    PsiClassOwner {
    override fun createUnlinkedFileClone(): KtFile {
        check(this::class == KtFile::class) { "Concrete Kotlin file subtype needs its own clone allocation hook" }
        return kotlin.native.internal.createUninitializedInstance<KtFile>().also { copyKtCommonFileStateTo(it) }
    }

    override fun getClasses(): Array<PsiClass> {
        val fileClassProvider = project.getService(KtFileClassProvider::class)
        return fileClassProvider?.getFileClasses(this) ?: PsiClass.EMPTY_ARRAY
    }

    override fun setPackageName(packageName: String) {}

    @Deprecated(
        message = "getPackageFqName should be used instead",
        replaceWith = ReplaceWith("packageFqName.asString()"),
        level = DeprecationLevel.ERROR,
    )
    override fun getPackageName(): String {
        return packageFqName.asString()
    }

    override fun <R, D> accept(visitor: KtVisitor<R, D>, data: D): R {
        return visitor.visitKtFile(this, data)
    }

    companion object {
        /**
         * The stub element types that may appear as top-level members of a Kotlin file: all declaration types plus scripts.
         */
        val FILE_DECLARATION_TYPES = TokenSet.orSet(KtTokenSets.DECLARATION_TYPES, TokenSet.create(KtNodeTypes.SCRIPT))
    }
}
