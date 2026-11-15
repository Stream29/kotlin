// Generated from the fixed Kotlin protobuf schemas; do not edit.
// Licensed under Apache-2.0; see ../upstream-license/LICENSE.txt.
@file:Suppress("UNCHECKED_CAST", "UNUSED_PARAMETER", "RemoveRedundantQualifierName", "RedundantVisibilityModifier")
package org.jetbrains.kotlin.metadata
import org.jetbrains.kotlin.protobuf.*
object ProtoBuf {
enum class Modality(override val number: Int) : Internal.EnumLite {
FINAL(0),
OPEN(1),
ABSTRACT(2),
SEALED(3),
; companion object : Internal.EnumLiteMap<Modality> {
override fun findValueByNumber(number: Int): Modality? = valueOf(number)
fun valueOf(number: Int): Modality? = when (number) {
0 -> FINAL
1 -> OPEN
2 -> ABSTRACT
3 -> SEALED
else -> null
}
fun internalGetValueMap(): Internal.EnumLiteMap<Modality> = this
}}
enum class Visibility(override val number: Int) : Internal.EnumLite {
INTERNAL(0),
PRIVATE(1),
PROTECTED(2),
PUBLIC(3),
PRIVATE_TO_THIS(4),
LOCAL(5),
; companion object : Internal.EnumLiteMap<Visibility> {
override fun findValueByNumber(number: Int): Visibility? = valueOf(number)
fun valueOf(number: Int): Visibility? = when (number) {
0 -> INTERNAL
1 -> PRIVATE
2 -> PROTECTED
3 -> PUBLIC
4 -> PRIVATE_TO_THIS
5 -> LOCAL
else -> null
}
fun internalGetValueMap(): Internal.EnumLiteMap<Visibility> = this
}}
enum class MemberKind(override val number: Int) : Internal.EnumLite {
DECLARATION(0),
FAKE_OVERRIDE(1),
DELEGATION(2),
SYNTHESIZED(3),
; companion object : Internal.EnumLiteMap<MemberKind> {
override fun findValueByNumber(number: Int): MemberKind? = valueOf(number)
fun valueOf(number: Int): MemberKind? = when (number) {
0 -> DECLARATION
1 -> FAKE_OVERRIDE
2 -> DELEGATION
3 -> SYNTHESIZED
else -> null
}
fun internalGetValueMap(): Internal.EnumLiteMap<MemberKind> = this
}}
enum class ReturnValueStatus(override val number: Int) : Internal.EnumLite {
UNSPECIFIED(0),
MUST_USE(1),
EXPLICITLY_IGNORABLE(2),
; companion object : Internal.EnumLiteMap<ReturnValueStatus> {
override fun findValueByNumber(number: Int): ReturnValueStatus? = valueOf(number)
fun valueOf(number: Int): ReturnValueStatus? = when (number) {
0 -> UNSPECIFIED
1 -> MUST_USE
2 -> EXPLICITLY_IGNORABLE
else -> null
}
fun internalGetValueMap(): Internal.EnumLiteMap<ReturnValueStatus> = this
}}
class StringTable : GeneratedMessageLite.ExtendableMessage<StringTable> {
internal var field_1_: LazyStringList = LazyStringArrayList.EMPTY
private var memoizedSerializedSize: Int = -1
private constructor()
private constructor(builder: Builder) {
this.unknownFields = builder.unknownFields
}
private constructor(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite) {
var mutBit0_ = 0
var unknownBuffer: ByteString.Output? = null
var unknownOut: CodedOutputStream? = null
fun unknownOutput(): CodedOutputStream {
var out = unknownOut
if (out == null) { val buf = ByteString.newOutput(); unknownBuffer = buf; out = CodedOutputStream.newInstance(buf); unknownOut = out }
return out
}
try {
var done = false
while (!done) {
val tag = input.readTag()
when (tag) {
0 -> done = true
10 -> {
val bs = input.readBytes()
if ((mutBit0_ and 1) == 0) { field_1_ = LazyStringArrayList(); mutBit0_ = mutBit0_ or 1 }
field_1_.add(bs)
}
else -> if (!input.skipField(tag, unknownOutput())) done = true
}}
} catch (e: InvalidProtocolBufferException) {
throw e.setUnfinishedMessage(this)
} finally {
if ((mutBit0_ and 1) != 0) field_1_ = field_1_.getUnmodifiableView()
if (unknownBuffer != null) { unknownOut!!.flush(); unknownFields = unknownBuffer.toByteString() }
}}
val `stringList`: List<String> get() = field_1_
val `stringCount`: Int get() = field_1_.size
fun getStringList(): List<String> = field_1_
fun getStringCount(): Int = field_1_.size
fun getString(index: Int): String = field_1_[index]
override fun isInitialized(): Boolean = true
override fun writeTo(output: CodedOutputStream) {
for (i in 0 until field_1_.size) {
val elem = field_1_.getUnderlying(i)
output.writeRawVarint32(10)
if (elem is ByteString) output.writeBytesNoTag(elem) else output.writeStringNoTag(elem as String)
}
if (!unknownFields.isEmpty()) unknownFields.writeTo(output)
}
override val serializedSize: Int get() {
val cached = memoizedSerializedSize
if (cached != -1) return cached
var size = 0
if (field_1_.isNotEmpty()) {
var dataSize = 0
for (i in 0 until field_1_.size) {
val elem = field_1_.getUnderlying(i)
dataSize += if (elem is ByteString) CodedOutputStream.computeBytesSizeNoTag(elem) else CodedOutputStream.computeStringSizeNoTag(elem as String)
}
size += dataSize + 1 * field_1_.size
}
size += unknownFields.size()
memoizedSerializedSize = size
return size
}
override val defaultInstanceForType: StringTable get() = defaultInstance
override fun newBuilderForType(): Builder = Builder()
override fun toBuilder(): Builder = Builder().mergeFrom(this)
class Builder : GeneratedMessageLite.ExtendableBuilder<StringTable, Builder>() {
private var mutBitField0_: Int = 0
private var field_1_: LazyStringList = LazyStringArrayList.EMPTY
private fun ensure_1_Mutable() { if ((mutBitField0_ and 1) == 0) { field_1_ = LazyStringArrayList(field_1_); mutBitField0_ = mutBitField0_ or 1 } }
override val defaultInstanceForType: StringTable get() = defaultInstance
override fun clear(): Builder {
mutBitField0_ = 0
field_1_ = LazyStringArrayList.EMPTY
unknownFields = ByteString.EMPTY
return this
}
override fun clone(): Builder = Builder().mergeFrom(buildPartial())
override fun build(): StringTable {
val result = buildPartial()
return result
}
override fun buildPartial(): StringTable {
val result = StringTable(this)
if ((mutBitField0_ and 1) != 0) { field_1_ = field_1_.getUnmodifiableView(); mutBitField0_ = mutBitField0_ and -2 }
result.field_1_ = field_1_
return result
}
override fun mergeFrom(other: StringTable): Builder {
if (other === defaultInstance) return this
if (other.field_1_.isNotEmpty()) {
if (field_1_.isEmpty()) { field_1_ = other.field_1_; mutBitField0_ = mutBitField0_ and -2 } else { ensure_1_Mutable(); (field_1_ as MutableList<Any>).addAll(other.field_1_ as Collection<Any>) }
}
unknownFields = unknownFields.concat(other.unknownFields)
return this
}
override fun isInitialized(): Boolean = true
override fun mergeFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Builder {
var parsedMessage: StringTable? = null
try {
parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry)
} catch (e: InvalidProtocolBufferException) {
parsedMessage = e.unfinishedMessage as? StringTable
throw e
} finally {
if (parsedMessage != null) mergeFrom(parsedMessage)
}
return this
}
val `stringList`: List<String> get() = if (field_1_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_1_)
val `stringCount`: Int get() = field_1_.size
fun getStringList(): List<String> = `stringList`
fun getStringCount(): Int = field_1_.size
fun getString(index: Int): String = field_1_[index]
fun addString(value: String): Builder { ensure_1_Mutable(); (field_1_ as LazyStringArrayList).add(value); return this }
fun setString(index: Int, value: String): Builder { ensure_1_Mutable(); (field_1_ as LazyStringArrayList)[index] = value; return this }
fun addAllString(values: Iterable<String>): Builder { ensure_1_Mutable(); AbstractMessageLite.Builder.addAll(values, field_1_ as LazyStringArrayList); return this }
fun clearString(): Builder { field_1_ = LazyStringArrayList.EMPTY; mutBitField0_ = mutBitField0_ and -2; return this }
}
companion object {
val defaultInstance: StringTable = StringTable()
fun getDefaultInstance(): StringTable = defaultInstance
fun newBuilder(): Builder = Builder()
fun newBuilder(prototype: StringTable): Builder = Builder().mergeFrom(prototype)
val PARSER: Parser<StringTable> = object : AbstractParser<StringTable>() {
override fun parsePartialFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): StringTable =
StringTable(input, extensionRegistry)
}
fun parseFrom(bytes: ByteArray): StringTable = PARSER.parseFrom(bytes)
fun parseFrom(bytes: ByteArray, registry: ExtensionRegistryLite): StringTable = PARSER.parseFrom(bytes, registry)
fun parseFrom(bytes: ByteString): StringTable { val input = bytes.newCodedInput(); val msg = PARSER.parsePartialFrom(input, ExtensionRegistryLite.getEmptyRegistry()); input.checkLastTagWas(0); if (!msg.isInitialized()) throw UninitializedMessageException().asInvalidProtocolBufferException().setUnfinishedMessage(msg); return msg }
fun parseFrom(input: CodedInputStream): StringTable = PARSER.parseFrom(input)
fun parseFrom(input: CodedInputStream, registry: ExtensionRegistryLite): StringTable = PARSER.parseFrom(input, registry)
const val STRING_FIELD_NUMBER = 1
}}
class QualifiedNameTable : GeneratedMessageLite.ExtendableMessage<QualifiedNameTable> {
class QualifiedName : GeneratedMessageLite.ExtendableMessage<QualifiedName> {
enum class Kind(override val number: Int) : Internal.EnumLite {
CLASS(0),
PACKAGE(1),
LOCAL(2),
; companion object : Internal.EnumLiteMap<Kind> {
override fun findValueByNumber(number: Int): Kind? = valueOf(number)
fun valueOf(number: Int): Kind? = when (number) {
0 -> CLASS
1 -> PACKAGE
2 -> LOCAL
else -> null
}
fun internalGetValueMap(): Internal.EnumLiteMap<Kind> = this
}}
internal var bitField0_: Int = 0
internal var field_1_: Int = -1
internal var field_2_: Int = 0
internal var field_3_: org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.QualifiedName.Kind = org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.QualifiedName.Kind.PACKAGE
private var memoizedIsInitialized: Byte = -1
private var memoizedSerializedSize: Int = -1
private constructor()
private constructor(builder: Builder) {
this.unknownFields = builder.unknownFields
}
private constructor(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite) {
var unknownBuffer: ByteString.Output? = null
var unknownOut: CodedOutputStream? = null
fun unknownOutput(): CodedOutputStream {
var out = unknownOut
if (out == null) { val buf = ByteString.newOutput(); unknownBuffer = buf; out = CodedOutputStream.newInstance(buf); unknownOut = out }
return out
}
try {
var done = false
while (!done) {
val tag = input.readTag()
when (tag) {
0 -> done = true
8 -> { bitField0_ = bitField0_ or 1; field_1_ = input.readInt32() }
16 -> { bitField0_ = bitField0_ or 2; field_2_ = input.readInt32() }
24 -> {
val raw = input.readEnum()
val parsed = org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.QualifiedName.Kind.valueOf(raw)
if (parsed == null) { unknownOutput().writeRawVarint32(24); unknownOutput().writeRawVarint32(raw) } else {
bitField0_ = bitField0_ or 4; field_3_ = parsed
}}
else -> if (!input.skipField(tag, unknownOutput())) done = true
}}
} catch (e: InvalidProtocolBufferException) {
throw e.setUnfinishedMessage(this)
} finally {
if (unknownBuffer != null) { unknownOut!!.flush(); unknownFields = unknownBuffer.toByteString() }
}}
val `parentQualifiedName`: Int get() = field_1_
fun getParentQualifiedName(): Int = `parentQualifiedName`
fun hasParentQualifiedName(): Boolean = (bitField0_ and 1) != 0
val `shortName`: Int get() = field_2_
fun getShortName(): Int = `shortName`
fun hasShortName(): Boolean = (bitField0_ and 2) != 0
val `kind`: org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.QualifiedName.Kind get() = field_3_
fun getKind(): org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.QualifiedName.Kind = `kind`
fun hasKind(): Boolean = (bitField0_ and 4) != 0
override fun isInitialized(): Boolean {
val isInitialized = memoizedIsInitialized.toInt()
if (isInitialized == 1) return true
if (isInitialized == 0) return false
if ((bitField0_ and 2) == 0) { memoizedIsInitialized = 0; return false }
memoizedIsInitialized = 1
return true
}
override fun writeTo(output: CodedOutputStream) {
if ((bitField0_ and 1) != 0) { output.writeRawVarint32(8); output.writeInt32NoTag(field_1_) }
if ((bitField0_ and 2) != 0) { output.writeRawVarint32(16); output.writeInt32NoTag(field_2_) }
if ((bitField0_ and 4) != 0) { output.writeRawVarint32(24); output.writeInt32NoTag((field_3_).number) }
if (!unknownFields.isEmpty()) unknownFields.writeTo(output)
}
override val serializedSize: Int get() {
val cached = memoizedSerializedSize
if (cached != -1) return cached
var size = 0
if ((bitField0_ and 1) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_1_)
if ((bitField0_ and 2) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_2_)
if ((bitField0_ and 4) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag((field_3_).number)
size += unknownFields.size()
memoizedSerializedSize = size
return size
}
override val defaultInstanceForType: QualifiedName get() = defaultInstance
override fun newBuilderForType(): Builder = Builder()
override fun toBuilder(): Builder = Builder().mergeFrom(this)
class Builder : GeneratedMessageLite.ExtendableBuilder<QualifiedName, Builder>() {
private var bitField0_: Int = 0
private var field_1_: Int = -1
private var field_2_: Int = 0
private var field_3_: org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.QualifiedName.Kind = org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.QualifiedName.Kind.PACKAGE
override val defaultInstanceForType: QualifiedName get() = defaultInstance
override fun clear(): Builder {
bitField0_ = 0
field_1_ = -1
field_2_ = 0
field_3_ = org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.QualifiedName.Kind.PACKAGE
unknownFields = ByteString.EMPTY
return this
}
override fun clone(): Builder = Builder().mergeFrom(buildPartial())
override fun build(): QualifiedName {
val result = buildPartial()
if (!result.isInitialized()) throw UninitializedMessageException()
return result
}
override fun buildPartial(): QualifiedName {
val result = QualifiedName(this)
val from_bitField0_ = bitField0_
var to_bitField0_ = 0
if ((from_bitField0_ and 1) != 0) to_bitField0_ = to_bitField0_ or 1
result.field_1_ = field_1_
if ((from_bitField0_ and 2) != 0) to_bitField0_ = to_bitField0_ or 2
result.field_2_ = field_2_
if ((from_bitField0_ and 4) != 0) to_bitField0_ = to_bitField0_ or 4
result.field_3_ = field_3_
result.bitField0_ = to_bitField0_
return result
}
override fun mergeFrom(other: QualifiedName): Builder {
if (other === defaultInstance) return this
if (other.hasParentQualifiedName()) setParentQualifiedName(other.getParentQualifiedName())
if (other.hasShortName()) setShortName(other.getShortName())
if (other.hasKind()) setKind(other.getKind())
unknownFields = unknownFields.concat(other.unknownFields)
return this
}
override fun isInitialized(): Boolean {
if ((bitField0_ and 2) == 0) return false
return true
}
override fun mergeFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Builder {
var parsedMessage: QualifiedName? = null
try {
parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry)
} catch (e: InvalidProtocolBufferException) {
parsedMessage = e.unfinishedMessage as? QualifiedName
throw e
} finally {
if (parsedMessage != null) mergeFrom(parsedMessage)
}
return this
}
var `parentQualifiedName`: Int
get() = field_1_
set(value) { bitField0_ = bitField0_ or 1; field_1_ = value }
fun getParentQualifiedName(): Int = field_1_
fun hasParentQualifiedName(): Boolean = (bitField0_ and 1) != 0
fun setParentQualifiedName(value: Int): Builder { bitField0_ = bitField0_ or 1; field_1_ = value; return this }
fun clearParentQualifiedName(): Builder { bitField0_ = bitField0_ and -2; field_1_ = -1; return this }
var `shortName`: Int
get() = field_2_
set(value) { bitField0_ = bitField0_ or 2; field_2_ = value }
fun getShortName(): Int = field_2_
fun hasShortName(): Boolean = (bitField0_ and 2) != 0
fun setShortName(value: Int): Builder { bitField0_ = bitField0_ or 2; field_2_ = value; return this }
fun clearShortName(): Builder { bitField0_ = bitField0_ and -3; field_2_ = 0; return this }
var `kind`: org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.QualifiedName.Kind
get() = field_3_
set(value) { bitField0_ = bitField0_ or 4; field_3_ = value }
fun getKind(): org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.QualifiedName.Kind = field_3_
fun hasKind(): Boolean = (bitField0_ and 4) != 0
fun setKind(value: org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.QualifiedName.Kind): Builder { bitField0_ = bitField0_ or 4; field_3_ = value; return this }
fun clearKind(): Builder { bitField0_ = bitField0_ and -5; field_3_ = org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.QualifiedName.Kind.PACKAGE; return this }
}
companion object {
val defaultInstance: QualifiedName = QualifiedName()
fun getDefaultInstance(): QualifiedName = defaultInstance
fun newBuilder(): Builder = Builder()
fun newBuilder(prototype: QualifiedName): Builder = Builder().mergeFrom(prototype)
val PARSER: Parser<QualifiedName> = object : AbstractParser<QualifiedName>() {
override fun parsePartialFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): QualifiedName =
QualifiedName(input, extensionRegistry)
}
fun parseFrom(bytes: ByteArray): QualifiedName = PARSER.parseFrom(bytes)
fun parseFrom(bytes: ByteArray, registry: ExtensionRegistryLite): QualifiedName = PARSER.parseFrom(bytes, registry)
fun parseFrom(bytes: ByteString): QualifiedName { val input = bytes.newCodedInput(); val msg = PARSER.parsePartialFrom(input, ExtensionRegistryLite.getEmptyRegistry()); input.checkLastTagWas(0); if (!msg.isInitialized()) throw UninitializedMessageException().asInvalidProtocolBufferException().setUnfinishedMessage(msg); return msg }
fun parseFrom(input: CodedInputStream): QualifiedName = PARSER.parseFrom(input)
fun parseFrom(input: CodedInputStream, registry: ExtensionRegistryLite): QualifiedName = PARSER.parseFrom(input, registry)
const val PARENT_QUALIFIED_NAME_FIELD_NUMBER = 1
const val SHORT_NAME_FIELD_NUMBER = 2
const val KIND_FIELD_NUMBER = 3
}}
internal var field_1_: List<org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.QualifiedName> = emptyList()
private var memoizedIsInitialized: Byte = -1
private var memoizedSerializedSize: Int = -1
private constructor()
private constructor(builder: Builder) {
this.unknownFields = builder.unknownFields
}
private constructor(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite) {
var mutBit0_ = 0
var unknownBuffer: ByteString.Output? = null
var unknownOut: CodedOutputStream? = null
fun unknownOutput(): CodedOutputStream {
var out = unknownOut
if (out == null) { val buf = ByteString.newOutput(); unknownBuffer = buf; out = CodedOutputStream.newInstance(buf); unknownOut = out }
return out
}
try {
var done = false
while (!done) {
val tag = input.readTag()
when (tag) {
0 -> done = true
10 -> {
if ((mutBit0_ and 1) == 0) { field_1_ = ArrayList(); mutBit0_ = mutBit0_ or 1 }
(field_1_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.QualifiedName>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.QualifiedName.PARSER, extensionRegistry))
}
else -> if (!input.skipField(tag, unknownOutput())) done = true
}}
} catch (e: InvalidProtocolBufferException) {
throw e.setUnfinishedMessage(this)
} finally {
if ((mutBit0_ and 1) != 0) field_1_ = GeneratedMessageLite.unmodifiableProtoList(field_1_)
if (unknownBuffer != null) { unknownOut!!.flush(); unknownFields = unknownBuffer.toByteString() }
}}
val `qualifiedNameList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.QualifiedName> get() = field_1_
val `qualifiedNameCount`: Int get() = field_1_.size
fun getQualifiedNameList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.QualifiedName> = field_1_
fun getQualifiedNameCount(): Int = field_1_.size
fun getQualifiedName(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.QualifiedName = field_1_[index]
override fun isInitialized(): Boolean {
val isInitialized = memoizedIsInitialized.toInt()
if (isInitialized == 1) return true
if (isInitialized == 0) return false
for (i in 0 until field_1_.size) { if (!field_1_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
memoizedIsInitialized = 1
return true
}
override fun writeTo(output: CodedOutputStream) {
for (i in 0 until field_1_.size) { output.writeRawVarint32(10); output.writeMessageNoTag(field_1_[i]) }
if (!unknownFields.isEmpty()) unknownFields.writeTo(output)
}
override val serializedSize: Int get() {
val cached = memoizedSerializedSize
if (cached != -1) return cached
var size = 0
for (i in 0 until field_1_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_1_[i]) }
size += unknownFields.size()
memoizedSerializedSize = size
return size
}
override val defaultInstanceForType: QualifiedNameTable get() = defaultInstance
override fun newBuilderForType(): Builder = Builder()
override fun toBuilder(): Builder = Builder().mergeFrom(this)
class Builder : GeneratedMessageLite.ExtendableBuilder<QualifiedNameTable, Builder>() {
private var mutBitField0_: Int = 0
private var field_1_: List<org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.QualifiedName> = emptyList()
private fun ensure_1_Mutable() { if ((mutBitField0_ and 1) == 0) { field_1_ = ArrayList(field_1_); mutBitField0_ = mutBitField0_ or 1 } }
override val defaultInstanceForType: QualifiedNameTable get() = defaultInstance
override fun clear(): Builder {
mutBitField0_ = 0
field_1_ = emptyList()
unknownFields = ByteString.EMPTY
return this
}
override fun clone(): Builder = Builder().mergeFrom(buildPartial())
override fun build(): QualifiedNameTable {
val result = buildPartial()
if (!result.isInitialized()) throw UninitializedMessageException()
return result
}
override fun buildPartial(): QualifiedNameTable {
val result = QualifiedNameTable(this)
if ((mutBitField0_ and 1) != 0) { field_1_ = GeneratedMessageLite.unmodifiableProtoList(field_1_); mutBitField0_ = mutBitField0_ and -2 }
result.field_1_ = field_1_
return result
}
override fun mergeFrom(other: QualifiedNameTable): Builder {
if (other === defaultInstance) return this
if (other.field_1_.isNotEmpty()) {
if (field_1_.isEmpty()) { field_1_ = other.field_1_; mutBitField0_ = mutBitField0_ and -2 } else { ensure_1_Mutable(); (field_1_ as MutableList<Any>).addAll(other.field_1_ as Collection<Any>) }
}
unknownFields = unknownFields.concat(other.unknownFields)
return this
}
override fun isInitialized(): Boolean {
for (i in 0 until field_1_.size) { if (!field_1_[i].isInitialized()) return false }
return true
}
override fun mergeFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Builder {
var parsedMessage: QualifiedNameTable? = null
try {
parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry)
} catch (e: InvalidProtocolBufferException) {
parsedMessage = e.unfinishedMessage as? QualifiedNameTable
throw e
} finally {
if (parsedMessage != null) mergeFrom(parsedMessage)
}
return this
}
val `qualifiedNameList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.QualifiedName> get() = if (field_1_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_1_)
val `qualifiedNameCount`: Int get() = field_1_.size
fun getQualifiedNameList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.QualifiedName> = `qualifiedNameList`
fun getQualifiedNameCount(): Int = field_1_.size
fun getQualifiedName(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.QualifiedName = field_1_[index]
fun addQualifiedName(value: org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.QualifiedName): Builder { ensure_1_Mutable(); (field_1_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.QualifiedName>).add(value); return this }
fun setQualifiedName(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.QualifiedName): Builder { ensure_1_Mutable(); (field_1_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.QualifiedName>)[index] = value; return this }
fun addAllQualifiedName(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.QualifiedName>): Builder { ensure_1_Mutable(); AbstractMessageLite.Builder.addAll(values, field_1_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.QualifiedName>); return this }
fun clearQualifiedName(): Builder { field_1_ = emptyList(); mutBitField0_ = mutBitField0_ and -2; return this }
fun addQualifiedName(value: org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.QualifiedName.Builder): Builder = addQualifiedName(value.build())
fun setQualifiedName(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.QualifiedName.Builder): Builder = setQualifiedName(index, value.build())
}
companion object {
val defaultInstance: QualifiedNameTable = QualifiedNameTable()
fun getDefaultInstance(): QualifiedNameTable = defaultInstance
fun newBuilder(): Builder = Builder()
fun newBuilder(prototype: QualifiedNameTable): Builder = Builder().mergeFrom(prototype)
val PARSER: Parser<QualifiedNameTable> = object : AbstractParser<QualifiedNameTable>() {
override fun parsePartialFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): QualifiedNameTable =
QualifiedNameTable(input, extensionRegistry)
}
fun parseFrom(bytes: ByteArray): QualifiedNameTable = PARSER.parseFrom(bytes)
fun parseFrom(bytes: ByteArray, registry: ExtensionRegistryLite): QualifiedNameTable = PARSER.parseFrom(bytes, registry)
fun parseFrom(bytes: ByteString): QualifiedNameTable { val input = bytes.newCodedInput(); val msg = PARSER.parsePartialFrom(input, ExtensionRegistryLite.getEmptyRegistry()); input.checkLastTagWas(0); if (!msg.isInitialized()) throw UninitializedMessageException().asInvalidProtocolBufferException().setUnfinishedMessage(msg); return msg }
fun parseFrom(input: CodedInputStream): QualifiedNameTable = PARSER.parseFrom(input)
fun parseFrom(input: CodedInputStream, registry: ExtensionRegistryLite): QualifiedNameTable = PARSER.parseFrom(input, registry)
const val QUALIFIED_NAME_FIELD_NUMBER = 1
}}
class Annotation : GeneratedMessageLite.ExtendableMessage<Annotation> {
class Argument : GeneratedMessageLite.ExtendableMessage<Argument> {
class Value : GeneratedMessageLite.ExtendableMessage<Value> {
enum class Type(override val number: Int) : Internal.EnumLite {
BYTE(0),
CHAR(1),
SHORT(2),
INT(3),
LONG(4),
FLOAT(5),
DOUBLE(6),
BOOLEAN(7),
STRING(8),
CLASS(9),
ENUM(10),
ANNOTATION(11),
ARRAY(12),
; companion object : Internal.EnumLiteMap<Type> {
override fun findValueByNumber(number: Int): Type? = valueOf(number)
fun valueOf(number: Int): Type? = when (number) {
0 -> BYTE
1 -> CHAR
2 -> SHORT
3 -> INT
4 -> LONG
5 -> FLOAT
6 -> DOUBLE
7 -> BOOLEAN
8 -> STRING
9 -> CLASS
10 -> ENUM
11 -> ANNOTATION
12 -> ARRAY
else -> null
}
fun internalGetValueMap(): Internal.EnumLiteMap<Type> = this
}}
internal var bitField0_: Int = 0
internal var field_1_: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value.Type = org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value.Type.BYTE
internal var field_2_: Long = 0L
internal var field_3_: Float = 0.0f
internal var field_4_: Double = 0.0
internal var field_5_: Int = 0
internal var field_6_: Int = 0
internal var field_7_: Int = 0
internal var field_8_: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation? = null
internal var field_11_: Int = 0
internal var field_10_: Int = 0
internal var field_9_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value> = emptyList()
private var memoizedIsInitialized: Byte = -1
private var memoizedSerializedSize: Int = -1
private constructor()
private constructor(builder: Builder) {
this.unknownFields = builder.unknownFields
}
private constructor(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite) {
var mutBit0_ = 0
var unknownBuffer: ByteString.Output? = null
var unknownOut: CodedOutputStream? = null
fun unknownOutput(): CodedOutputStream {
var out = unknownOut
if (out == null) { val buf = ByteString.newOutput(); unknownBuffer = buf; out = CodedOutputStream.newInstance(buf); unknownOut = out }
return out
}
try {
var done = false
while (!done) {
val tag = input.readTag()
when (tag) {
0 -> done = true
8 -> {
val raw = input.readEnum()
val parsed = org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value.Type.valueOf(raw)
if (parsed == null) { unknownOutput().writeRawVarint32(8); unknownOutput().writeRawVarint32(raw) } else {
bitField0_ = bitField0_ or 1; field_1_ = parsed
}}
16 -> { bitField0_ = bitField0_ or 2; field_2_ = input.readSInt64() }
29 -> { bitField0_ = bitField0_ or 4; field_3_ = input.readFloat() }
33 -> { bitField0_ = bitField0_ or 8; field_4_ = input.readDouble() }
40 -> { bitField0_ = bitField0_ or 16; field_5_ = input.readInt32() }
48 -> { bitField0_ = bitField0_ or 32; field_6_ = input.readInt32() }
56 -> { bitField0_ = bitField0_ or 64; field_7_ = input.readInt32() }
66 -> {
var subBuilder: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Builder? = null
if ((bitField0_ and 128) != 0) subBuilder = field_8_?.toBuilder()
val parsed = input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.PARSER, extensionRegistry)
field_8_ = if (subBuilder != null) subBuilder.mergeFrom(parsed).buildPartial() else parsed
bitField0_ = bitField0_ or 128
}
74 -> {
if ((mutBit0_ and 1) == 0) { field_9_ = ArrayList(); mutBit0_ = mutBit0_ or 1 }
(field_9_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value.PARSER, extensionRegistry))
}
80 -> { bitField0_ = bitField0_ or 512; field_10_ = input.readInt32() }
88 -> { bitField0_ = bitField0_ or 256; field_11_ = input.readInt32() }
else -> if (!input.skipField(tag, unknownOutput())) done = true
}}
} catch (e: InvalidProtocolBufferException) {
throw e.setUnfinishedMessage(this)
} finally {
if ((mutBit0_ and 1) != 0) field_9_ = GeneratedMessageLite.unmodifiableProtoList(field_9_)
if (unknownBuffer != null) { unknownOut!!.flush(); unknownFields = unknownBuffer.toByteString() }
}}
val `type`: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value.Type get() = field_1_
fun getType(): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value.Type = `type`
fun hasType(): Boolean = (bitField0_ and 1) != 0
val `intValue`: Long get() = field_2_
fun getIntValue(): Long = `intValue`
fun hasIntValue(): Boolean = (bitField0_ and 2) != 0
val `floatValue`: Float get() = field_3_
fun getFloatValue(): Float = `floatValue`
fun hasFloatValue(): Boolean = (bitField0_ and 4) != 0
val `doubleValue`: Double get() = field_4_
fun getDoubleValue(): Double = `doubleValue`
fun hasDoubleValue(): Boolean = (bitField0_ and 8) != 0
val `stringValue`: Int get() = field_5_
fun getStringValue(): Int = `stringValue`
fun hasStringValue(): Boolean = (bitField0_ and 16) != 0
val `classId`: Int get() = field_6_
fun getClassId(): Int = `classId`
fun hasClassId(): Boolean = (bitField0_ and 32) != 0
val `enumValueId`: Int get() = field_7_
fun getEnumValueId(): Int = `enumValueId`
fun hasEnumValueId(): Boolean = (bitField0_ and 64) != 0
val `annotation`: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation get() = field_8_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.defaultInstance
fun getAnnotation(): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation = `annotation`
fun hasAnnotation(): Boolean = (bitField0_ and 128) != 0
val `arrayElementList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value> get() = field_9_
val `arrayElementCount`: Int get() = field_9_.size
fun getArrayElementList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value> = field_9_
fun getArrayElementCount(): Int = field_9_.size
fun getArrayElement(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value = field_9_[index]
val `flags`: Int get() = field_10_
fun getFlags(): Int = `flags`
fun hasFlags(): Boolean = (bitField0_ and 512) != 0
val `arrayDimensionCount`: Int get() = field_11_
fun getArrayDimensionCount(): Int = `arrayDimensionCount`
fun hasArrayDimensionCount(): Boolean = (bitField0_ and 256) != 0
override fun isInitialized(): Boolean {
val isInitialized = memoizedIsInitialized.toInt()
if (isInitialized == 1) return true
if (isInitialized == 0) return false
if ((bitField0_ and 128) != 0 && !getAnnotation().isInitialized()) { memoizedIsInitialized = 0; return false }
for (i in 0 until field_9_.size) { if (!field_9_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
memoizedIsInitialized = 1
return true
}
override fun writeTo(output: CodedOutputStream) {
if ((bitField0_ and 1) != 0) { output.writeRawVarint32(8); output.writeInt32NoTag((field_1_).number) }
if ((bitField0_ and 2) != 0) { output.writeRawVarint32(16); output.writeRawVarint64(CodedOutputStream.encodeZigZag64(field_2_)) }
if ((bitField0_ and 4) != 0) { output.writeRawVarint32(29); output.writeRawLittleEndian32((field_3_).toRawBits()) }
if ((bitField0_ and 8) != 0) { output.writeRawVarint32(33); output.writeRawLittleEndian64((field_4_).toRawBits()) }
if ((bitField0_ and 16) != 0) { output.writeRawVarint32(40); output.writeInt32NoTag(field_5_) }
if ((bitField0_ and 32) != 0) { output.writeRawVarint32(48); output.writeInt32NoTag(field_6_) }
if ((bitField0_ and 64) != 0) { output.writeRawVarint32(56); output.writeInt32NoTag(field_7_) }
if ((bitField0_ and 128) != 0) { output.writeRawVarint32(66); output.writeMessageNoTag(getAnnotation()) }
for (i in 0 until field_9_.size) { output.writeRawVarint32(74); output.writeMessageNoTag(field_9_[i]) }
if ((bitField0_ and 512) != 0) { output.writeRawVarint32(80); output.writeInt32NoTag(field_10_) }
if ((bitField0_ and 256) != 0) { output.writeRawVarint32(88); output.writeInt32NoTag(field_11_) }
if (!unknownFields.isEmpty()) unknownFields.writeTo(output)
}
override val serializedSize: Int get() {
val cached = memoizedSerializedSize
if (cached != -1) return cached
var size = 0
if ((bitField0_ and 1) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag((field_1_).number)
if ((bitField0_ and 2) != 0) size += 1 + CodedOutputStream.computeRawVarint64Size(CodedOutputStream.encodeZigZag64(field_2_))
if ((bitField0_ and 4) != 0) size += 5
if ((bitField0_ and 8) != 0) size += 9
if ((bitField0_ and 16) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_5_)
if ((bitField0_ and 32) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_6_)
if ((bitField0_ and 64) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_7_)
if ((bitField0_ and 128) != 0) size += 1 + CodedOutputStream.computeMessageSizeNoTag(getAnnotation())
if ((bitField0_ and 256) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_11_)
if ((bitField0_ and 512) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_10_)
for (i in 0 until field_9_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_9_[i]) }
size += unknownFields.size()
memoizedSerializedSize = size
return size
}
override val defaultInstanceForType: Value get() = defaultInstance
override fun newBuilderForType(): Builder = Builder()
override fun toBuilder(): Builder = Builder().mergeFrom(this)
class Builder : GeneratedMessageLite.ExtendableBuilder<Value, Builder>() {
private var bitField0_: Int = 0
private var mutBitField0_: Int = 0
private var field_1_: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value.Type = org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value.Type.BYTE
private var field_2_: Long = 0L
private var field_3_: Float = 0.0f
private var field_4_: Double = 0.0
private var field_5_: Int = 0
private var field_6_: Int = 0
private var field_7_: Int = 0
private var field_8_: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation? = null
private var field_11_: Int = 0
private var field_10_: Int = 0
private var field_9_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value> = emptyList()
private fun ensure_9_Mutable() { if ((mutBitField0_ and 1) == 0) { field_9_ = ArrayList(field_9_); mutBitField0_ = mutBitField0_ or 1 } }
override val defaultInstanceForType: Value get() = defaultInstance
override fun clear(): Builder {
bitField0_ = 0
mutBitField0_ = 0
field_1_ = org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value.Type.BYTE
field_2_ = 0L
field_3_ = 0.0f
field_4_ = 0.0
field_5_ = 0
field_6_ = 0
field_7_ = 0
field_8_ = null
field_11_ = 0
field_10_ = 0
field_9_ = emptyList()
unknownFields = ByteString.EMPTY
return this
}
override fun clone(): Builder = Builder().mergeFrom(buildPartial())
override fun build(): Value {
val result = buildPartial()
if (!result.isInitialized()) throw UninitializedMessageException()
return result
}
override fun buildPartial(): Value {
val result = Value(this)
val from_bitField0_ = bitField0_
var to_bitField0_ = 0
if ((from_bitField0_ and 1) != 0) to_bitField0_ = to_bitField0_ or 1
result.field_1_ = field_1_
if ((from_bitField0_ and 2) != 0) to_bitField0_ = to_bitField0_ or 2
result.field_2_ = field_2_
if ((from_bitField0_ and 4) != 0) to_bitField0_ = to_bitField0_ or 4
result.field_3_ = field_3_
if ((from_bitField0_ and 8) != 0) to_bitField0_ = to_bitField0_ or 8
result.field_4_ = field_4_
if ((from_bitField0_ and 16) != 0) to_bitField0_ = to_bitField0_ or 16
result.field_5_ = field_5_
if ((from_bitField0_ and 32) != 0) to_bitField0_ = to_bitField0_ or 32
result.field_6_ = field_6_
if ((from_bitField0_ and 64) != 0) to_bitField0_ = to_bitField0_ or 64
result.field_7_ = field_7_
if ((from_bitField0_ and 128) != 0) to_bitField0_ = to_bitField0_ or 128
result.field_8_ = field_8_
if ((from_bitField0_ and 256) != 0) to_bitField0_ = to_bitField0_ or 256
result.field_11_ = field_11_
if ((from_bitField0_ and 512) != 0) to_bitField0_ = to_bitField0_ or 512
result.field_10_ = field_10_
result.bitField0_ = to_bitField0_
if ((mutBitField0_ and 1) != 0) { field_9_ = GeneratedMessageLite.unmodifiableProtoList(field_9_); mutBitField0_ = mutBitField0_ and -2 }
result.field_9_ = field_9_
return result
}
override fun mergeFrom(other: Value): Builder {
if (other === defaultInstance) return this
if (other.hasType()) setType(other.getType())
if (other.hasIntValue()) setIntValue(other.getIntValue())
if (other.hasFloatValue()) setFloatValue(other.getFloatValue())
if (other.hasDoubleValue()) setDoubleValue(other.getDoubleValue())
if (other.hasStringValue()) setStringValue(other.getStringValue())
if (other.hasClassId()) setClassId(other.getClassId())
if (other.hasEnumValueId()) setEnumValueId(other.getEnumValueId())
if (other.hasAnnotation()) mergeAnnotation(other.getAnnotation())
if (other.hasArrayDimensionCount()) setArrayDimensionCount(other.getArrayDimensionCount())
if (other.hasFlags()) setFlags(other.getFlags())
if (other.field_9_.isNotEmpty()) {
if (field_9_.isEmpty()) { field_9_ = other.field_9_; mutBitField0_ = mutBitField0_ and -2 } else { ensure_9_Mutable(); (field_9_ as MutableList<Any>).addAll(other.field_9_ as Collection<Any>) }
}
unknownFields = unknownFields.concat(other.unknownFields)
return this
}
override fun isInitialized(): Boolean {
if ((bitField0_ and 128) != 0 && !getAnnotation().isInitialized()) return false
for (i in 0 until field_9_.size) { if (!field_9_[i].isInitialized()) return false }
return true
}
override fun mergeFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Builder {
var parsedMessage: Value? = null
try {
parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry)
} catch (e: InvalidProtocolBufferException) {
parsedMessage = e.unfinishedMessage as? Value
throw e
} finally {
if (parsedMessage != null) mergeFrom(parsedMessage)
}
return this
}
var `type`: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value.Type
get() = field_1_
set(value) { bitField0_ = bitField0_ or 1; field_1_ = value }
fun getType(): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value.Type = field_1_
fun hasType(): Boolean = (bitField0_ and 1) != 0
fun setType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value.Type): Builder { bitField0_ = bitField0_ or 1; field_1_ = value; return this }
fun clearType(): Builder { bitField0_ = bitField0_ and -2; field_1_ = org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value.Type.BYTE; return this }
var `intValue`: Long
get() = field_2_
set(value) { bitField0_ = bitField0_ or 2; field_2_ = value }
fun getIntValue(): Long = field_2_
fun hasIntValue(): Boolean = (bitField0_ and 2) != 0
fun setIntValue(value: Long): Builder { bitField0_ = bitField0_ or 2; field_2_ = value; return this }
fun clearIntValue(): Builder { bitField0_ = bitField0_ and -3; field_2_ = 0L; return this }
var `floatValue`: Float
get() = field_3_
set(value) { bitField0_ = bitField0_ or 4; field_3_ = value }
fun getFloatValue(): Float = field_3_
fun hasFloatValue(): Boolean = (bitField0_ and 4) != 0
fun setFloatValue(value: Float): Builder { bitField0_ = bitField0_ or 4; field_3_ = value; return this }
fun clearFloatValue(): Builder { bitField0_ = bitField0_ and -5; field_3_ = 0.0f; return this }
var `doubleValue`: Double
get() = field_4_
set(value) { bitField0_ = bitField0_ or 8; field_4_ = value }
fun getDoubleValue(): Double = field_4_
fun hasDoubleValue(): Boolean = (bitField0_ and 8) != 0
fun setDoubleValue(value: Double): Builder { bitField0_ = bitField0_ or 8; field_4_ = value; return this }
fun clearDoubleValue(): Builder { bitField0_ = bitField0_ and -9; field_4_ = 0.0; return this }
var `stringValue`: Int
get() = field_5_
set(value) { bitField0_ = bitField0_ or 16; field_5_ = value }
fun getStringValue(): Int = field_5_
fun hasStringValue(): Boolean = (bitField0_ and 16) != 0
fun setStringValue(value: Int): Builder { bitField0_ = bitField0_ or 16; field_5_ = value; return this }
fun clearStringValue(): Builder { bitField0_ = bitField0_ and -17; field_5_ = 0; return this }
var `classId`: Int
get() = field_6_
set(value) { bitField0_ = bitField0_ or 32; field_6_ = value }
fun getClassId(): Int = field_6_
fun hasClassId(): Boolean = (bitField0_ and 32) != 0
fun setClassId(value: Int): Builder { bitField0_ = bitField0_ or 32; field_6_ = value; return this }
fun clearClassId(): Builder { bitField0_ = bitField0_ and -33; field_6_ = 0; return this }
var `enumValueId`: Int
get() = field_7_
set(value) { bitField0_ = bitField0_ or 64; field_7_ = value }
fun getEnumValueId(): Int = field_7_
fun hasEnumValueId(): Boolean = (bitField0_ and 64) != 0
fun setEnumValueId(value: Int): Builder { bitField0_ = bitField0_ or 64; field_7_ = value; return this }
fun clearEnumValueId(): Builder { bitField0_ = bitField0_ and -65; field_7_ = 0; return this }
var `annotation`: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation
get() = field_8_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.defaultInstance
set(value) { field_8_ = value; bitField0_ = bitField0_ or 128 }
fun getAnnotation(): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation = `annotation`
fun hasAnnotation(): Boolean = (bitField0_ and 128) != 0
fun setAnnotation(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation): Builder { field_8_ = value; bitField0_ = bitField0_ or 128; return this }
fun setAnnotation(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Builder): Builder { field_8_ = value.build(); bitField0_ = bitField0_ or 128; return this }
fun mergeAnnotation(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation): Builder {
val cur = field_8_
field_8_ = if ((bitField0_ and 128) != 0 && cur != null && cur !== org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.defaultInstance) org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.newBuilder(cur).mergeFrom(value).buildPartial() else value
bitField0_ = bitField0_ or 128
return this
}
fun clearAnnotation(): Builder { field_8_ = null; bitField0_ = bitField0_ and -129; return this }
val `arrayElementList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value> get() = if (field_9_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_9_)
val `arrayElementCount`: Int get() = field_9_.size
fun getArrayElementList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value> = `arrayElementList`
fun getArrayElementCount(): Int = field_9_.size
fun getArrayElement(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value = field_9_[index]
fun addArrayElement(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value): Builder { ensure_9_Mutable(); (field_9_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value>).add(value); return this }
fun setArrayElement(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value): Builder { ensure_9_Mutable(); (field_9_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value>)[index] = value; return this }
fun addAllArrayElement(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value>): Builder { ensure_9_Mutable(); AbstractMessageLite.Builder.addAll(values, field_9_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value>); return this }
fun clearArrayElement(): Builder { field_9_ = emptyList(); mutBitField0_ = mutBitField0_ and -2; return this }
fun addArrayElement(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value.Builder): Builder = addArrayElement(value.build())
fun setArrayElement(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value.Builder): Builder = setArrayElement(index, value.build())
var `flags`: Int
get() = field_10_
set(value) { bitField0_ = bitField0_ or 512; field_10_ = value }
fun getFlags(): Int = field_10_
fun hasFlags(): Boolean = (bitField0_ and 512) != 0
fun setFlags(value: Int): Builder { bitField0_ = bitField0_ or 512; field_10_ = value; return this }
fun clearFlags(): Builder { bitField0_ = bitField0_ and -513; field_10_ = 0; return this }
var `arrayDimensionCount`: Int
get() = field_11_
set(value) { bitField0_ = bitField0_ or 256; field_11_ = value }
fun getArrayDimensionCount(): Int = field_11_
fun hasArrayDimensionCount(): Boolean = (bitField0_ and 256) != 0
fun setArrayDimensionCount(value: Int): Builder { bitField0_ = bitField0_ or 256; field_11_ = value; return this }
fun clearArrayDimensionCount(): Builder { bitField0_ = bitField0_ and -257; field_11_ = 0; return this }
}
companion object {
val defaultInstance: Value = Value()
fun getDefaultInstance(): Value = defaultInstance
fun newBuilder(): Builder = Builder()
fun newBuilder(prototype: Value): Builder = Builder().mergeFrom(prototype)
val PARSER: Parser<Value> = object : AbstractParser<Value>() {
override fun parsePartialFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Value =
Value(input, extensionRegistry)
}
fun parseFrom(bytes: ByteArray): Value = PARSER.parseFrom(bytes)
fun parseFrom(bytes: ByteArray, registry: ExtensionRegistryLite): Value = PARSER.parseFrom(bytes, registry)
fun parseFrom(bytes: ByteString): Value { val input = bytes.newCodedInput(); val msg = PARSER.parsePartialFrom(input, ExtensionRegistryLite.getEmptyRegistry()); input.checkLastTagWas(0); if (!msg.isInitialized()) throw UninitializedMessageException().asInvalidProtocolBufferException().setUnfinishedMessage(msg); return msg }
fun parseFrom(input: CodedInputStream): Value = PARSER.parseFrom(input)
fun parseFrom(input: CodedInputStream, registry: ExtensionRegistryLite): Value = PARSER.parseFrom(input, registry)
const val TYPE_FIELD_NUMBER = 1
const val INT_VALUE_FIELD_NUMBER = 2
const val FLOAT_VALUE_FIELD_NUMBER = 3
const val DOUBLE_VALUE_FIELD_NUMBER = 4
const val STRING_VALUE_FIELD_NUMBER = 5
const val CLASS_ID_FIELD_NUMBER = 6
const val ENUM_VALUE_ID_FIELD_NUMBER = 7
const val ANNOTATION_FIELD_NUMBER = 8
const val ARRAY_ELEMENT_FIELD_NUMBER = 9
const val FLAGS_FIELD_NUMBER = 10
const val ARRAY_DIMENSION_COUNT_FIELD_NUMBER = 11
}}
internal var bitField0_: Int = 0
internal var field_1_: Int = 0
internal var field_2_: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value? = null
private var memoizedIsInitialized: Byte = -1
private var memoizedSerializedSize: Int = -1
private constructor()
private constructor(builder: Builder) {
this.unknownFields = builder.unknownFields
}
private constructor(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite) {
var unknownBuffer: ByteString.Output? = null
var unknownOut: CodedOutputStream? = null
fun unknownOutput(): CodedOutputStream {
var out = unknownOut
if (out == null) { val buf = ByteString.newOutput(); unknownBuffer = buf; out = CodedOutputStream.newInstance(buf); unknownOut = out }
return out
}
try {
var done = false
while (!done) {
val tag = input.readTag()
when (tag) {
0 -> done = true
8 -> { bitField0_ = bitField0_ or 1; field_1_ = input.readInt32() }
18 -> {
var subBuilder: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value.Builder? = null
if ((bitField0_ and 2) != 0) subBuilder = field_2_?.toBuilder()
val parsed = input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value.PARSER, extensionRegistry)
field_2_ = if (subBuilder != null) subBuilder.mergeFrom(parsed).buildPartial() else parsed
bitField0_ = bitField0_ or 2
}
else -> if (!input.skipField(tag, unknownOutput())) done = true
}}
} catch (e: InvalidProtocolBufferException) {
throw e.setUnfinishedMessage(this)
} finally {
if (unknownBuffer != null) { unknownOut!!.flush(); unknownFields = unknownBuffer.toByteString() }
}}
val `nameId`: Int get() = field_1_
fun getNameId(): Int = `nameId`
fun hasNameId(): Boolean = (bitField0_ and 1) != 0
val `value`: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value get() = field_2_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value.defaultInstance
fun getValue(): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value = `value`
fun hasValue(): Boolean = (bitField0_ and 2) != 0
override fun isInitialized(): Boolean {
val isInitialized = memoizedIsInitialized.toInt()
if (isInitialized == 1) return true
if (isInitialized == 0) return false
if ((bitField0_ and 1) == 0) { memoizedIsInitialized = 0; return false }
if ((bitField0_ and 2) == 0) { memoizedIsInitialized = 0; return false }
if ((bitField0_ and 2) != 0 && !getValue().isInitialized()) { memoizedIsInitialized = 0; return false }
memoizedIsInitialized = 1
return true
}
override fun writeTo(output: CodedOutputStream) {
if ((bitField0_ and 1) != 0) { output.writeRawVarint32(8); output.writeInt32NoTag(field_1_) }
if ((bitField0_ and 2) != 0) { output.writeRawVarint32(18); output.writeMessageNoTag(getValue()) }
if (!unknownFields.isEmpty()) unknownFields.writeTo(output)
}
override val serializedSize: Int get() {
val cached = memoizedSerializedSize
if (cached != -1) return cached
var size = 0
if ((bitField0_ and 1) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_1_)
if ((bitField0_ and 2) != 0) size += 1 + CodedOutputStream.computeMessageSizeNoTag(getValue())
size += unknownFields.size()
memoizedSerializedSize = size
return size
}
override val defaultInstanceForType: Argument get() = defaultInstance
override fun newBuilderForType(): Builder = Builder()
override fun toBuilder(): Builder = Builder().mergeFrom(this)
class Builder : GeneratedMessageLite.ExtendableBuilder<Argument, Builder>() {
private var bitField0_: Int = 0
private var field_1_: Int = 0
private var field_2_: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value? = null
override val defaultInstanceForType: Argument get() = defaultInstance
override fun clear(): Builder {
bitField0_ = 0
field_1_ = 0
field_2_ = null
unknownFields = ByteString.EMPTY
return this
}
override fun clone(): Builder = Builder().mergeFrom(buildPartial())
override fun build(): Argument {
val result = buildPartial()
if (!result.isInitialized()) throw UninitializedMessageException()
return result
}
override fun buildPartial(): Argument {
val result = Argument(this)
val from_bitField0_ = bitField0_
var to_bitField0_ = 0
if ((from_bitField0_ and 1) != 0) to_bitField0_ = to_bitField0_ or 1
result.field_1_ = field_1_
if ((from_bitField0_ and 2) != 0) to_bitField0_ = to_bitField0_ or 2
result.field_2_ = field_2_
result.bitField0_ = to_bitField0_
return result
}
override fun mergeFrom(other: Argument): Builder {
if (other === defaultInstance) return this
if (other.hasNameId()) setNameId(other.getNameId())
if (other.hasValue()) mergeValue(other.getValue())
unknownFields = unknownFields.concat(other.unknownFields)
return this
}
override fun isInitialized(): Boolean {
if ((bitField0_ and 1) == 0) return false
if ((bitField0_ and 2) == 0) return false
if ((bitField0_ and 2) != 0 && !getValue().isInitialized()) return false
return true
}
override fun mergeFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Builder {
var parsedMessage: Argument? = null
try {
parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry)
} catch (e: InvalidProtocolBufferException) {
parsedMessage = e.unfinishedMessage as? Argument
throw e
} finally {
if (parsedMessage != null) mergeFrom(parsedMessage)
}
return this
}
var `nameId`: Int
get() = field_1_
set(value) { bitField0_ = bitField0_ or 1; field_1_ = value }
fun getNameId(): Int = field_1_
fun hasNameId(): Boolean = (bitField0_ and 1) != 0
fun setNameId(value: Int): Builder { bitField0_ = bitField0_ or 1; field_1_ = value; return this }
fun clearNameId(): Builder { bitField0_ = bitField0_ and -2; field_1_ = 0; return this }
var `value`: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value
get() = field_2_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value.defaultInstance
set(value) { field_2_ = value; bitField0_ = bitField0_ or 2 }
fun getValue(): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value = `value`
fun hasValue(): Boolean = (bitField0_ and 2) != 0
fun setValue(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value): Builder { field_2_ = value; bitField0_ = bitField0_ or 2; return this }
fun setValue(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value.Builder): Builder { field_2_ = value.build(); bitField0_ = bitField0_ or 2; return this }
fun mergeValue(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value): Builder {
val cur = field_2_
field_2_ = if ((bitField0_ and 2) != 0 && cur != null && cur !== org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value.defaultInstance) org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value.newBuilder(cur).mergeFrom(value).buildPartial() else value
bitField0_ = bitField0_ or 2
return this
}
fun clearValue(): Builder { field_2_ = null; bitField0_ = bitField0_ and -3; return this }
}
companion object {
val defaultInstance: Argument = Argument()
fun getDefaultInstance(): Argument = defaultInstance
fun newBuilder(): Builder = Builder()
fun newBuilder(prototype: Argument): Builder = Builder().mergeFrom(prototype)
val PARSER: Parser<Argument> = object : AbstractParser<Argument>() {
override fun parsePartialFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Argument =
Argument(input, extensionRegistry)
}
fun parseFrom(bytes: ByteArray): Argument = PARSER.parseFrom(bytes)
fun parseFrom(bytes: ByteArray, registry: ExtensionRegistryLite): Argument = PARSER.parseFrom(bytes, registry)
fun parseFrom(bytes: ByteString): Argument { val input = bytes.newCodedInput(); val msg = PARSER.parsePartialFrom(input, ExtensionRegistryLite.getEmptyRegistry()); input.checkLastTagWas(0); if (!msg.isInitialized()) throw UninitializedMessageException().asInvalidProtocolBufferException().setUnfinishedMessage(msg); return msg }
fun parseFrom(input: CodedInputStream): Argument = PARSER.parseFrom(input)
fun parseFrom(input: CodedInputStream, registry: ExtensionRegistryLite): Argument = PARSER.parseFrom(input, registry)
const val NAME_ID_FIELD_NUMBER = 1
const val VALUE_FIELD_NUMBER = 2
}}
internal var bitField0_: Int = 0
internal var field_1_: Int = 0
internal var field_2_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument> = emptyList()
private var memoizedIsInitialized: Byte = -1
private var memoizedSerializedSize: Int = -1
private constructor()
private constructor(builder: Builder) {
this.unknownFields = builder.unknownFields
}
private constructor(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite) {
var mutBit0_ = 0
var unknownBuffer: ByteString.Output? = null
var unknownOut: CodedOutputStream? = null
fun unknownOutput(): CodedOutputStream {
var out = unknownOut
if (out == null) { val buf = ByteString.newOutput(); unknownBuffer = buf; out = CodedOutputStream.newInstance(buf); unknownOut = out }
return out
}
try {
var done = false
while (!done) {
val tag = input.readTag()
when (tag) {
0 -> done = true
8 -> { bitField0_ = bitField0_ or 1; field_1_ = input.readInt32() }
18 -> {
if ((mutBit0_ and 1) == 0) { field_2_ = ArrayList(); mutBit0_ = mutBit0_ or 1 }
(field_2_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.PARSER, extensionRegistry))
}
else -> if (!input.skipField(tag, unknownOutput())) done = true
}}
} catch (e: InvalidProtocolBufferException) {
throw e.setUnfinishedMessage(this)
} finally {
if ((mutBit0_ and 1) != 0) field_2_ = GeneratedMessageLite.unmodifiableProtoList(field_2_)
if (unknownBuffer != null) { unknownOut!!.flush(); unknownFields = unknownBuffer.toByteString() }
}}
val `id`: Int get() = field_1_
fun getId(): Int = `id`
fun hasId(): Boolean = (bitField0_ and 1) != 0
val `argumentList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument> get() = field_2_
val `argumentCount`: Int get() = field_2_.size
fun getArgumentList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument> = field_2_
fun getArgumentCount(): Int = field_2_.size
fun getArgument(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument = field_2_[index]
override fun isInitialized(): Boolean {
val isInitialized = memoizedIsInitialized.toInt()
if (isInitialized == 1) return true
if (isInitialized == 0) return false
if ((bitField0_ and 1) == 0) { memoizedIsInitialized = 0; return false }
for (i in 0 until field_2_.size) { if (!field_2_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
memoizedIsInitialized = 1
return true
}
override fun writeTo(output: CodedOutputStream) {
if ((bitField0_ and 1) != 0) { output.writeRawVarint32(8); output.writeInt32NoTag(field_1_) }
for (i in 0 until field_2_.size) { output.writeRawVarint32(18); output.writeMessageNoTag(field_2_[i]) }
if (!unknownFields.isEmpty()) unknownFields.writeTo(output)
}
override val serializedSize: Int get() {
val cached = memoizedSerializedSize
if (cached != -1) return cached
var size = 0
if ((bitField0_ and 1) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_1_)
for (i in 0 until field_2_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_2_[i]) }
size += unknownFields.size()
memoizedSerializedSize = size
return size
}
override val defaultInstanceForType: Annotation get() = defaultInstance
override fun newBuilderForType(): Builder = Builder()
override fun toBuilder(): Builder = Builder().mergeFrom(this)
class Builder : GeneratedMessageLite.ExtendableBuilder<Annotation, Builder>() {
private var bitField0_: Int = 0
private var mutBitField0_: Int = 0
private var field_1_: Int = 0
private var field_2_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument> = emptyList()
private fun ensure_2_Mutable() { if ((mutBitField0_ and 1) == 0) { field_2_ = ArrayList(field_2_); mutBitField0_ = mutBitField0_ or 1 } }
override val defaultInstanceForType: Annotation get() = defaultInstance
override fun clear(): Builder {
bitField0_ = 0
mutBitField0_ = 0
field_1_ = 0
field_2_ = emptyList()
unknownFields = ByteString.EMPTY
return this
}
override fun clone(): Builder = Builder().mergeFrom(buildPartial())
override fun build(): Annotation {
val result = buildPartial()
if (!result.isInitialized()) throw UninitializedMessageException()
return result
}
override fun buildPartial(): Annotation {
val result = Annotation(this)
val from_bitField0_ = bitField0_
var to_bitField0_ = 0
if ((from_bitField0_ and 1) != 0) to_bitField0_ = to_bitField0_ or 1
result.field_1_ = field_1_
result.bitField0_ = to_bitField0_
if ((mutBitField0_ and 1) != 0) { field_2_ = GeneratedMessageLite.unmodifiableProtoList(field_2_); mutBitField0_ = mutBitField0_ and -2 }
result.field_2_ = field_2_
return result
}
override fun mergeFrom(other: Annotation): Builder {
if (other === defaultInstance) return this
if (other.hasId()) setId(other.getId())
if (other.field_2_.isNotEmpty()) {
if (field_2_.isEmpty()) { field_2_ = other.field_2_; mutBitField0_ = mutBitField0_ and -2 } else { ensure_2_Mutable(); (field_2_ as MutableList<Any>).addAll(other.field_2_ as Collection<Any>) }
}
unknownFields = unknownFields.concat(other.unknownFields)
return this
}
override fun isInitialized(): Boolean {
if ((bitField0_ and 1) == 0) return false
for (i in 0 until field_2_.size) { if (!field_2_[i].isInitialized()) return false }
return true
}
override fun mergeFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Builder {
var parsedMessage: Annotation? = null
try {
parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry)
} catch (e: InvalidProtocolBufferException) {
parsedMessage = e.unfinishedMessage as? Annotation
throw e
} finally {
if (parsedMessage != null) mergeFrom(parsedMessage)
}
return this
}
var `id`: Int
get() = field_1_
set(value) { bitField0_ = bitField0_ or 1; field_1_ = value }
fun getId(): Int = field_1_
fun hasId(): Boolean = (bitField0_ and 1) != 0
fun setId(value: Int): Builder { bitField0_ = bitField0_ or 1; field_1_ = value; return this }
fun clearId(): Builder { bitField0_ = bitField0_ and -2; field_1_ = 0; return this }
val `argumentList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument> get() = if (field_2_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_2_)
val `argumentCount`: Int get() = field_2_.size
fun getArgumentList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument> = `argumentList`
fun getArgumentCount(): Int = field_2_.size
fun getArgument(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument = field_2_[index]
fun addArgument(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument): Builder { ensure_2_Mutable(); (field_2_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument>).add(value); return this }
fun setArgument(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument): Builder { ensure_2_Mutable(); (field_2_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument>)[index] = value; return this }
fun addAllArgument(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument>): Builder { ensure_2_Mutable(); AbstractMessageLite.Builder.addAll(values, field_2_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument>); return this }
fun clearArgument(): Builder { field_2_ = emptyList(); mutBitField0_ = mutBitField0_ and -2; return this }
fun addArgument(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Builder): Builder = addArgument(value.build())
fun setArgument(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Builder): Builder = setArgument(index, value.build())
}
companion object {
val defaultInstance: Annotation = Annotation()
fun getDefaultInstance(): Annotation = defaultInstance
fun newBuilder(): Builder = Builder()
fun newBuilder(prototype: Annotation): Builder = Builder().mergeFrom(prototype)
val PARSER: Parser<Annotation> = object : AbstractParser<Annotation>() {
override fun parsePartialFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Annotation =
Annotation(input, extensionRegistry)
}
fun parseFrom(bytes: ByteArray): Annotation = PARSER.parseFrom(bytes)
fun parseFrom(bytes: ByteArray, registry: ExtensionRegistryLite): Annotation = PARSER.parseFrom(bytes, registry)
fun parseFrom(bytes: ByteString): Annotation { val input = bytes.newCodedInput(); val msg = PARSER.parsePartialFrom(input, ExtensionRegistryLite.getEmptyRegistry()); input.checkLastTagWas(0); if (!msg.isInitialized()) throw UninitializedMessageException().asInvalidProtocolBufferException().setUnfinishedMessage(msg); return msg }
fun parseFrom(input: CodedInputStream): Annotation = PARSER.parseFrom(input)
fun parseFrom(input: CodedInputStream, registry: ExtensionRegistryLite): Annotation = PARSER.parseFrom(input, registry)
const val ID_FIELD_NUMBER = 1
const val ARGUMENT_FIELD_NUMBER = 2
}}
class Type : GeneratedMessageLite.ExtendableMessage<Type> {
class Argument : GeneratedMessageLite.ExtendableMessage<Argument> {
enum class Projection(override val number: Int) : Internal.EnumLite {
IN(0),
OUT(1),
INV(2),
STAR(3),
; companion object : Internal.EnumLiteMap<Projection> {
override fun findValueByNumber(number: Int): Projection? = valueOf(number)
fun valueOf(number: Int): Projection? = when (number) {
0 -> IN
1 -> OUT
2 -> INV
3 -> STAR
else -> null
}
fun internalGetValueMap(): Internal.EnumLiteMap<Projection> = this
}}
internal var bitField0_: Int = 0
internal var field_1_: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Argument.Projection = org.jetbrains.kotlin.metadata.ProtoBuf.Type.Argument.Projection.INV
internal var field_2_: org.jetbrains.kotlin.metadata.ProtoBuf.Type? = null
internal var field_3_: Int = 0
private var memoizedIsInitialized: Byte = -1
private var memoizedSerializedSize: Int = -1
private constructor()
private constructor(builder: Builder) {
this.unknownFields = builder.unknownFields
}
private constructor(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite) {
var unknownBuffer: ByteString.Output? = null
var unknownOut: CodedOutputStream? = null
fun unknownOutput(): CodedOutputStream {
var out = unknownOut
if (out == null) { val buf = ByteString.newOutput(); unknownBuffer = buf; out = CodedOutputStream.newInstance(buf); unknownOut = out }
return out
}
try {
var done = false
while (!done) {
val tag = input.readTag()
when (tag) {
0 -> done = true
8 -> {
val raw = input.readEnum()
val parsed = org.jetbrains.kotlin.metadata.ProtoBuf.Type.Argument.Projection.valueOf(raw)
if (parsed == null) { unknownOutput().writeRawVarint32(8); unknownOutput().writeRawVarint32(raw) } else {
bitField0_ = bitField0_ or 1; field_1_ = parsed
}}
18 -> {
var subBuilder: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder? = null
if ((bitField0_ and 2) != 0) subBuilder = field_2_?.toBuilder()
val parsed = input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Type.PARSER, extensionRegistry)
field_2_ = if (subBuilder != null) subBuilder.mergeFrom(parsed).buildPartial() else parsed
bitField0_ = bitField0_ or 2
}
24 -> { bitField0_ = bitField0_ or 4; field_3_ = input.readInt32() }
else -> if (!input.skipField(tag, unknownOutput())) done = true
}}
} catch (e: InvalidProtocolBufferException) {
throw e.setUnfinishedMessage(this)
} finally {
if (unknownBuffer != null) { unknownOut!!.flush(); unknownFields = unknownBuffer.toByteString() }
}}
val `projection`: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Argument.Projection get() = field_1_
fun getProjection(): org.jetbrains.kotlin.metadata.ProtoBuf.Type.Argument.Projection = `projection`
fun hasProjection(): Boolean = (bitField0_ and 1) != 0
val `type`: org.jetbrains.kotlin.metadata.ProtoBuf.Type get() = field_2_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance
fun getType(): org.jetbrains.kotlin.metadata.ProtoBuf.Type = `type`
fun hasType(): Boolean = (bitField0_ and 2) != 0
val `typeId`: Int get() = field_3_
fun getTypeId(): Int = `typeId`
fun hasTypeId(): Boolean = (bitField0_ and 4) != 0
override fun isInitialized(): Boolean {
val isInitialized = memoizedIsInitialized.toInt()
if (isInitialized == 1) return true
if (isInitialized == 0) return false
if ((bitField0_ and 2) != 0 && !getType().isInitialized()) { memoizedIsInitialized = 0; return false }
memoizedIsInitialized = 1
return true
}
override fun writeTo(output: CodedOutputStream) {
if ((bitField0_ and 1) != 0) { output.writeRawVarint32(8); output.writeInt32NoTag((field_1_).number) }
if ((bitField0_ and 2) != 0) { output.writeRawVarint32(18); output.writeMessageNoTag(getType()) }
if ((bitField0_ and 4) != 0) { output.writeRawVarint32(24); output.writeInt32NoTag(field_3_) }
if (!unknownFields.isEmpty()) unknownFields.writeTo(output)
}
override val serializedSize: Int get() {
val cached = memoizedSerializedSize
if (cached != -1) return cached
var size = 0
if ((bitField0_ and 1) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag((field_1_).number)
if ((bitField0_ and 2) != 0) size += 1 + CodedOutputStream.computeMessageSizeNoTag(getType())
if ((bitField0_ and 4) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_3_)
size += unknownFields.size()
memoizedSerializedSize = size
return size
}
override val defaultInstanceForType: Argument get() = defaultInstance
override fun newBuilderForType(): Builder = Builder()
override fun toBuilder(): Builder = Builder().mergeFrom(this)
class Builder : GeneratedMessageLite.ExtendableBuilder<Argument, Builder>() {
private var bitField0_: Int = 0
private var field_1_: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Argument.Projection = org.jetbrains.kotlin.metadata.ProtoBuf.Type.Argument.Projection.INV
private var field_2_: org.jetbrains.kotlin.metadata.ProtoBuf.Type? = null
private var field_3_: Int = 0
override val defaultInstanceForType: Argument get() = defaultInstance
override fun clear(): Builder {
bitField0_ = 0
field_1_ = org.jetbrains.kotlin.metadata.ProtoBuf.Type.Argument.Projection.INV
field_2_ = null
field_3_ = 0
unknownFields = ByteString.EMPTY
return this
}
override fun clone(): Builder = Builder().mergeFrom(buildPartial())
override fun build(): Argument {
val result = buildPartial()
if (!result.isInitialized()) throw UninitializedMessageException()
return result
}
override fun buildPartial(): Argument {
val result = Argument(this)
val from_bitField0_ = bitField0_
var to_bitField0_ = 0
if ((from_bitField0_ and 1) != 0) to_bitField0_ = to_bitField0_ or 1
result.field_1_ = field_1_
if ((from_bitField0_ and 2) != 0) to_bitField0_ = to_bitField0_ or 2
result.field_2_ = field_2_
if ((from_bitField0_ and 4) != 0) to_bitField0_ = to_bitField0_ or 4
result.field_3_ = field_3_
result.bitField0_ = to_bitField0_
return result
}
override fun mergeFrom(other: Argument): Builder {
if (other === defaultInstance) return this
if (other.hasProjection()) setProjection(other.getProjection())
if (other.hasType()) mergeType(other.getType())
if (other.hasTypeId()) setTypeId(other.getTypeId())
unknownFields = unknownFields.concat(other.unknownFields)
return this
}
override fun isInitialized(): Boolean {
if ((bitField0_ and 2) != 0 && !getType().isInitialized()) return false
return true
}
override fun mergeFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Builder {
var parsedMessage: Argument? = null
try {
parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry)
} catch (e: InvalidProtocolBufferException) {
parsedMessage = e.unfinishedMessage as? Argument
throw e
} finally {
if (parsedMessage != null) mergeFrom(parsedMessage)
}
return this
}
var `projection`: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Argument.Projection
get() = field_1_
set(value) { bitField0_ = bitField0_ or 1; field_1_ = value }
fun getProjection(): org.jetbrains.kotlin.metadata.ProtoBuf.Type.Argument.Projection = field_1_
fun hasProjection(): Boolean = (bitField0_ and 1) != 0
fun setProjection(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Argument.Projection): Builder { bitField0_ = bitField0_ or 1; field_1_ = value; return this }
fun clearProjection(): Builder { bitField0_ = bitField0_ and -2; field_1_ = org.jetbrains.kotlin.metadata.ProtoBuf.Type.Argument.Projection.INV; return this }
var `type`: org.jetbrains.kotlin.metadata.ProtoBuf.Type
get() = field_2_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance
set(value) { field_2_ = value; bitField0_ = bitField0_ or 2 }
fun getType(): org.jetbrains.kotlin.metadata.ProtoBuf.Type = `type`
fun hasType(): Boolean = (bitField0_ and 2) != 0
fun setType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder { field_2_ = value; bitField0_ = bitField0_ or 2; return this }
fun setType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder): Builder { field_2_ = value.build(); bitField0_ = bitField0_ or 2; return this }
fun mergeType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder {
val cur = field_2_
field_2_ = if ((bitField0_ and 2) != 0 && cur != null && cur !== org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance) org.jetbrains.kotlin.metadata.ProtoBuf.Type.newBuilder(cur).mergeFrom(value).buildPartial() else value
bitField0_ = bitField0_ or 2
return this
}
fun clearType(): Builder { field_2_ = null; bitField0_ = bitField0_ and -3; return this }
var `typeId`: Int
get() = field_3_
set(value) { bitField0_ = bitField0_ or 4; field_3_ = value }
fun getTypeId(): Int = field_3_
fun hasTypeId(): Boolean = (bitField0_ and 4) != 0
fun setTypeId(value: Int): Builder { bitField0_ = bitField0_ or 4; field_3_ = value; return this }
fun clearTypeId(): Builder { bitField0_ = bitField0_ and -5; field_3_ = 0; return this }
}
companion object {
val defaultInstance: Argument = Argument()
fun getDefaultInstance(): Argument = defaultInstance
fun newBuilder(): Builder = Builder()
fun newBuilder(prototype: Argument): Builder = Builder().mergeFrom(prototype)
val PARSER: Parser<Argument> = object : AbstractParser<Argument>() {
override fun parsePartialFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Argument =
Argument(input, extensionRegistry)
}
fun parseFrom(bytes: ByteArray): Argument = PARSER.parseFrom(bytes)
fun parseFrom(bytes: ByteArray, registry: ExtensionRegistryLite): Argument = PARSER.parseFrom(bytes, registry)
fun parseFrom(bytes: ByteString): Argument { val input = bytes.newCodedInput(); val msg = PARSER.parsePartialFrom(input, ExtensionRegistryLite.getEmptyRegistry()); input.checkLastTagWas(0); if (!msg.isInitialized()) throw UninitializedMessageException().asInvalidProtocolBufferException().setUnfinishedMessage(msg); return msg }
fun parseFrom(input: CodedInputStream): Argument = PARSER.parseFrom(input)
fun parseFrom(input: CodedInputStream, registry: ExtensionRegistryLite): Argument = PARSER.parseFrom(input, registry)
const val PROJECTION_FIELD_NUMBER = 1
const val TYPE_FIELD_NUMBER = 2
const val TYPE_ID_FIELD_NUMBER = 3
}}
internal var bitField0_: Int = 0
internal var field_3_: Boolean = false
internal var field_4_: Int = 0
internal var field_5_: org.jetbrains.kotlin.metadata.ProtoBuf.Type? = null
internal var field_8_: Int = 0
internal var field_6_: Int = 0
internal var field_7_: Int = 0
internal var field_9_: Int = 0
internal var field_12_: Int = 0
internal var field_10_: org.jetbrains.kotlin.metadata.ProtoBuf.Type? = null
internal var field_11_: Int = 0
internal var field_13_: org.jetbrains.kotlin.metadata.ProtoBuf.Type? = null
internal var field_14_: Int = 0
internal var field_1_: Int = 0
internal var field_2_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Type.Argument> = emptyList()
internal var field_100_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = emptyList()
private var memoizedIsInitialized: Byte = -1
private var memoizedSerializedSize: Int = -1
private constructor()
private constructor(builder: Builder) {
this.unknownFields = builder.unknownFields
copyExtensionsFromBuilder(builder)
}
private constructor(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite) {
var mutBit0_ = 0
var unknownBuffer: ByteString.Output? = null
var unknownOut: CodedOutputStream? = null
fun unknownOutput(): CodedOutputStream {
var out = unknownOut
if (out == null) { val buf = ByteString.newOutput(); unknownBuffer = buf; out = CodedOutputStream.newInstance(buf); unknownOut = out }
return out
}
try {
var done = false
while (!done) {
val tag = input.readTag()
when (tag) {
0 -> done = true
8 -> { bitField0_ = bitField0_ or 4096; field_1_ = input.readInt32() }
18 -> {
if ((mutBit0_ and 1) == 0) { field_2_ = ArrayList(); mutBit0_ = mutBit0_ or 1 }
(field_2_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Type.Argument>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Type.Argument.PARSER, extensionRegistry))
}
24 -> { bitField0_ = bitField0_ or 1; field_3_ = input.readBool() }
32 -> { bitField0_ = bitField0_ or 2; field_4_ = input.readInt32() }
42 -> {
var subBuilder: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder? = null
if ((bitField0_ and 4) != 0) subBuilder = field_5_?.toBuilder()
val parsed = input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Type.PARSER, extensionRegistry)
field_5_ = if (subBuilder != null) subBuilder.mergeFrom(parsed).buildPartial() else parsed
bitField0_ = bitField0_ or 4
}
48 -> { bitField0_ = bitField0_ or 16; field_6_ = input.readInt32() }
56 -> { bitField0_ = bitField0_ or 32; field_7_ = input.readInt32() }
64 -> { bitField0_ = bitField0_ or 8; field_8_ = input.readInt32() }
72 -> { bitField0_ = bitField0_ or 64; field_9_ = input.readInt32() }
82 -> {
var subBuilder: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder? = null
if ((bitField0_ and 256) != 0) subBuilder = field_10_?.toBuilder()
val parsed = input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Type.PARSER, extensionRegistry)
field_10_ = if (subBuilder != null) subBuilder.mergeFrom(parsed).buildPartial() else parsed
bitField0_ = bitField0_ or 256
}
88 -> { bitField0_ = bitField0_ or 512; field_11_ = input.readInt32() }
96 -> { bitField0_ = bitField0_ or 128; field_12_ = input.readInt32() }
106 -> {
var subBuilder: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder? = null
if ((bitField0_ and 1024) != 0) subBuilder = field_13_?.toBuilder()
val parsed = input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Type.PARSER, extensionRegistry)
field_13_ = if (subBuilder != null) subBuilder.mergeFrom(parsed).buildPartial() else parsed
bitField0_ = bitField0_ or 1024
}
112 -> { bitField0_ = bitField0_ or 2048; field_14_ = input.readInt32() }
802 -> {
if ((mutBit0_ and 2) == 0) { field_100_ = ArrayList(); mutBit0_ = mutBit0_ or 2 }
(field_100_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.PARSER, extensionRegistry))
}
else -> if (!parseUnknownField(input, unknownOutput(), extensionRegistry, tag)) done = true
}}
} catch (e: InvalidProtocolBufferException) {
throw e.setUnfinishedMessage(this)
} finally {
if ((mutBit0_ and 1) != 0) field_2_ = GeneratedMessageLite.unmodifiableProtoList(field_2_)
if ((mutBit0_ and 2) != 0) field_100_ = GeneratedMessageLite.unmodifiableProtoList(field_100_)
if (unknownBuffer != null) { unknownOut!!.flush(); unknownFields = unknownBuffer.toByteString() }
makeExtensionsImmutable()
}}
val `flags`: Int get() = field_1_
fun getFlags(): Int = `flags`
fun hasFlags(): Boolean = (bitField0_ and 4096) != 0
val `argumentList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Type.Argument> get() = field_2_
val `argumentCount`: Int get() = field_2_.size
fun getArgumentList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Type.Argument> = field_2_
fun getArgumentCount(): Int = field_2_.size
fun getArgument(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Type.Argument = field_2_[index]
val `nullable`: Boolean get() = field_3_
fun getNullable(): Boolean = `nullable`
fun hasNullable(): Boolean = (bitField0_ and 1) != 0
val `flexibleTypeCapabilitiesId`: Int get() = field_4_
fun getFlexibleTypeCapabilitiesId(): Int = `flexibleTypeCapabilitiesId`
fun hasFlexibleTypeCapabilitiesId(): Boolean = (bitField0_ and 2) != 0
val `flexibleUpperBound`: org.jetbrains.kotlin.metadata.ProtoBuf.Type get() = field_5_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance
fun getFlexibleUpperBound(): org.jetbrains.kotlin.metadata.ProtoBuf.Type = `flexibleUpperBound`
fun hasFlexibleUpperBound(): Boolean = (bitField0_ and 4) != 0
val `className`: Int get() = field_6_
fun getClassName(): Int = `className`
fun hasClassName(): Boolean = (bitField0_ and 16) != 0
val `typeParameter`: Int get() = field_7_
fun getTypeParameter(): Int = `typeParameter`
fun hasTypeParameter(): Boolean = (bitField0_ and 32) != 0
val `flexibleUpperBoundId`: Int get() = field_8_
fun getFlexibleUpperBoundId(): Int = `flexibleUpperBoundId`
fun hasFlexibleUpperBoundId(): Boolean = (bitField0_ and 8) != 0
val `typeParameterName`: Int get() = field_9_
fun getTypeParameterName(): Int = `typeParameterName`
fun hasTypeParameterName(): Boolean = (bitField0_ and 64) != 0
val `outerType`: org.jetbrains.kotlin.metadata.ProtoBuf.Type get() = field_10_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance
fun getOuterType(): org.jetbrains.kotlin.metadata.ProtoBuf.Type = `outerType`
fun hasOuterType(): Boolean = (bitField0_ and 256) != 0
val `outerTypeId`: Int get() = field_11_
fun getOuterTypeId(): Int = `outerTypeId`
fun hasOuterTypeId(): Boolean = (bitField0_ and 512) != 0
val `typeAliasName`: Int get() = field_12_
fun getTypeAliasName(): Int = `typeAliasName`
fun hasTypeAliasName(): Boolean = (bitField0_ and 128) != 0
val `abbreviatedType`: org.jetbrains.kotlin.metadata.ProtoBuf.Type get() = field_13_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance
fun getAbbreviatedType(): org.jetbrains.kotlin.metadata.ProtoBuf.Type = `abbreviatedType`
fun hasAbbreviatedType(): Boolean = (bitField0_ and 1024) != 0
val `abbreviatedTypeId`: Int get() = field_14_
fun getAbbreviatedTypeId(): Int = `abbreviatedTypeId`
fun hasAbbreviatedTypeId(): Boolean = (bitField0_ and 2048) != 0
val `annotationList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> get() = field_100_
val `annotationCount`: Int get() = field_100_.size
fun getAnnotationList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = field_100_
fun getAnnotationCount(): Int = field_100_.size
fun getAnnotation(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation = field_100_[index]
override fun isInitialized(): Boolean {
val isInitialized = memoizedIsInitialized.toInt()
if (isInitialized == 1) return true
if (isInitialized == 0) return false
if ((bitField0_ and 4) != 0 && !getFlexibleUpperBound().isInitialized()) { memoizedIsInitialized = 0; return false }
if ((bitField0_ and 256) != 0 && !getOuterType().isInitialized()) { memoizedIsInitialized = 0; return false }
if ((bitField0_ and 1024) != 0 && !getAbbreviatedType().isInitialized()) { memoizedIsInitialized = 0; return false }
for (i in 0 until field_2_.size) { if (!field_2_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
for (i in 0 until field_100_.size) { if (!field_100_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
if (!extensionsAreInitialized()) { memoizedIsInitialized = 0; return false }
memoizedIsInitialized = 1
return true
}
override fun writeTo(output: CodedOutputStream) {
val extensionWriter = if (hasAnyExtensions()) newExtensionWriter() else null
extensionWriter?.writeUntil(1, output)
if ((bitField0_ and 4096) != 0) { output.writeRawVarint32(8); output.writeInt32NoTag(field_1_) }
extensionWriter?.writeUntil(2, output)
for (i in 0 until field_2_.size) { output.writeRawVarint32(18); output.writeMessageNoTag(field_2_[i]) }
extensionWriter?.writeUntil(3, output)
if ((bitField0_ and 1) != 0) { output.writeRawVarint32(24); output.writeRawByte(if (field_3_) 1 else 0) }
extensionWriter?.writeUntil(4, output)
if ((bitField0_ and 2) != 0) { output.writeRawVarint32(32); output.writeInt32NoTag(field_4_) }
extensionWriter?.writeUntil(5, output)
if ((bitField0_ and 4) != 0) { output.writeRawVarint32(42); output.writeMessageNoTag(getFlexibleUpperBound()) }
extensionWriter?.writeUntil(6, output)
if ((bitField0_ and 16) != 0) { output.writeRawVarint32(48); output.writeInt32NoTag(field_6_) }
extensionWriter?.writeUntil(7, output)
if ((bitField0_ and 32) != 0) { output.writeRawVarint32(56); output.writeInt32NoTag(field_7_) }
extensionWriter?.writeUntil(8, output)
if ((bitField0_ and 8) != 0) { output.writeRawVarint32(64); output.writeInt32NoTag(field_8_) }
extensionWriter?.writeUntil(9, output)
if ((bitField0_ and 64) != 0) { output.writeRawVarint32(72); output.writeInt32NoTag(field_9_) }
extensionWriter?.writeUntil(10, output)
if ((bitField0_ and 256) != 0) { output.writeRawVarint32(82); output.writeMessageNoTag(getOuterType()) }
extensionWriter?.writeUntil(11, output)
if ((bitField0_ and 512) != 0) { output.writeRawVarint32(88); output.writeInt32NoTag(field_11_) }
extensionWriter?.writeUntil(12, output)
if ((bitField0_ and 128) != 0) { output.writeRawVarint32(96); output.writeInt32NoTag(field_12_) }
extensionWriter?.writeUntil(13, output)
if ((bitField0_ and 1024) != 0) { output.writeRawVarint32(106); output.writeMessageNoTag(getAbbreviatedType()) }
extensionWriter?.writeUntil(14, output)
if ((bitField0_ and 2048) != 0) { output.writeRawVarint32(112); output.writeInt32NoTag(field_14_) }
extensionWriter?.writeUntil(100, output)
for (i in 0 until field_100_.size) { output.writeRawVarint32(802); output.writeMessageNoTag(field_100_[i]) }
extensionWriter?.writeUntil(Int.MAX_VALUE, output)
if (!unknownFields.isEmpty()) unknownFields.writeTo(output)
}
override val serializedSize: Int get() {
val cached = memoizedSerializedSize
if (cached != -1) return cached
var size = 0
if ((bitField0_ and 1) != 0) size += 2
if ((bitField0_ and 2) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_4_)
if ((bitField0_ and 4) != 0) size += 1 + CodedOutputStream.computeMessageSizeNoTag(getFlexibleUpperBound())
if ((bitField0_ and 8) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_8_)
if ((bitField0_ and 16) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_6_)
if ((bitField0_ and 32) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_7_)
if ((bitField0_ and 64) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_9_)
if ((bitField0_ and 128) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_12_)
if ((bitField0_ and 256) != 0) size += 1 + CodedOutputStream.computeMessageSizeNoTag(getOuterType())
if ((bitField0_ and 512) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_11_)
if ((bitField0_ and 1024) != 0) size += 1 + CodedOutputStream.computeMessageSizeNoTag(getAbbreviatedType())
if ((bitField0_ and 2048) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_14_)
if ((bitField0_ and 4096) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_1_)
for (i in 0 until field_2_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_2_[i]) }
for (i in 0 until field_100_.size) { size += 2 + CodedOutputStream.computeMessageSizeNoTag(field_100_[i]) }
size += extensionsSerializedSize()
size += unknownFields.size()
memoizedSerializedSize = size
return size
}
override val defaultInstanceForType: Type get() = defaultInstance
override fun newBuilderForType(): Builder = Builder()
override fun toBuilder(): Builder = Builder().mergeFrom(this)
class Builder : GeneratedMessageLite.ExtendableBuilder<Type, Builder>() {
private var bitField0_: Int = 0
private var mutBitField0_: Int = 0
private var field_3_: Boolean = false
private var field_4_: Int = 0
private var field_5_: org.jetbrains.kotlin.metadata.ProtoBuf.Type? = null
private var field_8_: Int = 0
private var field_6_: Int = 0
private var field_7_: Int = 0
private var field_9_: Int = 0
private var field_12_: Int = 0
private var field_10_: org.jetbrains.kotlin.metadata.ProtoBuf.Type? = null
private var field_11_: Int = 0
private var field_13_: org.jetbrains.kotlin.metadata.ProtoBuf.Type? = null
private var field_14_: Int = 0
private var field_1_: Int = 0
private var field_2_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Type.Argument> = emptyList()
private fun ensure_2_Mutable() { if ((mutBitField0_ and 1) == 0) { field_2_ = ArrayList(field_2_); mutBitField0_ = mutBitField0_ or 1 } }
private var field_100_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = emptyList()
private fun ensure_100_Mutable() { if ((mutBitField0_ and 2) == 0) { field_100_ = ArrayList(field_100_); mutBitField0_ = mutBitField0_ or 2 } }
override val defaultInstanceForType: Type get() = defaultInstance
override fun clear(): Builder {
bitField0_ = 0
mutBitField0_ = 0
field_3_ = false
field_4_ = 0
field_5_ = null
field_8_ = 0
field_6_ = 0
field_7_ = 0
field_9_ = 0
field_12_ = 0
field_10_ = null
field_11_ = 0
field_13_ = null
field_14_ = 0
field_1_ = 0
field_2_ = emptyList()
field_100_ = emptyList()
clearExtensions()
unknownFields = ByteString.EMPTY
return this
}
override fun clone(): Builder = Builder().mergeFrom(buildPartial())
override fun build(): Type {
val result = buildPartial()
if (!result.isInitialized()) throw UninitializedMessageException()
return result
}
override fun buildPartial(): Type {
val result = Type(this)
val from_bitField0_ = bitField0_
var to_bitField0_ = 0
if ((from_bitField0_ and 1) != 0) to_bitField0_ = to_bitField0_ or 1
result.field_3_ = field_3_
if ((from_bitField0_ and 2) != 0) to_bitField0_ = to_bitField0_ or 2
result.field_4_ = field_4_
if ((from_bitField0_ and 4) != 0) to_bitField0_ = to_bitField0_ or 4
result.field_5_ = field_5_
if ((from_bitField0_ and 8) != 0) to_bitField0_ = to_bitField0_ or 8
result.field_8_ = field_8_
if ((from_bitField0_ and 16) != 0) to_bitField0_ = to_bitField0_ or 16
result.field_6_ = field_6_
if ((from_bitField0_ and 32) != 0) to_bitField0_ = to_bitField0_ or 32
result.field_7_ = field_7_
if ((from_bitField0_ and 64) != 0) to_bitField0_ = to_bitField0_ or 64
result.field_9_ = field_9_
if ((from_bitField0_ and 128) != 0) to_bitField0_ = to_bitField0_ or 128
result.field_12_ = field_12_
if ((from_bitField0_ and 256) != 0) to_bitField0_ = to_bitField0_ or 256
result.field_10_ = field_10_
if ((from_bitField0_ and 512) != 0) to_bitField0_ = to_bitField0_ or 512
result.field_11_ = field_11_
if ((from_bitField0_ and 1024) != 0) to_bitField0_ = to_bitField0_ or 1024
result.field_13_ = field_13_
if ((from_bitField0_ and 2048) != 0) to_bitField0_ = to_bitField0_ or 2048
result.field_14_ = field_14_
if ((from_bitField0_ and 4096) != 0) to_bitField0_ = to_bitField0_ or 4096
result.field_1_ = field_1_
result.bitField0_ = to_bitField0_
if ((mutBitField0_ and 1) != 0) { field_2_ = GeneratedMessageLite.unmodifiableProtoList(field_2_); mutBitField0_ = mutBitField0_ and -2 }
result.field_2_ = field_2_
if ((mutBitField0_ and 2) != 0) { field_100_ = GeneratedMessageLite.unmodifiableProtoList(field_100_); mutBitField0_ = mutBitField0_ and -3 }
result.field_100_ = field_100_
return result
}
override fun mergeFrom(other: Type): Builder {
if (other === defaultInstance) return this
if (other.hasNullable()) setNullable(other.getNullable())
if (other.hasFlexibleTypeCapabilitiesId()) setFlexibleTypeCapabilitiesId(other.getFlexibleTypeCapabilitiesId())
if (other.hasFlexibleUpperBound()) mergeFlexibleUpperBound(other.getFlexibleUpperBound())
if (other.hasFlexibleUpperBoundId()) setFlexibleUpperBoundId(other.getFlexibleUpperBoundId())
if (other.hasClassName()) setClassName(other.getClassName())
if (other.hasTypeParameter()) setTypeParameter(other.getTypeParameter())
if (other.hasTypeParameterName()) setTypeParameterName(other.getTypeParameterName())
if (other.hasTypeAliasName()) setTypeAliasName(other.getTypeAliasName())
if (other.hasOuterType()) mergeOuterType(other.getOuterType())
if (other.hasOuterTypeId()) setOuterTypeId(other.getOuterTypeId())
if (other.hasAbbreviatedType()) mergeAbbreviatedType(other.getAbbreviatedType())
if (other.hasAbbreviatedTypeId()) setAbbreviatedTypeId(other.getAbbreviatedTypeId())
if (other.hasFlags()) setFlags(other.getFlags())
if (other.field_2_.isNotEmpty()) {
if (field_2_.isEmpty()) { field_2_ = other.field_2_; mutBitField0_ = mutBitField0_ and -2 } else { ensure_2_Mutable(); (field_2_ as MutableList<Any>).addAll(other.field_2_ as Collection<Any>) }
}
if (other.field_100_.isNotEmpty()) {
if (field_100_.isEmpty()) { field_100_ = other.field_100_; mutBitField0_ = mutBitField0_ and -3 } else { ensure_100_Mutable(); (field_100_ as MutableList<Any>).addAll(other.field_100_ as Collection<Any>) }
}
mergeExtensionFields(other)
unknownFields = unknownFields.concat(other.unknownFields)
return this
}
override fun isInitialized(): Boolean {
if ((bitField0_ and 4) != 0 && !getFlexibleUpperBound().isInitialized()) return false
if ((bitField0_ and 256) != 0 && !getOuterType().isInitialized()) return false
if ((bitField0_ and 1024) != 0 && !getAbbreviatedType().isInitialized()) return false
for (i in 0 until field_2_.size) { if (!field_2_[i].isInitialized()) return false }
for (i in 0 until field_100_.size) { if (!field_100_[i].isInitialized()) return false }
if (!extensionsAreInitialized()) return false
return true
}
override fun mergeFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Builder {
var parsedMessage: Type? = null
try {
parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry)
} catch (e: InvalidProtocolBufferException) {
parsedMessage = e.unfinishedMessage as? Type
throw e
} finally {
if (parsedMessage != null) mergeFrom(parsedMessage)
}
return this
}
var `flags`: Int
get() = field_1_
set(value) { bitField0_ = bitField0_ or 4096; field_1_ = value }
fun getFlags(): Int = field_1_
fun hasFlags(): Boolean = (bitField0_ and 4096) != 0
fun setFlags(value: Int): Builder { bitField0_ = bitField0_ or 4096; field_1_ = value; return this }
fun clearFlags(): Builder { bitField0_ = bitField0_ and -4097; field_1_ = 0; return this }
val `argumentList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Type.Argument> get() = if (field_2_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_2_)
val `argumentCount`: Int get() = field_2_.size
fun getArgumentList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Type.Argument> = `argumentList`
fun getArgumentCount(): Int = field_2_.size
fun getArgument(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Type.Argument = field_2_[index]
fun addArgument(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Argument): Builder { ensure_2_Mutable(); (field_2_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Type.Argument>).add(value); return this }
fun setArgument(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Argument): Builder { ensure_2_Mutable(); (field_2_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Type.Argument>)[index] = value; return this }
fun addAllArgument(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.Type.Argument>): Builder { ensure_2_Mutable(); AbstractMessageLite.Builder.addAll(values, field_2_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Type.Argument>); return this }
fun clearArgument(): Builder { field_2_ = emptyList(); mutBitField0_ = mutBitField0_ and -2; return this }
fun addArgument(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Argument.Builder): Builder = addArgument(value.build())
fun setArgument(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Argument.Builder): Builder = setArgument(index, value.build())
var `nullable`: Boolean
get() = field_3_
set(value) { bitField0_ = bitField0_ or 1; field_3_ = value }
fun getNullable(): Boolean = field_3_
fun hasNullable(): Boolean = (bitField0_ and 1) != 0
fun setNullable(value: Boolean): Builder { bitField0_ = bitField0_ or 1; field_3_ = value; return this }
fun clearNullable(): Builder { bitField0_ = bitField0_ and -2; field_3_ = false; return this }
var `flexibleTypeCapabilitiesId`: Int
get() = field_4_
set(value) { bitField0_ = bitField0_ or 2; field_4_ = value }
fun getFlexibleTypeCapabilitiesId(): Int = field_4_
fun hasFlexibleTypeCapabilitiesId(): Boolean = (bitField0_ and 2) != 0
fun setFlexibleTypeCapabilitiesId(value: Int): Builder { bitField0_ = bitField0_ or 2; field_4_ = value; return this }
fun clearFlexibleTypeCapabilitiesId(): Builder { bitField0_ = bitField0_ and -3; field_4_ = 0; return this }
var `flexibleUpperBound`: org.jetbrains.kotlin.metadata.ProtoBuf.Type
get() = field_5_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance
set(value) { field_5_ = value; bitField0_ = bitField0_ or 4 }
fun getFlexibleUpperBound(): org.jetbrains.kotlin.metadata.ProtoBuf.Type = `flexibleUpperBound`
fun hasFlexibleUpperBound(): Boolean = (bitField0_ and 4) != 0
fun setFlexibleUpperBound(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder { field_5_ = value; bitField0_ = bitField0_ or 4; return this }
fun setFlexibleUpperBound(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder): Builder { field_5_ = value.build(); bitField0_ = bitField0_ or 4; return this }
fun mergeFlexibleUpperBound(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder {
val cur = field_5_
field_5_ = if ((bitField0_ and 4) != 0 && cur != null && cur !== org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance) org.jetbrains.kotlin.metadata.ProtoBuf.Type.newBuilder(cur).mergeFrom(value).buildPartial() else value
bitField0_ = bitField0_ or 4
return this
}
fun clearFlexibleUpperBound(): Builder { field_5_ = null; bitField0_ = bitField0_ and -5; return this }
var `className`: Int
get() = field_6_
set(value) { bitField0_ = bitField0_ or 16; field_6_ = value }
fun getClassName(): Int = field_6_
fun hasClassName(): Boolean = (bitField0_ and 16) != 0
fun setClassName(value: Int): Builder { bitField0_ = bitField0_ or 16; field_6_ = value; return this }
fun clearClassName(): Builder { bitField0_ = bitField0_ and -17; field_6_ = 0; return this }
var `typeParameter`: Int
get() = field_7_
set(value) { bitField0_ = bitField0_ or 32; field_7_ = value }
fun getTypeParameter(): Int = field_7_
fun hasTypeParameter(): Boolean = (bitField0_ and 32) != 0
fun setTypeParameter(value: Int): Builder { bitField0_ = bitField0_ or 32; field_7_ = value; return this }
fun clearTypeParameter(): Builder { bitField0_ = bitField0_ and -33; field_7_ = 0; return this }
var `flexibleUpperBoundId`: Int
get() = field_8_
set(value) { bitField0_ = bitField0_ or 8; field_8_ = value }
fun getFlexibleUpperBoundId(): Int = field_8_
fun hasFlexibleUpperBoundId(): Boolean = (bitField0_ and 8) != 0
fun setFlexibleUpperBoundId(value: Int): Builder { bitField0_ = bitField0_ or 8; field_8_ = value; return this }
fun clearFlexibleUpperBoundId(): Builder { bitField0_ = bitField0_ and -9; field_8_ = 0; return this }
var `typeParameterName`: Int
get() = field_9_
set(value) { bitField0_ = bitField0_ or 64; field_9_ = value }
fun getTypeParameterName(): Int = field_9_
fun hasTypeParameterName(): Boolean = (bitField0_ and 64) != 0
fun setTypeParameterName(value: Int): Builder { bitField0_ = bitField0_ or 64; field_9_ = value; return this }
fun clearTypeParameterName(): Builder { bitField0_ = bitField0_ and -65; field_9_ = 0; return this }
var `outerType`: org.jetbrains.kotlin.metadata.ProtoBuf.Type
get() = field_10_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance
set(value) { field_10_ = value; bitField0_ = bitField0_ or 256 }
fun getOuterType(): org.jetbrains.kotlin.metadata.ProtoBuf.Type = `outerType`
fun hasOuterType(): Boolean = (bitField0_ and 256) != 0
fun setOuterType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder { field_10_ = value; bitField0_ = bitField0_ or 256; return this }
fun setOuterType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder): Builder { field_10_ = value.build(); bitField0_ = bitField0_ or 256; return this }
fun mergeOuterType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder {
val cur = field_10_
field_10_ = if ((bitField0_ and 256) != 0 && cur != null && cur !== org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance) org.jetbrains.kotlin.metadata.ProtoBuf.Type.newBuilder(cur).mergeFrom(value).buildPartial() else value
bitField0_ = bitField0_ or 256
return this
}
fun clearOuterType(): Builder { field_10_ = null; bitField0_ = bitField0_ and -257; return this }
var `outerTypeId`: Int
get() = field_11_
set(value) { bitField0_ = bitField0_ or 512; field_11_ = value }
fun getOuterTypeId(): Int = field_11_
fun hasOuterTypeId(): Boolean = (bitField0_ and 512) != 0
fun setOuterTypeId(value: Int): Builder { bitField0_ = bitField0_ or 512; field_11_ = value; return this }
fun clearOuterTypeId(): Builder { bitField0_ = bitField0_ and -513; field_11_ = 0; return this }
var `typeAliasName`: Int
get() = field_12_
set(value) { bitField0_ = bitField0_ or 128; field_12_ = value }
fun getTypeAliasName(): Int = field_12_
fun hasTypeAliasName(): Boolean = (bitField0_ and 128) != 0
fun setTypeAliasName(value: Int): Builder { bitField0_ = bitField0_ or 128; field_12_ = value; return this }
fun clearTypeAliasName(): Builder { bitField0_ = bitField0_ and -129; field_12_ = 0; return this }
var `abbreviatedType`: org.jetbrains.kotlin.metadata.ProtoBuf.Type
get() = field_13_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance
set(value) { field_13_ = value; bitField0_ = bitField0_ or 1024 }
fun getAbbreviatedType(): org.jetbrains.kotlin.metadata.ProtoBuf.Type = `abbreviatedType`
fun hasAbbreviatedType(): Boolean = (bitField0_ and 1024) != 0
fun setAbbreviatedType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder { field_13_ = value; bitField0_ = bitField0_ or 1024; return this }
fun setAbbreviatedType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder): Builder { field_13_ = value.build(); bitField0_ = bitField0_ or 1024; return this }
fun mergeAbbreviatedType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder {
val cur = field_13_
field_13_ = if ((bitField0_ and 1024) != 0 && cur != null && cur !== org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance) org.jetbrains.kotlin.metadata.ProtoBuf.Type.newBuilder(cur).mergeFrom(value).buildPartial() else value
bitField0_ = bitField0_ or 1024
return this
}
fun clearAbbreviatedType(): Builder { field_13_ = null; bitField0_ = bitField0_ and -1025; return this }
var `abbreviatedTypeId`: Int
get() = field_14_
set(value) { bitField0_ = bitField0_ or 2048; field_14_ = value }
fun getAbbreviatedTypeId(): Int = field_14_
fun hasAbbreviatedTypeId(): Boolean = (bitField0_ and 2048) != 0
fun setAbbreviatedTypeId(value: Int): Builder { bitField0_ = bitField0_ or 2048; field_14_ = value; return this }
fun clearAbbreviatedTypeId(): Builder { bitField0_ = bitField0_ and -2049; field_14_ = 0; return this }
val `annotationList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> get() = if (field_100_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_100_)
val `annotationCount`: Int get() = field_100_.size
fun getAnnotationList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = `annotationList`
fun getAnnotationCount(): Int = field_100_.size
fun getAnnotation(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation = field_100_[index]
fun addAnnotation(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation): Builder { ensure_100_Mutable(); (field_100_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>).add(value); return this }
fun setAnnotation(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation): Builder { ensure_100_Mutable(); (field_100_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>)[index] = value; return this }
fun addAllAnnotation(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>): Builder { ensure_100_Mutable(); AbstractMessageLite.Builder.addAll(values, field_100_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>); return this }
fun clearAnnotation(): Builder { field_100_ = emptyList(); mutBitField0_ = mutBitField0_ and -3; return this }
fun addAnnotation(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Builder): Builder = addAnnotation(value.build())
fun setAnnotation(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Builder): Builder = setAnnotation(index, value.build())
}
companion object {
val defaultInstance: Type = Type()
fun getDefaultInstance(): Type = defaultInstance
fun newBuilder(): Builder = Builder()
fun newBuilder(prototype: Type): Builder = Builder().mergeFrom(prototype)
val PARSER: Parser<Type> = object : AbstractParser<Type>() {
override fun parsePartialFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Type =
Type(input, extensionRegistry)
}
fun parseFrom(bytes: ByteArray): Type = PARSER.parseFrom(bytes)
fun parseFrom(bytes: ByteArray, registry: ExtensionRegistryLite): Type = PARSER.parseFrom(bytes, registry)
fun parseFrom(bytes: ByteString): Type { val input = bytes.newCodedInput(); val msg = PARSER.parsePartialFrom(input, ExtensionRegistryLite.getEmptyRegistry()); input.checkLastTagWas(0); if (!msg.isInitialized()) throw UninitializedMessageException().asInvalidProtocolBufferException().setUnfinishedMessage(msg); return msg }
fun parseFrom(input: CodedInputStream): Type = PARSER.parseFrom(input)
fun parseFrom(input: CodedInputStream, registry: ExtensionRegistryLite): Type = PARSER.parseFrom(input, registry)
const val FLAGS_FIELD_NUMBER = 1
const val ARGUMENT_FIELD_NUMBER = 2
const val NULLABLE_FIELD_NUMBER = 3
const val FLEXIBLE_TYPE_CAPABILITIES_ID_FIELD_NUMBER = 4
const val FLEXIBLE_UPPER_BOUND_FIELD_NUMBER = 5
const val CLASS_NAME_FIELD_NUMBER = 6
const val TYPE_PARAMETER_FIELD_NUMBER = 7
const val FLEXIBLE_UPPER_BOUND_ID_FIELD_NUMBER = 8
const val TYPE_PARAMETER_NAME_FIELD_NUMBER = 9
const val OUTER_TYPE_FIELD_NUMBER = 10
const val OUTER_TYPE_ID_FIELD_NUMBER = 11
const val TYPE_ALIAS_NAME_FIELD_NUMBER = 12
const val ABBREVIATED_TYPE_FIELD_NUMBER = 13
const val ABBREVIATED_TYPE_ID_FIELD_NUMBER = 14
const val ANNOTATION_FIELD_NUMBER = 100
}}
class TypeParameter : GeneratedMessageLite.ExtendableMessage<TypeParameter> {
enum class Variance(override val number: Int) : Internal.EnumLite {
IN(0),
OUT(1),
INV(2),
; companion object : Internal.EnumLiteMap<Variance> {
override fun findValueByNumber(number: Int): Variance? = valueOf(number)
fun valueOf(number: Int): Variance? = when (number) {
0 -> IN
1 -> OUT
2 -> INV
else -> null
}
fun internalGetValueMap(): Internal.EnumLiteMap<Variance> = this
}}
internal var bitField0_: Int = 0
internal var field_1_: Int = 0
internal var field_2_: Int = 0
internal var field_3_: Boolean = false
internal var field_4_: org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter.Variance = org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter.Variance.INV
internal var field_5_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> = emptyList()
internal var field_6_: List<Int> = emptyList()
private var packedSize_6_: Int = -1
internal var field_100_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = emptyList()
private var memoizedIsInitialized: Byte = -1
private var memoizedSerializedSize: Int = -1
private constructor()
private constructor(builder: Builder) {
this.unknownFields = builder.unknownFields
copyExtensionsFromBuilder(builder)
}
private constructor(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite) {
var mutBit0_ = 0
var unknownBuffer: ByteString.Output? = null
var unknownOut: CodedOutputStream? = null
fun unknownOutput(): CodedOutputStream {
var out = unknownOut
if (out == null) { val buf = ByteString.newOutput(); unknownBuffer = buf; out = CodedOutputStream.newInstance(buf); unknownOut = out }
return out
}
try {
var done = false
while (!done) {
val tag = input.readTag()
when (tag) {
0 -> done = true
8 -> { bitField0_ = bitField0_ or 1; field_1_ = input.readInt32() }
16 -> { bitField0_ = bitField0_ or 2; field_2_ = input.readInt32() }
24 -> { bitField0_ = bitField0_ or 4; field_3_ = input.readBool() }
32 -> {
val raw = input.readEnum()
val parsed = org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter.Variance.valueOf(raw)
if (parsed == null) { unknownOutput().writeRawVarint32(32); unknownOutput().writeRawVarint32(raw) } else {
bitField0_ = bitField0_ or 8; field_4_ = parsed
}}
42 -> {
if ((mutBit0_ and 1) == 0) { field_5_ = ArrayList(); mutBit0_ = mutBit0_ or 1 }
(field_5_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Type>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Type.PARSER, extensionRegistry))
}
48 -> {
if ((mutBit0_ and 2) == 0) { field_6_ = ArrayList(); mutBit0_ = mutBit0_ or 2 }
(field_6_ as MutableList<Int>).add(input.readInt32())
}
50 -> {
val length = input.readRawVarint32()
val oldLimit = input.pushLimit(length)
try {
if (length > 0 && (mutBit0_ and 2) == 0) { field_6_ = ArrayList(); mutBit0_ = mutBit0_ or 2 }
while (input.getBytesUntilLimit() > 0) { (field_6_ as MutableList<Int>).add(input.readInt32()) }
} finally { input.popLimit(oldLimit) }
}
802 -> {
if ((mutBit0_ and 4) == 0) { field_100_ = ArrayList(); mutBit0_ = mutBit0_ or 4 }
(field_100_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.PARSER, extensionRegistry))
}
else -> if (!parseUnknownField(input, unknownOutput(), extensionRegistry, tag)) done = true
}}
} catch (e: InvalidProtocolBufferException) {
throw e.setUnfinishedMessage(this)
} finally {
if ((mutBit0_ and 1) != 0) field_5_ = GeneratedMessageLite.unmodifiableProtoList(field_5_)
if ((mutBit0_ and 2) != 0) field_6_ = GeneratedMessageLite.unmodifiableProtoList(field_6_)
if ((mutBit0_ and 4) != 0) field_100_ = GeneratedMessageLite.unmodifiableProtoList(field_100_)
if (unknownBuffer != null) { unknownOut!!.flush(); unknownFields = unknownBuffer.toByteString() }
makeExtensionsImmutable()
}}
val `id`: Int get() = field_1_
fun getId(): Int = `id`
fun hasId(): Boolean = (bitField0_ and 1) != 0
val `name`: Int get() = field_2_
fun getName(): Int = `name`
fun hasName(): Boolean = (bitField0_ and 2) != 0
val `reified`: Boolean get() = field_3_
fun getReified(): Boolean = `reified`
fun hasReified(): Boolean = (bitField0_ and 4) != 0
val `variance`: org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter.Variance get() = field_4_
fun getVariance(): org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter.Variance = `variance`
fun hasVariance(): Boolean = (bitField0_ and 8) != 0
val `upperBoundList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> get() = field_5_
val `upperBoundCount`: Int get() = field_5_.size
fun getUpperBoundList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> = field_5_
fun getUpperBoundCount(): Int = field_5_.size
fun getUpperBound(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Type = field_5_[index]
val `upperBoundIdList`: List<Int> get() = field_6_
val `upperBoundIdCount`: Int get() = field_6_.size
fun getUpperBoundIdList(): List<Int> = field_6_
fun getUpperBoundIdCount(): Int = field_6_.size
fun getUpperBoundId(index: Int): Int = field_6_[index]
val `annotationList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> get() = field_100_
val `annotationCount`: Int get() = field_100_.size
fun getAnnotationList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = field_100_
fun getAnnotationCount(): Int = field_100_.size
fun getAnnotation(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation = field_100_[index]
override fun isInitialized(): Boolean {
val isInitialized = memoizedIsInitialized.toInt()
if (isInitialized == 1) return true
if (isInitialized == 0) return false
if ((bitField0_ and 1) == 0) { memoizedIsInitialized = 0; return false }
if ((bitField0_ and 2) == 0) { memoizedIsInitialized = 0; return false }
for (i in 0 until field_5_.size) { if (!field_5_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
for (i in 0 until field_100_.size) { if (!field_100_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
if (!extensionsAreInitialized()) { memoizedIsInitialized = 0; return false }
memoizedIsInitialized = 1
return true
}
override fun writeTo(output: CodedOutputStream) {
serializedSize
val extensionWriter = if (hasAnyExtensions()) newExtensionWriter() else null
extensionWriter?.writeUntil(1, output)
if ((bitField0_ and 1) != 0) { output.writeRawVarint32(8); output.writeInt32NoTag(field_1_) }
extensionWriter?.writeUntil(2, output)
if ((bitField0_ and 2) != 0) { output.writeRawVarint32(16); output.writeInt32NoTag(field_2_) }
extensionWriter?.writeUntil(3, output)
if ((bitField0_ and 4) != 0) { output.writeRawVarint32(24); output.writeRawByte(if (field_3_) 1 else 0) }
extensionWriter?.writeUntil(4, output)
if ((bitField0_ and 8) != 0) { output.writeRawVarint32(32); output.writeInt32NoTag((field_4_).number) }
extensionWriter?.writeUntil(5, output)
for (i in 0 until field_5_.size) { output.writeRawVarint32(42); output.writeMessageNoTag(field_5_[i]) }
extensionWriter?.writeUntil(6, output)
if (field_6_.isNotEmpty()) {
output.writeRawVarint32(50)
output.writeRawVarint32(packedSize_6_)
for (i in 0 until field_6_.size) { output.writeInt32NoTag(field_6_[i]) }
}
extensionWriter?.writeUntil(100, output)
for (i in 0 until field_100_.size) { output.writeRawVarint32(802); output.writeMessageNoTag(field_100_[i]) }
extensionWriter?.writeUntil(Int.MAX_VALUE, output)
if (!unknownFields.isEmpty()) unknownFields.writeTo(output)
}
override val serializedSize: Int get() {
val cached = memoizedSerializedSize
if (cached != -1) return cached
var size = 0
if ((bitField0_ and 1) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_1_)
if ((bitField0_ and 2) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_2_)
if ((bitField0_ and 4) != 0) size += 2
if ((bitField0_ and 8) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag((field_4_).number)
for (i in 0 until field_5_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_5_[i]) }
if (field_6_.isNotEmpty()) {
var dataSize = 0
for (i in 0 until field_6_.size) { dataSize += CodedOutputStream.computeInt32SizeNoTag(field_6_[i]) }
size += 1 + CodedOutputStream.computeRawVarint32Size(dataSize) + dataSize
packedSize_6_ = dataSize
} else { packedSize_6_ = 0 }
for (i in 0 until field_100_.size) { size += 2 + CodedOutputStream.computeMessageSizeNoTag(field_100_[i]) }
size += extensionsSerializedSize()
size += unknownFields.size()
memoizedSerializedSize = size
return size
}
override val defaultInstanceForType: TypeParameter get() = defaultInstance
override fun newBuilderForType(): Builder = Builder()
override fun toBuilder(): Builder = Builder().mergeFrom(this)
class Builder : GeneratedMessageLite.ExtendableBuilder<TypeParameter, Builder>() {
private var bitField0_: Int = 0
private var mutBitField0_: Int = 0
private var field_1_: Int = 0
private var field_2_: Int = 0
private var field_3_: Boolean = false
private var field_4_: org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter.Variance = org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter.Variance.INV
private var field_5_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> = emptyList()
private fun ensure_5_Mutable() { if ((mutBitField0_ and 1) == 0) { field_5_ = ArrayList(field_5_); mutBitField0_ = mutBitField0_ or 1 } }
private var field_6_: List<Int> = emptyList()
private fun ensure_6_Mutable() { if ((mutBitField0_ and 2) == 0) { field_6_ = ArrayList(field_6_); mutBitField0_ = mutBitField0_ or 2 } }
private var field_100_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = emptyList()
private fun ensure_100_Mutable() { if ((mutBitField0_ and 4) == 0) { field_100_ = ArrayList(field_100_); mutBitField0_ = mutBitField0_ or 4 } }
override val defaultInstanceForType: TypeParameter get() = defaultInstance
override fun clear(): Builder {
bitField0_ = 0
mutBitField0_ = 0
field_1_ = 0
field_2_ = 0
field_3_ = false
field_4_ = org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter.Variance.INV
field_5_ = emptyList()
field_6_ = emptyList()
field_100_ = emptyList()
clearExtensions()
unknownFields = ByteString.EMPTY
return this
}
override fun clone(): Builder = Builder().mergeFrom(buildPartial())
override fun build(): TypeParameter {
val result = buildPartial()
if (!result.isInitialized()) throw UninitializedMessageException()
return result
}
override fun buildPartial(): TypeParameter {
val result = TypeParameter(this)
val from_bitField0_ = bitField0_
var to_bitField0_ = 0
if ((from_bitField0_ and 1) != 0) to_bitField0_ = to_bitField0_ or 1
result.field_1_ = field_1_
if ((from_bitField0_ and 2) != 0) to_bitField0_ = to_bitField0_ or 2
result.field_2_ = field_2_
if ((from_bitField0_ and 4) != 0) to_bitField0_ = to_bitField0_ or 4
result.field_3_ = field_3_
if ((from_bitField0_ and 8) != 0) to_bitField0_ = to_bitField0_ or 8
result.field_4_ = field_4_
result.bitField0_ = to_bitField0_
if ((mutBitField0_ and 1) != 0) { field_5_ = GeneratedMessageLite.unmodifiableProtoList(field_5_); mutBitField0_ = mutBitField0_ and -2 }
result.field_5_ = field_5_
if ((mutBitField0_ and 2) != 0) { field_6_ = GeneratedMessageLite.unmodifiableProtoList(field_6_); mutBitField0_ = mutBitField0_ and -3 }
result.field_6_ = field_6_
if ((mutBitField0_ and 4) != 0) { field_100_ = GeneratedMessageLite.unmodifiableProtoList(field_100_); mutBitField0_ = mutBitField0_ and -5 }
result.field_100_ = field_100_
return result
}
override fun mergeFrom(other: TypeParameter): Builder {
if (other === defaultInstance) return this
if (other.hasId()) setId(other.getId())
if (other.hasName()) setName(other.getName())
if (other.hasReified()) setReified(other.getReified())
if (other.hasVariance()) setVariance(other.getVariance())
if (other.field_5_.isNotEmpty()) {
if (field_5_.isEmpty()) { field_5_ = other.field_5_; mutBitField0_ = mutBitField0_ and -2 } else { ensure_5_Mutable(); (field_5_ as MutableList<Any>).addAll(other.field_5_ as Collection<Any>) }
}
if (other.field_6_.isNotEmpty()) {
if (field_6_.isEmpty()) { field_6_ = other.field_6_; mutBitField0_ = mutBitField0_ and -3 } else { ensure_6_Mutable(); (field_6_ as MutableList<Any>).addAll(other.field_6_ as Collection<Any>) }
}
if (other.field_100_.isNotEmpty()) {
if (field_100_.isEmpty()) { field_100_ = other.field_100_; mutBitField0_ = mutBitField0_ and -5 } else { ensure_100_Mutable(); (field_100_ as MutableList<Any>).addAll(other.field_100_ as Collection<Any>) }
}
mergeExtensionFields(other)
unknownFields = unknownFields.concat(other.unknownFields)
return this
}
override fun isInitialized(): Boolean {
if ((bitField0_ and 1) == 0) return false
if ((bitField0_ and 2) == 0) return false
for (i in 0 until field_5_.size) { if (!field_5_[i].isInitialized()) return false }
for (i in 0 until field_100_.size) { if (!field_100_[i].isInitialized()) return false }
if (!extensionsAreInitialized()) return false
return true
}
override fun mergeFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Builder {
var parsedMessage: TypeParameter? = null
try {
parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry)
} catch (e: InvalidProtocolBufferException) {
parsedMessage = e.unfinishedMessage as? TypeParameter
throw e
} finally {
if (parsedMessage != null) mergeFrom(parsedMessage)
}
return this
}
var `id`: Int
get() = field_1_
set(value) { bitField0_ = bitField0_ or 1; field_1_ = value }
fun getId(): Int = field_1_
fun hasId(): Boolean = (bitField0_ and 1) != 0
fun setId(value: Int): Builder { bitField0_ = bitField0_ or 1; field_1_ = value; return this }
fun clearId(): Builder { bitField0_ = bitField0_ and -2; field_1_ = 0; return this }
var `name`: Int
get() = field_2_
set(value) { bitField0_ = bitField0_ or 2; field_2_ = value }
fun getName(): Int = field_2_
fun hasName(): Boolean = (bitField0_ and 2) != 0
fun setName(value: Int): Builder { bitField0_ = bitField0_ or 2; field_2_ = value; return this }
fun clearName(): Builder { bitField0_ = bitField0_ and -3; field_2_ = 0; return this }
var `reified`: Boolean
get() = field_3_
set(value) { bitField0_ = bitField0_ or 4; field_3_ = value }
fun getReified(): Boolean = field_3_
fun hasReified(): Boolean = (bitField0_ and 4) != 0
fun setReified(value: Boolean): Builder { bitField0_ = bitField0_ or 4; field_3_ = value; return this }
fun clearReified(): Builder { bitField0_ = bitField0_ and -5; field_3_ = false; return this }
var `variance`: org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter.Variance
get() = field_4_
set(value) { bitField0_ = bitField0_ or 8; field_4_ = value }
fun getVariance(): org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter.Variance = field_4_
fun hasVariance(): Boolean = (bitField0_ and 8) != 0
fun setVariance(value: org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter.Variance): Builder { bitField0_ = bitField0_ or 8; field_4_ = value; return this }
fun clearVariance(): Builder { bitField0_ = bitField0_ and -9; field_4_ = org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter.Variance.INV; return this }
val `upperBoundList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> get() = if (field_5_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_5_)
val `upperBoundCount`: Int get() = field_5_.size
fun getUpperBoundList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> = `upperBoundList`
fun getUpperBoundCount(): Int = field_5_.size
fun getUpperBound(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Type = field_5_[index]
fun addUpperBound(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder { ensure_5_Mutable(); (field_5_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Type>).add(value); return this }
fun setUpperBound(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder { ensure_5_Mutable(); (field_5_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Type>)[index] = value; return this }
fun addAllUpperBound(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.Type>): Builder { ensure_5_Mutable(); AbstractMessageLite.Builder.addAll(values, field_5_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Type>); return this }
fun clearUpperBound(): Builder { field_5_ = emptyList(); mutBitField0_ = mutBitField0_ and -2; return this }
fun addUpperBound(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder): Builder = addUpperBound(value.build())
fun setUpperBound(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder): Builder = setUpperBound(index, value.build())
val `upperBoundIdList`: List<Int> get() = if (field_6_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_6_)
val `upperBoundIdCount`: Int get() = field_6_.size
fun getUpperBoundIdList(): List<Int> = `upperBoundIdList`
fun getUpperBoundIdCount(): Int = field_6_.size
fun getUpperBoundId(index: Int): Int = field_6_[index]
fun addUpperBoundId(value: Int): Builder { ensure_6_Mutable(); (field_6_ as MutableList<Int>).add(value); return this }
fun setUpperBoundId(index: Int, value: Int): Builder { ensure_6_Mutable(); (field_6_ as MutableList<Int>)[index] = value; return this }
fun addAllUpperBoundId(values: Iterable<Int>): Builder { ensure_6_Mutable(); AbstractMessageLite.Builder.addAll(values, field_6_ as MutableList<Int>); return this }
fun clearUpperBoundId(): Builder { field_6_ = emptyList(); mutBitField0_ = mutBitField0_ and -3; return this }
val `annotationList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> get() = if (field_100_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_100_)
val `annotationCount`: Int get() = field_100_.size
fun getAnnotationList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = `annotationList`
fun getAnnotationCount(): Int = field_100_.size
fun getAnnotation(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation = field_100_[index]
fun addAnnotation(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation): Builder { ensure_100_Mutable(); (field_100_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>).add(value); return this }
fun setAnnotation(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation): Builder { ensure_100_Mutable(); (field_100_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>)[index] = value; return this }
fun addAllAnnotation(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>): Builder { ensure_100_Mutable(); AbstractMessageLite.Builder.addAll(values, field_100_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>); return this }
fun clearAnnotation(): Builder { field_100_ = emptyList(); mutBitField0_ = mutBitField0_ and -5; return this }
fun addAnnotation(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Builder): Builder = addAnnotation(value.build())
fun setAnnotation(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Builder): Builder = setAnnotation(index, value.build())
}
companion object {
val defaultInstance: TypeParameter = TypeParameter()
fun getDefaultInstance(): TypeParameter = defaultInstance
fun newBuilder(): Builder = Builder()
fun newBuilder(prototype: TypeParameter): Builder = Builder().mergeFrom(prototype)
val PARSER: Parser<TypeParameter> = object : AbstractParser<TypeParameter>() {
override fun parsePartialFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): TypeParameter =
TypeParameter(input, extensionRegistry)
}
fun parseFrom(bytes: ByteArray): TypeParameter = PARSER.parseFrom(bytes)
fun parseFrom(bytes: ByteArray, registry: ExtensionRegistryLite): TypeParameter = PARSER.parseFrom(bytes, registry)
fun parseFrom(bytes: ByteString): TypeParameter { val input = bytes.newCodedInput(); val msg = PARSER.parsePartialFrom(input, ExtensionRegistryLite.getEmptyRegistry()); input.checkLastTagWas(0); if (!msg.isInitialized()) throw UninitializedMessageException().asInvalidProtocolBufferException().setUnfinishedMessage(msg); return msg }
fun parseFrom(input: CodedInputStream): TypeParameter = PARSER.parseFrom(input)
fun parseFrom(input: CodedInputStream, registry: ExtensionRegistryLite): TypeParameter = PARSER.parseFrom(input, registry)
const val ID_FIELD_NUMBER = 1
const val NAME_FIELD_NUMBER = 2
const val REIFIED_FIELD_NUMBER = 3
const val VARIANCE_FIELD_NUMBER = 4
const val UPPER_BOUND_FIELD_NUMBER = 5
const val UPPER_BOUND_ID_FIELD_NUMBER = 6
const val ANNOTATION_FIELD_NUMBER = 100
}}
class Class : GeneratedMessageLite.ExtendableMessage<Class> {
enum class Kind(override val number: Int) : Internal.EnumLite {
CLASS(0),
INTERFACE(1),
ENUM_CLASS(2),
ENUM_ENTRY(3),
ANNOTATION_CLASS(4),
OBJECT(5),
COMPANION_OBJECT(6),
; companion object : Internal.EnumLiteMap<Kind> {
override fun findValueByNumber(number: Int): Kind? = valueOf(number)
fun valueOf(number: Int): Kind? = when (number) {
0 -> CLASS
1 -> INTERFACE
2 -> ENUM_CLASS
3 -> ENUM_ENTRY
4 -> ANNOTATION_CLASS
5 -> OBJECT
6 -> COMPANION_OBJECT
else -> null
}
fun internalGetValueMap(): Internal.EnumLiteMap<Kind> = this
}}
internal var bitField0_: Int = 0
internal var field_1_: Int = 6
internal var field_3_: Int = 0
internal var field_4_: Int = 0
internal var field_17_: Int = 0
internal var field_18_: org.jetbrains.kotlin.metadata.ProtoBuf.Type? = null
internal var field_19_: Int = 0
internal var field_30_: org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable? = null
internal var field_32_: org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirementTable? = null
internal var field_5_: List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter> = emptyList()
internal var field_6_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> = emptyList()
internal var field_2_: List<Int> = emptyList()
private var packedSize_2_: Int = -1
internal var field_7_: List<Int> = emptyList()
private var packedSize_7_: Int = -1
internal var field_20_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> = emptyList()
internal var field_21_: List<Int> = emptyList()
private var packedSize_21_: Int = -1
internal var field_8_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Constructor> = emptyList()
internal var field_9_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Function> = emptyList()
internal var field_10_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Property> = emptyList()
internal var field_11_: List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias> = emptyList()
internal var field_13_: List<org.jetbrains.kotlin.metadata.ProtoBuf.EnumEntry> = emptyList()
internal var field_16_: List<Int> = emptyList()
private var packedSize_16_: Int = -1
internal var field_25_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = emptyList()
internal var field_31_: List<Int> = emptyList()
internal var field_33_: List<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData> = emptyList()
private var memoizedIsInitialized: Byte = -1
private var memoizedSerializedSize: Int = -1
private constructor()
private constructor(builder: Builder) {
this.unknownFields = builder.unknownFields
copyExtensionsFromBuilder(builder)
}
private constructor(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite) {
var mutBit0_ = 0
var unknownBuffer: ByteString.Output? = null
var unknownOut: CodedOutputStream? = null
fun unknownOutput(): CodedOutputStream {
var out = unknownOut
if (out == null) { val buf = ByteString.newOutput(); unknownBuffer = buf; out = CodedOutputStream.newInstance(buf); unknownOut = out }
return out
}
try {
var done = false
while (!done) {
val tag = input.readTag()
when (tag) {
0 -> done = true
8 -> { bitField0_ = bitField0_ or 1; field_1_ = input.readInt32() }
16 -> {
if ((mutBit0_ and 4) == 0) { field_2_ = ArrayList(); mutBit0_ = mutBit0_ or 4 }
(field_2_ as MutableList<Int>).add(input.readInt32())
}
18 -> {
val length = input.readRawVarint32()
val oldLimit = input.pushLimit(length)
try {
if (length > 0 && (mutBit0_ and 4) == 0) { field_2_ = ArrayList(); mutBit0_ = mutBit0_ or 4 }
while (input.getBytesUntilLimit() > 0) { (field_2_ as MutableList<Int>).add(input.readInt32()) }
} finally { input.popLimit(oldLimit) }
}
24 -> { bitField0_ = bitField0_ or 2; field_3_ = input.readInt32() }
32 -> { bitField0_ = bitField0_ or 4; field_4_ = input.readInt32() }
42 -> {
if ((mutBit0_ and 1) == 0) { field_5_ = ArrayList(); mutBit0_ = mutBit0_ or 1 }
(field_5_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter.PARSER, extensionRegistry))
}
50 -> {
if ((mutBit0_ and 2) == 0) { field_6_ = ArrayList(); mutBit0_ = mutBit0_ or 2 }
(field_6_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Type>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Type.PARSER, extensionRegistry))
}
56 -> {
if ((mutBit0_ and 8) == 0) { field_7_ = ArrayList(); mutBit0_ = mutBit0_ or 8 }
(field_7_ as MutableList<Int>).add(input.readInt32())
}
58 -> {
val length = input.readRawVarint32()
val oldLimit = input.pushLimit(length)
try {
if (length > 0 && (mutBit0_ and 8) == 0) { field_7_ = ArrayList(); mutBit0_ = mutBit0_ or 8 }
while (input.getBytesUntilLimit() > 0) { (field_7_ as MutableList<Int>).add(input.readInt32()) }
} finally { input.popLimit(oldLimit) }
}
66 -> {
if ((mutBit0_ and 64) == 0) { field_8_ = ArrayList(); mutBit0_ = mutBit0_ or 64 }
(field_8_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Constructor>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Constructor.PARSER, extensionRegistry))
}
74 -> {
if ((mutBit0_ and 128) == 0) { field_9_ = ArrayList(); mutBit0_ = mutBit0_ or 128 }
(field_9_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Function>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Function.PARSER, extensionRegistry))
}
82 -> {
if ((mutBit0_ and 256) == 0) { field_10_ = ArrayList(); mutBit0_ = mutBit0_ or 256 }
(field_10_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Property>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Property.PARSER, extensionRegistry))
}
90 -> {
if ((mutBit0_ and 512) == 0) { field_11_ = ArrayList(); mutBit0_ = mutBit0_ or 512 }
(field_11_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias.PARSER, extensionRegistry))
}
106 -> {
if ((mutBit0_ and 1024) == 0) { field_13_ = ArrayList(); mutBit0_ = mutBit0_ or 1024 }
(field_13_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.EnumEntry>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.EnumEntry.PARSER, extensionRegistry))
}
128 -> {
if ((mutBit0_ and 2048) == 0) { field_16_ = ArrayList(); mutBit0_ = mutBit0_ or 2048 }
(field_16_ as MutableList<Int>).add(input.readInt32())
}
130 -> {
val length = input.readRawVarint32()
val oldLimit = input.pushLimit(length)
try {
if (length > 0 && (mutBit0_ and 2048) == 0) { field_16_ = ArrayList(); mutBit0_ = mutBit0_ or 2048 }
while (input.getBytesUntilLimit() > 0) { (field_16_ as MutableList<Int>).add(input.readInt32()) }
} finally { input.popLimit(oldLimit) }
}
136 -> { bitField0_ = bitField0_ or 8; field_17_ = input.readInt32() }
146 -> {
var subBuilder: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder? = null
if ((bitField0_ and 16) != 0) subBuilder = field_18_?.toBuilder()
val parsed = input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Type.PARSER, extensionRegistry)
field_18_ = if (subBuilder != null) subBuilder.mergeFrom(parsed).buildPartial() else parsed
bitField0_ = bitField0_ or 16
}
152 -> { bitField0_ = bitField0_ or 32; field_19_ = input.readInt32() }
162 -> {
if ((mutBit0_ and 16) == 0) { field_20_ = ArrayList(); mutBit0_ = mutBit0_ or 16 }
(field_20_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Type>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Type.PARSER, extensionRegistry))
}
168 -> {
if ((mutBit0_ and 32) == 0) { field_21_ = ArrayList(); mutBit0_ = mutBit0_ or 32 }
(field_21_ as MutableList<Int>).add(input.readInt32())
}
170 -> {
val length = input.readRawVarint32()
val oldLimit = input.pushLimit(length)
try {
if (length > 0 && (mutBit0_ and 32) == 0) { field_21_ = ArrayList(); mutBit0_ = mutBit0_ or 32 }
while (input.getBytesUntilLimit() > 0) { (field_21_ as MutableList<Int>).add(input.readInt32()) }
} finally { input.popLimit(oldLimit) }
}
202 -> {
if ((mutBit0_ and 4096) == 0) { field_25_ = ArrayList(); mutBit0_ = mutBit0_ or 4096 }
(field_25_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.PARSER, extensionRegistry))
}
242 -> {
var subBuilder: org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable.Builder? = null
if ((bitField0_ and 64) != 0) subBuilder = field_30_?.toBuilder()
val parsed = input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable.PARSER, extensionRegistry)
field_30_ = if (subBuilder != null) subBuilder.mergeFrom(parsed).buildPartial() else parsed
bitField0_ = bitField0_ or 64
}
248 -> {
if ((mutBit0_ and 8192) == 0) { field_31_ = ArrayList(); mutBit0_ = mutBit0_ or 8192 }
(field_31_ as MutableList<Int>).add(input.readInt32())
}
250 -> {
val length = input.readRawVarint32()
val oldLimit = input.pushLimit(length)
try {
if (length > 0 && (mutBit0_ and 8192) == 0) { field_31_ = ArrayList(); mutBit0_ = mutBit0_ or 8192 }
while (input.getBytesUntilLimit() > 0) { (field_31_ as MutableList<Int>).add(input.readInt32()) }
} finally { input.popLimit(oldLimit) }
}
258 -> {
var subBuilder: org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirementTable.Builder? = null
if ((bitField0_ and 128) != 0) subBuilder = field_32_?.toBuilder()
val parsed = input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirementTable.PARSER, extensionRegistry)
field_32_ = if (subBuilder != null) subBuilder.mergeFrom(parsed).buildPartial() else parsed
bitField0_ = bitField0_ or 128
}
266 -> {
if ((mutBit0_ and 16384) == 0) { field_33_ = ArrayList(); mutBit0_ = mutBit0_ or 16384 }
(field_33_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData.PARSER, extensionRegistry))
}
else -> if (!parseUnknownField(input, unknownOutput(), extensionRegistry, tag)) done = true
}}
} catch (e: InvalidProtocolBufferException) {
throw e.setUnfinishedMessage(this)
} finally {
if ((mutBit0_ and 1) != 0) field_5_ = GeneratedMessageLite.unmodifiableProtoList(field_5_)
if ((mutBit0_ and 2) != 0) field_6_ = GeneratedMessageLite.unmodifiableProtoList(field_6_)
if ((mutBit0_ and 4) != 0) field_2_ = GeneratedMessageLite.unmodifiableProtoList(field_2_)
if ((mutBit0_ and 8) != 0) field_7_ = GeneratedMessageLite.unmodifiableProtoList(field_7_)
if ((mutBit0_ and 16) != 0) field_20_ = GeneratedMessageLite.unmodifiableProtoList(field_20_)
if ((mutBit0_ and 32) != 0) field_21_ = GeneratedMessageLite.unmodifiableProtoList(field_21_)
if ((mutBit0_ and 64) != 0) field_8_ = GeneratedMessageLite.unmodifiableProtoList(field_8_)
if ((mutBit0_ and 128) != 0) field_9_ = GeneratedMessageLite.unmodifiableProtoList(field_9_)
if ((mutBit0_ and 256) != 0) field_10_ = GeneratedMessageLite.unmodifiableProtoList(field_10_)
if ((mutBit0_ and 512) != 0) field_11_ = GeneratedMessageLite.unmodifiableProtoList(field_11_)
if ((mutBit0_ and 1024) != 0) field_13_ = GeneratedMessageLite.unmodifiableProtoList(field_13_)
if ((mutBit0_ and 2048) != 0) field_16_ = GeneratedMessageLite.unmodifiableProtoList(field_16_)
if ((mutBit0_ and 4096) != 0) field_25_ = GeneratedMessageLite.unmodifiableProtoList(field_25_)
if ((mutBit0_ and 8192) != 0) field_31_ = GeneratedMessageLite.unmodifiableProtoList(field_31_)
if ((mutBit0_ and 16384) != 0) field_33_ = GeneratedMessageLite.unmodifiableProtoList(field_33_)
if (unknownBuffer != null) { unknownOut!!.flush(); unknownFields = unknownBuffer.toByteString() }
makeExtensionsImmutable()
}}
val `flags`: Int get() = field_1_
fun getFlags(): Int = `flags`
fun hasFlags(): Boolean = (bitField0_ and 1) != 0
val `supertypeIdList`: List<Int> get() = field_2_
val `supertypeIdCount`: Int get() = field_2_.size
fun getSupertypeIdList(): List<Int> = field_2_
fun getSupertypeIdCount(): Int = field_2_.size
fun getSupertypeId(index: Int): Int = field_2_[index]
val `fqName`: Int get() = field_3_
fun getFqName(): Int = `fqName`
fun hasFqName(): Boolean = (bitField0_ and 2) != 0
val `companionObjectName`: Int get() = field_4_
fun getCompanionObjectName(): Int = `companionObjectName`
fun hasCompanionObjectName(): Boolean = (bitField0_ and 4) != 0
val `typeParameterList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter> get() = field_5_
val `typeParameterCount`: Int get() = field_5_.size
fun getTypeParameterList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter> = field_5_
fun getTypeParameterCount(): Int = field_5_.size
fun getTypeParameter(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter = field_5_[index]
val `supertypeList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> get() = field_6_
val `supertypeCount`: Int get() = field_6_.size
fun getSupertypeList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> = field_6_
fun getSupertypeCount(): Int = field_6_.size
fun getSupertype(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Type = field_6_[index]
val `nestedClassNameList`: List<Int> get() = field_7_
val `nestedClassNameCount`: Int get() = field_7_.size
fun getNestedClassNameList(): List<Int> = field_7_
fun getNestedClassNameCount(): Int = field_7_.size
fun getNestedClassName(index: Int): Int = field_7_[index]
val `constructorList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Constructor> get() = field_8_
val `constructorCount`: Int get() = field_8_.size
fun getConstructorList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Constructor> = field_8_
fun getConstructorCount(): Int = field_8_.size
fun getConstructor(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Constructor = field_8_[index]
val `functionList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Function> get() = field_9_
val `functionCount`: Int get() = field_9_.size
fun getFunctionList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Function> = field_9_
fun getFunctionCount(): Int = field_9_.size
fun getFunction(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Function = field_9_[index]
val `propertyList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Property> get() = field_10_
val `propertyCount`: Int get() = field_10_.size
fun getPropertyList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Property> = field_10_
fun getPropertyCount(): Int = field_10_.size
fun getProperty(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Property = field_10_[index]
val `typeAliasList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias> get() = field_11_
val `typeAliasCount`: Int get() = field_11_.size
fun getTypeAliasList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias> = field_11_
fun getTypeAliasCount(): Int = field_11_.size
fun getTypeAlias(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias = field_11_[index]
val `enumEntryList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.EnumEntry> get() = field_13_
val `enumEntryCount`: Int get() = field_13_.size
fun getEnumEntryList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.EnumEntry> = field_13_
fun getEnumEntryCount(): Int = field_13_.size
fun getEnumEntry(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.EnumEntry = field_13_[index]
val `sealedSubclassFqNameList`: List<Int> get() = field_16_
val `sealedSubclassFqNameCount`: Int get() = field_16_.size
fun getSealedSubclassFqNameList(): List<Int> = field_16_
fun getSealedSubclassFqNameCount(): Int = field_16_.size
fun getSealedSubclassFqName(index: Int): Int = field_16_[index]
val `inlineClassUnderlyingPropertyName`: Int get() = field_17_
fun getInlineClassUnderlyingPropertyName(): Int = `inlineClassUnderlyingPropertyName`
fun hasInlineClassUnderlyingPropertyName(): Boolean = (bitField0_ and 8) != 0
val `inlineClassUnderlyingType`: org.jetbrains.kotlin.metadata.ProtoBuf.Type get() = field_18_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance
fun getInlineClassUnderlyingType(): org.jetbrains.kotlin.metadata.ProtoBuf.Type = `inlineClassUnderlyingType`
fun hasInlineClassUnderlyingType(): Boolean = (bitField0_ and 16) != 0
val `inlineClassUnderlyingTypeId`: Int get() = field_19_
fun getInlineClassUnderlyingTypeId(): Int = `inlineClassUnderlyingTypeId`
fun hasInlineClassUnderlyingTypeId(): Boolean = (bitField0_ and 32) != 0
val `contextReceiverTypeList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> get() = field_20_
val `contextReceiverTypeCount`: Int get() = field_20_.size
fun getContextReceiverTypeList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> = field_20_
fun getContextReceiverTypeCount(): Int = field_20_.size
fun getContextReceiverType(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Type = field_20_[index]
val `contextReceiverTypeIdList`: List<Int> get() = field_21_
val `contextReceiverTypeIdCount`: Int get() = field_21_.size
fun getContextReceiverTypeIdList(): List<Int> = field_21_
fun getContextReceiverTypeIdCount(): Int = field_21_.size
fun getContextReceiverTypeId(index: Int): Int = field_21_[index]
val `annotationList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> get() = field_25_
val `annotationCount`: Int get() = field_25_.size
fun getAnnotationList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = field_25_
fun getAnnotationCount(): Int = field_25_.size
fun getAnnotation(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation = field_25_[index]
val `typeTable`: org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable get() = field_30_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable.defaultInstance
fun getTypeTable(): org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable = `typeTable`
fun hasTypeTable(): Boolean = (bitField0_ and 64) != 0
val `versionRequirementList`: List<Int> get() = field_31_
val `versionRequirementCount`: Int get() = field_31_.size
fun getVersionRequirementList(): List<Int> = field_31_
fun getVersionRequirementCount(): Int = field_31_.size
fun getVersionRequirement(index: Int): Int = field_31_[index]
val `versionRequirementTable`: org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirementTable get() = field_32_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirementTable.defaultInstance
fun getVersionRequirementTable(): org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirementTable = `versionRequirementTable`
fun hasVersionRequirementTable(): Boolean = (bitField0_ and 128) != 0
val `compilerPluginDataList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData> get() = field_33_
val `compilerPluginDataCount`: Int get() = field_33_.size
fun getCompilerPluginDataList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData> = field_33_
fun getCompilerPluginDataCount(): Int = field_33_.size
fun getCompilerPluginData(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData = field_33_[index]
override fun isInitialized(): Boolean {
val isInitialized = memoizedIsInitialized.toInt()
if (isInitialized == 1) return true
if (isInitialized == 0) return false
if ((bitField0_ and 2) == 0) { memoizedIsInitialized = 0; return false }
if ((bitField0_ and 16) != 0 && !getInlineClassUnderlyingType().isInitialized()) { memoizedIsInitialized = 0; return false }
if ((bitField0_ and 64) != 0 && !getTypeTable().isInitialized()) { memoizedIsInitialized = 0; return false }
for (i in 0 until field_5_.size) { if (!field_5_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
for (i in 0 until field_6_.size) { if (!field_6_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
for (i in 0 until field_20_.size) { if (!field_20_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
for (i in 0 until field_8_.size) { if (!field_8_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
for (i in 0 until field_9_.size) { if (!field_9_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
for (i in 0 until field_10_.size) { if (!field_10_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
for (i in 0 until field_11_.size) { if (!field_11_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
for (i in 0 until field_13_.size) { if (!field_13_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
for (i in 0 until field_25_.size) { if (!field_25_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
for (i in 0 until field_33_.size) { if (!field_33_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
if (!extensionsAreInitialized()) { memoizedIsInitialized = 0; return false }
memoizedIsInitialized = 1
return true
}
override fun writeTo(output: CodedOutputStream) {
serializedSize
val extensionWriter = if (hasAnyExtensions()) newExtensionWriter() else null
extensionWriter?.writeUntil(1, output)
if ((bitField0_ and 1) != 0) { output.writeRawVarint32(8); output.writeInt32NoTag(field_1_) }
extensionWriter?.writeUntil(2, output)
if (field_2_.isNotEmpty()) {
output.writeRawVarint32(18)
output.writeRawVarint32(packedSize_2_)
for (i in 0 until field_2_.size) { output.writeInt32NoTag(field_2_[i]) }
}
extensionWriter?.writeUntil(3, output)
if ((bitField0_ and 2) != 0) { output.writeRawVarint32(24); output.writeInt32NoTag(field_3_) }
extensionWriter?.writeUntil(4, output)
if ((bitField0_ and 4) != 0) { output.writeRawVarint32(32); output.writeInt32NoTag(field_4_) }
extensionWriter?.writeUntil(5, output)
for (i in 0 until field_5_.size) { output.writeRawVarint32(42); output.writeMessageNoTag(field_5_[i]) }
extensionWriter?.writeUntil(6, output)
for (i in 0 until field_6_.size) { output.writeRawVarint32(50); output.writeMessageNoTag(field_6_[i]) }
extensionWriter?.writeUntil(7, output)
if (field_7_.isNotEmpty()) {
output.writeRawVarint32(58)
output.writeRawVarint32(packedSize_7_)
for (i in 0 until field_7_.size) { output.writeInt32NoTag(field_7_[i]) }
}
extensionWriter?.writeUntil(8, output)
for (i in 0 until field_8_.size) { output.writeRawVarint32(66); output.writeMessageNoTag(field_8_[i]) }
extensionWriter?.writeUntil(9, output)
for (i in 0 until field_9_.size) { output.writeRawVarint32(74); output.writeMessageNoTag(field_9_[i]) }
extensionWriter?.writeUntil(10, output)
for (i in 0 until field_10_.size) { output.writeRawVarint32(82); output.writeMessageNoTag(field_10_[i]) }
extensionWriter?.writeUntil(11, output)
for (i in 0 until field_11_.size) { output.writeRawVarint32(90); output.writeMessageNoTag(field_11_[i]) }
extensionWriter?.writeUntil(13, output)
for (i in 0 until field_13_.size) { output.writeRawVarint32(106); output.writeMessageNoTag(field_13_[i]) }
extensionWriter?.writeUntil(16, output)
if (field_16_.isNotEmpty()) {
output.writeRawVarint32(130)
output.writeRawVarint32(packedSize_16_)
for (i in 0 until field_16_.size) { output.writeInt32NoTag(field_16_[i]) }
}
extensionWriter?.writeUntil(17, output)
if ((bitField0_ and 8) != 0) { output.writeRawVarint32(136); output.writeInt32NoTag(field_17_) }
extensionWriter?.writeUntil(18, output)
if ((bitField0_ and 16) != 0) { output.writeRawVarint32(146); output.writeMessageNoTag(getInlineClassUnderlyingType()) }
extensionWriter?.writeUntil(19, output)
if ((bitField0_ and 32) != 0) { output.writeRawVarint32(152); output.writeInt32NoTag(field_19_) }
extensionWriter?.writeUntil(20, output)
for (i in 0 until field_20_.size) { output.writeRawVarint32(162); output.writeMessageNoTag(field_20_[i]) }
extensionWriter?.writeUntil(21, output)
if (field_21_.isNotEmpty()) {
output.writeRawVarint32(170)
output.writeRawVarint32(packedSize_21_)
for (i in 0 until field_21_.size) { output.writeInt32NoTag(field_21_[i]) }
}
extensionWriter?.writeUntil(25, output)
for (i in 0 until field_25_.size) { output.writeRawVarint32(202); output.writeMessageNoTag(field_25_[i]) }
extensionWriter?.writeUntil(30, output)
if ((bitField0_ and 64) != 0) { output.writeRawVarint32(242); output.writeMessageNoTag(getTypeTable()) }
extensionWriter?.writeUntil(31, output)
for (i in 0 until field_31_.size) { output.writeRawVarint32(248); output.writeInt32NoTag(field_31_[i]) }
extensionWriter?.writeUntil(32, output)
if ((bitField0_ and 128) != 0) { output.writeRawVarint32(258); output.writeMessageNoTag(getVersionRequirementTable()) }
extensionWriter?.writeUntil(33, output)
for (i in 0 until field_33_.size) { output.writeRawVarint32(266); output.writeMessageNoTag(field_33_[i]) }
extensionWriter?.writeUntil(Int.MAX_VALUE, output)
if (!unknownFields.isEmpty()) unknownFields.writeTo(output)
}
override val serializedSize: Int get() {
val cached = memoizedSerializedSize
if (cached != -1) return cached
var size = 0
if ((bitField0_ and 1) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_1_)
if ((bitField0_ and 2) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_3_)
if ((bitField0_ and 4) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_4_)
if ((bitField0_ and 8) != 0) size += 2 + CodedOutputStream.computeInt32SizeNoTag(field_17_)
if ((bitField0_ and 16) != 0) size += 2 + CodedOutputStream.computeMessageSizeNoTag(getInlineClassUnderlyingType())
if ((bitField0_ and 32) != 0) size += 2 + CodedOutputStream.computeInt32SizeNoTag(field_19_)
if ((bitField0_ and 64) != 0) size += 2 + CodedOutputStream.computeMessageSizeNoTag(getTypeTable())
if ((bitField0_ and 128) != 0) size += 2 + CodedOutputStream.computeMessageSizeNoTag(getVersionRequirementTable())
for (i in 0 until field_5_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_5_[i]) }
for (i in 0 until field_6_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_6_[i]) }
if (field_2_.isNotEmpty()) {
var dataSize = 0
for (i in 0 until field_2_.size) { dataSize += CodedOutputStream.computeInt32SizeNoTag(field_2_[i]) }
size += 1 + CodedOutputStream.computeRawVarint32Size(dataSize) + dataSize
packedSize_2_ = dataSize
} else { packedSize_2_ = 0 }
if (field_7_.isNotEmpty()) {
var dataSize = 0
for (i in 0 until field_7_.size) { dataSize += CodedOutputStream.computeInt32SizeNoTag(field_7_[i]) }
size += 1 + CodedOutputStream.computeRawVarint32Size(dataSize) + dataSize
packedSize_7_ = dataSize
} else { packedSize_7_ = 0 }
for (i in 0 until field_20_.size) { size += 2 + CodedOutputStream.computeMessageSizeNoTag(field_20_[i]) }
if (field_21_.isNotEmpty()) {
var dataSize = 0
for (i in 0 until field_21_.size) { dataSize += CodedOutputStream.computeInt32SizeNoTag(field_21_[i]) }
size += 2 + CodedOutputStream.computeRawVarint32Size(dataSize) + dataSize
packedSize_21_ = dataSize
} else { packedSize_21_ = 0 }
for (i in 0 until field_8_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_8_[i]) }
for (i in 0 until field_9_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_9_[i]) }
for (i in 0 until field_10_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_10_[i]) }
for (i in 0 until field_11_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_11_[i]) }
for (i in 0 until field_13_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_13_[i]) }
if (field_16_.isNotEmpty()) {
var dataSize = 0
for (i in 0 until field_16_.size) { dataSize += CodedOutputStream.computeInt32SizeNoTag(field_16_[i]) }
size += 2 + CodedOutputStream.computeRawVarint32Size(dataSize) + dataSize
packedSize_16_ = dataSize
} else { packedSize_16_ = 0 }
for (i in 0 until field_25_.size) { size += 2 + CodedOutputStream.computeMessageSizeNoTag(field_25_[i]) }
for (i in 0 until field_31_.size) { size += 2 + CodedOutputStream.computeInt32SizeNoTag(field_31_[i]) }
for (i in 0 until field_33_.size) { size += 2 + CodedOutputStream.computeMessageSizeNoTag(field_33_[i]) }
size += extensionsSerializedSize()
size += unknownFields.size()
memoizedSerializedSize = size
return size
}
override val defaultInstanceForType: Class get() = defaultInstance
override fun newBuilderForType(): Builder = Builder()
override fun toBuilder(): Builder = Builder().mergeFrom(this)
class Builder : GeneratedMessageLite.ExtendableBuilder<Class, Builder>() {
private var bitField0_: Int = 0
private var mutBitField0_: Int = 0
private var field_1_: Int = 6
private var field_3_: Int = 0
private var field_4_: Int = 0
private var field_17_: Int = 0
private var field_18_: org.jetbrains.kotlin.metadata.ProtoBuf.Type? = null
private var field_19_: Int = 0
private var field_30_: org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable? = null
private var field_32_: org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirementTable? = null
private var field_5_: List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter> = emptyList()
private fun ensure_5_Mutable() { if ((mutBitField0_ and 1) == 0) { field_5_ = ArrayList(field_5_); mutBitField0_ = mutBitField0_ or 1 } }
private var field_6_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> = emptyList()
private fun ensure_6_Mutable() { if ((mutBitField0_ and 2) == 0) { field_6_ = ArrayList(field_6_); mutBitField0_ = mutBitField0_ or 2 } }
private var field_2_: List<Int> = emptyList()
private fun ensure_2_Mutable() { if ((mutBitField0_ and 4) == 0) { field_2_ = ArrayList(field_2_); mutBitField0_ = mutBitField0_ or 4 } }
private var field_7_: List<Int> = emptyList()
private fun ensure_7_Mutable() { if ((mutBitField0_ and 8) == 0) { field_7_ = ArrayList(field_7_); mutBitField0_ = mutBitField0_ or 8 } }
private var field_20_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> = emptyList()
private fun ensure_20_Mutable() { if ((mutBitField0_ and 16) == 0) { field_20_ = ArrayList(field_20_); mutBitField0_ = mutBitField0_ or 16 } }
private var field_21_: List<Int> = emptyList()
private fun ensure_21_Mutable() { if ((mutBitField0_ and 32) == 0) { field_21_ = ArrayList(field_21_); mutBitField0_ = mutBitField0_ or 32 } }
private var field_8_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Constructor> = emptyList()
private fun ensure_8_Mutable() { if ((mutBitField0_ and 64) == 0) { field_8_ = ArrayList(field_8_); mutBitField0_ = mutBitField0_ or 64 } }
private var field_9_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Function> = emptyList()
private fun ensure_9_Mutable() { if ((mutBitField0_ and 128) == 0) { field_9_ = ArrayList(field_9_); mutBitField0_ = mutBitField0_ or 128 } }
private var field_10_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Property> = emptyList()
private fun ensure_10_Mutable() { if ((mutBitField0_ and 256) == 0) { field_10_ = ArrayList(field_10_); mutBitField0_ = mutBitField0_ or 256 } }
private var field_11_: List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias> = emptyList()
private fun ensure_11_Mutable() { if ((mutBitField0_ and 512) == 0) { field_11_ = ArrayList(field_11_); mutBitField0_ = mutBitField0_ or 512 } }
private var field_13_: List<org.jetbrains.kotlin.metadata.ProtoBuf.EnumEntry> = emptyList()
private fun ensure_13_Mutable() { if ((mutBitField0_ and 1024) == 0) { field_13_ = ArrayList(field_13_); mutBitField0_ = mutBitField0_ or 1024 } }
private var field_16_: List<Int> = emptyList()
private fun ensure_16_Mutable() { if ((mutBitField0_ and 2048) == 0) { field_16_ = ArrayList(field_16_); mutBitField0_ = mutBitField0_ or 2048 } }
private var field_25_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = emptyList()
private fun ensure_25_Mutable() { if ((mutBitField0_ and 4096) == 0) { field_25_ = ArrayList(field_25_); mutBitField0_ = mutBitField0_ or 4096 } }
private var field_31_: List<Int> = emptyList()
private fun ensure_31_Mutable() { if ((mutBitField0_ and 8192) == 0) { field_31_ = ArrayList(field_31_); mutBitField0_ = mutBitField0_ or 8192 } }
private var field_33_: List<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData> = emptyList()
private fun ensure_33_Mutable() { if ((mutBitField0_ and 16384) == 0) { field_33_ = ArrayList(field_33_); mutBitField0_ = mutBitField0_ or 16384 } }
override val defaultInstanceForType: Class get() = defaultInstance
override fun clear(): Builder {
bitField0_ = 0
mutBitField0_ = 0
field_1_ = 6
field_3_ = 0
field_4_ = 0
field_17_ = 0
field_18_ = null
field_19_ = 0
field_30_ = null
field_32_ = null
field_5_ = emptyList()
field_6_ = emptyList()
field_2_ = emptyList()
field_7_ = emptyList()
field_20_ = emptyList()
field_21_ = emptyList()
field_8_ = emptyList()
field_9_ = emptyList()
field_10_ = emptyList()
field_11_ = emptyList()
field_13_ = emptyList()
field_16_ = emptyList()
field_25_ = emptyList()
field_31_ = emptyList()
field_33_ = emptyList()
clearExtensions()
unknownFields = ByteString.EMPTY
return this
}
override fun clone(): Builder = Builder().mergeFrom(buildPartial())
override fun build(): Class {
val result = buildPartial()
if (!result.isInitialized()) throw UninitializedMessageException()
return result
}
override fun buildPartial(): Class {
val result = Class(this)
val from_bitField0_ = bitField0_
var to_bitField0_ = 0
if ((from_bitField0_ and 1) != 0) to_bitField0_ = to_bitField0_ or 1
result.field_1_ = field_1_
if ((from_bitField0_ and 2) != 0) to_bitField0_ = to_bitField0_ or 2
result.field_3_ = field_3_
if ((from_bitField0_ and 4) != 0) to_bitField0_ = to_bitField0_ or 4
result.field_4_ = field_4_
if ((from_bitField0_ and 8) != 0) to_bitField0_ = to_bitField0_ or 8
result.field_17_ = field_17_
if ((from_bitField0_ and 16) != 0) to_bitField0_ = to_bitField0_ or 16
result.field_18_ = field_18_
if ((from_bitField0_ and 32) != 0) to_bitField0_ = to_bitField0_ or 32
result.field_19_ = field_19_
if ((from_bitField0_ and 64) != 0) to_bitField0_ = to_bitField0_ or 64
result.field_30_ = field_30_
if ((from_bitField0_ and 128) != 0) to_bitField0_ = to_bitField0_ or 128
result.field_32_ = field_32_
result.bitField0_ = to_bitField0_
if ((mutBitField0_ and 1) != 0) { field_5_ = GeneratedMessageLite.unmodifiableProtoList(field_5_); mutBitField0_ = mutBitField0_ and -2 }
result.field_5_ = field_5_
if ((mutBitField0_ and 2) != 0) { field_6_ = GeneratedMessageLite.unmodifiableProtoList(field_6_); mutBitField0_ = mutBitField0_ and -3 }
result.field_6_ = field_6_
if ((mutBitField0_ and 4) != 0) { field_2_ = GeneratedMessageLite.unmodifiableProtoList(field_2_); mutBitField0_ = mutBitField0_ and -5 }
result.field_2_ = field_2_
if ((mutBitField0_ and 8) != 0) { field_7_ = GeneratedMessageLite.unmodifiableProtoList(field_7_); mutBitField0_ = mutBitField0_ and -9 }
result.field_7_ = field_7_
if ((mutBitField0_ and 16) != 0) { field_20_ = GeneratedMessageLite.unmodifiableProtoList(field_20_); mutBitField0_ = mutBitField0_ and -17 }
result.field_20_ = field_20_
if ((mutBitField0_ and 32) != 0) { field_21_ = GeneratedMessageLite.unmodifiableProtoList(field_21_); mutBitField0_ = mutBitField0_ and -33 }
result.field_21_ = field_21_
if ((mutBitField0_ and 64) != 0) { field_8_ = GeneratedMessageLite.unmodifiableProtoList(field_8_); mutBitField0_ = mutBitField0_ and -65 }
result.field_8_ = field_8_
if ((mutBitField0_ and 128) != 0) { field_9_ = GeneratedMessageLite.unmodifiableProtoList(field_9_); mutBitField0_ = mutBitField0_ and -129 }
result.field_9_ = field_9_
if ((mutBitField0_ and 256) != 0) { field_10_ = GeneratedMessageLite.unmodifiableProtoList(field_10_); mutBitField0_ = mutBitField0_ and -257 }
result.field_10_ = field_10_
if ((mutBitField0_ and 512) != 0) { field_11_ = GeneratedMessageLite.unmodifiableProtoList(field_11_); mutBitField0_ = mutBitField0_ and -513 }
result.field_11_ = field_11_
if ((mutBitField0_ and 1024) != 0) { field_13_ = GeneratedMessageLite.unmodifiableProtoList(field_13_); mutBitField0_ = mutBitField0_ and -1025 }
result.field_13_ = field_13_
if ((mutBitField0_ and 2048) != 0) { field_16_ = GeneratedMessageLite.unmodifiableProtoList(field_16_); mutBitField0_ = mutBitField0_ and -2049 }
result.field_16_ = field_16_
if ((mutBitField0_ and 4096) != 0) { field_25_ = GeneratedMessageLite.unmodifiableProtoList(field_25_); mutBitField0_ = mutBitField0_ and -4097 }
result.field_25_ = field_25_
if ((mutBitField0_ and 8192) != 0) { field_31_ = GeneratedMessageLite.unmodifiableProtoList(field_31_); mutBitField0_ = mutBitField0_ and -8193 }
result.field_31_ = field_31_
if ((mutBitField0_ and 16384) != 0) { field_33_ = GeneratedMessageLite.unmodifiableProtoList(field_33_); mutBitField0_ = mutBitField0_ and -16385 }
result.field_33_ = field_33_
return result
}
override fun mergeFrom(other: Class): Builder {
if (other === defaultInstance) return this
if (other.hasFlags()) setFlags(other.getFlags())
if (other.hasFqName()) setFqName(other.getFqName())
if (other.hasCompanionObjectName()) setCompanionObjectName(other.getCompanionObjectName())
if (other.hasInlineClassUnderlyingPropertyName()) setInlineClassUnderlyingPropertyName(other.getInlineClassUnderlyingPropertyName())
if (other.hasInlineClassUnderlyingType()) mergeInlineClassUnderlyingType(other.getInlineClassUnderlyingType())
if (other.hasInlineClassUnderlyingTypeId()) setInlineClassUnderlyingTypeId(other.getInlineClassUnderlyingTypeId())
if (other.hasTypeTable()) mergeTypeTable(other.getTypeTable())
if (other.hasVersionRequirementTable()) mergeVersionRequirementTable(other.getVersionRequirementTable())
if (other.field_5_.isNotEmpty()) {
if (field_5_.isEmpty()) { field_5_ = other.field_5_; mutBitField0_ = mutBitField0_ and -2 } else { ensure_5_Mutable(); (field_5_ as MutableList<Any>).addAll(other.field_5_ as Collection<Any>) }
}
if (other.field_6_.isNotEmpty()) {
if (field_6_.isEmpty()) { field_6_ = other.field_6_; mutBitField0_ = mutBitField0_ and -3 } else { ensure_6_Mutable(); (field_6_ as MutableList<Any>).addAll(other.field_6_ as Collection<Any>) }
}
if (other.field_2_.isNotEmpty()) {
if (field_2_.isEmpty()) { field_2_ = other.field_2_; mutBitField0_ = mutBitField0_ and -5 } else { ensure_2_Mutable(); (field_2_ as MutableList<Any>).addAll(other.field_2_ as Collection<Any>) }
}
if (other.field_7_.isNotEmpty()) {
if (field_7_.isEmpty()) { field_7_ = other.field_7_; mutBitField0_ = mutBitField0_ and -9 } else { ensure_7_Mutable(); (field_7_ as MutableList<Any>).addAll(other.field_7_ as Collection<Any>) }
}
if (other.field_20_.isNotEmpty()) {
if (field_20_.isEmpty()) { field_20_ = other.field_20_; mutBitField0_ = mutBitField0_ and -17 } else { ensure_20_Mutable(); (field_20_ as MutableList<Any>).addAll(other.field_20_ as Collection<Any>) }
}
if (other.field_21_.isNotEmpty()) {
if (field_21_.isEmpty()) { field_21_ = other.field_21_; mutBitField0_ = mutBitField0_ and -33 } else { ensure_21_Mutable(); (field_21_ as MutableList<Any>).addAll(other.field_21_ as Collection<Any>) }
}
if (other.field_8_.isNotEmpty()) {
if (field_8_.isEmpty()) { field_8_ = other.field_8_; mutBitField0_ = mutBitField0_ and -65 } else { ensure_8_Mutable(); (field_8_ as MutableList<Any>).addAll(other.field_8_ as Collection<Any>) }
}
if (other.field_9_.isNotEmpty()) {
if (field_9_.isEmpty()) { field_9_ = other.field_9_; mutBitField0_ = mutBitField0_ and -129 } else { ensure_9_Mutable(); (field_9_ as MutableList<Any>).addAll(other.field_9_ as Collection<Any>) }
}
if (other.field_10_.isNotEmpty()) {
if (field_10_.isEmpty()) { field_10_ = other.field_10_; mutBitField0_ = mutBitField0_ and -257 } else { ensure_10_Mutable(); (field_10_ as MutableList<Any>).addAll(other.field_10_ as Collection<Any>) }
}
if (other.field_11_.isNotEmpty()) {
if (field_11_.isEmpty()) { field_11_ = other.field_11_; mutBitField0_ = mutBitField0_ and -513 } else { ensure_11_Mutable(); (field_11_ as MutableList<Any>).addAll(other.field_11_ as Collection<Any>) }
}
if (other.field_13_.isNotEmpty()) {
if (field_13_.isEmpty()) { field_13_ = other.field_13_; mutBitField0_ = mutBitField0_ and -1025 } else { ensure_13_Mutable(); (field_13_ as MutableList<Any>).addAll(other.field_13_ as Collection<Any>) }
}
if (other.field_16_.isNotEmpty()) {
if (field_16_.isEmpty()) { field_16_ = other.field_16_; mutBitField0_ = mutBitField0_ and -2049 } else { ensure_16_Mutable(); (field_16_ as MutableList<Any>).addAll(other.field_16_ as Collection<Any>) }
}
if (other.field_25_.isNotEmpty()) {
if (field_25_.isEmpty()) { field_25_ = other.field_25_; mutBitField0_ = mutBitField0_ and -4097 } else { ensure_25_Mutable(); (field_25_ as MutableList<Any>).addAll(other.field_25_ as Collection<Any>) }
}
if (other.field_31_.isNotEmpty()) {
if (field_31_.isEmpty()) { field_31_ = other.field_31_; mutBitField0_ = mutBitField0_ and -8193 } else { ensure_31_Mutable(); (field_31_ as MutableList<Any>).addAll(other.field_31_ as Collection<Any>) }
}
if (other.field_33_.isNotEmpty()) {
if (field_33_.isEmpty()) { field_33_ = other.field_33_; mutBitField0_ = mutBitField0_ and -16385 } else { ensure_33_Mutable(); (field_33_ as MutableList<Any>).addAll(other.field_33_ as Collection<Any>) }
}
mergeExtensionFields(other)
unknownFields = unknownFields.concat(other.unknownFields)
return this
}
override fun isInitialized(): Boolean {
if ((bitField0_ and 2) == 0) return false
if ((bitField0_ and 16) != 0 && !getInlineClassUnderlyingType().isInitialized()) return false
if ((bitField0_ and 64) != 0 && !getTypeTable().isInitialized()) return false
for (i in 0 until field_5_.size) { if (!field_5_[i].isInitialized()) return false }
for (i in 0 until field_6_.size) { if (!field_6_[i].isInitialized()) return false }
for (i in 0 until field_20_.size) { if (!field_20_[i].isInitialized()) return false }
for (i in 0 until field_8_.size) { if (!field_8_[i].isInitialized()) return false }
for (i in 0 until field_9_.size) { if (!field_9_[i].isInitialized()) return false }
for (i in 0 until field_10_.size) { if (!field_10_[i].isInitialized()) return false }
for (i in 0 until field_11_.size) { if (!field_11_[i].isInitialized()) return false }
for (i in 0 until field_13_.size) { if (!field_13_[i].isInitialized()) return false }
for (i in 0 until field_25_.size) { if (!field_25_[i].isInitialized()) return false }
for (i in 0 until field_33_.size) { if (!field_33_[i].isInitialized()) return false }
if (!extensionsAreInitialized()) return false
return true
}
override fun mergeFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Builder {
var parsedMessage: Class? = null
try {
parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry)
} catch (e: InvalidProtocolBufferException) {
parsedMessage = e.unfinishedMessage as? Class
throw e
} finally {
if (parsedMessage != null) mergeFrom(parsedMessage)
}
return this
}
var `flags`: Int
get() = field_1_
set(value) { bitField0_ = bitField0_ or 1; field_1_ = value }
fun getFlags(): Int = field_1_
fun hasFlags(): Boolean = (bitField0_ and 1) != 0
fun setFlags(value: Int): Builder { bitField0_ = bitField0_ or 1; field_1_ = value; return this }
fun clearFlags(): Builder { bitField0_ = bitField0_ and -2; field_1_ = 6; return this }
val `supertypeIdList`: List<Int> get() = if (field_2_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_2_)
val `supertypeIdCount`: Int get() = field_2_.size
fun getSupertypeIdList(): List<Int> = `supertypeIdList`
fun getSupertypeIdCount(): Int = field_2_.size
fun getSupertypeId(index: Int): Int = field_2_[index]
fun addSupertypeId(value: Int): Builder { ensure_2_Mutable(); (field_2_ as MutableList<Int>).add(value); return this }
fun setSupertypeId(index: Int, value: Int): Builder { ensure_2_Mutable(); (field_2_ as MutableList<Int>)[index] = value; return this }
fun addAllSupertypeId(values: Iterable<Int>): Builder { ensure_2_Mutable(); AbstractMessageLite.Builder.addAll(values, field_2_ as MutableList<Int>); return this }
fun clearSupertypeId(): Builder { field_2_ = emptyList(); mutBitField0_ = mutBitField0_ and -5; return this }
var `fqName`: Int
get() = field_3_
set(value) { bitField0_ = bitField0_ or 2; field_3_ = value }
fun getFqName(): Int = field_3_
fun hasFqName(): Boolean = (bitField0_ and 2) != 0
fun setFqName(value: Int): Builder { bitField0_ = bitField0_ or 2; field_3_ = value; return this }
fun clearFqName(): Builder { bitField0_ = bitField0_ and -3; field_3_ = 0; return this }
var `companionObjectName`: Int
get() = field_4_
set(value) { bitField0_ = bitField0_ or 4; field_4_ = value }
fun getCompanionObjectName(): Int = field_4_
fun hasCompanionObjectName(): Boolean = (bitField0_ and 4) != 0
fun setCompanionObjectName(value: Int): Builder { bitField0_ = bitField0_ or 4; field_4_ = value; return this }
fun clearCompanionObjectName(): Builder { bitField0_ = bitField0_ and -5; field_4_ = 0; return this }
val `typeParameterList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter> get() = if (field_5_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_5_)
val `typeParameterCount`: Int get() = field_5_.size
fun getTypeParameterList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter> = `typeParameterList`
fun getTypeParameterCount(): Int = field_5_.size
fun getTypeParameter(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter = field_5_[index]
fun addTypeParameter(value: org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter): Builder { ensure_5_Mutable(); (field_5_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter>).add(value); return this }
fun setTypeParameter(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter): Builder { ensure_5_Mutable(); (field_5_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter>)[index] = value; return this }
fun addAllTypeParameter(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter>): Builder { ensure_5_Mutable(); AbstractMessageLite.Builder.addAll(values, field_5_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter>); return this }
fun clearTypeParameter(): Builder { field_5_ = emptyList(); mutBitField0_ = mutBitField0_ and -2; return this }
fun addTypeParameter(value: org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter.Builder): Builder = addTypeParameter(value.build())
fun setTypeParameter(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter.Builder): Builder = setTypeParameter(index, value.build())
val `supertypeList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> get() = if (field_6_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_6_)
val `supertypeCount`: Int get() = field_6_.size
fun getSupertypeList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> = `supertypeList`
fun getSupertypeCount(): Int = field_6_.size
fun getSupertype(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Type = field_6_[index]
fun addSupertype(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder { ensure_6_Mutable(); (field_6_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Type>).add(value); return this }
fun setSupertype(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder { ensure_6_Mutable(); (field_6_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Type>)[index] = value; return this }
fun addAllSupertype(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.Type>): Builder { ensure_6_Mutable(); AbstractMessageLite.Builder.addAll(values, field_6_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Type>); return this }
fun clearSupertype(): Builder { field_6_ = emptyList(); mutBitField0_ = mutBitField0_ and -3; return this }
fun addSupertype(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder): Builder = addSupertype(value.build())
fun setSupertype(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder): Builder = setSupertype(index, value.build())
val `nestedClassNameList`: List<Int> get() = if (field_7_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_7_)
val `nestedClassNameCount`: Int get() = field_7_.size
fun getNestedClassNameList(): List<Int> = `nestedClassNameList`
fun getNestedClassNameCount(): Int = field_7_.size
fun getNestedClassName(index: Int): Int = field_7_[index]
fun addNestedClassName(value: Int): Builder { ensure_7_Mutable(); (field_7_ as MutableList<Int>).add(value); return this }
fun setNestedClassName(index: Int, value: Int): Builder { ensure_7_Mutable(); (field_7_ as MutableList<Int>)[index] = value; return this }
fun addAllNestedClassName(values: Iterable<Int>): Builder { ensure_7_Mutable(); AbstractMessageLite.Builder.addAll(values, field_7_ as MutableList<Int>); return this }
fun clearNestedClassName(): Builder { field_7_ = emptyList(); mutBitField0_ = mutBitField0_ and -9; return this }
val `constructorList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Constructor> get() = if (field_8_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_8_)
val `constructorCount`: Int get() = field_8_.size
fun getConstructorList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Constructor> = `constructorList`
fun getConstructorCount(): Int = field_8_.size
fun getConstructor(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Constructor = field_8_[index]
fun addConstructor(value: org.jetbrains.kotlin.metadata.ProtoBuf.Constructor): Builder { ensure_8_Mutable(); (field_8_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Constructor>).add(value); return this }
fun setConstructor(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Constructor): Builder { ensure_8_Mutable(); (field_8_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Constructor>)[index] = value; return this }
fun addAllConstructor(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.Constructor>): Builder { ensure_8_Mutable(); AbstractMessageLite.Builder.addAll(values, field_8_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Constructor>); return this }
fun clearConstructor(): Builder { field_8_ = emptyList(); mutBitField0_ = mutBitField0_ and -65; return this }
fun addConstructor(value: org.jetbrains.kotlin.metadata.ProtoBuf.Constructor.Builder): Builder = addConstructor(value.build())
fun setConstructor(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Constructor.Builder): Builder = setConstructor(index, value.build())
val `functionList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Function> get() = if (field_9_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_9_)
val `functionCount`: Int get() = field_9_.size
fun getFunctionList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Function> = `functionList`
fun getFunctionCount(): Int = field_9_.size
fun getFunction(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Function = field_9_[index]
fun addFunction(value: org.jetbrains.kotlin.metadata.ProtoBuf.Function): Builder { ensure_9_Mutable(); (field_9_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Function>).add(value); return this }
fun setFunction(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Function): Builder { ensure_9_Mutable(); (field_9_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Function>)[index] = value; return this }
fun addAllFunction(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.Function>): Builder { ensure_9_Mutable(); AbstractMessageLite.Builder.addAll(values, field_9_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Function>); return this }
fun clearFunction(): Builder { field_9_ = emptyList(); mutBitField0_ = mutBitField0_ and -129; return this }
fun addFunction(value: org.jetbrains.kotlin.metadata.ProtoBuf.Function.Builder): Builder = addFunction(value.build())
fun setFunction(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Function.Builder): Builder = setFunction(index, value.build())
val `propertyList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Property> get() = if (field_10_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_10_)
val `propertyCount`: Int get() = field_10_.size
fun getPropertyList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Property> = `propertyList`
fun getPropertyCount(): Int = field_10_.size
fun getProperty(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Property = field_10_[index]
fun addProperty(value: org.jetbrains.kotlin.metadata.ProtoBuf.Property): Builder { ensure_10_Mutable(); (field_10_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Property>).add(value); return this }
fun setProperty(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Property): Builder { ensure_10_Mutable(); (field_10_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Property>)[index] = value; return this }
fun addAllProperty(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.Property>): Builder { ensure_10_Mutable(); AbstractMessageLite.Builder.addAll(values, field_10_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Property>); return this }
fun clearProperty(): Builder { field_10_ = emptyList(); mutBitField0_ = mutBitField0_ and -257; return this }
fun addProperty(value: org.jetbrains.kotlin.metadata.ProtoBuf.Property.Builder): Builder = addProperty(value.build())
fun setProperty(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Property.Builder): Builder = setProperty(index, value.build())
val `typeAliasList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias> get() = if (field_11_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_11_)
val `typeAliasCount`: Int get() = field_11_.size
fun getTypeAliasList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias> = `typeAliasList`
fun getTypeAliasCount(): Int = field_11_.size
fun getTypeAlias(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias = field_11_[index]
fun addTypeAlias(value: org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias): Builder { ensure_11_Mutable(); (field_11_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias>).add(value); return this }
fun setTypeAlias(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias): Builder { ensure_11_Mutable(); (field_11_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias>)[index] = value; return this }
fun addAllTypeAlias(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias>): Builder { ensure_11_Mutable(); AbstractMessageLite.Builder.addAll(values, field_11_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias>); return this }
fun clearTypeAlias(): Builder { field_11_ = emptyList(); mutBitField0_ = mutBitField0_ and -513; return this }
fun addTypeAlias(value: org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias.Builder): Builder = addTypeAlias(value.build())
fun setTypeAlias(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias.Builder): Builder = setTypeAlias(index, value.build())
val `enumEntryList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.EnumEntry> get() = if (field_13_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_13_)
val `enumEntryCount`: Int get() = field_13_.size
fun getEnumEntryList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.EnumEntry> = `enumEntryList`
fun getEnumEntryCount(): Int = field_13_.size
fun getEnumEntry(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.EnumEntry = field_13_[index]
fun addEnumEntry(value: org.jetbrains.kotlin.metadata.ProtoBuf.EnumEntry): Builder { ensure_13_Mutable(); (field_13_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.EnumEntry>).add(value); return this }
fun setEnumEntry(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.EnumEntry): Builder { ensure_13_Mutable(); (field_13_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.EnumEntry>)[index] = value; return this }
fun addAllEnumEntry(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.EnumEntry>): Builder { ensure_13_Mutable(); AbstractMessageLite.Builder.addAll(values, field_13_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.EnumEntry>); return this }
fun clearEnumEntry(): Builder { field_13_ = emptyList(); mutBitField0_ = mutBitField0_ and -1025; return this }
fun addEnumEntry(value: org.jetbrains.kotlin.metadata.ProtoBuf.EnumEntry.Builder): Builder = addEnumEntry(value.build())
fun setEnumEntry(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.EnumEntry.Builder): Builder = setEnumEntry(index, value.build())
val `sealedSubclassFqNameList`: List<Int> get() = if (field_16_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_16_)
val `sealedSubclassFqNameCount`: Int get() = field_16_.size
fun getSealedSubclassFqNameList(): List<Int> = `sealedSubclassFqNameList`
fun getSealedSubclassFqNameCount(): Int = field_16_.size
fun getSealedSubclassFqName(index: Int): Int = field_16_[index]
fun addSealedSubclassFqName(value: Int): Builder { ensure_16_Mutable(); (field_16_ as MutableList<Int>).add(value); return this }
fun setSealedSubclassFqName(index: Int, value: Int): Builder { ensure_16_Mutable(); (field_16_ as MutableList<Int>)[index] = value; return this }
fun addAllSealedSubclassFqName(values: Iterable<Int>): Builder { ensure_16_Mutable(); AbstractMessageLite.Builder.addAll(values, field_16_ as MutableList<Int>); return this }
fun clearSealedSubclassFqName(): Builder { field_16_ = emptyList(); mutBitField0_ = mutBitField0_ and -2049; return this }
var `inlineClassUnderlyingPropertyName`: Int
get() = field_17_
set(value) { bitField0_ = bitField0_ or 8; field_17_ = value }
fun getInlineClassUnderlyingPropertyName(): Int = field_17_
fun hasInlineClassUnderlyingPropertyName(): Boolean = (bitField0_ and 8) != 0
fun setInlineClassUnderlyingPropertyName(value: Int): Builder { bitField0_ = bitField0_ or 8; field_17_ = value; return this }
fun clearInlineClassUnderlyingPropertyName(): Builder { bitField0_ = bitField0_ and -9; field_17_ = 0; return this }
var `inlineClassUnderlyingType`: org.jetbrains.kotlin.metadata.ProtoBuf.Type
get() = field_18_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance
set(value) { field_18_ = value; bitField0_ = bitField0_ or 16 }
fun getInlineClassUnderlyingType(): org.jetbrains.kotlin.metadata.ProtoBuf.Type = `inlineClassUnderlyingType`
fun hasInlineClassUnderlyingType(): Boolean = (bitField0_ and 16) != 0
fun setInlineClassUnderlyingType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder { field_18_ = value; bitField0_ = bitField0_ or 16; return this }
fun setInlineClassUnderlyingType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder): Builder { field_18_ = value.build(); bitField0_ = bitField0_ or 16; return this }
fun mergeInlineClassUnderlyingType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder {
val cur = field_18_
field_18_ = if ((bitField0_ and 16) != 0 && cur != null && cur !== org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance) org.jetbrains.kotlin.metadata.ProtoBuf.Type.newBuilder(cur).mergeFrom(value).buildPartial() else value
bitField0_ = bitField0_ or 16
return this
}
fun clearInlineClassUnderlyingType(): Builder { field_18_ = null; bitField0_ = bitField0_ and -17; return this }
var `inlineClassUnderlyingTypeId`: Int
get() = field_19_
set(value) { bitField0_ = bitField0_ or 32; field_19_ = value }
fun getInlineClassUnderlyingTypeId(): Int = field_19_
fun hasInlineClassUnderlyingTypeId(): Boolean = (bitField0_ and 32) != 0
fun setInlineClassUnderlyingTypeId(value: Int): Builder { bitField0_ = bitField0_ or 32; field_19_ = value; return this }
fun clearInlineClassUnderlyingTypeId(): Builder { bitField0_ = bitField0_ and -33; field_19_ = 0; return this }
val `contextReceiverTypeList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> get() = if (field_20_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_20_)
val `contextReceiverTypeCount`: Int get() = field_20_.size
fun getContextReceiverTypeList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> = `contextReceiverTypeList`
fun getContextReceiverTypeCount(): Int = field_20_.size
fun getContextReceiverType(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Type = field_20_[index]
fun addContextReceiverType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder { ensure_20_Mutable(); (field_20_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Type>).add(value); return this }
fun setContextReceiverType(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder { ensure_20_Mutable(); (field_20_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Type>)[index] = value; return this }
fun addAllContextReceiverType(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.Type>): Builder { ensure_20_Mutable(); AbstractMessageLite.Builder.addAll(values, field_20_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Type>); return this }
fun clearContextReceiverType(): Builder { field_20_ = emptyList(); mutBitField0_ = mutBitField0_ and -17; return this }
fun addContextReceiverType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder): Builder = addContextReceiverType(value.build())
fun setContextReceiverType(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder): Builder = setContextReceiverType(index, value.build())
val `contextReceiverTypeIdList`: List<Int> get() = if (field_21_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_21_)
val `contextReceiverTypeIdCount`: Int get() = field_21_.size
fun getContextReceiverTypeIdList(): List<Int> = `contextReceiverTypeIdList`
fun getContextReceiverTypeIdCount(): Int = field_21_.size
fun getContextReceiverTypeId(index: Int): Int = field_21_[index]
fun addContextReceiverTypeId(value: Int): Builder { ensure_21_Mutable(); (field_21_ as MutableList<Int>).add(value); return this }
fun setContextReceiverTypeId(index: Int, value: Int): Builder { ensure_21_Mutable(); (field_21_ as MutableList<Int>)[index] = value; return this }
fun addAllContextReceiverTypeId(values: Iterable<Int>): Builder { ensure_21_Mutable(); AbstractMessageLite.Builder.addAll(values, field_21_ as MutableList<Int>); return this }
fun clearContextReceiverTypeId(): Builder { field_21_ = emptyList(); mutBitField0_ = mutBitField0_ and -33; return this }
val `annotationList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> get() = if (field_25_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_25_)
val `annotationCount`: Int get() = field_25_.size
fun getAnnotationList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = `annotationList`
fun getAnnotationCount(): Int = field_25_.size
fun getAnnotation(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation = field_25_[index]
fun addAnnotation(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation): Builder { ensure_25_Mutable(); (field_25_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>).add(value); return this }
fun setAnnotation(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation): Builder { ensure_25_Mutable(); (field_25_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>)[index] = value; return this }
fun addAllAnnotation(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>): Builder { ensure_25_Mutable(); AbstractMessageLite.Builder.addAll(values, field_25_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>); return this }
fun clearAnnotation(): Builder { field_25_ = emptyList(); mutBitField0_ = mutBitField0_ and -4097; return this }
fun addAnnotation(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Builder): Builder = addAnnotation(value.build())
fun setAnnotation(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Builder): Builder = setAnnotation(index, value.build())
var `typeTable`: org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable
get() = field_30_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable.defaultInstance
set(value) { field_30_ = value; bitField0_ = bitField0_ or 64 }
fun getTypeTable(): org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable = `typeTable`
fun hasTypeTable(): Boolean = (bitField0_ and 64) != 0
fun setTypeTable(value: org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable): Builder { field_30_ = value; bitField0_ = bitField0_ or 64; return this }
fun setTypeTable(value: org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable.Builder): Builder { field_30_ = value.build(); bitField0_ = bitField0_ or 64; return this }
fun mergeTypeTable(value: org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable): Builder {
val cur = field_30_
field_30_ = if ((bitField0_ and 64) != 0 && cur != null && cur !== org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable.defaultInstance) org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable.newBuilder(cur).mergeFrom(value).buildPartial() else value
bitField0_ = bitField0_ or 64
return this
}
fun clearTypeTable(): Builder { field_30_ = null; bitField0_ = bitField0_ and -65; return this }
val `versionRequirementList`: List<Int> get() = if (field_31_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_31_)
val `versionRequirementCount`: Int get() = field_31_.size
fun getVersionRequirementList(): List<Int> = `versionRequirementList`
fun getVersionRequirementCount(): Int = field_31_.size
fun getVersionRequirement(index: Int): Int = field_31_[index]
fun addVersionRequirement(value: Int): Builder { ensure_31_Mutable(); (field_31_ as MutableList<Int>).add(value); return this }
fun setVersionRequirement(index: Int, value: Int): Builder { ensure_31_Mutable(); (field_31_ as MutableList<Int>)[index] = value; return this }
fun addAllVersionRequirement(values: Iterable<Int>): Builder { ensure_31_Mutable(); AbstractMessageLite.Builder.addAll(values, field_31_ as MutableList<Int>); return this }
fun clearVersionRequirement(): Builder { field_31_ = emptyList(); mutBitField0_ = mutBitField0_ and -8193; return this }
var `versionRequirementTable`: org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirementTable
get() = field_32_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirementTable.defaultInstance
set(value) { field_32_ = value; bitField0_ = bitField0_ or 128 }
fun getVersionRequirementTable(): org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirementTable = `versionRequirementTable`
fun hasVersionRequirementTable(): Boolean = (bitField0_ and 128) != 0
fun setVersionRequirementTable(value: org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirementTable): Builder { field_32_ = value; bitField0_ = bitField0_ or 128; return this }
fun setVersionRequirementTable(value: org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirementTable.Builder): Builder { field_32_ = value.build(); bitField0_ = bitField0_ or 128; return this }
fun mergeVersionRequirementTable(value: org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirementTable): Builder {
val cur = field_32_
field_32_ = if ((bitField0_ and 128) != 0 && cur != null && cur !== org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirementTable.defaultInstance) org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirementTable.newBuilder(cur).mergeFrom(value).buildPartial() else value
bitField0_ = bitField0_ or 128
return this
}
fun clearVersionRequirementTable(): Builder { field_32_ = null; bitField0_ = bitField0_ and -129; return this }
val `compilerPluginDataList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData> get() = if (field_33_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_33_)
val `compilerPluginDataCount`: Int get() = field_33_.size
fun getCompilerPluginDataList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData> = `compilerPluginDataList`
fun getCompilerPluginDataCount(): Int = field_33_.size
fun getCompilerPluginData(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData = field_33_[index]
fun addCompilerPluginData(value: org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData): Builder { ensure_33_Mutable(); (field_33_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData>).add(value); return this }
fun setCompilerPluginData(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData): Builder { ensure_33_Mutable(); (field_33_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData>)[index] = value; return this }
fun addAllCompilerPluginData(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData>): Builder { ensure_33_Mutable(); AbstractMessageLite.Builder.addAll(values, field_33_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData>); return this }
fun clearCompilerPluginData(): Builder { field_33_ = emptyList(); mutBitField0_ = mutBitField0_ and -16385; return this }
fun addCompilerPluginData(value: org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData.Builder): Builder = addCompilerPluginData(value.build())
fun setCompilerPluginData(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData.Builder): Builder = setCompilerPluginData(index, value.build())
}
companion object {
val defaultInstance: Class = Class()
fun getDefaultInstance(): Class = defaultInstance
fun newBuilder(): Builder = Builder()
fun newBuilder(prototype: Class): Builder = Builder().mergeFrom(prototype)
val PARSER: Parser<Class> = object : AbstractParser<Class>() {
override fun parsePartialFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Class =
Class(input, extensionRegistry)
}
fun parseFrom(bytes: ByteArray): Class = PARSER.parseFrom(bytes)
fun parseFrom(bytes: ByteArray, registry: ExtensionRegistryLite): Class = PARSER.parseFrom(bytes, registry)
fun parseFrom(bytes: ByteString): Class { val input = bytes.newCodedInput(); val msg = PARSER.parsePartialFrom(input, ExtensionRegistryLite.getEmptyRegistry()); input.checkLastTagWas(0); if (!msg.isInitialized()) throw UninitializedMessageException().asInvalidProtocolBufferException().setUnfinishedMessage(msg); return msg }
fun parseFrom(input: CodedInputStream): Class = PARSER.parseFrom(input)
fun parseFrom(input: CodedInputStream, registry: ExtensionRegistryLite): Class = PARSER.parseFrom(input, registry)
const val FLAGS_FIELD_NUMBER = 1
const val SUPERTYPE_ID_FIELD_NUMBER = 2
const val FQ_NAME_FIELD_NUMBER = 3
const val COMPANION_OBJECT_NAME_FIELD_NUMBER = 4
const val TYPE_PARAMETER_FIELD_NUMBER = 5
const val SUPERTYPE_FIELD_NUMBER = 6
const val NESTED_CLASS_NAME_FIELD_NUMBER = 7
const val CONSTRUCTOR_FIELD_NUMBER = 8
const val FUNCTION_FIELD_NUMBER = 9
const val PROPERTY_FIELD_NUMBER = 10
const val TYPE_ALIAS_FIELD_NUMBER = 11
const val ENUM_ENTRY_FIELD_NUMBER = 13
const val SEALED_SUBCLASS_FQ_NAME_FIELD_NUMBER = 16
const val INLINE_CLASS_UNDERLYING_PROPERTY_NAME_FIELD_NUMBER = 17
const val INLINE_CLASS_UNDERLYING_TYPE_FIELD_NUMBER = 18
const val INLINE_CLASS_UNDERLYING_TYPE_ID_FIELD_NUMBER = 19
const val CONTEXT_RECEIVER_TYPE_FIELD_NUMBER = 20
const val CONTEXT_RECEIVER_TYPE_ID_FIELD_NUMBER = 21
const val ANNOTATION_FIELD_NUMBER = 25
const val TYPE_TABLE_FIELD_NUMBER = 30
const val VERSION_REQUIREMENT_FIELD_NUMBER = 31
const val VERSION_REQUIREMENT_TABLE_FIELD_NUMBER = 32
const val COMPILER_PLUGIN_DATA_FIELD_NUMBER = 33
}}
class Package : GeneratedMessageLite.ExtendableMessage<Package> {
internal var bitField0_: Int = 0
internal var field_30_: org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable? = null
internal var field_32_: org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirementTable? = null
internal var field_3_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Function> = emptyList()
internal var field_4_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Property> = emptyList()
internal var field_5_: List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias> = emptyList()
private var memoizedIsInitialized: Byte = -1
private var memoizedSerializedSize: Int = -1
private constructor()
private constructor(builder: Builder) {
this.unknownFields = builder.unknownFields
copyExtensionsFromBuilder(builder)
}
private constructor(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite) {
var mutBit0_ = 0
var unknownBuffer: ByteString.Output? = null
var unknownOut: CodedOutputStream? = null
fun unknownOutput(): CodedOutputStream {
var out = unknownOut
if (out == null) { val buf = ByteString.newOutput(); unknownBuffer = buf; out = CodedOutputStream.newInstance(buf); unknownOut = out }
return out
}
try {
var done = false
while (!done) {
val tag = input.readTag()
when (tag) {
0 -> done = true
26 -> {
if ((mutBit0_ and 1) == 0) { field_3_ = ArrayList(); mutBit0_ = mutBit0_ or 1 }
(field_3_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Function>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Function.PARSER, extensionRegistry))
}
34 -> {
if ((mutBit0_ and 2) == 0) { field_4_ = ArrayList(); mutBit0_ = mutBit0_ or 2 }
(field_4_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Property>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Property.PARSER, extensionRegistry))
}
42 -> {
if ((mutBit0_ and 4) == 0) { field_5_ = ArrayList(); mutBit0_ = mutBit0_ or 4 }
(field_5_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias.PARSER, extensionRegistry))
}
242 -> {
var subBuilder: org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable.Builder? = null
if ((bitField0_ and 1) != 0) subBuilder = field_30_?.toBuilder()
val parsed = input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable.PARSER, extensionRegistry)
field_30_ = if (subBuilder != null) subBuilder.mergeFrom(parsed).buildPartial() else parsed
bitField0_ = bitField0_ or 1
}
258 -> {
var subBuilder: org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirementTable.Builder? = null
if ((bitField0_ and 2) != 0) subBuilder = field_32_?.toBuilder()
val parsed = input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirementTable.PARSER, extensionRegistry)
field_32_ = if (subBuilder != null) subBuilder.mergeFrom(parsed).buildPartial() else parsed
bitField0_ = bitField0_ or 2
}
else -> if (!parseUnknownField(input, unknownOutput(), extensionRegistry, tag)) done = true
}}
} catch (e: InvalidProtocolBufferException) {
throw e.setUnfinishedMessage(this)
} finally {
if ((mutBit0_ and 1) != 0) field_3_ = GeneratedMessageLite.unmodifiableProtoList(field_3_)
if ((mutBit0_ and 2) != 0) field_4_ = GeneratedMessageLite.unmodifiableProtoList(field_4_)
if ((mutBit0_ and 4) != 0) field_5_ = GeneratedMessageLite.unmodifiableProtoList(field_5_)
if (unknownBuffer != null) { unknownOut!!.flush(); unknownFields = unknownBuffer.toByteString() }
makeExtensionsImmutable()
}}
val `functionList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Function> get() = field_3_
val `functionCount`: Int get() = field_3_.size
fun getFunctionList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Function> = field_3_
fun getFunctionCount(): Int = field_3_.size
fun getFunction(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Function = field_3_[index]
val `propertyList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Property> get() = field_4_
val `propertyCount`: Int get() = field_4_.size
fun getPropertyList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Property> = field_4_
fun getPropertyCount(): Int = field_4_.size
fun getProperty(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Property = field_4_[index]
val `typeAliasList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias> get() = field_5_
val `typeAliasCount`: Int get() = field_5_.size
fun getTypeAliasList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias> = field_5_
fun getTypeAliasCount(): Int = field_5_.size
fun getTypeAlias(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias = field_5_[index]
val `typeTable`: org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable get() = field_30_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable.defaultInstance
fun getTypeTable(): org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable = `typeTable`
fun hasTypeTable(): Boolean = (bitField0_ and 1) != 0
val `versionRequirementTable`: org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirementTable get() = field_32_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirementTable.defaultInstance
fun getVersionRequirementTable(): org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirementTable = `versionRequirementTable`
fun hasVersionRequirementTable(): Boolean = (bitField0_ and 2) != 0
override fun isInitialized(): Boolean {
val isInitialized = memoizedIsInitialized.toInt()
if (isInitialized == 1) return true
if (isInitialized == 0) return false
if ((bitField0_ and 1) != 0 && !getTypeTable().isInitialized()) { memoizedIsInitialized = 0; return false }
for (i in 0 until field_3_.size) { if (!field_3_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
for (i in 0 until field_4_.size) { if (!field_4_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
for (i in 0 until field_5_.size) { if (!field_5_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
if (!extensionsAreInitialized()) { memoizedIsInitialized = 0; return false }
memoizedIsInitialized = 1
return true
}
override fun writeTo(output: CodedOutputStream) {
val extensionWriter = if (hasAnyExtensions()) newExtensionWriter() else null
extensionWriter?.writeUntil(3, output)
for (i in 0 until field_3_.size) { output.writeRawVarint32(26); output.writeMessageNoTag(field_3_[i]) }
extensionWriter?.writeUntil(4, output)
for (i in 0 until field_4_.size) { output.writeRawVarint32(34); output.writeMessageNoTag(field_4_[i]) }
extensionWriter?.writeUntil(5, output)
for (i in 0 until field_5_.size) { output.writeRawVarint32(42); output.writeMessageNoTag(field_5_[i]) }
extensionWriter?.writeUntil(30, output)
if ((bitField0_ and 1) != 0) { output.writeRawVarint32(242); output.writeMessageNoTag(getTypeTable()) }
extensionWriter?.writeUntil(32, output)
if ((bitField0_ and 2) != 0) { output.writeRawVarint32(258); output.writeMessageNoTag(getVersionRequirementTable()) }
extensionWriter?.writeUntil(Int.MAX_VALUE, output)
if (!unknownFields.isEmpty()) unknownFields.writeTo(output)
}
override val serializedSize: Int get() {
val cached = memoizedSerializedSize
if (cached != -1) return cached
var size = 0
if ((bitField0_ and 1) != 0) size += 2 + CodedOutputStream.computeMessageSizeNoTag(getTypeTable())
if ((bitField0_ and 2) != 0) size += 2 + CodedOutputStream.computeMessageSizeNoTag(getVersionRequirementTable())
for (i in 0 until field_3_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_3_[i]) }
for (i in 0 until field_4_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_4_[i]) }
for (i in 0 until field_5_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_5_[i]) }
size += extensionsSerializedSize()
size += unknownFields.size()
memoizedSerializedSize = size
return size
}
override val defaultInstanceForType: Package get() = defaultInstance
override fun newBuilderForType(): Builder = Builder()
override fun toBuilder(): Builder = Builder().mergeFrom(this)
class Builder : GeneratedMessageLite.ExtendableBuilder<Package, Builder>() {
private var bitField0_: Int = 0
private var mutBitField0_: Int = 0
private var field_30_: org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable? = null
private var field_32_: org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirementTable? = null
private var field_3_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Function> = emptyList()
private fun ensure_3_Mutable() { if ((mutBitField0_ and 1) == 0) { field_3_ = ArrayList(field_3_); mutBitField0_ = mutBitField0_ or 1 } }
private var field_4_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Property> = emptyList()
private fun ensure_4_Mutable() { if ((mutBitField0_ and 2) == 0) { field_4_ = ArrayList(field_4_); mutBitField0_ = mutBitField0_ or 2 } }
private var field_5_: List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias> = emptyList()
private fun ensure_5_Mutable() { if ((mutBitField0_ and 4) == 0) { field_5_ = ArrayList(field_5_); mutBitField0_ = mutBitField0_ or 4 } }
override val defaultInstanceForType: Package get() = defaultInstance
override fun clear(): Builder {
bitField0_ = 0
mutBitField0_ = 0
field_30_ = null
field_32_ = null
field_3_ = emptyList()
field_4_ = emptyList()
field_5_ = emptyList()
clearExtensions()
unknownFields = ByteString.EMPTY
return this
}
override fun clone(): Builder = Builder().mergeFrom(buildPartial())
override fun build(): Package {
val result = buildPartial()
if (!result.isInitialized()) throw UninitializedMessageException()
return result
}
override fun buildPartial(): Package {
val result = Package(this)
val from_bitField0_ = bitField0_
var to_bitField0_ = 0
if ((from_bitField0_ and 1) != 0) to_bitField0_ = to_bitField0_ or 1
result.field_30_ = field_30_
if ((from_bitField0_ and 2) != 0) to_bitField0_ = to_bitField0_ or 2
result.field_32_ = field_32_
result.bitField0_ = to_bitField0_
if ((mutBitField0_ and 1) != 0) { field_3_ = GeneratedMessageLite.unmodifiableProtoList(field_3_); mutBitField0_ = mutBitField0_ and -2 }
result.field_3_ = field_3_
if ((mutBitField0_ and 2) != 0) { field_4_ = GeneratedMessageLite.unmodifiableProtoList(field_4_); mutBitField0_ = mutBitField0_ and -3 }
result.field_4_ = field_4_
if ((mutBitField0_ and 4) != 0) { field_5_ = GeneratedMessageLite.unmodifiableProtoList(field_5_); mutBitField0_ = mutBitField0_ and -5 }
result.field_5_ = field_5_
return result
}
override fun mergeFrom(other: Package): Builder {
if (other === defaultInstance) return this
if (other.hasTypeTable()) mergeTypeTable(other.getTypeTable())
if (other.hasVersionRequirementTable()) mergeVersionRequirementTable(other.getVersionRequirementTable())
if (other.field_3_.isNotEmpty()) {
if (field_3_.isEmpty()) { field_3_ = other.field_3_; mutBitField0_ = mutBitField0_ and -2 } else { ensure_3_Mutable(); (field_3_ as MutableList<Any>).addAll(other.field_3_ as Collection<Any>) }
}
if (other.field_4_.isNotEmpty()) {
if (field_4_.isEmpty()) { field_4_ = other.field_4_; mutBitField0_ = mutBitField0_ and -3 } else { ensure_4_Mutable(); (field_4_ as MutableList<Any>).addAll(other.field_4_ as Collection<Any>) }
}
if (other.field_5_.isNotEmpty()) {
if (field_5_.isEmpty()) { field_5_ = other.field_5_; mutBitField0_ = mutBitField0_ and -5 } else { ensure_5_Mutable(); (field_5_ as MutableList<Any>).addAll(other.field_5_ as Collection<Any>) }
}
mergeExtensionFields(other)
unknownFields = unknownFields.concat(other.unknownFields)
return this
}
override fun isInitialized(): Boolean {
if ((bitField0_ and 1) != 0 && !getTypeTable().isInitialized()) return false
for (i in 0 until field_3_.size) { if (!field_3_[i].isInitialized()) return false }
for (i in 0 until field_4_.size) { if (!field_4_[i].isInitialized()) return false }
for (i in 0 until field_5_.size) { if (!field_5_[i].isInitialized()) return false }
if (!extensionsAreInitialized()) return false
return true
}
override fun mergeFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Builder {
var parsedMessage: Package? = null
try {
parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry)
} catch (e: InvalidProtocolBufferException) {
parsedMessage = e.unfinishedMessage as? Package
throw e
} finally {
if (parsedMessage != null) mergeFrom(parsedMessage)
}
return this
}
val `functionList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Function> get() = if (field_3_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_3_)
val `functionCount`: Int get() = field_3_.size
fun getFunctionList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Function> = `functionList`
fun getFunctionCount(): Int = field_3_.size
fun getFunction(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Function = field_3_[index]
fun addFunction(value: org.jetbrains.kotlin.metadata.ProtoBuf.Function): Builder { ensure_3_Mutable(); (field_3_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Function>).add(value); return this }
fun setFunction(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Function): Builder { ensure_3_Mutable(); (field_3_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Function>)[index] = value; return this }
fun addAllFunction(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.Function>): Builder { ensure_3_Mutable(); AbstractMessageLite.Builder.addAll(values, field_3_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Function>); return this }
fun clearFunction(): Builder { field_3_ = emptyList(); mutBitField0_ = mutBitField0_ and -2; return this }
fun addFunction(value: org.jetbrains.kotlin.metadata.ProtoBuf.Function.Builder): Builder = addFunction(value.build())
fun setFunction(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Function.Builder): Builder = setFunction(index, value.build())
val `propertyList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Property> get() = if (field_4_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_4_)
val `propertyCount`: Int get() = field_4_.size
fun getPropertyList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Property> = `propertyList`
fun getPropertyCount(): Int = field_4_.size
fun getProperty(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Property = field_4_[index]
fun addProperty(value: org.jetbrains.kotlin.metadata.ProtoBuf.Property): Builder { ensure_4_Mutable(); (field_4_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Property>).add(value); return this }
fun setProperty(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Property): Builder { ensure_4_Mutable(); (field_4_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Property>)[index] = value; return this }
fun addAllProperty(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.Property>): Builder { ensure_4_Mutable(); AbstractMessageLite.Builder.addAll(values, field_4_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Property>); return this }
fun clearProperty(): Builder { field_4_ = emptyList(); mutBitField0_ = mutBitField0_ and -3; return this }
fun addProperty(value: org.jetbrains.kotlin.metadata.ProtoBuf.Property.Builder): Builder = addProperty(value.build())
fun setProperty(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Property.Builder): Builder = setProperty(index, value.build())
val `typeAliasList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias> get() = if (field_5_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_5_)
val `typeAliasCount`: Int get() = field_5_.size
fun getTypeAliasList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias> = `typeAliasList`
fun getTypeAliasCount(): Int = field_5_.size
fun getTypeAlias(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias = field_5_[index]
fun addTypeAlias(value: org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias): Builder { ensure_5_Mutable(); (field_5_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias>).add(value); return this }
fun setTypeAlias(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias): Builder { ensure_5_Mutable(); (field_5_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias>)[index] = value; return this }
fun addAllTypeAlias(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias>): Builder { ensure_5_Mutable(); AbstractMessageLite.Builder.addAll(values, field_5_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias>); return this }
fun clearTypeAlias(): Builder { field_5_ = emptyList(); mutBitField0_ = mutBitField0_ and -5; return this }
fun addTypeAlias(value: org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias.Builder): Builder = addTypeAlias(value.build())
fun setTypeAlias(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias.Builder): Builder = setTypeAlias(index, value.build())
var `typeTable`: org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable
get() = field_30_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable.defaultInstance
set(value) { field_30_ = value; bitField0_ = bitField0_ or 1 }
fun getTypeTable(): org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable = `typeTable`
fun hasTypeTable(): Boolean = (bitField0_ and 1) != 0
fun setTypeTable(value: org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable): Builder { field_30_ = value; bitField0_ = bitField0_ or 1; return this }
fun setTypeTable(value: org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable.Builder): Builder { field_30_ = value.build(); bitField0_ = bitField0_ or 1; return this }
fun mergeTypeTable(value: org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable): Builder {
val cur = field_30_
field_30_ = if ((bitField0_ and 1) != 0 && cur != null && cur !== org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable.defaultInstance) org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable.newBuilder(cur).mergeFrom(value).buildPartial() else value
bitField0_ = bitField0_ or 1
return this
}
fun clearTypeTable(): Builder { field_30_ = null; bitField0_ = bitField0_ and -2; return this }
var `versionRequirementTable`: org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirementTable
get() = field_32_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirementTable.defaultInstance
set(value) { field_32_ = value; bitField0_ = bitField0_ or 2 }
fun getVersionRequirementTable(): org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirementTable = `versionRequirementTable`
fun hasVersionRequirementTable(): Boolean = (bitField0_ and 2) != 0
fun setVersionRequirementTable(value: org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirementTable): Builder { field_32_ = value; bitField0_ = bitField0_ or 2; return this }
fun setVersionRequirementTable(value: org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirementTable.Builder): Builder { field_32_ = value.build(); bitField0_ = bitField0_ or 2; return this }
fun mergeVersionRequirementTable(value: org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirementTable): Builder {
val cur = field_32_
field_32_ = if ((bitField0_ and 2) != 0 && cur != null && cur !== org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirementTable.defaultInstance) org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirementTable.newBuilder(cur).mergeFrom(value).buildPartial() else value
bitField0_ = bitField0_ or 2
return this
}
fun clearVersionRequirementTable(): Builder { field_32_ = null; bitField0_ = bitField0_ and -3; return this }
}
companion object {
val defaultInstance: Package = Package()
fun getDefaultInstance(): Package = defaultInstance
fun newBuilder(): Builder = Builder()
fun newBuilder(prototype: Package): Builder = Builder().mergeFrom(prototype)
val PARSER: Parser<Package> = object : AbstractParser<Package>() {
override fun parsePartialFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Package =
Package(input, extensionRegistry)
}
fun parseFrom(bytes: ByteArray): Package = PARSER.parseFrom(bytes)
fun parseFrom(bytes: ByteArray, registry: ExtensionRegistryLite): Package = PARSER.parseFrom(bytes, registry)
fun parseFrom(bytes: ByteString): Package { val input = bytes.newCodedInput(); val msg = PARSER.parsePartialFrom(input, ExtensionRegistryLite.getEmptyRegistry()); input.checkLastTagWas(0); if (!msg.isInitialized()) throw UninitializedMessageException().asInvalidProtocolBufferException().setUnfinishedMessage(msg); return msg }
fun parseFrom(input: CodedInputStream): Package = PARSER.parseFrom(input)
fun parseFrom(input: CodedInputStream, registry: ExtensionRegistryLite): Package = PARSER.parseFrom(input, registry)
const val FUNCTION_FIELD_NUMBER = 3
const val PROPERTY_FIELD_NUMBER = 4
const val TYPE_ALIAS_FIELD_NUMBER = 5
const val TYPE_TABLE_FIELD_NUMBER = 30
const val VERSION_REQUIREMENT_TABLE_FIELD_NUMBER = 32
}}
class TypeTable : GeneratedMessageLite.ExtendableMessage<TypeTable> {
internal var bitField0_: Int = 0
internal var field_2_: Int = -1
internal var field_1_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> = emptyList()
private var memoizedIsInitialized: Byte = -1
private var memoizedSerializedSize: Int = -1
private constructor()
private constructor(builder: Builder) {
this.unknownFields = builder.unknownFields
}
private constructor(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite) {
var mutBit0_ = 0
var unknownBuffer: ByteString.Output? = null
var unknownOut: CodedOutputStream? = null
fun unknownOutput(): CodedOutputStream {
var out = unknownOut
if (out == null) { val buf = ByteString.newOutput(); unknownBuffer = buf; out = CodedOutputStream.newInstance(buf); unknownOut = out }
return out
}
try {
var done = false
while (!done) {
val tag = input.readTag()
when (tag) {
0 -> done = true
10 -> {
if ((mutBit0_ and 1) == 0) { field_1_ = ArrayList(); mutBit0_ = mutBit0_ or 1 }
(field_1_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Type>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Type.PARSER, extensionRegistry))
}
16 -> { bitField0_ = bitField0_ or 1; field_2_ = input.readInt32() }
else -> if (!input.skipField(tag, unknownOutput())) done = true
}}
} catch (e: InvalidProtocolBufferException) {
throw e.setUnfinishedMessage(this)
} finally {
if ((mutBit0_ and 1) != 0) field_1_ = GeneratedMessageLite.unmodifiableProtoList(field_1_)
if (unknownBuffer != null) { unknownOut!!.flush(); unknownFields = unknownBuffer.toByteString() }
}}
val `typeList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> get() = field_1_
val `typeCount`: Int get() = field_1_.size
fun getTypeList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> = field_1_
fun getTypeCount(): Int = field_1_.size
fun getType(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Type = field_1_[index]
val `firstNullable`: Int get() = field_2_
fun getFirstNullable(): Int = `firstNullable`
fun hasFirstNullable(): Boolean = (bitField0_ and 1) != 0
override fun isInitialized(): Boolean {
val isInitialized = memoizedIsInitialized.toInt()
if (isInitialized == 1) return true
if (isInitialized == 0) return false
for (i in 0 until field_1_.size) { if (!field_1_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
memoizedIsInitialized = 1
return true
}
override fun writeTo(output: CodedOutputStream) {
for (i in 0 until field_1_.size) { output.writeRawVarint32(10); output.writeMessageNoTag(field_1_[i]) }
if ((bitField0_ and 1) != 0) { output.writeRawVarint32(16); output.writeInt32NoTag(field_2_) }
if (!unknownFields.isEmpty()) unknownFields.writeTo(output)
}
override val serializedSize: Int get() {
val cached = memoizedSerializedSize
if (cached != -1) return cached
var size = 0
if ((bitField0_ and 1) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_2_)
for (i in 0 until field_1_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_1_[i]) }
size += unknownFields.size()
memoizedSerializedSize = size
return size
}
override val defaultInstanceForType: TypeTable get() = defaultInstance
override fun newBuilderForType(): Builder = Builder()
override fun toBuilder(): Builder = Builder().mergeFrom(this)
class Builder : GeneratedMessageLite.ExtendableBuilder<TypeTable, Builder>() {
private var bitField0_: Int = 0
private var mutBitField0_: Int = 0
private var field_2_: Int = -1
private var field_1_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> = emptyList()
private fun ensure_1_Mutable() { if ((mutBitField0_ and 1) == 0) { field_1_ = ArrayList(field_1_); mutBitField0_ = mutBitField0_ or 1 } }
override val defaultInstanceForType: TypeTable get() = defaultInstance
override fun clear(): Builder {
bitField0_ = 0
mutBitField0_ = 0
field_2_ = -1
field_1_ = emptyList()
unknownFields = ByteString.EMPTY
return this
}
override fun clone(): Builder = Builder().mergeFrom(buildPartial())
override fun build(): TypeTable {
val result = buildPartial()
if (!result.isInitialized()) throw UninitializedMessageException()
return result
}
override fun buildPartial(): TypeTable {
val result = TypeTable(this)
val from_bitField0_ = bitField0_
var to_bitField0_ = 0
if ((from_bitField0_ and 1) != 0) to_bitField0_ = to_bitField0_ or 1
result.field_2_ = field_2_
result.bitField0_ = to_bitField0_
if ((mutBitField0_ and 1) != 0) { field_1_ = GeneratedMessageLite.unmodifiableProtoList(field_1_); mutBitField0_ = mutBitField0_ and -2 }
result.field_1_ = field_1_
return result
}
override fun mergeFrom(other: TypeTable): Builder {
if (other === defaultInstance) return this
if (other.hasFirstNullable()) setFirstNullable(other.getFirstNullable())
if (other.field_1_.isNotEmpty()) {
if (field_1_.isEmpty()) { field_1_ = other.field_1_; mutBitField0_ = mutBitField0_ and -2 } else { ensure_1_Mutable(); (field_1_ as MutableList<Any>).addAll(other.field_1_ as Collection<Any>) }
}
unknownFields = unknownFields.concat(other.unknownFields)
return this
}
override fun isInitialized(): Boolean {
for (i in 0 until field_1_.size) { if (!field_1_[i].isInitialized()) return false }
return true
}
override fun mergeFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Builder {
var parsedMessage: TypeTable? = null
try {
parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry)
} catch (e: InvalidProtocolBufferException) {
parsedMessage = e.unfinishedMessage as? TypeTable
throw e
} finally {
if (parsedMessage != null) mergeFrom(parsedMessage)
}
return this
}
val `typeList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> get() = if (field_1_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_1_)
val `typeCount`: Int get() = field_1_.size
fun getTypeList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> = `typeList`
fun getTypeCount(): Int = field_1_.size
fun getType(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Type = field_1_[index]
fun addType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder { ensure_1_Mutable(); (field_1_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Type>).add(value); return this }
fun setType(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder { ensure_1_Mutable(); (field_1_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Type>)[index] = value; return this }
fun addAllType(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.Type>): Builder { ensure_1_Mutable(); AbstractMessageLite.Builder.addAll(values, field_1_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Type>); return this }
fun clearType(): Builder { field_1_ = emptyList(); mutBitField0_ = mutBitField0_ and -2; return this }
fun addType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder): Builder = addType(value.build())
fun setType(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder): Builder = setType(index, value.build())
var `firstNullable`: Int
get() = field_2_
set(value) { bitField0_ = bitField0_ or 1; field_2_ = value }
fun getFirstNullable(): Int = field_2_
fun hasFirstNullable(): Boolean = (bitField0_ and 1) != 0
fun setFirstNullable(value: Int): Builder { bitField0_ = bitField0_ or 1; field_2_ = value; return this }
fun clearFirstNullable(): Builder { bitField0_ = bitField0_ and -2; field_2_ = -1; return this }
}
companion object {
val defaultInstance: TypeTable = TypeTable()
fun getDefaultInstance(): TypeTable = defaultInstance
fun newBuilder(): Builder = Builder()
fun newBuilder(prototype: TypeTable): Builder = Builder().mergeFrom(prototype)
val PARSER: Parser<TypeTable> = object : AbstractParser<TypeTable>() {
override fun parsePartialFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): TypeTable =
TypeTable(input, extensionRegistry)
}
fun parseFrom(bytes: ByteArray): TypeTable = PARSER.parseFrom(bytes)
fun parseFrom(bytes: ByteArray, registry: ExtensionRegistryLite): TypeTable = PARSER.parseFrom(bytes, registry)
fun parseFrom(bytes: ByteString): TypeTable { val input = bytes.newCodedInput(); val msg = PARSER.parsePartialFrom(input, ExtensionRegistryLite.getEmptyRegistry()); input.checkLastTagWas(0); if (!msg.isInitialized()) throw UninitializedMessageException().asInvalidProtocolBufferException().setUnfinishedMessage(msg); return msg }
fun parseFrom(input: CodedInputStream): TypeTable = PARSER.parseFrom(input)
fun parseFrom(input: CodedInputStream, registry: ExtensionRegistryLite): TypeTable = PARSER.parseFrom(input, registry)
const val TYPE_FIELD_NUMBER = 1
const val FIRST_NULLABLE_FIELD_NUMBER = 2
}}
class Constructor : GeneratedMessageLite.ExtendableMessage<Constructor> {
internal var bitField0_: Int = 0
internal var field_1_: Int = 6
internal var field_2_: List<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter> = emptyList()
internal var field_31_: List<Int> = emptyList()
internal var field_32_: List<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData> = emptyList()
internal var field_3_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = emptyList()
private var memoizedIsInitialized: Byte = -1
private var memoizedSerializedSize: Int = -1
private constructor()
private constructor(builder: Builder) {
this.unknownFields = builder.unknownFields
copyExtensionsFromBuilder(builder)
}
private constructor(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite) {
var mutBit0_ = 0
var unknownBuffer: ByteString.Output? = null
var unknownOut: CodedOutputStream? = null
fun unknownOutput(): CodedOutputStream {
var out = unknownOut
if (out == null) { val buf = ByteString.newOutput(); unknownBuffer = buf; out = CodedOutputStream.newInstance(buf); unknownOut = out }
return out
}
try {
var done = false
while (!done) {
val tag = input.readTag()
when (tag) {
0 -> done = true
8 -> { bitField0_ = bitField0_ or 1; field_1_ = input.readInt32() }
18 -> {
if ((mutBit0_ and 1) == 0) { field_2_ = ArrayList(); mutBit0_ = mutBit0_ or 1 }
(field_2_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter.PARSER, extensionRegistry))
}
26 -> {
if ((mutBit0_ and 8) == 0) { field_3_ = ArrayList(); mutBit0_ = mutBit0_ or 8 }
(field_3_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.PARSER, extensionRegistry))
}
248 -> {
if ((mutBit0_ and 2) == 0) { field_31_ = ArrayList(); mutBit0_ = mutBit0_ or 2 }
(field_31_ as MutableList<Int>).add(input.readInt32())
}
250 -> {
val length = input.readRawVarint32()
val oldLimit = input.pushLimit(length)
try {
if (length > 0 && (mutBit0_ and 2) == 0) { field_31_ = ArrayList(); mutBit0_ = mutBit0_ or 2 }
while (input.getBytesUntilLimit() > 0) { (field_31_ as MutableList<Int>).add(input.readInt32()) }
} finally { input.popLimit(oldLimit) }
}
258 -> {
if ((mutBit0_ and 4) == 0) { field_32_ = ArrayList(); mutBit0_ = mutBit0_ or 4 }
(field_32_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData.PARSER, extensionRegistry))
}
else -> if (!parseUnknownField(input, unknownOutput(), extensionRegistry, tag)) done = true
}}
} catch (e: InvalidProtocolBufferException) {
throw e.setUnfinishedMessage(this)
} finally {
if ((mutBit0_ and 1) != 0) field_2_ = GeneratedMessageLite.unmodifiableProtoList(field_2_)
if ((mutBit0_ and 2) != 0) field_31_ = GeneratedMessageLite.unmodifiableProtoList(field_31_)
if ((mutBit0_ and 4) != 0) field_32_ = GeneratedMessageLite.unmodifiableProtoList(field_32_)
if ((mutBit0_ and 8) != 0) field_3_ = GeneratedMessageLite.unmodifiableProtoList(field_3_)
if (unknownBuffer != null) { unknownOut!!.flush(); unknownFields = unknownBuffer.toByteString() }
makeExtensionsImmutable()
}}
val `flags`: Int get() = field_1_
fun getFlags(): Int = `flags`
fun hasFlags(): Boolean = (bitField0_ and 1) != 0
val `valueParameterList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter> get() = field_2_
val `valueParameterCount`: Int get() = field_2_.size
fun getValueParameterList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter> = field_2_
fun getValueParameterCount(): Int = field_2_.size
fun getValueParameter(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter = field_2_[index]
val `annotationList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> get() = field_3_
val `annotationCount`: Int get() = field_3_.size
fun getAnnotationList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = field_3_
fun getAnnotationCount(): Int = field_3_.size
fun getAnnotation(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation = field_3_[index]
val `versionRequirementList`: List<Int> get() = field_31_
val `versionRequirementCount`: Int get() = field_31_.size
fun getVersionRequirementList(): List<Int> = field_31_
fun getVersionRequirementCount(): Int = field_31_.size
fun getVersionRequirement(index: Int): Int = field_31_[index]
val `compilerPluginDataList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData> get() = field_32_
val `compilerPluginDataCount`: Int get() = field_32_.size
fun getCompilerPluginDataList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData> = field_32_
fun getCompilerPluginDataCount(): Int = field_32_.size
fun getCompilerPluginData(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData = field_32_[index]
override fun isInitialized(): Boolean {
val isInitialized = memoizedIsInitialized.toInt()
if (isInitialized == 1) return true
if (isInitialized == 0) return false
for (i in 0 until field_2_.size) { if (!field_2_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
for (i in 0 until field_32_.size) { if (!field_32_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
for (i in 0 until field_3_.size) { if (!field_3_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
if (!extensionsAreInitialized()) { memoizedIsInitialized = 0; return false }
memoizedIsInitialized = 1
return true
}
override fun writeTo(output: CodedOutputStream) {
val extensionWriter = if (hasAnyExtensions()) newExtensionWriter() else null
extensionWriter?.writeUntil(1, output)
if ((bitField0_ and 1) != 0) { output.writeRawVarint32(8); output.writeInt32NoTag(field_1_) }
extensionWriter?.writeUntil(2, output)
for (i in 0 until field_2_.size) { output.writeRawVarint32(18); output.writeMessageNoTag(field_2_[i]) }
extensionWriter?.writeUntil(3, output)
for (i in 0 until field_3_.size) { output.writeRawVarint32(26); output.writeMessageNoTag(field_3_[i]) }
extensionWriter?.writeUntil(31, output)
for (i in 0 until field_31_.size) { output.writeRawVarint32(248); output.writeInt32NoTag(field_31_[i]) }
extensionWriter?.writeUntil(32, output)
for (i in 0 until field_32_.size) { output.writeRawVarint32(258); output.writeMessageNoTag(field_32_[i]) }
extensionWriter?.writeUntil(Int.MAX_VALUE, output)
if (!unknownFields.isEmpty()) unknownFields.writeTo(output)
}
override val serializedSize: Int get() {
val cached = memoizedSerializedSize
if (cached != -1) return cached
var size = 0
if ((bitField0_ and 1) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_1_)
for (i in 0 until field_2_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_2_[i]) }
for (i in 0 until field_31_.size) { size += 2 + CodedOutputStream.computeInt32SizeNoTag(field_31_[i]) }
for (i in 0 until field_32_.size) { size += 2 + CodedOutputStream.computeMessageSizeNoTag(field_32_[i]) }
for (i in 0 until field_3_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_3_[i]) }
size += extensionsSerializedSize()
size += unknownFields.size()
memoizedSerializedSize = size
return size
}
override val defaultInstanceForType: Constructor get() = defaultInstance
override fun newBuilderForType(): Builder = Builder()
override fun toBuilder(): Builder = Builder().mergeFrom(this)
class Builder : GeneratedMessageLite.ExtendableBuilder<Constructor, Builder>() {
private var bitField0_: Int = 0
private var mutBitField0_: Int = 0
private var field_1_: Int = 6
private var field_2_: List<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter> = emptyList()
private fun ensure_2_Mutable() { if ((mutBitField0_ and 1) == 0) { field_2_ = ArrayList(field_2_); mutBitField0_ = mutBitField0_ or 1 } }
private var field_31_: List<Int> = emptyList()
private fun ensure_31_Mutable() { if ((mutBitField0_ and 2) == 0) { field_31_ = ArrayList(field_31_); mutBitField0_ = mutBitField0_ or 2 } }
private var field_32_: List<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData> = emptyList()
private fun ensure_32_Mutable() { if ((mutBitField0_ and 4) == 0) { field_32_ = ArrayList(field_32_); mutBitField0_ = mutBitField0_ or 4 } }
private var field_3_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = emptyList()
private fun ensure_3_Mutable() { if ((mutBitField0_ and 8) == 0) { field_3_ = ArrayList(field_3_); mutBitField0_ = mutBitField0_ or 8 } }
override val defaultInstanceForType: Constructor get() = defaultInstance
override fun clear(): Builder {
bitField0_ = 0
mutBitField0_ = 0
field_1_ = 6
field_2_ = emptyList()
field_31_ = emptyList()
field_32_ = emptyList()
field_3_ = emptyList()
clearExtensions()
unknownFields = ByteString.EMPTY
return this
}
override fun clone(): Builder = Builder().mergeFrom(buildPartial())
override fun build(): Constructor {
val result = buildPartial()
if (!result.isInitialized()) throw UninitializedMessageException()
return result
}
override fun buildPartial(): Constructor {
val result = Constructor(this)
val from_bitField0_ = bitField0_
var to_bitField0_ = 0
if ((from_bitField0_ and 1) != 0) to_bitField0_ = to_bitField0_ or 1
result.field_1_ = field_1_
result.bitField0_ = to_bitField0_
if ((mutBitField0_ and 1) != 0) { field_2_ = GeneratedMessageLite.unmodifiableProtoList(field_2_); mutBitField0_ = mutBitField0_ and -2 }
result.field_2_ = field_2_
if ((mutBitField0_ and 2) != 0) { field_31_ = GeneratedMessageLite.unmodifiableProtoList(field_31_); mutBitField0_ = mutBitField0_ and -3 }
result.field_31_ = field_31_
if ((mutBitField0_ and 4) != 0) { field_32_ = GeneratedMessageLite.unmodifiableProtoList(field_32_); mutBitField0_ = mutBitField0_ and -5 }
result.field_32_ = field_32_
if ((mutBitField0_ and 8) != 0) { field_3_ = GeneratedMessageLite.unmodifiableProtoList(field_3_); mutBitField0_ = mutBitField0_ and -9 }
result.field_3_ = field_3_
return result
}
override fun mergeFrom(other: Constructor): Builder {
if (other === defaultInstance) return this
if (other.hasFlags()) setFlags(other.getFlags())
if (other.field_2_.isNotEmpty()) {
if (field_2_.isEmpty()) { field_2_ = other.field_2_; mutBitField0_ = mutBitField0_ and -2 } else { ensure_2_Mutable(); (field_2_ as MutableList<Any>).addAll(other.field_2_ as Collection<Any>) }
}
if (other.field_31_.isNotEmpty()) {
if (field_31_.isEmpty()) { field_31_ = other.field_31_; mutBitField0_ = mutBitField0_ and -3 } else { ensure_31_Mutable(); (field_31_ as MutableList<Any>).addAll(other.field_31_ as Collection<Any>) }
}
if (other.field_32_.isNotEmpty()) {
if (field_32_.isEmpty()) { field_32_ = other.field_32_; mutBitField0_ = mutBitField0_ and -5 } else { ensure_32_Mutable(); (field_32_ as MutableList<Any>).addAll(other.field_32_ as Collection<Any>) }
}
if (other.field_3_.isNotEmpty()) {
if (field_3_.isEmpty()) { field_3_ = other.field_3_; mutBitField0_ = mutBitField0_ and -9 } else { ensure_3_Mutable(); (field_3_ as MutableList<Any>).addAll(other.field_3_ as Collection<Any>) }
}
mergeExtensionFields(other)
unknownFields = unknownFields.concat(other.unknownFields)
return this
}
override fun isInitialized(): Boolean {
for (i in 0 until field_2_.size) { if (!field_2_[i].isInitialized()) return false }
for (i in 0 until field_32_.size) { if (!field_32_[i].isInitialized()) return false }
for (i in 0 until field_3_.size) { if (!field_3_[i].isInitialized()) return false }
if (!extensionsAreInitialized()) return false
return true
}
override fun mergeFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Builder {
var parsedMessage: Constructor? = null
try {
parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry)
} catch (e: InvalidProtocolBufferException) {
parsedMessage = e.unfinishedMessage as? Constructor
throw e
} finally {
if (parsedMessage != null) mergeFrom(parsedMessage)
}
return this
}
var `flags`: Int
get() = field_1_
set(value) { bitField0_ = bitField0_ or 1; field_1_ = value }
fun getFlags(): Int = field_1_
fun hasFlags(): Boolean = (bitField0_ and 1) != 0
fun setFlags(value: Int): Builder { bitField0_ = bitField0_ or 1; field_1_ = value; return this }
fun clearFlags(): Builder { bitField0_ = bitField0_ and -2; field_1_ = 6; return this }
val `valueParameterList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter> get() = if (field_2_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_2_)
val `valueParameterCount`: Int get() = field_2_.size
fun getValueParameterList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter> = `valueParameterList`
fun getValueParameterCount(): Int = field_2_.size
fun getValueParameter(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter = field_2_[index]
fun addValueParameter(value: org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter): Builder { ensure_2_Mutable(); (field_2_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter>).add(value); return this }
fun setValueParameter(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter): Builder { ensure_2_Mutable(); (field_2_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter>)[index] = value; return this }
fun addAllValueParameter(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter>): Builder { ensure_2_Mutable(); AbstractMessageLite.Builder.addAll(values, field_2_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter>); return this }
fun clearValueParameter(): Builder { field_2_ = emptyList(); mutBitField0_ = mutBitField0_ and -2; return this }
fun addValueParameter(value: org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter.Builder): Builder = addValueParameter(value.build())
fun setValueParameter(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter.Builder): Builder = setValueParameter(index, value.build())
val `annotationList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> get() = if (field_3_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_3_)
val `annotationCount`: Int get() = field_3_.size
fun getAnnotationList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = `annotationList`
fun getAnnotationCount(): Int = field_3_.size
fun getAnnotation(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation = field_3_[index]
fun addAnnotation(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation): Builder { ensure_3_Mutable(); (field_3_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>).add(value); return this }
fun setAnnotation(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation): Builder { ensure_3_Mutable(); (field_3_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>)[index] = value; return this }
fun addAllAnnotation(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>): Builder { ensure_3_Mutable(); AbstractMessageLite.Builder.addAll(values, field_3_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>); return this }
fun clearAnnotation(): Builder { field_3_ = emptyList(); mutBitField0_ = mutBitField0_ and -9; return this }
fun addAnnotation(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Builder): Builder = addAnnotation(value.build())
fun setAnnotation(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Builder): Builder = setAnnotation(index, value.build())
val `versionRequirementList`: List<Int> get() = if (field_31_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_31_)
val `versionRequirementCount`: Int get() = field_31_.size
fun getVersionRequirementList(): List<Int> = `versionRequirementList`
fun getVersionRequirementCount(): Int = field_31_.size
fun getVersionRequirement(index: Int): Int = field_31_[index]
fun addVersionRequirement(value: Int): Builder { ensure_31_Mutable(); (field_31_ as MutableList<Int>).add(value); return this }
fun setVersionRequirement(index: Int, value: Int): Builder { ensure_31_Mutable(); (field_31_ as MutableList<Int>)[index] = value; return this }
fun addAllVersionRequirement(values: Iterable<Int>): Builder { ensure_31_Mutable(); AbstractMessageLite.Builder.addAll(values, field_31_ as MutableList<Int>); return this }
fun clearVersionRequirement(): Builder { field_31_ = emptyList(); mutBitField0_ = mutBitField0_ and -3; return this }
val `compilerPluginDataList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData> get() = if (field_32_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_32_)
val `compilerPluginDataCount`: Int get() = field_32_.size
fun getCompilerPluginDataList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData> = `compilerPluginDataList`
fun getCompilerPluginDataCount(): Int = field_32_.size
fun getCompilerPluginData(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData = field_32_[index]
fun addCompilerPluginData(value: org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData): Builder { ensure_32_Mutable(); (field_32_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData>).add(value); return this }
fun setCompilerPluginData(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData): Builder { ensure_32_Mutable(); (field_32_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData>)[index] = value; return this }
fun addAllCompilerPluginData(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData>): Builder { ensure_32_Mutable(); AbstractMessageLite.Builder.addAll(values, field_32_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData>); return this }
fun clearCompilerPluginData(): Builder { field_32_ = emptyList(); mutBitField0_ = mutBitField0_ and -5; return this }
fun addCompilerPluginData(value: org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData.Builder): Builder = addCompilerPluginData(value.build())
fun setCompilerPluginData(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData.Builder): Builder = setCompilerPluginData(index, value.build())
}
companion object {
val defaultInstance: Constructor = Constructor()
fun getDefaultInstance(): Constructor = defaultInstance
fun newBuilder(): Builder = Builder()
fun newBuilder(prototype: Constructor): Builder = Builder().mergeFrom(prototype)
val PARSER: Parser<Constructor> = object : AbstractParser<Constructor>() {
override fun parsePartialFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Constructor =
Constructor(input, extensionRegistry)
}
fun parseFrom(bytes: ByteArray): Constructor = PARSER.parseFrom(bytes)
fun parseFrom(bytes: ByteArray, registry: ExtensionRegistryLite): Constructor = PARSER.parseFrom(bytes, registry)
fun parseFrom(bytes: ByteString): Constructor { val input = bytes.newCodedInput(); val msg = PARSER.parsePartialFrom(input, ExtensionRegistryLite.getEmptyRegistry()); input.checkLastTagWas(0); if (!msg.isInitialized()) throw UninitializedMessageException().asInvalidProtocolBufferException().setUnfinishedMessage(msg); return msg }
fun parseFrom(input: CodedInputStream): Constructor = PARSER.parseFrom(input)
fun parseFrom(input: CodedInputStream, registry: ExtensionRegistryLite): Constructor = PARSER.parseFrom(input, registry)
const val FLAGS_FIELD_NUMBER = 1
const val VALUE_PARAMETER_FIELD_NUMBER = 2
const val ANNOTATION_FIELD_NUMBER = 3
const val VERSION_REQUIREMENT_FIELD_NUMBER = 31
const val COMPILER_PLUGIN_DATA_FIELD_NUMBER = 32
}}
class Function : GeneratedMessageLite.ExtendableMessage<Function> {
internal var bitField0_: Int = 0
internal var field_9_: Int = 6
internal var field_1_: Int = 6
internal var field_2_: Int = 0
internal var field_3_: org.jetbrains.kotlin.metadata.ProtoBuf.Type? = null
internal var field_7_: Int = 0
internal var field_5_: org.jetbrains.kotlin.metadata.ProtoBuf.Type? = null
internal var field_8_: Int = 0
internal var field_14_: org.jetbrains.kotlin.metadata.ProtoBuf.Type? = null
internal var field_15_: Int = 0
internal var field_30_: org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable? = null
internal var field_32_: org.jetbrains.kotlin.metadata.ProtoBuf.Contract? = null
internal var field_4_: List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter> = emptyList()
internal var field_10_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> = emptyList()
internal var field_11_: List<Int> = emptyList()
private var packedSize_11_: Int = -1
internal var field_13_: List<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter> = emptyList()
internal var field_6_: List<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter> = emptyList()
internal var field_31_: List<Int> = emptyList()
internal var field_33_: List<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData> = emptyList()
internal var field_12_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = emptyList()
internal var field_34_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = emptyList()
private var memoizedIsInitialized: Byte = -1
private var memoizedSerializedSize: Int = -1
private constructor()
private constructor(builder: Builder) {
this.unknownFields = builder.unknownFields
copyExtensionsFromBuilder(builder)
}
private constructor(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite) {
var mutBit0_ = 0
var unknownBuffer: ByteString.Output? = null
var unknownOut: CodedOutputStream? = null
fun unknownOutput(): CodedOutputStream {
var out = unknownOut
if (out == null) { val buf = ByteString.newOutput(); unknownBuffer = buf; out = CodedOutputStream.newInstance(buf); unknownOut = out }
return out
}
try {
var done = false
while (!done) {
val tag = input.readTag()
when (tag) {
0 -> done = true
8 -> { bitField0_ = bitField0_ or 2; field_1_ = input.readInt32() }
16 -> { bitField0_ = bitField0_ or 4; field_2_ = input.readInt32() }
26 -> {
var subBuilder: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder? = null
if ((bitField0_ and 8) != 0) subBuilder = field_3_?.toBuilder()
val parsed = input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Type.PARSER, extensionRegistry)
field_3_ = if (subBuilder != null) subBuilder.mergeFrom(parsed).buildPartial() else parsed
bitField0_ = bitField0_ or 8
}
34 -> {
if ((mutBit0_ and 1) == 0) { field_4_ = ArrayList(); mutBit0_ = mutBit0_ or 1 }
(field_4_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter.PARSER, extensionRegistry))
}
42 -> {
var subBuilder: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder? = null
if ((bitField0_ and 32) != 0) subBuilder = field_5_?.toBuilder()
val parsed = input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Type.PARSER, extensionRegistry)
field_5_ = if (subBuilder != null) subBuilder.mergeFrom(parsed).buildPartial() else parsed
bitField0_ = bitField0_ or 32
}
50 -> {
if ((mutBit0_ and 16) == 0) { field_6_ = ArrayList(); mutBit0_ = mutBit0_ or 16 }
(field_6_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter.PARSER, extensionRegistry))
}
56 -> { bitField0_ = bitField0_ or 16; field_7_ = input.readInt32() }
64 -> { bitField0_ = bitField0_ or 64; field_8_ = input.readInt32() }
72 -> { bitField0_ = bitField0_ or 1; field_9_ = input.readInt32() }
82 -> {
if ((mutBit0_ and 2) == 0) { field_10_ = ArrayList(); mutBit0_ = mutBit0_ or 2 }
(field_10_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Type>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Type.PARSER, extensionRegistry))
}
88 -> {
if ((mutBit0_ and 4) == 0) { field_11_ = ArrayList(); mutBit0_ = mutBit0_ or 4 }
(field_11_ as MutableList<Int>).add(input.readInt32())
}
90 -> {
val length = input.readRawVarint32()
val oldLimit = input.pushLimit(length)
try {
if (length > 0 && (mutBit0_ and 4) == 0) { field_11_ = ArrayList(); mutBit0_ = mutBit0_ or 4 }
while (input.getBytesUntilLimit() > 0) { (field_11_ as MutableList<Int>).add(input.readInt32()) }
} finally { input.popLimit(oldLimit) }
}
98 -> {
if ((mutBit0_ and 128) == 0) { field_12_ = ArrayList(); mutBit0_ = mutBit0_ or 128 }
(field_12_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.PARSER, extensionRegistry))
}
106 -> {
if ((mutBit0_ and 8) == 0) { field_13_ = ArrayList(); mutBit0_ = mutBit0_ or 8 }
(field_13_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter.PARSER, extensionRegistry))
}
114 -> {
var subBuilder: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder? = null
if ((bitField0_ and 128) != 0) subBuilder = field_14_?.toBuilder()
val parsed = input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Type.PARSER, extensionRegistry)
field_14_ = if (subBuilder != null) subBuilder.mergeFrom(parsed).buildPartial() else parsed
bitField0_ = bitField0_ or 128
}
120 -> { bitField0_ = bitField0_ or 256; field_15_ = input.readInt32() }
242 -> {
var subBuilder: org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable.Builder? = null
if ((bitField0_ and 512) != 0) subBuilder = field_30_?.toBuilder()
val parsed = input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable.PARSER, extensionRegistry)
field_30_ = if (subBuilder != null) subBuilder.mergeFrom(parsed).buildPartial() else parsed
bitField0_ = bitField0_ or 512
}
248 -> {
if ((mutBit0_ and 32) == 0) { field_31_ = ArrayList(); mutBit0_ = mutBit0_ or 32 }
(field_31_ as MutableList<Int>).add(input.readInt32())
}
250 -> {
val length = input.readRawVarint32()
val oldLimit = input.pushLimit(length)
try {
if (length > 0 && (mutBit0_ and 32) == 0) { field_31_ = ArrayList(); mutBit0_ = mutBit0_ or 32 }
while (input.getBytesUntilLimit() > 0) { (field_31_ as MutableList<Int>).add(input.readInt32()) }
} finally { input.popLimit(oldLimit) }
}
258 -> {
var subBuilder: org.jetbrains.kotlin.metadata.ProtoBuf.Contract.Builder? = null
if ((bitField0_ and 1024) != 0) subBuilder = field_32_?.toBuilder()
val parsed = input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Contract.PARSER, extensionRegistry)
field_32_ = if (subBuilder != null) subBuilder.mergeFrom(parsed).buildPartial() else parsed
bitField0_ = bitField0_ or 1024
}
266 -> {
if ((mutBit0_ and 64) == 0) { field_33_ = ArrayList(); mutBit0_ = mutBit0_ or 64 }
(field_33_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData.PARSER, extensionRegistry))
}
274 -> {
if ((mutBit0_ and 256) == 0) { field_34_ = ArrayList(); mutBit0_ = mutBit0_ or 256 }
(field_34_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.PARSER, extensionRegistry))
}
else -> if (!parseUnknownField(input, unknownOutput(), extensionRegistry, tag)) done = true
}}
} catch (e: InvalidProtocolBufferException) {
throw e.setUnfinishedMessage(this)
} finally {
if ((mutBit0_ and 1) != 0) field_4_ = GeneratedMessageLite.unmodifiableProtoList(field_4_)
if ((mutBit0_ and 2) != 0) field_10_ = GeneratedMessageLite.unmodifiableProtoList(field_10_)
if ((mutBit0_ and 4) != 0) field_11_ = GeneratedMessageLite.unmodifiableProtoList(field_11_)
if ((mutBit0_ and 8) != 0) field_13_ = GeneratedMessageLite.unmodifiableProtoList(field_13_)
if ((mutBit0_ and 16) != 0) field_6_ = GeneratedMessageLite.unmodifiableProtoList(field_6_)
if ((mutBit0_ and 32) != 0) field_31_ = GeneratedMessageLite.unmodifiableProtoList(field_31_)
if ((mutBit0_ and 64) != 0) field_33_ = GeneratedMessageLite.unmodifiableProtoList(field_33_)
if ((mutBit0_ and 128) != 0) field_12_ = GeneratedMessageLite.unmodifiableProtoList(field_12_)
if ((mutBit0_ and 256) != 0) field_34_ = GeneratedMessageLite.unmodifiableProtoList(field_34_)
if (unknownBuffer != null) { unknownOut!!.flush(); unknownFields = unknownBuffer.toByteString() }
makeExtensionsImmutable()
}}
val `oldFlags`: Int get() = field_1_
fun getOldFlags(): Int = `oldFlags`
fun hasOldFlags(): Boolean = (bitField0_ and 2) != 0
val `name`: Int get() = field_2_
fun getName(): Int = `name`
fun hasName(): Boolean = (bitField0_ and 4) != 0
val `returnType`: org.jetbrains.kotlin.metadata.ProtoBuf.Type get() = field_3_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance
fun getReturnType(): org.jetbrains.kotlin.metadata.ProtoBuf.Type = `returnType`
fun hasReturnType(): Boolean = (bitField0_ and 8) != 0
val `typeParameterList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter> get() = field_4_
val `typeParameterCount`: Int get() = field_4_.size
fun getTypeParameterList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter> = field_4_
fun getTypeParameterCount(): Int = field_4_.size
fun getTypeParameter(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter = field_4_[index]
val `receiverType`: org.jetbrains.kotlin.metadata.ProtoBuf.Type get() = field_5_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance
fun getReceiverType(): org.jetbrains.kotlin.metadata.ProtoBuf.Type = `receiverType`
fun hasReceiverType(): Boolean = (bitField0_ and 32) != 0
val `valueParameterList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter> get() = field_6_
val `valueParameterCount`: Int get() = field_6_.size
fun getValueParameterList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter> = field_6_
fun getValueParameterCount(): Int = field_6_.size
fun getValueParameter(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter = field_6_[index]
val `returnTypeId`: Int get() = field_7_
fun getReturnTypeId(): Int = `returnTypeId`
fun hasReturnTypeId(): Boolean = (bitField0_ and 16) != 0
val `receiverTypeId`: Int get() = field_8_
fun getReceiverTypeId(): Int = `receiverTypeId`
fun hasReceiverTypeId(): Boolean = (bitField0_ and 64) != 0
val `flags`: Int get() = field_9_
fun getFlags(): Int = `flags`
fun hasFlags(): Boolean = (bitField0_ and 1) != 0
val `contextReceiverTypeList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> get() = field_10_
val `contextReceiverTypeCount`: Int get() = field_10_.size
fun getContextReceiverTypeList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> = field_10_
fun getContextReceiverTypeCount(): Int = field_10_.size
fun getContextReceiverType(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Type = field_10_[index]
val `contextReceiverTypeIdList`: List<Int> get() = field_11_
val `contextReceiverTypeIdCount`: Int get() = field_11_.size
fun getContextReceiverTypeIdList(): List<Int> = field_11_
fun getContextReceiverTypeIdCount(): Int = field_11_.size
fun getContextReceiverTypeId(index: Int): Int = field_11_[index]
val `annotationList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> get() = field_12_
val `annotationCount`: Int get() = field_12_.size
fun getAnnotationList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = field_12_
fun getAnnotationCount(): Int = field_12_.size
fun getAnnotation(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation = field_12_[index]
val `contextParameterList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter> get() = field_13_
val `contextParameterCount`: Int get() = field_13_.size
fun getContextParameterList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter> = field_13_
fun getContextParameterCount(): Int = field_13_.size
fun getContextParameter(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter = field_13_[index]
val `companionExtensionReceiverType`: org.jetbrains.kotlin.metadata.ProtoBuf.Type get() = field_14_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance
fun getCompanionExtensionReceiverType(): org.jetbrains.kotlin.metadata.ProtoBuf.Type = `companionExtensionReceiverType`
fun hasCompanionExtensionReceiverType(): Boolean = (bitField0_ and 128) != 0
val `companionExtensionReceiverTypeId`: Int get() = field_15_
fun getCompanionExtensionReceiverTypeId(): Int = `companionExtensionReceiverTypeId`
fun hasCompanionExtensionReceiverTypeId(): Boolean = (bitField0_ and 256) != 0
val `typeTable`: org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable get() = field_30_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable.defaultInstance
fun getTypeTable(): org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable = `typeTable`
fun hasTypeTable(): Boolean = (bitField0_ and 512) != 0
val `versionRequirementList`: List<Int> get() = field_31_
val `versionRequirementCount`: Int get() = field_31_.size
fun getVersionRequirementList(): List<Int> = field_31_
fun getVersionRequirementCount(): Int = field_31_.size
fun getVersionRequirement(index: Int): Int = field_31_[index]
val `contract`: org.jetbrains.kotlin.metadata.ProtoBuf.Contract get() = field_32_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Contract.defaultInstance
fun getContract(): org.jetbrains.kotlin.metadata.ProtoBuf.Contract = `contract`
fun hasContract(): Boolean = (bitField0_ and 1024) != 0
val `compilerPluginDataList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData> get() = field_33_
val `compilerPluginDataCount`: Int get() = field_33_.size
fun getCompilerPluginDataList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData> = field_33_
fun getCompilerPluginDataCount(): Int = field_33_.size
fun getCompilerPluginData(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData = field_33_[index]
val `extensionReceiverAnnotationList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> get() = field_34_
val `extensionReceiverAnnotationCount`: Int get() = field_34_.size
fun getExtensionReceiverAnnotationList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = field_34_
fun getExtensionReceiverAnnotationCount(): Int = field_34_.size
fun getExtensionReceiverAnnotation(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation = field_34_[index]
override fun isInitialized(): Boolean {
val isInitialized = memoizedIsInitialized.toInt()
if (isInitialized == 1) return true
if (isInitialized == 0) return false
if ((bitField0_ and 4) == 0) { memoizedIsInitialized = 0; return false }
if ((bitField0_ and 8) != 0 && !getReturnType().isInitialized()) { memoizedIsInitialized = 0; return false }
if ((bitField0_ and 32) != 0 && !getReceiverType().isInitialized()) { memoizedIsInitialized = 0; return false }
if ((bitField0_ and 128) != 0 && !getCompanionExtensionReceiverType().isInitialized()) { memoizedIsInitialized = 0; return false }
if ((bitField0_ and 512) != 0 && !getTypeTable().isInitialized()) { memoizedIsInitialized = 0; return false }
if ((bitField0_ and 1024) != 0 && !getContract().isInitialized()) { memoizedIsInitialized = 0; return false }
for (i in 0 until field_4_.size) { if (!field_4_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
for (i in 0 until field_10_.size) { if (!field_10_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
for (i in 0 until field_13_.size) { if (!field_13_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
for (i in 0 until field_6_.size) { if (!field_6_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
for (i in 0 until field_33_.size) { if (!field_33_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
for (i in 0 until field_12_.size) { if (!field_12_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
for (i in 0 until field_34_.size) { if (!field_34_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
if (!extensionsAreInitialized()) { memoizedIsInitialized = 0; return false }
memoizedIsInitialized = 1
return true
}
override fun writeTo(output: CodedOutputStream) {
serializedSize
val extensionWriter = if (hasAnyExtensions()) newExtensionWriter() else null
extensionWriter?.writeUntil(1, output)
if ((bitField0_ and 2) != 0) { output.writeRawVarint32(8); output.writeInt32NoTag(field_1_) }
extensionWriter?.writeUntil(2, output)
if ((bitField0_ and 4) != 0) { output.writeRawVarint32(16); output.writeInt32NoTag(field_2_) }
extensionWriter?.writeUntil(3, output)
if ((bitField0_ and 8) != 0) { output.writeRawVarint32(26); output.writeMessageNoTag(getReturnType()) }
extensionWriter?.writeUntil(4, output)
for (i in 0 until field_4_.size) { output.writeRawVarint32(34); output.writeMessageNoTag(field_4_[i]) }
extensionWriter?.writeUntil(5, output)
if ((bitField0_ and 32) != 0) { output.writeRawVarint32(42); output.writeMessageNoTag(getReceiverType()) }
extensionWriter?.writeUntil(6, output)
for (i in 0 until field_6_.size) { output.writeRawVarint32(50); output.writeMessageNoTag(field_6_[i]) }
extensionWriter?.writeUntil(7, output)
if ((bitField0_ and 16) != 0) { output.writeRawVarint32(56); output.writeInt32NoTag(field_7_) }
extensionWriter?.writeUntil(8, output)
if ((bitField0_ and 64) != 0) { output.writeRawVarint32(64); output.writeInt32NoTag(field_8_) }
extensionWriter?.writeUntil(9, output)
if ((bitField0_ and 1) != 0) { output.writeRawVarint32(72); output.writeInt32NoTag(field_9_) }
extensionWriter?.writeUntil(10, output)
for (i in 0 until field_10_.size) { output.writeRawVarint32(82); output.writeMessageNoTag(field_10_[i]) }
extensionWriter?.writeUntil(11, output)
if (field_11_.isNotEmpty()) {
output.writeRawVarint32(90)
output.writeRawVarint32(packedSize_11_)
for (i in 0 until field_11_.size) { output.writeInt32NoTag(field_11_[i]) }
}
extensionWriter?.writeUntil(12, output)
for (i in 0 until field_12_.size) { output.writeRawVarint32(98); output.writeMessageNoTag(field_12_[i]) }
extensionWriter?.writeUntil(13, output)
for (i in 0 until field_13_.size) { output.writeRawVarint32(106); output.writeMessageNoTag(field_13_[i]) }
extensionWriter?.writeUntil(14, output)
if ((bitField0_ and 128) != 0) { output.writeRawVarint32(114); output.writeMessageNoTag(getCompanionExtensionReceiverType()) }
extensionWriter?.writeUntil(15, output)
if ((bitField0_ and 256) != 0) { output.writeRawVarint32(120); output.writeInt32NoTag(field_15_) }
extensionWriter?.writeUntil(30, output)
if ((bitField0_ and 512) != 0) { output.writeRawVarint32(242); output.writeMessageNoTag(getTypeTable()) }
extensionWriter?.writeUntil(31, output)
for (i in 0 until field_31_.size) { output.writeRawVarint32(248); output.writeInt32NoTag(field_31_[i]) }
extensionWriter?.writeUntil(32, output)
if ((bitField0_ and 1024) != 0) { output.writeRawVarint32(258); output.writeMessageNoTag(getContract()) }
extensionWriter?.writeUntil(33, output)
for (i in 0 until field_33_.size) { output.writeRawVarint32(266); output.writeMessageNoTag(field_33_[i]) }
extensionWriter?.writeUntil(34, output)
for (i in 0 until field_34_.size) { output.writeRawVarint32(274); output.writeMessageNoTag(field_34_[i]) }
extensionWriter?.writeUntil(Int.MAX_VALUE, output)
if (!unknownFields.isEmpty()) unknownFields.writeTo(output)
}
override val serializedSize: Int get() {
val cached = memoizedSerializedSize
if (cached != -1) return cached
var size = 0
if ((bitField0_ and 1) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_9_)
if ((bitField0_ and 2) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_1_)
if ((bitField0_ and 4) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_2_)
if ((bitField0_ and 8) != 0) size += 1 + CodedOutputStream.computeMessageSizeNoTag(getReturnType())
if ((bitField0_ and 16) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_7_)
if ((bitField0_ and 32) != 0) size += 1 + CodedOutputStream.computeMessageSizeNoTag(getReceiverType())
if ((bitField0_ and 64) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_8_)
if ((bitField0_ and 128) != 0) size += 1 + CodedOutputStream.computeMessageSizeNoTag(getCompanionExtensionReceiverType())
if ((bitField0_ and 256) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_15_)
if ((bitField0_ and 512) != 0) size += 2 + CodedOutputStream.computeMessageSizeNoTag(getTypeTable())
if ((bitField0_ and 1024) != 0) size += 2 + CodedOutputStream.computeMessageSizeNoTag(getContract())
for (i in 0 until field_4_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_4_[i]) }
for (i in 0 until field_10_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_10_[i]) }
if (field_11_.isNotEmpty()) {
var dataSize = 0
for (i in 0 until field_11_.size) { dataSize += CodedOutputStream.computeInt32SizeNoTag(field_11_[i]) }
size += 1 + CodedOutputStream.computeRawVarint32Size(dataSize) + dataSize
packedSize_11_ = dataSize
} else { packedSize_11_ = 0 }
for (i in 0 until field_13_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_13_[i]) }
for (i in 0 until field_6_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_6_[i]) }
for (i in 0 until field_31_.size) { size += 2 + CodedOutputStream.computeInt32SizeNoTag(field_31_[i]) }
for (i in 0 until field_33_.size) { size += 2 + CodedOutputStream.computeMessageSizeNoTag(field_33_[i]) }
for (i in 0 until field_12_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_12_[i]) }
for (i in 0 until field_34_.size) { size += 2 + CodedOutputStream.computeMessageSizeNoTag(field_34_[i]) }
size += extensionsSerializedSize()
size += unknownFields.size()
memoizedSerializedSize = size
return size
}
override val defaultInstanceForType: Function get() = defaultInstance
override fun newBuilderForType(): Builder = Builder()
override fun toBuilder(): Builder = Builder().mergeFrom(this)
class Builder : GeneratedMessageLite.ExtendableBuilder<Function, Builder>() {
private var bitField0_: Int = 0
private var mutBitField0_: Int = 0
private var field_9_: Int = 6
private var field_1_: Int = 6
private var field_2_: Int = 0
private var field_3_: org.jetbrains.kotlin.metadata.ProtoBuf.Type? = null
private var field_7_: Int = 0
private var field_5_: org.jetbrains.kotlin.metadata.ProtoBuf.Type? = null
private var field_8_: Int = 0
private var field_14_: org.jetbrains.kotlin.metadata.ProtoBuf.Type? = null
private var field_15_: Int = 0
private var field_30_: org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable? = null
private var field_32_: org.jetbrains.kotlin.metadata.ProtoBuf.Contract? = null
private var field_4_: List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter> = emptyList()
private fun ensure_4_Mutable() { if ((mutBitField0_ and 1) == 0) { field_4_ = ArrayList(field_4_); mutBitField0_ = mutBitField0_ or 1 } }
private var field_10_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> = emptyList()
private fun ensure_10_Mutable() { if ((mutBitField0_ and 2) == 0) { field_10_ = ArrayList(field_10_); mutBitField0_ = mutBitField0_ or 2 } }
private var field_11_: List<Int> = emptyList()
private fun ensure_11_Mutable() { if ((mutBitField0_ and 4) == 0) { field_11_ = ArrayList(field_11_); mutBitField0_ = mutBitField0_ or 4 } }
private var field_13_: List<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter> = emptyList()
private fun ensure_13_Mutable() { if ((mutBitField0_ and 8) == 0) { field_13_ = ArrayList(field_13_); mutBitField0_ = mutBitField0_ or 8 } }
private var field_6_: List<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter> = emptyList()
private fun ensure_6_Mutable() { if ((mutBitField0_ and 16) == 0) { field_6_ = ArrayList(field_6_); mutBitField0_ = mutBitField0_ or 16 } }
private var field_31_: List<Int> = emptyList()
private fun ensure_31_Mutable() { if ((mutBitField0_ and 32) == 0) { field_31_ = ArrayList(field_31_); mutBitField0_ = mutBitField0_ or 32 } }
private var field_33_: List<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData> = emptyList()
private fun ensure_33_Mutable() { if ((mutBitField0_ and 64) == 0) { field_33_ = ArrayList(field_33_); mutBitField0_ = mutBitField0_ or 64 } }
private var field_12_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = emptyList()
private fun ensure_12_Mutable() { if ((mutBitField0_ and 128) == 0) { field_12_ = ArrayList(field_12_); mutBitField0_ = mutBitField0_ or 128 } }
private var field_34_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = emptyList()
private fun ensure_34_Mutable() { if ((mutBitField0_ and 256) == 0) { field_34_ = ArrayList(field_34_); mutBitField0_ = mutBitField0_ or 256 } }
override val defaultInstanceForType: Function get() = defaultInstance
override fun clear(): Builder {
bitField0_ = 0
mutBitField0_ = 0
field_9_ = 6
field_1_ = 6
field_2_ = 0
field_3_ = null
field_7_ = 0
field_5_ = null
field_8_ = 0
field_14_ = null
field_15_ = 0
field_30_ = null
field_32_ = null
field_4_ = emptyList()
field_10_ = emptyList()
field_11_ = emptyList()
field_13_ = emptyList()
field_6_ = emptyList()
field_31_ = emptyList()
field_33_ = emptyList()
field_12_ = emptyList()
field_34_ = emptyList()
clearExtensions()
unknownFields = ByteString.EMPTY
return this
}
override fun clone(): Builder = Builder().mergeFrom(buildPartial())
override fun build(): Function {
val result = buildPartial()
if (!result.isInitialized()) throw UninitializedMessageException()
return result
}
override fun buildPartial(): Function {
val result = Function(this)
val from_bitField0_ = bitField0_
var to_bitField0_ = 0
if ((from_bitField0_ and 1) != 0) to_bitField0_ = to_bitField0_ or 1
result.field_9_ = field_9_
if ((from_bitField0_ and 2) != 0) to_bitField0_ = to_bitField0_ or 2
result.field_1_ = field_1_
if ((from_bitField0_ and 4) != 0) to_bitField0_ = to_bitField0_ or 4
result.field_2_ = field_2_
if ((from_bitField0_ and 8) != 0) to_bitField0_ = to_bitField0_ or 8
result.field_3_ = field_3_
if ((from_bitField0_ and 16) != 0) to_bitField0_ = to_bitField0_ or 16
result.field_7_ = field_7_
if ((from_bitField0_ and 32) != 0) to_bitField0_ = to_bitField0_ or 32
result.field_5_ = field_5_
if ((from_bitField0_ and 64) != 0) to_bitField0_ = to_bitField0_ or 64
result.field_8_ = field_8_
if ((from_bitField0_ and 128) != 0) to_bitField0_ = to_bitField0_ or 128
result.field_14_ = field_14_
if ((from_bitField0_ and 256) != 0) to_bitField0_ = to_bitField0_ or 256
result.field_15_ = field_15_
if ((from_bitField0_ and 512) != 0) to_bitField0_ = to_bitField0_ or 512
result.field_30_ = field_30_
if ((from_bitField0_ and 1024) != 0) to_bitField0_ = to_bitField0_ or 1024
result.field_32_ = field_32_
result.bitField0_ = to_bitField0_
if ((mutBitField0_ and 1) != 0) { field_4_ = GeneratedMessageLite.unmodifiableProtoList(field_4_); mutBitField0_ = mutBitField0_ and -2 }
result.field_4_ = field_4_
if ((mutBitField0_ and 2) != 0) { field_10_ = GeneratedMessageLite.unmodifiableProtoList(field_10_); mutBitField0_ = mutBitField0_ and -3 }
result.field_10_ = field_10_
if ((mutBitField0_ and 4) != 0) { field_11_ = GeneratedMessageLite.unmodifiableProtoList(field_11_); mutBitField0_ = mutBitField0_ and -5 }
result.field_11_ = field_11_
if ((mutBitField0_ and 8) != 0) { field_13_ = GeneratedMessageLite.unmodifiableProtoList(field_13_); mutBitField0_ = mutBitField0_ and -9 }
result.field_13_ = field_13_
if ((mutBitField0_ and 16) != 0) { field_6_ = GeneratedMessageLite.unmodifiableProtoList(field_6_); mutBitField0_ = mutBitField0_ and -17 }
result.field_6_ = field_6_
if ((mutBitField0_ and 32) != 0) { field_31_ = GeneratedMessageLite.unmodifiableProtoList(field_31_); mutBitField0_ = mutBitField0_ and -33 }
result.field_31_ = field_31_
if ((mutBitField0_ and 64) != 0) { field_33_ = GeneratedMessageLite.unmodifiableProtoList(field_33_); mutBitField0_ = mutBitField0_ and -65 }
result.field_33_ = field_33_
if ((mutBitField0_ and 128) != 0) { field_12_ = GeneratedMessageLite.unmodifiableProtoList(field_12_); mutBitField0_ = mutBitField0_ and -129 }
result.field_12_ = field_12_
if ((mutBitField0_ and 256) != 0) { field_34_ = GeneratedMessageLite.unmodifiableProtoList(field_34_); mutBitField0_ = mutBitField0_ and -257 }
result.field_34_ = field_34_
return result
}
override fun mergeFrom(other: Function): Builder {
if (other === defaultInstance) return this
if (other.hasFlags()) setFlags(other.getFlags())
if (other.hasOldFlags()) setOldFlags(other.getOldFlags())
if (other.hasName()) setName(other.getName())
if (other.hasReturnType()) mergeReturnType(other.getReturnType())
if (other.hasReturnTypeId()) setReturnTypeId(other.getReturnTypeId())
if (other.hasReceiverType()) mergeReceiverType(other.getReceiverType())
if (other.hasReceiverTypeId()) setReceiverTypeId(other.getReceiverTypeId())
if (other.hasCompanionExtensionReceiverType()) mergeCompanionExtensionReceiverType(other.getCompanionExtensionReceiverType())
if (other.hasCompanionExtensionReceiverTypeId()) setCompanionExtensionReceiverTypeId(other.getCompanionExtensionReceiverTypeId())
if (other.hasTypeTable()) mergeTypeTable(other.getTypeTable())
if (other.hasContract()) mergeContract(other.getContract())
if (other.field_4_.isNotEmpty()) {
if (field_4_.isEmpty()) { field_4_ = other.field_4_; mutBitField0_ = mutBitField0_ and -2 } else { ensure_4_Mutable(); (field_4_ as MutableList<Any>).addAll(other.field_4_ as Collection<Any>) }
}
if (other.field_10_.isNotEmpty()) {
if (field_10_.isEmpty()) { field_10_ = other.field_10_; mutBitField0_ = mutBitField0_ and -3 } else { ensure_10_Mutable(); (field_10_ as MutableList<Any>).addAll(other.field_10_ as Collection<Any>) }
}
if (other.field_11_.isNotEmpty()) {
if (field_11_.isEmpty()) { field_11_ = other.field_11_; mutBitField0_ = mutBitField0_ and -5 } else { ensure_11_Mutable(); (field_11_ as MutableList<Any>).addAll(other.field_11_ as Collection<Any>) }
}
if (other.field_13_.isNotEmpty()) {
if (field_13_.isEmpty()) { field_13_ = other.field_13_; mutBitField0_ = mutBitField0_ and -9 } else { ensure_13_Mutable(); (field_13_ as MutableList<Any>).addAll(other.field_13_ as Collection<Any>) }
}
if (other.field_6_.isNotEmpty()) {
if (field_6_.isEmpty()) { field_6_ = other.field_6_; mutBitField0_ = mutBitField0_ and -17 } else { ensure_6_Mutable(); (field_6_ as MutableList<Any>).addAll(other.field_6_ as Collection<Any>) }
}
if (other.field_31_.isNotEmpty()) {
if (field_31_.isEmpty()) { field_31_ = other.field_31_; mutBitField0_ = mutBitField0_ and -33 } else { ensure_31_Mutable(); (field_31_ as MutableList<Any>).addAll(other.field_31_ as Collection<Any>) }
}
if (other.field_33_.isNotEmpty()) {
if (field_33_.isEmpty()) { field_33_ = other.field_33_; mutBitField0_ = mutBitField0_ and -65 } else { ensure_33_Mutable(); (field_33_ as MutableList<Any>).addAll(other.field_33_ as Collection<Any>) }
}
if (other.field_12_.isNotEmpty()) {
if (field_12_.isEmpty()) { field_12_ = other.field_12_; mutBitField0_ = mutBitField0_ and -129 } else { ensure_12_Mutable(); (field_12_ as MutableList<Any>).addAll(other.field_12_ as Collection<Any>) }
}
if (other.field_34_.isNotEmpty()) {
if (field_34_.isEmpty()) { field_34_ = other.field_34_; mutBitField0_ = mutBitField0_ and -257 } else { ensure_34_Mutable(); (field_34_ as MutableList<Any>).addAll(other.field_34_ as Collection<Any>) }
}
mergeExtensionFields(other)
unknownFields = unknownFields.concat(other.unknownFields)
return this
}
override fun isInitialized(): Boolean {
if ((bitField0_ and 4) == 0) return false
if ((bitField0_ and 8) != 0 && !getReturnType().isInitialized()) return false
if ((bitField0_ and 32) != 0 && !getReceiverType().isInitialized()) return false
if ((bitField0_ and 128) != 0 && !getCompanionExtensionReceiverType().isInitialized()) return false
if ((bitField0_ and 512) != 0 && !getTypeTable().isInitialized()) return false
if ((bitField0_ and 1024) != 0 && !getContract().isInitialized()) return false
for (i in 0 until field_4_.size) { if (!field_4_[i].isInitialized()) return false }
for (i in 0 until field_10_.size) { if (!field_10_[i].isInitialized()) return false }
for (i in 0 until field_13_.size) { if (!field_13_[i].isInitialized()) return false }
for (i in 0 until field_6_.size) { if (!field_6_[i].isInitialized()) return false }
for (i in 0 until field_33_.size) { if (!field_33_[i].isInitialized()) return false }
for (i in 0 until field_12_.size) { if (!field_12_[i].isInitialized()) return false }
for (i in 0 until field_34_.size) { if (!field_34_[i].isInitialized()) return false }
if (!extensionsAreInitialized()) return false
return true
}
override fun mergeFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Builder {
var parsedMessage: Function? = null
try {
parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry)
} catch (e: InvalidProtocolBufferException) {
parsedMessage = e.unfinishedMessage as? Function
throw e
} finally {
if (parsedMessage != null) mergeFrom(parsedMessage)
}
return this
}
var `oldFlags`: Int
get() = field_1_
set(value) { bitField0_ = bitField0_ or 2; field_1_ = value }
fun getOldFlags(): Int = field_1_
fun hasOldFlags(): Boolean = (bitField0_ and 2) != 0
fun setOldFlags(value: Int): Builder { bitField0_ = bitField0_ or 2; field_1_ = value; return this }
fun clearOldFlags(): Builder { bitField0_ = bitField0_ and -3; field_1_ = 6; return this }
var `name`: Int
get() = field_2_
set(value) { bitField0_ = bitField0_ or 4; field_2_ = value }
fun getName(): Int = field_2_
fun hasName(): Boolean = (bitField0_ and 4) != 0
fun setName(value: Int): Builder { bitField0_ = bitField0_ or 4; field_2_ = value; return this }
fun clearName(): Builder { bitField0_ = bitField0_ and -5; field_2_ = 0; return this }
var `returnType`: org.jetbrains.kotlin.metadata.ProtoBuf.Type
get() = field_3_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance
set(value) { field_3_ = value; bitField0_ = bitField0_ or 8 }
fun getReturnType(): org.jetbrains.kotlin.metadata.ProtoBuf.Type = `returnType`
fun hasReturnType(): Boolean = (bitField0_ and 8) != 0
fun setReturnType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder { field_3_ = value; bitField0_ = bitField0_ or 8; return this }
fun setReturnType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder): Builder { field_3_ = value.build(); bitField0_ = bitField0_ or 8; return this }
fun mergeReturnType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder {
val cur = field_3_
field_3_ = if ((bitField0_ and 8) != 0 && cur != null && cur !== org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance) org.jetbrains.kotlin.metadata.ProtoBuf.Type.newBuilder(cur).mergeFrom(value).buildPartial() else value
bitField0_ = bitField0_ or 8
return this
}
fun clearReturnType(): Builder { field_3_ = null; bitField0_ = bitField0_ and -9; return this }
val `typeParameterList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter> get() = if (field_4_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_4_)
val `typeParameterCount`: Int get() = field_4_.size
fun getTypeParameterList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter> = `typeParameterList`
fun getTypeParameterCount(): Int = field_4_.size
fun getTypeParameter(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter = field_4_[index]
fun addTypeParameter(value: org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter): Builder { ensure_4_Mutable(); (field_4_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter>).add(value); return this }
fun setTypeParameter(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter): Builder { ensure_4_Mutable(); (field_4_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter>)[index] = value; return this }
fun addAllTypeParameter(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter>): Builder { ensure_4_Mutable(); AbstractMessageLite.Builder.addAll(values, field_4_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter>); return this }
fun clearTypeParameter(): Builder { field_4_ = emptyList(); mutBitField0_ = mutBitField0_ and -2; return this }
fun addTypeParameter(value: org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter.Builder): Builder = addTypeParameter(value.build())
fun setTypeParameter(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter.Builder): Builder = setTypeParameter(index, value.build())
var `receiverType`: org.jetbrains.kotlin.metadata.ProtoBuf.Type
get() = field_5_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance
set(value) { field_5_ = value; bitField0_ = bitField0_ or 32 }
fun getReceiverType(): org.jetbrains.kotlin.metadata.ProtoBuf.Type = `receiverType`
fun hasReceiverType(): Boolean = (bitField0_ and 32) != 0
fun setReceiverType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder { field_5_ = value; bitField0_ = bitField0_ or 32; return this }
fun setReceiverType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder): Builder { field_5_ = value.build(); bitField0_ = bitField0_ or 32; return this }
fun mergeReceiverType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder {
val cur = field_5_
field_5_ = if ((bitField0_ and 32) != 0 && cur != null && cur !== org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance) org.jetbrains.kotlin.metadata.ProtoBuf.Type.newBuilder(cur).mergeFrom(value).buildPartial() else value
bitField0_ = bitField0_ or 32
return this
}
fun clearReceiverType(): Builder { field_5_ = null; bitField0_ = bitField0_ and -33; return this }
val `valueParameterList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter> get() = if (field_6_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_6_)
val `valueParameterCount`: Int get() = field_6_.size
fun getValueParameterList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter> = `valueParameterList`
fun getValueParameterCount(): Int = field_6_.size
fun getValueParameter(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter = field_6_[index]
fun addValueParameter(value: org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter): Builder { ensure_6_Mutable(); (field_6_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter>).add(value); return this }
fun setValueParameter(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter): Builder { ensure_6_Mutable(); (field_6_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter>)[index] = value; return this }
fun addAllValueParameter(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter>): Builder { ensure_6_Mutable(); AbstractMessageLite.Builder.addAll(values, field_6_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter>); return this }
fun clearValueParameter(): Builder { field_6_ = emptyList(); mutBitField0_ = mutBitField0_ and -17; return this }
fun addValueParameter(value: org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter.Builder): Builder = addValueParameter(value.build())
fun setValueParameter(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter.Builder): Builder = setValueParameter(index, value.build())
var `returnTypeId`: Int
get() = field_7_
set(value) { bitField0_ = bitField0_ or 16; field_7_ = value }
fun getReturnTypeId(): Int = field_7_
fun hasReturnTypeId(): Boolean = (bitField0_ and 16) != 0
fun setReturnTypeId(value: Int): Builder { bitField0_ = bitField0_ or 16; field_7_ = value; return this }
fun clearReturnTypeId(): Builder { bitField0_ = bitField0_ and -17; field_7_ = 0; return this }
var `receiverTypeId`: Int
get() = field_8_
set(value) { bitField0_ = bitField0_ or 64; field_8_ = value }
fun getReceiverTypeId(): Int = field_8_
fun hasReceiverTypeId(): Boolean = (bitField0_ and 64) != 0
fun setReceiverTypeId(value: Int): Builder { bitField0_ = bitField0_ or 64; field_8_ = value; return this }
fun clearReceiverTypeId(): Builder { bitField0_ = bitField0_ and -65; field_8_ = 0; return this }
var `flags`: Int
get() = field_9_
set(value) { bitField0_ = bitField0_ or 1; field_9_ = value }
fun getFlags(): Int = field_9_
fun hasFlags(): Boolean = (bitField0_ and 1) != 0
fun setFlags(value: Int): Builder { bitField0_ = bitField0_ or 1; field_9_ = value; return this }
fun clearFlags(): Builder { bitField0_ = bitField0_ and -2; field_9_ = 6; return this }
val `contextReceiverTypeList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> get() = if (field_10_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_10_)
val `contextReceiverTypeCount`: Int get() = field_10_.size
fun getContextReceiverTypeList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> = `contextReceiverTypeList`
fun getContextReceiverTypeCount(): Int = field_10_.size
fun getContextReceiverType(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Type = field_10_[index]
fun addContextReceiverType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder { ensure_10_Mutable(); (field_10_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Type>).add(value); return this }
fun setContextReceiverType(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder { ensure_10_Mutable(); (field_10_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Type>)[index] = value; return this }
fun addAllContextReceiverType(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.Type>): Builder { ensure_10_Mutable(); AbstractMessageLite.Builder.addAll(values, field_10_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Type>); return this }
fun clearContextReceiverType(): Builder { field_10_ = emptyList(); mutBitField0_ = mutBitField0_ and -3; return this }
fun addContextReceiverType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder): Builder = addContextReceiverType(value.build())
fun setContextReceiverType(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder): Builder = setContextReceiverType(index, value.build())
val `contextReceiverTypeIdList`: List<Int> get() = if (field_11_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_11_)
val `contextReceiverTypeIdCount`: Int get() = field_11_.size
fun getContextReceiverTypeIdList(): List<Int> = `contextReceiverTypeIdList`
fun getContextReceiverTypeIdCount(): Int = field_11_.size
fun getContextReceiverTypeId(index: Int): Int = field_11_[index]
fun addContextReceiverTypeId(value: Int): Builder { ensure_11_Mutable(); (field_11_ as MutableList<Int>).add(value); return this }
fun setContextReceiverTypeId(index: Int, value: Int): Builder { ensure_11_Mutable(); (field_11_ as MutableList<Int>)[index] = value; return this }
fun addAllContextReceiverTypeId(values: Iterable<Int>): Builder { ensure_11_Mutable(); AbstractMessageLite.Builder.addAll(values, field_11_ as MutableList<Int>); return this }
fun clearContextReceiverTypeId(): Builder { field_11_ = emptyList(); mutBitField0_ = mutBitField0_ and -5; return this }
val `annotationList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> get() = if (field_12_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_12_)
val `annotationCount`: Int get() = field_12_.size
fun getAnnotationList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = `annotationList`
fun getAnnotationCount(): Int = field_12_.size
fun getAnnotation(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation = field_12_[index]
fun addAnnotation(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation): Builder { ensure_12_Mutable(); (field_12_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>).add(value); return this }
fun setAnnotation(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation): Builder { ensure_12_Mutable(); (field_12_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>)[index] = value; return this }
fun addAllAnnotation(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>): Builder { ensure_12_Mutable(); AbstractMessageLite.Builder.addAll(values, field_12_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>); return this }
fun clearAnnotation(): Builder { field_12_ = emptyList(); mutBitField0_ = mutBitField0_ and -129; return this }
fun addAnnotation(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Builder): Builder = addAnnotation(value.build())
fun setAnnotation(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Builder): Builder = setAnnotation(index, value.build())
val `contextParameterList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter> get() = if (field_13_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_13_)
val `contextParameterCount`: Int get() = field_13_.size
fun getContextParameterList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter> = `contextParameterList`
fun getContextParameterCount(): Int = field_13_.size
fun getContextParameter(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter = field_13_[index]
fun addContextParameter(value: org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter): Builder { ensure_13_Mutable(); (field_13_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter>).add(value); return this }
fun setContextParameter(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter): Builder { ensure_13_Mutable(); (field_13_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter>)[index] = value; return this }
fun addAllContextParameter(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter>): Builder { ensure_13_Mutable(); AbstractMessageLite.Builder.addAll(values, field_13_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter>); return this }
fun clearContextParameter(): Builder { field_13_ = emptyList(); mutBitField0_ = mutBitField0_ and -9; return this }
fun addContextParameter(value: org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter.Builder): Builder = addContextParameter(value.build())
fun setContextParameter(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter.Builder): Builder = setContextParameter(index, value.build())
var `companionExtensionReceiverType`: org.jetbrains.kotlin.metadata.ProtoBuf.Type
get() = field_14_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance
set(value) { field_14_ = value; bitField0_ = bitField0_ or 128 }
fun getCompanionExtensionReceiverType(): org.jetbrains.kotlin.metadata.ProtoBuf.Type = `companionExtensionReceiverType`
fun hasCompanionExtensionReceiverType(): Boolean = (bitField0_ and 128) != 0
fun setCompanionExtensionReceiverType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder { field_14_ = value; bitField0_ = bitField0_ or 128; return this }
fun setCompanionExtensionReceiverType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder): Builder { field_14_ = value.build(); bitField0_ = bitField0_ or 128; return this }
fun mergeCompanionExtensionReceiverType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder {
val cur = field_14_
field_14_ = if ((bitField0_ and 128) != 0 && cur != null && cur !== org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance) org.jetbrains.kotlin.metadata.ProtoBuf.Type.newBuilder(cur).mergeFrom(value).buildPartial() else value
bitField0_ = bitField0_ or 128
return this
}
fun clearCompanionExtensionReceiverType(): Builder { field_14_ = null; bitField0_ = bitField0_ and -129; return this }
var `companionExtensionReceiverTypeId`: Int
get() = field_15_
set(value) { bitField0_ = bitField0_ or 256; field_15_ = value }
fun getCompanionExtensionReceiverTypeId(): Int = field_15_
fun hasCompanionExtensionReceiverTypeId(): Boolean = (bitField0_ and 256) != 0
fun setCompanionExtensionReceiverTypeId(value: Int): Builder { bitField0_ = bitField0_ or 256; field_15_ = value; return this }
fun clearCompanionExtensionReceiverTypeId(): Builder { bitField0_ = bitField0_ and -257; field_15_ = 0; return this }
var `typeTable`: org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable
get() = field_30_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable.defaultInstance
set(value) { field_30_ = value; bitField0_ = bitField0_ or 512 }
fun getTypeTable(): org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable = `typeTable`
fun hasTypeTable(): Boolean = (bitField0_ and 512) != 0
fun setTypeTable(value: org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable): Builder { field_30_ = value; bitField0_ = bitField0_ or 512; return this }
fun setTypeTable(value: org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable.Builder): Builder { field_30_ = value.build(); bitField0_ = bitField0_ or 512; return this }
fun mergeTypeTable(value: org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable): Builder {
val cur = field_30_
field_30_ = if ((bitField0_ and 512) != 0 && cur != null && cur !== org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable.defaultInstance) org.jetbrains.kotlin.metadata.ProtoBuf.TypeTable.newBuilder(cur).mergeFrom(value).buildPartial() else value
bitField0_ = bitField0_ or 512
return this
}
fun clearTypeTable(): Builder { field_30_ = null; bitField0_ = bitField0_ and -513; return this }
val `versionRequirementList`: List<Int> get() = if (field_31_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_31_)
val `versionRequirementCount`: Int get() = field_31_.size
fun getVersionRequirementList(): List<Int> = `versionRequirementList`
fun getVersionRequirementCount(): Int = field_31_.size
fun getVersionRequirement(index: Int): Int = field_31_[index]
fun addVersionRequirement(value: Int): Builder { ensure_31_Mutable(); (field_31_ as MutableList<Int>).add(value); return this }
fun setVersionRequirement(index: Int, value: Int): Builder { ensure_31_Mutable(); (field_31_ as MutableList<Int>)[index] = value; return this }
fun addAllVersionRequirement(values: Iterable<Int>): Builder { ensure_31_Mutable(); AbstractMessageLite.Builder.addAll(values, field_31_ as MutableList<Int>); return this }
fun clearVersionRequirement(): Builder { field_31_ = emptyList(); mutBitField0_ = mutBitField0_ and -33; return this }
var `contract`: org.jetbrains.kotlin.metadata.ProtoBuf.Contract
get() = field_32_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Contract.defaultInstance
set(value) { field_32_ = value; bitField0_ = bitField0_ or 1024 }
fun getContract(): org.jetbrains.kotlin.metadata.ProtoBuf.Contract = `contract`
fun hasContract(): Boolean = (bitField0_ and 1024) != 0
fun setContract(value: org.jetbrains.kotlin.metadata.ProtoBuf.Contract): Builder { field_32_ = value; bitField0_ = bitField0_ or 1024; return this }
fun setContract(value: org.jetbrains.kotlin.metadata.ProtoBuf.Contract.Builder): Builder { field_32_ = value.build(); bitField0_ = bitField0_ or 1024; return this }
fun mergeContract(value: org.jetbrains.kotlin.metadata.ProtoBuf.Contract): Builder {
val cur = field_32_
field_32_ = if ((bitField0_ and 1024) != 0 && cur != null && cur !== org.jetbrains.kotlin.metadata.ProtoBuf.Contract.defaultInstance) org.jetbrains.kotlin.metadata.ProtoBuf.Contract.newBuilder(cur).mergeFrom(value).buildPartial() else value
bitField0_ = bitField0_ or 1024
return this
}
fun clearContract(): Builder { field_32_ = null; bitField0_ = bitField0_ and -1025; return this }
val `compilerPluginDataList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData> get() = if (field_33_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_33_)
val `compilerPluginDataCount`: Int get() = field_33_.size
fun getCompilerPluginDataList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData> = `compilerPluginDataList`
fun getCompilerPluginDataCount(): Int = field_33_.size
fun getCompilerPluginData(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData = field_33_[index]
fun addCompilerPluginData(value: org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData): Builder { ensure_33_Mutable(); (field_33_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData>).add(value); return this }
fun setCompilerPluginData(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData): Builder { ensure_33_Mutable(); (field_33_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData>)[index] = value; return this }
fun addAllCompilerPluginData(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData>): Builder { ensure_33_Mutable(); AbstractMessageLite.Builder.addAll(values, field_33_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData>); return this }
fun clearCompilerPluginData(): Builder { field_33_ = emptyList(); mutBitField0_ = mutBitField0_ and -65; return this }
fun addCompilerPluginData(value: org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData.Builder): Builder = addCompilerPluginData(value.build())
fun setCompilerPluginData(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData.Builder): Builder = setCompilerPluginData(index, value.build())
val `extensionReceiverAnnotationList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> get() = if (field_34_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_34_)
val `extensionReceiverAnnotationCount`: Int get() = field_34_.size
fun getExtensionReceiverAnnotationList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = `extensionReceiverAnnotationList`
fun getExtensionReceiverAnnotationCount(): Int = field_34_.size
fun getExtensionReceiverAnnotation(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation = field_34_[index]
fun addExtensionReceiverAnnotation(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation): Builder { ensure_34_Mutable(); (field_34_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>).add(value); return this }
fun setExtensionReceiverAnnotation(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation): Builder { ensure_34_Mutable(); (field_34_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>)[index] = value; return this }
fun addAllExtensionReceiverAnnotation(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>): Builder { ensure_34_Mutable(); AbstractMessageLite.Builder.addAll(values, field_34_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>); return this }
fun clearExtensionReceiverAnnotation(): Builder { field_34_ = emptyList(); mutBitField0_ = mutBitField0_ and -257; return this }
fun addExtensionReceiverAnnotation(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Builder): Builder = addExtensionReceiverAnnotation(value.build())
fun setExtensionReceiverAnnotation(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Builder): Builder = setExtensionReceiverAnnotation(index, value.build())
}
companion object {
val defaultInstance: Function = Function()
fun getDefaultInstance(): Function = defaultInstance
fun newBuilder(): Builder = Builder()
fun newBuilder(prototype: Function): Builder = Builder().mergeFrom(prototype)
val PARSER: Parser<Function> = object : AbstractParser<Function>() {
override fun parsePartialFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Function =
Function(input, extensionRegistry)
}
fun parseFrom(bytes: ByteArray): Function = PARSER.parseFrom(bytes)
fun parseFrom(bytes: ByteArray, registry: ExtensionRegistryLite): Function = PARSER.parseFrom(bytes, registry)
fun parseFrom(bytes: ByteString): Function { val input = bytes.newCodedInput(); val msg = PARSER.parsePartialFrom(input, ExtensionRegistryLite.getEmptyRegistry()); input.checkLastTagWas(0); if (!msg.isInitialized()) throw UninitializedMessageException().asInvalidProtocolBufferException().setUnfinishedMessage(msg); return msg }
fun parseFrom(input: CodedInputStream): Function = PARSER.parseFrom(input)
fun parseFrom(input: CodedInputStream, registry: ExtensionRegistryLite): Function = PARSER.parseFrom(input, registry)
const val OLD_FLAGS_FIELD_NUMBER = 1
const val NAME_FIELD_NUMBER = 2
const val RETURN_TYPE_FIELD_NUMBER = 3
const val TYPE_PARAMETER_FIELD_NUMBER = 4
const val RECEIVER_TYPE_FIELD_NUMBER = 5
const val VALUE_PARAMETER_FIELD_NUMBER = 6
const val RETURN_TYPE_ID_FIELD_NUMBER = 7
const val RECEIVER_TYPE_ID_FIELD_NUMBER = 8
const val FLAGS_FIELD_NUMBER = 9
const val CONTEXT_RECEIVER_TYPE_FIELD_NUMBER = 10
const val CONTEXT_RECEIVER_TYPE_ID_FIELD_NUMBER = 11
const val ANNOTATION_FIELD_NUMBER = 12
const val CONTEXT_PARAMETER_FIELD_NUMBER = 13
const val COMPANION_EXTENSION_RECEIVER_TYPE_FIELD_NUMBER = 14
const val COMPANION_EXTENSION_RECEIVER_TYPE_ID_FIELD_NUMBER = 15
const val TYPE_TABLE_FIELD_NUMBER = 30
const val VERSION_REQUIREMENT_FIELD_NUMBER = 31
const val CONTRACT_FIELD_NUMBER = 32
const val COMPILER_PLUGIN_DATA_FIELD_NUMBER = 33
const val EXTENSION_RECEIVER_ANNOTATION_FIELD_NUMBER = 34
}}
class Property : GeneratedMessageLite.ExtendableMessage<Property> {
internal var bitField0_: Int = 0
internal var field_11_: Int = 518
internal var field_1_: Int = 2054
internal var field_2_: Int = 0
internal var field_3_: org.jetbrains.kotlin.metadata.ProtoBuf.Type? = null
internal var field_9_: Int = 0
internal var field_5_: org.jetbrains.kotlin.metadata.ProtoBuf.Type? = null
internal var field_10_: Int = 0
internal var field_18_: org.jetbrains.kotlin.metadata.ProtoBuf.Type? = null
internal var field_19_: Int = 0
internal var field_6_: org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter? = null
internal var field_7_: Int = 0
internal var field_8_: Int = 0
internal var field_40_: org.jetbrains.kotlin.metadata.ProtoBuf.Contract? = null
internal var field_41_: org.jetbrains.kotlin.metadata.ProtoBuf.Contract? = null
internal var field_4_: List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter> = emptyList()
internal var field_12_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> = emptyList()
internal var field_13_: List<Int> = emptyList()
private var packedSize_13_: Int = -1
internal var field_17_: List<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter> = emptyList()
internal var field_31_: List<Int> = emptyList()
internal var field_32_: List<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData> = emptyList()
internal var field_14_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = emptyList()
internal var field_15_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = emptyList()
internal var field_16_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = emptyList()
internal var field_33_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = emptyList()
internal var field_34_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = emptyList()
internal var field_35_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = emptyList()
private var memoizedIsInitialized: Byte = -1
private var memoizedSerializedSize: Int = -1
private constructor()
private constructor(builder: Builder) {
this.unknownFields = builder.unknownFields
copyExtensionsFromBuilder(builder)
}
private constructor(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite) {
var mutBit0_ = 0
var unknownBuffer: ByteString.Output? = null
var unknownOut: CodedOutputStream? = null
fun unknownOutput(): CodedOutputStream {
var out = unknownOut
if (out == null) { val buf = ByteString.newOutput(); unknownBuffer = buf; out = CodedOutputStream.newInstance(buf); unknownOut = out }
return out
}
try {
var done = false
while (!done) {
val tag = input.readTag()
when (tag) {
0 -> done = true
8 -> { bitField0_ = bitField0_ or 2; field_1_ = input.readInt32() }
16 -> { bitField0_ = bitField0_ or 4; field_2_ = input.readInt32() }
26 -> {
var subBuilder: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder? = null
if ((bitField0_ and 8) != 0) subBuilder = field_3_?.toBuilder()
val parsed = input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Type.PARSER, extensionRegistry)
field_3_ = if (subBuilder != null) subBuilder.mergeFrom(parsed).buildPartial() else parsed
bitField0_ = bitField0_ or 8
}
34 -> {
if ((mutBit0_ and 1) == 0) { field_4_ = ArrayList(); mutBit0_ = mutBit0_ or 1 }
(field_4_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter.PARSER, extensionRegistry))
}
42 -> {
var subBuilder: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder? = null
if ((bitField0_ and 32) != 0) subBuilder = field_5_?.toBuilder()
val parsed = input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Type.PARSER, extensionRegistry)
field_5_ = if (subBuilder != null) subBuilder.mergeFrom(parsed).buildPartial() else parsed
bitField0_ = bitField0_ or 32
}
50 -> {
var subBuilder: org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter.Builder? = null
if ((bitField0_ and 512) != 0) subBuilder = field_6_?.toBuilder()
val parsed = input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter.PARSER, extensionRegistry)
field_6_ = if (subBuilder != null) subBuilder.mergeFrom(parsed).buildPartial() else parsed
bitField0_ = bitField0_ or 512
}
56 -> { bitField0_ = bitField0_ or 1024; field_7_ = input.readInt32() }
64 -> { bitField0_ = bitField0_ or 2048; field_8_ = input.readInt32() }
72 -> { bitField0_ = bitField0_ or 16; field_9_ = input.readInt32() }
80 -> { bitField0_ = bitField0_ or 64; field_10_ = input.readInt32() }
88 -> { bitField0_ = bitField0_ or 1; field_11_ = input.readInt32() }
98 -> {
if ((mutBit0_ and 2) == 0) { field_12_ = ArrayList(); mutBit0_ = mutBit0_ or 2 }
(field_12_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Type>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Type.PARSER, extensionRegistry))
}
104 -> {
if ((mutBit0_ and 4) == 0) { field_13_ = ArrayList(); mutBit0_ = mutBit0_ or 4 }
(field_13_ as MutableList<Int>).add(input.readInt32())
}
106 -> {
val length = input.readRawVarint32()
val oldLimit = input.pushLimit(length)
try {
if (length > 0 && (mutBit0_ and 4) == 0) { field_13_ = ArrayList(); mutBit0_ = mutBit0_ or 4 }
while (input.getBytesUntilLimit() > 0) { (field_13_ as MutableList<Int>).add(input.readInt32()) }
} finally { input.popLimit(oldLimit) }
}
114 -> {
if ((mutBit0_ and 64) == 0) { field_14_ = ArrayList(); mutBit0_ = mutBit0_ or 64 }
(field_14_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.PARSER, extensionRegistry))
}
122 -> {
if ((mutBit0_ and 128) == 0) { field_15_ = ArrayList(); mutBit0_ = mutBit0_ or 128 }
(field_15_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.PARSER, extensionRegistry))
}
130 -> {
if ((mutBit0_ and 256) == 0) { field_16_ = ArrayList(); mutBit0_ = mutBit0_ or 256 }
(field_16_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.PARSER, extensionRegistry))
}
138 -> {
if ((mutBit0_ and 8) == 0) { field_17_ = ArrayList(); mutBit0_ = mutBit0_ or 8 }
(field_17_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter.PARSER, extensionRegistry))
}
146 -> {
var subBuilder: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder? = null
if ((bitField0_ and 128) != 0) subBuilder = field_18_?.toBuilder()
val parsed = input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Type.PARSER, extensionRegistry)
field_18_ = if (subBuilder != null) subBuilder.mergeFrom(parsed).buildPartial() else parsed
bitField0_ = bitField0_ or 128
}
152 -> { bitField0_ = bitField0_ or 256; field_19_ = input.readInt32() }
248 -> {
if ((mutBit0_ and 16) == 0) { field_31_ = ArrayList(); mutBit0_ = mutBit0_ or 16 }
(field_31_ as MutableList<Int>).add(input.readInt32())
}
250 -> {
val length = input.readRawVarint32()
val oldLimit = input.pushLimit(length)
try {
if (length > 0 && (mutBit0_ and 16) == 0) { field_31_ = ArrayList(); mutBit0_ = mutBit0_ or 16 }
while (input.getBytesUntilLimit() > 0) { (field_31_ as MutableList<Int>).add(input.readInt32()) }
} finally { input.popLimit(oldLimit) }
}
258 -> {
if ((mutBit0_ and 32) == 0) { field_32_ = ArrayList(); mutBit0_ = mutBit0_ or 32 }
(field_32_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData.PARSER, extensionRegistry))
}
266 -> {
if ((mutBit0_ and 512) == 0) { field_33_ = ArrayList(); mutBit0_ = mutBit0_ or 512 }
(field_33_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.PARSER, extensionRegistry))
}
274 -> {
if ((mutBit0_ and 1024) == 0) { field_34_ = ArrayList(); mutBit0_ = mutBit0_ or 1024 }
(field_34_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.PARSER, extensionRegistry))
}
282 -> {
if ((mutBit0_ and 2048) == 0) { field_35_ = ArrayList(); mutBit0_ = mutBit0_ or 2048 }
(field_35_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.PARSER, extensionRegistry))
}
322 -> {
var subBuilder: org.jetbrains.kotlin.metadata.ProtoBuf.Contract.Builder? = null
if ((bitField0_ and 4096) != 0) subBuilder = field_40_?.toBuilder()
val parsed = input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Contract.PARSER, extensionRegistry)
field_40_ = if (subBuilder != null) subBuilder.mergeFrom(parsed).buildPartial() else parsed
bitField0_ = bitField0_ or 4096
}
330 -> {
var subBuilder: org.jetbrains.kotlin.metadata.ProtoBuf.Contract.Builder? = null
if ((bitField0_ and 8192) != 0) subBuilder = field_41_?.toBuilder()
val parsed = input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Contract.PARSER, extensionRegistry)
field_41_ = if (subBuilder != null) subBuilder.mergeFrom(parsed).buildPartial() else parsed
bitField0_ = bitField0_ or 8192
}
else -> if (!parseUnknownField(input, unknownOutput(), extensionRegistry, tag)) done = true
}}
} catch (e: InvalidProtocolBufferException) {
throw e.setUnfinishedMessage(this)
} finally {
if ((mutBit0_ and 1) != 0) field_4_ = GeneratedMessageLite.unmodifiableProtoList(field_4_)
if ((mutBit0_ and 2) != 0) field_12_ = GeneratedMessageLite.unmodifiableProtoList(field_12_)
if ((mutBit0_ and 4) != 0) field_13_ = GeneratedMessageLite.unmodifiableProtoList(field_13_)
if ((mutBit0_ and 8) != 0) field_17_ = GeneratedMessageLite.unmodifiableProtoList(field_17_)
if ((mutBit0_ and 16) != 0) field_31_ = GeneratedMessageLite.unmodifiableProtoList(field_31_)
if ((mutBit0_ and 32) != 0) field_32_ = GeneratedMessageLite.unmodifiableProtoList(field_32_)
if ((mutBit0_ and 64) != 0) field_14_ = GeneratedMessageLite.unmodifiableProtoList(field_14_)
if ((mutBit0_ and 128) != 0) field_15_ = GeneratedMessageLite.unmodifiableProtoList(field_15_)
if ((mutBit0_ and 256) != 0) field_16_ = GeneratedMessageLite.unmodifiableProtoList(field_16_)
if ((mutBit0_ and 512) != 0) field_33_ = GeneratedMessageLite.unmodifiableProtoList(field_33_)
if ((mutBit0_ and 1024) != 0) field_34_ = GeneratedMessageLite.unmodifiableProtoList(field_34_)
if ((mutBit0_ and 2048) != 0) field_35_ = GeneratedMessageLite.unmodifiableProtoList(field_35_)
if (unknownBuffer != null) { unknownOut!!.flush(); unknownFields = unknownBuffer.toByteString() }
makeExtensionsImmutable()
}}
val `oldFlags`: Int get() = field_1_
fun getOldFlags(): Int = `oldFlags`
fun hasOldFlags(): Boolean = (bitField0_ and 2) != 0
val `name`: Int get() = field_2_
fun getName(): Int = `name`
fun hasName(): Boolean = (bitField0_ and 4) != 0
val `returnType`: org.jetbrains.kotlin.metadata.ProtoBuf.Type get() = field_3_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance
fun getReturnType(): org.jetbrains.kotlin.metadata.ProtoBuf.Type = `returnType`
fun hasReturnType(): Boolean = (bitField0_ and 8) != 0
val `typeParameterList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter> get() = field_4_
val `typeParameterCount`: Int get() = field_4_.size
fun getTypeParameterList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter> = field_4_
fun getTypeParameterCount(): Int = field_4_.size
fun getTypeParameter(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter = field_4_[index]
val `receiverType`: org.jetbrains.kotlin.metadata.ProtoBuf.Type get() = field_5_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance
fun getReceiverType(): org.jetbrains.kotlin.metadata.ProtoBuf.Type = `receiverType`
fun hasReceiverType(): Boolean = (bitField0_ and 32) != 0
val `setterValueParameter`: org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter get() = field_6_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter.defaultInstance
fun getSetterValueParameter(): org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter = `setterValueParameter`
fun hasSetterValueParameter(): Boolean = (bitField0_ and 512) != 0
val `getterFlags`: Int get() = field_7_
fun getGetterFlags(): Int = `getterFlags`
fun hasGetterFlags(): Boolean = (bitField0_ and 1024) != 0
val `setterFlags`: Int get() = field_8_
fun getSetterFlags(): Int = `setterFlags`
fun hasSetterFlags(): Boolean = (bitField0_ and 2048) != 0
val `returnTypeId`: Int get() = field_9_
fun getReturnTypeId(): Int = `returnTypeId`
fun hasReturnTypeId(): Boolean = (bitField0_ and 16) != 0
val `receiverTypeId`: Int get() = field_10_
fun getReceiverTypeId(): Int = `receiverTypeId`
fun hasReceiverTypeId(): Boolean = (bitField0_ and 64) != 0
val `flags`: Int get() = field_11_
fun getFlags(): Int = `flags`
fun hasFlags(): Boolean = (bitField0_ and 1) != 0
val `contextReceiverTypeList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> get() = field_12_
val `contextReceiverTypeCount`: Int get() = field_12_.size
fun getContextReceiverTypeList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> = field_12_
fun getContextReceiverTypeCount(): Int = field_12_.size
fun getContextReceiverType(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Type = field_12_[index]
val `contextReceiverTypeIdList`: List<Int> get() = field_13_
val `contextReceiverTypeIdCount`: Int get() = field_13_.size
fun getContextReceiverTypeIdList(): List<Int> = field_13_
fun getContextReceiverTypeIdCount(): Int = field_13_.size
fun getContextReceiverTypeId(index: Int): Int = field_13_[index]
val `annotationList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> get() = field_14_
val `annotationCount`: Int get() = field_14_.size
fun getAnnotationList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = field_14_
fun getAnnotationCount(): Int = field_14_.size
fun getAnnotation(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation = field_14_[index]
val `getterAnnotationList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> get() = field_15_
val `getterAnnotationCount`: Int get() = field_15_.size
fun getGetterAnnotationList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = field_15_
fun getGetterAnnotationCount(): Int = field_15_.size
fun getGetterAnnotation(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation = field_15_[index]
val `setterAnnotationList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> get() = field_16_
val `setterAnnotationCount`: Int get() = field_16_.size
fun getSetterAnnotationList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = field_16_
fun getSetterAnnotationCount(): Int = field_16_.size
fun getSetterAnnotation(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation = field_16_[index]
val `contextParameterList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter> get() = field_17_
val `contextParameterCount`: Int get() = field_17_.size
fun getContextParameterList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter> = field_17_
fun getContextParameterCount(): Int = field_17_.size
fun getContextParameter(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter = field_17_[index]
val `companionExtensionReceiverType`: org.jetbrains.kotlin.metadata.ProtoBuf.Type get() = field_18_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance
fun getCompanionExtensionReceiverType(): org.jetbrains.kotlin.metadata.ProtoBuf.Type = `companionExtensionReceiverType`
fun hasCompanionExtensionReceiverType(): Boolean = (bitField0_ and 128) != 0
val `companionExtensionReceiverTypeId`: Int get() = field_19_
fun getCompanionExtensionReceiverTypeId(): Int = `companionExtensionReceiverTypeId`
fun hasCompanionExtensionReceiverTypeId(): Boolean = (bitField0_ and 256) != 0
val `versionRequirementList`: List<Int> get() = field_31_
val `versionRequirementCount`: Int get() = field_31_.size
fun getVersionRequirementList(): List<Int> = field_31_
fun getVersionRequirementCount(): Int = field_31_.size
fun getVersionRequirement(index: Int): Int = field_31_[index]
val `compilerPluginDataList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData> get() = field_32_
val `compilerPluginDataCount`: Int get() = field_32_.size
fun getCompilerPluginDataList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData> = field_32_
fun getCompilerPluginDataCount(): Int = field_32_.size
fun getCompilerPluginData(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData = field_32_[index]
val `extensionReceiverAnnotationList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> get() = field_33_
val `extensionReceiverAnnotationCount`: Int get() = field_33_.size
fun getExtensionReceiverAnnotationList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = field_33_
fun getExtensionReceiverAnnotationCount(): Int = field_33_.size
fun getExtensionReceiverAnnotation(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation = field_33_[index]
val `backingFieldAnnotationList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> get() = field_34_
val `backingFieldAnnotationCount`: Int get() = field_34_.size
fun getBackingFieldAnnotationList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = field_34_
fun getBackingFieldAnnotationCount(): Int = field_34_.size
fun getBackingFieldAnnotation(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation = field_34_[index]
val `delegateFieldAnnotationList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> get() = field_35_
val `delegateFieldAnnotationCount`: Int get() = field_35_.size
fun getDelegateFieldAnnotationList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = field_35_
fun getDelegateFieldAnnotationCount(): Int = field_35_.size
fun getDelegateFieldAnnotation(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation = field_35_[index]
val `getterContract`: org.jetbrains.kotlin.metadata.ProtoBuf.Contract get() = field_40_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Contract.defaultInstance
fun getGetterContract(): org.jetbrains.kotlin.metadata.ProtoBuf.Contract = `getterContract`
fun hasGetterContract(): Boolean = (bitField0_ and 4096) != 0
val `setterContract`: org.jetbrains.kotlin.metadata.ProtoBuf.Contract get() = field_41_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Contract.defaultInstance
fun getSetterContract(): org.jetbrains.kotlin.metadata.ProtoBuf.Contract = `setterContract`
fun hasSetterContract(): Boolean = (bitField0_ and 8192) != 0
override fun isInitialized(): Boolean {
val isInitialized = memoizedIsInitialized.toInt()
if (isInitialized == 1) return true
if (isInitialized == 0) return false
if ((bitField0_ and 4) == 0) { memoizedIsInitialized = 0; return false }
if ((bitField0_ and 8) != 0 && !getReturnType().isInitialized()) { memoizedIsInitialized = 0; return false }
if ((bitField0_ and 32) != 0 && !getReceiverType().isInitialized()) { memoizedIsInitialized = 0; return false }
if ((bitField0_ and 128) != 0 && !getCompanionExtensionReceiverType().isInitialized()) { memoizedIsInitialized = 0; return false }
if ((bitField0_ and 512) != 0 && !getSetterValueParameter().isInitialized()) { memoizedIsInitialized = 0; return false }
if ((bitField0_ and 4096) != 0 && !getGetterContract().isInitialized()) { memoizedIsInitialized = 0; return false }
if ((bitField0_ and 8192) != 0 && !getSetterContract().isInitialized()) { memoizedIsInitialized = 0; return false }
for (i in 0 until field_4_.size) { if (!field_4_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
for (i in 0 until field_12_.size) { if (!field_12_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
for (i in 0 until field_17_.size) { if (!field_17_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
for (i in 0 until field_32_.size) { if (!field_32_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
for (i in 0 until field_14_.size) { if (!field_14_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
for (i in 0 until field_15_.size) { if (!field_15_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
for (i in 0 until field_16_.size) { if (!field_16_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
for (i in 0 until field_33_.size) { if (!field_33_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
for (i in 0 until field_34_.size) { if (!field_34_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
for (i in 0 until field_35_.size) { if (!field_35_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
if (!extensionsAreInitialized()) { memoizedIsInitialized = 0; return false }
memoizedIsInitialized = 1
return true
}
override fun writeTo(output: CodedOutputStream) {
serializedSize
val extensionWriter = if (hasAnyExtensions()) newExtensionWriter() else null
extensionWriter?.writeUntil(1, output)
if ((bitField0_ and 2) != 0) { output.writeRawVarint32(8); output.writeInt32NoTag(field_1_) }
extensionWriter?.writeUntil(2, output)
if ((bitField0_ and 4) != 0) { output.writeRawVarint32(16); output.writeInt32NoTag(field_2_) }
extensionWriter?.writeUntil(3, output)
if ((bitField0_ and 8) != 0) { output.writeRawVarint32(26); output.writeMessageNoTag(getReturnType()) }
extensionWriter?.writeUntil(4, output)
for (i in 0 until field_4_.size) { output.writeRawVarint32(34); output.writeMessageNoTag(field_4_[i]) }
extensionWriter?.writeUntil(5, output)
if ((bitField0_ and 32) != 0) { output.writeRawVarint32(42); output.writeMessageNoTag(getReceiverType()) }
extensionWriter?.writeUntil(6, output)
if ((bitField0_ and 512) != 0) { output.writeRawVarint32(50); output.writeMessageNoTag(getSetterValueParameter()) }
extensionWriter?.writeUntil(7, output)
if ((bitField0_ and 1024) != 0) { output.writeRawVarint32(56); output.writeInt32NoTag(field_7_) }
extensionWriter?.writeUntil(8, output)
if ((bitField0_ and 2048) != 0) { output.writeRawVarint32(64); output.writeInt32NoTag(field_8_) }
extensionWriter?.writeUntil(9, output)
if ((bitField0_ and 16) != 0) { output.writeRawVarint32(72); output.writeInt32NoTag(field_9_) }
extensionWriter?.writeUntil(10, output)
if ((bitField0_ and 64) != 0) { output.writeRawVarint32(80); output.writeInt32NoTag(field_10_) }
extensionWriter?.writeUntil(11, output)
if ((bitField0_ and 1) != 0) { output.writeRawVarint32(88); output.writeInt32NoTag(field_11_) }
extensionWriter?.writeUntil(12, output)
for (i in 0 until field_12_.size) { output.writeRawVarint32(98); output.writeMessageNoTag(field_12_[i]) }
extensionWriter?.writeUntil(13, output)
if (field_13_.isNotEmpty()) {
output.writeRawVarint32(106)
output.writeRawVarint32(packedSize_13_)
for (i in 0 until field_13_.size) { output.writeInt32NoTag(field_13_[i]) }
}
extensionWriter?.writeUntil(14, output)
for (i in 0 until field_14_.size) { output.writeRawVarint32(114); output.writeMessageNoTag(field_14_[i]) }
extensionWriter?.writeUntil(15, output)
for (i in 0 until field_15_.size) { output.writeRawVarint32(122); output.writeMessageNoTag(field_15_[i]) }
extensionWriter?.writeUntil(16, output)
for (i in 0 until field_16_.size) { output.writeRawVarint32(130); output.writeMessageNoTag(field_16_[i]) }
extensionWriter?.writeUntil(17, output)
for (i in 0 until field_17_.size) { output.writeRawVarint32(138); output.writeMessageNoTag(field_17_[i]) }
extensionWriter?.writeUntil(18, output)
if ((bitField0_ and 128) != 0) { output.writeRawVarint32(146); output.writeMessageNoTag(getCompanionExtensionReceiverType()) }
extensionWriter?.writeUntil(19, output)
if ((bitField0_ and 256) != 0) { output.writeRawVarint32(152); output.writeInt32NoTag(field_19_) }
extensionWriter?.writeUntil(31, output)
for (i in 0 until field_31_.size) { output.writeRawVarint32(248); output.writeInt32NoTag(field_31_[i]) }
extensionWriter?.writeUntil(32, output)
for (i in 0 until field_32_.size) { output.writeRawVarint32(258); output.writeMessageNoTag(field_32_[i]) }
extensionWriter?.writeUntil(33, output)
for (i in 0 until field_33_.size) { output.writeRawVarint32(266); output.writeMessageNoTag(field_33_[i]) }
extensionWriter?.writeUntil(34, output)
for (i in 0 until field_34_.size) { output.writeRawVarint32(274); output.writeMessageNoTag(field_34_[i]) }
extensionWriter?.writeUntil(35, output)
for (i in 0 until field_35_.size) { output.writeRawVarint32(282); output.writeMessageNoTag(field_35_[i]) }
extensionWriter?.writeUntil(40, output)
if ((bitField0_ and 4096) != 0) { output.writeRawVarint32(322); output.writeMessageNoTag(getGetterContract()) }
extensionWriter?.writeUntil(41, output)
if ((bitField0_ and 8192) != 0) { output.writeRawVarint32(330); output.writeMessageNoTag(getSetterContract()) }
extensionWriter?.writeUntil(Int.MAX_VALUE, output)
if (!unknownFields.isEmpty()) unknownFields.writeTo(output)
}
override val serializedSize: Int get() {
val cached = memoizedSerializedSize
if (cached != -1) return cached
var size = 0
if ((bitField0_ and 1) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_11_)
if ((bitField0_ and 2) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_1_)
if ((bitField0_ and 4) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_2_)
if ((bitField0_ and 8) != 0) size += 1 + CodedOutputStream.computeMessageSizeNoTag(getReturnType())
if ((bitField0_ and 16) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_9_)
if ((bitField0_ and 32) != 0) size += 1 + CodedOutputStream.computeMessageSizeNoTag(getReceiverType())
if ((bitField0_ and 64) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_10_)
if ((bitField0_ and 128) != 0) size += 2 + CodedOutputStream.computeMessageSizeNoTag(getCompanionExtensionReceiverType())
if ((bitField0_ and 256) != 0) size += 2 + CodedOutputStream.computeInt32SizeNoTag(field_19_)
if ((bitField0_ and 512) != 0) size += 1 + CodedOutputStream.computeMessageSizeNoTag(getSetterValueParameter())
if ((bitField0_ and 1024) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_7_)
if ((bitField0_ and 2048) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_8_)
if ((bitField0_ and 4096) != 0) size += 2 + CodedOutputStream.computeMessageSizeNoTag(getGetterContract())
if ((bitField0_ and 8192) != 0) size += 2 + CodedOutputStream.computeMessageSizeNoTag(getSetterContract())
for (i in 0 until field_4_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_4_[i]) }
for (i in 0 until field_12_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_12_[i]) }
if (field_13_.isNotEmpty()) {
var dataSize = 0
for (i in 0 until field_13_.size) { dataSize += CodedOutputStream.computeInt32SizeNoTag(field_13_[i]) }
size += 1 + CodedOutputStream.computeRawVarint32Size(dataSize) + dataSize
packedSize_13_ = dataSize
} else { packedSize_13_ = 0 }
for (i in 0 until field_17_.size) { size += 2 + CodedOutputStream.computeMessageSizeNoTag(field_17_[i]) }
for (i in 0 until field_31_.size) { size += 2 + CodedOutputStream.computeInt32SizeNoTag(field_31_[i]) }
for (i in 0 until field_32_.size) { size += 2 + CodedOutputStream.computeMessageSizeNoTag(field_32_[i]) }
for (i in 0 until field_14_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_14_[i]) }
for (i in 0 until field_15_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_15_[i]) }
for (i in 0 until field_16_.size) { size += 2 + CodedOutputStream.computeMessageSizeNoTag(field_16_[i]) }
for (i in 0 until field_33_.size) { size += 2 + CodedOutputStream.computeMessageSizeNoTag(field_33_[i]) }
for (i in 0 until field_34_.size) { size += 2 + CodedOutputStream.computeMessageSizeNoTag(field_34_[i]) }
for (i in 0 until field_35_.size) { size += 2 + CodedOutputStream.computeMessageSizeNoTag(field_35_[i]) }
size += extensionsSerializedSize()
size += unknownFields.size()
memoizedSerializedSize = size
return size
}
override val defaultInstanceForType: Property get() = defaultInstance
override fun newBuilderForType(): Builder = Builder()
override fun toBuilder(): Builder = Builder().mergeFrom(this)
class Builder : GeneratedMessageLite.ExtendableBuilder<Property, Builder>() {
private var bitField0_: Int = 0
private var mutBitField0_: Int = 0
private var field_11_: Int = 518
private var field_1_: Int = 2054
private var field_2_: Int = 0
private var field_3_: org.jetbrains.kotlin.metadata.ProtoBuf.Type? = null
private var field_9_: Int = 0
private var field_5_: org.jetbrains.kotlin.metadata.ProtoBuf.Type? = null
private var field_10_: Int = 0
private var field_18_: org.jetbrains.kotlin.metadata.ProtoBuf.Type? = null
private var field_19_: Int = 0
private var field_6_: org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter? = null
private var field_7_: Int = 0
private var field_8_: Int = 0
private var field_40_: org.jetbrains.kotlin.metadata.ProtoBuf.Contract? = null
private var field_41_: org.jetbrains.kotlin.metadata.ProtoBuf.Contract? = null
private var field_4_: List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter> = emptyList()
private fun ensure_4_Mutable() { if ((mutBitField0_ and 1) == 0) { field_4_ = ArrayList(field_4_); mutBitField0_ = mutBitField0_ or 1 } }
private var field_12_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> = emptyList()
private fun ensure_12_Mutable() { if ((mutBitField0_ and 2) == 0) { field_12_ = ArrayList(field_12_); mutBitField0_ = mutBitField0_ or 2 } }
private var field_13_: List<Int> = emptyList()
private fun ensure_13_Mutable() { if ((mutBitField0_ and 4) == 0) { field_13_ = ArrayList(field_13_); mutBitField0_ = mutBitField0_ or 4 } }
private var field_17_: List<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter> = emptyList()
private fun ensure_17_Mutable() { if ((mutBitField0_ and 8) == 0) { field_17_ = ArrayList(field_17_); mutBitField0_ = mutBitField0_ or 8 } }
private var field_31_: List<Int> = emptyList()
private fun ensure_31_Mutable() { if ((mutBitField0_ and 16) == 0) { field_31_ = ArrayList(field_31_); mutBitField0_ = mutBitField0_ or 16 } }
private var field_32_: List<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData> = emptyList()
private fun ensure_32_Mutable() { if ((mutBitField0_ and 32) == 0) { field_32_ = ArrayList(field_32_); mutBitField0_ = mutBitField0_ or 32 } }
private var field_14_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = emptyList()
private fun ensure_14_Mutable() { if ((mutBitField0_ and 64) == 0) { field_14_ = ArrayList(field_14_); mutBitField0_ = mutBitField0_ or 64 } }
private var field_15_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = emptyList()
private fun ensure_15_Mutable() { if ((mutBitField0_ and 128) == 0) { field_15_ = ArrayList(field_15_); mutBitField0_ = mutBitField0_ or 128 } }
private var field_16_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = emptyList()
private fun ensure_16_Mutable() { if ((mutBitField0_ and 256) == 0) { field_16_ = ArrayList(field_16_); mutBitField0_ = mutBitField0_ or 256 } }
private var field_33_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = emptyList()
private fun ensure_33_Mutable() { if ((mutBitField0_ and 512) == 0) { field_33_ = ArrayList(field_33_); mutBitField0_ = mutBitField0_ or 512 } }
private var field_34_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = emptyList()
private fun ensure_34_Mutable() { if ((mutBitField0_ and 1024) == 0) { field_34_ = ArrayList(field_34_); mutBitField0_ = mutBitField0_ or 1024 } }
private var field_35_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = emptyList()
private fun ensure_35_Mutable() { if ((mutBitField0_ and 2048) == 0) { field_35_ = ArrayList(field_35_); mutBitField0_ = mutBitField0_ or 2048 } }
override val defaultInstanceForType: Property get() = defaultInstance
override fun clear(): Builder {
bitField0_ = 0
mutBitField0_ = 0
field_11_ = 518
field_1_ = 2054
field_2_ = 0
field_3_ = null
field_9_ = 0
field_5_ = null
field_10_ = 0
field_18_ = null
field_19_ = 0
field_6_ = null
field_7_ = 0
field_8_ = 0
field_40_ = null
field_41_ = null
field_4_ = emptyList()
field_12_ = emptyList()
field_13_ = emptyList()
field_17_ = emptyList()
field_31_ = emptyList()
field_32_ = emptyList()
field_14_ = emptyList()
field_15_ = emptyList()
field_16_ = emptyList()
field_33_ = emptyList()
field_34_ = emptyList()
field_35_ = emptyList()
clearExtensions()
unknownFields = ByteString.EMPTY
return this
}
override fun clone(): Builder = Builder().mergeFrom(buildPartial())
override fun build(): Property {
val result = buildPartial()
if (!result.isInitialized()) throw UninitializedMessageException()
return result
}
override fun buildPartial(): Property {
val result = Property(this)
val from_bitField0_ = bitField0_
var to_bitField0_ = 0
if ((from_bitField0_ and 1) != 0) to_bitField0_ = to_bitField0_ or 1
result.field_11_ = field_11_
if ((from_bitField0_ and 2) != 0) to_bitField0_ = to_bitField0_ or 2
result.field_1_ = field_1_
if ((from_bitField0_ and 4) != 0) to_bitField0_ = to_bitField0_ or 4
result.field_2_ = field_2_
if ((from_bitField0_ and 8) != 0) to_bitField0_ = to_bitField0_ or 8
result.field_3_ = field_3_
if ((from_bitField0_ and 16) != 0) to_bitField0_ = to_bitField0_ or 16
result.field_9_ = field_9_
if ((from_bitField0_ and 32) != 0) to_bitField0_ = to_bitField0_ or 32
result.field_5_ = field_5_
if ((from_bitField0_ and 64) != 0) to_bitField0_ = to_bitField0_ or 64
result.field_10_ = field_10_
if ((from_bitField0_ and 128) != 0) to_bitField0_ = to_bitField0_ or 128
result.field_18_ = field_18_
if ((from_bitField0_ and 256) != 0) to_bitField0_ = to_bitField0_ or 256
result.field_19_ = field_19_
if ((from_bitField0_ and 512) != 0) to_bitField0_ = to_bitField0_ or 512
result.field_6_ = field_6_
if ((from_bitField0_ and 1024) != 0) to_bitField0_ = to_bitField0_ or 1024
result.field_7_ = field_7_
if ((from_bitField0_ and 2048) != 0) to_bitField0_ = to_bitField0_ or 2048
result.field_8_ = field_8_
if ((from_bitField0_ and 4096) != 0) to_bitField0_ = to_bitField0_ or 4096
result.field_40_ = field_40_
if ((from_bitField0_ and 8192) != 0) to_bitField0_ = to_bitField0_ or 8192
result.field_41_ = field_41_
result.bitField0_ = to_bitField0_
if ((mutBitField0_ and 1) != 0) { field_4_ = GeneratedMessageLite.unmodifiableProtoList(field_4_); mutBitField0_ = mutBitField0_ and -2 }
result.field_4_ = field_4_
if ((mutBitField0_ and 2) != 0) { field_12_ = GeneratedMessageLite.unmodifiableProtoList(field_12_); mutBitField0_ = mutBitField0_ and -3 }
result.field_12_ = field_12_
if ((mutBitField0_ and 4) != 0) { field_13_ = GeneratedMessageLite.unmodifiableProtoList(field_13_); mutBitField0_ = mutBitField0_ and -5 }
result.field_13_ = field_13_
if ((mutBitField0_ and 8) != 0) { field_17_ = GeneratedMessageLite.unmodifiableProtoList(field_17_); mutBitField0_ = mutBitField0_ and -9 }
result.field_17_ = field_17_
if ((mutBitField0_ and 16) != 0) { field_31_ = GeneratedMessageLite.unmodifiableProtoList(field_31_); mutBitField0_ = mutBitField0_ and -17 }
result.field_31_ = field_31_
if ((mutBitField0_ and 32) != 0) { field_32_ = GeneratedMessageLite.unmodifiableProtoList(field_32_); mutBitField0_ = mutBitField0_ and -33 }
result.field_32_ = field_32_
if ((mutBitField0_ and 64) != 0) { field_14_ = GeneratedMessageLite.unmodifiableProtoList(field_14_); mutBitField0_ = mutBitField0_ and -65 }
result.field_14_ = field_14_
if ((mutBitField0_ and 128) != 0) { field_15_ = GeneratedMessageLite.unmodifiableProtoList(field_15_); mutBitField0_ = mutBitField0_ and -129 }
result.field_15_ = field_15_
if ((mutBitField0_ and 256) != 0) { field_16_ = GeneratedMessageLite.unmodifiableProtoList(field_16_); mutBitField0_ = mutBitField0_ and -257 }
result.field_16_ = field_16_
if ((mutBitField0_ and 512) != 0) { field_33_ = GeneratedMessageLite.unmodifiableProtoList(field_33_); mutBitField0_ = mutBitField0_ and -513 }
result.field_33_ = field_33_
if ((mutBitField0_ and 1024) != 0) { field_34_ = GeneratedMessageLite.unmodifiableProtoList(field_34_); mutBitField0_ = mutBitField0_ and -1025 }
result.field_34_ = field_34_
if ((mutBitField0_ and 2048) != 0) { field_35_ = GeneratedMessageLite.unmodifiableProtoList(field_35_); mutBitField0_ = mutBitField0_ and -2049 }
result.field_35_ = field_35_
return result
}
override fun mergeFrom(other: Property): Builder {
if (other === defaultInstance) return this
if (other.hasFlags()) setFlags(other.getFlags())
if (other.hasOldFlags()) setOldFlags(other.getOldFlags())
if (other.hasName()) setName(other.getName())
if (other.hasReturnType()) mergeReturnType(other.getReturnType())
if (other.hasReturnTypeId()) setReturnTypeId(other.getReturnTypeId())
if (other.hasReceiverType()) mergeReceiverType(other.getReceiverType())
if (other.hasReceiverTypeId()) setReceiverTypeId(other.getReceiverTypeId())
if (other.hasCompanionExtensionReceiverType()) mergeCompanionExtensionReceiverType(other.getCompanionExtensionReceiverType())
if (other.hasCompanionExtensionReceiverTypeId()) setCompanionExtensionReceiverTypeId(other.getCompanionExtensionReceiverTypeId())
if (other.hasSetterValueParameter()) mergeSetterValueParameter(other.getSetterValueParameter())
if (other.hasGetterFlags()) setGetterFlags(other.getGetterFlags())
if (other.hasSetterFlags()) setSetterFlags(other.getSetterFlags())
if (other.hasGetterContract()) mergeGetterContract(other.getGetterContract())
if (other.hasSetterContract()) mergeSetterContract(other.getSetterContract())
if (other.field_4_.isNotEmpty()) {
if (field_4_.isEmpty()) { field_4_ = other.field_4_; mutBitField0_ = mutBitField0_ and -2 } else { ensure_4_Mutable(); (field_4_ as MutableList<Any>).addAll(other.field_4_ as Collection<Any>) }
}
if (other.field_12_.isNotEmpty()) {
if (field_12_.isEmpty()) { field_12_ = other.field_12_; mutBitField0_ = mutBitField0_ and -3 } else { ensure_12_Mutable(); (field_12_ as MutableList<Any>).addAll(other.field_12_ as Collection<Any>) }
}
if (other.field_13_.isNotEmpty()) {
if (field_13_.isEmpty()) { field_13_ = other.field_13_; mutBitField0_ = mutBitField0_ and -5 } else { ensure_13_Mutable(); (field_13_ as MutableList<Any>).addAll(other.field_13_ as Collection<Any>) }
}
if (other.field_17_.isNotEmpty()) {
if (field_17_.isEmpty()) { field_17_ = other.field_17_; mutBitField0_ = mutBitField0_ and -9 } else { ensure_17_Mutable(); (field_17_ as MutableList<Any>).addAll(other.field_17_ as Collection<Any>) }
}
if (other.field_31_.isNotEmpty()) {
if (field_31_.isEmpty()) { field_31_ = other.field_31_; mutBitField0_ = mutBitField0_ and -17 } else { ensure_31_Mutable(); (field_31_ as MutableList<Any>).addAll(other.field_31_ as Collection<Any>) }
}
if (other.field_32_.isNotEmpty()) {
if (field_32_.isEmpty()) { field_32_ = other.field_32_; mutBitField0_ = mutBitField0_ and -33 } else { ensure_32_Mutable(); (field_32_ as MutableList<Any>).addAll(other.field_32_ as Collection<Any>) }
}
if (other.field_14_.isNotEmpty()) {
if (field_14_.isEmpty()) { field_14_ = other.field_14_; mutBitField0_ = mutBitField0_ and -65 } else { ensure_14_Mutable(); (field_14_ as MutableList<Any>).addAll(other.field_14_ as Collection<Any>) }
}
if (other.field_15_.isNotEmpty()) {
if (field_15_.isEmpty()) { field_15_ = other.field_15_; mutBitField0_ = mutBitField0_ and -129 } else { ensure_15_Mutable(); (field_15_ as MutableList<Any>).addAll(other.field_15_ as Collection<Any>) }
}
if (other.field_16_.isNotEmpty()) {
if (field_16_.isEmpty()) { field_16_ = other.field_16_; mutBitField0_ = mutBitField0_ and -257 } else { ensure_16_Mutable(); (field_16_ as MutableList<Any>).addAll(other.field_16_ as Collection<Any>) }
}
if (other.field_33_.isNotEmpty()) {
if (field_33_.isEmpty()) { field_33_ = other.field_33_; mutBitField0_ = mutBitField0_ and -513 } else { ensure_33_Mutable(); (field_33_ as MutableList<Any>).addAll(other.field_33_ as Collection<Any>) }
}
if (other.field_34_.isNotEmpty()) {
if (field_34_.isEmpty()) { field_34_ = other.field_34_; mutBitField0_ = mutBitField0_ and -1025 } else { ensure_34_Mutable(); (field_34_ as MutableList<Any>).addAll(other.field_34_ as Collection<Any>) }
}
if (other.field_35_.isNotEmpty()) {
if (field_35_.isEmpty()) { field_35_ = other.field_35_; mutBitField0_ = mutBitField0_ and -2049 } else { ensure_35_Mutable(); (field_35_ as MutableList<Any>).addAll(other.field_35_ as Collection<Any>) }
}
mergeExtensionFields(other)
unknownFields = unknownFields.concat(other.unknownFields)
return this
}
override fun isInitialized(): Boolean {
if ((bitField0_ and 4) == 0) return false
if ((bitField0_ and 8) != 0 && !getReturnType().isInitialized()) return false
if ((bitField0_ and 32) != 0 && !getReceiverType().isInitialized()) return false
if ((bitField0_ and 128) != 0 && !getCompanionExtensionReceiverType().isInitialized()) return false
if ((bitField0_ and 512) != 0 && !getSetterValueParameter().isInitialized()) return false
if ((bitField0_ and 4096) != 0 && !getGetterContract().isInitialized()) return false
if ((bitField0_ and 8192) != 0 && !getSetterContract().isInitialized()) return false
for (i in 0 until field_4_.size) { if (!field_4_[i].isInitialized()) return false }
for (i in 0 until field_12_.size) { if (!field_12_[i].isInitialized()) return false }
for (i in 0 until field_17_.size) { if (!field_17_[i].isInitialized()) return false }
for (i in 0 until field_32_.size) { if (!field_32_[i].isInitialized()) return false }
for (i in 0 until field_14_.size) { if (!field_14_[i].isInitialized()) return false }
for (i in 0 until field_15_.size) { if (!field_15_[i].isInitialized()) return false }
for (i in 0 until field_16_.size) { if (!field_16_[i].isInitialized()) return false }
for (i in 0 until field_33_.size) { if (!field_33_[i].isInitialized()) return false }
for (i in 0 until field_34_.size) { if (!field_34_[i].isInitialized()) return false }
for (i in 0 until field_35_.size) { if (!field_35_[i].isInitialized()) return false }
if (!extensionsAreInitialized()) return false
return true
}
override fun mergeFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Builder {
var parsedMessage: Property? = null
try {
parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry)
} catch (e: InvalidProtocolBufferException) {
parsedMessage = e.unfinishedMessage as? Property
throw e
} finally {
if (parsedMessage != null) mergeFrom(parsedMessage)
}
return this
}
var `oldFlags`: Int
get() = field_1_
set(value) { bitField0_ = bitField0_ or 2; field_1_ = value }
fun getOldFlags(): Int = field_1_
fun hasOldFlags(): Boolean = (bitField0_ and 2) != 0
fun setOldFlags(value: Int): Builder { bitField0_ = bitField0_ or 2; field_1_ = value; return this }
fun clearOldFlags(): Builder { bitField0_ = bitField0_ and -3; field_1_ = 2054; return this }
var `name`: Int
get() = field_2_
set(value) { bitField0_ = bitField0_ or 4; field_2_ = value }
fun getName(): Int = field_2_
fun hasName(): Boolean = (bitField0_ and 4) != 0
fun setName(value: Int): Builder { bitField0_ = bitField0_ or 4; field_2_ = value; return this }
fun clearName(): Builder { bitField0_ = bitField0_ and -5; field_2_ = 0; return this }
var `returnType`: org.jetbrains.kotlin.metadata.ProtoBuf.Type
get() = field_3_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance
set(value) { field_3_ = value; bitField0_ = bitField0_ or 8 }
fun getReturnType(): org.jetbrains.kotlin.metadata.ProtoBuf.Type = `returnType`
fun hasReturnType(): Boolean = (bitField0_ and 8) != 0
fun setReturnType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder { field_3_ = value; bitField0_ = bitField0_ or 8; return this }
fun setReturnType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder): Builder { field_3_ = value.build(); bitField0_ = bitField0_ or 8; return this }
fun mergeReturnType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder {
val cur = field_3_
field_3_ = if ((bitField0_ and 8) != 0 && cur != null && cur !== org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance) org.jetbrains.kotlin.metadata.ProtoBuf.Type.newBuilder(cur).mergeFrom(value).buildPartial() else value
bitField0_ = bitField0_ or 8
return this
}
fun clearReturnType(): Builder { field_3_ = null; bitField0_ = bitField0_ and -9; return this }
val `typeParameterList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter> get() = if (field_4_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_4_)
val `typeParameterCount`: Int get() = field_4_.size
fun getTypeParameterList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter> = `typeParameterList`
fun getTypeParameterCount(): Int = field_4_.size
fun getTypeParameter(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter = field_4_[index]
fun addTypeParameter(value: org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter): Builder { ensure_4_Mutable(); (field_4_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter>).add(value); return this }
fun setTypeParameter(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter): Builder { ensure_4_Mutable(); (field_4_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter>)[index] = value; return this }
fun addAllTypeParameter(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter>): Builder { ensure_4_Mutable(); AbstractMessageLite.Builder.addAll(values, field_4_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter>); return this }
fun clearTypeParameter(): Builder { field_4_ = emptyList(); mutBitField0_ = mutBitField0_ and -2; return this }
fun addTypeParameter(value: org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter.Builder): Builder = addTypeParameter(value.build())
fun setTypeParameter(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter.Builder): Builder = setTypeParameter(index, value.build())
var `receiverType`: org.jetbrains.kotlin.metadata.ProtoBuf.Type
get() = field_5_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance
set(value) { field_5_ = value; bitField0_ = bitField0_ or 32 }
fun getReceiverType(): org.jetbrains.kotlin.metadata.ProtoBuf.Type = `receiverType`
fun hasReceiverType(): Boolean = (bitField0_ and 32) != 0
fun setReceiverType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder { field_5_ = value; bitField0_ = bitField0_ or 32; return this }
fun setReceiverType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder): Builder { field_5_ = value.build(); bitField0_ = bitField0_ or 32; return this }
fun mergeReceiverType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder {
val cur = field_5_
field_5_ = if ((bitField0_ and 32) != 0 && cur != null && cur !== org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance) org.jetbrains.kotlin.metadata.ProtoBuf.Type.newBuilder(cur).mergeFrom(value).buildPartial() else value
bitField0_ = bitField0_ or 32
return this
}
fun clearReceiverType(): Builder { field_5_ = null; bitField0_ = bitField0_ and -33; return this }
var `setterValueParameter`: org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter
get() = field_6_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter.defaultInstance
set(value) { field_6_ = value; bitField0_ = bitField0_ or 512 }
fun getSetterValueParameter(): org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter = `setterValueParameter`
fun hasSetterValueParameter(): Boolean = (bitField0_ and 512) != 0
fun setSetterValueParameter(value: org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter): Builder { field_6_ = value; bitField0_ = bitField0_ or 512; return this }
fun setSetterValueParameter(value: org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter.Builder): Builder { field_6_ = value.build(); bitField0_ = bitField0_ or 512; return this }
fun mergeSetterValueParameter(value: org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter): Builder {
val cur = field_6_
field_6_ = if ((bitField0_ and 512) != 0 && cur != null && cur !== org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter.defaultInstance) org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter.newBuilder(cur).mergeFrom(value).buildPartial() else value
bitField0_ = bitField0_ or 512
return this
}
fun clearSetterValueParameter(): Builder { field_6_ = null; bitField0_ = bitField0_ and -513; return this }
var `getterFlags`: Int
get() = field_7_
set(value) { bitField0_ = bitField0_ or 1024; field_7_ = value }
fun getGetterFlags(): Int = field_7_
fun hasGetterFlags(): Boolean = (bitField0_ and 1024) != 0
fun setGetterFlags(value: Int): Builder { bitField0_ = bitField0_ or 1024; field_7_ = value; return this }
fun clearGetterFlags(): Builder { bitField0_ = bitField0_ and -1025; field_7_ = 0; return this }
var `setterFlags`: Int
get() = field_8_
set(value) { bitField0_ = bitField0_ or 2048; field_8_ = value }
fun getSetterFlags(): Int = field_8_
fun hasSetterFlags(): Boolean = (bitField0_ and 2048) != 0
fun setSetterFlags(value: Int): Builder { bitField0_ = bitField0_ or 2048; field_8_ = value; return this }
fun clearSetterFlags(): Builder { bitField0_ = bitField0_ and -2049; field_8_ = 0; return this }
var `returnTypeId`: Int
get() = field_9_
set(value) { bitField0_ = bitField0_ or 16; field_9_ = value }
fun getReturnTypeId(): Int = field_9_
fun hasReturnTypeId(): Boolean = (bitField0_ and 16) != 0
fun setReturnTypeId(value: Int): Builder { bitField0_ = bitField0_ or 16; field_9_ = value; return this }
fun clearReturnTypeId(): Builder { bitField0_ = bitField0_ and -17; field_9_ = 0; return this }
var `receiverTypeId`: Int
get() = field_10_
set(value) { bitField0_ = bitField0_ or 64; field_10_ = value }
fun getReceiverTypeId(): Int = field_10_
fun hasReceiverTypeId(): Boolean = (bitField0_ and 64) != 0
fun setReceiverTypeId(value: Int): Builder { bitField0_ = bitField0_ or 64; field_10_ = value; return this }
fun clearReceiverTypeId(): Builder { bitField0_ = bitField0_ and -65; field_10_ = 0; return this }
var `flags`: Int
get() = field_11_
set(value) { bitField0_ = bitField0_ or 1; field_11_ = value }
fun getFlags(): Int = field_11_
fun hasFlags(): Boolean = (bitField0_ and 1) != 0
fun setFlags(value: Int): Builder { bitField0_ = bitField0_ or 1; field_11_ = value; return this }
fun clearFlags(): Builder { bitField0_ = bitField0_ and -2; field_11_ = 518; return this }
val `contextReceiverTypeList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> get() = if (field_12_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_12_)
val `contextReceiverTypeCount`: Int get() = field_12_.size
fun getContextReceiverTypeList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Type> = `contextReceiverTypeList`
fun getContextReceiverTypeCount(): Int = field_12_.size
fun getContextReceiverType(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Type = field_12_[index]
fun addContextReceiverType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder { ensure_12_Mutable(); (field_12_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Type>).add(value); return this }
fun setContextReceiverType(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder { ensure_12_Mutable(); (field_12_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Type>)[index] = value; return this }
fun addAllContextReceiverType(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.Type>): Builder { ensure_12_Mutable(); AbstractMessageLite.Builder.addAll(values, field_12_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Type>); return this }
fun clearContextReceiverType(): Builder { field_12_ = emptyList(); mutBitField0_ = mutBitField0_ and -3; return this }
fun addContextReceiverType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder): Builder = addContextReceiverType(value.build())
fun setContextReceiverType(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder): Builder = setContextReceiverType(index, value.build())
val `contextReceiverTypeIdList`: List<Int> get() = if (field_13_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_13_)
val `contextReceiverTypeIdCount`: Int get() = field_13_.size
fun getContextReceiverTypeIdList(): List<Int> = `contextReceiverTypeIdList`
fun getContextReceiverTypeIdCount(): Int = field_13_.size
fun getContextReceiverTypeId(index: Int): Int = field_13_[index]
fun addContextReceiverTypeId(value: Int): Builder { ensure_13_Mutable(); (field_13_ as MutableList<Int>).add(value); return this }
fun setContextReceiverTypeId(index: Int, value: Int): Builder { ensure_13_Mutable(); (field_13_ as MutableList<Int>)[index] = value; return this }
fun addAllContextReceiverTypeId(values: Iterable<Int>): Builder { ensure_13_Mutable(); AbstractMessageLite.Builder.addAll(values, field_13_ as MutableList<Int>); return this }
fun clearContextReceiverTypeId(): Builder { field_13_ = emptyList(); mutBitField0_ = mutBitField0_ and -5; return this }
val `annotationList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> get() = if (field_14_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_14_)
val `annotationCount`: Int get() = field_14_.size
fun getAnnotationList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = `annotationList`
fun getAnnotationCount(): Int = field_14_.size
fun getAnnotation(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation = field_14_[index]
fun addAnnotation(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation): Builder { ensure_14_Mutable(); (field_14_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>).add(value); return this }
fun setAnnotation(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation): Builder { ensure_14_Mutable(); (field_14_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>)[index] = value; return this }
fun addAllAnnotation(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>): Builder { ensure_14_Mutable(); AbstractMessageLite.Builder.addAll(values, field_14_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>); return this }
fun clearAnnotation(): Builder { field_14_ = emptyList(); mutBitField0_ = mutBitField0_ and -65; return this }
fun addAnnotation(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Builder): Builder = addAnnotation(value.build())
fun setAnnotation(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Builder): Builder = setAnnotation(index, value.build())
val `getterAnnotationList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> get() = if (field_15_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_15_)
val `getterAnnotationCount`: Int get() = field_15_.size
fun getGetterAnnotationList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = `getterAnnotationList`
fun getGetterAnnotationCount(): Int = field_15_.size
fun getGetterAnnotation(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation = field_15_[index]
fun addGetterAnnotation(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation): Builder { ensure_15_Mutable(); (field_15_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>).add(value); return this }
fun setGetterAnnotation(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation): Builder { ensure_15_Mutable(); (field_15_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>)[index] = value; return this }
fun addAllGetterAnnotation(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>): Builder { ensure_15_Mutable(); AbstractMessageLite.Builder.addAll(values, field_15_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>); return this }
fun clearGetterAnnotation(): Builder { field_15_ = emptyList(); mutBitField0_ = mutBitField0_ and -129; return this }
fun addGetterAnnotation(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Builder): Builder = addGetterAnnotation(value.build())
fun setGetterAnnotation(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Builder): Builder = setGetterAnnotation(index, value.build())
val `setterAnnotationList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> get() = if (field_16_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_16_)
val `setterAnnotationCount`: Int get() = field_16_.size
fun getSetterAnnotationList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = `setterAnnotationList`
fun getSetterAnnotationCount(): Int = field_16_.size
fun getSetterAnnotation(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation = field_16_[index]
fun addSetterAnnotation(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation): Builder { ensure_16_Mutable(); (field_16_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>).add(value); return this }
fun setSetterAnnotation(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation): Builder { ensure_16_Mutable(); (field_16_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>)[index] = value; return this }
fun addAllSetterAnnotation(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>): Builder { ensure_16_Mutable(); AbstractMessageLite.Builder.addAll(values, field_16_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>); return this }
fun clearSetterAnnotation(): Builder { field_16_ = emptyList(); mutBitField0_ = mutBitField0_ and -257; return this }
fun addSetterAnnotation(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Builder): Builder = addSetterAnnotation(value.build())
fun setSetterAnnotation(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Builder): Builder = setSetterAnnotation(index, value.build())
val `contextParameterList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter> get() = if (field_17_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_17_)
val `contextParameterCount`: Int get() = field_17_.size
fun getContextParameterList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter> = `contextParameterList`
fun getContextParameterCount(): Int = field_17_.size
fun getContextParameter(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter = field_17_[index]
fun addContextParameter(value: org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter): Builder { ensure_17_Mutable(); (field_17_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter>).add(value); return this }
fun setContextParameter(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter): Builder { ensure_17_Mutable(); (field_17_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter>)[index] = value; return this }
fun addAllContextParameter(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter>): Builder { ensure_17_Mutable(); AbstractMessageLite.Builder.addAll(values, field_17_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter>); return this }
fun clearContextParameter(): Builder { field_17_ = emptyList(); mutBitField0_ = mutBitField0_ and -9; return this }
fun addContextParameter(value: org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter.Builder): Builder = addContextParameter(value.build())
fun setContextParameter(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter.Builder): Builder = setContextParameter(index, value.build())
var `companionExtensionReceiverType`: org.jetbrains.kotlin.metadata.ProtoBuf.Type
get() = field_18_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance
set(value) { field_18_ = value; bitField0_ = bitField0_ or 128 }
fun getCompanionExtensionReceiverType(): org.jetbrains.kotlin.metadata.ProtoBuf.Type = `companionExtensionReceiverType`
fun hasCompanionExtensionReceiverType(): Boolean = (bitField0_ and 128) != 0
fun setCompanionExtensionReceiverType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder { field_18_ = value; bitField0_ = bitField0_ or 128; return this }
fun setCompanionExtensionReceiverType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder): Builder { field_18_ = value.build(); bitField0_ = bitField0_ or 128; return this }
fun mergeCompanionExtensionReceiverType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder {
val cur = field_18_
field_18_ = if ((bitField0_ and 128) != 0 && cur != null && cur !== org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance) org.jetbrains.kotlin.metadata.ProtoBuf.Type.newBuilder(cur).mergeFrom(value).buildPartial() else value
bitField0_ = bitField0_ or 128
return this
}
fun clearCompanionExtensionReceiverType(): Builder { field_18_ = null; bitField0_ = bitField0_ and -129; return this }
var `companionExtensionReceiverTypeId`: Int
get() = field_19_
set(value) { bitField0_ = bitField0_ or 256; field_19_ = value }
fun getCompanionExtensionReceiverTypeId(): Int = field_19_
fun hasCompanionExtensionReceiverTypeId(): Boolean = (bitField0_ and 256) != 0
fun setCompanionExtensionReceiverTypeId(value: Int): Builder { bitField0_ = bitField0_ or 256; field_19_ = value; return this }
fun clearCompanionExtensionReceiverTypeId(): Builder { bitField0_ = bitField0_ and -257; field_19_ = 0; return this }
val `versionRequirementList`: List<Int> get() = if (field_31_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_31_)
val `versionRequirementCount`: Int get() = field_31_.size
fun getVersionRequirementList(): List<Int> = `versionRequirementList`
fun getVersionRequirementCount(): Int = field_31_.size
fun getVersionRequirement(index: Int): Int = field_31_[index]
fun addVersionRequirement(value: Int): Builder { ensure_31_Mutable(); (field_31_ as MutableList<Int>).add(value); return this }
fun setVersionRequirement(index: Int, value: Int): Builder { ensure_31_Mutable(); (field_31_ as MutableList<Int>)[index] = value; return this }
fun addAllVersionRequirement(values: Iterable<Int>): Builder { ensure_31_Mutable(); AbstractMessageLite.Builder.addAll(values, field_31_ as MutableList<Int>); return this }
fun clearVersionRequirement(): Builder { field_31_ = emptyList(); mutBitField0_ = mutBitField0_ and -17; return this }
val `compilerPluginDataList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData> get() = if (field_32_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_32_)
val `compilerPluginDataCount`: Int get() = field_32_.size
fun getCompilerPluginDataList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData> = `compilerPluginDataList`
fun getCompilerPluginDataCount(): Int = field_32_.size
fun getCompilerPluginData(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData = field_32_[index]
fun addCompilerPluginData(value: org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData): Builder { ensure_32_Mutable(); (field_32_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData>).add(value); return this }
fun setCompilerPluginData(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData): Builder { ensure_32_Mutable(); (field_32_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData>)[index] = value; return this }
fun addAllCompilerPluginData(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData>): Builder { ensure_32_Mutable(); AbstractMessageLite.Builder.addAll(values, field_32_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData>); return this }
fun clearCompilerPluginData(): Builder { field_32_ = emptyList(); mutBitField0_ = mutBitField0_ and -33; return this }
fun addCompilerPluginData(value: org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData.Builder): Builder = addCompilerPluginData(value.build())
fun setCompilerPluginData(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData.Builder): Builder = setCompilerPluginData(index, value.build())
val `extensionReceiverAnnotationList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> get() = if (field_33_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_33_)
val `extensionReceiverAnnotationCount`: Int get() = field_33_.size
fun getExtensionReceiverAnnotationList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = `extensionReceiverAnnotationList`
fun getExtensionReceiverAnnotationCount(): Int = field_33_.size
fun getExtensionReceiverAnnotation(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation = field_33_[index]
fun addExtensionReceiverAnnotation(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation): Builder { ensure_33_Mutable(); (field_33_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>).add(value); return this }
fun setExtensionReceiverAnnotation(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation): Builder { ensure_33_Mutable(); (field_33_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>)[index] = value; return this }
fun addAllExtensionReceiverAnnotation(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>): Builder { ensure_33_Mutable(); AbstractMessageLite.Builder.addAll(values, field_33_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>); return this }
fun clearExtensionReceiverAnnotation(): Builder { field_33_ = emptyList(); mutBitField0_ = mutBitField0_ and -513; return this }
fun addExtensionReceiverAnnotation(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Builder): Builder = addExtensionReceiverAnnotation(value.build())
fun setExtensionReceiverAnnotation(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Builder): Builder = setExtensionReceiverAnnotation(index, value.build())
val `backingFieldAnnotationList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> get() = if (field_34_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_34_)
val `backingFieldAnnotationCount`: Int get() = field_34_.size
fun getBackingFieldAnnotationList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = `backingFieldAnnotationList`
fun getBackingFieldAnnotationCount(): Int = field_34_.size
fun getBackingFieldAnnotation(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation = field_34_[index]
fun addBackingFieldAnnotation(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation): Builder { ensure_34_Mutable(); (field_34_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>).add(value); return this }
fun setBackingFieldAnnotation(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation): Builder { ensure_34_Mutable(); (field_34_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>)[index] = value; return this }
fun addAllBackingFieldAnnotation(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>): Builder { ensure_34_Mutable(); AbstractMessageLite.Builder.addAll(values, field_34_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>); return this }
fun clearBackingFieldAnnotation(): Builder { field_34_ = emptyList(); mutBitField0_ = mutBitField0_ and -1025; return this }
fun addBackingFieldAnnotation(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Builder): Builder = addBackingFieldAnnotation(value.build())
fun setBackingFieldAnnotation(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Builder): Builder = setBackingFieldAnnotation(index, value.build())
val `delegateFieldAnnotationList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> get() = if (field_35_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_35_)
val `delegateFieldAnnotationCount`: Int get() = field_35_.size
fun getDelegateFieldAnnotationList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = `delegateFieldAnnotationList`
fun getDelegateFieldAnnotationCount(): Int = field_35_.size
fun getDelegateFieldAnnotation(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation = field_35_[index]
fun addDelegateFieldAnnotation(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation): Builder { ensure_35_Mutable(); (field_35_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>).add(value); return this }
fun setDelegateFieldAnnotation(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation): Builder { ensure_35_Mutable(); (field_35_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>)[index] = value; return this }
fun addAllDelegateFieldAnnotation(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>): Builder { ensure_35_Mutable(); AbstractMessageLite.Builder.addAll(values, field_35_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>); return this }
fun clearDelegateFieldAnnotation(): Builder { field_35_ = emptyList(); mutBitField0_ = mutBitField0_ and -2049; return this }
fun addDelegateFieldAnnotation(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Builder): Builder = addDelegateFieldAnnotation(value.build())
fun setDelegateFieldAnnotation(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Builder): Builder = setDelegateFieldAnnotation(index, value.build())
var `getterContract`: org.jetbrains.kotlin.metadata.ProtoBuf.Contract
get() = field_40_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Contract.defaultInstance
set(value) { field_40_ = value; bitField0_ = bitField0_ or 4096 }
fun getGetterContract(): org.jetbrains.kotlin.metadata.ProtoBuf.Contract = `getterContract`
fun hasGetterContract(): Boolean = (bitField0_ and 4096) != 0
fun setGetterContract(value: org.jetbrains.kotlin.metadata.ProtoBuf.Contract): Builder { field_40_ = value; bitField0_ = bitField0_ or 4096; return this }
fun setGetterContract(value: org.jetbrains.kotlin.metadata.ProtoBuf.Contract.Builder): Builder { field_40_ = value.build(); bitField0_ = bitField0_ or 4096; return this }
fun mergeGetterContract(value: org.jetbrains.kotlin.metadata.ProtoBuf.Contract): Builder {
val cur = field_40_
field_40_ = if ((bitField0_ and 4096) != 0 && cur != null && cur !== org.jetbrains.kotlin.metadata.ProtoBuf.Contract.defaultInstance) org.jetbrains.kotlin.metadata.ProtoBuf.Contract.newBuilder(cur).mergeFrom(value).buildPartial() else value
bitField0_ = bitField0_ or 4096
return this
}
fun clearGetterContract(): Builder { field_40_ = null; bitField0_ = bitField0_ and -4097; return this }
var `setterContract`: org.jetbrains.kotlin.metadata.ProtoBuf.Contract
get() = field_41_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Contract.defaultInstance
set(value) { field_41_ = value; bitField0_ = bitField0_ or 8192 }
fun getSetterContract(): org.jetbrains.kotlin.metadata.ProtoBuf.Contract = `setterContract`
fun hasSetterContract(): Boolean = (bitField0_ and 8192) != 0
fun setSetterContract(value: org.jetbrains.kotlin.metadata.ProtoBuf.Contract): Builder { field_41_ = value; bitField0_ = bitField0_ or 8192; return this }
fun setSetterContract(value: org.jetbrains.kotlin.metadata.ProtoBuf.Contract.Builder): Builder { field_41_ = value.build(); bitField0_ = bitField0_ or 8192; return this }
fun mergeSetterContract(value: org.jetbrains.kotlin.metadata.ProtoBuf.Contract): Builder {
val cur = field_41_
field_41_ = if ((bitField0_ and 8192) != 0 && cur != null && cur !== org.jetbrains.kotlin.metadata.ProtoBuf.Contract.defaultInstance) org.jetbrains.kotlin.metadata.ProtoBuf.Contract.newBuilder(cur).mergeFrom(value).buildPartial() else value
bitField0_ = bitField0_ or 8192
return this
}
fun clearSetterContract(): Builder { field_41_ = null; bitField0_ = bitField0_ and -8193; return this }
}
companion object {
val defaultInstance: Property = Property()
fun getDefaultInstance(): Property = defaultInstance
fun newBuilder(): Builder = Builder()
fun newBuilder(prototype: Property): Builder = Builder().mergeFrom(prototype)
val PARSER: Parser<Property> = object : AbstractParser<Property>() {
override fun parsePartialFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Property =
Property(input, extensionRegistry)
}
fun parseFrom(bytes: ByteArray): Property = PARSER.parseFrom(bytes)
fun parseFrom(bytes: ByteArray, registry: ExtensionRegistryLite): Property = PARSER.parseFrom(bytes, registry)
fun parseFrom(bytes: ByteString): Property { val input = bytes.newCodedInput(); val msg = PARSER.parsePartialFrom(input, ExtensionRegistryLite.getEmptyRegistry()); input.checkLastTagWas(0); if (!msg.isInitialized()) throw UninitializedMessageException().asInvalidProtocolBufferException().setUnfinishedMessage(msg); return msg }
fun parseFrom(input: CodedInputStream): Property = PARSER.parseFrom(input)
fun parseFrom(input: CodedInputStream, registry: ExtensionRegistryLite): Property = PARSER.parseFrom(input, registry)
const val OLD_FLAGS_FIELD_NUMBER = 1
const val NAME_FIELD_NUMBER = 2
const val RETURN_TYPE_FIELD_NUMBER = 3
const val TYPE_PARAMETER_FIELD_NUMBER = 4
const val RECEIVER_TYPE_FIELD_NUMBER = 5
const val SETTER_VALUE_PARAMETER_FIELD_NUMBER = 6
const val GETTER_FLAGS_FIELD_NUMBER = 7
const val SETTER_FLAGS_FIELD_NUMBER = 8
const val RETURN_TYPE_ID_FIELD_NUMBER = 9
const val RECEIVER_TYPE_ID_FIELD_NUMBER = 10
const val FLAGS_FIELD_NUMBER = 11
const val CONTEXT_RECEIVER_TYPE_FIELD_NUMBER = 12
const val CONTEXT_RECEIVER_TYPE_ID_FIELD_NUMBER = 13
const val ANNOTATION_FIELD_NUMBER = 14
const val GETTER_ANNOTATION_FIELD_NUMBER = 15
const val SETTER_ANNOTATION_FIELD_NUMBER = 16
const val CONTEXT_PARAMETER_FIELD_NUMBER = 17
const val COMPANION_EXTENSION_RECEIVER_TYPE_FIELD_NUMBER = 18
const val COMPANION_EXTENSION_RECEIVER_TYPE_ID_FIELD_NUMBER = 19
const val VERSION_REQUIREMENT_FIELD_NUMBER = 31
const val COMPILER_PLUGIN_DATA_FIELD_NUMBER = 32
const val EXTENSION_RECEIVER_ANNOTATION_FIELD_NUMBER = 33
const val BACKING_FIELD_ANNOTATION_FIELD_NUMBER = 34
const val DELEGATE_FIELD_ANNOTATION_FIELD_NUMBER = 35
const val GETTERCONTRACT_FIELD_NUMBER = 40
const val SETTERCONTRACT_FIELD_NUMBER = 41
}}
class ValueParameter : GeneratedMessageLite.ExtendableMessage<ValueParameter> {
internal var bitField0_: Int = 0
internal var field_1_: Int = 0
internal var field_2_: Int = 0
internal var field_3_: org.jetbrains.kotlin.metadata.ProtoBuf.Type? = null
internal var field_5_: Int = 0
internal var field_4_: org.jetbrains.kotlin.metadata.ProtoBuf.Type? = null
internal var field_6_: Int = 0
internal var field_8_: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value? = null
internal var field_9_: org.jetbrains.kotlin.metadata.ProtoBuf.Type? = null
internal var field_10_: Int = 0
internal var field_7_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = emptyList()
private var memoizedIsInitialized: Byte = -1
private var memoizedSerializedSize: Int = -1
private constructor()
private constructor(builder: Builder) {
this.unknownFields = builder.unknownFields
copyExtensionsFromBuilder(builder)
}
private constructor(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite) {
var mutBit0_ = 0
var unknownBuffer: ByteString.Output? = null
var unknownOut: CodedOutputStream? = null
fun unknownOutput(): CodedOutputStream {
var out = unknownOut
if (out == null) { val buf = ByteString.newOutput(); unknownBuffer = buf; out = CodedOutputStream.newInstance(buf); unknownOut = out }
return out
}
try {
var done = false
while (!done) {
val tag = input.readTag()
when (tag) {
0 -> done = true
8 -> { bitField0_ = bitField0_ or 1; field_1_ = input.readInt32() }
16 -> { bitField0_ = bitField0_ or 2; field_2_ = input.readInt32() }
26 -> {
var subBuilder: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder? = null
if ((bitField0_ and 4) != 0) subBuilder = field_3_?.toBuilder()
val parsed = input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Type.PARSER, extensionRegistry)
field_3_ = if (subBuilder != null) subBuilder.mergeFrom(parsed).buildPartial() else parsed
bitField0_ = bitField0_ or 4
}
34 -> {
var subBuilder: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder? = null
if ((bitField0_ and 16) != 0) subBuilder = field_4_?.toBuilder()
val parsed = input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Type.PARSER, extensionRegistry)
field_4_ = if (subBuilder != null) subBuilder.mergeFrom(parsed).buildPartial() else parsed
bitField0_ = bitField0_ or 16
}
40 -> { bitField0_ = bitField0_ or 8; field_5_ = input.readInt32() }
48 -> { bitField0_ = bitField0_ or 32; field_6_ = input.readInt32() }
58 -> {
if ((mutBit0_ and 1) == 0) { field_7_ = ArrayList(); mutBit0_ = mutBit0_ or 1 }
(field_7_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.PARSER, extensionRegistry))
}
66 -> {
var subBuilder: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value.Builder? = null
if ((bitField0_ and 64) != 0) subBuilder = field_8_?.toBuilder()
val parsed = input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value.PARSER, extensionRegistry)
field_8_ = if (subBuilder != null) subBuilder.mergeFrom(parsed).buildPartial() else parsed
bitField0_ = bitField0_ or 64
}
74 -> {
var subBuilder: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder? = null
if ((bitField0_ and 128) != 0) subBuilder = field_9_?.toBuilder()
val parsed = input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Type.PARSER, extensionRegistry)
field_9_ = if (subBuilder != null) subBuilder.mergeFrom(parsed).buildPartial() else parsed
bitField0_ = bitField0_ or 128
}
80 -> { bitField0_ = bitField0_ or 256; field_10_ = input.readInt32() }
else -> if (!parseUnknownField(input, unknownOutput(), extensionRegistry, tag)) done = true
}}
} catch (e: InvalidProtocolBufferException) {
throw e.setUnfinishedMessage(this)
} finally {
if ((mutBit0_ and 1) != 0) field_7_ = GeneratedMessageLite.unmodifiableProtoList(field_7_)
if (unknownBuffer != null) { unknownOut!!.flush(); unknownFields = unknownBuffer.toByteString() }
makeExtensionsImmutable()
}}
val `flags`: Int get() = field_1_
fun getFlags(): Int = `flags`
fun hasFlags(): Boolean = (bitField0_ and 1) != 0
val `name`: Int get() = field_2_
fun getName(): Int = `name`
fun hasName(): Boolean = (bitField0_ and 2) != 0
val `type`: org.jetbrains.kotlin.metadata.ProtoBuf.Type get() = field_3_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance
fun getType(): org.jetbrains.kotlin.metadata.ProtoBuf.Type = `type`
fun hasType(): Boolean = (bitField0_ and 4) != 0
val `varargElementType`: org.jetbrains.kotlin.metadata.ProtoBuf.Type get() = field_4_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance
fun getVarargElementType(): org.jetbrains.kotlin.metadata.ProtoBuf.Type = `varargElementType`
fun hasVarargElementType(): Boolean = (bitField0_ and 16) != 0
val `typeId`: Int get() = field_5_
fun getTypeId(): Int = `typeId`
fun hasTypeId(): Boolean = (bitField0_ and 8) != 0
val `varargElementTypeId`: Int get() = field_6_
fun getVarargElementTypeId(): Int = `varargElementTypeId`
fun hasVarargElementTypeId(): Boolean = (bitField0_ and 32) != 0
val `annotationList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> get() = field_7_
val `annotationCount`: Int get() = field_7_.size
fun getAnnotationList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = field_7_
fun getAnnotationCount(): Int = field_7_.size
fun getAnnotation(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation = field_7_[index]
val `annotationParameterDefaultValue`: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value get() = field_8_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value.defaultInstance
fun getAnnotationParameterDefaultValue(): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value = `annotationParameterDefaultValue`
fun hasAnnotationParameterDefaultValue(): Boolean = (bitField0_ and 64) != 0
val `equalityBoundType`: org.jetbrains.kotlin.metadata.ProtoBuf.Type get() = field_9_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance
fun getEqualityBoundType(): org.jetbrains.kotlin.metadata.ProtoBuf.Type = `equalityBoundType`
fun hasEqualityBoundType(): Boolean = (bitField0_ and 128) != 0
val `equalityBoundTypeId`: Int get() = field_10_
fun getEqualityBoundTypeId(): Int = `equalityBoundTypeId`
fun hasEqualityBoundTypeId(): Boolean = (bitField0_ and 256) != 0
override fun isInitialized(): Boolean {
val isInitialized = memoizedIsInitialized.toInt()
if (isInitialized == 1) return true
if (isInitialized == 0) return false
if ((bitField0_ and 2) == 0) { memoizedIsInitialized = 0; return false }
if ((bitField0_ and 4) != 0 && !getType().isInitialized()) { memoizedIsInitialized = 0; return false }
if ((bitField0_ and 16) != 0 && !getVarargElementType().isInitialized()) { memoizedIsInitialized = 0; return false }
if ((bitField0_ and 64) != 0 && !getAnnotationParameterDefaultValue().isInitialized()) { memoizedIsInitialized = 0; return false }
if ((bitField0_ and 128) != 0 && !getEqualityBoundType().isInitialized()) { memoizedIsInitialized = 0; return false }
for (i in 0 until field_7_.size) { if (!field_7_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
if (!extensionsAreInitialized()) { memoizedIsInitialized = 0; return false }
memoizedIsInitialized = 1
return true
}
override fun writeTo(output: CodedOutputStream) {
val extensionWriter = if (hasAnyExtensions()) newExtensionWriter() else null
extensionWriter?.writeUntil(1, output)
if ((bitField0_ and 1) != 0) { output.writeRawVarint32(8); output.writeInt32NoTag(field_1_) }
extensionWriter?.writeUntil(2, output)
if ((bitField0_ and 2) != 0) { output.writeRawVarint32(16); output.writeInt32NoTag(field_2_) }
extensionWriter?.writeUntil(3, output)
if ((bitField0_ and 4) != 0) { output.writeRawVarint32(26); output.writeMessageNoTag(getType()) }
extensionWriter?.writeUntil(4, output)
if ((bitField0_ and 16) != 0) { output.writeRawVarint32(34); output.writeMessageNoTag(getVarargElementType()) }
extensionWriter?.writeUntil(5, output)
if ((bitField0_ and 8) != 0) { output.writeRawVarint32(40); output.writeInt32NoTag(field_5_) }
extensionWriter?.writeUntil(6, output)
if ((bitField0_ and 32) != 0) { output.writeRawVarint32(48); output.writeInt32NoTag(field_6_) }
extensionWriter?.writeUntil(7, output)
for (i in 0 until field_7_.size) { output.writeRawVarint32(58); output.writeMessageNoTag(field_7_[i]) }
extensionWriter?.writeUntil(8, output)
if ((bitField0_ and 64) != 0) { output.writeRawVarint32(66); output.writeMessageNoTag(getAnnotationParameterDefaultValue()) }
extensionWriter?.writeUntil(9, output)
if ((bitField0_ and 128) != 0) { output.writeRawVarint32(74); output.writeMessageNoTag(getEqualityBoundType()) }
extensionWriter?.writeUntil(10, output)
if ((bitField0_ and 256) != 0) { output.writeRawVarint32(80); output.writeInt32NoTag(field_10_) }
extensionWriter?.writeUntil(Int.MAX_VALUE, output)
if (!unknownFields.isEmpty()) unknownFields.writeTo(output)
}
override val serializedSize: Int get() {
val cached = memoizedSerializedSize
if (cached != -1) return cached
var size = 0
if ((bitField0_ and 1) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_1_)
if ((bitField0_ and 2) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_2_)
if ((bitField0_ and 4) != 0) size += 1 + CodedOutputStream.computeMessageSizeNoTag(getType())
if ((bitField0_ and 8) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_5_)
if ((bitField0_ and 16) != 0) size += 1 + CodedOutputStream.computeMessageSizeNoTag(getVarargElementType())
if ((bitField0_ and 32) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_6_)
if ((bitField0_ and 64) != 0) size += 1 + CodedOutputStream.computeMessageSizeNoTag(getAnnotationParameterDefaultValue())
if ((bitField0_ and 128) != 0) size += 1 + CodedOutputStream.computeMessageSizeNoTag(getEqualityBoundType())
if ((bitField0_ and 256) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_10_)
for (i in 0 until field_7_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_7_[i]) }
size += extensionsSerializedSize()
size += unknownFields.size()
memoizedSerializedSize = size
return size
}
override val defaultInstanceForType: ValueParameter get() = defaultInstance
override fun newBuilderForType(): Builder = Builder()
override fun toBuilder(): Builder = Builder().mergeFrom(this)
class Builder : GeneratedMessageLite.ExtendableBuilder<ValueParameter, Builder>() {
private var bitField0_: Int = 0
private var mutBitField0_: Int = 0
private var field_1_: Int = 0
private var field_2_: Int = 0
private var field_3_: org.jetbrains.kotlin.metadata.ProtoBuf.Type? = null
private var field_5_: Int = 0
private var field_4_: org.jetbrains.kotlin.metadata.ProtoBuf.Type? = null
private var field_6_: Int = 0
private var field_8_: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value? = null
private var field_9_: org.jetbrains.kotlin.metadata.ProtoBuf.Type? = null
private var field_10_: Int = 0
private var field_7_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = emptyList()
private fun ensure_7_Mutable() { if ((mutBitField0_ and 1) == 0) { field_7_ = ArrayList(field_7_); mutBitField0_ = mutBitField0_ or 1 } }
override val defaultInstanceForType: ValueParameter get() = defaultInstance
override fun clear(): Builder {
bitField0_ = 0
mutBitField0_ = 0
field_1_ = 0
field_2_ = 0
field_3_ = null
field_5_ = 0
field_4_ = null
field_6_ = 0
field_8_ = null
field_9_ = null
field_10_ = 0
field_7_ = emptyList()
clearExtensions()
unknownFields = ByteString.EMPTY
return this
}
override fun clone(): Builder = Builder().mergeFrom(buildPartial())
override fun build(): ValueParameter {
val result = buildPartial()
if (!result.isInitialized()) throw UninitializedMessageException()
return result
}
override fun buildPartial(): ValueParameter {
val result = ValueParameter(this)
val from_bitField0_ = bitField0_
var to_bitField0_ = 0
if ((from_bitField0_ and 1) != 0) to_bitField0_ = to_bitField0_ or 1
result.field_1_ = field_1_
if ((from_bitField0_ and 2) != 0) to_bitField0_ = to_bitField0_ or 2
result.field_2_ = field_2_
if ((from_bitField0_ and 4) != 0) to_bitField0_ = to_bitField0_ or 4
result.field_3_ = field_3_
if ((from_bitField0_ and 8) != 0) to_bitField0_ = to_bitField0_ or 8
result.field_5_ = field_5_
if ((from_bitField0_ and 16) != 0) to_bitField0_ = to_bitField0_ or 16
result.field_4_ = field_4_
if ((from_bitField0_ and 32) != 0) to_bitField0_ = to_bitField0_ or 32
result.field_6_ = field_6_
if ((from_bitField0_ and 64) != 0) to_bitField0_ = to_bitField0_ or 64
result.field_8_ = field_8_
if ((from_bitField0_ and 128) != 0) to_bitField0_ = to_bitField0_ or 128
result.field_9_ = field_9_
if ((from_bitField0_ and 256) != 0) to_bitField0_ = to_bitField0_ or 256
result.field_10_ = field_10_
result.bitField0_ = to_bitField0_
if ((mutBitField0_ and 1) != 0) { field_7_ = GeneratedMessageLite.unmodifiableProtoList(field_7_); mutBitField0_ = mutBitField0_ and -2 }
result.field_7_ = field_7_
return result
}
override fun mergeFrom(other: ValueParameter): Builder {
if (other === defaultInstance) return this
if (other.hasFlags()) setFlags(other.getFlags())
if (other.hasName()) setName(other.getName())
if (other.hasType()) mergeType(other.getType())
if (other.hasTypeId()) setTypeId(other.getTypeId())
if (other.hasVarargElementType()) mergeVarargElementType(other.getVarargElementType())
if (other.hasVarargElementTypeId()) setVarargElementTypeId(other.getVarargElementTypeId())
if (other.hasAnnotationParameterDefaultValue()) mergeAnnotationParameterDefaultValue(other.getAnnotationParameterDefaultValue())
if (other.hasEqualityBoundType()) mergeEqualityBoundType(other.getEqualityBoundType())
if (other.hasEqualityBoundTypeId()) setEqualityBoundTypeId(other.getEqualityBoundTypeId())
if (other.field_7_.isNotEmpty()) {
if (field_7_.isEmpty()) { field_7_ = other.field_7_; mutBitField0_ = mutBitField0_ and -2 } else { ensure_7_Mutable(); (field_7_ as MutableList<Any>).addAll(other.field_7_ as Collection<Any>) }
}
mergeExtensionFields(other)
unknownFields = unknownFields.concat(other.unknownFields)
return this
}
override fun isInitialized(): Boolean {
if ((bitField0_ and 2) == 0) return false
if ((bitField0_ and 4) != 0 && !getType().isInitialized()) return false
if ((bitField0_ and 16) != 0 && !getVarargElementType().isInitialized()) return false
if ((bitField0_ and 64) != 0 && !getAnnotationParameterDefaultValue().isInitialized()) return false
if ((bitField0_ and 128) != 0 && !getEqualityBoundType().isInitialized()) return false
for (i in 0 until field_7_.size) { if (!field_7_[i].isInitialized()) return false }
if (!extensionsAreInitialized()) return false
return true
}
override fun mergeFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Builder {
var parsedMessage: ValueParameter? = null
try {
parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry)
} catch (e: InvalidProtocolBufferException) {
parsedMessage = e.unfinishedMessage as? ValueParameter
throw e
} finally {
if (parsedMessage != null) mergeFrom(parsedMessage)
}
return this
}
var `flags`: Int
get() = field_1_
set(value) { bitField0_ = bitField0_ or 1; field_1_ = value }
fun getFlags(): Int = field_1_
fun hasFlags(): Boolean = (bitField0_ and 1) != 0
fun setFlags(value: Int): Builder { bitField0_ = bitField0_ or 1; field_1_ = value; return this }
fun clearFlags(): Builder { bitField0_ = bitField0_ and -2; field_1_ = 0; return this }
var `name`: Int
get() = field_2_
set(value) { bitField0_ = bitField0_ or 2; field_2_ = value }
fun getName(): Int = field_2_
fun hasName(): Boolean = (bitField0_ and 2) != 0
fun setName(value: Int): Builder { bitField0_ = bitField0_ or 2; field_2_ = value; return this }
fun clearName(): Builder { bitField0_ = bitField0_ and -3; field_2_ = 0; return this }
var `type`: org.jetbrains.kotlin.metadata.ProtoBuf.Type
get() = field_3_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance
set(value) { field_3_ = value; bitField0_ = bitField0_ or 4 }
fun getType(): org.jetbrains.kotlin.metadata.ProtoBuf.Type = `type`
fun hasType(): Boolean = (bitField0_ and 4) != 0
fun setType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder { field_3_ = value; bitField0_ = bitField0_ or 4; return this }
fun setType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder): Builder { field_3_ = value.build(); bitField0_ = bitField0_ or 4; return this }
fun mergeType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder {
val cur = field_3_
field_3_ = if ((bitField0_ and 4) != 0 && cur != null && cur !== org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance) org.jetbrains.kotlin.metadata.ProtoBuf.Type.newBuilder(cur).mergeFrom(value).buildPartial() else value
bitField0_ = bitField0_ or 4
return this
}
fun clearType(): Builder { field_3_ = null; bitField0_ = bitField0_ and -5; return this }
var `varargElementType`: org.jetbrains.kotlin.metadata.ProtoBuf.Type
get() = field_4_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance
set(value) { field_4_ = value; bitField0_ = bitField0_ or 16 }
fun getVarargElementType(): org.jetbrains.kotlin.metadata.ProtoBuf.Type = `varargElementType`
fun hasVarargElementType(): Boolean = (bitField0_ and 16) != 0
fun setVarargElementType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder { field_4_ = value; bitField0_ = bitField0_ or 16; return this }
fun setVarargElementType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder): Builder { field_4_ = value.build(); bitField0_ = bitField0_ or 16; return this }
fun mergeVarargElementType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder {
val cur = field_4_
field_4_ = if ((bitField0_ and 16) != 0 && cur != null && cur !== org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance) org.jetbrains.kotlin.metadata.ProtoBuf.Type.newBuilder(cur).mergeFrom(value).buildPartial() else value
bitField0_ = bitField0_ or 16
return this
}
fun clearVarargElementType(): Builder { field_4_ = null; bitField0_ = bitField0_ and -17; return this }
var `typeId`: Int
get() = field_5_
set(value) { bitField0_ = bitField0_ or 8; field_5_ = value }
fun getTypeId(): Int = field_5_
fun hasTypeId(): Boolean = (bitField0_ and 8) != 0
fun setTypeId(value: Int): Builder { bitField0_ = bitField0_ or 8; field_5_ = value; return this }
fun clearTypeId(): Builder { bitField0_ = bitField0_ and -9; field_5_ = 0; return this }
var `varargElementTypeId`: Int
get() = field_6_
set(value) { bitField0_ = bitField0_ or 32; field_6_ = value }
fun getVarargElementTypeId(): Int = field_6_
fun hasVarargElementTypeId(): Boolean = (bitField0_ and 32) != 0
fun setVarargElementTypeId(value: Int): Builder { bitField0_ = bitField0_ or 32; field_6_ = value; return this }
fun clearVarargElementTypeId(): Builder { bitField0_ = bitField0_ and -33; field_6_ = 0; return this }
val `annotationList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> get() = if (field_7_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_7_)
val `annotationCount`: Int get() = field_7_.size
fun getAnnotationList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = `annotationList`
fun getAnnotationCount(): Int = field_7_.size
fun getAnnotation(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation = field_7_[index]
fun addAnnotation(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation): Builder { ensure_7_Mutable(); (field_7_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>).add(value); return this }
fun setAnnotation(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation): Builder { ensure_7_Mutable(); (field_7_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>)[index] = value; return this }
fun addAllAnnotation(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>): Builder { ensure_7_Mutable(); AbstractMessageLite.Builder.addAll(values, field_7_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>); return this }
fun clearAnnotation(): Builder { field_7_ = emptyList(); mutBitField0_ = mutBitField0_ and -2; return this }
fun addAnnotation(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Builder): Builder = addAnnotation(value.build())
fun setAnnotation(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Builder): Builder = setAnnotation(index, value.build())
var `annotationParameterDefaultValue`: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value
get() = field_8_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value.defaultInstance
set(value) { field_8_ = value; bitField0_ = bitField0_ or 64 }
fun getAnnotationParameterDefaultValue(): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value = `annotationParameterDefaultValue`
fun hasAnnotationParameterDefaultValue(): Boolean = (bitField0_ and 64) != 0
fun setAnnotationParameterDefaultValue(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value): Builder { field_8_ = value; bitField0_ = bitField0_ or 64; return this }
fun setAnnotationParameterDefaultValue(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value.Builder): Builder { field_8_ = value.build(); bitField0_ = bitField0_ or 64; return this }
fun mergeAnnotationParameterDefaultValue(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value): Builder {
val cur = field_8_
field_8_ = if ((bitField0_ and 64) != 0 && cur != null && cur !== org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value.defaultInstance) org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value.newBuilder(cur).mergeFrom(value).buildPartial() else value
bitField0_ = bitField0_ or 64
return this
}
fun clearAnnotationParameterDefaultValue(): Builder { field_8_ = null; bitField0_ = bitField0_ and -65; return this }
var `equalityBoundType`: org.jetbrains.kotlin.metadata.ProtoBuf.Type
get() = field_9_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance
set(value) { field_9_ = value; bitField0_ = bitField0_ or 128 }
fun getEqualityBoundType(): org.jetbrains.kotlin.metadata.ProtoBuf.Type = `equalityBoundType`
fun hasEqualityBoundType(): Boolean = (bitField0_ and 128) != 0
fun setEqualityBoundType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder { field_9_ = value; bitField0_ = bitField0_ or 128; return this }
fun setEqualityBoundType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder): Builder { field_9_ = value.build(); bitField0_ = bitField0_ or 128; return this }
fun mergeEqualityBoundType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder {
val cur = field_9_
field_9_ = if ((bitField0_ and 128) != 0 && cur != null && cur !== org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance) org.jetbrains.kotlin.metadata.ProtoBuf.Type.newBuilder(cur).mergeFrom(value).buildPartial() else value
bitField0_ = bitField0_ or 128
return this
}
fun clearEqualityBoundType(): Builder { field_9_ = null; bitField0_ = bitField0_ and -129; return this }
var `equalityBoundTypeId`: Int
get() = field_10_
set(value) { bitField0_ = bitField0_ or 256; field_10_ = value }
fun getEqualityBoundTypeId(): Int = field_10_
fun hasEqualityBoundTypeId(): Boolean = (bitField0_ and 256) != 0
fun setEqualityBoundTypeId(value: Int): Builder { bitField0_ = bitField0_ or 256; field_10_ = value; return this }
fun clearEqualityBoundTypeId(): Builder { bitField0_ = bitField0_ and -257; field_10_ = 0; return this }
}
companion object {
val defaultInstance: ValueParameter = ValueParameter()
fun getDefaultInstance(): ValueParameter = defaultInstance
fun newBuilder(): Builder = Builder()
fun newBuilder(prototype: ValueParameter): Builder = Builder().mergeFrom(prototype)
val PARSER: Parser<ValueParameter> = object : AbstractParser<ValueParameter>() {
override fun parsePartialFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): ValueParameter =
ValueParameter(input, extensionRegistry)
}
fun parseFrom(bytes: ByteArray): ValueParameter = PARSER.parseFrom(bytes)
fun parseFrom(bytes: ByteArray, registry: ExtensionRegistryLite): ValueParameter = PARSER.parseFrom(bytes, registry)
fun parseFrom(bytes: ByteString): ValueParameter { val input = bytes.newCodedInput(); val msg = PARSER.parsePartialFrom(input, ExtensionRegistryLite.getEmptyRegistry()); input.checkLastTagWas(0); if (!msg.isInitialized()) throw UninitializedMessageException().asInvalidProtocolBufferException().setUnfinishedMessage(msg); return msg }
fun parseFrom(input: CodedInputStream): ValueParameter = PARSER.parseFrom(input)
fun parseFrom(input: CodedInputStream, registry: ExtensionRegistryLite): ValueParameter = PARSER.parseFrom(input, registry)
const val FLAGS_FIELD_NUMBER = 1
const val NAME_FIELD_NUMBER = 2
const val TYPE_FIELD_NUMBER = 3
const val VARARG_ELEMENT_TYPE_FIELD_NUMBER = 4
const val TYPE_ID_FIELD_NUMBER = 5
const val VARARG_ELEMENT_TYPE_ID_FIELD_NUMBER = 6
const val ANNOTATION_FIELD_NUMBER = 7
const val ANNOTATION_PARAMETER_DEFAULT_VALUE_FIELD_NUMBER = 8
const val EQUALITY_BOUND_TYPE_FIELD_NUMBER = 9
const val EQUALITY_BOUND_TYPE_ID_FIELD_NUMBER = 10
}}
class TypeAlias : GeneratedMessageLite.ExtendableMessage<TypeAlias> {
internal var bitField0_: Int = 0
internal var field_1_: Int = 6
internal var field_2_: Int = 0
internal var field_4_: org.jetbrains.kotlin.metadata.ProtoBuf.Type? = null
internal var field_5_: Int = 0
internal var field_6_: org.jetbrains.kotlin.metadata.ProtoBuf.Type? = null
internal var field_7_: Int = 0
internal var field_3_: List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter> = emptyList()
internal var field_8_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = emptyList()
internal var field_31_: List<Int> = emptyList()
internal var field_32_: List<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData> = emptyList()
private var memoizedIsInitialized: Byte = -1
private var memoizedSerializedSize: Int = -1
private constructor()
private constructor(builder: Builder) {
this.unknownFields = builder.unknownFields
copyExtensionsFromBuilder(builder)
}
private constructor(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite) {
var mutBit0_ = 0
var unknownBuffer: ByteString.Output? = null
var unknownOut: CodedOutputStream? = null
fun unknownOutput(): CodedOutputStream {
var out = unknownOut
if (out == null) { val buf = ByteString.newOutput(); unknownBuffer = buf; out = CodedOutputStream.newInstance(buf); unknownOut = out }
return out
}
try {
var done = false
while (!done) {
val tag = input.readTag()
when (tag) {
0 -> done = true
8 -> { bitField0_ = bitField0_ or 1; field_1_ = input.readInt32() }
16 -> { bitField0_ = bitField0_ or 2; field_2_ = input.readInt32() }
26 -> {
if ((mutBit0_ and 1) == 0) { field_3_ = ArrayList(); mutBit0_ = mutBit0_ or 1 }
(field_3_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter.PARSER, extensionRegistry))
}
34 -> {
var subBuilder: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder? = null
if ((bitField0_ and 4) != 0) subBuilder = field_4_?.toBuilder()
val parsed = input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Type.PARSER, extensionRegistry)
field_4_ = if (subBuilder != null) subBuilder.mergeFrom(parsed).buildPartial() else parsed
bitField0_ = bitField0_ or 4
}
40 -> { bitField0_ = bitField0_ or 8; field_5_ = input.readInt32() }
50 -> {
var subBuilder: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder? = null
if ((bitField0_ and 16) != 0) subBuilder = field_6_?.toBuilder()
val parsed = input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Type.PARSER, extensionRegistry)
field_6_ = if (subBuilder != null) subBuilder.mergeFrom(parsed).buildPartial() else parsed
bitField0_ = bitField0_ or 16
}
56 -> { bitField0_ = bitField0_ or 32; field_7_ = input.readInt32() }
66 -> {
if ((mutBit0_ and 2) == 0) { field_8_ = ArrayList(); mutBit0_ = mutBit0_ or 2 }
(field_8_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.PARSER, extensionRegistry))
}
248 -> {
if ((mutBit0_ and 4) == 0) { field_31_ = ArrayList(); mutBit0_ = mutBit0_ or 4 }
(field_31_ as MutableList<Int>).add(input.readInt32())
}
250 -> {
val length = input.readRawVarint32()
val oldLimit = input.pushLimit(length)
try {
if (length > 0 && (mutBit0_ and 4) == 0) { field_31_ = ArrayList(); mutBit0_ = mutBit0_ or 4 }
while (input.getBytesUntilLimit() > 0) { (field_31_ as MutableList<Int>).add(input.readInt32()) }
} finally { input.popLimit(oldLimit) }
}
258 -> {
if ((mutBit0_ and 8) == 0) { field_32_ = ArrayList(); mutBit0_ = mutBit0_ or 8 }
(field_32_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData.PARSER, extensionRegistry))
}
else -> if (!parseUnknownField(input, unknownOutput(), extensionRegistry, tag)) done = true
}}
} catch (e: InvalidProtocolBufferException) {
throw e.setUnfinishedMessage(this)
} finally {
if ((mutBit0_ and 1) != 0) field_3_ = GeneratedMessageLite.unmodifiableProtoList(field_3_)
if ((mutBit0_ and 2) != 0) field_8_ = GeneratedMessageLite.unmodifiableProtoList(field_8_)
if ((mutBit0_ and 4) != 0) field_31_ = GeneratedMessageLite.unmodifiableProtoList(field_31_)
if ((mutBit0_ and 8) != 0) field_32_ = GeneratedMessageLite.unmodifiableProtoList(field_32_)
if (unknownBuffer != null) { unknownOut!!.flush(); unknownFields = unknownBuffer.toByteString() }
makeExtensionsImmutable()
}}
val `flags`: Int get() = field_1_
fun getFlags(): Int = `flags`
fun hasFlags(): Boolean = (bitField0_ and 1) != 0
val `name`: Int get() = field_2_
fun getName(): Int = `name`
fun hasName(): Boolean = (bitField0_ and 2) != 0
val `typeParameterList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter> get() = field_3_
val `typeParameterCount`: Int get() = field_3_.size
fun getTypeParameterList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter> = field_3_
fun getTypeParameterCount(): Int = field_3_.size
fun getTypeParameter(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter = field_3_[index]
val `underlyingType`: org.jetbrains.kotlin.metadata.ProtoBuf.Type get() = field_4_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance
fun getUnderlyingType(): org.jetbrains.kotlin.metadata.ProtoBuf.Type = `underlyingType`
fun hasUnderlyingType(): Boolean = (bitField0_ and 4) != 0
val `underlyingTypeId`: Int get() = field_5_
fun getUnderlyingTypeId(): Int = `underlyingTypeId`
fun hasUnderlyingTypeId(): Boolean = (bitField0_ and 8) != 0
val `expandedType`: org.jetbrains.kotlin.metadata.ProtoBuf.Type get() = field_6_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance
fun getExpandedType(): org.jetbrains.kotlin.metadata.ProtoBuf.Type = `expandedType`
fun hasExpandedType(): Boolean = (bitField0_ and 16) != 0
val `expandedTypeId`: Int get() = field_7_
fun getExpandedTypeId(): Int = `expandedTypeId`
fun hasExpandedTypeId(): Boolean = (bitField0_ and 32) != 0
val `annotationList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> get() = field_8_
val `annotationCount`: Int get() = field_8_.size
fun getAnnotationList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = field_8_
fun getAnnotationCount(): Int = field_8_.size
fun getAnnotation(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation = field_8_[index]
val `versionRequirementList`: List<Int> get() = field_31_
val `versionRequirementCount`: Int get() = field_31_.size
fun getVersionRequirementList(): List<Int> = field_31_
fun getVersionRequirementCount(): Int = field_31_.size
fun getVersionRequirement(index: Int): Int = field_31_[index]
val `compilerPluginDataList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData> get() = field_32_
val `compilerPluginDataCount`: Int get() = field_32_.size
fun getCompilerPluginDataList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData> = field_32_
fun getCompilerPluginDataCount(): Int = field_32_.size
fun getCompilerPluginData(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData = field_32_[index]
override fun isInitialized(): Boolean {
val isInitialized = memoizedIsInitialized.toInt()
if (isInitialized == 1) return true
if (isInitialized == 0) return false
if ((bitField0_ and 2) == 0) { memoizedIsInitialized = 0; return false }
if ((bitField0_ and 4) != 0 && !getUnderlyingType().isInitialized()) { memoizedIsInitialized = 0; return false }
if ((bitField0_ and 16) != 0 && !getExpandedType().isInitialized()) { memoizedIsInitialized = 0; return false }
for (i in 0 until field_3_.size) { if (!field_3_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
for (i in 0 until field_8_.size) { if (!field_8_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
for (i in 0 until field_32_.size) { if (!field_32_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
if (!extensionsAreInitialized()) { memoizedIsInitialized = 0; return false }
memoizedIsInitialized = 1
return true
}
override fun writeTo(output: CodedOutputStream) {
val extensionWriter = if (hasAnyExtensions()) newExtensionWriter() else null
extensionWriter?.writeUntil(1, output)
if ((bitField0_ and 1) != 0) { output.writeRawVarint32(8); output.writeInt32NoTag(field_1_) }
extensionWriter?.writeUntil(2, output)
if ((bitField0_ and 2) != 0) { output.writeRawVarint32(16); output.writeInt32NoTag(field_2_) }
extensionWriter?.writeUntil(3, output)
for (i in 0 until field_3_.size) { output.writeRawVarint32(26); output.writeMessageNoTag(field_3_[i]) }
extensionWriter?.writeUntil(4, output)
if ((bitField0_ and 4) != 0) { output.writeRawVarint32(34); output.writeMessageNoTag(getUnderlyingType()) }
extensionWriter?.writeUntil(5, output)
if ((bitField0_ and 8) != 0) { output.writeRawVarint32(40); output.writeInt32NoTag(field_5_) }
extensionWriter?.writeUntil(6, output)
if ((bitField0_ and 16) != 0) { output.writeRawVarint32(50); output.writeMessageNoTag(getExpandedType()) }
extensionWriter?.writeUntil(7, output)
if ((bitField0_ and 32) != 0) { output.writeRawVarint32(56); output.writeInt32NoTag(field_7_) }
extensionWriter?.writeUntil(8, output)
for (i in 0 until field_8_.size) { output.writeRawVarint32(66); output.writeMessageNoTag(field_8_[i]) }
extensionWriter?.writeUntil(31, output)
for (i in 0 until field_31_.size) { output.writeRawVarint32(248); output.writeInt32NoTag(field_31_[i]) }
extensionWriter?.writeUntil(32, output)
for (i in 0 until field_32_.size) { output.writeRawVarint32(258); output.writeMessageNoTag(field_32_[i]) }
extensionWriter?.writeUntil(Int.MAX_VALUE, output)
if (!unknownFields.isEmpty()) unknownFields.writeTo(output)
}
override val serializedSize: Int get() {
val cached = memoizedSerializedSize
if (cached != -1) return cached
var size = 0
if ((bitField0_ and 1) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_1_)
if ((bitField0_ and 2) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_2_)
if ((bitField0_ and 4) != 0) size += 1 + CodedOutputStream.computeMessageSizeNoTag(getUnderlyingType())
if ((bitField0_ and 8) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_5_)
if ((bitField0_ and 16) != 0) size += 1 + CodedOutputStream.computeMessageSizeNoTag(getExpandedType())
if ((bitField0_ and 32) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_7_)
for (i in 0 until field_3_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_3_[i]) }
for (i in 0 until field_8_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_8_[i]) }
for (i in 0 until field_31_.size) { size += 2 + CodedOutputStream.computeInt32SizeNoTag(field_31_[i]) }
for (i in 0 until field_32_.size) { size += 2 + CodedOutputStream.computeMessageSizeNoTag(field_32_[i]) }
size += extensionsSerializedSize()
size += unknownFields.size()
memoizedSerializedSize = size
return size
}
override val defaultInstanceForType: TypeAlias get() = defaultInstance
override fun newBuilderForType(): Builder = Builder()
override fun toBuilder(): Builder = Builder().mergeFrom(this)
class Builder : GeneratedMessageLite.ExtendableBuilder<TypeAlias, Builder>() {
private var bitField0_: Int = 0
private var mutBitField0_: Int = 0
private var field_1_: Int = 6
private var field_2_: Int = 0
private var field_4_: org.jetbrains.kotlin.metadata.ProtoBuf.Type? = null
private var field_5_: Int = 0
private var field_6_: org.jetbrains.kotlin.metadata.ProtoBuf.Type? = null
private var field_7_: Int = 0
private var field_3_: List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter> = emptyList()
private fun ensure_3_Mutable() { if ((mutBitField0_ and 1) == 0) { field_3_ = ArrayList(field_3_); mutBitField0_ = mutBitField0_ or 1 } }
private var field_8_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = emptyList()
private fun ensure_8_Mutable() { if ((mutBitField0_ and 2) == 0) { field_8_ = ArrayList(field_8_); mutBitField0_ = mutBitField0_ or 2 } }
private var field_31_: List<Int> = emptyList()
private fun ensure_31_Mutable() { if ((mutBitField0_ and 4) == 0) { field_31_ = ArrayList(field_31_); mutBitField0_ = mutBitField0_ or 4 } }
private var field_32_: List<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData> = emptyList()
private fun ensure_32_Mutable() { if ((mutBitField0_ and 8) == 0) { field_32_ = ArrayList(field_32_); mutBitField0_ = mutBitField0_ or 8 } }
override val defaultInstanceForType: TypeAlias get() = defaultInstance
override fun clear(): Builder {
bitField0_ = 0
mutBitField0_ = 0
field_1_ = 6
field_2_ = 0
field_4_ = null
field_5_ = 0
field_6_ = null
field_7_ = 0
field_3_ = emptyList()
field_8_ = emptyList()
field_31_ = emptyList()
field_32_ = emptyList()
clearExtensions()
unknownFields = ByteString.EMPTY
return this
}
override fun clone(): Builder = Builder().mergeFrom(buildPartial())
override fun build(): TypeAlias {
val result = buildPartial()
if (!result.isInitialized()) throw UninitializedMessageException()
return result
}
override fun buildPartial(): TypeAlias {
val result = TypeAlias(this)
val from_bitField0_ = bitField0_
var to_bitField0_ = 0
if ((from_bitField0_ and 1) != 0) to_bitField0_ = to_bitField0_ or 1
result.field_1_ = field_1_
if ((from_bitField0_ and 2) != 0) to_bitField0_ = to_bitField0_ or 2
result.field_2_ = field_2_
if ((from_bitField0_ and 4) != 0) to_bitField0_ = to_bitField0_ or 4
result.field_4_ = field_4_
if ((from_bitField0_ and 8) != 0) to_bitField0_ = to_bitField0_ or 8
result.field_5_ = field_5_
if ((from_bitField0_ and 16) != 0) to_bitField0_ = to_bitField0_ or 16
result.field_6_ = field_6_
if ((from_bitField0_ and 32) != 0) to_bitField0_ = to_bitField0_ or 32
result.field_7_ = field_7_
result.bitField0_ = to_bitField0_
if ((mutBitField0_ and 1) != 0) { field_3_ = GeneratedMessageLite.unmodifiableProtoList(field_3_); mutBitField0_ = mutBitField0_ and -2 }
result.field_3_ = field_3_
if ((mutBitField0_ and 2) != 0) { field_8_ = GeneratedMessageLite.unmodifiableProtoList(field_8_); mutBitField0_ = mutBitField0_ and -3 }
result.field_8_ = field_8_
if ((mutBitField0_ and 4) != 0) { field_31_ = GeneratedMessageLite.unmodifiableProtoList(field_31_); mutBitField0_ = mutBitField0_ and -5 }
result.field_31_ = field_31_
if ((mutBitField0_ and 8) != 0) { field_32_ = GeneratedMessageLite.unmodifiableProtoList(field_32_); mutBitField0_ = mutBitField0_ and -9 }
result.field_32_ = field_32_
return result
}
override fun mergeFrom(other: TypeAlias): Builder {
if (other === defaultInstance) return this
if (other.hasFlags()) setFlags(other.getFlags())
if (other.hasName()) setName(other.getName())
if (other.hasUnderlyingType()) mergeUnderlyingType(other.getUnderlyingType())
if (other.hasUnderlyingTypeId()) setUnderlyingTypeId(other.getUnderlyingTypeId())
if (other.hasExpandedType()) mergeExpandedType(other.getExpandedType())
if (other.hasExpandedTypeId()) setExpandedTypeId(other.getExpandedTypeId())
if (other.field_3_.isNotEmpty()) {
if (field_3_.isEmpty()) { field_3_ = other.field_3_; mutBitField0_ = mutBitField0_ and -2 } else { ensure_3_Mutable(); (field_3_ as MutableList<Any>).addAll(other.field_3_ as Collection<Any>) }
}
if (other.field_8_.isNotEmpty()) {
if (field_8_.isEmpty()) { field_8_ = other.field_8_; mutBitField0_ = mutBitField0_ and -3 } else { ensure_8_Mutable(); (field_8_ as MutableList<Any>).addAll(other.field_8_ as Collection<Any>) }
}
if (other.field_31_.isNotEmpty()) {
if (field_31_.isEmpty()) { field_31_ = other.field_31_; mutBitField0_ = mutBitField0_ and -5 } else { ensure_31_Mutable(); (field_31_ as MutableList<Any>).addAll(other.field_31_ as Collection<Any>) }
}
if (other.field_32_.isNotEmpty()) {
if (field_32_.isEmpty()) { field_32_ = other.field_32_; mutBitField0_ = mutBitField0_ and -9 } else { ensure_32_Mutable(); (field_32_ as MutableList<Any>).addAll(other.field_32_ as Collection<Any>) }
}
mergeExtensionFields(other)
unknownFields = unknownFields.concat(other.unknownFields)
return this
}
override fun isInitialized(): Boolean {
if ((bitField0_ and 2) == 0) return false
if ((bitField0_ and 4) != 0 && !getUnderlyingType().isInitialized()) return false
if ((bitField0_ and 16) != 0 && !getExpandedType().isInitialized()) return false
for (i in 0 until field_3_.size) { if (!field_3_[i].isInitialized()) return false }
for (i in 0 until field_8_.size) { if (!field_8_[i].isInitialized()) return false }
for (i in 0 until field_32_.size) { if (!field_32_[i].isInitialized()) return false }
if (!extensionsAreInitialized()) return false
return true
}
override fun mergeFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Builder {
var parsedMessage: TypeAlias? = null
try {
parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry)
} catch (e: InvalidProtocolBufferException) {
parsedMessage = e.unfinishedMessage as? TypeAlias
throw e
} finally {
if (parsedMessage != null) mergeFrom(parsedMessage)
}
return this
}
var `flags`: Int
get() = field_1_
set(value) { bitField0_ = bitField0_ or 1; field_1_ = value }
fun getFlags(): Int = field_1_
fun hasFlags(): Boolean = (bitField0_ and 1) != 0
fun setFlags(value: Int): Builder { bitField0_ = bitField0_ or 1; field_1_ = value; return this }
fun clearFlags(): Builder { bitField0_ = bitField0_ and -2; field_1_ = 6; return this }
var `name`: Int
get() = field_2_
set(value) { bitField0_ = bitField0_ or 2; field_2_ = value }
fun getName(): Int = field_2_
fun hasName(): Boolean = (bitField0_ and 2) != 0
fun setName(value: Int): Builder { bitField0_ = bitField0_ or 2; field_2_ = value; return this }
fun clearName(): Builder { bitField0_ = bitField0_ and -3; field_2_ = 0; return this }
val `typeParameterList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter> get() = if (field_3_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_3_)
val `typeParameterCount`: Int get() = field_3_.size
fun getTypeParameterList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter> = `typeParameterList`
fun getTypeParameterCount(): Int = field_3_.size
fun getTypeParameter(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter = field_3_[index]
fun addTypeParameter(value: org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter): Builder { ensure_3_Mutable(); (field_3_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter>).add(value); return this }
fun setTypeParameter(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter): Builder { ensure_3_Mutable(); (field_3_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter>)[index] = value; return this }
fun addAllTypeParameter(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter>): Builder { ensure_3_Mutable(); AbstractMessageLite.Builder.addAll(values, field_3_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter>); return this }
fun clearTypeParameter(): Builder { field_3_ = emptyList(); mutBitField0_ = mutBitField0_ and -2; return this }
fun addTypeParameter(value: org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter.Builder): Builder = addTypeParameter(value.build())
fun setTypeParameter(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter.Builder): Builder = setTypeParameter(index, value.build())
var `underlyingType`: org.jetbrains.kotlin.metadata.ProtoBuf.Type
get() = field_4_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance
set(value) { field_4_ = value; bitField0_ = bitField0_ or 4 }
fun getUnderlyingType(): org.jetbrains.kotlin.metadata.ProtoBuf.Type = `underlyingType`
fun hasUnderlyingType(): Boolean = (bitField0_ and 4) != 0
fun setUnderlyingType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder { field_4_ = value; bitField0_ = bitField0_ or 4; return this }
fun setUnderlyingType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder): Builder { field_4_ = value.build(); bitField0_ = bitField0_ or 4; return this }
fun mergeUnderlyingType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder {
val cur = field_4_
field_4_ = if ((bitField0_ and 4) != 0 && cur != null && cur !== org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance) org.jetbrains.kotlin.metadata.ProtoBuf.Type.newBuilder(cur).mergeFrom(value).buildPartial() else value
bitField0_ = bitField0_ or 4
return this
}
fun clearUnderlyingType(): Builder { field_4_ = null; bitField0_ = bitField0_ and -5; return this }
var `underlyingTypeId`: Int
get() = field_5_
set(value) { bitField0_ = bitField0_ or 8; field_5_ = value }
fun getUnderlyingTypeId(): Int = field_5_
fun hasUnderlyingTypeId(): Boolean = (bitField0_ and 8) != 0
fun setUnderlyingTypeId(value: Int): Builder { bitField0_ = bitField0_ or 8; field_5_ = value; return this }
fun clearUnderlyingTypeId(): Builder { bitField0_ = bitField0_ and -9; field_5_ = 0; return this }
var `expandedType`: org.jetbrains.kotlin.metadata.ProtoBuf.Type
get() = field_6_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance
set(value) { field_6_ = value; bitField0_ = bitField0_ or 16 }
fun getExpandedType(): org.jetbrains.kotlin.metadata.ProtoBuf.Type = `expandedType`
fun hasExpandedType(): Boolean = (bitField0_ and 16) != 0
fun setExpandedType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder { field_6_ = value; bitField0_ = bitField0_ or 16; return this }
fun setExpandedType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder): Builder { field_6_ = value.build(); bitField0_ = bitField0_ or 16; return this }
fun mergeExpandedType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder {
val cur = field_6_
field_6_ = if ((bitField0_ and 16) != 0 && cur != null && cur !== org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance) org.jetbrains.kotlin.metadata.ProtoBuf.Type.newBuilder(cur).mergeFrom(value).buildPartial() else value
bitField0_ = bitField0_ or 16
return this
}
fun clearExpandedType(): Builder { field_6_ = null; bitField0_ = bitField0_ and -17; return this }
var `expandedTypeId`: Int
get() = field_7_
set(value) { bitField0_ = bitField0_ or 32; field_7_ = value }
fun getExpandedTypeId(): Int = field_7_
fun hasExpandedTypeId(): Boolean = (bitField0_ and 32) != 0
fun setExpandedTypeId(value: Int): Builder { bitField0_ = bitField0_ or 32; field_7_ = value; return this }
fun clearExpandedTypeId(): Builder { bitField0_ = bitField0_ and -33; field_7_ = 0; return this }
val `annotationList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> get() = if (field_8_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_8_)
val `annotationCount`: Int get() = field_8_.size
fun getAnnotationList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = `annotationList`
fun getAnnotationCount(): Int = field_8_.size
fun getAnnotation(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation = field_8_[index]
fun addAnnotation(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation): Builder { ensure_8_Mutable(); (field_8_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>).add(value); return this }
fun setAnnotation(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation): Builder { ensure_8_Mutable(); (field_8_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>)[index] = value; return this }
fun addAllAnnotation(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>): Builder { ensure_8_Mutable(); AbstractMessageLite.Builder.addAll(values, field_8_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>); return this }
fun clearAnnotation(): Builder { field_8_ = emptyList(); mutBitField0_ = mutBitField0_ and -3; return this }
fun addAnnotation(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Builder): Builder = addAnnotation(value.build())
fun setAnnotation(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Builder): Builder = setAnnotation(index, value.build())
val `versionRequirementList`: List<Int> get() = if (field_31_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_31_)
val `versionRequirementCount`: Int get() = field_31_.size
fun getVersionRequirementList(): List<Int> = `versionRequirementList`
fun getVersionRequirementCount(): Int = field_31_.size
fun getVersionRequirement(index: Int): Int = field_31_[index]
fun addVersionRequirement(value: Int): Builder { ensure_31_Mutable(); (field_31_ as MutableList<Int>).add(value); return this }
fun setVersionRequirement(index: Int, value: Int): Builder { ensure_31_Mutable(); (field_31_ as MutableList<Int>)[index] = value; return this }
fun addAllVersionRequirement(values: Iterable<Int>): Builder { ensure_31_Mutable(); AbstractMessageLite.Builder.addAll(values, field_31_ as MutableList<Int>); return this }
fun clearVersionRequirement(): Builder { field_31_ = emptyList(); mutBitField0_ = mutBitField0_ and -5; return this }
val `compilerPluginDataList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData> get() = if (field_32_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_32_)
val `compilerPluginDataCount`: Int get() = field_32_.size
fun getCompilerPluginDataList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData> = `compilerPluginDataList`
fun getCompilerPluginDataCount(): Int = field_32_.size
fun getCompilerPluginData(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData = field_32_[index]
fun addCompilerPluginData(value: org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData): Builder { ensure_32_Mutable(); (field_32_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData>).add(value); return this }
fun setCompilerPluginData(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData): Builder { ensure_32_Mutable(); (field_32_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData>)[index] = value; return this }
fun addAllCompilerPluginData(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData>): Builder { ensure_32_Mutable(); AbstractMessageLite.Builder.addAll(values, field_32_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData>); return this }
fun clearCompilerPluginData(): Builder { field_32_ = emptyList(); mutBitField0_ = mutBitField0_ and -9; return this }
fun addCompilerPluginData(value: org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData.Builder): Builder = addCompilerPluginData(value.build())
fun setCompilerPluginData(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.CompilerPluginData.Builder): Builder = setCompilerPluginData(index, value.build())
}
companion object {
val defaultInstance: TypeAlias = TypeAlias()
fun getDefaultInstance(): TypeAlias = defaultInstance
fun newBuilder(): Builder = Builder()
fun newBuilder(prototype: TypeAlias): Builder = Builder().mergeFrom(prototype)
val PARSER: Parser<TypeAlias> = object : AbstractParser<TypeAlias>() {
override fun parsePartialFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): TypeAlias =
TypeAlias(input, extensionRegistry)
}
fun parseFrom(bytes: ByteArray): TypeAlias = PARSER.parseFrom(bytes)
fun parseFrom(bytes: ByteArray, registry: ExtensionRegistryLite): TypeAlias = PARSER.parseFrom(bytes, registry)
fun parseFrom(bytes: ByteString): TypeAlias { val input = bytes.newCodedInput(); val msg = PARSER.parsePartialFrom(input, ExtensionRegistryLite.getEmptyRegistry()); input.checkLastTagWas(0); if (!msg.isInitialized()) throw UninitializedMessageException().asInvalidProtocolBufferException().setUnfinishedMessage(msg); return msg }
fun parseFrom(input: CodedInputStream): TypeAlias = PARSER.parseFrom(input)
fun parseFrom(input: CodedInputStream, registry: ExtensionRegistryLite): TypeAlias = PARSER.parseFrom(input, registry)
const val FLAGS_FIELD_NUMBER = 1
const val NAME_FIELD_NUMBER = 2
const val TYPE_PARAMETER_FIELD_NUMBER = 3
const val UNDERLYING_TYPE_FIELD_NUMBER = 4
const val UNDERLYING_TYPE_ID_FIELD_NUMBER = 5
const val EXPANDED_TYPE_FIELD_NUMBER = 6
const val EXPANDED_TYPE_ID_FIELD_NUMBER = 7
const val ANNOTATION_FIELD_NUMBER = 8
const val VERSION_REQUIREMENT_FIELD_NUMBER = 31
const val COMPILER_PLUGIN_DATA_FIELD_NUMBER = 32
}}
class EnumEntry : GeneratedMessageLite.ExtendableMessage<EnumEntry> {
internal var bitField0_: Int = 0
internal var field_1_: Int = 0
internal var field_2_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = emptyList()
private var memoizedIsInitialized: Byte = -1
private var memoizedSerializedSize: Int = -1
private constructor()
private constructor(builder: Builder) {
this.unknownFields = builder.unknownFields
copyExtensionsFromBuilder(builder)
}
private constructor(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite) {
var mutBit0_ = 0
var unknownBuffer: ByteString.Output? = null
var unknownOut: CodedOutputStream? = null
fun unknownOutput(): CodedOutputStream {
var out = unknownOut
if (out == null) { val buf = ByteString.newOutput(); unknownBuffer = buf; out = CodedOutputStream.newInstance(buf); unknownOut = out }
return out
}
try {
var done = false
while (!done) {
val tag = input.readTag()
when (tag) {
0 -> done = true
8 -> { bitField0_ = bitField0_ or 1; field_1_ = input.readInt32() }
18 -> {
if ((mutBit0_ and 1) == 0) { field_2_ = ArrayList(); mutBit0_ = mutBit0_ or 1 }
(field_2_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.PARSER, extensionRegistry))
}
else -> if (!parseUnknownField(input, unknownOutput(), extensionRegistry, tag)) done = true
}}
} catch (e: InvalidProtocolBufferException) {
throw e.setUnfinishedMessage(this)
} finally {
if ((mutBit0_ and 1) != 0) field_2_ = GeneratedMessageLite.unmodifiableProtoList(field_2_)
if (unknownBuffer != null) { unknownOut!!.flush(); unknownFields = unknownBuffer.toByteString() }
makeExtensionsImmutable()
}}
val `name`: Int get() = field_1_
fun getName(): Int = `name`
fun hasName(): Boolean = (bitField0_ and 1) != 0
val `annotationList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> get() = field_2_
val `annotationCount`: Int get() = field_2_.size
fun getAnnotationList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = field_2_
fun getAnnotationCount(): Int = field_2_.size
fun getAnnotation(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation = field_2_[index]
override fun isInitialized(): Boolean {
val isInitialized = memoizedIsInitialized.toInt()
if (isInitialized == 1) return true
if (isInitialized == 0) return false
for (i in 0 until field_2_.size) { if (!field_2_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
if (!extensionsAreInitialized()) { memoizedIsInitialized = 0; return false }
memoizedIsInitialized = 1
return true
}
override fun writeTo(output: CodedOutputStream) {
val extensionWriter = if (hasAnyExtensions()) newExtensionWriter() else null
extensionWriter?.writeUntil(1, output)
if ((bitField0_ and 1) != 0) { output.writeRawVarint32(8); output.writeInt32NoTag(field_1_) }
extensionWriter?.writeUntil(2, output)
for (i in 0 until field_2_.size) { output.writeRawVarint32(18); output.writeMessageNoTag(field_2_[i]) }
extensionWriter?.writeUntil(Int.MAX_VALUE, output)
if (!unknownFields.isEmpty()) unknownFields.writeTo(output)
}
override val serializedSize: Int get() {
val cached = memoizedSerializedSize
if (cached != -1) return cached
var size = 0
if ((bitField0_ and 1) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_1_)
for (i in 0 until field_2_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_2_[i]) }
size += extensionsSerializedSize()
size += unknownFields.size()
memoizedSerializedSize = size
return size
}
override val defaultInstanceForType: EnumEntry get() = defaultInstance
override fun newBuilderForType(): Builder = Builder()
override fun toBuilder(): Builder = Builder().mergeFrom(this)
class Builder : GeneratedMessageLite.ExtendableBuilder<EnumEntry, Builder>() {
private var bitField0_: Int = 0
private var mutBitField0_: Int = 0
private var field_1_: Int = 0
private var field_2_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = emptyList()
private fun ensure_2_Mutable() { if ((mutBitField0_ and 1) == 0) { field_2_ = ArrayList(field_2_); mutBitField0_ = mutBitField0_ or 1 } }
override val defaultInstanceForType: EnumEntry get() = defaultInstance
override fun clear(): Builder {
bitField0_ = 0
mutBitField0_ = 0
field_1_ = 0
field_2_ = emptyList()
clearExtensions()
unknownFields = ByteString.EMPTY
return this
}
override fun clone(): Builder = Builder().mergeFrom(buildPartial())
override fun build(): EnumEntry {
val result = buildPartial()
if (!result.isInitialized()) throw UninitializedMessageException()
return result
}
override fun buildPartial(): EnumEntry {
val result = EnumEntry(this)
val from_bitField0_ = bitField0_
var to_bitField0_ = 0
if ((from_bitField0_ and 1) != 0) to_bitField0_ = to_bitField0_ or 1
result.field_1_ = field_1_
result.bitField0_ = to_bitField0_
if ((mutBitField0_ and 1) != 0) { field_2_ = GeneratedMessageLite.unmodifiableProtoList(field_2_); mutBitField0_ = mutBitField0_ and -2 }
result.field_2_ = field_2_
return result
}
override fun mergeFrom(other: EnumEntry): Builder {
if (other === defaultInstance) return this
if (other.hasName()) setName(other.getName())
if (other.field_2_.isNotEmpty()) {
if (field_2_.isEmpty()) { field_2_ = other.field_2_; mutBitField0_ = mutBitField0_ and -2 } else { ensure_2_Mutable(); (field_2_ as MutableList<Any>).addAll(other.field_2_ as Collection<Any>) }
}
mergeExtensionFields(other)
unknownFields = unknownFields.concat(other.unknownFields)
return this
}
override fun isInitialized(): Boolean {
for (i in 0 until field_2_.size) { if (!field_2_[i].isInitialized()) return false }
if (!extensionsAreInitialized()) return false
return true
}
override fun mergeFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Builder {
var parsedMessage: EnumEntry? = null
try {
parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry)
} catch (e: InvalidProtocolBufferException) {
parsedMessage = e.unfinishedMessage as? EnumEntry
throw e
} finally {
if (parsedMessage != null) mergeFrom(parsedMessage)
}
return this
}
var `name`: Int
get() = field_1_
set(value) { bitField0_ = bitField0_ or 1; field_1_ = value }
fun getName(): Int = field_1_
fun hasName(): Boolean = (bitField0_ and 1) != 0
fun setName(value: Int): Builder { bitField0_ = bitField0_ or 1; field_1_ = value; return this }
fun clearName(): Builder { bitField0_ = bitField0_ and -2; field_1_ = 0; return this }
val `annotationList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> get() = if (field_2_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_2_)
val `annotationCount`: Int get() = field_2_.size
fun getAnnotationList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = `annotationList`
fun getAnnotationCount(): Int = field_2_.size
fun getAnnotation(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation = field_2_[index]
fun addAnnotation(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation): Builder { ensure_2_Mutable(); (field_2_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>).add(value); return this }
fun setAnnotation(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation): Builder { ensure_2_Mutable(); (field_2_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>)[index] = value; return this }
fun addAllAnnotation(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>): Builder { ensure_2_Mutable(); AbstractMessageLite.Builder.addAll(values, field_2_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>); return this }
fun clearAnnotation(): Builder { field_2_ = emptyList(); mutBitField0_ = mutBitField0_ and -2; return this }
fun addAnnotation(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Builder): Builder = addAnnotation(value.build())
fun setAnnotation(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Builder): Builder = setAnnotation(index, value.build())
}
companion object {
val defaultInstance: EnumEntry = EnumEntry()
fun getDefaultInstance(): EnumEntry = defaultInstance
fun newBuilder(): Builder = Builder()
fun newBuilder(prototype: EnumEntry): Builder = Builder().mergeFrom(prototype)
val PARSER: Parser<EnumEntry> = object : AbstractParser<EnumEntry>() {
override fun parsePartialFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): EnumEntry =
EnumEntry(input, extensionRegistry)
}
fun parseFrom(bytes: ByteArray): EnumEntry = PARSER.parseFrom(bytes)
fun parseFrom(bytes: ByteArray, registry: ExtensionRegistryLite): EnumEntry = PARSER.parseFrom(bytes, registry)
fun parseFrom(bytes: ByteString): EnumEntry { val input = bytes.newCodedInput(); val msg = PARSER.parsePartialFrom(input, ExtensionRegistryLite.getEmptyRegistry()); input.checkLastTagWas(0); if (!msg.isInitialized()) throw UninitializedMessageException().asInvalidProtocolBufferException().setUnfinishedMessage(msg); return msg }
fun parseFrom(input: CodedInputStream): EnumEntry = PARSER.parseFrom(input)
fun parseFrom(input: CodedInputStream, registry: ExtensionRegistryLite): EnumEntry = PARSER.parseFrom(input, registry)
const val NAME_FIELD_NUMBER = 1
const val ANNOTATION_FIELD_NUMBER = 2
}}
class VersionRequirement : GeneratedMessageLite.ExtendableMessage<VersionRequirement> {
enum class Level(override val number: Int) : Internal.EnumLite {
WARNING(0),
ERROR(1),
HIDDEN(2),
; companion object : Internal.EnumLiteMap<Level> {
override fun findValueByNumber(number: Int): Level? = valueOf(number)
fun valueOf(number: Int): Level? = when (number) {
0 -> WARNING
1 -> ERROR
2 -> HIDDEN
else -> null
}
fun internalGetValueMap(): Internal.EnumLiteMap<Level> = this
}}
enum class VersionKind(override val number: Int) : Internal.EnumLite {
LANGUAGE_VERSION(0),
COMPILER_VERSION(1),
API_VERSION(2),
; companion object : Internal.EnumLiteMap<VersionKind> {
override fun findValueByNumber(number: Int): VersionKind? = valueOf(number)
fun valueOf(number: Int): VersionKind? = when (number) {
0 -> LANGUAGE_VERSION
1 -> COMPILER_VERSION
2 -> API_VERSION
else -> null
}
fun internalGetValueMap(): Internal.EnumLiteMap<VersionKind> = this
}}
internal var bitField0_: Int = 0
internal var field_1_: Int = 0
internal var field_2_: Int = 0
internal var field_3_: org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement.Level = org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement.Level.ERROR
internal var field_4_: Int = 0
internal var field_5_: Int = 0
internal var field_6_: org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement.VersionKind = org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement.VersionKind.LANGUAGE_VERSION
private var memoizedSerializedSize: Int = -1
private constructor()
private constructor(builder: Builder) {
this.unknownFields = builder.unknownFields
}
private constructor(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite) {
var unknownBuffer: ByteString.Output? = null
var unknownOut: CodedOutputStream? = null
fun unknownOutput(): CodedOutputStream {
var out = unknownOut
if (out == null) { val buf = ByteString.newOutput(); unknownBuffer = buf; out = CodedOutputStream.newInstance(buf); unknownOut = out }
return out
}
try {
var done = false
while (!done) {
val tag = input.readTag()
when (tag) {
0 -> done = true
8 -> { bitField0_ = bitField0_ or 1; field_1_ = input.readInt32() }
16 -> { bitField0_ = bitField0_ or 2; field_2_ = input.readInt32() }
24 -> {
val raw = input.readEnum()
val parsed = org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement.Level.valueOf(raw)
if (parsed == null) { unknownOutput().writeRawVarint32(24); unknownOutput().writeRawVarint32(raw) } else {
bitField0_ = bitField0_ or 4; field_3_ = parsed
}}
32 -> { bitField0_ = bitField0_ or 8; field_4_ = input.readInt32() }
40 -> { bitField0_ = bitField0_ or 16; field_5_ = input.readInt32() }
48 -> {
val raw = input.readEnum()
val parsed = org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement.VersionKind.valueOf(raw)
if (parsed == null) { unknownOutput().writeRawVarint32(48); unknownOutput().writeRawVarint32(raw) } else {
bitField0_ = bitField0_ or 32; field_6_ = parsed
}}
else -> if (!input.skipField(tag, unknownOutput())) done = true
}}
} catch (e: InvalidProtocolBufferException) {
throw e.setUnfinishedMessage(this)
} finally {
if (unknownBuffer != null) { unknownOut!!.flush(); unknownFields = unknownBuffer.toByteString() }
}}
val `version`: Int get() = field_1_
fun getVersion(): Int = `version`
fun hasVersion(): Boolean = (bitField0_ and 1) != 0
val `versionFull`: Int get() = field_2_
fun getVersionFull(): Int = `versionFull`
fun hasVersionFull(): Boolean = (bitField0_ and 2) != 0
val `level`: org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement.Level get() = field_3_
fun getLevel(): org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement.Level = `level`
fun hasLevel(): Boolean = (bitField0_ and 4) != 0
val `errorCode`: Int get() = field_4_
fun getErrorCode(): Int = `errorCode`
fun hasErrorCode(): Boolean = (bitField0_ and 8) != 0
val `message`: Int get() = field_5_
fun getMessage(): Int = `message`
fun hasMessage(): Boolean = (bitField0_ and 16) != 0
val `versionKind`: org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement.VersionKind get() = field_6_
fun getVersionKind(): org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement.VersionKind = `versionKind`
fun hasVersionKind(): Boolean = (bitField0_ and 32) != 0
override fun isInitialized(): Boolean = true
override fun writeTo(output: CodedOutputStream) {
if ((bitField0_ and 1) != 0) { output.writeRawVarint32(8); output.writeInt32NoTag(field_1_) }
if ((bitField0_ and 2) != 0) { output.writeRawVarint32(16); output.writeInt32NoTag(field_2_) }
if ((bitField0_ and 4) != 0) { output.writeRawVarint32(24); output.writeInt32NoTag((field_3_).number) }
if ((bitField0_ and 8) != 0) { output.writeRawVarint32(32); output.writeInt32NoTag(field_4_) }
if ((bitField0_ and 16) != 0) { output.writeRawVarint32(40); output.writeInt32NoTag(field_5_) }
if ((bitField0_ and 32) != 0) { output.writeRawVarint32(48); output.writeInt32NoTag((field_6_).number) }
if (!unknownFields.isEmpty()) unknownFields.writeTo(output)
}
override val serializedSize: Int get() {
val cached = memoizedSerializedSize
if (cached != -1) return cached
var size = 0
if ((bitField0_ and 1) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_1_)
if ((bitField0_ and 2) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_2_)
if ((bitField0_ and 4) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag((field_3_).number)
if ((bitField0_ and 8) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_4_)
if ((bitField0_ and 16) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_5_)
if ((bitField0_ and 32) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag((field_6_).number)
size += unknownFields.size()
memoizedSerializedSize = size
return size
}
override val defaultInstanceForType: VersionRequirement get() = defaultInstance
override fun newBuilderForType(): Builder = Builder()
override fun toBuilder(): Builder = Builder().mergeFrom(this)
class Builder : GeneratedMessageLite.ExtendableBuilder<VersionRequirement, Builder>() {
private var bitField0_: Int = 0
private var field_1_: Int = 0
private var field_2_: Int = 0
private var field_3_: org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement.Level = org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement.Level.ERROR
private var field_4_: Int = 0
private var field_5_: Int = 0
private var field_6_: org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement.VersionKind = org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement.VersionKind.LANGUAGE_VERSION
override val defaultInstanceForType: VersionRequirement get() = defaultInstance
override fun clear(): Builder {
bitField0_ = 0
field_1_ = 0
field_2_ = 0
field_3_ = org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement.Level.ERROR
field_4_ = 0
field_5_ = 0
field_6_ = org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement.VersionKind.LANGUAGE_VERSION
unknownFields = ByteString.EMPTY
return this
}
override fun clone(): Builder = Builder().mergeFrom(buildPartial())
override fun build(): VersionRequirement {
val result = buildPartial()
return result
}
override fun buildPartial(): VersionRequirement {
val result = VersionRequirement(this)
val from_bitField0_ = bitField0_
var to_bitField0_ = 0
if ((from_bitField0_ and 1) != 0) to_bitField0_ = to_bitField0_ or 1
result.field_1_ = field_1_
if ((from_bitField0_ and 2) != 0) to_bitField0_ = to_bitField0_ or 2
result.field_2_ = field_2_
if ((from_bitField0_ and 4) != 0) to_bitField0_ = to_bitField0_ or 4
result.field_3_ = field_3_
if ((from_bitField0_ and 8) != 0) to_bitField0_ = to_bitField0_ or 8
result.field_4_ = field_4_
if ((from_bitField0_ and 16) != 0) to_bitField0_ = to_bitField0_ or 16
result.field_5_ = field_5_
if ((from_bitField0_ and 32) != 0) to_bitField0_ = to_bitField0_ or 32
result.field_6_ = field_6_
result.bitField0_ = to_bitField0_
return result
}
override fun mergeFrom(other: VersionRequirement): Builder {
if (other === defaultInstance) return this
if (other.hasVersion()) setVersion(other.getVersion())
if (other.hasVersionFull()) setVersionFull(other.getVersionFull())
if (other.hasLevel()) setLevel(other.getLevel())
if (other.hasErrorCode()) setErrorCode(other.getErrorCode())
if (other.hasMessage()) setMessage(other.getMessage())
if (other.hasVersionKind()) setVersionKind(other.getVersionKind())
unknownFields = unknownFields.concat(other.unknownFields)
return this
}
override fun isInitialized(): Boolean = true
override fun mergeFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Builder {
var parsedMessage: VersionRequirement? = null
try {
parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry)
} catch (e: InvalidProtocolBufferException) {
parsedMessage = e.unfinishedMessage as? VersionRequirement
throw e
} finally {
if (parsedMessage != null) mergeFrom(parsedMessage)
}
return this
}
var `version`: Int
get() = field_1_
set(value) { bitField0_ = bitField0_ or 1; field_1_ = value }
fun getVersion(): Int = field_1_
fun hasVersion(): Boolean = (bitField0_ and 1) != 0
fun setVersion(value: Int): Builder { bitField0_ = bitField0_ or 1; field_1_ = value; return this }
fun clearVersion(): Builder { bitField0_ = bitField0_ and -2; field_1_ = 0; return this }
var `versionFull`: Int
get() = field_2_
set(value) { bitField0_ = bitField0_ or 2; field_2_ = value }
fun getVersionFull(): Int = field_2_
fun hasVersionFull(): Boolean = (bitField0_ and 2) != 0
fun setVersionFull(value: Int): Builder { bitField0_ = bitField0_ or 2; field_2_ = value; return this }
fun clearVersionFull(): Builder { bitField0_ = bitField0_ and -3; field_2_ = 0; return this }
var `level`: org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement.Level
get() = field_3_
set(value) { bitField0_ = bitField0_ or 4; field_3_ = value }
fun getLevel(): org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement.Level = field_3_
fun hasLevel(): Boolean = (bitField0_ and 4) != 0
fun setLevel(value: org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement.Level): Builder { bitField0_ = bitField0_ or 4; field_3_ = value; return this }
fun clearLevel(): Builder { bitField0_ = bitField0_ and -5; field_3_ = org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement.Level.ERROR; return this }
var `errorCode`: Int
get() = field_4_
set(value) { bitField0_ = bitField0_ or 8; field_4_ = value }
fun getErrorCode(): Int = field_4_
fun hasErrorCode(): Boolean = (bitField0_ and 8) != 0
fun setErrorCode(value: Int): Builder { bitField0_ = bitField0_ or 8; field_4_ = value; return this }
fun clearErrorCode(): Builder { bitField0_ = bitField0_ and -9; field_4_ = 0; return this }
var `message`: Int
get() = field_5_
set(value) { bitField0_ = bitField0_ or 16; field_5_ = value }
fun getMessage(): Int = field_5_
fun hasMessage(): Boolean = (bitField0_ and 16) != 0
fun setMessage(value: Int): Builder { bitField0_ = bitField0_ or 16; field_5_ = value; return this }
fun clearMessage(): Builder { bitField0_ = bitField0_ and -17; field_5_ = 0; return this }
var `versionKind`: org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement.VersionKind
get() = field_6_
set(value) { bitField0_ = bitField0_ or 32; field_6_ = value }
fun getVersionKind(): org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement.VersionKind = field_6_
fun hasVersionKind(): Boolean = (bitField0_ and 32) != 0
fun setVersionKind(value: org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement.VersionKind): Builder { bitField0_ = bitField0_ or 32; field_6_ = value; return this }
fun clearVersionKind(): Builder { bitField0_ = bitField0_ and -33; field_6_ = org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement.VersionKind.LANGUAGE_VERSION; return this }
}
companion object {
val defaultInstance: VersionRequirement = VersionRequirement()
fun getDefaultInstance(): VersionRequirement = defaultInstance
fun newBuilder(): Builder = Builder()
fun newBuilder(prototype: VersionRequirement): Builder = Builder().mergeFrom(prototype)
val PARSER: Parser<VersionRequirement> = object : AbstractParser<VersionRequirement>() {
override fun parsePartialFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): VersionRequirement =
VersionRequirement(input, extensionRegistry)
}
fun parseFrom(bytes: ByteArray): VersionRequirement = PARSER.parseFrom(bytes)
fun parseFrom(bytes: ByteArray, registry: ExtensionRegistryLite): VersionRequirement = PARSER.parseFrom(bytes, registry)
fun parseFrom(bytes: ByteString): VersionRequirement { val input = bytes.newCodedInput(); val msg = PARSER.parsePartialFrom(input, ExtensionRegistryLite.getEmptyRegistry()); input.checkLastTagWas(0); if (!msg.isInitialized()) throw UninitializedMessageException().asInvalidProtocolBufferException().setUnfinishedMessage(msg); return msg }
fun parseFrom(input: CodedInputStream): VersionRequirement = PARSER.parseFrom(input)
fun parseFrom(input: CodedInputStream, registry: ExtensionRegistryLite): VersionRequirement = PARSER.parseFrom(input, registry)
const val VERSION_FIELD_NUMBER = 1
const val VERSION_FULL_FIELD_NUMBER = 2
const val LEVEL_FIELD_NUMBER = 3
const val ERROR_CODE_FIELD_NUMBER = 4
const val MESSAGE_FIELD_NUMBER = 5
const val VERSION_KIND_FIELD_NUMBER = 6
}}
class VersionRequirementTable : GeneratedMessageLite.ExtendableMessage<VersionRequirementTable> {
internal var field_1_: List<org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement> = emptyList()
private var memoizedSerializedSize: Int = -1
private constructor()
private constructor(builder: Builder) {
this.unknownFields = builder.unknownFields
}
private constructor(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite) {
var mutBit0_ = 0
var unknownBuffer: ByteString.Output? = null
var unknownOut: CodedOutputStream? = null
fun unknownOutput(): CodedOutputStream {
var out = unknownOut
if (out == null) { val buf = ByteString.newOutput(); unknownBuffer = buf; out = CodedOutputStream.newInstance(buf); unknownOut = out }
return out
}
try {
var done = false
while (!done) {
val tag = input.readTag()
when (tag) {
0 -> done = true
10 -> {
if ((mutBit0_ and 1) == 0) { field_1_ = ArrayList(); mutBit0_ = mutBit0_ or 1 }
(field_1_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement.PARSER, extensionRegistry))
}
else -> if (!input.skipField(tag, unknownOutput())) done = true
}}
} catch (e: InvalidProtocolBufferException) {
throw e.setUnfinishedMessage(this)
} finally {
if ((mutBit0_ and 1) != 0) field_1_ = GeneratedMessageLite.unmodifiableProtoList(field_1_)
if (unknownBuffer != null) { unknownOut!!.flush(); unknownFields = unknownBuffer.toByteString() }
}}
val `requirementList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement> get() = field_1_
val `requirementCount`: Int get() = field_1_.size
fun getRequirementList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement> = field_1_
fun getRequirementCount(): Int = field_1_.size
fun getRequirement(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement = field_1_[index]
override fun isInitialized(): Boolean = true
override fun writeTo(output: CodedOutputStream) {
for (i in 0 until field_1_.size) { output.writeRawVarint32(10); output.writeMessageNoTag(field_1_[i]) }
if (!unknownFields.isEmpty()) unknownFields.writeTo(output)
}
override val serializedSize: Int get() {
val cached = memoizedSerializedSize
if (cached != -1) return cached
var size = 0
for (i in 0 until field_1_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_1_[i]) }
size += unknownFields.size()
memoizedSerializedSize = size
return size
}
override val defaultInstanceForType: VersionRequirementTable get() = defaultInstance
override fun newBuilderForType(): Builder = Builder()
override fun toBuilder(): Builder = Builder().mergeFrom(this)
class Builder : GeneratedMessageLite.ExtendableBuilder<VersionRequirementTable, Builder>() {
private var mutBitField0_: Int = 0
private var field_1_: List<org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement> = emptyList()
private fun ensure_1_Mutable() { if ((mutBitField0_ and 1) == 0) { field_1_ = ArrayList(field_1_); mutBitField0_ = mutBitField0_ or 1 } }
override val defaultInstanceForType: VersionRequirementTable get() = defaultInstance
override fun clear(): Builder {
mutBitField0_ = 0
field_1_ = emptyList()
unknownFields = ByteString.EMPTY
return this
}
override fun clone(): Builder = Builder().mergeFrom(buildPartial())
override fun build(): VersionRequirementTable {
val result = buildPartial()
return result
}
override fun buildPartial(): VersionRequirementTable {
val result = VersionRequirementTable(this)
if ((mutBitField0_ and 1) != 0) { field_1_ = GeneratedMessageLite.unmodifiableProtoList(field_1_); mutBitField0_ = mutBitField0_ and -2 }
result.field_1_ = field_1_
return result
}
override fun mergeFrom(other: VersionRequirementTable): Builder {
if (other === defaultInstance) return this
if (other.field_1_.isNotEmpty()) {
if (field_1_.isEmpty()) { field_1_ = other.field_1_; mutBitField0_ = mutBitField0_ and -2 } else { ensure_1_Mutable(); (field_1_ as MutableList<Any>).addAll(other.field_1_ as Collection<Any>) }
}
unknownFields = unknownFields.concat(other.unknownFields)
return this
}
override fun isInitialized(): Boolean = true
override fun mergeFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Builder {
var parsedMessage: VersionRequirementTable? = null
try {
parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry)
} catch (e: InvalidProtocolBufferException) {
parsedMessage = e.unfinishedMessage as? VersionRequirementTable
throw e
} finally {
if (parsedMessage != null) mergeFrom(parsedMessage)
}
return this
}
val `requirementList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement> get() = if (field_1_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_1_)
val `requirementCount`: Int get() = field_1_.size
fun getRequirementList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement> = `requirementList`
fun getRequirementCount(): Int = field_1_.size
fun getRequirement(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement = field_1_[index]
fun addRequirement(value: org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement): Builder { ensure_1_Mutable(); (field_1_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement>).add(value); return this }
fun setRequirement(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement): Builder { ensure_1_Mutable(); (field_1_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement>)[index] = value; return this }
fun addAllRequirement(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement>): Builder { ensure_1_Mutable(); AbstractMessageLite.Builder.addAll(values, field_1_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement>); return this }
fun clearRequirement(): Builder { field_1_ = emptyList(); mutBitField0_ = mutBitField0_ and -2; return this }
fun addRequirement(value: org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement.Builder): Builder = addRequirement(value.build())
fun setRequirement(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.VersionRequirement.Builder): Builder = setRequirement(index, value.build())
}
companion object {
val defaultInstance: VersionRequirementTable = VersionRequirementTable()
fun getDefaultInstance(): VersionRequirementTable = defaultInstance
fun newBuilder(): Builder = Builder()
fun newBuilder(prototype: VersionRequirementTable): Builder = Builder().mergeFrom(prototype)
val PARSER: Parser<VersionRequirementTable> = object : AbstractParser<VersionRequirementTable>() {
override fun parsePartialFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): VersionRequirementTable =
VersionRequirementTable(input, extensionRegistry)
}
fun parseFrom(bytes: ByteArray): VersionRequirementTable = PARSER.parseFrom(bytes)
fun parseFrom(bytes: ByteArray, registry: ExtensionRegistryLite): VersionRequirementTable = PARSER.parseFrom(bytes, registry)
fun parseFrom(bytes: ByteString): VersionRequirementTable { val input = bytes.newCodedInput(); val msg = PARSER.parsePartialFrom(input, ExtensionRegistryLite.getEmptyRegistry()); input.checkLastTagWas(0); if (!msg.isInitialized()) throw UninitializedMessageException().asInvalidProtocolBufferException().setUnfinishedMessage(msg); return msg }
fun parseFrom(input: CodedInputStream): VersionRequirementTable = PARSER.parseFrom(input)
fun parseFrom(input: CodedInputStream, registry: ExtensionRegistryLite): VersionRequirementTable = PARSER.parseFrom(input, registry)
const val REQUIREMENT_FIELD_NUMBER = 1
}}
class PackageFragment : GeneratedMessageLite.ExtendableMessage<PackageFragment> {
internal var bitField0_: Int = 0
internal var field_1_: org.jetbrains.kotlin.metadata.ProtoBuf.StringTable? = null
internal var field_2_: org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable? = null
internal var field_3_: org.jetbrains.kotlin.metadata.ProtoBuf.Package? = null
internal var field_4_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Class> = emptyList()
internal var field_5_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = emptyList()
private var memoizedIsInitialized: Byte = -1
private var memoizedSerializedSize: Int = -1
private constructor()
private constructor(builder: Builder) {
this.unknownFields = builder.unknownFields
copyExtensionsFromBuilder(builder)
}
private constructor(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite) {
var mutBit0_ = 0
var unknownBuffer: ByteString.Output? = null
var unknownOut: CodedOutputStream? = null
fun unknownOutput(): CodedOutputStream {
var out = unknownOut
if (out == null) { val buf = ByteString.newOutput(); unknownBuffer = buf; out = CodedOutputStream.newInstance(buf); unknownOut = out }
return out
}
try {
var done = false
while (!done) {
val tag = input.readTag()
when (tag) {
0 -> done = true
10 -> {
var subBuilder: org.jetbrains.kotlin.metadata.ProtoBuf.StringTable.Builder? = null
if ((bitField0_ and 1) != 0) subBuilder = field_1_?.toBuilder()
val parsed = input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.StringTable.PARSER, extensionRegistry)
field_1_ = if (subBuilder != null) subBuilder.mergeFrom(parsed).buildPartial() else parsed
bitField0_ = bitField0_ or 1
}
18 -> {
var subBuilder: org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.Builder? = null
if ((bitField0_ and 2) != 0) subBuilder = field_2_?.toBuilder()
val parsed = input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.PARSER, extensionRegistry)
field_2_ = if (subBuilder != null) subBuilder.mergeFrom(parsed).buildPartial() else parsed
bitField0_ = bitField0_ or 2
}
26 -> {
var subBuilder: org.jetbrains.kotlin.metadata.ProtoBuf.Package.Builder? = null
if ((bitField0_ and 4) != 0) subBuilder = field_3_?.toBuilder()
val parsed = input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Package.PARSER, extensionRegistry)
field_3_ = if (subBuilder != null) subBuilder.mergeFrom(parsed).buildPartial() else parsed
bitField0_ = bitField0_ or 4
}
34 -> {
if ((mutBit0_ and 1) == 0) { field_4_ = ArrayList(); mutBit0_ = mutBit0_ or 1 }
(field_4_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Class>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Class.PARSER, extensionRegistry))
}
42 -> {
if ((mutBit0_ and 2) == 0) { field_5_ = ArrayList(); mutBit0_ = mutBit0_ or 2 }
(field_5_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.PARSER, extensionRegistry))
}
else -> if (!parseUnknownField(input, unknownOutput(), extensionRegistry, tag)) done = true
}}
} catch (e: InvalidProtocolBufferException) {
throw e.setUnfinishedMessage(this)
} finally {
if ((mutBit0_ and 1) != 0) field_4_ = GeneratedMessageLite.unmodifiableProtoList(field_4_)
if ((mutBit0_ and 2) != 0) field_5_ = GeneratedMessageLite.unmodifiableProtoList(field_5_)
if (unknownBuffer != null) { unknownOut!!.flush(); unknownFields = unknownBuffer.toByteString() }
makeExtensionsImmutable()
}}
val `strings`: org.jetbrains.kotlin.metadata.ProtoBuf.StringTable get() = field_1_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.StringTable.defaultInstance
fun getStrings(): org.jetbrains.kotlin.metadata.ProtoBuf.StringTable = `strings`
fun hasStrings(): Boolean = (bitField0_ and 1) != 0
val `qualifiedNames`: org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable get() = field_2_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.defaultInstance
fun getQualifiedNames(): org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable = `qualifiedNames`
fun hasQualifiedNames(): Boolean = (bitField0_ and 2) != 0
val `package`: org.jetbrains.kotlin.metadata.ProtoBuf.Package get() = field_3_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Package.defaultInstance
fun getPackage(): org.jetbrains.kotlin.metadata.ProtoBuf.Package = `package`
fun hasPackage(): Boolean = (bitField0_ and 4) != 0
val `class_List`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Class> get() = field_4_
val `class_Count`: Int get() = field_4_.size
fun getClass_List(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Class> = field_4_
fun getClass_Count(): Int = field_4_.size
fun getClass_(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Class = field_4_[index]
val `fileAnnotationList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> get() = field_5_
val `fileAnnotationCount`: Int get() = field_5_.size
fun getFileAnnotationList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = field_5_
fun getFileAnnotationCount(): Int = field_5_.size
fun getFileAnnotation(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation = field_5_[index]
override fun isInitialized(): Boolean {
val isInitialized = memoizedIsInitialized.toInt()
if (isInitialized == 1) return true
if (isInitialized == 0) return false
if ((bitField0_ and 2) != 0 && !getQualifiedNames().isInitialized()) { memoizedIsInitialized = 0; return false }
if ((bitField0_ and 4) != 0 && !getPackage().isInitialized()) { memoizedIsInitialized = 0; return false }
for (i in 0 until field_4_.size) { if (!field_4_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
for (i in 0 until field_5_.size) { if (!field_5_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
if (!extensionsAreInitialized()) { memoizedIsInitialized = 0; return false }
memoizedIsInitialized = 1
return true
}
override fun writeTo(output: CodedOutputStream) {
val extensionWriter = if (hasAnyExtensions()) newExtensionWriter() else null
extensionWriter?.writeUntil(1, output)
if ((bitField0_ and 1) != 0) { output.writeRawVarint32(10); output.writeMessageNoTag(getStrings()) }
extensionWriter?.writeUntil(2, output)
if ((bitField0_ and 2) != 0) { output.writeRawVarint32(18); output.writeMessageNoTag(getQualifiedNames()) }
extensionWriter?.writeUntil(3, output)
if ((bitField0_ and 4) != 0) { output.writeRawVarint32(26); output.writeMessageNoTag(getPackage()) }
extensionWriter?.writeUntil(4, output)
for (i in 0 until field_4_.size) { output.writeRawVarint32(34); output.writeMessageNoTag(field_4_[i]) }
extensionWriter?.writeUntil(5, output)
for (i in 0 until field_5_.size) { output.writeRawVarint32(42); output.writeMessageNoTag(field_5_[i]) }
extensionWriter?.writeUntil(Int.MAX_VALUE, output)
if (!unknownFields.isEmpty()) unknownFields.writeTo(output)
}
override val serializedSize: Int get() {
val cached = memoizedSerializedSize
if (cached != -1) return cached
var size = 0
if ((bitField0_ and 1) != 0) size += 1 + CodedOutputStream.computeMessageSizeNoTag(getStrings())
if ((bitField0_ and 2) != 0) size += 1 + CodedOutputStream.computeMessageSizeNoTag(getQualifiedNames())
if ((bitField0_ and 4) != 0) size += 1 + CodedOutputStream.computeMessageSizeNoTag(getPackage())
for (i in 0 until field_4_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_4_[i]) }
for (i in 0 until field_5_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_5_[i]) }
size += extensionsSerializedSize()
size += unknownFields.size()
memoizedSerializedSize = size
return size
}
override val defaultInstanceForType: PackageFragment get() = defaultInstance
override fun newBuilderForType(): Builder = Builder()
override fun toBuilder(): Builder = Builder().mergeFrom(this)
class Builder : GeneratedMessageLite.ExtendableBuilder<PackageFragment, Builder>() {
private var bitField0_: Int = 0
private var mutBitField0_: Int = 0
private var field_1_: org.jetbrains.kotlin.metadata.ProtoBuf.StringTable? = null
private var field_2_: org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable? = null
private var field_3_: org.jetbrains.kotlin.metadata.ProtoBuf.Package? = null
private var field_4_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Class> = emptyList()
private fun ensure_4_Mutable() { if ((mutBitField0_ and 1) == 0) { field_4_ = ArrayList(field_4_); mutBitField0_ = mutBitField0_ or 1 } }
private var field_5_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = emptyList()
private fun ensure_5_Mutable() { if ((mutBitField0_ and 2) == 0) { field_5_ = ArrayList(field_5_); mutBitField0_ = mutBitField0_ or 2 } }
override val defaultInstanceForType: PackageFragment get() = defaultInstance
override fun clear(): Builder {
bitField0_ = 0
mutBitField0_ = 0
field_1_ = null
field_2_ = null
field_3_ = null
field_4_ = emptyList()
field_5_ = emptyList()
clearExtensions()
unknownFields = ByteString.EMPTY
return this
}
override fun clone(): Builder = Builder().mergeFrom(buildPartial())
override fun build(): PackageFragment {
val result = buildPartial()
if (!result.isInitialized()) throw UninitializedMessageException()
return result
}
override fun buildPartial(): PackageFragment {
val result = PackageFragment(this)
val from_bitField0_ = bitField0_
var to_bitField0_ = 0
if ((from_bitField0_ and 1) != 0) to_bitField0_ = to_bitField0_ or 1
result.field_1_ = field_1_
if ((from_bitField0_ and 2) != 0) to_bitField0_ = to_bitField0_ or 2
result.field_2_ = field_2_
if ((from_bitField0_ and 4) != 0) to_bitField0_ = to_bitField0_ or 4
result.field_3_ = field_3_
result.bitField0_ = to_bitField0_
if ((mutBitField0_ and 1) != 0) { field_4_ = GeneratedMessageLite.unmodifiableProtoList(field_4_); mutBitField0_ = mutBitField0_ and -2 }
result.field_4_ = field_4_
if ((mutBitField0_ and 2) != 0) { field_5_ = GeneratedMessageLite.unmodifiableProtoList(field_5_); mutBitField0_ = mutBitField0_ and -3 }
result.field_5_ = field_5_
return result
}
override fun mergeFrom(other: PackageFragment): Builder {
if (other === defaultInstance) return this
if (other.hasStrings()) mergeStrings(other.getStrings())
if (other.hasQualifiedNames()) mergeQualifiedNames(other.getQualifiedNames())
if (other.hasPackage()) mergePackage(other.getPackage())
if (other.field_4_.isNotEmpty()) {
if (field_4_.isEmpty()) { field_4_ = other.field_4_; mutBitField0_ = mutBitField0_ and -2 } else { ensure_4_Mutable(); (field_4_ as MutableList<Any>).addAll(other.field_4_ as Collection<Any>) }
}
if (other.field_5_.isNotEmpty()) {
if (field_5_.isEmpty()) { field_5_ = other.field_5_; mutBitField0_ = mutBitField0_ and -3 } else { ensure_5_Mutable(); (field_5_ as MutableList<Any>).addAll(other.field_5_ as Collection<Any>) }
}
mergeExtensionFields(other)
unknownFields = unknownFields.concat(other.unknownFields)
return this
}
override fun isInitialized(): Boolean {
if ((bitField0_ and 2) != 0 && !getQualifiedNames().isInitialized()) return false
if ((bitField0_ and 4) != 0 && !getPackage().isInitialized()) return false
for (i in 0 until field_4_.size) { if (!field_4_[i].isInitialized()) return false }
for (i in 0 until field_5_.size) { if (!field_5_[i].isInitialized()) return false }
if (!extensionsAreInitialized()) return false
return true
}
override fun mergeFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Builder {
var parsedMessage: PackageFragment? = null
try {
parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry)
} catch (e: InvalidProtocolBufferException) {
parsedMessage = e.unfinishedMessage as? PackageFragment
throw e
} finally {
if (parsedMessage != null) mergeFrom(parsedMessage)
}
return this
}
var `strings`: org.jetbrains.kotlin.metadata.ProtoBuf.StringTable
get() = field_1_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.StringTable.defaultInstance
set(value) { field_1_ = value; bitField0_ = bitField0_ or 1 }
fun getStrings(): org.jetbrains.kotlin.metadata.ProtoBuf.StringTable = `strings`
fun hasStrings(): Boolean = (bitField0_ and 1) != 0
fun setStrings(value: org.jetbrains.kotlin.metadata.ProtoBuf.StringTable): Builder { field_1_ = value; bitField0_ = bitField0_ or 1; return this }
fun setStrings(value: org.jetbrains.kotlin.metadata.ProtoBuf.StringTable.Builder): Builder { field_1_ = value.build(); bitField0_ = bitField0_ or 1; return this }
fun mergeStrings(value: org.jetbrains.kotlin.metadata.ProtoBuf.StringTable): Builder {
val cur = field_1_
field_1_ = if ((bitField0_ and 1) != 0 && cur != null && cur !== org.jetbrains.kotlin.metadata.ProtoBuf.StringTable.defaultInstance) org.jetbrains.kotlin.metadata.ProtoBuf.StringTable.newBuilder(cur).mergeFrom(value).buildPartial() else value
bitField0_ = bitField0_ or 1
return this
}
fun clearStrings(): Builder { field_1_ = null; bitField0_ = bitField0_ and -2; return this }
var `qualifiedNames`: org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable
get() = field_2_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.defaultInstance
set(value) { field_2_ = value; bitField0_ = bitField0_ or 2 }
fun getQualifiedNames(): org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable = `qualifiedNames`
fun hasQualifiedNames(): Boolean = (bitField0_ and 2) != 0
fun setQualifiedNames(value: org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable): Builder { field_2_ = value; bitField0_ = bitField0_ or 2; return this }
fun setQualifiedNames(value: org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.Builder): Builder { field_2_ = value.build(); bitField0_ = bitField0_ or 2; return this }
fun mergeQualifiedNames(value: org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable): Builder {
val cur = field_2_
field_2_ = if ((bitField0_ and 2) != 0 && cur != null && cur !== org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.defaultInstance) org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.newBuilder(cur).mergeFrom(value).buildPartial() else value
bitField0_ = bitField0_ or 2
return this
}
fun clearQualifiedNames(): Builder { field_2_ = null; bitField0_ = bitField0_ and -3; return this }
var `package`: org.jetbrains.kotlin.metadata.ProtoBuf.Package
get() = field_3_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Package.defaultInstance
set(value) { field_3_ = value; bitField0_ = bitField0_ or 4 }
fun getPackage(): org.jetbrains.kotlin.metadata.ProtoBuf.Package = `package`
fun hasPackage(): Boolean = (bitField0_ and 4) != 0
fun setPackage(value: org.jetbrains.kotlin.metadata.ProtoBuf.Package): Builder { field_3_ = value; bitField0_ = bitField0_ or 4; return this }
fun setPackage(value: org.jetbrains.kotlin.metadata.ProtoBuf.Package.Builder): Builder { field_3_ = value.build(); bitField0_ = bitField0_ or 4; return this }
fun mergePackage(value: org.jetbrains.kotlin.metadata.ProtoBuf.Package): Builder {
val cur = field_3_
field_3_ = if ((bitField0_ and 4) != 0 && cur != null && cur !== org.jetbrains.kotlin.metadata.ProtoBuf.Package.defaultInstance) org.jetbrains.kotlin.metadata.ProtoBuf.Package.newBuilder(cur).mergeFrom(value).buildPartial() else value
bitField0_ = bitField0_ or 4
return this
}
fun clearPackage(): Builder { field_3_ = null; bitField0_ = bitField0_ and -5; return this }
val `class_List`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Class> get() = if (field_4_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_4_)
val `class_Count`: Int get() = field_4_.size
fun getClass_List(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Class> = `class_List`
fun getClass_Count(): Int = field_4_.size
fun getClass_(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Class = field_4_[index]
fun addClass_(value: org.jetbrains.kotlin.metadata.ProtoBuf.Class): Builder { ensure_4_Mutable(); (field_4_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Class>).add(value); return this }
fun setClass_(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Class): Builder { ensure_4_Mutable(); (field_4_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Class>)[index] = value; return this }
fun addAllClass_(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.Class>): Builder { ensure_4_Mutable(); AbstractMessageLite.Builder.addAll(values, field_4_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Class>); return this }
fun clearClass_(): Builder { field_4_ = emptyList(); mutBitField0_ = mutBitField0_ and -2; return this }
fun addClass_(value: org.jetbrains.kotlin.metadata.ProtoBuf.Class.Builder): Builder = addClass_(value.build())
fun setClass_(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Class.Builder): Builder = setClass_(index, value.build())
val `fileAnnotationList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> get() = if (field_5_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_5_)
val `fileAnnotationCount`: Int get() = field_5_.size
fun getFileAnnotationList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation> = `fileAnnotationList`
fun getFileAnnotationCount(): Int = field_5_.size
fun getFileAnnotation(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Annotation = field_5_[index]
fun addFileAnnotation(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation): Builder { ensure_5_Mutable(); (field_5_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>).add(value); return this }
fun setFileAnnotation(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation): Builder { ensure_5_Mutable(); (field_5_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>)[index] = value; return this }
fun addAllFileAnnotation(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>): Builder { ensure_5_Mutable(); AbstractMessageLite.Builder.addAll(values, field_5_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>); return this }
fun clearFileAnnotation(): Builder { field_5_ = emptyList(); mutBitField0_ = mutBitField0_ and -3; return this }
fun addFileAnnotation(value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Builder): Builder = addFileAnnotation(value.build())
fun setFileAnnotation(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Builder): Builder = setFileAnnotation(index, value.build())
}
companion object {
val defaultInstance: PackageFragment = PackageFragment()
fun getDefaultInstance(): PackageFragment = defaultInstance
fun newBuilder(): Builder = Builder()
fun newBuilder(prototype: PackageFragment): Builder = Builder().mergeFrom(prototype)
val PARSER: Parser<PackageFragment> = object : AbstractParser<PackageFragment>() {
override fun parsePartialFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): PackageFragment =
PackageFragment(input, extensionRegistry)
}
fun parseFrom(bytes: ByteArray): PackageFragment = PARSER.parseFrom(bytes)
fun parseFrom(bytes: ByteArray, registry: ExtensionRegistryLite): PackageFragment = PARSER.parseFrom(bytes, registry)
fun parseFrom(bytes: ByteString): PackageFragment { val input = bytes.newCodedInput(); val msg = PARSER.parsePartialFrom(input, ExtensionRegistryLite.getEmptyRegistry()); input.checkLastTagWas(0); if (!msg.isInitialized()) throw UninitializedMessageException().asInvalidProtocolBufferException().setUnfinishedMessage(msg); return msg }
fun parseFrom(input: CodedInputStream): PackageFragment = PARSER.parseFrom(input)
fun parseFrom(input: CodedInputStream, registry: ExtensionRegistryLite): PackageFragment = PARSER.parseFrom(input, registry)
const val STRINGS_FIELD_NUMBER = 1
const val QUALIFIED_NAMES_FIELD_NUMBER = 2
const val PACKAGE_FIELD_NUMBER = 3
const val CLASS_FIELD_NUMBER = 4
const val FILE_ANNOTATION_FIELD_NUMBER = 5
}}
class Contract : GeneratedMessageLite.ExtendableMessage<Contract> {
internal var field_1_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Effect> = emptyList()
private var memoizedIsInitialized: Byte = -1
private var memoizedSerializedSize: Int = -1
private constructor()
private constructor(builder: Builder) {
this.unknownFields = builder.unknownFields
}
private constructor(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite) {
var mutBit0_ = 0
var unknownBuffer: ByteString.Output? = null
var unknownOut: CodedOutputStream? = null
fun unknownOutput(): CodedOutputStream {
var out = unknownOut
if (out == null) { val buf = ByteString.newOutput(); unknownBuffer = buf; out = CodedOutputStream.newInstance(buf); unknownOut = out }
return out
}
try {
var done = false
while (!done) {
val tag = input.readTag()
when (tag) {
0 -> done = true
10 -> {
if ((mutBit0_ and 1) == 0) { field_1_ = ArrayList(); mutBit0_ = mutBit0_ or 1 }
(field_1_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Effect>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Effect.PARSER, extensionRegistry))
}
else -> if (!input.skipField(tag, unknownOutput())) done = true
}}
} catch (e: InvalidProtocolBufferException) {
throw e.setUnfinishedMessage(this)
} finally {
if ((mutBit0_ and 1) != 0) field_1_ = GeneratedMessageLite.unmodifiableProtoList(field_1_)
if (unknownBuffer != null) { unknownOut!!.flush(); unknownFields = unknownBuffer.toByteString() }
}}
val `effectList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Effect> get() = field_1_
val `effectCount`: Int get() = field_1_.size
fun getEffectList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Effect> = field_1_
fun getEffectCount(): Int = field_1_.size
fun getEffect(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Effect = field_1_[index]
override fun isInitialized(): Boolean {
val isInitialized = memoizedIsInitialized.toInt()
if (isInitialized == 1) return true
if (isInitialized == 0) return false
for (i in 0 until field_1_.size) { if (!field_1_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
memoizedIsInitialized = 1
return true
}
override fun writeTo(output: CodedOutputStream) {
for (i in 0 until field_1_.size) { output.writeRawVarint32(10); output.writeMessageNoTag(field_1_[i]) }
if (!unknownFields.isEmpty()) unknownFields.writeTo(output)
}
override val serializedSize: Int get() {
val cached = memoizedSerializedSize
if (cached != -1) return cached
var size = 0
for (i in 0 until field_1_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_1_[i]) }
size += unknownFields.size()
memoizedSerializedSize = size
return size
}
override val defaultInstanceForType: Contract get() = defaultInstance
override fun newBuilderForType(): Builder = Builder()
override fun toBuilder(): Builder = Builder().mergeFrom(this)
class Builder : GeneratedMessageLite.ExtendableBuilder<Contract, Builder>() {
private var mutBitField0_: Int = 0
private var field_1_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Effect> = emptyList()
private fun ensure_1_Mutable() { if ((mutBitField0_ and 1) == 0) { field_1_ = ArrayList(field_1_); mutBitField0_ = mutBitField0_ or 1 } }
override val defaultInstanceForType: Contract get() = defaultInstance
override fun clear(): Builder {
mutBitField0_ = 0
field_1_ = emptyList()
unknownFields = ByteString.EMPTY
return this
}
override fun clone(): Builder = Builder().mergeFrom(buildPartial())
override fun build(): Contract {
val result = buildPartial()
if (!result.isInitialized()) throw UninitializedMessageException()
return result
}
override fun buildPartial(): Contract {
val result = Contract(this)
if ((mutBitField0_ and 1) != 0) { field_1_ = GeneratedMessageLite.unmodifiableProtoList(field_1_); mutBitField0_ = mutBitField0_ and -2 }
result.field_1_ = field_1_
return result
}
override fun mergeFrom(other: Contract): Builder {
if (other === defaultInstance) return this
if (other.field_1_.isNotEmpty()) {
if (field_1_.isEmpty()) { field_1_ = other.field_1_; mutBitField0_ = mutBitField0_ and -2 } else { ensure_1_Mutable(); (field_1_ as MutableList<Any>).addAll(other.field_1_ as Collection<Any>) }
}
unknownFields = unknownFields.concat(other.unknownFields)
return this
}
override fun isInitialized(): Boolean {
for (i in 0 until field_1_.size) { if (!field_1_[i].isInitialized()) return false }
return true
}
override fun mergeFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Builder {
var parsedMessage: Contract? = null
try {
parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry)
} catch (e: InvalidProtocolBufferException) {
parsedMessage = e.unfinishedMessage as? Contract
throw e
} finally {
if (parsedMessage != null) mergeFrom(parsedMessage)
}
return this
}
val `effectList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Effect> get() = if (field_1_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_1_)
val `effectCount`: Int get() = field_1_.size
fun getEffectList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Effect> = `effectList`
fun getEffectCount(): Int = field_1_.size
fun getEffect(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Effect = field_1_[index]
fun addEffect(value: org.jetbrains.kotlin.metadata.ProtoBuf.Effect): Builder { ensure_1_Mutable(); (field_1_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Effect>).add(value); return this }
fun setEffect(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Effect): Builder { ensure_1_Mutable(); (field_1_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Effect>)[index] = value; return this }
fun addAllEffect(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.Effect>): Builder { ensure_1_Mutable(); AbstractMessageLite.Builder.addAll(values, field_1_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Effect>); return this }
fun clearEffect(): Builder { field_1_ = emptyList(); mutBitField0_ = mutBitField0_ and -2; return this }
fun addEffect(value: org.jetbrains.kotlin.metadata.ProtoBuf.Effect.Builder): Builder = addEffect(value.build())
fun setEffect(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Effect.Builder): Builder = setEffect(index, value.build())
}
companion object {
val defaultInstance: Contract = Contract()
fun getDefaultInstance(): Contract = defaultInstance
fun newBuilder(): Builder = Builder()
fun newBuilder(prototype: Contract): Builder = Builder().mergeFrom(prototype)
val PARSER: Parser<Contract> = object : AbstractParser<Contract>() {
override fun parsePartialFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Contract =
Contract(input, extensionRegistry)
}
fun parseFrom(bytes: ByteArray): Contract = PARSER.parseFrom(bytes)
fun parseFrom(bytes: ByteArray, registry: ExtensionRegistryLite): Contract = PARSER.parseFrom(bytes, registry)
fun parseFrom(bytes: ByteString): Contract { val input = bytes.newCodedInput(); val msg = PARSER.parsePartialFrom(input, ExtensionRegistryLite.getEmptyRegistry()); input.checkLastTagWas(0); if (!msg.isInitialized()) throw UninitializedMessageException().asInvalidProtocolBufferException().setUnfinishedMessage(msg); return msg }
fun parseFrom(input: CodedInputStream): Contract = PARSER.parseFrom(input)
fun parseFrom(input: CodedInputStream, registry: ExtensionRegistryLite): Contract = PARSER.parseFrom(input, registry)
const val EFFECT_FIELD_NUMBER = 1
}}
class Effect : GeneratedMessageLite.ExtendableMessage<Effect> {
enum class EffectType(override val number: Int) : Internal.EnumLite {
RETURNS_CONSTANT(0),
CALLS(1),
RETURNS_NOT_NULL(2),
RETURNS_RESULT_OF(3),
RETURNS_PARAMETER(4),
; companion object : Internal.EnumLiteMap<EffectType> {
override fun findValueByNumber(number: Int): EffectType? = valueOf(number)
fun valueOf(number: Int): EffectType? = when (number) {
0 -> RETURNS_CONSTANT
1 -> CALLS
2 -> RETURNS_NOT_NULL
3 -> RETURNS_RESULT_OF
4 -> RETURNS_PARAMETER
else -> null
}
fun internalGetValueMap(): Internal.EnumLiteMap<EffectType> = this
}}
enum class InvocationKind(override val number: Int) : Internal.EnumLite {
AT_MOST_ONCE(0),
EXACTLY_ONCE(1),
AT_LEAST_ONCE(2),
; companion object : Internal.EnumLiteMap<InvocationKind> {
override fun findValueByNumber(number: Int): InvocationKind? = valueOf(number)
fun valueOf(number: Int): InvocationKind? = when (number) {
0 -> AT_MOST_ONCE
1 -> EXACTLY_ONCE
2 -> AT_LEAST_ONCE
else -> null
}
fun internalGetValueMap(): Internal.EnumLiteMap<InvocationKind> = this
}}
enum class EffectConditionKind(override val number: Int) : Internal.EnumLite {
CONCLUSION_CONDITION(0),
RETURNS_CONDITION(1),
HOLDSIN_CONDITION(2),
; companion object : Internal.EnumLiteMap<EffectConditionKind> {
override fun findValueByNumber(number: Int): EffectConditionKind? = valueOf(number)
fun valueOf(number: Int): EffectConditionKind? = when (number) {
0 -> CONCLUSION_CONDITION
1 -> RETURNS_CONDITION
2 -> HOLDSIN_CONDITION
else -> null
}
fun internalGetValueMap(): Internal.EnumLiteMap<EffectConditionKind> = this
}}
internal var bitField0_: Int = 0
internal var field_1_: org.jetbrains.kotlin.metadata.ProtoBuf.Effect.EffectType = org.jetbrains.kotlin.metadata.ProtoBuf.Effect.EffectType.RETURNS_CONSTANT
internal var field_3_: org.jetbrains.kotlin.metadata.ProtoBuf.Expression? = null
internal var field_4_: org.jetbrains.kotlin.metadata.ProtoBuf.Effect.InvocationKind = org.jetbrains.kotlin.metadata.ProtoBuf.Effect.InvocationKind.AT_MOST_ONCE
internal var field_5_: org.jetbrains.kotlin.metadata.ProtoBuf.Effect.EffectConditionKind = org.jetbrains.kotlin.metadata.ProtoBuf.Effect.EffectConditionKind.CONCLUSION_CONDITION
internal var field_2_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Expression> = emptyList()
private var memoizedIsInitialized: Byte = -1
private var memoizedSerializedSize: Int = -1
private constructor()
private constructor(builder: Builder) {
this.unknownFields = builder.unknownFields
}
private constructor(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite) {
var mutBit0_ = 0
var unknownBuffer: ByteString.Output? = null
var unknownOut: CodedOutputStream? = null
fun unknownOutput(): CodedOutputStream {
var out = unknownOut
if (out == null) { val buf = ByteString.newOutput(); unknownBuffer = buf; out = CodedOutputStream.newInstance(buf); unknownOut = out }
return out
}
try {
var done = false
while (!done) {
val tag = input.readTag()
when (tag) {
0 -> done = true
8 -> {
val raw = input.readEnum()
val parsed = org.jetbrains.kotlin.metadata.ProtoBuf.Effect.EffectType.valueOf(raw)
if (parsed == null) { unknownOutput().writeRawVarint32(8); unknownOutput().writeRawVarint32(raw) } else {
bitField0_ = bitField0_ or 1; field_1_ = parsed
}}
18 -> {
if ((mutBit0_ and 1) == 0) { field_2_ = ArrayList(); mutBit0_ = mutBit0_ or 1 }
(field_2_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Expression>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Expression.PARSER, extensionRegistry))
}
26 -> {
var subBuilder: org.jetbrains.kotlin.metadata.ProtoBuf.Expression.Builder? = null
if ((bitField0_ and 2) != 0) subBuilder = field_3_?.toBuilder()
val parsed = input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Expression.PARSER, extensionRegistry)
field_3_ = if (subBuilder != null) subBuilder.mergeFrom(parsed).buildPartial() else parsed
bitField0_ = bitField0_ or 2
}
32 -> {
val raw = input.readEnum()
val parsed = org.jetbrains.kotlin.metadata.ProtoBuf.Effect.InvocationKind.valueOf(raw)
if (parsed == null) { unknownOutput().writeRawVarint32(32); unknownOutput().writeRawVarint32(raw) } else {
bitField0_ = bitField0_ or 4; field_4_ = parsed
}}
40 -> {
val raw = input.readEnum()
val parsed = org.jetbrains.kotlin.metadata.ProtoBuf.Effect.EffectConditionKind.valueOf(raw)
if (parsed == null) { unknownOutput().writeRawVarint32(40); unknownOutput().writeRawVarint32(raw) } else {
bitField0_ = bitField0_ or 8; field_5_ = parsed
}}
else -> if (!input.skipField(tag, unknownOutput())) done = true
}}
} catch (e: InvalidProtocolBufferException) {
throw e.setUnfinishedMessage(this)
} finally {
if ((mutBit0_ and 1) != 0) field_2_ = GeneratedMessageLite.unmodifiableProtoList(field_2_)
if (unknownBuffer != null) { unknownOut!!.flush(); unknownFields = unknownBuffer.toByteString() }
}}
val `effectType`: org.jetbrains.kotlin.metadata.ProtoBuf.Effect.EffectType get() = field_1_
fun getEffectType(): org.jetbrains.kotlin.metadata.ProtoBuf.Effect.EffectType = `effectType`
fun hasEffectType(): Boolean = (bitField0_ and 1) != 0
val `effectConstructorArgumentList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Expression> get() = field_2_
val `effectConstructorArgumentCount`: Int get() = field_2_.size
fun getEffectConstructorArgumentList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Expression> = field_2_
fun getEffectConstructorArgumentCount(): Int = field_2_.size
fun getEffectConstructorArgument(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Expression = field_2_[index]
val `conclusionOfConditionalEffect`: org.jetbrains.kotlin.metadata.ProtoBuf.Expression get() = field_3_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Expression.defaultInstance
fun getConclusionOfConditionalEffect(): org.jetbrains.kotlin.metadata.ProtoBuf.Expression = `conclusionOfConditionalEffect`
fun hasConclusionOfConditionalEffect(): Boolean = (bitField0_ and 2) != 0
val `kind`: org.jetbrains.kotlin.metadata.ProtoBuf.Effect.InvocationKind get() = field_4_
fun getKind(): org.jetbrains.kotlin.metadata.ProtoBuf.Effect.InvocationKind = `kind`
fun hasKind(): Boolean = (bitField0_ and 4) != 0
val `conditionKind`: org.jetbrains.kotlin.metadata.ProtoBuf.Effect.EffectConditionKind get() = field_5_
fun getConditionKind(): org.jetbrains.kotlin.metadata.ProtoBuf.Effect.EffectConditionKind = `conditionKind`
fun hasConditionKind(): Boolean = (bitField0_ and 8) != 0
override fun isInitialized(): Boolean {
val isInitialized = memoizedIsInitialized.toInt()
if (isInitialized == 1) return true
if (isInitialized == 0) return false
if ((bitField0_ and 2) != 0 && !getConclusionOfConditionalEffect().isInitialized()) { memoizedIsInitialized = 0; return false }
for (i in 0 until field_2_.size) { if (!field_2_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
memoizedIsInitialized = 1
return true
}
override fun writeTo(output: CodedOutputStream) {
if ((bitField0_ and 1) != 0) { output.writeRawVarint32(8); output.writeInt32NoTag((field_1_).number) }
for (i in 0 until field_2_.size) { output.writeRawVarint32(18); output.writeMessageNoTag(field_2_[i]) }
if ((bitField0_ and 2) != 0) { output.writeRawVarint32(26); output.writeMessageNoTag(getConclusionOfConditionalEffect()) }
if ((bitField0_ and 4) != 0) { output.writeRawVarint32(32); output.writeInt32NoTag((field_4_).number) }
if ((bitField0_ and 8) != 0) { output.writeRawVarint32(40); output.writeInt32NoTag((field_5_).number) }
if (!unknownFields.isEmpty()) unknownFields.writeTo(output)
}
override val serializedSize: Int get() {
val cached = memoizedSerializedSize
if (cached != -1) return cached
var size = 0
if ((bitField0_ and 1) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag((field_1_).number)
if ((bitField0_ and 2) != 0) size += 1 + CodedOutputStream.computeMessageSizeNoTag(getConclusionOfConditionalEffect())
if ((bitField0_ and 4) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag((field_4_).number)
if ((bitField0_ and 8) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag((field_5_).number)
for (i in 0 until field_2_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_2_[i]) }
size += unknownFields.size()
memoizedSerializedSize = size
return size
}
override val defaultInstanceForType: Effect get() = defaultInstance
override fun newBuilderForType(): Builder = Builder()
override fun toBuilder(): Builder = Builder().mergeFrom(this)
class Builder : GeneratedMessageLite.ExtendableBuilder<Effect, Builder>() {
private var bitField0_: Int = 0
private var mutBitField0_: Int = 0
private var field_1_: org.jetbrains.kotlin.metadata.ProtoBuf.Effect.EffectType = org.jetbrains.kotlin.metadata.ProtoBuf.Effect.EffectType.RETURNS_CONSTANT
private var field_3_: org.jetbrains.kotlin.metadata.ProtoBuf.Expression? = null
private var field_4_: org.jetbrains.kotlin.metadata.ProtoBuf.Effect.InvocationKind = org.jetbrains.kotlin.metadata.ProtoBuf.Effect.InvocationKind.AT_MOST_ONCE
private var field_5_: org.jetbrains.kotlin.metadata.ProtoBuf.Effect.EffectConditionKind = org.jetbrains.kotlin.metadata.ProtoBuf.Effect.EffectConditionKind.CONCLUSION_CONDITION
private var field_2_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Expression> = emptyList()
private fun ensure_2_Mutable() { if ((mutBitField0_ and 1) == 0) { field_2_ = ArrayList(field_2_); mutBitField0_ = mutBitField0_ or 1 } }
override val defaultInstanceForType: Effect get() = defaultInstance
override fun clear(): Builder {
bitField0_ = 0
mutBitField0_ = 0
field_1_ = org.jetbrains.kotlin.metadata.ProtoBuf.Effect.EffectType.RETURNS_CONSTANT
field_3_ = null
field_4_ = org.jetbrains.kotlin.metadata.ProtoBuf.Effect.InvocationKind.AT_MOST_ONCE
field_5_ = org.jetbrains.kotlin.metadata.ProtoBuf.Effect.EffectConditionKind.CONCLUSION_CONDITION
field_2_ = emptyList()
unknownFields = ByteString.EMPTY
return this
}
override fun clone(): Builder = Builder().mergeFrom(buildPartial())
override fun build(): Effect {
val result = buildPartial()
if (!result.isInitialized()) throw UninitializedMessageException()
return result
}
override fun buildPartial(): Effect {
val result = Effect(this)
val from_bitField0_ = bitField0_
var to_bitField0_ = 0
if ((from_bitField0_ and 1) != 0) to_bitField0_ = to_bitField0_ or 1
result.field_1_ = field_1_
if ((from_bitField0_ and 2) != 0) to_bitField0_ = to_bitField0_ or 2
result.field_3_ = field_3_
if ((from_bitField0_ and 4) != 0) to_bitField0_ = to_bitField0_ or 4
result.field_4_ = field_4_
if ((from_bitField0_ and 8) != 0) to_bitField0_ = to_bitField0_ or 8
result.field_5_ = field_5_
result.bitField0_ = to_bitField0_
if ((mutBitField0_ and 1) != 0) { field_2_ = GeneratedMessageLite.unmodifiableProtoList(field_2_); mutBitField0_ = mutBitField0_ and -2 }
result.field_2_ = field_2_
return result
}
override fun mergeFrom(other: Effect): Builder {
if (other === defaultInstance) return this
if (other.hasEffectType()) setEffectType(other.getEffectType())
if (other.hasConclusionOfConditionalEffect()) mergeConclusionOfConditionalEffect(other.getConclusionOfConditionalEffect())
if (other.hasKind()) setKind(other.getKind())
if (other.hasConditionKind()) setConditionKind(other.getConditionKind())
if (other.field_2_.isNotEmpty()) {
if (field_2_.isEmpty()) { field_2_ = other.field_2_; mutBitField0_ = mutBitField0_ and -2 } else { ensure_2_Mutable(); (field_2_ as MutableList<Any>).addAll(other.field_2_ as Collection<Any>) }
}
unknownFields = unknownFields.concat(other.unknownFields)
return this
}
override fun isInitialized(): Boolean {
if ((bitField0_ and 2) != 0 && !getConclusionOfConditionalEffect().isInitialized()) return false
for (i in 0 until field_2_.size) { if (!field_2_[i].isInitialized()) return false }
return true
}
override fun mergeFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Builder {
var parsedMessage: Effect? = null
try {
parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry)
} catch (e: InvalidProtocolBufferException) {
parsedMessage = e.unfinishedMessage as? Effect
throw e
} finally {
if (parsedMessage != null) mergeFrom(parsedMessage)
}
return this
}
var `effectType`: org.jetbrains.kotlin.metadata.ProtoBuf.Effect.EffectType
get() = field_1_
set(value) { bitField0_ = bitField0_ or 1; field_1_ = value }
fun getEffectType(): org.jetbrains.kotlin.metadata.ProtoBuf.Effect.EffectType = field_1_
fun hasEffectType(): Boolean = (bitField0_ and 1) != 0
fun setEffectType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Effect.EffectType): Builder { bitField0_ = bitField0_ or 1; field_1_ = value; return this }
fun clearEffectType(): Builder { bitField0_ = bitField0_ and -2; field_1_ = org.jetbrains.kotlin.metadata.ProtoBuf.Effect.EffectType.RETURNS_CONSTANT; return this }
val `effectConstructorArgumentList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Expression> get() = if (field_2_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_2_)
val `effectConstructorArgumentCount`: Int get() = field_2_.size
fun getEffectConstructorArgumentList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Expression> = `effectConstructorArgumentList`
fun getEffectConstructorArgumentCount(): Int = field_2_.size
fun getEffectConstructorArgument(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Expression = field_2_[index]
fun addEffectConstructorArgument(value: org.jetbrains.kotlin.metadata.ProtoBuf.Expression): Builder { ensure_2_Mutable(); (field_2_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Expression>).add(value); return this }
fun setEffectConstructorArgument(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Expression): Builder { ensure_2_Mutable(); (field_2_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Expression>)[index] = value; return this }
fun addAllEffectConstructorArgument(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.Expression>): Builder { ensure_2_Mutable(); AbstractMessageLite.Builder.addAll(values, field_2_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Expression>); return this }
fun clearEffectConstructorArgument(): Builder { field_2_ = emptyList(); mutBitField0_ = mutBitField0_ and -2; return this }
fun addEffectConstructorArgument(value: org.jetbrains.kotlin.metadata.ProtoBuf.Expression.Builder): Builder = addEffectConstructorArgument(value.build())
fun setEffectConstructorArgument(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Expression.Builder): Builder = setEffectConstructorArgument(index, value.build())
var `conclusionOfConditionalEffect`: org.jetbrains.kotlin.metadata.ProtoBuf.Expression
get() = field_3_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Expression.defaultInstance
set(value) { field_3_ = value; bitField0_ = bitField0_ or 2 }
fun getConclusionOfConditionalEffect(): org.jetbrains.kotlin.metadata.ProtoBuf.Expression = `conclusionOfConditionalEffect`
fun hasConclusionOfConditionalEffect(): Boolean = (bitField0_ and 2) != 0
fun setConclusionOfConditionalEffect(value: org.jetbrains.kotlin.metadata.ProtoBuf.Expression): Builder { field_3_ = value; bitField0_ = bitField0_ or 2; return this }
fun setConclusionOfConditionalEffect(value: org.jetbrains.kotlin.metadata.ProtoBuf.Expression.Builder): Builder { field_3_ = value.build(); bitField0_ = bitField0_ or 2; return this }
fun mergeConclusionOfConditionalEffect(value: org.jetbrains.kotlin.metadata.ProtoBuf.Expression): Builder {
val cur = field_3_
field_3_ = if ((bitField0_ and 2) != 0 && cur != null && cur !== org.jetbrains.kotlin.metadata.ProtoBuf.Expression.defaultInstance) org.jetbrains.kotlin.metadata.ProtoBuf.Expression.newBuilder(cur).mergeFrom(value).buildPartial() else value
bitField0_ = bitField0_ or 2
return this
}
fun clearConclusionOfConditionalEffect(): Builder { field_3_ = null; bitField0_ = bitField0_ and -3; return this }
var `kind`: org.jetbrains.kotlin.metadata.ProtoBuf.Effect.InvocationKind
get() = field_4_
set(value) { bitField0_ = bitField0_ or 4; field_4_ = value }
fun getKind(): org.jetbrains.kotlin.metadata.ProtoBuf.Effect.InvocationKind = field_4_
fun hasKind(): Boolean = (bitField0_ and 4) != 0
fun setKind(value: org.jetbrains.kotlin.metadata.ProtoBuf.Effect.InvocationKind): Builder { bitField0_ = bitField0_ or 4; field_4_ = value; return this }
fun clearKind(): Builder { bitField0_ = bitField0_ and -5; field_4_ = org.jetbrains.kotlin.metadata.ProtoBuf.Effect.InvocationKind.AT_MOST_ONCE; return this }
var `conditionKind`: org.jetbrains.kotlin.metadata.ProtoBuf.Effect.EffectConditionKind
get() = field_5_
set(value) { bitField0_ = bitField0_ or 8; field_5_ = value }
fun getConditionKind(): org.jetbrains.kotlin.metadata.ProtoBuf.Effect.EffectConditionKind = field_5_
fun hasConditionKind(): Boolean = (bitField0_ and 8) != 0
fun setConditionKind(value: org.jetbrains.kotlin.metadata.ProtoBuf.Effect.EffectConditionKind): Builder { bitField0_ = bitField0_ or 8; field_5_ = value; return this }
fun clearConditionKind(): Builder { bitField0_ = bitField0_ and -9; field_5_ = org.jetbrains.kotlin.metadata.ProtoBuf.Effect.EffectConditionKind.CONCLUSION_CONDITION; return this }
}
companion object {
val defaultInstance: Effect = Effect()
fun getDefaultInstance(): Effect = defaultInstance
fun newBuilder(): Builder = Builder()
fun newBuilder(prototype: Effect): Builder = Builder().mergeFrom(prototype)
val PARSER: Parser<Effect> = object : AbstractParser<Effect>() {
override fun parsePartialFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Effect =
Effect(input, extensionRegistry)
}
fun parseFrom(bytes: ByteArray): Effect = PARSER.parseFrom(bytes)
fun parseFrom(bytes: ByteArray, registry: ExtensionRegistryLite): Effect = PARSER.parseFrom(bytes, registry)
fun parseFrom(bytes: ByteString): Effect { val input = bytes.newCodedInput(); val msg = PARSER.parsePartialFrom(input, ExtensionRegistryLite.getEmptyRegistry()); input.checkLastTagWas(0); if (!msg.isInitialized()) throw UninitializedMessageException().asInvalidProtocolBufferException().setUnfinishedMessage(msg); return msg }
fun parseFrom(input: CodedInputStream): Effect = PARSER.parseFrom(input)
fun parseFrom(input: CodedInputStream, registry: ExtensionRegistryLite): Effect = PARSER.parseFrom(input, registry)
const val EFFECT_TYPE_FIELD_NUMBER = 1
const val EFFECT_CONSTRUCTOR_ARGUMENT_FIELD_NUMBER = 2
const val CONCLUSION_OF_CONDITIONAL_EFFECT_FIELD_NUMBER = 3
const val KIND_FIELD_NUMBER = 4
const val CONDITION_KIND_FIELD_NUMBER = 5
}}
class Expression : GeneratedMessageLite.ExtendableMessage<Expression> {
enum class ConstantValue(override val number: Int) : Internal.EnumLite {
TRUE(0),
FALSE(1),
NULL(2),
; companion object : Internal.EnumLiteMap<ConstantValue> {
override fun findValueByNumber(number: Int): ConstantValue? = valueOf(number)
fun valueOf(number: Int): ConstantValue? = when (number) {
0 -> TRUE
1 -> FALSE
2 -> NULL
else -> null
}
fun internalGetValueMap(): Internal.EnumLiteMap<ConstantValue> = this
}}
internal var bitField0_: Int = 0
internal var field_1_: Int = 0
internal var field_2_: Int = 0
internal var field_3_: org.jetbrains.kotlin.metadata.ProtoBuf.Expression.ConstantValue = org.jetbrains.kotlin.metadata.ProtoBuf.Expression.ConstantValue.TRUE
internal var field_4_: org.jetbrains.kotlin.metadata.ProtoBuf.Type? = null
internal var field_5_: Int = 0
internal var field_6_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Expression> = emptyList()
internal var field_7_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Expression> = emptyList()
private var memoizedIsInitialized: Byte = -1
private var memoizedSerializedSize: Int = -1
private constructor()
private constructor(builder: Builder) {
this.unknownFields = builder.unknownFields
}
private constructor(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite) {
var mutBit0_ = 0
var unknownBuffer: ByteString.Output? = null
var unknownOut: CodedOutputStream? = null
fun unknownOutput(): CodedOutputStream {
var out = unknownOut
if (out == null) { val buf = ByteString.newOutput(); unknownBuffer = buf; out = CodedOutputStream.newInstance(buf); unknownOut = out }
return out
}
try {
var done = false
while (!done) {
val tag = input.readTag()
when (tag) {
0 -> done = true
8 -> { bitField0_ = bitField0_ or 1; field_1_ = input.readInt32() }
16 -> { bitField0_ = bitField0_ or 2; field_2_ = input.readInt32() }
24 -> {
val raw = input.readEnum()
val parsed = org.jetbrains.kotlin.metadata.ProtoBuf.Expression.ConstantValue.valueOf(raw)
if (parsed == null) { unknownOutput().writeRawVarint32(24); unknownOutput().writeRawVarint32(raw) } else {
bitField0_ = bitField0_ or 4; field_3_ = parsed
}}
34 -> {
var subBuilder: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder? = null
if ((bitField0_ and 8) != 0) subBuilder = field_4_?.toBuilder()
val parsed = input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Type.PARSER, extensionRegistry)
field_4_ = if (subBuilder != null) subBuilder.mergeFrom(parsed).buildPartial() else parsed
bitField0_ = bitField0_ or 8
}
40 -> { bitField0_ = bitField0_ or 16; field_5_ = input.readInt32() }
50 -> {
if ((mutBit0_ and 1) == 0) { field_6_ = ArrayList(); mutBit0_ = mutBit0_ or 1 }
(field_6_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Expression>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Expression.PARSER, extensionRegistry))
}
58 -> {
if ((mutBit0_ and 2) == 0) { field_7_ = ArrayList(); mutBit0_ = mutBit0_ or 2 }
(field_7_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Expression>).add(input.readMessage(org.jetbrains.kotlin.metadata.ProtoBuf.Expression.PARSER, extensionRegistry))
}
else -> if (!input.skipField(tag, unknownOutput())) done = true
}}
} catch (e: InvalidProtocolBufferException) {
throw e.setUnfinishedMessage(this)
} finally {
if ((mutBit0_ and 1) != 0) field_6_ = GeneratedMessageLite.unmodifiableProtoList(field_6_)
if ((mutBit0_ and 2) != 0) field_7_ = GeneratedMessageLite.unmodifiableProtoList(field_7_)
if (unknownBuffer != null) { unknownOut!!.flush(); unknownFields = unknownBuffer.toByteString() }
}}
val `flags`: Int get() = field_1_
fun getFlags(): Int = `flags`
fun hasFlags(): Boolean = (bitField0_ and 1) != 0
val `valueParameterReference`: Int get() = field_2_
fun getValueParameterReference(): Int = `valueParameterReference`
fun hasValueParameterReference(): Boolean = (bitField0_ and 2) != 0
val `constantValue`: org.jetbrains.kotlin.metadata.ProtoBuf.Expression.ConstantValue get() = field_3_
fun getConstantValue(): org.jetbrains.kotlin.metadata.ProtoBuf.Expression.ConstantValue = `constantValue`
fun hasConstantValue(): Boolean = (bitField0_ and 4) != 0
val `isInstanceType`: org.jetbrains.kotlin.metadata.ProtoBuf.Type get() = field_4_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance
fun getIsInstanceType(): org.jetbrains.kotlin.metadata.ProtoBuf.Type = `isInstanceType`
fun hasIsInstanceType(): Boolean = (bitField0_ and 8) != 0
val `isInstanceTypeId`: Int get() = field_5_
fun getIsInstanceTypeId(): Int = `isInstanceTypeId`
fun hasIsInstanceTypeId(): Boolean = (bitField0_ and 16) != 0
val `andArgumentList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Expression> get() = field_6_
val `andArgumentCount`: Int get() = field_6_.size
fun getAndArgumentList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Expression> = field_6_
fun getAndArgumentCount(): Int = field_6_.size
fun getAndArgument(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Expression = field_6_[index]
val `orArgumentList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Expression> get() = field_7_
val `orArgumentCount`: Int get() = field_7_.size
fun getOrArgumentList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Expression> = field_7_
fun getOrArgumentCount(): Int = field_7_.size
fun getOrArgument(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Expression = field_7_[index]
override fun isInitialized(): Boolean {
val isInitialized = memoizedIsInitialized.toInt()
if (isInitialized == 1) return true
if (isInitialized == 0) return false
if ((bitField0_ and 8) != 0 && !getIsInstanceType().isInitialized()) { memoizedIsInitialized = 0; return false }
for (i in 0 until field_6_.size) { if (!field_6_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
for (i in 0 until field_7_.size) { if (!field_7_[i].isInitialized()) { memoizedIsInitialized = 0; return false } }
memoizedIsInitialized = 1
return true
}
override fun writeTo(output: CodedOutputStream) {
if ((bitField0_ and 1) != 0) { output.writeRawVarint32(8); output.writeInt32NoTag(field_1_) }
if ((bitField0_ and 2) != 0) { output.writeRawVarint32(16); output.writeInt32NoTag(field_2_) }
if ((bitField0_ and 4) != 0) { output.writeRawVarint32(24); output.writeInt32NoTag((field_3_).number) }
if ((bitField0_ and 8) != 0) { output.writeRawVarint32(34); output.writeMessageNoTag(getIsInstanceType()) }
if ((bitField0_ and 16) != 0) { output.writeRawVarint32(40); output.writeInt32NoTag(field_5_) }
for (i in 0 until field_6_.size) { output.writeRawVarint32(50); output.writeMessageNoTag(field_6_[i]) }
for (i in 0 until field_7_.size) { output.writeRawVarint32(58); output.writeMessageNoTag(field_7_[i]) }
if (!unknownFields.isEmpty()) unknownFields.writeTo(output)
}
override val serializedSize: Int get() {
val cached = memoizedSerializedSize
if (cached != -1) return cached
var size = 0
if ((bitField0_ and 1) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_1_)
if ((bitField0_ and 2) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_2_)
if ((bitField0_ and 4) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag((field_3_).number)
if ((bitField0_ and 8) != 0) size += 1 + CodedOutputStream.computeMessageSizeNoTag(getIsInstanceType())
if ((bitField0_ and 16) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_5_)
for (i in 0 until field_6_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_6_[i]) }
for (i in 0 until field_7_.size) { size += 1 + CodedOutputStream.computeMessageSizeNoTag(field_7_[i]) }
size += unknownFields.size()
memoizedSerializedSize = size
return size
}
override val defaultInstanceForType: Expression get() = defaultInstance
override fun newBuilderForType(): Builder = Builder()
override fun toBuilder(): Builder = Builder().mergeFrom(this)
class Builder : GeneratedMessageLite.ExtendableBuilder<Expression, Builder>() {
private var bitField0_: Int = 0
private var mutBitField0_: Int = 0
private var field_1_: Int = 0
private var field_2_: Int = 0
private var field_3_: org.jetbrains.kotlin.metadata.ProtoBuf.Expression.ConstantValue = org.jetbrains.kotlin.metadata.ProtoBuf.Expression.ConstantValue.TRUE
private var field_4_: org.jetbrains.kotlin.metadata.ProtoBuf.Type? = null
private var field_5_: Int = 0
private var field_6_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Expression> = emptyList()
private fun ensure_6_Mutable() { if ((mutBitField0_ and 1) == 0) { field_6_ = ArrayList(field_6_); mutBitField0_ = mutBitField0_ or 1 } }
private var field_7_: List<org.jetbrains.kotlin.metadata.ProtoBuf.Expression> = emptyList()
private fun ensure_7_Mutable() { if ((mutBitField0_ and 2) == 0) { field_7_ = ArrayList(field_7_); mutBitField0_ = mutBitField0_ or 2 } }
override val defaultInstanceForType: Expression get() = defaultInstance
override fun clear(): Builder {
bitField0_ = 0
mutBitField0_ = 0
field_1_ = 0
field_2_ = 0
field_3_ = org.jetbrains.kotlin.metadata.ProtoBuf.Expression.ConstantValue.TRUE
field_4_ = null
field_5_ = 0
field_6_ = emptyList()
field_7_ = emptyList()
unknownFields = ByteString.EMPTY
return this
}
override fun clone(): Builder = Builder().mergeFrom(buildPartial())
override fun build(): Expression {
val result = buildPartial()
if (!result.isInitialized()) throw UninitializedMessageException()
return result
}
override fun buildPartial(): Expression {
val result = Expression(this)
val from_bitField0_ = bitField0_
var to_bitField0_ = 0
if ((from_bitField0_ and 1) != 0) to_bitField0_ = to_bitField0_ or 1
result.field_1_ = field_1_
if ((from_bitField0_ and 2) != 0) to_bitField0_ = to_bitField0_ or 2
result.field_2_ = field_2_
if ((from_bitField0_ and 4) != 0) to_bitField0_ = to_bitField0_ or 4
result.field_3_ = field_3_
if ((from_bitField0_ and 8) != 0) to_bitField0_ = to_bitField0_ or 8
result.field_4_ = field_4_
if ((from_bitField0_ and 16) != 0) to_bitField0_ = to_bitField0_ or 16
result.field_5_ = field_5_
result.bitField0_ = to_bitField0_
if ((mutBitField0_ and 1) != 0) { field_6_ = GeneratedMessageLite.unmodifiableProtoList(field_6_); mutBitField0_ = mutBitField0_ and -2 }
result.field_6_ = field_6_
if ((mutBitField0_ and 2) != 0) { field_7_ = GeneratedMessageLite.unmodifiableProtoList(field_7_); mutBitField0_ = mutBitField0_ and -3 }
result.field_7_ = field_7_
return result
}
override fun mergeFrom(other: Expression): Builder {
if (other === defaultInstance) return this
if (other.hasFlags()) setFlags(other.getFlags())
if (other.hasValueParameterReference()) setValueParameterReference(other.getValueParameterReference())
if (other.hasConstantValue()) setConstantValue(other.getConstantValue())
if (other.hasIsInstanceType()) mergeIsInstanceType(other.getIsInstanceType())
if (other.hasIsInstanceTypeId()) setIsInstanceTypeId(other.getIsInstanceTypeId())
if (other.field_6_.isNotEmpty()) {
if (field_6_.isEmpty()) { field_6_ = other.field_6_; mutBitField0_ = mutBitField0_ and -2 } else { ensure_6_Mutable(); (field_6_ as MutableList<Any>).addAll(other.field_6_ as Collection<Any>) }
}
if (other.field_7_.isNotEmpty()) {
if (field_7_.isEmpty()) { field_7_ = other.field_7_; mutBitField0_ = mutBitField0_ and -3 } else { ensure_7_Mutable(); (field_7_ as MutableList<Any>).addAll(other.field_7_ as Collection<Any>) }
}
unknownFields = unknownFields.concat(other.unknownFields)
return this
}
override fun isInitialized(): Boolean {
if ((bitField0_ and 8) != 0 && !getIsInstanceType().isInitialized()) return false
for (i in 0 until field_6_.size) { if (!field_6_[i].isInitialized()) return false }
for (i in 0 until field_7_.size) { if (!field_7_[i].isInitialized()) return false }
return true
}
override fun mergeFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Builder {
var parsedMessage: Expression? = null
try {
parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry)
} catch (e: InvalidProtocolBufferException) {
parsedMessage = e.unfinishedMessage as? Expression
throw e
} finally {
if (parsedMessage != null) mergeFrom(parsedMessage)
}
return this
}
var `flags`: Int
get() = field_1_
set(value) { bitField0_ = bitField0_ or 1; field_1_ = value }
fun getFlags(): Int = field_1_
fun hasFlags(): Boolean = (bitField0_ and 1) != 0
fun setFlags(value: Int): Builder { bitField0_ = bitField0_ or 1; field_1_ = value; return this }
fun clearFlags(): Builder { bitField0_ = bitField0_ and -2; field_1_ = 0; return this }
var `valueParameterReference`: Int
get() = field_2_
set(value) { bitField0_ = bitField0_ or 2; field_2_ = value }
fun getValueParameterReference(): Int = field_2_
fun hasValueParameterReference(): Boolean = (bitField0_ and 2) != 0
fun setValueParameterReference(value: Int): Builder { bitField0_ = bitField0_ or 2; field_2_ = value; return this }
fun clearValueParameterReference(): Builder { bitField0_ = bitField0_ and -3; field_2_ = 0; return this }
var `constantValue`: org.jetbrains.kotlin.metadata.ProtoBuf.Expression.ConstantValue
get() = field_3_
set(value) { bitField0_ = bitField0_ or 4; field_3_ = value }
fun getConstantValue(): org.jetbrains.kotlin.metadata.ProtoBuf.Expression.ConstantValue = field_3_
fun hasConstantValue(): Boolean = (bitField0_ and 4) != 0
fun setConstantValue(value: org.jetbrains.kotlin.metadata.ProtoBuf.Expression.ConstantValue): Builder { bitField0_ = bitField0_ or 4; field_3_ = value; return this }
fun clearConstantValue(): Builder { bitField0_ = bitField0_ and -5; field_3_ = org.jetbrains.kotlin.metadata.ProtoBuf.Expression.ConstantValue.TRUE; return this }
var `isInstanceType`: org.jetbrains.kotlin.metadata.ProtoBuf.Type
get() = field_4_ ?: org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance
set(value) { field_4_ = value; bitField0_ = bitField0_ or 8 }
fun getIsInstanceType(): org.jetbrains.kotlin.metadata.ProtoBuf.Type = `isInstanceType`
fun hasIsInstanceType(): Boolean = (bitField0_ and 8) != 0
fun setIsInstanceType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder { field_4_ = value; bitField0_ = bitField0_ or 8; return this }
fun setIsInstanceType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type.Builder): Builder { field_4_ = value.build(); bitField0_ = bitField0_ or 8; return this }
fun mergeIsInstanceType(value: org.jetbrains.kotlin.metadata.ProtoBuf.Type): Builder {
val cur = field_4_
field_4_ = if ((bitField0_ and 8) != 0 && cur != null && cur !== org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance) org.jetbrains.kotlin.metadata.ProtoBuf.Type.newBuilder(cur).mergeFrom(value).buildPartial() else value
bitField0_ = bitField0_ or 8
return this
}
fun clearIsInstanceType(): Builder { field_4_ = null; bitField0_ = bitField0_ and -9; return this }
var `isInstanceTypeId`: Int
get() = field_5_
set(value) { bitField0_ = bitField0_ or 16; field_5_ = value }
fun getIsInstanceTypeId(): Int = field_5_
fun hasIsInstanceTypeId(): Boolean = (bitField0_ and 16) != 0
fun setIsInstanceTypeId(value: Int): Builder { bitField0_ = bitField0_ or 16; field_5_ = value; return this }
fun clearIsInstanceTypeId(): Builder { bitField0_ = bitField0_ and -17; field_5_ = 0; return this }
val `andArgumentList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Expression> get() = if (field_6_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_6_)
val `andArgumentCount`: Int get() = field_6_.size
fun getAndArgumentList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Expression> = `andArgumentList`
fun getAndArgumentCount(): Int = field_6_.size
fun getAndArgument(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Expression = field_6_[index]
fun addAndArgument(value: org.jetbrains.kotlin.metadata.ProtoBuf.Expression): Builder { ensure_6_Mutable(); (field_6_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Expression>).add(value); return this }
fun setAndArgument(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Expression): Builder { ensure_6_Mutable(); (field_6_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Expression>)[index] = value; return this }
fun addAllAndArgument(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.Expression>): Builder { ensure_6_Mutable(); AbstractMessageLite.Builder.addAll(values, field_6_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Expression>); return this }
fun clearAndArgument(): Builder { field_6_ = emptyList(); mutBitField0_ = mutBitField0_ and -2; return this }
fun addAndArgument(value: org.jetbrains.kotlin.metadata.ProtoBuf.Expression.Builder): Builder = addAndArgument(value.build())
fun setAndArgument(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Expression.Builder): Builder = setAndArgument(index, value.build())
val `orArgumentList`: List<org.jetbrains.kotlin.metadata.ProtoBuf.Expression> get() = if (field_7_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_7_)
val `orArgumentCount`: Int get() = field_7_.size
fun getOrArgumentList(): List<org.jetbrains.kotlin.metadata.ProtoBuf.Expression> = `orArgumentList`
fun getOrArgumentCount(): Int = field_7_.size
fun getOrArgument(index: Int): org.jetbrains.kotlin.metadata.ProtoBuf.Expression = field_7_[index]
fun addOrArgument(value: org.jetbrains.kotlin.metadata.ProtoBuf.Expression): Builder { ensure_7_Mutable(); (field_7_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Expression>).add(value); return this }
fun setOrArgument(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Expression): Builder { ensure_7_Mutable(); (field_7_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Expression>)[index] = value; return this }
fun addAllOrArgument(values: Iterable<org.jetbrains.kotlin.metadata.ProtoBuf.Expression>): Builder { ensure_7_Mutable(); AbstractMessageLite.Builder.addAll(values, field_7_ as MutableList<org.jetbrains.kotlin.metadata.ProtoBuf.Expression>); return this }
fun clearOrArgument(): Builder { field_7_ = emptyList(); mutBitField0_ = mutBitField0_ and -3; return this }
fun addOrArgument(value: org.jetbrains.kotlin.metadata.ProtoBuf.Expression.Builder): Builder = addOrArgument(value.build())
fun setOrArgument(index: Int, value: org.jetbrains.kotlin.metadata.ProtoBuf.Expression.Builder): Builder = setOrArgument(index, value.build())
}
companion object {
val defaultInstance: Expression = Expression()
fun getDefaultInstance(): Expression = defaultInstance
fun newBuilder(): Builder = Builder()
fun newBuilder(prototype: Expression): Builder = Builder().mergeFrom(prototype)
val PARSER: Parser<Expression> = object : AbstractParser<Expression>() {
override fun parsePartialFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Expression =
Expression(input, extensionRegistry)
}
fun parseFrom(bytes: ByteArray): Expression = PARSER.parseFrom(bytes)
fun parseFrom(bytes: ByteArray, registry: ExtensionRegistryLite): Expression = PARSER.parseFrom(bytes, registry)
fun parseFrom(bytes: ByteString): Expression { val input = bytes.newCodedInput(); val msg = PARSER.parsePartialFrom(input, ExtensionRegistryLite.getEmptyRegistry()); input.checkLastTagWas(0); if (!msg.isInitialized()) throw UninitializedMessageException().asInvalidProtocolBufferException().setUnfinishedMessage(msg); return msg }
fun parseFrom(input: CodedInputStream): Expression = PARSER.parseFrom(input)
fun parseFrom(input: CodedInputStream, registry: ExtensionRegistryLite): Expression = PARSER.parseFrom(input, registry)
const val FLAGS_FIELD_NUMBER = 1
const val VALUE_PARAMETER_REFERENCE_FIELD_NUMBER = 2
const val CONSTANT_VALUE_FIELD_NUMBER = 3
const val IS_INSTANCE_TYPE_FIELD_NUMBER = 4
const val IS_INSTANCE_TYPE_ID_FIELD_NUMBER = 5
const val AND_ARGUMENT_FIELD_NUMBER = 6
const val OR_ARGUMENT_FIELD_NUMBER = 7
}}
class CompilerPluginData : GeneratedMessageLite.ExtendableMessage<CompilerPluginData> {
internal var bitField0_: Int = 0
internal var field_1_: Int = 0
internal var field_2_: ByteString = ByteString.EMPTY
private var memoizedIsInitialized: Byte = -1
private var memoizedSerializedSize: Int = -1
private constructor()
private constructor(builder: Builder) {
this.unknownFields = builder.unknownFields
}
private constructor(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite) {
var unknownBuffer: ByteString.Output? = null
var unknownOut: CodedOutputStream? = null
fun unknownOutput(): CodedOutputStream {
var out = unknownOut
if (out == null) { val buf = ByteString.newOutput(); unknownBuffer = buf; out = CodedOutputStream.newInstance(buf); unknownOut = out }
return out
}
try {
var done = false
while (!done) {
val tag = input.readTag()
when (tag) {
0 -> done = true
8 -> { bitField0_ = bitField0_ or 1; field_1_ = input.readInt32() }
18 -> { bitField0_ = bitField0_ or 2; field_2_ = input.readBytes() }
else -> if (!input.skipField(tag, unknownOutput())) done = true
}}
} catch (e: InvalidProtocolBufferException) {
throw e.setUnfinishedMessage(this)
} finally {
if (unknownBuffer != null) { unknownOut!!.flush(); unknownFields = unknownBuffer.toByteString() }
}}
val `pluginId`: Int get() = field_1_
fun getPluginId(): Int = `pluginId`
fun hasPluginId(): Boolean = (bitField0_ and 1) != 0
val `data`: ByteString get() = field_2_
fun getData(): ByteString = `data`
fun hasData(): Boolean = (bitField0_ and 2) != 0
override fun isInitialized(): Boolean {
val isInitialized = memoizedIsInitialized.toInt()
if (isInitialized == 1) return true
if (isInitialized == 0) return false
if ((bitField0_ and 1) == 0) { memoizedIsInitialized = 0; return false }
if ((bitField0_ and 2) == 0) { memoizedIsInitialized = 0; return false }
memoizedIsInitialized = 1
return true
}
override fun writeTo(output: CodedOutputStream) {
if ((bitField0_ and 1) != 0) { output.writeRawVarint32(8); output.writeInt32NoTag(field_1_) }
if ((bitField0_ and 2) != 0) { output.writeRawVarint32(18); output.writeBytesNoTag(field_2_) }
if (!unknownFields.isEmpty()) unknownFields.writeTo(output)
}
override val serializedSize: Int get() {
val cached = memoizedSerializedSize
if (cached != -1) return cached
var size = 0
if ((bitField0_ and 1) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_1_)
if ((bitField0_ and 2) != 0) size += 1 + CodedOutputStream.computeBytesSizeNoTag(field_2_)
size += unknownFields.size()
memoizedSerializedSize = size
return size
}
override val defaultInstanceForType: CompilerPluginData get() = defaultInstance
override fun newBuilderForType(): Builder = Builder()
override fun toBuilder(): Builder = Builder().mergeFrom(this)
class Builder : GeneratedMessageLite.ExtendableBuilder<CompilerPluginData, Builder>() {
private var bitField0_: Int = 0
private var field_1_: Int = 0
private var field_2_: ByteString = ByteString.EMPTY
override val defaultInstanceForType: CompilerPluginData get() = defaultInstance
override fun clear(): Builder {
bitField0_ = 0
field_1_ = 0
field_2_ = ByteString.EMPTY
unknownFields = ByteString.EMPTY
return this
}
override fun clone(): Builder = Builder().mergeFrom(buildPartial())
override fun build(): CompilerPluginData {
val result = buildPartial()
if (!result.isInitialized()) throw UninitializedMessageException()
return result
}
override fun buildPartial(): CompilerPluginData {
val result = CompilerPluginData(this)
val from_bitField0_ = bitField0_
var to_bitField0_ = 0
if ((from_bitField0_ and 1) != 0) to_bitField0_ = to_bitField0_ or 1
result.field_1_ = field_1_
if ((from_bitField0_ and 2) != 0) to_bitField0_ = to_bitField0_ or 2
result.field_2_ = field_2_
result.bitField0_ = to_bitField0_
return result
}
override fun mergeFrom(other: CompilerPluginData): Builder {
if (other === defaultInstance) return this
if (other.hasPluginId()) setPluginId(other.getPluginId())
if (other.hasData()) setData(other.getData())
unknownFields = unknownFields.concat(other.unknownFields)
return this
}
override fun isInitialized(): Boolean {
if ((bitField0_ and 1) == 0) return false
if ((bitField0_ and 2) == 0) return false
return true
}
override fun mergeFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Builder {
var parsedMessage: CompilerPluginData? = null
try {
parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry)
} catch (e: InvalidProtocolBufferException) {
parsedMessage = e.unfinishedMessage as? CompilerPluginData
throw e
} finally {
if (parsedMessage != null) mergeFrom(parsedMessage)
}
return this
}
var `pluginId`: Int
get() = field_1_
set(value) { bitField0_ = bitField0_ or 1; field_1_ = value }
fun getPluginId(): Int = field_1_
fun hasPluginId(): Boolean = (bitField0_ and 1) != 0
fun setPluginId(value: Int): Builder { bitField0_ = bitField0_ or 1; field_1_ = value; return this }
fun clearPluginId(): Builder { bitField0_ = bitField0_ and -2; field_1_ = 0; return this }
var `data`: ByteString
get() = field_2_
set(value) { bitField0_ = bitField0_ or 2; field_2_ = value }
fun getData(): ByteString = field_2_
fun hasData(): Boolean = (bitField0_ and 2) != 0
fun setData(value: ByteString): Builder { bitField0_ = bitField0_ or 2; field_2_ = value; return this }
fun clearData(): Builder { bitField0_ = bitField0_ and -3; field_2_ = ByteString.EMPTY; return this }
}
companion object {
val defaultInstance: CompilerPluginData = CompilerPluginData()
fun getDefaultInstance(): CompilerPluginData = defaultInstance
fun newBuilder(): Builder = Builder()
fun newBuilder(prototype: CompilerPluginData): Builder = Builder().mergeFrom(prototype)
val PARSER: Parser<CompilerPluginData> = object : AbstractParser<CompilerPluginData>() {
override fun parsePartialFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): CompilerPluginData =
CompilerPluginData(input, extensionRegistry)
}
fun parseFrom(bytes: ByteArray): CompilerPluginData = PARSER.parseFrom(bytes)
fun parseFrom(bytes: ByteArray, registry: ExtensionRegistryLite): CompilerPluginData = PARSER.parseFrom(bytes, registry)
fun parseFrom(bytes: ByteString): CompilerPluginData { val input = bytes.newCodedInput(); val msg = PARSER.parsePartialFrom(input, ExtensionRegistryLite.getEmptyRegistry()); input.checkLastTagWas(0); if (!msg.isInitialized()) throw UninitializedMessageException().asInvalidProtocolBufferException().setUnfinishedMessage(msg); return msg }
fun parseFrom(input: CodedInputStream): CompilerPluginData = PARSER.parseFrom(input)
fun parseFrom(input: CodedInputStream, registry: ExtensionRegistryLite): CompilerPluginData = PARSER.parseFrom(input, registry)
const val PLUGIN_ID_FIELD_NUMBER = 1
const val DATA_FIELD_NUMBER = 2
}}
fun registerAllExtensions(registry: ExtensionRegistryLite) {
}
}
