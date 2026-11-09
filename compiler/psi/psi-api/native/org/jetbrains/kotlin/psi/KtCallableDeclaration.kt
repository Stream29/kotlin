/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.psi.PsiElement



interface KtCallableDeclaration : KtNamedDeclaration, KtDeclarationWithReturnType, KtTypeParameterListOwner {
fun getValueParameterList(): KtParameterList?

fun getValueParameters(): List<KtParameter>

fun getReceiverTypeReference(): KtTypeReference?

fun getContextReceivers(): List<KtContextReceiver> {

var modifierList: KtModifierList? = getModifierList()
if (modifierList == null) {
return emptyList()
}
var contextParameterList: KtContextParameterList? = modifierList.getContextParameterList()
if (contextParameterList == null) {
return emptyList()
}
return contextParameterList.contextReceivers()
}

fun getContextParameters(): List<KtParameter> {

var modifierList: KtModifierList? = getModifierList()
if (modifierList == null) {
return emptyList()
}
var contextParameterList: KtContextParameterList? = modifierList.getContextParameterList()
if (contextParameterList == null) {
return emptyList()
}
return contextParameterList.contextParameters
}

override fun getTypeReference(): KtTypeReference?

fun setTypeReference(typeRef: KtTypeReference?): KtTypeReference?

fun getColon(): PsiElement?
}
