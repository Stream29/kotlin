package org.jetbrains.kotlin.psi.stubs.elements

import com.intellij.openapi.Disposable
import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.stubs.NativeStubHolderField
import com.intellij.psi.stubs.NativeStubHolderMetadata
import com.intellij.psi.stubs.NativeStubHolderRegistry

internal object KotlinStubHolderMetadata {
    const val binaryName = "org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes"
    private val fields: List<NativeStubHolderField> = listOf(
        NativeStubHolderField("CLASS", org.jetbrains.kotlin.psi.stubs.elements.KtClassElementType::class, false) { KtStubElementTypes.CLASS },
        NativeStubHolderField("FUNCTION", org.jetbrains.kotlin.psi.stubs.elements.KtFunctionElementType::class, false) { KtStubElementTypes.FUNCTION },
        NativeStubHolderField("PROPERTY", org.jetbrains.kotlin.psi.stubs.elements.KtPropertyElementType::class, false) { KtStubElementTypes.PROPERTY },
        NativeStubHolderField("PROPERTY_ACCESSOR", org.jetbrains.kotlin.psi.stubs.elements.KtPropertyAccessorElementType::class, false) { KtStubElementTypes.PROPERTY_ACCESSOR },
        NativeStubHolderField("BACKING_FIELD", org.jetbrains.kotlin.psi.stubs.elements.KtBackingFieldElementType::class, false) { KtStubElementTypes.BACKING_FIELD },
        NativeStubHolderField("DESTRUCTURING_DECLARATION", org.jetbrains.kotlin.psi.stubs.elements.KtDestructuringDeclarationElementType::class, false) { KtStubElementTypes.DESTRUCTURING_DECLARATION },
        NativeStubHolderField("TYPEALIAS", org.jetbrains.kotlin.psi.stubs.elements.KtTypeAliasElementType::class, false) { KtStubElementTypes.TYPEALIAS },
        NativeStubHolderField("ENUM_ENTRY", org.jetbrains.kotlin.psi.stubs.elements.KtEnumEntryElementType::class, false) { KtStubElementTypes.ENUM_ENTRY },
        NativeStubHolderField("OBJECT_DECLARATION", org.jetbrains.kotlin.psi.stubs.elements.KtObjectElementType::class, false) { KtStubElementTypes.OBJECT_DECLARATION },
        NativeStubHolderField("CLASS_INITIALIZER", org.jetbrains.kotlin.psi.stubs.elements.KtPlaceHolderStubElementType::class, false) { KtStubElementTypes.CLASS_INITIALIZER },
        NativeStubHolderField("SCRIPT_INITIALIZER", org.jetbrains.kotlin.psi.stubs.elements.KtPlaceHolderStubElementType::class, false) { KtStubElementTypes.SCRIPT_INITIALIZER },
        NativeStubHolderField("SECONDARY_CONSTRUCTOR", org.jetbrains.kotlin.psi.stubs.elements.KtSecondaryConstructorElementType::class, false) { KtStubElementTypes.SECONDARY_CONSTRUCTOR },
        NativeStubHolderField("PRIMARY_CONSTRUCTOR", org.jetbrains.kotlin.psi.stubs.elements.KtPrimaryConstructorElementType::class, false) { KtStubElementTypes.PRIMARY_CONSTRUCTOR },
        NativeStubHolderField("VALUE_PARAMETER", org.jetbrains.kotlin.psi.stubs.elements.KtParameterElementType::class, false) { KtStubElementTypes.VALUE_PARAMETER },
        NativeStubHolderField("VALUE_PARAMETER_LIST", org.jetbrains.kotlin.psi.stubs.elements.KtPlaceHolderStubElementType::class, false) { KtStubElementTypes.VALUE_PARAMETER_LIST },
        NativeStubHolderField("TYPE_PARAMETER", org.jetbrains.kotlin.psi.stubs.elements.KtTypeParameterElementType::class, false) { KtStubElementTypes.TYPE_PARAMETER },
        NativeStubHolderField("TYPE_PARAMETER_LIST", org.jetbrains.kotlin.psi.stubs.elements.KtPlaceHolderStubElementType::class, false) { KtStubElementTypes.TYPE_PARAMETER_LIST },
        NativeStubHolderField("ANNOTATION_ENTRY", org.jetbrains.kotlin.psi.stubs.elements.KtAnnotationEntryElementType::class, false) { KtStubElementTypes.ANNOTATION_ENTRY },
        NativeStubHolderField("ANNOTATION", org.jetbrains.kotlin.psi.stubs.elements.KtPlaceHolderStubElementType::class, false) { KtStubElementTypes.ANNOTATION },
        NativeStubHolderField("ANNOTATION_TARGET", org.jetbrains.kotlin.psi.stubs.elements.KtAnnotationUseSiteTargetElementType::class, false) { KtStubElementTypes.ANNOTATION_TARGET },
        NativeStubHolderField("CLASS_BODY", org.jetbrains.kotlin.psi.stubs.elements.KtPlaceHolderStubElementType::class, false) { KtStubElementTypes.CLASS_BODY },
        NativeStubHolderField("COMPANION_BLOCK", org.jetbrains.kotlin.psi.stubs.elements.KtPlaceHolderStubElementType::class, false) { KtStubElementTypes.COMPANION_BLOCK },
        NativeStubHolderField("IMPORT_LIST", org.jetbrains.kotlin.psi.stubs.elements.KtPlaceHolderStubElementType::class, false) { KtStubElementTypes.IMPORT_LIST },
        NativeStubHolderField("FILE_ANNOTATION_LIST", org.jetbrains.kotlin.psi.stubs.elements.KtPlaceHolderStubElementType::class, false) { KtStubElementTypes.FILE_ANNOTATION_LIST },
        NativeStubHolderField("IMPORT_DIRECTIVE", org.jetbrains.kotlin.psi.stubs.elements.KtImportDirectiveElementType::class, false) { KtStubElementTypes.IMPORT_DIRECTIVE },
        NativeStubHolderField("IMPORT_ALIAS", org.jetbrains.kotlin.psi.stubs.elements.KtImportAliasElementType::class, false) { KtStubElementTypes.IMPORT_ALIAS },
        NativeStubHolderField("PACKAGE_DIRECTIVE", org.jetbrains.kotlin.psi.stubs.elements.KtPlaceHolderStubElementType::class, false) { KtStubElementTypes.PACKAGE_DIRECTIVE },
        NativeStubHolderField("MODIFIER_LIST", org.jetbrains.kotlin.psi.stubs.elements.KtModifierListElementType::class, false) { KtStubElementTypes.MODIFIER_LIST },
        NativeStubHolderField("TYPE_CONSTRAINT_LIST", org.jetbrains.kotlin.psi.stubs.elements.KtPlaceHolderStubElementType::class, false) { KtStubElementTypes.TYPE_CONSTRAINT_LIST },
        NativeStubHolderField("TYPE_CONSTRAINT", org.jetbrains.kotlin.psi.stubs.elements.KtPlaceHolderStubElementType::class, false) { KtStubElementTypes.TYPE_CONSTRAINT },
        NativeStubHolderField("NULLABLE_TYPE", org.jetbrains.kotlin.psi.stubs.elements.KtPlaceHolderStubElementType::class, false) { KtStubElementTypes.NULLABLE_TYPE },
        NativeStubHolderField("INTERSECTION_TYPE", org.jetbrains.kotlin.psi.stubs.elements.KtPlaceHolderStubElementType::class, false) { KtStubElementTypes.INTERSECTION_TYPE },
        NativeStubHolderField("TYPE_REFERENCE", org.jetbrains.kotlin.psi.stubs.elements.KtPlaceHolderStubElementType::class, false) { KtStubElementTypes.TYPE_REFERENCE },
        NativeStubHolderField("USER_TYPE", org.jetbrains.kotlin.psi.stubs.elements.KtUserTypeElementType::class, false) { KtStubElementTypes.USER_TYPE },
        NativeStubHolderField("DYNAMIC_TYPE", org.jetbrains.kotlin.psi.stubs.elements.KtPlaceHolderStubElementType::class, false) { KtStubElementTypes.DYNAMIC_TYPE },
        NativeStubHolderField("FUNCTION_TYPE", org.jetbrains.kotlin.psi.stubs.elements.KtFunctionTypeElementType::class, false) { KtStubElementTypes.FUNCTION_TYPE },
        NativeStubHolderField("TYPE_CODE_FRAGMENT", org.jetbrains.kotlin.psi.KtTypeCodeFragmentType::class, false) { KtStubElementTypes.TYPE_CODE_FRAGMENT },
        NativeStubHolderField("EXPRESSION_CODE_FRAGMENT", org.jetbrains.kotlin.psi.KtExpressionCodeFragmentType::class, false) { KtStubElementTypes.EXPRESSION_CODE_FRAGMENT },
        NativeStubHolderField("BLOCK_CODE_FRAGMENT", org.jetbrains.kotlin.psi.KtBlockCodeFragmentType::class, false) { KtStubElementTypes.BLOCK_CODE_FRAGMENT },
        NativeStubHolderField("TYPE_PROJECTION", org.jetbrains.kotlin.psi.stubs.elements.KtTypeProjectionElementType::class, false) { KtStubElementTypes.TYPE_PROJECTION },
        NativeStubHolderField("FUNCTION_TYPE_RECEIVER", org.jetbrains.kotlin.psi.stubs.elements.KtPlaceHolderStubElementType::class, false) { KtStubElementTypes.FUNCTION_TYPE_RECEIVER },
        NativeStubHolderField("REFERENCE_EXPRESSION", org.jetbrains.kotlin.psi.stubs.elements.KtNameReferenceExpressionElementType::class, false) { KtStubElementTypes.REFERENCE_EXPRESSION },
        NativeStubHolderField("DOT_QUALIFIED_EXPRESSION", org.jetbrains.kotlin.psi.stubs.elements.KtDotQualifiedExpressionElementType::class, false) { KtStubElementTypes.DOT_QUALIFIED_EXPRESSION },
        NativeStubHolderField("CALL_EXPRESSION", org.jetbrains.kotlin.psi.stubs.elements.KtPlaceHolderStubElementType::class, false) { KtStubElementTypes.CALL_EXPRESSION },
        NativeStubHolderField("ENUM_ENTRY_SUPERCLASS_REFERENCE_EXPRESSION", org.jetbrains.kotlin.psi.stubs.elements.KtEnumEntrySuperClassReferenceExpressionElementType::class, false) { KtStubElementTypes.ENUM_ENTRY_SUPERCLASS_REFERENCE_EXPRESSION },
        NativeStubHolderField("TYPE_ARGUMENT_LIST", org.jetbrains.kotlin.psi.stubs.elements.KtPlaceHolderStubElementType::class, false) { KtStubElementTypes.TYPE_ARGUMENT_LIST },
        NativeStubHolderField("VALUE_ARGUMENT_LIST", org.jetbrains.kotlin.psi.stubs.elements.KtPlaceHolderStubElementType::class, false) { KtStubElementTypes.VALUE_ARGUMENT_LIST },
        NativeStubHolderField("VALUE_ARGUMENT", org.jetbrains.kotlin.psi.stubs.elements.KtValueArgumentElementType::class, false) { KtStubElementTypes.VALUE_ARGUMENT },
        NativeStubHolderField("CONTRACT_EFFECT_LIST", org.jetbrains.kotlin.psi.stubs.elements.KtPlaceHolderStubElementType::class, false) { KtStubElementTypes.CONTRACT_EFFECT_LIST },
        NativeStubHolderField("CONTRACT_EFFECT", org.jetbrains.kotlin.psi.stubs.elements.KtContractEffectElementType::class, false) { KtStubElementTypes.CONTRACT_EFFECT },
        NativeStubHolderField("LAMBDA_ARGUMENT", org.jetbrains.kotlin.psi.stubs.elements.KtValueArgumentElementType::class, false) { KtStubElementTypes.LAMBDA_ARGUMENT },
        NativeStubHolderField("VALUE_ARGUMENT_NAME", org.jetbrains.kotlin.psi.stubs.elements.KtPlaceHolderStubElementType::class, false) { KtStubElementTypes.VALUE_ARGUMENT_NAME },
        NativeStubHolderField("SUPER_TYPE_LIST", org.jetbrains.kotlin.psi.stubs.elements.KtPlaceHolderStubElementType::class, false) { KtStubElementTypes.SUPER_TYPE_LIST },
        NativeStubHolderField("INITIALIZER_LIST", org.jetbrains.kotlin.psi.stubs.elements.KtPlaceHolderStubElementType::class, false) { KtStubElementTypes.INITIALIZER_LIST },
        NativeStubHolderField("DELEGATED_SUPER_TYPE_ENTRY", org.jetbrains.kotlin.psi.stubs.elements.KtPlaceHolderStubElementType::class, false) { KtStubElementTypes.DELEGATED_SUPER_TYPE_ENTRY },
        NativeStubHolderField("SUPER_TYPE_CALL_ENTRY", org.jetbrains.kotlin.psi.stubs.elements.KtPlaceHolderStubElementType::class, false) { KtStubElementTypes.SUPER_TYPE_CALL_ENTRY },
        NativeStubHolderField("SUPER_TYPE_ENTRY", org.jetbrains.kotlin.psi.stubs.elements.KtPlaceHolderStubElementType::class, false) { KtStubElementTypes.SUPER_TYPE_ENTRY },
        NativeStubHolderField("CONSTRUCTOR_CALLEE", org.jetbrains.kotlin.psi.stubs.elements.KtPlaceHolderStubElementType::class, false) { KtStubElementTypes.CONSTRUCTOR_CALLEE },
        NativeStubHolderField("CONTEXT_RECEIVER", org.jetbrains.kotlin.psi.stubs.elements.KtContextReceiverElementType::class, false) { KtStubElementTypes.CONTEXT_RECEIVER },
        NativeStubHolderField("CONTEXT_PARAMETER_LIST", org.jetbrains.kotlin.psi.stubs.elements.KtPlaceHolderStubElementType::class, false) { KtStubElementTypes.CONTEXT_PARAMETER_LIST },
        NativeStubHolderField("NULL", org.jetbrains.kotlin.psi.stubs.elements.KtConstantExpressionElementType::class, false) { KtStubElementTypes.NULL },
        NativeStubHolderField("BOOLEAN_CONSTANT", org.jetbrains.kotlin.psi.stubs.elements.KtConstantExpressionElementType::class, false) { KtStubElementTypes.BOOLEAN_CONSTANT },
        NativeStubHolderField("FLOAT_CONSTANT", org.jetbrains.kotlin.psi.stubs.elements.KtConstantExpressionElementType::class, false) { KtStubElementTypes.FLOAT_CONSTANT },
        NativeStubHolderField("CHARACTER_CONSTANT", org.jetbrains.kotlin.psi.stubs.elements.KtConstantExpressionElementType::class, false) { KtStubElementTypes.CHARACTER_CONSTANT },
        NativeStubHolderField("INTEGER_CONSTANT", org.jetbrains.kotlin.psi.stubs.elements.KtConstantExpressionElementType::class, false) { KtStubElementTypes.INTEGER_CONSTANT },
        NativeStubHolderField("CLASS_LITERAL_EXPRESSION", org.jetbrains.kotlin.psi.stubs.elements.KtClassLiteralExpressionElementType::class, false) { KtStubElementTypes.CLASS_LITERAL_EXPRESSION },
        NativeStubHolderField("COLLECTION_LITERAL_EXPRESSION", org.jetbrains.kotlin.psi.stubs.elements.KtCollectionLiteralExpressionElementType::class, false) { KtStubElementTypes.COLLECTION_LITERAL_EXPRESSION },
        NativeStubHolderField("STRING_TEMPLATE", org.jetbrains.kotlin.psi.stubs.elements.KtPlaceHolderStubElementType::class, false) { KtStubElementTypes.STRING_TEMPLATE },
        NativeStubHolderField("LONG_STRING_TEMPLATE_ENTRY", org.jetbrains.kotlin.psi.stubs.elements.KtBlockStringTemplateEntryElementType::class, false) { KtStubElementTypes.LONG_STRING_TEMPLATE_ENTRY },
        NativeStubHolderField("SHORT_STRING_TEMPLATE_ENTRY", org.jetbrains.kotlin.psi.stubs.elements.KtPlaceHolderWithTextStubElementType::class, false) { KtStubElementTypes.SHORT_STRING_TEMPLATE_ENTRY },
        NativeStubHolderField("LITERAL_STRING_TEMPLATE_ENTRY", org.jetbrains.kotlin.psi.stubs.elements.KtPlaceHolderWithTextStubElementType::class, false) { KtStubElementTypes.LITERAL_STRING_TEMPLATE_ENTRY },
        NativeStubHolderField("ESCAPE_STRING_TEMPLATE_ENTRY", org.jetbrains.kotlin.psi.stubs.elements.KtPlaceHolderWithTextStubElementType::class, false) { KtStubElementTypes.ESCAPE_STRING_TEMPLATE_ENTRY },
        NativeStubHolderField("SCRIPT", org.jetbrains.kotlin.psi.stubs.elements.KtScriptElementType::class, false) { KtStubElementTypes.SCRIPT },
        NativeStubHolderField("STRING_INTERPOLATION_PREFIX", org.jetbrains.kotlin.psi.stubs.elements.KtStringInterpolationPrefixElementType::class, false) { KtStubElementTypes.STRING_INTERPOLATION_PREFIX },
    )
    fun create(): NativeStubHolderMetadata = NativeStubHolderMetadata(
        binaryName, true, fields, { fields.first().read() },
    )
    fun register(plugin: PluginId, lifetime: Disposable) {
        NativeStubHolderRegistry.register(plugin, create(), lifetime)
    }
}
