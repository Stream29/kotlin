/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.common.serialization

import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.ir.IrFileEntry
import org.jetbrains.kotlin.ir.util.NaiveSourceBasedFileEntryImpl

/**
 * The interface provide an API for interning [String], [Name] and [NaiveSourceBasedFileEntryImpl] values
 * to save memory by eliminating duplicates of instances of those classes
 */
class IrInterningService {
    // Native maps retain canonical instances with the original content-equality policy.
    private val strings by lazy { HashMap<String, String>() }
    private val names by lazy { HashMap<Name, Name>() }
    private val fileEntries by lazy { HashMap<FileEntryKey, IrFileEntry>() }

    private class FileEntryKey(val entry: IrFileEntry) {
            override fun hashCode(): Int {
                val o = entry
                if (o !is NaiveSourceBasedFileEntryImpl) return o.hashCode()

                var result = 1
                result = 31 * result + o.maxOffset
                result = 31 * result + o.firstRelevantLineIndex
                result = 31 * result + o.name.hashCode()
                result = 31 * result + o.lineStartOffsets.contentHashCode()
                return result
            }

            override fun equals(other: Any?): Boolean {
                if (other !is FileEntryKey) return false
                val a = entry
                val b = other.entry
                if (a === b) return true
                if (a == null || b == null) return false
                if (a !is NaiveSourceBasedFileEntryImpl || b !is NaiveSourceBasedFileEntryImpl) return a == b

                if (a.maxOffset != b.maxOffset) return false
                if (a.firstRelevantLineIndex != b.firstRelevantLineIndex) return false
                if (a.name != b.name) return false
                if (!a.lineStartOffsets.contentEquals(b.lineStartOffsets)) return false

                return true
            }
    }

    fun string(string: String): String {
        return strings.getOrPut(string) { string }
    }

    fun name(name: Name): Name {
        return names.getOrPut(name) { name }
    }

    fun fileEntry(file: IrFileEntry): IrFileEntry {
        return fileEntries.getOrPut(FileEntryKey(file)) { file }
    }

    /**
     * Clean up internal interner caches. After calling this function the interner will 'forget' all previously
     * seen instances and will start the deduplication from scratch.
     */
    fun reset() {
        strings.clear()
        names.clear()
        fileEntries.clear()
    }
}
