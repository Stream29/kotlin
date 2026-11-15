/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Copyright 2008 Google Inc.
 * Use of this source code is governed by the Apache 2.0 and Protobuf BSD licenses.
 */

package org.jetbrains.kotlin.protobuf

abstract class GeneratedMessageLite : AbstractMessageLite() {
    var unknownFields: ByteString = ByteString.EMPTY
        protected set

    abstract class Builder<MessageType : GeneratedMessageLite, BuilderType : Builder<MessageType, BuilderType>> :
        AbstractMessageLite.Builder<BuilderType>() {

        var unknownFields: ByteString = ByteString.EMPTY
            protected set

        fun setUnknownFields(unknownFields: ByteString): BuilderType {
            this.unknownFields = unknownFields
            @Suppress("UNCHECKED_CAST")
            return this as BuilderType
        }

        abstract override fun clear(): BuilderType
        abstract fun mergeFrom(other: MessageType): BuilderType

        abstract override fun build(): MessageType
        abstract override fun buildPartial(): MessageType

        fun mergeUntyped(other: MessageLite): BuilderType {
            require(defaultInstanceForType::class == other::class)
            @Suppress("UNCHECKED_CAST")
            return mergeFrom(other as MessageType)
        }
    }

    class ExtensionDescriptor(
        val number: Int,
        val type: WireFormat.FieldType,
        val isRepeated: Boolean,
        val isPacked: Boolean,
        val enumTypeMap: Internal.EnumLiteMap<*>? = null
    ) : Comparable<ExtensionDescriptor> {
        override fun compareTo(other: ExtensionDescriptor): Int = number.compareTo(other.number)
    }

    open class GeneratedExtension<ContainingType : ExtendableMessage<ContainingType>, Type>(
        val containingTypeDefaultInstance: ContainingType,
        val defaultValue: Type,
        val messageDefaultInstance: MessageLite?,
        val descriptor: ExtensionDescriptor,
        val singularType: Any? = null
    ) {
        val number: Int get() = descriptor.number
    }

    private class UnmodifiableProtoList<T>(private val delegate: List<T>) : AbstractList<T>(), RandomAccess {
        override val size: Int get() = delegate.size
        override fun get(index: Int): T = delegate[index]
    }

    abstract class ExtendableMessage<MessageType : ExtendableMessage<MessageType>> : GeneratedMessageLite() {

        internal var _extensions: MutableMap<ExtensionDescriptor, Any>? = null

        internal val extensions: MutableMap<ExtensionDescriptor, Any>
            get() = _extensions ?: LinkedHashMap<ExtensionDescriptor, Any>().also { _extensions = it }

        protected fun hasAnyExtensions(): Boolean = !_extensions.isNullOrEmpty()

        protected fun copyExtensionsFromBuilder(builder: ExtendableBuilder<MessageType, *>) {
            val src = builder._extensions
            if (!src.isNullOrEmpty()) {
                val dst = LinkedHashMap<ExtensionDescriptor, Any>(src.size)
                for ((k, v) in src) {
                    dst[k] = if (v is List<*>) unmodifiableProtoList(ArrayList(v)) else v
                }
                _extensions = dst
            }
        }

        protected fun makeExtensionsImmutable() {
            val ext = _extensions ?: return
            for (entry in ext.entries) {
                val v = entry.value
                if (v is MutableList<*>) {
                    entry.setValue(unmodifiableProtoList(v))
                }
            }
        }

        protected fun parseUnknownField(
            input: CodedInputStream,
            unknownFieldsOutput: CodedOutputStream,
            extensionRegistry: ExtensionRegistryLite,
            tag: Int
        ): Boolean = parseUnknownOrExtensionField(
            getExistingExtensions = { _extensions },
            getOrCreateExtensions = { extensions },
            defaultInstanceForType = defaultInstanceForType,
            input = input,
            unknownFieldsOutput = unknownFieldsOutput,
            extensionRegistry = extensionRegistry,
            tag = tag
        )

        fun <Type> hasExtension(extension: GeneratedExtension<MessageType, Type>): Boolean {
            if (extension.descriptor.isRepeated) {
                throw IllegalArgumentException("hasExtension() can only be called on non-repeated fields.")
            }
            return _extensions?.containsKey(extension.descriptor) == true
        }

