/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.interpreter.proxy

import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.interpreter.*
import org.jetbrains.kotlin.ir.interpreter.state.Common
import org.jetbrains.kotlin.ir.interpreter.state.State
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.isFakeOverriddenFromAny
import org.jetbrains.kotlin.ir.util.isUnsigned
import org.jetbrains.kotlin.ir.util.nonDispatchParameters
import kotlin.reflect.KClass

internal class CommonProxy private constructor(override val state: Common, override val callInterceptor: CallInterceptor) : Proxy {
    private fun defaultEquals(other: Any?): Boolean = if (other is Proxy) this.state === other.state else false
    private fun defaultHashCode(): Int = state.hashCode()
    private fun defaultToString(): String = "${state.irClass.internalName()}@" + hashCode().toString(16).padStart(8, '0')

    /**
     *  This check used to avoid cyclic calls. For example:
     *     override fun toString(): String = super.toString()
     */
    private fun IrFunction.wasAlreadyCalled(): Boolean {
        val anyParameter = this.getLastOverridden().dispatchReceiverParameter!!.symbol
        val callStack = callInterceptor.environment.callStack
        if (callStack.containsStateInMemory(anyParameter) && callStack.loadState(anyParameter) === state) return true
        return this == callInterceptor.environment.callStack.currentFrameOwner
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false

        val valueArguments = mutableListOf<State>()
        val equalsFun = state.getEqualsFunction()
        if (equalsFun.isFakeOverriddenFromAny() || equalsFun.wasAlreadyCalled()) return defaultEquals(other)

        valueArguments.add(state)
        valueArguments.add(if (other is Proxy) other.state else other as State)

        return callInterceptor.interceptProxy(equalsFun, valueArguments) as Boolean
    }

    override fun hashCode(): Int {
        val valueArguments = mutableListOf<State>()
        val hashCodeFun = state.getHashCodeFunction()
        if (hashCodeFun.isFakeOverriddenFromAny() || hashCodeFun.wasAlreadyCalled()) return defaultHashCode()

        valueArguments.add(state)
        return callInterceptor.interceptProxy(hashCodeFun, valueArguments) as Int
    }

    override fun toString(): String {
        // TODO this check can be dropped after serialization introduction
        // for now declarations in unsigned class don't have bodies and must be treated separately
        if (state.irClass.defaultType.isUnsigned()) {
            return state.unsignedToString()
        }
        val valueArguments = mutableListOf<State>()
        val toStringFun = state.getToStringFunction()
        if (toStringFun.isFakeOverriddenFromAny() || toStringFun.wasAlreadyCalled()) return defaultToString()

        valueArguments.add(state)
        return callInterceptor.interceptProxy(toStringFun, valueArguments) as String
    }

    companion object {
        @Suppress("UNUSED_PARAMETER")
        internal fun Common.asProxy(callInterceptor: CallInterceptor, extendFrom: KClass<*>? = null): Any {
            return CommonProxy(this, callInterceptor)
        }
    }
}