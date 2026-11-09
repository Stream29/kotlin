package org.jetbrains.kotlin.backend.konan

import org.jetbrains.kotlin.backend.konan.driver.NativeBackendPhaseContext
import org.jetbrains.kotlin.backend.konan.util.toObsoleteKind
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.nativeBinaryOptions.AndroidProgramType
import org.jetbrains.kotlin.config.nativeBinaryOptions.BinaryOptions
import org.jetbrains.kotlin.konan.KonanExternalToolFailure
import org.jetbrains.kotlin.konan.TempFiles
import org.jetbrains.kotlin.konan.config.*
import org.jetbrains.kotlin.konan.exec.Command
import org.jetbrains.kotlin.konan.file.*
import org.jetbrains.kotlin.konan.library.components.nativeIncludedBinaries
import org.jetbrains.kotlin.konan.library.linkerOpts
import org.jetbrains.kotlin.konan.target.*
import org.jetbrains.kotlin.library.KlibPath
import org.jetbrains.kotlin.library.metadata.isCInteropLibrary
import org.jetbrains.kotlin.library.uniqueName

typealias ExecutableFile = String

enum class LinkerOutputKind {
    DYNAMIC_LIBRARY,
    STATIC_LIBRARY,
    EXECUTABLE
}

val String.isUnixStaticLib: Boolean
    get() = this.endsWith(".a")

val String.isWindowsStaticLib: Boolean
    get() = this.endsWith(".lib")

val KlibPath.isBitcode: Boolean
    get() = this.pathString.endsWith(".bc")

fun File.javaFile(): File = this

private fun llvmArStaticLibraryCommands(
    llvmAr: String,
    executable: ExecutableFile,
    objectFiles: List<ObjectFile>,
    libraries: List<String>,
    tempFiles: TempFiles,
): List<Command> {
    val members = objectFiles + libraries
    val operation = "qcsDL"
    return listOf(Command(llvmAr).apply {
        +"--rsp-quoting=windows"
        +operation
        +executable
        +responseFileArg(tempFiles, "ar-members", members)
    })
}

private fun responseFileArg(tempFiles: TempFiles, responseFilePrefix: String, paths: List<String>): String {
    val responseFile = tempFiles.create(responseFilePrefix, ".rsp")
    responseFile.writeLines(paths.map { "\"$it\"" })
    return "@${responseFile.absolutePath}"
}

class LinkerArguments(
    val tempFiles: TempFiles,
    val objectFiles: List<ObjectFile>,
    val executable: ExecutableFile,
    val staticLibraries: List<String>,
    val dynamicLibraries: List<String>,
    val linkerArgs: List<String>,
    val optimize: Boolean,
    val debug: Boolean,
    val kind: LinkerOutputKind,
    val outputDsymBundle: String,
    val sanitizer: SanitizerKind? = null,
)

abstract class LinkerFlags(val configurables: Configurables) {

    open val useCompilerDriverAsLinker: Boolean get() = false

    abstract fun LinkerArguments.finalLinkCommands(): List<Command>

    open fun preLinkCommands(objectFiles: List<ObjectFile>, output: ObjectFile): List<Command> =
            error("Pre-link is unsupported for ${configurables.target}.")

    abstract fun filterStaticLibraries(binaries: List<String>): List<String>

    open fun linkStaticLibraries(binaries: List<String>): List<String> = filterStaticLibraries(binaries)

    open fun provideCompilerRtLibrary(libraryName: String, isDynamic: Boolean = false): String? {
        println("Can't provide $libraryName.")
        return null
    }
}

private object Android {
    const val API = "21"
    fun architectureDirForTarget(target: KonanTarget): String = when (target) {
        KonanTarget.ANDROID_ARM32 -> "arch-arm"
        KonanTarget.ANDROID_ARM64 -> "arch-arm64"
        KonanTarget.ANDROID_X86 -> "arch-x86"
        KonanTarget.ANDROID_X64 -> "arch-x86_64"
        else -> error("Unexpected Android target: $target")
    }
}

