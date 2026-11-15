@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package nativekotlin.host.bigint

import kotlinx.cinterop.*
import nativekotlin.host.bigint.c.*

class BigInteger private constructor(private val decimal: String, @Suppress("UNUSED_PARAMETER") canonical: Boolean) :
    Number(), Comparable<BigInteger> {
    constructor(value: String, radix: Int = 10) : this(normalize(value, radix), true)

    fun add(other: BigInteger) = binary(1, other)
    fun subtract(other: BigInteger) = binary(2, other)
    fun multiply(other: BigInteger) = binary(3, other)
    fun divide(other: BigInteger) = binary(4, other)
    fun remainder(other: BigInteger) = binary(5, other)
    operator fun rem(other: BigInteger) = remainder(other)
    fun and(other: BigInteger) = binary(6, other)
    fun or(other: BigInteger) = binary(7, other)
    fun xor(other: BigInteger) = binary(8, other)
    fun shiftLeft(distance: Int) = BigInteger(call(9, decimal, shift = distance), true)
    fun shiftRight(distance: Int) = BigInteger(call(10, decimal, shift = distance), true)
    fun negate() = BigInteger(call(13, decimal), true)
    fun abs() = if (signum() < 0) negate() else this
    fun signum() = if (decimal == "0") 0 else if (decimal[0] == '-') -1 else 1
    private fun binary(op: Int, other: BigInteger) = BigInteger(call(op, decimal, other.decimal), true)
    override fun compareTo(other: BigInteger) = call(11, decimal, other.decimal).toInt()
    override fun equals(other: Any?) = other is BigInteger && decimal == other.decimal
    override fun hashCode(): Int = memScoped {
        val status = alloc<IntVar>()
        val result = h2a_bigint_hash(decimal, status.ptr)
        if (status.value != 0) throw OutOfMemoryError("BigInteger hash allocation")
        result
    }
    override fun toString() = decimal
    fun toString(radix: Int): String = call(12, decimal, radix = if (radix in 2..36) radix else 10)
    override fun toLong(): Long = h2a_bigint_long(decimal)
    override fun toInt(): Int = toLong().toInt()
    override fun toShort(): Short = toLong().toShort()
    override fun toByte(): Byte = toLong().toByte()
    override fun toDouble(): Double = h2a_bigint_double(decimal)
    override fun toFloat(): Float = h2a_bigint_float(decimal)

    companion object {
        val ZERO = BigInteger("0", true)
        val ONE = BigInteger("1", true)
        val TEN = BigInteger("10", true)
        fun valueOf(value: Long) = BigInteger(value.toString(), true)
        fun parse(value: String, radix: Int = 10) = BigInteger(value, radix)

        private fun normalize(value: String, radix: Int): String {
            if (radix !in 2..36 || value.isEmpty()) throw NumberFormatException("Invalid integer/radix")
            val start = if (value[0] == '+' || value[0] == '-') 1 else 0
            if (value.length == start) throw NumberFormatException("Missing digits")
            val ascii = buildString {
                if (value[0] == '-') append('-')
                for (i in start until value.length) {
                    val digit = value[i].digitToIntOrNull(radix)
                        ?: throw NumberFormatException("Invalid digit at $i")
                    append("0123456789abcdefghijklmnopqrstuvwxyz"[digit])
                }
            }
            return call(0, ascii, radix = radix)
        }

        private fun call(op: Int, a: String, b: String? = null, shift: Int = 0, radix: Int = 10): String =
            memScoped {
                val status = alloc<IntVar>()
                val result = h2a_bigint(op, a, b, shift, radix, status.ptr)
                try {
                    when (status.value) {
                        0 -> result?.toKString() ?: error("GMP returned no result")
                        1 -> throw NumberFormatException("Invalid integer")
                        2 -> throw ArithmeticException("BigInteger divide by zero")
                        3 -> throw ArithmeticException("BigInteger would overflow supported range")
                        5 -> throw OutOfMemoryError("BigInteger allocation")
                        else -> error("Invalid BigInteger operation")
                    }
                } finally {
                    h2a_bigint_free(result)
                }
            }
    }
}
