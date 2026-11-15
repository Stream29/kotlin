/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.konan.library.impl

import org.jetbrains.kotlin.konan.library.components.KlibBitcodeComponent
import org.jetbrains.kotlin.konan.library.components.KlibBitcodeComponentLayout
import org.jetbrains.kotlin.konan.library.components.KlibNativeIncludedBinariesComponent
import org.jetbrains.kotlin.konan.library.components.KlibNativeIncludedBinariesComponentLayout
import org.jetbrains.kotlin.konan.target.KonanTarget
import org.jetbrains.kotlin.library.KlibLayoutReader
import org.jetbrains.kotlin.library.KlibPath as Path
import org.jetbrains.kotlin.library.writer.KlibComponentWriter

internal class KlibBitcodeComponentImpl(
    private val layoutReader: KlibLayoutReader<KlibBitcodeComponentLayout>
) : KlibBitcodeComponent {
    override val bitcodeFilePaths: List<Path> by lazy {
        layoutReader.readExtractingToTemp(KlibBitcodeComponentLayout::bitcodeDir)
            .listDirectoryEntries()
            .map(Path::absolute)
    }
}

internal class KlibBitcodeComponentWriterImpl(
    private val target: KonanTarget,
    private val bitcodeFilePaths: Collection<Path>,
) : KlibComponentWriter {
    override fun writeTo(root: Path) {
        val layout = KlibBitcodeComponentLayout(target, root)
        layout.bitcodeDir.createDirectories()

        for (filePath in bitcodeFilePaths) {
            filePath.copyTo(layout.bitcodeDir.resolve(filePath.name), overwrite = true)
        }
    }
}

internal class KlibNativeIncludedBinariesComponentImpl(
    private val layoutReader: KlibLayoutReader<KlibNativeIncludedBinariesComponentLayout>
) : KlibNativeIncludedBinariesComponent {
    override val nativeIncludedBinaryFilePaths: List<Path> by lazy {
        layoutReader.readExtractingToTemp(KlibNativeIncludedBinariesComponentLayout::nativeIncludedBinariesDir)
            .listDirectoryEntries()
            .map(Path::absolute)
    }
}

internal class KlibNativeIncludedBinariesComponentWriterImpl(
    private val target: KonanTarget,
    private val nativeIncludedBinaryFilePaths: Collection<Path>,
) : KlibComponentWriter {
    override fun writeTo(root: Path) {
        val layout = KlibNativeIncludedBinariesComponentLayout(target, root)
        layout.nativeIncludedBinariesDir.createDirectories()

        for (filePath in nativeIncludedBinaryFilePaths) {
            filePath.copyTo(layout.nativeIncludedBinariesDir.resolve(filePath.name), overwrite = true)
        }
    }
}
