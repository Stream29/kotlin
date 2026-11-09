/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.elements

import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.jetbrains.kotlin.psi.KtBackingField
import org.jetbrains.kotlin.psi.stubs.KotlinBackingFieldStub
import org.jetbrains.kotlin.psi.stubs.impl.KotlinBackingFieldStubImpl



open class KtBackingFieldElementType : KtStubElementType<KotlinBackingFieldStubImpl,KtBackingField> {
constructor(debugName: String) : super(debugName,KtBackingField ::class,KotlinBackingFieldStub ::class) {


}

override fun createStub(psi: KtBackingField, parentStub: StubElement<*>?): KotlinBackingFieldStubImpl {

return KotlinBackingFieldStubImpl(parentStub,psi.hasInitializer())
}

override fun serialize(stub: KotlinBackingFieldStubImpl, dataStream: StubOutputStream): Unit {

dataStream.writeBoolean(stub.hasInitializer)
}

override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): KotlinBackingFieldStubImpl {

var hasInitializer: Boolean = dataStream.readBoolean()
return KotlinBackingFieldStubImpl(parentStub,hasInitializer)
}
}
