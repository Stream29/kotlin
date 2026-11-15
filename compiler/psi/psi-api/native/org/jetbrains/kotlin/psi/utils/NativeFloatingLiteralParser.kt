/*
 * Copyright (c) 1996, 2023, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package org.jetbrains.kotlin.psi.utils

import nativekotlin.host.bigint.BigInteger
import kotlin.math.max
import kotlin.math.min

/**
 * Selected original FloatingDecimal ASCII-to-binary algorithms, not a java.lang
 * facade. FDBigInteger value arithmetic uses the accepted immutable BigInteger
 * boundary; lexical parsing, fast paths, correction loops and hex rounding are
 * retained. The selected conversion passed original-JDK/JVM/Native consumers;
 * complete parser/frontend acceptance remains separate.
 */
internal object NativeFloatingLiteralParser {
    private const val EXP_SHIFT = 52
    private const val FRACT_HOB = 1L shl EXP_SHIFT
    private const val DOUBLE_BIAS = 1023
    private const val DOUBLE_SIGN = Long.MIN_VALUE
    private const val DOUBLE_FRACTION = (1L shl EXP_SHIFT) - 1
    private const val DOUBLE_EXPONENT = ((1L shl 11) - 1) shl EXP_SHIFT
    private const val SINGLE_EXP_SHIFT = 23
    private const val SINGLE_FRACT_HOB = 1 shl SINGLE_EXP_SHIFT
    private const val FLOAT_BIAS = 127
    private const val FLOAT_SIGN = Int.MIN_VALUE
    private const val FLOAT_FRACTION = (1 shl SINGLE_EXP_SHIFT) - 1
    private const val FLOAT_EXPONENT = ((1 shl 8) - 1) shl SINGLE_EXP_SHIFT
    private const val MAX_DECIMAL_DIGITS = 15
    private const val MAX_DECIMAL_EXPONENT = 308
    private const val MIN_DECIMAL_EXPONENT = -324
    private const val BIG_DECIMAL_EXPONENT = 324
    private const val MAX_NDIGITS = 1100
    private const val SINGLE_MAX_DECIMAL_DIGITS = 7
    private const val SINGLE_MAX_DECIMAL_EXPONENT = 38
    private const val SINGLE_MIN_DECIMAL_EXPONENT = -45
    private const val SINGLE_MAX_NDIGITS = 200
    private const val INT_DECIMAL_DIGITS = 9

    private val SMALL_10_POW = doubleArrayOf(
        1.0e0, 1.0e1, 1.0e2, 1.0e3, 1.0e4, 1.0e5,
        1.0e6, 1.0e7, 1.0e8, 1.0e9, 1.0e10,
        1.0e11, 1.0e12, 1.0e13, 1.0e14, 1.0e15,
        1.0e16, 1.0e17, 1.0e18, 1.0e19, 1.0e20, 1.0e21, 1.0e22,
    )
    private val SINGLE_SMALL_10_POW = floatArrayOf(
        1.0e0f, 1.0e1f, 1.0e2f, 1.0e3f, 1.0e4f, 1.0e5f,
        1.0e6f, 1.0e7f, 1.0e8f, 1.0e9f, 1.0e10f,
    )
    private val BIG_10_POW = doubleArrayOf(1e16, 1e32, 1e64, 1e128, 1e256)
    private val TINY_10_POW = doubleArrayOf(1e-16, 1e-32, 1e-64, 1e-128, 1e-256)

    private interface Converter {
        fun doubleValue(): Double
        fun floatValue(): Float
    }

    private class Prepared(private val double: Double, private val float: Float) : Converter {
        override fun doubleValue() = double
        override fun floatValue() = float
    }

