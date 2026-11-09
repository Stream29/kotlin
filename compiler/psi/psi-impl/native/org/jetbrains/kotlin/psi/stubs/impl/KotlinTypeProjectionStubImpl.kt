/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.jetbrains.kotlin.psi.KtImplementationDetail
import org.jetbrains.kotlin.psi.KtProjectionKind
import org.jetbrains.kotlin.psi.KtTypeProjection
import org.jetbrains.kotlin.psi.stubs.KotlinStubElement
import org.jetbrains.kotlin.psi.stubs.KotlinTypeProjectionStub
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes



open class KotlinTypeProjectionStubImpl : KotlinStubBaseImpl<KtTypeProjection>, KotlinTypeProjectionStub {
private val projectionKindOrdinal: Int

constructor(parent: StubElement<*>?, projectionKindOrdinal: Int) : super(parent,KtStubElementTypes.TYPE_PROJECTION) {

this.projectionKindOrdinal = projectionKindOrdinal
}

override val projectionKind: KtProjectionKind
get() {

return KtProjectionKind.values()[projectionKindOrdinal]
}

override fun copyInto(newParent: StubElement<*>?): KotlinTypeProjectionStubImpl {

return KotlinTypeProjectionStubImpl(newParent,projectionKindOrdinal)
}

override fun isEquivalentTo(other: KotlinStubElement<*>): Boolean {

if (!(other is KotlinTypeProjectionStubImpl)) return false
return this.projectionKindOrdinal == ((other as KotlinTypeProjectionStubImpl)).projectionKindOrdinal
}
}
