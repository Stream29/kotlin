/*
 * Copyright 2010-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */
@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package org.jetbrains.kotlin.cli.utilities

import kotlinx.cli.*
import org.jetbrains.kotlin.backend.konan.CachedLibraries
import org.jetbrains.kotlin.backend.konan.OutputFiles
import org.jetbrains.kotlin.cli.bc.K2Native
import org.jetbrains.kotlin.konan.file.absolutePath
import org.jetbrains.kotlin.konan.target.*
import org.jetbrains.kotlin.konan.util.visibleName
import org.jetbrains.kotlin.library.KlibPath as Path
import org.jetbrains.kotlin.library.*
import org.jetbrains.kotlin.library.createDirectories
import org.jetbrains.kotlin.library.createTempDirectory
import org.jetbrains.kotlin.library.createTempFile
import org.jetbrains.kotlin.library.forEachLine
import org.jetbrains.kotlin.native.interop.gen.jvm.parseKeyValuePairs
import org.jetbrains.kotlin.native.interop.tool.SHORT_MODULE_NAME
import org.jetbrains.kotlin.utils.KotlinNativePaths
import org.jetbrains.kotlin.utils.usingNativeMemoryAllocator
import platform.posix.rename
import kotlin.system.exitProcess

private object PlatformLibsInfo {
    const val namePrefix = "org.jetbrains.kotlin.native.platform."
}

// TODO: We definitely need to unify logging in different parts of the compiler.
private class Logger(val level: Level = Level.NORMAL) {

    fun log(message: String) {
        println(message)
    }

    fun verbose(message: String) {
        if (level == Level.VERBOSE) {
            println(message)
        }
    }

    enum class Level {
        NORMAL, VERBOSE
    }
}

private fun Logger.logFailedLibraries(built: Map<DefFile, ProcessingStatus>) {
    log("Processing platform libraries finished with errors.")
    built.forEach { (def, status) ->
        if (status is ProcessingStatus.FAIL) {
            log("    ${def.name}: ${status.error}")
        }
    }
}

private fun Logger.logStackTrace(error: Throwable) {
    verbose(error.stackTraceToString())
}

private enum class CacheKind(val outputKind: CompilerOutputKind) {
    DYNAMIC_CACHE(CompilerOutputKind.DYNAMIC_CACHE),
    STATIC_CACHE(CompilerOutputKind.STATIC_CACHE)
}

private class CInteropOptions(val additionalArguments: List<String>)

