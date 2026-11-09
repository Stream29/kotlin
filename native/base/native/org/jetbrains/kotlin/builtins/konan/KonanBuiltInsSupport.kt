/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("PackageDirectoryMismatch")

package org.jetbrains.kotlin.builtins.konan

import org.jetbrains.kotlin.K1Deprecation
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.builtins.StandardNames.COROUTINES_PACKAGE_FQ_NAME
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.findClassAcrossModuleDependencies
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.storage.StorageManager

@K1Deprecation
class KonanBuiltIns(storageManager: StorageManager) : KotlinBuiltIns(storageManager) {
    val builtInsModule: ModuleDescriptorImpl
        get() = getBuiltInsModule()

    override fun getSuspendFunction(parameterCount: Int): ClassDescriptor =
        getBuiltInsModule().findClassAcrossModuleDependencies(
            ClassId(
                COROUTINES_PACKAGE_FQ_NAME,
                Name.identifier("SuspendFunction$parameterCount")
            )
        )!!
}
