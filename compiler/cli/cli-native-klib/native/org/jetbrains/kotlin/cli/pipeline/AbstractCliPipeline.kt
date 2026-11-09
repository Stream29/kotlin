/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.pipeline

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.analyzer.CompilationErrorException
import org.jetbrains.kotlin.cli.common.*
import org.jetbrains.kotlin.cli.common.arguments.*
import org.jetbrains.kotlin.cli.common.fir.FirDiagnosticsCompilerResultsReporter
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.GroupingMessageCollector
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.messages.MessageCollectorUtil
import org.jetbrains.kotlin.cli.pipeline.CheckCompilationErrors.CheckDiagnosticCollector
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.Services
import org.jetbrains.kotlin.config.phaser.CompilerPhase
import org.jetbrains.kotlin.config.phaser.PhaseConfig
import org.jetbrains.kotlin.config.phaser.invokeToplevel
import org.jetbrains.kotlin.progress.CompilationCanceledException
import org.jetbrains.kotlin.progress.CompilationCanceledStatus
import org.jetbrains.kotlin.progress.ProgressIndicatorAndCompilationCanceledStatus
import org.jetbrains.kotlin.psi.impl.KotlinPsiBootstrap
import org.jetbrains.kotlin.util.CompilerType
import org.jetbrains.kotlin.util.PerformanceManager
import org.jetbrains.kotlin.util.forEachStringMeasurement
import org.jetbrains.kotlin.utils.addToStdlib.shouldNotBeCalled

abstract class AbstractCliPipeline<A : CommonCompilerArguments> {
    fun execute(
        arguments: A,
        services: Services,
        originalMessageCollector: MessageCollector,
    ): ExitCode =
        executeAndReturnPipeLineArtifact(arguments, services, originalMessageCollector).exitCode

    fun executeAndReturnPipeLineArtifact(
        arguments: A,
        services: Services,
        originalMessageCollector: MessageCollector,
        providedDisposable: Disposable? = null,
    ): PipelineArtifactWithExitCode {
        val canceledStatus = services[CompilationCanceledStatus::class]
        ProgressIndicatorAndCompilationCanceledStatus.setCompilationCanceledStatus(canceledStatus)
        val rootDisposable = providedDisposable ?: Disposer.newDisposable("Disposable for ${AbstractCliPipeline::class.simpleName}.execImpl")
        val performanceManager = createPerformanceManager(arguments, services).apply { compilerType = CompilerType.K2 }
        if (arguments.reportPerf || arguments.dumpPerf != null) {
            performanceManager.enableExtendedStats()
        }

        val messageCollector = GroupingMessageCollector(
            originalMessageCollector,
            arguments.allWarningsAsErrors,
            arguments.reportAllWarnings
        )
        val argumentsInput = ArgumentsPipelineArtifact(
            arguments,
            services,
            rootDisposable,
            messageCollector,
            performanceManager
        )

        fun reportException(e: Throwable): ExitCodeArtifact {
            MessageCollectorUtil.reportException(messageCollector, e)
            val code = if (e is OutOfMemoryError || e.hasOOMCause()) ExitCode.OOM_ERROR else ExitCode.INTERNAL_ERROR
            return ExitCodeArtifact(code)
        }

        fun reportCompilationCanceled(e: CompilationCanceledException): ExitCodeArtifact {
            messageCollector.report(CompilerMessageSeverity.INFO, "Compilation was canceled: ${e.message ?: ""}".trimEnd(':', ' '), null)
            return ExitCodeArtifact(ExitCode.OK)
        }

        var ownedRuntime: Disposable? = null
        return try {
            val app = ApplicationManager.getApplicationOrNull() ?: run {
                val runtime = KotlinPsiBootstrap.install(PluginId.getId("org.jetbrains.kotlin.native.klib.cli"))
                ownedRuntime = runtime
                runtime.application
            }
            val result = if (!app.isReadAccessAllowed) {
                app.runReadAction { runPhasedPipeline(argumentsInput) }
            } else {
                runPhasedPipeline(argumentsInput)
            }
            if (!isNativeOneStage) {
                performanceManager.notifyCompilationFinished()
                if (arguments.reportPerf) {
                    messageCollector.report(CompilerMessageSeverity.LOGGING, "PERF: " + performanceManager.getTargetInfo())
                    performanceManager.forEachStringMeasurement {
                        messageCollector.report(CompilerMessageSeverity.LOGGING, "PERF: $it", null)
                    }
                }

                if (arguments.dumpPerf != null) {
                    performanceManager.dumpPerformanceReport(arguments.dumpPerf!!)
                }
            }
            if (messageCollector.hasErrors()) ExitCodeArtifact(ExitCode.COMPILATION_ERROR) else result
        } catch (_: CompilationErrorException) {
            ExitCodeArtifact(ExitCode.COMPILATION_ERROR)
        } catch (e: RuntimeException) {
            when (val cause = e.cause) {
                is CompilationCanceledException -> reportCompilationCanceled(cause)
                else -> reportException(e)
            }
        } catch (t: Throwable) {
            reportException(t)
        } finally {
            messageCollector.flush()
            if (providedDisposable == null) {
                Disposer.dispose(rootDisposable)
            }
            ownedRuntime?.dispose()
            ProgressIndicatorAndCompilationCanceledStatus.setCompilationCanceledStatus(null)
        }
    }

