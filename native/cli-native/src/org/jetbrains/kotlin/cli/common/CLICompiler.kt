/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("DEPRECATION", "DEPRECATION_ERROR")
@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package org.jetbrains.kotlin.cli.common

import kotlinx.cinterop.toKString
import org.jetbrains.kotlin.cli.CliDiagnostics
import org.jetbrains.kotlin.cli.CliDiagnostics.COMPILER_ARGUMENTS_ERROR
import org.jetbrains.kotlin.cli.common.arguments.*
import org.jetbrains.kotlin.cli.common.messages.*
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.*
import org.jetbrains.kotlin.cli.report
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.KotlinCompilerVersion
import org.jetbrains.kotlin.config.Services
import org.jetbrains.kotlin.konan.file.File
import org.jetbrains.kotlin.metadata.deserialization.BinaryVersion
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.progress.CompilationCanceledException
import org.jetbrains.kotlin.progress.IncrementalNextRoundException
import org.jetbrains.kotlin.util.PerformanceManager
import platform.posix.fflush
import platform.posix.fputs
import platform.posix.getenv
import platform.posix.stderr
import kotlin.system.exitProcess

private object StderrAppendable : Appendable {
    override fun append(value: Char): Appendable {
        fputs(value.toString(), stderr)
        fflush(stderr)
        return this
    }

    override fun append(value: CharSequence?): Appendable {
        fputs((value ?: "null").toString(), stderr)
        fflush(stderr)
        return this
    }

    override fun append(value: CharSequence?, startIndex: Int, endIndex: Int): Appendable {
        val sub = (value ?: "null").subSequence(startIndex, endIndex).toString()
        fputs(sub, stderr)
        fflush(stderr)
        return this
    }
}

abstract class CLICompiler<A : CommonCompilerArguments> {
    abstract val platform: TargetPlatform

    val defaultPerformanceManager: PerformanceManager by lazy {
        createPerformanceManagerFor(platform)
    }

    var isReadingSettingsFromEnvironmentAllowed: Boolean = false

    protected open fun createPerformanceManager(arguments: A, services: Services): PerformanceManager =
        defaultPerformanceManager.apply {
            detailedPerf = arguments.detailedPerf
        }

    @Suppress("unused")
    fun execAndOutputXml(errStream: Appendable, services: Services, vararg args: String): ExitCode {
        return exec(errStream, services, MessageRenderer.XML, args)
    }

    fun execFullPathsInMessages(errStream: Appendable, args: Array<String>): ExitCode {
        return exec(errStream, Services.EMPTY, MessageRenderer.PLAIN_FULL_PATHS, args)
    }

    protected abstract fun createMetadataVersion(versionArray: IntArray): BinaryVersion

    /**
     * Main method for execution the new phased CLI compiler pipeline
     */
    protected abstract fun doExecutePhased(
        arguments: A,
        services: Services,
        basicMessageCollector: MessageCollector,
    ): ExitCode

    fun exec(errStream: Appendable, vararg args: String): ExitCode =
        exec(errStream, Services.EMPTY, defaultMessageRenderer(), args)

    fun exec(errStream: Appendable, messageRenderer: MessageRenderer, vararg args: String): ExitCode =
        exec(errStream, Services.EMPTY, messageRenderer, args)

    protected fun exec(
        errStream: Appendable,
        services: Services,
        messageRenderer: MessageRenderer,
        args: Array<out String>,
    ): ExitCode {
        val arguments = createArguments()
        parseCommandLineArguments(args.asList(), arguments)

        if (isReadingSettingsFromEnvironmentAllowed) {
            parseCommandLineArgumentsFromEnvironment(arguments)
        }

        val normalizedRenderer = normalizeCliMessageRenderer(messageRenderer)
        val collector = PrintingMessageCollector(errStream, normalizedRenderer, arguments.verbose)

        try {
            errStream.append(normalizedRenderer.renderPreamble())

            val errorMessages = validateArgumentsAllErrors(arguments.errors)
            if (errorMessages.isNotEmpty()) {
                errorMessages.forEach {
                    collector.report(ERROR, it, null)
                }
                collector.report(INFO, "Use -help for more information", null)
                return ExitCode.COMPILATION_ERROR
            }

            if (arguments.help || arguments.extraHelp) {
                errStream.append(normalizedRenderer.renderUsage(Usage.render(this, arguments)))
                return ExitCode.OK
            }

            return exec(collector, services, arguments)
        } finally {
            errStream.append(normalizedRenderer.renderConclusion())
        }
    }

