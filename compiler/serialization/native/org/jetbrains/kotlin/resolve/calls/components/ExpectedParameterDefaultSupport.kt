/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("PackageDirectoryMismatch")

package org.jetbrains.kotlin.resolve.calls.components

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor

val ValueParameterDescriptor.isActualParameterWithAnyExpectedDefault: Boolean
    get() = getContainingDeclaration().let {
        val index = (it as? CallableDescriptor)?.getValueParameters()?.indexOf(this) ?: -1
        it is CallableMemberDescriptor && it.isActual() && index >= 0 && it.getOverriddenDescriptors().any { overridden ->
            overridden.getValueParameters().getOrNull(index)?.declaresDefaultValue() == true
        }
    }
