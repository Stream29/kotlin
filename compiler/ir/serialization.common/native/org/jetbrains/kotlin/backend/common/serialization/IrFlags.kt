/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.common.serialization

import org.jetbrains.kotlin.metadata.ProtoBuf
import org.jetbrains.kotlin.metadata.deserialization.Flags
import org.jetbrains.kotlin.metadata.deserialization.Flags.FlagField
import org.jetbrains.kotlin.metadata.deserialization.Flags.BooleanFlagField

class IrFlags private constructor() : Flags() {
    companion object {
        val SUSPEND_TYPE = Flags.SUSPEND_TYPE
        val DEFINITELY_NOT_NULL_TYPE = Flags.DEFINITELY_NOT_NULL_TYPE
        val HAS_ANNOTATIONS = Flags.HAS_ANNOTATIONS
        val VISIBILITY = Flags.VISIBILITY
        val MODALITY = Flags.MODALITY
        val CLASS_KIND = Flags.CLASS_KIND
        val IS_INNER = Flags.IS_INNER
        val IS_DATA = Flags.IS_DATA
        val IS_EXTERNAL_CLASS = Flags.IS_EXTERNAL_CLASS
        val IS_EXPECT_CLASS = Flags.IS_EXPECT_CLASS
        val IS_VALUE_CLASS = Flags.IS_VALUE_CLASS
        val IS_FUN_INTERFACE = Flags.IS_FUN_INTERFACE
        val HAS_ENUM_ENTRIES = Flags.HAS_ENUM_ENTRIES
        val IS_SECONDARY = Flags.IS_SECONDARY
        val IS_CONSTRUCTOR_WITH_NON_STABLE_PARAMETER_NAMES = Flags.IS_CONSTRUCTOR_WITH_NON_STABLE_PARAMETER_NAMES
        val RETURN_VALUE_STATUS_CTOR = Flags.RETURN_VALUE_STATUS_CTOR
        val MEMBER_KIND = Flags.MEMBER_KIND
        val IS_OPERATOR = Flags.IS_OPERATOR
        val IS_INFIX = Flags.IS_INFIX
        val IS_INLINE = Flags.IS_INLINE
        val IS_TAILREC = Flags.IS_TAILREC
        val IS_EXTERNAL_FUNCTION = Flags.IS_EXTERNAL_FUNCTION
        val IS_SUSPEND = Flags.IS_SUSPEND
        val IS_EXPECT_FUNCTION = Flags.IS_EXPECT_FUNCTION
        val IS_FUNCTION_WITH_NON_STABLE_PARAMETER_NAMES = Flags.IS_FUNCTION_WITH_NON_STABLE_PARAMETER_NAMES
        val RETURN_VALUE_STATUS_FUNCTION = Flags.RETURN_VALUE_STATUS_FUNCTION
        val IS_STATIC_FUNCTION = Flags.IS_STATIC_FUNCTION
        val IS_VAR = Flags.IS_VAR
        val HAS_GETTER = Flags.HAS_GETTER
        val HAS_SETTER = Flags.HAS_SETTER
        val IS_CONST = Flags.IS_CONST
        val IS_LATEINIT = Flags.IS_LATEINIT
        val HAS_CONSTANT = Flags.HAS_CONSTANT
        val IS_EXTERNAL_PROPERTY = Flags.IS_EXTERNAL_PROPERTY
        val IS_DELEGATED = Flags.IS_DELEGATED
        val IS_EXPECT_PROPERTY = Flags.IS_EXPECT_PROPERTY
        val RETURN_VALUE_STATUS_PROPERTY = Flags.RETURN_VALUE_STATUS_PROPERTY
        val IS_STATIC_PROPERTY = Flags.IS_STATIC_PROPERTY
        val DECLARES_DEFAULT_VALUE = Flags.DECLARES_DEFAULT_VALUE
        val IS_CROSSINLINE = Flags.IS_CROSSINLINE
        val IS_NOINLINE = Flags.IS_NOINLINE
        val IS_NOT_DEFAULT = Flags.IS_NOT_DEFAULT
        val IS_EXTERNAL_ACCESSOR = Flags.IS_EXTERNAL_ACCESSOR
        val IS_INLINE_ACCESSOR = Flags.IS_INLINE_ACCESSOR
        val IS_NEGATED = Flags.IS_NEGATED
        val IS_NULL_CHECK_PREDICATE = Flags.IS_NULL_CHECK_PREDICATE
        val IS_UNSIGNED = Flags.IS_UNSIGNED
        val IS_PRIMARY: BooleanFlagField = FlagField.booleanAfter(IS_EXPECT_FUNCTION)
        val IS_ACTUAL: BooleanFlagField = FlagField.booleanAfter(VISIBILITY)
        val VARIANCE: FlagField<ProtoBuf.TypeParameter.Variance> = FlagField.after(HAS_ANNOTATIONS, ProtoBuf.TypeParameter.Variance.values())
        val IS_REIFIED: BooleanFlagField = FlagField.booleanAfter(VARIANCE)
        val IS_FINAL: BooleanFlagField = FlagField.booleanAfter(VISIBILITY)
        val IS_EXTERNAL_FIELD: BooleanFlagField = FlagField.booleanAfter(IS_FINAL)
        val IS_STATIC: BooleanFlagField = FlagField.booleanAfter(IS_EXTERNAL_FIELD)
        val IS_FAKE_OVERRIDE: BooleanFlagField = FlagField.booleanAfter(IS_STATIC)
        val IS_HIDDEN: BooleanFlagField = FlagField.booleanAfter(IS_NOINLINE)
        val IS_ASSIGNABLE: BooleanFlagField = FlagField.booleanAfter(IS_HIDDEN)
        val IS_LOCAL_VAR: BooleanFlagField = FlagField.booleanAfter(HAS_ANNOTATIONS)
        val IS_LOCAL_CONST: BooleanFlagField = FlagField.booleanAfter(IS_LOCAL_VAR)
        val IS_LOCAL_LATEINIT: BooleanFlagField = FlagField.booleanAfter(IS_LOCAL_CONST)
        fun getTypeFlags(isSuspend: Boolean, isDefinitelyNotNull: Boolean): Int = Flags.getTypeFlags(isSuspend, isDefinitelyNotNull)
        fun getClassFlags(hasAnnotations: Boolean, visibility: ProtoBuf.Visibility, modality: ProtoBuf.Modality, kind: ProtoBuf.Class.Kind, inner: Boolean, isData: Boolean, isExternal: Boolean, isExpect: Boolean, isValue: Boolean, isFun: Boolean, hasEnumEntries: Boolean): Int = Flags.getClassFlags(hasAnnotations, visibility, modality, kind, inner, isData, isExternal, isExpect, isValue, isFun, hasEnumEntries)
        fun getConstructorFlags(hasAnnotations: Boolean, visibility: ProtoBuf.Visibility, isSecondary: Boolean, hasStableParameterNames: Boolean, returnValueStatus: ProtoBuf.ReturnValueStatus): Int = Flags.getConstructorFlags(hasAnnotations, visibility, isSecondary, hasStableParameterNames, returnValueStatus)
        fun getFunctionFlags(hasAnnotations: Boolean, visibility: ProtoBuf.Visibility, modality: ProtoBuf.Modality, memberKind: ProtoBuf.MemberKind, isOperator: Boolean, isInfix: Boolean, isInline: Boolean, isTailrec: Boolean, isExternal: Boolean, isSuspend: Boolean, isExpect: Boolean, hasStableParameterNames: Boolean, isStatic: Boolean, returnValueStatus: ProtoBuf.ReturnValueStatus): Int = Flags.getFunctionFlags(hasAnnotations, visibility, modality, memberKind, isOperator, isInfix, isInline, isTailrec, isExternal, isSuspend, isExpect, hasStableParameterNames, isStatic, returnValueStatus)
        fun getPropertyFlags(hasAnnotations: Boolean, visibility: ProtoBuf.Visibility, modality: ProtoBuf.Modality, memberKind: ProtoBuf.MemberKind, isVar: Boolean, hasGetter: Boolean, hasSetter: Boolean, hasConstant: Boolean, isConst: Boolean, lateInit: Boolean, isExternal: Boolean, isDelegated: Boolean, isExpect: Boolean, isStatic: Boolean, returnValueStatus: ProtoBuf.ReturnValueStatus): Int = Flags.getPropertyFlags(hasAnnotations, visibility, modality, memberKind, isVar, hasGetter, hasSetter, hasConstant, isConst, lateInit, isExternal, isDelegated, isExpect, isStatic, returnValueStatus)
        fun getAccessorFlags(hasAnnotations: Boolean, visibility: ProtoBuf.Visibility, modality: ProtoBuf.Modality, isNotDefault: Boolean, isExternal: Boolean, isInlineAccessor: Boolean): Int = Flags.getAccessorFlags(hasAnnotations, visibility, modality, isNotDefault, isExternal, isInlineAccessor)
        fun getContractExpressionFlags(isNegated: Boolean, isNullCheckPredicate: Boolean): Int = Flags.getContractExpressionFlags(isNegated, isNullCheckPredicate)
        fun getValueParameterFlags(hasAnnotations: Boolean, declaresDefaultValue: Boolean, isCrossinline: Boolean, isNoinline: Boolean): Int = Flags.getValueParameterFlags(hasAnnotations, declaresDefaultValue, isCrossinline, isNoinline)
        fun getTypeAliasFlags(hasAnnotations: Boolean, visibility: ProtoBuf.Visibility): Int = Flags.getTypeAliasFlags(hasAnnotations, visibility)
        fun getConstructorFlags(hasAnnotations: Boolean, visibility: ProtoBuf.Visibility, isInline: Boolean, isExternal: Boolean, isExpect: Boolean, isPrimary: Boolean): Int =
            HAS_ANNOTATIONS.toFlags(hasAnnotations) or VISIBILITY.toFlags(visibility) or IS_INLINE.toFlags(isInline) or IS_EXTERNAL_FUNCTION.toFlags(isExternal) or IS_EXPECT_FUNCTION.toFlags(isExpect) or IS_PRIMARY.toFlags(isPrimary)
        fun getTypeAliasFlags(hasAnnotations: Boolean, visibility: ProtoBuf.Visibility, isActual: Boolean): Int =
            HAS_ANNOTATIONS.toFlags(hasAnnotations) or VISIBILITY.toFlags(visibility) or IS_ACTUAL.toFlags(isActual)
        fun getTypeParameterFlags(hasAnnotations: Boolean, variance: ProtoBuf.TypeParameter.Variance, isReified: Boolean): Int =
            HAS_ANNOTATIONS.toFlags(hasAnnotations) or VARIANCE.toFlags(variance) or IS_REIFIED.toFlags(isReified)
        fun getFieldFlags(hasAnnotations: Boolean, visibility: ProtoBuf.Visibility, isFinal: Boolean, isExternal: Boolean, isStatic: Boolean): Int =
            HAS_ANNOTATIONS.toFlags(hasAnnotations) or VISIBILITY.toFlags(visibility) or IS_FINAL.toFlags(isFinal) or IS_EXTERNAL_FIELD.toFlags(isExternal) or IS_STATIC.toFlags(isStatic)
        fun getValueParameterFlags(hasAnnotations: Boolean, declaresDefaultValue: Boolean, isCrossinline: Boolean, isNoinline: Boolean, isHidden: Boolean, isAssignable: Boolean): Int =
            HAS_ANNOTATIONS.toFlags(hasAnnotations) or DECLARES_DEFAULT_VALUE.toFlags(declaresDefaultValue) or IS_CROSSINLINE.toFlags(isCrossinline) or IS_NOINLINE.toFlags(isNoinline) or IS_HIDDEN.toFlags(isHidden) or IS_ASSIGNABLE.toFlags(isAssignable)
        fun getLocalFlags(hasAnnotations: Boolean, isVar: Boolean, isConst: Boolean, isLateinit: Boolean): Int =
            HAS_ANNOTATIONS.toFlags(hasAnnotations) or IS_LOCAL_VAR.toFlags(isVar) or IS_LOCAL_CONST.toFlags(isConst) or IS_LOCAL_LATEINIT.toFlags(isLateinit)
    }
}
