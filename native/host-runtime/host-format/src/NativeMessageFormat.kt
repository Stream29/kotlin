@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package nativekotlin.host.format

import kotlinx.cinterop.*
import nativekotlin.host.bigint.BigInteger
import nativekotlin.host.format.c.*

data class NativeDate(val epochMilliseconds: Double)

private val decimalDigits = ByteArray(65536) { it.toChar().digitToIntOrNull(10)?.toByte() ?: (-1).toByte() }

private fun MemScope.utf16(value: String): CPointer<UShortVar> {
    val data = allocArray<UShortVar>(maxOf(1, value.length))
    for (i in value.indices) data[i] = value[i].code.toUShort()
    if (value.isEmpty()) data[0] = 0u
    return data
}

private class HostArguments(val values: Array<out Any?>, val scope: MemScope) {
    var failure: Throwable? = null

    fun request(index: Int, operation: Int, arg: H2AFormatArg): Int {
        if (failure != null) return 0
        try {
            val value = values[index]
            arg.tag = 0
            arg.text = null
            arg.length = 0
            arg.integer = 0L
            arg.floating = 0.0
            fun text(s: String, tag: Int = 1) {
                arg.tag = tag
                arg.text = scope.utf16(s)
                arg.length = s.length
            }
            // Fixed MessageFormat.subformat, DecimalFormat/NumberFormat.format(Object)
            // and DateFormat.format(Object): dispatch before calling any user method.
            when {
                value == null -> Unit
                operation == H2A_REQUEST_DATE -> when (value) {
                    is NativeDate -> { arg.tag = 5; arg.floating = value.epochMilliseconds }
                    is Number -> { arg.tag = 2; arg.integer = value.toLong() }
                    else -> throw IllegalArgumentException("Cannot format given Object as a Date")
                }
                operation != H2A_REQUEST_DEFAULT && value !is Number ->
                    throw IllegalArgumentException("Cannot format given Object as a Number")
                value is Byte || value is Short || value is Int || value is Long -> {
                    arg.tag = 2
                    arg.integer = (value as Number).toLong()
                }
                value is BigInteger && operation != H2A_REQUEST_CHOICE -> text(value.toString(), 4)
                value is Number -> {
                    arg.tag = 3
                    arg.floating = value.toDouble()
                }
                value is NativeDate -> { arg.tag = 5; arg.floating = value.epochMilliseconds }
                value is String -> text(value)
                else -> text(value.toString())
            }
            return 1
        } catch (thrown: Throwable) {
            failure = thrown
            return 0
        }
    }
}

private val hostRequest = staticCFunction {
        context: COpaquePointer?, index: Int, operation: Int, output: CPointer<H2AFormatArg>? ->
    context!!.asStableRef<HostArguments>().get().request(index, operation, output!!.pointed)
}

class NativeMessageFormat(
    val pattern: String,
    val locale: String = DEFAULT_LOCALE,
    val timeZone: String = DEFAULT_TIME_ZONE,
) {
    init {
        invoke(null)
    }

    fun format(arguments: Array<out Any?>): String = invoke(arguments)

    private fun invoke(arguments: Array<out Any?>?): String = memScoped {
        val count = arguments?.size ?: -1
        val length = alloc<IntVar>()
        val status = alloc<IntVar>()
        val offset = alloc<IntVar>()
        val host = HostArguments(arguments ?: emptyArray(), this)
        val reference = StableRef.create(host)
        var result: CPointer<UShortVar>? = null
        try {
            result = decimalDigits.usePinned { digits ->
                h2a_format(utf16(pattern), pattern.length, locale, timeZone, hostRequest,
                    reference.asCPointer(), count, digits.addressOf(0), length.ptr, status.ptr, offset.ptr)
            }
            host.failure?.let { throw it }
            if (status.value != 0) {
                val message = "ICU MessageFormat error ${status.value} at UTF-16 offset ${offset.value}"
                when (h2a_format_error_kind(status.value, offset.value)) {
                    2 -> throw OutOfMemoryError(message)
                    3 -> error(message)
                    4 -> throw ArrayIndexOutOfBoundsException(message)
                    else -> throw IllegalArgumentException(message)
                }
            }
            if (arguments == null) "" else {
                val output = checkNotNull(result) { "ICU returned no result" }
                buildString(length.value) {
                    for (i in 0 until length.value) append(output[i].toInt().toChar())
                }
            }
        } finally {
            h2a_format_free(result)
            reference.dispose()
        }
    }

    companion object {
        const val DEFAULT_LOCALE = "en_US"
        const val DEFAULT_TIME_ZONE = "UTC"
    }
}
