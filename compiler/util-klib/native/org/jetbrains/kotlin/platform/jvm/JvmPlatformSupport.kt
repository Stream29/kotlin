/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.platform.jvm

import org.jetbrains.kotlin.platform.SimplePlatform
import org.jetbrains.kotlin.platform.TargetPlatform

abstract class JvmPlatform : SimplePlatform("JVM") {
    override val oldFashionedDescription: String
        get() = "JVM "
}

fun TargetPlatform?.isJvm(): Boolean =
    this != null && componentPlatforms.isNotEmpty() && componentPlatforms.all { it is JvmPlatform || it.platformName == "JVM" }
