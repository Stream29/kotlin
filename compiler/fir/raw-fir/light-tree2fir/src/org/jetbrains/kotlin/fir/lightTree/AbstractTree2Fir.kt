/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.lightTree

import com.intellij.platform.native.vfs.NativeVfsPath as Path
import nativekotlin.host.files.NativeFile as File
import org.jetbrains.kotlin.KtIoFileSourceFile
import org.jetbrains.kotlin.KtSourceFile
import org.jetbrains.kotlin.KtSourceFileLinesMapping
import org.jetbrains.kotlin.SourceFileReader
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirFile
import org.jetbrains.kotlin.readSourceFileWithMapping

abstract class AbstractTree2Fir {
    abstract val session: FirSession

    fun buildFirFile(path: Path): FirFile {
        return buildFirFile(path.toFile())
    }

    fun buildFirFile(file: File): FirFile {
        val sourceFile = KtIoFileSourceFile(file)
        val [code, linesMapping] = SourceFileReader(sourceFile.getContentsAsStream()).use {
            it.readSourceFileWithMapping()
        }
        return buildFirFile(code, sourceFile, linesMapping)
    }

    abstract fun buildFirFile(code: CharSequence, sourceFile: KtSourceFile, linesMapping: KtSourceFileLinesMapping): FirFile
}
