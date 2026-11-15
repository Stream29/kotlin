/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("EXPOSED_PROPERTY_TYPE")

package org.jetbrains.kotlin.psi.stubs.elements

import com.intellij.psi.stubs.StubElementTypeHolderEP
import org.jetbrains.kotlin.KtNodeType
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.impl.KtUnionTypeImpl

interface KtStubElementTypes {
companion object {
val CLASS: KtNodeType = KtNodeType("CLASS",::KtClass)

val FUNCTION: KtNodeType = KtNodeType("FUNCTION",::KtNamedFunction)

val PROPERTY: KtNodeType = KtNodeType("PROPERTY",::KtProperty)

val PROPERTY_ACCESSOR: KtNodeType = KtNodeType("PROPERTY_ACCESSOR",::KtPropertyAccessor)

val BACKING_FIELD: KtNodeType = KtNodeType("BACKING_FIELD",::KtBackingField)

val DESTRUCTURING_DECLARATION: KtNodeType = KtNodeType("DESTRUCTURING_DECLARATION",::KtDestructuringDeclaration)

val TYPEALIAS: KtNodeType = KtNodeType("TYPEALIAS",::KtTypeAlias)

val ENUM_ENTRY: KtNodeType = KtNodeType("ENUM_ENTRY",::KtEnumEntry)

val OBJECT_DECLARATION: KtNodeType = KtNodeType("OBJECT_DECLARATION",::KtObjectDeclaration)

val CLASS_INITIALIZER: KtNodeType = KtNodeType("CLASS_INITIALIZER",::KtClassInitializer)

val SCRIPT_INITIALIZER: KtNodeType = KtNodeType("SCRIPT_INITIALIZER",::KtScriptInitializer)

val SECONDARY_CONSTRUCTOR: KtNodeType = KtNodeType("SECONDARY_CONSTRUCTOR",::KtSecondaryConstructor)

val PRIMARY_CONSTRUCTOR: KtNodeType = KtNodeType("PRIMARY_CONSTRUCTOR",::KtPrimaryConstructor)

val VALUE_PARAMETER: KtNodeType = KtNodeType("VALUE_PARAMETER",::KtParameter)

val VALUE_PARAMETER_LIST: KtNodeType = KtNodeType("VALUE_PARAMETER_LIST",::KtParameterList)

val TYPE_PARAMETER: KtNodeType = KtNodeType("TYPE_PARAMETER",::KtTypeParameter)

val TYPE_PARAMETER_LIST: KtNodeType = KtNodeType("TYPE_PARAMETER_LIST",::KtTypeParameterList)

val ANNOTATION_ENTRY: KtNodeType = KtNodeType("ANNOTATION_ENTRY",::KtAnnotationEntry)

val ANNOTATION: KtNodeType = KtNodeType("ANNOTATION",::KtAnnotation)

val ANNOTATION_TARGET: KtNodeType = KtNodeType("ANNOTATION_TARGET",::KtAnnotationUseSiteTarget)

val CLASS_BODY: KtNodeType = KtNodeType("CLASS_BODY",::KtClassBody)

val COMPANION_BLOCK: KtNodeType = KtNodeType("COMPANION_BLOCK",::KtCompanionBlock)

val IMPORT_LIST: KtNodeType = KtNodeType("IMPORT_LIST",::KtImportList)

val FILE_ANNOTATION_LIST: KtNodeType = KtNodeType("FILE_ANNOTATION_LIST",::KtFileAnnotationList)

val IMPORT_DIRECTIVE: KtNodeType = KtNodeType("IMPORT_DIRECTIVE",::KtImportDirective)

val IMPORT_ALIAS: KtNodeType = KtNodeType("IMPORT_ALIAS",::KtImportAlias)

val PACKAGE_DIRECTIVE: KtNodeType = KtNodeType("PACKAGE_DIRECTIVE",::KtPackageDirective)

val MODIFIER_LIST: KtNodeType = KtNodeType("MODIFIER_LIST",::KtDeclarationModifierList)

val TYPE_CONSTRAINT_LIST: KtNodeType = KtNodeType("TYPE_CONSTRAINT_LIST",::KtTypeConstraintList)

val TYPE_CONSTRAINT: KtNodeType = KtNodeType("TYPE_CONSTRAINT",::KtTypeConstraint)

val NULLABLE_TYPE: KtNodeType = KtNodeType("NULLABLE_TYPE",::KtNullableType)

val INTERSECTION_TYPE: KtNodeType = KtNodeType("INTERSECTION_TYPE",::KtIntersectionType)

val UNION_TYPE: KtNodeType = KtNodeType("UNION_TYPE",::KtUnionTypeImpl)

val TYPE_REFERENCE: KtNodeType = KtNodeType("TYPE_REFERENCE",::KtTypeReference)

val USER_TYPE: KtNodeType = KtNodeType("USER_TYPE",::KtUserType)

val DYNAMIC_TYPE: KtNodeType = KtNodeType("DYNAMIC_TYPE",::KtDynamicType)

val FUNCTION_TYPE: KtNodeType = KtNodeType("FUNCTION_TYPE",::KtFunctionType)

val TYPE_PROJECTION: KtNodeType = KtNodeType("TYPE_PROJECTION",::KtTypeProjection)

val FUNCTION_TYPE_RECEIVER: KtNodeType = KtNodeType("FUNCTION_TYPE_RECEIVER",::KtFunctionTypeReceiver)

val REFERENCE_EXPRESSION: KtNodeType = KtNodeType("REFERENCE_EXPRESSION",::KtNameReferenceExpression)

val DOT_QUALIFIED_EXPRESSION: KtNodeType = KtNodeType("DOT_QUALIFIED_EXPRESSION",::KtDotQualifiedExpression)

val CALL_EXPRESSION: KtNodeType = KtNodeType("CALL_EXPRESSION",::KtCallExpression)

val OPERATION_REFERENCE: KtNodeType = KtNodeType("OPERATION_REFERENCE",::KtOperationReferenceExpression)

val PREFIX_EXPRESSION: KtNodeType = KtNodeType("PREFIX_EXPRESSION",::KtPrefixExpression)

val POSTFIX_EXPRESSION: KtNodeType = KtNodeType("POSTFIX_EXPRESSION",::KtPostfixExpression)

val BINARY_EXPRESSION: KtNodeType = KtNodeType("BINARY_EXPRESSION",::KtBinaryExpression)

val PARENTHESIZED: KtNodeType = KtNodeType("PARENTHESIZED",::KtParenthesizedExpression)

val OBJECT_LITERAL: KtNodeType = KtNodeType("OBJECT_LITERAL",::KtObjectLiteralExpression)

val ENUM_ENTRY_SUPERCLASS_REFERENCE_EXPRESSION: KtNodeType = KtNodeType("ENUM_ENTRY_SUPERCLASS_REFERENCE_EXPRESSION",::KtEnumEntrySuperclassReferenceExpression)

val TYPE_ARGUMENT_LIST: KtNodeType = KtNodeType("TYPE_ARGUMENT_LIST",::KtTypeArgumentList)

val VALUE_ARGUMENT_LIST: KtNodeType = KtNodeType("VALUE_ARGUMENT_LIST",::KtValueArgumentList)

val VALUE_ARGUMENT: KtNodeType = KtNodeType("VALUE_ARGUMENT",::KtValueArgument)

val CONTRACT_EFFECT_LIST: KtNodeType = KtNodeType("CONTRACT_EFFECT_LIST",::KtContractEffectList)

val CONTRACT_EFFECT: KtNodeType = KtNodeType("CONTRACT_EFFECT",::KtContractEffect)

val LAMBDA_ARGUMENT: KtNodeType = KtNodeType("LAMBDA_ARGUMENT",::KtLambdaArgument)

val VALUE_ARGUMENT_NAME: KtNodeType = KtNodeType("VALUE_ARGUMENT_NAME",::KtValueArgumentName)

val SUPER_TYPE_LIST: KtNodeType = KtNodeType("SUPER_TYPE_LIST",::KtSuperTypeList)

val INITIALIZER_LIST: KtNodeType = KtNodeType("INITIALIZER_LIST",::KtInitializerList)

val DELEGATED_SUPER_TYPE_ENTRY: KtNodeType = KtNodeType("DELEGATED_SUPER_TYPE_ENTRY",::KtDelegatedSuperTypeEntry)

val SUPER_TYPE_CALL_ENTRY: KtNodeType = KtNodeType("SUPER_TYPE_CALL_ENTRY",::KtSuperTypeCallEntry)

val SUPER_TYPE_ENTRY: KtNodeType = KtNodeType("SUPER_TYPE_ENTRY",::KtSuperTypeEntry)

val CONSTRUCTOR_CALLEE: KtNodeType = KtNodeType("CONSTRUCTOR_CALLEE",::KtConstructorCalleeExpression)

val CONTEXT_RECEIVER: KtNodeType = KtNodeType("CONTEXT_RECEIVER",::KtContextReceiver)

val CONTEXT_PARAMETER_LIST: KtNodeType = KtNodeType("CONTEXT_PARAMETER_LIST",::KtContextReceiverList)

val NULL: KtNodeType = KtNodeType("NULL",::KtConstantExpression)

val BOOLEAN_CONSTANT: KtNodeType = KtNodeType("BOOLEAN_CONSTANT",::KtConstantExpression)

val FLOAT_CONSTANT: KtNodeType = KtNodeType("FLOAT_CONSTANT",::KtConstantExpression)

val CHARACTER_CONSTANT: KtNodeType = KtNodeType("CHARACTER_CONSTANT",::KtConstantExpression)

val INTEGER_CONSTANT: KtNodeType = KtNodeType("INTEGER_CONSTANT",::KtConstantExpression)

val CLASS_LITERAL_EXPRESSION: KtNodeType = KtNodeType("CLASS_LITERAL_EXPRESSION",::KtClassLiteralExpression)

val COLLECTION_LITERAL_EXPRESSION: KtNodeType = KtNodeType("COLLECTION_LITERAL_EXPRESSION",::KtCollectionLiteralExpression)

val STRING_TEMPLATE: KtNodeType = KtNodeType("STRING_TEMPLATE",::KtStringTemplateExpression)

val LONG_STRING_TEMPLATE_ENTRY: KtNodeType = KtNodeType("LONG_STRING_TEMPLATE_ENTRY",::KtBlockStringTemplateEntry)

val SHORT_STRING_TEMPLATE_ENTRY: KtNodeType = KtNodeType("SHORT_STRING_TEMPLATE_ENTRY",::KtSimpleNameStringTemplateEntry)

val LITERAL_STRING_TEMPLATE_ENTRY: KtNodeType = KtNodeType("LITERAL_STRING_TEMPLATE_ENTRY",::KtLiteralStringTemplateEntry)

val ESCAPE_STRING_TEMPLATE_ENTRY: KtNodeType = KtNodeType("ESCAPE_STRING_TEMPLATE_ENTRY",::KtEscapeStringTemplateEntry)

val SCRIPT: KtNodeType = KtNodeType("SCRIPT",::KtScript)

val STRING_INTERPOLATION_PREFIX: KtNodeType = KtNodeType("STRING_INTERPOLATION_PREFIX",::KtStringInterpolationPrefix)
}
}