        fun <Type> getExtensionCount(extension: GeneratedExtension<MessageType, List<Type>>): Int {
            if (!extension.descriptor.isRepeated) {
                throw IllegalArgumentException("getExtensionCount() can only be called on repeated fields.")
            }
            val list = _extensions?.get(extension.descriptor) as? List<*> ?: return 0
            return list.size
        }

        @Suppress("UNCHECKED_CAST")
        fun <Type> getExtension(extension: GeneratedExtension<MessageType, Type>): Type {
            val ext = _extensions
            if (extension.descriptor.isRepeated) {
                val list = ext?.get(extension.descriptor) as? List<*>
                return (list ?: extension.defaultValue) as Type
            }
            val value = ext?.get(extension.descriptor)
            return (value ?: extension.defaultValue) as Type
        }

        @Suppress("UNCHECKED_CAST")
        fun <Type> getExtension(extension: GeneratedExtension<MessageType, List<Type>>, index: Int): Type {
            if (!extension.descriptor.isRepeated) {
                throw IllegalArgumentException("getExtension() with index can only be called on repeated fields.")
            }
            val list = (_extensions?.get(extension.descriptor) as? List<*>) ?: (extension.defaultValue as List<*>)
            return list[index] as Type
        }

        protected fun extensionsAreInitialized(): Boolean {
            val ext = _extensions ?: return true
            for ((descriptor, value) in ext) {
                if (descriptor.type.javaType == WireFormat.JavaType.MESSAGE) {
                    if (descriptor.isRepeated) {
                        for (element in (value as List<*>)) {
                            if (element is MessageLite && !element.isInitialized()) return false
                        }
                    } else {
                        if (value is MessageLite && !value.isInitialized()) return false
                    }
                }
            }
            return true
        }

        inner class ExtensionWriter(private val messageSetWireFormat: Boolean = false) {
            private val sortedEntries: List<Map.Entry<ExtensionDescriptor, Any>> =
                _extensions?.entries?.sortedBy { it.key.number } ?: emptyList()
            private var index = 0

            fun writeUntil(end: Int, output: CodedOutputStream) {
                while (index < sortedEntries.size && sortedEntries[index].key.number < end) {
                    val entry = sortedEntries[index++]
                    writeExtensionField(entry.key, entry.value, output)
                }
            }
        }

        protected fun newExtensionWriter(): ExtensionWriter = ExtensionWriter()

        protected fun extensionsSerializedSize(): Int {
            val ext = _extensions ?: return 0
            var size = 0
            for ((descriptor, value) in ext) {
                size += computeExtensionFieldSize(descriptor, value)
            }
            return size
        }
    }

