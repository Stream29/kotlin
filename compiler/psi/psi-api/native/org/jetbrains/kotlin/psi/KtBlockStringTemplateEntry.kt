/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.KtStubBasedElementTypes
import org.jetbrains.kotlin.psi.stubs.KotlinBlockStringTemplateEntryStub
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderWithTextStub



open class KtBlockStringTemplateEntry : KtStringTemplateEntryWithExpression {
constructor(node: ASTNode) : super(node) {


}

constructor(stub: KotlinBlockStringTemplateEntryStub) : super(stub,KtStubBasedElementTypes.LONG_STRING_TEMPLATE_ENTRY) {


}

override fun <R,D> accept(visitor: KtVisitor<R,D>, data: D): R {

return visitor.visitBlockStringTemplateEntry(this,data)
}

override fun getExpressions(): List<KtExpression> {

var stub: KotlinPlaceHolderWithTextStub<out KtStringTemplateEntry>? = stub
if (stub != null && !((stub as KotlinBlockStringTemplateEntryStub)).hasMultipleExpressions) {
return super.getExpressions()
}
return PsiTreeUtil.getChildrenOfTypeAsList(this,KtExpression ::class)
}
}
