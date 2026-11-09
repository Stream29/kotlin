package org.jetbrains.kotlin.psi.stubs.impl

import kotlin.reflect.KClass

internal class NativeStubProperty(val name: String, val read: (KotlinStubBaseImpl<*>) -> Any?)
internal class NativeStubInterface(val type: KClass<*>, val properties: List<NativeStubProperty>)
internal object NativeStubRenderMetadata {
    private val entries: Map<KClass<*>, NativeStubInterface> = mapOf(
        org.jetbrains.kotlin.psi.stubs.impl.KotlinAnnotationEntryStubImpl::class to NativeStubInterface(org.jetbrains.kotlin.psi.stubs.KotlinAnnotationEntryStub::class, listOf(
            NativeStubProperty("hasValueArguments") { (it as org.jetbrains.kotlin.psi.stubs.KotlinAnnotationEntryStub).hasValueArguments },
            NativeStubProperty("shortName") { (it as org.jetbrains.kotlin.psi.stubs.KotlinAnnotationEntryStub).shortName },
        )),
        org.jetbrains.kotlin.psi.stubs.impl.KotlinBackingFieldStubImpl::class to NativeStubInterface(org.jetbrains.kotlin.psi.stubs.KotlinBackingFieldStub::class, listOf(
            NativeStubProperty("hasInitializer") { (it as org.jetbrains.kotlin.psi.stubs.KotlinBackingFieldStub).hasInitializer },
        )),
        org.jetbrains.kotlin.psi.stubs.impl.KotlinBlockStringTemplateEntryStubImpl::class to NativeStubInterface(org.jetbrains.kotlin.psi.stubs.KotlinBlockStringTemplateEntryStub::class, listOf(
            NativeStubProperty("hasMultipleExpressions") { (it as org.jetbrains.kotlin.psi.stubs.KotlinBlockStringTemplateEntryStub).hasMultipleExpressions },
            NativeStubProperty("text") { (it as org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderWithTextStub<*>).text },
        )),
        org.jetbrains.kotlin.psi.stubs.impl.KotlinClassLiteralExpressionStubImpl::class to NativeStubInterface(org.jetbrains.kotlin.psi.stubs.KotlinClassLiteralExpressionStub::class, listOf(
        )),
        org.jetbrains.kotlin.psi.stubs.impl.KotlinClassStubImpl::class to NativeStubInterface(org.jetbrains.kotlin.psi.stubs.KotlinClassStub::class, listOf(
            NativeStubProperty("isInterface") { (it as org.jetbrains.kotlin.psi.stubs.KotlinClassStub).isInterface },
            NativeStubProperty("isClsStubCompiledToJvmDefaultImplementation") { (it as org.jetbrains.kotlin.psi.stubs.KotlinClassStub).isClsStubCompiledToJvmDefaultImplementation },
            NativeStubProperty("isLocal") { (it as org.jetbrains.kotlin.psi.stubs.KotlinClassOrObjectStub<*>).isLocal },
            NativeStubProperty("isTopLevel") { (it as org.jetbrains.kotlin.psi.stubs.KotlinClassOrObjectStub<*>).isTopLevel },
            NativeStubProperty("kdocText") { (it as org.jetbrains.kotlin.psi.stubs.KotlinClassOrObjectStub<*>).kdocText },
            NativeStubProperty("superNames") { (it as org.jetbrains.kotlin.psi.stubs.KotlinClassOrObjectStub<*>).superNames },
            NativeStubProperty("classId") { (it as org.jetbrains.kotlin.psi.stubs.KotlinClassifierStub<*>).classId },
            NativeStubProperty("fqName") { (it as org.jetbrains.kotlin.psi.stubs.KotlinStubWithFqName<*>).fqName },
            NativeStubProperty("name") { (it as com.intellij.psi.stubs.NamedStub<*>).name },
        )),
        org.jetbrains.kotlin.psi.stubs.impl.KotlinCollectionLiteralExpressionStubImpl::class to NativeStubInterface(org.jetbrains.kotlin.psi.stubs.KotlinCollectionLiteralExpressionStub::class, listOf(
            NativeStubProperty("innerExpressionCount") { (it as org.jetbrains.kotlin.psi.stubs.KotlinCollectionLiteralExpressionStub).innerExpressionCount },
        )),
        org.jetbrains.kotlin.psi.stubs.impl.KotlinConstantExpressionStubImpl::class to NativeStubInterface(org.jetbrains.kotlin.psi.stubs.KotlinConstantExpressionStub::class, listOf(
            NativeStubProperty("value") { (it as org.jetbrains.kotlin.psi.stubs.KotlinConstantExpressionStub).value },
            NativeStubProperty("kind") { (it as org.jetbrains.kotlin.psi.stubs.KotlinConstantExpressionStub).kind },
        )),
        org.jetbrains.kotlin.psi.stubs.impl.KotlinContextReceiverStubImpl::class to NativeStubInterface(org.jetbrains.kotlin.psi.stubs.KotlinContextReceiverStub::class, listOf(
            NativeStubProperty("label") { (it as org.jetbrains.kotlin.psi.stubs.KotlinContextReceiverStub).label },
        )),
        org.jetbrains.kotlin.psi.stubs.impl.KotlinContractEffectStubImpl::class to NativeStubInterface(org.jetbrains.kotlin.psi.stubs.KotlinContractEffectStub::class, listOf(
        )),
        org.jetbrains.kotlin.psi.stubs.impl.KotlinDestructuringDeclarationStubImpl::class to NativeStubInterface(org.jetbrains.kotlin.psi.stubs.KotlinDestructuringDeclarationStub::class, listOf(
            NativeStubProperty("hasInitializer") { (it as org.jetbrains.kotlin.psi.stubs.KotlinDestructuringDeclarationStub).hasInitializer },
            NativeStubProperty("isVar") { (it as org.jetbrains.kotlin.psi.stubs.KotlinDestructuringDeclarationStub).isVar },
        )),
        org.jetbrains.kotlin.psi.stubs.impl.KotlinEnumEntryStubImpl::class to NativeStubInterface(org.jetbrains.kotlin.psi.stubs.KotlinClassStub::class, listOf(
            NativeStubProperty("isInterface") { (it as org.jetbrains.kotlin.psi.stubs.KotlinClassStub).isInterface },
            NativeStubProperty("isClsStubCompiledToJvmDefaultImplementation") { (it as org.jetbrains.kotlin.psi.stubs.KotlinClassStub).isClsStubCompiledToJvmDefaultImplementation },
            NativeStubProperty("isLocal") { (it as org.jetbrains.kotlin.psi.stubs.KotlinClassOrObjectStub<*>).isLocal },
            NativeStubProperty("isTopLevel") { (it as org.jetbrains.kotlin.psi.stubs.KotlinClassOrObjectStub<*>).isTopLevel },
            NativeStubProperty("kdocText") { (it as org.jetbrains.kotlin.psi.stubs.KotlinClassOrObjectStub<*>).kdocText },
            NativeStubProperty("superNames") { (it as org.jetbrains.kotlin.psi.stubs.KotlinClassOrObjectStub<*>).superNames },
            NativeStubProperty("classId") { (it as org.jetbrains.kotlin.psi.stubs.KotlinClassifierStub<*>).classId },
            NativeStubProperty("fqName") { (it as org.jetbrains.kotlin.psi.stubs.KotlinStubWithFqName<*>).fqName },
            NativeStubProperty("name") { (it as com.intellij.psi.stubs.NamedStub<*>).name },
        )),
        org.jetbrains.kotlin.psi.stubs.impl.KotlinEnumEntrySuperclassReferenceExpressionStubImpl::class to NativeStubInterface(org.jetbrains.kotlin.psi.stubs.KotlinEnumEntrySuperclassReferenceExpressionStub::class, listOf(
            NativeStubProperty("referencedName") { (it as org.jetbrains.kotlin.psi.stubs.KotlinEnumEntrySuperclassReferenceExpressionStub).referencedName },
        )),
        org.jetbrains.kotlin.psi.stubs.impl.KotlinFunctionStubImpl::class to NativeStubInterface(org.jetbrains.kotlin.psi.stubs.KotlinFunctionStub::class, listOf(
            NativeStubProperty("hasTypeParameterListBeforeFunctionName") { (it as org.jetbrains.kotlin.psi.stubs.KotlinFunctionStub).hasTypeParameterListBeforeFunctionName },
            NativeStubProperty("isExtension") { (it as org.jetbrains.kotlin.psi.stubs.KotlinCallableStubBase<*>).isExtension },
            NativeStubProperty("isTopLevel") { (it as org.jetbrains.kotlin.psi.stubs.KotlinCallableStubBase<*>).isTopLevel },
            NativeStubProperty("kdocText") { (it as org.jetbrains.kotlin.psi.stubs.KotlinCallableStubBase<*>).kdocText },
            NativeStubProperty("fqName") { (it as org.jetbrains.kotlin.psi.stubs.KotlinStubWithFqName<*>).fqName },
            NativeStubProperty("name") { (it as com.intellij.psi.stubs.NamedStub<*>).name },
            NativeStubProperty("mayHaveContract") { (it as org.jetbrains.kotlin.psi.stubs.KotlinDeclarationWithBodyStub<*>).mayHaveContract },
            NativeStubProperty("hasNoExpressionBody") { (it as org.jetbrains.kotlin.psi.stubs.KotlinDeclarationWithBodyStub<*>).hasNoExpressionBody },
            NativeStubProperty("hasBody") { (it as org.jetbrains.kotlin.psi.stubs.KotlinDeclarationWithBodyStub<*>).hasBody },
        )),
        org.jetbrains.kotlin.psi.stubs.impl.KotlinFunctionTypeStubImpl::class to NativeStubInterface(org.jetbrains.kotlin.psi.stubs.KotlinFunctionTypeStub::class, listOf(
        )),
        org.jetbrains.kotlin.psi.stubs.impl.KotlinImportAliasStubImpl::class to NativeStubInterface(org.jetbrains.kotlin.psi.stubs.KotlinImportAliasStub::class, listOf(
            NativeStubProperty("name") { (it as com.intellij.psi.stubs.NamedStub<*>).name },
        )),
        org.jetbrains.kotlin.psi.stubs.impl.KotlinImportDirectiveStubImpl::class to NativeStubInterface(org.jetbrains.kotlin.psi.stubs.KotlinImportDirectiveStub::class, listOf(
            NativeStubProperty("isValid") { (it as org.jetbrains.kotlin.psi.stubs.KotlinImportDirectiveStub).isValid },
            NativeStubProperty("isAllUnder") { (it as org.jetbrains.kotlin.psi.stubs.KotlinImportDirectiveStub).isAllUnder },
            NativeStubProperty("importedFqName") { (it as org.jetbrains.kotlin.psi.stubs.KotlinImportDirectiveStub).importedFqName },
        )),
        org.jetbrains.kotlin.psi.stubs.impl.KotlinModifierListStubImpl::class to NativeStubInterface(org.jetbrains.kotlin.psi.stubs.KotlinModifierListStub::class, listOf(
        )),
        org.jetbrains.kotlin.psi.stubs.impl.KotlinNameReferenceExpressionStubImpl::class to NativeStubInterface(org.jetbrains.kotlin.psi.stubs.KotlinNameReferenceExpressionStub::class, listOf(
            NativeStubProperty("referencedName") { (it as org.jetbrains.kotlin.psi.stubs.KotlinNameReferenceExpressionStub).referencedName },
        )),
        org.jetbrains.kotlin.psi.stubs.impl.KotlinObjectStubImpl::class to NativeStubInterface(org.jetbrains.kotlin.psi.stubs.KotlinObjectStub::class, listOf(
            NativeStubProperty("isObjectLiteral") { (it as org.jetbrains.kotlin.psi.stubs.KotlinObjectStub).isObjectLiteral },
            NativeStubProperty("isLocal") { (it as org.jetbrains.kotlin.psi.stubs.KotlinClassOrObjectStub<*>).isLocal },
            NativeStubProperty("isTopLevel") { (it as org.jetbrains.kotlin.psi.stubs.KotlinClassOrObjectStub<*>).isTopLevel },
            NativeStubProperty("kdocText") { (it as org.jetbrains.kotlin.psi.stubs.KotlinClassOrObjectStub<*>).kdocText },
            NativeStubProperty("superNames") { (it as org.jetbrains.kotlin.psi.stubs.KotlinClassOrObjectStub<*>).superNames },
            NativeStubProperty("classId") { (it as org.jetbrains.kotlin.psi.stubs.KotlinClassifierStub<*>).classId },
            NativeStubProperty("fqName") { (it as org.jetbrains.kotlin.psi.stubs.KotlinStubWithFqName<*>).fqName },
            NativeStubProperty("name") { (it as com.intellij.psi.stubs.NamedStub<*>).name },
        )),
        org.jetbrains.kotlin.psi.stubs.impl.KotlinParameterStubImpl::class to NativeStubInterface(org.jetbrains.kotlin.psi.stubs.KotlinParameterStub::class, listOf(
            NativeStubProperty("isMutable") { (it as org.jetbrains.kotlin.psi.stubs.KotlinParameterStub).isMutable },
            NativeStubProperty("hasValOrVar") { (it as org.jetbrains.kotlin.psi.stubs.KotlinParameterStub).hasValOrVar },
            NativeStubProperty("hasDefaultValue") { (it as org.jetbrains.kotlin.psi.stubs.KotlinParameterStub).hasDefaultValue },
            NativeStubProperty("fqName") { (it as org.jetbrains.kotlin.psi.stubs.KotlinStubWithFqName<*>).fqName },
            NativeStubProperty("name") { (it as com.intellij.psi.stubs.NamedStub<*>).name },
        )),
        org.jetbrains.kotlin.psi.stubs.impl.KotlinPlaceHolderStubImpl::class to NativeStubInterface(org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub::class, listOf(
        )),
        org.jetbrains.kotlin.psi.stubs.impl.KotlinPlaceHolderWithTextStubImpl::class to NativeStubInterface(org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderWithTextStub::class, listOf(
            NativeStubProperty("text") { (it as org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderWithTextStub<*>).text },
        )),
        org.jetbrains.kotlin.psi.stubs.impl.KotlinPrimaryConstructorStubImpl::class to NativeStubInterface(org.jetbrains.kotlin.psi.stubs.KotlinConstructorStub::class, listOf(
            NativeStubProperty("isDelegatedCallToThis") { (it as org.jetbrains.kotlin.psi.stubs.KotlinConstructorStub<*>).isDelegatedCallToThis },
            NativeStubProperty("isExplicitDelegationCall") { (it as org.jetbrains.kotlin.psi.stubs.KotlinConstructorStub<*>).isExplicitDelegationCall },
            NativeStubProperty("isExtension") { (it as org.jetbrains.kotlin.psi.stubs.KotlinCallableStubBase<*>).isExtension },
            NativeStubProperty("isTopLevel") { (it as org.jetbrains.kotlin.psi.stubs.KotlinCallableStubBase<*>).isTopLevel },
            NativeStubProperty("kdocText") { (it as org.jetbrains.kotlin.psi.stubs.KotlinCallableStubBase<*>).kdocText },
            NativeStubProperty("fqName") { (it as org.jetbrains.kotlin.psi.stubs.KotlinStubWithFqName<*>).fqName },
            NativeStubProperty("name") { (it as com.intellij.psi.stubs.NamedStub<*>).name },
            NativeStubProperty("mayHaveContract") { (it as org.jetbrains.kotlin.psi.stubs.KotlinDeclarationWithBodyStub<*>).mayHaveContract },
            NativeStubProperty("hasNoExpressionBody") { (it as org.jetbrains.kotlin.psi.stubs.KotlinDeclarationWithBodyStub<*>).hasNoExpressionBody },
            NativeStubProperty("hasBody") { (it as org.jetbrains.kotlin.psi.stubs.KotlinDeclarationWithBodyStub<*>).hasBody },
        )),
        org.jetbrains.kotlin.psi.stubs.impl.KotlinPropertyAccessorStubImpl::class to NativeStubInterface(org.jetbrains.kotlin.psi.stubs.KotlinPropertyAccessorStub::class, listOf(
            NativeStubProperty("isGetter") { (it as org.jetbrains.kotlin.psi.stubs.KotlinPropertyAccessorStub).isGetter },
            NativeStubProperty("mayHaveContract") { (it as org.jetbrains.kotlin.psi.stubs.KotlinDeclarationWithBodyStub<*>).mayHaveContract },
            NativeStubProperty("hasNoExpressionBody") { (it as org.jetbrains.kotlin.psi.stubs.KotlinDeclarationWithBodyStub<*>).hasNoExpressionBody },
            NativeStubProperty("hasBody") { (it as org.jetbrains.kotlin.psi.stubs.KotlinDeclarationWithBodyStub<*>).hasBody },
        )),
        org.jetbrains.kotlin.psi.stubs.impl.KotlinPropertyStubImpl::class to NativeStubInterface(org.jetbrains.kotlin.psi.stubs.KotlinPropertyStub::class, listOf(
            NativeStubProperty("hasInitializer") { (it as org.jetbrains.kotlin.psi.stubs.KotlinPropertyStub).hasInitializer },
            NativeStubProperty("isVar") { (it as org.jetbrains.kotlin.psi.stubs.KotlinPropertyStub).isVar },
            NativeStubProperty("hasDelegate") { (it as org.jetbrains.kotlin.psi.stubs.KotlinPropertyStub).hasDelegate },
            NativeStubProperty("hasDelegateExpression") { (it as org.jetbrains.kotlin.psi.stubs.KotlinPropertyStub).hasDelegateExpression },
            NativeStubProperty("hasReturnTypeRef") { (it as org.jetbrains.kotlin.psi.stubs.KotlinPropertyStub).hasReturnTypeRef },
            NativeStubProperty("hasBackingField") { (it as org.jetbrains.kotlin.psi.stubs.KotlinPropertyStub).hasBackingField },
            NativeStubProperty("isExtension") { (it as org.jetbrains.kotlin.psi.stubs.KotlinCallableStubBase<*>).isExtension },
            NativeStubProperty("isTopLevel") { (it as org.jetbrains.kotlin.psi.stubs.KotlinCallableStubBase<*>).isTopLevel },
            NativeStubProperty("kdocText") { (it as org.jetbrains.kotlin.psi.stubs.KotlinCallableStubBase<*>).kdocText },
            NativeStubProperty("fqName") { (it as org.jetbrains.kotlin.psi.stubs.KotlinStubWithFqName<*>).fqName },
            NativeStubProperty("name") { (it as com.intellij.psi.stubs.NamedStub<*>).name },
        )),
        org.jetbrains.kotlin.psi.stubs.impl.KotlinScriptStubImpl::class to NativeStubInterface(org.jetbrains.kotlin.psi.stubs.KotlinScriptStub::class, listOf(
            NativeStubProperty("fqName") { (it as org.jetbrains.kotlin.psi.stubs.KotlinScriptStub).fqName },
            NativeStubProperty("isReplSnippet") { (it as org.jetbrains.kotlin.psi.stubs.KotlinScriptStub).isReplSnippet },
            NativeStubProperty("fqName") { (it as org.jetbrains.kotlin.psi.stubs.KotlinStubWithFqName<*>).fqName },
            NativeStubProperty("name") { (it as com.intellij.psi.stubs.NamedStub<*>).name },
        )),
        org.jetbrains.kotlin.psi.stubs.impl.KotlinSecondaryConstructorStubImpl::class to NativeStubInterface(org.jetbrains.kotlin.psi.stubs.KotlinConstructorStub::class, listOf(
            NativeStubProperty("isDelegatedCallToThis") { (it as org.jetbrains.kotlin.psi.stubs.KotlinConstructorStub<*>).isDelegatedCallToThis },
            NativeStubProperty("isExplicitDelegationCall") { (it as org.jetbrains.kotlin.psi.stubs.KotlinConstructorStub<*>).isExplicitDelegationCall },
            NativeStubProperty("isExtension") { (it as org.jetbrains.kotlin.psi.stubs.KotlinCallableStubBase<*>).isExtension },
            NativeStubProperty("isTopLevel") { (it as org.jetbrains.kotlin.psi.stubs.KotlinCallableStubBase<*>).isTopLevel },
            NativeStubProperty("kdocText") { (it as org.jetbrains.kotlin.psi.stubs.KotlinCallableStubBase<*>).kdocText },
            NativeStubProperty("fqName") { (it as org.jetbrains.kotlin.psi.stubs.KotlinStubWithFqName<*>).fqName },
            NativeStubProperty("name") { (it as com.intellij.psi.stubs.NamedStub<*>).name },
            NativeStubProperty("mayHaveContract") { (it as org.jetbrains.kotlin.psi.stubs.KotlinDeclarationWithBodyStub<*>).mayHaveContract },
            NativeStubProperty("hasNoExpressionBody") { (it as org.jetbrains.kotlin.psi.stubs.KotlinDeclarationWithBodyStub<*>).hasNoExpressionBody },
            NativeStubProperty("hasBody") { (it as org.jetbrains.kotlin.psi.stubs.KotlinDeclarationWithBodyStub<*>).hasBody },
        )),
        org.jetbrains.kotlin.psi.stubs.impl.KotlinStringInterpolationPrefixStubImpl::class to NativeStubInterface(org.jetbrains.kotlin.psi.stubs.KotlinStringInterpolationPrefixStub::class, listOf(
            NativeStubProperty("dollarSignCount") { (it as org.jetbrains.kotlin.psi.stubs.KotlinStringInterpolationPrefixStub).dollarSignCount },
        )),
        org.jetbrains.kotlin.psi.stubs.impl.KotlinTypeAliasStubImpl::class to NativeStubInterface(org.jetbrains.kotlin.psi.stubs.KotlinTypeAliasStub::class, listOf(
            NativeStubProperty("isTopLevel") { (it as org.jetbrains.kotlin.psi.stubs.KotlinTypeAliasStub).isTopLevel },
            NativeStubProperty("classId") { (it as org.jetbrains.kotlin.psi.stubs.KotlinClassifierStub<*>).classId },
            NativeStubProperty("fqName") { (it as org.jetbrains.kotlin.psi.stubs.KotlinStubWithFqName<*>).fqName },
            NativeStubProperty("name") { (it as com.intellij.psi.stubs.NamedStub<*>).name },
        )),
        org.jetbrains.kotlin.psi.stubs.impl.KotlinTypeParameterStubImpl::class to NativeStubInterface(org.jetbrains.kotlin.psi.stubs.KotlinTypeParameterStub::class, listOf(
            NativeStubProperty("fqName") { (it as org.jetbrains.kotlin.psi.stubs.KotlinStubWithFqName<*>).fqName },
            NativeStubProperty("name") { (it as com.intellij.psi.stubs.NamedStub<*>).name },
        )),
        org.jetbrains.kotlin.psi.stubs.impl.KotlinTypeProjectionStubImpl::class to NativeStubInterface(org.jetbrains.kotlin.psi.stubs.KotlinTypeProjectionStub::class, listOf(
            NativeStubProperty("projectionKind") { (it as org.jetbrains.kotlin.psi.stubs.KotlinTypeProjectionStub).projectionKind },
        )),
        org.jetbrains.kotlin.psi.stubs.impl.KotlinUserTypeStubImpl::class to NativeStubInterface(org.jetbrains.kotlin.psi.stubs.KotlinUserTypeStub::class, listOf(
        )),
        org.jetbrains.kotlin.psi.stubs.impl.KotlinValueArgumentStubImpl::class to NativeStubInterface(org.jetbrains.kotlin.psi.stubs.KotlinValueArgumentStub::class, listOf(
            NativeStubProperty("isSpread") { (it as org.jetbrains.kotlin.psi.stubs.KotlinValueArgumentStub<*>).isSpread },
        )),
    )
    fun get(type: KClass<*>): NativeStubInterface = entries[type]
        ?: error("Missing original stub-interface metadata for $type")
}
