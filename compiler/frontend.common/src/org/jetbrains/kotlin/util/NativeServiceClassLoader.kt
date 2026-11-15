/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin.util

import nativekotlin.host.files.NativeFile
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.reflect.KClass

class NativeServiceClass<T : Any>(
    val classifier: KClass<T>,
    val binaryName: String,
    val constructor: (() -> T)?,
)

class NativeServiceClassNotFound(name: String) : Exception(name)
class NativeServiceInstantiationFailure(name: String) : Exception("No public no-argument constructor: $name")

/** Linked class metadata; discovery still reads the actual service files. */
@OptIn(ExperimentalAtomicApi::class)
object NativeServiceClassNames {
    private val names = AtomicReference<Map<KClass<*>, String>>(emptyMap())
    fun install(classes: List<NativeServiceClass<*>>) {
        while (true) {
            val previous = names.load()
            val updated = previous.toMutableMap()
            for (type in classes) {
                val old = updated[type.classifier]
                require(old == null || old == type.binaryName) { "Conflicting binary class identity: ${type.classifier}" }
                require(updated.none { it.key != type.classifier && it.value == type.binaryName }) {
                    "Two linked classifiers have the same binary identity: ${type.binaryName}"
                }
                updated[type.classifier] = type.binaryName
            }
            if (names.compareAndSet(previous, updated.toMap())) return
        }
    }
    fun binaryName(type: KClass<*>): String =
        names.load()[type] ?: throw NativeServiceClassNotFound("No linked binary-name metadata for $type")
}

/**
 * Replaces ClassLoader's loading/constructor boundary, not ServiceLoaderLite's
 * discovery algorithm. Only explicitly linked real constructors are executable.
 */
class NativeServiceClassLoader(
    roots: List<NativeFile>,
    classes: List<NativeServiceClass<*>>,
    private val parent: NativeServiceClassLoader? = null,
) {
    private val localRoots = roots.toList()
    private val definitions: Map<String, NativeServiceClass<*>> = buildMap {
        for (type in classes) {
            require(!containsKey(type.binaryName)) { "Duplicate linked class: ${type.binaryName}" }
            put(type.binaryName, type)
        }
    }
    init {
        NativeServiceClassNames.install(classes)
    }
    val files: List<NativeFile> get() = localRoots.toList()
    private fun findClass(binaryName: String): NativeServiceClass<*>? =
        parent?.findClass(binaryName) ?: definitions[binaryName]
    fun newInstance(binaryName: String): Any {
        val type = findClass(binaryName) ?: throw NativeServiceClassNotFound(binaryName)
        val constructor = type.constructor ?: throw NativeServiceInstantiationFailure(binaryName)
        // Class.newInstance, unlike Constructor.newInstance, propagates the
        // target constructor exception directly. Do not swallow or wrap it.
        val instance = constructor()
        check(type.classifier.isInstance(instance)) { "Constructor metadata returned the wrong class for $binaryName" }
        return instance
    }
}
