/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin







import com.intellij.psi.impl.source.tree.ICodeFragmentElementType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IFileElementType
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.stubs.*
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementType



@OptIn(KtImplementationDetail::class)
internal object KtStubBasedElementTypes {
    private val provider = KotlinElementTypeProvider.instance

    val FILE: IFileElementType =
        provider.fileType


    // Classifiers

    val CLASS: KtStubElementType<out KotlinClassStub, KtClass> =
        provider.classType

    val OBJECT_DECLARATION: KtStubElementType<out KotlinObjectStub, KtObjectDeclaration> =
        provider.objectType

    val TYPEALIAS: KtStubElementType<out KotlinTypeAliasStub, KtTypeAlias> =
        provider.typeAliasType

    val CLASS_BODY: KtStubElementType<out KotlinPlaceHolderStub<KtClassBody>, KtClassBody> =
        provider.classBodyType

    @KtExperimentalApi
    val COMPANION_BLOCK: KtStubElementType<out KotlinPlaceHolderStub<KtCompanionBlock>, KtCompanionBlock> =
        provider.companionBlockType


    // Initializers

    val CLASS_INITIALIZER: KtStubElementType<out KotlinPlaceHolderStub<KtClassInitializer>, KtClassInitializer> =
        provider.classInitializerType

    val SCRIPT_INITIALIZER: KtStubElementType<out KotlinPlaceHolderStub<KtScriptInitializer>, KtScriptInitializer> =
        provider.scriptInitializerType


    // Callables

    val FUNCTION: KtStubElementType<out KotlinFunctionStub, KtNamedFunction> =
        provider.functionType

    val PROPERTY: KtStubElementType<out KotlinPropertyStub, KtProperty> =
        provider.propertyType

    val ENUM_ENTRY: KtStubElementType<out KotlinClassStub, KtEnumEntry> =
        provider.enumEntryType

    val PRIMARY_CONSTRUCTOR: KtStubElementType<out KotlinConstructorStub<KtPrimaryConstructor>, KtPrimaryConstructor> =
        provider.primaryConstructorType

    val SECONDARY_CONSTRUCTOR: KtStubElementType<out KotlinConstructorStub<KtSecondaryConstructor>, KtSecondaryConstructor> =
        provider.secondaryConstructorType

    val CONSTRUCTOR_CALLEE: KtStubElementType<out KotlinPlaceHolderStub<KtConstructorCalleeExpression>, KtConstructorCalleeExpression> =
        provider.constructorCalleeType

    val PROPERTY_ACCESSOR: KtStubElementType<out KotlinPropertyAccessorStub, KtPropertyAccessor> =
        provider.propertyAccessorType

    val BACKING_FIELD: KtStubElementType<out KotlinBackingFieldStub, KtBackingField> =
        provider.backingFieldType

    val DESTRUCTURING_DECLARATION: KtStubElementType<out KotlinDestructuringDeclarationStub, KtDestructuringDeclaration> =
        provider.destructuringDeclarationType

    val INITIALIZER_LIST: KtStubElementType<out KotlinPlaceHolderStub<KtInitializerList>, KtInitializerList> =
        provider.initializerListType


    // Value parameters

    val VALUE_PARAMETER_LIST: KtStubElementType<out KotlinPlaceHolderStub<KtParameterList>, KtParameterList> =
        provider.valueParameterListType

    val VALUE_PARAMETER: KtStubElementType<out KotlinParameterStub, KtParameter> =
        provider.valueParameterType

    val CONTEXT_PARAMETER_LIST: KtStubElementType<out KotlinPlaceHolderStub<KtContextParameterList>, KtContextParameterList> =
        provider.contextParameterListType

    val CONTEXT_RECEIVER: KtStubElementType<out KotlinContextReceiverStub, KtContextReceiver> =
        provider.contextReceiverType


    // Type parameters

    val TYPE_PARAMETER_LIST: KtStubElementType<out KotlinPlaceHolderStub<KtTypeParameterList>, KtTypeParameterList> =
        provider.typeParameterListType

    val TYPE_PARAMETER: KtStubElementType<out KotlinTypeParameterStub, KtTypeParameter> =
        provider.typeParameterType

    val TYPE_CONSTRAINT_LIST: KtStubElementType<out KotlinPlaceHolderStub<KtTypeConstraintList>, KtTypeConstraintList> =
        provider.typeConstraintListType

    val TYPE_CONSTRAINT: KtStubElementType<out KotlinPlaceHolderStub<KtTypeConstraint>, KtTypeConstraint> =
        provider.typeConstraintType


    // Supertypes

