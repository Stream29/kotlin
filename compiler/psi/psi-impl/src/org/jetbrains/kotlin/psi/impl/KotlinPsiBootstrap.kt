/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin.psi.impl

import com.intellij.core.NativeCompilerRuntime
import com.intellij.core.CoreFileTypeRegistry
import com.intellij.openapi.Disposable
import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.util.Disposer
import com.intellij.psi.stubs.STUB_DEFINITION_EP
import com.intellij.psi.stubs.STUB_REGISTRY_EP
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.parsing.KotlinParserDefinition
import org.jetbrains.kotlin.psi.stubs.KotlinLanguageStubDefinition
import org.jetbrains.kotlin.psi.stubs.KotlinStubRegistryExtension
import org.jetbrains.kotlin.psi.stubs.elements.KotlinStubRuntimeMetadata

/**
 * Kotlin's real parser/context registration on the actual Native host.
 * This is not the complete compiler DI graph or a substitute Project.
 */
object KotlinPsiBootstrap {
    fun install(plugin: PluginId): NativeCompilerRuntime = NativeCompilerRuntime.install(
        listOf(KotlinLanguage.INSTANCE to KotlinParserDefinition()),
    ) { application, _, lifetime ->
        val stubDef = KotlinLanguageStubDefinition()
        STUB_DEFINITION_EP.addExplicitExtension(KotlinLanguage.INSTANCE, stubDef)
        Disposer.register(lifetime, object : Disposable {
            override fun dispose() {
                STUB_DEFINITION_EP.removeExplicitExtension(KotlinLanguage.INSTANCE, stubDef)
                STUB_DEFINITION_EP.clearCache(KotlinLanguage.INSTANCE)
            }
        })
        STUB_REGISTRY_EP.point.registerExtension(KotlinStubRegistryExtension(), lifetime)
        KotlinStubRuntimeMetadata.registerFileMethodOwners(plugin, lifetime)
        KotlinStubRuntimeMetadata.registerFileTypeMethodOwners(lifetime)
        KotlinStubRuntimeMetadata.registerHolder(plugin, lifetime)
        val fileTypes = application.getService(FileTypeRegistry::class) as CoreFileTypeRegistry
        fileTypes.registerFileType(KotlinFileType.INSTANCE, "kt")
        fileTypes.registerFileType(KotlinFileType.INSTANCE, KotlinParserDefinition.STD_SCRIPT_SUFFIX)
        KotlinPsiContextClassifier.install()
    }
}
