/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.text.StringUtil
import org.jetbrains.kotlin.KtStubBasedElementTypes
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderWithTextStub



open class KtEscapeStringTemplateEntry : KtStringTemplateEntry {
constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinPlaceHolderWithTextStub<KtEscapeStringTemplateEntry>) : super(stub,KtStubBasedElementTypes.ESCAPE_STRING_TEMPLATE_ENTRY) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitEscapeStringTemplateEntry(this,data)
}

open fun getUnescapedValue(): String {

return StringUtil.unescapeStringCharacters(text)
}
}
