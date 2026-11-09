/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin

import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IFileElementType
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.*



interface KtNodeTypes {
companion object {
val FILE: IFileElementType = KtStubBasedElementTypes.FILE

val KT_FILE: IFileElementType = IFileElementType(KotlinLanguage.INSTANCE)

val CLASS: IElementType = KtStubBasedElementTypes.CLASS

val FUN: IElementType = KtStubBasedElementTypes.FUNCTION

val PROPERTY: IElementType = KtStubBasedElementTypes.PROPERTY

val DESTRUCTURING_DECLARATION: IElementType = KtStubBasedElementTypes.DESTRUCTURING_DECLARATION

val DESTRUCTURING_DECLARATION_ENTRY: IElementType = KtNodeType("DESTRUCTURING_DECLARATION_ENTRY",::KtDestructuringDeclarationEntry)

val OBJECT_DECLARATION: IElementType = KtStubBasedElementTypes.OBJECT_DECLARATION

val TYPEALIAS: IElementType = KtStubBasedElementTypes.TYPEALIAS

val COMPANION_BLOCK: IElementType = KtStubBasedElementTypes.COMPANION_BLOCK

val ENUM_ENTRY: IElementType = KtStubBasedElementTypes.ENUM_ENTRY

val CLASS_INITIALIZER: IElementType = KtStubBasedElementTypes.CLASS_INITIALIZER

val SCRIPT_INITIALIZER: IElementType = KtStubBasedElementTypes.SCRIPT_INITIALIZER

val SECONDARY_CONSTRUCTOR: IElementType = KtStubBasedElementTypes.SECONDARY_CONSTRUCTOR

val PRIMARY_CONSTRUCTOR: IElementType = KtStubBasedElementTypes.PRIMARY_CONSTRUCTOR

val CONTEXT_RECEIVER: IElementType = KtStubBasedElementTypes.CONTEXT_RECEIVER

val CONTEXT_PARAMETER_LIST: IElementType = KtStubBasedElementTypes.CONTEXT_PARAMETER_LIST

val CONTEXT_RECEIVER_LIST: IElementType = CONTEXT_PARAMETER_LIST

val TYPE_PARAMETER_LIST: IElementType = KtStubBasedElementTypes.TYPE_PARAMETER_LIST

val TYPE_PARAMETER: IElementType = KtStubBasedElementTypes.TYPE_PARAMETER

val SUPER_TYPE_LIST: IElementType = KtStubBasedElementTypes.SUPER_TYPE_LIST

val DELEGATED_SUPER_TYPE_ENTRY: IElementType = KtStubBasedElementTypes.DELEGATED_SUPER_TYPE_ENTRY

val SUPER_TYPE_CALL_ENTRY: IElementType = KtStubBasedElementTypes.SUPER_TYPE_CALL_ENTRY

val SUPER_TYPE_ENTRY: IElementType = KtStubBasedElementTypes.SUPER_TYPE_ENTRY

val PROPERTY_DELEGATE: IElementType = KtNodeType("PROPERTY_DELEGATE",::KtPropertyDelegate)

val CONSTRUCTOR_CALLEE: IElementType = KtStubBasedElementTypes.CONSTRUCTOR_CALLEE

val VALUE_PARAMETER_LIST: IElementType = KtStubBasedElementTypes.VALUE_PARAMETER_LIST

val VALUE_PARAMETER: IElementType = KtStubBasedElementTypes.VALUE_PARAMETER

val CLASS_BODY: IElementType = KtStubBasedElementTypes.CLASS_BODY

val IMPORT_LIST: IElementType = KtStubBasedElementTypes.IMPORT_LIST

val FILE_ANNOTATION_LIST: IElementType = KtStubBasedElementTypes.FILE_ANNOTATION_LIST

val IMPORT_DIRECTIVE: IElementType = KtStubBasedElementTypes.IMPORT_DIRECTIVE

val IMPORT_ALIAS: IElementType = KtStubBasedElementTypes.IMPORT_ALIAS

val MODIFIER_LIST: IElementType = KtStubBasedElementTypes.MODIFIER_LIST

val ANNOTATION: IElementType = KtStubBasedElementTypes.ANNOTATION

val ANNOTATION_ENTRY: IElementType = KtStubBasedElementTypes.ANNOTATION_ENTRY

val ANNOTATION_TARGET: IElementType = KtStubBasedElementTypes.ANNOTATION_TARGET

val TYPE_ARGUMENT_LIST: IElementType = KtStubBasedElementTypes.TYPE_ARGUMENT_LIST

val VALUE_ARGUMENT_LIST: IElementType = KtStubBasedElementTypes.VALUE_ARGUMENT_LIST

val VALUE_ARGUMENT: IElementType = KtStubBasedElementTypes.VALUE_ARGUMENT

val CONTRACT_EFFECT_LIST: IElementType = KtStubBasedElementTypes.CONTRACT_EFFECT_LIST

val CONTRACT_EFFECT: IElementType = KtStubBasedElementTypes.CONTRACT_EFFECT

val LAMBDA_ARGUMENT: IElementType = KtStubBasedElementTypes.LAMBDA_ARGUMENT

val VALUE_ARGUMENT_NAME: IElementType = KtStubBasedElementTypes.VALUE_ARGUMENT_NAME

val TYPE_REFERENCE: IElementType = KtStubBasedElementTypes.TYPE_REFERENCE

val USER_TYPE: IElementType = KtStubBasedElementTypes.USER_TYPE

val DYNAMIC_TYPE: IElementType = KtStubBasedElementTypes.DYNAMIC_TYPE

val FUNCTION_TYPE: IElementType = KtStubBasedElementTypes.FUNCTION_TYPE

val FUNCTION_TYPE_RECEIVER: IElementType = KtStubBasedElementTypes.FUNCTION_TYPE_RECEIVER

val NULLABLE_TYPE: IElementType = KtStubBasedElementTypes.NULLABLE_TYPE

val INTERSECTION_TYPE: IElementType = KtStubBasedElementTypes.INTERSECTION_TYPE

val TYPE_PROJECTION: IElementType = KtStubBasedElementTypes.TYPE_PROJECTION

val PROPERTY_ACCESSOR: IElementType = KtStubBasedElementTypes.PROPERTY_ACCESSOR

val BACKING_FIELD: IElementType = KtStubBasedElementTypes.BACKING_FIELD

val INITIALIZER_LIST: IElementType = KtStubBasedElementTypes.INITIALIZER_LIST

val TYPE_CONSTRAINT_LIST: IElementType = KtStubBasedElementTypes.TYPE_CONSTRAINT_LIST

val TYPE_CONSTRAINT: IElementType = KtStubBasedElementTypes.TYPE_CONSTRAINT

val CONSTRUCTOR_DELEGATION_CALL: IElementType = KtNodeType.KtLeftBoundNodeType("CONSTRUCTOR_DELEGATION_CALL",::KtConstructorDelegationCall)

val CONSTRUCTOR_DELEGATION_REFERENCE: IElementType = KtNodeType.KtLeftBoundNodeType("CONSTRUCTOR_DELEGATION_REFERENCE",::KtConstructorDelegationReferenceExpression)

val NULL: IElementType = KtStubBasedElementTypes.NULL

val BOOLEAN_CONSTANT: IElementType = KtStubBasedElementTypes.BOOLEAN_CONSTANT

val FLOAT_CONSTANT: IElementType = KtStubBasedElementTypes.FLOAT_CONSTANT

val CHARACTER_CONSTANT: IElementType = KtStubBasedElementTypes.CHARACTER_CONSTANT

val INTEGER_CONSTANT: IElementType = KtStubBasedElementTypes.INTEGER_CONSTANT

val STRING_TEMPLATE: IElementType = KtStubBasedElementTypes.STRING_TEMPLATE

val LONG_STRING_TEMPLATE_ENTRY: IElementType = KtStubBasedElementTypes.LONG_STRING_TEMPLATE_ENTRY

val SHORT_STRING_TEMPLATE_ENTRY: IElementType = KtStubBasedElementTypes.SHORT_STRING_TEMPLATE_ENTRY

val LITERAL_STRING_TEMPLATE_ENTRY: IElementType = KtStubBasedElementTypes.LITERAL_STRING_TEMPLATE_ENTRY

val ESCAPE_STRING_TEMPLATE_ENTRY: IElementType = KtStubBasedElementTypes.ESCAPE_STRING_TEMPLATE_ENTRY

val STRING_INTERPOLATION_PREFIX: IElementType = KtStubBasedElementTypes.STRING_INTERPOLATION_PREFIX

val PARENTHESIZED: IElementType = KtNodeType("PARENTHESIZED",::KtParenthesizedExpression)

val RETURN: IElementType = KtNodeType("RETURN",::KtReturnExpression)

val THROW: IElementType = KtNodeType("THROW",::KtThrowExpression)

val CONTINUE: IElementType = KtNodeType("CONTINUE",::KtContinueExpression)

val BREAK: IElementType = KtNodeType("BREAK",::KtBreakExpression)

val IF: IElementType = KtNodeType("IF",::KtIfExpression)

val CONDITION: IElementType = KtNodeType("CONDITION",::KtContainerNode)

val THEN: IElementType = KtNodeType("THEN",::KtContainerNodeForControlStructureBody)

val ELSE: IElementType = KtNodeType("ELSE",::KtContainerNodeForControlStructureBody)

val TRY: IElementType = KtNodeType("TRY",::KtTryExpression)

val CATCH: IElementType = KtNodeType("CATCH",::KtCatchClause)

val FINALLY: IElementType = KtNodeType("FINALLY",::KtFinallySection)

val FOR: IElementType = KtNodeType("FOR",::KtForExpression)

val WHILE: IElementType = KtNodeType("WHILE",::KtWhileExpression)

val DO_WHILE: IElementType = KtNodeType("DO_WHILE",::KtDoWhileExpression)

val LOOP_RANGE: IElementType = KtNodeType("LOOP_RANGE",::KtContainerNode)

val BODY: IElementType = KtNodeType("BODY",::KtContainerNodeForControlStructureBody)

val BLOCK: IElementType = KtStubBasedElementTypes.BLOCK

val LAMBDA_EXPRESSION: IElementType = KtStubBasedElementTypes.LAMBDA_EXPRESSION

val FUNCTION_LITERAL: IElementType = KtNodeType("FUNCTION_LITERAL",::KtFunctionLiteral)

val ANNOTATED_EXPRESSION: IElementType = KtNodeType("ANNOTATED_EXPRESSION",::KtAnnotatedExpression)

val REFERENCE_EXPRESSION: IElementType = KtStubBasedElementTypes.REFERENCE_EXPRESSION

val ENUM_ENTRY_SUPERCLASS_REFERENCE_EXPRESSION: IElementType = KtStubBasedElementTypes.ENUM_ENTRY_SUPERCLASS_REFERENCE_EXPRESSION

val OPERATION_REFERENCE: IElementType = KtNodeType("OPERATION_REFERENCE",::KtOperationReferenceExpression)

val LABEL: IElementType = KtNodeType("LABEL",::KtLabelReferenceExpression)

val LABEL_QUALIFIER: IElementType = KtNodeType("LABEL_QUALIFIER",::KtContainerNode)

val THIS_EXPRESSION: IElementType = KtNodeType("THIS_EXPRESSION",::KtThisExpression)

val SUPER_EXPRESSION: IElementType = KtNodeType("SUPER_EXPRESSION",::KtSuperExpression)

val BINARY_EXPRESSION: IElementType = KtNodeType("BINARY_EXPRESSION",::KtBinaryExpression)

val BINARY_WITH_TYPE: IElementType = KtNodeType("BINARY_WITH_TYPE",::KtBinaryExpressionWithTypeRHS)

val IS_EXPRESSION: IElementType = KtNodeType("IS_EXPRESSION",::KtIsExpression)

val PREFIX_EXPRESSION: IElementType = KtNodeType("PREFIX_EXPRESSION",::KtPrefixExpression)

val POSTFIX_EXPRESSION: IElementType = KtNodeType("POSTFIX_EXPRESSION",::KtPostfixExpression)

val LABELED_EXPRESSION: IElementType = KtNodeType("LABELED_EXPRESSION",::KtLabeledExpression)

val CALL_EXPRESSION: IElementType = KtStubBasedElementTypes.CALL_EXPRESSION

val ARRAY_ACCESS_EXPRESSION: IElementType = KtNodeType("ARRAY_ACCESS_EXPRESSION",::KtArrayAccessExpression)

val INDICES: IElementType = KtNodeType("INDICES",::KtContainerNode)

val DOT_QUALIFIED_EXPRESSION: IElementType = KtStubBasedElementTypes.DOT_QUALIFIED_EXPRESSION

val CALLABLE_REFERENCE_EXPRESSION: IElementType = KtNodeType("CALLABLE_REFERENCE_EXPRESSION",::KtCallableReferenceExpression)

val CLASS_LITERAL_EXPRESSION: IElementType = KtStubBasedElementTypes.CLASS_LITERAL_EXPRESSION

val SAFE_ACCESS_EXPRESSION: IElementType = KtNodeType("SAFE_ACCESS_EXPRESSION",::KtSafeQualifiedExpression)

val OBJECT_LITERAL: IElementType = KtNodeType("OBJECT_LITERAL",::KtObjectLiteralExpression)

val WHEN: IElementType = KtNodeType("WHEN",::KtWhenExpression)

val WHEN_ENTRY: IElementType = KtNodeType("WHEN_ENTRY",::KtWhenEntry)

val WHEN_ENTRY_GUARD: IElementType = KtNodeType("WHEN_ENTRY_GUARD",::KtWhenEntryGuard)

val WHEN_CONDITION_IN_RANGE: IElementType = KtNodeType("WHEN_CONDITION_IN_RANGE",::KtWhenConditionInRange)

val WHEN_CONDITION_IS_PATTERN: IElementType = KtNodeType("WHEN_CONDITION_IS_PATTERN",::KtWhenConditionIsPattern)

val WHEN_CONDITION_EXPRESSION: IElementType = KtNodeType("WHEN_CONDITION_WITH_EXPRESSION",::KtWhenConditionWithExpression)

val COLLECTION_LITERAL_EXPRESSION: IElementType = KtStubBasedElementTypes.COLLECTION_LITERAL_EXPRESSION

val PACKAGE_DIRECTIVE: IElementType = KtStubBasedElementTypes.PACKAGE_DIRECTIVE

val SCRIPT: IElementType = KtStubBasedElementTypes.SCRIPT

val TYPE_CODE_FRAGMENT: IFileElementType = KtStubBasedElementTypes.TYPE_CODE_FRAGMENT

val EXPRESSION_CODE_FRAGMENT: IFileElementType = KtStubBasedElementTypes.EXPRESSION_CODE_FRAGMENT

val BLOCK_CODE_FRAGMENT: IFileElementType = KtStubBasedElementTypes.BLOCK_CODE_FRAGMENT
}
}
