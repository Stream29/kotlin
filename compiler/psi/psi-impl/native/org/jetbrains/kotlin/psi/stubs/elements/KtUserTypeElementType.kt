/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.elements

import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.psi.stubs.KotlinUserTypeStub
import org.jetbrains.kotlin.psi.stubs.impl.KotlinUserTypeStubImpl



open class KtUserTypeElementType : KtStubElementType<KotlinUserTypeStubImpl,KtUserType> {
constructor(debugName: String) : super(debugName,KtUserType ::class,KotlinUserTypeStub ::class) {


}

override fun createStub(psi: KtUserType, parentStub: StubElement<*>?): KotlinUserTypeStubImpl {

return KotlinUserTypeStubImpl(parentStub,null,null)
}

override fun serialize(stub: KotlinUserTypeStubImpl, dataStream: StubOutputStream): Unit {

serializeTypeBean(dataStream,stub.upperBound)
serializeTypeBean(dataStream,stub.abbreviatedType)
}

override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): KotlinUserTypeStubImpl {

return KotlinUserTypeStubImpl((parentStub as StubElement<*>?),deserializeTypeBean(dataStream),deserializeClassTypeBean(dataStream))
}
}
