/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.native.interop.indexer

import org.jetbrains.kotlin.konan.host.NativeFile as File
import org.jetbrains.kotlin.konan.host.environment
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

open class IndexerTests {
    companion object {
        private val temporaryDirectories = mutableSetOf<File>()
    }

    @BeforeTest
    fun init() {
        check(temporaryDirectories.isEmpty()) { "Previous test did not dispose its temporary directories" }
    }

    @AfterTest
    fun dispose() {
        try {
            temporaryDirectories.forEach {
                check(!it.exists() || it.deleteRecursively()) { "Cannot remove test directory $it" }
            }
        } finally {
            temporaryDirectories.clear()
        }
    }

    class TempFiles(name: String) {
        private val tempRootDir = environment("TMPDIR") ?: "."

        val directory: File = File(File(tempRootDir), name).canonicalFile.also {
            check(it.mkdirs()) { "Cannot create test directory $it" }
            temporaryDirectories.add(it)
        }

        fun file(relativePath: String, contents: String): File = File(directory, relativePath).canonicalFile.apply {
            parentFile.mkdirs()
            writeText(contents)
        }
    }
}
