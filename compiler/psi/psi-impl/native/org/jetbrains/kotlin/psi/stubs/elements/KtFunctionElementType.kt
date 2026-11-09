/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.elements

import org.jetbrains.kotlin.psi.fqName
import org.jetbrains.kotlin.psi.isTopLevel







import org.jetbrains.kotlin.psi.psiUtil.safeFqNameForLazyResolve as p1kOriginal_safeFqNameForLazyResolve
import org.jetbrains.kotlin.psi.stubs.StubUtils.deserializeKdocText as p1kOriginal_deserializeKdocText
import org.jetbrains.kotlin.psi.stubs.StubUtils.readContract as p1kOriginal_readContract
import org.jetbrains.kotlin.psi.stubs.StubUtils.serializeKdocText as p1kOriginal_serializeKdocText
import org.jetbrains.kotlin.psi.stubs.StubUtils.writeContract as p1kOriginal_writeContract

import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import com.intellij.util.io.StringRef
import org.jetbrains.kotlin.contracts.description.KtContractDescriptionElement
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.stubs.KotlinFunctionStub
import org.jetbrains.kotlin.psi.stubs.StubUtils
import org.jetbrains.kotlin.psi.stubs.impl.KotlinFunctionStubImpl
import org.jetbrains.kotlin.psi.stubs.impl.KotlinStubOrigin
import org.jetbrains.kotlin.psi.stubs.impl.KotlinTypeBean



open class KtFunctionElementType : KtStubElementType<KotlinFunctionStubImpl,KtNamedFunction> {
constructor(debugName: String) : super(debugName,KtNamedFunction ::class,KotlinFunctionStub ::class) {


}

override fun shouldCreateStub(node: ASTNode): Boolean {

return true
}

override fun createStub(psi: KtNamedFunction, parentStub: StubElement<*>?): KotlinFunctionStubImpl {

var isTopLevel: Boolean = psi.parent is KtFile
var isExtension: Boolean = psi.getReceiverTypeReference() != null
var fqName: FqName? = (psi).p1kOriginal_safeFqNameForLazyResolve()
var hasNoExpressionBody: Boolean = psi.hasBlockBody()
var hasBody: Boolean = psi.hasBody()
return KotlinFunctionStubImpl((parentStub as StubElement<*>?),StringRef.fromString(psi.name),isTopLevel,fqName,isExtension,hasNoExpressionBody,hasBody,psi.hasTypeParameterListBeforeFunctionName(),psi.mayHaveContract(),null,null,null)
}

override fun serialize(stub: KotlinFunctionStubImpl, dataStream: StubOutputStream): Unit {

dataStream.writeName(stub.name)
dataStream.writeBoolean(stub.isTopLevel)
var fqName: FqName? = stub.fqName
dataStream.writeName((if(fqName != null) fqName.asString() else null))
dataStream.writeBoolean(stub.isExtension)
dataStream.writeBoolean(stub.hasNoExpressionBody)
dataStream.writeBoolean(stub.hasBody)
dataStream.writeBoolean(stub.hasTypeParameterListBeforeFunctionName)
var haveContract: Boolean = stub.mayHaveContract
dataStream.writeBoolean(haveContract)
if (haveContract) {
(dataStream).p1kOriginal_writeContract(stub.contract)
}
(dataStream).p1kOriginal_serializeKdocText(stub.kdocText)
KotlinStubOrigin.serialize(stub.origin,dataStream)
}

override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): KotlinFunctionStubImpl {

var name: StringRef? = dataStream.readName()
var isTopLevel: Boolean = dataStream.readBoolean()
var fqNameAsString: StringRef? = dataStream.readName()
var fqName: FqName? = (if (fqNameAsString != null) FqName(fqNameAsString.toString()) else null)
var isExtension: Boolean = dataStream.readBoolean()
var hasNoExpressionBody: Boolean = dataStream.readBoolean()
var hasBody: Boolean = dataStream.readBoolean()
var hasTypeParameterListBeforeFunctionName: Boolean = dataStream.readBoolean()
var mayHaveContract: Boolean = dataStream.readBoolean()
var contract: List<KtContractDescriptionElement<KotlinTypeBean, Nothing?>>? = (if (mayHaveContract) (dataStream).p1kOriginal_readContract() else null)
var kdocText: String? = (dataStream).p1kOriginal_deserializeKdocText()
return KotlinFunctionStubImpl((parentStub as StubElement<*>?),name,isTopLevel,fqName,isExtension,hasNoExpressionBody,hasBody,hasTypeParameterListBeforeFunctionName,mayHaveContract,kdocText,contract,KotlinStubOrigin.deserialize(dataStream))
}

override fun indexStub(stub: KotlinFunctionStubImpl, sink: IndexSink): Unit {

StubIndexService.getInstance().indexFunction(stub,sink)
}

override val externalId: String
get() {

return NAME
}

companion object {
private const val NAME: String = "kotlin.FUNCTION"
}
}
