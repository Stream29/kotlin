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







open class SemanticWhitespaceAwarePsiBuilderForByClause : SemanticWhitespaceAwarePsiBuilderAdapter {
private var stackSize: Int = 0

constructor(builder: SemanticWhitespaceAwarePsiBuilder) : super(builder) {


}

override fun disableNewlines(): Unit {

super.disableNewlines()
stackSize ++
}

override fun enableNewlines(): Unit {

super.enableNewlines()
stackSize ++
}

override fun restoreNewlinesState(): Unit {

super.restoreNewlinesState()
stackSize --
}

open fun getStackSize(): Int {

return stackSize
}
}
