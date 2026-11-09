/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.elements

import org.jetbrains.kotlin.psi.isValidImport







import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import com.intellij.util.io.StringRef
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.stubs.KotlinImportDirectiveStub
import org.jetbrains.kotlin.psi.stubs.impl.KotlinImportDirectiveStubImpl



open class KtImportDirectiveElementType : KtStubElementType<KotlinImportDirectiveStubImpl,KtImportDirective> {
constructor(debugName: String) : super(debugName,KtImportDirective ::class,KotlinImportDirectiveStub ::class) {


}

override fun createStub(psi: KtImportDirective, parentStub: StubElement<*>?): KotlinImportDirectiveStubImpl {

var importedFqName: FqName? = psi.importedFqName
var fqName: StringRef? = StringRef.fromString((if(importedFqName == null) null else importedFqName.asString()))
return KotlinImportDirectiveStubImpl(parentStub,psi.isAllUnder,fqName,psi.isValidImport())
}

override fun serialize(stub: KotlinImportDirectiveStubImpl, dataStream: StubOutputStream): Unit {

dataStream.writeBoolean(stub.isAllUnder)
var importedFqName: FqName? = stub.importedFqName
dataStream.writeName((if(importedFqName != null) importedFqName.asString() else null))
dataStream.writeBoolean(stub.isValid)
}

override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): KotlinImportDirectiveStubImpl {

var isAllUnder: Boolean = dataStream.readBoolean()
var importedName: StringRef? = dataStream.readName()
var isValid: Boolean = dataStream.readBoolean()
return KotlinImportDirectiveStubImpl(parentStub,isAllUnder,importedName,isValid)
}
}
