/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.psi.PsiElement



interface KtDeclarationWithBody : KtDeclaration {
fun getBodyExpression(): KtExpression?

fun getEqualsToken(): PsiElement?

override val name: String?

fun getContractDescription(): KtContractEffectList? {

return null
}

fun hasContractEffectList(): Boolean {

return getContractDescription() != null
}

fun mayHaveContract(): Boolean {

return false
}

fun hasBlockBody(): Boolean

fun hasBody(): Boolean

fun hasDeclaredReturnType(): Boolean

fun getValueParameters(): List<KtParameter>

fun getBodyBlockExpression(): KtBlockExpression? {

var bodyExpression: KtExpression? = getBodyExpression()
if (bodyExpression is KtBlockExpression) {
return (bodyExpression as KtBlockExpression)
}
return null
}
}
