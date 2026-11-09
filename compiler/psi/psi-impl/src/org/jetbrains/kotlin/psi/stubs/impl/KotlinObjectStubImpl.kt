/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.impl

import org.jetbrains.kotlin.psi.classId
import org.jetbrains.kotlin.psi.fqName
import org.jetbrains.kotlin.psi.isLocal
import org.jetbrains.kotlin.psi.isObjectLiteral
import org.jetbrains.kotlin.psi.isTopLevel



import com.intellij.psi.stubs.StubElement
import com.intellij.util.io.StringRef
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtImplementationDetail
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.stubs.KotlinObjectStub
import org.jetbrains.kotlin.psi.stubs.KotlinStubElement
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes



@OptIn(KtImplementationDetail::class)
class KotlinObjectStubImpl(
    parent: StubElement<*>?,
    name: StringRef?,
    override val fqName: FqName?,
    override val classId: ClassId?,
    private val superNameRefs: Array<StringRef>,
    override val isTopLevel: Boolean,
    override val isLocal: Boolean,
    override val isObjectLiteral: Boolean,
    override val kdocText: String?
) : KotlinStubBaseImpl<KtObjectDeclaration>(parent, KtStubElementTypes.OBJECT_DECLARATION), KotlinObjectStub {
    private val nameRef = name
    override val name: String?
    get() = nameRef?.string
    override val superNames: List<String>
        get() = superNameRefs.map { it.string!! }

    @KtImplementationDetail
    override fun copyInto(newParent: StubElement<*>?): KotlinObjectStubImpl = KotlinObjectStubImpl(
        parent = newParent,
        name = nameRef,
        fqName = fqName,
        classId = classId,
        superNameRefs = superNameRefs,
        isTopLevel = isTopLevel,
        isLocal = isLocal,
        isObjectLiteral = isObjectLiteral,
        kdocText = kdocText,
    )

    @KtImplementationDetail
    override fun isEquivalentTo(other: KotlinStubElement<*>): Boolean =
        other is KotlinObjectStubImpl &&
                other.nameRef == nameRef &&
                other.fqName == fqName &&
                other.classId == classId &&
                other.isTopLevel == isTopLevel &&
                other.isLocal == isLocal &&
                other.isObjectLiteral == isObjectLiteral &&
                other.kdocText == kdocText &&
                other.superNameRefs.contentEquals(superNameRefs)
}
