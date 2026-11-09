/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.platform.konan

import org.jetbrains.kotlin.konan.target.KonanTarget
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.platform.toTargetPlatform

data class NativePlatformWithTarget(val target: KonanTarget) : NativePlatform() {
    override fun toString() = super.toString() // override the method generated for data class

    override val targetName: String
        get() = target.visibleName

    override val oldFashionedDescription: String
        get() = "Kotlin/Native "
}

private val predefinedNativeTargetToSimpleNativePlatform: Map<KonanTarget, NativePlatformWithTarget> =
    KonanTarget.predefinedTargets.values.associateWith { NativePlatformWithTarget(it) }

private val predefinedNativeTargetToNativePlatform: Map<KonanTarget, TargetPlatform> =
    predefinedNativeTargetToSimpleNativePlatform.mapValues { [_, simplePlatform] -> simplePlatform.toTargetPlatform() }

private val allNativePlatformsList: List<TargetPlatform> =
    listOf(NativePlatforms.unspecifiedNativePlatform) + predefinedNativeTargetToNativePlatform.values

val NativePlatforms.allNativePlatforms: List<TargetPlatform>
    get() = allNativePlatformsList

fun NativePlatforms.nativePlatformBySingleTarget(target: KonanTarget): TargetPlatform =
    predefinedNativeTargetToNativePlatform[target] ?: unspecifiedNativePlatform

fun NativePlatforms.nativePlatformByTargets(targets: Collection<KonanTarget>): TargetPlatform {
    val simplePlatforms = targets.mapNotNullTo(HashSet()) { predefinedNativeTargetToSimpleNativePlatform[it] }
    return when (simplePlatforms.size) {
        0 -> unspecifiedNativePlatform
        1 -> nativePlatformBySingleTarget(simplePlatforms.first().target)
        else -> TargetPlatform(simplePlatforms)
    }
}

fun NativePlatforms.nativePlatformByTargetNames(targets: Collection<String>): TargetPlatform =
    nativePlatformByTargets(targets.mapNotNull { KonanTarget.predefinedTargets[it] })

fun TargetPlatform?.isNative(): Boolean = this != null && this.size > 0 && all { it is NativePlatform }

private val legacyNativePlatformUnspecifiedTargetSerializedRepresentation = "${NativePlatformUnspecifiedTarget.platformName} []"
fun NativePlatformUnspecifiedTarget.legacySerializeToString(): String = legacyNativePlatformUnspecifiedTargetSerializedRepresentation
