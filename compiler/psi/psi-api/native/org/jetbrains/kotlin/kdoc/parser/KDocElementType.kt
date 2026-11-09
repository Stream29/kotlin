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
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.StaticPsiConstructors
import org.jetbrains.kotlin.kdoc.psi.impl.KDocName
import org.jetbrains.kotlin.kdoc.psi.impl.KDocSection
import org.jetbrains.kotlin.kdoc.psi.impl.KDocTag
import org.jetbrains.kotlin.psi.KtElement
import kotlin.reflect.KClass

open class KDocElementType(
    debugName: String,
    psiClass: KClass<out PsiElement>,
) : IElementType(debugName, KotlinLanguage.INSTANCE) {
    // Exact constructor signatures selected by the original KDocElementTypes.
    private val psiFactory: (ASTNode) -> PsiElement = when (psiClass) {
        KDocSection::class -> ::KDocSection
        KDocTag::class -> ::KDocTag
        KDocName::class -> ::KDocName
        else -> {
            @Suppress("UNCHECKED_CAST")
            StaticPsiConstructors.factories[psiClass as KClass<out KtElement>]
                ?: throw RuntimeException("Must have a constructor with ASTNode")
        }
    }

    open fun createPsi(node: ASTNode): PsiElement {
        assert(node.elementType == this)
        try {
            return psiFactory(node)
        } catch (target: Throwable) {
            throw RuntimeException("Error creating psi element for node", KDocConstructorInvocationFailure(target))
        }
    }
}

private class KDocConstructorInvocationFailure(target: Throwable) : Exception(target)
