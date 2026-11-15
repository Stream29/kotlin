/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.konan.driver.phases

import org.jetbrains.kotlin.K1Deprecation
import org.jetbrains.kotlin.backend.common.phaser.createSimpleNamedCompilerPhase
import org.jetbrains.kotlin.backend.konan.*
import org.jetbrains.kotlin.backend.konan.descriptors.isNativeStdlib
import org.jetbrains.kotlin.backend.konan.driver.NativeBackendPhaseContext
import org.jetbrains.kotlin.builtins.functions.functionInterfacePackageFragmentProvider
import org.jetbrains.kotlin.builtins.konan.KonanBuiltIns
import org.jetbrains.kotlin.config.phaser.NamedCompilerPhase
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentProvider
import org.jetbrains.kotlin.descriptors.impl.CompositePackageFragmentProvider
import org.jetbrains.kotlin.descriptors.impl.ModuleDependenciesImpl
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl
import org.jetbrains.kotlin.konan.target.CompilerOutputKind
import org.jetbrains.kotlin.library.metadata.CurrentKlibModuleOrigin
import org.jetbrains.kotlin.library.metadata.KlibModuleOrigin
import org.jetbrains.kotlin.library.metadata.impl.KlibResolvedModuleDescriptorsFactoryImpl
import org.jetbrains.kotlin.library.metadata.isCInteropLibrary
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.platform.konan.NativePlatforms
import org.jetbrains.kotlin.resolve.ImplicitIntegerCoercion
import org.jetbrains.kotlin.storage.LockBasedStorageManager
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toLowerCaseAsciiOnly

data class K1FrontendPhaseOutput(val moduleDescriptor: ModuleDescriptor, val disposeCallback: () -> Unit)

@OptIn(K1Deprecation::class)
internal val K1FrontendPhase: NamedCompilerPhase<NativeBackendPhaseContext, KotlinCoreEnvironment, K1FrontendPhaseOutput?> = createSimpleNamedCompilerPhase(
        "Frontend",
        outputIfNotEnabled = { _, _, _, _ -> null }
) { context, input: KotlinCoreEnvironment ->
    val sourceFiles = input.getSourceFiles()

    check(context.config.produce != CompilerOutputKind.LIBRARY) {
        "Internal error: An attempt to run the 2nd compilation stage to produce ${CompilerOutputKind.LIBRARY}"
    }

    check(sourceFiles.isEmpty()) {
        "Internal error: no source files should have been passed here (${sourceFiles.first().virtualFilePath} in particular)\n" +
                "to produce binary (e.g. a ${context.config.produce.name.toLowerCaseAsciiOnly()})\n" +
                "KonanDriver.kt::splitOntoTwoStages() must transform such compilation into two-stage compilation. Please report this here: https://kotl.in/issue"
    }

    val moduleDescriptor = setupModuleDescriptor(context)
    context.config.configuration.sourcesModules = moduleDescriptor.getIncludedLibraryDescriptors(context.config).toSet() + moduleDescriptor

    K1FrontendPhaseOutput(moduleDescriptor) {}
}

@OptIn(K1Deprecation::class)
private fun setupModuleDescriptor(context: NativeBackendPhaseContext): ModuleDescriptorImpl {
    val config = context.config
    val moduleName = Name.special("<${config.moduleId}>")
    val storageManager = LockBasedStorageManager("TopDownAnalyzer for Konan")

    val builtIns = KonanBuiltIns(storageManager)
    val module = ModuleDescriptorImpl(
            moduleName,
            storageManager,
            builtIns,
            capabilities = mapOf(
                    KlibModuleOrigin.CAPABILITY to CurrentKlibModuleOrigin,
                    ImplicitIntegerCoercion.MODULE_CAPABILITY to CurrentKlibModuleOrigin.isCInteropLibrary()
            ),
            platform = NativePlatforms.unspecifiedNativePlatform
    )
    builtIns.setBuiltInsModule(module)

    val moduleDescriptorFactory = K1KlibMetadataModuleDescriptorFactoryImpl()
    val resolvedModuleDescriptors = KlibResolvedModuleDescriptorsFactoryImpl(moduleDescriptorFactory).createResolved2(
            // Note: The order of libraries is not important except for stdlib, which should go the first.
            libraries = config.loadedKlibs.all,
            storageManager = storageManager,
            builtIns = module.builtIns,
            languageVersionSettings = config.languageVersionSettings,
            friendModuleFiles = config.loadedKlibs.friends.map { it.path }.toSet(),
            refinesModuleFiles = config.refinesModuleFiles,
            includedLibraryFiles = config.loadedKlibs.included.map { it.path }.toSet(),
            additionalDependencyModules = listOf(module),
            isForMetadataCompilation = config.metadataKlib
    )

    val additionalPackages = mutableListOf<PackageFragmentProvider>()
    if (!module.isNativeStdlib()) {
        module.setDependencies(ModuleDependenciesImpl(
                allDependencies =
                        listOf(module) + resolvedModuleDescriptors.resolvedDescriptors + resolvedModuleDescriptors.forwardDeclarationsModule,
                modulesWhoseInternalsAreVisible = resolvedModuleDescriptors.friendModules,
                directExpectedByDependencies = resolvedModuleDescriptors.refinesModules.toList(),
                allExpectedByDependencies = resolvedModuleDescriptors.refinesModules
        ))
    } else {
        assert(resolvedModuleDescriptors.resolvedDescriptors.isEmpty())
        module.setDependencies(module)
        // [K][Suspend]FunctionN belong to stdlib.
        additionalPackages += functionInterfacePackageFragmentProvider(storageManager, module)
    }

    module.initialize(
            CompositePackageFragmentProvider(
                    additionalPackages,
                    "CompositeProvider@createTopDownAnalyzerProviderForKonan for module $module"
            )
    )

    return module
}
