package org.jetbrains.kotlin.util
fun String.suffixIfNot(suffix: String): String =
    if (this.endsWith(suffix)) this else "$this$suffix"
