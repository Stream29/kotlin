/*
 * Derived from OpenJDK 21 StringUTF16.compareToCIImpl and its helpers.
 * Copyright (c) 2010, 2023, Oracle and/or its affiliates. All rights reserved.
 * GPL version 2 only, with the Classpath exception. Original file and notices:
 * porting/H2U/string-compare/originals/StringUTF16.java.
 */
package nativekotlin.host.text

private fun scalarString(cp: Int): String = if (cp < 0x10000) cp.toChar().toString() else {
    val value = cp - 0x10000
    charArrayOf((0xd800 + (value ushr 10)).toChar(), (0xdc00 + (value and 1023)).toChar()).concatToString()
}

private fun scalar(value: String): Int {
    if (value.length == 1) return value[0].code
    check(value.length == 2 && value[0].code in 0xd800..0xdbff && value[1].code in 0xdc00..0xdfff)
    return 0x10000 + ((value[0].code - 0xd800) shl 10) + value[1].code - 0xdc00
}

private fun simpleUpper(cp: Int): Int =
    if (cp < 0x10000) jdkUppercaseChar(cp.toChar()).code else scalar(jdkUppercase(scalarString(cp)))

private fun simpleLower(cp: Int): Int =
    if (cp < 0x10000) jdkLowercaseChar(cp.toChar()).code else scalar(jdkLowercase(scalarString(cp)))

private fun compareCodePoint(left: Int, right: Int): Int {
    var a = simpleUpper(left)
    var b = simpleUpper(right)
    if (a != b) {
        a = simpleLower(a)
        b = simpleLower(b)
        if (a != b) return a - b
    }
    return 0
}

private fun including(value: String, cp: Int, index: Int): Int {
    if (cp !in 0xd800..0xdfff) return cp
    if (cp in 0xdc00..0xdfff) {
        if (index > 0 && value[index - 1].code in 0xd800..0xdbff)
            return 0x10000 + ((value[index - 1].code - 0xd800) shl 10) + cp - 0xdc00
    } else if (index + 1 < value.length && value[index + 1].code in 0xdc00..0xdfff) {
        return -(0x10000 + ((cp - 0xd800) shl 10) + value[index + 1].code - 0xdc00)
    }
    return cp
}

fun jdkCompareToIgnoreCase(left: String, right: String): Int {
    if (left.all { it.code <= 255 } || right.all { it.code <= 255 }) {
        val limit = minOf(left.length, right.length)
        for (index in 0 until limit) {
            var a = left[index]
            var b = right[index]
            if (a != b) {
                a = jdkUppercaseChar(a)
                b = jdkUppercaseChar(b)
                if (a != b) {
                    a = jdkLowercaseChar(a)
                    b = jdkLowercaseChar(b)
                    if (a != b) return a.code - b.code
                }
            }
        }
        return left.length - right.length
    }
    var i = 0
    var j = 0
    while (i < left.length && j < right.length) {
        var a = left[i].code
        var b = right[j].code
        if (a != b && compareCodePoint(a, b) != 0) {
            a = including(left, a, i)
            if (a < 0) { i++; a = -a }
            b = including(right, b, j)
            if (b < 0) { j++; b = -b }
            val difference = compareCodePoint(a, b)
            if (difference != 0) return difference
        }
        i++
        j++
    }
    return left.length - right.length
}
