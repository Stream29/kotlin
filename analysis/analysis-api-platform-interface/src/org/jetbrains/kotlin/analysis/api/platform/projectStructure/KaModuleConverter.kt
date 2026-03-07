/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.api.platform.projectStructure

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.serviceOrNull
import com.intellij.openapi.module.Module
import org.jetbrains.kotlin.analysis.api.KaIdeApi
import org.jetbrains.kotlin.analysis.api.platform.KotlinOptionalPlatformComponent
import org.jetbrains.kotlin.analysis.api.projectStructure.KaModule

/**
 * A service for converting [KaModule] to [Module] and vice versa.
 */
@KaIdeApi
public interface KaModuleConverter : KotlinOptionalPlatformComponent {
    /**
     * Returns [KaModule] corresponding to [module] or `null` if not found.
     */
    public fun asKaModule(module: Module): KaModule?

    /**
     * Returns [Module] corresponding to [module] or `null` if not found.
     */
    public fun asOpenApiModule(module: KaModule): Module?

    @KaIdeApi
    public companion object {
        public fun getInstance(): KaModuleConverter? = ApplicationManager.getApplication().serviceOrNull()
    }
}
