/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin.diagnostics.rendering

import org.jetbrains.kotlin.config.LanguageFeature

private data class FeatureAndValue(val feature: LanguageFeature, val value: String)

fun buildRuntimeFeatureToFlagMap(): Map<LanguageFeature, String> {
    data class ArgumentAndValue(val argument: String, val value: String)

    return NativeCompilerArgumentMetadata.fields
        .flatMap { field ->
            val name = field.argument ?: return@flatMap emptyList()
            field.enables.map { (feature, value) -> FeatureAndValue(feature, value) to name }
        }
        .groupBy(
            keySelector = { it.first.feature },
            valueTransform = { ArgumentAndValue(argument = it.second, value = it.first.value) },
        )
        .mapValues { (_, values) ->
            val argument = values.first().argument
            if (values.any { it.value.isNotEmpty() }) {
                buildString {
                    append(argument)
                    append('=')
                    if (values.size == 1) {
                        append(values.first().value)
                    } else {
                        values.joinTo(buffer = this, separator = "|", prefix = "{", postfix = "}") { it.value }
                    }
                }
            } else {
                argument
            }
        }
}
