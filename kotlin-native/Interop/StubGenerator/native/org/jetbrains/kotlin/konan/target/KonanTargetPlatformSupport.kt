/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.konan.target

import org.jetbrains.kotlin.io.loadProperties
import org.jetbrains.kotlin.konan.file.File
import org.jetbrains.kotlin.konan.file.absolutePath
import org.jetbrains.kotlin.konan.file.canonicalFile
import org.jetbrains.kotlin.konan.file.child
import org.jetbrains.kotlin.konan.file.listFiles
import org.jetbrains.kotlin.konan.host.environment
import org.jetbrains.kotlin.konan.properties.KonanPropertiesLoader
import org.jetbrains.kotlin.konan.properties.Properties
import org.jetbrains.kotlin.konan.properties.TargetableExternalStorage
import org.jetbrains.kotlin.konan.target.KonanTarget.*
import org.jetbrains.kotlin.konan.util.DependencyDirectories
import org.jetbrains.kotlin.konan.util.ProgressCallback
import org.jetbrains.kotlin.library.KlibPath
import org.jetbrains.kotlin.library.KlibPaths


enum class SanitizerKind {
    ADDRESS,
    THREAD,
}

val SanitizerKind?.targetSuffix: String
    get() = when (this) {
        null -> ""
        SanitizerKind.THREAD -> "_tsan"
        SanitizerKind.ADDRESS -> "_asan"
    }

@Deprecated(
    level = DeprecationLevel.WARNING,
    message = "K/N subtargets are removed. You can freely remove the usages of SubTargetProvider from your code",
)
interface SubTargetProvider {
    fun availableSubTarget(genericName: String): List<String>

    @Suppress("DEPRECATION")
    object NoSubTargets : SubTargetProvider {
        override fun availableSubTarget(genericName: String): List<String> = emptyList()
    }
}

data class TargetTriple(
    val architecture: String,
    val vendor: String,
    val os: String,
    val environment: String?,
) {
    companion object {
        fun fromString(tripleString: String): TargetTriple {
            val components = tripleString.split('-')
            require(components.size == 3 || components.size == 4) {
                "Malformed target triple: $tripleString. Expected format: <arch>-<vendor>-<os>-<environment?>."
            }
            return TargetTriple(
                architecture = components[0],
                vendor = components[1],
                os = components[2],
                environment = components.getOrNull(3),
            )
        }
    }

    override fun toString(): String {
        val envSuffix = environment?.let { "-$environment" } ?: ""
        return "$architecture-$vendor-$os$envSuffix"
    }
}

val TargetTriple.isSimulator: Boolean
    get() = environment == "simulator"

val TargetTriple.isMacabi: Boolean
    get() = environment == "macabi"

fun TargetTriple.withOSVersion(osVersion: String): TargetTriple =
    copy(os = "${os}${osVersion}")

fun TargetTriple.withoutVendor(): String {
    val envSuffix = environment?.let { "-$environment" } ?: ""
    return "$architecture-$os$envSuffix"
}

enum class BinaryFormat {
    ELF,
    PE_COFF,
    MACH_O,
}

fun KonanTarget.binaryFormat() = when (family) {
    Family.WATCHOS -> BinaryFormat.MACH_O
    Family.IOS -> BinaryFormat.MACH_O
    Family.TVOS -> BinaryFormat.MACH_O
    Family.OSX -> BinaryFormat.MACH_O
    Family.ANDROID -> BinaryFormat.ELF
    Family.LINUX -> BinaryFormat.ELF
    Family.MINGW -> BinaryFormat.PE_COFF
}

fun KonanTarget.pointerBits() = when (architecture) {
    Architecture.X64 -> 64
    Architecture.X86 -> 32
    Architecture.ARM64 -> if (this == KonanTarget.WATCHOS_ARM64) 32 else 64
    Architecture.ARM32 -> 32
}

fun KonanTarget.supportsLibBacktrace(): Boolean =
    this.family.isAppleFamily ||
        this.family == Family.LINUX ||
        this.family == Family.ANDROID

