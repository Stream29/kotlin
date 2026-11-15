/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.common

import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.diagnostics.impl.BaseDiagnosticsCollector

object CLIConfigurationKeys {
    val DIAGNOSTICS_COLLECTOR = CompilerConfigurationKey.create<BaseDiagnosticsCollector>("DIAGNOSTICS_COLLECTOR")

    val TEST_ENVIRONMENT = CompilerConfigurationKey.create<Boolean>("TEST_ENVIRONMENT")
}

var CompilerConfiguration.diagnosticsCollector: BaseDiagnosticsCollector
    get() = getNotNull(CLIConfigurationKeys.DIAGNOSTICS_COLLECTOR)
    set(value) { put(CLIConfigurationKeys.DIAGNOSTICS_COLLECTOR, value) }

var CompilerConfiguration.testEnvironment: Boolean
    get() = getBoolean(CLIConfigurationKeys.TEST_ENVIRONMENT)
    set(value) { put(CLIConfigurationKeys.TEST_ENVIRONMENT, value) }
