/*
 * Copyright 2010-2017 JetBrains s.r.o.
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

package org.jetbrains.kotlin







import com.intellij.lang.ASTNode
import com.intellij.lang.Language
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiBuilderFactory
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IErrorCounterReparseableElementType
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.lexer.KotlinLexer
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.parsing.KotlinParser
import org.jetbrains.kotlin.psi.KtFunctionLiteral
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtParameterList



open class LambdaExpressionElementType : IErrorCounterReparseableElementType {
constructor() : super("LAMBDA_EXPRESSION",KotlinLanguage.INSTANCE) {


}

override fun parseContents(chameleon: ASTNode): ASTNode? {

var project: Project = chameleon.psi!!.project
var builder: PsiBuilder = PsiBuilderFactory.getInstance().createBuilder(project,chameleon,null,KotlinLanguage.INSTANCE,chameleon.chars)
return KotlinParser.parseLambdaExpression(builder).firstChildNode
}

override fun createNode(text: CharSequence?): ASTNode {

return KtLambdaExpression(text)
}

override fun isParsable(parent: ASTNode?, buffer: CharSequence, fileLanguage: Language, project: Project): Boolean {

return super.isParsable(parent,buffer,fileLanguage,project) && ! wasArrowMovedOrDeleted(parent,buffer) && ! wasParameterCommaMovedOrDeleted(parent,buffer)
}

override fun getErrorsCount(seq: CharSequence, fileLanguage: Language, project: Project): Int {

return ElementTypeUtils.getKotlinBlockImbalanceCount(seq)
}

companion object {
private fun wasArrowMovedOrDeleted(parent: ASTNode?, buffer: CharSequence): Boolean {

var lambdaExpression: KtLambdaExpression? = findLambdaExpression(parent)
if (lambdaExpression == null) {
return false
}
var literal: KtFunctionLiteral = lambdaExpression.getFunctionLiteral()
var arrow: PsiElement? = literal.getArrow()
if (arrow == null) return false
var arrowOffset: Int = arrow.startOffsetInParent + literal.startOffsetInParent
return hasTokenMoved(lambdaExpression.text,buffer,arrowOffset,KtTokens.ARROW)
}

private fun wasParameterCommaMovedOrDeleted(parent: ASTNode?, buffer: CharSequence): Boolean {

var lambdaExpression: KtLambdaExpression? = findLambdaExpression(parent)
if (lambdaExpression == null) {
return false
}
var literal: KtFunctionLiteral = lambdaExpression.getFunctionLiteral()
var valueParameterList: KtParameterList? = literal.getValueParameterList()
if (valueParameterList == null || valueParameterList.getParameters().size <= 1) {
return false
}
var comma: PsiElement? = valueParameterList.getFirstComma()
if (comma == null) {
return false
}
var commaOffset: Int = comma.textOffset - lambdaExpression.textOffset
return hasTokenMoved(lambdaExpression.text,buffer,commaOffset,KtTokens.COMMA)
}

private fun findLambdaExpression(parent: ASTNode?): KtLambdaExpression? {

if (parent == null) return null
var parentPsi: PsiElement? = parent.psi
var lambdaExpressions: Array<KtLambdaExpression>? = PsiTreeUtil.getChildrenOfType(parentPsi,KtLambdaExpression ::class)
if (lambdaExpressions == null || lambdaExpressions.size != 1) return null
return lambdaExpressions[0]
}

private fun hasTokenMoved(oldText: String, buffer: CharSequence, oldOffset: Int, tokenType: IElementType?): Boolean {

var oldLexer: Lexer = KotlinLexer()
oldLexer.start(oldText)
var newLexer: Lexer = KotlinLexer()
newLexer.start(buffer)
while (true) {
var oldType: IElementType? = oldLexer.tokenType
if (oldType == null) break
var newType: IElementType? = newLexer.tokenType
if (newType == null) return true
if (newType != oldType) {
if (newType == KtTokens.WHITE_SPACE) {
newLexer.advance()
continue
}
else if (oldType == KtTokens.WHITE_SPACE) {
oldLexer.advance()
continue
}
return true
}
if (oldType == tokenType && oldLexer.currentPosition.offset == oldOffset) {
break
}
oldLexer.advance()
newLexer.advance()
}
return false
}
}
}
