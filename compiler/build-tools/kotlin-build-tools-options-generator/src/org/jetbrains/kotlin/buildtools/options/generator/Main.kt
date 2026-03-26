/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin.buildtools.options.generator

import com.squareup.kotlinpoet.ClassName
import org.jetbrains.kotlin.arguments.description.CompilerArgumentsLevelNames
import org.jetbrains.kotlin.arguments.description.kotlinCompilerArguments
import org.jetbrains.kotlin.arguments.dsl.base.KotlinCompilerArgumentsLevel
import org.jetbrains.kotlin.arguments.dsl.base.KotlinReleaseVersion
import org.jetbrains.kotlin.generators.util.GeneratorsFileUtil
import org.jetbrains.kotlin.utils.addToStdlib.popLast
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.deleteExisting
import kotlin.io.path.isRegularFile
import kotlin.io.path.walk

/**
 * Arguments are as follows:
 * 1. `<path>` - Output directory for generated classes
 * 1. `<kotlin-version>` - the release of Kotlin for which arguments should be generated, e.g. "2.2.0"
 * 1. `"api"` - (optional) turns on API classes generation and marks the start of parameters for the API generator:
 *     1. `"*"` or `<argumentsLevelName1,argumentsLevelName2>` -
 *     generate classes for all levels (`*`) or only generate classes for the specified list of argument level names and their parents
 *     (see CompilerArgumentsLevelNames.kt)
 *     1. `<package>` - (optional) the target package for generated arguments
 * 1. `"impl"` - (optional) turns on implementation classes generator and marks the start of parameters for the implementation generator:
 *     1. `"*"` or `<argumentsLevelName1,argumentsLevelName2>` -
 *     generate classes for all levels (`*`) or only generate classes for the specified list of argument level names and their parents
 *     (see CompilerArgumentsLevelNames.kt)
 *     1. `<package>` - (optional) the target package for generated arguments
 *     1. `"compat"` – use special mode for compatibility layer generator
 *
 * You must specify at least one of "api" or "impl", and if both are specified "api" must come before "impl".
 */
fun main(args: Array<String>) {
    println("GENERATOR ARGS: " + args.joinToString(" "))
    val genDir = Paths.get(args[0])
    val kotlinVersion = args[1].let { argVersionString ->
        try {
            KotlinReleaseVersion.valueOf(argVersionString)
        } catch (_: IllegalArgumentException) {
            parseLastKotlinReleaseVersion(argVersionString)
        }
    }
    val apiArgsStart = args.indexOf("api").let { if (it == -1) null else it }
    val implArgsStart = args.indexOf("impl").let { if (it == -1) null else it }

    val generatedFiles = mutableListOf<Path>()
    listOfNotNull(
        apiArgsStart?.let { args.copyOfRange(apiArgsStart, implArgsStart ?: args.size) },
        implArgsStart?.let { args.copyOfRange(implArgsStart, args.size) }).map { localArgs ->
        val allowedLevels = if (localArgs[1] == "*") {
            null
        } else {
            localArgs[1].split(",").flatMap { leafName -> kotlinCompilerArguments.topLevel.findPathToLeaf(leafName) }.map { it.name }
                .toSet()
        }
        val targetPackage = if (localArgs.size > 2) {
            localArgs[2]
        } else null
        val generateCompatLayer = localArgs.size > 3 && localArgs[3] == "compat"
        when (localArgs[0]) {
            "api" -> {
                BtaApiGenerator(targetPackage ?: API_ARGUMENTS_PACKAGE, skipXX = true, kotlinVersion) to allowedLevels
            }
            "impl" -> {
                BtaImplGenerator(
                    targetPackage ?: IMPL_ARGUMENTS_PACKAGE,
                    skipXX = false,
                    kotlinVersion,
                    generateCompatLayer,
                ) to allowedLevels
            }
            else -> {
                error("Only `api` and `impl` are supported as arguments for the main function of the options generator")
            }
        }
    }.forEach { (generator, allowedLevels) ->
        val levelsToProcess = mutableListOf(LevelWithParent(kotlinCompilerArguments.topLevel, null))

        if (generator is BtaApiGenerator) {
            levelsToProcess.addAll(syntheticArgumentInterfaces.map { it.toLevelWithParent(generator.targetPackage) })
        }

        while (levelsToProcess.isNotEmpty()) {
            val currentLevel = levelsToProcess.popLast()
            if (currentLevel.level == DummyLevel) {
                continue
            }
            if (allowedLevels != null && currentLevel.level.name !in allowedLevels && currentLevel.level.name !in syntheticArgumentInterfaces.map { it.name }) {
                continue
            }
            val output = generator.generateArgumentsForLevel(currentLevel.level, currentLevel.parentName, currentLevel.additionalInterfaces)
            output.generatedFiles.forEach { (path, content) ->
                val genFile = genDir.resolve(path)
                GeneratorsFileUtil.writeFileIfContentChanged(genFile.toFile(), content, logNotChanged = false)
                generatedFiles.add(genFile)
            }
            levelsToProcess += currentLevel.level.nestedLevels.flatMap { level ->
                // "Skip" the deprecated and soon to be removed Wasm arguments level from the JS arguments hierarchy.
                // There is a separate Wasm-only level in another arguments branch to avoid mixing JS and Wasm hierarchies.
                if (level.name == CompilerArgumentsLevelNames.legacyWasmArguments) {
                    level.nestedLevels
                } else {
                    listOf(level)
                }.map {
                    LevelWithParent(it, output.argumentTypeName)
                }
            }
        }
    }
    genDir.walk().filter { it.isRegularFile() }.forEach {
        if (it !in generatedFiles) {
            GeneratorsFileUtil.writeFileIfContentChanged(it.toFile(), "", logNotChanged = false)
            it.deleteExisting()
        }
    }
}

