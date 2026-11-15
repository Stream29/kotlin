package nativekotlin.host.text.tools

import java.text.BreakIterator
import java.util.Locale

fun main() {
    check(System.getProperty("java.runtime.version") == "21.0.12.1+1-LTS")
    println("package nativekotlin.host.text")
    println("// Generated from JDK 21.0.12.1+1-LTS; see upstream-license/openjdk.")
    println("internal val jdkLowercaseMappings: IntArray = intArrayOf(")
    for (cp in 0..0x10ffff) {
        val lower = Character.toLowerCase(cp)
        if (lower != cp) println("    $cp, $lower,")
    }
    println(")")
    println("internal val jdkUppercaseMappings: Map<Int, String> = mapOf(")
    for (cp in 0..0x10ffff) {
        val original = String(Character.toChars(cp))
        val upper = original.uppercase(Locale.ROOT)
        if (upper != original) {
            val escaped = upper.map { "\\u" + it.code.toString(16).padStart(4, '0') }.joinToString("")
            println("    $cp to \"$escaped\",")
        }
    }
    println(")")
    val iterator = BreakIterator.getWordInstance(Locale.ENGLISH)
    check(iterator.javaClass.name == "sun.text.RuleBasedBreakIterator")
    val clazz = iterator.javaClass
    fun field(name: String): Any = clazz.getDeclaredField(name).apply { isAccessible = true }.get(iterator)
    fun array(name: String, values: List<Int>) {
        println("internal val $name: IntArray = intArrayOf(")
        for (chunk in values.chunked(16)) println("    ${chunk.joinToString()},")
        println(")")
    }
    array("jdkWordStates", (field("stateTable") as ShortArray).map { it.toInt() })
    array("jdkWordEnds", (field("endStates") as BooleanArray).map { if (it) 1 else 0 })
    array("jdkWordLookahead", (field("lookaheadStates") as BooleanArray).map { if (it) 1 else 0 })
    println("internal const val jdkWordCategoryCount: Int = ${field("numCategories")}")
    val category = clazz.getDeclaredMethod("lookupCategory", Int::class.javaPrimitiveType).apply { isAccessible = true }
    val cased = Class.forName("java.lang.ConditionalSpecialCasing")
        .getDeclaredMethod("isCased", Int::class.javaPrimitiveType).apply { isAccessible = true }
    fun ranges(name: String, lookup: (Int) -> Int) {
        val result = mutableListOf<Int>()
        var previous = Int.MIN_VALUE
        for (cp in 0..0x10ffff) {
            val current = lookup(cp)
            if (current != previous) { result.add(cp); result.add(current); previous = current }
        }
        array(name, result)
    }
    ranges("jdkWordCategories") { category.invoke(iterator, it) as Int }
    ranges("jdkCasedCharacters") { if (cased.invoke(null, it) as Boolean) 1 else 0 }
    ranges("jdkCaseProperties") {
        (if (Character.isLowerCase(it)) 1 else 0) or (if (Character.isUpperCase(it)) 2 else 0) or
            (Character.getType(it) shl 2) or (if (Character.isJavaIdentifierStart(it)) 256 else 0) or
            (if (Character.isJavaIdentifierPart(it)) 512 else 0)
    }
}
