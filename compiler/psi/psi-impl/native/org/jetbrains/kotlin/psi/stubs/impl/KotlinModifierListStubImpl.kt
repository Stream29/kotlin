/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.jetbrains.kotlin.lexer.KtModifierKeywordToken
import org.jetbrains.kotlin.psi.KtDeclarationModifierList
import org.jetbrains.kotlin.psi.KtImplementationDetail
import org.jetbrains.kotlin.psi.stubs.KotlinModifierListStub
import org.jetbrains.kotlin.psi.stubs.KotlinModifierListStub.SpecialFlag
import org.jetbrains.kotlin.psi.stubs.KotlinStubElement
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes



open class KotlinModifierListStubImpl : KotlinStubBaseImpl<KtDeclarationModifierList>, KotlinModifierListStub {
private val mask: Long

constructor(parent: StubElement<*>?, mask: Long) : super(parent,KtStubElementTypes.MODIFIER_LIST) {

this.mask = mask
}

open fun getMask(): Long {

return mask
}

override fun hasModifier(modifierToken: KtModifierKeywordToken): Boolean {

return ModifierMaskUtils.maskHasModifier(mask,modifierToken)
}

override fun hasSpecialFlag(flag: SpecialFlag): Boolean {

return ModifierMaskUtils.maskHasSpecialFlag(mask,flag)
}

override fun toString(): String {

return super.toString() + ModifierMaskUtils.maskToString(mask)
}

open fun hasAnyModifier(): Boolean {

return mask != 0L
}

override fun copyInto(newParent: StubElement<*>?): KotlinModifierListStubImpl {

return KotlinModifierListStubImpl(newParent,mask)
}

override fun isEquivalentTo(other: KotlinStubElement<*>): Boolean {

if (!(other is KotlinModifierListStubImpl)) return false
return this.mask == ((other as KotlinModifierListStubImpl)).mask
}
}
