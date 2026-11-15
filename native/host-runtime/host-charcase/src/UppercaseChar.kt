package nativekotlin.host.text

fun jdkUppercaseChar(value: Char): Char {
    val code = value.code
    if (code < 128) {
        return if (code in 'a'.code..'z'.code) (code - 32).toChar() else value
    }
    var low = 0
    var high = jdkUppercaseCharMappings.size / 2 - 1
    while (low <= high) {
        val middle = (low + high) ushr 1
        val original = jdkUppercaseCharMappings[middle * 2]
        when {
            original < code -> low = middle + 1
            original > code -> high = middle - 1
            else -> return jdkUppercaseCharMappings[middle * 2 + 1].toChar()
        }
    }
    return value
}
