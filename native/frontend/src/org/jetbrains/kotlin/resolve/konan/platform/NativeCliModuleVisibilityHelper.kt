/*
 * Copyright 2026 NativeKotlin contributors.
 * Licensed under the Apache License, Version 2.0.
 */
package org.jetbrains.kotlin.resolve.konan.platform

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.util.ModuleVisibilityHelper

/**
 * Explicit host configuration for the approved standard Native CLI specialization.
 * It is not a replacement for the shared descriptor module-friendship check.
 */
data class NativeCliVisibilityConfiguration(
    val environmentConfigFilesName: String,
    val moduleVisibilityManagerEnabled: Boolean
) {
    companion object {
        fun standardNativeCli(): NativeCliVisibilityConfiguration =
            NativeCliVisibilityConfiguration("NATIVE_CONFIG_FILES", false)
    }
}

/**
 * Specializes ModuleVisibilityHelperImpl's disabled CLI chunk-policy branch.
 *
 * Fixed upstream K2Native uses NATIVE_CONFIG_FILES; KotlinCoreEnvironment registers
 * CliModuleVisibilityManagerImpl(configFiles == JVM_CONFIG_FILES). Thus the original
 * helper either finds no source project or returns at `if (!manager.enabled)`.
 * JVM/IDE/plugin chunk policies are deliberately rejected, not silently approximated.
 *
 * See porting/S0/native-cli-visibility-proof.md and the coordinator's approval.
 */
class NativeCliModuleVisibilityHelper(
    private val configuration: NativeCliVisibilityConfiguration
) : ModuleVisibilityHelper {
    init {
        require(configuration.environmentConfigFilesName == "NATIVE_CONFIG_FILES") {
            "Native CLI descriptor services require NATIVE_CONFIG_FILES; other hosts need their original visibility provider"
        }
        require(!configuration.moduleVisibilityManagerEnabled) {
            "Enabled JVM/IDE chunk visibility policy is outside the Native CLI specialization"
        }
    }

    override fun isInFriendModule(what: DeclarationDescriptor, from: DeclarationDescriptor): Boolean =
        !configuration.moduleVisibilityManagerEnabled
}
