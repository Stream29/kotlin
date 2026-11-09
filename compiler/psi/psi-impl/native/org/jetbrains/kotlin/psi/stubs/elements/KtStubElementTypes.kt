/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

// The original Java public fields expose these same internal Kotlin object types.
@file:Suppress("EXPOSED_PROPERTY_TYPE")

package org.jetbrains.kotlin.psi.stubs.elements

import org.jetbrains.kotlin.psi.*



interface KtStubElementTypes {
companion object {
val CLASS: KtClassElementType = KtClassElementType

val FUNCTION: KtFunctionElementType = KtFunctionElementType("FUN")

val PROPERTY: KtPropertyElementType = KtPropertyElementType("PROPERTY")

val PROPERTY_ACCESSOR: KtPropertyAccessorElementType = KtPropertyAccessorElementType

val BACKING_FIELD: KtBackingFieldElementType = KtBackingFieldElementType("BACKING_FIELD")

val DESTRUCTURING_DECLARATION: KtDestructuringDeclarationElementType = KtDestructuringDeclarationElementType

val TYPEALIAS: KtTypeAliasElementType = KtTypeAliasElementType("TYPEALIAS")

val ENUM_ENTRY: KtEnumEntryElementType = KtEnumEntryElementType

val OBJECT_DECLARATION: KtObjectElementType = KtObjectElementType("OBJECT_DECLARATION")

val CLASS_INITIALIZER: KtPlaceHolderStubElementType<KtClassInitializer> = KtPlaceHolderStubElementType("CLASS_INITIALIZER",KtClassInitializer ::class)

val SCRIPT_INITIALIZER: KtPlaceHolderStubElementType<KtScriptInitializer> = KtScriptInitializerElementType

val SECONDARY_CONSTRUCTOR: KtSecondaryConstructorElementType = KtSecondaryConstructorElementType("SECONDARY_CONSTRUCTOR")

val PRIMARY_CONSTRUCTOR: KtPrimaryConstructorElementType = KtPrimaryConstructorElementType("PRIMARY_CONSTRUCTOR")

val VALUE_PARAMETER: KtParameterElementType = KtParameterElementType("VALUE_PARAMETER")

val VALUE_PARAMETER_LIST: KtPlaceHolderStubElementType<KtParameterList> = KtPlaceHolderStubElementType("VALUE_PARAMETER_LIST",KtParameterList ::class)

val TYPE_PARAMETER: KtTypeParameterElementType = KtTypeParameterElementType("TYPE_PARAMETER")

val TYPE_PARAMETER_LIST: KtPlaceHolderStubElementType<KtTypeParameterList> = KtPlaceHolderStubElementType("TYPE_PARAMETER_LIST",KtTypeParameterList ::class)

val ANNOTATION_ENTRY: KtAnnotationEntryElementType = KtAnnotationEntryElementType

val ANNOTATION: KtPlaceHolderStubElementType<KtAnnotation> = KtPlaceHolderStubElementType("ANNOTATION",KtAnnotation ::class)

val ANNOTATION_TARGET: KtAnnotationUseSiteTargetElementType = KtAnnotationUseSiteTargetElementType("ANNOTATION_TARGET")

val CLASS_BODY: KtPlaceHolderStubElementType<KtClassBody> = KtPlaceHolderStubElementType("CLASS_BODY",KtClassBody ::class)

val COMPANION_BLOCK: KtPlaceHolderStubElementType<KtCompanionBlock> = KtPlaceHolderStubElementType("COMPANION_BLOCK",KtCompanionBlock ::class)

val IMPORT_LIST: KtPlaceHolderStubElementType<KtImportList> = KtPlaceHolderStubElementType("IMPORT_LIST",KtImportList ::class)

val FILE_ANNOTATION_LIST: KtPlaceHolderStubElementType<KtFileAnnotationList> = KtPlaceHolderStubElementType("FILE_ANNOTATION_LIST",KtFileAnnotationList ::class)

val IMPORT_DIRECTIVE: KtImportDirectiveElementType = KtImportDirectiveElementType("IMPORT_DIRECTIVE")

val IMPORT_ALIAS: KtImportAliasElementType = KtImportAliasElementType("IMPORT_ALIAS")

val PACKAGE_DIRECTIVE: KtPlaceHolderStubElementType<KtPackageDirective> = KtPlaceHolderStubElementType("PACKAGE_DIRECTIVE",KtPackageDirective ::class)

val MODIFIER_LIST: KtModifierListElementType<KtDeclarationModifierList> = KtModifierListElementType("MODIFIER_LIST",KtDeclarationModifierList ::class)

val TYPE_CONSTRAINT_LIST: KtPlaceHolderStubElementType<KtTypeConstraintList> = KtPlaceHolderStubElementType("TYPE_CONSTRAINT_LIST",KtTypeConstraintList ::class)

val TYPE_CONSTRAINT: KtPlaceHolderStubElementType<KtTypeConstraint> = KtPlaceHolderStubElementType("TYPE_CONSTRAINT",KtTypeConstraint ::class)

val NULLABLE_TYPE: KtPlaceHolderStubElementType<KtNullableType> = KtPlaceHolderStubElementType("NULLABLE_TYPE",KtNullableType ::class)

val INTERSECTION_TYPE: KtPlaceHolderStubElementType<KtIntersectionType> = KtPlaceHolderStubElementType("INTERSECTION_TYPE",KtIntersectionType ::class)

val TYPE_REFERENCE: KtPlaceHolderStubElementType<KtTypeReference> = KtPlaceHolderStubElementType("TYPE_REFERENCE",KtTypeReference ::class)

val USER_TYPE: KtUserTypeElementType = KtUserTypeElementType("USER_TYPE")

val DYNAMIC_TYPE: KtPlaceHolderStubElementType<KtDynamicType> = KtPlaceHolderStubElementType("DYNAMIC_TYPE",KtDynamicType ::class)

val FUNCTION_TYPE: KtFunctionTypeElementType = KtFunctionTypeElementType("FUNCTION_TYPE")

val TYPE_CODE_FRAGMENT: KtTypeCodeFragmentType = KtTypeCodeFragmentType()

val EXPRESSION_CODE_FRAGMENT: KtExpressionCodeFragmentType = KtExpressionCodeFragmentType()

val BLOCK_CODE_FRAGMENT: KtBlockCodeFragmentType = KtBlockCodeFragmentType()

val TYPE_PROJECTION: KtTypeProjectionElementType = KtTypeProjectionElementType("TYPE_PROJECTION")

val FUNCTION_TYPE_RECEIVER: KtPlaceHolderStubElementType<KtFunctionTypeReceiver> = KtPlaceHolderStubElementType("FUNCTION_TYPE_RECEIVER",KtFunctionTypeReceiver ::class)

val REFERENCE_EXPRESSION: KtNameReferenceExpressionElementType = KtNameReferenceExpressionElementType("REFERENCE_EXPRESSION")

val DOT_QUALIFIED_EXPRESSION: KtDotQualifiedExpressionElementType = KtDotQualifiedExpressionElementType("DOT_QUALIFIED_EXPRESSION")

val CALL_EXPRESSION: KtPlaceHolderStubElementType<KtCallExpression> = KtCallExpressionElementType

val ENUM_ENTRY_SUPERCLASS_REFERENCE_EXPRESSION: KtEnumEntrySuperClassReferenceExpressionElementType = KtEnumEntrySuperClassReferenceExpressionElementType("ENUM_ENTRY_SUPERCLASS_REFERENCE_EXPRESSION")

val TYPE_ARGUMENT_LIST: KtPlaceHolderStubElementType<KtTypeArgumentList> = KtPlaceHolderStubElementType("TYPE_ARGUMENT_LIST",KtTypeArgumentList ::class)

val VALUE_ARGUMENT_LIST: KtPlaceHolderStubElementType<KtValueArgumentList> = KtPlaceHolderStubElementType("VALUE_ARGUMENT_LIST",KtValueArgumentList ::class)

val VALUE_ARGUMENT: KtValueArgumentElementType<KtValueArgument> = KtValueArgumentElementType("VALUE_ARGUMENT",KtValueArgument ::class)

val CONTRACT_EFFECT_LIST: KtPlaceHolderStubElementType<KtContractEffectList> = KtContractEffectListElementType("CONTRACT_EFFECT_LIST")

val CONTRACT_EFFECT: KtContractEffectElementType = KtContractEffectElementType("CONTRACT_EFFECT",KtContractEffect ::class)

val LAMBDA_ARGUMENT: KtValueArgumentElementType<KtLambdaArgument> = KtValueArgumentElementType("LAMBDA_ARGUMENT",KtLambdaArgument ::class)

val VALUE_ARGUMENT_NAME: KtPlaceHolderStubElementType<KtValueArgumentName> = KtPlaceHolderStubElementType("VALUE_ARGUMENT_NAME",KtValueArgumentName ::class)

val SUPER_TYPE_LIST: KtPlaceHolderStubElementType<KtSuperTypeList> = KtPlaceHolderStubElementType("SUPER_TYPE_LIST",KtSuperTypeList ::class)

val INITIALIZER_LIST: KtPlaceHolderStubElementType<KtInitializerList> = KtPlaceHolderStubElementType("INITIALIZER_LIST",KtInitializerList ::class)

val DELEGATED_SUPER_TYPE_ENTRY: KtPlaceHolderStubElementType<KtDelegatedSuperTypeEntry> = KtPlaceHolderStubElementType("DELEGATED_SUPER_TYPE_ENTRY",KtDelegatedSuperTypeEntry ::class)

val SUPER_TYPE_CALL_ENTRY: KtPlaceHolderStubElementType<KtSuperTypeCallEntry> = KtPlaceHolderStubElementType("SUPER_TYPE_CALL_ENTRY",KtSuperTypeCallEntry ::class)

val SUPER_TYPE_ENTRY: KtPlaceHolderStubElementType<KtSuperTypeEntry> = KtPlaceHolderStubElementType("SUPER_TYPE_ENTRY",KtSuperTypeEntry ::class)

val CONSTRUCTOR_CALLEE: KtPlaceHolderStubElementType<KtConstructorCalleeExpression> = KtPlaceHolderStubElementType("CONSTRUCTOR_CALLEE",KtConstructorCalleeExpression ::class)

val CONTEXT_RECEIVER: KtContextReceiverElementType = KtContextReceiverElementType("CONTEXT_RECEIVER")

// Preserve the original Java raw/unchecked legacy subtype constructor binding.
@Suppress("UNCHECKED_CAST")
val CONTEXT_PARAMETER_LIST: KtPlaceHolderStubElementType<KtContextParameterList> =
    KtPlaceHolderStubElementType("CONTEXT_PARAMETER_LIST",KtContextReceiverList ::class) as KtPlaceHolderStubElementType<KtContextParameterList>

val NULL: KtConstantExpressionElementType = KtConstantExpressionElementType("NULL")

val BOOLEAN_CONSTANT: KtConstantExpressionElementType = KtConstantExpressionElementType("BOOLEAN_CONSTANT")

val FLOAT_CONSTANT: KtConstantExpressionElementType = KtConstantExpressionElementType("FLOAT_CONSTANT")

val CHARACTER_CONSTANT: KtConstantExpressionElementType = KtConstantExpressionElementType("CHARACTER_CONSTANT")

val INTEGER_CONSTANT: KtConstantExpressionElementType = KtConstantExpressionElementType("INTEGER_CONSTANT")

val CLASS_LITERAL_EXPRESSION: KtClassLiteralExpressionElementType = KtClassLiteralExpressionElementType("CLASS_LITERAL_EXPRESSION")

val COLLECTION_LITERAL_EXPRESSION: KtCollectionLiteralExpressionElementType = KtCollectionLiteralExpressionElementType("COLLECTION_LITERAL_EXPRESSION")

val STRING_TEMPLATE: KtPlaceHolderStubElementType<KtStringTemplateExpression> = KtStringTemplateExpressionElementType("STRING_TEMPLATE")

val LONG_STRING_TEMPLATE_ENTRY: KtBlockStringTemplateEntryElementType = KtBlockStringTemplateEntryElementType("LONG_STRING_TEMPLATE_ENTRY")

val SHORT_STRING_TEMPLATE_ENTRY: KtPlaceHolderWithTextStubElementType<KtSimpleNameStringTemplateEntry> = KtPlaceHolderWithTextStubElementType("SHORT_STRING_TEMPLATE_ENTRY",KtSimpleNameStringTemplateEntry ::class)

val LITERAL_STRING_TEMPLATE_ENTRY: KtPlaceHolderWithTextStubElementType<KtLiteralStringTemplateEntry> = KtPlaceHolderWithTextStubElementType("LITERAL_STRING_TEMPLATE_ENTRY",KtLiteralStringTemplateEntry ::class)

val ESCAPE_STRING_TEMPLATE_ENTRY: KtPlaceHolderWithTextStubElementType<KtEscapeStringTemplateEntry> = KtPlaceHolderWithTextStubElementType("ESCAPE_STRING_TEMPLATE_ENTRY",KtEscapeStringTemplateEntry ::class)

val SCRIPT: KtScriptElementType = KtScriptElementType("SCRIPT")

val STRING_INTERPOLATION_PREFIX: KtStringInterpolationPrefixElementType = KtStringInterpolationPrefixElementType("STRING_INTERPOLATION_PREFIX")
}
}
