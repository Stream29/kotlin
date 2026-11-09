/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.impl

import org.jetbrains.kotlin.psi.classId
import org.jetbrains.kotlin.psi.isTopLevel
import org.jetbrains.kotlin.psi.qualifiedName





import com.intellij.psi.stubs.StubElement
import com.intellij.util.io.StringRef
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtImplementationDetail
import org.jetbrains.kotlin.psi.KtTypeAlias
import org.jetbrains.kotlin.psi.stubs.KotlinStubElement
import org.jetbrains.kotlin.psi.stubs.KotlinTypeAliasStub
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes



@OptIn(KtImplementationDetail::class)
class KotlinTypeAliasStubImpl(
    parent: StubElement<*>?,
    name: StringRef?,
    private val qualifiedName: StringRef?,
    override val classId: ClassId?,
    override val isTopLevel: Boolean,
) : KotlinStubBaseImpl<KtTypeAlias>(parent, KtStubElementTypes.TYPEALIAS), KotlinTypeAliasStub {
    private val nameRef = name
    override val name: String?
    get() =
        StringRef.toString(nameRef)

    override val fqName: FqName?
        get() = StringRef.toString(qualifiedName)?.let(::FqName)

    @KtImplementationDetail
    override fun copyInto(newParent: StubElement<*>?): KotlinTypeAliasStubImpl = KotlinTypeAliasStubImpl(
        parent = newParent,
        name = nameRef,
        qualifiedName = qualifiedName,
        classId = classId,
        isTopLevel = isTopLevel,
    )

    @KtImplementationDetail
    override fun isEquivalentTo(other: KotlinStubElement<*>): Boolean =
        other is KotlinTypeAliasStubImpl &&
                other.nameRef == nameRef &&
                other.qualifiedName == qualifiedName &&
                other.isTopLevel == isTopLevel &&
                other.classId == classId
}
