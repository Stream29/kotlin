// TARGET_BACKEND: JVM
// WITH_STDLIB

@file:OptIn(kotlin.ExperimentalStdlibApi::class)

fun <@JvmSpecialize reified T> getClazz() = T::class.java

fun box(): String {
    if (getClazz<Int>().name != "java.lang.Integer") return "fail: Int"
    if (getClazz<String>().name != "java.lang.String") return "fail: String"
    if (getClazz<Array<Int>>().name != "[Ljava.lang.Integer;") return "fail: Array<Int>"
    if (getClazz<IntArray>().name != "[I") return "fail: IntArray"
    return "OK"
}