// TODO: Use Distribution's paths after compiler update.
fun generatePlatformLibraries(args: Array<String>) = usingNativeMemoryAllocator {
    // IMPORTANT! These command line keys are used by the Gradle plugin to configure platform libraries generation,
    // so any changes in them must be reflected at the Gradle plugin side too.
    // See org.jetbrains.kotlin.gradle.targets.native.internal.PlatformLibrariesGenerator in the Big Kotlin repo.
    val argParser = ArgParser("generate-platform", prefixStyle = ArgParser.OptionPrefixStyle.JVM)
    val inputDirectoryPath by argParser.option(
            ArgType.String,
            "input-directory", "i",
            "Input directory. Default value is <dist>/konan/platformDef/<target>"
    )
    val outputDirectoryPath by argParser.option(
            ArgType.String,
            "output-directory", "o",
            "Output directory. Default value is <dist>/klib/platform/<target>"
    )
    val targetName by argParser.option(
            ArgType.String, "target", "t", "Compilation target").required()
    val saveTemps by argParser.option(
            ArgType.Boolean, "save-temps", "s", "Save temporary files").default(false)
    val stdlibPath by argParser.option(
            ArgType.String,
            "stdlib-path", "S",
            "Place where stdlib is located. Default value is <dist>/klib/common/stdlib"
    )

    val cacheKind by argParser.option(
            ArgType.Choice<CacheKind>(toString = { it.outputKind.visibleName }), "cache-kind", "k", "Type of cache."
    ).default(CacheKind.DYNAMIC_CACHE)

    val cacheDirectoryPath by argParser.option(
            ArgType.String, "cache-directory", "c", "Cache output directory")

    val verbose by argParser.option(
            ArgType.Boolean,
            "verbose", "v",
            "Show verbose log messages"
    ).default(false)

    val cacheArgs by argParser.option(
            ArgType.String, "cache-arg",
            description = "An argument passed to compiler during cache building. Used only if -cache-directory is specified."
    ).multiple()

    val rebuild by argParser.option(
            ArgType.Boolean, fullName = "rebuild", description = "Rebuild already existing libraries"
    ).default(false)

    val overrideKonanProperties by argParser.option(ArgType.String,
            fullName = "Xoverride-konan-properties",
            description = "Override konan.properties.values"
    ).multiple().delimiter(";")

    val konanDataDir by argParser.option(ArgType.String,
            fullName = "Xkonan-data-dir",
            description = "Path to konan and dependencies root folder")

    argParser.parse(args)

    val distribution = Distribution(
            KotlinNativePaths.homePath.absolutePath,
            onlyDefaultProfiles = false,
            runtimeFileOverride = null,
            propertyOverrides = parseKeyValuePairs(overrideKonanProperties),
            konanDataDir = konanDataDir
    )

    val platformManager = PlatformManager(distribution)
    val target = platformManager.targetByName(targetName)
    val targetCacheArgs = platformManager.let {
        target.let(it::loader).additionalCacheFlags
    }
    val inputDirectory = inputDirectoryPath?.let { Path(it) }
            ?: Path(distribution.konanSubdir, "platformDef", target.visibleName)

    val outputDirectory = outputDirectoryPath?.let { Path(it) }
            ?: Path(distribution.klib, "platform", target.visibleName)

    val cacheDirectory = cacheDirectoryPath?.let { Path(it) }

    if (!inputDirectory.exists()) throw Error("input directory doesn't exist")
    if (!outputDirectory.exists()) {
        outputDirectory.createDirectories()
    }
    if (cacheDirectory != null && !cacheDirectory.exists()) {
        cacheDirectory.createDirectories()
    }

    val stdlibFile = stdlibPath?.let { Path(it) } ?: Path(distribution.stdlib)

    val logger = Logger(if (verbose) Logger.Level.VERBOSE else Logger.Level.NORMAL)

    val cacheInfo = cacheDirectory?.let {
        CacheInfo(it, cacheKind.outputKind.visibleName, cacheArgs + targetCacheArgs)
    }

    val cinteropOptions = CInteropOptions(
            additionalArguments = buildList {
                if (overrideKonanProperties.isNotEmpty()) {
                    add("-Xoverride-konan-properties")
                    add(overrideKonanProperties.joinToString(";"))
                }

                konanDataDir?.let {
                    add("-Xkonan-data-dir")
                    add(it)
                }
            }
    )

    generatePlatformLibraries(
            target, cinteropOptions,
            DirectoriesInfo(inputDirectory, outputDirectory, stdlibFile), cacheInfo,
            rebuild, saveTemps, logger, konanDataDir
    )
}

private sealed class ProcessingStatus {
    object WAIT : ProcessingStatus()
    object SUCCESS : ProcessingStatus()
    object FAILED_DEPENDENCIES : ProcessingStatus()
    class FAIL(val error: Throwable) : ProcessingStatus()
}

private data class DirectoriesInfo(val inputDirectory: Path, val outputDirectory: Path, val stdlib: Path)

private data class CacheInfo(val cacheDirectory: Path, val cacheKind: String, val cacheArgs: List<String>)

private class DefFile(val name: String, val depends: MutableList<DefFile>) {
    override fun toString(): String = "$name: [${depends.joinToString(separator = ", ") { it.name }}]"

    val libraryName: String
        get() = "${PlatformLibsInfo.namePrefix}$name"

    val shortLibraryName: String
        get() = name
}

private fun renameAtomic(from: String, to: String, replaceExisting: Boolean): Boolean {
    if (!replaceExisting && Path(to).exists()) return false
    return rename(from, to) == 0
}

private fun createTempDir(prefix: String, parent: Path): Path =
    createTempDirectory(parent.absolute(), prefix)

private fun Path.deleteAtomicallyIfPossible(tmpDirectory: Path) {
    // Try to atomically delete the old directory.
    val tmpToDelete = createTempFile(tmpDirectory.absolute(), "delete-", "")
    if (renameAtomic(this.absolutePathString(), tmpToDelete.absolutePathString(), replaceExisting = true)) {
        tmpToDelete.deleteRecursively()
    } else {
        // Can't move to a tmp directory -> delete in a regular way.
        this.deleteRecursively()
    }
}

