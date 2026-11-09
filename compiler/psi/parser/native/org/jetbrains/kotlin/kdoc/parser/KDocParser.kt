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

package org.jetbrains.kotlin.kdoc.parser


import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.kdoc.lexer.KDocTokens



open class KDocParser : PsiParser {
override fun parse(root: IElementType, builder: PsiBuilder): ASTNode {

var rootMarker: PsiBuilder.Marker = builder.mark()
if (builder.tokenType == KDocTokens.START) {
builder.advanceLexer()
}
var currentSectionMarker: PsiBuilder.Marker? = builder.mark()
while (! builder.eof()) {
if (builder.tokenType == KDocTokens.TAG_NAME) {
currentSectionMarker = parseTag(builder,currentSectionMarker)
}
else if (builder.tokenType == KDocTokens.END) {
if (currentSectionMarker != null) {
currentSectionMarker!!.done(KDocElementTypes.KDOC_SECTION)
currentSectionMarker = null
}
builder.advanceLexer()
}
else {
builder.advanceLexer()
}
}
if (currentSectionMarker != null) {
currentSectionMarker!!.done(KDocElementTypes.KDOC_SECTION)
}
rootMarker.done(root)
return builder.treeBuilt
}

companion object {
private fun parseTag(builder: PsiBuilder, currentSectionMarker: PsiBuilder.Marker?): PsiBuilder.Marker? {
var currentSectionMarker = currentSectionMarker
var tagName: String = builder.tokenText!!
var knownTag: KDocKnownTag? = KDocKnownTag.Companion.findByTagName(tagName)
if (knownTag != null && knownTag.isSectionStart) {
currentSectionMarker!!.done(KDocElementTypes.KDOC_SECTION)
currentSectionMarker = builder.mark()
}
var tagStart: PsiBuilder.Marker = builder.mark()
builder.advanceLexer()
while (! builder.eof() && ! isAtEndOfTag(builder)) {
builder.advanceLexer()
}
tagStart.done(KDocElementTypes.KDOC_TAG)
return currentSectionMarker
}

private fun isAtEndOfTag(builder: PsiBuilder): Boolean {

if (builder.tokenType == KDocTokens.END) {
return true
}
if (builder.tokenType == KDocTokens.LEADING_ASTERISK) {
var lookAheadCount: Int = 1
if (builder.lookAhead(1) == KDocTokens.TEXT) {
lookAheadCount ++
}
if (builder.lookAhead(lookAheadCount) == KDocTokens.TAG_NAME) {
return true
}
}
return false
}
}
}
