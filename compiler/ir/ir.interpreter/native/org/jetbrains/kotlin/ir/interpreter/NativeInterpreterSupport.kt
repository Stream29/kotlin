/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")

package org.jetbrains.kotlin.ir.interpreter

import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.interpreter.proxy.Proxy
import org.jetbrains.kotlin.ir.interpreter.proxy.reflection.*
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.isArray
import org.jetbrains.kotlin.ir.types.isNullableArray
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.isObject
import kotlin.reflect.KClass
import kotlin.reflect.KTypeProjection
import kotlin.reflect.KVariance

internal open class StackOverflowError(message: String? = null) : Error(message)
internal open class InternalError(message: String? = null) : Error(message)

internal class MethodType(
    val returnType: KClass<*>,
    val parameterTypes: List<KClass<*>>,
) {
    fun parameterType(index: Int): KClass<*> = parameterTypes.getOrElse(index) { Any::class }

    companion object {
        fun methodType(returnType: KClass<*>, parameterTypes: List<KClass<*>>): MethodType =
            MethodType(returnType, parameterTypes)

        fun methodType(returnType: KClass<*>): MethodType =
            MethodType(returnType, emptyList())

        fun methodType(returnType: KClass<*>, parameterType0: KClass<*>): MethodType =
            MethodType(returnType, listOf(parameterType0))

        fun methodType(returnType: KClass<*>, parameterType0: KClass<*>, vararg parameterTypes: KClass<*>): MethodType =
            MethodType(returnType, listOf(parameterType0) + parameterTypes.toList())
    }
}

internal class MethodHandle(
    private val methodType: MethodType,
    private val invoker: (List<Any?>) -> Any?,
) {
    fun type(): MethodType = methodType
    fun invokeWithArguments(arguments: List<Any?>): Any? = invoker(arguments)
    fun invokeWithArguments(vararg arguments: Any?): Any? = invoker(arguments.toList())
}

internal object NativeMethodHandles {
    fun findConstructor(receiverClass: KClass<*>, methodType: MethodType): MethodHandle? {
        val paramCount = methodType.parameterTypes.size
        val firstParam = methodType.parameterTypes.firstOrNull()
        return when (receiverClass) {
            StringBuilder::class -> MethodHandle(methodType) { args ->
                when (paramCount) {
                    0 -> StringBuilder()
                    1 -> when (firstParam) {
                        Int::class -> StringBuilder(args[0] as Int)
                        String::class -> StringBuilder(args[0] as String)
                        CharSequence::class -> StringBuilder(args[0] as CharSequence)
                        else -> StringBuilder(args[0].toString())
                    }
                    else -> error("Unsupported StringBuilder constructor: $paramCount")
                }
            }
            Pair::class -> MethodHandle(methodType) { args -> Pair(args[0], args[1]) }
            Triple::class -> MethodHandle(methodType) { args -> Triple(args[0], args[1], args[2]) }
            ArrayList::class -> MethodHandle(methodType) { args ->
                when (paramCount) {
                    0 -> ArrayList<Any?>()
                    1 -> when (val a0 = args[0]) {
                        is Int -> ArrayList<Any?>(a0)
                        is Collection<*> -> ArrayList<Any?>(a0)
                        else -> ArrayList<Any?>()
                    }
                    else -> ArrayList<Any?>()
                }
            }
            HashMap::class -> MethodHandle(methodType) { args ->
                when (paramCount) {
                    0 -> HashMap<Any?, Any?>()
                    1 -> when (val a0 = args[0]) {
                        is Int -> HashMap<Any?, Any?>(a0)
                        is Map<*, *> -> HashMap<Any?, Any?>(a0)
                        else -> HashMap<Any?, Any?>()
                    }
                    2 -> HashMap<Any?, Any?>(args[0] as Int, args[1] as Float)
                    else -> HashMap<Any?, Any?>()
                }
            }
            LinkedHashMap::class -> MethodHandle(methodType) { args ->
                when (paramCount) {
                    0 -> LinkedHashMap<Any?, Any?>()
                    1 -> when (val a0 = args[0]) {
                        is Int -> LinkedHashMap<Any?, Any?>(a0)
                        is Map<*, *> -> LinkedHashMap<Any?, Any?>(a0)
                        else -> LinkedHashMap<Any?, Any?>()
                    }
                    2 -> LinkedHashMap<Any?, Any?>(args[0] as Int, args[1] as Float)
                    else -> LinkedHashMap<Any?, Any?>()
                }
            }
            HashSet::class -> MethodHandle(methodType) { args ->
                when (paramCount) {
                    0 -> HashSet<Any?>()
                    1 -> when (val a0 = args[0]) {
                        is Int -> HashSet<Any?>(a0)
                        is Collection<*> -> HashSet<Any?>(a0)
                        else -> HashSet<Any?>()
                    }
                    2 -> HashSet<Any?>(args[0] as Int, args[1] as Float)
                    else -> HashSet<Any?>()
                }
            }
            LinkedHashSet::class -> MethodHandle(methodType) { args ->
                when (paramCount) {
                    0 -> LinkedHashSet<Any?>()
                    1 -> when (val a0 = args[0]) {
                        is Int -> LinkedHashSet<Any?>(a0)
                        is Collection<*> -> LinkedHashSet<Any?>(a0)
                        else -> LinkedHashSet<Any?>()
                    }
                    2 -> LinkedHashSet<Any?>(args[0] as Int, args[1] as Float)
                    else -> LinkedHashSet<Any?>()
                }
            }
            Regex::class -> MethodHandle(methodType) { args ->
                when (paramCount) {
                    1 -> Regex(args[0] as String)
                    2 -> when (val a1 = args[1]) {
                        is RegexOption -> Regex(args[0] as String, a1)
                        is Set<*> -> @Suppress("UNCHECKED_CAST") Regex(args[0] as String, a1 as Set<RegexOption>)
                        else -> Regex(args[0] as String)
                    }
                    else -> Regex(args[0] as String)
                }
            }
            CharRange::class -> MethodHandle(methodType) { args -> CharRange(args[0] as Char, args[1] as Char) }
            IntRange::class -> MethodHandle(methodType) { args -> IntRange(args[0] as Int, args[1] as Int) }
            LongRange::class -> MethodHandle(methodType) { args -> LongRange(args[0] as Long, args[1] as Long) }
            UIntRange::class -> MethodHandle(methodType) { args -> UIntRange(args[0] as UInt, args[1] as UInt) }
            ULongRange::class -> MethodHandle(methodType) { args -> ULongRange(args[0] as ULong, args[1] as ULong) }
            KTypeProjection::class -> MethodHandle(methodType) { args ->
                KTypeProjection(args[0] as KVariance?, args[1] as kotlin.reflect.KType?)
            }
            else -> findExceptionConstructor(receiverClass, methodType)
        }
    }

