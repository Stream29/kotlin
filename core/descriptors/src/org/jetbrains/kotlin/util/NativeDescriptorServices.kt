/*
 * Copyright 2026 NativeKotlin contributors.
 * Licensed under the Apache License, Version 2.0.
 */
@file:OptIn(kotlin.concurrent.atomics.ExperimentalAtomicApi::class)

package org.jetbrains.kotlin.util

import kotlin.concurrent.atomics.AtomicReference
import org.jetbrains.kotlin.resolve.ExternalOverridabilityCondition
import org.jetbrains.kotlin.resolve.ExternalOverridabilityCondition.Contract

/**
 * Native replacement for the two JVM ServiceLoader lookups in core.
 * The host must supply the real providers before using service-dependent algorithms.
 * IDs declare provenance; their implementation provenance must also be checked by the host build.
 */
object NativeDescriptorServices {
    enum class ConditionId(val providerClassName: String, val contract: Contract) {
        FIELD("org.jetbrains.kotlin.load.java.FieldOverridabilityCondition", Contract.BOTH),
        ERASED("org.jetbrains.kotlin.load.java.ErasedOverridabilityCondition", Contract.SUCCESS_ONLY),
        JAVA_INCOMPATIBILITY("org.jetbrains.kotlin.load.java.JavaIncompatibilityRulesOverridabilityCondition", Contract.CONFLICTS_ONLY),
        OBJC("org.jetbrains.kotlin.resolve.konan.platform.ObjCOverridabilityCondition", Contract.BOTH)
    }

    data class ConditionRegistration(val id: ConditionId, val implementation: ExternalOverridabilityCondition)

    private class Installation(
        val visibility: ModuleVisibilityHelper,
        val conditions: List<ExternalOverridabilityCondition>
    )

    private val installed = AtomicReference<Installation?>(null)

    /**
     * Publishes both services together exactly once. Invalid input publishes nothing.
     * Order is the fixed stage0 service-file order, not enum/set iteration supplied by a caller.
     */
    fun install(visibility: ModuleVisibilityHelper, conditions: List<ConditionRegistration>) {
        check(installed.load() == null) { "Native descriptor services are already installed" }
        require(visibility !== ModuleVisibilityHelper.EMPTY) { "ModuleVisibilityHelper.EMPTY is not a host provider" }
        val snapshot = conditions.toList()
        require(snapshot.map { it.id } == ConditionId.entries.toList()) {
            "Expected exactly FIELD, ERASED, JAVA_INCOMPATIBILITY, OBJC in that order"
        }
        snapshot.forEachIndexed { index, entry ->
            require(entry.implementation.getContract() == entry.id.contract) {
                "Wrong contract for ${entry.id.providerClassName}"
            }
            require(snapshot.take(index).none { it.implementation === entry.implementation }) {
                "Each condition must have a distinct provider instance"
            }
        }
        val value = Installation(visibility, snapshot.map { it.implementation })
        check(installed.compareAndSet(null, value)) { "Native descriptor services are already installed" }
    }

    private fun requireInstallation(): Installation = checkNotNull(installed.load()) {
        "Native descriptor services are not installed: host visibility and four ordered conditions are required"
    }

    val moduleVisibilityHelper: ModuleVisibilityHelper
        get() = requireInstallation().visibility

    val externalConditions: List<ExternalOverridabilityCondition>
        get() = requireInstallation().conditions.toList()
}
