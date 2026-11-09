/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.elements

import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.jetbrains.kotlin.psi.KtElementImplStub
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub
import org.jetbrains.kotlin.psi.stubs.impl.KotlinPlaceHolderStubImpl



open class KtPlaceHolderStubElementType<T : KtElementImplStub<out StubElement<*>>> : KtStubElementType<KotlinPlaceHolderStubImpl<T>,T> {
constructor(debugName: String, psiClass: kotlin.reflect.KClass<T>) : super(debugName,psiClass,KotlinPlaceHolderStub ::class) {


}

override fun createStub(psi: T, parentStub: StubElement<*>?): KotlinPlaceHolderStubImpl<T> {

return KotlinPlaceHolderStubImpl(parentStub,this)
}

override fun serialize(stub: KotlinPlaceHolderStubImpl<T>, dataStream: StubOutputStream): Unit {


}

override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): KotlinPlaceHolderStubImpl<T> {

return KotlinPlaceHolderStubImpl(parentStub,this)
}
}
