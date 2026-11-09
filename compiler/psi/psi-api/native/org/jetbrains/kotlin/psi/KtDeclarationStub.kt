/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.openapi.application.ApplicationManager
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import org.jetbrains.kotlin.kdoc.psi.api.KDoc



abstract class KtDeclarationStub<T : StubElement<*>> : KtModifierListOwnerStub<T>, KtDeclaration {
private var modificationStampCounter = kotlin.concurrent.atomics.AtomicLong(0L)

protected override fun copyKotlinPsiStateTo(clone: com.intellij.psi.impl.PsiElementBase) {
super.copyKotlinPsiStateTo(clone)
(clone as KtDeclarationStub<*>).modificationStampCounter = modificationStampCounter
}

constructor(stub: T, nodeType: IStubElementType<*, *>) : super(stub,nodeType) {


}

constructor(node: ASTNode) : super(node) {


}

override fun subtreeChanged(): Unit {

super<KtModifierListOwnerStub>.subtreeChanged()
modificationStampCounter.fetchAndAdd(1L)
}

open val modificationStamp: Long get() = getModificationStamp()

open fun getModificationStamp(): Long {

return modificationStampCounter.load()
}

override fun getDocComment(): KDoc? {

return org.jetbrains.kotlin.psi.findDocComment.findDocComment(this)
}

override val originalElement: PsiElement
get() {

var navigationPolicy: KotlinDeclarationNavigationPolicy? = ApplicationManager.getApplication().getService(KotlinDeclarationNavigationPolicy ::class)
return (if (navigationPolicy != null) navigationPolicy.getOriginalElement(this) else this)
}

override val navigationElement: PsiElement
get() {

var navigationPolicy: KotlinDeclarationNavigationPolicy? = ApplicationManager.getApplication().getService(KotlinDeclarationNavigationPolicy ::class)
return (if (navigationPolicy != null) navigationPolicy.getNavigationElement(this) else this)
}
}
