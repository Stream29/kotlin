/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject as p1kOriginal_containingClassOrObject

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.KtStubBasedElementTypes
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.stubs.KotlinClassStub



open class KtEnumEntry : KtClass, KtDeclarationWithReturnType {
constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinClassStub) : super(stub,KtStubBasedElementTypes.ENUM_ENTRY) {


}

override fun getSuperTypeListEntries(): List<KtSuperTypeListEntry> {

var initializerList: KtInitializerList? = getInitializerList()
if (initializerList == null) {
return emptyList()
}
return initializerList.getInitializers()
}

open fun hasInitializer(): Boolean {

return ! getSuperTypeListEntries().isEmpty()
}

override fun getClassId(): ClassId? {

return null
}

open fun getInitializerList(): KtInitializerList? {

return getStubOrPsiChild(KtStubBasedElementTypes.INITIALIZER_LIST)
}

override fun isEquivalentTo(another: PsiElement?): Boolean {

if (this == another) return true
if (!(another is KtEnumEntry)) return false
var anotherEnumEntry: KtEnumEntry = (another as KtEnumEntry)
if (name != anotherEnumEntry.name) return false
var thisContainingClass: KtClassOrObject? = (this).p1kOriginal_containingClassOrObject
if (thisContainingClass == null) return false
var anotherContainingClass: KtClassOrObject? = (anotherEnumEntry).p1kOriginal_containingClassOrObject
return thisContainingClass.isEquivalentTo(anotherContainingClass)
}

open fun getComma(): PsiElement? {

return this.findLastChildByType(KtTokens.COMMA)
}

open fun getSemicolon(): PsiElement? {

return this.findLastChildByType(KtTokens.SEMICOLON)
}

open fun addSemicolon(): PsiElement {

return KtPsiMutationService.getInstance().addEnumEntrySemicolon(this)
}

override fun delete(): Unit {

KtPsiMutationService.getInstance().deleteEnumEntry(this)
}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitEnumEntry(this,data)
}

override fun getTypeReference(): KtTypeReference? {

return null
}
}
