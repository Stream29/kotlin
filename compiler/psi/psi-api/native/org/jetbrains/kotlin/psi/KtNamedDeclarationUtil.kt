/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject as p1kOriginal_containingClassOrObject

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name



class KtNamedDeclarationUtil {
private constructor() {


}

companion object {
fun getFQName(namedDeclaration: KtNamedDeclaration): FqName? {

var name: Name? = namedDeclaration.getNameAsName()
if (name == null) {
return null
}
var parentFqName: FqName? = getParentFqName(namedDeclaration)
if (parentFqName == null) {
return null
}
return parentFqName.child(name)
}

fun getParentFqName(namedDeclaration: KtNamedDeclaration): FqName? {

var parent: PsiElement? = namedDeclaration.parent
if (parent is KtClassBody) {
parent = ((parent as KtClassBody)).p1kOriginal_containingClassOrObject
}
if (parent is KtFile) {
return ((parent as KtFile)).packageFqName
}
else if (parent is KtNamedFunction || parent is KtClass) {
return getFQName((parent as KtNamedDeclaration))
}
else if (namedDeclaration is KtParameter) {
var constructorClass: KtClassOrObject? = KtPsiUtil.getClassIfParameterIsProperty((namedDeclaration as KtParameter))
if (constructorClass != null) {
return getFQName(constructorClass)
}
}
else if (parent is KtObjectDeclaration) {
return getFQName((parent as KtNamedDeclaration))
}
else if (parent is KtBlockExpression) {
var parentOfParent: PsiElement? = parent.parent
if (parentOfParent is KtScript) {
return ((parentOfParent as KtScript)).fqName
}
}
return null
}
}
}
