/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

package org.jetbrains.kotlin.backend.konan.descriptors

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.konan.library.KONAN_STDLIB_NAME
import org.jetbrains.kotlin.name.Name

internal val String.synthesizedName get() = Name.identifier(this.synthesizedString)

internal val String.synthesizedString get() = "\$$this"

internal val CallableMemberDescriptor.propertyIfAccessor
    get() = if (this is PropertyAccessorDescriptor)
                this.getCorrespondingProperty()
                else this

internal val CallableDescriptor.explicitParameters: List<ParameterDescriptor>
    get() {
        val vps = getValueParameters()
        val result = ArrayList<ParameterDescriptor>(vps.size + 2)
        getDispatchReceiverParameter()?.let { result.add(it) }
        getExtensionReceiverParameter()?.let { result.add(it) }
        result.addAll(vps)
        return result
    }

internal val CallableDescriptor.allParameters: List<ParameterDescriptor>
    get() = if (this is ConstructorDescriptor) {
        listOf(this.getConstructedClass().getThisAsReceiverParameter()) + explicitParameters
    } else {
        explicitParameters
    }

internal fun ModuleDescriptor.isNativeStdlib(): Boolean =
    getName() == Name.special("<$KONAN_STDLIB_NAME>")