class AndroidLinker(targetProperties: AndroidConfigurables)
    : LinkerFlags(targetProperties), AndroidConfigurables by targetProperties {

    private val clangTarget = when (targetProperties.targetTriple.toString()) {
        "arm-unknown-linux-androideabi" -> "armv7a-linux-androideabi"
        else -> targetProperties.targetTriple.withoutVendor()
    }
    private val prefix = "$absoluteTargetToolchain/bin/${clangTarget}${Android.API}"
    private val clang = if (HostManager.hostIsMingw) "$prefix-clang.cmd" else "$prefix-clang"
    private val llvmAr = "$absoluteLlvmHome/bin/llvm-ar"

    override val useCompilerDriverAsLinker: Boolean get() = true

    override fun filterStaticLibraries(binaries: List<String>) = binaries.filter { it.isUnixStaticLib }

    override fun LinkerArguments.finalLinkCommands(): List<Command> {
        require(sanitizer == null) {
            "Sanitizers are unsupported"
        }
        if (kind == LinkerOutputKind.STATIC_LIBRARY)
            return llvmArStaticLibraryCommands(llvmAr, executable, objectFiles, staticLibraries, tempFiles)

        val dynamic = kind == LinkerOutputKind.DYNAMIC_LIBRARY
        val toolchainSysroot = "${absoluteTargetToolchain}/sysroot"
        val architectureDir = Android.architectureDirForTarget(target)
        val apiSysroot = "$absoluteTargetSysRoot/$architectureDir"
        val clangTarget = targetTriple.withoutVendor()
        val libDirs = listOf(
                "--sysroot=$apiSysroot",
                if (target == KonanTarget.ANDROID_X64) "-L$apiSysroot/usr/lib64" else "-L$apiSysroot/usr/lib",
                "-L$toolchainSysroot/usr/lib/$clangTarget/${Android.API}",
                "-L$toolchainSysroot/usr/lib/$clangTarget")
        return listOf(Command(clang).apply {
            +"-o"
            +executable
            when (kind) {
                LinkerOutputKind.EXECUTABLE -> +listOf("-fPIE", "-pie")
                LinkerOutputKind.DYNAMIC_LIBRARY -> +listOf("-fPIC", "-shared")
                LinkerOutputKind.STATIC_LIBRARY -> {}
            }
            +"-target"
            +clangTarget
            +libDirs
            +objectFiles
            if (optimize) +linkerOptimizationFlags
            if (!debug) +linkerNoDebugFlags
            if (dynamic) +linkerDynamicFlags
            if (dynamic) +"-Wl,-soname,${File(executable).name}"
            +linkerKonanFlags
            +staticLibraries
            +dynamicLibraries
            +linkerArgs
        })
    }
}

