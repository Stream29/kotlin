/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.pipeline

import org.jetbrains.kotlin.config.LoggingContext
import org.jetbrains.kotlin.config.perfManager
import org.jetbrains.kotlin.config.phaser.*
import org.jetbrains.kotlin.util.PerformanceManager
import org.jetbrains.kotlin.utils.addToStdlib.shouldNotBeCalled

/**
 * [PipelineContext] contains the information which can be used by pre- and post-actions of pipeline phases to report
 *   some information regarding the executed phase.
 * [PipelinePhase] itself could be run without the context
 */
class PipelineContext(
    val performanceManager: PerformanceManager?,
    val kaptMode: Boolean,
) : LoggingContext {
    override var inVerbosePhase: Boolean = false
}

abstract class PipelinePhase<I : PipelineArtifact, O : PipelineArtifact>(
    name: String,
    preActions: Set<Action<I, PipelineContext>> = emptySet(),
    postActions: Set<Action<O, PipelineContext>> = emptySet(),
) : NamedCompilerPhase<PipelineContext, I, O>(
    name = name,
    preactions = preActions,
    postactions = postActions.mapTo(mutableSetOf()) { it.toPostAction() }
) {
    final override fun phaseBody(context: PipelineContext, input: I): O {
        return executePhase(input) ?: throw PipelineStepException()
    }

    abstract fun executePhase(input: I): O?

    override fun outputIfNotEnabled(
        phaseConfig: PhaseConfig,
        phaserState: PhaserState,
        context: PipelineContext,
        input: I,
    ): O {
        shouldNotBeCalled()
    }
}

/**
 * If [definitelyCompilationError] set to `true` the [AbstractCliPipeline] will
 * return [org.jetbrains.kotlin.cli.common.ExitCode.COMPILATION_ERROR] regardless if there
 * are any errors in the message collector or not
 */
class PipelineStepException(val definitelyCompilationError: Boolean = false) : RuntimeException()
class SuccessfulPipelineExecutionException : RuntimeException()

private fun <Input, Output, Context> Action<Output, Context>.toPostAction(): Action<Pair<Input, Output>, Context> {
    return { state: ActionState, inputOutput: Pair<Input, Output>, context: Context ->
        this.invoke(state, inputOutput.second, context)
    }
}

fun <I : PipelineArtifact, O : PipelineArtifact> PipelinePhase<I, O>.executePhaseIsolatedWithActions(input: I): O? {
    val phaseConfig = PhaseConfig()
    val phaserState = PhaserState()
    val context = PipelineContext(input.configuration.perfManager, kaptMode = false)
    runBefore(phaseConfig, phaserState, context, input)
    val output = executePhase(input) ?: return null
    runAfter(phaseConfig, phaserState, context, input, output)
    return output
}
