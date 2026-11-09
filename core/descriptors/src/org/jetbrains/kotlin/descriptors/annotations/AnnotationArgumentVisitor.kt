/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.descriptors.annotations

import org.jetbrains.kotlin.resolve.constants.*

// Translated-unverified.
interface AnnotationArgumentVisitor<R, D> {
    fun visitLongValue(value: LongValue, data: D): R
    fun visitIntValue(value: IntValue, data: D): R
    fun visitErrorValue(value: ErrorValue, data: D): R
    fun visitShortValue(value: ShortValue, data: D): R
    fun visitByteValue(value: ByteValue, data: D): R
    fun visitDoubleValue(value: DoubleValue, data: D): R
    fun visitFloatValue(value: FloatValue, data: D): R
    fun visitBooleanValue(value: BooleanValue, data: D): R
    fun visitCharValue(value: CharValue, data: D): R
    fun visitStringValue(value: StringValue, data: D): R
    fun visitNullValue(value: NullValue, data: D): R
    fun visitEnumValue(value: EnumValue, data: D): R
    fun visitArrayValue(value: ArrayValue, data: D): R
    fun visitAnnotationValue(value: AnnotationValue, data: D): R
    fun visitKClassValue(value: KClassValue, data: D): R
    fun visitUByteValue(value: UByteValue, data: D): R
    fun visitUShortValue(value: UShortValue, data: D): R
    fun visitUIntValue(value: UIntValue, data: D): R
    fun visitULongValue(value: ULongValue, data: D): R
}
