/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.interpreter.proxy.reflection

import kotlin.reflect.KClassifier
import kotlin.reflect.KType
import kotlin.reflect.KVariance

internal enum class KVisibility {
    PUBLIC,
    PROTECTED,
    INTERNAL,
    PRIVATE
}

internal interface KAnnotatedElement {
    val annotations: List<Annotation>
}

internal interface KParameter : KAnnotatedElement {
    val index: Int
    val name: String?
    val type: KType
    val kind: Kind
    val isOptional: Boolean
    val isVararg: Boolean

    enum class Kind {
        INSTANCE,
        EXTENSION_RECEIVER,
        CONTEXT,
        VALUE
    }
}

internal interface KTypeParameter : KClassifier {
    val name: String
    val upperBounds: List<KType>
    val variance: KVariance
    val isReified: Boolean
}

internal interface KCallable<out R> : KAnnotatedElement {
    val name: String
    val parameters: List<KParameter>
    val returnType: KType
    val typeParameters: List<KTypeParameter>
    val visibility: KVisibility?
    val isFinal: Boolean
    val isOpen: Boolean
    val isAbstract: Boolean
    val isSuspend: Boolean

    fun call(vararg args: Any?): R
    fun callBy(args: Map<KParameter, Any?>): R
}

internal interface KFunction<out R> : KCallable<R>, Function<R> {
    val isInline: Boolean
    val isExternal: Boolean
    val isOperator: Boolean
    val isInfix: Boolean
    override val isSuspend: Boolean
}

internal interface KProperty<out V> : KCallable<V> {
    val isLateinit: Boolean
    val isConst: Boolean
    val getter: Getter<V>

    interface Accessor<out V> {
        val property: KProperty<V>
    }

    interface Getter<out V> : Accessor<V>, KFunction<V>
}

internal interface KMutableProperty<V> : KProperty<V> {
    val setter: Setter<V>

    interface Setter<V> : KProperty.Accessor<V>, KFunction<Unit>
}

internal interface KProperty0<out V> : KProperty<V>, () -> V {
    fun get(): V
    fun getDelegate(): Any?
    override val getter: Getter<V>

    interface Getter<out V> : KProperty.Getter<V>, () -> V
}

internal interface KMutableProperty0<V> : KProperty0<V>, KMutableProperty<V> {
    fun set(value: V)
    override val setter: Setter<V>

    interface Setter<V> : KMutableProperty.Setter<V>, (V) -> Unit
}

internal interface KProperty1<T, out V> : KProperty<V>, (T) -> V {
    fun get(receiver: T): V
    fun getDelegate(receiver: T): Any?
    override val getter: Getter<T, V>

    interface Getter<T, out V> : KProperty.Getter<V>, (T) -> V
}

internal interface KMutableProperty1<T, V> : KProperty1<T, V>, KMutableProperty<V> {
    fun set(receiver: T, value: V)
    override val setter: Setter<T, V>

    interface Setter<T, V> : KMutableProperty.Setter<V>, (T, V) -> Unit
}

internal interface KProperty2<D, E, out V> : KProperty<V>, (D, E) -> V {
    fun get(receiver1: D, receiver2: E): V
    fun getDelegate(receiver1: D, receiver2: E): Any?
    override val getter: Getter<D, E, V>

    interface Getter<D, E, out V> : KProperty.Getter<V>, (D, E) -> V
}

internal interface KMutableProperty2<D, E, V> : KProperty2<D, E, V>, KMutableProperty<V> {
    fun set(receiver1: D, receiver2: E, value: V)
    override val setter: Setter<D, E, V>

    interface Setter<D, E, V> : KMutableProperty.Setter<V>, (D, E, V) -> Unit
}
