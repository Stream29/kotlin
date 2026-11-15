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

package org.jetbrains.kotlin.parsing


import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.idea.KotlinFileType



open class KotlinParser : PsiParser {
constructor(project: Project?) {


}

override fun parse(iElementType: IElementType, psiBuilder: PsiBuilder): ASTNode {

throw IllegalStateException("use another parse")
}

companion object {
fun parse(psiBuilder: PsiBuilder, psiFile: PsiFile): ASTNode {

var ktParsing: KotlinParsing = KotlinParsing.createForTopLevel(SemanticWhitespaceAwarePsiBuilderImpl(psiBuilder))
var extension: String = FileUtilRt.getExtension(psiFile.name)
if (extension.isEmpty() || extension.equals(KotlinFileType.EXTENSION) || isCompiledFile(psiFile)) {
ktParsing.parseFile()
}
else {
ktParsing.parseScript()
}
return psiBuilder.treeBuilt
}

private fun isCompiledFile(psiFile: PsiFile): Boolean {

return psiFile is org.jetbrains.kotlin.psi.KtCommonFile && ((psiFile as org.jetbrains.kotlin.psi.KtCommonFile)).isCompiled
}

fun parseTypeCodeFragment(psiBuilder: PsiBuilder): ASTNode {

var ktParsing: KotlinParsing = KotlinParsing.createForTopLevel(SemanticWhitespaceAwarePsiBuilderImpl(psiBuilder))
ktParsing.parseTypeCodeFragment()
return psiBuilder.treeBuilt
}

fun parseExpressionCodeFragment(psiBuilder: PsiBuilder): ASTNode {

var ktParsing: KotlinParsing = KotlinParsing.createForTopLevel(SemanticWhitespaceAwarePsiBuilderImpl(psiBuilder))
ktParsing.parseExpressionCodeFragment()
return psiBuilder.treeBuilt
}

fun parseBlockCodeFragment(psiBuilder: PsiBuilder): ASTNode {

var ktParsing: KotlinParsing = KotlinParsing.createForTopLevel(SemanticWhitespaceAwarePsiBuilderImpl(psiBuilder))
ktParsing.parseBlockCodeFragment()
return psiBuilder.treeBuilt
}

fun parseLambdaExpression(psiBuilder: PsiBuilder): ASTNode {

var ktParsing: KotlinParsing = KotlinParsing.createForTopLevel(SemanticWhitespaceAwarePsiBuilderImpl(psiBuilder))
ktParsing.parseLambdaExpression()
return psiBuilder.treeBuilt
}

fun parseBlockExpression(psiBuilder: PsiBuilder): ASTNode {

var ktParsing: KotlinParsing = KotlinParsing.createForTopLevel(SemanticWhitespaceAwarePsiBuilderImpl(psiBuilder))
ktParsing.parseBlockExpression()
return psiBuilder.treeBuilt
}
}
}
