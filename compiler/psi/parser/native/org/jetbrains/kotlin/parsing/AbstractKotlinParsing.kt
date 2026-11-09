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

import org.jetbrains.kotlin.lexer.isSoft






import com.intellij.lang.PsiBuilder
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import com.intellij.util.containers.Stack
import org.jetbrains.kotlin.lexer.KtKeywordToken
import org.jetbrains.kotlin.lexer.KtToken
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.utils.strings.substringWithContext
import org.jetbrains.kotlin.lexer.KtTokens.Companion.EOL_OR_SEMICOLON
import org.jetbrains.kotlin.lexer.KtTokens.Companion.GT_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.IDENTIFIER
import org.jetbrains.kotlin.lexer.KtTokens.Companion.INVALID_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.KEYWORDS
import org.jetbrains.kotlin.lexer.KtTokens.Companion.LBRACE
import org.jetbrains.kotlin.lexer.KtTokens.Companion.LBRACE_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.LBRACKET
import org.jetbrains.kotlin.lexer.KtTokens.Companion.LBRACKET_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.LPAR
import org.jetbrains.kotlin.lexer.KtTokens.Companion.LPAR_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.LT
import org.jetbrains.kotlin.lexer.KtTokens.Companion.LT_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.RBRACE
import org.jetbrains.kotlin.lexer.KtTokens.Companion.RBRACE_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.RBRACKET_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.RPAR
import org.jetbrains.kotlin.lexer.KtTokens.Companion.RPAR_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.SEMICOLON
import org.jetbrains.kotlin.lexer.KtTokens.Companion.SOFT_KEYWORDS
import org.jetbrains.kotlin.lexer.KtTokens.Companion.WHITE_SPACE_OR_COMMENT_BIT_SET



