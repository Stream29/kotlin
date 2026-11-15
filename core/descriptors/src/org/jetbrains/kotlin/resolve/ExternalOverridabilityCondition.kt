/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.resolve

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor

// Translated-unverified.
interface ExternalOverridabilityCondition {
    enum class Result { OVERRIDABLE, INCOMPATIBLE, UNKNOWN }
    enum class Contract { CONFLICTS_ONLY, SUCCESS_ONLY, BOTH }
    fun isOverridable(superDescriptor: CallableDescriptor, subDescriptor: CallableDescriptor, subClassDescriptor: ClassDescriptor?): Result
    fun getContract(): Contract
}

val ExternalOverridabilityCondition.contract: ExternalOverridabilityCondition.Contract get() = getContract()
