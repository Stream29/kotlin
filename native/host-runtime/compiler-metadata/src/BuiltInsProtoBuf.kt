// Generated from the fixed Kotlin protobuf schemas; do not edit.
// Licensed under Apache-2.0; see ../upstream-license/LICENSE.txt.
@file:Suppress("UNCHECKED_CAST", "UNUSED_PARAMETER", "RemoveRedundantQualifierName", "RedundantVisibilityModifier")
package org.jetbrains.kotlin.metadata.builtins
import org.jetbrains.kotlin.protobuf.*
object BuiltInsProtoBuf {
val packageFqName: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.Package, Int> =
GeneratedMessageLite.newSingularGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.Package.defaultInstance, 0, null, null, 151, WireFormat.FieldType.INT32)
val classAnnotation: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.Class, List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>> =
GeneratedMessageLite.newRepeatedGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.Class.defaultInstance, org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.defaultInstance, null, 150, WireFormat.FieldType.MESSAGE, false)
val constructorAnnotation: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.Constructor, List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>> =
GeneratedMessageLite.newRepeatedGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.Constructor.defaultInstance, org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.defaultInstance, null, 150, WireFormat.FieldType.MESSAGE, false)
val functionAnnotation: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.Function, List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>> =
GeneratedMessageLite.newRepeatedGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.Function.defaultInstance, org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.defaultInstance, null, 150, WireFormat.FieldType.MESSAGE, false)
val propertyAnnotation: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.Property, List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>> =
GeneratedMessageLite.newRepeatedGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.Property.defaultInstance, org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.defaultInstance, null, 150, WireFormat.FieldType.MESSAGE, false)
val propertyGetterAnnotation: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.Property, List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>> =
GeneratedMessageLite.newRepeatedGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.Property.defaultInstance, org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.defaultInstance, null, 152, WireFormat.FieldType.MESSAGE, false)
val propertySetterAnnotation: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.Property, List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>> =
GeneratedMessageLite.newRepeatedGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.Property.defaultInstance, org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.defaultInstance, null, 153, WireFormat.FieldType.MESSAGE, false)
val compileTimeValue: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.Property, org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value> =
GeneratedMessageLite.newSingularGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.Property.defaultInstance, org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value.defaultInstance, org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.Argument.Value.defaultInstance, null, 151, WireFormat.FieldType.MESSAGE)
val enumEntryAnnotation: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.EnumEntry, List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>> =
GeneratedMessageLite.newRepeatedGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.EnumEntry.defaultInstance, org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.defaultInstance, null, 150, WireFormat.FieldType.MESSAGE, false)
val parameterAnnotation: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter, List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>> =
GeneratedMessageLite.newRepeatedGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.ValueParameter.defaultInstance, org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.defaultInstance, null, 150, WireFormat.FieldType.MESSAGE, false)
val typeAnnotation: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.Type, List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>> =
GeneratedMessageLite.newRepeatedGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.Type.defaultInstance, org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.defaultInstance, null, 150, WireFormat.FieldType.MESSAGE, false)
val typeParameterAnnotation: GeneratedMessageLite.GeneratedExtension<org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter, List<org.jetbrains.kotlin.metadata.ProtoBuf.Annotation>> =
GeneratedMessageLite.newRepeatedGeneratedExtension(org.jetbrains.kotlin.metadata.ProtoBuf.TypeParameter.defaultInstance, org.jetbrains.kotlin.metadata.ProtoBuf.Annotation.defaultInstance, null, 150, WireFormat.FieldType.MESSAGE, false)
fun registerAllExtensions(registry: ExtensionRegistryLite) {
registry.add(packageFqName)
registry.add(classAnnotation)
registry.add(constructorAnnotation)
registry.add(functionAnnotation)
registry.add(propertyAnnotation)
registry.add(propertyGetterAnnotation)
registry.add(propertySetterAnnotation)
registry.add(compileTimeValue)
registry.add(enumEntryAnnotation)
registry.add(parameterAnnotation)
registry.add(typeAnnotation)
registry.add(typeParameterAnnotation)
}
}