class MacOSBasedLinker(targetProperties: AppleConfigurables)
    : LinkerFlags(targetProperties), AppleConfigurables by targetProperties {

    private val libtool = "$absoluteTargetToolchain/bin/libtool"
    private val linker = "$absoluteTargetToolchain/bin/ld"
    private val strip = "$absoluteTargetToolchain/bin/strip"
    private val dsymutil = "$absoluteTargetToolchain/bin/dsymutil"

    private val compilerRtDir: String? by lazy {
        val dir = File("$absoluteTargetToolchain/lib/clang/").listFiles.firstOrNull()?.absolutePath
        if (dir != null) "$dir/lib/darwin/" else null
    }

    override fun provideCompilerRtLibrary(libraryName: String, isDynamic: Boolean): String? {
        val prefix = when (target.family) {
            Family.IOS -> if (targetTriple.isMacabi) "osx" else "ios"
            Family.WATCHOS -> "watchos"
            Family.TVOS -> "tvos"
            Family.OSX -> "osx"
            else -> error("Target $target is unsupported")
        }
        val suffix = if (targetTriple.isSimulator) "sim" else ""
        val dir = compilerRtDir
        val mangledLibraryName = if (libraryName.isEmpty()) "" else "${libraryName}_"
        val extension = if (isDynamic) "_dynamic.dylib" else ".a"
        return if (dir != null) "$dir/libclang_rt.$mangledLibraryName$prefix$suffix$extension" else null
    }

    override fun filterStaticLibraries(binaries: List<String>) = binaries.filter { it.isUnixStaticLib }

    override fun preLinkCommands(objectFiles: List<ObjectFile>, output: ObjectFile): List<Command> =
        Command(linker).apply {
            +"-r"
            +listOf("-arch", arch)
            +listOf("-syslibroot", absoluteTargetSysRoot)
            +objectFiles
            +listOf("-o", output)
        }.let(::listOf)

    private fun platformVersionFlags(): List<String> = mutableListOf<String>().apply {
        add("-platform_version")
        val platformName = when (target.family) {
            Family.OSX -> "macos"
            Family.IOS -> if (targetTriple.isMacabi) "mac-catalyst" else "ios"
            Family.TVOS -> "tvos"
            Family.WATCHOS -> "watchos"
            else -> error("Unexpected Apple target family: ${target.family}")
        } + if (targetTriple.isSimulator) "-simulator" else ""
        add(platformName)
        add("$osVersionMin.0")
        add(sdkVersion)
    }.toList()

    override fun LinkerArguments.finalLinkCommands(): List<Command> {
        val staticLibrariesArgs = if (staticLibraries.isEmpty())
            staticLibraries
        else tempFiles.create("libraries").let { librariesListFile ->
            librariesListFile.writeLines(staticLibraries)
            listOf("-filelist", librariesListFile.absolutePath)
        }

        val dynamicLibrariesArgs = if (dynamicLibraries.isEmpty())
            dynamicLibraries
        else tempFiles.create("dynamic").let { dynamicLibrariesListFile ->
            dynamicLibrariesListFile.writeLines(dynamicLibraries)
            listOf("-filelist", dynamicLibrariesListFile.absolutePath)
        }

        if (kind == LinkerOutputKind.STATIC_LIBRARY) {
            require(sanitizer == null) {
                "Sanitizers are unsupported"
            }
            return listOf(Command(libtool).apply {
                +"-D"
                +"-static"
                +listOf("-o", executable)
                +listOf("-arch_only", arch)
                +objectFiles
                +staticLibrariesArgs
                +dynamicLibrariesArgs
            })
        }
        val dynamic = kind == LinkerOutputKind.DYNAMIC_LIBRARY

        val result = mutableListOf<Command>()

        result += Command(linker).apply {
            +"-demangle"
            +listOf("-dynamic", "-arch", arch)
            +platformVersionFlags()
            +listOf("-syslibroot", absoluteTargetSysRoot, "-o", executable)
            +objectFiles
            if (targetTriple.isMacabi) +listOf(
                "-L$absoluteTargetSysRoot/System/iOSSupport/usr/lib",
                "-L$absoluteTargetToolchain/lib/swift/maccatalyst",
                "-F$absoluteTargetSysRoot/System/iOSSupport/System/Library/Frameworks",
            )
            if (optimize) +linkerOptimizationFlags
            if (!debug) +linkerNoDebugFlags
            if (dynamic) +linkerDynamicFlags
            +linkerKonanFlags
            if (compilerRtLibrary != null) +compilerRtLibrary!!
            +staticLibrariesArgs
            +dynamicLibrariesArgs
            +linkerArgs
            +rpath(dynamic, sanitizer)
            when (sanitizer) {
                null -> {}
                SanitizerKind.ADDRESS -> +provideCompilerRtLibrary("asan", isDynamic=true)!!
                SanitizerKind.THREAD -> +provideCompilerRtLibrary("tsan", isDynamic=true)!!
            }
        }

        if (debug) {
            result += dsymUtilCommand(executable, outputDsymBundle)
            if (optimize) {
                result += Command(strip, *stripFlags.toTypedArray(), executable)
            }
        }

        return result
    }

    private val compilerRtLibrary: String? by lazy {
        provideCompilerRtLibrary("")
    }

    private fun rpath(dynamic: Boolean, sanitizer: SanitizerKind?): List<String> = listOfNotNull(
            when (target.family) {
                Family.OSX -> "@executable_path/../Frameworks"
                Family.IOS,
                Family.WATCHOS,
                Family.TVOS -> "@executable_path/Frameworks"
                else -> error(target)
            },
            "@loader_path/Frameworks".takeIf { dynamic },
            compilerRtDir.takeIf { sanitizer != null }
    ).flatMap { listOf("-rpath", it) }

    fun dsymUtilCommand(executable: ExecutableFile, outputDsymBundle: String): Command =
            Command(dsymutilCommand(executable, outputDsymBundle))

    fun dsymutilCommand(executable: ExecutableFile, outputDsymBundle: String): List<String> =
            listOf(dsymutil, executable, "-o", outputDsymBundle)
}

