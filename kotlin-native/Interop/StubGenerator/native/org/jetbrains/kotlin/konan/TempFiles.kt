/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.konan

import org.jetbrains.kotlin.konan.host.NativeFile
import org.jetbrains.kotlin.konan.host.NativeFiles
import org.jetbrains.kotlin.konan.host.isFile

class TempFiles(pathToTemporaryDir: String? = null) {
    fun dispose() {
        if (deleteOnExit) {
            dir.deleteRecursively()
        }
    }

    val deleteOnExit = pathToTemporaryDir == null || pathToTemporaryDir.isEmpty()

    private val dir: NativeFile by lazy {
        if (deleteOnExit) {
            NativeFiles.createTempDirectory("konan_temp").toFile().also { it.deleteOnExit() }
        } else {
            createDirForTemporaryFiles(pathToTemporaryDir!!)
        }
    }

    private fun createDirForTemporaryFiles(path: String): NativeFile {
        if (NativeFile(path).isFile) {
            throw IllegalArgumentException("Given file is not a directory: $path")
        }
        return NativeFile(path).apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    fun create(prefix: String, suffix: String = ""): NativeFile =
        NativeFile(dir, "$prefix$suffix")
}
