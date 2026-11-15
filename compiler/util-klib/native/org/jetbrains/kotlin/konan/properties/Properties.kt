/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.konan.properties

import nativekotlin.klib.KlibProperties

class Properties(
    private val map: LinkedHashMap<String, String> = linkedMapOf()
) : MutableMap<String, String> by map {

    fun getProperty(key: String): String? = map[key]

    fun getProperty(key: String, defaultValue: String): String = map[key] ?: defaultValue

    fun setProperty(key: String, value: String): String? = map.put(key, value)

    fun stringPropertyNames(): Set<String> = map.keys.toSet()

    fun propertyNames(): List<String> = map.keys.toList()

    operator fun plusAssign(other: Properties) {
        map.putAll(other.map)
    }

    operator fun plusAssign(other: Map<String, String>) {
        map.putAll(other)
    }

    operator fun plusAssign(pairs: Iterable<Pair<String, String>>) {
        for ((k, v) in pairs) {
            map[k] = v
        }
    }

    fun copy(): Properties = Properties(LinkedHashMap(map))

    fun load(bytes: ByteArray) {
        map.clear()
        map.putAll(KlibProperties.parse(bytes))
    }

    fun toByteArray(): ByteArray = KlibProperties.write(map)

    override fun equals(other: Any?): Boolean =
        this === other || (other is Properties && map == other.map)

    override fun hashCode(): Int = map.hashCode()

    override fun toString(): String = map.toString()
}
