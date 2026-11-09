/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import org.jetbrains.kotlin.util.NativeArrays as Arrays

import com.intellij.lang.ASTNode
import com.intellij.util.IncorrectOperationException
import org.jetbrains.kotlin.KtStubBasedElementTypes
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub
import org.jetbrains.kotlin.psi.stubs.elements.KtTokenSets



open class KtSuperTypeList : KtElementImplStub<KotlinPlaceHolderStub<KtSuperTypeList>> {
private var modificationStampCounter = kotlin.concurrent.atomics.AtomicLong(0L)

protected override fun copyKotlinPsiStateTo(clone: com.intellij.psi.impl.PsiElementBase) {
super.copyKotlinPsiStateTo(clone)
(clone as KtSuperTypeList).modificationStampCounter = modificationStampCounter
}

constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinPlaceHolderStub<KtSuperTypeList>) : super(stub,KtStubBasedElementTypes.SUPER_TYPE_LIST) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitSuperTypeList(this,data)
}

open fun addEntry(entry: KtSuperTypeListEntry): KtSuperTypeListEntry {

return KtPsiMutationService.getInstance().addSuperType(this,entry)
}

open fun removeEntry(entry: KtSuperTypeListEntry): Unit {

KtPsiMutationService.getInstance().removeSuperType(this,entry)
}

override fun delete(): Unit {

KtPsiMutationService.getInstance().deleteSuperTypeList(this)
}

@Suppress("UNCHECKED_CAST")
open fun getEntries(): List<KtSuperTypeListEntry> {

return Arrays.asList(getStubOrPsiChildren(KtTokenSets.SUPER_TYPE_LIST_ENTRIES,KtSuperTypeListEntry.ARRAY_FACTORY)) as List<KtSuperTypeListEntry>
}

override fun subtreeChanged(): Unit {

super<KtElementImplStub>.subtreeChanged()
modificationStampCounter.fetchAndAdd(1L)
}

open val modificationStamp: Long get() = getModificationStamp()

open fun getModificationStamp(): Long {

return modificationStampCounter.load()
}
}
