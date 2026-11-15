/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.native.interop.indexer

import org.jetbrains.kotlin.konan.host.NativeFile as File

/**
 * `SwiftName` refinements extracted from Clang API notes (`*.apinotes`) files.
 *
 * The type hierarchy mirrors `clang/APINotes/Types.h` so that the semantics stay recognizable, but only the
 * `SwiftName` field is kept, for classes, protocols, and their methods and properties. Every other refinement
 * (nullability, availability, bridging, etc.) is intentionally dropped.
 *
 * The versioned `SwiftVersions:` section is also ignored on purpose. Those entries hold names for *older* Swift
 * language modes; the top-level `SwiftName` is the current one, which is what Clang selects for any modern Swift
 * version (see `APINotesReader::VersionedInfo`).
 *
 * @see load for the (modular-only) discovery rules.
 */
class ApiNotes private constructor(
        private val classes: Map<String, Context>,
        private val protocols: Map<String, Context>,
) {
    fun objCClass(name: String): Context? = classes[name]

    fun protocol(name: String): Context? = protocols[name]

    open class CommonEntityInfo(val swiftName: String?)

    class Method(swiftName: String?) : CommonEntityInfo(swiftName)

    class Property(swiftName: String?) : CommonEntityInfo(swiftName)

    /** An Objective-C class or protocol context, holding the notes for its members. */
    class Context internal constructor(
            swiftName: String?,
            private val instanceMethods: Map<String, Method>,
            private val classMethods: Map<String, Method>,
            private val instanceProperties: Map<String, Property>,
            private val classProperties: Map<String, Property>,
    ) : CommonEntityInfo(swiftName) {
        fun method(selector: String, isInstance: Boolean): Method? =
                (if (isInstance) instanceMethods else classMethods)[selector]

        fun property(name: String, isInstance: Boolean): Property? =
                (if (isInstance) instanceProperties else classProperties)[name]
    }

    companion object {
        /**
         * Discovers and parses the API notes applicable to [library], following Clang's discovery rules for
         * modular interop: for each imported module, an `<ModuleName>.apinotes` file is looked up next to the
         * module's headers (e.g. `Foo.framework/.../Headers/Foo.apinotes`).
         *
         * Header-based (non-modular) interop is not covered yet; such libraries produce null.
         */
        fun load(library: NativeLibrary): ApiNotes? {
            val headerFilter = library.headerFilter
            if (headerFilter !is NativeLibraryHeaderFilter.Predefined || headerFilter.modules.isEmpty()) {
                return null
            }

            // Clang looks for `<ModuleName>.apinotes` in the module's headers directory. For every module header we
            // already resolved, its parent directory is that headers directory (this also covers versioned frameworks,
            // whose headers live in `Foo.framework/Versions/X/Headers`).
            val headerDirs = headerFilter.headers.mapNotNullTo(linkedSetOf()) { File(it).parentFile }

            val files = linkedSetOf<File>()
            for (module in headerFilter.modules) {
                for (dir in headerDirs) {
                    val file = File(dir, "$module.apinotes")
                    if (file.isFile) files += file
                }
            }
            if (files.isEmpty()) return null

            val classes = mutableMapOf<String, Context>()
            val protocols = mutableMapOf<String, Context>()
            for (file in files) {
                val module = runCatching { parseApiNotesModule(file.readText()) }.getOrNull() ?: continue
                module.classes.forEach { classes.putContext(it) }
                module.protocols.forEach { protocols.putContext(it) }
            }

            return ApiNotes(classes, protocols)
        }

        private fun MutableMap<String, Context>.putContext(raw: ApiNotesContext) {
            val instanceMethods = mutableMapOf<String, Method>()
            val classMethods = mutableMapOf<String, Method>()
            for (method in raw.methods) {
                val swiftName = method.swiftName.blankToNull() ?: continue
                val target = if (method.methodKind == EntityKind.Instance) instanceMethods else classMethods
                target[method.selector] = Method(swiftName)
            }

            val instanceProperties = mutableMapOf<String, Property>()
            val classProperties = mutableMapOf<String, Property>()
            for (property in raw.properties) {
                val swiftName = property.swiftName.blankToNull() ?: continue
                // A property without a PropertyKind applies to both the instance and the class property (matching Clang).
                if (property.propertyKind != EntityKind.Class) instanceProperties[property.name] = Property(swiftName)
                if (property.propertyKind != EntityKind.Instance) classProperties[property.name] = Property(swiftName)
            }

            val swiftName = raw.swiftName.blankToNull()
            if (swiftName == null && instanceMethods.isEmpty() && classMethods.isEmpty() &&
                    instanceProperties.isEmpty() && classProperties.isEmpty()
            ) return
            this[raw.name] = Context(swiftName, instanceMethods, classMethods, instanceProperties, classProperties)
        }

        private fun String?.blankToNull(): String? = this?.takeIf { it.isNotBlank() }
    }
}

private enum class EntityKind { Class, Instance }