    private fun findExceptionConstructor(receiverClass: KClass<*>, methodType: MethodType): MethodHandle? {
        val paramCount = methodType.parameterTypes.size
        val firstParam = methodType.parameterTypes.firstOrNull()
        val builder: (String?, Throwable?, Boolean, Boolean) -> Throwable = when (receiverClass) {
            Throwable::class -> { m, c, hasM, hasC -> if (hasM && hasC) Throwable(m, c) else if (hasM) Throwable(m) else if (hasC) Throwable(c) else Throwable() }
            Exception::class -> { m, c, hasM, hasC -> if (hasM && hasC) Exception(m, c) else if (hasM) Exception(m) else if (hasC) Exception(c) else Exception() }
            RuntimeException::class -> { m, c, hasM, hasC -> if (hasM && hasC) RuntimeException(m, c) else if (hasM) RuntimeException(m) else if (hasC) RuntimeException(c) else RuntimeException() }
            Error::class -> { m, c, hasM, hasC -> if (hasM && hasC) Error(m, c) else if (hasM) Error(m) else if (hasC) Error(c) else Error() }
            AssertionError::class -> { m, c, hasM, hasC -> if (hasM && hasC) AssertionError(m, c) else if (hasM) AssertionError(m) else if (hasC) AssertionError(c) else AssertionError() }
            IllegalArgumentException::class -> { m, c, hasM, hasC -> if (hasM && hasC) IllegalArgumentException(m, c) else if (hasM) IllegalArgumentException(m) else if (hasC) IllegalArgumentException(c) else IllegalArgumentException() }
            IllegalStateException::class -> { m, c, hasM, hasC -> if (hasM && hasC) IllegalStateException(m, c) else if (hasM) IllegalStateException(m) else if (hasC) IllegalStateException(c) else IllegalStateException() }
            IndexOutOfBoundsException::class -> { m, _, hasM, _ -> if (hasM) IndexOutOfBoundsException(m) else IndexOutOfBoundsException() }
            UnsupportedOperationException::class -> { m, c, hasM, hasC -> if (hasM && hasC) UnsupportedOperationException(m, c) else if (hasM) UnsupportedOperationException(m) else if (hasC) UnsupportedOperationException(c) else UnsupportedOperationException() }
            NumberFormatException::class -> { m, _, hasM, _ -> if (hasM) NumberFormatException(m) else NumberFormatException() }
            NullPointerException::class -> { m, _, hasM, _ -> if (hasM) NullPointerException(m) else NullPointerException() }
            ClassCastException::class -> { m, _, hasM, _ -> if (hasM) ClassCastException(m) else ClassCastException() }
            ArithmeticException::class -> { m, _, hasM, _ -> if (hasM) ArithmeticException(m) else ArithmeticException() }
            NoSuchElementException::class -> { m, _, hasM, _ -> if (hasM) NoSuchElementException(m) else NoSuchElementException() }
            ConcurrentModificationException::class -> { m, c, hasM, hasC -> if (hasM && hasC) ConcurrentModificationException(m, c) else if (hasM) ConcurrentModificationException(m) else if (hasC) ConcurrentModificationException(c) else ConcurrentModificationException() }
            kotlin.NoWhenBranchMatchedException::class -> { m, c, hasM, hasC -> if (hasM && hasC) kotlin.NoWhenBranchMatchedException(m, c) else if (hasM) kotlin.NoWhenBranchMatchedException(m) else if (hasC) kotlin.NoWhenBranchMatchedException(c) else kotlin.NoWhenBranchMatchedException() }
            kotlin.UninitializedPropertyAccessException::class -> { m, c, hasM, hasC -> if (hasM && hasC) kotlin.UninitializedPropertyAccessException(m, c) else if (hasM) kotlin.UninitializedPropertyAccessException(m) else if (hasC) kotlin.UninitializedPropertyAccessException(c) else kotlin.UninitializedPropertyAccessException() }
            else -> return null
        }
        return MethodHandle(methodType) { args ->
            when (paramCount) {
                0 -> builder(null, null, false, false)
                1 -> if (firstParam == Throwable::class) {
                    builder(null, args[0] as Throwable?, false, true)
                } else {
                    builder(args[0]?.toString(), null, true, false)
                }
                else -> builder(args[0] as String?, args[1] as Throwable?, true, true)
            }
        }
    }

    fun findStaticGetter(receiverClass: KClass<*>, fieldName: String, returnType: KClass<*>): MethodHandle {
        return MethodHandle(MethodType(returnType, emptyList())) {
            when (fieldName) {
                "TRUE" -> true
                "FALSE" -> false
                "Companion" -> when (receiverClass) {
                    Regex::class -> Regex.Companion
                    CharRange::class -> CharRange.Companion
                    IntRange::class -> IntRange.Companion
                    LongRange::class -> LongRange.Companion
                    UIntRange::class -> UIntRange.Companion
                    ULongRange::class -> ULongRange.Companion
                    KTypeProjection::class -> KTypeProjection.Companion
                    else -> error("Unsupported static Companion on $receiverClass")
                }
                else -> error("Unsupported static field $receiverClass.$fieldName")
            }
        }
    }

