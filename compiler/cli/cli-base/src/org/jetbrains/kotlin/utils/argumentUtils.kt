/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.utils

import org.jetbrains.kotlin.cli.common.arguments.CommonToolArguments
import org.jetbrains.kotlin.cli.common.arguments.getArgumentsInfo

fun populateExplicitArguments(arguments: CommonToolArguments) {
    val argumentsInfo = getArgumentsInfo(arguments.javaClass)

    arguments.explicitArguments = buildMap {
        for (argumentField in argumentsInfo.cliArgNameToArguments.values) {
            val actualValue = argumentField.getter.invoke(arguments)
            val defaultValue = argumentsInfo.getDefaultValue(argumentField)

            val isDefaultValue = if (actualValue is Array<*>) {
                actualValue.contentEquals(defaultValue as Array<*>)
            } else {
                actualValue == defaultValue
            }

            if (!isDefaultValue) {
                this[argumentField] = listOf(actualValue)
            }
        }
    }
}
