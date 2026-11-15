/*
 * Copyright 2010-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin.builtins

import org.jetbrains.kotlin.container.DefaultImplementation
import org.jetbrains.kotlin.descriptors.ClassDescriptor

// Translated-unverified. The empty Default mapping is upstream behavior.
@DefaultImplementation(impl = PlatformToKotlinClassMapper.Default::class)
interface PlatformToKotlinClassMapper {
    fun mapPlatformClass(classDescriptor: ClassDescriptor): Collection<ClassDescriptor>
    open class Default : PlatformToKotlinClassMapper {
        override fun mapPlatformClass(classDescriptor: ClassDescriptor): Collection<ClassDescriptor> = emptyList()
    }
}
