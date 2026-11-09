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

import com.intellij.psi.tree.ILazyParseableElementType
import com.intellij.psi.tree.TokenSet
import org.jetbrains.kotlin.KotlinElementTypeProvider



interface KDocTokens {
companion object {
val KDOC: ILazyParseableElementType = KotlinElementTypeProvider.instance.kdocType

const val START_Id: Int = 0

const val END_Id: Int = 1

const val LEADING_ASTERISK_Id: Int = 2

const val TEXT_Id: Int = 3

const val CODE_BLOCK_TEXT_Id: Int = 4

const val TAG_NAME_Id: Int = 5

const val MARKDOWN_ESCAPED_CHAR_Id: Int = 6

const val MARKDOWN_INLINE_LINK_Id: Int = 7

const val KDOC_LPAR_Id: Int = 8

const val KDOC_RPAR_Id: Int = 9

const val CODE_SPAN_TEXT_Id: Int = 10

val START: KDocToken = KDocToken("KDOC_START",START_Id)

val END: KDocToken = KDocToken("KDOC_END",END_Id)

val LEADING_ASTERISK: KDocToken = KDocToken("KDOC_LEADING_ASTERISK",LEADING_ASTERISK_Id)

val TEXT: KDocToken = KDocToken("KDOC_TEXT",TEXT_Id)

val CODE_BLOCK_TEXT: KDocToken = KDocToken("KDOC_CODE_BLOCK_TEXT",CODE_BLOCK_TEXT_Id)

val CODE_SPAN_TEXT: KDocToken = KDocToken("KDOC_CODE_SPAN_TEXT",CODE_SPAN_TEXT_Id)

val TAG_NAME: KDocToken = KDocToken("KDOC_TAG_NAME",TAG_NAME_Id)

val MARKDOWN_LINK: ILazyParseableElementType = KotlinElementTypeProvider.instance.kdocMarkdownLinkType

val KDOC_LPAR: KDocToken = KDocToken("KDOC_LPAR",KDOC_LPAR_Id)

val KDOC_RPAR: KDocToken = KDocToken("KDOC_RPAR",KDOC_RPAR_Id)

val MARKDOWN_ESCAPED_CHAR: KDocToken = KDocToken("KDOC_MARKDOWN_ESCAPED_CHAR",MARKDOWN_ESCAPED_CHAR_Id)

val MARKDOWN_INLINE_LINK: KDocToken = KDocToken("KDOC_MARKDOWN_INLINE_LINK",MARKDOWN_INLINE_LINK_Id)

val KDOC_HIGHLIGHT_TOKENS: TokenSet = TokenSet.create(START,END,LEADING_ASTERISK,TEXT,CODE_BLOCK_TEXT,CODE_SPAN_TEXT,MARKDOWN_LINK,MARKDOWN_ESCAPED_CHAR,MARKDOWN_INLINE_LINK,KDOC_LPAR,KDOC_RPAR)

val CONTENT_TOKENS: TokenSet = TokenSet.create(TEXT,CODE_BLOCK_TEXT,CODE_SPAN_TEXT,TAG_NAME,MARKDOWN_LINK,MARKDOWN_ESCAPED_CHAR,MARKDOWN_INLINE_LINK,KDOC_LPAR,KDOC_RPAR)
}
}
