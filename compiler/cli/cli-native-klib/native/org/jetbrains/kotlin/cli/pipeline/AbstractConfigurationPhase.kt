/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.pipeline

import org.jetbrains.kotlin.backend.common.CommonBackendErrors
import org.jetbrains.kotlin.backend.common.actualizer.IrActualizationErrors
import org.jetbrains.kotlin.backend.common.diagnostics.SerializationErrors
import org.jetbrains.kotlin.cli.common.*
import org.jetbrains.kotlin.cli.common.arguments.*
import org.jetbrains.kotlin.cli.diagnosticFactoriesStorage
import org.jetbrains.kotlin.cli.extensionsStorage
import org.jetbrains.kotlin.cli.reportInfo
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.compiler.plugin.TEST_ONLY_PLUGIN_REGISTRATION_CALLBACK
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.config.Services
import org.jetbrains.kotlin.config.perfManager
import org.jetbrains.kotlin.config.phaser.Action
import org.jetbrains.kotlin.ir.validation.IrValidationDiagnostics
import org.jetbrains.kotlin.metadata.deserialization.BinaryVersion

private val SERVICES_CONFIGURATION_KEY = CompilerConfigurationKey.create<Services>("SERVICES")

var CompilerConfiguration.services: Services?
    get() = get(SERVICES_CONFIGURATION_KEY)
    set(value) { putIfNotNull(SERVICES_CONFIGURATION_KEY, value) }

/**
 * Updates the compiler configuration using information from the compiler arguments
 */
abstract class ConfigurationUpdater<in A : CommonCompilerArguments> {
    abstract fun fillConfiguration(input: ArgumentsPipelineArtifact<A>, configuration: CompilerConfiguration)
}

abstract class AbstractConfigurationPhase<A : CommonCompilerArguments>(
    name: String,
    preActions: Set<Action<ArgumentsPipelineArtifact<A>, PipelineContext>> = emptySet(),
    postActions: Set<Action<ConfigurationPipelineArtifact, PipelineContext>> = emptySet(),
    val configurationUpdaters: List<ConfigurationUpdater<A>>,
) : PipelinePhase<ArgumentsPipelineArtifact<A>, ConfigurationPipelineArtifact>(name, preActions, postActions) {
    override fun executePhase(input: ArgumentsPipelineArtifact<A>): ConfigurationPipelineArtifact {
        val configuration = input.configuration
        configuration.setupCommonConfiguration(input)

        configuration.diagnosticFactoriesStorage?.registerDiagnosticContainers(
            IrActualizationErrors,
            CommonBackendErrors,
            SerializationErrors,
            IrValidationDiagnostics,
        )

        for (filler in configurationUpdaters) {
            filler.fillConfiguration(input, configuration)
        }

        setupPluginExtensions(configuration)

        if (input.arguments.printConfiguration || input.arguments.verbose) {
            configuration.reportInfo(configuration.toString())
        }

        return ConfigurationPipelineArtifact(configuration, input.rootDisposable)
    }

    protected abstract fun createMetadataVersion(versionArray: IntArray): BinaryVersion
    protected open fun provideCustomScriptingPluginOptions(arguments: A): List<String> = emptyList()

    private fun CompilerConfiguration.setupCommonConfiguration(input: ArgumentsPipelineArtifact<A>) {
        val [arguments, services, _, _, performanceManager] = input
        perfManager = performanceManager
        this.services = services
        printVersion = arguments.version
        scriptMode = arguments.script
        replMode = arguments.repl
        setupCommonArguments(arguments, ::createMetadataVersion)
    }

    @OptIn(ExperimentalCompilerApi::class)
    private fun setupPluginExtensions(configuration: CompilerConfiguration) {
        val extensionStorage = configuration.extensionsStorage ?: CompilerPluginRegistrar.ExtensionStorage()
        for (registrar in configuration.getList(CompilerPluginRegistrar.COMPILER_PLUGIN_REGISTRARS)) {
            with(registrar) {
                extensionStorage.registerExtensions(configuration)
            }
        }
        configuration.get(TEST_ONLY_PLUGIN_REGISTRATION_CALLBACK)?.invoke(extensionStorage)
        configuration.extensionsStorage = extensionStorage
    }
}
