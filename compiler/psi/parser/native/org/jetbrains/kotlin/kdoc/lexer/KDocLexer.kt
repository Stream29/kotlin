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

package org.jetbrains.kotlin.kdoc.lexer


import com.intellij.lexer.*
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType



open class KDocLexer : MergingLexerAdapterBase {
private val myMergeFunction: MergeFunction = KDocLexerMergeFunction()

constructor() : super(FlexAdapter(_KDocLexer(null))) {


}

override val mergeFunction: MergeFunction
get() {

return myMergeFunction
}

private open class KDocLexerMergeFunction : MergeFunction {
override fun merge(type: IElementType?, originalLexer: Lexer): IElementType? {

var nextTokenType: IElementType? = originalLexer.tokenType
var nextTokenText: String? = originalLexer.tokenText
if (type == KDocTokens.CODE_BLOCK_TEXT && nextTokenType == KDocTokens.TEXT && isValidCodeFence(nextTokenText!!)) {
originalLexer.advance()
return KDocTokens.TEXT
}
else if (type == KDocTokens.CODE_BLOCK_TEXT || type == KDocTokens.CODE_SPAN_TEXT || type == KDocTokens.TEXT || type == TokenType.WHITE_SPACE) {
while (type == originalLexer.tokenType) {
originalLexer.advance()
}
}
return type
}
}

companion object {
private fun isValidCodeFence(str: String): Boolean {

if (str.length < 3) return false
var firstChar: Char = str.get(0)
if (firstChar != '`' && firstChar != '~') return false
run {
var i: Int = 1
forLoop1@ while (i < str.length) {
forBody2@ do {
if (str.get(i) != firstChar) return false
} while (false)
++ i
}
}
return true
}
}
}
