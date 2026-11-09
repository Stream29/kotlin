/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.impl




import com.intellij.psi.stubs.StubElement
import com.intellij.util.io.StringRef
import org.jetbrains.kotlin.psi.KtImplementationDetail
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.stubs.KotlinNameReferenceExpressionStub
import org.jetbrains.kotlin.psi.stubs.KotlinStubElement
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes



open class KotlinNameReferenceExpressionStubImpl : KotlinStubBaseImpl<KtNameReferenceExpression>, KotlinNameReferenceExpressionStub {
// StubBase can log this object before the original Java field is initialized.
private val referencedNameRef: StringRef?

private val myClassRef: Boolean

constructor(parent: StubElement<*>?, referencedName: StringRef, myClassRef: Boolean) : super(parent,KtStubElementTypes.REFERENCE_EXPRESSION) {

this.referencedNameRef = referencedName
this.myClassRef = myClassRef
}

open fun isClassRef(): Boolean {

return myClassRef
}

override val referencedName: String
get() {

return referencedNameRef!!.string!!
}

override fun copyInto(newParent: StubElement<*>?): KotlinNameReferenceExpressionStubImpl {

return KotlinNameReferenceExpressionStubImpl(newParent,referencedNameRef!!,myClassRef)
}

override fun isEquivalentTo(other: KotlinStubElement<*>): Boolean {

if (!(other is KotlinNameReferenceExpressionStubImpl)) return false
if (this.myClassRef != ((other as KotlinNameReferenceExpressionStubImpl)).myClassRef) return false
return this.referencedNameRef!!.equals(((other as KotlinNameReferenceExpressionStubImpl)).referencedNameRef)
}
}
