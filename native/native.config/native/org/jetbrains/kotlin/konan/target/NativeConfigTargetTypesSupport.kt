/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("PackageDirectoryMismatch")

package org.jetbrains.kotlin.konan.target

import org.jetbrains.kotlin.konan.target.KonanTarget.*

enum class CompilerOutputKind {
    PROGRAM {
        override fun suffix(target: KonanTarget?) = ".${target!!.family.exeSuffix}"
    },
    DYNAMIC {
        override fun suffix(target: KonanTarget?) = ".${target!!.family.dynamicSuffix}"
        override fun prefix(target: KonanTarget?) = target!!.family.dynamicPrefix
    },
    STATIC {
        override fun suffix(target: KonanTarget?) = ".${target!!.family.staticSuffix}"
        override fun prefix(target: KonanTarget?) = target!!.family.staticPrefix
    },
    FRAMEWORK {
        override fun suffix(target: KonanTarget?): String = ".framework"
    },
    LIBRARY {
        override fun suffix(target: KonanTarget?) = ".klib"
    },
    BITCODE {
        override fun suffix(target: KonanTarget?) = ".bc"
    },
    TEST_BUNDLE {
        override fun suffix(target: KonanTarget?): String = ".xctest"
    },
    DYNAMIC_CACHE {
        override fun suffix(target: KonanTarget?) = ".${target!!.family.dynamicSuffix}"
        override fun prefix(target: KonanTarget?) = target!!.family.dynamicPrefix
    },
    STATIC_CACHE {
        override fun suffix(target: KonanTarget?) = ".${target!!.family.staticSuffix}"
        override fun prefix(target: KonanTarget?) = target!!.family.staticPrefix
    },
    HEADER_CACHE {
        override fun suffix(target: KonanTarget?) = ".header"
    };

    abstract fun suffix(target: KonanTarget? = null): String
    open fun prefix(target: KonanTarget? = null): String = ""
}

open class HostManager {
    val targetValues: List<KonanTarget> by lazy { KonanTarget.predefinedTargets.values.toList() }

    val targets = targetValues.associateBy { it.visibleName }

    fun toKonanTargets(names: Iterable<String>): List<KonanTarget> {
        return names.map {
            if (it == "host") host else targets.getValue(known(resolveAlias(it)))
        }
    }

    fun known(name: String): String {
        if (targets[name] == null) {
            throw TargetSupportException("Unknown target: $name. Use -list_targets to see the list of available targets")
        }
        return name
    }

    fun targetByName(name: String): KonanTarget {
        if (name == "host") return host
        return targets[resolveAlias(name)] ?: throw TargetSupportException("Unknown target name: $name")
    }

    private val commonTargets = setOf(
        LINUX_X64,
        LINUX_ARM32_HFP,
        LINUX_ARM64,
        MINGW_X64,
        ANDROID_X86,
        ANDROID_X64,
        ANDROID_ARM32,
        ANDROID_ARM64,
    )

    private val appleTargets = setOf(
        MACOS_X64,
        MACOS_ARM64,
        IOS_ARM64,
        IOS_X64,
        IOS_SIMULATOR_ARM64,
        WATCHOS_ARM32,
        WATCHOS_ARM64,
        WATCHOS_X64,
        WATCHOS_SIMULATOR_ARM64,
        WATCHOS_DEVICE_ARM64,
        TVOS_ARM64,
        TVOS_X64,
        TVOS_SIMULATOR_ARM64,
    )

    val enabledByHost: Map<KonanTarget, Set<KonanTarget>> = mapOf(
        LINUX_X64 to commonTargets,
        LINUX_ARM64 to commonTargets,
        MINGW_X64 to commonTargets,
        MACOS_X64 to commonTargets + appleTargets,
        MACOS_ARM64 to commonTargets + appleTargets
    )

    val enabled: List<KonanTarget>
        get() = enabledByHost[host]?.toList() ?: throw TargetSupportException("Unknown host platform: $host")

    fun isEnabled(target: KonanTarget) = enabled.contains(target)

