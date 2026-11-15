/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package nativekotlin.generator

import java.io.File

/**
 * Reproducible protobuf code generator for Kotlin metadata, KLIB metadata, and Kotlin IR schemas.
 * Compiles under JVM with kotlin-native-compiler-embeddable.jar.
 */
object MetadataGenerator {

    data class EnumValue(val name: String, val number: Int)
    data class EnumDef(val name: String, val values: List<EnumValue>)

    data class FieldDef(
        val rule: String, // optional, required, repeated
        val type: String,
        val name: String,
        val tag: Int,
        val default: String? = null,
        val isPacked: Boolean = false,
        var bitIndex: Int = -1
    )

    data class OneofDef(val name: String, val fields: List<FieldDef>)

    data class MessageDef(
        val name: String,
        val fields: List<FieldDef>,
        val oneofs: List<OneofDef>,
        val nestedMessages: List<MessageDef>,
        val nestedEnums: List<EnumDef>,
        val extensionRanges: List<Pair<Int, Int>>,
        val isExtendable: Boolean
    )

    data class ExtensionDef(val target: String, val fields: List<FieldDef>)

    data class ProtoFile(
        val packageDirective: String,
        val javaOuterClassname: String,
        val javaMultipleFiles: Boolean,
        val enums: List<EnumDef>,
        val messages: List<MessageDef>,
        val extensions: List<ExtensionDef>
    )

    class Tokenizer(private val text: String) {
        private var pos = 0

        fun nextToken(): String? {
            while (pos < text.length && text[pos].isWhitespace()) pos++
            if (pos >= text.length) return null
            val c = text[pos]
            if (c in "{};=[](),<>") {
                pos++
                return c.toString()
            }
            if (c == '"' || c == '\'') {
                val quote = c
                pos++
                val start = pos
                while (pos < text.length && text[pos] != quote) {
                    if (text[pos] == '\\') pos++
                    pos++
                }
                val value = text.substring(start, pos)
                if (pos < text.length) pos++
                return "\"$value\""
            }
            val start = pos
            while (pos < text.length && !text[pos].isWhitespace() && text[pos] !in "{};=[](),<>") {
                pos++
            }
            return text.substring(start, pos)
        }
    }

    class Parser(private val tokens: List<String>) {
        private var pos = 0

        private fun peek(): String? = if (pos < tokens.size) tokens[pos] else null
        private fun consume(expected: String? = null): String {
            val t = peek() ?: error("Unexpected end of tokens, expected $expected")
            if (expected != null && t != expected) error("Expected $expected, got $t at $pos")
            pos++
            return t
        }

        fun parseFile(): ProtoFile {
            var pkg = ""
            var outerClass = ""
            var multipleFiles = false
            val enums = mutableListOf<EnumDef>()
            val messages = mutableListOf<MessageDef>()
            val extensions = mutableListOf<ExtensionDef>()

            while (peek() != null) {
                when (peek()) {
                    "syntax" -> {
                        consume("syntax"); consume("="); consume(); consume(";")
                    }
                    "package" -> {
                        consume("package"); pkg = consume(); consume(";")
                    }
                    "import" -> {
                        consume("import"); consume(); consume(";")
                    }
                    "option" -> {
                        consume("option")
                        var optName = consume()
                        if (optName == "(") {
                            val parts = mutableListOf<String>()
                            while (peek() != ")") parts.add(consume())
                            consume(")")
                            optName = parts.joinToString("")
                        }
                        consume("=")
                        val optVal = consume()
                        if (optName == "java_outer_classname") outerClass = optVal.trim('"')
                        if (optName == "java_multiple_files") multipleFiles = (optVal == "true")
                        consume(";")
                    }
                    "enum" -> enums.add(parseEnum())
                    "message" -> messages.add(parseMessage())
                    "extend" -> extensions.add(parseExtend())
                    else -> error("Unexpected top-level token: ${peek()}")
                }
            }
            return ProtoFile(pkg, outerClass, multipleFiles, enums, messages, extensions)
        }

        private fun parseEnum(): EnumDef {
            consume("enum")
            val name = consume()
            consume("{")
            val values = mutableListOf<EnumValue>()
            while (peek() != "}") {
                if (peek() == "option") {
                    while (consume() != ";") {}
                    continue
                }
                val vname = consume()
                if (peek() == ";") continue
                consume("=")
                val vnum = consume().toInt()
                if (peek() == "[") {
                    while (consume() != "]") {}
                }
                consume(";")
                values.add(EnumValue(vname, vnum))
            }
            consume("}")
            return EnumDef(name, values)
        }

        private fun parseExtend(): ExtensionDef {
            consume("extend")
            val target = consume()
            consume("{")
            val fields = mutableListOf<FieldDef>()
            while (peek() != "}") fields.add(parseField())
            consume("}")
            return ExtensionDef(target, fields)
        }

