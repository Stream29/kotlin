/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.kmp.utils

import com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.kmp.lexer.KtTokens
import org.jetbrains.kotlin.lexer.KtToken

/**
 * @receiver Some of Kotlin-based element types
 * @return Same integer ID as the corresponding [com.intellij.platform.syntax.SyntaxElementType] has.
 * If the element type is not Kotlin-based, the result is [org.jetbrains.kotlin.lexer.KtTokens.INVALID_Id].
 */
fun IElementType.kmpId(): Int =
    when {
        this is KtToken -> tokenId
        index >= 0 && index < indexToId.size -> indexToId[index.toInt()]
        else -> org.jetbrains.kotlin.lexer.KtTokens.INVALID_Id
    }

private val indexToId: IntArray = run {
    val indexToIdMap = mapOf<Short, Int>(
        KtNodeTypes.FILE.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.FILE),
        KtNodeTypes.CLASS.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.CLASS),
        KtNodeTypes.FUN.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.FUN),
        KtNodeTypes.PROPERTY.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.PROPERTY),
        KtNodeTypes.PROPERTY_ACCESSOR.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.PROPERTY_ACCESSOR),
        KtNodeTypes.BACKING_FIELD.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.BACKING_FIELD),
        KtNodeTypes.TYPEALIAS.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.TYPEALIAS),
        KtNodeTypes.ENUM_ENTRY.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.ENUM_ENTRY),
        KtNodeTypes.OBJECT_DECLARATION.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.OBJECT_DECLARATION),
        KtNodeTypes.CLASS_INITIALIZER.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.CLASS_INITIALIZER),
        KtNodeTypes.SECONDARY_CONSTRUCTOR.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.SECONDARY_CONSTRUCTOR),
        KtNodeTypes.PRIMARY_CONSTRUCTOR.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.PRIMARY_CONSTRUCTOR),
        KtNodeTypes.VALUE_PARAMETER.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.VALUE_PARAMETER),
        KtNodeTypes.VALUE_PARAMETER_LIST.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.VALUE_PARAMETER_LIST),
        KtNodeTypes.TYPE_PARAMETER.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.TYPE_PARAMETER),
        KtNodeTypes.TYPE_PARAMETER_LIST.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.TYPE_PARAMETER_LIST),
        KtNodeTypes.ANNOTATION_ENTRY.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.ANNOTATION_ENTRY),
        KtNodeTypes.ANNOTATION.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.ANNOTATION),
        KtNodeTypes.ANNOTATION_TARGET.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.ANNOTATION_TARGET),
        KtNodeTypes.CLASS_BODY.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.CLASS_BODY),
        KtNodeTypes.COMPANION_BLOCK.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.COMPANION_BLOCK),
        KtNodeTypes.IMPORT_LIST.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.IMPORT_LIST),
        KtNodeTypes.FILE_ANNOTATION_LIST.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.FILE_ANNOTATION_LIST),
        KtNodeTypes.IMPORT_DIRECTIVE.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.IMPORT_DIRECTIVE),
        KtNodeTypes.IMPORT_ALIAS.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.IMPORT_ALIAS),
        KtNodeTypes.PACKAGE_DIRECTIVE.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.PACKAGE_DIRECTIVE),
        KtNodeTypes.MODIFIER_LIST.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.MODIFIER_LIST),
        KtNodeTypes.TYPE_CONSTRAINT_LIST.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.TYPE_CONSTRAINT_LIST),
        KtNodeTypes.TYPE_CONSTRAINT.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.TYPE_CONSTRAINT),
        KtNodeTypes.NULLABLE_TYPE.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.NULLABLE_TYPE),
        KtNodeTypes.INTERSECTION_TYPE.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.INTERSECTION_TYPE),
        KtNodeTypes.UNION_TYPE.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.UNION_TYPE),
        KtNodeTypes.TYPE_REFERENCE.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.TYPE_REFERENCE),
        KtNodeTypes.USER_TYPE.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.USER_TYPE),
        KtNodeTypes.DYNAMIC_TYPE.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.DYNAMIC_TYPE),
        KtNodeTypes.FUNCTION_TYPE.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.FUNCTION_TYPE),
        KtNodeTypes.TYPE_CODE_FRAGMENT.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.TYPE_CODE_FRAGMENT),
        KtNodeTypes.EXPRESSION_CODE_FRAGMENT.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.EXPRESSION_CODE_FRAGMENT),
        KtNodeTypes.BLOCK_CODE_FRAGMENT.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.BLOCK_CODE_FRAGMENT),
        KtNodeTypes.TYPE_PROJECTION.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.TYPE_PROJECTION),
        KtNodeTypes.FUNCTION_TYPE_RECEIVER.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.FUNCTION_TYPE_RECEIVER),
        KtNodeTypes.REFERENCE_EXPRESSION.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.REFERENCE_EXPRESSION),
        KtNodeTypes.DOT_QUALIFIED_EXPRESSION.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.DOT_QUALIFIED_EXPRESSION),
        KtNodeTypes.ENUM_ENTRY_SUPERCLASS_REFERENCE_EXPRESSION.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.ENUM_ENTRY_SUPERCLASS_REFERENCE_EXPRESSION),
        KtNodeTypes.TYPE_ARGUMENT_LIST.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.TYPE_ARGUMENT_LIST),
        KtNodeTypes.VALUE_ARGUMENT_LIST.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.VALUE_ARGUMENT_LIST),
        KtNodeTypes.VALUE_ARGUMENT.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.VALUE_ARGUMENT),
        KtNodeTypes.CONTRACT_EFFECT_LIST.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.CONTRACT_EFFECT_LIST),
        KtNodeTypes.CONTRACT_EFFECT.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.CONTRACT_EFFECT),
        KtNodeTypes.LAMBDA_ARGUMENT.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.LAMBDA_ARGUMENT),
        KtNodeTypes.VALUE_ARGUMENT_NAME.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.VALUE_ARGUMENT_NAME),
        KtNodeTypes.SUPER_TYPE_LIST.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.SUPER_TYPE_LIST),
        KtNodeTypes.INITIALIZER_LIST.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.INITIALIZER_LIST),
        KtNodeTypes.DELEGATED_SUPER_TYPE_ENTRY.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.DELEGATED_SUPER_TYPE_ENTRY),
        KtNodeTypes.SUPER_TYPE_CALL_ENTRY.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.SUPER_TYPE_CALL_ENTRY),
        KtNodeTypes.SUPER_TYPE_ENTRY.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.SUPER_TYPE_ENTRY),
        KtNodeTypes.CONSTRUCTOR_CALLEE.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.CONSTRUCTOR_CALLEE),
        KtNodeTypes.CONTEXT_RECEIVER.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.CONTEXT_RECEIVER),
        KtNodeTypes.CONTEXT_PARAMETER_LIST.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.CONTEXT_PARAMETER_LIST),
        KtNodeTypes.NULL.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.NULL),
        KtNodeTypes.BOOLEAN_CONSTANT.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.BOOLEAN_CONSTANT),
        KtNodeTypes.FLOAT_CONSTANT.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.FLOAT_CONSTANT),
        KtNodeTypes.CHARACTER_CONSTANT.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.CHARACTER_CONSTANT),
        KtNodeTypes.INTEGER_CONSTANT.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.INTEGER_CONSTANT),
        KtNodeTypes.CLASS_LITERAL_EXPRESSION.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.CLASS_LITERAL_EXPRESSION),
        KtNodeTypes.COLLECTION_LITERAL_EXPRESSION.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.COLLECTION_LITERAL_EXPRESSION),
        KtNodeTypes.STRING_TEMPLATE.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.STRING_TEMPLATE),
        KtNodeTypes.LONG_STRING_TEMPLATE_ENTRY.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.LONG_STRING_TEMPLATE_ENTRY),
        KtNodeTypes.SHORT_STRING_TEMPLATE_ENTRY.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.SHORT_STRING_TEMPLATE_ENTRY),
        KtNodeTypes.LITERAL_STRING_TEMPLATE_ENTRY.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.LITERAL_STRING_TEMPLATE_ENTRY),
        KtNodeTypes.ESCAPE_STRING_TEMPLATE_ENTRY.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.ESCAPE_STRING_TEMPLATE_ENTRY),
        KtNodeTypes.SCRIPT.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.SCRIPT),
        KtNodeTypes.STRING_INTERPOLATION_PREFIX.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.STRING_INTERPOLATION_PREFIX),
        KtNodeTypes.DESTRUCTURING_DECLARATION.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.DESTRUCTURING_DECLARATION),
        KtNodeTypes.DESTRUCTURING_DECLARATION_ENTRY.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.DESTRUCTURING_DECLARATION_ENTRY),
        KtNodeTypes.SCRIPT_INITIALIZER.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.SCRIPT_INITIALIZER),
        KtNodeTypes.PROPERTY_DELEGATE.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.PROPERTY_DELEGATE),
        KtNodeTypes.CONSTRUCTOR_DELEGATION_CALL.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.CONSTRUCTOR_DELEGATION_CALL),
        KtNodeTypes.CONSTRUCTOR_DELEGATION_REFERENCE.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.CONSTRUCTOR_DELEGATION_REFERENCE),
        KtNodeTypes.PARENTHESIZED.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.PARENTHESIZED),
        KtNodeTypes.RETURN.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.RETURN),
        KtNodeTypes.THROW.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.THROW),
        KtNodeTypes.CONTINUE.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.CONTINUE),
        KtNodeTypes.BREAK.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.BREAK),
        KtNodeTypes.IF.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.IF),
        KtNodeTypes.CONDITION.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.CONDITION),
        KtNodeTypes.THEN.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.THEN),
        KtNodeTypes.ELSE.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.ELSE),
        KtNodeTypes.TRY.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.TRY),
        KtNodeTypes.CATCH.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.CATCH),
        KtNodeTypes.FINALLY.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.FINALLY),
        KtNodeTypes.FOR.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.FOR),
        KtNodeTypes.WHILE.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.WHILE),
        KtNodeTypes.DO_WHILE.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.DO_WHILE),
        KtNodeTypes.LOOP_RANGE.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.LOOP_RANGE),
        KtNodeTypes.BODY.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.BODY),
        KtNodeTypes.BLOCK.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.BLOCK),
        KtNodeTypes.LAMBDA_EXPRESSION.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.LAMBDA_EXPRESSION),
        KtNodeTypes.FUNCTION_LITERAL.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.FUNCTION_LITERAL),
        KtNodeTypes.ANNOTATED_EXPRESSION.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.ANNOTATED_EXPRESSION),
        KtNodeTypes.OPERATION_REFERENCE.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.OPERATION_REFERENCE),
        KtNodeTypes.LABEL.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.LABEL),
        KtNodeTypes.LABEL_QUALIFIER.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.LABEL_QUALIFIER),
        KtNodeTypes.THIS_EXPRESSION.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.THIS_EXPRESSION),
        KtNodeTypes.SUPER_EXPRESSION.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.SUPER_EXPRESSION),
        KtNodeTypes.BINARY_EXPRESSION.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.BINARY_EXPRESSION),
        KtNodeTypes.BINARY_WITH_TYPE.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.BINARY_WITH_TYPE),
        KtNodeTypes.IS_EXPRESSION.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.IS_EXPRESSION),
        KtNodeTypes.PREFIX_EXPRESSION.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.PREFIX_EXPRESSION),
        KtNodeTypes.POSTFIX_EXPRESSION.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.POSTFIX_EXPRESSION),
        KtNodeTypes.LABELED_EXPRESSION.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.LABELED_EXPRESSION),
        KtNodeTypes.CALL_EXPRESSION.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.CALL_EXPRESSION),
        KtNodeTypes.ARRAY_ACCESS_EXPRESSION.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.ARRAY_ACCESS_EXPRESSION),
        KtNodeTypes.INDICES.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.INDICES),
        KtNodeTypes.CALLABLE_REFERENCE_EXPRESSION.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.CALLABLE_REFERENCE_EXPRESSION),
        KtNodeTypes.SAFE_ACCESS_EXPRESSION.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.SAFE_ACCESS_EXPRESSION),
        KtNodeTypes.ERROR_SAFE_ACCESS_EXPRESSION.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.ERROR_SAFE_ACCESS_EXPRESSION),
        KtNodeTypes.OBJECT_LITERAL.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.OBJECT_LITERAL),
        KtNodeTypes.WHEN.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.WHEN),
        KtNodeTypes.WHEN_ENTRY.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.WHEN_ENTRY),
        KtNodeTypes.WHEN_ENTRY_GUARD.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.WHEN_ENTRY_GUARD),
        KtNodeTypes.WHEN_CONDITION_IN_RANGE.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.WHEN_CONDITION_IN_RANGE),
        KtNodeTypes.WHEN_CONDITION_IS_PATTERN.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.WHEN_CONDITION_IS_PATTERN),
        KtNodeTypes.WHEN_CONDITION_EXPRESSION.index to org.jetbrains.kotlin.kmp.parser.KtNodeTypes.getElementTypeId(org.jetbrains.kotlin.kmp.parser.KtNodeTypes.WHEN_CONDITION_EXPRESSION),
        org.jetbrains.kotlin.lexer.KtTokens.DOC_COMMENT.index to KtTokens.DOC_COMMENT_ID,
        org.jetbrains.kotlin.lexer.KtTokens.WHITE_SPACE.index to KtTokens.WHITE_SPACE_ID,
    )
    var maxIndex: Short = 0
    for (idx in indexToIdMap.keys) {
        if (idx > maxIndex) maxIndex = idx
    }
    IntArray(maxIndex.toInt() + 1).apply {
        for (entry in indexToIdMap) {
            this[entry.key.toInt()] = entry.value
        }
    }
}
