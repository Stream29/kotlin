/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.konan.util

import org.jetbrains.kotlin.io.parseSpaceSeparatedArgs
import org.jetbrains.kotlin.konan.properties.Properties
import org.jetbrains.kotlin.konan.target.KonanTarget

val <T : Enum<T>> T.visibleName get() = name.lowercase()

interface Named {
    val name: String
    val visibleName get() = name
}

sealed interface DefFileProperty<T> {
    val propertyName: String
    fun parse(rawValue: String?): T

    enum class StringListProperty(override val propertyName: String, val isSubstitutable: Boolean = false) : DefFileProperty<List<String>> {
        Headers("headers", isSubstitutable = true),
        Modules("modules", isSubstitutable = true),
        CompilerOpts("compilerOpts", isSubstitutable = true),
        EntryPoints("entryPoint"),
        LinkerOpts("linkerOpts", isSubstitutable = true),
        ExcludedFunctions("excludedFunctions", isSubstitutable = true),
        ExcludedMacros("excludedMacros"),
        StaticLibraries("staticLibraries", isSubstitutable = true),
        LibraryPaths("libraryPaths", isSubstitutable = true),
        HeaderFilter("headerFilter"),
        ExcludeFilter("excludeFilter"),
        StrictEnums("strictEnums"),
        NonStrictEnums("nonStrictEnums"),
        NoStringConversion("noStringConversion"),
        Depends("depends"),
        ExportForwardDeclarations("exportForwardDeclarations"),
        AllowedOverloadsForCFunctions("allowedOverloadsForCFunctions"),
        ObjcClassesIncludingCategories("objcClassesIncludingCategories");

        override fun parse(rawValue: String?): List<String> = rawValue?.let(::parseSpaceSeparatedArgs) ?: emptyList()
    }

    enum class NullableStringProperty(override val propertyName: String, val defaultValue: String? = null) : DefFileProperty<String?> {
        Language("language"),
        Linker("linker", defaultValue = "clang"),
        PackageName("package"),
        ForeignExceptionMode("foreignExceptionMode"),
        UserSetupHint("userSetupHint");

        override fun parse(rawValue: String?): String? = rawValue ?: defaultValue
    }

    enum class BooleanProperty(override val propertyName: String) : DefFileProperty<Boolean> {
        ExcludeSystemLibs("excludeSystemLibs"),
        ExcludeDependentModules("excludeDependentModules"),
        DisableDesignatedInitializerChecks("disableDesignatedInitializerChecks"),
        AllowIncludingObjCCategoriesFromDefFile("allowIncludingObjCCategoriesFromDefFile"),
        SkipNonImportableModules("skipNonImportableModules");

        override fun parse(rawValue: String?): Boolean = rawValue.toBoolean()
    }

    companion object {
        val substitutablePropertyNames: Set<String> by lazy {
            StringListProperty.values().filter { it.isSubstitutable }.map { it.propertyName }.toSet()
        }
    }
}

fun Properties.substituteFor(target: KonanTarget): Properties {
    val tagetSuffix = target.visibleName
    val archSuffix = target.architecture.visibleName
    val familySuffix = target.family.visibleName

    val substitutionMap: MutableMap<String, Substitutions> = hashMapOf()

    val result = Properties()

    for (propertyName in stringPropertyNames()) {
        val propertyValue = getProperty(propertyName) ?: continue
        result[propertyName] = propertyValue

        val dotIndex = propertyName.indexOf('.')
        if (dotIndex != -1) {
            val basePropertyName = propertyName.take(dotIndex)
            if (basePropertyName in DefFileProperty.substitutablePropertyNames) {
                fun getSubstitution() = substitutionMap.getOrPut(basePropertyName) { Substitutions() }

                when (propertyName.substring(dotIndex + 1)) {
                    tagetSuffix -> getSubstitution().targetValue = propertyValue
                    archSuffix -> getSubstitution().archValue = propertyValue
                    familySuffix -> getSubstitution().familyValue = propertyValue
                }
            }
        }
    }

    for ((basePropertyName, substitutions) in substitutionMap) {
        result[basePropertyName] = listOfNotNull(
            result.getProperty(basePropertyName),
            substitutions.targetValue,
            substitutions.archValue,
            substitutions.familyValue,
        ).joinToString(" ")
    }

    return result
}

private class Substitutions {
    var targetValue: String? = null
    var archValue: String? = null
    var familyValue: String? = null
}
