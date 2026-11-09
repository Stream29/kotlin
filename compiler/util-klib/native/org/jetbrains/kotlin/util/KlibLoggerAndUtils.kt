/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.util

interface Logger {
    fun log(message: String)
    fun error(message: String)
    fun warning(message: String)
    fun fatal(message: String): Nothing

    @Deprecated(
        "Use warning(String) instead",
        ReplaceWith("warning(message)"),
    )
    fun strongWarning(message: String) = warning(message)
}

interface WithLogger {
    val logger: Logger
}

object DummyLogger : Logger {
    override fun log(message: String) = Unit
    override fun error(message: String) = Unit
    override fun warning(message: String) = Unit
    override fun fatal(message: String): Nothing = throw IllegalStateException(message)
}

inline fun <T> profile(message: String, block: () -> T): T = block()

fun String.removeSuffixIfPresent(suffix: String): String =
    if (endsWith(suffix)) dropLast(suffix.length) else this