fun KonanTarget.supportsCoreSymbolication(): Boolean =
    this in listOf(
        KonanTarget.MACOS_X64,
        KonanTarget.MACOS_ARM64,
        KonanTarget.IOS_X64,
        KonanTarget.IOS_SIMULATOR_ARM64,
        KonanTarget.TVOS_X64,
        KonanTarget.TVOS_SIMULATOR_ARM64,
        KonanTarget.WATCHOS_X64,
        KonanTarget.WATCHOS_SIMULATOR_ARM64,
    )

fun KonanTarget.supportsGccUnwind(): Boolean = family == Family.ANDROID || family == Family.LINUX
fun KonanTarget.supportsWinAPIUnwind(): Boolean = this is KonanTarget.MINGW_X64

fun KonanTarget.supportsObjcInterop(): Boolean = family.isAppleFamily
fun KonanTarget.hasFoundationFramework(): Boolean = family.isAppleFamily
fun KonanTarget.hasUIKitFramework(): Boolean = family == Family.IOS || family == Family.TVOS
fun KonanTarget.supports64BitMulOverflow(): Boolean = when (this) {
    is KonanTarget.LINUX_ARM32_HFP -> false
    is KonanTarget.ANDROID_ARM32 -> false
    is KonanTarget.ANDROID_X86 -> false
    else -> true
}

fun KonanTarget.supportsIosCrashLog(): Boolean = when (this) {
    KonanTarget.IOS_ARM64 -> true
    KonanTarget.WATCHOS_ARM32 -> true
    KonanTarget.WATCHOS_ARM64 -> true
    KonanTarget.TVOS_ARM64 -> true
    else -> false
}

fun KonanTarget.supports64BitAtomics(): Boolean = when (architecture) {
    Architecture.ARM32 -> false
    Architecture.X86, Architecture.ARM64, Architecture.X64 -> true
} && this != KonanTarget.WATCHOS_ARM64

fun KonanTarget.supportsUnalignedAccess(): Boolean = when (architecture) {
    Architecture.ARM32 -> false
    Architecture.X86, Architecture.ARM64, Architecture.X64 -> true
} && this != KonanTarget.WATCHOS_ARM64

fun KonanTarget.needSmallBinary() = when {
    family == Family.WATCHOS -> true
    family.isAppleFamily -> architecture == Architecture.ARM32
    else -> false
}

fun KonanTarget.supportedSanitizers(): List<SanitizerKind> =
    when (this) {
        is KonanTarget.LINUX_X64 -> listOf(SanitizerKind.ADDRESS, SanitizerKind.THREAD)
        is KonanTarget.MACOS_X64 -> listOf(SanitizerKind.THREAD)
        is KonanTarget.MACOS_ARM64 -> listOf(SanitizerKind.THREAD)
        else -> listOf()
    }

val KonanTarget.supportsGrandCentralDispatch: Boolean
    get() = family.isAppleFamily

val KonanTarget.supportsSignposts: Boolean
    get() = family.isAppleFamily

fun HostManager.targetManager(userRequest: String? = null): TargetManager = TargetManagerImpl(userRequest, this)


interface TargetManager {
    val target: KonanTarget
    val targetName: String
    fun list()
}

internal class TargetManagerImpl(val userRequest: String?, val hostManager: HostManager) : TargetManager {
    override val target = determineCurrent()
    override val targetName: String
        get() = target.visibleName

    override fun list() {
        hostManager.enabled.forEach {
            val isDefault = if (it == target) " (default)" else ""
            val isDeprecated = if (it in KonanTarget.deprecatedTargets) " (deprecated)" else ""
            println("${it.visibleName}$isDefault$isDeprecated")
        }
    }

    private fun determineCurrent(): KonanTarget {
        return if (userRequest == null || userRequest == "host") {
            HostManager.host
        } else {
            val resolvedAlias = HostManager.resolveAlias(userRequest)
            hostManager.targets.getValue(hostManager.known(resolvedAlias))
        }
    }
}

fun hostTargetSuffix(host: KonanTarget, target: KonanTarget) =
    if (target == host) host.name else "${host.name}-${target.name}"