private fun topoSort(defFiles: List<DefFile>): List<DefFile> {
    // Do DFS toposort.
    val markGray = mutableSetOf<DefFile>()
    val markBlack = mutableSetOf<DefFile>()
    val result = mutableListOf<DefFile>()

    fun visit(def: DefFile) {
        if (markBlack.contains(def)) return
        if (markGray.contains(def)) throw Error("$def is part of cycle")
        markGray += def
        def.depends.forEach {
            visit(it)
        }
        markGray -= def
        markBlack += def
        result += def
    }

    var index = 0
    while (markBlack.size < defFiles.size) {
        visit(defFiles[index++])
    }
    return result
}

private fun generateLibrary(
        target: KonanTarget,
        cinteropOptions: CInteropOptions,
        def: DefFile,
        directories: DirectoriesInfo,
        tmpDirectory: Path,
        rebuild: Boolean,
        logger: Logger
): Unit = with(directories) {
    val defFile = inputDirectory.resolve("${def.name}.def")
    val outKlib = outputDirectory.resolve(def.libraryName)

    if (outKlib.exists() && !rebuild) {
        logger.verbose("Skip generating ${def.name} as it's already generated")
        return
    }

    val tmpKlib = tmpDirectory.resolve(def.libraryName)

    try {
        val cinteropArgs = arrayOf(
                "-o", tmpKlib.absolutePathString(),
                "-target", target.visibleName,
                "-def", defFile.absolutePathString(),
                "-compiler-option", "-fmodules-cache-path=${tmpDirectory.resolve("clangModulesCache").absolutePathString()}",
                "-no-default-libs", "-nopack",
                "-Xdisable-experimental-annotation",
                *cinteropOptions.additionalArguments.toTypedArray(),
                "-$SHORT_MODULE_NAME", def.shortLibraryName,
                *def.depends.flatMap { listOf("-l", "${outputDirectory.pathString}/${it.libraryName}") }.toTypedArray()
        )
        logger.verbose("Run cinterop with args: ${cinteropArgs.joinToString(separator = " ")}")
        invokeInterop("native", cinteropArgs, runFromDaemon = false)?.let { K2Native.mainNoExit(it) }

        if (rebuild) {
            outKlib.deleteAtomicallyIfPossible(tmpDirectory)
        }

        // Atomically move the generated library to the destination path.
        if (!renameAtomic(tmpKlib.absolutePathString(), outKlib.absolutePathString(), replaceExisting = false)) {
            tmpKlib.deleteRecursively()
        }
    } finally {
        tmpKlib.deleteRecursively()
    }
}

private fun getLibraryCacheDir(
        libraryName: String,
        target: KonanTarget,
        cacheDirectory: Path,
        cacheKind: String
): Path {
    val cacheBaseName = CachedLibraries.getCachedLibraryName(libraryName)
    val cacheOutputKind = CompilerOutputKind.valueOf(cacheKind.uppercase())
    return OutputFiles(cacheDirectory.resolve(cacheBaseName).absolutePathString(), target, cacheOutputKind).mainFile
}

private fun buildCache(
        target: KonanTarget,
        def: DefFile,
        outputDirectory: Path,
        cacheInfo: CacheInfo,
        rebuild: Boolean,
        logger: Logger,
        konanDataDir: String?,
): Unit = with(cacheInfo) {
    val libraryCacheDir = getLibraryCacheDir(def.name, target, cacheDirectory, cacheKind)
    if (libraryCacheDir.listDirectoryEntriesIfDirectoryExists().isNotEmpty() && !rebuild) {
        logger.verbose("Skip precompiling ${def.name} as it's already precompiled")
        return
    }

    if (rebuild) {
        libraryCacheDir.deleteRecursively()
    }

    val compilerArgs = listOfNotNull(
            "-p", cacheKind,
            "-target", target.visibleName,
            "-Xadd-cache=${outputDirectory.absolutePathString()}/${def.libraryName}",
            "-Xcache-directory=${cacheDirectory.absolutePathString()}",
            konanDataDir?.let { "-Xkonan-data-dir=${it}" },
    ) + cacheArgs

    logger.verbose("Run compiler with args: ${compilerArgs.joinToString(separator = " ")}")
    K2Native.mainNoExit(compilerArgs.toTypedArray())
}

