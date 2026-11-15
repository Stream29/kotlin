package nativekotlin.generator

import nativekotlin.generator.MetadataGenerator.EnumDef
import nativekotlin.generator.MetadataGenerator.EnumValue
import nativekotlin.generator.MetadataGenerator.FieldDef
import nativekotlin.generator.MetadataGenerator.MessageDef
import nativekotlin.generator.MetadataGenerator.OneofDef
import nativekotlin.generator.MetadataGenerator.ProtoFile

class KotlinEmitter(files: List<ProtoFile>) {
    private data class Symbol(val kotlin: String, val enumeration: EnumDef?)
    private val symbols = mutableMapOf<String, Symbol>()
    private val messagesByKotlin = mutableMapOf<String, Pair<MessageDef, String>>()
    private val extendableMessages = mutableSetOf<String>()
    private val mayBeUninitialized = mutableSetOf<String>()

    private val scalar = mapOf(
        "int32" to "Int", "uint32" to "Int", "sint32" to "Int", "fixed32" to "Int", "sfixed32" to "Int",
        "int64" to "Long", "uint64" to "Long", "sint64" to "Long", "fixed64" to "Long", "sfixed64" to "Long",
        "bool" to "Boolean", "float" to "Float", "double" to "Double", "string" to "String", "bytes" to "ByteString"
    )

    init {
        fun addMessages(messages: List<MessageDef>, proto: String, kotlin: String) {
            for (m in messages) {
                val ktName = "$kotlin.${m.name}"
                symbols["$proto.${m.name}"] = Symbol(ktName, null)
                messagesByKotlin[ktName] = m to "$proto.${m.name}"
                if (m.isExtendable) extendableMessages.add(ktName)
                for (e in m.nestedEnums) {
                    symbols["$proto.${m.name}.${e.name}"] = Symbol("$ktName.${e.name}", e)
                }
                addMessages(m.nestedMessages, "$proto.${m.name}", ktName)
            }
        }
        for (file in files) {
            val owner = file.packageDirective + if (file.javaMultipleFiles) "" else ".${file.javaOuterClassname}"
            for (e in file.enums) symbols["${file.packageDirective}.${e.name}"] = Symbol("$owner.${e.name}", e)
            addMessages(file.messages, file.packageDirective, owner)
        }
        for (file in files) {
            if (file.javaOuterClassname == "ExtOptionsProtoBuf") continue
            for (ext in file.extensions) {
                extendableMessages.add(resolve(ext.target, file.packageDirective).kotlin)
            }
        }
        for ((ktName, pair) in messagesByKotlin) {
            val (m, _) = pair
            if (ktName in extendableMessages || m.fields.any { it.rule == "required" }) {
                mayBeUninitialized.add(ktName)
            }
        }
        var changed = true
        while (changed) {
            changed = false
            for ((ktName, pair) in messagesByKotlin) {
                if (ktName in mayBeUninitialized) continue
                val (m, scope) = pair
                val allFields = m.fields + m.oneofs.flatMap { it.fields }
                if (allFields.any { f -> message(f, scope) && type(f, scope) in mayBeUninitialized }) {
                    mayBeUninitialized.add(ktName)
                    changed = true
                }
            }
        }
    }

    private fun resolve(type: String, scope: String): Symbol {
        if (type.startsWith(".")) return symbols.getValue(type.drop(1))
        var current = scope
        while (true) {
            symbols["$current.$type"]?.let { return it }
            if ('.' !in current) break
            current = current.substringBeforeLast('.')
        }
        return symbols[type] ?: error("Unresolved proto type $type in $scope")
    }

    private fun camel(name: String): String = if (name == "class") "class_" else name.split('_').mapIndexed { i, p ->
        if (i == 0) p else p.replaceFirstChar { it.uppercaseChar() }
    }.joinToString("")
    private fun cap(name: String) = camel(name).replaceFirstChar { it.uppercaseChar() }
    private fun ident(name: String) = "`$name`"
    private fun type(f: FieldDef, scope: String) = scalar[f.type] ?: resolve(f.type, scope).kotlin
    private fun enumeration(f: FieldDef, scope: String) =
        if (f.type in scalar) null else resolve(f.type, scope).enumeration
    private fun message(f: FieldDef, scope: String) = f.type !in scalar && enumeration(f, scope) == null
    private fun wire(f: FieldDef, scope: String) =
        if (f.type in scalar) f.type.uppercase() else if (enumeration(f, scope) != null) "ENUM" else "MESSAGE"

    private fun wireTypeCode(f: FieldDef, scope: String): Int = when (f.type) {
        "int32", "uint32", "sint32", "int64", "uint64", "sint64", "bool" -> 0
        "fixed64", "sfixed64", "double" -> 1
        "string", "bytes" -> 2
        "fixed32", "sfixed32", "float" -> 5
        else -> if (enumeration(f, scope) != null) 0 else 2
    }

    private fun isPackableRepeated(f: FieldDef, scope: String): Boolean =
        f.rule == "repeated" && f.type != "string" && f.type != "bytes" && !message(f, scope)

    private fun varint32Bytes(value: Int): Int {
        if ((value and (0.inv() shl 7)) == 0) return 1
        if ((value and (0.inv() shl 14)) == 0) return 2
        if ((value and (0.inv() shl 21)) == 0) return 3
        if ((value and (0.inv() shl 28)) == 0) return 4
        return 5
    }

    private fun default(f: FieldDef, scope: String): String {
        val type = type(f, scope)
        val enum = enumeration(f, scope)
        return when {
            message(f, scope) -> "$type.defaultInstance"
            enum != null -> "$type.${f.default ?: enum.values.first().name}"
            type == "String" -> "\"${f.default ?: ""}\""
            type == "ByteString" -> "ByteString.EMPTY"
            type == "Boolean" -> f.default ?: "false"
            type == "Long" -> "${f.default ?: "0"}L"
            type == "Float" -> "${f.default ?: "0.0"}f"
            type == "Double" -> f.default ?: "0.0"
            else -> f.default ?: "0"
        }
    }

    private fun sample(f: FieldDef, scope: String): String =
        if (message(f, scope)) "${type(f, scope)}.getDefaultInstance()" else when (type(f, scope)) {
            "String" -> "\"A\\u0000中文\\ud83d\\ude03\""
            "ByteString" -> "ByteString.copyFrom(byteArrayOf(0, -1, 5))"
            "Int" -> "-1234567"
            "Long" -> "-123456789012345L"
            "Float" -> "-1.25f"
            "Double" -> "-1.25"
            "Boolean" -> "true"
            else -> "${type(f, scope)}.values().last()"
        }

    private fun initializedSample(f: FieldDef, scope: String, depth: Int = 0): String {
        require(depth < 32) { "Required-message cycle" }
        if (!message(f, scope)) return sample(f, scope)
        val type = type(f, scope)
        val (definition, owner) = messagesByKotlin.getValue(type)
        return "$type.newBuilder()" + definition.fields.filter { it.rule == "required" }.joinToString("") {
            ".set${cap(it.name)}(${initializedSample(it, owner, depth + 1)})"
        } + ".build()"
    }

    fun emit(file: ProtoFile): String = buildString {
        appendLine("// Generated from the fixed Kotlin protobuf schemas; do not edit.")
        appendLine("// Licensed under Apache-2.0; see ../upstream-license/LICENSE.txt.")
        appendLine("@file:Suppress(\"UNCHECKED_CAST\", \"UNUSED_PARAMETER\", \"RemoveRedundantQualifierName\", \"RedundantVisibilityModifier\")")
        appendLine("package ${file.packageDirective}")
        appendLine("import org.jetbrains.kotlin.protobuf.*")
        if (!file.javaMultipleFiles) appendLine("object ${file.javaOuterClassname} {")
        for (e in file.enums) emitEnum(e)
        for (m in file.messages) emitMessage(m, "${file.packageDirective}.${m.name}")
        if (file.javaMultipleFiles) appendLine("object ${file.javaOuterClassname} {")
        emitExtensions(file)
        appendLine("}")
    }