class GccBasedLinker(targetProperties: GccConfigurables)
    : LinkerFlags(targetProperties), GccConfigurables by targetProperties {

    private val llvmAr = "$absoluteLlvmHome/bin/llvm-ar"
    override val libGcc = "$absoluteTargetSysRoot/${super.libGcc}"

    private val specificLibs = abiSpecificLibraries.map { "-L${absoluteTargetSysRoot}/$it" }

    override fun provideCompilerRtLibrary(libraryName: String, isDynamic: Boolean): String? {
        require(!isDynamic) {
            "Dynamic compiler rt librares are unsupported"
        }
        val clangdir = File("$absoluteLlvmHome/lib/clang/").listFiles.firstOrNull()?.absolutePath ?: return null
        val libdir = File("$clangdir/lib/").listFiles.firstOrNull()?.absolutePath ?: return null
        val llvm11lib = File("$libdir/libclang_rt.$libraryName-x86_64.a")
        return if (llvm11lib.exists) llvm11lib.absolutePath else "$libdir/libclang_rt.$libraryName.a"
    }

    override fun filterStaticLibraries(binaries: List<String>) = binaries.filter { it.isUnixStaticLib }

    override fun LinkerArguments.finalLinkCommands(): List<Command> {
        if (kind == LinkerOutputKind.STATIC_LIBRARY) {
            require(sanitizer == null) {
                "Sanitizers are unsupported"
            }
            return llvmArStaticLibraryCommands(llvmAr, executable, objectFiles, staticLibraries, tempFiles)
        }
        val dynamic = kind == LinkerOutputKind.DYNAMIC_LIBRARY
        val crtPrefix = "$absoluteTargetSysRoot/$crtFilesLocation"
        return listOf(Command(absoluteLinker).apply {
            +"--sysroot=${absoluteTargetSysRoot}"
            +"-export-dynamic"
            +"-z"
            +"relro"
            +"--build-id"
            +"--eh-frame-hdr"
            +"-dynamic-linker"
            +dynamicLinker
            linkerHostSpecificFlags.forEach { +it }
            +"-o"
            +executable
            if (!dynamic) +"$crtPrefix/crt1.o"
            +"$crtPrefix/crti.o"
            +if (dynamic) "$libGcc/crtbeginS.o" else "$libGcc/crtbegin.o"
            +"-L$libGcc"
            +"--hash-style=gnu"
            +specificLibs
            if (optimize) +linkerOptimizationFlags
            if (!debug) +linkerNoDebugFlags
            if (dynamic) +linkerDynamicFlags
            +objectFiles
            when (sanitizer) {
                null -> {}
                SanitizerKind.ADDRESS -> {
                    +"-lrt"
                    +provideCompilerRtLibrary("asan")!!
                    +provideCompilerRtLibrary("asan_cxx")!!
                }
                SanitizerKind.THREAD -> {
                    +"-lrt"
                    +provideCompilerRtLibrary("tsan")!!
                    +provideCompilerRtLibrary("tsan_cxx")!!
                }
            }
            +staticLibraries
            +dynamicLibraries
            +linkerArgs
            +linkerKonanFlags
            +linkerGccFlags
            +if (dynamic) "$libGcc/crtendS.o" else "$libGcc/crtend.o"
            +"$crtPrefix/crtn.o"
        })
    }
}

