/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.utils

interface Type

interface Constructor<T> {
    val genericParameterTypes: Array<Type>
    val parameterTypes: Array<*>
    val declaringClass: Class<*>
}

interface Class<T> : Type {
    val declaringClass: Class<*>?
    val modifiers: Int
}

object Modifier {
    const val STATIC: Int = 0x00000008
    fun isStatic(modifiers: Int): Boolean = (modifiers and STATIC) != 0
}

private val Constructor<*>.genericParameterTypesWithoutEnclosingThis: Array<Type>
    get() {
        val genericParameterTypes = genericParameterTypes
        val parameterTypes = parameterTypes
        return if (genericParameterTypes.size == parameterTypes.size) {
            val clazz = declaringClass
            if (!Modifier.isStatic(clazz.modifiers) && clazz.declaringClass != null) {
                genericParameterTypes.drop(1).toTypedArray()
            } else {
                genericParameterTypes
            }
        } else {
            genericParameterTypes
        }
    }

private val Constructor<*>.outerClassForInnerClassConstructor: Type?
    get() =
        declaringClass.let { klass ->
            val outerClass = klass.declaringClass
            if (outerClass != null && !Modifier.isStatic(klass.modifiers)) outerClass else null
        }

val Constructor<*>.genericParameterTypesWithEnclosingThis: Array<Type>
    get() {
        val outerThis = outerClassForInnerClassConstructor
        return if (outerThis != null) arrayOf(
            outerThis,
            *genericParameterTypesWithoutEnclosingThis
        ) else genericParameterTypesWithoutEnclosingThis
    }
