/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.elements

import org.jetbrains.kotlin.psi.referencedName
import org.jetbrains.kotlin.psi.stubs.impl.isClassRef






import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import com.intellij.util.io.StringRef
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.stubs.KotlinNameReferenceExpressionStub
import org.jetbrains.kotlin.psi.stubs.impl.KotlinNameReferenceExpressionStubImpl



open class KtNameReferenceExpressionElementType : KtStubElementType<KotlinNameReferenceExpressionStubImpl,KtNameReferenceExpression> {
constructor(debugName: String) : super(debugName,KtNameReferenceExpression ::class,KotlinNameReferenceExpressionStub ::class) {


}

override fun createStub(psi: KtNameReferenceExpression, parentStub: StubElement<*>?): KotlinNameReferenceExpressionStubImpl {

return KotlinNameReferenceExpressionStubImpl(parentStub,StringRef.fromString(psi.referencedName)!!,false)
}

override fun serialize(stub: KotlinNameReferenceExpressionStubImpl, dataStream: StubOutputStream): Unit {

dataStream.writeName(stub.referencedName)
dataStream.writeBoolean(stub.isClassRef())
}

override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): KotlinNameReferenceExpressionStubImpl {

var referencedName: StringRef = dataStream.readName()!!
var isClassRef: Boolean = dataStream.readBoolean()
return KotlinNameReferenceExpressionStubImpl(parentStub,referencedName,isClassRef)
}
}
