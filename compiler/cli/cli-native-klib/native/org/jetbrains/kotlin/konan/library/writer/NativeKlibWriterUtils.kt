/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.konan.library.writer

import org.jetbrains.kotlin.io.propertyList
import org.jetbrains.kotlin.konan.library.impl.KlibBitcodeComponentWriterImpl
import org.jetbrains.kotlin.konan.library.impl.KlibNativeIncludedBinariesComponentWriterImpl
import org.jetbrains.kotlin.konan.target.KonanTarget
import org.jetbrains.kotlin.library.KLIB_PROPERTY_DEPENDS
import org.jetbrains.kotlin.library.KLIB_PROPERTY_SHORT_NAME
import org.jetbrains.kotlin.library.KlibPath as Path
import org.jetbrains.kotlin.library.writer.KlibManifestWriterSpec
import org.jetbrains.kotlin.library.writer.KlibWriterSpec

fun KlibManifestWriterSpec.legacyNativeDependenciesInManifest(uniqueNames: Collection<String>) {
    if (uniqueNames.isEmpty()) return

    customProperties {
        val existingDependencies: List<String> = propertyList(KLIB_PROPERTY_DEPENDS, escapeInQuotes = true)

        val newDependencies: List<String> = buildSet<String> {
            this += existingDependencies
            this += uniqueNames
        }.sorted()

        setProperty(KLIB_PROPERTY_DEPENDS, newDependencies.joinToString(" "))
    }
}

fun KlibManifestWriterSpec.legacyNativeShortNameInManifest(shortName: String?) {
    if (shortName == null) return

    customProperties {
        setProperty(KLIB_PROPERTY_SHORT_NAME, shortName)
    }
}

fun KlibWriterSpec.includeBitcode(
    target: KonanTarget,
    bitcodeFilePaths: Collection<Path>,
) {
    include(KlibBitcodeComponentWriterImpl(target, bitcodeFilePaths))
}

fun KlibWriterSpec.includeNativeIncludedBinaries(
    target: KonanTarget,
    nativeIncludedBinaryFilePaths: Collection<Path>,
) {
    include(KlibNativeIncludedBinariesComponentWriterImpl(target, nativeIncludedBinaryFilePaths))
}
