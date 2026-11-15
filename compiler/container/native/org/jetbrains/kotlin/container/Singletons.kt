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
enum class ComponentState {
    Null,
    Initializing,
    Initialized,
    Corrupted,
    Disposing,
    Disposed
}

@K1Deprecation
abstract class SingletonDescriptor(val container: ComponentContainer) : ComponentDescriptor, AutoCloseable {
    private var instance: Any? = null
    protected var state: ComponentState = ComponentState.Null
    private val disposableObjects by lazy { ArrayList<AutoCloseable>() }

    override fun getValue(): Any {
        when {
            state == ComponentState.Corrupted -> throw ContainerConsistencyException("Component descriptor $this is corrupted and cannot be accessed")
            state == ComponentState.Disposed -> throw ContainerConsistencyException("Component descriptor $this is disposed and cannot be accessed")
            instance == null -> createInstance(container)
        }
        return instance!!
    }

    protected fun registerDisposableObject(ownedObject: AutoCloseable) {
        disposableObjects.add(ownedObject)
    }

    protected abstract fun createInstance(context: ValueResolveContext): Any

    private fun createInstance(container: ComponentContainer) {
        when (state) {
            ComponentState.Null -> {
                try {
                    instance = createInstance(container.createResolveContext(this))
                    return
                }
                catch (ex: Throwable) {
                    state = ComponentState.Corrupted
                    for (disposable in disposableObjects)
                        disposable.close()
                    throw ex
                }
            }
            ComponentState.Initializing ->
                throw ContainerConsistencyException("Could not create the component $this because it is being initialized. Do we have undetected circular dependency?")
            ComponentState.Initialized ->
                throw ContainerConsistencyException("Could not get the component $this. Instance is null in Initialized state")
            ComponentState.Corrupted ->
                throw ContainerConsistencyException("Could not get the component $this because it is corrupted")
            ComponentState.Disposing ->
                throw ContainerConsistencyException("Could not get the component $this because it is being disposed")
            ComponentState.Disposed ->
                throw ContainerConsistencyException("Could not get the component $this because it is already disposed")
        }
    }

    private fun disposeImpl() {
        val wereInstance = instance
        state = ComponentState.Disposing
        instance = null // cannot get instance any more
        try {
            if (wereInstance is AutoCloseable)
                wereInstance.close()
            for (disposable in disposableObjects)
                disposable.close()
        }
        catch(ex: Throwable) {
            state = ComponentState.Corrupted
            throw ex
        }
        state = ComponentState.Disposed
    }

    override fun close() {
        when (state) {
            ComponentState.Initialized ->
                disposeImpl()
            ComponentState.Corrupted -> {
            } // corrupted component is in the undefined state, ignore
            ComponentState.Null -> {
            } // it's ok to to remove null component, it may have been never needed

            ComponentState.Initializing ->
                throw ContainerConsistencyException("The component is being initialized and cannot be disposed.")
            ComponentState.Disposing ->
                throw ContainerConsistencyException("The component is already in disposing state.")
            ComponentState.Disposed ->
                throw ContainerConsistencyException("The component has already been destroyed.")
        }
    }

    override val shouldInjectProperties: Boolean
        get() = true
}

@K1Deprecation
open class SingletonTypeComponentDescriptor(container: ComponentContainer, val klass: KClass<*>) : SingletonDescriptor(container) {
    override fun createInstance(context: ValueResolveContext): Any = createInstanceOf(klass, context)
    override fun getRegistrations(): Iterable<ComponentType> = klass.getInfo().registrations

    private fun createInstanceOf(klass: KClass<*>, context: ValueResolveContext): Any {
        val binding = klass.bindToConstructor(container.containerId, context)
        state = ComponentState.Initializing
        for (argumentDescriptor in binding.argumentDescriptors) {
            if (argumentDescriptor is AutoCloseable && argumentDescriptor !is SingletonDescriptor) {
                registerDisposableObject(argumentDescriptor)
            }
        }

        val constructor = binding.constructor
        val arguments = computeArguments(binding.argumentDescriptors)

        val instance = runWithUnwrappingInvocationException { constructor.newInstance(arguments.toTypedArray()) }
        state = ComponentState.Initialized
        return instance
    }

    override fun getDependencies(context: ValueResolveContext): Collection<ComponentType> {
        val classInfo = klass.getInfo()
        val constructorParameters = classInfo.constructorInfo?.parameters.orEmpty()
        val setterInfos = classInfo.setterInfos

        // In most cases, setterInfos is empty (KT-52756)
        return if (setterInfos.isEmpty())
            constructorParameters
        else
            constructorParameters + setterInfos.flatMap { it.parameters }
    }

    override fun toString(): String = "Singleton: ${klass.simpleName}"
}

internal class ClashResolutionDescriptor<E : PlatformSpecificExtension<E>>(
    container: ComponentContainer,
    private val resolver: PlatformExtensionsClashResolver<E>,
    private val clashedComponents: List<ComponentDescriptor>
) : SingletonDescriptor(container) {

    override fun createInstance(context: ValueResolveContext): Any {
        state = ComponentState.Initializing
        @Suppress("UNCHECKED_CAST")
        val extensions = computeArguments(clashedComponents) as List<E>
        val resolution = resolver.resolveExtensionsClash(extensions)
        state = ComponentState.Initialized
        return resolution
    }

    override fun getRegistrations(): Iterable<ComponentType> {
        throw IllegalStateException("Shouldn't be called")
    }

    override fun getDependencies(context: ValueResolveContext): Collection<ComponentType> {
        throw IllegalStateException("Shouldn't be called")
    }
}

@K1Deprecation
class ImplicitSingletonTypeComponentDescriptor(container: ComponentContainer, klass: KClass<*>) : SingletonTypeComponentDescriptor(container, klass) {
    override fun toString(): String {
        return "Implicit: ${klass.simpleName}"
    }
}

@K1Deprecation
class DefaultSingletonTypeComponentDescriptor(container: ComponentContainer, klass: KClass<*>) : SingletonTypeComponentDescriptor(container, klass) {
    override fun toString(): String {
        return "Default: ${klass.simpleName}"
    }
}
