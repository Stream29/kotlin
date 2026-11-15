/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.resolve.jvm

import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName

open class JvmClassName private constructor(val internalName: String) {
    private var fqName: FqName? = null

    // Dollar signs are interpreted as inner-class separators, as in upstream.
    val fqNameForClassNameWithoutDollars: FqName
        get() {
            if (fqName == null) fqName = FqName(internalName.replace('$', '.').replace('/', '.'))
            return fqName!!
        }

    // Here dollar signs instead remain part of the identifier.
    val fqNameForTopLevelClassMaybeWithDollars: FqName
        get() = FqName(internalName.replace('/', '.'))

    val packageFqName: FqName
        get() {
            val lastSlash = internalName.lastIndexOf('/')
            return if (lastSlash == -1) FqName.ROOT else FqName(internalName.substring(0, lastSlash).replace('/', '.'))
        }

    override fun toString(): String = internalName
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        return internalName == (other as JvmClassName).internalName
    }
    override fun hashCode(): Int = internalName.hashCode()

    companion object {
        fun byInternalName(internalName: String): JvmClassName = JvmClassName(internalName)
        fun byClassId(classId: ClassId): JvmClassName = JvmClassName(internalNameByClassId(classId))
        fun internalNameByClassId(classId: ClassId): String {
            val packageFqName = classId.packageFqName
            val relativeClassName = classId.relativeClassName.asString().replace('.', '$')
            return if (packageFqName.isRoot) relativeClassName else packageFqName.asString().replace('.', '/') + "/" + relativeClassName
        }
        fun byFqNameWithoutInnerClasses(fqName: FqName): JvmClassName =
            JvmClassName(fqName.asString().replace('.', '/')).also { it.fqName = fqName }
        fun byFqNameWithoutInnerClasses(fqName: String): JvmClassName = byFqNameWithoutInnerClasses(FqName(fqName))
    }
}
