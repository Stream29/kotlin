/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.konan.config

import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

object NativeConfigurationKeys {
    val KONAN_NO_STDLIB = CompilerConfigurationKey.create<Boolean>("KONAN_NO_STDLIB")

    val KONAN_NO_DEFAULT_LIBS = CompilerConfigurationKey.create<Boolean>("KONAN_NO_DEFAULT_LIBS")

    val KONAN_HOME = CompilerConfigurationKey.create<String>("KONAN_HOME")

    val KONAN_LIBRARIES = CompilerConfigurationKey.create<List<String>>("KONAN_LIBRARIES")

    val KONAN_FRIEND_LIBRARIES = CompilerConfigurationKey.create<List<String>>("KONAN_FRIEND_LIBRARIES")

    val KONAN_INCLUDED_LIBRARIES = CompilerConfigurationKey.create<List<String>>("KONAN_INCLUDED_LIBRARIES")
}

var CompilerConfiguration.konanNoStdlib: Boolean
    get() = getBoolean(NativeConfigurationKeys.KONAN_NO_STDLIB)
    set(value) { put(NativeConfigurationKeys.KONAN_NO_STDLIB, value) }

var CompilerConfiguration.konanNoDefaultLibs: Boolean
    get() = getBoolean(NativeConfigurationKeys.KONAN_NO_DEFAULT_LIBS)
    set(value) { put(NativeConfigurationKeys.KONAN_NO_DEFAULT_LIBS, value) }

var CompilerConfiguration.konanHome: String?
    get() = get(NativeConfigurationKeys.KONAN_HOME)
    set(value) { putIfNotNull(NativeConfigurationKeys.KONAN_HOME, value) }

var CompilerConfiguration.konanLibraries: List<String>
    get() = getList(NativeConfigurationKeys.KONAN_LIBRARIES)
    set(value) { put(NativeConfigurationKeys.KONAN_LIBRARIES, value) }

var CompilerConfiguration.konanFriendLibraries: List<String>
    get() = getList(NativeConfigurationKeys.KONAN_FRIEND_LIBRARIES)
    set(value) { put(NativeConfigurationKeys.KONAN_FRIEND_LIBRARIES, value) }

var CompilerConfiguration.konanIncludedLibraries: List<String>
    get() = getList(NativeConfigurationKeys.KONAN_INCLUDED_LIBRARIES)
    set(value) { put(NativeConfigurationKeys.KONAN_INCLUDED_LIBRARIES, value) }
