/*
 * Copyright 2010-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.serialization.deserialization.builtins

import org.jetbrains.kotlin.K1Deprecation
import org.jetbrains.kotlin.serialization.deserialization.ByteArrayInputStream
import org.jetbrains.kotlin.serialization.deserialization.InputStream

@K1Deprecation
class BuiltInsResourceLoader {
    fun loadResource(path: String): InputStream? {
        // Fixed stage0 resources, with one independent byte buffer and cursor for each request.
        // This preserves the upstream non-shared stream lifetime (useCaches=false), without a JAR handle.
        val bytes = GeneratedBuiltInsResourceData.get(path) ?: return null
        return ByteArrayInputStream(bytes)
    }
}