private fun buildStdlibCache(
        target: KonanTarget,
        stdlib: Path,
        cacheInfo: CacheInfo,
        logger: Logger
): Unit = with(cacheInfo) {
    val stdlibCacheFile = getLibraryCacheDir("stdlib", target, cacheDirectory, cacheKind)
    if (stdlibCacheFile.exists()) {
        logger.verbose("Skip precompiling standard library as it's already precompiled")
        return
    }

    logger.log("Precompiling standard library...")
    val compilerArgs = arrayOf(
            "-p", cacheKind,
            "-target", target.visibleName,
            "-Xadd-cache=${stdlib.absolutePathString()}",
            "-Xcache-directory=${cacheDirectory.absolutePathString()}",
            *cacheArgs.toTypedArray()
    )
    logger.verbose("Run compiler with args: ${compilerArgs.joinToString(separator = " ")}")
    K2Native.mainNoExit(compilerArgs)
}

private fun generatePlatformLibraries(
        target: KonanTarget,
        cinteropOptions: CInteropOptions,
        directories: DirectoriesInfo,
        cacheInfo: CacheInfo?,
        rebuild: Boolean,
        saveTemps: Boolean,
        logger: Logger,
        konanDataDir: String?,
) = with(directories) {
    if (cacheInfo != null) {
        buildStdlibCache(target, stdlib, cacheInfo, logger)
    }

    logger.verbose("Generating platform libraries from ${inputDirectory.pathString} to ${outputDirectory.pathString} for ${target.visibleName}")
    if (cacheInfo != null) {
        logger.verbose("Precompiling platform libraries to ${cacheInfo.cacheDirectory.pathString} (cache kind: ${cacheInfo.cacheKind})")
    }

    val tmpDirectory = createTempDir("build-", outputDirectory)

    // Build dependencies graph.
    val defFiles = mutableMapOf<String, DefFile>()
    val dependsRegex = Regex("^depends = (.*)")
    inputDirectory.listDirectoryEntriesIfDirectoryExists().filter { it.extension == "def" }.forEach { file ->
        val name = file.name.split(".").also { assert(it.size == 2) }[0]
        val def = defFiles.getOrPut(name) {
            DefFile(name, mutableListOf())
        }
        file.forEachLine { line ->
            val match = dependsRegex.matchEntire(line)
            if (match != null) {
                match.groupValues[1].split(" ").forEach { dependency ->
                    def.depends.add(defFiles.getOrPut(dependency) {
                        DefFile(dependency, mutableListOf())
                    })
                }
            }
        }
    }
    val sorted = topoSort(defFiles.values.toList())
    val built = sorted.associateWithTo(mutableMapOf<DefFile, ProcessingStatus>()) { ProcessingStatus.WAIT }
    // Now run interop tool on toposorted dependencies.
    val countTotal = sorted.size
    var countProcessed = 0
    try {
        tmpDirectory.createDirectories()
        sorted.forEach { def ->
            try {
                if (def.depends.any { built[it] is ProcessingStatus.FAIL || built[it] is ProcessingStatus.FAILED_DEPENDENCIES }) {
                    built[def] = ProcessingStatus.FAILED_DEPENDENCIES
                    return@forEach
                }

                logger.log("Processing ${def.name} (${++countProcessed}/$countTotal)...")
                generateLibrary(target, cinteropOptions, def, directories, tmpDirectory, rebuild, logger)
                if (cacheInfo != null) {
                    buildCache(target, def, outputDirectory, cacheInfo, rebuild, logger, konanDataDir)
                }

                built[def] = ProcessingStatus.SUCCESS
            } catch (e: Throwable) {
                built[def] = ProcessingStatus.FAIL(e)
                logger.logStackTrace(e)
            }
        }

        if (built.values.any { it != ProcessingStatus.SUCCESS }) {
            logger.logFailedLibraries(built)
            exitProcess(-1)
        }

    } finally {
        if (!saveTemps) {
            tmpDirectory.deleteRecursively()
        }
    }
}

private fun Path.listDirectoryEntriesIfDirectoryExists(): List<Path> =
    if (isDirectory()) listDirectoryEntries() else emptyList()
