/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.KtStubBasedElementTypes
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub
import org.jetbrains.kotlin.psi.stubs.elements.KtTokenSets



/**
 * Represents the body of a class or object declaration enclosed in curly braces.
 *
 * ### Example:
 *
 * ```kotlin
 *    class Foo {
 *        val x = 1
 *        fun bar() = 2
 *    }
 * // ^_________^
 * // The block from '{' to '}'
 * ```
 */
class KtClassBody : KtElementImplStub<KotlinPlaceHolderStub<KtClassBody>>, KtDeclarationContainer {
    private var lBraceTokenSet = TokenSet.create(KtTokens.LBRACE)
    private var rBraceTokenSet = TokenSet.create(KtTokens.RBRACE)

    protected override fun copyKotlinPsiStateTo(clone: com.intellij.psi.impl.PsiElementBase) {
        super.copyKotlinPsiStateTo(clone)
        val target = clone as KtClassBody
        target.lBraceTokenSet = lBraceTokenSet
        target.rBraceTokenSet = rBraceTokenSet
    }

    constructor(node: ASTNode) : super(node)

    constructor(stub: KotlinPlaceHolderStub<KtClassBody>) : super(stub, KtStubBasedElementTypes.CLASS_BODY)

    @Suppress("UNCHECKED_CAST")
    override fun getDeclarations(): List<KtDeclaration> =
        (stub?.getChildrenByType(KtFile.FILE_DECLARATION_TYPES, KtDeclaration.ARRAY_FACTORY)?.toList() as List<KtDeclaration>?)
        ?: PsiTreeUtil.getChildrenOfTypeAsList(this, KtDeclaration::class)

    /**
     * The list of all declarations and companion blocks.
     */
    @KtExperimentalApi
    @Suppress("UNCHECKED_CAST")
    val declarationsAndCompanionBlocks: List<PsiElement>
        get() = (stub?.getChildrenByType(KtTokenSets.DECLARATION_AND_COMPANION_BLOCK_TYPES, PsiElement.ARRAY_FACTORY)?.toList() as List<PsiElement>?)
            ?: PsiTreeUtil.getChildrenOfAnyType(this, KtDeclaration::class, KtCompanionBlock::class)

    override fun <R, D> accept(visitor: KtVisitor<R, D>, data: D) = visitor.visitClassBody(this, data)

    val anonymousInitializers: List<KtAnonymousInitializer>
        get() = getStubOrPsiChildrenAsList(KtStubBasedElementTypes.CLASS_INITIALIZER)

    internal val secondaryConstructors: List<KtSecondaryConstructor>
        get() = getStubOrPsiChildrenAsList(KtStubBasedElementTypes.SECONDARY_CONSTRUCTOR)

    val properties: List<KtProperty>
        get() = getStubOrPsiChildrenAsList(KtStubBasedElementTypes.PROPERTY)

    val functions: List<KtNamedFunction>
        get() = getStubOrPsiChildrenAsList(KtStubBasedElementTypes.FUNCTION)

    val enumEntries: List<KtEnumEntry>
        get() = getStubOrPsiChildrenAsList(KtStubBasedElementTypes.ENUM_ENTRY)

    val allCompanionObjects: List<KtObjectDeclaration>
        get() = getStubOrPsiChildrenAsList(KtStubBasedElementTypes.OBJECT_DECLARATION).filter { it.isCompanion() }

    /**
     * The list of all companion blocks.
     */
    @KtExperimentalApi
    val companionBlocks: List<KtCompanionBlock>
        get() = getStubOrPsiChildrenAsList(KtStubBasedElementTypes.COMPANION_BLOCK)

    val rBrace: PsiElement?
        get() = node.getChildren(rBraceTokenSet).singleOrNull()?.psi

    val lBrace: PsiElement?
        get() = node.getChildren(lBraceTokenSet).singleOrNull()?.psi

    /**
     * @return annotations that do not belong to any declaration due to incomplete code or syntax errors
     */
    val danglingAnnotations: List<KtAnnotationEntry>
        get() = danglingModifierLists.flatMap { it.annotationEntries }

    /**
     * @return modifier lists that do not belong to any declaration due to incomplete code or syntax errors
     */
    val danglingModifierLists: List<KtModifierList>
        get() = getStubOrPsiChildrenAsList(KtStubBasedElementTypes.MODIFIER_LIST)
}