class Distribution(
    val konanHome: String,
    private val onlyDefaultProfiles: Boolean = false,
    private val runtimeFileOverride: String? = null,
    private val propertyOverrides: Map<String, String>? = null,
    private val konanDataDir: String? = null,
) {
    val localKonanDir = DependencyDirectories.getLocalKonanDir(konanDataDir)

    val konanSubdir = "$konanHome/konan"
    val mainPropertyFileName = "$konanSubdir/konan.properties"

    private fun propertyFilesFromConfigDir(configDir: String, genericName: String): List<File> {
        val directory = File(configDir, "platforms/$genericName")
        return if (directory.isDirectory) directory.listFiles else emptyList()
    }

    private fun preconfiguredPropertyFiles(genericName: String) =
        propertyFilesFromConfigDir(konanSubdir, genericName)

    private fun userPropertyFiles(genericName: String) =
        propertyFilesFromConfigDir(localKonanDir.absolutePath, genericName)

    fun additionalPropertyFiles(genericName: String) =
        preconfiguredPropertyFiles(genericName) + userPropertyFiles(genericName)

    val properties by lazy {
        val result = Properties()

        fun loadPropertiesSafely(source: File) {
            if (source.isFile) result.putAll(source.loadProperties())
        }

        loadPropertiesSafely(File(mainPropertyFileName))

        if (onlyDefaultProfiles) {
            result.keepOnlyDefaultProfiles()
        }
        propertyOverrides?.let(result::putAll)
        result
    }

    val compilerVersion by lazy {
        getCompilerVersion(properties["compilerVersion"], konanHome)
    }

    val klib = "$konanHome/klib"
    val stdlib = "$klib/common/stdlib"
    val stdlibDefaultComponent = "$stdlib/default"

    val kotlinRuntimeForSwiftHome = "$konanHome/konan/swift_export/kotlin_runtime"
    val kotlinRuntimeForSwiftModuleMap = "$kotlinRuntimeForSwiftHome/module.modulemap"

    fun defaultNatives(target: KonanTarget) = "$konanHome/konan/targets/${target.visibleName}/native"

    fun runtime(target: KonanTarget) = runtimeFileOverride ?: "${defaultNatives(target)}/runtime.bc"

    fun compilerInterface(target: KonanTarget) =
        runtimeFileOverride ?: "${defaultNatives(target)}/compiler_interface.bc"

    fun platformDefs(target: KonanTarget) = "$konanHome/konan/platformDef/${target.visibleName}"

    fun platformLibs(target: KonanTarget) = "$klib/platform/${target.visibleName}"

    val launcherFiles = listOf("launcher.bc")

    val dependenciesDir = DependencyDirectories
        .getDependenciesRoot(konanDataDir)
        .absolutePath

    companion object {
        private fun getBundleVersion(konanHome: String): String? =
            if (konanHome.contains("-1"))
                konanHome.substring(konanHome.lastIndexOf("-1") + 1)
            else
                null

        fun getCompilerVersion(propertyVersion: String?, konanHome: String): String? =
            propertyVersion ?: getBundleVersion(konanHome)
    }
}

fun buildDistribution(konanHome: String, konanDataDir: String? = null) =
    Distribution(konanHome, true, null, konanDataDir = konanDataDir)

fun customerDistribution(konanHome: String, konanDataDir: String? = null) =
    Distribution(konanHome, false, null, konanDataDir = konanDataDir)

private fun Properties.keepOnlyDefaultProfiles() {
    val DEPENDENCY_PROFILES_KEY = "dependencyProfiles"
    val dependencyProfiles = this.getProperty(DEPENDENCY_PROFILES_KEY)
    if (dependencyProfiles != "default alt")
        error("unexpected $DEPENDENCY_PROFILES_KEY value: expected 'default alt', got '$dependencyProfiles'")
    this.setProperty(DEPENDENCY_PROFILES_KEY, "default")
}

interface RelocationModeFlags : TargetableExternalStorage {
    val dynamicLibraryRelocationMode get() = targetString("dynamicLibraryRelocationMode").mode()
    val staticLibraryRelocationMode get() = targetString("staticLibraryRelocationMode").mode()
    val executableRelocationMode get() = targetString("executableRelocationMode").mode()