    abstract class ExtendableBuilder<MessageType : ExtendableMessage<MessageType>, BuilderType : ExtendableBuilder<MessageType, BuilderType>> :
        Builder<MessageType, BuilderType>() {

        internal var _extensions: MutableMap<ExtensionDescriptor, Any>? = null

        internal val extensions: MutableMap<ExtensionDescriptor, Any>
            get() = _extensions ?: LinkedHashMap<ExtensionDescriptor, Any>().also { _extensions = it }

        protected fun clearExtensions() {
            _extensions?.clear()
        }

        fun extensionSnapshot(): Map<ExtensionDescriptor, Any> {
            val ext = _extensions ?: return emptyMap()
            return ext.mapValues { (_, value) -> if (value is List<*>) value.toList() else value }
        }

        fun <Type> hasExtension(extension: GeneratedExtension<MessageType, Type>): Boolean {
            if (extension.descriptor.isRepeated) {
                throw IllegalArgumentException("hasExtension() can only be called on non-repeated fields.")
            }
            return _extensions?.containsKey(extension.descriptor) == true
        }

        fun <Type> getExtensionCount(extension: GeneratedExtension<MessageType, List<Type>>): Int {
            if (!extension.descriptor.isRepeated) {
                throw IllegalArgumentException("getExtensionCount() can only be called on repeated fields.")
            }
            val list = _extensions?.get(extension.descriptor) as? List<*> ?: return 0
            return list.size
        }

        @Suppress("UNCHECKED_CAST")
        fun <Type> getExtension(extension: GeneratedExtension<MessageType, Type>): Type {
            val ext = _extensions
            if (extension.descriptor.isRepeated) {
                val list = ext?.get(extension.descriptor) as? List<*>
                return (list ?: extension.defaultValue) as Type
            }
            val value = ext?.get(extension.descriptor)
            return (value ?: extension.defaultValue) as Type
        }

        @Suppress("UNCHECKED_CAST")
        fun <Type> getExtension(extension: GeneratedExtension<MessageType, List<Type>>, index: Int): Type {
            if (!extension.descriptor.isRepeated) {
                throw IllegalArgumentException("getExtension() with index can only be called on repeated fields.")
            }
            val list = (_extensions?.get(extension.descriptor) as? List<*>) ?: (extension.defaultValue as List<*>)
            return list[index] as Type
        }

        @Suppress("UNCHECKED_CAST")
        fun <Type> setExtension(extension: GeneratedExtension<MessageType, Type>, value: Type): BuilderType {
            if (extension.descriptor.isRepeated) {
                require(value is List<*>)
                extensions[extension.descriptor] = value.map { requireNotNull(it) }.toMutableList()
            } else {
                extensions[extension.descriptor] = value as Any
            }
            return this as BuilderType
        }

        @Suppress("UNCHECKED_CAST")
        fun <Type> setExtension(
            extension: GeneratedExtension<MessageType, List<Type>>,
            index: Int,
            value: Type
        ): BuilderType {
            if (!extension.descriptor.isRepeated) {
                throw IllegalArgumentException("setExtension() with index can only be called on repeated fields.")
            }
            val list = extensions.getOrPut(extension.descriptor) { mutableListOf<Type>() } as MutableList<Type>
            list[index] = value
            return this as BuilderType
        }

        @Suppress("UNCHECKED_CAST")
        fun <Type> addExtension(extension: GeneratedExtension<MessageType, List<Type>>, value: Type): BuilderType {
            if (!extension.descriptor.isRepeated) {
                throw IllegalArgumentException("addExtension() can only be called on repeated fields.")
            }
            val list = extensions.getOrPut(extension.descriptor) { mutableListOf<Type>() } as MutableList<Type>
            list.add(value)
            return this as BuilderType
        }

        @Suppress("UNCHECKED_CAST")
        fun <Type> clearExtension(extension: GeneratedExtension<MessageType, *>): BuilderType {
            _extensions?.remove(extension.descriptor)
            return this as BuilderType
        }

        @Suppress("UNCHECKED_CAST")
        protected fun mergeExtensionFields(other: MessageType) {
            val otherExt = other._extensions ?: return
            for ((descriptor, value) in otherExt) {
                if (descriptor.isRepeated) {
                    val list = extensions.getOrPut(descriptor) { mutableListOf<Any>() } as MutableList<Any>
                    list.addAll(value as List<Any>)
                } else if (descriptor.type.javaType == WireFormat.JavaType.MESSAGE) {
                    val existing = _extensions?.get(descriptor) as? MessageLite
                    if (existing != null) {
                        extensions[descriptor] = (existing.toBuilder() as Builder<*, *>)
                            .mergeUntyped(value as MessageLite).buildPartial()
                    } else {
                        extensions[descriptor] = value
                    }
                } else {
                    extensions[descriptor] = value
                }
            }
        }

        protected fun extensionsAreInitialized(): Boolean {
            val ext = _extensions ?: return true
            for ((descriptor, value) in ext) {
                if (descriptor.type.javaType == WireFormat.JavaType.MESSAGE) {
                    if (descriptor.isRepeated) {
                        for (element in (value as List<*>)) {
                            if (element is MessageLite && !element.isInitialized()) return false
                        }
                    } else {
                        if (value is MessageLite && !value.isInitialized()) return false
                    }
                }
            }
            return true
        }

        protected fun parseUnknownField(
            input: CodedInputStream,
            unknownFieldsOutput: CodedOutputStream,
            extensionRegistry: ExtensionRegistryLite,
            tag: Int
        ): Boolean = parseUnknownOrExtensionField(
            getExistingExtensions = { _extensions },
            getOrCreateExtensions = { extensions },
            defaultInstanceForType = defaultInstanceForType,
            input = input,
            unknownFieldsOutput = unknownFieldsOutput,
            extensionRegistry = extensionRegistry,
            tag = tag
        )
    }

