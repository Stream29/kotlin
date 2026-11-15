@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package nativekotlin.host.normalization

import kotlinx.cinterop.*
import nativekotlin.host.normalization.c.nativekotlin_nfc15

fun normalizeNfc15(text: String): String = memScoped {
    val source = allocArray<UShortVar>(maxOf(1, text.length))
    for (index in text.indices) source[index] = text[index].code.toUShort()
    val status = alloc<IntVar>()
    val length = nativekotlin_nfc15(source, text.length, null, 0, status.ptr)
    check(status.value <= 0 || status.value == 15) {
        "NFC preflight failed: ICU status ${status.value}"
    }
    check(length >= 0) { "NFC returned negative length" }
    if (length == 0) return@memScoped ""
    val destination = allocArray<UShortVar>(length)
    val actual = nativekotlin_nfc15(source, text.length, destination, length, status.ptr)
    check(status.value <= 0 && actual == length) {
        "NFC conversion failed: ICU status ${status.value}, length $actual/$length"
    }
    CharArray(length) { destination[it].toInt().toChar() }.concatToString()
}