    private val POSITIVE_INFINITY = Prepared(Double.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    private val NEGATIVE_INFINITY = Prepared(Double.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY)
    private val NOT_A_NUMBER = Prepared(Double.NaN, Float.NaN)
    private val POSITIVE_ZERO = Prepared(0.0, 0.0f)
    private val NEGATIVE_ZERO = Prepared(-0.0, -0.0f)

    fun parseDouble(text: String): Double = readJavaFormatString(text).doubleValue()
    fun parseFloat(text: String): Float = readJavaFormatString(text).floatValue()

    private fun pow5(power: Int): BigInteger {
        require(power >= 0)
        var remaining = power
        var result = BigInteger.ONE
        var factor = BigInteger.valueOf(5)
        while (remaining != 0) {
            if (remaining and 1 != 0) result = result.multiply(factor)
            remaining = remaining ushr 1
            if (remaining != 0) factor = factor.multiply(factor)
        }
        return result
    }

    private fun BigInteger.mulPow52(five: Int, two: Int): BigInteger =
        (if (five == 0) this else multiply(pow5(five))).shiftLeft(two)

    private fun valueOfMulPow52(value: Long, five: Int, two: Int): BigInteger =
        BigInteger.valueOf(value).mulPow52(five, two)

    private fun BigInteger.cmpPow52(five: Int, two: Int): Int =
        compareTo(pow5(five).shiftLeft(two))

    private class Decimal(
        private val isNegative: Boolean,
        private val decExponent: Int,
        private val digits: CharArray,
        private var nDigits: Int,
    ) : Converter {
        override fun doubleValue(): Double {
            val kDigits = min(nDigits, MAX_DECIMAL_DIGITS + 1)
            var iValue = digits[0] - '0'
            val iDigits = min(kDigits, INT_DECIMAL_DIGITS)
            for (i in 1 until iDigits) iValue = iValue * 10 + (digits[i] - '0')
            var lValue = iValue.toLong()
            for (i in iDigits until kDigits) lValue = lValue * 10L + (digits[i] - '0')
            var dValue = lValue.toDouble()
            var exp = decExponent - kDigits
            if (nDigits <= MAX_DECIMAL_DIGITS) {
                if (exp == 0 || dValue == 0.0) return if (isNegative) -dValue else dValue
                if (exp >= 0) {
                    if (exp <= SMALL_10_POW.lastIndex) {
                        val rValue = dValue * SMALL_10_POW[exp]
                        return if (isNegative) -rValue else rValue
                    }
                    val slop = MAX_DECIMAL_DIGITS - kDigits
                    if (exp <= SMALL_10_POW.lastIndex + slop) {
                        dValue *= SMALL_10_POW[slop]
                        val rValue = dValue * SMALL_10_POW[exp - slop]
                        return if (isNegative) -rValue else rValue
                    }
                } else if (exp >= -SMALL_10_POW.lastIndex) {
                    val rValue = dValue / SMALL_10_POW[-exp]
                    return if (isNegative) -rValue else rValue
                }
            }
            if (exp > 0) {
                if (decExponent > MAX_DECIMAL_EXPONENT + 1) {
                    return if (isNegative) Double.NEGATIVE_INFINITY else Double.POSITIVE_INFINITY
                }
                if (exp and 15 != 0) dValue *= SMALL_10_POW[exp and 15]
                exp = exp shr 4
                if (exp != 0) {
                    var j = 0
                    while (exp > 1) {
                        if (exp and 1 != 0) dValue *= BIG_10_POW[j]
                        j++
                        exp = exp shr 1
                    }
                    var t = dValue * BIG_10_POW[j]
                    if (t.isInfinite()) {
                        t = dValue / 2.0
                        t *= BIG_10_POW[j]
                        if (t.isInfinite()) {
                            return if (isNegative) Double.NEGATIVE_INFINITY else Double.POSITIVE_INFINITY
                        }
                        t = Double.MAX_VALUE
                    }
                    dValue = t
                }
            } else if (exp < 0) {
                exp = -exp
                if (decExponent < MIN_DECIMAL_EXPONENT - 1) return if (isNegative) -0.0 else 0.0
                if (exp and 15 != 0) dValue /= SMALL_10_POW[exp and 15]
                exp = exp shr 4
                if (exp != 0) {
                    var j = 0
                    while (exp > 1) {
                        if (exp and 1 != 0) dValue *= TINY_10_POW[j]
                        j++
                        exp = exp shr 1
                    }
                    var t = dValue * TINY_10_POW[j]
                    if (t == 0.0) {
                        t = dValue * 2.0
                        t *= TINY_10_POW[j]
                        if (t == 0.0) return if (isNegative) -0.0 else 0.0
                        t = Double.MIN_VALUE
                    }
                    dValue = t
                }
            }
            if (nDigits > MAX_NDIGITS) {
                nDigits = MAX_NDIGITS + 1
                digits[MAX_NDIGITS] = '1'
            }
            // FDBigInteger's prefix+remaining-digits constructor, represented by
            // the same exact decimal value through genuine immutable arithmetic.
            var bigD0 = BigInteger(digits.concatToString(0, nDigits))
            exp = decExponent - nDigits
            var ieeeBits = dValue.toRawBits()
            val b5 = max(0, -exp)
            val d5 = max(0, exp)
            bigD0 = bigD0.mulPow52(d5, 0)
            var bigD: BigInteger? = null
            var prevD2 = 0
            while (true) {
                var binexp = (ieeeBits ushr EXP_SHIFT).toInt()
                var bigBbits = ieeeBits and DOUBLE_FRACTION
                if (binexp > 0) {
                    bigBbits = bigBbits or FRACT_HOB
                } else {
                    assert(bigBbits != 0L)
                    val shift = bigBbits.countLeadingZeroBits() - (63 - EXP_SHIFT)
                    bigBbits = bigBbits shl shift
                    binexp = 1 - shift
                }
                binexp -= DOUBLE_BIAS
                val lowOrderZeros = bigBbits.countTrailingZeroBits()
                bigBbits = bigBbits ushr lowOrderZeros
                val bigIntExp = binexp - EXP_SHIFT + lowOrderZeros
                val bigIntNBits = EXP_SHIFT + 1 - lowOrderZeros
                var b2 = b5
                var d2 = d5
                if (bigIntExp >= 0) b2 += bigIntExp else d2 -= bigIntExp
                var ulp2 = b2
                val hulpbias = if (binexp <= -DOUBLE_BIAS) binexp + lowOrderZeros + DOUBLE_BIAS else 1 + lowOrderZeros
                b2 += hulpbias
                d2 += hulpbias
                val common2 = min(b2, min(d2, ulp2))
                b2 -= common2
                d2 -= common2
                ulp2 -= common2
                val bigB = valueOfMulPow52(bigBbits, b5, b2)
                if (bigD == null || prevD2 != d2) {
                    bigD = bigD0.shiftLeft(d2)
                    prevD2 = d2
                }
                val exactD = bigD!!
                var cmpResult = bigB.compareTo(exactD)
                val overvalue: Boolean
                var diff: BigInteger
                if (cmpResult > 0) {
                    overvalue = true
                    diff = bigB.subtract(exactD)
                    if (bigIntNBits == 1 && bigIntExp > -DOUBLE_BIAS + 1) {
                        ulp2--
                        if (ulp2 < 0) {
                            ulp2 = 0
                            diff = diff.shiftLeft(1)
                        }
                    }
                } else if (cmpResult < 0) {
                    overvalue = false
                    diff = exactD.subtract(bigB)
                } else break
                cmpResult = diff.cmpPow52(b5, ulp2)
                if (cmpResult < 0) break
                if (cmpResult == 0) {
                    if (ieeeBits and 1L != 0L) ieeeBits += if (overvalue) -1L else 1L
                    break
                }
                ieeeBits += if (overvalue) -1L else 1L
                if (ieeeBits == 0L || ieeeBits == DOUBLE_EXPONENT) break
            }
            if (isNegative) ieeeBits = ieeeBits or DOUBLE_SIGN
            return Double.fromBits(ieeeBits)
        }

        override fun floatValue(): Float {
            val kDigits = min(nDigits, SINGLE_MAX_DECIMAL_DIGITS + 1)
            var iValue = digits[0] - '0'
            for (i in 1 until kDigits) iValue = iValue * 10 + (digits[i] - '0')
            var fValue = iValue.toFloat()
            var exp = decExponent - kDigits
            if (nDigits <= SINGLE_MAX_DECIMAL_DIGITS) {
                if (exp == 0 || fValue == 0.0f) return if (isNegative) -fValue else fValue
                if (exp >= 0) {
                    if (exp <= SINGLE_SMALL_10_POW.lastIndex) {
                        fValue *= SINGLE_SMALL_10_POW[exp]
                        return if (isNegative) -fValue else fValue
                    }
                    val slop = SINGLE_MAX_DECIMAL_DIGITS - kDigits
                    if (exp <= SINGLE_SMALL_10_POW.lastIndex + slop) {
                        fValue *= SINGLE_SMALL_10_POW[slop]
                        fValue *= SINGLE_SMALL_10_POW[exp - slop]
                        return if (isNegative) -fValue else fValue
                    }
                } else if (exp >= -SINGLE_SMALL_10_POW.lastIndex) {
                    fValue /= SINGLE_SMALL_10_POW[-exp]
                    return if (isNegative) -fValue else fValue
                }
            } else if (decExponent >= nDigits && nDigits + decExponent <= MAX_DECIMAL_DIGITS) {
                var lValue = iValue.toLong()
                for (i in kDigits until nDigits) lValue = lValue * 10L + (digits[i] - '0')
                var dValue = lValue.toDouble()
                exp = decExponent - nDigits
                dValue *= SMALL_10_POW[exp]
                fValue = dValue.toFloat()
                return if (isNegative) -fValue else fValue
            }
            var dValue = fValue.toDouble()
            if (exp > 0) {
                if (decExponent > SINGLE_MAX_DECIMAL_EXPONENT + 1) {
                    return if (isNegative) Float.NEGATIVE_INFINITY else Float.POSITIVE_INFINITY
                }
                if (exp and 15 != 0) dValue *= SMALL_10_POW[exp and 15]
                exp = exp shr 4
                var j = 0
                while (exp > 0) {
                    if (exp and 1 != 0) dValue *= BIG_10_POW[j]
                    j++
                    exp = exp shr 1
                }
            } else if (exp < 0) {
                exp = -exp
                if (decExponent < SINGLE_MIN_DECIMAL_EXPONENT - 1) return if (isNegative) -0.0f else 0.0f
                if (exp and 15 != 0) dValue /= SMALL_10_POW[exp and 15]
                exp = exp shr 4
                var j = 0
                while (exp > 0) {
                    if (exp and 1 != 0) dValue *= TINY_10_POW[j]
                    j++
                    exp = exp shr 1
                }
            }
            fValue = dValue.toFloat().coerceIn(Float.MIN_VALUE, Float.MAX_VALUE)
            if (nDigits > SINGLE_MAX_NDIGITS) {
                nDigits = SINGLE_MAX_NDIGITS + 1
                digits[SINGLE_MAX_NDIGITS] = '1'
            }
            var bigD0 = BigInteger(digits.concatToString(0, nDigits))
            exp = decExponent - nDigits
            var ieeeBits = fValue.toRawBits()
            val b5 = max(0, -exp)
            val d5 = max(0, exp)
            bigD0 = bigD0.mulPow52(d5, 0)
            var bigD: BigInteger? = null
            var prevD2 = 0
            while (true) {
                var binexp = ieeeBits ushr SINGLE_EXP_SHIFT
                var bigBbits = ieeeBits and FLOAT_FRACTION
                if (binexp > 0) {
                    bigBbits = bigBbits or SINGLE_FRACT_HOB
                } else {
                    assert(bigBbits != 0)
                    val shift = bigBbits.countLeadingZeroBits() - (31 - SINGLE_EXP_SHIFT)
                    bigBbits = bigBbits shl shift
                    binexp = 1 - shift
                }
                binexp -= FLOAT_BIAS
                val lowOrderZeros = bigBbits.countTrailingZeroBits()
                bigBbits = bigBbits ushr lowOrderZeros
                val bigIntExp = binexp - SINGLE_EXP_SHIFT + lowOrderZeros
                val bigIntNBits = SINGLE_EXP_SHIFT + 1 - lowOrderZeros
                var b2 = b5
                var d2 = d5
                if (bigIntExp >= 0) b2 += bigIntExp else d2 -= bigIntExp
                var ulp2 = b2
                val hulpbias = if (binexp <= -FLOAT_BIAS) binexp + lowOrderZeros + FLOAT_BIAS else 1 + lowOrderZeros
                b2 += hulpbias
                d2 += hulpbias
                val common2 = min(b2, min(d2, ulp2))
                b2 -= common2
                d2 -= common2
                ulp2 -= common2
                val bigB = valueOfMulPow52(bigBbits.toLong(), b5, b2)
                if (bigD == null || prevD2 != d2) {
                    bigD = bigD0.shiftLeft(d2)
                    prevD2 = d2
                }
                val exactD = bigD!!
                var cmpResult = bigB.compareTo(exactD)
                val overvalue: Boolean
                var diff: BigInteger
                if (cmpResult > 0) {
                    overvalue = true
                    diff = bigB.subtract(exactD)
                    if (bigIntNBits == 1 && bigIntExp > -FLOAT_BIAS + 1) {
                        ulp2--
                        if (ulp2 < 0) {
                            ulp2 = 0
                            diff = diff.shiftLeft(1)
                        }
                    }
                } else if (cmpResult < 0) {
                    overvalue = false
                    diff = exactD.subtract(bigB)
                } else break
                cmpResult = diff.cmpPow52(b5, ulp2)
                if (cmpResult < 0) break
                if (cmpResult == 0) {
                    if (ieeeBits and 1 != 0) ieeeBits += if (overvalue) -1 else 1
                    break
                }
                ieeeBits += if (overvalue) -1 else 1
                if (ieeeBits == 0 || ieeeBits == FLOAT_EXPONENT) break
            }
            if (isNegative) ieeeBits = ieeeBits or FLOAT_SIGN
            return Float.fromBits(ieeeBits)
        }
    }

    private fun invalid(text: String): Nothing = throw NumberFormatException("For input string: \"$text\"")

    private fun readJavaFormatString(text: String): Converter {
        val s = text.trim { it <= ' ' }
        var isNegative = false
        var signSeen = false
        try {
            val len = s.length
            if (len == 0) throw NumberFormatException("empty String")
            var i = 0
            if (s[i] == '-' || s[i] == '+') {
                isNegative = s[i] == '-'
                i++
                signSeen = true
            }
            var c = s[i]
            if (c == 'N') return if (s.substring(i) == "NaN") NOT_A_NUMBER else invalid(s)
            if (c == 'I') {
                return if (s.substring(i) == "Infinity") {
                    if (isNegative) NEGATIVE_INFINITY else POSITIVE_INFINITY
                } else invalid(s)
            }
            if (c == '0' && len > i + 1 && (s[i + 1] == 'x' || s[i + 1] == 'X')) return parseHexString(s)
            val digits = CharArray(len)
            var decSeen = false
            var nDigits = 0
            var decPt = 0
            var nLeadZero = 0
            var nTrailZero = 0
            while (i < len) {
                c = s[i]
                if (c == '0') {
                    nLeadZero++
                } else if (c == '.') {
                    if (decSeen) throw NumberFormatException("multiple points")
                    decPt = i - if (signSeen) 1 else 0
                    decSeen = true
                } else break
                i++
            }
            while (i < len) {
                c = s[i]
                if (c in '1'..'9') {
                    digits[nDigits++] = c
                    nTrailZero = 0
                } else if (c == '0') {
                    digits[nDigits++] = c
                    nTrailZero++
                } else if (c == '.') {
                    if (decSeen) throw NumberFormatException("multiple points")
                    decPt = i - if (signSeen) 1 else 0
                    decSeen = true
                } else break
                i++
            }
            nDigits -= nTrailZero
            val isZero = nDigits == 0
            if (isZero && nLeadZero == 0) invalid(s)
            var decExp = if (decSeen) decPt - nLeadZero else nDigits + nTrailZero
            if (i < len && (s[i] == 'e' || s[i] == 'E')) {
                var expSign = 1
                var expVal = 0
                val reallyBig = Int.MAX_VALUE / 10
                var expOverflow = false
                i++
                if (s[i] == '-' || s[i] == '+') {
                    if (s[i] == '-') expSign = -1
                    i++
                }
                val expAt = i
                while (i < len) {
                    if (expVal >= reallyBig) expOverflow = true
                    c = s[i++]
                    if (c in '0'..'9') expVal = expVal * 10 + (c - '0') else {
                        i--
                        break
                    }
                }
                val expLimit = BIG_DECIMAL_EXPONENT + nDigits + nTrailZero
                if (expOverflow || expVal > expLimit) {
                    if (!expOverflow && expSign == 1 && decExp < 0 && expVal + decExp < expLimit) {
                        decExp += expVal
                    } else decExp = expSign * expLimit
                } else decExp += expSign * expVal
                if (i == expAt) invalid(s)
            }
            if (i < len && (i != len - 1 || s[i] !in "fFdD")) invalid(s)
            if (isZero) return if (isNegative) NEGATIVE_ZERO else POSITIVE_ZERO
            return Decimal(isNegative, decExp, digits, nDigits)
        } catch (_: IndexOutOfBoundsException) {
            invalid(s)
        }
    }

    private val HEX_PATTERN = Regex(
        "([-+])?0[xX]((([0-9a-fA-F]+)\\.?)|(([0-9a-fA-F]*)\\.([0-9a-fA-F]+)))[pP]([-+])?([0-9]+)[fFdD]?"
    )

    private fun getHexDigit(s: String, position: Int): Long = when (val c = s[position]) {
        in '0'..'9' -> (c - '0').toLong()
        in 'a'..'f' -> (c - 'a' + 10).toLong()
        in 'A'..'F' -> (c - 'A' + 10).toLong()
        else -> throw AssertionError("Unexpected failure of digit conversion of $c")
    }

    private fun parseHexString(s: String): Converter {
        val groups = (HEX_PATTERN.matchEntire(s) ?: invalid(s)).groups
        val isNegative = groups[1]?.value == "-"
        val group4 = groups[4]?.value
        var leftDigits = 0
        var rightDigits = 0
        var significandString: String
        if (group4 != null) {
            significandString = group4.trimStart('0')
            leftDigits = significandString.length
        } else {
            val group6 = groups[6]!!.value.trimStart('0')
            leftDigits = group6.length
            val group7 = groups[7]!!.value
            rightDigits = group7.length
            significandString = group6 + group7
        }
        significandString = significandString.trimStart('0')
        val signifLength = significandString.length
        val exponentAdjust = if (leftDigits >= 1) 4 * (leftDigits - 1) else -4 * (rightDigits - signifLength + 1)
        if (signifLength == 0) return if (isNegative) NEGATIVE_ZERO else POSITIVE_ZERO
        val group8 = groups[8]?.value
        val positiveExponent = group8 == null || group8 == "+"
        val unsignedRawExponent = try {
            groups[9]!!.value.toInt().toLong()
        } catch (_: NumberFormatException) {
            return if (isNegative) {
                if (positiveExponent) NEGATIVE_INFINITY else NEGATIVE_ZERO
            } else {
                if (positiveExponent) POSITIVE_INFINITY else POSITIVE_ZERO
            }
        }
        var exponent = (if (positiveExponent) 1L else -1L) * unsignedRawExponent + exponentAdjust
        var round = false
        var sticky = false
        var nextShift: Int
        var significand = 0L
        val leadingDigit = getHexDigit(significandString, 0)
        if (leadingDigit == 1L) {
            significand = significand or (leadingDigit shl 52)
            nextShift = 52 - 4
        } else if (leadingDigit <= 3L) {
            significand = significand or (leadingDigit shl 51)
            nextShift = 52 - 5
            exponent++
        } else if (leadingDigit <= 7L) {
            significand = significand or (leadingDigit shl 50)
            nextShift = 52 - 6
            exponent += 2
        } else if (leadingDigit <= 15L) {
            significand = significand or (leadingDigit shl 49)
            nextShift = 52 - 7
            exponent += 3
        } else throw AssertionError("Result from digit conversion too large!")
        var i = 1
        while (i < signifLength && nextShift >= 0) {
            significand = significand or (getHexDigit(significandString, i) shl nextShift)
            nextShift -= 4
            i++
        }
        if (i < signifLength) {
            var currentDigit = getHexDigit(significandString, i)
            when (nextShift) {
                -1 -> {
                    significand = significand or ((currentDigit and 0xeL) shr 1)
                    round = currentDigit and 1L != 0L
                }
                -2 -> {
                    significand = significand or ((currentDigit and 0xcL) shr 2)
                    round = currentDigit and 2L != 0L
                    sticky = currentDigit and 1L != 0L
                }
                -3 -> {
                    significand = significand or ((currentDigit and 8L) shr 3)
                    round = currentDigit and 4L != 0L
                    sticky = currentDigit and 3L != 0L
                }
                -4 -> {
                    round = currentDigit and 8L != 0L
                    sticky = currentDigit and 7L != 0L
                }
                else -> throw AssertionError("Unexpected shift distance remainder.")
            }
            i++
            while (i < signifLength && !sticky) {
                currentDigit = getHexDigit(significandString, i)
                sticky = sticky || currentDigit != 0L
                i++
            }
        }
        var floatBits = if (isNegative) FLOAT_SIGN else 0
        if (exponent >= -126L) {
            if (exponent > 127L) {
                floatBits = floatBits or FLOAT_EXPONENT
            } else {
                val threshShift = 53 - 24 - 1
                val floatSticky = significand and ((1L shl threshShift) - 1) != 0L || round || sticky
                var iValue = (significand ushr threshShift).toInt()
                if (iValue and 3 != 1 || floatSticky) iValue++
                floatBits = floatBits or (((exponent.toInt() + (FLOAT_BIAS - 1)) shl SINGLE_EXP_SHIFT) + (iValue shr 1))
            }
        } else if (exponent >= -150L) {
            val threshShift = ((53 - 2 - 149) - exponent).toInt()
            assert(threshShift >= 53 - 24 && threshShift < 53)
            val floatSticky = significand and ((1L shl threshShift) - 1) != 0L || round || sticky
            var iValue = (significand ushr threshShift).toInt()
            if (iValue and 3 != 1 || floatSticky) iValue++
            floatBits = floatBits or (iValue shr 1)
        }
        val fValue = Float.fromBits(floatBits)
        if (exponent > 1023L) return if (isNegative) NEGATIVE_INFINITY else POSITIVE_INFINITY
        if (exponent >= -1022L) {
            significand = (((exponent + DOUBLE_BIAS) shl EXP_SHIFT) and DOUBLE_EXPONENT) or
                (DOUBLE_FRACTION and significand)
        } else {
            if (exponent < -1075L) return if (isNegative) NEGATIVE_ZERO else POSITIVE_ZERO
            sticky = sticky || round
            val bitsDiscarded = 53 - (exponent.toInt() + 1074 + 1)
            assert(bitsDiscarded in 1..53)
            round = significand and (1L shl (bitsDiscarded - 1)) != 0L
            if (bitsDiscarded > 1) {
                val mask = (-1L shl (bitsDiscarded - 1)).inv()
                sticky = sticky || significand and mask != 0L
            }
            significand = significand shr bitsDiscarded
            significand = ((((-1022L - 1) + DOUBLE_BIAS) shl EXP_SHIFT) and DOUBLE_EXPONENT) or
                (DOUBLE_FRACTION and significand)
        }
        val leastZero = significand and 1L == 0L
        if ((leastZero && round && sticky) || (!leastZero && round)) significand++
        val value = Double.fromBits(if (isNegative) significand or DOUBLE_SIGN else significand)
        return Prepared(value, fValue)
    }
}
