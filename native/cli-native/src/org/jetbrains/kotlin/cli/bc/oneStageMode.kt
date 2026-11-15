/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.bc

import org.jetbrains.kotlin.cli.common.arguments.*
import org.jetbrains.kotlin.cli.common.contentRoots
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.useFir
import org.jetbrains.kotlin.konan.config.emitLazyObjcHeaderFile
import org.jetbrains.kotlin.konan.config.konanIncludedLibraries
import org.jetbrains.kotlin.konan.config.konanLibraries
import org.jetbrains.kotlin.konan.file.deleteOnExit
import org.jetbrains.kotlin.library.KlibPath as Path
import org.jetbrains.kotlin.library.createTempDirectory

internal fun isOneStageCompilation(arguments: K2NativeCompilerArguments): Boolean {
    val producingBinary = arguments.produce != "library"
    val hasSources = arguments.freeArgs.isNotEmpty()
    val notFromBitcode = arguments.compileFromBitcode.isNullOrEmpty()
    return producingBinary && hasSources && notFromBitcode
}

internal fun prepareKlibArgumentsForOneStage(
    original: K2NativeCompilerArguments,
    intermediateKlibPath: String
): K2NativeCompilerArguments {
    val klibArgs = original.copyOf()
    // Override produce, output and includes as we should produce an intermediate KLib
    klibArgs.produce = "library"
    klibArgs.outputName = intermediateKlibPath
    klibArgs.includes = emptyArray()
    return klibArgs
}

internal fun adjustConfigurationForSecondStage(
    configuration: CompilerConfiguration,
    intermediateKLib: Path
) {
    // For the second stage, remove already compiled source files from the configuration.
    configuration.contentRoots = listOf()
    // Frontend version must not be passed to 2nd stage (same as Gradle plugin does when calling CLI compiler), since there are no sources anymore
    configuration.useFir = false
    // For the second stage, provide just compiled intermediate KLib as "-Xinclude=" param.
    require(intermediateKLib.exists()) { "Intermediate KLib ${intermediateKLib.pathString} must have been created by successful first compilation stage" }
    // We need to remove this flag, as it would otherwise override header written previously.
    // Unfortunately, there is no way to remove the flag, so empty string is put instead
    configuration.emitLazyObjcHeaderFile?.let { configuration.emitLazyObjcHeaderFile = "" }

    val intermediateKlibPath = intermediateKLib.absolutePathString()
    configuration.konanIncludedLibraries += intermediateKlibPath
    configuration.konanLibraries += intermediateKlibPath
}

internal fun createIntermediateKlib(): Path {
    val tempDir = createTempDirectory("konan-one-stage").toRealPath().deleteOnExit()
    return tempDir.resolve("stage1.klib").also {
        require(!it.exists()) { "Collision writing intermediate KLib ${it.pathString}" }
        it.deleteOnExit()
    }
}