    fun exec(messageCollector: MessageCollector, services: Services, arguments: A): ExitCode {
        printVersionIfNeeded(messageCollector, arguments)

        val fixedMessageCollector = if (arguments.suppressWarnings && !arguments.allWarningsAsErrors) {
            FilteringMessageCollector(messageCollector) { it == WARNING }
        } else {
            messageCollector
        }

        fixedMessageCollector.reportArgumentParseProblems(arguments)
        return doExecutePhased(arguments, services, fixedMessageCollector)
    }

    abstract fun createArguments(): A

    fun parseArguments(args: Array<out String>, arguments: A) {
        parseCommandLineArguments(args.asList(), arguments)
        val message = validateArguments(arguments.errors)
        if (message != null) {
            throw IllegalArgumentException(message)
        }
    }

    private fun <A : CommonCompilerArguments> printVersionIfNeeded(messageCollector: MessageCollector, arguments: A) {
        if (arguments.version) {
            val jreVersion = getenv("JAVA_RUNTIME_VERSION")?.toKString()?.takeIf { it.isNotEmpty() } ?: "21.0.12+1.1-LTS"
            messageCollector.report(INFO, "${executableScriptFileName()} ${KotlinCompilerVersion.VERSION} (JRE $jreVersion)")
        }
    }

    abstract fun executableScriptFileName(): String

    companion object {
        const val SCRIPT_PLUGIN_REGISTRAR_NAME =
            "org.jetbrains.kotlin.scripting.compiler.plugin.ScriptingCompilerConfigurationComponentRegistrar"
        const val SCRIPT_PLUGIN_COMMANDLINE_PROCESSOR_NAME =
            "org.jetbrains.kotlin.scripting.compiler.plugin.ScriptingCommandLineProcessor"
        const val SCRIPT_PLUGIN_K2_REGISTRAR_NAME =
            "org.jetbrains.kotlin.scripting.compiler.plugin.ScriptingK2CompilerPluginRegistrar"

        fun defaultMessageRenderer(): MessageRenderer =
            when (getenv(MessageRenderer.PROPERTY_KEY)?.toKString()) {
                MessageRenderer.XML.getName() -> MessageRenderer.XML
                MessageRenderer.GRADLE_STYLE.getName() -> MessageRenderer.GRADLE_STYLE
                MessageRenderer.XCODE_STYLE.getName() -> MessageRenderer.XCODE_STYLE
                MessageRenderer.WITHOUT_PATHS.getName(), "Pathless" -> MessageRenderer.WITHOUT_PATHS
                MessageRenderer.PLAIN_FULL_PATHS.getName(), "FullPath" -> MessageRenderer.PLAIN_FULL_PATHS
                else -> MessageRenderer.PLAIN_RELATIVE_PATHS
            }

        fun doMain(compiler: CLICompiler<*>, args: Array<String>) {
            if (CompilerSystemProperties.KOTLIN_COLORS_ENABLED_PROPERTY.value == null) {
                CompilerSystemProperties.KOTLIN_COLORS_ENABLED_PROPERTY.value = "true"
            }

            val exitCode = doMainNoExit(compiler, args)
            if (exitCode != ExitCode.OK) {
                exitProcess(exitCode.code)
            }
        }

        fun doMainNoExit(
            compiler: CLICompiler<*>,
            args: Array<String>,
            messageRenderer: MessageRenderer = defaultMessageRenderer(),
        ): ExitCode = try {
            compiler.exec(StderrAppendable, messageRenderer, *args)
        } catch (e: Throwable) {
            StderrAppendable.append((e.message ?: e.toString()) + "\n")
            ExitCode.INTERNAL_ERROR
        }
    }
}

