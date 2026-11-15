/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.load.java.descriptors

import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.CallableDescriptor.UserDataKey
import org.jetbrains.kotlin.types.KotlinType

interface JavaCallableMemberDescriptor : CallableMemberDescriptor {
    fun enhance(
        enhancedReceiverType: KotlinType?,
        enhancedValueParameterTypes: List<KotlinType>,
        enhancedReturnType: KotlinType,
        additionalUserData: Pair<UserDataKey<*>, Any?>?
    ): JavaCallableMemberDescriptor
}
