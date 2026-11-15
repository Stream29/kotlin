/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin.load.java

import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.jvm.JvmClassName

object JvmAnnotationNames {
    val METADATA_FQ_NAME = FqName("kotlin.Metadata")
    val METADATA_DESC = "L" + JvmClassName.byFqNameWithoutInnerClasses(METADATA_FQ_NAME).internalName + ";"
    const val METADATA_VERSION_FIELD_NAME = "mv"
    const val KIND_FIELD_NAME = "k"
    const val METADATA_DATA_FIELD_NAME = "d1"
    const val METADATA_STRINGS_FIELD_NAME = "d2"
    const val METADATA_EXTRA_STRING_FIELD_NAME = "xs"
    const val METADATA_PACKAGE_NAME_FIELD_NAME = "pn"
    const val METADATA_MULTIFILE_CLASS_NAME_FIELD_NAME = METADATA_EXTRA_STRING_FIELD_NAME
    const val METADATA_EXTRA_INT_FIELD_NAME = "xi"
    const val METADATA_MULTIFILE_PARTS_INHERIT_FLAG = 1 shl 0
    const val METADATA_PRE_RELEASE_FLAG = 1 shl 1
    const val METADATA_SCRIPT_FLAG = 1 shl 2
    const val METADATA_STRICT_VERSION_SEMANTICS_FLAG = 1 shl 3
    const val METADATA_JVM_IR_FLAG = 1 shl 4
    const val METADATA_JVM_IR_STABLE_ABI_FLAG = 1 shl 5
    const val METADATA_FIR_FLAG = 1 shl 6
    const val METADATA_PUBLIC_ABI_FLAG = 1 shl 7
    const val METADATA_SYNTHETIC_CLASS_VISIBILITY_BIT_FIRST = 8
    const val METADATA_SYNTHETIC_CLASS_VISIBILITY_BIT_LAST = 10
    val DEFAULT_ANNOTATION_MEMBER_NAME = Name.identifier("value")

    // Fixed names, not runtime reflection on JVM classes.
    val TARGET_ANNOTATION = FqName("java.lang.annotation.Target")
    val ELEMENT_TYPE_ENUM = FqName("java.lang.annotation.ElementType")
    val RETENTION_ANNOTATION = FqName("java.lang.annotation.Retention")
    val RETENTION_POLICY_ENUM = FqName("java.lang.annotation.RetentionPolicy")
    val DEPRECATED_ANNOTATION = FqName("java.lang.Deprecated")
    val DOCUMENTED_ANNOTATION = FqName("java.lang.annotation.Documented")
    val REPEATABLE_ANNOTATION = FqName("java.lang.annotation.Repeatable")
    val INHERITED_ANNOTATION = FqName("java.lang.annotation.Inherited")
    val OVERRIDE_ANNOTATION = FqName("java.lang.Override")
    val JETBRAINS_NOT_NULL_ANNOTATION = FqName("org.jetbrains.annotations.NotNull")
    val JETBRAINS_NULLABLE_ANNOTATION = FqName("org.jetbrains.annotations.Nullable")
    val JETBRAINS_MUTABLE_ANNOTATION = FqName("org.jetbrains.annotations.Mutable")
    val JETBRAINS_READONLY_ANNOTATION = FqName("org.jetbrains.annotations.ReadOnly")
    val JETBRAINS_UNMODIFIABLE_ANNOTATION = FqName("org.jetbrains.annotations.Unmodifiable")
    val JETBRAINS_UNMODIFIABLE_VIEW_ANNOTATION = FqName("org.jetbrains.annotations.UnmodifiableView")
    val READONLY_ANNOTATION = FqName("kotlin.annotations.jvm.ReadOnly")
    val MUTABLE_ANNOTATION = FqName("kotlin.annotations.jvm.Mutable")
    val PURELY_IMPLEMENTS_ANNOTATION = FqName("kotlin.jvm.PurelyImplements")
    val KOTLIN_JVM_INTERNAL = FqName("kotlin.jvm.internal")
    const val SOURCE_DEBUG_EXTENSION_DESC = "Lkotlin/jvm/internal/SourceDebugExtension;"
    val ENHANCED_NULLABILITY_ANNOTATION = FqName("kotlin.jvm.internal.EnhancedNullability")
    val ENHANCED_MUTABILITY_ANNOTATION = FqName("kotlin.jvm.internal.EnhancedMutability")
}
