/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.common

import org.jetbrains.kotlin.backend.common.loadMetadataKlibs
import org.jetbrains.kotlin.config.*
import org.jetbrains.kotlin.fir.*
import org.jetbrains.kotlin.fir.analysis.checkers.CliOnlyLanguageVersionSettingsCheckers
import org.jetbrains.kotlin.fir.checkers.registerExperimentalCheckers
import org.jetbrains.kotlin.fir.checkers.registerExtraCommonCheckers
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar
import org.jetbrains.kotlin.fir.resolve.providers.impl.FirBuiltinSyntheticFunctionInterfaceProvider
import org.jetbrains.kotlin.fir.resolve.providers.impl.syntheticFunctionInterfacesSymbolProvider
import org.jetbrains.kotlin.fir.session.*
import org.jetbrains.kotlin.library.KotlinLibrary
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.platform.konan.NativePlatforms

/**
 * Creates library session and sources session for Native platform
 * Number of created session depends on mode of MPP:
 *   - disabled
 *   - legacy (one platform and one common module)
 *   - HMPP (multiple number of modules)
 */
fun <F> prepareNativeSessions(
    files: List<F>,
    configuration: CompilerConfiguration,
    rootModuleName: Name,
    resolvedLibraries: List<KotlinLibrary>,
    libraryList: DependencyListForCliModule,
    extensionRegistrars: List<FirExtensionRegistrar>,
    metadataCompilationMode: Boolean,
    isCommonSource: (F) -> Boolean,
    fileBelongsToModule: (F, String) -> Boolean,
): List<SessionWithSources<F>> {
    return prepareKlibSessions(
        if (metadataCompilationMode) FirNativeSessionFactory.ForMetadata else FirNativeSessionFactory,
        NativePlatforms.unspecifiedNativePlatform, files, configuration, rootModuleName, resolvedLibraries,
        libraryList, extensionRegistrars, isCommonSource, fileBelongsToModule, metadataCompilationMode, icData = null
    )
}

private fun <F> prepareKlibSessions(
    sessionFactory: AbstractFirKlibSessionFactory<*>,
    platform: TargetPlatform,
    files: List<F>,
    configuration: CompilerConfiguration,
    rootModuleName: Name,
    resolvedLibraries: List<KotlinLibrary>,
    libraryList: DependencyListForCliModule,
    extensionRegistrars: List<FirExtensionRegistrar>,
    isCommonSource: (F) -> Boolean,
    fileBelongsToModule: (F, String) -> Boolean,
    metadataCompilationMode: Boolean,
    icData: KlibIcData?,
): List<SessionWithSources<F>> {
    return SessionConstructionUtils.prepareSessions(
        files, configuration, rootModuleName, platform,
        metadataCompilationMode, libraryList, extensionRegistrars, isCommonSource, isScript = { false }, fileBelongsToModule,
        createSharedLibrarySession = {
            sessionFactory.createSharedLibrarySession(
                rootModuleName,
                configuration,
                extensionRegistrars,
            )
        },
        createLibrarySession = { sharedLibrarySession ->
            sessionFactory.createLibrarySession(
                resolvedLibraries,
                sharedLibrarySession,
                libraryList.moduleDataProvider,
                extensionRegistrars,
                configuration,
            )
        },
        createSourceSession = { moduleData, kmpModuleKind, sessionConfigurator ->
            sessionFactory.createSourceSession(
                moduleData,
                extensionRegistrars,
                configuration,
                kmpModuleKind,
                icData = icData,
                init = sessionConfigurator,
            )
        },
    )
}

fun interface FirSessionProducer {
    fun createSession(
        moduleData: FirModuleData,
        kmpModuleKind: KmpModuleKind,
        sessionConfigurator: FirSessionConfigurator.() -> Unit,
    ): FirSession
}

