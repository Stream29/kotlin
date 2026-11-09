/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.library.impl

import org.jetbrains.kotlin.library.KlibComponentLayout
import org.jetbrains.kotlin.library.KlibLayoutReader
import org.jetbrains.kotlin.library.KlibPath
import org.jetbrains.kotlin.library.createDirectories

internal inline fun <KCL : KlibComponentLayout> IrArrayReader(
    layoutReader: KlibLayoutReader<KCL>,
    crossinline getFile: KCL.() -> KlibPath,
): IrArrayReader = IrArrayReader {
    layoutReader.readInPlace { it.getFile().readBytes() }
}

internal inline fun <KCL : KlibComponentLayout> IrMultiArrayReader(
    layoutReader: KlibLayoutReader<KCL>,
    crossinline getFile: KCL.() -> KlibPath,
): IrMultiArrayReader = IrMultiArrayReader {
    layoutReader.readInPlace { it.getFile().readBytes() }
}

internal inline fun <KCL : KlibComponentLayout> DeclarationIdMultiTableReader(
    layoutReader: KlibLayoutReader<KCL>,
    crossinline getFile: KCL.() -> KlibPath,
): DeclarationIdMultiTableReader = DeclarationIdMultiTableReader {
    layoutReader.readInPlace { it.getFile().readBytes() }
}

fun IrDataWriter.writeIntoFile(path: KlibPath) {
    path.parent.createDirectories()
    path.writeBytes(writeIntoMemory())
}
