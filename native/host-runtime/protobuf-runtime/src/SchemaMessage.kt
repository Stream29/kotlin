package org.jetbrains.kotlin.protobuf

class SchemaField(
    val number: Int,
    val type: WireFormat.FieldType,
    val repeated: Boolean = false,
    val packed: Boolean = false,
    val required: Boolean = false,
    val oneof: String? = null,
    val default: () -> Any,
    val enumMap: Internal.EnumLiteMap<*>? = null
) {
    val descriptor = GeneratedMessageLite.ExtensionDescriptor(number, type, repeated, packed, enumMap)
}

abstract class SchemaMessage<M : SchemaMessage<M>>(
    internal val fields: List<SchemaField>,
    builder: SchemaBuilder<M, *>?
) : GeneratedMessageLite.ExtendableMessage<M>() {
    internal val values: Map<Int, Any> = builder?.values?.mapValues { (_, v) ->
        if (v is List<*>) ImmutableProtoList(v.toList()) else v
    } ?: emptyMap()

    init {
        unknownFields = builder?.unknownFields ?: ByteString.EMPTY
        builder?.extensionSnapshot()?.let { extensions.putAll(it.mapValues { (_, v) ->
            if (v is List<*>) ImmutableProtoList(v) else v
        }) }
    }

    protected fun has(number: Int): Boolean = values.containsKey(number)
    @Suppress("UNCHECKED_CAST")
    protected fun <T> value(number: Int): T = decodeValue(values[number] ?: fields.first { it.number == number }.let {
        if (it.repeated) emptyList<Any>() else it.default()
    }) as T
    protected fun caseNumber(group: String): Int = fields.firstOrNull { it.oneof == group && has(it.number) }?.number ?: 0

    override val serializedSize: Int get() =
        fields.sumOf { f -> values[f.number]?.let { encodedSize(f, it) } ?: 0 } +
            extensionsSerializedSize() + unknownFields.size()

    override fun writeTo(output: CodedOutputStream) {
        val extensionWriter = newExtensionWriter()
        for (field in fields.sortedBy { it.number }) {
            extensionWriter.writeUntil(field.number, output)
            values[field.number]?.let { writeValue(field, it, output) }
        }
        extensionWriter.writeUntil(Int.MAX_VALUE, output)
        output.writeRawBytes(unknownFields.toByteArray())
    }

    override fun isInitialized(): Boolean {
        for (field in fields) {
            val v = values[field.number]
            if (field.required && v == null) return false
            if (v is MessageLite && !v.isInitialized()) return false
            if (v is List<*> && v.any { it is MessageLite && !it.isInitialized() }) return false
        }
        return extensionsAreInitialized()
    }
}

private class ImmutableProtoList<T>(private val items: List<T>) : AbstractList<T>() {
    override val size get() = items.size
    override fun get(index: Int): T = items[index]
}

private class ProtoString(val bytes: ByteString)

private fun decodeValue(value: Any): Any = when (value) {
    is ProtoString -> value.bytes.toStringUtf8()
    is List<*> -> object : AbstractList<Any>() {
        override val size get() = value.size
        override fun get(index: Int): Any = decodeValue(value[index]!!)
    }
    else -> value
}

private fun stringBytes(value: Any): ByteString =
    if (value is ProtoString) value.bytes else ByteString.copyFromUtf8(value as String)

private fun encodedSize(field: SchemaField, value: Any): Int {
    if (field.type != WireFormat.FieldType.STRING)
        return GeneratedMessageLite.computeExtensionFieldSize(field.descriptor, value)
    return if (field.repeated) (value as List<*>).sumOf { CodedOutputStream.computeBytesSize(field.number, stringBytes(it!!)) }
    else CodedOutputStream.computeBytesSize(field.number, stringBytes(value))
}

private fun writeValue(field: SchemaField, value: Any, output: CodedOutputStream) {
    if (field.type != WireFormat.FieldType.STRING) {
        GeneratedMessageLite.writeExtensionField(field.descriptor, value, output)
    } else if (field.repeated) {
        (value as List<*>).forEach { output.writeBytes(field.number, stringBytes(it!!)) }
    } else output.writeBytes(field.number, stringBytes(value))
}

