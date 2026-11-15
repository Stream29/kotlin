/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.konan.library

import org.jetbrains.kotlin.io.propertyList
import org.jetbrains.kotlin.library.KotlinLibrary
import org.jetbrains.kotlin.library.commonizerNativeTargets
import org.jetbrains.kotlin.library.nativeTargets

const val KLIB_PROPERTY_LINKED_OPTS = "linkerOpts"
const val KLIB_PROPERTY_INCLUDED_HEADERS = "includedHeaders"

val KotlinLibrary.includedHeaders: List<String>
    get() = manifestProperties.propertyList(KLIB_PROPERTY_INCLUDED_HEADERS, escapeInQuotes = true)

val KotlinLibrary.supportedTargetList: List<String>
    get() = commonizerNativeTargets?.takeIf(Collection<String>::isNotEmpty) ?: nativeTargets

val KotlinLibrary.linkerOpts: List<String>
    get() = manifestProperties.propertyList(KLIB_PROPERTY_LINKED_OPTS, escapeInQuotes = true)
