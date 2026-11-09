/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.konan.library.components

import org.jetbrains.kotlin.konan.library.components.KlibBitcodeConstants.KLIB_BITCODE_FOLDER_NAME
import org.jetbrains.kotlin.konan.library.components.KlibNativeIncludedBinariesConstants.KLIB_NATIVE_INCLUDED_BINARIES_FOLDER_NAME
import org.jetbrains.kotlin.konan.library.impl.KlibBitcodeComponentImpl
import org.jetbrains.kotlin.konan.library.impl.KlibNativeIncludedBinariesComponentImpl
import org.jetbrains.kotlin.konan.target.KonanTarget
import org.jetbrains.kotlin.library.Klib
import org.jetbrains.kotlin.library.KlibComponent
import org.jetbrains.kotlin.library.KlibComponentLayout
import org.jetbrains.kotlin.library.KlibConstants.KLIB_DEFAULT_COMPONENT_NAME
import org.jetbrains.kotlin.library.KlibLayoutReader
import org.jetbrains.kotlin.library.KlibPath as Path
import org.jetbrains.kotlin.library.components.KlibNativeConstants.KLIB_TARGETS_FOLDER_NAME

interface KlibBitcodeComponent : KlibComponent {
    val bitcodeFilePaths: List<Path>

    data class Kind(val target: KonanTarget) : KlibComponent.Kind<KlibBitcodeComponent, KlibBitcodeComponentLayout> {
        override fun createLayout(root: Path) = KlibBitcodeComponentLayout(target, root)

        override fun createComponentIfDataInKlibIsAvailable(layoutReader: KlibLayoutReader<KlibBitcodeComponentLayout>): KlibBitcodeComponent? =
            if (layoutReader.readInPlaceOrFallback(false) { it.bitcodeDir.exists() }) KlibBitcodeComponentImpl(layoutReader) else null
    }
}

fun Klib.bitcode(target: KonanTarget): KlibBitcodeComponent? =
    getComponent(KlibBitcodeComponent.Kind(target))

class KlibBitcodeComponentLayout(val target: KonanTarget, root: Path) : KlibComponentLayout(root) {
    val bitcodeDir: Path
        get() = root.resolve(KLIB_DEFAULT_COMPONENT_NAME)
            .resolve(KLIB_TARGETS_FOLDER_NAME)
            .resolve(target.visibleName)
            .resolve(KLIB_BITCODE_FOLDER_NAME)
}

object KlibBitcodeConstants {
    const val KLIB_BITCODE_FOLDER_NAME = "native"
}

interface KlibNativeIncludedBinariesComponent : KlibComponent {
    val nativeIncludedBinaryFilePaths: List<Path>

    data class Kind(val target: KonanTarget) : KlibComponent.Kind<KlibNativeIncludedBinariesComponent, KlibNativeIncludedBinariesComponentLayout> {
        override fun createLayout(root: Path) = KlibNativeIncludedBinariesComponentLayout(target, root)

        override fun createComponentIfDataInKlibIsAvailable(layoutReader: KlibLayoutReader<KlibNativeIncludedBinariesComponentLayout>): KlibNativeIncludedBinariesComponent? =
            if (layoutReader.readInPlaceOrFallback(false) { it.nativeIncludedBinariesDir.exists() }) KlibNativeIncludedBinariesComponentImpl(layoutReader) else null
    }
}

fun Klib.nativeIncludedBinaries(target: KonanTarget): KlibNativeIncludedBinariesComponent? =
    getComponent(KlibNativeIncludedBinariesComponent.Kind(target))

class KlibNativeIncludedBinariesComponentLayout(val target: KonanTarget, root: Path) : KlibComponentLayout(root) {
    val nativeIncludedBinariesDir: Path
        get() = root.resolve(KLIB_DEFAULT_COMPONENT_NAME)
            .resolve(KLIB_TARGETS_FOLDER_NAME)
            .resolve(target.visibleName)
            .resolve(KLIB_NATIVE_INCLUDED_BINARIES_FOLDER_NAME)
}

object KlibNativeIncludedBinariesConstants {
    const val KLIB_NATIVE_INCLUDED_BINARIES_FOLDER_NAME = "included"
}
