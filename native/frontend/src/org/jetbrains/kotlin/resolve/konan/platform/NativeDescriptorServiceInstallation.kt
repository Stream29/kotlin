/*
 * Copyright 2026 NativeKotlin contributors.
 * Licensed under the Apache License, Version 2.0.
 */
package org.jetbrains.kotlin.resolve.konan.platform

import org.jetbrains.kotlin.load.java.ErasedOverridabilityCondition
import org.jetbrains.kotlin.load.java.FieldOverridabilityCondition
import org.jetbrains.kotlin.load.java.JavaIncompatibilityRulesOverridabilityCondition
import org.jetbrains.kotlin.util.NativeDescriptorServices
import org.jetbrains.kotlin.util.NativeDescriptorServices.ConditionId
import org.jetbrains.kotlin.util.NativeDescriptorServices.ConditionRegistration

/** Install the fixed upstream providers; publication and duplicate checks belong to I0. */
fun installNativeDescriptorServices(configuration: NativeCliVisibilityConfiguration) {
    NativeDescriptorServices.install(
        NativeCliModuleVisibilityHelper(configuration),
        listOf(
            ConditionRegistration(ConditionId.FIELD, FieldOverridabilityCondition()),
            ConditionRegistration(ConditionId.ERASED, ErasedOverridabilityCondition()),
            ConditionRegistration(ConditionId.JAVA_INCOMPATIBILITY, JavaIncompatibilityRulesOverridabilityCondition()),
            ConditionRegistration(ConditionId.OBJC, ObjCOverridabilityCondition())
        )
    )
}