    fun findStatic(receiverClass: KClass<*>, methodName: String, methodType: MethodType): MethodHandle? {
        return when (methodName) {
            "asList" -> MethodHandle(methodType) { args -> (args[0] as Array<*>).asList() }
            "mutableListOf" -> MethodHandle(methodType) { args -> (args[0] as Array<*>).toMutableList() }
            "arrayListOf" -> MethodHandle(methodType) { args -> ArrayList((args[0] as Array<*>).asList()) }
            "isWhitespace" -> MethodHandle(methodType) { args -> (args[0] as Char).isWhitespace() }
            "toMutableList" -> MethodHandle(methodType) { args -> (args[0] as Array<*>).toMutableList() }
            "copyToArrayOfAny" -> MethodHandle(methodType) { args -> (args[0] as Array<*>).copyOf() }
            "arrayOfNulls" -> MethodHandle(methodType) { args -> arrayOfNulls<Any?>(args[0] as Int) }
            "enumValues" -> MethodHandle(methodType) { emptyArray<Enum<*>>() }
            "enumValueOf" -> MethodHandle(methodType) { args ->
                val name = args[0] as String
                RegexOption.entries.firstOrNull { it.name == name }
                    ?: KVariance.entries.firstOrNull { it.name == name }
                    ?: KVisibility.entries.firstOrNull { it.name == name }
                    ?: KParameter.Kind.entries.firstOrNull { it.name == name }
                    ?: throw IllegalArgumentException("No enum constant $name")
            }
            "noWhenBranchMatchedException" -> MethodHandle(methodType) { throw kotlin.NoWhenBranchMatchedException() }
            "throwUninitializedPropertyAccessException" -> MethodHandle(methodType) { args ->
                throw kotlin.UninitializedPropertyAccessException("lateinit property ${args[0]} has not been initialized")
            }
            "synchronized" -> MethodHandle(methodType) { args ->
                @Suppress("UNCHECKED_CAST")
                val block = args[1] as () -> Any?
                block()
            }
            "valueOf" -> MethodHandle(methodType) { args ->
                val name = args[0] as String
                when (receiverClass) {
                    RegexOption::class -> RegexOption.valueOf(name)
                    KVariance::class -> KVariance.valueOf(name)
                    else -> throw IllegalArgumentException("Unknown enum $receiverClass.$name")
                }
            }
            "values" -> MethodHandle(methodType) {
                when (receiverClass) {
                    RegexOption::class -> RegexOption.values()
                    KVariance::class -> KVariance.values()
                    else -> emptyArray<Enum<*>>()
                }
            }
            else -> null
        }
    }