    private fun String?.mode(): Mode = when (this?.lowercase()) {
        null -> Mode.DEFAULT
        "pic" -> Mode.PIC
        "static" -> Mode.STATIC
        else -> error("Unknown relocation mode: $this")
    }

    enum class Mode {
        PIC,
        STATIC,
        DEFAULT,
    }
}

interface ClangFlags : TargetableExternalStorage, RelocationModeFlags {
    val clangFlags get() = targetList("clangFlags")
    val clangNooptFlags get() = targetList("clangNooptFlags")
    val clangOptFlags get() = targetList("clangOptFlags")
    val clangDebugFlags get() = targetList("clangDebugFlags")
}

interface LldFlags : TargetableExternalStorage {
    val lldFlags get() = targetList("lld")
}

interface Configurables : TargetableExternalStorage, RelocationModeFlags {
    val target: KonanTarget
    val targetTriple: TargetTriple
        get() = targetString("targetTriple")
            ?.let(TargetTriple.Companion::fromString)
            ?: error("quadruple for $target is not set.")

    val llvmHome get() = hostString("llvmHome")
    val llvmVersion get() = hostString("llvmVersion")
    val libffiDir get() = hostString("libffiDir")

    val cacheableTargets get() = hostList("cacheableTargets")
    val additionalCacheFlags get() = targetList("additionalCacheFlags")

    val linkerOptimizationFlags get() = targetList("linkerOptimizationFlags")
    val linkerKonanFlags get() = targetList("linkerKonanFlags")
    val linkerNoDebugFlags get() = targetList("linkerNoDebugFlags")
    val linkerDynamicFlags get() = targetList("linkerDynamicFlags")
    val targetSysRoot get() = targetString("targetSysRoot")

    val targetToolchain get() = hostTargetString("targetToolchain")

    val absoluteTargetSysRoot get() = absolute(targetSysRoot)
    val absoluteTargetToolchain get() = absolute(targetToolchain)
    val absoluteLlvmHome get() = absolute(llvmHome)

    val targetCpu get() = targetString("targetCpu")
    val targetCpuFeatures get() = targetString("targetCpuFeatures")
    val llvmInlineThreshold get() = targetString("llvmInlineThreshold")

    val runtimeDefinitions get() = targetList("runtimeDefinitions")
}

interface ConfigurablesWithEmulator : Configurables {
    val emulatorDependency get() = hostTargetString("emulatorDependency")
    val emulatorExecutable get() = hostTargetString("emulatorExecutable")
    val absoluteEmulatorExecutable get() = absolute(emulatorExecutable)
}

interface AppleConfigurables : Configurables, ClangFlags {
    val arch get() = targetTriple.architecture
    val osVersionMin get() = targetString("osVersionMin")!!
    val sdkVersion get() = targetString("sdkVersion")!!
    val stripFlags get() = targetList("stripFlags")
    val additionalToolsDir get() = hostString("additionalToolsDir")
    val absoluteAdditionalToolsDir get() = absolute(additionalToolsDir)
}

interface MingwConfigurables : Configurables, ClangFlags {
    val linker get() = hostTargetString("linker")!!
    val absoluteLinker get() = absolute(linker)

    val windowsKit: WindowsKit
    val msvc: Msvc

    val windowsKitParts get() = hostString("windowsKitParts")!!
    val msvcParts get() = hostString("msvcParts")!!
}

interface GccConfigurables : Configurables, ClangFlags {
    val gccToolchain get() = targetString("gccToolchain")
    val absoluteGccToolchain get() = absolute(gccToolchain)

    val libGcc get() = targetString("libGcc")!!
    val dynamicLinker get() = targetString("dynamicLinker")!!
    val abiSpecificLibraries get() = targetList("abiSpecificLibraries")
    val crtFilesLocation get() = targetString("crtFilesLocation")!!

    val linker get() = hostTargetString("linker")
    val linkerHostSpecificFlags get() = hostTargetList("linkerHostSpecificFlags")
    val absoluteLinker get() = absolute(linker)