object SessionConstructionUtils {
    @OptIn(PrivateSessionConstructor::class, SessionConfiguration::class)
    fun <F> prepareSessions(
        files: List<F>,
        configuration: CompilerConfiguration,
        rootModuleName: Name,
        targetPlatform: TargetPlatform,
        metadataCompilationMode: Boolean,
        libraryList: DependencyListForCliModule,
        extensionRegistrars: List<FirExtensionRegistrar>,
        isCommonSource: (F) -> Boolean,
        isScript: (F) -> Boolean,
        fileBelongsToModule: (F, String) -> Boolean,
        createSharedLibrarySession: () -> FirSession,
        createLibrarySession: (sharedLibrarySession: FirSession) -> FirSession,
        createSourceSession: FirSessionProducer,
    ): List<SessionWithSources<F>> {
        val languageVersionSettings = configuration.languageVersionSettings
        val [scripts, nonScriptFiles] = when (configuration.dontCreateSeparateSessionForScripts) {
            false -> files.partition(isScript)
            true -> emptyList<F>() to files
        }

        val isMppEnabled = languageVersionSettings.supportsFeature(LanguageFeature.MultiPlatformProjects)
        val hmppModuleStructure = configuration.get(CommonConfigurationKeys.HMPP_MODULE_STRUCTURE)

        val sharedLibrarySession = createSharedLibrarySession()
        createLibrarySession(sharedLibrarySession)
        val extraAnalysisMode = configuration.useFirExtraCheckers
        val experimentalAnalysisMode = configuration.useFirExperimentalCheckers
        val sessionConfigurator: FirSessionConfigurator.() -> Unit = {
            registerComponent(
                FirBuiltinSyntheticFunctionInterfaceProvider::class,
                sharedLibrarySession.syntheticFunctionInterfacesSymbolProvider
            )

            if (extraAnalysisMode) {
                registerExtraCommonCheckers()
            }
            if (experimentalAnalysisMode) {
                registerExperimentalCheckers()
            }
        }

        val nonScriptSessions = when {
            metadataCompilationMode || !isMppEnabled -> {
                listOf(
                    createSingleSession(
                        nonScriptFiles, rootModuleName, libraryList, targetPlatform,
                        sessionConfigurator, createSourceSession
                    )
                )
            }

            hmppModuleStructure == null -> createSessionsForLegacyMppProject(
                nonScriptFiles, rootModuleName, libraryList, targetPlatform,
                sessionConfigurator, isCommonSource, createSourceSession
            )

            languageVersionSettings.getFlag(AnalysisFlags.hierarchicalMultiplatformCompilation) -> createSessionsForHierarchicalMppProject(
                nonScriptFiles, rootModuleName, hmppModuleStructure, libraryList, configuration,
                extensionRegistrars, sharedLibrarySession, targetPlatform,
                sessionConfigurator, fileBelongsToModule, createSourceSession
            )

            else -> createSessionsForMppProject(
                nonScriptFiles, rootModuleName, hmppModuleStructure, libraryList, targetPlatform,
                sessionConfigurator, fileBelongsToModule, createSourceSession
            )
        }

        return if (scripts.isEmpty()) nonScriptSessions
        else nonScriptSessions +
                createScriptsSession(
                    scripts, rootModuleName, libraryList, nonScriptSessions.last().session.moduleData,
                    targetPlatform, sessionConfigurator, createSourceSession
                )
    }

    private fun <F> createScriptsSession(
        scripts: List<F>,
        rootModuleName: Name,
        libraryList: DependencyListForCliModule,
        lastModuleData: FirModuleData,
        targetPlatform: TargetPlatform,
        sessionConfigurator: FirSessionConfigurator.() -> Unit,
        createSourceSession: FirSessionProducer,
    ): SessionWithSources<F> =
        createSingleSession(
            scripts, Name.identifier("${rootModuleName.asString()}-scripts"),
            @OptIn(PrivateSessionConstructor::class)
            DependencyListForCliModule(
                libraryList.regularDependencies,
                listOf(lastModuleData),
                libraryList.friendDependencies,
                libraryList.moduleDataProvider
            ),
            targetPlatform,
            sessionConfigurator, createSourceSession
        )