abstract class AbstractKotlinParsing {
protected val myBuilder: SemanticWhitespaceAwarePsiBuilder

protected val isLazy: Boolean

constructor(builder: SemanticWhitespaceAwarePsiBuilder) : this(builder,true) {


}

constructor(builder: SemanticWhitespaceAwarePsiBuilder, isLazy: Boolean) {

this.myBuilder = builder
this.isLazy = isLazy
}

protected open fun getLastToken(): IElementType? {

var i: Int = 1
var currentOffset: Int = myBuilder.currentOffset
while (i <= currentOffset && WHITE_SPACE_OR_COMMENT_BIT_SET.contains(myBuilder.rawLookup(- i))) {
i ++
}
return myBuilder.rawLookup(- i)
}

protected open fun expect(expectation: KtToken, message: String): Boolean {

return expect(expectation,message,null)
}

protected open fun mark(): PsiBuilder.Marker {

return myBuilder.mark()
}

protected open fun error(message: String): Unit {

myBuilder.error(message)
}

protected open fun expect(expectation: KtToken, message: String, recoverySet: TokenSet?): Boolean {

if (expect(expectation)) {
return true
}
errorWithRecovery(message,recoverySet)
return false
}

protected open fun expect(expectation: KtToken): Boolean {

if (at(expectation)) {
advance()
return true
}
if (expectation == KtTokens.IDENTIFIER && "`".equals(myBuilder.tokenText)) {
advance()
}
return false
}

protected open fun expectNoAdvance(expectation: KtToken, message: String): Unit {

if (at(expectation)) {
advance()
return 
}
error(message)
}

protected open fun errorWithRecovery(message: String, recoverySet: TokenSet?): Unit {

var tt: IElementType? = tt()
if (recoverySet == null || recoverySet.contains(tt) || tt == LBRACE || tt == RBRACE || (recoverySet.contains(EOL_OR_SEMICOLON) &&(eof() || tt == SEMICOLON || myBuilder.newlineBeforeCurrentToken()))) {
error(message)
}
else {
errorAndAdvance(message)
}
}

protected open fun errorAndAdvance(message: String): Unit {

errorAndAdvance(message,1)
}

protected open fun errorAndAdvance(message: String, advanceTokenCount: Int): Unit {

var err: PsiBuilder.Marker = mark()
advance(advanceTokenCount)
err.error(message)
}

protected open fun eof(): Boolean {

return myBuilder.eof()
}

protected open fun advance(): Unit {

myBuilder.advanceLexer()
}

protected open fun advance(advanceTokenCount: Int): Unit {

run {
var i: Int = 0
forLoop1@ while (i < advanceTokenCount) {
forBody2@ do {
advance()
} while (false)
i ++
}
}
}

protected open fun advanceAt(current: IElementType): Unit {

assert(_at(current))
myBuilder.advanceLexer()
}

protected open fun getTokenId(): Int {

var elementType: IElementType? = tt()
return (if ((elementType is KtToken)) ((elementType as KtToken)).tokenId else INVALID_Id)
}

protected open fun tt(): IElementType? {

return myBuilder.tokenType
}

protected open fun _at(expectation: IElementType): Boolean {

var token: IElementType? = tt()
return tokenMatches(token,expectation)
}

private fun tokenMatches(token: IElementType?, expectation: IElementType): Boolean {

if (token == expectation) return true
if (expectation == EOL_OR_SEMICOLON) {
if (eof()) return true
if (token == SEMICOLON) return true
if (myBuilder.newlineBeforeCurrentToken()) return true
}
return false
}

protected open fun at(expectation: IElementType): Boolean {

if (_at(expectation)) return true
var token: IElementType? = tt()
if (token == IDENTIFIER && expectation is KtKeywordToken) {
var expectedKeyword: KtKeywordToken = (expectation as KtKeywordToken)
if (expectedKeyword.isSoft() && expectedKeyword.value.equals(myBuilder.tokenText)) {
myBuilder.remapCurrentToken(expectation)
return true
}
}
if (expectation == IDENTIFIER && token is KtKeywordToken) {
var keywordToken: KtKeywordToken = (token as KtKeywordToken)
if (keywordToken.isSoft()) {
myBuilder.remapCurrentToken(IDENTIFIER)
return true
}
}
return false
}

protected open fun _atSet(set: TokenSet): Boolean {

var token: IElementType? = tt()
if (set.contains(token)) return true
if (set.contains(EOL_OR_SEMICOLON)) {
if (eof()) return true
if (token == SEMICOLON) return true
if (myBuilder.newlineBeforeCurrentToken()) return true
}
return false
}

protected open fun atSet(set: TokenSet): Boolean {

if (_atSet(set)) return true
var token: IElementType? = tt()
if (token == IDENTIFIER) {
var keywordToken: KtKeywordToken? = SOFT_KEYWORD_TEXTS.get(myBuilder.tokenText)
if (keywordToken != null && set.contains(keywordToken)) {
myBuilder.remapCurrentToken(keywordToken)
return true
}
}
else {
if (set.contains(IDENTIFIER) && token is KtKeywordToken) {
if (((token as KtKeywordToken)).isSoft()) {
myBuilder.remapCurrentToken(IDENTIFIER)
return true
}
}
}
return false
}

protected open fun lookahead(k: Int): IElementType? {

return myBuilder.lookAhead(k)
}

protected open fun consumeIf(token: KtToken): Boolean {

if (at(token)) {
advance()
return true
}
return false
}

protected open fun skipUntil(tokenSet: TokenSet): Unit {

var stopAtEolOrSemi: Boolean = tokenSet.contains(EOL_OR_SEMICOLON)
while (! eof() && ! tokenSet.contains(tt()) && !(stopAtEolOrSemi && at(EOL_OR_SEMICOLON))) {
advance()
}
}

protected open fun errorUntil(message: String, tokenSet: TokenSet): Unit {

assert(tokenSet.contains(LBRACE)) { "Cannot include LBRACE into error element!" }
assert(tokenSet.contains(RBRACE)) { "Cannot include RBRACE into error element!" }
var error: PsiBuilder.Marker = mark()
skipUntil(tokenSet)
error.error(message)
}

protected inner open class OptionalMarker {
private val marker: PsiBuilder.Marker?

private val offset: Int

constructor(actuallyMark: Boolean) {

marker = (if (actuallyMark) mark() else null)
offset = myBuilder.currentOffset
}

open fun done(elementType: IElementType): Unit {

if (marker == null) return 
marker.done(elementType)
}

open fun error(message: String): Unit {

if (marker == null) return 
if (offset == myBuilder.currentOffset) {
marker.drop()
}
else {
marker.error(message)
}
}

open fun drop(): Unit {

if (marker == null) return 
marker.drop()
}
}

protected open fun matchTokenStreamPredicate(pattern: TokenStreamPattern): Int {

var currentPosition: PsiBuilder.Marker = mark()
var opens: Stack<IElementType> = Stack()
var openAngleBrackets: Int = 0
var openBraces: Int = 0
var openParentheses: Int = 0
var openBrackets: Int = 0
while (! eof()) {
if (pattern.processToken(myBuilder.currentOffset,pattern.isTopLevel(openAngleBrackets,openBrackets,openBraces,openParentheses))) {
break
}
switch3@ do {
when (getTokenId()) {
LPAR_Id -> {
openParentheses ++
opens.push(LPAR)
break@switch3
}
LT_Id -> {
openAngleBrackets ++
opens.push(LT)
break@switch3
}
LBRACE_Id -> {
openBraces ++
opens.push(LBRACE)
break@switch3
}
LBRACKET_Id -> {
openBrackets ++
opens.push(LBRACKET)
break@switch3
}
RPAR_Id -> {
openParentheses --
if (opens.isEmpty() || opens.pop() != LPAR) {
if (pattern.handleUnmatchedClosing(RPAR)) {
break@switch3
}
}
break@switch3
}
GT_Id -> {
openAngleBrackets --
break@switch3
}
RBRACE_Id -> {
openBraces --
break@switch3
}
RBRACKET_Id -> {
openBrackets --
break@switch3
}
}
} while (false)
advance()
}
currentPosition.rollbackTo()
return pattern.result()
}

protected open fun eol(): Boolean {

return myBuilder.newlineBeforeCurrentToken() || eof()
}

internal fun createForSibling(builder: SemanticWhitespaceAwarePsiBuilder): KotlinParsing = create(builder)

protected abstract fun create(builder: SemanticWhitespaceAwarePsiBuilder): KotlinParsing

protected open fun createTruncatedBuilder(eofPosition: Int): KotlinParsing {

return create(TruncatedSemanticWhitespaceAwarePsiBuilder(myBuilder,eofPosition))
}

protected inner open class At : AbstractTokenStreamPredicate {
private val lookFor: IElementType

private val topLevelOnly: Boolean

constructor(lookFor: IElementType, topLevelOnly: Boolean) : super() {

this.lookFor = lookFor
this.topLevelOnly = topLevelOnly
}

constructor(lookFor: IElementType) : this(lookFor,true) {


}

override fun matching(topLevel: Boolean): Boolean {

return (topLevel || ! topLevelOnly) && at(lookFor)
}
}

protected inner open class AtSet : AbstractTokenStreamPredicate {
private val lookFor: TokenSet

private val topLevelOnly: TokenSet

constructor(lookFor: TokenSet, topLevelOnly: TokenSet) : super() {

this.lookFor = lookFor
this.topLevelOnly = topLevelOnly
}

constructor(lookFor: TokenSet) : this(lookFor,lookFor) {


}

override fun matching(topLevel: Boolean): Boolean {

return (topLevel || ! atSet(topLevelOnly)) && atSet(lookFor)
}
}

open fun currentContext(): String {

return myBuilder.originalText.substringWithContext(myBuilder.currentOffset,myBuilder.currentOffset,20)
}

companion object {
private val SOFT_KEYWORD_TEXTS: MutableMap<String,KtKeywordToken> = HashMap()

init {
for (type in KtTokens.SOFT_KEYWORDS.types) {
var keywordToken: KtKeywordToken = (type as KtKeywordToken)
assert(keywordToken.isSoft())
SOFT_KEYWORD_TEXTS.put(keywordToken.value,keywordToken)
}
}

init {
for (token in KtTokens.KEYWORDS.types) {
assert(token is KtKeywordToken) { "Must be KtKeywordToken: " + token }
assert(!((token as KtKeywordToken)).isSoft()) { "Must not be soft: " + token }
}
}

protected fun errorIf(marker: PsiBuilder.Marker, condition: Boolean, message: String): Unit {

if (condition) {
marker.error(message)
}
else {
marker.drop()
}
}

protected fun closeDeclarationWithCommentBinders(marker: PsiBuilder.Marker, elementType: IElementType, precedingNonDocComments: Boolean): Unit {

marker.done(elementType)
marker.setCustomEdgeTokenBinders((if(precedingNonDocComments) PrecedingCommentsBinder else PrecedingDocCommentsBinder),TrailingCommentsBinder)
}
}
}