    companion object {
        fun <T> unmodifiableProtoList(list: List<T>): List<T> =
            if (list.isEmpty()) emptyList() else if (list is UnmodifiableProtoList<*>) list else UnmodifiableProtoList(list)

        private fun readExtensionScalar(
            extension: GeneratedExtension<*, *>,
            fieldNumber: Int,
            input: CodedInputStream,
            unknownFieldsOutput: CodedOutputStream
        ): Any? {
            if (extension.descriptor.type != WireFormat.FieldType.ENUM)
                return readSingularValue(input, extension.descriptor.type, extension.descriptor.enumTypeMap)
            val raw = input.readEnum()
            val value = extension.descriptor.enumTypeMap?.findValueByNumber(raw)
            if (value == null) {
                unknownFieldsOutput.writeTag(fieldNumber, WireFormat.WIRETYPE_VARINT)
                unknownFieldsOutput.writeRawVarint32(raw)
            }
            return value
        }

        @Suppress("UNCHECKED_CAST")
        private inline fun parseUnknownOrExtensionField(
            getExistingExtensions: () -> MutableMap<ExtensionDescriptor, Any>?,
            getOrCreateExtensions: () -> MutableMap<ExtensionDescriptor, Any>,
            defaultInstanceForType: MessageLite,
            input: CodedInputStream,
            unknownFieldsOutput: CodedOutputStream,
            extensionRegistry: ExtensionRegistryLite,
            tag: Int
        ): Boolean {
            val wireType = WireFormat.getTagWireType(tag)
            val fieldNumber = WireFormat.getTagFieldNumber(tag)
            val extension = extensionRegistry.findLiteExtensionByNumber(defaultInstanceForType, fieldNumber)
            var unknown = false
            var packed = false
            if (extension == null) {
                unknown = true
            } else if (wireType == extension.descriptor.type.wireType) {
                packed = false
            } else if (extension.descriptor.isRepeated &&
                extension.descriptor.type.javaType != WireFormat.JavaType.MESSAGE &&
                extension.descriptor.type != WireFormat.FieldType.STRING &&
                extension.descriptor.type != WireFormat.FieldType.BYTES &&
                wireType == WireFormat.WIRETYPE_LENGTH_DELIMITED
            ) {
                packed = true
            } else {
                unknown = true
            }

            if (unknown || extension == null) {
                return input.skipField(tag, unknownFieldsOutput)
            }

            if (packed) {
                val length = input.readRawVarint32()
                val limit = input.pushLimit(length)
                val list = getOrCreateExtensions().getOrPut(extension.descriptor) { mutableListOf<Any>() } as MutableList<Any>
                while (input.getBytesUntilLimit() > 0) {
                    val value = readExtensionScalar(extension, fieldNumber, input, unknownFieldsOutput)
                    if (value != null) list.add(value)
                }
                input.popLimit(limit)
            } else {
                val value: Any? = if (extension.descriptor.type.javaType == WireFormat.JavaType.MESSAGE) {
                    var subBuilder: MessageLite.Builder? = null
                    if (!extension.descriptor.isRepeated) {
                        val existing = getExistingExtensions()?.get(extension.descriptor) as? MessageLite
                        if (existing != null) {
                            subBuilder = existing.toBuilder()
                        }
                    }
                    if (subBuilder == null) {
                        subBuilder = (extension.messageDefaultInstance
                            ?: error("Missing messageDefaultInstance for extension ${extension.number}")).newBuilderForType()
                    }
                    if (extension.descriptor.type == WireFormat.FieldType.GROUP)
                        input.readGroup(fieldNumber, subBuilder, extensionRegistry)
                    else input.readMessage(subBuilder, extensionRegistry)
                    subBuilder.build()
                } else {
                    readExtensionScalar(extension, fieldNumber, input, unknownFieldsOutput)
                }

                if (value != null) {
                    if (extension.descriptor.isRepeated) {
                        val list = getOrCreateExtensions().getOrPut(extension.descriptor) { mutableListOf<Any>() } as MutableList<Any>
                        list.add(value)
                    } else {
                        getOrCreateExtensions()[extension.descriptor] = value
                    }
                }
            }
            return true
        }

        fun <ContainingType : ExtendableMessage<ContainingType>, Type> newSingularGeneratedExtension(
            containingTypeDefaultInstance: ContainingType,
            defaultValue: Type,
            messageDefaultInstance: MessageLite?,
            enumTypeMap: Internal.EnumLiteMap<*>?,
            number: Int,
            type: WireFormat.FieldType,
            singularType: Any? = null
        ): GeneratedExtension<ContainingType, Type> =
            GeneratedExtension(
                containingTypeDefaultInstance,
                defaultValue,
                messageDefaultInstance,
                ExtensionDescriptor(number, type, isRepeated = false, isPacked = false, enumTypeMap = enumTypeMap),
                singularType
            )

        fun <ContainingType : ExtendableMessage<ContainingType>, Type> newRepeatedGeneratedExtension(
            containingTypeDefaultInstance: ContainingType,
            messageDefaultInstance: MessageLite?,
            enumTypeMap: Internal.EnumLiteMap<*>?,
            number: Int,
            type: WireFormat.FieldType,
            isPacked: Boolean,
            singularType: Any? = null
        ): GeneratedExtension<ContainingType, List<Type>> =
            GeneratedExtension(
                containingTypeDefaultInstance,
                emptyList(),
                messageDefaultInstance,
                ExtensionDescriptor(number, type, isRepeated = true, isPacked = isPacked, enumTypeMap = enumTypeMap),
                singularType
            )

        internal fun readSingularValue(
            input: CodedInputStream,
            type: WireFormat.FieldType,
            enumMap: Internal.EnumLiteMap<*>?
        ): Any? = when (type) {
            WireFormat.FieldType.DOUBLE -> input.readDouble()
            WireFormat.FieldType.FLOAT -> input.readFloat()
            WireFormat.FieldType.INT64 -> input.readInt64()
            WireFormat.FieldType.UINT64 -> input.readUInt64()
            WireFormat.FieldType.INT32 -> input.readInt32()
            WireFormat.FieldType.FIXED64 -> input.readFixed64()
            WireFormat.FieldType.FIXED32 -> input.readFixed32()
            WireFormat.FieldType.BOOL -> input.readBool()
            WireFormat.FieldType.STRING -> input.readString()
            WireFormat.FieldType.BYTES -> input.readBytes()
            WireFormat.FieldType.UINT32 -> input.readUInt32()
            WireFormat.FieldType.SFIXED32 -> input.readSFixed32()
            WireFormat.FieldType.SFIXED64 -> input.readSFixed64()
            WireFormat.FieldType.SINT32 -> input.readSInt32()
            WireFormat.FieldType.SINT64 -> input.readSInt64()
            WireFormat.FieldType.ENUM -> {
                val raw = input.readEnum()
                if (enumMap == null) raw else enumMap.findValueByNumber(raw)
            }
            WireFormat.FieldType.MESSAGE, WireFormat.FieldType.GROUP ->
                error("readSingularValue called on message type")
        }

        private fun writeSingularValueNoTag(type: WireFormat.FieldType, value: Any, output: CodedOutputStream) {
            when (type) {
                WireFormat.FieldType.DOUBLE -> output.writeRawLittleEndian64((value as Double).toRawBits())
                WireFormat.FieldType.FLOAT -> output.writeRawLittleEndian32((value as Float).toRawBits())
                WireFormat.FieldType.INT64 -> output.writeRawVarint64(value as Long)
                WireFormat.FieldType.UINT64 -> output.writeRawVarint64(value as Long)
                WireFormat.FieldType.INT32 -> output.writeInt32NoTag(value as Int)
                WireFormat.FieldType.FIXED64 -> output.writeRawLittleEndian64(value as Long)
                WireFormat.FieldType.FIXED32 -> output.writeRawLittleEndian32(value as Int)
                WireFormat.FieldType.BOOL -> output.writeRawByte(if (value as Boolean) 1 else 0)
                WireFormat.FieldType.STRING -> output.writeStringNoTag(value as String)
                WireFormat.FieldType.BYTES -> (value as ByteString).writeTo(output)
                WireFormat.FieldType.UINT32 -> output.writeRawVarint32(value as Int)
                WireFormat.FieldType.SFIXED32 -> output.writeRawLittleEndian32(value as Int)
                WireFormat.FieldType.SFIXED64 -> output.writeRawLittleEndian64(value as Long)
                WireFormat.FieldType.SINT32 -> output.writeRawVarint32(CodedOutputStream.encodeZigZag32(value as Int))
                WireFormat.FieldType.SINT64 -> output.writeRawVarint64(CodedOutputStream.encodeZigZag64(value as Long))
                WireFormat.FieldType.ENUM -> {
                    val raw = if (value is Internal.EnumLite) value.number else (value as Int)
                    output.writeInt32NoTag(raw)
                }
                WireFormat.FieldType.MESSAGE, WireFormat.FieldType.GROUP ->
                    error("Cannot write message without length/tag")
            }
        }

        private fun computeSingularValueSizeNoTag(type: WireFormat.FieldType, value: Any): Int = when (type) {
            WireFormat.FieldType.DOUBLE -> 8
            WireFormat.FieldType.FLOAT -> 4
            WireFormat.FieldType.INT64 -> CodedOutputStream.computeRawVarint64Size(value as Long)
            WireFormat.FieldType.UINT64 -> CodedOutputStream.computeRawVarint64Size(value as Long)
            WireFormat.FieldType.INT32 -> CodedOutputStream.computeInt32SizeNoTag(value as Int)
            WireFormat.FieldType.FIXED64 -> 8
            WireFormat.FieldType.FIXED32 -> 4
            WireFormat.FieldType.BOOL -> 1
            WireFormat.FieldType.STRING -> CodedOutputStream.computeStringSizeNoTag(value as String)
            WireFormat.FieldType.BYTES -> {
                val bs = value as ByteString
                CodedOutputStream.computeRawVarint32Size(bs.size()) + bs.size()
            }
            WireFormat.FieldType.UINT32 -> CodedOutputStream.computeRawVarint32Size(value as Int)
            WireFormat.FieldType.SFIXED32 -> 4
            WireFormat.FieldType.SFIXED64 -> 8
            WireFormat.FieldType.SINT32 -> CodedOutputStream.computeRawVarint32Size(CodedOutputStream.encodeZigZag32(value as Int))
            WireFormat.FieldType.SINT64 -> CodedOutputStream.computeRawVarint64Size(CodedOutputStream.encodeZigZag64(value as Long))
            WireFormat.FieldType.ENUM -> {
                val raw = if (value is Internal.EnumLite) value.number else (value as Int)
                CodedOutputStream.computeInt32SizeNoTag(raw)
            }
            WireFormat.FieldType.MESSAGE -> {
                val msg = value as MessageLite
                CodedOutputStream.computeRawVarint32Size(msg.serializedSize) + msg.serializedSize
            }
            WireFormat.FieldType.GROUP -> (value as MessageLite).serializedSize
        }

        private fun writeSingularValue(descriptor: ExtensionDescriptor, value: Any, output: CodedOutputStream) {
            val fieldNumber = descriptor.number
            when (descriptor.type) {
                WireFormat.FieldType.DOUBLE -> output.writeDouble(fieldNumber, value as Double)
                WireFormat.FieldType.FLOAT -> output.writeFloat(fieldNumber, value as Float)
                WireFormat.FieldType.INT64 -> output.writeInt64(fieldNumber, value as Long)
                WireFormat.FieldType.UINT64 -> output.writeUInt64(fieldNumber, value as Long)
                WireFormat.FieldType.INT32 -> output.writeInt32(fieldNumber, value as Int)
                WireFormat.FieldType.FIXED64 -> output.writeFixed64(fieldNumber, value as Long)
                WireFormat.FieldType.FIXED32 -> output.writeFixed32(fieldNumber, value as Int)
                WireFormat.FieldType.BOOL -> output.writeBool(fieldNumber, value as Boolean)
                WireFormat.FieldType.STRING -> output.writeString(fieldNumber, value as String)
                WireFormat.FieldType.BYTES -> output.writeBytes(fieldNumber, value as ByteString)
                WireFormat.FieldType.UINT32 -> output.writeUInt32(fieldNumber, value as Int)
                WireFormat.FieldType.SFIXED32 -> output.writeSFixed32(fieldNumber, value as Int)
                WireFormat.FieldType.SFIXED64 -> output.writeSFixed64(fieldNumber, value as Long)
                WireFormat.FieldType.SINT32 -> output.writeSInt32(fieldNumber, value as Int)
                WireFormat.FieldType.SINT64 -> output.writeSInt64(fieldNumber, value as Long)
                WireFormat.FieldType.ENUM -> {
                    val raw = if (value is Internal.EnumLite) value.number else (value as Int)
                    output.writeEnum(fieldNumber, raw)
                }
                WireFormat.FieldType.MESSAGE -> output.writeMessage(fieldNumber, value as MessageLite)
                WireFormat.FieldType.GROUP -> output.writeGroup(fieldNumber, value as MessageLite)
            }
        }

        private fun computeSingularValueSize(descriptor: ExtensionDescriptor, value: Any): Int {
            val fieldNumber = descriptor.number
            return when (descriptor.type) {
                WireFormat.FieldType.DOUBLE -> CodedOutputStream.computeDoubleSize(fieldNumber, value as Double)
                WireFormat.FieldType.FLOAT -> CodedOutputStream.computeFloatSize(fieldNumber, value as Float)
                WireFormat.FieldType.INT64 -> CodedOutputStream.computeInt64Size(fieldNumber, value as Long)
                WireFormat.FieldType.UINT64 -> CodedOutputStream.computeUInt64Size(fieldNumber, value as Long)
                WireFormat.FieldType.INT32 -> CodedOutputStream.computeInt32Size(fieldNumber, value as Int)
                WireFormat.FieldType.FIXED64 -> CodedOutputStream.computeFixed64Size(fieldNumber, value as Long)
                WireFormat.FieldType.FIXED32 -> CodedOutputStream.computeFixed32Size(fieldNumber, value as Int)
                WireFormat.FieldType.BOOL -> CodedOutputStream.computeBoolSize(fieldNumber, value as Boolean)
                WireFormat.FieldType.STRING -> CodedOutputStream.computeStringSize(fieldNumber, value as String)
                WireFormat.FieldType.BYTES -> CodedOutputStream.computeBytesSize(fieldNumber, value as ByteString)
                WireFormat.FieldType.UINT32 -> CodedOutputStream.computeUInt32Size(fieldNumber, value as Int)
                WireFormat.FieldType.SFIXED32 -> CodedOutputStream.computeSFixed32Size(fieldNumber, value as Int)
                WireFormat.FieldType.SFIXED64 -> CodedOutputStream.computeSFixed64Size(fieldNumber, value as Long)
                WireFormat.FieldType.SINT32 -> CodedOutputStream.computeSInt32Size(fieldNumber, value as Int)
                WireFormat.FieldType.SINT64 -> CodedOutputStream.computeSInt64Size(fieldNumber, value as Long)
                WireFormat.FieldType.ENUM -> {
                    val raw = if (value is Internal.EnumLite) value.number else (value as Int)
                    CodedOutputStream.computeEnumSize(fieldNumber, raw)
                }
                WireFormat.FieldType.MESSAGE -> CodedOutputStream.computeMessageSize(fieldNumber, value as MessageLite)
                WireFormat.FieldType.GROUP -> CodedOutputStream.computeGroupSize(fieldNumber, value as MessageLite)
            }
        }

        internal fun writeExtensionField(descriptor: ExtensionDescriptor, value: Any, output: CodedOutputStream) {
            if (descriptor.isRepeated) {
                val list = value as List<*>
                if (descriptor.isPacked) {
                    if (list.isEmpty()) return
                    output.writeTag(descriptor.number, WireFormat.WIRETYPE_LENGTH_DELIMITED)
                    var dataSize = 0
                    for (item in list) {
                        dataSize += computeSingularValueSizeNoTag(descriptor.type, item!!)
                    }
                    output.writeRawVarint32(dataSize)
                    for (item in list) {
                        writeSingularValueNoTag(descriptor.type, item!!, output)
                    }
                } else {
                    for (item in list) {
                        writeSingularValue(descriptor, item!!, output)
                    }
                }
            } else {
                writeSingularValue(descriptor, value, output)
            }
        }

        internal fun computeExtensionFieldSize(descriptor: ExtensionDescriptor, value: Any): Int {
            if (descriptor.isRepeated) {
                val list = value as List<*>
                if (list.isEmpty()) return 0
                if (descriptor.isPacked) {
                    var dataSize = 0
                    for (item in list) {
                        dataSize += computeSingularValueSizeNoTag(descriptor.type, item!!)
                    }
                    return CodedOutputStream.computeTagSize(descriptor.number) +
                        CodedOutputStream.computeRawVarint32Size(dataSize) + dataSize
                } else {
                    var size = 0
                    for (item in list) {
                        size += computeSingularValueSize(descriptor, item!!)
                    }
                    return size
                }
            } else {
                return computeSingularValueSize(descriptor, value)
            }
        }
    }
}
