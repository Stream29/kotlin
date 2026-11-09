/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.elements

import org.jetbrains.kotlin.psi.fqName




import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import com.intellij.util.io.StringRef
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.stubs.KotlinParameterStub
import org.jetbrains.kotlin.psi.stubs.impl.KotlinParameterStubImpl



open class KtParameterElementType : KtStubElementType<KotlinParameterStubImpl,KtParameter> {
constructor(debugName: String) : super(debugName,KtParameter ::class,KotlinParameterStub ::class) {


}

override fun createStub(psi: KtParameter, parentStub: StubElement<*>?): KotlinParameterStubImpl {

var fqName: FqName? = psi.fqName
var fqNameRef: StringRef? = StringRef.fromString((if(fqName != null) fqName.asString() else null))
return KotlinParameterStubImpl((parentStub as StubElement<*>?),fqNameRef,StringRef.fromString(psi.name),psi.isMutable,psi.hasValOrVar(),psi.hasDefaultValue(),null)
}

override fun serialize(stub: KotlinParameterStubImpl, dataStream: StubOutputStream): Unit {

dataStream.writeName(stub.name)
dataStream.writeBoolean(stub.isMutable)
dataStream.writeBoolean(stub.hasValOrVar)
dataStream.writeBoolean(stub.hasDefaultValue)
var name: FqName? = stub.fqName
dataStream.writeName((if(name != null) name.asString() else null))
dataStream.writeName(stub.functionTypeParameterName)
}

override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): KotlinParameterStubImpl {

var name: StringRef? = dataStream.readName()
var isMutable: Boolean = dataStream.readBoolean()
var hasValOrValNode: Boolean = dataStream.readBoolean()
var hasDefaultValue: Boolean = dataStream.readBoolean()
var fqName: StringRef? = dataStream.readName()
return KotlinParameterStubImpl((parentStub as StubElement<*>?),fqName,name,isMutable,hasValOrValNode,hasDefaultValue,dataStream.readNameString())
}

override fun indexStub(stub: KotlinParameterStubImpl, sink: IndexSink): Unit {

StubIndexService.getInstance().indexParameter(stub,sink)
}
}
