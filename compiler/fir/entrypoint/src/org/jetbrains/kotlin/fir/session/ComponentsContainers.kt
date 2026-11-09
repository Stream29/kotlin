/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.session

import org.jetbrains.kotlin.KtSourceElement
import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.diagnostics.KtRegisteredDiagnosticFactoriesStorage
import org.jetbrains.kotlin.fir.*
import org.jetbrains.kotlin.fir.analysis.CheckersComponent
import org.jetbrains.kotlin.fir.analysis.checkers.FirInlineCheckerPlatformSpecificComponent
import org.jetbrains.kotlin.fir.analysis.checkers.FirPlatformSpecificCastChecker
import org.jetbrains.kotlin.fir.analysis.checkers.FirPlatformSpecificEqualityChecker
import org.jetbrains.kotlin.fir.analysis.checkers.FirPrimaryConstructorSuperTypeCheckerPlatformComponent
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirNameConflictsTrackerImpl
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirGenericArrayClassLiteralSupport
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirRegisteredDiagnosticFactoriesStorage
import org.jetbrains.kotlin.fir.caches.FirCachesFactory
import org.jetbrains.kotlin.fir.caches.FirThreadUnsafeCachesFactory
import org.jetbrains.kotlin.fir.declarations.*
import org.jetbrains.kotlin.fir.expressions.FirInlineConstTrackerComponent
import org.jetbrains.kotlin.fir.extensions.*
import org.jetbrains.kotlin.fir.resolve.*
import org.jetbrains.kotlin.fir.resolve.calls.overloads.FirDeclarationOverloadabilityHelperImpl
import org.jetbrains.kotlin.fir.resolve.inference.InferenceComponents
import org.jetbrains.kotlin.fir.resolve.providers.impl.FirQualifierResolverImpl
import org.jetbrains.kotlin.fir.resolve.providers.impl.FirTypeResolverImpl
import org.jetbrains.kotlin.fir.resolve.transformers.FirCliJumpingPhaseComputationSessionForLocalClassesProvider
import org.jetbrains.kotlin.fir.resolve.transformers.FirDummyCompilerLazyDeclarationResolver
import org.jetbrains.kotlin.fir.resolve.transformers.FirJumpingPhaseComputationSessionForLocalClassesProvider
import org.jetbrains.kotlin.fir.resolve.transformers.mpp.FirExpectActualMatchingContextImpl
import org.jetbrains.kotlin.fir.scopes.FirLookupDefaultStarImportsInSourcesSettingHolder
import org.jetbrains.kotlin.fir.scopes.FirOverrideChecker
import org.jetbrains.kotlin.fir.scopes.FirOverrideService
import org.jetbrains.kotlin.fir.scopes.SubstitutionScopeKeyFactory
import org.jetbrains.kotlin.fir.scopes.impl.*
import org.jetbrains.kotlin.fir.serialization.FirProvidedDeclarationsForMetadataService
import org.jetbrains.kotlin.fir.symbols.FirLazyDeclarationResolver
import org.jetbrains.kotlin.fir.types.*
import org.jetbrains.kotlin.incremental.components.EnumWhenTracker
import org.jetbrains.kotlin.incremental.components.ICFileMappingTracker
import org.jetbrains.kotlin.incremental.components.ImportTracker
import org.jetbrains.kotlin.incremental.components.LookupTracker

// -------------------------- Required components --------------------------

@OptIn(SessionConfiguration::class)
fun FirSession.registerCommonComponents(languageVersionSettings: LanguageVersionSettings, isMetadataCompilation: Boolean) {
    register(FirLanguageSettingsComponent::class, FirLanguageSettingsComponent(languageVersionSettings, isMetadataCompilation))
    register(TypeComponents::class, TypeComponents(this))
    register(InferenceComponents::class, InferenceComponents(this))

    register(FirDeclaredMemberScopeProvider::class, FirDeclaredMemberScopeProvider(this))
    register(FirCorrespondingSupertypesCache::class, FirCorrespondingSupertypesCache(this))
    register(FirDefaultParametersResolver::class, FirDefaultParametersResolver())

    register(FirExtensionService::class, FirExtensionService(this))

    register(FirSubstitutionOverrideStorage::class, FirSubstitutionOverrideStorage(this))
    register(FirIntersectionOverrideStorage::class, FirIntersectionOverrideStorage(this))
    register(FirTypealiasConstructorStorage::class, FirTypealiasConstructorStorage(this))
    register(FirSynthesizedStorage::class, FirSynthesizedStorage(this))
    register(FirGeneratedMemberDeclarationsStorage::class, FirGeneratedMemberDeclarationsStorage(this))
    register(FirSamConstructorStorage::class, FirSamConstructorStorage(this))
    register(FirOverrideService::class, FirOverrideService(this))
    register(FirDynamicMembersStorage::class, FirDynamicMembersStorage(this))
    register(FirOverrideChecker::class, FirStandardOverrideChecker(this))
    register(FirDeclarationOverloadabilityHelper::class, FirDeclarationOverloadabilityHelperImpl(this))
    register(FirAnnotationsPlatformSpecificSupportComponent.Default)
    register(FirPrimaryConstructorSuperTypeCheckerPlatformComponent.Default)
    register(FirGenericArrayClassLiteralSupport::class, FirGenericArrayClassLiteralSupport.Disabled)
    register(FirMissingDependencyStorage::class, FirMissingDependencyStorage(this))
    register(FirPlatformSpecificCastChecker::class, FirPlatformSpecificCastChecker.Default)
    register(FirPlatformSpecificEqualityChecker::class, FirPlatformSpecificEqualityChecker.Default)
    register(FirMustUseReturnValueStatusComponent::class, FirMustUseReturnValueStatusComponent.create(languageVersionSettings))
    register(FirInlineCheckerPlatformSpecificComponent::class, FirInlineCheckerPlatformSpecificComponent.NonJvmDefault)
    register(FirExpectActualMappingStorage::class, FirExpectActualMappingStorage(this))
    register(FirInlineConstTrackerComponent::class, FirInlineConstTrackerComponent.Default)
}

