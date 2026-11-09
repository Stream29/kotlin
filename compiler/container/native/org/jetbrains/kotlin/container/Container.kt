/*
 * Copyright 2010-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.container

import kotlin.reflect.KClass

import org.jetbrains.kotlin.K1Deprecation

@K1Deprecation
class ContainerConsistencyException(message: String) : Exception(message)

@K1Deprecation
interface ComponentContainer {
    val containerId: String

    fun createResolveContext(requestingDescriptor: ValueDescriptor): ValueResolveContext
}

@K1Deprecation
interface ComponentProvider {
    fun resolve(request: ComponentType): ValueDescriptor?
    fun <T : Any> create(request: KClass<T>): T
}

@K1Deprecation
object DynamicComponentDescriptor : ValueDescriptor {
    override fun getValue(): Any = throw UnsupportedOperationException()
    override fun toString(): String = "Dynamic"
}

@K1Deprecation
class StorageComponentContainer(
    private val id: String,
    parent: StorageComponentContainer? = null
) : ComponentContainer, ComponentProvider, AutoCloseable {
    val unknownContext: ComponentResolveContext by lazy {
        val parentContext = parent?.let { ComponentResolveContext(it, DynamicComponentDescriptor) }
        ComponentResolveContext(this, DynamicComponentDescriptor, parentContext)
    }
    private val componentStorage: ComponentStorage = ComponentStorage(id, parent?.componentStorage)

    override fun createResolveContext(requestingDescriptor: ValueDescriptor): ValueResolveContext {
        if (requestingDescriptor == DynamicComponentDescriptor) // cache unknown component descriptor
            return unknownContext
        return ComponentResolveContext(this, requestingDescriptor)
    }

    fun compose(): StorageComponentContainer {
        componentStorage.compose(unknownContext)
        return this
    }

    fun dump(printer: Appendable) {
        componentStorage.dump(printer)
    }

    override fun close() = componentStorage.dispose()

    fun resolve(request: ComponentType, context: ValueResolveContext): ValueDescriptor? {
        return componentStorage.resolve(request, context) ?: resolveIterable(request, context)
    }

    override fun resolve(request: ComponentType): ValueDescriptor? {
        return resolve(request, unknownContext)
    }

    private fun resolveIterable(request: ComponentType, context: ValueResolveContext): ValueDescriptor? {
        if (request !is ParameterizedComponentType) return null
        val rawType = request.rawType
        if (rawType != Iterable::class.asComponentType()) return null
        val typeArguments = request.actualTypeArguments
        if (typeArguments.size != 1) return null
        val iterableTypeArgument = typeArguments[0]
        val iterableType = when (iterableTypeArgument) {
            is WildcardComponentType -> {
                val upperBounds = iterableTypeArgument.upperBounds
                if (upperBounds.size != 1) return null
                upperBounds[0]
            }
            is ClassComponentType -> iterableTypeArgument
            is ParameterizedComponentType -> iterableTypeArgument
            else -> return null
        }
        return IterableDescriptor(componentStorage.resolveMultiple(iterableType, context))
    }

    fun resolveMultiple(request: KClass<*>, context: ValueResolveContext = unknownContext): Iterable<ValueDescriptor> {
        return componentStorage.resolveMultiple(request.asComponentType(), context)
    }

    internal fun registerDescriptors(descriptors: List<ComponentDescriptor>): StorageComponentContainer {
        componentStorage.registerDescriptors(unknownContext, descriptors)
        return this
    }

    internal fun registerClashResolvers(resolvers: List<PlatformExtensionsClashResolver<*>>): StorageComponentContainer {
        componentStorage.registerClashResolvers(resolvers)
        return this
    }

    override fun <T : Any> create(request: KClass<T>): T {
        val constructorBinding = request.bindToConstructor(containerId, unknownContext)
        val args = constructorBinding.argumentDescriptors.map { it.getValue() }.toTypedArray()
        return runWithUnwrappingInvocationException {
            @Suppress("UNCHECKED_CAST")
            constructorBinding.constructor.newInstance(args) as T
        }
    }

    override val containerId
        get() = "Container: $id"

    override fun toString() = containerId
}

@K1Deprecation
fun StorageComponentContainer.registerSingleton(klass: KClass<*>): StorageComponentContainer {
    return registerDescriptors(listOf(SingletonTypeComponentDescriptor(this, klass)))
}

@K1Deprecation
fun StorageComponentContainer.registerInstance(instance: Any): StorageComponentContainer {
    return registerDescriptors(listOf(InstanceComponentDescriptor(instance)))
}

@K1Deprecation
inline fun <reified T : Any> StorageComponentContainer.resolve(context: ValueResolveContext = unknownContext): ValueDescriptor? {
    return resolve(T::class.asComponentType(), context)
}

@K1Deprecation
inline fun <reified T : Any> StorageComponentContainer.resolveMultiple(context: ValueResolveContext = unknownContext): Iterable<ValueDescriptor> {
    return resolveMultiple(T::class, context)
}
