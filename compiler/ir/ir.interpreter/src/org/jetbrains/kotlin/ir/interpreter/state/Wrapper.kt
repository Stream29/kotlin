/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.interpreter.state

import org.jetbrains.kotlin.builtins.StandardNames
import org.jetbrains.kotlin.builtins.functions.BuiltInFunctionArity
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.interpreter.*
import org.jetbrains.kotlin.ir.interpreter.stack.Field
import org.jetbrains.kotlin.ir.interpreter.stack.Fields
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.util.isNullable
import org.jetbrains.kotlin.util.capitalizeDecapitalize.capitalizeAsciiOnly
import kotlin.reflect.KClass

internal class Wrapper(val value: Any, override val irClass: IrClass, environment: IrInterpreterEnvironment) : Complex {
    override val fields: Fields = mutableMapOf()

    override var superWrapperClass: Wrapper? = null
    override var outerClass: Field? = null

    private val receiverClass = irClass.defaultType.getClass(true)

    init {
        val kClass = value::class
        if (kClass == HashMap::class || kClass == LinkedHashMap::class) {
            val mutableMap = irClass.superTypes.mapNotNull { it.classOrNull?.owner }
                .singleOrNull { it.name == StandardNames.FqNames.mutableMap.shortName() }
            val entryIrClass = mutableMap?.declarations?.filterIsInstance<IrClass>()?.singleOrNull()
            if (entryIrClass != null) {
                environment.javaClassToIrClass[Map.Entry::class] = entryIrClass
            }
        }
        if (environment.javaClassToIrClass[kClass].let { it == null || irClass.isSubclassOf(it) }) {
            environment.javaClassToIrClass[kClass] = irClass
        }
    }

    override fun getIrFunctionByIrCall(expression: IrCall): IrFunction? = null

    fun getMethod(irFunction: IrFunction): MethodHandle? {
        val propertyName = irFunction.property?.name?.asString()
        val propertyCall = propertyName?.let { "get${it.capitalizeAsciiOnly()}" }
        val intrinsicName = getJavaOriginalName(irFunction)
        val methodName = intrinsicName ?: propertyCall ?: irFunction.name.toString()
        val methodType = irFunction.getMethodType()
        return NativeMethodHandles.findVirtual(receiverClass, methodName, methodType)
    }

    // This method is used to get correct java method name
    private fun getJavaOriginalName(irFunction: IrFunction): String? {
        return when (irFunction.getLastOverridden().fqName) {
            "kotlin.collections.Map.<get-entries>" -> "entrySet"
            "kotlin.collections.Map.<get-keys>" -> "keySet"
            "kotlin.CharSequence.get" -> "charAt"
            "kotlin.collections.MutableList.removeAt" -> "remove"
            else -> null
        }
    }

    override fun toString(): String {
        return value.toString()
    }

