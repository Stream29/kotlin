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
import com.intellij.psi.PsiElement
import com.intellij.util.IncorrectOperationException
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.lexer.KtTokens



abstract class KtNamedDeclarationNotStubbed : KtDeclarationImpl, KtNamedDeclaration {
constructor(node: ASTNode) : super(node) {


}

override val name: String?
get() {

var identifier: PsiElement? = nameIdentifier
if (identifier != null) {
var text: String? = identifier.text
return (if (text != null) KtPsiUtil.unquoteIdentifier(text) else null)
}
else {
return null
}
}

override fun getNameAsName(): Name? {

var name: String? = name
return (if (name != null) Name.identifier(name) else null)
}

override fun getNameAsSafeName(): Name {

return KtPsiUtil.safeName(name)
}

override val nameIdentifier: PsiElement?
get() {

return findChildByType(KtTokens.IDENTIFIER)
}

override fun setName(name: String): PsiElement {

return KtPsiMutationService.getInstance().setNamedDeclarationName(this,name)
}

override val textOffset: Int
get() {

var identifier: PsiElement? = nameIdentifier
return (if (identifier != null) identifier.textRange!!.startOffset else textRange.startOffset)
}
}
