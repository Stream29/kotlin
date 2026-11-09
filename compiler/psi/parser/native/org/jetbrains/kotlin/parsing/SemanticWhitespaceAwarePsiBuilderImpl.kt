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


import com.intellij.lang.PsiBuilder
import com.intellij.lang.impl.PsiBuilderAdapter
import com.intellij.lang.impl.PsiBuilderImpl
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import com.intellij.util.containers.Stack
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.lexer.KtTokens.Companion.BLOCK_COMMENT
import org.jetbrains.kotlin.lexer.KtTokens.Companion.COLON
import org.jetbrains.kotlin.lexer.KtTokens.Companion.DOC_COMMENT
import org.jetbrains.kotlin.lexer.KtTokens.Companion.DOT
import org.jetbrains.kotlin.lexer.KtTokens.Companion.ELVIS
import org.jetbrains.kotlin.lexer.KtTokens.Companion.EOL_COMMENT
import org.jetbrains.kotlin.lexer.KtTokens.Companion.EXCL
import org.jetbrains.kotlin.lexer.KtTokens.Companion.EXCLEXCL
import org.jetbrains.kotlin.lexer.KtTokens.Companion.QUEST
import org.jetbrains.kotlin.lexer.KtTokens.Companion.SAFE_ACCESS
import org.jetbrains.kotlin.lexer.KtTokens.Companion.SHEBANG_COMMENT
import org.jetbrains.kotlin.lexer.KtTokens.Companion.WHITE_SPACE



open class SemanticWhitespaceAwarePsiBuilderImpl : PsiBuilderAdapter, SemanticWhitespaceAwarePsiBuilder {
private val complexTokens: TokenSet = TokenSet.create(SAFE_ACCESS,ELVIS,EXCLEXCL)

private val joinComplexTokens: Stack<Boolean> = Stack()

private val newlinesEnabled: Stack<Boolean> = Stack()

private val delegateImpl: PsiBuilderImpl?

constructor(delegate: PsiBuilder) : super(delegate) {

newlinesEnabled.push(true)
joinComplexTokens.push(true)
delegateImpl = findPsiBuilderImpl(delegate)
}

override fun isWhitespaceOrComment(elementType: IElementType): Boolean {

assert(delegateImpl != null) { "PsiBuilderImpl not found" }
return delegateImpl!!.whitespaceOrComment(elementType)
}

override fun newlineBeforeCurrentToken(): Boolean {

if (! newlinesEnabled.peek()) return false
if (eof()) return true
run {
var i: Int = 1
forLoop1@ while (i <= currentOffset) {
forBody2@ do {
var previousToken: IElementType? = rawLookup(- i)
if (previousToken == KtTokens.BLOCK_COMMENT || previousToken == KtTokens.DOC_COMMENT || previousToken == KtTokens.EOL_COMMENT || previousToken == SHEBANG_COMMENT) {
break@forBody2
}
if (previousToken != TokenType.WHITE_SPACE) {
break@forLoop1
}
var previousTokenStart: Int = rawTokenTypeStart(- i)
var previousTokenEnd: Int = rawTokenTypeStart(- i + 1)
assert(previousTokenStart >= 0)
assert(previousTokenEnd < originalText.length)
run {
var j: Int = previousTokenStart
forLoop3@ while (j < previousTokenEnd) {
forBody4@ do {
if (originalText.get(j) == '\n') {
return true
}
} while (false)
j ++
}
}
} while (false)
i ++
}
}
return false
}

override fun disableNewlines(): Unit {

newlinesEnabled.push(false)
}

override fun enableNewlines(): Unit {

newlinesEnabled.push(true)
}

override fun restoreNewlinesState(): Unit {

assert(newlinesEnabled.size > 1)
newlinesEnabled.pop()
}

private fun joinComplexTokens(): Boolean {

return joinComplexTokens.peek()
}

override fun restoreJoiningComplexTokensState(): Unit {

joinComplexTokens.pop()
}

override fun enableJoiningComplexTokens(): Unit {

joinComplexTokens.push(true)
}

override fun disableJoiningComplexTokens(): Unit {

joinComplexTokens.push(false)
}

override val tokenType: IElementType?
get() {

if (! joinComplexTokens()) return super.tokenType
return getJoinedTokenType(super.tokenType,1)
}

private fun getJoinedTokenType(rawTokenType: IElementType?, rawLookupSteps: Int): IElementType? {

if (rawTokenType == QUEST) {
var nextRawToken: IElementType? = rawLookup(rawLookupSteps)
if (nextRawToken == DOT) return SAFE_ACCESS
if (nextRawToken == COLON) return ELVIS
}
else if (rawTokenType == EXCL) {
var nextRawToken: IElementType? = rawLookup(rawLookupSteps)
if (nextRawToken == EXCL) return EXCLEXCL
}
return rawTokenType
}

override fun advanceLexer(): Unit {

if (! joinComplexTokens()) {
super.advanceLexer()
return 
}
var tokenType: IElementType? = tokenType
if (complexTokens.contains(tokenType)) {
var mark: PsiBuilder.Marker = mark()
super.advanceLexer()
super.advanceLexer()
mark.collapse(tokenType!!)
}
else {
super.advanceLexer()
}
}

override val tokenText: String?
get() {

if (! joinComplexTokens()) return super.tokenText
var tokenType: IElementType? = tokenType
if (complexTokens.contains(tokenType)) {
if (tokenType == ELVIS) return "?:"
if (tokenType == SAFE_ACCESS) return "?."
}
return super.tokenText
}

override fun lookAhead(steps: Int): IElementType? {

if (! joinComplexTokens()) return super.lookAhead(steps)
if (complexTokens.contains(tokenType)) {
return super.lookAhead(steps + 1)
}
return getJoinedTokenType(super.lookAhead(steps),2)
}

companion object {
private fun findPsiBuilderImpl(builder: PsiBuilder): PsiBuilderImpl? {
var builder = builder
while (true) {
if (builder is PsiBuilderImpl) {
return (builder as PsiBuilderImpl)
}
if (!(builder is PsiBuilderAdapter)) {
return null
}
builder = ((builder as PsiBuilderAdapter)).delegate
}
}
}
}