class MingwLinker(targetProperties: MingwConfigurables)
    : LinkerFlags(targetProperties), MingwConfigurables by targetProperties {

    private val llvmAr = "$absoluteLlvmHome/bin/llvm-ar"
    private val clang = "$absoluteLlvmHome/bin/clang++"

    override val useCompilerDriverAsLinker: Boolean get() = true

    override fun filterStaticLibraries(binaries: List<String>) = binaries.filter { it.isWindowsStaticLib || it.isUnixStaticLib }

    override fun provideCompilerRtLibrary(libraryName: String, isDynamic: Boolean): String? {
        require(!isDynamic) {
            "Dynamic compiler rt librares are unsupported"
        }
        val targetSuffix = when (target) {
            KonanTarget.MINGW_X64 -> "x86_64"
            else -> error("$target is not supported.")
        }
        val dir = File("$absoluteLlvmHome/lib/clang/").listFiles.firstOrNull()?.absolutePath
        return if (dir != null) "$dir/lib/windows/libclang_rt.$libraryName-$targetSuffix.a" else null
    }

    override fun LinkerArguments.finalLinkCommands(): List<Command> {
        require(sanitizer == null) {
            "Sanitizers are unsupported"
        }
        if (kind == LinkerOutputKind.STATIC_LIBRARY)
            return llvmArStaticLibraryCommands(llvmAr, executable, objectFiles, staticLibraries, tempFiles)

        val dynamic = kind == LinkerOutputKind.DYNAMIC_LIBRARY

        fun List<String>.asLinkerInputs(responseFilePrefix: String): List<String> =
            if (isEmpty()) this else listOf(responseFileArg(tempFiles, responseFilePrefix, this))

        val librariesArgs = (staticLibraries + dynamicLibraries).asLinkerInputs("libs")
        val usesResponseFile = librariesArgs.isNotEmpty()

        fun Command.constructLinkerArguments(
                additionalArguments: List<String> = listOf(),
                skipDefaultArguments: List<String> = listOf()
        ): Command = apply {
            if (usesResponseFile) +"--rsp-quoting=windows"
            +listOf("--sysroot", absoluteTargetSysRoot)
            +listOf("-target", targetTriple.toString())
            +listOf("-o", executable)
            +objectFiles
            if (optimize) {
                +linkerOptimizationFlags
            }
            if (!debug) +linkerNoDebugFlags
            if (dynamic) +linkerDynamicFlags
            +librariesArgs
            +linkerArgs
            +linkerKonanFlags.filterNot { it in skipDefaultArguments }
            +additionalArguments
        }

        return listOf(Command(clang).constructLinkerArguments(additionalArguments = listOf("-fuse-ld=$absoluteLinker")))
    }
}

fun linker(configurables: Configurables): LinkerFlags =
        when (configurables) {
            is GccConfigurables -> GccBasedLinker(configurables)
            is AppleConfigurables -> MacOSBasedLinker(configurables)
            is AndroidConfigurables -> AndroidLinker(configurables)
            is MingwConfigurables -> MingwLinker(configurables)
            else -> error("Unexpected target: ${configurables.target}")
        }

val Platform.linker: LinkerFlags
    get() = linker(configurables)

internal fun determineLinkerOutput(context: NativeBackendPhaseContext): LinkerOutputKind =
        when (context.config.produce) {
            CompilerOutputKind.FRAMEWORK -> {
                val staticFramework = context.config.produceStaticFramework
                if (staticFramework) LinkerOutputKind.STATIC_LIBRARY else LinkerOutputKind.DYNAMIC_LIBRARY
            }
            CompilerOutputKind.TEST_BUNDLE,
            CompilerOutputKind.DYNAMIC_CACHE,
            CompilerOutputKind.DYNAMIC -> LinkerOutputKind.DYNAMIC_LIBRARY
            CompilerOutputKind.STATIC_CACHE,
            CompilerOutputKind.STATIC -> LinkerOutputKind.STATIC_LIBRARY
            CompilerOutputKind.PROGRAM -> run {
                if (context.config.target.family == Family.ANDROID) {
                    val configuration = context.config.configuration
                    val androidProgramType = configuration.get(BinaryOptions.androidProgramType) ?: AndroidProgramType.Default
                    when (androidProgramType) {
                        AndroidProgramType.Standalone -> LinkerOutputKind.EXECUTABLE
                        AndroidProgramType.NativeActivity -> LinkerOutputKind.DYNAMIC_LIBRARY
                    }
                }
                LinkerOutputKind.EXECUTABLE
            }
            else -> TODO("${context.config.produce} should not reach native linker stage")
        }

