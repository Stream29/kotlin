package nativekotlin.host.text

private val asciiCaseProperties = IntArray(128) { cp -> rangeValue(jdkCaseProperties, cp) }

/** English case mapping using pinned JDK character and word-boundary data. */
fun jdkLowercase(value: String): String {
    val len = value.length
    var firstChange = -1
    var hasNonAscii = false
    for (i in 0 until len) {
        val code = value[i].code
        if (code >= 0x80) {
            hasNonAscii = true
            break
        }
        if (code in 'A'.code..'Z'.code && firstChange == -1) {
            firstChange = i
        }
    }
    if (!hasNonAscii) {
        if (firstChange == -1) return value
        val chars = CharArray(len)
        for (i in 0 until firstChange) chars[i] = value[i]
        for (i in firstChange until len) {
            val c = value[i].code
            chars[i] = (if (c in 'A'.code..'Z'.code) c + 32 else c).toChar()
        }
        return chars.concatToString()
    }
    val boundaries = if ('\u03a3' in value) wordBoundaries(value) else null
    return buildString(len) {
        var index = 0
        while (index < len) {
            val start = index
            val cp = codePointAt(value, index)
            index += width(cp)
            if (cp < 0x80) {
                append((if (cp in 'A'.code..'Z'.code) cp + 32 else cp).toChar())
                continue
            }
            if (cp == 0x130) { append("i\u0307"); continue }
            if (cp == 0x3a3) {
                append(if (isFinalCased(value, start, boundaries!!)) '\u03c2' else '\u03c3')
                continue
            }
            var lo = 0
            var hi = jdkLowercaseMappings.size / 2 - 1
            var mapped = cp
            while (lo <= hi) {
                val mid = (lo + hi) ushr 1
                val key = jdkLowercaseMappings[mid * 2]
                when {
                    key < cp -> lo = mid + 1
                    key > cp -> hi = mid - 1
                    else -> { mapped = jdkLowercaseMappings[mid * 2 + 1]; break }
                }
            }
            if (mapped < 0x10000) append(mapped.toChar())
            else {
                val shifted = mapped - 0x10000
                append((0xd800 + (shifted ushr 10)).toChar())
                append((0xdc00 + (shifted and 0x3ff)).toChar())
            }
        }
    }
}

fun jdkIsLowercase(value: Char): Boolean {
    val code = value.code
    val props = if (code < 128) asciiCaseProperties[code] else rangeValue(jdkCaseProperties, code)
    return props and 1 != 0
}

fun jdkIsUppercase(value: Char): Boolean {
    val code = value.code
    val props = if (code < 128) asciiCaseProperties[code] else rangeValue(jdkCaseProperties, code)
    return props and 2 != 0
}

fun jdkCharacterType(codePoint: Int): Int = characterProperties(codePoint) ushr 2 and 31
fun jdkJavaIdentifierStart(codePoint: Int): Boolean = characterProperties(codePoint) and 256 != 0
fun jdkJavaIdentifierPart(codePoint: Int): Boolean = characterProperties(codePoint) and 512 != 0

private fun characterProperties(codePoint: Int): Int {
    if (codePoint in 0 until 128) return asciiCaseProperties[codePoint]
    return if (codePoint in 128..0x10ffff) rangeValue(jdkCaseProperties, codePoint) else 0
}

fun jdkLowercaseChar(value: Char): Char {
    val code = value.code
    if (code < 128) {
        return if (code in 'A'.code..'Z'.code) (code + 32).toChar() else value
    }
    var lo = 0
    var hi = jdkLowercaseMappings.size / 2 - 1
    while (lo <= hi) {
        val mid = (lo + hi) ushr 1
        when {
            jdkLowercaseMappings[mid * 2] < code -> lo = mid + 1
            jdkLowercaseMappings[mid * 2] > code -> hi = mid - 1
            else -> return jdkLowercaseMappings[mid * 2 + 1].toChar()
        }
    }
    return value
}

fun jdkUppercase(value: String): String {
    val len = value.length
    var firstChange = -1
    var hasNonAscii = false
    for (i in 0 until len) {
        val code = value[i].code
        if (code >= 0x80) {
            hasNonAscii = true
            break
        }
        if (code in 'a'.code..'z'.code && firstChange == -1) {
            firstChange = i
        }
    }
    if (!hasNonAscii) {
        if (firstChange == -1) return value
        val chars = CharArray(len)
        for (i in 0 until firstChange) chars[i] = value[i]
        for (i in firstChange until len) {
            val c = value[i].code
            chars[i] = (if (c in 'a'.code..'z'.code) c - 32 else c).toChar()
        }
        return chars.concatToString()
    }
    return buildString(len) {
        var index = 0
        while (index < len) {
            val cp = codePointAt(value, index)
            val size = width(cp)
            val mapped = jdkUppercaseMappings[cp]
            if (mapped != null) append(mapped) else append(value, index, index + size)
            index += size
        }
    }
}

private fun width(cp: Int): Int = if (cp >= 0x10000) 2 else 1

private fun codePointAt(text: String, index: Int): Int {
    val first = text[index].code
    return if (first in 0xd800..0xdbff && index + 1 < text.length && text[index + 1].code in 0xdc00..0xdfff)
        0x10000 + ((first - 0xd800) shl 10) + text[index + 1].code - 0xdc00
    else first
}

private fun rangeValue(table: IntArray, cp: Int): Int {
    var lo = 0
    var hi = table.size / 2
    while (lo < hi) {
        val mid = (lo + hi) ushr 1
        if (table[mid * 2] <= cp) lo = mid + 1 else hi = mid
    }
    return table[(lo - 1) * 2 + 1]
}

private fun wordBoundaries(text: String): BooleanArray {
    val boundaries = BooleanArray(text.length + 1)
    boundaries[0] = true
    var start = 0
    while (start < text.length) {
        var state = 1
        var index = start
        var accepted = start + width(codePointAt(text, start))
        var lookahead = 0
        while (index < text.length && state != 0) {
            val cp = codePointAt(text, index)
            if (cp == 0xffff) break
            val next = index + width(cp)
            val category = rangeValue(jdkWordCategories, cp)
            if (category != -1) state = jdkWordStates[state * jdkWordCategoryCount + category]
            if (jdkWordLookahead[state] != 0) {
                if (jdkWordEnds[state] != 0) accepted = lookahead else lookahead = next
            } else if (jdkWordEnds[state] != 0) accepted = next
            index = next
        }
        if (index == text.length && lookahead == text.length) accepted = lookahead
        check(accepted > start && accepted <= text.length)
        boundaries[accepted] = true
        start = accepted
    }
    return boundaries
}

private fun isFinalCased(text: String, position: Int, boundaries: BooleanArray): Boolean {
    var wordStart = position
    while (wordStart > 0 && !boundaries[wordStart]) wordStart--
    var preceded = false
    var index = wordStart
    while (index < position) {
        val cp = codePointAt(text, index)
        if (rangeValue(jdkCasedCharacters, cp) == 1) preceded = true
        index += width(cp)
    }
    if (!preceded) return false
    index = position + 1
    while (index < text.length && !boundaries[index]) {
        val cp = codePointAt(text, index)
        if (rangeValue(jdkCasedCharacters, cp) == 1) return false
        index += width(cp)
    }
    return true
}
