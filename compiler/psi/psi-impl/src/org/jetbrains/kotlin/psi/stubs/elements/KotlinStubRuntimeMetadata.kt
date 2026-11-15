/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin.psi.stubs.elements

import com.intellij.openapi.Disposable
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.platform.native.NativeSelectedMethod
import com.intellij.platform.native.NativeTypeRegistry
import com.intellij.psi.stubs.NativeStubFileMethodOwners
import kotlin.reflect.KClass
import org.jetbrains.kotlin.idea.KotlinFileType

/** Original-JVM measured Kotlin records; platform registry/lifetime remain host-owned. */
object KotlinStubRuntimeMetadata {
    const val holderBinaryName: String = "org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes"
    const val fileTypeMethodOwnerSourceSha256: String =
        "0f5cb3acf7321416af6de888adc3d7c9541666f2cd136b9543303efc6b9dbfb4"
    const val fileTypeMethodOwnerOracleSha256: String =
        "5ef9dca929cbf1e63d6357672fbe8eb1df01fc74439c5819f8781a8c89c8b502"

    private val kotlinFileTypeCharsetOwners: Map<NativeSelectedMethod, KClass<*>> = mapOf(
        NativeSelectedMethod.FILE_TYPE_CHAR_SEQUENCE to LanguageFileType::class,
        NativeSelectedMethod.FILE_TYPE_STRING to LanguageFileType::class,
    )

    private val fileTypeCharsetOwnerEntries: Map<KClass<*>, Map<NativeSelectedMethod, KClass<*>>> = mapOf(
        KotlinFileType::class to kotlinFileTypeCharsetOwners,
    )

    fun registerFileMethodOwners(plugin: PluginId, lifetime: Disposable) {
        NativeStubFileMethodOwners.register(plugin, fileExternalIdMethodOwners(), lifetime)
    }

    fun registerFileTypeMethodOwners(lifetime: Disposable? = null): Disposable =
        NativeTypeRegistry.registerDeclaringClasses(
            KotlinFileType::class,
            kotlinFileTypeCharsetOwners,
            fileTypeMethodOwnerSourceSha256,
            fileTypeMethodOwnerOracleSha256,
            lifetime,
        )

    fun registerHolder(plugin: PluginId, lifetime: Disposable) {
        KotlinStubHolderMetadata.register(plugin, lifetime)
    }

    fun fileExternalIdMethodOwners(): Map<KClass<*>, KClass<*>> =
        KotlinStubFileMethodOwners.entries.toMap()

    fun fileTypeCharsetMethodOwners(): Map<KClass<*>, Map<NativeSelectedMethod, KClass<*>>> =
        fileTypeCharsetOwnerEntries.mapValues { (_, owners) -> owners.toMap() }
}
