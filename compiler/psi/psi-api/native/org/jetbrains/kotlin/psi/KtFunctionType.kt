/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import org.jetbrains.kotlin.KtNodeTypes

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.lexer.KtToken
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.stubs.KotlinFunctionTypeStub
import org.jetbrains.kotlin.resolution.KtResolvable



open class KtFunctionType : KtElementImplStub<KotlinFunctionTypeStub>, KtTypeElement, KtResolvable {
constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinFunctionTypeStub) : super(stub,KtNodeTypes.FUNCTION_TYPE) {


}

override fun getTypeArgumentsAsTypes(): List<KtTypeReference?> {

var result: MutableList<KtTypeReference?> = ArrayList()
var contextReceiversTypeRefs: List<KtTypeReference>? = getContextReceiversTypeReferences()
if (contextReceiversTypeRefs != null) {
result.addAll(contextReceiversTypeRefs)
}
var receiverTypeRef: KtTypeReference? = getReceiverTypeReference()
if (receiverTypeRef != null) {
result.add(receiverTypeRef)
}
for (ktParameter in getParameters()) {
result.add(ktParameter.getTypeReference())
}
var returnTypeRef: KtTypeReference? = getReturnTypeReference()
if (returnTypeRef != null) {
result.add(returnTypeRef)
}
return result
}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitFunctionType(this,data)
}

open fun getParameterList(): KtParameterList? {

return getStubOrPsiChild(KtNodeTypes.VALUE_PARAMETER_LIST, KtParameterList::class)
}

open fun getParameters(): List<KtParameter> {

var list: KtParameterList? = getParameterList()
return (if (list != null) list.getParameters() else emptyList())
}

open fun getReceiver(): KtFunctionTypeReceiver? {

return getStubOrPsiChild(KtNodeTypes.FUNCTION_TYPE_RECEIVER, KtFunctionTypeReceiver::class)
}

open fun getReceiverTypeReference(): KtTypeReference? {

var receiverDeclaration: KtFunctionTypeReceiver? = getReceiver()
if (receiverDeclaration == null) {
return null
}
return receiverDeclaration.getTypeReference()
}

open fun getContextReceiverList(): KtContextReceiverList? {

return (getContextParameterList() as KtContextReceiverList?)
}

open fun getContextParameterList(): KtContextParameterList? {

return getStubOrPsiChild(KtNodeTypes.CONTEXT_PARAMETER_LIST, KtContextParameterList::class)
}

open fun getContextReceiversTypeReferences(): List<KtTypeReference> {

var contextReceiverList: KtContextParameterList? = getContextParameterList()
if (contextReceiverList != null) {
return contextReceiverList.typeReferences()
}
else {
return emptyList()
}
}

open fun getReturnTypeReference(): KtTypeReference? {

return getStubOrPsiChild(KtNodeTypes.TYPE_REFERENCE, KtTypeReference::class)
}

open fun getTotalParameterCount(): Int {

var count: Int = 0
var contextReceiverList: KtContextParameterList? = getContextParameterList()
if (contextReceiverList != null) {
count += contextReceiverList.contextReceivers().size
}
var receiverDeclaration: KtFunctionTypeReceiver? = getReceiver()
if (receiverDeclaration != null) {
count ++
}
var list: List<KtParameter> = getParameters()
count += list.size
return count
}

companion object {
val RETURN_TYPE_SEPARATOR: KtToken = KtTokens.ARROW
}
}
