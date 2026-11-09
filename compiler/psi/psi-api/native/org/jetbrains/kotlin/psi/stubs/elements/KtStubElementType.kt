/*
 * Copyright 2010-2025 JetBrains s.r.o.
 * Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin.psi.stubs.elements

import com.intellij.lang.ASTNode
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.tree.IStubFileElementType
import com.intellij.util.ArrayFactory
import org.jetbrains.kotlin.StaticStubPsiConstructors
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.KtElementImplStub
import kotlin.reflect.KClass

/**
 * Whole-file Native variant of KtStubElementType.java.
 * Direct generated constructor metadata replaces only Java reflection/arrays.
 */
abstract class KtStubElementType<StubT : StubElement<*>, PsiT : KtElementImplStub<*>>(
    debugName: String,
    psiClass: KClass<PsiT>,
    stubClass: KClass<*>,
) : IStubElementType<StubT, PsiT>(debugName, KotlinLanguage.INSTANCE) {
    private val constructors = StaticStubPsiConstructors.get(psiClass, stubClass)

    @Suppress("UNCHECKED_CAST")
    private val cachedEmptyArray: Array<PsiT?> = constructors.arrays(0) as Array<PsiT?>

    val arrayFactory: ArrayFactory<PsiT> = object : ArrayFactory<PsiT> {
        override fun create(count: Int): Array<PsiT?> {
            if (count == 0) return cachedEmptyArray
            @Suppress("UNCHECKED_CAST")
            return constructors.arrays(count) as Array<PsiT?>
        }
    }

    val isExpression: Boolean get() = constructors.isExpression

    open fun createPsiFromAst(node: ASTNode): PsiT = invokeConstructor {
        @Suppress("UNCHECKED_CAST")
        constructors.byNode(node) as PsiT
    }

    override fun createPsi(stub: StubT): PsiT = invokeConstructor {
        @Suppress("UNCHECKED_CAST")
        constructors.byStub(stub) as PsiT
    }

    override val externalId: String get() = "kotlin.$debugName"

    override fun shouldCreateStub(node: ASTNode): Boolean = createStubDependingOnParent(node)

    override fun indexStub(stub: StubT, sink: IndexSink) {
        // The original default intentionally does not force inheritors to index.
    }

    fun getArrayFactory(): ArrayFactory<PsiT> = arrayFactory
    fun isExpression(): Boolean = isExpression

    companion object {
        private fun createStubDependingOnParent(node: ASTNode): Boolean {
            val parent = node.treeParent!!
            val parentType = parent.elementType
            if (parentType is IStubElementType<*, *>) return parentType.shouldCreateStub(parent)
            if (parentType is IStubFileElementType<*>) return true
            return false
        }

        private inline fun <T> invokeConstructor(block: () -> T): T =
            try {
                block()
            } catch (target: Throwable) {
                // ReflectionUtil wraps InvocationTargetException, not a null
                // result or a swallowed target failure. Keep the cause chain.
                throw RuntimeException(PsiConstructorInvocationFailure(target))
            }
    }
}

private class PsiConstructorInvocationFailure(target: Throwable) : Exception(target)
