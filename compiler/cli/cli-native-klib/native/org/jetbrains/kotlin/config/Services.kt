/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.config

import kotlin.reflect.KClass

class Services private constructor(
    private val map: Map<KClass<*>, Any>,
) {
    @Suppress("UNCHECKED_CAST")
    operator fun <T : Any> get(serviceClass: KClass<T>): T? = map[serviceClass] as? T

    class Builder {
        private val map = LinkedHashMap<KClass<*>, Any>()

        fun <T : Any> register(serviceClass: KClass<T>, instance: T): Builder {
            map[serviceClass] = instance
            return this
        }

        fun build(): Services = Services(map.toMap())
    }

    companion object {
        val EMPTY: Services = Services(emptyMap())
    }
}
