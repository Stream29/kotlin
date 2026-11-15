// Generated from the fixed Kotlin protobuf schemas; do not edit.
// Licensed under Apache-2.0; see ../upstream-license/LICENSE.txt.
@file:Suppress("UNCHECKED_CAST", "UNUSED_PARAMETER", "RemoveRedundantQualifierName", "RedundantVisibilityModifier")
package org.jetbrains.kotlin.library.metadata
import org.jetbrains.kotlin.protobuf.*
object KlibMetadataProtoBuf {
class Header : GeneratedMessageLite.ExtendableMessage<Header> {
internal var bitField0_: Int = 0
internal var field_1_: Any = ""
internal var field_2_: Int = 0
internal var field_7_: LazyStringList = LazyStringArrayList.EMPTY
internal var field_8_: LazyStringList = LazyStringArrayList.EMPTY
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
10 -> { bitField0_ = bitField0_ or 1; field_1_ = input.readBytes() }
16 -> { bitField0_ = bitField0_ or 2; field_2_ = input.readInt32() }
58 -> {
val bs = input.readBytes()
if ((mutBit0_ and 1) == 0) { field_7_ = LazyStringArrayList(); mutBit0_ = mutBit0_ or 1 }
field_7_.add(bs)
}
66 -> {
val bs = input.readBytes()
if ((mutBit0_ and 2) == 0) { field_8_ = LazyStringArrayList(); mutBit0_ = mutBit0_ or 2 }
field_8_.add(bs)
}
else -> if (!input.skipField(tag, unknownOutput())) done = true
}}
} catch (e: InvalidProtocolBufferException) {
throw e.setUnfinishedMessage(this)
} finally {
if ((mutBit0_ and 1) != 0) field_7_ = field_7_.getUnmodifiableView()
if ((mutBit0_ and 2) != 0) field_8_ = field_8_.getUnmodifiableView()
if (unknownBuffer != null) { unknownOut!!.flush(); unknownFields = unknownBuffer.toByteString() }
}}
val `moduleName`: String get() {
val ref = field_1_
if (ref is String) return ref
val bs = ref as ByteString
val s = bs.toStringUtf8()
if (bs.isValidUtf8()) field_1_ = s
return s
}
fun getModuleName(): String = `moduleName`
fun hasModuleName(): Boolean = (bitField0_ and 1) != 0
val `flags`: Int get() = field_2_
fun getFlags(): Int = `flags`
fun hasFlags(): Boolean = (bitField0_ and 2) != 0
val `packageFragmentNameList`: List<String> get() = field_7_
val `packageFragmentNameCount`: Int get() = field_7_.size
fun getPackageFragmentNameList(): List<String> = field_7_
fun getPackageFragmentNameCount(): Int = field_7_.size
fun getPackageFragmentName(index: Int): String = field_7_[index]
val `emptyPackageList`: List<String> get() = field_8_
val `emptyPackageCount`: Int get() = field_8_.size
fun getEmptyPackageList(): List<String> = field_8_
fun getEmptyPackageCount(): Int = field_8_.size
fun getEmptyPackage(index: Int): String = field_8_[index]
override fun isInitialized(): Boolean {
val isInitialized = memoizedIsInitialized.toInt()
if (isInitialized == 1) return true
if (isInitialized == 0) return false
if ((bitField0_ and 1) == 0) { memoizedIsInitialized = 0; return false }
memoizedIsInitialized = 1
return true
}
override fun writeTo(output: CodedOutputStream) {
if ((bitField0_ and 1) != 0) { output.writeRawVarint32(10); if (field_1_ is ByteString) output.writeBytesNoTag(field_1_ as ByteString) else output.writeStringNoTag(field_1_ as String) }
if ((bitField0_ and 2) != 0) { output.writeRawVarint32(16); output.writeInt32NoTag(field_2_) }
for (i in 0 until field_7_.size) {
val elem = field_7_.getUnderlying(i)
output.writeRawVarint32(58)
if (elem is ByteString) output.writeBytesNoTag(elem) else output.writeStringNoTag(elem as String)
}
for (i in 0 until field_8_.size) {
val elem = field_8_.getUnderlying(i)
output.writeRawVarint32(66)
if (elem is ByteString) output.writeBytesNoTag(elem) else output.writeStringNoTag(elem as String)
}
if (!unknownFields.isEmpty()) unknownFields.writeTo(output)
}
override val serializedSize: Int get() {
val cached = memoizedSerializedSize
if (cached != -1) return cached
var size = 0
if ((bitField0_ and 1) != 0) size += 1 + (if (field_1_ is ByteString) CodedOutputStream.computeBytesSizeNoTag(field_1_ as ByteString) else CodedOutputStream.computeStringSizeNoTag(field_1_ as String))
if ((bitField0_ and 2) != 0) size += 1 + CodedOutputStream.computeInt32SizeNoTag(field_2_)
if (field_7_.isNotEmpty()) {
var dataSize = 0
for (i in 0 until field_7_.size) {
val elem = field_7_.getUnderlying(i)
dataSize += if (elem is ByteString) CodedOutputStream.computeBytesSizeNoTag(elem) else CodedOutputStream.computeStringSizeNoTag(elem as String)
}
size += dataSize + 1 * field_7_.size
}
if (field_8_.isNotEmpty()) {
var dataSize = 0
for (i in 0 until field_8_.size) {
val elem = field_8_.getUnderlying(i)
dataSize += if (elem is ByteString) CodedOutputStream.computeBytesSizeNoTag(elem) else CodedOutputStream.computeStringSizeNoTag(elem as String)
}
size += dataSize + 1 * field_8_.size
}
size += unknownFields.size()
memoizedSerializedSize = size
return size
}
override val defaultInstanceForType: Header get() = defaultInstance
override fun newBuilderForType(): Builder = Builder()
override fun toBuilder(): Builder = Builder().mergeFrom(this)
class Builder : GeneratedMessageLite.ExtendableBuilder<Header, Builder>() {
private var bitField0_: Int = 0
private var mutBitField0_: Int = 0
private var field_1_: Any = ""
private var field_2_: Int = 0
private var field_7_: LazyStringList = LazyStringArrayList.EMPTY
private fun ensure_7_Mutable() { if ((mutBitField0_ and 1) == 0) { field_7_ = LazyStringArrayList(field_7_); mutBitField0_ = mutBitField0_ or 1 } }
private var field_8_: LazyStringList = LazyStringArrayList.EMPTY
private fun ensure_8_Mutable() { if ((mutBitField0_ and 2) == 0) { field_8_ = LazyStringArrayList(field_8_); mutBitField0_ = mutBitField0_ or 2 } }
override val defaultInstanceForType: Header get() = defaultInstance
override fun clear(): Builder {
bitField0_ = 0
mutBitField0_ = 0
field_1_ = ""
field_2_ = 0
field_7_ = LazyStringArrayList.EMPTY
field_8_ = LazyStringArrayList.EMPTY
unknownFields = ByteString.EMPTY
return this
}
override fun clone(): Builder = Builder().mergeFrom(buildPartial())
override fun build(): Header {
val result = buildPartial()
if (!result.isInitialized()) throw UninitializedMessageException()
return result
}
override fun buildPartial(): Header {
val result = Header(this)
val from_bitField0_ = bitField0_
var to_bitField0_ = 0
if ((from_bitField0_ and 1) != 0) to_bitField0_ = to_bitField0_ or 1
result.field_1_ = field_1_
if ((from_bitField0_ and 2) != 0) to_bitField0_ = to_bitField0_ or 2
result.field_2_ = field_2_
result.bitField0_ = to_bitField0_
if ((mutBitField0_ and 1) != 0) { field_7_ = field_7_.getUnmodifiableView(); mutBitField0_ = mutBitField0_ and -2 }
result.field_7_ = field_7_
if ((mutBitField0_ and 2) != 0) { field_8_ = field_8_.getUnmodifiableView(); mutBitField0_ = mutBitField0_ and -3 }
result.field_8_ = field_8_
return result
}
override fun mergeFrom(other: Header): Builder {
if (other === defaultInstance) return this
if (other.hasModuleName()) { bitField0_ = bitField0_ or 1; field_1_ = other.field_1_ }
if (other.hasFlags()) setFlags(other.getFlags())
if (other.field_7_.isNotEmpty()) {
if (field_7_.isEmpty()) { field_7_ = other.field_7_; mutBitField0_ = mutBitField0_ and -2 } else { ensure_7_Mutable(); (field_7_ as MutableList<Any>).addAll(other.field_7_ as Collection<Any>) }
}
if (other.field_8_.isNotEmpty()) {
if (field_8_.isEmpty()) { field_8_ = other.field_8_; mutBitField0_ = mutBitField0_ and -3 } else { ensure_8_Mutable(); (field_8_ as MutableList<Any>).addAll(other.field_8_ as Collection<Any>) }
}
unknownFields = unknownFields.concat(other.unknownFields)
return this
}
override fun isInitialized(): Boolean {
if ((bitField0_ and 1) == 0) return false
return true
}
override fun mergeFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Builder {
var parsedMessage: Header? = null
try {
parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry)
} catch (e: InvalidProtocolBufferException) {
parsedMessage = e.unfinishedMessage as? Header
throw e
} finally {
if (parsedMessage != null) mergeFrom(parsedMessage)
}
return this
}
var `moduleName`: String
get() {
val ref = field_1_
if (ref is String) return ref
val bs = ref as ByteString
val s = bs.toStringUtf8()
if (bs.isValidUtf8()) field_1_ = s
return s
}
set(value) { bitField0_ = bitField0_ or 1; field_1_ = value }
fun getModuleName(): String = `moduleName`
fun hasModuleName(): Boolean = (bitField0_ and 1) != 0
fun setModuleName(value: String): Builder { bitField0_ = bitField0_ or 1; field_1_ = value; return this }
fun clearModuleName(): Builder { bitField0_ = bitField0_ and -2; field_1_ = ""; return this }
var `flags`: Int
get() = field_2_
set(value) { bitField0_ = bitField0_ or 2; field_2_ = value }
fun getFlags(): Int = field_2_
fun hasFlags(): Boolean = (bitField0_ and 2) != 0
fun setFlags(value: Int): Builder { bitField0_ = bitField0_ or 2; field_2_ = value; return this }
fun clearFlags(): Builder { bitField0_ = bitField0_ and -3; field_2_ = 0; return this }
val `packageFragmentNameList`: List<String> get() = if (field_7_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_7_)
val `packageFragmentNameCount`: Int get() = field_7_.size
fun getPackageFragmentNameList(): List<String> = `packageFragmentNameList`
fun getPackageFragmentNameCount(): Int = field_7_.size
fun getPackageFragmentName(index: Int): String = field_7_[index]
fun addPackageFragmentName(value: String): Builder { ensure_7_Mutable(); (field_7_ as LazyStringArrayList).add(value); return this }
fun setPackageFragmentName(index: Int, value: String): Builder { ensure_7_Mutable(); (field_7_ as LazyStringArrayList)[index] = value; return this }
fun addAllPackageFragmentName(values: Iterable<String>): Builder { ensure_7_Mutable(); AbstractMessageLite.Builder.addAll(values, field_7_ as LazyStringArrayList); return this }
fun clearPackageFragmentName(): Builder { field_7_ = LazyStringArrayList.EMPTY; mutBitField0_ = mutBitField0_ and -2; return this }
val `emptyPackageList`: List<String> get() = if (field_8_.isEmpty()) emptyList() else GeneratedMessageLite.unmodifiableProtoList(field_8_)
val `emptyPackageCount`: Int get() = field_8_.size
fun getEmptyPackageList(): List<String> = `emptyPackageList`
fun getEmptyPackageCount(): Int = field_8_.size
fun getEmptyPackage(index: Int): String = field_8_[index]
fun addEmptyPackage(value: String): Builder { ensure_8_Mutable(); (field_8_ as LazyStringArrayList).add(value); return this }
fun setEmptyPackage(index: Int, value: String): Builder { ensure_8_Mutable(); (field_8_ as LazyStringArrayList)[index] = value; return this }
fun addAllEmptyPackage(values: Iterable<String>): Builder { ensure_8_Mutable(); AbstractMessageLite.Builder.addAll(values, field_8_ as LazyStringArrayList); return this }
fun clearEmptyPackage(): Builder { field_8_ = LazyStringArrayList.EMPTY; mutBitField0_ = mutBitField0_ and -3; return this }
}
companion object {
val defaultInstance: Header = Header()
fun getDefaultInstance(): Header = defaultInstance
fun newBuilder(): Builder = Builder()
fun newBuilder(prototype: Header): Builder = Builder().mergeFrom(prototype)
val PARSER: Parser<Header> = object : AbstractParser<Header>() {
override fun parsePartialFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): Header =
Header(input, extensionRegistry)
}
fun parseFrom(bytes: ByteArray): Header = PARSER.parseFrom(bytes)
fun parseFrom(bytes: ByteArray, registry: ExtensionRegistryLite): Header = PARSER.parseFrom(bytes, registry)
fun parseFrom(bytes: ByteString): Header { val input = bytes.newCodedInput(); val msg = PARSER.parsePartialFrom(input, ExtensionRegistryLite.getEmptyRegistry()); input.checkLastTagWas(0); if (!msg.isInitialized()) throw UninitializedMessageException().asInvalidProtocolBufferException().setUnfinishedMessage(msg); return msg }
fun parseFrom(input: CodedInputStream): Header = PARSER.parseFrom(input)
fun parseFrom(input: CodedInputStream, registry: ExtensionRegistryLite): Header = PARSER.parseFrom(input, registry)
const val MODULE_NAME_FIELD_NUMBER = 1
const val FLAGS_FIELD_NUMBER = 2
const val PACKAGE_FRAGMENT_NAME_FIELD_NUMBER = 7
const val EMPTY_PACKAGE_FIELD_NUMBER = 8
}}
val packageFqName: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.Package, Int> =
GeneratedMessageLite.newSingularGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.Package.defaultInstance, 0, null, null, 171, WireFormat.FieldType.INT32)
val typeAliasFile: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias, Int> =
GeneratedMessageLite.newSingularGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.TypeAlias.defaultInstance, 0, null, null, 170, WireFormat.FieldType.INT32)
val classAnnotation: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.Class, List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>> =
GeneratedMessageLite.newRepeatedGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.Class.defaultInstance, org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.defaultInstance, null, 170, WireFormat.FieldType.MESSAGE, false)
val classFile: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.Class, Int> =
GeneratedMessageLite.newSingularGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.Class.defaultInstance, 0, null, null, 175, WireFormat.FieldType.INT32)
val classKdoc: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.Class, String> =
GeneratedMessageLite.newSingularGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.Class.defaultInstance, "", null, null, 176, WireFormat.FieldType.STRING)
val constructorAnnotation: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.Constructor, List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>> =
GeneratedMessageLite.newRepeatedGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.Constructor.defaultInstance, org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.defaultInstance, null, 170, WireFormat.FieldType.MESSAGE, false)
val constructorKdoc: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.Constructor, String> =
GeneratedMessageLite.newSingularGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.Constructor.defaultInstance, "", null, null, 173, WireFormat.FieldType.STRING)
val functionAnnotation: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.Function, List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>> =
GeneratedMessageLite.newRepeatedGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.Function.defaultInstance, org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.defaultInstance, null, 170, WireFormat.FieldType.MESSAGE, false)
val functionExtensionReceiverAnnotation: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.Function, List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>> =
GeneratedMessageLite.newRepeatedGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.Function.defaultInstance, org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.defaultInstance, null, 171, WireFormat.FieldType.MESSAGE, false)
val functionFile: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.Function, Int> =
GeneratedMessageLite.newSingularGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.Function.defaultInstance, 0, null, null, 172, WireFormat.FieldType.INT32)
val functionKdoc: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.Function, String> =
GeneratedMessageLite.newSingularGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.Function.defaultInstance, "", null, null, 174, WireFormat.FieldType.STRING)
val propertyAnnotation: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.Property, List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>> =
GeneratedMessageLite.newRepeatedGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.Property.defaultInstance, org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.defaultInstance, null, 170, WireFormat.FieldType.MESSAGE, false)
val propertyGetterAnnotation: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.Property, List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>> =
GeneratedMessageLite.newRepeatedGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.Property.defaultInstance, org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.defaultInstance, null, 177, WireFormat.FieldType.MESSAGE, false)
val propertySetterAnnotation: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.Property, List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>> =
GeneratedMessageLite.newRepeatedGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.Property.defaultInstance, org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.defaultInstance, null, 178, WireFormat.FieldType.MESSAGE, false)
val propertyBackingFieldAnnotation: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.Property, List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>> =
GeneratedMessageLite.newRepeatedGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.Property.defaultInstance, org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.defaultInstance, null, 181, WireFormat.FieldType.MESSAGE, false)
val propertyDelegatedFieldAnnotation: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.Property, List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>> =
GeneratedMessageLite.newRepeatedGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.Property.defaultInstance, org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.defaultInstance, null, 182, WireFormat.FieldType.MESSAGE, false)
val propertyExtensionReceiverAnnotation: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.Property, List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>> =
GeneratedMessageLite.newRepeatedGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.Property.defaultInstance, org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.defaultInstance, null, 183, WireFormat.FieldType.MESSAGE, false)
val compileTimeValue: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.Property, org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value> =
GeneratedMessageLite.newSingularGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.Property.defaultInstance, org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value.defaultInstance, org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value.defaultInstance, null, 173, WireFormat.FieldType.MESSAGE)
val propertyFile: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.Property, Int> =
GeneratedMessageLite.newSingularGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.Property.defaultInstance, 0, null, null, 176, WireFormat.FieldType.INT32)
val propertyKdoc: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.Property, String> =
GeneratedMessageLite.newSingularGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.Property.defaultInstance, "", null, null, 180, WireFormat.FieldType.STRING)
val enumEntryAnnotation: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.EnumEntry, List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>> =
GeneratedMessageLite.newRepeatedGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.EnumEntry.defaultInstance, org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.defaultInstance, null, 170, WireFormat.FieldType.MESSAGE, false)
val enumEntryOrdinal: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.EnumEntry, Int> =
GeneratedMessageLite.newSingularGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.EnumEntry.defaultInstance, 0, null, null, 171, WireFormat.FieldType.INT32)
val parameterAnnotation: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter, List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>> =
GeneratedMessageLite.newRepeatedGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter.defaultInstance, org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.defaultInstance, null, 170, WireFormat.FieldType.MESSAGE, false)
val typeAnnotation: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.Type, List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>> =
GeneratedMessageLite.newRepeatedGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance, org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.defaultInstance, null, 170, WireFormat.FieldType.MESSAGE, false)
val typeParameterAnnotation: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter, List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>> =
GeneratedMessageLite.newRepeatedGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter.defaultInstance, org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.defaultInstance, null, 170, WireFormat.FieldType.MESSAGE, false)
val isEmpty: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.PackageFragment, Boolean> =
GeneratedMessageLite.newSingularGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.PackageFragment.defaultInstance, false, null, null, 172, WireFormat.FieldType.BOOL)
val fqName: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.PackageFragment, String> =
GeneratedMessageLite.newSingularGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.PackageFragment.defaultInstance, "", null, null, 173, WireFormat.FieldType.STRING)
val className: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.PackageFragment, List<Int>> =
GeneratedMessageLite.newRepeatedGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.PackageFragment.defaultInstance, null, null, 174, WireFormat.FieldType.INT32, true)
fun registerAllExtensions(registry: ExtensionRegistryLite) {
registry.add(packageFqName)
registry.add(typeAliasFile)
registry.add(classAnnotation)
registry.add(classFile)
registry.add(classKdoc)
registry.add(constructorAnnotation)
registry.add(constructorKdoc)
registry.add(functionAnnotation)
registry.add(functionExtensionReceiverAnnotation)
registry.add(functionFile)
registry.add(functionKdoc)
registry.add(propertyAnnotation)
registry.add(propertyGetterAnnotation)
registry.add(propertySetterAnnotation)
registry.add(propertyBackingFieldAnnotation)
registry.add(propertyDelegatedFieldAnnotation)
registry.add(propertyExtensionReceiverAnnotation)
registry.add(compileTimeValue)
registry.add(propertyFile)
registry.add(propertyKdoc)
registry.add(enumEntryAnnotation)
registry.add(enumEntryOrdinal)
registry.add(parameterAnnotation)
registry.add(typeAnnotation)
registry.add(typeParameterAnnotation)
registry.add(isEmpty)
registry.add(fqName)
registry.add(className)
}
}
