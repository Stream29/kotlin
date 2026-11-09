/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType



open class KtContainerNode : KtElementImpl {
constructor(node: ASTNode) : super(node) {


}

public override fun <T : Any> findChildByClass(aClass: kotlin.reflect.KClass<T>): T? {

return super<KtElementImpl>.findChildByClass(aClass)
}

public override fun <T : PsiElement> findChildByType(type: IElementType): T? {

return super<KtElementImpl>.findChildByType(type)
}
}