private fun AdditionalInterface.toLevelWithParent(
    targetPackage: String,
): LevelWithParent = LevelWithParent(
    KotlinCompilerArgumentsLevel(
        name, level.arguments, if (syntheticArgumentInterfaces.any { this in it.parentInterfaces }) {
            setOf(DummyLevel)
        } else emptySet(), level.modifiers
    ), parentInterfaces.firstOrNull()?.let {
        ClassName(targetPackage, it.name)
    } ?: ClassName(targetPackage, "CommonCompilerArguments"), parentInterfaces.map { ClassName(targetPackage, it.name) })

val DummyLevel = KotlinCompilerArgumentsLevel("", emptySet(), emptySet(), emptySet())

internal interface BtaGenerator {
    val targetPackage: String
    fun generateArgumentsForLevel(
        level: KotlinCompilerArgumentsLevel,
        parentClass: ClassName? = null,
        additionalInterfaces: List<ClassName> = emptyList(),
    ): GeneratorOutputs
}

internal class GeneratorOutputs(val argumentTypeName: ClassName, val generatedFiles: List<Pair<Path, String>>)
private class LevelWithParent(
    val level: KotlinCompilerArgumentsLevel,
    val parentName: ClassName?,
    val additionalInterfaces: List<ClassName> = emptyList(),
)

private fun KotlinCompilerArgumentsLevel.findPathToLeaf(leafName: String): Set<KotlinCompilerArgumentsLevel> {
    if (name == leafName) {
        return setOf(this)
    }

    for (nestedLevel in nestedLevels) {
        val path = nestedLevel.findPathToLeaf(leafName)
        if (path.isNotEmpty()) {
            return setOf(this) + path
        }
    }
    return emptySet()
}

private fun parseLastKotlinReleaseVersion(kotlinVersionString: String): KotlinReleaseVersion {
    val baseVersion = kotlinVersionString.split("-", limit = 2)[0]

    val baseVersionSplit = baseVersion.split(".")

    val majorVersion =
        baseVersionSplit[0].toIntOrNull() ?: error("Invalid Kotlin version: $kotlinVersionString (Failed parsing major version)")
    val minorVersion =
        baseVersionSplit.getOrNull(1)?.toIntOrNull() ?: error("Invalid Kotlin version: $kotlinVersionString (Failed parsing minor version)")
    val patchVersion = baseVersionSplit.getOrNull(2)?.toIntOrNull() ?: 0

    return KotlinReleaseVersion.entries.last { releaseVersion ->
        releaseVersion.major < majorVersion || (releaseVersion.major == majorVersion && releaseVersion.minor < minorVersion) || (releaseVersion.major == majorVersion && releaseVersion.minor == minorVersion && releaseVersion.patch <= patchVersion)
    }
}