    private fun <F> createSingleSession(
        files: List<F>,
        rootModuleName: Name,
        libraryList: DependencyListForCliModule,
        targetPlatform: TargetPlatform,
        sessionConfigurator: FirSessionConfigurator.() -> Unit,
        sourceSessionProducer: FirSessionProducer,
    ): SessionWithSources<F> {
        val platformModuleData = FirSourceModuleData(
            rootModuleName,
            libraryList.regularDependencies,
            libraryList.dependsOnDependencies,
            libraryList.friendDependencies,
            targetPlatform,
        )

        val session = sourceSessionProducer.createSession(platformModuleData, kmpModuleKind = KmpModuleKind.SingleModule) {
            sessionConfigurator()
            useCheckers(CliOnlyLanguageVersionSettingsCheckers)
        }
        return SessionWithSources(session, files)
    }

    private fun <F> createSessionsForLegacyMppProject(
        files: List<F>,
        rootModuleName: Name,
        libraryList: DependencyListForCliModule,
        targetPlatform: TargetPlatform,
        sessionConfigurator: FirSessionConfigurator.() -> Unit,
        isCommonSource: (F) -> Boolean,
        sourceSessionProducer: FirSessionProducer,
    ): List<SessionWithSources<F>> {
        val commonModuleData = FirSourceModuleData(
            Name.identifier("${rootModuleName.asString()}-common"),
            libraryList.regularDependencies,
            listOf(),
            libraryList.friendDependencies,
            targetPlatform,
            isCommon = true
        )

        val platformModuleData = FirSourceModuleData(
            rootModuleName,
            libraryList.regularDependencies,
            listOf(commonModuleData),
            libraryList.friendDependencies,
            targetPlatform,
            isCommon = false
        )

        val commonFiles = mutableListOf<F>()
        val platformFiles = mutableListOf<F>()
        for (file in files) {
            (if (isCommonSource(file)) commonFiles else platformFiles).add(file)
        }

        val commonSession = sourceSessionProducer.createSession(
            commonModuleData,
            kmpModuleKind = KmpModuleKind.NonLeafRegularModule,
            sessionConfigurator
        )
        val platformSession = sourceSessionProducer.createSession(
            platformModuleData,
            kmpModuleKind = KmpModuleKind.LeafRegularModule
        ) {
            sessionConfigurator()
            useCheckers(CliOnlyLanguageVersionSettingsCheckers)
        }

        return listOf(
            SessionWithSources(commonSession, commonFiles),
            SessionWithSources(platformSession, platformFiles)
        )
    }

    private fun <F> createSessionsForMppProject(
        files: List<F>,
        rootModuleName: Name,
        hmppModuleStructure: HmppCliModuleStructure,
        libraryList: DependencyListForCliModule,
        targetPlatform: TargetPlatform,
        sessionConfigurator: FirSessionConfigurator.() -> Unit,
        fileBelongsToModule: (F, String) -> Boolean,
        createFirSession: FirSessionProducer,
    ): List<SessionWithSources<F>> {
        val moduleDataForHmppModule = LinkedHashMap<HmppCliModule, FirModuleData>()

        for ([index, module] in hmppModuleStructure.modules.withIndex()) {
            val dependencies = hmppModuleStructure.sourceDependencies[module]
                ?.map { moduleDataForHmppModule.getValue(it) }
                .orEmpty()
            val moduleName = if (index == hmppModuleStructure.modules.lastIndex) {
                rootModuleName
            } else {
                Name.special("<${module.name}>")
            }
            val moduleData = FirSourceModuleData(
                moduleName,
                libraryList.regularDependencies,
                dependsOnDependencies = dependencies,
                libraryList.friendDependencies,
                targetPlatform,
                isCommon = index < hmppModuleStructure.modules.size - 1
            )
            moduleDataForHmppModule[module] = moduleData
        }

        return createSourceSessionsForMppCompilation(
            hmppModuleStructure, moduleDataForHmppModule, files, fileBelongsToModule,
            createFirSession, sessionConfigurator, isHmppEnabled = false
        )
    }