    companion object {
        private val companionObjectValue = mapOf<String, Any>("kotlin.text.Regex\$Companion" to Regex.Companion)

        // TODO remove later; used for tests only
        private val intrinsicClasses = setOf(
            "kotlin.text.StringBuilder", "kotlin.Pair", "kotlin.collections.ArrayList",
            "kotlin.collections.HashMap", "kotlin.collections.LinkedHashMap",
            "kotlin.collections.HashSet", "kotlin.collections.LinkedHashSet",
            "kotlin.text.RegexOption", "kotlin.text.Regex", "kotlin.text.Regex.Companion", "kotlin.text.MatchGroup",
        )

        private val intrinsicJavaClasses = setOf(
            "java.lang.StringBuilder", "java.util.ArrayList",
            "java.util.LinkedHashMap", "java.util.LinkedHashSet",
            "java.lang.Exception", "java.util.NoSuchElementException", "java.lang.NullPointerException",
            "java.lang.IllegalArgumentException", "java.lang.ArithmeticException", "java.lang.UnsupportedOperationException",
        )

        private val intrinsicFunctionToHandler = mapOf(
            "Array.kotlin.collections.asList()" to "kotlin.collections.ArraysKt",
            "kotlin.collections.mutableListOf(Array)" to "kotlin.collections.CollectionsKt",
            "kotlin.collections.arrayListOf(Array)" to "kotlin.collections.CollectionsKt",
            "Char.kotlin.text.isWhitespace()" to "kotlin.text.CharsKt",
            "Array.kotlin.collections.toMutableList()" to "kotlin.collections.ArraysKt",
            "Array.kotlin.collections.copyToArrayOfAny(Boolean)" to "kotlin.collections.CollectionsKt",
        )

        private val ranges = setOf("kotlin.ranges.CharRange", "kotlin.ranges.IntRange", "kotlin.ranges.LongRange")

        private fun IrFunction.getSignature(fqName: String = this.fqName): String {
            val [receiverParameters, otherParameters] = parameters
                .partition { it.kind == IrParameterKind.DispatchReceiver || it.kind == IrParameterKind.ExtensionReceiver }
            val receiver = receiverParameters.firstOrNull()?.type?.getOnlyName()?.let { "$it." } ?: ""
            return otherParameters.joinToString(prefix = "$receiver$fqName(", postfix = ")") { it.type.getOnlyName() }
        }

        private fun IrFunction.getJvmClassName(): String? {
            return intrinsicFunctionToHandler[this.getSignature()]
        }

        fun mustBeHandledWithWrapper(declaration: IrDeclarationWithName): Boolean {
            val fqName = declaration.fqName
            return when {
                declaration is IrFunction -> declaration.getSignature(fqName) in intrinsicFunctionToHandler
                fqName in ranges && (declaration as IrClass).primaryConstructor?.body == null -> true
                else -> fqName in intrinsicClasses || fqName in intrinsicJavaClasses
            }
        }

        fun getReflectionMethod(irFunction: IrFunction): MethodHandle {
            val receiverClass = irFunction.dispatchReceiverParameter!!.type.getClass(asObject = true)
            val methodType = irFunction.getMethodType()
            val methodName = when (irFunction) {
                is IrSimpleFunction -> {
                    val property = irFunction.property
                    when {
                        property?.getter == irFunction -> "get${property.name.asString().capitalizeAsciiOnly()}"
                        property?.setter == irFunction -> "set${property.name.asString().capitalizeAsciiOnly()}"
                        else -> irFunction.name.asString()
                    }
                }
                is IrConstructor -> irFunction.name.asString()
            }
            return NativeMethodHandles.findVirtual(receiverClass, methodName, methodType)!!
        }

        fun getCompanionObject(irClass: IrClass, environment: IrInterpreterEnvironment): Wrapper {
            val objectName = irClass.internalName()
            val objectValue = companionObjectValue[objectName] ?: throw InternalError("Companion object $objectName cannot be interpreted")
            return Wrapper(objectValue, irClass, environment)
        }

        fun getConstructorMethod(irConstructor: IrFunction): MethodHandle? {
            val intrinsicValue = irConstructor.parentAsClass.internalName()
            if (intrinsicValue == "kotlin.Char" || intrinsicValue == "kotlin.Long") return null // used in JS, must be handled as intrinsics

            val methodType = irConstructor.getMethodType()
            return NativeMethodHandles.findConstructor(irConstructor.returnType.getClass(true), methodType)
        }

        fun getStaticMethod(irFunction: IrFunction): MethodHandle? {
            val intrinsicName = irFunction.getJvmClassName()
            if (intrinsicName?.isEmpty() != false) return null

            val methodType = irFunction.getMethodType()
            return NativeMethodHandles.findStatic(Any::class, irFunction.name.asString(), methodType)
        }

        fun getStaticGetter(field: IrField): MethodHandle {
            val jvmClass = field.parentAsClass.defaultType.getClass(true)
            val returnType = field.type.let { it.getClass(it.isNullable()) }
            return NativeMethodHandles.findStaticGetter(jvmClass, field.name.asString(), returnType)
        }

        fun getEnumEntry(irEnumClass: IrClass): MethodHandle {
            val jvmEnumClass = irEnumClass.defaultType.getClass(true)
            val methodType = MethodType.methodType(jvmEnumClass, String::class)
            return NativeMethodHandles.findStatic(jvmEnumClass, StandardNames.ENUM_VALUE_OF.identifier, methodType)!!
        }

        private fun IrFunction.getMethodType(): MethodType {
            val parameterClasses = this.nonDispatchParameters.map { it.type.getClass(this.isValueParameterPrimitiveAsObject(it.indexInParameters)) }
            return when (this) {
                is IrSimpleFunction -> {
                    val returnClass = this.returnType.getClass(this.isReturnTypePrimitiveAsObject())
                    MethodType.methodType(returnClass, parameterClasses)
                }
                is IrConstructor -> {
                    MethodType.methodType(Unit::class, parameterClasses)
                }
            }
        }

        private fun IrType.getClass(asObject: Boolean): KClass<out Any> = this.getNativeClass(asObject) ?: Any::class

        private fun IrFunction.getOriginalOverriddenSymbols(): MutableList<IrFunctionSymbol> {
            val overriddenSymbols = mutableListOf<IrFunctionSymbol>()
            if (this is IrSimpleFunction) {
                val pool = this.overriddenSymbols.toMutableList()
                val iterator = pool.listIterator()
                for (symbol in iterator) {
                    if (symbol.owner.overriddenSymbols.isEmpty()) {
                        overriddenSymbols += symbol
                        iterator.remove()
                    } else {
                        symbol.owner.overriddenSymbols.forEach { iterator.add(it) }
                    }
                }
            }

            if (overriddenSymbols.isEmpty()) overriddenSymbols.add(this.symbol)
            return overriddenSymbols
        }

        private fun IrFunction.isReturnTypePrimitiveAsObject(): Boolean {
            for (symbol in getOriginalOverriddenSymbols()) {
                if (!symbol.owner.returnType.isTypeParameter() && !symbol.owner.returnType.isNullable()) {
                    return false
                }
            }
            return true
        }

        private fun IrFunction.isValueParameterPrimitiveAsObject(index: Int): Boolean {
            for (symbol in getOriginalOverriddenSymbols()) {
                if (!symbol.owner.parameters[index].type.isTypeParameter() && !symbol.owner.parameters[index].type.isNullable()) {
                    return false
                }
            }
            return true
        }
    }
}
