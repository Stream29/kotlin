/*
 * Copyright 2010-2026 JetBrains s.r.o.
 * Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin

import com.intellij.lang.ASTNode
import com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtElementImpl
import kotlin.reflect.KClass



open class KtNodeType(
    debugName: String,
    private val myPsiFactory: ((ASTNode) -> KtElement)?,
) : IElementType(debugName, KotlinLanguage.INSTANCE) {
    @Deprecated("Use the factory constructor")
    constructor(debugName: String, psiClass: KClass<out KtElement>?) :
        this(debugName, createFactory(psiClass))

    open fun createPsi(node: ASTNode): KtElement {
        assert(node.elementType == this)
        val factory = myPsiFactory ?: return KtElementImpl(node)
        return factory(node)
    }

    companion object {
        private fun createFactory(psiClass: KClass<out KtElement>?): ((ASTNode) -> KtElement)? {
            if (psiClass == null) return null
            val constructor = StaticPsiConstructors.factories[psiClass]
                ?: throw RuntimeException("Must have a constructor with ASTNode", MissingPsiConstructor("${psiClass.simpleName}(ASTNode)"))
            return { node ->
                try {
                    constructor(node)
                } catch (target: Throwable) {
                    throw RuntimeException("Error creating psi element for node", AstConstructorInvocationFailure(target))
                }
            }
        }
    }

    open class KtLeftBoundNodeType : KtNodeType {
        @Deprecated("Use the factory constructor")
        constructor(debugName: String, psiClass: KClass<out KtElement>?) : super(debugName, psiClass)
        constructor(debugName: String, psiFactory: ((ASTNode) -> KtElement)?) : super(debugName, psiFactory)
        override fun isLeftBound(): Boolean = true
    }
}

private class AstConstructorInvocationFailure(target: Throwable) : Exception(target)
