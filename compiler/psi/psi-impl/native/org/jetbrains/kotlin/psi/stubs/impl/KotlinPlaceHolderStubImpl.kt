/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.impl

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import org.jetbrains.kotlin.psi.KtElementImplStub
import org.jetbrains.kotlin.psi.KtImplementationDetail
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub
import org.jetbrains.kotlin.psi.stubs.KotlinStubElement



open class KotlinPlaceHolderStubImpl<T : KtElementImplStub<out StubElement<*>>> : KotlinStubBaseImpl<T>, KotlinPlaceHolderStub<T> {
constructor(parent: StubElement<*>?, elementType: IStubElementType<*,*>) : super(parent,elementType) {


}

override fun copyInto(newParent: StubElement<*>?): KotlinPlaceHolderStubImpl<T> {

return KotlinPlaceHolderStubImpl(newParent,stubType)
}

override fun isEquivalentTo(other: KotlinStubElement<*>): Boolean {

if ((other!!)::class != this::class) return false
return this.elementType == other.elementType
}
}