// TODO: We have a Linker.kt file in the shared module.
internal class Linker(
    private val config: NativeSecondStageCompilationConfig,
    private val linkerOutput: LinkerOutputKind,
    private val outputFiles: OutputFiles,
    private val tempFiles: TempFiles,
) {
    private val platform = config.platform
    private val linker = platform.linker
    private val target = config.target
    private val optimize = config.optimizationsEnabled
    private val debug = config.debug || config.lightDebug

    fun linkCommands(
            outputFile: String,
            objectFiles: List<ObjectFile>,
            dependenciesTrackingResult: DependenciesTrackingResult,
            caches: ResolvedCacheBinaries,
    ): List<Command> {
        val nativeDependencies = dependenciesTrackingResult.nativeDependenciesToLink

        val includedBinariesLibraries = config.libraryToCache?.let { listOf(it.klib) }
                ?: nativeDependencies.filterNot { config.cachedLibraries.isLibraryCached(it) }
        val includedBinaries = includedBinariesLibraries.flatMap { library ->
            library.nativeIncludedBinaries(config.target)?.nativeIncludedBinaryFilePaths?.map { it.pathString }.orEmpty()
        }

        val libraryProvidedLinkerFlags = dependenciesTrackingResult.allNativeDependencies.map { it.linkerOpts }.flatten()
        return runLinker(outputFile, objectFiles, includedBinaries, libraryProvidedLinkerFlags, caches)
    }

    private fun asLinkerArgs(args: List<String>): List<String> {
        if (linker.useCompilerDriverAsLinker) {
            return args
        }

        val result = mutableListOf<String>()
        for (arg in args) {
            // If user passes compiler arguments to us - transform them to linker ones.
            if (arg.startsWith("-Wl,")) {
                result.addAll(arg.substring(4).split(','))
            } else {
                result.add(arg)
            }
        }
        return result
    }

    private fun runLinker(
            outputFile: String,
            objectFiles: List<ObjectFile>,
            includedBinaries: List<String>,
            libraryProvidedLinkerFlags: List<String>,
            caches: ResolvedCacheBinaries,
    ): List<Command> {
        val additionalLinkerArgs: List<String>
        val executable: String

        when (config.produce) {
            CompilerOutputKind.TEST_BUNDLE -> {
                val bundleDir = File(outputFile)
                val name = bundleDir.name.removeSuffix(config.produce.suffix())
                require(target.family.isAppleFamily)
                val bundleRelativePath = if (target.family == Family.OSX) "Contents/MacOS/$name" else name
                additionalLinkerArgs = listOf("-bundle", "-dead_strip")
                val bundlePath = bundleDir.child(bundleRelativePath)
                bundlePath.absoluteFile.parentFile!!.mkdirs()
                executable = bundlePath.absolutePath
            }
            CompilerOutputKind.FRAMEWORK -> {
                val framework = File(outputFile)
                val dylibName = framework.name.removeSuffix(".framework")
                val dylibRelativePath = when (target.family) {
                    Family.IOS,
                    Family.TVOS,
                    Family.WATCHOS -> dylibName
                    Family.OSX -> "Versions/A/$dylibName"
                    else -> error(target)
                }
                additionalLinkerArgs = listOf("-dead_strip", "-install_name", "@rpath/${framework.name}/$dylibRelativePath")
                val dylibPath = framework.child(dylibRelativePath)
                dylibPath.absoluteFile.parentFile!!.mkdirs()
                executable = dylibPath.absolutePath
            }
            else -> {
                additionalLinkerArgs = if (target.family.isAppleFamily) {
                    when (config.produce) {
                        CompilerOutputKind.DYNAMIC_CACHE ->
                            listOf("-install_name", outputFiles.dynamicCacheInstallName)
                        else -> listOf("-dead_strip")
                    }
                } else {
                    emptyList()
                }
                executable = outputFiles.nativeBinaryFile
            }
        }
        File(executable).delete()

        val linkerArgs = asLinkerArgs(config.configuration.getNotNull(NativeConfigurationKeys.LINKER_ARGS)) +
                libraryProvidedLinkerFlags + additionalLinkerArgs

        return with(linker) {
            LinkerArguments(
                    tempFiles = tempFiles,
                    objectFiles = objectFiles,
                    executable = executable,
                    staticLibraries = linker.linkStaticLibraries(includedBinaries) + caches.static,
                    dynamicLibraries = caches.dynamic,
                    linkerArgs = linkerArgs,
                    optimize = optimize,
                    debug = debug,
                    kind = linkerOutput,
                    outputDsymBundle = outputFiles.symbolicInfoFile,
                    sanitizer = config.sanitizer?.toObsoleteKind(),
            ).finalLinkCommands()
        }
    }
}

