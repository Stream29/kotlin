/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package org.jetbrains.kotlin.load.kotlin.header

import kotlinx.cinterop.toKString
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.load.java.JvmAnnotationNames
import org.jetbrains.kotlin.load.kotlin.KotlinJvmBinaryClass.*
import org.jetbrains.kotlin.load.kotlin.header.KotlinClassHeader.Kind.*
import org.jetbrains.kotlin.metadata.deserialization.MetadataVersion
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.constants.ClassLiteralValue
import platform.posix.getenv

class ReadKotlinClassHeaderAnnotationVisitor : AnnotationVisitor {
    private var metadataVersionArray: IntArray? = null
    private var extraString: String? = null
    private var extraInt = 0
    private var packageName: String? = null
    private var data: Array<String>? = null
    private var strings: Array<String>? = null
    private var incompatibleData: Array<String>? = null
    private var headerKind: KotlinClassHeader.Kind? = null

    fun createHeaderWithDefaultMetadataVersion(): KotlinClassHeader? = createHeader(MetadataVersion.INSTANCE)

    fun createHeader(metadataVersionFromLanguageVersion: MetadataVersion): KotlinClassHeader? {
        val kind = headerKind ?: return null
        val versionArray = metadataVersionArray ?: return null
        val metadataVersion = MetadataVersion(
            versionArray, (extraInt and JvmAnnotationNames.METADATA_STRICT_VERSION_SEMANTICS_FLAG) != 0
        )
        if (!metadataVersion.isCompatible(metadataVersionFromLanguageVersion)) {
            incompatibleData = data
            data = null
        } else if (shouldHaveData() && data == null) {
            return null
        }
        return KotlinClassHeader(kind, metadataVersion, data, incompatibleData, strings, extraString, extraInt, packageName)
    }

    private fun shouldHaveData(): Boolean =
        headerKind == CLASS || headerKind == FILE_FACADE || headerKind == MULTIFILE_CLASS_PART

    override fun visitAnnotation(classId: ClassId, source: SourceElement): AnnotationArgumentVisitor? {
        if (classId.asSingleFqName() == JvmAnnotationNames.METADATA_FQ_NAME) return KotlinMetadataArgumentVisitor()
        if (IGNORE_OLD_METADATA) return null
        if (headerKind != null) return null
        val newKind = HEADER_KINDS[classId]
        if (newKind != null) {
            headerKind = newKind
            return OldDeprecatedAnnotationArgumentVisitor()
        }
        return null
    }

    override fun visitEnd() {}

    private inner class KotlinMetadataArgumentVisitor : AnnotationArgumentVisitor {
        override fun visit(name: Name?, value: Any?) {
            if (name == null) return
            when (name.asString()) {
                JvmAnnotationNames.KIND_FIELD_NAME -> if (value is Int) headerKind = KotlinClassHeader.Kind.getById(value)
                JvmAnnotationNames.METADATA_VERSION_FIELD_NAME -> if (value is IntArray) metadataVersionArray = value
                JvmAnnotationNames.METADATA_EXTRA_STRING_FIELD_NAME -> if (value is String && value.isNotEmpty()) extraString = value
                JvmAnnotationNames.METADATA_EXTRA_INT_FIELD_NAME -> if (value is Int) extraInt = value
                JvmAnnotationNames.METADATA_PACKAGE_NAME_FIELD_NAME -> if (value is String && value.isNotEmpty()) packageName = value
            }
        }

        override fun visitClassLiteral(name: Name?, value: ClassLiteralValue) {}
        override fun visitArray(name: Name?): AnnotationArrayArgumentVisitor? = when (name?.asString()) {
            JvmAnnotationNames.METADATA_DATA_FIELD_NAME -> dataArrayVisitor()
            JvmAnnotationNames.METADATA_STRINGS_FIELD_NAME -> stringsArrayVisitor()
            else -> null
        }

        private fun dataArrayVisitor(): AnnotationArrayArgumentVisitor = object : CollectStringArrayAnnotationVisitor() {
            override fun visitEnd(result: Array<String>) { data = result }
        }
        private fun stringsArrayVisitor(): AnnotationArrayArgumentVisitor = object : CollectStringArrayAnnotationVisitor() {
            override fun visitEnd(result: Array<String>) { strings = result }
        }
        override fun visitEnum(name: Name?, enumClassId: ClassId, enumEntryName: Name) {}
        override fun visitAnnotation(name: Name?, classId: ClassId): AnnotationArgumentVisitor? = null
        override fun visitEnd() {}
    }

    private inner class OldDeprecatedAnnotationArgumentVisitor : AnnotationArgumentVisitor {
        override fun visit(name: Name?, value: Any?) {
            if (name == null) return
            when (name.asString()) {
                "version" -> if (value is IntArray) metadataVersionArray = value
                "multifileClassName" -> extraString = value as? String
            }
        }
        override fun visitClassLiteral(name: Name?, value: ClassLiteralValue) {}
        override fun visitArray(name: Name?): AnnotationArrayArgumentVisitor? = when (name?.asString()) {
            "data", "filePartClassNames" -> dataArrayVisitor()
            "strings" -> stringsArrayVisitor()
            else -> null
        }
        private fun dataArrayVisitor(): AnnotationArrayArgumentVisitor = object : CollectStringArrayAnnotationVisitor() {
            override fun visitEnd(result: Array<String>) { data = result }
        }
        private fun stringsArrayVisitor(): AnnotationArrayArgumentVisitor = object : CollectStringArrayAnnotationVisitor() {
            override fun visitEnd(result: Array<String>) { strings = result }
        }
        override fun visitEnum(name: Name?, enumClassId: ClassId, enumEntryName: Name) {}
        override fun visitAnnotation(name: Name?, classId: ClassId): AnnotationArgumentVisitor? = null
        override fun visitEnd() {}
    }

    private abstract class CollectStringArrayAnnotationVisitor : AnnotationArrayArgumentVisitor {
        private val strings = arrayListOf<String>()
        override fun visit(value: Any?) { if (value is String) strings.add(value) }
        override fun visitEnum(enumClassId: ClassId, enumEntryName: Name) {}
        override fun visitClassLiteral(value: ClassLiteralValue) {}
        override fun visitAnnotation(classId: ClassId): AnnotationArgumentVisitor? = null
        override fun visitEnd() = visitEnd(strings.toTypedArray())
        protected abstract fun visitEnd(result: Array<String>)
    }

    companion object {
        // Native host boundary: the exact upstream property name is read from the process environment.
        private val IGNORE_OLD_METADATA = getenv("kotlin.ignore.old.metadata")?.toKString() == "true"
        private val HEADER_KINDS = mapOf(
            ClassId.topLevel(FqName("kotlin.jvm.internal.KotlinClass")) to CLASS,
            ClassId.topLevel(FqName("kotlin.jvm.internal.KotlinFileFacade")) to FILE_FACADE,
            ClassId.topLevel(FqName("kotlin.jvm.internal.KotlinMultifileClass")) to MULTIFILE_CLASS,
            ClassId.topLevel(FqName("kotlin.jvm.internal.KotlinMultifileClassPart")) to MULTIFILE_CLASS_PART,
            ClassId.topLevel(FqName("kotlin.jvm.internal.KotlinSyntheticClass")) to SYNTHETIC_CLASS
        )
    }
}
