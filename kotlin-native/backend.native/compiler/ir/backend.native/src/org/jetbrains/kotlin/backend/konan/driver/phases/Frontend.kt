/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.konan.driver.phases

import org.jetbrains.kotlin.backend.common.phaser.createSimpleNamedCompilerPhase
import org.jetbrains.kotlin.backend.konan.*
import org.jetbrains.kotlin.backend.konan.TopDownAnalyzerFacadeForKonan
import org.jetbrains.kotlin.backend.konan.driver.BasicNativeBackendPhaseContext
import org.jetbrains.kotlin.backend.konan.driver.NativeBackendPhaseContext
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.konan.target.CompilerOutputKind
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toLowerCaseAsciiOnly
import org.jetbrains.kotlin.K1Deprecation

sealed class FrontendPhaseOutput {
    object ShouldNotGenerateCode : FrontendPhaseOutput()

    data class Full(
            val moduleDescriptor: ModuleDescriptor,
            val bindingContext: BindingContext,
            val frontendServices: FrontendServices,
            val environment: KotlinCoreEnvironment,
    ) : FrontendPhaseOutput()
}

internal interface FrontendContext : NativeBackendPhaseContext {
    var frontendServices: FrontendServices
}

internal class FrontendContextImpl(
        config: NativeSecondStageCompilationConfig
) : BasicNativeBackendPhaseContext(config), FrontendContext {
    override lateinit var frontendServices: FrontendServices
}

@OptIn(K1Deprecation::class)
internal val FrontendPhase = createSimpleNamedCompilerPhase(
        "Frontend",
        outputIfNotEnabled = { _, _, _, _ -> FrontendPhaseOutput.ShouldNotGenerateCode }
) { context: FrontendContext, input: KotlinCoreEnvironment ->
    lateinit var analysisResult: AnalysisResult

    do {
        val sourceFiles = input.getSourceFiles()

        require(context.config.produce == CompilerOutputKind.LIBRARY || sourceFiles.isEmpty()) {
            "Internal error: no source files should have been passed here (${sourceFiles.first().virtualFilePath} in particular)\n" +
                    "to produce binary (e.g. a ${context.config.produce.name.toLowerCaseAsciiOnly()})\n" +
                    "KonanDriver.kt::splitOntoTwoStages() must transform such compilation into two-stage compilation. Please report this here: https://kotl.in/issue"
        }

        // Build module descriptor and binding info.
        analysisResult = TopDownAnalyzerFacadeForKonan.analyzeFiles(sourceFiles, context)
        if (analysisResult.isError()) {
            throw KonanCompilationException()
        }
        if (analysisResult is AnalysisResult.RetryWithAdditionalRoots) {
            input.addKotlinSourceRoots(analysisResult.additionalKotlinRoots)
        }
    } while (analysisResult is AnalysisResult.RetryWithAdditionalRoots)

    val moduleDescriptor = analysisResult.moduleDescriptor
    val bindingContext = analysisResult.bindingContext

    if (analysisResult.shouldGenerateCode) {
        FrontendPhaseOutput.Full(moduleDescriptor, bindingContext, context.frontendServices, input)
    } else {
        FrontendPhaseOutput.ShouldNotGenerateCode
    }
}
