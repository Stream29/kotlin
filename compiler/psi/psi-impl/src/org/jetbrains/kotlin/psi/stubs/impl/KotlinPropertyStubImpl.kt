/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.impl

import org.jetbrains.kotlin.psi.fqName
import org.jetbrains.kotlin.psi.isTopLevel







import com.intellij.psi.stubs.StubElement
import com.intellij.util.io.StringRef
import org.jetbrains.kotlin.constant.ConstantValue
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtImplementationDetail
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.stubs.KotlinPropertyStub
import org.jetbrains.kotlin.psi.stubs.KotlinStubElement
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes



@OptIn(KtImplementationDetail::class)
class KotlinPropertyStubImpl(
    parent: StubElement<*>?,
    name: StringRef?,
    override val isVar: Boolean,
    override val isTopLevel: Boolean,
    override val hasDelegate: Boolean,
    override val hasDelegateExpression: Boolean,
    override val hasInitializer: Boolean,
    override val isExtension: Boolean,
    override val hasReturnTypeRef: Boolean,
    override val fqName: FqName?,
    val constantInitializer: ConstantValue<*>?,
    val origin: KotlinStubOrigin?,
    override val hasBackingField: Boolean?,
    override val kdocText: String?,
) : KotlinStubBaseImpl<KtProperty>(parent, KtStubElementTypes.PROPERTY), KotlinPropertyStub {
    private val nameRef = name

    init {
        if (isTopLevel && fqName == null) {
            throw IllegalArgumentException("fqName shouldn't be null for top level properties")
        }
        if (hasDelegateExpression && !hasDelegate) {
            throw IllegalArgumentException("Can't have delegate expression without delegate")
        }
    }

    override val name: String?
    get() = StringRef.toString(nameRef)

    @KtImplementationDetail
    override fun copyInto(newParent: StubElement<*>?): KotlinPropertyStubImpl = KotlinPropertyStubImpl(
        parent = newParent,
        name = nameRef,
        isVar = isVar,
        isTopLevel = isTopLevel,
        hasDelegate = hasDelegate,
        hasDelegateExpression = hasDelegateExpression,
        hasInitializer = hasInitializer,
        isExtension = isExtension,
        hasReturnTypeRef = hasReturnTypeRef,
        fqName = fqName,
        constantInitializer = constantInitializer,
        origin = origin,
        hasBackingField = hasBackingField,
        kdocText = kdocText,
    )

    @KtImplementationDetail
    override fun isEquivalentTo(other: KotlinStubElement<*>): Boolean =
        other is KotlinPropertyStubImpl &&
                other.nameRef == nameRef &&
                other.fqName == fqName &&
                other.isTopLevel == isTopLevel &&
                other.hasDelegate == hasDelegate &&
                other.hasDelegateExpression == hasDelegateExpression &&
                other.hasInitializer == hasInitializer &&
                other.isExtension == isExtension &&
                other.hasReturnTypeRef == hasReturnTypeRef &&
                other.hasBackingField == hasBackingField &&
                other.origin == origin &&
                other.isVar == isVar &&
                other.constantInitializer == constantInitializer &&
                other.kdocText == kdocText
}
