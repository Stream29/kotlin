/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin.psi.stubs.elements

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilderFactory
import com.intellij.psi.PsiElement
import com.intellij.psi.StubBuilder
import com.intellij.psi.stubs.*
import com.intellij.psi.tree.IStubFileElementType
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.parsing.KotlinParser
import org.jetbrains.kotlin.psi.KtImplementationDetail
import org.jetbrains.kotlin.psi.stubs.KotlinStubVersions
import org.jetbrains.kotlin.psi.stubs.impl.KotlinFileStubImpl
import org.jetbrains.kotlin.psi.stubs.impl.KotlinFileStubKindImpl



@OptIn(KtImplementationDetail::class)
object KtFileElementType : IStubFileElementType<KotlinFileStubImpl>(KtFileElementType.NAME, KotlinLanguage.INSTANCE) {
    internal const val NAME = "kotlin.FILE"

    override val builder: StubBuilder get() {
        return KtFileStubBuilder()
    }

    override val stubVersion: Int get() {
        return KotlinStubVersions.SOURCE_STUB_VERSION
    }

    override val externalId: String get() {
        return NAME
    }

    override fun serialize(stub: KotlinFileStubImpl, dataStream: StubOutputStream) {
        KotlinFileStubKindImpl.serialize(stub.kind, dataStream)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): KotlinFileStubImpl {
        val kind = KotlinFileStubKindImpl.deserialize(dataStream)
        return KotlinFileStubImpl(file = null, kind = kind)
    }

    override fun doParseContents(chameleon: ASTNode, psi: PsiElement): ASTNode? {
        val project = psi.project
        val languageForParser = getLanguageForParser(psi)
        val builder = PsiBuilderFactory.getInstance().createBuilder(project, chameleon, null, languageForParser, chameleon.chars)
        return KotlinParser.parse(builder, psi.containingFile!!).firstChildNode
    }

    override fun indexStub(stub: KotlinFileStubImpl, sink: IndexSink) {
        StubIndexService.getInstance().indexFile(stub, sink)
    }

    fun indexStub(stub: PsiFileStub<*>, sink: IndexSink) {
        indexStub(stub as KotlinFileStubImpl, sink)
    }
}
