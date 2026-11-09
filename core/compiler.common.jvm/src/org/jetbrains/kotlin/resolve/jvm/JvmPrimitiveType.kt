/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.resolve.jvm

import org.jetbrains.kotlin.builtins.PrimitiveType
import org.jetbrains.kotlin.name.FqName

enum class JvmPrimitiveType(
    val primitiveType: PrimitiveType, val javaKeywordName: String, val desc: String, wrapperClassName: String
) {
    BOOLEAN(PrimitiveType.BOOLEAN, "boolean", "Z", "java.lang.Boolean"),
    CHAR(PrimitiveType.CHAR, "char", "C", "java.lang.Character"),
    BYTE(PrimitiveType.BYTE, "byte", "B", "java.lang.Byte"),
    SHORT(PrimitiveType.SHORT, "short", "S", "java.lang.Short"),
    INT(PrimitiveType.INT, "int", "I", "java.lang.Integer"),
    FLOAT(PrimitiveType.FLOAT, "float", "F", "java.lang.Float"),
    LONG(PrimitiveType.LONG, "long", "J", "java.lang.Long"),
    DOUBLE(PrimitiveType.DOUBLE, "double", "D", "java.lang.Double");

    val wrapperFqName = FqName(wrapperClassName)

    companion object {
        private val TYPE_BY_NAME = entries.associateBy { it.javaKeywordName }
        private val TYPE_BY_PRIMITIVE_TYPE = entries.associateBy { it.primitiveType }
        private val TYPE_BY_DESC = entries.associateBy { it.desc }
        private val WRAPPER_CLASS_INTERNAL_NAMES = entries.mapTo(hashSetOf()) { it.wrapperFqName.asString().replace('.', '/') }
        private val OWNER_TO_BOXING_METHOD_DESCRIPTOR = entries.associate {
            val internalName = it.wrapperFqName.asString().replace('.', '/')
            internalName to "(${it.desc})L$internalName;"
        }

        fun isWrapperClassInternalName(internalName: String): Boolean = internalName in WRAPPER_CLASS_INTERNAL_NAMES
        fun isBoxingMethodDescriptor(owner: String, methodDescriptor: String): Boolean =
            methodDescriptor == OWNER_TO_BOXING_METHOD_DESCRIPTOR[owner]
        fun get(name: String): JvmPrimitiveType =
            TYPE_BY_NAME[name] ?: throw AssertionError("Non-primitive type name passed: $name")
        fun get(type: PrimitiveType): JvmPrimitiveType = TYPE_BY_PRIMITIVE_TYPE.getValue(type)
        fun getByDesc(desc: String): JvmPrimitiveType? = TYPE_BY_DESC[desc]
    }
}