    val linkerGccFlags get() = targetList("linkerGccFlags")
}

interface AndroidConfigurables : Configurables, ClangFlags

sealed class Msvc {
    abstract fun compilerFlags(): List<String>

    object DefaultPath : Msvc() {
        override fun compilerFlags(): List<String> = emptyList()
    }

    class CustomPath(private val msvcParts: KlibPath) : Msvc() {
        override fun compilerFlags(): List<String> = buildList {
            val pathStr = msvcParts.toAbsolutePath().toString()
            addAll(listOf("-Xmicrosoft-visualc-tools-root", pathStr))
            add("-Wl,-vctoolsdir:$pathStr")
        }
    }
}

sealed class WindowsKit {
    abstract fun compilerFlags(): List<String>

    object DefaultPath : WindowsKit() {
        override fun compilerFlags(): List<String> = emptyList()
    }

    class CustomPath(private val windowsKitParts: KlibPath, val target: KonanTarget) : WindowsKit() {
        override fun compilerFlags(): List<String> = buildList {
            val pathStr = windowsKitParts.toAbsolutePath().toString()
            addAll(listOf("-Xmicrosoft-windows-sdk-root", pathStr))
            add("-Wl,-winsdkdir:$pathStr")
            val arch = when (target) {
                KonanTarget.MINGW_X64 -> "x64"
                else -> error("WindowsKit.CustomPath supports only mingw_x64 for now.")
            }
            val libUmPath = windowsKitParts.resolve("Lib").resolve("um").resolve(arch)
            addAll(listOf("-L", libUmPath.toAbsolutePath().toString()))
        }
    }
}

class MingwConfigurablesImpl(
    target: KonanTarget,
    properties: Properties,
    dependenciesRoot: String?,
    progressCallback: ProgressCallback,
) : MingwConfigurables, KonanPropertiesLoader(target, properties, dependenciesRoot, progressCallback = progressCallback) {
    override val windowsKit: WindowsKit = WindowsKit.DefaultPath
    override val msvc: Msvc = Msvc.DefaultPath
}

class AppleConfigurablesImpl(
    target: KonanTarget,
    properties: Properties,
    dependenciesDir: String?,
    progressCallback: ProgressCallback,
) : AppleConfigurables, KonanPropertiesLoader(target, properties, dependenciesDir, progressCallback = progressCallback) {
    override val absoluteTargetSysRoot: String
        get() = absolute(targetSysRoot)

    override val absoluteTargetToolchain: String
        get() = "${absolute(targetToolchain)}/usr"

    override val absoluteAdditionalToolsDir: String
        get() = absolute(additionalToolsDir)
}

fun AppleConfigurables.platformName(): String = when (target.family) {
    Family.OSX -> "MacOSX"
    Family.IOS -> if (targetTriple.isSimulator) "iPhoneSimulator" else "iPhoneOS"
    Family.TVOS -> if (targetTriple.isSimulator) "AppleTVSimulator" else "AppleTVOS"
    Family.WATCHOS -> if (targetTriple.isSimulator) "WatchSimulator" else "WatchOS"
    else -> error("Not an Apple target: $target")
}

class GccConfigurablesImpl(
    target: KonanTarget,
    properties: Properties,
    dependenciesRoot: String?,
    progressCallback: ProgressCallback,
) : GccConfigurables,
    KonanPropertiesLoader(target, properties, dependenciesRoot, progressCallback = progressCallback),
    ConfigurablesWithEmulator {
    override val dependencies: List<String>
        get() = super.dependencies + listOfNotNull(emulatorDependency)
}

class AndroidConfigurablesImpl(
    target: KonanTarget,
    properties: Properties,
    dependenciesRoot: String?,
    progressCallback: ProgressCallback,
) : AndroidConfigurables, KonanPropertiesLoader(target, properties, dependenciesRoot, progressCallback = progressCallback)

