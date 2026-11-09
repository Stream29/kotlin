/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.IStubElementType
import org.jetbrains.kotlin.KtStubBasedElementTypes
import org.jetbrains.kotlin.psi.stubs.KotlinStubWithFqName



abstract class KtTypeParameterListOwnerStub<T : KotlinStubWithFqName<*>> : KtNamedDeclarationStub<T>, KtTypeParameterListOwner {
constructor(stub: T, nodeType: IStubElementType<*, *>) : super(stub,nodeType) {


}

constructor(node: ASTNode) : super(node) {


}

override fun getTypeParameterList(): KtTypeParameterList? {

return getStubOrPsiChild(KtStubBasedElementTypes.TYPE_PARAMETER_LIST)
}

override fun getTypeConstraintList(): KtTypeConstraintList? {

return getStubOrPsiChild(KtStubBasedElementTypes.TYPE_CONSTRAINT_LIST)
}

override fun getTypeConstraints(): List<KtTypeConstraint> {

var typeConstraintList: KtTypeConstraintList? = getTypeConstraintList()
if (typeConstraintList == null) {
return emptyList()
}
return typeConstraintList.getConstraints()
}

override fun getTypeParameters(): List<KtTypeParameter> {

var list: KtTypeParameterList? = getTypeParameterList()
if (list == null) return emptyList()
return list.getParameters()
}

open fun getContextReceiverList(): KtContextReceiverList? {

var modifierList: KtModifierList? = getModifierList()
return (if (modifierList == null) null else (modifierList.getContextParameterList() as KtContextReceiverList?))
}

open fun getContextReceiverLists(): List<KtContextReceiverList> {

var modifierList: KtModifierList? = getModifierList()
return (if (modifierList == null) emptyList() else (modifierList.getContextParameterLists() as List<KtContextReceiverList>))
}
}
