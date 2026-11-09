/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.elements

import org.jetbrains.kotlin.psi.classId
import org.jetbrains.kotlin.psi.fqName
import org.jetbrains.kotlin.psi.isLocal
import org.jetbrains.kotlin.psi.isObjectLiteral
import org.jetbrains.kotlin.psi.isTopLevel




import org.jetbrains.kotlin.psi.psiUtil.getSuperNames as p1kOriginal_getSuperNames
import org.jetbrains.kotlin.psi.psiUtil.safeFqNameForLazyResolve as p1kOriginal_safeFqNameForLazyResolve
import org.jetbrains.kotlin.psi.stubs.StubUtils.deserializeKdocText as p1kOriginal_deserializeKdocText
import org.jetbrains.kotlin.psi.stubs.StubUtils.serializeKdocText as p1kOriginal_serializeKdocText

import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import com.intellij.util.io.StringRef
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.stubs.KotlinObjectStub
import org.jetbrains.kotlin.psi.stubs.StubUtils
import org.jetbrains.kotlin.psi.stubs.impl.KotlinObjectStubImpl
import org.jetbrains.kotlin.psi.stubs.impl.Utils



open class KtObjectElementType : KtStubElementType<KotlinObjectStubImpl,KtObjectDeclaration> {
constructor(debugName: String) : super(debugName,KtObjectDeclaration ::class,KotlinObjectStub ::class) {


}

override fun shouldCreateStub(node: ASTNode): Boolean {

return true
}

override fun createStub(psi: KtObjectDeclaration, parentStub: StubElement<*>?): KotlinObjectStubImpl {

var name: String? = psi.name
var fqName: FqName? = (psi).p1kOriginal_safeFqNameForLazyResolve()
var superNames: List<String> = (psi).p1kOriginal_getSuperNames()
var classId: ClassId? = StubUtils.createClassId(parentStub!!,psi)
return KotlinObjectStubImpl((parentStub as StubElement<*>?),StringRef.fromString(name),fqName,classId,Utils.wrapStrings(superNames),psi.isTopLevel,psi.isLocal,psi.isObjectLiteral,null)
}

override fun serialize(stub: KotlinObjectStubImpl, dataStream: StubOutputStream): Unit {

dataStream.writeName(stub.name)
var fqName: FqName? = stub.fqName
dataStream.writeName((if(fqName != null) fqName.toString() else null))
StubUtils.serializeClassId(dataStream,stub.classId)
dataStream.writeBoolean(stub.isTopLevel)
dataStream.writeBoolean(stub.isLocal)
dataStream.writeBoolean(stub.isObjectLiteral)
(dataStream).p1kOriginal_serializeKdocText(stub.kdocText)
var superNames: List<String> = stub.superNames
dataStream.writeVarInt(superNames.size)
for (name in superNames) {
dataStream.writeName(name)
}
}

override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): KotlinObjectStubImpl {

var name: StringRef? = dataStream.readName()
var fqNameStr: StringRef? = dataStream.readName()
var fqName: FqName? = (if (fqNameStr != null) FqName(fqNameStr.toString()) else null)
var classId: ClassId? = StubUtils.deserializeClassId(dataStream)
var isTopLevel: Boolean = dataStream.readBoolean()
var isLocal: Boolean = dataStream.readBoolean()
var isObjectLiteral: Boolean = dataStream.readBoolean()
var kdocText: String? = (dataStream).p1kOriginal_deserializeKdocText()
var superCount: Int = dataStream.readVarInt()
@Suppress("UNCHECKED_CAST")
var superNames: Array<StringRef> = StringRef.createArray(superCount) as Array<StringRef>
run {
var i: Int = 0
forLoop1@ while (i < superCount) {
forBody2@ do {
superNames[i] = dataStream.readName()!!
} while (false)
i ++
}
}
return KotlinObjectStubImpl((parentStub as StubElement<*>?),name,fqName,classId,superNames,isTopLevel,isLocal,isObjectLiteral,kdocText)
}

override fun indexStub(stub: KotlinObjectStubImpl, sink: IndexSink): Unit {

StubIndexService.getInstance().indexObject(stub,sink)
}
}
