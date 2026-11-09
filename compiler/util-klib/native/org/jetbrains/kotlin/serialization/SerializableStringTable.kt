/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.serialization

import org.jetbrains.kotlin.metadata.ProtoBuf
import org.jetbrains.kotlin.metadata.ProtoBuf.QualifiedNameTable.QualifiedName
import org.jetbrains.kotlin.metadata.serialization.Interner
import org.jetbrains.kotlin.metadata.serialization.StringTable
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName

open class SerializableStringTable : StringTable {
    protected val strings = Interner<String>()
    private val qualifiedNames = Interner<Wrapper>()

    private class Wrapper(val qualifiedName: QualifiedName.Builder) {
        private val hashCode: Int = run {
            var result = 13
            result = 31 * result + qualifiedName.parentQualifiedName
            result = 31 * result + qualifiedName.shortName
            result = 31 * result + qualifiedName.kind.hashCode()
            result
        }

        override fun equals(other: Any?): Boolean {
            if (other == null || other !is Wrapper) return false
            val otherProto = other.qualifiedName
            return qualifiedName.parentQualifiedName == otherProto.parentQualifiedName
                    && qualifiedName.shortName == otherProto.shortName
                    && qualifiedName.kind == otherProto.kind
        }

        override fun hashCode(): Int = hashCode
    }

    override fun getStringIndex(string: String): Int = strings.intern(string)

    override fun getQualifiedClassNameIndex(className: String, isLocal: Boolean): Int =
        getQualifiedClassNameIndex(ClassId.fromString(className, isLocal))

    fun getQualifiedClassNameIndex(classId: ClassId): Int {
        val builder = QualifiedName.newBuilder()
        builder.kind = if (classId.isLocal) QualifiedName.Kind.LOCAL else QualifiedName.Kind.CLASS

        val outerClassId = classId.outerClassId
        if (outerClassId != null) {
            builder.parentQualifiedName = getQualifiedClassNameIndex(outerClassId)
        } else if (!classId.packageFqName.isRoot) {
            builder.parentQualifiedName = getPackageFqNameIndex(classId.packageFqName)
        }

        builder.shortName = getStringIndex(classId.shortClassName.asString())

        return qualifiedNames.intern(Wrapper(builder))
    }

    override fun getPackageFqNameIndexByString(fqName: String): Int =
        getPackageFqNameIndex(FqName(fqName))

    fun getPackageFqNameIndex(fqName: FqName): Int {
        var result = -1
        for (segment in fqName.pathSegments()) {
            val builder = QualifiedName.newBuilder()
            builder.shortName = getStringIndex(segment.asString())
            builder.kind = QualifiedName.Kind.PACKAGE
            if (result != -1) {
                builder.parentQualifiedName = result
            }
            result = qualifiedNames.intern(Wrapper(builder))
        }
        return result
    }

    fun buildProto(): Pair<ProtoBuf.StringTable, ProtoBuf.QualifiedNameTable> {
        val stringsBuilder = ProtoBuf.StringTable.newBuilder()
        for (simpleName in strings.allInternedObjects) {
            stringsBuilder.addString(simpleName)
        }

        val qualifiedNamesBuilder = ProtoBuf.QualifiedNameTable.newBuilder()
        for (fqName in qualifiedNames.allInternedObjects) {
            qualifiedNamesBuilder.addQualifiedName(fqName.qualifiedName)
        }

        return Pair(stringsBuilder.build(), qualifiedNamesBuilder.build())
    }
}
