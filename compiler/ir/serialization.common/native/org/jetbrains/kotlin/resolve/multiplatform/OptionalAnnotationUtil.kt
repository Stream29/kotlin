/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.multiplatform

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.name.StandardClassIds

object OptionalAnnotationUtil {
    val OPTIONAL_EXPECTATION_FQ_NAME = StandardClassIds.Annotations.OptionalExpectation.asSingleFqName()

    fun shouldGenerateExpectClass(descriptor: ClassDescriptor): Boolean {
        return descriptor.getKind() == ClassKind.ANNOTATION_CLASS &&
            descriptor.isExpect() &&
            descriptor.annotations.hasAnnotation(OPTIONAL_EXPECTATION_FQ_NAME)
    }
}