    private fun Throwable.hasOOMCause(): Boolean {
        var current: Throwable? = this.cause
        while (current != null) {
            if (current is OutOfMemoryError) return true
            current = current.cause
        }
        return false
    }

    private fun runPhasedPipeline(input: ArgumentsPipelineArtifact<A>): PipelineArtifactWithExitCode {
        val compoundPhase = createCompoundPhase(input.arguments)

        val phaseConfig = PhaseConfig()
        val context = PipelineContext(
            input.performanceManager,
            kaptMode = isKaptMode(input.arguments)
        )
        return try {
            val result = compoundPhase.invokeToplevel(
                phaseConfig,
                context,
                input
            )
            when (result) {
                is PipelineArtifactWithExitCode -> result
                else -> ExitCodeArtifact(ExitCode.OK)
            }
        } catch (e: PipelineStepException) {
            val configuration = input.configuration
            if (e.definitelyCompilationError || CheckDiagnosticCollector.checkHasErrors(configuration)) {
                ExitCodeArtifact(ExitCode.COMPILATION_ERROR)
            } else {
                ExitCodeArtifact(ExitCode.OK)
            }
        } catch (_: SuccessfulPipelineExecutionException) {
            ExitCodeArtifact(ExitCode.OK)
        } finally {
            val configuration = input.configuration
            FirDiagnosticsCompilerResultsReporter.reportToMessageCollector(configuration.diagnosticsCollector, configuration)
        }
    }

    data class ExitCodeArtifact(override val exitCode: ExitCode) : PipelineArtifactWithExitCode() {
        override val configuration: CompilerConfiguration
            get() = shouldNotBeCalled("No Configuration available from this artifact.")

        @CliPipelineInternals(OPT_IN_MESSAGE)
        override fun withCompilerConfiguration(newConfiguration: CompilerConfiguration): PipelineArtifact {
            return this
        }
    }

    abstract fun createCompoundPhase(arguments: A): CompilerPhase<PipelineContext, ArgumentsPipelineArtifact<A>, *>
    abstract val defaultPerformanceManager: PerformanceManager

    protected open fun createPerformanceManager(arguments: A, services: Services): PerformanceManager {
        return defaultPerformanceManager.apply {
            detailedPerf = arguments.detailedPerf
        }
    }

    protected open fun isKaptMode(arguments: A): Boolean = false

    protected open val isNativeOneStage = false
}