    companion object {
        @Deprecated("Use `hostOs` instead", ReplaceWith("HostManager.hostOs()"))
        fun host_os(): String = hostOs()

        fun hostOs(): String = when (Platform.osFamily) {
            OsFamily.MACOSX -> "osx"
            OsFamily.LINUX -> "linux"
            OsFamily.WINDOWS -> "windows"
            else -> throw TargetSupportException("Unknown operating system: ${Platform.osFamily}")
        }

        fun simpleOsName(): String {
            val hostOs = hostOs()
            return if (hostOs == "osx") "macos" else hostOs
        }

        fun platformName(): String {
            val hostOs = runCatching { hostOs() }.getOrDefault("unknown_os")
            val arch = runCatching { hostArch() }.getOrDefault("unknown_arch")
            return when (hostOs) {
                "osx" -> "macos-$arch"
                else -> "$hostOs-$arch"
            }
        }

        val jniHostPlatformIncludeDir: String
            get() = when (host) {
                MACOS_X64, MACOS_ARM64 -> "darwin"
                LINUX_X64, LINUX_ARM64 -> "linux"
                MINGW_X64 -> "win32"
                else -> throw TargetSupportException("Unknown host: $host.")
            }

        @Deprecated("Use `hostArch` instead", ReplaceWith("HostManager.hostArch()"))
        fun host_arch(): String = hostArch()

        fun hostArch(): String =
            hostArchOrNull()
                ?: throw TargetSupportException("Unknown hardware platform: ${Platform.cpuArchitecture}")

        fun hostArchOrNull(): String? = when (Platform.cpuArchitecture) {
            CpuArchitecture.X64 -> "x86_64"
            CpuArchitecture.ARM64 -> "aarch64"
            else -> null
        }

        private val hostMapping: Map<Pair<String, String>, KonanTarget> = mapOf(
            Pair("osx", "x86_64") to MACOS_X64,
            Pair("osx", "aarch64") to MACOS_ARM64,
            Pair("linux", "x86_64") to LINUX_X64,
            Pair("linux", "aarch64") to LINUX_ARM64,
            Pair("windows", "x86_64") to MINGW_X64
        )

        val host: KonanTarget get() = determineHost(hostOs(), hostArchOrNull())

        val hostOrNull: KonanTarget?
            get() = try {
                host
            } catch (_: TargetSupportException) {
                null
            }

        private fun determineHost(os: String, arch: String?): KonanTarget {
            hostMapping[os to arch]?.let { return it }
            throw TargetSupportException("Unknown host target: $os $arch")
        }

        val defaultJvmArgs = listOf("-XX:TieredStopAtLevel=1", "-ea", "-Dfile.encoding=UTF-8")
        val regularJvmArgs = defaultJvmArgs + "-Xmx3G"

        val hostIsSupported: Boolean get() = hostOrNull != null
        val hostIsMac get() = hostOrNull?.family == Family.OSX
        val hostIsLinux get() = hostOrNull?.family == Family.LINUX
        val hostIsMingw get() = hostOrNull?.family == Family.MINGW

        val hostName: String
            get() = host.name

        private val targetAliasResolutions = mapOf(
            "linux" to "linux_x64",
            "macbook" to "macos_x64",
            "macos" to "macos_x64",
            "imac" to "macos_x64",
            "raspberrypi" to "linux_arm32_hfp",
            "iphone" to "ios_arm64",
            "ipad" to "ios_arm64",
            "ios" to "ios_arm64",
            "iphone_sim" to "ios_x64",
            "mingw" to "mingw_x64"
        )

        private val targetAliases: Map<String, List<String>> by lazy {
            val result = mutableMapOf<String, MutableList<String>>()
            targetAliasResolutions.entries.forEach {
                result.getOrPut(it.value) { mutableListOf() }.add(it.key)
            }
            result
        }

        fun resolveAlias(request: String): String = targetAliasResolutions[request] ?: request
        fun listAliases(target: String): List<String> = targetAliases[target] ?: emptyList()
    }
}

class TargetSupportException(message: String = "", cause: Throwable? = null) : Exception(message, cause)
