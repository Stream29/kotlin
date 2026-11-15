/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin.container

import kotlin.reflect.KClass

/**
 * Native replacement of Cache.kt's reflection boundary. Entries describe the
 * same constructor, setters, registration hierarchy, and default implementation
 * that traverseClass discovers on the JVM. Container algorithms consume these
 * records unchanged; missing metadata is an error, not an absent service.
 */
data class ClassInfo(
    val constructorInfo: ConstructorInfo?,
    val setterInfos: List<SetterInfo>,
    val registrations: List<ComponentType>,
    val defaultImplementation: KClass<*>?,
    val isAbstract: Boolean = false,
    val isPrimitive: Boolean = false,
    val objectInstance: Any? = null,
)

data class ConstructorInfo(val constructor: StaticConstructor, val parameters: List<ComponentType>)
data class SetterInfo(val method: StaticMethod, val parameters: List<ComponentType>)

sealed interface ComponentType
data class ClassComponentType(val klass: KClass<*>) : ComponentType
data class ParameterizedComponentType(
    val rawType: ComponentType,
    val actualTypeArguments: List<ComponentType>,
    val ownerType: ComponentType? = null,
) : ComponentType
data class WildcardComponentType(
    val upperBounds: List<ComponentType>,
    val lowerBounds: List<ComponentType> = emptyList(),
) : ComponentType

fun KClass<*>.asComponentType(): ComponentType = ClassComponentType(this)

sealed interface StaticCallable {
    val name: String
    val parameters: List<ComponentType>
}

class StaticConstructor(
    override val name: String,
    override val parameters: List<ComponentType>,
    private val factory: (Array<out Any>) -> Any,
) : StaticCallable {
    fun newInstance(arguments: Array<out Any>): Any {
        require(arguments.size == parameters.size) { "Wrong argument count for $name" }
        return factory(arguments)
    }
    override fun toString(): String = name
}

class StaticMethod(
    override val name: String,
    override val parameters: List<ComponentType>,
    private val call: (Any, Array<out Any>) -> Unit,
) : StaticCallable {
    fun invoke(instance: Any, arguments: Array<out Any>) {
        require(arguments.size == parameters.size) { "Wrong argument count for $name" }
        call(instance, arguments)
    }
    override fun toString(): String = name
}

object StaticComponentMetadata {
    private var entries: Map<KClass<*>, ClassInfo> = emptyMap()

    /** Install generated metadata before composing containers or starting workers. */
    fun install(additions: Map<KClass<*>, ClassInfo>) {
        require(additions.keys.none { it in entries }) { "Component metadata already installed" }
        for ((klass, info) in additions) {
            require(klass.asComponentType() in info.registrations) { "Missing self registration for $klass" }
            require(info.constructorInfo?.let { it.parameters == it.constructor.parameters } != false)
            require(info.setterInfos.all { it.parameters == it.method.parameters })
        }
        entries = entries + additions
    }

    internal fun get(klass: KClass<*>): ClassInfo =
        entries[klass] ?: error("No static component metadata for $klass")
}

fun KClass<*>.getInfo(): ClassInfo = StaticComponentMetadata.get(this)

internal fun Appendable.print(value: Any?) { append(value.toString()) }
internal fun Appendable.println(value: Any?) { append(value.toString()); append('\n') }
internal fun Appendable.println() { append('\n') }
