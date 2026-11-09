/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.elements

import org.jetbrains.kotlin.psi.fqName
import org.jetbrains.kotlin.psi.isLocal
import org.jetbrains.kotlin.psi.isTopLevel




import org.jetbrains.kotlin.psi.psiUtil.safeFqNameForLazyResolve as p1kOriginal_safeFqNameForLazyResolve
import org.jetbrains.kotlin.psi.stubs.StubUtils.deserializeKdocText as p1kOriginal_deserializeKdocText
import org.jetbrains.kotlin.psi.stubs.StubUtils.readNullableBoolean as p1kOriginal_readNullableBoolean
import org.jetbrains.kotlin.psi.stubs.StubUtils.serializeKdocText as p1kOriginal_serializeKdocText
import org.jetbrains.kotlin.psi.stubs.StubUtils.writeNullableBoolean as p1kOriginal_writeNullableBoolean

import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import com.intellij.psi.tree.IElementType
import com.intellij.util.io.StringRef
import org.jetbrains.kotlin.KtStubBasedElementTypes
import org.jetbrains.kotlin.constant.ConstantValue
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.stubs.KotlinPropertyStub
import org.jetbrains.kotlin.psi.stubs.StubUtils
import org.jetbrains.kotlin.psi.stubs.impl.KotlinPropertyStubImpl
import org.jetbrains.kotlin.psi.stubs.impl.KotlinStubOrigin



open class KtPropertyElementType : KtStubElementType<KotlinPropertyStubImpl,KtProperty> {
constructor(debugName: String) : super(debugName,KtProperty ::class,KotlinPropertyStub ::class) {


}

override fun shouldCreateStub(node: ASTNode): Boolean {

var parentNode: ASTNode = node.treeParent!!
var parentElementType: IElementType? = parentNode.elementType
if (parentElementType == KtStubElementTypes.CLASS_BODY || parentElementType == KtFileElementType) {
return true
}
if (parentElementType == KtStubBasedElementTypes.BLOCK) {
var grandparentElementType: IElementType? = parentNode.treeParent!!.elementType
if (grandparentElementType == KtStubElementTypes.SCRIPT) {
return true
}
if (grandparentElementType == KtStubElementTypes.FUNCTION || grandparentElementType == KtStubElementTypes.PROPERTY_ACCESSOR) {
return false
}
}
return !((node.psi as KtProperty)).isLocal
}

override fun createStub(psi: KtProperty, parentStub: StubElement<*>?): KotlinPropertyStubImpl {

assert(!psi.isLocal) {
    "Should not store local property: " + psi.text + ", parent " +
        (if (psi.parent != null) psi.parent!!.text else "<no parent>")
}
return KotlinPropertyStubImpl((parentStub as StubElement<*>?),StringRef.fromString(psi.name),psi.isVar,psi.isTopLevel,psi.hasDelegate(),psi.hasDelegateExpression(),psi.hasInitializer(),psi.getReceiverTypeReference() != null,psi.getTypeReference() != null,(psi).p1kOriginal_safeFqNameForLazyResolve(),null,null,null,null)
}

override fun serialize(stub: KotlinPropertyStubImpl, dataStream: StubOutputStream): Unit {

dataStream.writeName(stub.name)
dataStream.writeBoolean(stub.isVar)
dataStream.writeBoolean(stub.isTopLevel)
dataStream.writeBoolean(stub.hasDelegate)
dataStream.writeBoolean(stub.hasDelegateExpression)
dataStream.writeBoolean(stub.hasInitializer)
dataStream.writeBoolean(stub.isExtension)
dataStream.writeBoolean(stub.hasReturnTypeRef)
var fqName: FqName? = stub.fqName
dataStream.writeName((if(fqName != null) fqName.asString() else null))
var constantInitializer: ConstantValue<*>? = stub.constantInitializer
org.jetbrains.kotlin.psi.stubs.impl.serializeConstantValue(constantInitializer,dataStream)
KotlinStubOrigin.serialize(stub.origin,dataStream)
(dataStream).p1kOriginal_writeNullableBoolean(stub.hasBackingField)
(dataStream).p1kOriginal_serializeKdocText(stub.kdocText)
}

override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): KotlinPropertyStubImpl {

var name: StringRef? = dataStream.readName()
var isVar: Boolean = dataStream.readBoolean()
var isTopLevel: Boolean = dataStream.readBoolean()
var hasDelegate: Boolean = dataStream.readBoolean()
var hasDelegateExpression: Boolean = dataStream.readBoolean()
var hasInitializer: Boolean = dataStream.readBoolean()
var hasReceiverTypeRef: Boolean = dataStream.readBoolean()
var hasReturnTypeRef: Boolean = dataStream.readBoolean()
var fqNameAsString: StringRef? = dataStream.readName()
var fqName: FqName? = (if (fqNameAsString != null) FqName(fqNameAsString.toString()) else null)
var constantInitializer: ConstantValue<*>? = org.jetbrains.kotlin.psi.stubs.impl.deserializeConstantValue(dataStream)
var stubOrigin: KotlinStubOrigin? = KotlinStubOrigin.deserialize(dataStream)
var hasBackingFiled: Boolean? = (dataStream).p1kOriginal_readNullableBoolean()
var kdocText: String? = (dataStream).p1kOriginal_deserializeKdocText()
return KotlinPropertyStubImpl((parentStub as StubElement<*>?),name,isVar,isTopLevel,hasDelegate,hasDelegateExpression,hasInitializer,hasReceiverTypeRef,hasReturnTypeRef,fqName,constantInitializer,stubOrigin,hasBackingFiled,kdocText)
}

override fun indexStub(stub: KotlinPropertyStubImpl, sink: IndexSink): Unit {

StubIndexService.getInstance().indexProperty(stub,sink)
}
}
