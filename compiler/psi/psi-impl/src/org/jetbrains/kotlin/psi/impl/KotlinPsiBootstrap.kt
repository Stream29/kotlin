/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin.psi.impl

import com.intellij.core.NativeCompilerRuntime
import com.intellij.core.CoreFileTypeRegistry
import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.extensions.PluginId
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.parsing.KotlinParserDefinition
import org.jetbrains.kotlin.psi.stubs.elements.KotlinStubRuntimeMetadata

/**
 * Kotlin's real parser/context registration on the actual Native host.
 * This is not the complete compiler DI graph or a substitute Project.
 */
object KotlinPsiBootstrap {
    fun install(plugin: PluginId): NativeCompilerRuntime = NativeCompilerRuntime.install(
        listOf(KotlinLanguage.INSTANCE to KotlinParserDefinition()),
    ) { application, _, lifetime ->
        KotlinStubRuntimeMetadata.registerFileMethodOwners(plugin, lifetime)
        KotlinStubRuntimeMetadata.registerFileTypeMethodOwners(lifetime)
        KotlinStubRuntimeMetadata.registerHolder(plugin, lifetime)
        val fileTypes = application.getService(FileTypeRegistry::class) as CoreFileTypeRegistry
        fileTypes.registerFileType(KotlinFileType.INSTANCE, "kt")
        fileTypes.registerFileType(KotlinFileType.INSTANCE, KotlinParserDefinition.STD_SCRIPT_SUFFIX)
        KotlinPsiContextClassifier.install()
    }
}