abstract class SchemaBuilder<M : SchemaMessage<M>, B : SchemaBuilder<M, B>>(
    protected val fields: List<SchemaField>
) : GeneratedMessageLite.ExtendableBuilder<M, B>() {
    internal val values = mutableMapOf<Int, Any>()
    @Suppress("UNCHECKED_CAST")
    protected fun self(): B = this as B
    protected fun has(number: Int): Boolean = values.containsKey(number)
    @Suppress("UNCHECKED_CAST")
    protected fun <T> value(number: Int): T = decodeValue(values[number] ?: fields.first { it.number == number }.let {
        if (it.repeated) emptyList<Any>() else it.default()
    }) as T
    protected fun caseNumber(group: String): Int = fields.firstOrNull { it.oneof == group && has(it.number) }?.number ?: 0
    protected fun clearGroup(group: String): B {
        fields.filter { it.oneof == group }.forEach { values.remove(it.number) }
        return self()
    }
    protected fun set(number: Int, v: Any): B {
        fields.first { it.number == number }.oneof?.let { clearGroup(it) }
        values[number] = v
        return self()
    }
    protected fun clear(number: Int): B { values.remove(number); return self() }
    @Suppress("UNCHECKED_CAST")
    protected fun add(number: Int, v: Any): B {
        (values.getOrPut(number) { mutableListOf<Any>() } as MutableList<Any>).add(v)
        return self()
    }
    @Suppress("UNCHECKED_CAST")
    protected fun replace(number: Int, index: Int, v: Any): B {
        (values[number] as? MutableList<Any> ?: throw IndexOutOfBoundsException())[index] = v
        return self()
    }
    protected fun merge(number: Int, v: MessageLite): B {
        val old = values[number] as? MessageLite
        return set(number, if (old == null) v else
            (old.toBuilder() as GeneratedMessageLite.Builder<*, *>).mergeUntyped(v).buildPartial())
    }

    override fun clear(): B {
        values.clear(); extensions.clear(); unknownFields = ByteString.EMPTY
        return self()
    }
    override fun isInitialized(): Boolean = buildPartial().isInitialized()
    override fun build(): M = buildPartial().also { if (!it.isInitialized()) throw UninitializedMessageException() }
    override fun clone(): B {
        @Suppress("UNCHECKED_CAST")
        return (defaultInstanceForType.newBuilderForType() as B).mergeFrom(buildPartial())
    }
    override fun mergeFrom(other: M): B {
        for (field in fields) {
            val v = other.values[field.number] ?: continue
            when {
                field.repeated -> (v as List<*>).forEach { add(field.number, it!!) }
                v is MessageLite -> merge(field.number, v)
                else -> set(field.number, v)
            }
        }
        mergeExtensionFields(other)
        appendUnknown(other.unknownFields)
        return self()
    }
    private fun appendUnknown(bytes: ByteString) {
        if (!bytes.isEmpty()) unknownFields = ByteString.copyFrom(unknownFields.toByteArray() + bytes.toByteArray())
    }
    override fun mergeFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): B {
        val unknown = ByteString.newOutput()
        val output = CodedOutputStream.newInstance(unknown)
        try {
            while (true) {
                val tag = input.readTag()
                if (tag == 0) break
                val field = fields.firstOrNull { it.number == WireFormat.getTagFieldNumber(tag) }
                val wire = WireFormat.getTagWireType(tag)
                val packed = field != null && field.repeated && wire == WireFormat.WIRETYPE_LENGTH_DELIMITED &&
                    field.type.wireType != WireFormat.WIRETYPE_LENGTH_DELIMITED
                if (field == null || (!packed && field.type.wireType != wire)) {
                    if (!parseUnknownField(input, output, extensionRegistry, tag)) break
                    continue
                }
                fun readOne() {
                    val v: Any? = when (field.type) {
                        WireFormat.FieldType.STRING -> ProtoString(input.readBytes())
                        WireFormat.FieldType.MESSAGE -> {
                            val builder = (field.default() as MessageLite).newBuilderForType()
                            input.readMessage(builder, extensionRegistry)
                            builder.buildPartial()
                        }
                        WireFormat.FieldType.ENUM -> {
                            val raw = input.readEnum()
                            field.enumMap!!.findValueByNumber(raw).also {
                                if (it == null) {
                                    output.writeTag(field.number, WireFormat.WIRETYPE_VARINT)
                                    output.writeRawVarint32(raw)
                                }
                            }
                        }
                        else -> GeneratedMessageLite.readSingularValue(input, field.type, field.enumMap)
                    }
                    if (v != null) {
                        if (field.repeated) add(field.number, v)
                        else if (v is MessageLite) merge(field.number, v)
                        else set(field.number, v)
                    }
                }
                if (packed) {
                    val oldLimit = input.pushLimit(input.readRawVarint32())
                    try { while (input.getBytesUntilLimit() > 0) readOne() }
                    finally { input.popLimit(oldLimit) }
                } else readOne()
            }
        } finally {
            output.flush()
            appendUnknown(unknown.toByteString())
        }
        return self()
    }
}