        private fun parseMessage(): MessageDef {
            consume("message")
            val name = consume()
            consume("{")
            val fields = mutableListOf<FieldDef>()
            val oneofs = mutableListOf<OneofDef>()
            val nestedMessages = mutableListOf<MessageDef>()
            val nestedEnums = mutableListOf<EnumDef>()
            val extensionRanges = mutableListOf<Pair<Int, Int>>()

            while (peek() != "}") {
                when (peek()) {
                    "option" -> while (consume() != ";") {}
                    "enum" -> nestedEnums.add(parseEnum())
                    "message" -> nestedMessages.add(parseMessage())
                    "extensions" -> {
                        consume("extensions")
                        val start = consume().toInt()
                        consume("to")
                        val endToken = consume()
                        val end = if (endToken == "max") 0x1FFFFFFF else endToken.toInt()
                        consume(";")
                        extensionRanges.add(Pair(start, end))
                    }
                    "oneof" -> oneofs.add(parseOneof())
                    "optional", "required", "repeated" -> fields.add(parseField())
                    else -> error("Unexpected token in message $name: ${peek()}")
                }
            }
            consume("}")
            return MessageDef(name, fields, oneofs, nestedMessages, nestedEnums, extensionRanges, extensionRanges.isNotEmpty())
        }

        private fun parseOneof(): OneofDef {
            consume("oneof")
            val name = consume()
            consume("{")
            val fields = mutableListOf<FieldDef>()
            while (peek() != "}") {
                val ftype = consume()
                val fname = consume()
                consume("=")
                val ftag = consume().toInt()
                if (peek() == "[") while (consume() != "]") {}
                consume(";")
                fields.add(FieldDef("optional", ftype, fname, ftag))
            }
            consume("}")
            return OneofDef(name, fields)
        }

        private fun parseField(): FieldDef {
            val rule = consume()
            val ftype = consume()
            val fname = consume()
            consume("=")
            val ftag = consume().toInt()
            var defaultVal: String? = null
            var packed = false

            if (peek() == "[") {
                consume("[")
                while (peek() != "]") {
                    var optName = consume()
                    if (optName == "(") {
                        val parts = mutableListOf<String>()
                        while (peek() != ")") parts.add(consume())
                        consume(")")
                        optName = parts.joinToString("")
                    }
                    consume("=")
                    val optVal = consume()
                    if (optName == "default") defaultVal = optVal.trim('"')
                    if (optName == "packed") packed = (optVal == "true")
                    if (peek() == ",") consume(",")
                }
                consume("]")
            }
            consume(";")
            return FieldDef(rule, ftype, fname, ftag, defaultVal, packed)
        }
    }

    fun parseProto(content: String): ProtoFile {
        // Strip block comments
        val noBlockComments = content.replace(Regex("/\\*.*?\\*/", RegexOption.DOT_MATCHES_ALL), "")
        // Strip line comments
        val clean = noBlockComments.lines().joinToString("\n") { line ->
            val idx = line.indexOf("//")
            if (idx >= 0) line.substring(0, idx) else line
        }
        val tokenizer = Tokenizer(clean)
        val tokens = mutableListOf<String>()
        while (true) {
            val t = tokenizer.nextToken() ?: break
            tokens.add(t)
        }
        return Parser(tokens).parseFile()
    }

    fun generateAll(protoDir: File, srcDir: File) {
        srcDir.mkdirs()
        val files = protoDir.listFiles()!!.filter { it.extension == "proto" }.sortedBy { it.name }
            .map { it to parseProto(it.readText()) }
        for ((source, _) in files) {
            for (match in Regex("import\\s+\"([^\"]+)\"").findAll(source.readText())) {
                val path = match.groupValues[1]
                val imported = if (path.startsWith("google/")) File(protoDir, path) else File(protoDir, path.substringAfterLast('/'))
                check(imported.isFile) { "Missing imported schema: $path" }
            }
        }
        val emitter = KotlinEmitter(files.map { it.second })
        for ((_, file) in files) {
            // Descriptor options guide upstream generators, but are not part of serialized KLIB messages.
            if (file.javaOuterClassname == "ExtOptionsProtoBuf") continue
            File(srcDir, file.javaOuterClassname + ".kt").writeText(emitter.emit(file))
        }
        File(srcDir.parentFile, "GeneratedSchemaCases.kt").writeText(emitter.emitTests(files.map { it.second }))
        File(srcDir.parentFile, "GeneratedIrVisitor.kt").writeText(
            emitter.emitIrVisitor(files.single { it.second.javaOuterClassname == "KotlinIr" }.second)
        )
    }
}

fun main(args: Array<String>) {
    val root = if (args.isNotEmpty()) File(args[0]) else File("ports/compiler-metadata")
    val protoDir = File(root, "proto")
    val srcDir = if (args.size > 1) File(args[1]) else File(root, "src")
    MetadataGenerator.generateAll(protoDir, srcDir)
}
