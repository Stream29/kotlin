/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin







import com.intellij.psi.impl.source.tree.ICodeFragmentElementType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IFileElementType
import org.jetbrains.kotlin.psi.KtExperimentalApi
import org.jetbrains.kotlin.psi.KtImplementationDetail



@OptIn(KtImplementationDetail::class)
internal object KtStubBasedElementTypes {
    private val provider = KotlinElementTypeProvider.instance

    val FILE: IFileElementType =
        provider.fileType


    // Classifiers

    val CLASS: KtNodeType = provider.classType

    val OBJECT_DECLARATION: KtNodeType = provider.objectType

    val TYPEALIAS: KtNodeType = provider.typeAliasType

    val CLASS_BODY: KtNodeType = provider.classBodyType

    @KtExperimentalApi
    val COMPANION_BLOCK: KtNodeType = provider.companionBlockType


    // Initializers

    val CLASS_INITIALIZER: KtNodeType = provider.classInitializerType

    val SCRIPT_INITIALIZER: KtNodeType = provider.scriptInitializerType


    // Callables

    val FUNCTION: KtNodeType = provider.functionType

    val PROPERTY: KtNodeType = provider.propertyType

    val ENUM_ENTRY: KtNodeType = provider.enumEntryType

    val PRIMARY_CONSTRUCTOR: KtNodeType = provider.primaryConstructorType

    val SECONDARY_CONSTRUCTOR: KtNodeType = provider.secondaryConstructorType

    val CONSTRUCTOR_CALLEE: KtNodeType = provider.constructorCalleeType

    val PROPERTY_ACCESSOR: KtNodeType = provider.propertyAccessorType

    val BACKING_FIELD: KtNodeType = provider.backingFieldType

    val DESTRUCTURING_DECLARATION: KtNodeType = provider.destructuringDeclarationType

    val INITIALIZER_LIST: KtNodeType = provider.initializerListType


    // Value parameters

    val VALUE_PARAMETER_LIST: KtNodeType = provider.valueParameterListType

    val VALUE_PARAMETER: KtNodeType = provider.valueParameterType

    val CONTEXT_PARAMETER_LIST: KtNodeType = provider.contextParameterListType

    val CONTEXT_RECEIVER: KtNodeType = provider.contextReceiverType


    // Type parameters

    val TYPE_PARAMETER_LIST: KtNodeType = provider.typeParameterListType

    val TYPE_PARAMETER: KtNodeType = provider.typeParameterType

    val TYPE_CONSTRAINT_LIST: KtNodeType = provider.typeConstraintListType

    val TYPE_CONSTRAINT: KtNodeType = provider.typeConstraintType


    // Supertypes

    val SUPER_TYPE_LIST: KtNodeType = provider.superTypeListType

    val DELEGATED_SUPER_TYPE_ENTRY: KtNodeType = provider.delegatedSuperTypeEntryType

    val SUPER_TYPE_CALL_ENTRY: KtNodeType = provider.superTypeCallEntryType

    val SUPER_TYPE_ENTRY: KtNodeType = provider.superTypeEntryType


    // Modifiers and annotations

    val MODIFIER_LIST: KtNodeType = provider.modifierListType

    val ANNOTATION: KtNodeType = provider.annotationType

    val ANNOTATION_ENTRY: KtNodeType = provider.annotationEntryType

    val ANNOTATION_TARGET: KtNodeType = provider.annotationTargetType


    // Type references

    val TYPE_REFERENCE: KtNodeType = provider.typeReferenceType

    val USER_TYPE: KtNodeType = provider.userTypeType

    val DYNAMIC_TYPE: KtNodeType = provider.dynamicTypeType

    val FUNCTION_TYPE: KtNodeType = provider.functionTypeType

    val FUNCTION_TYPE_RECEIVER: KtNodeType = provider.functionTypeReceiverType

    val NULLABLE_TYPE: KtNodeType = provider.nullableTypeType

    val INTERSECTION_TYPE: KtNodeType = provider.intersectionTypeType

    val UNION_TYPE: KtNodeType = provider.unionTypeType

