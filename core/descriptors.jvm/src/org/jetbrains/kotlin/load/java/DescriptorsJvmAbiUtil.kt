/*
 * Copyright 2000-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin.load.java

import org.jetbrains.kotlin.builtins.CompanionObjectMapping
import org.jetbrains.kotlin.builtins.isMappedIntrinsicCompanionObject
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.load.java.JvmAbi.JVM_FIELD_ANNOTATION_FQ_NAME
import org.jetbrains.kotlin.resolve.DescriptorUtils.Companion.isClassOrEnumClass
import org.jetbrains.kotlin.resolve.DescriptorUtils.Companion.isCompanionObject

object DescriptorsJvmAbiUtil {
    fun isPropertyWithBackingFieldInOuterClass(propertyDescriptor: PropertyDescriptor): Boolean {
        if (propertyDescriptor.getKind() == CallableMemberDescriptor.Kind.FAKE_OVERRIDE) return false
        if (isClassCompanionObjectWithBackingFieldsInOuter(propertyDescriptor.getContainingDeclaration())) return true
        return isCompanionObject(propertyDescriptor.getContainingDeclaration()) && hasJvmFieldAnnotation(propertyDescriptor)
    }

    fun isClassCompanionObjectWithBackingFieldsInOuter(companionObject: DeclarationDescriptor): Boolean =
        isCompanionObject(companionObject) && isClassOrEnumClass(companionObject.getContainingDeclaration()) &&
                !isMappedIntrinsicCompanionObject(companionObject as ClassDescriptor)

    fun isMappedIntrinsicCompanionObject(companionObject: ClassDescriptor): Boolean =
        CompanionObjectMapping.isMappedIntrinsicCompanionObject(companionObject)

    fun hasJvmFieldAnnotation(memberDescriptor: CallableMemberDescriptor): Boolean {
        if (memberDescriptor is PropertyDescriptor) {
            val field = memberDescriptor.getBackingField()
            if (field != null && field.annotations.hasAnnotation(JVM_FIELD_ANNOTATION_FQ_NAME)) return true
        }
        return memberDescriptor.annotations.hasAnnotation(JVM_FIELD_ANNOTATION_FQ_NAME)
    }
}
