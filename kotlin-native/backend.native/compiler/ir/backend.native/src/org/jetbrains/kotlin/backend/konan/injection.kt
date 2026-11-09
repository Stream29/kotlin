/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */
package org.jetbrains.kotlin.backend.konan

import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.descriptors.PackageFragmentProvider
import org.jetbrains.kotlin.descriptors.impl.CompositePackageFragmentProvider
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl
import org.jetbrains.kotlin.K1Deprecation

internal class KonanComponentProvider(
        val config: NativeSecondStageCompilationConfig,
        val frontendServices: FrontendServices
)

@OptIn(K1Deprecation::class)
internal fun createTopDownAnalyzerProviderForKonan(
        moduleContext: ModuleContext,
        @Suppress("UNUSED_PARAMETER") bindingTrace: BindingTrace,
        @Suppress("UNUSED_PARAMETER") languageVersionSettings: LanguageVersionSettings,
        additionalPackages: List<PackageFragmentProvider>,
        config: NativeSecondStageCompilationConfig
): KonanComponentProvider {
    val moduleDescriptor = moduleContext.module as ModuleDescriptorImpl
    moduleDescriptor.initialize(
            CompositePackageFragmentProvider(
                    additionalPackages,
                    "CompositeProvider@createTopDownAnalyzerProviderForKonan for module ${moduleContext.module}"
            )
    )
    return KonanComponentProvider(config, FrontendServices(DeprecationResolver()))
}
