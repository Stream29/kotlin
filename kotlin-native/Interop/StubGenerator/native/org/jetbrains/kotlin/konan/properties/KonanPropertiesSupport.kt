/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.konan.properties

import org.jetbrains.kotlin.io.propertyList
import org.jetbrains.kotlin.io.resolvablePropertyList
import org.jetbrains.kotlin.io.resolvablePropertyString
import org.jetbrains.kotlin.konan.file.File
import org.jetbrains.kotlin.konan.file.absolutePath
import org.jetbrains.kotlin.konan.file.resolve
import org.jetbrains.kotlin.konan.target.Configurables
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.KonanTarget
import org.jetbrains.kotlin.konan.target.hostTargetSuffix
import org.jetbrains.kotlin.konan.util.ProgressCallback

fun Properties.hostString(name: String, host: KonanTarget): String? =
    this.resolvablePropertyString(name, suffix = host.name)

fun Properties.hostList(name: String, host: KonanTarget): List<String> =
    this.resolvablePropertyList(name, suffix = host.name)

fun Properties.targetString(name: String, target: KonanTarget): String? =
    this.resolvablePropertyString(name, suffix = target.name)

fun Properties.targetList(name: String, target: KonanTarget): List<String> =
    this.resolvablePropertyList(name, suffix = target.name)

fun Properties.hostTargetString(name: String, target: KonanTarget, host: KonanTarget): String? =
    this.resolvablePropertyString(name, suffix = hostTargetSuffix(host, target))

fun Properties.hostTargetList(name: String, target: KonanTarget, host: KonanTarget): List<String> =
    this.resolvablePropertyList(name, suffix = hostTargetSuffix(host, target))

interface TargetableExternalStorage {
    fun targetString(key: String): String?
    fun targetList(key: String): List<String>
    fun hostString(key: String): String?
    fun hostList(key: String): List<String>
    fun hostTargetString(key: String): String?
    fun hostTargetList(key: String): List<String>
    fun absolute(value: String?): String
    fun downloadDependencies()
}

abstract class KonanPropertiesLoader(
    override val target: KonanTarget,
    val properties: Properties,
    private val dependenciesRoot: String?,
    private val host: KonanTarget = HostManager.host,
    private val progressCallback: ProgressCallback,
) : Configurables {

    private val predefinedLlvmDistributions: Set<String> =
        properties.propertyList("predefinedLlvmDistributions").toSet()

    private val predefinedLibffiVersions: Set<String> =
        properties.propertyList("predefinedLibffiVersions").toSet()

    private fun getPredefinedDependencyOrNull(
        dependencyName: String,
        dependencyAccessor: () -> String?,
        predefinedDependencies: Set<String>,
    ): String? {
        val dependency = dependencyAccessor()
            ?: error("Undefined $dependencyName!")
        return when (dependency) {
            in predefinedDependencies -> dependency
            else -> null
        }
    }

    private fun compilerDependencies(): List<String> = listOfNotNull(
        getPredefinedDependencyOrNull("LLVM home", this::llvmHome, predefinedLlvmDistributions),
        getPredefinedDependencyOrNull("libffi version", this::libffiDir, predefinedLibffiVersions)
    )

    open val dependencies: List<String>
        get() = hostTargetList("dependencies") + compilerDependencies()

    override fun downloadDependencies() {
        // Offline Native build environment: dependencies are pre-installed or local.
    }

    override fun targetString(key: String): String? = properties.targetString(key, target)
    override fun targetList(key: String): List<String> = properties.targetList(key, target)
    override fun hostString(key: String): String? = properties.hostString(key, host)
    override fun hostList(key: String): List<String> = properties.hostList(key, host)
    override fun hostTargetString(key: String): String? = properties.hostTargetString(key, target, host)
    override fun hostTargetList(key: String): List<String> = properties.hostTargetList(key, target, host)

    override fun absolute(value: String?): String {
        val nonNull = value ?: error("Cannot resolve null path")
        if (nonNull.startsWith("/")) {
            return File(nonNull).absolutePath
        }
        val root = dependenciesRoot ?: error("dependenciesRoot is not set")
        val firstSegment = nonNull.substringBefore('/')
        val rest = if ('/' in nonNull) nonNull.substringAfter('/') else ""
        val profiles = properties.propertyList("dependencyProfiles").ifEmpty { listOf("default") }
        for (profile in profiles) {
            val candidates = properties.propertyList("$firstSegment.$profile")
            for (candidate in candidates) {
                if (!candidate.startsWith("remote:") && File(candidate).exists) {
                    val base = File(candidate)
                    return (if (rest.isEmpty()) base else base.resolve(rest)).absolutePath
                }
            }
        }
        return File(root).resolve(nonNull).absolutePath
    }
}
