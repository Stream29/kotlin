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

package org.jetbrains.kotlin.lexer







class KtModifierKeywordToken : KtKeywordToken {
private constructor(debugName: String, value: String, isSoft: Boolean) : super(debugName,value,isSoft) {


}

private constructor(debugName: String, value: String, isSoft: Boolean, tokenId: Int) : super(debugName,value,isSoft,tokenId) {


}

companion object {
fun keywordModifier(value: String): KtModifierKeywordToken {

return KtModifierKeywordToken(value,value,false)
}

fun keywordModifier(value: String, tokenId: Int): KtModifierKeywordToken {

return KtModifierKeywordToken(value,value,false,tokenId)
}

fun softKeywordModifier(value: String): KtModifierKeywordToken {

return KtModifierKeywordToken(value,value,true)
}

fun softKeywordModifier(value: String, tokenId: Int): KtModifierKeywordToken {

return KtModifierKeywordToken(value,value,true,tokenId)
}
}
}
