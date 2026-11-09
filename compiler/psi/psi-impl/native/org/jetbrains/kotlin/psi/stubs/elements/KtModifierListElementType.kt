/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.elements

import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import com.intellij.util.io.DataInputOutputUtil
import org.jetbrains.kotlin.psi.KtModifierList
import org.jetbrains.kotlin.psi.stubs.KotlinModifierListStub
import org.jetbrains.kotlin.psi.stubs.impl.KotlinModifierListStubImpl
import org.jetbrains.kotlin.psi.stubs.impl.ModifierMaskUtils.computeMaskFromModifierList



open class KtModifierListElementType<T : KtModifierList> : KtStubElementType<KotlinModifierListStubImpl,T> {
constructor(debugName: String, psiClass: kotlin.reflect.KClass<T>) : super(debugName,psiClass,KotlinModifierListStub ::class) {


}

override fun createStub(psi: T, parentStub: StubElement<*>?): KotlinModifierListStubImpl {

return KotlinModifierListStubImpl(parentStub,computeMaskFromModifierList(psi))
}

override fun serialize(stub: KotlinModifierListStubImpl, dataStream: StubOutputStream): Unit {

DataInputOutputUtil.writeLONG(dataStream,stub.getMask())
}

override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): KotlinModifierListStubImpl {

var mask: Long = DataInputOutputUtil.readLONG(dataStream)
return KotlinModifierListStubImpl(parentStub,mask)
}
}