    fun emitTests(files: List<ProtoFile>): String = buildString {
        appendLine("package nativekotlin.schemas")
        appendLine("import org.jetbrains.kotlin.protobuf.*")
        val functions = mutableListOf<String>()
        fun visit(messages: List<MessageDef>, scope: String) {
            for (m in messages) {
                val full = "$scope.${m.name}"
                val t = symbols.getValue(full).kotlin
                val fn = "case${functions.size}"
                functions.add(fn)
                appendLine("private fun $fn() {")
                appendLine("val registry = ExtensionRegistryLite.getEmptyRegistry()")
                appendLine("val default = $t.getDefaultInstance()")
                appendLine("println(\"DEFAULT:$full:\" + default.toByteArray().contentHashCode() + \":\" + default.isInitialized())")
                for (f in m.fields + m.oneofs.flatMap { it.fields }) {
                    val c = cap(f.name)
                    val v = sample(f, full)
                    appendLine("run { val builder = $t.newBuilder()")
                    appendLine("builder.${if (f.rule == "repeated") "add" else "set"}$c($v)")
                    appendLine("val message = builder.buildPartial(); val bytes = message.toByteArray()")
                    appendLine("val decoded = $t.PARSER.parsePartialFrom(bytes, registry)")
                    appendLine("check(bytes.contentEquals(decoded.toByteArray()))")
                    val observe = if (f.rule == "repeated") "decoded.get${c}Count()" else "decoded.has$c()"
                    appendLine("println(\"FIELD:$full:${f.name}:\" + bytes.contentHashCode() + \":\" + message.isInitialized() + \":\" + $observe)")
                    appendLine("val clone = builder.clone(); builder.clear$c()")
                    appendLine("check(bytes.contentEquals(clone.buildPartial().toByteArray()))")
                    appendLine("check(builder.buildPartial().toByteArray().isEmpty())")
                    appendLine("}")
                }
                appendLine("}")
                visit(m.nestedMessages, full)
            }
        }
        for (file in files) visit(file.messages, file.packageDirective)
        for (file in files.filter { it.javaOuterClassname != "ExtOptionsProtoBuf" }) {
            for (extension in file.extensions) for (f in extension.fields) {
                val fn = "case${functions.size}"
                functions.add(fn)
                val t = resolve(extension.target, file.packageDirective).kotlin
                val owner = "${file.packageDirective}.${file.javaOuterClassname}"
                val ext = "$owner.${camel(f.name)}"
                val repeated = f.rule == "repeated"
                appendLine("private fun $fn() {")
                appendLine("val registry = ExtensionRegistryLite.newInstance(); $owner.registerAllExtensions(registry)")
                appendLine("val builder = $t.newBuilder()")
                appendLine("builder.${if (repeated) "add" else "set"}Extension($ext, ${initializedSample(f, file.packageDirective)})")
                appendLine("val bytes = builder.buildPartial().toByteArray()")
                appendLine("val decoded = $t.PARSER.parsePartialFrom(bytes, registry)")
                appendLine("check(bytes.contentEquals(decoded.toByteArray()))")
                val observe = if (repeated) "decoded.getExtensionCount($ext)" else "decoded.hasExtension($ext)"
                appendLine("println(\"EXTENSION:${file.javaOuterClassname}:${f.name}:\" + bytes.contentHashCode() + \":\" + $observe)")
                appendLine("}")
            }
        }
        appendLine("fun main() {")
        functions.forEach { appendLine("$it()") }
        appendLine("runAdditionalCases()")
        appendLine("println(\"SCHEMA_CASE_GROUPS_OK count=${functions.size}\")")
        appendLine("}")
    }

    fun emitIrVisitor(file: ProtoFile): String = buildString {
        appendLine("package nativekotlin.irtest")
        appendLine("import org.jetbrains.kotlin.protobuf.MessageLite")
        appendLine("fun visitIrMessage(message: MessageLite, body: (Int, Boolean) -> Unit) {")
        appendLine("when (message) {")
        fun visit(messages: List<MessageDef>, parent: String) {
            for (m in messages) {
                val scope = "$parent.${m.name}"
                appendLine("is ${symbols.getValue(scope).kotlin} -> {")
                for (f in m.fields + m.oneofs.flatMap { it.fields }) {
                    val c = cap(f.name)
                    if (message(f, scope)) {
                        if (f.rule == "repeated") appendLine("message.get${c}List().forEach { visitIrMessage(it, body) }")
                        else appendLine("if (message.has$c()) visitIrMessage(message.get$c(), body)")
                    }
                    val bodyKind = when ("${m.name}.${f.name}") {
                        "IrFunctionBase.body", "IrAnonymousInit.body" -> false
                        "IrField.initializer", "IrEnumEntry.initializer", "IrValueParameter.default_value" -> true
                        else -> null
                    }
                    if (bodyKind != null) appendLine("if (message.has$c()) body(message.get$c(), $bodyKind)")
                }
                appendLine("}")
                visit(m.nestedMessages, scope)
            }
        }
        visit(file.messages, file.packageDirective)
        appendLine("else -> error(\"Not a Kotlin IR message\")")
        appendLine("}}")
    }

    private fun StringBuilder.emitEnum(e: EnumDef) {
        appendLine("enum class ${e.name}(override val number: Int) : Internal.EnumLite {")
        for (v in e.values) appendLine("${v.name}(${v.number}),")
        appendLine("; companion object : Internal.EnumLiteMap<${e.name}> {")
        appendLine("override fun findValueByNumber(number: Int): ${e.name}? = valueOf(number)")
        appendLine("fun valueOf(number: Int): ${e.name}? = when (number) {")
        for (v in e.values.distinctBy { it.number }) {
            appendLine("${v.number} -> ${v.name}")
        }
        appendLine("else -> null")
        appendLine("}")
        appendLine("fun internalGetValueMap(): Internal.EnumLiteMap<${e.name}> = this")
        appendLine("}}")
    }

    private fun computeSizeExprNoTag(f: FieldDef, scope: String, valExpr: String): String = when (f.type) {
        "double", "fixed64", "sfixed64" -> "8"
        "float", "fixed32", "sfixed32" -> "4"
        "bool" -> "1"
        "int32" -> "CodedOutputStream.computeInt32SizeNoTag($valExpr)"
        "uint32" -> "CodedOutputStream.computeRawVarint32Size($valExpr)"
        "sint32" -> "CodedOutputStream.computeRawVarint32Size(CodedOutputStream.encodeZigZag32($valExpr))"
        "int64", "uint64" -> "CodedOutputStream.computeRawVarint64Size($valExpr)"
        "sint64" -> "CodedOutputStream.computeRawVarint64Size(CodedOutputStream.encodeZigZag64($valExpr))"
        "bytes" -> "CodedOutputStream.computeBytesSizeNoTag($valExpr)"
        "string" -> "(if ($valExpr is ByteString) CodedOutputStream.computeBytesSizeNoTag($valExpr as ByteString) else CodedOutputStream.computeStringSizeNoTag($valExpr as String))"
        else -> if (enumeration(f, scope) != null) {
            "CodedOutputStream.computeInt32SizeNoTag(($valExpr).number)"
        } else {
            "CodedOutputStream.computeMessageSizeNoTag($valExpr)"
        }
    }

    private fun computeFixedElemSize(f: FieldDef): Int? = when (f.type) {
        "double", "fixed64", "sfixed64" -> 8
        "float", "fixed32", "sfixed32" -> 4
        "bool" -> 1
        else -> null
    }

    private fun writeExprNoTag(f: FieldDef, scope: String, valExpr: String): String = when (f.type) {
        "double" -> "output.writeRawLittleEndian64(($valExpr).toRawBits())"
        "float" -> "output.writeRawLittleEndian32(($valExpr).toRawBits())"
        "fixed64", "sfixed64" -> "output.writeRawLittleEndian64($valExpr)"
        "fixed32", "sfixed32" -> "output.writeRawLittleEndian32($valExpr)"
        "bool" -> "output.writeRawByte(if ($valExpr) 1 else 0)"
        "int32" -> "output.writeInt32NoTag($valExpr)"
        "uint32" -> "output.writeRawVarint32($valExpr)"
        "sint32" -> "output.writeRawVarint32(CodedOutputStream.encodeZigZag32($valExpr))"
        "int64", "uint64" -> "output.writeRawVarint64($valExpr)"
        "sint64" -> "output.writeRawVarint64(CodedOutputStream.encodeZigZag64($valExpr))"
        "bytes" -> "output.writeBytesNoTag($valExpr)"
        "string" -> "if ($valExpr is ByteString) output.writeBytesNoTag($valExpr as ByteString) else output.writeStringNoTag($valExpr as String)"
        else -> if (enumeration(f, scope) != null) {
            "output.writeInt32NoTag(($valExpr).number)"
        } else {
            "output.writeMessageNoTag($valExpr)"
        }
    }

