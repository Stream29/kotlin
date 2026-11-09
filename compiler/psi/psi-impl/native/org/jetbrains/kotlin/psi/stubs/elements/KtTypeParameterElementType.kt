/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.elements

import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import com.intellij.util.io.StringRef
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.psi.stubs.KotlinTypeParameterStub
import org.jetbrains.kotlin.psi.stubs.impl.KotlinTypeParameterStubImpl



open class KtTypeParameterElementType : KtStubElementType<KotlinTypeParameterStubImpl,KtTypeParameter> {
constructor(debugName: String) : super(debugName,KtTypeParameter ::class,KotlinTypeParameterStub ::class) {


}

override fun createStub(psi: KtTypeParameter, parentStub: StubElement<*>?): KotlinTypeParameterStubImpl {

return KotlinTypeParameterStubImpl((parentStub as StubElement<*>?),StringRef.fromString(psi.name))
}

override fun serialize(stub: KotlinTypeParameterStubImpl, dataStream: StubOutputStream): Unit {

dataStream.writeName(stub.name)
}

override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): KotlinTypeParameterStubImpl {

var name: StringRef? = dataStream.readName()
return KotlinTypeParameterStubImpl((parentStub as StubElement<*>?),name)
}
}
