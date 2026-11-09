/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.elements

import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.jetbrains.kotlin.psi.KtTypeProjection
import org.jetbrains.kotlin.psi.stubs.KotlinTypeProjectionStub
import org.jetbrains.kotlin.psi.stubs.impl.KotlinTypeProjectionStubImpl



open class KtTypeProjectionElementType : KtStubElementType<KotlinTypeProjectionStubImpl,KtTypeProjection> {
constructor(debugName: String) : super(debugName,KtTypeProjection ::class,KotlinTypeProjectionStub ::class) {


}

override fun createStub(psi: KtTypeProjection, parentStub: StubElement<*>?): KotlinTypeProjectionStubImpl {

return KotlinTypeProjectionStubImpl(parentStub,psi.projectionKind.ordinal)
}

override fun serialize(stub: KotlinTypeProjectionStubImpl, dataStream: StubOutputStream): Unit {

dataStream.writeVarInt(stub.projectionKind.ordinal)
}

override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): KotlinTypeProjectionStubImpl {

var projectionKindOrdinal: Int = dataStream.readVarInt()
return KotlinTypeProjectionStubImpl(parentStub,projectionKindOrdinal)
}
}