fun checkPluginsArguments(
    configuration: CompilerConfiguration,
    useK2: Boolean,
    pluginClasspaths: List<String>,
    pluginOptions: List<String>,
    pluginConfigurations: List<String>,
): Boolean {
    var hasErrors = false

    for (classpath in pluginClasspaths) {
        if (!File(classpath).exists) {
            configuration.report(COMPILER_ARGUMENTS_ERROR, "Plugin classpath entry points to a non-existent location: $classpath")
        }
    }

    if (pluginConfigurations.isNotEmpty()) {
        configuration.report(CliDiagnostics.COMPILER_PLUGIN_ARG_IS_EXPERIMENTAL, "Argument -Xcompiler-plugin is experimental")

        if (!useK2) {
            hasErrors = true
            configuration.report(
                COMPILER_ARGUMENTS_ERROR,
                "-Xcompiler-plugin argument is allowed only for language version 2.0. Please use -Xplugin argument for language version 1.9 and below"
            )
        }
        if (pluginClasspaths.isNotEmpty() || pluginOptions.isNotEmpty()) {
            hasErrors = true
            val message = buildString {
                appendLine("Mixing legacy and modern plugin arguments is prohibited. Please use only one syntax")
                appendLine("Legacy arguments:")
                if (pluginClasspaths.isNotEmpty()) {
                    appendLine("  -Xplugin=${pluginClasspaths.joinToString(",")}")
                }
                pluginOptions.forEach {
                    appendLine("  -P $it")
                }
                appendLine("Modern arguments:")
                pluginConfigurations.forEach {
                    appendLine("  -Xcompiler-plugin=$it")
                }
            }
            configuration.report(COMPILER_ARGUMENTS_ERROR, message)
        }
    }
    return !hasErrors
}

fun Throwable.hasOOMCause(): Boolean {
    var current: Throwable? = this
    while (current != null) {
        if (current::class.simpleName == "OutOfMemoryError") return true
        current = current.cause
    }
    return false
}

fun MessageCollector.reportCompilationCancelled(e: CompilationCanceledException) {
    if (e !is IncrementalNextRoundException) {
        report(INFO, "Compilation was canceled", null)
    }
}

object Usage {
    const val BAT_DELIMITER_CHARACTERS_NOTE: String =
        "Note: on Windows, arguments that contain delimiter characters (whitespace, =, ;, ,) need to be surrounded with double quotes (\")."

    private const val OPTION_NAME_PADDING_WIDTH = 29
    private val PADDING_STRING = " ".repeat(OPTION_NAME_PADDING_WIDTH)

    fun <A : CommonCompilerArguments> render(compiler: CLICompiler<A>, arguments: A): String {
        val extraHelp = arguments.extraHelp
        return buildString {
            appendLine("Usage: " + compiler.executableScriptFileName() + " <options> <source files>")
            appendLine("where " + (if (extraHelp) "advanced" else "possible") + " options include:")

            for (descriptor in allNativeArgumentDescriptors) {
                fieldUsage(this, descriptor, extraHelp)
            }

            if (extraHelp) {
                appendLine()
                appendLine("Advanced options are non-standard and may be changed or removed without any notice.")
            } else {
                renderOptionJUsage(this)
                renderArgfileUsage(this)
            }

            if (isWindows) {
                appendLine()
                appendLine(BAT_DELIMITER_CHARACTERS_NOTE)
            }

            if (!extraHelp) {
                appendLine()
                appendLine("For details, see https://kotl.in/cli")
            }
        }
    }

    private fun fieldUsage(sb: StringBuilder, descriptor: NativeArgumentDescriptor, extraHelp: Boolean) {
        val argument = descriptor.argument
        if (argument.isInternal || extraHelp != argument.isAdvanced) return

        val startLength = sb.length
        sb.append("  ")
        sb.append(argument.value)

        if (argument.shortName.isNotEmpty()) {
            sb.append(" (")
            sb.append(argument.shortName)
            sb.append(')')
        }

        if (argument.valueDescription.isNotEmpty()) {
            sb.append(if (argument.isAdvanced) '=' else ' ')
            sb.append(argument.valueDescription)
        }

        var margin = startLength + OPTION_NAME_PADDING_WIDTH - 1
        if (sb.length >= margin + 5) {
            sb.appendLine()
            margin += sb.length - startLength
        }
        while (sb.length < margin) {
            sb.append(' ')
        }

        sb.append(' ')
        sb.appendLine(argument.description.replace("\n", "\n" + PADDING_STRING))
    }

    private fun renderOptionJUsage(sb: StringBuilder) {
        val descriptionStart = sb.length + OPTION_NAME_PADDING_WIDTH
        sb.append("  -J<option>")
        while (sb.length < descriptionStart) {
            sb.append(" ")
        }
        sb.appendLine("Pass an option directly to JVM.")
    }

    private fun renderArgfileUsage(sb: StringBuilder) {
        val descriptionStart = sb.length + OPTION_NAME_PADDING_WIDTH
        sb.append("  ")
        sb.append(ARGFILE_ARGUMENT)
        sb.append("<argfile>")
        while (sb.length < descriptionStart) {
            sb.append(' ')
        }
        sb.appendLine("Read compiler arguments and file paths from the given file.")
    }
}