    private fun <F> createSessionsForHierarchicalMppProject(
        files: List<F>,
        rootModuleName: Name,
        hmppModuleStructure: HmppCliModuleStructure,
        libraryListForLeafModule: DependencyListForCliModule,
        configuration: CompilerConfiguration,
        extensionRegistrars: List<FirExtensionRegistrar>,
        sharedLibrarySession: FirSession,
        targetPlatform: TargetPlatform,
        sessionConfigurator: FirSessionConfigurator.() -> Unit,
        fileBelongsToModule: (F, String) -> Boolean,
        createFirSession: FirSessionProducer,
    ): List<SessionWithSources<F>> {
        val moduleDataForHmppModule = LinkedHashMap<HmppCliModule, FirModuleData>()

        for ([index, module] in hmppModuleStructure.modules.withIndex()) {
            val dependencies = hmppModuleStructure.sourceDependencies[module]
                ?.map { moduleDataForHmppModule.getValue(it) }
                .orEmpty()
            val moduleName = if (index == hmppModuleStructure.modules.lastIndex) {
                rootModuleName
            } else {
                Name.special("<${module.name}>")
            }

            val isLeaf = index == hmppModuleStructure.modules.lastIndex

            val libraryList = if (isLeaf) {
                libraryListForLeafModule
            } else {
                val libPaths = hmppModuleStructure.moduleDependencies[module].orEmpty().toMutableSet()

                for (dependency in hmppModuleStructure.sourceDependencies[module].orEmpty()) {
                    libPaths -= hmppModuleStructure.moduleDependencies[dependency].orEmpty()
                }

                val friendLibPaths = hmppModuleStructure.friendDependencies[module].orEmpty().toMutableSet()

                val klibs: List<KotlinLibrary> = loadMetadataKlibs(
                    libraryPaths = libPaths.toList() + friendLibPaths,
                    configuration = configuration
                ).all

                DependencyListForCliModule.build(moduleName) {
                    dependencies(libPaths)
                    friendDependencies(friendLibPaths)
                }.also { libraryList ->
                    FirNativeSessionFactory.ForMetadata.createLibrarySession(
                        klibs,
                        sharedLibrarySession,
                        libraryList.moduleDataProvider,
                        extensionRegistrars,
                        configuration,
                    )
                }
            }

            val moduleData = FirSourceModuleData(
                moduleName,
                dependencies = libraryList.regularDependencies,
                dependsOnDependencies = dependencies,
                friendDependencies = libraryList.friendDependencies,
                targetPlatform,
                isCommon = !isLeaf
            )
            moduleDataForHmppModule[module] = moduleData
        }

        return createSourceSessionsForMppCompilation(
            hmppModuleStructure, moduleDataForHmppModule, files, fileBelongsToModule,
            createFirSession, sessionConfigurator, isHmppEnabled = true
        )
    }

    private fun <F> createSourceSessionsForMppCompilation(
        hmppModuleStructure: HmppCliModuleStructure,
        moduleDataForHmppModule: LinkedHashMap<HmppCliModule, FirModuleData>,
        files: List<F>,
        fileBelongsToModule: (F, String) -> Boolean,
        sourceSessionProducer: FirSessionProducer,
        sessionConfigurator: FirSessionConfigurator.() -> Unit,
        isHmppEnabled: Boolean,
    ): List<SessionWithSources<F>> {
        return hmppModuleStructure.modules.mapIndexed { i, module ->
            val moduleData = moduleDataForHmppModule.getValue(module)
            val sources = files.filter { fileBelongsToModule(it, module.name) }
            val isLeafModule = i == hmppModuleStructure.modules.lastIndex
            val kmpModuleKind = when {
                isLeafModule -> KmpModuleKind.leafModule(isHmppEnabled)
                else -> KmpModuleKind.nonLeafModule(isHmppEnabled)
            }
            val session = sourceSessionProducer.createSession(
                moduleData,
                kmpModuleKind = kmpModuleKind
            ) {
                sessionConfigurator()
                if (isLeafModule) {
                    useCheckers(CliOnlyLanguageVersionSettingsCheckers)
                }
            }
            SessionWithSources(session, sources)
        }
    }
}

data class SessionWithSources<F>(val session: FirSession, val files: List<F>)