fun loadConfigurables(
    target: KonanTarget,
    properties: Properties,
    dependenciesRoot: String?,
    progressCallback: ProgressCallback = { url, currentBytes, totalBytes ->
        print("\n(KonanProperties) Downloading dependency: $url (${currentBytes}/${totalBytes}). ")
    },
): Configurables = when (target.family) {
    Family.LINUX -> GccConfigurablesImpl(target, properties, dependenciesRoot, progressCallback)
    Family.TVOS, Family.WATCHOS, Family.IOS, Family.OSX -> AppleConfigurablesImpl(target, properties, dependenciesRoot, progressCallback)
    Family.ANDROID -> AndroidConfigurablesImpl(target, properties, dependenciesRoot, progressCallback)
    Family.MINGW -> MingwConfigurablesImpl(target, properties, dependenciesRoot, progressCallback)
}

internal object Android {
    const val API = "21"
    private val architectureMap = mapOf(
        KonanTarget.ANDROID_X86 to "x86",
        KonanTarget.ANDROID_X64 to "x86_64",
        KonanTarget.ANDROID_ARM32 to "arm",
        KonanTarget.ANDROID_ARM64 to "arm64",
    )

    fun architectureDirForTarget(target: KonanTarget) =
        "android-${API}/arch-${architectureMap.getValue(target)}"
}

