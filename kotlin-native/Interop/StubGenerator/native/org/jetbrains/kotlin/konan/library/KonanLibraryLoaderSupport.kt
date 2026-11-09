/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.konan.library

import nativekotlin.host.files.NativeFile as File
import org.jetbrains.kotlin.konan.file.listFilesOrEmpty
import org.jetbrains.kotlin.konan.file.resolve
import org.jetbrains.kotlin.konan.properties.Properties
import org.jetbrains.kotlin.konan.target.KonanTarget
import org.jetbrains.kotlin.konan.util.substituteFor
import org.jetbrains.kotlin.library.Klib
import org.jetbrains.kotlin.library.loader.KlibLibraryProvider
import org.jetbrains.kotlin.library.loader.KlibManifestTransformer

const val KONAN_STDLIB_NAME = "stdlib"

const val KONAN_DISTRIBUTION_KLIB_DIR = "klib"
const val KONAN_DISTRIBUTION_COMMON_LIBS_DIR = "common"
const val KONAN_DISTRIBUTION_PLATFORM_LIBS_DIR = "platform"
const val KONAN_DISTRIBUTION_COMMONIZED_LIBS_DIR = "commonized"
const val KLIB_INTEROP_IR_PROVIDER_IDENTIFIER = "kotlin.native.cinterop"

const val KONAN_DISTRIBUTION_SOURCES_DIR = "sources"
const val KONAN_DISTRIBUTION_TOOLS_DIR = "tools"

fun konanCommonLibraryPath(libraryName: String): File =
    File(KONAN_DISTRIBUTION_KLIB_DIR, KONAN_DISTRIBUTION_COMMON_LIBS_DIR).resolve(libraryName)

fun konanPlatformLibraryPath(libraryName: String, platform: String): File =
    File(KONAN_DISTRIBUTION_KLIB_DIR, KONAN_DISTRIBUTION_PLATFORM_LIBS_DIR).resolve(platform).resolve(libraryName)

const val KONAN_PLATFORM_LIBS_NAME_PREFIX = "org.jetbrains.kotlin.native.platform."

class KlibNativeDistributionLibraryProvider(
    private val nativeHome: File,
    init: KlibNativeDistributionLibraryProviderSpec.() -> Unit,
) : KlibLibraryProvider {
    private var withStdlib = false
    private var withPlatformLibsForTarget: KonanTarget? = null

    init {
        object : KlibNativeDistributionLibraryProviderSpec {
            override fun withStdlib() {
                withStdlib = true
            }

            override fun withPlatformLibs(target: KonanTarget) {
                withPlatformLibsForTarget = target
            }
        }.init()
    }

    override fun getLibraryPaths(): List<String> = buildList {
        if (withStdlib) {
            this += nativeHome.resolve(KONAN_DISTRIBUTION_KLIB_DIR)
                .resolve(KONAN_DISTRIBUTION_COMMON_LIBS_DIR)
                .resolve(KONAN_STDLIB_NAME)
                .path
        }

        withPlatformLibsForTarget?.let { target ->
            nativeHome.resolve(KONAN_DISTRIBUTION_KLIB_DIR)
                .resolve(KONAN_DISTRIBUTION_PLATFORM_LIBS_DIR)
                .resolve(target.visibleName)
                .listFilesOrEmpty
                .mapNotNullTo(this) { if (it.isDirectory && it.name.startsWith(KONAN_PLATFORM_LIBS_NAME_PREFIX)) it.path else null }
        }
    }

    override fun postProcessLoadedLibrary(klib: Klib) {
        klib.isImplicitlyLoadedFromKotlinNativeDistribution = true
    }
}

interface KlibNativeDistributionLibraryProviderSpec {
    fun withStdlib()
    fun withPlatformLibs(target: KonanTarget)
}

class KlibNativeManifestTransformer(private val target: KonanTarget) : KlibManifestTransformer {
    override fun transform(manifestProperties: Properties): Properties = manifestProperties.substituteFor(target)
}
