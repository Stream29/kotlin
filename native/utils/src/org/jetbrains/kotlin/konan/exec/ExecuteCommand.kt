/*
 * Copyright 2010-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.konan.exec

import org.jetbrains.kotlin.konan.KonanExternalToolFailure
import org.jetbrains.kotlin.konan.host.NativeFile as File
import org.jetbrains.kotlin.konan.host.NativeFiles as Files
import org.jetbrains.kotlin.konan.host.runNativeProcess


open class Command(
    initialCommand: List<String>,
    val redirectInputFile: File? = null,
    /**
     * Working directory for the spawned process. When `null`, the current process working directory is inherited.
     * Applied before exec in the native child process.
     */
    val workingDirectory: File? = null,
) {

    constructor(tool: String) : this(listOf(tool)) 
    constructor(vararg command: String) : this(command.toList<String>()) 
    protected val command = initialCommand.toMutableList()

    val argsWithExecutable: List<String> = command

    val args: List<String> 
        get() = command.drop(1)

    operator fun String.unaryPlus(): Command {
        command += this
        return this@Command
    }

    operator fun List<String>.unaryPlus(): Command {
        command.addAll(this)
        return this@Command
    }

    var logger: ((() -> String)->Unit)? = null

    private var stdError: List<String> = emptyList()

    fun logWith(newLogger: ((() -> String)->Unit)): Command {
        logger = newLogger
        return this
    }

    open fun runProcess(): Int {
        stdError = emptyList()
        val errors = Files.createTempFile(null, null).toFile()
        try {
            val exitCode = runNativeProcess(command, workingDirectory, redirectInputFile, null, errors, false)
            stdError = errors.readLines()
            return exitCode
        } finally {
            errors.delete()
        }
    }

    open fun execute() {
        log()
        val code = runProcess()
        handleExitCode(code, stdError)
    }

    /**
     * If withErrors is true then output from error stream will be added
     */
    fun getOutputLines(withErrors: Boolean = false): List<String> =
            getResult(withErrors, handleError = true).outputLines

    fun getResult(withErrors: Boolean, handleError: Boolean = false): Result {
        log()

        val outputFile = Files.createTempFile(null, null).toFile()
        outputFile.deleteOnExit()

        try {
            // Note: getting process output could be done without redirecting to temporary file,
            // however this would require managing a thread to read `process.inputStream` because
            // it may have limited capacity.

            val code = runNativeProcess(command, workingDirectory, redirectInputFile, outputFile, null, withErrors)
            if (handleError) handleExitCode(code, outputFile.readLines())

            return Result(code, outputFile.readLines())
        } finally {
            outputFile.delete()
        }
    }

    class Result(val exitCode: Int, val outputLines: List<String>)

    private fun handleExitCode(code: Int, output: List<String> = emptyList()) {
        if (code != 0) throw KonanExternalToolFailure("""
            The ${command[0]} command returned non-zero exit code: $code.
            output:
            """.trimIndent() + "\n${output.joinToString("\n")}", command[0])
        // Show warnings in case of success linkage.
        if (stdError.isNotEmpty()) {
            stdError.joinToString("\n").also { message ->
                logger?.let { it { message } } ?: println(message)
            }
        }
    }

    private fun log() {
        if (logger != null) logger!! { command.joinToString(" ") }
    }
}
