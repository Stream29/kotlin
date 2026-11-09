/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.common.arguments

import kotlin.reflect.KProperty1

open class CommonCompilerArguments {
    var disableDefaultScriptingPlugin: Boolean = false
    var reportPerf: Boolean = false
    var dumpPerf: String? = null
    var allWarningsAsErrors: Boolean = false
    var reportAllWarnings: Boolean = false
    var freeArgs: List<String> = emptyList()
    var version: Boolean = false
    var pluginClasspaths: Array<String>? = null
    var pluginOptions: Array<String>? = null
    var pluginConfigurations: Array<String>? = null
    var pluginOrderConstraints: Array<String>? = null
    var listPhases: Boolean = false
}

open class CommonKlibBasedCompilerArguments : CommonCompilerArguments() {
    var libraries: String? = null
    var friendModules: String? = null
    var includes: String? = null
    var partialLinkageMode: String? = null
    var partialLinkageLogLevel: String? = null
}

open class K2JSCompilerArguments : CommonKlibBasedCompilerArguments()

open class K2NativeCompilerArguments : CommonKlibBasedCompilerArguments() {
    var listTargets: Boolean = false
    var checkDependencies: Boolean = false
    var produce: String? = null
    var exportedLibraries: Array<String>? = null
    var libraryToAddToCache: String? = null
    var compileFromBitcode: Array<String>? = null
}

val KProperty1<out CommonCompilerArguments, *>.cliArgument: String
    get() = when (name) {
        CommonKlibBasedCompilerArguments::partialLinkageMode.name -> "-Xpartial-linkage"
        CommonKlibBasedCompilerArguments::partialLinkageLogLevel.name -> "-Xpartial-linkage-loglevel"
        else -> "-X$name"
    }

fun KProperty1<out CommonCompilerArguments, *>.cliArgument(value: String): String =
    "${this.cliArgument}=$value"