    val TYPE_PROJECTION: KtNodeType = provider.typeProjectionType


    // Constants

    val NULL: KtNodeType = provider.nullType

    val BOOLEAN_CONSTANT: KtNodeType = provider.booleanConstantType

    val FLOAT_CONSTANT: KtNodeType = provider.floatConstantType

    val CHARACTER_CONSTANT: KtNodeType = provider.characterConstantType

    val INTEGER_CONSTANT: KtNodeType = provider.integerConstantType


    // String templates

    val STRING_TEMPLATE: KtNodeType = provider.stringTemplateType

    val LONG_STRING_TEMPLATE_ENTRY: KtNodeType = provider.longStringTemplateEntryType

    val SHORT_STRING_TEMPLATE_ENTRY: KtNodeType = provider.shortStringTemplateEntryType

    val LITERAL_STRING_TEMPLATE_ENTRY: KtNodeType = provider.literalStringTemplateEntryType

    val ESCAPE_STRING_TEMPLATE_ENTRY: KtNodeType = provider.escapeStringTemplateEntryType

    val STRING_INTERPOLATION_PREFIX: KtNodeType = provider.stringInterpolationPrefixType


    // Expressions

    val BLOCK: IElementType =
        provider.blockExpressionType

    val LAMBDA_EXPRESSION: IElementType =
        provider.lambdaExpressionType

    val REFERENCE_EXPRESSION: KtNodeType = provider.referenceExpressionType

    val ENUM_ENTRY_SUPERCLASS_REFERENCE_EXPRESSION: KtNodeType = provider.enumEntrySuperclassReferenceExpressionType

    val OPERATION_REFERENCE: KtNodeType = provider.operationReferenceType

    val DOT_QUALIFIED_EXPRESSION: KtNodeType = provider.dotQualifiedExpressionType

    val CALL_EXPRESSION: KtNodeType = provider.callExpressionType

    val PREFIX_EXPRESSION: KtNodeType = provider.prefixExpressionType

    val POSTFIX_EXPRESSION: KtNodeType = provider.postfixExpressionType

    val BINARY_EXPRESSION: KtNodeType = provider.binaryExpressionType

    val PARENTHESIZED: KtNodeType = provider.parenthesizedExpressionType

    val CLASS_LITERAL_EXPRESSION: KtNodeType = provider.classLiteralExpressionType

    val COLLECTION_LITERAL_EXPRESSION: KtNodeType = provider.collectionLiteralExpressionType

    val OBJECT_LITERAL: KtNodeType = provider.objectLiteralType

    // Arguments

    val TYPE_ARGUMENT_LIST: KtNodeType = provider.typeArgumentListType

    val VALUE_ARGUMENT_LIST: KtNodeType = provider.valueArgumentListType

    val VALUE_ARGUMENT: KtNodeType = provider.valueArgumentType

    val CONTRACT_EFFECT_LIST: KtNodeType = provider.contractEffectListType

    val CONTRACT_EFFECT: KtNodeType = provider.contractEffectType

    val LAMBDA_ARGUMENT: KtNodeType = provider.lambdaArgumentType

    val VALUE_ARGUMENT_NAME: KtNodeType = provider.valueArgumentNameType


    // Special

    val PACKAGE_DIRECTIVE: KtNodeType = provider.packageDirectiveType

    val FILE_ANNOTATION_LIST: KtNodeType = provider.fileAnnotationListType

    val IMPORT_LIST: KtNodeType = provider.importListType

    val IMPORT_DIRECTIVE: KtNodeType = provider.importDirectiveType

    val IMPORT_ALIAS: KtNodeType = provider.importAliasType

    val SCRIPT: KtNodeType = provider.scriptType


    // Code fragments

    val EXPRESSION_CODE_FRAGMENT: ICodeFragmentElementType =
        provider.expressionCodeFragmentType

    val BLOCK_CODE_FRAGMENT: ICodeFragmentElementType =
        provider.blockCodeFragmentType

    val TYPE_CODE_FRAGMENT: ICodeFragmentElementType =
        provider.typeCodeFragmentType
}
