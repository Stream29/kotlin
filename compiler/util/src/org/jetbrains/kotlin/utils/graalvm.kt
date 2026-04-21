/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.utils


val isGraalNativeimage: Boolean get() = isGraalNativeImageRuntime || isGraalNatibeImageBuildtime
val isGraalNativeImageRuntime: Boolean get() = System.getProperty("org.graalvm.nativeimage.imagecode") == "runtime"

val isGraalNatibeImageBuildtime: Boolean get() = System.getProperty("org.graalvm.nativeimage.imagecode") == "buildtime"