    val SUPER_TYPE_LIST: KtStubElementType<out KotlinPlaceHolderStub<KtSuperTypeList>, KtSuperTypeList> =
        provider.superTypeListType

    val DELEGATED_SUPER_TYPE_ENTRY: KtStubElementType<out KotlinPlaceHolderStub<KtDelegatedSuperTypeEntry>, KtDelegatedSuperTypeEntry> =
        provider.delegatedSuperTypeEntryType

    val SUPER_TYPE_CALL_ENTRY: KtStubElementType<out KotlinPlaceHolderStub<KtSuperTypeCallEntry>, KtSuperTypeCallEntry> =
        provider.superTypeCallEntryType

    val SUPER_TYPE_ENTRY: KtStubElementType<out KotlinPlaceHolderStub<KtSuperTypeEntry>, KtSuperTypeEntry> =
        provider.superTypeEntryType


    // Modifiers and annotations

    val MODIFIER_LIST: KtStubElementType<out KotlinModifierListStub, KtDeclarationModifierList> =
        provider.modifierListType

    val ANNOTATION: KtStubElementType<out KotlinPlaceHolderStub<KtAnnotation>, KtAnnotation> =
        provider.annotationType

    val ANNOTATION_ENTRY: KtStubElementType<out KotlinAnnotationEntryStub, KtAnnotationEntry> =
        provider.annotationEntryType

    val ANNOTATION_TARGET: KtStubElementType<out KotlinAnnotationUseSiteTargetStub, KtAnnotationUseSiteTarget> =
        provider.annotationTargetType


    // Type references

    val TYPE_REFERENCE: KtStubElementType<out KotlinPlaceHolderStub<KtTypeReference>, KtTypeReference> =
        provider.typeReferenceType

    val USER_TYPE: KtStubElementType<out KotlinUserTypeStub, KtUserType> =
        provider.userTypeType

    val DYNAMIC_TYPE: KtStubElementType<out KotlinPlaceHolderStub<KtDynamicType>, KtDynamicType> =
        provider.dynamicTypeType

    val FUNCTION_TYPE: KtStubElementType<out KotlinFunctionTypeStub, KtFunctionType> =
        provider.functionTypeType

    val FUNCTION_TYPE_RECEIVER: KtStubElementType<out KotlinPlaceHolderStub<KtFunctionTypeReceiver>, KtFunctionTypeReceiver> =
        provider.functionTypeReceiverType

    val NULLABLE_TYPE: KtStubElementType<out KotlinPlaceHolderStub<KtNullableType>, KtNullableType> =
        provider.nullableTypeType

    val INTERSECTION_TYPE: KtStubElementType<out KotlinPlaceHolderStub<KtIntersectionType>, KtIntersectionType> =
        provider.intersectionTypeType

    val TYPE_PROJECTION: KtStubElementType<out KotlinTypeProjectionStub, KtTypeProjection> =
        provider.typeProjectionType


    // Constants

    val NULL: KtStubElementType<out KotlinConstantExpressionStub, KtConstantExpression> =
        provider.nullType

    val BOOLEAN_CONSTANT: KtStubElementType<out KotlinConstantExpressionStub, KtConstantExpression> =
        provider.booleanConstantType

    val FLOAT_CONSTANT: KtStubElementType<out KotlinConstantExpressionStub, KtConstantExpression> =
        provider.floatConstantType

    val CHARACTER_CONSTANT: KtStubElementType<out KotlinConstantExpressionStub, KtConstantExpression> =
        provider.characterConstantType

    val INTEGER_CONSTANT: KtStubElementType<out KotlinConstantExpressionStub, KtConstantExpression> =
        provider.integerConstantType


    // String templates

    val STRING_TEMPLATE: KtStubElementType<out KotlinPlaceHolderStub<KtStringTemplateExpression>, KtStringTemplateExpression> =
        provider.stringTemplateType

    val LONG_STRING_TEMPLATE_ENTRY: KtStubElementType<out KotlinBlockStringTemplateEntryStub, KtBlockStringTemplateEntry> =
        provider.longStringTemplateEntryType

    val SHORT_STRING_TEMPLATE_ENTRY: KtStubElementType<out KotlinPlaceHolderWithTextStub<KtSimpleNameStringTemplateEntry>, KtSimpleNameStringTemplateEntry> =
        provider.shortStringTemplateEntryType

    val LITERAL_STRING_TEMPLATE_ENTRY: KtStubElementType<out KotlinPlaceHolderWithTextStub<KtLiteralStringTemplateEntry>, KtLiteralStringTemplateEntry> =
        provider.literalStringTemplateEntryType

