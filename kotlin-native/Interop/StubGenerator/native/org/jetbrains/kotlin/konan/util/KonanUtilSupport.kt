/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.konan.util

import org.jetbrains.kotlin.konan.file.File as KlibFile
import org.jetbrains.kotlin.konan.file.resolve
import org.jetbrains.kotlin.konan.host.NativeFile
import org.jetbrains.kotlin.konan.host.environment
import org.jetbrains.kotlin.konan.host.nameWithoutExtension
import org.jetbrains.kotlin.konan.properties.Properties
import org.jetbrains.kotlin.konan.target.KonanTarget
import org.jetbrains.kotlin.konan.util.DefFile.DefFileConfig
import org.jetbrains.kotlin.konan.util.DefFileProperty.BooleanProperty.*
import org.jetbrains.kotlin.konan.util.DefFileProperty.NullableStringProperty.*
import org.jetbrains.kotlin.konan.util.DefFileProperty.StringListProperty.*
import kotlin.reflect.KProperty

typealias ProgressCallback = (url: String, currentBytes: Long, totalBytes: Long) -> Unit

object CInteropHints {
    val fmodulesHint = """
            It seems that library is using clang modules. Try adding `-compiler-option -fmodules` to cinterop.
            For example, in case of cocoapods plugin:
            
            pod("PodName") {
                // Add these lines
                extraOpts += listOf("-compiler-option", "-fmodules")
            }
        """.trimIndent()
}

object DependencyDirectories {
    private const val DEPENDENCIES_FOLDER_NAME = "dependencies"
    private const val CACHE_FOLDER_NAME = "cache"

    val localKonanDir: KlibFile
        get() = getLocalKonanDir()

    val defaultDependenciesRoot: KlibFile
        get() = getDependenciesRoot()

    fun getLocalKonanDir(konanDataDir: String? = null): KlibFile {
        val home = environment("HOME") ?: environment("USERPROFILE") ?: environment("TEMP") ?: "/tmp"
        return KlibFile(
            konanDataDir
                ?: environment("KONAN_DATA_DIR")
                ?: "$home/.konan"
        )
    }

    fun getDependenciesRoot(konanDataDir: String? = null): KlibFile =
        getLocalKonanDir(konanDataDir).resolve(DEPENDENCIES_FOLDER_NAME)

    fun getDependencyCacheDir(konanDataDir: String? = null): KlibFile =
        getLocalKonanDir(konanDataDir).resolve(CACHE_FOLDER_NAME)
}

class DefFile(
    val file: NativeFile?,
    val config: DefFileConfig,
    val manifestAddendProperties: Properties,
    val defHeaderLines: List<String>,
) {
    val name by lazy {
        file?.nameWithoutExtension ?: ""
    }

    class DefFileConfig(private val properties: Properties) {
        private operator fun <T> DefFileProperty<T>.getValue(thisRef: DefFileConfig, property: KProperty<*>): T =
            parse(thisRef.properties.getProperty(propertyName))

        val headers: List<String> by Headers
        val modules: List<String> by Modules
        val language: String? by Language
        val compilerOpts: List<String> by CompilerOpts
        val excludeSystemLibs: Boolean by ExcludeSystemLibs
        val excludeDependentModules: Boolean by ExcludeDependentModules
        val entryPoints: List<String> by EntryPoints
        val linkerOpts: List<String> by LinkerOpts
        val linker: String? by Linker
        val excludedFunctions: List<String> by ExcludedFunctions
        val excludedMacros: List<String> by ExcludedMacros
        val staticLibraries: List<String> by StaticLibraries
        val libraryPaths: List<String> by LibraryPaths
        val packageName: String? by PackageName
        val headerFilter: List<String> by HeaderFilter
        val excludeFilter: List<String> by ExcludeFilter
        val strictEnums: List<String> by StrictEnums
        val nonStrictEnums: List<String> by NonStrictEnums
        val noStringConversion: List<String> by NoStringConversion
        val depends: List<String> by Depends
        val exportForwardDeclarations: List<String> by ExportForwardDeclarations
        val allowedOverloadsForCFunctions: List<String> by AllowedOverloadsForCFunctions
        val disableDesignatedInitializerChecks: Boolean by DisableDesignatedInitializerChecks
        val foreignExceptionMode: String? by ForeignExceptionMode
        val objcClassesIncludingCategories: List<String> by ObjcClassesIncludingCategories
        val allowIncludingObjCCategoriesFromDefFile: Boolean by AllowIncludingObjCCategoriesFromDefFile
        val userSetupHint: String? by UserSetupHint
        val skipNonImportableModules by SkipNonImportableModules
    }
}

private fun parseDefFile(file: NativeFile?, target: KonanTarget): Triple<Properties, Properties, List<String>> {
    val properties = Properties()

    if (file == null) {
        return Triple(properties, Properties(), emptyList())
    }

    val lines = file.readLines()

    val separator = "---"
    val separatorIndex = lines.indexOf(separator)

    val propertyLines: List<String>
    val headerLines: List<String>

    if (separatorIndex != -1) {
        propertyLines = lines.subList(0, separatorIndex)
        headerLines = lines.subList(separatorIndex + 1, lines.size)
    } else {
        propertyLines = lines
        headerLines = emptyList()
    }

    val joinedLines = propertyLines.joinToString("\n")
    val escapedTokens = joinedLines.split('"')
    val postprocessProperties = escapedTokens.mapIndexed { index, token ->
        if (index % 2 != 0) {
            token.replace("""\\(?=.)""".toRegex(), Regex.escapeReplacement("""\\"""))
        } else {
            token
        }
    }.joinToString("\"")
    properties.load(postprocessProperties.encodeToByteArray())

    val manifestAddendProperties = properties.copy()

    return Triple(properties.substituteFor(target), manifestAddendProperties, headerLines)
}

fun DefFile(file: NativeFile?, target: KonanTarget): DefFile {
    val (substitutedProperties, originalProperties, headerLines) = parseDefFile(file, target)
    return DefFile(file, DefFileConfig(substitutedProperties), originalProperties, headerLines)
}
