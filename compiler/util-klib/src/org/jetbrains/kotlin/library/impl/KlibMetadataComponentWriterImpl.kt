/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.library.impl

import org.jetbrains.kotlin.library.SerializedMetadata
import org.jetbrains.kotlin.library.components.KlibMetadataComponentLayout
import org.jetbrains.kotlin.library.writer.KlibComponentWriter
import org.jetbrains.kotlin.library.KlibPath as Path

/**
 * An implementation of [KlibComponentWriter] that writes [SerializedMetadata] to the constructed Klib library.
 */
internal class KlibMetadataComponentWriterImpl(
    private val metadata: SerializedMetadata,
) : KlibComponentWriter {
    override fun writeTo(root: Path) {
        val layout = KlibMetadataComponentLayout(root)
        layout.metadataDir.createDirectories()

        layout.moduleHeaderFile.writeBytes(metadata.module)

        metadata.fragmentNames.forEachIndexed { index, packageFqName ->
            val packageFragmentDir: Path = layout.getPackageFragmentsDir(packageFqName)
            packageFragmentDir.createDirectories()

            val shortPackageName: String = packageFqName.substringAfterLast(".")
            val packageFragmentParts: List<ByteArray> = metadata.fragments[index]

            val padding: Int = packageFragmentParts.size.toString().length
            fun withPadding(packageFragmentPartIndex: Int) = packageFragmentPartIndex.toString().padStart(padding, '0')

            packageFragmentParts.forEachIndexed { packageFragmentPartIndex, packageFragmentPart ->
                layout.getPackageFragmentFile(
                    packageFqName = packageFqName,
                    partName = "${withPadding(packageFragmentPartIndex)}_$shortPackageName"
                ).writeBytes(packageFragmentPart)
            }
        }
    }
}
