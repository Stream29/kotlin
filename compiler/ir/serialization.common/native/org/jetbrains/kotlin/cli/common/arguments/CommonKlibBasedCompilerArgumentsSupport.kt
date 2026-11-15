/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
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
    var pluginClasspaths: Array<String> = emptyArray()
    var pluginOptions: Array<String> = emptyArray()
    var pluginConfigurations: Array<String> = emptyArray()
    var pluginOrderConstraints: Array<String> = emptyArray()
    var listPhases: Boolean = false
}

open class CommonKlibBasedCompilerArguments : CommonCompilerArguments() {
    var partialLinkageMode: String? = null
    var partialLinkageLogLevel: String? = null
}

open class K2JSCompilerArguments : CommonKlibBasedCompilerArguments()

open class K2NativeCompilerArguments : CommonKlibBasedCompilerArguments() {
    var libraries: Array<String> = emptyArray()
    var friendModules: String? = null
    var includes: Array<String> = emptyArray()
    var listTargets: Boolean = false
    var checkDependencies: Boolean = false
    var produce: String? = null
    var exportedLibraries: Array<String> = emptyArray()
    var libraryToAddToCache: String? = null
    var compileFromBitcode: String? = null
    var shortModuleName: String? = null
}

val KProperty1<out CommonCompilerArguments, *>.cliArgument: String
    get() = when (name) {
        CommonKlibBasedCompilerArguments::partialLinkageMode.name -> "-Xpartial-linkage"
        CommonKlibBasedCompilerArguments::partialLinkageLogLevel.name -> "-Xpartial-linkage-loglevel"
        "klibZipFileAccessorCacheLimit" -> "-Xklib-zip-file-accessor-cache-limit"
        "duplicatedUniqueNameStrategy" -> "-Xklib-duplicated-unique-name-strategy"
        "irInlinerBeforeKlibSerialization" -> "-Xklib-ir-inliner"
        "customKlibAbiVersion" -> "-Xklib-abi-version"
        "enableSignatureClashChecks" -> "-Xklib-enable-signature-clash-checks"
        "relativePathBases" -> "-Xklib-relative-path-base"
        "skipLibrarySpecialCompatibilityChecks" -> "-Xskip-library-special-compatibility-checks"
        "separateKmpCompilationScheme" -> "-Xseparate-kmp-compilation"
        "fragmentRefines" -> "-Xfragment-refines"
        "fragmentSources" -> "-Xfragment-sources"
        "fragmentDependencies" -> "-Xfragment-dependencies"
        "fragmentFriendDependencies" -> "-Xfragment-friend-dependencies"
        "fragments" -> "-Xfragments"
        "allowPre17RuntimeJdk" -> "-Xallow-pre-17-runtime-jdk"
        K2NativeCompilerArguments::libraries.name -> "-library"
        K2NativeCompilerArguments::friendModules.name -> "-friend-modules"
        K2NativeCompilerArguments::exportedLibraries.name -> "-Xexport-library"
        K2NativeCompilerArguments::includes.name -> "-Xinclude"
        K2NativeCompilerArguments::libraryToAddToCache.name -> "-Xadd-cache"
        K2NativeCompilerArguments::shortModuleName.name -> "-Xshort-module-name"
        K2NativeCompilerArguments::compileFromBitcode.name -> "-Xcompile-from-bitcode"
        else -> "-X$name"
    }

fun KProperty1<out CommonCompilerArguments, *>.cliArgument(value: String): String =
    "${this.cliArgument}=$value"