    private fun readScalarWireExpr(f: FieldDef): String = when (f.type) {
        "double" -> "input.readDouble()"
        "float" -> "input.readFloat()"
        "int64" -> "input.readInt64()"
        "uint64" -> "input.readUInt64()"
        "int32" -> "input.readInt32()"
        "fixed64" -> "input.readFixed64()"
        "fixed32" -> "input.readFixed32()"
        "bool" -> "input.readBool()"
        "string" -> "input.readBytes()"
        "bytes" -> "input.readBytes()"
        "uint32" -> "input.readUInt32()"
        "sfixed32" -> "input.readSFixed32()"
        "sfixed64" -> "input.readSFixed64()"
        "sint32" -> "input.readSInt32()"
        "sint64" -> "input.readSInt64()"
        else -> error("Not a scalar wire type: ${f.type}")
    }

    private fun StringBuilder.emitMessage(m: MessageDef, scope: String) {
        val name = m.name
        val ktName = symbols.getValue(scope).kotlin
        val isExtendable = ktName in extendableMessages
        val checkInit = ktName in mayBeUninitialized

        val all = (m.fields + m.oneofs.flatMap { it.fields }).sortedBy { it.tag }
        require(all.map { it.tag }.distinct().size == all.size)

        val singularFields = m.fields.filter { it.rule != "repeated" }
        val repeatedFields = m.fields.filter { it.rule == "repeated" }
        val singularIdx = singularFields.withIndex().associate { (i, f) -> f.tag to i }
        val repeatedIdx = repeatedFields.withIndex().associate { (i, f) -> f.tag to i }
        val oneofByTag = mutableMapOf<Int, Int>()
        m.oneofs.forEachIndexed { oIdx, oneof ->
            oneof.fields.forEach { f -> oneofByTag[f.tag] = oIdx }
        }

        val bitWordCount = (singularFields.size + 31) / 32
        val mutWordCount = (repeatedFields.size + 31) / 32

        appendLine("class $name : GeneratedMessageLite.ExtendableMessage<$name> {")
        m.nestedEnums.forEach { emitEnum(it) }
        m.nestedMessages.forEach { emitMessage(it, "$scope.${it.name}") }

        // Bitfields & backing fields on Message
        for (w in 0 until bitWordCount) {
            appendLine("internal var bitField${w}_: Int = 0")
        }
        for (f in singularFields) {
            val t = type(f, scope)
            when {
                t == "String" -> appendLine("internal var field_${f.tag}_: Any = ${default(f, scope)}")
                message(f, scope) -> appendLine("internal var field_${f.tag}_: $t? = null")
                else -> appendLine("internal var field_${f.tag}_: $t = ${default(f, scope)}")
            }
        }
        for (f in repeatedFields) {
            val t = type(f, scope)
            if (t == "String") {
                appendLine("internal var field_${f.tag}_: LazyStringList = LazyStringArrayList.EMPTY")
            } else {
                appendLine("internal var field_${f.tag}_: List<$t> = emptyList()")
            }
            if (f.isPacked) {
                appendLine("private var packedSize_${f.tag}_: Int = -1")
            }
        }
        for (oIdx in m.oneofs.indices) {
            appendLine("internal var oneofCase_${oIdx}_: Int = 0")
            appendLine("internal var oneofVal_${oIdx}_: Any? = null")
        }
        if (checkInit) {
            appendLine("private var memoizedIsInitialized: Byte = -1")
        }
        appendLine("private var memoizedSerializedSize: Int = -1")

        // Constructors
        appendLine("private constructor()")
        appendLine("private constructor(builder: Builder) {")
        appendLine("this.unknownFields = builder.unknownFields")
        if (isExtendable) {
            appendLine("copyExtensionsFromBuilder(builder)")
        }
        appendLine("}")

        // Direct streaming constructor
        appendLine("private constructor(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite) {")
        for (w in 0 until mutWordCount) {
            appendLine("var mutBit${w}_ = 0")
        }
        appendLine("var unknownBuffer: ByteString.Output? = null")
        appendLine("var unknownOut: CodedOutputStream? = null")
        appendLine("fun unknownOutput(): CodedOutputStream {")
        appendLine("var out = unknownOut")
        appendLine("if (out == null) { val buf = ByteString.newOutput(); unknownBuffer = buf; out = CodedOutputStream.newInstance(buf); unknownOut = out }")
        appendLine("return out")
        appendLine("}")
        appendLine("try {")
        appendLine("var done = false")
        appendLine("while (!done) {")
        appendLine("val tag = input.readTag()")
        appendLine("when (tag) {")
        appendLine("0 -> done = true")
        for (f in all) {
            val t = type(f, scope)
            val wType = wireTypeCode(f, scope)
            val primaryTag = (f.tag shl 3) or wType
            val oIdx = oneofByTag[f.tag]
            if (f.rule == "repeated") {
                val rIdx = repeatedIdx.getValue(f.tag)
                val rWord = rIdx / 32
                val rMask = 1 shl (rIdx % 32)
                if (t == "String") {
                    appendLine("$primaryTag -> {")
                    appendLine("val bs = input.readBytes()")
                    appendLine("if ((mutBit${rWord}_ and $rMask) == 0) { field_${f.tag}_ = LazyStringArrayList(); mutBit${rWord}_ = mutBit${rWord}_ or $rMask }")
                    appendLine("field_${f.tag}_.add(bs)")
                    appendLine("}")
                } else if (message(f, scope)) {
                    appendLine("$primaryTag -> {")
                    appendLine("if ((mutBit${rWord}_ and $rMask) == 0) { field_${f.tag}_ = ArrayList(); mutBit${rWord}_ = mutBit${rWord}_ or $rMask }")
                    appendLine("(field_${f.tag}_ as MutableList<$t>).add(input.readMessage($t.PARSER, extensionRegistry))")
                    appendLine("}")
                } else if (enumeration(f, scope) != null) {
                    appendLine("$primaryTag -> {")
                    appendLine("val raw = input.readEnum()")
                    appendLine("val parsed = $t.valueOf(raw)")
                    appendLine("if (parsed == null) { unknownOutput().writeRawVarint32($primaryTag); unknownOutput().writeRawVarint32(raw) } else {")
                    appendLine("if ((mutBit${rWord}_ and $rMask) == 0) { field_${f.tag}_ = ArrayList(); mutBit${rWord}_ = mutBit${rWord}_ or $rMask }")
                    appendLine("(field_${f.tag}_ as MutableList<$t>).add(parsed)")
                    appendLine("}}")
                    val packedTag = (f.tag shl 3) or 2
                    appendLine("$packedTag -> {")
                    appendLine("val length = input.readRawVarint32()")
                    appendLine("val oldLimit = input.pushLimit(length)")
                    appendLine("try { while (input.getBytesUntilLimit() > 0) {")
                    appendLine("val raw = input.readEnum()")
                    appendLine("val parsed = $t.valueOf(raw)")
                    appendLine("if (parsed == null) { unknownOutput().writeRawVarint32($primaryTag); unknownOutput().writeRawVarint32(raw) } else {")
                    appendLine("if ((mutBit${rWord}_ and $rMask) == 0) { field_${f.tag}_ = ArrayList(); mutBit${rWord}_ = mutBit${rWord}_ or $rMask }")
                    appendLine("(field_${f.tag}_ as MutableList<$t>).add(parsed)")
                    appendLine("}}} finally { input.popLimit(oldLimit) }")
                    appendLine("}")
                } else {
                    appendLine("$primaryTag -> {")
                    appendLine("if ((mutBit${rWord}_ and $rMask) == 0) { field_${f.tag}_ = ArrayList(); mutBit${rWord}_ = mutBit${rWord}_ or $rMask }")
                    appendLine("(field_${f.tag}_ as MutableList<$t>).add(${readScalarWireExpr(f)})")
                    appendLine("}")
                    if (isPackableRepeated(f, scope)) {
                        val packedTag = (f.tag shl 3) or 2
                        appendLine("$packedTag -> {")
                        appendLine("val length = input.readRawVarint32()")
                        appendLine("val oldLimit = input.pushLimit(length)")
                        appendLine("try {")
                        appendLine("if (length > 0 && (mutBit${rWord}_ and $rMask) == 0) { field_${f.tag}_ = ArrayList(); mutBit${rWord}_ = mutBit${rWord}_ or $rMask }")
                        appendLine("while (input.getBytesUntilLimit() > 0) { (field_${f.tag}_ as MutableList<$t>).add(${readScalarWireExpr(f)}) }")
                        appendLine("} finally { input.popLimit(oldLimit) }")
                        appendLine("}")
                    }
                }
            } else if (oIdx != null) {
                if (message(f, scope)) {
                    appendLine("$primaryTag -> {")
                    appendLine("var subBuilder: $t.Builder? = null")
                    appendLine("if (oneofCase_${oIdx}_ == ${f.tag}) subBuilder = (oneofVal_${oIdx}_ as $t).toBuilder()")
                    appendLine("val parsed = input.readMessage($t.PARSER, extensionRegistry)")
                    appendLine("oneofVal_${oIdx}_ = if (subBuilder != null) subBuilder.mergeFrom(parsed).buildPartial() else parsed")
                    appendLine("oneofCase_${oIdx}_ = ${f.tag}")
                    appendLine("}")
                } else if (enumeration(f, scope) != null) {
                    appendLine("$primaryTag -> {")
                    appendLine("val raw = input.readEnum()")
                    appendLine("val parsed = $t.valueOf(raw)")
                    appendLine("if (parsed == null) { unknownOutput().writeRawVarint32($primaryTag); unknownOutput().writeRawVarint32(raw) } else {")
                    appendLine("oneofCase_${oIdx}_ = ${f.tag}; oneofVal_${oIdx}_ = parsed")
                    appendLine("}}")
                } else {
                    appendLine("$primaryTag -> { oneofCase_${oIdx}_ = ${f.tag}; oneofVal_${oIdx}_ = ${readScalarWireExpr(f)} }")
                }
            } else {
                val sIdx = singularIdx.getValue(f.tag)
                val sWord = sIdx / 32
                val sMask = 1 shl (sIdx % 32)
                if (message(f, scope)) {
                    appendLine("$primaryTag -> {")
                    appendLine("var subBuilder: $t.Builder? = null")
                    appendLine("if ((bitField${sWord}_ and $sMask) != 0) subBuilder = field_${f.tag}_?.toBuilder()")
                    appendLine("val parsed = input.readMessage($t.PARSER, extensionRegistry)")
                    appendLine("field_${f.tag}_ = if (subBuilder != null) subBuilder.mergeFrom(parsed).buildPartial() else parsed")
                    appendLine("bitField${sWord}_ = bitField${sWord}_ or $sMask")
                    appendLine("}")
                } else if (enumeration(f, scope) != null) {
                    appendLine("$primaryTag -> {")
                    appendLine("val raw = input.readEnum()")
                    appendLine("val parsed = $t.valueOf(raw)")
                    appendLine("if (parsed == null) { unknownOutput().writeRawVarint32($primaryTag); unknownOutput().writeRawVarint32(raw) } else {")
                    appendLine("bitField${sWord}_ = bitField${sWord}_ or $sMask; field_${f.tag}_ = parsed")
                    appendLine("}}")
                } else {
                    appendLine("$primaryTag -> { bitField${sWord}_ = bitField${sWord}_ or $sMask; field_${f.tag}_ = ${readScalarWireExpr(f)} }")
                }
            }
        }
        if (isExtendable) {
            appendLine("else -> if (!parseUnknownField(input, unknownOutput(), extensionRegistry, tag)) done = true")
        } else {
            appendLine("else -> if (!input.skipField(tag, unknownOutput())) done = true")
        }
        appendLine("}}")
        appendLine("} catch (e: InvalidProtocolBufferException) {")
        appendLine("throw e.setUnfinishedMessage(this)")
        appendLine("} finally {")
        for (f in repeatedFields) {
            val rIdx = repeatedIdx.getValue(f.tag)
            val rWord = rIdx / 32
            val rMask = 1 shl (rIdx % 32)
            if (type(f, scope) == "String") {
                appendLine("if ((mutBit${rWord}_ and $rMask) != 0) field_${f.tag}_ = field_${f.tag}_.getUnmodifiableView()")
            } else {
                appendLine("if ((mutBit${rWord}_ and $rMask) != 0) field_${f.tag}_ = GeneratedMessageLite.unmodifiableProtoList(field_${f.tag}_)")
            }
        }
        appendLine("if (unknownBuffer != null) { unknownOut!!.flush(); unknownFields = unknownBuffer.toByteString() }")
        if (isExtendable) {
            appendLine("makeExtensionsImmutable()")
        }
        appendLine("}}")

        // Oneof enums & getters on Message
        m.oneofs.forEachIndexed { oIdx, oneof ->
            val caseEnum = cap(oneof.name) + "Case"
            emitEnum(
                EnumDef(
                    caseEnum,
                    oneof.fields.map { EnumValue(it.name.uppercase(), it.tag) } +
                        EnumValue(oneof.name.uppercase() + "_NOT_SET", 0)
                )
            )
            appendLine("val ${camel(oneof.name)}Case: $caseEnum get() = $caseEnum.valueOf(oneofCase_${oIdx}_)!!")
            appendLine("fun get${cap(oneof.name)}Case(): $caseEnum = ${camel(oneof.name)}Case")
        }

        // Field getters on Message
        for (f in all) {
            val n = camel(f.name)
            val c = cap(f.name)
            val t = type(f, scope)
            val oIdx = oneofByTag[f.tag]
            if (f.rule == "repeated") {
                appendLine("val ${ident(n + "List")}: List<$t> get() = field_${f.tag}_")
                appendLine("val ${ident(n + "Count")}: Int get() = field_${f.tag}_.size")
                appendLine("fun get${c}List(): List<$t> = field_${f.tag}_")
                appendLine("fun get${c}Count(): Int = field_${f.tag}_.size")
                appendLine("fun get$c(index: Int): $t = field_${f.tag}_[index]")
            } else if (oIdx != null) {
                appendLine("val ${ident(n)}: $t get() {")
                if (t == "String") {
                    appendLine("if (oneofCase_${oIdx}_ != ${f.tag}) return ${default(f, scope)}")
                    appendLine("val ref = oneofVal_${oIdx}_")
                    appendLine("if (ref is String) return ref")
                    appendLine("val bs = ref as ByteString")
                    appendLine("val s = bs.toStringUtf8()")
                    appendLine("if (bs.isValidUtf8()) oneofVal_${oIdx}_ = s")
                    appendLine("return s")
                } else {
                    appendLine("return if (oneofCase_${oIdx}_ == ${f.tag}) oneofVal_${oIdx}_ as $t else ${default(f, scope)}")
                }
                appendLine("}")
                appendLine("fun get$c(): $t = ${ident(n)}")
                appendLine("fun has$c(): Boolean = oneofCase_${oIdx}_ == ${f.tag}")
            } else {
                val sIdx = singularIdx.getValue(f.tag)
                val sWord = sIdx / 32
                val sMask = 1 shl (sIdx % 32)
                if (t == "String") {
                    appendLine("val ${ident(n)}: String get() {")
                    appendLine("val ref = field_${f.tag}_")
                    appendLine("if (ref is String) return ref")
                    appendLine("val bs = ref as ByteString")
                    appendLine("val s = bs.toStringUtf8()")
                    appendLine("if (bs.isValidUtf8()) field_${f.tag}_ = s")
                    appendLine("return s")
                    appendLine("}")
                } else if (message(f, scope)) {
                    appendLine("val ${ident(n)}: $t get() = field_${f.tag}_ ?: $t.defaultInstance")
                } else {
                    appendLine("val ${ident(n)}: $t get() = field_${f.tag}_")
                }
                appendLine("fun get$c(): $t = ${ident(n)}")
                appendLine("fun has$c(): Boolean = (bitField${sWord}_ and $sMask) != 0")
            }
        }

        // isInitialized on Message
        if (!checkInit) {
            appendLine("override fun isInitialized(): Boolean = true")
        } else {
            appendLine("override fun isInitialized(): Boolean {")
            appendLine("val isInitialized = memoizedIsInitialized.toInt()")
            appendLine("if (isInitialized == 1) return true")
            appendLine("if (isInitialized == 0) return false")
            for (f in singularFields) {
                if (f.rule == "required") {
                    val sIdx = singularIdx.getValue(f.tag)
                    appendLine("if ((bitField${sIdx / 32}_ and ${1 shl (sIdx % 32)}) == 0) { memoizedIsInitialized = 0; return false }")
                }
            }
            for (f in singularFields) {
                val t = type(f, scope)
                if (message(f, scope) && t in mayBeUninitialized) {
                    val sIdx = singularIdx.getValue(f.tag)
                    appendLine("if ((bitField${sIdx / 32}_ and ${1 shl (sIdx % 32)}) != 0 && !get${cap(f.name)}().isInitialized()) { memoizedIsInitialized = 0; return false }")
                }
            }
            for (f in repeatedFields) {
                val t = type(f, scope)
                if (message(f, scope) && t in mayBeUninitialized) {
                    appendLine("for (i in 0 until field_${f.tag}_.size) { if (!field_${f.tag}_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }")
                }
            }
            m.oneofs.forEachIndexed { oIdx, oneof ->
                val checkArms = oneof.fields.filter { message(it, scope) && type(it, scope) in mayBeUninitialized }
                if (checkArms.isNotEmpty()) {
                    appendLine("when (oneofCase_${oIdx}_) {")
                    for (f in checkArms) {
                        val t = type(f, scope)
                        appendLine("${f.tag} -> if (!(oneofVal_${oIdx}_ as $t).isInitialized()) { memoizedIsInitialized = 0; return false }")
                    }
                    appendLine("}")
                }
            }
            if (isExtendable) {
                appendLine("if (!extensionsAreInitialized()) { memoizedIsInitialized = 0; return false }")
            }
            appendLine("memoizedIsInitialized = 1")
            appendLine("return true")
            appendLine("}")
        }

        // writeTo on Message
        appendLine("override fun writeTo(output: CodedOutputStream) {")
        if (repeatedFields.any { it.isPacked }) {
            appendLine("serializedSize")
        }
        if (m.fields.isEmpty() && m.oneofs.size == 1 && !isExtendable) {
            val oneof = m.oneofs[0]
            appendLine("when (oneofCase_0_) {")
            for (f in oneof.fields) {
                val t = type(f, scope)
                val rawTag = (f.tag shl 3) or wireTypeCode(f, scope)
                val vExpr = if (t == "String") "oneofVal_0_!!" else "(oneofVal_0_ as $t)"
                appendLine("${f.tag} -> { output.writeRawVarint32($rawTag); ${writeExprNoTag(f, scope, vExpr)} }")
            }
            appendLine("}")
        } else {
            if (isExtendable) {
                appendLine("val extensionWriter = if (hasAnyExtensions()) newExtensionWriter() else null")
            }
            for (f in all) {
                if (isExtendable) {
                    appendLine("extensionWriter?.writeUntil(${f.tag}, output)")
                }
                val t = type(f, scope)
                val oIdx = oneofByTag[f.tag]
                if (f.rule == "repeated") {
                    if (f.isPacked) {
                        val packedTag = (f.tag shl 3) or 2
                        appendLine("if (field_${f.tag}_.isNotEmpty()) {")
                        appendLine("output.writeRawVarint32($packedTag)")
                        appendLine("output.writeRawVarint32(packedSize_${f.tag}_)")
                        appendLine("for (i in 0 until field_${f.tag}_.size) { ${writeExprNoTag(f, scope, "field_${f.tag}_[i]")} }")
                        appendLine("}")
                    } else {
                        val rawTag = (f.tag shl 3) or wireTypeCode(f, scope)
                        if (t == "String") {
                            appendLine("for (i in 0 until field_${f.tag}_.size) {")
                            appendLine("val elem = field_${f.tag}_.getUnderlying(i)")
                            appendLine("output.writeRawVarint32($rawTag)")
                            appendLine("if (elem is ByteString) output.writeBytesNoTag(elem) else output.writeStringNoTag(elem as String)")
                            appendLine("}")
                        } else {
                            appendLine("for (i in 0 until field_${f.tag}_.size) { output.writeRawVarint32($rawTag); ${writeExprNoTag(f, scope, "field_${f.tag}_[i]")} }")
                        }
                    }
                } else if (oIdx != null) {
                    val rawTag = (f.tag shl 3) or wireTypeCode(f, scope)
                    val vExpr = if (t == "String") "oneofVal_${oIdx}_!!" else "(oneofVal_${oIdx}_ as $t)"
                    appendLine("if (oneofCase_${oIdx}_ == ${f.tag}) { output.writeRawVarint32($rawTag); ${writeExprNoTag(f, scope, vExpr)} }")
                } else {
                    val sIdx = singularIdx.getValue(f.tag)
                    val rawTag = (f.tag shl 3) or wireTypeCode(f, scope)
                    val vExpr = if (message(f, scope)) "get${cap(f.name)}()" else "field_${f.tag}_"
                    appendLine("if ((bitField${sIdx / 32}_ and ${1 shl (sIdx % 32)}) != 0) { output.writeRawVarint32($rawTag); ${writeExprNoTag(f, scope, vExpr)} }")
                }
            }
            if (isExtendable) {
                appendLine("extensionWriter?.writeUntil(Int.MAX_VALUE, output)")
            }
        }
        appendLine("if (!unknownFields.isEmpty()) unknownFields.writeTo(output)")
        appendLine("}")

        // serializedSize on Message
        appendLine("override val serializedSize: Int get() {")
        appendLine("val cached = memoizedSerializedSize")
        appendLine("if (cached != -1) return cached")
        appendLine("var size = 0")
        for (f in singularFields) {
            val sIdx = singularIdx.getValue(f.tag)
            val rawTag = (f.tag shl 3) or wireTypeCode(f, scope)
            val tagBytes = varint32Bytes(rawTag)
            val fixed = computeFixedElemSize(f)
            if (fixed != null) {
                appendLine("if ((bitField${sIdx / 32}_ and ${1 shl (sIdx % 32)}) != 0) size += ${tagBytes + fixed}")
            } else {
                val vExpr = if (message(f, scope)) "get${cap(f.name)}()" else "field_${f.tag}_"
                appendLine("if ((bitField${sIdx / 32}_ and ${1 shl (sIdx % 32)}) != 0) size += $tagBytes + ${computeSizeExprNoTag(f, scope, vExpr)}")
            }
        }
        for (f in repeatedFields) {
            val t = type(f, scope)
            val fixed = computeFixedElemSize(f)
            if (f.isPacked) {
                val packedTag = (f.tag shl 3) or 2
                val tagBytes = varint32Bytes(packedTag)
                appendLine("if (field_${f.tag}_.isNotEmpty()) {")
                if (fixed != null) {
                    appendLine("val dataSize = $fixed * field_${f.tag}_.size")
                } else {
                    appendLine("var dataSize = 0")
                    appendLine("for (i in 0 until field_${f.tag}_.size) { dataSize += ${computeSizeExprNoTag(f, scope, "field_${f.tag}_[i]")} }")
                }
                appendLine("size += $tagBytes + CodedOutputStream.computeRawVarint32Size(dataSize) + dataSize")
                appendLine("packedSize_${f.tag}_ = dataSize")
                appendLine("} else { packedSize_${f.tag}_ = 0 }")
            } else {
                val rawTag = (f.tag shl 3) or wireTypeCode(f, scope)
                val tagBytes = varint32Bytes(rawTag)
                if (fixed != null) {
                    appendLine("size += ${tagBytes + fixed} * field_${f.tag}_.size")
                } else if (t == "String") {
                    appendLine("if (field_${f.tag}_.isNotEmpty()) {")
                    appendLine("var dataSize = 0")
                    appendLine("for (i in 0 until field_${f.tag}_.size) {")
                    appendLine("val elem = field_${f.tag}_.getUnderlying(i)")
                    appendLine("dataSize += if (elem is ByteString) CodedOutputStream.computeBytesSizeNoTag(elem) else CodedOutputStream.computeStringSizeNoTag(elem as String)")
                    appendLine("}")
                    appendLine("size += dataSize + $tagBytes * field_${f.tag}_.size")
                    appendLine("}")
                } else {
                    appendLine("for (i in 0 until field_${f.tag}_.size) { size += $tagBytes + ${computeSizeExprNoTag(f, scope, "field_${f.tag}_[i]")} }")
                }
            }
        }
        m.oneofs.forEachIndexed { oIdx, oneof ->
            appendLine("when (oneofCase_${oIdx}_) {")
            for (f in oneof.fields) {
                val t = type(f, scope)
                val rawTag = (f.tag shl 3) or wireTypeCode(f, scope)
                val tagBytes = varint32Bytes(rawTag)
                val fixed = computeFixedElemSize(f)
                if (fixed != null) {
                    appendLine("${f.tag} -> size += ${tagBytes + fixed}")
                } else {
                    val vExpr = if (t == "String") "oneofVal_${oIdx}_!!" else "(oneofVal_${oIdx}_ as $t)"
                    appendLine("${f.tag} -> size += $tagBytes + ${computeSizeExprNoTag(f, scope, vExpr)}")
                }
            }
            appendLine("}")
        }
        if (isExtendable) {
            appendLine("size += extensionsSerializedSize()")
        }
        appendLine("size += unknownFields.size()")
        appendLine("memoizedSerializedSize = size")
        appendLine("return size")
        appendLine("}")

        appendLine("override val defaultInstanceForType: $name get() = defaultInstance")
        appendLine("override fun newBuilderForType(): Builder = Builder()")
        appendLine("override fun toBuilder(): Builder = Builder().mergeFrom(this)")

        // Builder class
        appendLine("class Builder : GeneratedMessageLite.ExtendableBuilder<$name, Builder>() {")
        for (w in 0 until bitWordCount) {
            appendLine("private var bitField${w}_: Int = 0")
        }
        for (w in 0 until mutWordCount) {
            appendLine("private var mutBitField${w}_: Int = 0")
        }
        for (f in singularFields) {
            val t = type(f, scope)
            when {
                t == "String" -> appendLine("private var field_${f.tag}_: Any = ${default(f, scope)}")
                message(f, scope) -> appendLine("private var field_${f.tag}_: $t? = null")
                else -> appendLine("private var field_${f.tag}_: $t = ${default(f, scope)}")
            }
        }
        for (f in repeatedFields) {
            val t = type(f, scope)
            val rIdx = repeatedIdx.getValue(f.tag)
            val rWord = rIdx / 32
            val rMask = 1 shl (rIdx % 32)
            if (t == "String") {
                appendLine("private var field_${f.tag}_: LazyStringList = LazyStringArrayList.EMPTY")
                appendLine("private fun ensure_${f.tag}_Mutable() { if ((mutBitField${rWord}_ and $rMask) == 0) { field_${f.tag}_ = LazyStringArrayList(field_${f.tag}_); mutBitField${rWord}_ = mutBitField${rWord}_ or $rMask } }")
            } else {
                appendLine("private var field_${f.tag}_: List<$t> = emptyList()")
                appendLine("private fun ensure_${f.tag}_Mutable() { if ((mutBitField${rWord}_ and $rMask) == 0) { field_${f.tag}_ = ArrayList(field_${f.tag}_); mutBitField${rWord}_ = mutBitField${rWord}_ or $rMask } }")
            }
        }
        for (oIdx in m.oneofs.indices) {
            appendLine("private var oneofCase_${oIdx}_: Int = 0")
            appendLine("private var oneofVal_${oIdx}_: Any? = null")
        }

        appendLine("override val defaultInstanceForType: $name get() = defaultInstance")
        appendLine("override fun clear(): Builder {")
        for (w in 0 until bitWordCount) appendLine("bitField${w}_ = 0")
        for (w in 0 until mutWordCount) appendLine("mutBitField${w}_ = 0")
        for (f in singularFields) {
            val t = type(f, scope)
            when {
                t == "String" -> appendLine("field_${f.tag}_ = ${default(f, scope)}")
                message(f, scope) -> appendLine("field_${f.tag}_ = null")
                else -> appendLine("field_${f.tag}_ = ${default(f, scope)}")
            }
        }
        for (f in repeatedFields) {
            if (type(f, scope) == "String") appendLine("field_${f.tag}_ = LazyStringArrayList.EMPTY")
            else appendLine("field_${f.tag}_ = emptyList()")
        }
        for (oIdx in m.oneofs.indices) {
            appendLine("oneofCase_${oIdx}_ = 0; oneofVal_${oIdx}_ = null")
        }
        if (isExtendable) appendLine("clearExtensions()")
        appendLine("unknownFields = ByteString.EMPTY")
        appendLine("return this")
        appendLine("}")

        appendLine("override fun clone(): Builder = Builder().mergeFrom(buildPartial())")

        appendLine("override fun build(): $name {")
        appendLine("val result = buildPartial()")
        if (checkInit) {
            appendLine("if (!result.isInitialized()) throw UninitializedMessageException()")
        }
        appendLine("return result")
        appendLine("}")

        appendLine("override fun buildPartial(): $name {")
        appendLine("val result = $name(this)")
        for (w in 0 until bitWordCount) {
            appendLine("val from_bitField${w}_ = bitField${w}_")
            appendLine("var to_bitField${w}_ = 0")
        }
        for (f in singularFields) {
            val sIdx = singularIdx.getValue(f.tag)
            val sWord = sIdx / 32
            val sMask = 1 shl (sIdx % 32)
            appendLine("if ((from_bitField${sWord}_ and $sMask) != 0) to_bitField${sWord}_ = to_bitField${sWord}_ or $sMask")
            appendLine("result.field_${f.tag}_ = field_${f.tag}_")
        }
        for (w in 0 until bitWordCount) {
            appendLine("result.bitField${w}_ = to_bitField${w}_")
        }
        for (f in repeatedFields) {
            val rIdx = repeatedIdx.getValue(f.tag)
            val rWord = rIdx / 32
            val rMask = 1 shl (rIdx % 32)
            if (type(f, scope) == "String") {
                appendLine("if ((mutBitField${rWord}_ and $rMask) != 0) { field_${f.tag}_ = field_${f.tag}_.getUnmodifiableView(); mutBitField${rWord}_ = mutBitField${rWord}_ and ${rMask.inv()} }")
            } else {
                appendLine("if ((mutBitField${rWord}_ and $rMask) != 0) { field_${f.tag}_ = GeneratedMessageLite.unmodifiableProtoList(field_${f.tag}_); mutBitField${rWord}_ = mutBitField${rWord}_ and ${rMask.inv()} }")
            }
            appendLine("result.field_${f.tag}_ = field_${f.tag}_")
        }
        for (oIdx in m.oneofs.indices) {
            appendLine("result.oneofCase_${oIdx}_ = oneofCase_${oIdx}_")
            appendLine("result.oneofVal_${oIdx}_ = oneofVal_${oIdx}_")
        }
        appendLine("return result")
        appendLine("}")

        // mergeFrom(other: Message)
        appendLine("override fun mergeFrom(other: $name): Builder {")
        appendLine("if (other === defaultInstance) return this")
        for (f in singularFields) {
            val c = cap(f.name)
            val t = type(f, scope)
            val sIdx = singularIdx.getValue(f.tag)
            val sWord = sIdx / 32
            val sMask = 1 shl (sIdx % 32)
            when {
                t == "String" -> appendLine("if (other.has$c()) { bitField${sWord}_ = bitField${sWord}_ or $sMask; field_${f.tag}_ = other.field_${f.tag}_ }")
                message(f, scope) -> appendLine("if (other.has$c()) merge$c(other.get$c())")
                else -> appendLine("if (other.has$c()) set$c(other.get$c())")
            }
        }
        for (f in repeatedFields) {
            val rIdx = repeatedIdx.getValue(f.tag)
            val rWord = rIdx / 32
            val rMask = 1 shl (rIdx % 32)
            appendLine("if (other.field_${f.tag}_.isNotEmpty()) {")
            appendLine("if (field_${f.tag}_.isEmpty()) { field_${f.tag}_ = other.field_${f.tag}_; mutBitField${rWord}_ = mutBitField${rWord}_ and ${rMask.inv()} } else { ensure_${f.tag}_Mutable(); (field_${f.tag}_ as MutableList<Any>).addAll(other.field_${f.tag}_ as Collection<Any>) }")
            appendLine("}")
        }
        m.oneofs.forEachIndexed { oIdx, oneof ->
            appendLine("when (other.oneofCase_${oIdx}_) {")
            for (f in oneof.fields) {
                val c = cap(f.name)
                val t = type(f, scope)
                when {
                    message(f, scope) -> appendLine("${f.tag} -> merge$c(other.get$c())")
                    t == "String" -> appendLine("${f.tag} -> { oneofCase_${oIdx}_ = ${f.tag}; oneofVal_${oIdx}_ = other.oneofVal_${oIdx}_ }")
                    else -> appendLine("${f.tag} -> set$c(other.get$c())")
                }
            }
            appendLine("}")
        }
        if (isExtendable) appendLine("mergeExtensionFields(other)")
        appendLine("unknownFields = unknownFields.concat(other.unknownFields)")
        appendLine("return this")
        appendLine("}")

        // isInitialized on Builder
        if (!checkInit) {
            appendLine("override fun isInitialized(): Boolean = true")
        } else {
            appendLine("override fun isInitialized(): Boolean {")
            for (f in singularFields) {
                if (f.rule == "required") {
                    val sIdx = singularIdx.getValue(f.tag)
                    appendLine("if ((bitField${sIdx / 32}_ and ${1 shl (sIdx % 32)}) == 0) return false")
                }
            }
            for (f in singularFields) {
                val t = type(f, scope)
                if (message(f, scope) && t in mayBeUninitialized) {
                    val sIdx = singularIdx.getValue(f.tag)
                    appendLine("if ((bitField${sIdx / 32}_ and ${1 shl (sIdx % 32)}) != 0 && !get${cap(f.name)}().isInitialized()) return false")
                }
            }
            for (f in repeatedFields) {
                val t = type(f, scope)
                if (message(f, scope) && t in mayBeUninitialized) {
                    appendLine("for (i in 0 until field_${f.tag}_.size) { if (!field_${f.tag}_[i].isInitialized()) return false }")
                }
            }
            m.oneofs.forEachIndexed { oIdx, oneof ->
                val checkArms = oneof.fields.filter { message(it, scope) && type(it, scope) in mayBeUninitialized }
                if (checkArms.isNotEmpty()) {
                    appendLine("when (oneofCase_${oIdx}_) {")
                    for (f in checkArms) {
                        val t = type(f, scope)
                        appendLine("${f.tag} -> if (!(oneofVal_${oIdx}_ as $t).isInitialized()) return false")
                    }
                    appendLine("}")
                }
            }
            if (isExtendable) {
                appendLine("if (!extensionsAreInitialized()) return false")
            }
            appendLine("return true")
            appendLine("}")
        }

        // mergeFrom(input, extensionRegistry) on Builder
        appendLine("override fun mergeFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Builder {")
        appendLine("var parsedMessage: $name? = null")
        appendLine("try {")
        appendLine("parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry)")
        appendLine("} catch (e: InvalidProtocolBufferException) {")
        appendLine("parsedMessage = e.unfinishedMessage as? $name")
        appendLine("throw e")
        appendLine("} finally {")
        appendLine("if (parsedMessage != null) mergeFrom(parsedMessage)")
        appendLine("}")
        appendLine("return this")
        appendLine("}")

        // Oneof getters & clear on Builder
        m.oneofs.forEachIndexed { oIdx, oneof ->
            val caseEnum = cap(oneof.name) + "Case"
            appendLine("val ${camel(oneof.name)}Case: $caseEnum get() = $caseEnum.valueOf(oneofCase_${oIdx}_)!!")
            appendLine("fun get${cap(oneof.name)}Case(): $caseEnum = ${camel(oneof.name)}Case")
            appendLine("fun clear${cap(oneof.name)}(): Builder { oneofCase_${oIdx}_ = 0; oneofVal_${oIdx}_ = null; return this }")
        }

        // Field accessors & mutators on Builder
        for (f in all) {
            val n = camel(f.name)
            val c = cap(f.name)
            val t = type(f, scope)
            val oIdx = oneofByTag[f.tag]
            if (f.rule == "repeated") {
                appendLine("val ${ident(n + "List")}: List<$t> get() = if (field_${f.tag}_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_${f.tag}_)")
                appendLine("val ${ident(n + "Count")}: Int get() = field_${f.tag}_.size")
                appendLine("fun get${c}List(): List<$t> = ${ident(n + "List")}")
                appendLine("fun get${c}Count(): Int = field_${f.tag}_.size")
                appendLine("fun get$c(index: Int): $t = field_${f.tag}_[index]")
                if (t == "String") {
                    appendLine("fun add$c(value: String): Builder { ensure_${f.tag}_Mutable(); (field_${f.tag}_ as LazyStringArrayList).add(value); return this }")
                    appendLine("fun set$c(index: Int, value: String): Builder { ensure_${f.tag}_Mutable(); (field_${f.tag}_ as LazyStringArrayList)[index] = value; return this }")
                    appendLine("fun addAll$c(values: Iterable<String>): Builder { ensure_${f.tag}_Mutable(); AbstractMessageLite.Builder.addAll(values, field_${f.tag}_ as LazyStringArrayList); return this }")
                    appendLine("fun clear$c(): Builder { field_${f.tag}_ = LazyStringArrayList.EMPTY; mutBitField${repeatedIdx.getValue(f.tag) / 32}_ = mutBitField${repeatedIdx.getValue(f.tag) / 32}_ and ${(1 shl (repeatedIdx.getValue(f.tag) % 32)).inv()}; return this }")
                } else {
                    appendLine("fun add$c(value: $t): Builder { ensure_${f.tag}_Mutable(); (field_${f.tag}_ as MutableList<$t>).add(value); return this }")
                    appendLine("fun set$c(index: Int, value: $t): Builder { ensure_${f.tag}_Mutable(); (field_${f.tag}_ as MutableList<$t>)[index] = value; return this }")
                    appendLine("fun addAll$c(values: Iterable<$t>): Builder { ensure_${f.tag}_Mutable(); AbstractMessageLite.Builder.addAll(values, field_${f.tag}_ as MutableList<$t>); return this }")
                    appendLine("fun clear$c(): Builder { field_${f.tag}_ = emptyList(); mutBitField${repeatedIdx.getValue(f.tag) / 32}_ = mutBitField${repeatedIdx.getValue(f.tag) / 32}_ and ${(1 shl (repeatedIdx.getValue(f.tag) % 32)).inv()}; return this }")
                    if (message(f, scope)) {
                        appendLine("fun add$c(value: $t.Builder): Builder = add$c(value.build())")
                        appendLine("fun set$c(index: Int, value: $t.Builder): Builder = set$c(index, value.build())")
                    }
                }
            } else if (oIdx != null) {
                appendLine("var ${ident(n)}: $t")
                appendLine("get() {")
                if (t == "String") {
                    appendLine("if (oneofCase_${oIdx}_ != ${f.tag}) return ${default(f, scope)}")
                    appendLine("val ref = oneofVal_${oIdx}_")
                    appendLine("if (ref is String) return ref")
                    appendLine("val bs = ref as ByteString")
                    appendLine("val s = bs.toStringUtf8()")
                    appendLine("if (bs.isValidUtf8()) oneofVal_${oIdx}_ = s")
                    appendLine("return s")
                } else {
                    appendLine("return if (oneofCase_${oIdx}_ == ${f.tag}) oneofVal_${oIdx}_ as $t else ${default(f, scope)}")
                }
                appendLine("}")
                appendLine("set(value) { oneofCase_${oIdx}_ = ${f.tag}; oneofVal_${oIdx}_ = value }")
                appendLine("fun get$c(): $t = ${ident(n)}")
                appendLine("fun has$c(): Boolean = oneofCase_${oIdx}_ == ${f.tag}")
                appendLine("fun set$c(value: $t): Builder { oneofCase_${oIdx}_ = ${f.tag}; oneofVal_${oIdx}_ = value; return this }")
                appendLine("fun clear$c(): Builder { if (oneofCase_${oIdx}_ == ${f.tag}) { oneofCase_${oIdx}_ = 0; oneofVal_${oIdx}_ = null }; return this }")
                if (message(f, scope)) {
                    appendLine("fun set$c(value: $t.Builder): Builder = set$c(value.build())")
                    appendLine("fun merge$c(value: $t): Builder {")
                    appendLine("oneofVal_${oIdx}_ = if (oneofCase_${oIdx}_ == ${f.tag} && oneofVal_${oIdx}_ !== $t.defaultInstance) $t.newBuilder(oneofVal_${oIdx}_ as $t).mergeFrom(value).buildPartial() else value")
                    appendLine("oneofCase_${oIdx}_ = ${f.tag}")
                    appendLine("return this")
                    appendLine("}")
                }
            } else {
                val sIdx = singularIdx.getValue(f.tag)
                val sWord = sIdx / 32
                val sMask = 1 shl (sIdx % 32)
                appendLine("var ${ident(n)}: $t")
                if (t == "String") {
                    appendLine("get() {")
                    appendLine("val ref = field_${f.tag}_")
                    appendLine("if (ref is String) return ref")
                    appendLine("val bs = ref as ByteString")
                    appendLine("val s = bs.toStringUtf8()")
                    appendLine("if (bs.isValidUtf8()) field_${f.tag}_ = s")
                    appendLine("return s")
                    appendLine("}")
                    appendLine("set(value) { bitField${sWord}_ = bitField${sWord}_ or $sMask; field_${f.tag}_ = value }")
                    appendLine("fun get$c(): String = ${ident(n)}")
                    appendLine("fun has$c(): Boolean = (bitField${sWord}_ and $sMask) != 0")
                    appendLine("fun set$c(value: String): Builder { bitField${sWord}_ = bitField${sWord}_ or $sMask; field_${f.tag}_ = value; return this }")
                    appendLine("fun clear$c(): Builder { bitField${sWord}_ = bitField${sWord}_ and ${sMask.inv()}; field_${f.tag}_ = ${default(f, scope)}; return this }")
                } else if (message(f, scope)) {
                    appendLine("get() = field_${f.tag}_ ?: $t.defaultInstance")
                    appendLine("set(value) { field_${f.tag}_ = value; bitField${sWord}_ = bitField${sWord}_ or $sMask }")
                    appendLine("fun get$c(): $t = ${ident(n)}")
                    appendLine("fun has$c(): Boolean = (bitField${sWord}_ and $sMask) != 0")
                    appendLine("fun set$c(value: $t): Builder { field_${f.tag}_ = value; bitField${sWord}_ = bitField${sWord}_ or $sMask; return this }")
                    appendLine("fun set$c(value: $t.Builder): Builder { field_${f.tag}_ = value.build(); bitField${sWord}_ = bitField${sWord}_ or $sMask; return this }")
                    appendLine("fun merge$c(value: $t): Builder {")
                    appendLine("val cur = field_${f.tag}_")
                    appendLine("field_${f.tag}_ = if ((bitField${sWord}_ and $sMask) != 0 && cur != null && cur !== $t.defaultInstance) $t.newBuilder(cur).mergeFrom(value).buildPartial() else value")
                    appendLine("bitField${sWord}_ = bitField${sWord}_ or $sMask")
                    appendLine("return this")
                    appendLine("}")
                    appendLine("fun clear$c(): Builder { field_${f.tag}_ = null; bitField${sWord}_ = bitField${sWord}_ and ${sMask.inv()}; return this }")
                } else {
                    appendLine("get() = field_${f.tag}_")
                    appendLine("set(value) { bitField${sWord}_ = bitField${sWord}_ or $sMask; field_${f.tag}_ = value }")
                    appendLine("fun get$c(): $t = field_${f.tag}_")
                    appendLine("fun has$c(): Boolean = (bitField${sWord}_ and $sMask) != 0")
                    appendLine("fun set$c(value: $t): Builder { bitField${sWord}_ = bitField${sWord}_ or $sMask; field_${f.tag}_ = value; return this }")
                    appendLine("fun clear$c(): Builder { bitField${sWord}_ = bitField${sWord}_ and ${sMask.inv()}; field_${f.tag}_ = ${default(f, scope)}; return this }")
                }
            }
        }
        appendLine("}")

        // Companion object
        appendLine("companion object {")
        appendLine("val defaultInstance: $name = $name()")
        appendLine("fun getDefaultInstance(): $name = defaultInstance")
        appendLine("fun newBuilder(): Builder = Builder()")
        appendLine("fun newBuilder(prototype: $name): Builder = Builder().mergeFrom(prototype)")
        appendLine("val PARSER: Parser<$name> = object : AbstractParser<$name>() {")
        appendLine("override fun parsePartialFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): $name =")
        appendLine("$name(input, extensionRegistry)")
        appendLine("}")
        appendLine("fun parseFrom(bytes: ByteArray): $name = PARSER.parseFrom(bytes)")
        appendLine("fun parseFrom(bytes: ByteArray, registry: ExtensionRegistryLite): $name = PARSER.parseFrom(bytes, registry)")
        appendLine("fun parseFrom(bytes: ByteString): $name { val input = bytes.newCodedInput(); val msg = PARSER.parsePartialFrom(input, ExtensionRegistryLite.getEmptyRegistry()); input.checkLastTagWas(0); if (!msg.isInitialized()) throw UninitializedMessageException().asInvalidProtocolBufferException().setUnfinishedMessage(msg); return msg }")
        appendLine("fun parseFrom(input: CodedInputStream): $name = PARSER.parseFrom(input)")
        appendLine("fun parseFrom(input: CodedInputStream, registry: ExtensionRegistryLite): $name = PARSER.parseFrom(input, registry)")
        for (f in all) appendLine("const val ${f.name.uppercase()}_FIELD_NUMBER = ${f.tag}")
        appendLine("}}")
    }

    private fun StringBuilder.emitExtensions(file: ProtoFile) {
        val names = mutableListOf<String>()
        for (extension in file.extensions) for (f in extension.fields) {
            val target = resolve(extension.target, file.packageDirective).kotlin
            val n = camel(f.name)
            names.add(n)
            val t = type(f, file.packageDirective)
            val repeated = f.rule == "repeated"
            val full = if (repeated) "List<$t>" else t
            appendLine("val $n: GeneratedMessageLite.GeneratedExtension<$target, $full> =")
            append("GeneratedMessageLite.new${if (repeated) "Repeated" else "Singular"}GeneratedExtension(")
            append("$target.defaultInstance, ")
            if (!repeated) append("${default(f, file.packageDirective)}, ")
            append(if (message(f, file.packageDirective)) "$t.defaultInstance, " else "null, ")
            append(if (enumeration(f, file.packageDirective) != null) "$t, " else "null, ")
            append("${f.tag}, WireFormat.FieldType.${wire(f, file.packageDirective)}")
            if (repeated) append(", ${f.isPacked}")
            appendLine(")")
        }
        appendLine("fun registerAllExtensions(registry: ExtensionRegistryLite) {")
        names.forEach { appendLine("registry.add($it)") }
        appendLine("}")
    }
}
