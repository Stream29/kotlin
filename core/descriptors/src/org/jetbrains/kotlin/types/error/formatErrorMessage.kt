/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin.types.error

// The fixed ErrorEntity/ErrorScopeKind/ErrorTypeKind patterns use only Formatter's %s conversion.
// Scan the pattern once: percent sequences inside an argument are literal, not new placeholders.
internal fun formatErrorMessage(pattern: String, vararg arguments: Any?): String = buildString {
    var index = 0
    var argument = 0
    while (index < pattern.length) {
        val character = pattern[index++]
        if (character != '%') {
            append(character)
            continue
        }
        require(index < pattern.length) { "Incomplete format: $pattern" }
        when (val conversion = pattern[index++]) {
            '%' -> append('%')
            's' -> {
                require(argument < arguments.size) { "Missing format argument: %s" }
                append(arguments[argument++])
            }
            else -> throw IllegalArgumentException("Unsupported error-message conversion: %$conversion")
        }
    }
}
