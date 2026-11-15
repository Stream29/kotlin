/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import org.jetbrains.kotlin.KtNodeTypes

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.stubs.KotlinModifierListStub



open class KtDeclarationModifierList : KtModifierList {
constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinModifierListStub) : super(stub,KtNodeTypes.MODIFIER_LIST) {


}

companion object {
val EMPTY_ARRAY: Array<KtDeclarationModifierList> = emptyArray()
}
}
