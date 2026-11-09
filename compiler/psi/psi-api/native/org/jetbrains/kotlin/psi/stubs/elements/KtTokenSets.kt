/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.elements

import com.intellij.psi.tree.TokenSet
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.KtNodeTypes.Companion.BOOLEAN_CONSTANT
import org.jetbrains.kotlin.KtNodeTypes.Companion.CALL_EXPRESSION
import org.jetbrains.kotlin.KtNodeTypes.Companion.CHARACTER_CONSTANT
import org.jetbrains.kotlin.KtNodeTypes.Companion.CLASS
import org.jetbrains.kotlin.KtNodeTypes.Companion.CLASS_INITIALIZER
import org.jetbrains.kotlin.KtNodeTypes.Companion.CLASS_LITERAL_EXPRESSION
import org.jetbrains.kotlin.KtNodeTypes.Companion.COLLECTION_LITERAL_EXPRESSION
import org.jetbrains.kotlin.KtNodeTypes.Companion.COMPANION_BLOCK
import org.jetbrains.kotlin.KtNodeTypes.Companion.DELEGATED_SUPER_TYPE_ENTRY
import org.jetbrains.kotlin.KtNodeTypes.Companion.DESTRUCTURING_DECLARATION
import org.jetbrains.kotlin.KtNodeTypes.Companion.DOT_QUALIFIED_EXPRESSION
import org.jetbrains.kotlin.KtNodeTypes.Companion.DYNAMIC_TYPE
import org.jetbrains.kotlin.KtNodeTypes.Companion.ENUM_ENTRY
import org.jetbrains.kotlin.KtNodeTypes.Companion.FLOAT_CONSTANT
import org.jetbrains.kotlin.KtNodeTypes.Companion.FUN
import org.jetbrains.kotlin.KtNodeTypes.Companion.FUNCTION_TYPE
import org.jetbrains.kotlin.KtNodeTypes.Companion.INTEGER_CONSTANT
import org.jetbrains.kotlin.KtNodeTypes.Companion.INTERSECTION_TYPE
import org.jetbrains.kotlin.KtNodeTypes.Companion.NULL
import org.jetbrains.kotlin.KtNodeTypes.Companion.NULLABLE_TYPE
import org.jetbrains.kotlin.KtNodeTypes.Companion.OBJECT_DECLARATION
import org.jetbrains.kotlin.KtNodeTypes.Companion.PROPERTY
import org.jetbrains.kotlin.KtNodeTypes.Companion.REFERENCE_EXPRESSION
import org.jetbrains.kotlin.KtNodeTypes.Companion.SECONDARY_CONSTRUCTOR
import org.jetbrains.kotlin.KtNodeTypes.Companion.STRING_TEMPLATE
import org.jetbrains.kotlin.KtNodeTypes.Companion.SUPER_TYPE_CALL_ENTRY
import org.jetbrains.kotlin.KtNodeTypes.Companion.SUPER_TYPE_ENTRY
import org.jetbrains.kotlin.KtNodeTypes.Companion.TYPEALIAS
import org.jetbrains.kotlin.KtNodeTypes.Companion.USER_TYPE



interface KtTokenSets {
companion object {
val DECLARATION_TYPES: TokenSet = TokenSet.create(CLASS,OBJECT_DECLARATION,FUN,PROPERTY,DESTRUCTURING_DECLARATION,TYPEALIAS,CLASS_INITIALIZER,SECONDARY_CONSTRUCTOR,ENUM_ENTRY)

val DECLARATION_AND_COMPANION_BLOCK_TYPES: TokenSet = TokenSet.orSet(DECLARATION_TYPES,TokenSet.create(COMPANION_BLOCK))

val SUPER_TYPE_LIST_ENTRIES: TokenSet = TokenSet.create(DELEGATED_SUPER_TYPE_ENTRY,SUPER_TYPE_CALL_ENTRY,SUPER_TYPE_ENTRY)

val TYPE_ELEMENT_TYPES: TokenSet = TokenSet.create(USER_TYPE,NULLABLE_TYPE,FUNCTION_TYPE,DYNAMIC_TYPE,INTERSECTION_TYPE)

val INSIDE_DIRECTIVE_EXPRESSIONS: TokenSet = TokenSet.create(REFERENCE_EXPRESSION,DOT_QUALIFIED_EXPRESSION)

val POSTFIX_OPERATIONS: TokenSet = TokenSet.create(KtTokens.PLUSPLUS,KtTokens.MINUSMINUS,KtTokens.EXCLEXCL,KtTokens.DOT,KtTokens.SAFE_ACCESS)

val PREFIX_OPERATIONS: TokenSet = TokenSet.create(KtTokens.MINUS,KtTokens.PLUS,KtTokens.MINUSMINUS,KtTokens.PLUSPLUS,KtTokens.EXCL)

val CONSTANT_EXPRESSIONS: TokenSet = TokenSet.create(NULL,BOOLEAN_CONSTANT,FLOAT_CONSTANT,CHARACTER_CONSTANT,INTEGER_CONSTANT,REFERENCE_EXPRESSION,CALL_EXPRESSION,DOT_QUALIFIED_EXPRESSION,STRING_TEMPLATE,CLASS_LITERAL_EXPRESSION,COLLECTION_LITERAL_EXPRESSION)
}
}