    val ESCAPE_STRING_TEMPLATE_ENTRY: KtStubElementType<out KotlinPlaceHolderWithTextStub<KtEscapeStringTemplateEntry>, KtEscapeStringTemplateEntry> =
        provider.escapeStringTemplateEntryType

    val STRING_INTERPOLATION_PREFIX: KtStubElementType<out KotlinStringInterpolationPrefixStub, KtStringInterpolationPrefix> =
        provider.stringInterpolationPrefixType


    // Expressions

    val BLOCK: IElementType =
        provider.blockExpressionType

    val LAMBDA_EXPRESSION: IElementType =
        provider.lambdaExpressionType

    val REFERENCE_EXPRESSION: KtStubElementType<out KotlinNameReferenceExpressionStub, KtNameReferenceExpression> =
        provider.referenceExpressionType

    val ENUM_ENTRY_SUPERCLASS_REFERENCE_EXPRESSION: KtStubElementType<out KotlinEnumEntrySuperclassReferenceExpressionStub, KtEnumEntrySuperclassReferenceExpression> =
        provider.enumEntrySuperclassReferenceExpressionType

    val DOT_QUALIFIED_EXPRESSION: KtStubElementType<out KotlinPlaceHolderStub<KtDotQualifiedExpression>, KtDotQualifiedExpression> =
        provider.dotQualifiedExpressionType

    val CALL_EXPRESSION: KtStubElementType<out KotlinPlaceHolderStub<KtCallExpression>, KtCallExpression> =
        provider.callExpressionType

    val CLASS_LITERAL_EXPRESSION: KtStubElementType<out KotlinClassLiteralExpressionStub, KtClassLiteralExpression> =
        provider.classLiteralExpressionType

    val COLLECTION_LITERAL_EXPRESSION: KtStubElementType<out KotlinCollectionLiteralExpressionStub, KtCollectionLiteralExpression> =
        provider.collectionLiteralExpressionType


    // Arguments

    val TYPE_ARGUMENT_LIST: KtStubElementType<out KotlinPlaceHolderStub<KtTypeArgumentList>, KtTypeArgumentList> =
        provider.typeArgumentListType

    val VALUE_ARGUMENT_LIST: KtStubElementType<out KotlinPlaceHolderStub<KtValueArgumentList>, KtValueArgumentList> =
        provider.valueArgumentListType

    val VALUE_ARGUMENT: KtStubElementType<out KotlinValueArgumentStub<KtValueArgument>, KtValueArgument> =
        provider.valueArgumentType

    val CONTRACT_EFFECT_LIST: KtStubElementType<out KotlinPlaceHolderStub<KtContractEffectList>, KtContractEffectList> =
        provider.contractEffectListType

    val CONTRACT_EFFECT: KtStubElementType<out KotlinContractEffectStub, KtContractEffect> =
        provider.contractEffectType

    val LAMBDA_ARGUMENT: KtStubElementType<out KotlinValueArgumentStub<KtLambdaArgument>, KtLambdaArgument> =
        provider.lambdaArgumentType

    val VALUE_ARGUMENT_NAME: KtStubElementType<out KotlinPlaceHolderStub<KtValueArgumentName>, KtValueArgumentName> =
        provider.valueArgumentNameType


    // Special

    val PACKAGE_DIRECTIVE: KtStubElementType<out KotlinPlaceHolderStub<KtPackageDirective>, KtPackageDirective> =
        provider.packageDirectiveType

    val FILE_ANNOTATION_LIST: KtStubElementType<out KotlinPlaceHolderStub<KtFileAnnotationList>, KtFileAnnotationList> =
        provider.fileAnnotationListType

    val IMPORT_LIST: KtStubElementType<out KotlinPlaceHolderStub<KtImportList>, KtImportList> =
        provider.importListType

    val IMPORT_DIRECTIVE: KtStubElementType<out KotlinImportDirectiveStub, KtImportDirective> =
        provider.importDirectiveType

    val IMPORT_ALIAS: KtStubElementType<out KotlinImportAliasStub, KtImportAlias> =
        provider.importAliasType

    val SCRIPT: KtStubElementType<out KotlinScriptStub, KtScript> =
        provider.scriptType


    // Code fragments

    val EXPRESSION_CODE_FRAGMENT: ICodeFragmentElementType =
        provider.expressionCodeFragmentType

    val BLOCK_CODE_FRAGMENT: ICodeFragmentElementType =
        provider.blockCodeFragmentType

    val TYPE_CODE_FRAGMENT: ICodeFragmentElementType =
        provider.typeCodeFragmentType
}
