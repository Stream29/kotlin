// Copyright 2000-2025 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package fleet.com.intellij.multiplatform.util.text

internal fun getCharsPlatformSpecificNative(
    sequence: CharSequence,
    srcOffset: Int,
    dst: CharArray,
    dstOffset: Int,
    len: Int,
): Boolean {
    require(srcOffset >= 0 && dstOffset >= 0 && len >= 0)
    require(srcOffset <= sequence.length - len && dstOffset <= dst.size - len)
    for (i in 0 until len) dst[dstOffset + i] = sequence[srcOffset + i]
    return true
}

/** Only array-backed sequences expose a reusable buffer; Native String does not. */
internal fun fromSequenceWithoutCopyingPlatformSpecificNative(seq: CharSequence?): CharArray? =
    (seq as? CharSequenceBackedByArray)?.chars