sealed class ClangArgs(
    private val configurables: Configurables,
    private val forJni: Boolean,
) {
    private val absoluteTargetToolchain = configurables.absoluteTargetToolchain
    private val absoluteTargetSysRoot = configurables.absoluteTargetSysRoot
    private val absoluteLlvmHome = configurables.absoluteLlvmHome
    private val target = configurables.target
    private val targetTriple = configurables.targetTriple

    private val argsForWindowsJni = forJni && target == KonanTarget.MINGW_X64

    @Deprecated("args for building runtime moved to runtime building code")
    val clangArgsSpecificForKonanSources: List<String>
        get() {
            val konanOptions = listOfNotNull(
                target.architecture.name.takeIf { target != KonanTarget.WATCHOS_ARM64 },
                "ARM32".takeIf { target == KonanTarget.WATCHOS_ARM64 },
                target.family.name.takeIf { target.family != Family.MINGW },
                "WINDOWS".takeIf { target.family == Family.MINGW },
                "MACOSX".takeIf { target.family == Family.OSX },
                "APPLE".takeIf { target.family.isAppleFamily },
                "NO_64BIT_ATOMIC".takeUnless { target.supports64BitAtomics() },
                "NO_UNALIGNED_ACCESS".takeUnless { target.supportsUnalignedAccess() },
                "FORBID_BUILTIN_MUL_OVERFLOW".takeUnless { target.supports64BitMulOverflow() },
                "OBJC_INTEROP".takeIf { target.supportsObjcInterop() },
                "HAS_FOUNDATION_FRAMEWORK".takeIf { target.hasFoundationFramework() },
                "HAS_UIKIT_FRAMEWORK".takeIf { target.hasUIKitFramework() },
                "REPORT_BACKTRACE_TO_IOS_CRASH_LOG".takeIf { target.supportsIosCrashLog() },
                "SUPPORTS_GRAND_CENTRAL_DISPATCH".takeIf { target.supportsGrandCentralDispatch },
                "SUPPORTS_SIGNPOSTS".takeIf { target.supportsSignposts },
            ).map { "KONAN_$it=1" }
            val otherOptions = listOfNotNull(
                "USE_ELF_SYMBOLS=1".takeIf { target.binaryFormat() == BinaryFormat.ELF },
                "ELFSIZE=${target.pointerBits()}".takeIf { target.binaryFormat() == BinaryFormat.ELF },
                "MACHSIZE=${target.pointerBits()}".takeIf { target.binaryFormat() == BinaryFormat.MACH_O },
                "__ANDROID__".takeIf { target.family == Family.ANDROID },
                "USE_PE_COFF_SYMBOLS=1".takeIf { target.binaryFormat() == BinaryFormat.PE_COFF },
                "UNICODE".takeIf { target.family == Family.MINGW },
                "USE_WINAPI_UNWIND=1".takeIf { target.supportsWinAPIUnwind() },
                "USE_GCC_UNWIND=1".takeIf { target.supportsGccUnwind() },
            )
            return (konanOptions + otherOptions).map { "-D$it" }
        }

    private val binDir = "$absoluteTargetToolchain/bin"

    private val commonClangArgs: List<String> = mutableListOf<List<String>>().apply {
        if (!argsForWindowsJni) {
            add(listOf("-B$binDir"))
        } else {
            require(configurables is MingwConfigurables)
            add(configurables.msvc.compilerFlags())
            add(configurables.windowsKit.compilerFlags())
            add(listOf("-fuse-ld=lld"))
        }
        add(listOf("-fno-stack-protector"))
        if (configurables is GccConfigurables) {
            add(listOf("--gcc-toolchain=${configurables.absoluteGccToolchain}"))
        }
        val targetString: String = when {
            argsForWindowsJni -> "x86_64-pc-windows-msvc"
            configurables is AppleConfigurables -> {
                targetTriple.copy(
                    os = "${targetTriple.os}${configurables.osVersionMin}"
                ).toString()
            }
            else -> configurables.targetTriple.toString()
        }
        add(listOf("-target", targetString))
        val hasCustomSysroot = configurables is AndroidConfigurables || argsForWindowsJni
        if (!hasCustomSysroot) {
            when (configurables) {
                is AppleConfigurables -> {
                    add(listOf("-isysroot", absoluteTargetSysRoot))
                    if (targetTriple.isMacabi) {
                        add(
                            listOf(
                                "-L$absoluteTargetSysRoot/System/iOSSupport/usr/lib",
                                "-L$absoluteTargetToolchain/lib/swift/maccatalyst",
                                "-F$absoluteTargetSysRoot/System/iOSSupport/System/Library/Frameworks",
                            )
                        )
                    }
                }
                else -> add(listOf("--sysroot=$absoluteTargetSysRoot"))
            }
        }
        if (configurables !is MingwConfigurables) {
            add(listOf("-fPIC"))
        }
    }.flatten()

    private val specificClangArgs: List<String> = when (target) {
        KonanTarget.LINUX_ARM32_HFP -> listOf("-mfpu=vfp", "-mfloat-abi=hard")
        KonanTarget.WATCHOS_ARM32 -> listOf("-marm")
        KonanTarget.ANDROID_ARM32, KonanTarget.ANDROID_ARM64,
        KonanTarget.ANDROID_X86, KonanTarget.ANDROID_X64 -> {
            val clangTarget = targetTriple.withoutVendor()
            val architectureDir = Android.architectureDirForTarget(target)
            val toolchainSysroot = "$absoluteTargetToolchain/sysroot"
            listOf(
                "-D__ANDROID_API__=${Android.API}",
                "--sysroot=$absoluteTargetSysRoot/$architectureDir",
                "-I$toolchainSysroot/usr/include/c++/v1",
                "-I$toolchainSysroot/usr/include",
                "-I$toolchainSysroot/usr/include/$clangTarget",
            ) + when (target) {
                KonanTarget.ANDROID_ARM64 -> listOf("-mno-outline-atomics")
                else -> emptyList()
            }
        }
        else -> emptyList()
    }

    val clangPaths = listOf("$absoluteLlvmHome/bin", binDir)

    val clangArgs: Array<String> = (commonClangArgs + specificClangArgs).toTypedArray()

    val clangXXArgs: Array<String> = clangArgs + when (configurables) {
        is AppleConfigurables -> arrayOf("-stdlib=libc++")
        else -> emptyArray()
    }

    @Deprecated("args for building runtime moved to runtime building code")
    @Suppress("DEPRECATION")
    val clangArgsForKonanSources = clangXXArgs + clangArgsSpecificForKonanSources

    @Deprecated("args for building runtime moved to runtime building code")
    @Suppress("DEPRECATION")
    val clangArgsForKonanCxxSources = (clangXXArgs + clangArgsSpecificForKonanSources).asList()

    @Deprecated("args for building runtime moved to runtime building code")
    @Suppress("DEPRECATION")
    val clangArgsForKonanCSources = (clangArgs + clangArgsSpecificForKonanSources).asList()

    private val libclangSpecificArgs =
        listOf("-isystem", "$absoluteLlvmHome/lib/clang/${configurables.llvmVersion}/include")

    val libclangArgs: List<String> = libclangSpecificArgs + clangArgs

    val libclangXXArgs: List<String> = libclangSpecificArgs + clangXXArgs

    private val targetClangCmd = listOf("${absoluteLlvmHome}/bin/clang") + clangArgs
    private val targetClangXXCmd = listOf("${absoluteLlvmHome}/bin/clang++") + clangXXArgs
    private val targetArCmd = listOf("${absoluteLlvmHome}/bin/llvm-ar")

    fun clangC(vararg userArgs: String) = targetClangCmd + userArgs.asList()
    fun clangCXX(vararg userArgs: String) = targetClangXXCmd + userArgs.asList()
    fun llvmAr(vararg userArgs: String) = targetArCmd + userArgs.asList()

    class Jni(configurables: Configurables) : ClangArgs(configurables, forJni = true) {
        private val jdkDir by lazy {
            val javaHome = environment("JAVA_HOME")?.let(::File)
            val candidates = listOfNotNull(javaHome, javaHome?.parentFile)
            candidates.firstOrNull { it.child("include").exists }?.canonicalFile?.absolutePath
                ?: error("JNI headers not found")
        }

        val hostCompilerArgsForJni: Array<String> by lazy {
            listOf("", HostManager.jniHostPlatformIncludeDir)
                .map { "-I$jdkDir/include/$it" }
                .toTypedArray()
        }
    }

    class Native(configurables: Configurables) : ClangArgs(configurables, forJni = false)
}

