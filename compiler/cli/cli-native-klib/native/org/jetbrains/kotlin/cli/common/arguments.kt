/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.common

import org.jetbrains.kotlin.cli.CliDiagnostics
import org.jetbrains.kotlin.cli.CliDiagnostics.COMPILER_ARGUMENTS_ERROR
import org.jetbrains.kotlin.cli.CliDiagnostics.COMPILER_ARGUMENTS_WARNING
import org.jetbrains.kotlin.cli.common.arguments.*
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.report
import org.jetbrains.kotlin.cli.reportInfo
import org.jetbrains.kotlin.config.*
import org.jetbrains.kotlin.diagnostics.KtSourcelessDiagnosticFactory
import org.jetbrains.kotlin.konan.file.File
import org.jetbrains.kotlin.metadata.deserialization.BinaryVersion
import org.jetbrains.kotlin.types.AbstractTypeChecker
import org.jetbrains.kotlin.types.FlexibleTypeImpl
import org.jetbrains.kotlin.utils.DFS

fun CompilerConfiguration.setupCommonArguments(
    arguments: CommonCompilerArguments,
    createMetadataVersion: ((IntArray) -> BinaryVersion)? = null
) {
    treatWarningsAsErrors = arguments.allWarningsAsErrors
    put(CommonConfigurationKeys.DISABLE_INLINE, arguments.noInline)
    put(CommonConfigurationKeys.USE_FIR_EXTRA_CHECKERS, arguments.extraWarnings)
    put(CommonConfigurationKeys.METADATA_KLIB, arguments.metadataKlib)

    val modelDumpDirString = arguments.dumpArgumentsDir
    val modelDumpDir = modelDumpDirString?.takeIf { it.isNotEmpty() && File(it).isDirectory }

    putIfNotNull(CommonConfigurationKeys.DUMP_MODEL, modelDumpDir)
    put(CommonConfigurationKeys.REPORT_OUTPUT_FILES, arguments.reportOutputFiles)
    put(CommonConfigurationKeys.INCREMENTAL_COMPILATION, arguments.incrementalCompilation == true)
    put(CommonConfigurationKeys.ALLOW_ANY_SCRIPTS_IN_SOURCE_ROOTS, arguments.allowAnyScriptsInSourceRoots)
    put(CommonConfigurationKeys.IGNORE_CONST_OPTIMIZATION_ERRORS, arguments.ignoreConstOptimizationErrors)
    put(CLIConfigurationKeys.RENDER_DIAGNOSTIC_INTERNAL_NAME, arguments.renderInternalDiagnosticNames)

    val irVerificationMode = arguments.verifyIr?.let { verifyIrString ->
        IrVerificationMode.resolveMode(verifyIrString).also {
            if (it == null) {
                this.report(COMPILER_ARGUMENTS_ERROR, "Unsupported IR verification mode $verifyIrString")
            }
        }
    } ?: IrVerificationMode.NONE
    put(CommonConfigurationKeys.VERIFY_IR, irVerificationMode)

    if (arguments.disableIrCheckers.isNotEmpty() && irVerificationMode == IrVerificationMode.NONE) {
        this.report(
            COMPILER_ARGUMENTS_WARNING,
            "'-Xdisable-ir-checkers' has no effect unless '-Xverify-ir=warning' or '-Xverify-ir=error' is specified"
        )
    }
    put(CommonConfigurationKeys.DISABLE_IR_CHECKERS, arguments.disableIrCheckers.toList())
    if (arguments.enableAdditionalIrCheckers.isNotEmpty() && irVerificationMode == IrVerificationMode.NONE) {
        this.report(
            COMPILER_ARGUMENTS_WARNING,
            "'-Xadditional-ir-checkers' has no effect unless '-Xverify-ir=warning' or '-Xverify-ir=error' is specified"
        )
    }
    put(CommonConfigurationKeys.ADDITIONAL_IR_CHECKERS, arguments.enableAdditionalIrCheckers.toList())

    @Suppress("DEPRECATION")
    if (arguments.useFirExperimentalCheckers) {
        put(CommonConfigurationKeys.USE_FIR_EXPERIMENTAL_CHECKERS, true)
        this.report(
            COMPILER_ARGUMENTS_WARNING,
            "'-Xuse-fir-experimental-checkers' is deprecated and will be removed in a future release"
        )
    }

    setupMetadataVersion(arguments, createMetadataVersion)

    setupLanguageVersionSettings(arguments)

    val usesK2 = languageVersionSettings.languageVersion.usesK2
    put(CommonConfigurationKeys.USE_FIR, usesK2)
    put(CommonConfigurationKeys.USE_LIGHT_TREE, arguments.useFirLT)
    buildHmppModuleStructure(arguments)?.let { put(CommonConfigurationKeys.HMPP_MODULE_STRUCTURE, it) }

    if (arguments.debugLevelCompilerChecks) {
        FlexibleTypeImpl.RUN_SLOW_ASSERTIONS = true
        AbstractTypeChecker.RUN_SLOW_ASSERTIONS = true
    }

    put(CommonConfigurationKeys.DONT_SORT_SOURCE_FILES, arguments.dontSortSourceFiles)
}

