/*
 * Copyright 2010-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.compiler.plugin

import org.jetbrains.kotlin.utils.addToStdlib.runIf

interface AbstractCliOption {
    val optionName: String
    val valueDescription: String
    val description: String
    val required: Boolean
    val allowMultipleOccurrences: Boolean
}

class CliOption(
    override val optionName: String,
    override val valueDescription: String,
    override val description: String,
    override val required: Boolean = true,
    override val allowMultipleOccurrences: Boolean = false
) : AbstractCliOption

open class CliOptionProcessingException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class PluginCliOptionProcessingException(
    val pluginId: String,
    val options: Collection<AbstractCliOption>,
    message: String,
    cause: Throwable? = null
) : CliOptionProcessingException(message, cause)

class PluginProcessingException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

fun cliPluginUsageString(pluginId: String, options: Collection<AbstractCliOption>): String {
    val LEFT_INDENT = 2
    val MAX_OPTION_WIDTH = 26

    val renderedOptions = options.map {
        val name = "${it.optionName} ${it.valueDescription}"
        val margin = if (name.length > MAX_OPTION_WIDTH) {
            "\n" + " ".repeat(MAX_OPTION_WIDTH + LEFT_INDENT + 1)
        } else " ".repeat(1 + MAX_OPTION_WIDTH - name.length)

        val modifiers = listOfNotNull(
            runIf(it.required) { "required" },
            runIf(it.allowMultipleOccurrences) { "multiple" }
        )
        val modifiersEnclosed = if (modifiers.isEmpty()) "" else " (${modifiers.joinToString()})"

        " ".repeat(LEFT_INDENT) + name + margin + it.description + modifiersEnclosed
    }
    return "Plugin \"$pluginId\" usage:\n" + renderedOptions.joinToString("\n", postfix = "\n")
}

data class CliOptionValue(
    val pluginId: String,
    val optionName: String,
    val value: String
) {
    override fun toString() = "$pluginId:$optionName=$value"
}

fun parseLegacyPluginOption(argumentValue: String): CliOptionValue? {
    val match = Regex("""^plugin:([^:]*):([^=]*)=(.*)$""").matchEntire(argumentValue) ?: return null
    return CliOptionValue(match.groupValues[1], match.groupValues[2], match.groupValues[3])
}

fun parseModernPluginOption(argumentValue: String): CliOptionValue? {
    val match = Regex("""^([^=]*)=(.*)$""").matchEntire(argumentValue) ?: return null
    return CliOptionValue("<NO_ID>", match.groupValues[1], match.groupValues[2])
}

fun getPluginOptionString(pluginId: String, key: String, value: String): String {
    return "plugin:$pluginId:$key=$value"
}