@OptIn(SessionConfiguration::class)
fun FirSession.registerCommonComponentsAfterExtensionsAreConfigured() {
    register(FirFunctionTypeKindService::class, FirFunctionTypeKindServiceImpl(this))
    register(FirProvidedDeclarationsForMetadataService::class, FirProvidedDeclarationsForMetadataService.create(this))
}

val firCachesFactoryForCliMode: FirCachesFactory
    get() = FirThreadUnsafeCachesFactory

@OptIn(SessionConfiguration::class)
fun FirSession.registerCliCompilerAndCommonComponents(languageVersionSettings: LanguageVersionSettings, isMetadataCompilation: Boolean) {
    register(FirCachesFactory::class, firCachesFactoryForCliMode)

    registerCommonComponents(languageVersionSettings, isMetadataCompilation)

    register(SealedClassInheritorsProvider::class, SealedClassInheritorsProviderImpl)
    register(FirLazyDeclarationResolver::class, FirDummyCompilerLazyDeclarationResolver)
    register(FirExceptionHandler::class, FirCliExceptionHandler)
    register(
        FirLookupDefaultStarImportsInSourcesSettingHolder::class,
        FirLookupDefaultStarImportsInSourcesSettingHolder.createDefault(languageVersionSettings)
    )

    register(FirRegisteredPluginAnnotations::class, FirRegisteredPluginAnnotationsImpl(this))
    register(FirPredicateBasedProvider::class, FirPredicateBasedProviderImpl(this))
    register(FirHiddenDeprecationProvider::class, FirHiddenDeprecationProvider(this))

    @OptIn(FirImplementationDetail::class)
    register(FirJumpingPhaseComputationSessionForLocalClassesProvider::class, FirCliJumpingPhaseComputationSessionForLocalClassesProvider)

    register(SubstitutionScopeKeyFactory::class, SubstitutionScopeKeyFactory.Default)
}

// -------------------------- Resolve components --------------------------

/*
 * Resolve components which are same on all platforms
 */
@OptIn(SessionConfiguration::class)
fun FirSession.registerResolveComponents(
    diagnosticFactoriesStorage: KtRegisteredDiagnosticFactoriesStorage,
    lookupTracker: LookupTracker? = null,
    enumWhenTracker: EnumWhenTracker? = null,
    importTracker: ImportTracker? = null,
    fileMappingTracker: ICFileMappingTracker? = null,
) {
    register(FirQualifierResolver::class, FirQualifierResolverImpl(this))
    register(FirTypeResolver::class, FirTypeResolverImpl(this))
    register(FirModuleVisibilityChecker::class, FirModuleVisibilityChecker.Standard(this))
    register(SourcesToPathsMapper::class, SourcesToPathsMapper())
    if (lookupTracker != null) {
        val firFileToPath: (KtSourceElement) -> String? = {
            sourcesToPathsMapper.getSourceFilePath(it)
        }
        register(
            FirLookupTrackerComponent::class,
            IncrementalPassThroughLookupTrackerComponent(this, lookupTracker, fileMappingTracker, firFileToPath)
        )
    }
    if (enumWhenTracker != null) {
        register(
            FirEnumWhenTrackerComponent::class,
            IncrementalPassThroughEnumWhenTrackerComponent(enumWhenTracker)
        )
    }
    if (importTracker != null) {
        register(
            FirImportTrackerComponent::class,
            IncrementalPassThroughImportTrackerComponent(importTracker)
        )
    }
    register(FirExpectActualMatchingContextFactory::class, FirExpectActualMatchingContextImpl.Factory)
    register(FirRegisteredDiagnosticFactoriesStorage::class, FirRegisteredDiagnosticFactoriesStorage(diagnosticFactoriesStorage))
}

@OptIn(SessionConfiguration::class)
fun FirSession.registerCliCompilerOnlyResolveComponents() {
    register(FirNameConflictsTracker::class, FirNameConflictsTrackerImpl())

    // The Analysis API uses `LLCheckersFactory`.
    register(CheckersComponent::class, CheckersComponent())
}

@OptIn(SessionConfiguration::class)
fun FirSession.registerModuleData(moduleData: FirModuleData) {
    register(FirModuleData::class, moduleData)
}