fun CompilerConfiguration.setupMetadataVersion(
    arguments: CommonCompilerArguments,
    createMetadataVersion: ((IntArray) -> BinaryVersion)?,
) {
    val metadataVersionString = arguments.metadataVersion
    if (metadataVersionString != null) {
        val versionArray = BinaryVersion.parseVersionArray(metadataVersionString)
        when {
            versionArray == null -> this.report(
                COMPILER_ARGUMENTS_ERROR, "Invalid metadata version: $metadataVersionString", null
            )
            createMetadataVersion == null -> throw IllegalStateException("Unable to create metadata version: missing argument")
            else -> put(CommonConfigurationKeys.METADATA_VERSION, createMetadataVersion(versionArray))
        }
    }
}

fun CommonCompilerArgumentsConfigurator.Reporter.Companion.fromConfiguration(configuration: CompilerConfiguration): CommonCompilerArgumentsConfigurator.Reporter {
    return object : CommonCompilerArgumentsConfigurator.Reporter {
        override fun reportWarning(message: String) {
            configuration.report(COMPILER_ARGUMENTS_WARNING, message)
        }

        override fun reportError(message: String) {
            configuration.report(COMPILER_ARGUMENTS_ERROR, message)
        }

        override fun report(factory: KtSourcelessDiagnosticFactory, message: String) {
            configuration.report(factory, message)
        }

        override fun info(message: String) {
            configuration.reportInfo(message)
        }

        override fun withLanguageVersionSettings(languageVersionSettings: LanguageVersionSettings): CommonCompilerArgumentsConfigurator.Reporter {
            val newConfiguration = configuration.copy().apply {
                this.languageVersionSettings = languageVersionSettings
            }
            return fromConfiguration(newConfiguration)
        }
    }
}

fun CompilerConfiguration.setupLanguageVersionSettings(arguments: CommonCompilerArguments) {
    val reporter = CommonCompilerArgumentsConfigurator.Reporter.fromConfiguration(this)
    languageVersionSettings = arguments.toLanguageVersionSettings(reporter)
}

private val FRAGMENTS_ARG_NAME = CommonCompilerArguments::fragments.cliArgument
private val FRAGMENT_REFINES_ARG_NAME = CommonCompilerArguments::fragmentRefines.cliArgument
private val FRAGMENT_SOURCES_ARG_NAME = CommonCompilerArguments::fragmentSources.cliArgument
private val FRAGMENT_DEPENDENCIES_ARG_NAME = CommonCompilerArguments::fragmentDependencies.cliArgument
private val FRAGMENT_FRIEND_DEPENDENCIES_ARG_NAME = CommonCompilerArguments::fragmentFriendDependencies.cliArgument