private data class ApiNotesModule(
        val classes: List<ApiNotesContext> = emptyList(),
        val protocols: List<ApiNotesContext> = emptyList(),
)

private data class ApiNotesContext(
        val name: String,
        val swiftName: String? = null,
        val methods: List<ApiNotesMethod> = emptyList(),
        val properties: List<ApiNotesProperty> = emptyList(),
)

private data class ApiNotesMethod(
        val selector: String,
        val methodKind: EntityKind,
        val swiftName: String? = null,
)

private data class ApiNotesProperty(
        val name: String,
        val propertyKind: EntityKind? = null,
        val swiftName: String? = null,
)

private sealed class YamlNode {
    data class MapNode(val entries: Map<String, YamlNode>) : YamlNode()
    data class ListNode(val items: List<YamlNode>) : YamlNode()
    data class ScalarNode(val value: String?) : YamlNode()
}

private class YamlLine(val indent: Int, val content: String)

private fun parseApiNotesModule(yamlText: String): ApiNotesModule {
    val lines = preprocessYamlLines(yamlText)
    if (lines.isEmpty()) return ApiNotesModule()
    val root = YamlParser(lines).parseNode(lines[0].indent) as? YamlNode.MapNode ?: return ApiNotesModule()
    return ApiNotesModule(
            classes = root.list("Classes").mapNotNull(::toApiNotesContext),
            protocols = root.list("Protocols").mapNotNull(::toApiNotesContext),
    )
}

private fun toApiNotesContext(node: YamlNode): ApiNotesContext? {
    val map = node as? YamlNode.MapNode ?: return null
    val name = map.scalar("Name") ?: return null
    val methods = map.list("Methods").mapNotNull { mNode ->
        val m = mNode as? YamlNode.MapNode ?: return@mapNotNull null
        val selector = m.scalar("Selector") ?: return@mapNotNull null
        val kindStr = m.scalar("MethodKind") ?: return@mapNotNull null
        val kind = parseEntityKind(kindStr) ?: return@mapNotNull null
        ApiNotesMethod(selector = selector, methodKind = kind, swiftName = m.scalar("SwiftName"))
    }
    val properties = map.list("Properties").mapNotNull { pNode ->
        val p = pNode as? YamlNode.MapNode ?: return@mapNotNull null
        val propName = p.scalar("Name") ?: return@mapNotNull null
        val kind = p.scalar("PropertyKind")?.let { parseEntityKind(it) ?: return@mapNotNull null }
        ApiNotesProperty(name = propName, propertyKind = kind, swiftName = p.scalar("SwiftName"))
    }
    return ApiNotesContext(
            name = name,
            swiftName = map.scalar("SwiftName"),
            methods = methods,
            properties = properties,
    )
}

private fun parseEntityKind(value: String): EntityKind? = when (value) {
    "Class" -> EntityKind.Class
    "Instance" -> EntityKind.Instance
    else -> null
}

private fun YamlNode.MapNode.scalar(key: String): String? =
        (entries[key] as? YamlNode.ScalarNode)?.value

private fun YamlNode.MapNode.list(key: String): List<YamlNode> =
        (entries[key] as? YamlNode.ListNode)?.items.orEmpty()

private fun preprocessYamlLines(text: String): List<YamlLine> {
    val result = mutableListOf<YamlLine>()
    for (rawLine in text.lineSequence()) {
        val stripped = stripYamlComment(rawLine).trimEnd()
        val trimmed = stripped.trimStart()
        if (trimmed.isEmpty()) continue
        if (trimmed == "---" || trimmed == "...") continue
        val indent = stripped.length - trimmed.length
        result += YamlLine(indent, trimmed)
    }
    return result
}

private fun stripYamlComment(line: String): String {
    var inSingle = false
    var inDouble = false
    var i = 0
    while (i < line.length) {
        val c = line[i]
        when {
            inSingle -> {
                if (c == '\'' && i + 1 < line.length && line[i + 1] == '\'') {
                    i += 2
                    continue
                } else if (c == '\'') {
                    inSingle = false
                }
            }
            inDouble -> {
                if (c == '\\' && i + 1 < line.length) {
                    i += 2
                    continue
                } else if (c == '"') {
                    inDouble = false
                }
            }
            else -> {
                when (c) {
                    '\'' -> inSingle = true
                    '"' -> inDouble = true
                    '#' -> if (i == 0 || line[i - 1].isWhitespace()) return line.substring(0, i)
                }
            }
        }
        i++
    }
    return line
}

private fun splitYamlKeyValue(content: String): Pair<String, String>? {
    var inSingle = false
    var inDouble = false
    var i = 0
    while (i < content.length) {
        val c = content[i]
        when {
            inSingle -> {
                if (c == '\'' && i + 1 < content.length && content[i + 1] == '\'') {
                    i += 2
                    continue
                } else if (c == '\'') {
                    inSingle = false
                }
            }
            inDouble -> {
                if (c == '\\' && i + 1 < content.length) {
                    i += 2
                    continue
                } else if (c == '"') {
                    inDouble = false
                }
            }
            else -> {
                if (c == '\'') inSingle = true
                else if (c == '"') inDouble = true
                else if (c == ':' && (i + 1 == content.length || content[i + 1].isWhitespace())) {
                    val key = unquoteYamlScalar(content.substring(0, i).trim()) ?: ""
                    val rest = content.substring(i + 1).trim()
                    return key to rest
                }
            }
        }
        i++
    }
    return null
}