internal fun runLinkerCommands(context: NativeBackendPhaseContext, commands: List<Command>, cachingInvolved: Boolean) = try {
    commands.forEach {
        it.logWith { msg -> context.log(msg()) }
        it.execute()
    }
} catch (e: KonanExternalToolFailure) {
    val extraUserInfo = if (cachingInvolved) {
        val incrementalCompilationEnabled = context.config.configuration[CommonConfigurationKeys.INCREMENTAL_COMPILATION] == true
        val workaround = when {
            incrementalCompilationEnabled ->
                "incremental compilation (kotlin.incremental.native=false)"
            else ->
                "compiler caches (https://kotl.in/disable-native-cache)"
        }
        """
            Please try to disable $workaround and rerun the build.

            Also, consider filing an issue with full Gradle log here: https://kotl.in/issue
            """.trimIndent()
    } else null

    val extraUserSetupInfo = run {
        context.config.resolvedLibraries.getFullList()
                .filter { it.isCInteropLibrary() }
                .mapNotNull { library ->
                    library.manifestProperties["userSetupHint"]?.let {
                        "From ${library.uniqueName}:\n$it".takeIf { it.isNotEmpty() }
                    }
                }
                .mapIndexed { index, message -> "$index. $message" }
                .takeIf { it.isNotEmpty() }
                ?.joinToString(separator = "\n\n")
                ?.let {
                    "It seems your project produced link errors.\nProposed solutions:\n\n$it\n"
                }
    }

    val extraInfo = listOfNotNull(extraUserInfo, extraUserSetupInfo).joinToString(separator = "\n")

    context.reportCompilationError("${e.toolName} invocation reported errors\n$extraInfo\n${e.message}")
}

internal fun File.normalize(): File = toPath().normalize().toFile()

internal val File.parentOrNull: String?
    get() = parent

internal fun File.writeLines(lines: Iterable<String>) {
    val list = lines.toList()
    if (list.isEmpty()) {
        writeText("")
    } else {
        writeText(list.joinToString("\n", postfix = "\n"))
    }
}

internal fun File.appendLines(lines: Iterable<String>) {
    val list = lines.toList()
    if (list.isEmpty()) return
    val existing = if (exists) readText() else ""
    writeText(existing + list.joinToString("\n", postfix = "\n"))
}

internal fun File.readStrings(): List<String> {
    val text = readText()
    if (text.isEmpty()) return emptyList()
    val lines = text.lines()
    return if (lines.isNotEmpty() && lines.last().isEmpty()) lines.dropLast(1) else lines
}

internal fun File.forEachLine(action: (String) -> Unit) {
    readStrings().forEach(action)
}

internal fun File.appendBytes(bytes: ByteArray) {
    val existing = if (exists) readBytes() else ByteArray(0)
    writeBytes(existing + bytes)
}

internal fun File.copyTo(destination: File) {
    toPath().copyTo(destination.toPath(), overwrite = true)
}

internal fun File.deleteRecursively() {
    toPath().deleteRecursively()
}

internal fun File.delete(): Boolean = toPath().deleteIfExists()

internal fun File.renameTo(destination: File): Boolean {
    if (destination.exists) destination.deleteRecursively()
    return platform.posix.rename(this.path, destination.path) == 0
}

internal fun File.createNew(): Boolean {
    if (exists) return false
    writeBytes(ByteArray(0))
    return true
}

internal fun File.createAsSymlink(target: String) {
    File(target).copyTo(this)
}

internal fun org.jetbrains.kotlin.konan.host.NativeFile.javaFile(): File = File(this.path)

internal fun org.jetbrains.kotlin.konan.host.NativeFile.writeLines(lines: Iterable<String>) {
    File(this.path).writeLines(lines)
}
