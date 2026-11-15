package org.jetbrains.kotlin.psi.stubs.elements

import com.intellij.openapi.Disposable
import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.stubs.NativeStubHolderField
import com.intellij.psi.stubs.NativeStubHolderMetadata
import com.intellij.psi.stubs.NativeStubHolderRegistry
import org.jetbrains.kotlin.KtNodeType

internal object KotlinStubHolderMetadata {
    const val binaryName = "org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes"
    private val fields: List<NativeStubHolderField> = listOf(
        NativeStubHolderField("CLASS", KtNodeType::class, false) { KtStubElementTypes.CLASS },
        NativeStubHolderField("FUNCTION", KtNodeType::class, false) { KtStubElementTypes.FUNCTION },
        NativeStubHolderField("PROPERTY", KtNodeType::class, false) { KtStubElementTypes.PROPERTY },
        NativeStubHolderField("PROPERTY_ACCESSOR", KtNodeType::class, false) { KtStubElementTypes.PROPERTY_ACCESSOR },
        NativeStubHolderField("BACKING_FIELD", KtNodeType::class, false) { KtStubElementTypes.BACKING_FIELD },
        NativeStubHolderField("DESTRUCTURING_DECLARATION", KtNodeType::class, false) { KtStubElementTypes.DESTRUCTURING_DECLARATION },
        NativeStubHolderField("TYPEALIAS", KtNodeType::class, false) { KtStubElementTypes.TYPEALIAS },
        NativeStubHolderField("ENUM_ENTRY", KtNodeType::class, false) { KtStubElementTypes.ENUM_ENTRY },
        NativeStubHolderField("OBJECT_DECLARATION", KtNodeType::class, false) { KtStubElementTypes.OBJECT_DECLARATION },
        NativeStubHolderField("CLASS_INITIALIZER", KtNodeType::class, false) { KtStubElementTypes.CLASS_INITIALIZER },
        NativeStubHolderField("SCRIPT_INITIALIZER", KtNodeType::class, false) { KtStubElementTypes.SCRIPT_INITIALIZER },
        NativeStubHolderField("SECONDARY_CONSTRUCTOR", KtNodeType::class, false) { KtStubElementTypes.SECONDARY_CONSTRUCTOR },
        NativeStubHolderField("PRIMARY_CONSTRUCTOR", KtNodeType::class, false) { KtStubElementTypes.PRIMARY_CONSTRUCTOR },
        NativeStubHolderField("VALUE_PARAMETER", KtNodeType::class, false) { KtStubElementTypes.VALUE_PARAMETER },
        NativeStubHolderField("VALUE_PARAMETER_LIST", KtNodeType::class, false) { KtStubElementTypes.VALUE_PARAMETER_LIST },
        NativeStubHolderField("TYPE_PARAMETER", KtNodeType::class, false) { KtStubElementTypes.TYPE_PARAMETER },
        NativeStubHolderField("TYPE_PARAMETER_LIST", KtNodeType::class, false) { KtStubElementTypes.TYPE_PARAMETER_LIST },
        NativeStubHolderField("ANNOTATION_ENTRY", KtNodeType::class, false) { KtStubElementTypes.ANNOTATION_ENTRY },
        NativeStubHolderField("ANNOTATION", KtNodeType::class, false) { KtStubElementTypes.ANNOTATION },
        NativeStubHolderField("ANNOTATION_TARGET", KtNodeType::class, false) { KtStubElementTypes.ANNOTATION_TARGET },
        NativeStubHolderField("CLASS_BODY", KtNodeType::class, false) { KtStubElementTypes.CLASS_BODY },
        NativeStubHolderField("COMPANION_BLOCK", KtNodeType::class, false) { KtStubElementTypes.COMPANION_BLOCK },
        NativeStubHolderField("IMPORT_LIST", KtNodeType::class, false) { KtStubElementTypes.IMPORT_LIST },
        NativeStubHolderField("FILE_ANNOTATION_LIST", KtNodeType::class, false) { KtStubElementTypes.FILE_ANNOTATION_LIST },
        NativeStubHolderField("IMPORT_DIRECTIVE", KtNodeType::class, false) { KtStubElementTypes.IMPORT_DIRECTIVE },
        NativeStubHolderField("IMPORT_ALIAS", KtNodeType::class, false) { KtStubElementTypes.IMPORT_ALIAS },
        NativeStubHolderField("PACKAGE_DIRECTIVE", KtNodeType::class, false) { KtStubElementTypes.PACKAGE_DIRECTIVE },
        NativeStubHolderField("MODIFIER_LIST", KtNodeType::class, false) { KtStubElementTypes.MODIFIER_LIST },
        NativeStubHolderField("TYPE_CONSTRAINT_LIST", KtNodeType::class, false) { KtStubElementTypes.TYPE_CONSTRAINT_LIST },
        NativeStubHolderField("TYPE_CONSTRAINT", KtNodeType::class, false) { KtStubElementTypes.TYPE_CONSTRAINT },
        NativeStubHolderField("NULLABLE_TYPE", KtNodeType::class, false) { KtStubElementTypes.NULLABLE_TYPE },
        NativeStubHolderField("INTERSECTION_TYPE", KtNodeType::class, false) { KtStubElementTypes.INTERSECTION_TYPE },
        NativeStubHolderField("UNION_TYPE", KtNodeType::class, false) { KtStubElementTypes.UNION_TYPE },
        NativeStubHolderField("TYPE_REFERENCE", KtNodeType::class, false) { KtStubElementTypes.TYPE_REFERENCE },
        NativeStubHolderField("USER_TYPE", KtNodeType::class, false) { KtStubElementTypes.USER_TYPE },
        NativeStubHolderField("DYNAMIC_TYPE", KtNodeType::class, false) { KtStubElementTypes.DYNAMIC_TYPE },
        NativeStubHolderField("FUNCTION_TYPE", KtNodeType::class, false) { KtStubElementTypes.FUNCTION_TYPE },
        NativeStubHolderField("TYPE_PROJECTION", KtNodeType::class, false) { KtStubElementTypes.TYPE_PROJECTION },
        NativeStubHolderField("FUNCTION_TYPE_RECEIVER", KtNodeType::class, false) { KtStubElementTypes.FUNCTION_TYPE_RECEIVER },
        NativeStubHolderField("REFERENCE_EXPRESSION", KtNodeType::class, false) { KtStubElementTypes.REFERENCE_EXPRESSION },
        NativeStubHolderField("DOT_QUALIFIED_EXPRESSION", KtNodeType::class, false) { KtStubElementTypes.DOT_QUALIFIED_EXPRESSION },
        NativeStubHolderField("CALL_EXPRESSION", KtNodeType::class, false) { KtStubElementTypes.CALL_EXPRESSION },
        NativeStubHolderField("OPERATION_REFERENCE", KtNodeType::class, false) { KtStubElementTypes.OPERATION_REFERENCE },
        NativeStubHolderField("PREFIX_EXPRESSION", KtNodeType::class, false) { KtStubElementTypes.PREFIX_EXPRESSION },
        NativeStubHolderField("POSTFIX_EXPRESSION", KtNodeType::class, false) { KtStubElementTypes.POSTFIX_EXPRESSION },
        NativeStubHolderField("BINARY_EXPRESSION", KtNodeType::class, false) { KtStubElementTypes.BINARY_EXPRESSION },
        NativeStubHolderField("PARENTHESIZED", KtNodeType::class, false) { KtStubElementTypes.PARENTHESIZED },
        NativeStubHolderField("OBJECT_LITERAL", KtNodeType::class, false) { KtStubElementTypes.OBJECT_LITERAL },
        NativeStubHolderField("ENUM_ENTRY_SUPERCLASS_REFERENCE_EXPRESSION", KtNodeType::class, false) { KtStubElementTypes.ENUM_ENTRY_SUPERCLASS_REFERENCE_EXPRESSION },
        NativeStubHolderField("TYPE_ARGUMENT_LIST", KtNodeType::class, false) { KtStubElementTypes.TYPE_ARGUMENT_LIST },
        NativeStubHolderField("VALUE_ARGUMENT_LIST", KtNodeType::class, false) { KtStubElementTypes.VALUE_ARGUMENT_LIST },
        NativeStubHolderField("VALUE_ARGUMENT", KtNodeType::class, false) { KtStubElementTypes.VALUE_ARGUMENT },
        NativeStubHolderField("CONTRACT_EFFECT_LIST", KtNodeType::class, false) { KtStubElementTypes.CONTRACT_EFFECT_LIST },
        NativeStubHolderField("CONTRACT_EFFECT", KtNodeType::class, false) { KtStubElementTypes.CONTRACT_EFFECT },
        NativeStubHolderField("LAMBDA_ARGUMENT", KtNodeType::class, false) { KtStubElementTypes.LAMBDA_ARGUMENT },
        NativeStubHolderField("VALUE_ARGUMENT_NAME", KtNodeType::class, false) { KtStubElementTypes.VALUE_ARGUMENT_NAME },
        NativeStubHolderField("SUPER_TYPE_LIST", KtNodeType::class, false) { KtStubElementTypes.SUPER_TYPE_LIST },
        NativeStubHolderField("INITIALIZER_LIST", KtNodeType::class, false) { KtStubElementTypes.INITIALIZER_LIST },
        NativeStubHolderField("DELEGATED_SUPER_TYPE_ENTRY", KtNodeType::class, false) { KtStubElementTypes.DELEGATED_SUPER_TYPE_ENTRY },
        NativeStubHolderField("SUPER_TYPE_CALL_ENTRY", KtNodeType::class, false) { KtStubElementTypes.SUPER_TYPE_CALL_ENTRY },
        NativeStubHolderField("SUPER_TYPE_ENTRY", KtNodeType::class, false) { KtStubElementTypes.SUPER_TYPE_ENTRY },
        NativeStubHolderField("CONSTRUCTOR_CALLEE", KtNodeType::class, false) { KtStubElementTypes.CONSTRUCTOR_CALLEE },
        NativeStubHolderField("CONTEXT_RECEIVER", KtNodeType::class, false) { KtStubElementTypes.CONTEXT_RECEIVER },
        NativeStubHolderField("CONTEXT_PARAMETER_LIST", KtNodeType::class, false) { KtStubElementTypes.CONTEXT_PARAMETER_LIST },
        NativeStubHolderField("NULL", KtNodeType::class, false) { KtStubElementTypes.NULL },
        NativeStubHolderField("BOOLEAN_CONSTANT", KtNodeType::class, false) { KtStubElementTypes.BOOLEAN_CONSTANT },
        NativeStubHolderField("FLOAT_CONSTANT", KtNodeType::class, false) { KtStubElementTypes.FLOAT_CONSTANT },
        NativeStubHolderField("CHARACTER_CONSTANT", KtNodeType::class, false) { KtStubElementTypes.CHARACTER_CONSTANT },
        NativeStubHolderField("INTEGER_CONSTANT", KtNodeType::class, false) { KtStubElementTypes.INTEGER_CONSTANT },
        NativeStubHolderField("CLASS_LITERAL_EXPRESSION", KtNodeType::class, false) { KtStubElementTypes.CLASS_LITERAL_EXPRESSION },
        NativeStubHolderField("COLLECTION_LITERAL_EXPRESSION", KtNodeType::class, false) { KtStubElementTypes.COLLECTION_LITERAL_EXPRESSION },
        NativeStubHolderField("STRING_TEMPLATE", KtNodeType::class, false) { KtStubElementTypes.STRING_TEMPLATE },
        NativeStubHolderField("LONG_STRING_TEMPLATE_ENTRY", KtNodeType::class, false) { KtStubElementTypes.LONG_STRING_TEMPLATE_ENTRY },
        NativeStubHolderField("SHORT_STRING_TEMPLATE_ENTRY", KtNodeType::class, false) { KtStubElementTypes.SHORT_STRING_TEMPLATE_ENTRY },
        NativeStubHolderField("LITERAL_STRING_TEMPLATE_ENTRY", KtNodeType::class, false) { KtStubElementTypes.LITERAL_STRING_TEMPLATE_ENTRY },
        NativeStubHolderField("ESCAPE_STRING_TEMPLATE_ENTRY", KtNodeType::class, false) { KtStubElementTypes.ESCAPE_STRING_TEMPLATE_ENTRY },
        NativeStubHolderField("SCRIPT", KtNodeType::class, false) { KtStubElementTypes.SCRIPT },
        NativeStubHolderField("STRING_INTERPOLATION_PREFIX", KtNodeType::class, false) { KtStubElementTypes.STRING_INTERPOLATION_PREFIX },
    )
    fun create(): NativeStubHolderMetadata = NativeStubHolderMetadata(
        binaryName, true, fields, { fields.first().readValue() },
    )
    fun register(plugin: PluginId, lifetime: Disposable) {
        NativeStubHolderRegistry.register(plugin, create(), lifetime)
    }
}
