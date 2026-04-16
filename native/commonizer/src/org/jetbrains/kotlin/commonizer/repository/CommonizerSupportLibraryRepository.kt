/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.commonizer.repository

import org.jetbrains.kotlin.commonizer.*
import org.jetbrains.kotlin.commonizer.konan.DefaultModulesProvider
import org.jetbrains.kotlin.commonizer.konan.NativeLibrariesToCommonize
import org.jetbrains.kotlin.commonizer.konan.NativeLibrary
import org.jetbrains.kotlin.konan.target.KonanTarget
import org.jetbrains.kotlin.util.Logger
import java.io.File

const val SUPPORT_LIB_PATH = "/Users/Nikolay.Lunyak/Documents/Projects/CommonizerE2ESample/module1/build/classes/kotlin/metadata"

val SUPPORT_LIB_FILE = File(SUPPORT_LIB_PATH)

val supportHierarchy = mapOf(
    "linuxArm64" to "linuxMain",
    "linuxX64" to "linuxMain",

    "iosX64" to "iosMain",
    "iosArm64" to "iosMain",
    "iosSimulatorArm64" to "iosMain",

    "macosArm64" to "macosMain",

    "tvosArm64" to "tvosMain",
    "tvosSimulatorArm64" to "tvosMain",

    "watchosArm32" to "watchosMain",
    "watchosArm64" to "watchosMain",
    "watchosDeviceArm64" to "watchosMain",
    "watchosSimulatorArm64" to "watchosMain",

    "iosMain" to "appleMain",
    "macosMain" to "appleMain",
    "tvosMain" to "appleMain",
    "watchosMain" to "appleMain",

    "linuxMain" to "nativeMain",
    "appleMain" to "nativeMain",
    "mingwX64" to "nativeMain",
)

class SupportHierarchyTarget(val name: String, val targets: MutableList<SupportHierarchyTarget>)

fun buildSupportHierarchyTargets(): Map<String, SupportHierarchyTarget> {
    val supportHierarchyTargets = (supportHierarchy.keys.toSet() + supportHierarchy.values)
        .associateWith { SupportHierarchyTarget(it, mutableListOf()) }

    for ((key, value) in supportHierarchy) {
        supportHierarchyTargets[value]!!.targets.add(supportHierarchyTargets[key]!!)
    }

    return supportHierarchyTargets
}

fun toCommonizerTargets(supportHierarchyTargets: Map<String, SupportHierarchyTarget>): Map<String, CommonizerTarget> {
    val supportHierarchyTargetCache = mutableMapOf<SupportHierarchyTarget, CommonizerTarget>()
    val leafTargets = KonanTarget.predefinedTargets.mapKeys { it.key.replace("_", "").lowercase() }
    fun SupportHierarchyTarget.toCommonizerTarget(): CommonizerTarget {
        return supportHierarchyTargetCache.getOrPut(this) {
            val leaf = leafTargets[name.lowercase()]
            when {
                leaf != null -> LeafCommonizerTarget(leaf)
                else -> SharedCommonizerTarget(targets.flatMap { it.toCommonizerTarget().konanTargets })
            }
        }
    }
    return supportHierarchyTargets.mapValues { it.value.toCommonizerTarget() }
}

internal fun loadSupportLibraries(logger: Logger): Map<String, NativeLibrary> {
    val supportLibSharedTargets = SUPPORT_LIB_FILE.list { _, name -> name.endsWith("Main") }!!
    val supportNativeLibraries = supportLibSharedTargets.associateWith {
        val file = SUPPORT_LIB_FILE.resolve(it).resolve("klib").resolve("module1_$it")
        DefaultNativeLibraryLoader(logger).invoke(file)
    }

    return supportNativeLibraries
}

fun loadSupport(parameters: CommonizerParameters) {
    val supportHierarchyTargets = buildSupportHierarchyTargets()
    val supportHierarchyCommonizerTargets = toCommonizerTargets(supportHierarchyTargets)
    val supportNativeLibraries = loadSupportLibraries(parameters.logger!!)

    val supportLibTargetProviders = supportNativeLibraries.mapValues { (it, nativeLibrary) ->
        TargetProvider(
            target = supportHierarchyCommonizerTargets[it]!!,
            modulesProvider = DefaultModulesProvider.create(
                NativeLibrariesToCommonize(supportHierarchyCommonizerTargets[it]!!, listOf(nativeLibrary))
            )
        )
    }
    val supportLibTrees = supportLibTargetProviders.mapValues { (_, targetProvider) ->
        deserializeTarget(parameters, targetProvider)
    }

    val modulesProvider = DefaultModulesProvider.forDependencies(supportNativeLibraries.values, parameters.logger)
}

internal class CommonizerSupportLibraryRepository(val logger: Logger) : Repository {
    val libraries by lazy {
        val supportLibraries = loadSupportLibraries(logger)
        val supportHierarchyTargets = buildSupportHierarchyTargets()
        val supportHierarchyCommonizerTargets = toCommonizerTargets(supportHierarchyTargets)

        supportHierarchyCommonizerTargets.entries.mapNotNull { (name, target) ->
            supportLibraries[name]?.let { target to it }
        }.toMap()
    }

    override fun getLibraries(target: CommonizerTarget): Set<NativeLibrary> =
        libraries.filterKeys { it.konanTargets.containsAll(target.konanTargets) }.values.toSet()
}
