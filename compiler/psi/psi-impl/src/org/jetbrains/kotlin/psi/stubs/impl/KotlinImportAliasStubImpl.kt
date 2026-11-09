/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.impl

import com.intellij.psi.stubs.StubElement
import com.intellij.util.io.StringRef
import org.jetbrains.kotlin.psi.KtImplementationDetail
import org.jetbrains.kotlin.psi.KtImportAlias
import org.jetbrains.kotlin.psi.stubs.KotlinImportAliasStub
import org.jetbrains.kotlin.psi.stubs.KotlinStubElement
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes



@OptIn(KtImplementationDetail::class)
class KotlinImportAliasStubImpl(
    parent: StubElement<*>?,
    name: StringRef?,
) : KotlinStubBaseImpl<KtImportAlias>(parent, KtStubElementTypes.IMPORT_ALIAS), KotlinImportAliasStub {
    private val nameRef = name
    override val name: String?
    get() = StringRef.toString(nameRef)

    @KtImplementationDetail
    override fun copyInto(newParent: StubElement<*>?): KotlinImportAliasStubImpl = KotlinImportAliasStubImpl(
        parent = newParent,
        name = nameRef
    )

    @KtImplementationDetail
    override fun isEquivalentTo(other: KotlinStubElement<*>): Boolean =
        other is KotlinImportAliasStubImpl &&
                other.nameRef == nameRef
}
