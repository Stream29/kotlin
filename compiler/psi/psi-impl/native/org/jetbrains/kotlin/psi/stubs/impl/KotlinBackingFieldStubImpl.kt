/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.jetbrains.kotlin.psi.KtBackingField
import org.jetbrains.kotlin.psi.KtImplementationDetail
import org.jetbrains.kotlin.psi.stubs.KotlinBackingFieldStub
import org.jetbrains.kotlin.psi.stubs.KotlinStubElement
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes



open class KotlinBackingFieldStubImpl : KotlinStubBaseImpl<KtBackingField>, KotlinBackingFieldStub {
private val hasInitializerValue: Boolean

constructor(parent: StubElement<*>?, hasInitializer: Boolean) : super(parent,KtStubElementTypes.BACKING_FIELD) {

this.hasInitializerValue = hasInitializer
}

override val hasInitializer: Boolean
get() {

return hasInitializerValue
}

override fun copyInto(newParent: StubElement<*>?): KotlinBackingFieldStubImpl {

return KotlinBackingFieldStubImpl(newParent,hasInitializerValue)
}

override fun isEquivalentTo(other: KotlinStubElement<*>): Boolean {

if (!(other is KotlinBackingFieldStubImpl)) return false
return hasInitializerValue == ((other as KotlinBackingFieldStubImpl)).hasInitializerValue
}
}
