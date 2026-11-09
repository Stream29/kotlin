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







open class KtKeywordToken : KtSingleValueToken {
private val myIsSoft: Boolean

protected constructor(debugName: String, value: String, isSoft: Boolean) : super(debugName,value) {

myIsSoft = isSoft
}

protected constructor(debugName: String, value: String, isSoft: Boolean, tokenId: Int) : super(debugName,value,tokenId) {

myIsSoft = isSoft
}

open fun isSoft(): Boolean {

return myIsSoft
}

companion object {
fun keyword(value: String): KtKeywordToken {

return keyword(value,value)
}

fun keyword(value: String, tokenId: Int): KtKeywordToken {

return keyword(value,value,tokenId)
}

fun keyword(debugName: String, value: String): KtKeywordToken {

return KtKeywordToken(debugName,value,false)
}

fun keyword(debugName: String, value: String, tokenId: Int): KtKeywordToken {

return KtKeywordToken(debugName,value,false,tokenId)
}

fun softKeyword(value: String): KtKeywordToken {

return KtKeywordToken(value,value,true)
}

fun softKeyword(value: String, tokenId: Int): KtKeywordToken {

return KtKeywordToken(value,value,true,tokenId)
}
}
}
