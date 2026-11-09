/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin

import com.intellij.lang.ASTNode
import com.intellij.platform.native.NativeReferenceArrays
import com.intellij.psi.stubs.StubElement
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.stubs.*
import kotlin.reflect.KClass

internal class StaticStubPsiFactory(
    val stubClass: KClass<*>,
    val byNode: (ASTNode) -> KtElementImplStub<*>,
    val byStub: (StubElement<*>) -> KtElementImplStub<*>,
    val arrays: (Int) -> Array<out KtElementImplStub<*>?>,
    val isExpression: Boolean,
)

/** Direct original constructor pairs, typed arrays, and source-inheritance metadata. */
internal object StaticStubPsiConstructors {
    @Suppress("UNCHECKED_CAST")
    private val factories: Map<KClass<*>, StaticStubPsiFactory> = mapOf(
        KtAnnotation::class to StaticStubPsiFactory(
            KotlinPlaceHolderStub::class, { KtAnnotation(it) }, { KtAnnotation(it as KotlinPlaceHolderStub<KtAnnotation>) },
            { NativeReferenceArrays.newArray(KtAnnotation::class, it) }, false,
        ),
        KtAnnotationEntry::class to StaticStubPsiFactory(
            KotlinAnnotationEntryStub::class, { KtAnnotationEntry(it) }, { KtAnnotationEntry(it as KotlinAnnotationEntryStub) },
            { NativeReferenceArrays.newArray(KtAnnotationEntry::class, it) }, false,
        ),
        KtAnnotationUseSiteTarget::class to StaticStubPsiFactory(
            KotlinAnnotationUseSiteTargetStub::class, { KtAnnotationUseSiteTarget(it) }, { KtAnnotationUseSiteTarget(it as KotlinAnnotationUseSiteTargetStub) },
            { NativeReferenceArrays.newArray(KtAnnotationUseSiteTarget::class, it) }, false,
        ),
        KtClassInitializer::class to StaticStubPsiFactory(
            KotlinPlaceHolderStub::class, { KtClassInitializer(it) }, { KtClassInitializer(it as KotlinPlaceHolderStub<KtClassInitializer>) },
            { NativeReferenceArrays.newArray(KtClassInitializer::class, it) }, true,
        ),
        KtScriptInitializer::class to StaticStubPsiFactory(
            KotlinPlaceHolderStub::class, { KtScriptInitializer(it) }, { KtScriptInitializer(it as KotlinPlaceHolderStub<KtScriptInitializer>) },
            { NativeReferenceArrays.newArray(KtScriptInitializer::class, it) }, true,
        ),
        KtBackingField::class to StaticStubPsiFactory(
            KotlinBackingFieldStub::class, { KtBackingField(it) }, { KtBackingField(it as KotlinBackingFieldStub) },
            { NativeReferenceArrays.newArray(KtBackingField::class, it) }, true,
        ),
        KtBlockStringTemplateEntry::class to StaticStubPsiFactory(
            KotlinBlockStringTemplateEntryStub::class, { KtBlockStringTemplateEntry(it) }, { KtBlockStringTemplateEntry(it as KotlinBlockStringTemplateEntryStub) },
            { NativeReferenceArrays.newArray(KtBlockStringTemplateEntry::class, it) }, false,
        ),
        KtCallExpression::class to StaticStubPsiFactory(
            KotlinPlaceHolderStub::class, { KtCallExpression(it) }, { KtCallExpression(it as KotlinPlaceHolderStub<KtCallExpression>) },
            { NativeReferenceArrays.newArray(KtCallExpression::class, it) }, true,
        ),
        KtClass::class to StaticStubPsiFactory(
            KotlinClassStub::class, { KtClass(it) }, { KtClass(it as KotlinClassStub) },
            { NativeReferenceArrays.newArray(KtClass::class, it) }, true,
        ),
        KtClassBody::class to StaticStubPsiFactory(
            KotlinPlaceHolderStub::class, { KtClassBody(it) }, { KtClassBody(it as KotlinPlaceHolderStub<KtClassBody>) },
            { NativeReferenceArrays.newArray(KtClassBody::class, it) }, false,
        ),
        KtClassLiteralExpression::class to StaticStubPsiFactory(
            KotlinClassLiteralExpressionStub::class, { KtClassLiteralExpression(it) }, { KtClassLiteralExpression(it as KotlinClassLiteralExpressionStub) },
            { NativeReferenceArrays.newArray(KtClassLiteralExpression::class, it) }, true,
        ),
        KtCollectionLiteralExpression::class to StaticStubPsiFactory(
            KotlinCollectionLiteralExpressionStub::class, { KtCollectionLiteralExpression(it) }, { KtCollectionLiteralExpression(it as KotlinCollectionLiteralExpressionStub) },
            { NativeReferenceArrays.newArray(KtCollectionLiteralExpression::class, it) }, true,
        ),
        KtCompanionBlock::class to StaticStubPsiFactory(
            KotlinPlaceHolderStub::class, { KtCompanionBlock(it) }, { KtCompanionBlock(it as KotlinPlaceHolderStub<KtCompanionBlock>) },
            { NativeReferenceArrays.newArray(KtCompanionBlock::class, it) }, false,
        ),
        KtConstantExpression::class to StaticStubPsiFactory(
            KotlinConstantExpressionStub::class, { KtConstantExpression(it) }, { KtConstantExpression(it as KotlinConstantExpressionStub) },
            { NativeReferenceArrays.newArray(KtConstantExpression::class, it) }, true,
        ),
        KtConstructorCalleeExpression::class to StaticStubPsiFactory(
            KotlinPlaceHolderStub::class, { KtConstructorCalleeExpression(it) }, { KtConstructorCalleeExpression(it as KotlinPlaceHolderStub<KtConstructorCalleeExpression>) },
            { NativeReferenceArrays.newArray(KtConstructorCalleeExpression::class, it) }, true,
        ),
        KtContextReceiver::class to StaticStubPsiFactory(
            KotlinContextReceiverStub::class, { KtContextReceiver(it) }, { KtContextReceiver(it as KotlinContextReceiverStub) },
            { NativeReferenceArrays.newArray(KtContextReceiver::class, it) }, false,
        ),
        KtContextReceiverList::class to StaticStubPsiFactory(
            KotlinPlaceHolderStub::class, { KtContextReceiverList(it) }, { KtContextReceiverList(it as KotlinPlaceHolderStub<KtContextParameterList>) },
            { NativeReferenceArrays.newArray(KtContextReceiverList::class, it) }, false,
        ),
        KtContractEffect::class to StaticStubPsiFactory(
            KotlinContractEffectStub::class, { KtContractEffect(it) }, { KtContractEffect(it as KotlinContractEffectStub) },
            { NativeReferenceArrays.newArray(KtContractEffect::class, it) }, false,
        ),
        KtContractEffectList::class to StaticStubPsiFactory(
            KotlinPlaceHolderStub::class, { KtContractEffectList(it) }, { KtContractEffectList(it as KotlinPlaceHolderStub<KtContractEffectList>) },
            { NativeReferenceArrays.newArray(KtContractEffectList::class, it) }, false,
        ),
        KtDeclarationModifierList::class to StaticStubPsiFactory(
            KotlinModifierListStub::class, { KtDeclarationModifierList(it) }, { KtDeclarationModifierList(it as KotlinModifierListStub) },
            { NativeReferenceArrays.newArray(KtDeclarationModifierList::class, it) }, false,
        ),
        KtDelegatedSuperTypeEntry::class to StaticStubPsiFactory(
            KotlinPlaceHolderStub::class, { KtDelegatedSuperTypeEntry(it) }, { KtDelegatedSuperTypeEntry(it as KotlinPlaceHolderStub<out KtSuperTypeListEntry>) },
            { NativeReferenceArrays.newArray(KtDelegatedSuperTypeEntry::class, it) }, false,
        ),
        KtDestructuringDeclaration::class to StaticStubPsiFactory(
            KotlinDestructuringDeclarationStub::class, { KtDestructuringDeclaration(it) }, { KtDestructuringDeclaration(it as KotlinDestructuringDeclarationStub) },
            { NativeReferenceArrays.newArray(KtDestructuringDeclaration::class, it) }, true,
        ),
        KtDotQualifiedExpression::class to StaticStubPsiFactory(
            KotlinPlaceHolderStub::class, { KtDotQualifiedExpression(it) }, { KtDotQualifiedExpression(it as KotlinPlaceHolderStub<KtDotQualifiedExpression>) },
            { NativeReferenceArrays.newArray(KtDotQualifiedExpression::class, it) }, true,
        ),
        KtDynamicType::class to StaticStubPsiFactory(
            KotlinPlaceHolderStub::class, { KtDynamicType(it) }, { KtDynamicType(it as KotlinPlaceHolderStub<KtDynamicType>) },
            { NativeReferenceArrays.newArray(KtDynamicType::class, it) }, false,
        ),
        KtEnumEntry::class to StaticStubPsiFactory(
            KotlinClassStub::class, { KtEnumEntry(it) }, { KtEnumEntry(it as KotlinClassStub) },
            { NativeReferenceArrays.newArray(KtEnumEntry::class, it) }, true,
        ),
        KtEnumEntrySuperclassReferenceExpression::class to StaticStubPsiFactory(
            KotlinEnumEntrySuperclassReferenceExpressionStub::class, { KtEnumEntrySuperclassReferenceExpression(it) }, { KtEnumEntrySuperclassReferenceExpression(it as KotlinEnumEntrySuperclassReferenceExpressionStub) },
            { NativeReferenceArrays.newArray(KtEnumEntrySuperclassReferenceExpression::class, it) }, true,
        ),
        KtEscapeStringTemplateEntry::class to StaticStubPsiFactory(
            KotlinPlaceHolderWithTextStub::class, { KtEscapeStringTemplateEntry(it) }, { KtEscapeStringTemplateEntry(it as KotlinPlaceHolderWithTextStub<KtEscapeStringTemplateEntry>) },
            { NativeReferenceArrays.newArray(KtEscapeStringTemplateEntry::class, it) }, false,
        ),
        KtFileAnnotationList::class to StaticStubPsiFactory(
            KotlinPlaceHolderStub::class, { KtFileAnnotationList(it) }, { KtFileAnnotationList(it as KotlinPlaceHolderStub<KtFileAnnotationList>) },
            { NativeReferenceArrays.newArray(KtFileAnnotationList::class, it) }, false,
        ),
        KtFunctionType::class to StaticStubPsiFactory(
            KotlinFunctionTypeStub::class, { KtFunctionType(it) }, { KtFunctionType(it as KotlinFunctionTypeStub) },
            { NativeReferenceArrays.newArray(KtFunctionType::class, it) }, false,
        ),
        KtFunctionTypeReceiver::class to StaticStubPsiFactory(
            KotlinPlaceHolderStub::class, { KtFunctionTypeReceiver(it) }, { KtFunctionTypeReceiver(it as KotlinPlaceHolderStub<KtFunctionTypeReceiver>) },
            { NativeReferenceArrays.newArray(KtFunctionTypeReceiver::class, it) }, false,
        ),
        KtImportAlias::class to StaticStubPsiFactory(
            KotlinImportAliasStub::class, { KtImportAlias(it) }, { KtImportAlias(it as KotlinImportAliasStub) },
            { NativeReferenceArrays.newArray(KtImportAlias::class, it) }, false,
        ),
        KtImportDirective::class to StaticStubPsiFactory(
            KotlinImportDirectiveStub::class, { KtImportDirective(it) }, { KtImportDirective(it as KotlinImportDirectiveStub) },
            { NativeReferenceArrays.newArray(KtImportDirective::class, it) }, false,
        ),
        KtImportList::class to StaticStubPsiFactory(
            KotlinPlaceHolderStub::class, { KtImportList(it) }, { KtImportList(it as KotlinPlaceHolderStub<KtImportList>) },
            { NativeReferenceArrays.newArray(KtImportList::class, it) }, false,
        ),
        KtInitializerList::class to StaticStubPsiFactory(
            KotlinPlaceHolderStub::class, { KtInitializerList(it) }, { KtInitializerList(it as KotlinPlaceHolderStub<KtInitializerList>) },
            { NativeReferenceArrays.newArray(KtInitializerList::class, it) }, false,
        ),
        KtIntersectionType::class to StaticStubPsiFactory(
            KotlinPlaceHolderStub::class, { KtIntersectionType(it) }, { KtIntersectionType(it as KotlinPlaceHolderStub<KtIntersectionType>) },
            { NativeReferenceArrays.newArray(KtIntersectionType::class, it) }, false,
        ),
        KtLambdaArgument::class to StaticStubPsiFactory(
            KotlinValueArgumentStub::class, { KtLambdaArgument(it) }, { KtLambdaArgument(it as KotlinValueArgumentStub<KtLambdaArgument>) },
            { NativeReferenceArrays.newArray(KtLambdaArgument::class, it) }, false,
        ),
        KtLiteralStringTemplateEntry::class to StaticStubPsiFactory(
            KotlinPlaceHolderWithTextStub::class, { KtLiteralStringTemplateEntry(it) }, { KtLiteralStringTemplateEntry(it as KotlinPlaceHolderWithTextStub<KtLiteralStringTemplateEntry>) },
            { NativeReferenceArrays.newArray(KtLiteralStringTemplateEntry::class, it) }, false,
        ),
        KtNameReferenceExpression::class to StaticStubPsiFactory(
            KotlinNameReferenceExpressionStub::class, { KtNameReferenceExpression(it) }, { KtNameReferenceExpression(it as KotlinNameReferenceExpressionStub) },
            { NativeReferenceArrays.newArray(KtNameReferenceExpression::class, it) }, true,
        ),
        KtNamedFunction::class to StaticStubPsiFactory(
            KotlinFunctionStub::class, { KtNamedFunction(it) }, { KtNamedFunction(it as KotlinFunctionStub) },
            { NativeReferenceArrays.newArray(KtNamedFunction::class, it) }, true,
        ),
        KtNullableType::class to StaticStubPsiFactory(
            KotlinPlaceHolderStub::class, { KtNullableType(it) }, { KtNullableType(it as KotlinPlaceHolderStub<KtNullableType>) },
            { NativeReferenceArrays.newArray(KtNullableType::class, it) }, false,
        ),
        KtObjectDeclaration::class to StaticStubPsiFactory(
            KotlinObjectStub::class, { KtObjectDeclaration(it) }, { KtObjectDeclaration(it as KotlinObjectStub) },
            { NativeReferenceArrays.newArray(KtObjectDeclaration::class, it) }, true,
        ),
        KtPackageDirective::class to StaticStubPsiFactory(
            KotlinPlaceHolderStub::class, { KtPackageDirective(it) }, { KtPackageDirective(it as KotlinPlaceHolderStub<KtPackageDirective>) },
            { NativeReferenceArrays.newArray(KtPackageDirective::class, it) }, false,
        ),
        KtParameter::class to StaticStubPsiFactory(
            KotlinParameterStub::class, { KtParameter(it) }, { KtParameter(it as KotlinParameterStub) },
            { NativeReferenceArrays.newArray(KtParameter::class, it) }, true,
        ),
        KtParameterList::class to StaticStubPsiFactory(
            KotlinPlaceHolderStub::class, { KtParameterList(it) }, { KtParameterList(it as KotlinPlaceHolderStub<KtParameterList>) },
            { NativeReferenceArrays.newArray(KtParameterList::class, it) }, false,
        ),
        KtPrimaryConstructor::class to StaticStubPsiFactory(
            KotlinConstructorStub::class, { KtPrimaryConstructor(it) }, { KtPrimaryConstructor(it as KotlinConstructorStub<KtPrimaryConstructor>) },
            { NativeReferenceArrays.newArray(KtPrimaryConstructor::class, it) }, true,
        ),
        KtProperty::class to StaticStubPsiFactory(
            KotlinPropertyStub::class, { KtProperty(it) }, { KtProperty(it as KotlinPropertyStub) },
            { NativeReferenceArrays.newArray(KtProperty::class, it) }, true,
        ),
        KtPropertyAccessor::class to StaticStubPsiFactory(
            KotlinPropertyAccessorStub::class, { KtPropertyAccessor(it) }, { KtPropertyAccessor(it as KotlinPropertyAccessorStub) },
            { NativeReferenceArrays.newArray(KtPropertyAccessor::class, it) }, true,
        ),
        KtScript::class to StaticStubPsiFactory(
            KotlinScriptStub::class, { KtScript(it) }, { KtScript(it as KotlinScriptStub) },
            { NativeReferenceArrays.newArray(KtScript::class, it) }, true,
        ),
        KtSecondaryConstructor::class to StaticStubPsiFactory(
            KotlinConstructorStub::class, { KtSecondaryConstructor(it) }, { KtSecondaryConstructor(it as KotlinConstructorStub<KtSecondaryConstructor>) },
            { NativeReferenceArrays.newArray(KtSecondaryConstructor::class, it) }, true,
        ),
        KtSimpleNameStringTemplateEntry::class to StaticStubPsiFactory(
            KotlinPlaceHolderWithTextStub::class, { KtSimpleNameStringTemplateEntry(it) }, { KtSimpleNameStringTemplateEntry(it as KotlinPlaceHolderWithTextStub<KtSimpleNameStringTemplateEntry>) },
            { NativeReferenceArrays.newArray(KtSimpleNameStringTemplateEntry::class, it) }, false,
        ),
        KtStringInterpolationPrefix::class to StaticStubPsiFactory(
            KotlinStringInterpolationPrefixStub::class, { KtStringInterpolationPrefix(it) }, { KtStringInterpolationPrefix(it as KotlinStringInterpolationPrefixStub) },
            { NativeReferenceArrays.newArray(KtStringInterpolationPrefix::class, it) }, false,
        ),
        KtStringTemplateExpression::class to StaticStubPsiFactory(
            KotlinPlaceHolderStub::class, { KtStringTemplateExpression(it) }, { KtStringTemplateExpression(it as KotlinPlaceHolderStub<KtStringTemplateExpression>) },
            { NativeReferenceArrays.newArray(KtStringTemplateExpression::class, it) }, true,
        ),
        KtSuperTypeCallEntry::class to StaticStubPsiFactory(
            KotlinPlaceHolderStub::class, { KtSuperTypeCallEntry(it) }, { KtSuperTypeCallEntry(it as KotlinPlaceHolderStub<out KtSuperTypeListEntry>) },
            { NativeReferenceArrays.newArray(KtSuperTypeCallEntry::class, it) }, false,
        ),
        KtSuperTypeEntry::class to StaticStubPsiFactory(
            KotlinPlaceHolderStub::class, { KtSuperTypeEntry(it) }, { KtSuperTypeEntry(it as KotlinPlaceHolderStub<out KtSuperTypeListEntry>) },
            { NativeReferenceArrays.newArray(KtSuperTypeEntry::class, it) }, false,
        ),
        KtSuperTypeList::class to StaticStubPsiFactory(
            KotlinPlaceHolderStub::class, { KtSuperTypeList(it) }, { KtSuperTypeList(it as KotlinPlaceHolderStub<KtSuperTypeList>) },
            { NativeReferenceArrays.newArray(KtSuperTypeList::class, it) }, false,
        ),
        KtTypeAlias::class to StaticStubPsiFactory(
            KotlinTypeAliasStub::class, { KtTypeAlias(it) }, { KtTypeAlias(it as KotlinTypeAliasStub) },
            { NativeReferenceArrays.newArray(KtTypeAlias::class, it) }, true,
        ),
        KtTypeArgumentList::class to StaticStubPsiFactory(
            KotlinPlaceHolderStub::class, { KtTypeArgumentList(it) }, { KtTypeArgumentList(it as KotlinPlaceHolderStub<KtTypeArgumentList>) },
            { NativeReferenceArrays.newArray(KtTypeArgumentList::class, it) }, false,
        ),
        KtTypeConstraint::class to StaticStubPsiFactory(
            KotlinPlaceHolderStub::class, { KtTypeConstraint(it) }, { KtTypeConstraint(it as KotlinPlaceHolderStub<KtTypeConstraint>) },
            { NativeReferenceArrays.newArray(KtTypeConstraint::class, it) }, false,
        ),
        KtTypeConstraintList::class to StaticStubPsiFactory(
            KotlinPlaceHolderStub::class, { KtTypeConstraintList(it) }, { KtTypeConstraintList(it as KotlinPlaceHolderStub<KtTypeConstraintList>) },
            { NativeReferenceArrays.newArray(KtTypeConstraintList::class, it) }, false,
        ),
        KtTypeParameter::class to StaticStubPsiFactory(
            KotlinTypeParameterStub::class, { KtTypeParameter(it) }, { KtTypeParameter(it as KotlinTypeParameterStub) },
            { NativeReferenceArrays.newArray(KtTypeParameter::class, it) }, true,
        ),
        KtTypeParameterList::class to StaticStubPsiFactory(
            KotlinPlaceHolderStub::class, { KtTypeParameterList(it) }, { KtTypeParameterList(it as KotlinPlaceHolderStub<KtTypeParameterList>) },
            { NativeReferenceArrays.newArray(KtTypeParameterList::class, it) }, false,
        ),
        KtTypeProjection::class to StaticStubPsiFactory(
            KotlinTypeProjectionStub::class, { KtTypeProjection(it) }, { KtTypeProjection(it as KotlinTypeProjectionStub) },
            { NativeReferenceArrays.newArray(KtTypeProjection::class, it) }, false,
        ),
        KtTypeReference::class to StaticStubPsiFactory(
            KotlinPlaceHolderStub::class, { KtTypeReference(it) }, { KtTypeReference(it as KotlinPlaceHolderStub<KtTypeReference>) },
            { NativeReferenceArrays.newArray(KtTypeReference::class, it) }, false,
        ),
        KtUserType::class to StaticStubPsiFactory(
            KotlinUserTypeStub::class, { KtUserType(it) }, { KtUserType(it as KotlinUserTypeStub) },
            { NativeReferenceArrays.newArray(KtUserType::class, it) }, false,
        ),
        KtValueArgument::class to StaticStubPsiFactory(
            KotlinValueArgumentStub::class, { KtValueArgument(it) }, { KtValueArgument(it as KotlinValueArgumentStub<KtValueArgument>) },
            { NativeReferenceArrays.newArray(KtValueArgument::class, it) }, false,
        ),
        KtValueArgumentList::class to StaticStubPsiFactory(
            KotlinPlaceHolderStub::class, { KtValueArgumentList(it) }, { KtValueArgumentList(it as KotlinPlaceHolderStub<KtValueArgumentList>) },
            { NativeReferenceArrays.newArray(KtValueArgumentList::class, it) }, false,
        ),
        KtValueArgumentName::class to StaticStubPsiFactory(
            KotlinPlaceHolderStub::class, { KtValueArgumentName(it) }, { KtValueArgumentName(it as KotlinPlaceHolderStub<KtValueArgumentName>) },
            { NativeReferenceArrays.newArray(KtValueArgumentName::class, it) }, false,
        ),
    )

    fun get(psiClass: KClass<*>, stubClass: KClass<*>): StaticStubPsiFactory {
        val factory = factories[psiClass]
        if (factory == null || factory.stubClass != stubClass) {
            val failure = MissingPsiConstructor(
                "${psiClass.simpleName}(ASTNode), ${psiClass.simpleName}(${stubClass.simpleName})"
            )
            throw RuntimeException(
                "Stub element type declaration for ${psiClass.simpleName} is missing required constructors",
                failure,
            )
        }
        return factory
    }
}

internal class MissingPsiConstructor(signature: String) : Exception(signature)
