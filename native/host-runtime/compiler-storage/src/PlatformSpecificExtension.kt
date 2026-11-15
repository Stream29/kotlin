/*
 * Copyright 2010-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.container

import kotlin.reflect.KClass

/**
 * This is a marker-interface for components which are needed for common resolve
 * facilities (like resolve, or deserialization), but are platform-specific.
 *
 * PlatformSpecificExtensions has to be present in the container in exactly one
 * instance (hence common pattern with providing no-op DEFAULT/EMPTY implementation
 * in the corresponding interface)
 *
 * In multiplatform modules such components require special treatment. Namely,
 * if several components of the same type are provided, then it's not an illegal state;
 * rather, we have to carefully resolve clash on case-by-case basis.
 * See also [PlatformExtensionsClashResolver].
 */
interface PlatformSpecificExtension<S : PlatformSpecificExtension<S>>

/**
 * Allows to specify which [PlatformSpecificExtension] should be used if there were two or more registrations
 * for [applicableTo] class in the container.
 */
abstract class PlatformExtensionsClashResolver<E : PlatformSpecificExtension<E>>(val applicableTo: KClass<E>) {
    abstract fun resolveExtensionsClash(extensions: List<E>): E

    class FallbackToDefault<E : PlatformSpecificExtension<E>>(
        private val defaultValue: E,
        applicableTo: KClass<E>
    ) : PlatformExtensionsClashResolver<E>(applicableTo) {

        override fun resolveExtensionsClash(extensions: List<E>): E = defaultValue
    }

    class FirstWins<E : PlatformSpecificExtension<E>>(applicableTo: KClass<E>) : PlatformExtensionsClashResolver<E>(applicableTo) {

        override fun resolveExtensionsClash(extensions: List<E>): E = extensions.first()
    }
}
