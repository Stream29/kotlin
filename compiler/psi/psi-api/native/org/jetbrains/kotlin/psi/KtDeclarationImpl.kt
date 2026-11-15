/*
 * Copyright 2010-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.kdoc.psi.api.KDoc
import org.jetbrains.kotlin.lexer.KtModifierKeywordToken



abstract class KtDeclarationImpl : KtExpressionImpl, KtDeclaration {
constructor(node: ASTNode) : super(node) {


}

override fun getModifierList(): KtModifierList? {

return (findChildByType(KtNodeTypes.MODIFIER_LIST) as KtModifierList?)
}

override fun hasModifier(modifier: KtModifierKeywordToken): Boolean {

var modifierList: KtModifierList? = getModifierList()
return modifierList != null && modifierList.hasModifier(modifier)
}

override fun addModifier(modifier: KtModifierKeywordToken): Unit {

KtPsiMutationService.getInstance().addModifierKeyword(this,modifier)
}

override fun removeModifier(modifier: KtModifierKeywordToken): Unit {

KtPsiMutationService.getInstance().removeModifierKeyword(this,modifier)
}

override fun addAnnotationEntry(annotationEntry: KtAnnotationEntry): KtAnnotationEntry {

return KtPsiMutationService.getInstance().addAnnotation(this,annotationEntry)
}

override fun getAnnotationEntries(): List<KtAnnotationEntry> {

var modifierList: KtModifierList? = getModifierList()
if (modifierList == null) return emptyList()
return modifierList.getAnnotationEntries()
}

override fun getAnnotations(): List<KtAnnotation> {

var modifierList: KtModifierList? = getModifierList()
if (modifierList == null) return emptyList()
return modifierList.getAnnotations()
}

override fun getDocComment(): KDoc? {

return org.jetbrains.kotlin.psi.findDocComment.findDocComment(this)
}
}