    fun findVirtual(receiverClass: KClass<*>, methodName: String, methodType: MethodType): MethodHandle? {
        val boundType = MethodType(methodType.returnType, listOf(receiverClass) + methodType.parameterTypes)
        return MethodHandle(boundType) { args ->
            val recv = args[0]
            val rest = if (args.size > 1) args.subList(1, args.size) else emptyList()
            invokeVirtual(receiverClass, recv, methodName, boundType, rest)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun invokeVirtual(
        receiverClass: KClass<*>,
        recv: Any?,
        methodName: String,
        methodType: MethodType,
        args: List<Any?>,
    ): Any? {
        if (recv is ReflectionProxy) {
            return invokeReflectionProxy(recv, methodName, args)
        }
        when (methodName) {
            "toString" -> if (args.isEmpty()) return recv.toString()
            "hashCode" -> if (args.isEmpty()) return recv.hashCode()
            "equals" -> if (args.size == 1) return recv == args[0]
        }
        return when (recv) {
            is StringBuilder -> invokeStringBuilder(recv, methodName, methodType, args)
            is Pair<*, *> -> when (methodName) {
                "getFirst", "component1" -> recv.first
                "getSecond", "component2" -> recv.second
                "copy" -> Pair(args[0], args[1])
                else -> error("Unsupported Pair method: $methodName")
            }
            is Triple<*, *, *> -> when (methodName) {
                "getFirst", "component1" -> recv.first
                "getSecond", "component2" -> recv.second
                "getThird", "component3" -> recv.third
                "copy" -> Triple(args[0], args[1], args[2])
                else -> error("Unsupported Triple method: $methodName")
            }
            is KTypeProjection -> when (methodName) {
                "getVariance", "component1" -> recv.variance
                "getType", "component2" -> recv.type
                "copy" -> KTypeProjection(args[0] as KVariance?, args[1] as kotlin.reflect.KType?)
                else -> error("Unsupported KTypeProjection method: $methodName")
            }
            is KTypeProjection.Companion -> when (methodName) {
                "getSTAR" -> KTypeProjection.STAR
                "invariant" -> KTypeProjection.invariant(args[0] as kotlin.reflect.KType)
                "contravariant" -> KTypeProjection.contravariant(args[0] as kotlin.reflect.KType)
                "covariant" -> KTypeProjection.covariant(args[0] as kotlin.reflect.KType)
                else -> error("Unsupported KTypeProjection.Companion method: $methodName")
            }
            is Regex.Companion -> when (methodName) {
                "fromLiteral" -> Regex.fromLiteral(args[0] as String)
                "escape" -> Regex.escape(args[0] as String)
                "escapeReplacement" -> Regex.escapeReplacement(args[0] as String)
                else -> error("Unsupported Regex.Companion method: $methodName")
            }
            is Regex -> invokeRegex(recv, methodName, args)
            is MatchResult -> invokeMatchResult(recv, methodName, args)
            is MatchGroup -> when (methodName) {
                "getValue", "component1" -> recv.value
                "getRange", "component2" -> recv.range
                else -> error("Unsupported MatchGroup method: $methodName")
            }
            is CharProgression -> invokeCharProgression(recv, methodName)
            is IntProgression -> invokeIntProgression(recv, methodName)
            is LongProgression -> invokeLongProgression(recv, methodName)
            is UIntProgression -> invokeUIntProgression(recv, methodName)
            is ULongProgression -> invokeULongProgression(recv, methodName)
            is CharRange.Companion -> if (methodName == "getEMPTY") CharRange.EMPTY else error("Unsupported $methodName")
            is IntRange.Companion -> if (methodName == "getEMPTY") IntRange.EMPTY else error("Unsupported $methodName")
            is LongRange.Companion -> if (methodName == "getEMPTY") LongRange.EMPTY else error("Unsupported $methodName")
            is UIntRange.Companion -> if (methodName == "getEMPTY") UIntRange.EMPTY else error("Unsupported $methodName")
            is ULongRange.Companion -> if (methodName == "getEMPTY") ULongRange.EMPTY else error("Unsupported $methodName")
            is CharProgression.Companion -> if (methodName == "fromClosedRange") CharProgression.fromClosedRange(args[0] as Char, args[1] as Char, args[2] as Int) else error("Unsupported $methodName")
            is IntProgression.Companion -> if (methodName == "fromClosedRange") IntProgression.fromClosedRange(args[0] as Int, args[1] as Int, args[2] as Int) else error("Unsupported $methodName")
            is LongProgression.Companion -> if (methodName == "fromClosedRange") LongProgression.fromClosedRange(args[0] as Long, args[1] as Long, args[2] as Long) else error("Unsupported $methodName")
            is UIntProgression.Companion -> if (methodName == "fromClosedRange") UIntProgression.fromClosedRange(args[0] as UInt, args[1] as UInt, args[2] as Int) else error("Unsupported $methodName")
            is ULongProgression.Companion -> if (methodName == "fromClosedRange") ULongProgression.fromClosedRange(args[0] as ULong, args[1] as ULong, args[2] as Long) else error("Unsupported $methodName")
            is List<*> -> invokeList(recv, methodName, methodType, args)
            is Set<*> -> invokeCollection(recv, methodName, args)
            is Collection<*> -> invokeCollection(recv, methodName, args)
            is Map<*, *> -> invokeMap(recv as Map<Any?, Any?>, methodName, args)
            is Map.Entry<*, *> -> invokeMapEntry(recv as Map.Entry<Any?, Any?>, methodName, args)
            is ListIterator<*> -> invokeListIterator(recv as ListIterator<Any?>, methodName, args)
            is Iterator<*> -> invokeIterator(recv as Iterator<Any?>, methodName)
            is Iterable<*> -> if (methodName == "iterator") recv.iterator() else error("Unsupported Iterable.$methodName")
            is Sequence<*> -> if (methodName == "iterator") recv.iterator() else error("Unsupported Sequence.$methodName")
            is CharSequence -> invokeCharSequence(recv, methodName, args)
            is Number -> invokeNumber(recv, methodName)
            is Enum<*> -> invokeEnum(recv, methodName, args)
            is Throwable -> invokeThrowable(recv, methodName, args)
            is Comparable<*> -> if (methodName == "compareTo") (recv as Comparable<Any?>).compareTo(args[0]) else error("Unsupported Comparable.$methodName")
            else -> error("Unsupported virtual call on ${recv?.let { it::class } ?: receiverClass}.$methodName(${args.size})")
        }
    }

    private fun invokeReflectionProxy(recv: ReflectionProxy, methodName: String, args: List<Any?>): Any? {
        return when (recv) {
            is KClassProxy -> when (methodName) {
                "getSimpleName" -> recv.simpleName
                "getQualifiedName" -> recv.qualifiedName
                "getMembers" -> recv.members
                "getConstructors" -> recv.constructors
                "getNestedClasses" -> recv.nestedClasses
                "getObjectInstance" -> recv.objectInstance
                "getTypeParameters" -> recv.typeParameters
                "getSupertypes" -> recv.supertypes
                "getSealedSubclasses" -> recv.sealedSubclasses
                "getVisibility" -> recv.visibility
                "isFinal" -> recv.isFinal
                "isOpen" -> recv.isOpen
                "isAbstract" -> recv.isAbstract
                "isSealed" -> recv.isSealed
                "isData" -> recv.isData
                "isInner" -> recv.isInner
                "isCompanion" -> recv.isCompanion
                "isFun" -> recv.isFun
                "isValue" -> recv.isValue
                "isInstance" -> recv.isInstance(args[0])
                "getAnnotations" -> recv.annotations
                "equals" -> recv == args[0]
                "hashCode" -> recv.hashCode()
                "toString" -> recv.toString()
                else -> error("Unsupported KClassProxy method: $methodName")
            }
            is KTypeProxy -> when (methodName) {
                "getClassifier" -> recv.classifier
                "getArguments" -> recv.arguments
                "isMarkedNullable" -> recv.isMarkedNullable
                "getAnnotations" -> recv.annotations
                "equals" -> recv == args[0]
                "hashCode" -> recv.hashCode()
                "toString" -> recv.toString()
                else -> error("Unsupported KTypeProxy method: $methodName")
            }
            is KTypeParameterProxy -> when (methodName) {
                "getName" -> recv.name
                "getUpperBounds" -> recv.upperBounds
                "getVariance" -> recv.variance
                "isReified" -> recv.isReified
                "equals" -> recv == args[0]
                "hashCode" -> recv.hashCode()
                "toString" -> recv.toString()
                else -> error("Unsupported KTypeParameterProxy method: $methodName")
            }
            is KParameterProxy -> when (methodName) {
                "getIndex" -> recv.index
                "getName" -> recv.name
                "getType" -> recv.type
                "getKind" -> recv.kind
                "isOptional" -> recv.isOptional
                "isVararg" -> recv.isVararg
                "getAnnotations" -> recv.annotations
                "equals" -> recv == args[0]
                "hashCode" -> recv.hashCode()
                "toString" -> recv.toString()
                else -> error("Unsupported KParameterProxy method: $methodName")
            }
            is KFunctionProxy<*> -> when (methodName) {
                "getName" -> recv.name
                "getParameters" -> recv.parameters
                "getReturnType" -> recv.returnType
                "getTypeParameters" -> recv.typeParameters
                "getVisibility" -> recv.visibility
                "isFinal" -> recv.isFinal
                "isOpen" -> recv.isOpen
                "isAbstract" -> recv.isAbstract
                "isSuspend" -> recv.isSuspend
                "isInline" -> recv.isInline
                "isExternal" -> recv.isExternal
                "isOperator" -> recv.isOperator
                "isInfix" -> recv.isInfix
                "getAnnotations" -> recv.annotations
                "call" -> {
                    val varargs = args[0] as Array<*>
                    recv.call(*varargs)
                }
                "callBy" -> {
                    @Suppress("UNCHECKED_CAST")
                    recv.callBy(args[0] as Map<KParameter, Any?>)
                }
                "invoke" -> recv.call(*args.toTypedArray())
                "equals" -> recv == args[0]
                "hashCode" -> recv.hashCode()
                "toString" -> recv.toString()
                else -> error("Unsupported KFunctionProxy method: $methodName")
            }
            is AbstractKPropertyProxy -> when (methodName) {
                "getName" -> recv.name
                "getParameters" -> recv.parameters
                "getReturnType" -> recv.returnType
                "getTypeParameters" -> recv.typeParameters
                "getVisibility" -> recv.visibility
                "isFinal" -> recv.isFinal
                "isOpen" -> recv.isOpen
                "isAbstract" -> recv.isAbstract
                "isSuspend" -> recv.isSuspend
                "isLateinit" -> recv.isLateinit
                "isConst" -> recv.isConst
                "getAnnotations" -> recv.annotations
                "getGetter" -> recv.getter
                "getSetter" -> (recv as KMutableProperty<*>).setter
                "get" -> when (recv) {
                    is KProperty0Proxy -> recv.get()
                    is KProperty1Proxy -> recv.get(args[0])
                    is KProperty2Proxy -> recv.get(args[0], args[1])
                    else -> error("Unsupported get on $recv")
                }
                "set" -> when (recv) {
                    is KMutableProperty0Proxy -> recv.set(args[0])
                    is KMutableProperty1Proxy -> recv.set(args[0], args[1])
                    is KMutableProperty2Proxy -> recv.set(args[0], args[1], args[2])
                    else -> error("Unsupported set on $recv")
                }
                "getDelegate" -> when (recv) {
                    is KProperty0Proxy -> recv.getDelegate()
                    is KProperty1Proxy -> recv.getDelegate(args[0])
                    is KProperty2Proxy -> recv.getDelegate(args[0], args[1])
                    else -> null
                }
                "invoke" -> when (recv) {
                    is KProperty0Proxy -> recv.invoke()
                    is KProperty1Proxy -> recv.invoke(args[0])
                    is KProperty2Proxy -> recv.invoke(args[0], args[1])
                    else -> error("Unsupported invoke on $recv")
                }
                "call" -> {
                    val varargs = args[0] as Array<*>
                    recv.call(*varargs)
                }
                "callBy" -> {
                    @Suppress("UNCHECKED_CAST")
                    recv.callBy(args[0] as Map<KParameter, Any?>)
                }
                "equals" -> recv == args[0]
                "hashCode" -> recv.hashCode()
                "toString" -> recv.toString()
                else -> error("Unsupported KPropertyProxy method: $methodName")
            }
            else -> error("Unsupported ReflectionProxy: $recv.$methodName")
        }
    }

    private fun invokeStringBuilder(sb: StringBuilder, methodName: String, methodType: MethodType, args: List<Any?>): Any? {
        val firstParam = methodType.parameterTypes.getOrNull(1)
        return when (methodName) {
            "append" -> when (args.size) {
                0 -> sb
                1 -> when (firstParam) {
                    Boolean::class -> sb.append(args[0] as Boolean)
                    Char::class -> sb.append(args[0] as Char)
                    Int::class -> sb.append(args[0] as Int)
                    Long::class -> sb.append(args[0] as Long)
                    Float::class -> sb.append(args[0] as Float)
                    Double::class -> sb.append(args[0] as Double)
                    CharArray::class -> sb.append(args[0] as CharArray)
                    String::class -> sb.append(args[0] as String?)
                    CharSequence::class -> sb.append(args[0] as CharSequence?)
                    else -> sb.append(args[0])
                }
                3 -> when (val a0 = args[0]) {
                    is CharSequence? -> sb.append(a0, args[1] as Int, args[2] as Int)
                    is CharArray -> sb.appendRange(a0, args[1] as Int, (args[1] as Int) + (args[2] as Int))
                    else -> error("Unsupported StringBuilder.append(3)")
                }
                else -> error("Unsupported StringBuilder.append(${args.size})")
            }
            "insert" -> when (args.size) {
                2 -> {
                    val idx = args[0] as Int
                    when (methodType.parameterTypes.getOrNull(2)) {
                        Boolean::class -> sb.insert(idx, args[1] as Boolean)
                        Char::class -> sb.insert(idx, args[1] as Char)
                        Int::class -> sb.insert(idx, args[1] as Int)
                        Long::class -> sb.insert(idx, args[1] as Long)
                        Float::class -> sb.insert(idx, args[1] as Float)
                        Double::class -> sb.insert(idx, args[1] as Double)
                        CharArray::class -> sb.insert(idx, args[1] as CharArray)
                        String::class -> sb.insert(idx, args[1] as String?)
                        CharSequence::class -> sb.insert(idx, args[1] as CharSequence?)
                        else -> sb.insert(idx, args[1])
                    }
                }
                4 -> {
                    val idx = args[0] as Int
                    when (val a1 = args[1]) {
                        is CharSequence? -> sb.insertRange(idx, a1 ?: "null", args[2] as Int, args[3] as Int)
                        is CharArray -> sb.insertRange(idx, a1, args[2] as Int, (args[2] as Int) + (args[3] as Int))
                        else -> error("Unsupported StringBuilder.insert(4)")
                    }
                }
                else -> error("Unsupported StringBuilder.insert(${args.size})")
            }
            "get", "charAt" -> sb[args[0] as Int]
            "set", "setCharAt" -> { sb.set(args[0] as Int, args[1] as Char); Unit }
            "length", "getLength" -> sb.length
            "setLength" -> { sb.setLength(args[0] as Int); Unit }
            "capacity" -> sb.capacity()
            "ensureCapacity" -> { sb.ensureCapacity(args[0] as Int); Unit }
            "trimToSize" -> { sb.trimToSize(); Unit }
            "reverse" -> sb.reverse()
            "clear" -> sb.clear()
            "delete", "deleteRange" -> sb.deleteRange(args[0] as Int, args[1] as Int)
            "deleteCharAt", "deleteAt" -> sb.deleteAt(args[0] as Int)
            "replace", "setRange" -> sb.setRange(args[0] as Int, args[1] as Int, args[2] as String)
            "subSequence" -> sb.subSequence(args[0] as Int, args[1] as Int)
            "substring" -> if (args.size == 1) sb.substring(args[0] as Int) else sb.substring(args[0] as Int, args[1] as Int)
            "indexOf" -> if (args.size == 1) sb.indexOf(args[0] as String) else sb.indexOf(args[0] as String, args[1] as Int)
            "lastIndexOf" -> if (args.size == 1) sb.lastIndexOf(args[0] as String) else sb.lastIndexOf(args[0] as String, args[1] as Int)
            "toCharArray" -> {
                val dst = args[0] as CharArray
                val dstOff = if (args.size > 1) args[1] as Int else 0
                val start = if (args.size > 2) args[2] as Int else 0
                val end = if (args.size > 3) args[3] as Int else sb.length
                for (i in start until end) dst[dstOff + (i - start)] = sb[i]
                Unit
            }
            else -> error("Unsupported StringBuilder.$methodName")
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun invokeRegex(regex: Regex, methodName: String, args: List<Any?>): Any? = when (methodName) {
        "getPattern" -> regex.pattern
        "getOptions" -> regex.options
        "matches" -> regex.matches(args[0] as CharSequence)
        "matchesAt" -> regex.matchesAt(args[0] as CharSequence, args[1] as Int)
        "MatchAt" -> regex.matchAt(args[0] as CharSequence, args[1] as Int)
        "matchAt" -> regex.matchAt(args[0] as CharSequence, args[1] as Int)
        "containsMatchIn" -> regex.containsMatchIn(args[0] as CharSequence)
        "find" -> regex.find(args[0] as CharSequence, if (args.size > 1) args[1] as Int else 0)
        "findAll" -> regex.findAll(args[0] as CharSequence, if (args.size > 1) args[1] as Int else 0)
        "matchEntire" -> regex.matchEntire(args[0] as CharSequence)
        "replace" -> when (val a1 = args[1]) {
            is String -> regex.replace(args[0] as CharSequence, a1)
            else -> regex.replace(args[0] as CharSequence, a1 as (MatchResult) -> CharSequence)
        }
        "replaceFirst" -> regex.replaceFirst(args[0] as CharSequence, args[1] as String)
        "split" -> regex.split(args[0] as CharSequence, if (args.size > 1) args[1] as Int else 0)
        "splitToSequence" -> regex.splitToSequence(args[0] as CharSequence, if (args.size > 1) args[1] as Int else 0)
        else -> error("Unsupported Regex.$methodName")
    }

    private fun invokeMatchResult(mr: MatchResult, methodName: String, args: List<Any?>): Any? = when (methodName) {
        "getValue" -> mr.value
        "getRange" -> mr.range
        "getGroups" -> mr.groups
        "getGroupValues" -> mr.groupValues
        "getDestructured" -> mr.destructured
        "next" -> mr.next()
        else -> error("Unsupported MatchResult.$methodName")
    }

    private fun invokeCharProgression(p: CharProgression, methodName: String): Any? = when (methodName) {
        "getFirst" -> p.first
        "getLast" -> p.last
        "getStep" -> p.step
        "getStart" -> (p as CharRange).start
        "getEndInclusive" -> (p as CharRange).endInclusive
        "getEndExclusive" -> (p as CharRange).endExclusive
        "isEmpty" -> p.isEmpty()
        "iterator" -> p.iterator()
        else -> error("Unsupported CharProgression.$methodName")
    }

    private fun invokeIntProgression(p: IntProgression, methodName: String): Any? = when (methodName) {
        "getFirst" -> p.first
        "getLast" -> p.last
        "getStep" -> p.step
        "getStart" -> (p as IntRange).start
        "getEndInclusive" -> (p as IntRange).endInclusive
        "getEndExclusive" -> (p as IntRange).endExclusive
        "isEmpty" -> p.isEmpty()
        "iterator" -> p.iterator()
        else -> error("Unsupported IntProgression.$methodName")
    }

    private fun invokeLongProgression(p: LongProgression, methodName: String): Any? = when (methodName) {
        "getFirst" -> p.first
        "getLast" -> p.last
        "getStep" -> p.step
        "getStart" -> (p as LongRange).start
        "getEndInclusive" -> (p as LongRange).endInclusive
        "getEndExclusive" -> (p as LongRange).endExclusive
        "isEmpty" -> p.isEmpty()
        "iterator" -> p.iterator()
        else -> error("Unsupported LongProgression.$methodName")
    }

    private fun invokeUIntProgression(p: UIntProgression, methodName: String): Any? = when (methodName) {
        "getFirst" -> p.first
        "getLast" -> p.last
        "getStep" -> p.step
        "getStart" -> (p as UIntRange).start
        "getEndInclusive" -> (p as UIntRange).endInclusive
        "getEndExclusive" -> (p as UIntRange).endExclusive
        "isEmpty" -> p.isEmpty()
        "iterator" -> p.iterator()
        else -> error("Unsupported UIntProgression.$methodName")
    }

    private fun invokeULongProgression(p: ULongProgression, methodName: String): Any? = when (methodName) {
        "getFirst" -> p.first
        "getLast" -> p.last
        "getStep" -> p.step
        "getStart" -> (p as ULongRange).start
        "getEndInclusive" -> (p as ULongRange).endInclusive
        "getEndExclusive" -> (p as ULongRange).endExclusive
        "isEmpty" -> p.isEmpty()
        "iterator" -> p.iterator()
        else -> error("Unsupported ULongProgression.$methodName")
    }

    @Suppress("UNCHECKED_CAST")
    private fun invokeList(list: List<*>, methodName: String, methodType: MethodType, args: List<Any?>): Any? {
        val mlist = list as? MutableList<Any?>
        return when (methodName) {
            "get" -> list[args[0] as Int]
            "set" -> mlist!!.set(args[0] as Int, args[1])
            "indexOf" -> list.indexOf(args[0])
            "lastIndexOf" -> list.lastIndexOf(args[0])
            "listIterator" -> if (args.isEmpty()) list.listIterator() else list.listIterator(args[0] as Int)
            "subList" -> list.subList(args[0] as Int, args[1] as Int)
            "add" -> if (args.size == 1) mlist!!.add(args[0]) else { mlist!!.add(args[0] as Int, args[1]); Unit }
            "addAll" -> if (args.size == 1) mlist!!.addAll(args[0] as Collection<Any?>) else mlist!!.addAll(args[0] as Int, args[1] as Collection<Any?>)
            "removeAt" -> mlist!!.removeAt(args[0] as Int)
            "remove" -> if (methodType.parameterTypes.getOrNull(1) == Int::class && methodType.returnType != Boolean::class) {
                mlist!!.removeAt(args[0] as Int)
            } else {
                mlist!!.remove(args[0])
            }
            "ensureCapacity" -> { (list as? ArrayList<*>)?.ensureCapacity(args[0] as Int); Unit }
            "trimToSize" -> { (list as? ArrayList<*>)?.trimToSize(); Unit }
            else -> invokeCollection(list, methodName, args)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun invokeCollection(coll: Collection<*>, methodName: String, args: List<Any?>): Any? {
        val mcoll = coll as? MutableCollection<Any?>
        return when (methodName) {
            "size", "getSize" -> coll.size
            "isEmpty" -> coll.isEmpty()
            "contains" -> coll.contains(args[0])
            "containsAll" -> coll.containsAll(args[0] as Collection<Any?>)
            "iterator" -> coll.iterator()
            "toArray" -> coll.toTypedArray()
            "add" -> mcoll!!.add(args[0])
            "remove" -> mcoll!!.remove(args[0])
            "addAll" -> mcoll!!.addAll(args[0] as Collection<Any?>)
            "removeAll" -> mcoll!!.removeAll(args[0] as Collection<Any?>)
            "retainAll" -> mcoll!!.retainAll(args[0] as Collection<Any?>)
            "clear" -> { mcoll!!.clear(); Unit }
            else -> error("Unsupported Collection.$methodName")
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun invokeMap(map: Map<Any?, Any?>, methodName: String, args: List<Any?>): Any? {
        val mmap = map as? MutableMap<Any?, Any?>
        return when (methodName) {
            "size", "getSize" -> map.size
            "isEmpty" -> map.isEmpty()
            "containsKey" -> map.containsKey(args[0])
            "containsValue" -> map.containsValue(args[0])
            "get" -> map[args[0]]
            "getOrDefault" -> map.getOrElse(args[0]) { args[1] }
            "put" -> mmap!!.put(args[0], args[1])
            "remove" -> if (args.size == 1) mmap!!.remove(args[0]) else {
                if (mmap!!.containsKey(args[0]) && mmap[args[0]] == args[1]) {
                    mmap.remove(args[0])
                    true
                } else false
            }
            "putAll" -> { mmap!!.putAll(args[0] as Map<Any?, Any?>); Unit }
            "clear" -> { mmap!!.clear(); Unit }
            "keySet", "getKeys" -> map.keys
            "values", "getValues" -> map.values
            "entrySet", "getEntries" -> map.entries
            else -> error("Unsupported Map.$methodName")
        }
    }

    private fun invokeMapEntry(entry: Map.Entry<Any?, Any?>, methodName: String, args: List<Any?>): Any? = when (methodName) {
        "getKey" -> entry.key
        "getValue" -> entry.value
        "setValue" -> (entry as MutableMap.MutableEntry<Any?, Any?>).setValue(args[0])
        else -> error("Unsupported Map.Entry.$methodName")
    }

    private fun invokeIterator(it: Iterator<Any?>, methodName: String): Any? = when (methodName) {
        "hasNext" -> it.hasNext()
        "next" -> it.next()
        "remove" -> { (it as MutableIterator<Any?>).remove(); Unit }
        else -> error("Unsupported Iterator.$methodName")
    }

    private fun invokeListIterator(it: ListIterator<Any?>, methodName: String, args: List<Any?>): Any? = when (methodName) {
        "hasNext" -> it.hasNext()
        "next" -> it.next()
        "hasPrevious" -> it.hasPrevious()
        "previous" -> it.previous()
        "nextIndex" -> it.nextIndex()
        "previousIndex" -> it.previousIndex()
        "remove" -> { (it as MutableListIterator<Any?>).remove(); Unit }
        "set" -> { (it as MutableListIterator<Any?>).set(args[0]); Unit }
        "add" -> { (it as MutableListIterator<Any?>).add(args[0]); Unit }
        else -> error("Unsupported ListIterator.$methodName")
    }

    private fun invokeCharSequence(cs: CharSequence, methodName: String, args: List<Any?>): Any? = when (methodName) {
        "length", "getLength" -> cs.length
        "get", "charAt" -> cs[args[0] as Int]
        "subSequence" -> cs.subSequence(args[0] as Int, args[1] as Int)
        else -> error("Unsupported CharSequence.$methodName")
    }

    private fun invokeNumber(n: Number, methodName: String): Any? = when (methodName) {
        "toByte", "byteValue" -> n.toByte()
        "toShort", "shortValue" -> n.toShort()
        "toInt", "intValue" -> n.toInt()
        "toLong", "longValue" -> n.toLong()
        "toFloat", "floatValue" -> n.toFloat()
        "toDouble", "doubleValue" -> n.toDouble()
        "toChar" -> n.toInt().toChar()
        else -> error("Unsupported Number.$methodName")
    }

    @Suppress("UNCHECKED_CAST")
    private fun invokeEnum(e: Enum<*>, methodName: String, args: List<Any?>): Any? = when (methodName) {
        "name", "getName" -> e.name
        "ordinal", "getOrdinal" -> e.ordinal
        "compareTo" -> (e as Comparable<Any?>).compareTo(args[0])
        "getValue", "getMask" -> (e as? RegexOption)?.ordinal
        else -> error("Unsupported Enum.$methodName")
    }

    private fun invokeThrowable(t: Throwable, methodName: String, args: List<Any?>): Any? = when (methodName) {
        "getMessage" -> t.message
        "getCause" -> t.cause
        "getLocalizedMessage" -> t.message
        "printStackTrace" -> { t.printStackTrace(); Unit }
        "getStackTrace" -> t.getStackTrace()
        "addSuppressed" -> { t.addSuppressed(args[0] as Throwable); Unit }
        "getSuppressed" -> t.suppressedExceptions.toTypedArray()
        "fillInStackTrace" -> t
        "initCause" -> t
        else -> error("Unsupported Throwable.$methodName")
    }
}

internal fun IrFunction.getNativeDispatchClass(): KClass<*>? {
    val parent = this.parent
    if (parent is org.jetbrains.kotlin.ir.declarations.IrClass) {
        return parent.defaultType.getNativeClass(parent.isObject)
    }
    return Any::class
}

internal fun org.jetbrains.kotlin.ir.types.IrType.getNativeClass(asObject: Boolean = false): KClass<*>? {
    val fqName = this.classFqName?.asString() ?: return Any::class
    if (this.isArray() || this.isNullableArray()) return Array::class
    return when (fqName) {
        "kotlin.Nothing" -> Nothing::class
        "kotlin.Unit" -> Unit::class
        "kotlin.Boolean" -> Boolean::class
        "kotlin.Char" -> Char::class
        "kotlin.Byte" -> Byte::class
        "kotlin.Short" -> Short::class
        "kotlin.Int" -> Int::class
        "kotlin.Long" -> Long::class
        "kotlin.Float" -> Float::class
        "kotlin.Double" -> Double::class
        "kotlin.UByte" -> UByte::class
        "kotlin.UShort" -> UShort::class
        "kotlin.UInt" -> UInt::class
        "kotlin.ULong" -> ULong::class
        "kotlin.String" -> String::class
        "kotlin.CharSequence" -> CharSequence::class
        "kotlin.Number" -> Number::class
        "kotlin.Comparable" -> Comparable::class
        "kotlin.Enum" -> Enum::class
        "kotlin.Any" -> Any::class
        "kotlin.BooleanArray" -> BooleanArray::class
        "kotlin.CharArray" -> CharArray::class
        "kotlin.ByteArray" -> ByteArray::class
        "kotlin.ShortArray" -> ShortArray::class
        "kotlin.IntArray" -> IntArray::class
        "kotlin.LongArray" -> LongArray::class
        "kotlin.FloatArray" -> FloatArray::class
        "kotlin.DoubleArray" -> DoubleArray::class
        "kotlin.UByteArray" -> UByteArray::class
        "kotlin.UShortArray" -> UShortArray::class
        "kotlin.UIntArray" -> UIntArray::class
        "kotlin.ULongArray" -> ULongArray::class
        "kotlin.Array" -> Array::class
        "kotlin.Pair" -> Pair::class
        "kotlin.Triple" -> Triple::class
        "kotlin.text.StringBuilder", "java.lang.StringBuilder" -> StringBuilder::class
        "kotlin.text.Appendable", "java.lang.Appendable" -> Appendable::class
        "kotlin.text.Regex" -> Regex::class
        "kotlin.text.Regex.Companion" -> Regex.Companion::class
        "kotlin.text.RegexOption" -> RegexOption::class
        "kotlin.text.MatchResult" -> MatchResult::class
        "kotlin.text.MatchGroup" -> MatchGroup::class
        "kotlin.text.MatchGroupCollection" -> MatchGroupCollection::class
        "kotlin.ranges.CharRange" -> CharRange::class
        "kotlin.ranges.CharRange.Companion" -> CharRange.Companion::class
        "kotlin.ranges.IntRange" -> IntRange::class
        "kotlin.ranges.IntRange.Companion" -> IntRange.Companion::class
        "kotlin.ranges.LongRange" -> LongRange::class
        "kotlin.ranges.LongRange.Companion" -> LongRange.Companion::class
        "kotlin.ranges.UIntRange" -> UIntRange::class
        "kotlin.ranges.UIntRange.Companion" -> UIntRange.Companion::class
        "kotlin.ranges.ULongRange" -> ULongRange::class
        "kotlin.ranges.ULongRange.Companion" -> ULongRange.Companion::class
        "kotlin.ranges.CharProgression" -> CharProgression::class
        "kotlin.ranges.CharProgression.Companion" -> CharProgression.Companion::class
        "kotlin.ranges.IntProgression" -> IntProgression::class
        "kotlin.ranges.IntProgression.Companion" -> IntProgression.Companion::class
        "kotlin.ranges.LongProgression" -> LongProgression::class
        "kotlin.ranges.LongProgression.Companion" -> LongProgression.Companion::class
        "kotlin.ranges.UIntProgression" -> UIntProgression::class
        "kotlin.ranges.UIntProgression.Companion" -> UIntProgression.Companion::class
        "kotlin.ranges.ULongProgression" -> ULongProgression::class
        "kotlin.ranges.ULongProgression.Companion" -> ULongProgression.Companion::class
        "kotlin.ranges.ClosedRange" -> ClosedRange::class
        "kotlin.ranges.OpenEndRange" -> OpenEndRange::class
        "kotlin.collections.Iterable", "kotlin.collections.MutableIterable", "java.lang.Iterable" -> Iterable::class
        "kotlin.collections.Iterator", "kotlin.collections.MutableIterator",
        "kotlin.collections.CharIterator", "kotlin.collections.IntIterator", "kotlin.collections.LongIterator",
        "kotlin.collections.BooleanIterator", "kotlin.collections.ByteIterator", "kotlin.collections.ShortIterator",
        "kotlin.collections.FloatIterator", "kotlin.collections.DoubleIterator",
        "java.util.Iterator" -> Iterator::class
        "kotlin.collections.ListIterator", "kotlin.collections.MutableListIterator", "java.util.ListIterator" -> ListIterator::class
        "kotlin.collections.Collection", "kotlin.collections.MutableCollection", "java.util.Collection" -> Collection::class
        "kotlin.collections.List", "kotlin.collections.MutableList", "java.util.List" -> List::class
        "kotlin.collections.ArrayList", "java.util.ArrayList" -> ArrayList::class
        "kotlin.collections.Set", "kotlin.collections.MutableSet", "java.util.Set" -> Set::class
        "kotlin.collections.HashSet", "java.util.HashSet" -> HashSet::class
        "kotlin.collections.LinkedHashSet", "java.util.LinkedHashSet" -> LinkedHashSet::class
        "kotlin.collections.Map", "kotlin.collections.MutableMap", "java.util.Map" -> Map::class
        "kotlin.collections.Map.Entry", "kotlin.collections.MutableMap.MutableEntry", "java.util.Map.Entry" -> Map.Entry::class
        "kotlin.collections.HashMap", "java.util.HashMap" -> HashMap::class
        "kotlin.collections.LinkedHashMap", "java.util.LinkedHashMap" -> LinkedHashMap::class
        "kotlin.sequences.Sequence" -> Sequence::class
        "kotlin.reflect.KClass" -> KClassProxy::class
        "kotlin.reflect.KType" -> KTypeProxy::class
        "kotlin.reflect.KTypeParameter" -> KTypeParameterProxy::class
        "kotlin.reflect.KParameter" -> KParameterProxy::class
        "kotlin.reflect.KFunction" -> KFunctionProxy::class
        "kotlin.reflect.KCallable", "kotlin.reflect.KProperty", "kotlin.reflect.KMutableProperty",
        "kotlin.reflect.KProperty0", "kotlin.reflect.KMutableProperty0",
        "kotlin.reflect.KProperty1", "kotlin.reflect.KMutableProperty1",
        "kotlin.reflect.KProperty2", "kotlin.reflect.KMutableProperty2" -> AbstractKPropertyProxy::class
        "kotlin.reflect.KTypeProjection" -> KTypeProjection::class
        "kotlin.reflect.KTypeProjection.Companion" -> KTypeProjection.Companion::class
        "kotlin.reflect.KVariance" -> KVariance::class
        "kotlin.reflect.KVisibility" -> KVisibility::class
        "kotlin.reflect.KParameter.Kind" -> KParameter.Kind::class
        "kotlin.Throwable", "java.lang.Throwable" -> Throwable::class
        "kotlin.Exception", "java.lang.Exception" -> Exception::class
        "kotlin.RuntimeException", "java.lang.RuntimeException" -> RuntimeException::class
        "kotlin.Error", "java.lang.Error" -> Error::class
        "kotlin.AssertionError", "java.lang.AssertionError" -> AssertionError::class
        "kotlin.IllegalArgumentException", "java.lang.IllegalArgumentException" -> IllegalArgumentException::class
        "kotlin.IllegalStateException", "java.lang.IllegalStateException" -> IllegalStateException::class
        "kotlin.IndexOutOfBoundsException", "java.lang.IndexOutOfBoundsException" -> IndexOutOfBoundsException::class
        "kotlin.UnsupportedOperationException", "java.lang.UnsupportedOperationException" -> UnsupportedOperationException::class
        "kotlin.NumberFormatException", "java.lang.NumberFormatException" -> NumberFormatException::class
        "kotlin.NullPointerException", "java.lang.NullPointerException" -> NullPointerException::class
        "kotlin.ClassCastException", "java.lang.ClassCastException" -> ClassCastException::class
        "kotlin.ArithmeticException", "java.lang.ArithmeticException" -> ArithmeticException::class
        "kotlin.NoSuchElementException", "java.util.NoSuchElementException" -> NoSuchElementException::class
        "kotlin.ConcurrentModificationException", "java.util.ConcurrentModificationException" -> ConcurrentModificationException::class
        "kotlin.NoWhenBranchMatchedException" -> kotlin.NoWhenBranchMatchedException::class
        "kotlin.UninitializedPropertyAccessException" -> kotlin.UninitializedPropertyAccessException::class
        "kotlin.Function" -> Function::class
        else -> {
            if (fqName.startsWith("kotlin.Function") || fqName.startsWith("kotlin.reflect.KFunction")) {
                KFunctionProxy::class
            } else {
                Any::class
            }
        }
    }
}