class Platform(val configurables: Configurables) : Configurables by configurables {
    val clang: ClangArgs.Native by lazy {
        ClangArgs.Native(configurables)
    }

    val clangForJni: ClangArgs.Jni by lazy {
        ClangArgs.Jni(configurables)
    }
}

class PlatformManager(private val distribution: Distribution) : HostManager() {
    fun targetManager(userRequest: String? = null): TargetManager = TargetManagerImpl(userRequest, this)
    @Suppress("UNUSED_PARAMETER")
    @Deprecated("Kept temporary, should be removed after the bootstrap")
    constructor(konanHome: String, experimental: Boolean = false, konanDataDir: String? = null) :
        this(Distribution(konanHome, konanDataDir = konanDataDir))

    @Suppress("UNUSED_PARAMETER")
    @Deprecated("Kept temporary, should be removed after the bootstrap")
    constructor(distribution: Distribution, experimental: Boolean = false) :
        this(distribution)

    constructor(konanHome: String, konanDataDir: String? = null) :
        this(Distribution(konanHome, konanDataDir = konanDataDir))

    private val loaders = enabled.associateWith {
        loadConfigurables(it, distribution.properties, distribution.dependenciesDir)
    }

    private val platforms = loaders.mapValues { Platform(it.value) }

    fun platform(target: KonanTarget) = platforms.getValue(target)
    val hostPlatform = platforms.getValue(host)

    fun loader(target: KonanTarget) = loaders.getValue(target)
}

abstract class AbstractToolConfig(
    konanHome: String,
    userProvidedTargetName: String?,
    propertyOverrides: Map<String, String>,
    konanDataDir: String? = null,
) {
    private val distribution = Distribution(konanHome, propertyOverrides = propertyOverrides, konanDataDir = konanDataDir)
    private val platformManager = PlatformManager(distribution)
    private val targetManager = platformManager.targetManager(userProvidedTargetName)
    private val host = HostManager.host
    val target = targetManager.target

    protected val platform = platformManager.platform(target)

    fun downloadDependencies() = platform.downloadDependencies()

    val llvmHome = platform.absoluteLlvmHome
    val sysRoot = platform.absoluteTargetSysRoot

    val libclang = when (host) {
        KonanTarget.MINGW_X64 -> "$llvmHome/bin/libclang.dll"
        else -> "$llvmHome/lib/${host.family.dynamicPrefix}clang.${host.family.dynamicSuffix}"
    }

    abstract fun loadLibclang()

    fun prepare() {
        downloadDependencies()
        loadLibclang()
    }
}
