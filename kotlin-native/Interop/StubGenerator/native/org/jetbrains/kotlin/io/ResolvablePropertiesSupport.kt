/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.io

import org.jetbrains.kotlin.konan.properties.Properties

fun String.suffix(suffix: String?): String =
    if (suffix == null) this else "$this.$suffix"

private fun Properties.propertyString(key: String, suffix: String? = null): String? =
    getProperty(key.suffix(suffix)) ?: getProperty(key)

fun Properties.resolvablePropertyList(
    key: String,
    suffix: String? = null,
    escapeInQuotes: Boolean = false,
    visitedProperties: MutableSet<String> = mutableSetOf(),
): List<String> =
    propertyList(key, suffix, escapeInQuotes).flatMap {
        resolveValue(it, this, visitedProperties.toMutableSet())
    }

fun Properties.resolvablePropertyString(
    key: String,
    suffix: String? = null,
    visitedProperties: MutableSet<String> = mutableSetOf(),
): String? {
    val rawValue = propertyString(key, suffix) ?: findValueByResolvableKey(key.suffix(suffix))
    return rawValue?.split(' ')?.flatMap { resolveValue(it, this, visitedProperties) }?.joinToString(" ")
}

private fun Properties.findValueByResolvableKey(resolvableKey: String): String? {
    val matchingKey = propertyNames()
        .asSequence()
        .filter { it.contains("$") }
        .firstOrNull { resolveValue(it, this).singleOrNull() == resolvableKey }
    return matchingKey?.let { getProperty(it) }
}

private fun resolveValue(
    value: String,
    properties: Properties,
    visitedProperties: MutableSet<String> = mutableSetOf(),
): List<String> {
    if (!value.contains("$")) return listOf(value)

    val index = value.indexOf('$')
    val prefix = value.substring(0, index)
    val afterDollar = value.substring(index + 1)
    val (property, suffix) = if (afterDollar.startsWith('{')) {
        val closeBraceIndex = afterDollar.indexOf('}')
        require(closeBraceIndex != -1) { "Unclosed '{' in: $value" }
        Pair(
            afterDollar.substring(1, closeBraceIndex),
            afterDollar.substring(closeBraceIndex + 1),
        )
    } else {
        Pair(
            afterDollar.substringBefore('/'),
            if (afterDollar.contains('/')) "/${afterDollar.substringAfter('/')}" else "",
        )
    }

    val pathSuffix = if (suffix.startsWith('/')) suffix.drop(1) else ""
    val otherSuffix = if (!suffix.startsWith('/')) suffix else ""

    check(visitedProperties.add(property)) {
        "Circular dependency: ${visitedProperties.joinToString()}"
    }

    val values = properties.resolvablePropertyList(property, visitedProperties = visitedProperties)
    return if (values.size > 1) {
        check(pathSuffix.isEmpty()) {
            "Cannot append `/$pathSuffix` to multiple values: ${values.joinToString()}"
        }
        check(prefix.isEmpty()) {
            "Cannot add prefix `$prefix` to multiple values: ${values.joinToString()}"
        }
        values
    } else {
        values.map { item ->
            if (pathSuffix.isNotEmpty()) {
                "$prefix${item.trimEnd('/')}/$pathSuffix$otherSuffix"
            } else {
                "$prefix$item$otherSuffix"
            }
        }
    }
}