private fun CompilerConfiguration.buildHmppModuleStructure(arguments: CommonCompilerArguments): HmppCliModuleStructure? {
    val rawFragments = arguments.fragments
    val rawFragmentSources = arguments.fragmentSources
    val rawFragmentRefines = arguments.fragmentRefines

    fun reportError(message: String) {
        this.report(COMPILER_ARGUMENTS_ERROR, message)
    }

    fun reportWarning(message: String) {
        this.report(COMPILER_ARGUMENTS_WARNING, message)
    }

    if (rawFragments.isEmpty()) {
        if (rawFragmentRefines.isNotEmpty()) {
            reportError("$FRAGMENT_REFINES_ARG_NAME flag can not be used without $FRAGMENTS_ARG_NAME")
        }
        return null
    }

    if (!languageVersionSettings.languageVersion.usesK2) {
        reportWarning("$FRAGMENTS_ARG_NAME flag is not supported for language version < 2.0")
        return null
    }

    val sourcesByFragmentName: Map<String, Set<String>> = rawFragments.associateWith { mutableSetOf<String>() }.apply {
        rawFragmentSources.forEach { rawFragmentSourceArg ->
            val split = rawFragmentSourceArg.split(":", limit = 2)
            if (split.size < 2) {
                reportError(
                    "Incorrect syntax for $FRAGMENT_SOURCES_ARG_NAME argument. " +
                            "`<module name>:<source file>` expected but got `$rawFragmentSourceArg`"
                )
                return@forEach
            }
            val fragmentName = split[0]
            val fragmentSource = split[1]

            getOrElse(fragmentName) {
                reportError(
                    "Passed $rawFragmentSourceArg, " +
                            "but fragment `$fragmentName` of source file $fragmentSource is not specified in $FRAGMENTS_ARG_NAME"
                )
                return@forEach
            }.add(fragmentSource)
        }
    }

    var modules = sourcesByFragmentName.map { [fragmentName, sources] -> HmppCliModule(fragmentName, sources) }

    var wasError = false
    for (i in modules.indices) {
        val m1 = modules[i]
        for (j in (i + 1) until modules.size) {
            val m2 = modules[j]
            val commonFiles = m1.sources.intersect(m2.sources)
            if (commonFiles.isNotEmpty()) {
                val message = buildString {
                    if (commonFiles.size == 1) {
                        append("File '${commonFiles.single()}'")
                    } else {
                        append("Files ")
                        append(commonFiles.joinToString(", ") { "'$it'" })
                    }
                    append(
                        " can be a part of only one module, but is listed as a source for both `${m1.name}` and `${m2.name}`, " +
                                "please check you $FRAGMENT_SOURCES_ARG_NAME options."
                    )
                }
                reportError(message)
                wasError = true
            }
        }
    }

    for (source in arguments.freeArgs) {
        if (source.endsWith(".java")) continue
        if (modules.none { source in it.sources }) {
            reportError("Source '$source' does not belong to any module")
            wasError = true
        }
    }

    if (wasError) {
        return null
    }

    if (modules.size == 1) {
        if (rawFragmentRefines.isNotEmpty()) {
            reportError("$FRAGMENT_REFINES_ARG_NAME flag is specified but there is only one module declared")
        }
        return HmppCliModuleStructure(
            modules,
            sourceDependencies = emptyMap(),
            moduleDependencies = emptyMap(),
            friendDependencies = emptyMap(),
            incrementalDependencies = emptyMap(),
        )
    }

    val duplicatedModules = modules.filter { module -> modules.count { it.name == module.name } > 1 }

    if (duplicatedModules.isNotEmpty()) {
        reportError("There are multiple modules with same name(s): ${duplicatedModules.distinct().joinToString(", ") { it.name }}")
        return null
    }

    val moduleByName = modules.associateBy { it.name }

    val sourceDependencies: Map<HmppCliModule, List<HmppCliModule>> = rawFragmentRefines.mapNotNull { rawFragmentRefinesEdge ->
        val split = rawFragmentRefinesEdge.split(":")
        if (split.size != 2) {
            reportError(
                "Incorrect syntax for $FRAGMENT_REFINES_ARG_NAME argument. " +
                        "Expected <fromModuleName>:<onModuleName> but got `$rawFragmentRefines`"
            )
            return@mapNotNull null
        }
        val moduleName1 = split[0]
        val moduleName2 = split[1]

        fun findModule(name: String): HmppCliModule? {
            return moduleByName[name].also { module ->
                if (module == null) {
                    reportError("`$FRAGMENT_REFINES_ARG_NAME=$rawFragmentRefinesEdge` Fragment `$name` not found in $FRAGMENTS_ARG_NAME arguments")
                }
            }
        }

        val module1 = findModule(moduleName1)
        val module2 = findModule(moduleName2)
        if (module1 == null || module2 == null) return@mapNotNull null
        module1 to module2
    }.groupBy(
        keySelector = { it.first },
        valueTransform = { it.second }
    )

    modules = DFS.topologicalOrder(modules) { sourceDependencies[it].orEmpty() }.asReversed()

    modules.forEachIndexed { i, module ->
        val dependencies = sourceDependencies[module].orEmpty()
        val previousModules = modules.subList(0, i)
        if (dependencies.any { it !in previousModules }) {
            reportError("There is a cycle in dependencies of module `${module.name}`")
        }
    }

    if (arguments.fragmentDependencies.isNotEmpty() && !arguments.separateKmpCompilationScheme) {
        reportError("$FRAGMENT_DEPENDENCIES_ARG_NAME flag could be used only with ${CommonCompilerArguments::separateKmpCompilationScheme.cliArgument}")
    }
    if (arguments.fragmentFriendDependencies.isNotEmpty() && !arguments.separateKmpCompilationScheme) {
        reportError("$FRAGMENT_FRIEND_DEPENDENCIES_ARG_NAME flag could be used only with ${CommonCompilerArguments::separateKmpCompilationScheme.cliArgument}")
    }

    fun buildFragmentDependencyMap(arguments: Array<String>?, argumentName: String): Map<HmppCliModule, MutableList<String>> {
        return buildMap {
            for (argument in arguments.orEmpty()) {
                val splitArg = argument.split(":", limit = 2)
                if (splitArg.size != 2) {
                    reportError(
                        "Incorrect syntax for $argumentName argument. " +
                                "Expected <moduleName>:<path> but got `$argument`"
                    )
                    continue
                }
                val [moduleName, dependency] = splitArg
                val module = moduleByName[moduleName] ?: run {
                    reportError("Module `$moduleName` not found in $FRAGMENTS_ARG_NAME arguments")
                    continue
                }
                val dependencies = getOrPut(module) { mutableListOf() }
                dependencies += dependency
            }
        }
    }

    val moduleDependencies = buildFragmentDependencyMap(arguments.fragmentDependencies, FRAGMENT_DEPENDENCIES_ARG_NAME)
    val friendDependencies = buildFragmentDependencyMap(arguments.fragmentFriendDependencies, FRAGMENT_FRIEND_DEPENDENCIES_ARG_NAME)

    return HmppCliModuleStructure(
        modules,
        sourceDependencies,
        moduleDependencies,
        friendDependencies,
        incrementalDependencies = emptyMap(),
    )
}
