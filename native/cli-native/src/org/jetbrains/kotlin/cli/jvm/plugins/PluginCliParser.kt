/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("DEPRECATION", "DEPRECATION_ERROR")
@file:OptIn(org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi::class)

package org.jetbrains.kotlin.cli.jvm.plugins

import com.intellij.openapi.Disposable
import org.jetbrains.kotlin.cli.CliDiagnostics.COMPILER_ARGUMENTS_ERROR
import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.plugins.*
import org.jetbrains.kotlin.cli.report
import org.jetbrains.kotlin.cli.reportException
import org.jetbrains.kotlin.compiler.plugin.*
import org.jetbrains.kotlin.config.CompilerConfiguration

object PluginCliParser {
    fun loadPluginsSafe(
        pluginClasspaths: Array<String>?,
        pluginOptions: Array<String>?,
        pluginConfigurations: Array<String>?,
        configuration: CompilerConfiguration,
        parentDisposable: Disposable,
    ): ExitCode = loadPluginsSafe(
        pluginClasspaths?.asList().orEmpty(),
        pluginOptions?.asList().orEmpty(),
        pluginConfigurations?.asList().orEmpty(),
        emptyList(),
        configuration,
        parentDisposable,
    )

    fun loadPluginsSafe(
        pluginClasspaths: Array<String>?,
        pluginOptions: Array<String>?,
        pluginConfigurations: Array<String>?,
        pluginOrderConstraints: Array<String>?,
        configuration: CompilerConfiguration,
        parentDisposable: Disposable,
    ): ExitCode = loadPluginsSafe(
        pluginClasspaths?.asList().orEmpty(),
        pluginOptions?.asList().orEmpty(),
        pluginConfigurations?.asList().orEmpty(),
        pluginOrderConstraints?.asList().orEmpty(),
        configuration,
        parentDisposable,
    )

    fun loadPluginsSafe(
        pluginClasspaths: Collection<String>,
        pluginOptions: Collection<String>,
        pluginConfigurations: Collection<String>,
        configuration: CompilerConfiguration,
        parentDisposable: Disposable,
    ): ExitCode = loadPluginsSafe(
        pluginClasspaths,
        pluginOptions,
        pluginConfigurations,
        emptyList(),
        configuration,
        parentDisposable,
    )

    fun loadPluginsSafe(
        @Suppress("UNUSED_PARAMETER") pluginClasspaths: Collection<String>,
        pluginOptions: Collection<String>,
        pluginConfigurations: Collection<String>,
        pluginOrderConstraints: Collection<String>,
        configuration: CompilerConfiguration,
        @Suppress("UNUSED_PARAMETER") parentDisposable: Disposable,
    ): ExitCode = loadPluginsSafe(configuration) {
        val orderConstraints = pluginOrderConstraints.map { rawConstraint ->
            extractPluginOrderConstraint(rawConstraint)
                ?: throw PluginProcessingException("Could not parse plugin order constraint: $rawConstraint")
        }
        if (orderConstraints.isNotEmpty()) {
            val registrarsById = configuration.getList(CompilerPluginRegistrar.COMPILER_PLUGIN_REGISTRARS)
                .filter { it.pluginId.isNotEmpty() }
                .associateBy { it.pluginId }
            for (constraint in orderConstraints) {
                if (constraint.before == constraint.after && constraint.before in registrarsById) {
                    throw PluginProcessingException(
                        "Compiler plugin '${constraint.before}' is part of an constraint cycle: ${orderConstraints.joinToString(", ")}"
                    )
                }
            }
        }

        processCompilerPluginsOptions(configuration, pluginOptions, emptyList())

        val parsedConfigurations = extractPluginClasspathAndOptions(pluginConfigurations)
        for (pluginConfiguration in parsedConfigurations) {
            throw PluginProcessingException("No plugins found in given classpath: ${pluginConfiguration.classpath.joinToString(",")}")
        }
    }

    private fun loadPluginsSafe(configuration: CompilerConfiguration, action: () -> Unit): ExitCode {
        try {
            action()
            return ExitCode.OK
        } catch (e: PluginProcessingException) {
            configuration.report(COMPILER_ARGUMENTS_ERROR, e.message!!)
        } catch (e: PluginCliOptionProcessingException) {
            val message = e.message + "\n\n" + cliPluginUsageString(e.pluginId, e.options)
            configuration.report(COMPILER_ARGUMENTS_ERROR, message)
        } catch (e: CliOptionProcessingException) {
            configuration.report(COMPILER_ARGUMENTS_ERROR, e.message!!)
        } catch (t: Throwable) {
            configuration.reportException(t)
        }
        return ExitCode.INTERNAL_ERROR
    }
}