private fun unquoteYamlScalar(raw: String): String? {
    if (raw.isEmpty() || raw == "null" || raw == "~") return null
    if (raw.length >= 2 && raw.startsWith('\'') && raw.endsWith('\'')) {
        return raw.substring(1, raw.length - 1).replace("''", "'")
    }
    if (raw.length >= 2 && raw.startsWith('"') && raw.endsWith('"')) {
        val sb = StringBuilder()
        var i = 1
        while (i < raw.length - 1) {
            val c = raw[i]
            if (c == '\\' && i + 1 < raw.length - 1) {
                i++
                when (val esc = raw[i]) {
                    'n' -> sb.append('\n')
                    'r' -> sb.append('\r')
                    't' -> sb.append('\t')
                    '"' -> sb.append('"')
                    '\\' -> sb.append('\\')
                    else -> sb.append(esc)
                }
            } else {
                sb.append(c)
            }
            i++
        }
        return sb.toString()
    }
    return raw
}

private class YamlParser(private val lines: List<YamlLine>) {
    private var index = 0

    fun parseNode(expectedIndent: Int): YamlNode {
        if (index >= lines.size) return YamlNode.ScalarNode(null)
        val line = lines[index]
        if (line.indent < expectedIndent) return YamlNode.ScalarNode(null)
        return if (line.content == "-" || line.content.startsWith("- ")) {
            parseList(line.indent)
        } else if (splitYamlKeyValue(line.content) != null) {
            parseMap(line.indent)
        } else {
            index++
            parseInlineValue(line.content)
        }
    }

    private fun parseList(listIndent: Int): YamlNode.ListNode {
        val items = mutableListOf<YamlNode>()
        while (index < lines.size) {
            val line = lines[index]
            if (line.indent < listIndent) break
            if (line.indent > listIndent) {
                // Unexpected over-indented line; skip safely
                index++
                continue
            }
            if (line.content != "-" && !line.content.startsWith("- ")) break
            val afterDash = if (line.content == "-") "" else line.content.substring(2).trimStart()
            if (afterDash.isEmpty()) {
                index++
                if (index < lines.size && lines[index].indent > listIndent) {
                    items += parseNode(lines[index].indent)
                } else {
                    items += YamlNode.ScalarNode(null)
                }
            } else {
                val kv = splitYamlKeyValue(afterDash)
                if (kv != null) {
                    val itemBaseIndent = line.indent + (line.content.length - afterDash.length)
                    val mapEntries = linkedMapOf<String, YamlNode>()
                    index++
                    mapEntries[kv.first] = resolveValueAfterColon(kv.second, itemBaseIndent)
                    if (index < lines.size && lines[index].indent >= itemBaseIndent) {
                        val restMap = parseMapEntries(lines[index].indent)
                        mapEntries.putAll(restMap)
                    }
                    items += YamlNode.MapNode(mapEntries)
                } else {
                    index++
                    items += parseInlineValue(afterDash)
                }
            }
        }
        return YamlNode.ListNode(items)
    }

    private fun parseMap(mapIndent: Int): YamlNode.MapNode =
            YamlNode.MapNode(parseMapEntries(mapIndent))

    private fun parseMapEntries(mapIndent: Int): Map<String, YamlNode> {
        val entries = linkedMapOf<String, YamlNode>()
        while (index < lines.size) {
            val line = lines[index]
            if (line.indent < mapIndent) break
            if (line.indent > mapIndent) {
                index++
                continue
            }
            if (line.content == "-" || line.content.startsWith("- ")) break
            val kv = splitYamlKeyValue(line.content) ?: run {
                index++
                continue
            }
            index++
            entries[kv.first] = resolveValueAfterColon(kv.second, mapIndent)
        }
        return entries
    }

    private fun resolveValueAfterColon(rest: String, keyIndent: Int): YamlNode {
        if (rest.isNotEmpty()) {
            return parseInlineValue(rest)
        }
        if (index < lines.size) {
            val next = lines[index]
            if (next.indent > keyIndent) {
                return parseNode(next.indent)
            }
            if (next.indent == keyIndent && (next.content == "-" || next.content.startsWith("- "))) {
                return parseList(next.indent)
            }
        }
        return YamlNode.ScalarNode(null)
    }

    private fun parseInlineValue(raw: String): YamlNode = when (raw) {
        "[]" -> YamlNode.ListNode(emptyList())
        "{}" -> YamlNode.MapNode(emptyMap())
        else -> YamlNode.ScalarNode(unquoteYamlScalar(raw))
    }
}
