/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.builtins.jvm

import org.jetbrains.kotlin.builtins.CompanionObjectMapping
import org.jetbrains.kotlin.builtins.StandardNames
import org.jetbrains.kotlin.builtins.StandardNames.FqNames
import org.jetbrains.kotlin.builtins.functions.BuiltInFunctionArity
import org.jetbrains.kotlin.builtins.functions.FunctionTypeKind
import org.jetbrains.kotlin.name.*
import org.jetbrains.kotlin.resolve.jvm.JvmPrimitiveType

object JavaToKotlinClassMap {
    private val NUMBERED_FUNCTION_PREFIX: String =
        FunctionTypeKind.Function.packageFqName.toString() + "." + FunctionTypeKind.Function.classNamePrefix
    private val NUMBERED_K_FUNCTION_PREFIX: String =
        FunctionTypeKind.KFunction.packageFqName.toString() + "." + FunctionTypeKind.KFunction.classNamePrefix
    private val NUMBERED_SUSPEND_FUNCTION_PREFIX: String =
        FunctionTypeKind.SuspendFunction.packageFqName.toString() + "." + FunctionTypeKind.SuspendFunction.classNamePrefix
    private val NUMBERED_K_SUSPEND_FUNCTION_PREFIX: String =
        FunctionTypeKind.KSuspendFunction.packageFqName.toString() + "." + FunctionTypeKind.KSuspendFunction.classNamePrefix

    private val FUNCTION_N_CLASS_ID: ClassId = ClassId.topLevel(FqName("kotlin.jvm.functions.FunctionN"))
    val FUNCTION_N_FQ_NAME: FqName = FUNCTION_N_CLASS_ID.asSingleFqName()
    private val K_FUNCTION_CLASS_ID: ClassId = StandardClassIds.KFunction
    private val K_CLASS_CLASS_ID: ClassId = StandardClassIds.KClass
    private val CLASS_CLASS_ID: ClassId = ClassId.topLevel(FqName("java.lang.Class"))

    private val javaToKotlin = HashMap<FqNameUnsafe, ClassId>()
    private val kotlinToJava = HashMap<FqNameUnsafe, ClassId>()

    private val mutableToReadOnly = HashMap<FqNameUnsafe, FqName>()
    private val readOnlyToMutable = HashMap<FqNameUnsafe, FqName>()

    private val mutableToReadOnlyClassId = HashMap<ClassId, ClassId>()
    private val readOnlyToMutableClassId = HashMap<ClassId, ClassId>()

    private val mappedKotlinClassFqNames = mutableSetOf<FqName>()

    // describes mapping for a java class that has separate readOnly and mutable equivalents in Kotlin
    data class PlatformMutabilityMapping(
        val javaClass: ClassId,
        val kotlinReadOnly: ClassId,
        val kotlinMutable: ClassId
    )

    private fun mutabilityMapping(javaClass: ClassId, kotlinReadOnly: ClassId, kotlinMutable: FqName): PlatformMutabilityMapping {
        val mutableClassId = ClassId(kotlinReadOnly.packageFqName, kotlinMutable.tail(kotlinReadOnly.packageFqName), isLocal = false)
        return PlatformMutabilityMapping(javaClass, kotlinReadOnly, mutableClassId)
    }

    val mutabilityMappings = listOf(
        mutabilityMapping(ClassId.topLevel(FqName("java.lang.Iterable")), ClassId.topLevel(FqNames.iterable), FqNames.mutableIterable),
        mutabilityMapping(ClassId.topLevel(FqName("java.util.Iterator")), ClassId.topLevel(FqNames.iterator), FqNames.mutableIterator),
        mutabilityMapping(ClassId.topLevel(FqName("java.util.Collection")), ClassId.topLevel(FqNames.collection), FqNames.mutableCollection),
        mutabilityMapping(ClassId.topLevel(FqName("java.util.List")), ClassId.topLevel(FqNames.list), FqNames.mutableList),
        mutabilityMapping(ClassId.topLevel(FqName("java.util.Set")), ClassId.topLevel(FqNames.set), FqNames.mutableSet),
        mutabilityMapping(ClassId.topLevel(FqName("java.util.ListIterator")), ClassId.topLevel(FqNames.listIterator), FqNames.mutableListIterator),
        mutabilityMapping(ClassId.topLevel(FqName("java.util.Map")), ClassId.topLevel(FqNames.map), FqNames.mutableMap),
        mutabilityMapping(
            ClassId.topLevel(FqName("java.util.Map")).createNestedClassId(Name.identifier("Entry")),
            ClassId.topLevel(FqNames.map).createNestedClassId(FqNames.mapEntry.shortName()), FqNames.mutableMapEntry
        )
    )

    init {
        addTopLevel("java.lang.Object", FqNames.any)
        addTopLevel("java.lang.String", FqNames.string)
        addTopLevel("java.lang.CharSequence", FqNames.charSequence)
        addTopLevel("java.lang.Throwable", FqNames.throwable)
        addTopLevel("java.lang.Cloneable", FqNames.cloneable)
        addTopLevel("java.lang.Number", FqNames.number)
        addTopLevel("java.lang.Comparable", FqNames.comparable)
        addTopLevel("java.lang.Enum", FqNames._enum)
        addTopLevel("java.lang.annotation.Annotation", FqNames.annotation)

        for (platformCollection in mutabilityMappings) {
            addMapping(platformCollection)
        }

        for (jvmType in JvmPrimitiveType.values()) {
            add(
                ClassId.topLevel(jvmType.wrapperFqName),
                ClassId.topLevel(StandardNames.getPrimitiveFqName(jvmType.primitiveType))
            )
        }

        for (classId in CompanionObjectMapping.allClassesWithIntrinsicCompanions()) {
            add(
                ClassId.topLevel(FqName("kotlin.jvm.internal." + classId.shortClassName.asString() + "CompanionObject")),
                classId.createNestedClassId(SpecialNames.DEFAULT_NAME_FOR_COMPANION_OBJECT)
            )
        }

        for (i in 0 until BuiltInFunctionArity.BIG_ARITY) {
            add(ClassId.topLevel(FqName("kotlin.jvm.functions.Function$i")), StandardNames.getFunctionClassId(i))
            addKotlinToJava(FqName(NUMBERED_K_FUNCTION_PREFIX + i), K_FUNCTION_CLASS_ID)
        }
        for (i in 0 until BuiltInFunctionArity.BIG_ARITY - 1) {
            addKotlinToJava(FqName(NUMBERED_K_SUSPEND_FUNCTION_PREFIX + i), K_FUNCTION_CLASS_ID)
        }

        addKotlinToJava(FqName("kotlin.concurrent.atomics.AtomicInt"), ClassId.topLevel(FqName("java.util.concurrent.atomic.AtomicInteger")))
        addKotlinToJava(FqName("kotlin.concurrent.atomics.AtomicLong"), ClassId.topLevel(FqName("java.util.concurrent.atomic.AtomicLong")))
        addKotlinToJava(FqName("kotlin.concurrent.atomics.AtomicBoolean"), ClassId.topLevel(FqName("java.util.concurrent.atomic.AtomicBoolean")))
        addKotlinToJava(FqName("kotlin.concurrent.atomics.AtomicReference"), ClassId.topLevel(FqName("java.util.concurrent.atomic.AtomicReference")))
        addKotlinToJava(FqName("kotlin.concurrent.atomics.AtomicIntArray"), ClassId.topLevel(FqName("java.util.concurrent.atomic.AtomicIntegerArray")))
        addKotlinToJava(FqName("kotlin.concurrent.atomics.AtomicLongArray"), ClassId.topLevel(FqName("java.util.concurrent.atomic.AtomicLongArray")))
        addKotlinToJava(FqName("kotlin.concurrent.atomics.AtomicArray"), ClassId.topLevel(FqName("java.util.concurrent.atomic.AtomicReferenceArray")))

        addKotlinToJava(FqNames.nothing.toSafe(), ClassId.topLevel(FqName("java.lang.Void")))
    }

    /**
     * E.g.
     * - java.lang.String -> kotlin.String
     * - java.lang.Integer -> kotlin.Int
     * - kotlin.jvm.internal.IntCompanionObject -> kotlin.Int.Companion
     * - java.util.List -> kotlin.List
     * - java.util.Map.Entry -> kotlin.Map.Entry
     * - java.lang.Void -> null
     * - kotlin.jvm.functions.Function3 -> kotlin.Function3
     * - kotlin.jvm.functions.FunctionN -> null // Without a type annotation like @Arity(n), it's impossible to find out arity
     */
    fun mapJavaToKotlin(fqName: FqName): ClassId? {
        return javaToKotlin[fqName.toUnsafe()]
    }

    fun mapJavaToKotlinIncludingClassMapping(fqName: FqName): ClassId? {
        if (fqName == CLASS_CLASS_ID.asSingleFqName()) return K_CLASS_CLASS_ID
        return mapJavaToKotlin(fqName)
    }

    /**
     * E.g.
     * - kotlin.Throwable -> java.lang.Throwable
     * - kotlin.Int -> java.lang.Integer
     * - kotlin.Int.Companion -> kotlin.jvm.internal.IntCompanionObject
     * - kotlin.Nothing -> java.lang.Void
     * - kotlin.IntArray -> null
     * - kotlin.Function3 -> kotlin.jvm.functions.Function3
     * - kotlin.coroutines.SuspendFunction3 -> null
     *     - NOT kotlin.jvm.functions.Function4, to make sure that Continuation type argument is correctly added manually at the call site.
     * - kotlin.Function23 -> kotlin.jvm.functions.FunctionN
     * - kotlin.coroutines.SuspendFunction22 -> kotlin.jvm.functions.FunctionN
     * - kotlin.reflect.KFunction3 -> kotlin.reflect.KFunction
     * - kotlin.reflect.KSuspendFunction3 -> kotlin.reflect.KFunction
     * - kotlin.reflect.KFunction23 -> kotlin.reflect.KFunction
     * - kotlin.reflect.KSuspendFunction22 -> kotlin.reflect.KFunction
     */
    fun mapKotlinToJava(kotlinFqName: FqNameUnsafe): ClassId? = when {
        isKotlinFunctionWithBigArity(kotlinFqName, NUMBERED_FUNCTION_PREFIX) -> FUNCTION_N_CLASS_ID
        isKotlinFunctionWithBigArity(kotlinFqName, NUMBERED_SUSPEND_FUNCTION_PREFIX, isSuspend = true) -> FUNCTION_N_CLASS_ID

        // TODO: Simplify handling KFunction and KSuspendFunction, since they all map directly to kotlin.reflect.KFunction
        isKotlinFunctionWithBigArity(kotlinFqName, NUMBERED_K_FUNCTION_PREFIX) -> K_FUNCTION_CLASS_ID
        isKotlinFunctionWithBigArity(kotlinFqName, NUMBERED_K_SUSPEND_FUNCTION_PREFIX, isSuspend = true) -> K_FUNCTION_CLASS_ID

        else -> kotlinToJava[kotlinFqName]
    }

    private fun isKotlinFunctionWithBigArity(kotlinFqName: FqNameUnsafe, prefix: String, isSuspend: Boolean = false): Boolean {
        val fqNameAsString = kotlinFqName.asString()
        if (!fqNameAsString.startsWith(prefix)) return false
        val arityString = fqNameAsString.substring(prefix.length)
        val arity = if (!arityString.startsWith('0')) arityString.toIntOrNull() else return false

        val cutOffArity = if (isSuspend) {
            BuiltInFunctionArity.BIG_ARITY - 1
        } else {
            BuiltInFunctionArity.BIG_ARITY
        }

        return arity != null && arity >= cutOffArity
    }

    private fun addMapping(platformMutabilityMapping: PlatformMutabilityMapping) {
        (val javaClassId = javaClass, val readOnlyClassId = kotlinReadOnly, val mutableClassId = kotlinMutable) = platformMutabilityMapping
        add(javaClassId, readOnlyClassId)
        addKotlinToJava(mutableClassId.asSingleFqName(), javaClassId)

        mutableToReadOnlyClassId[mutableClassId] = readOnlyClassId
        readOnlyToMutableClassId[readOnlyClassId] = mutableClassId

        val readOnlyFqName = readOnlyClassId.asSingleFqName()
        val mutableFqName = mutableClassId.asSingleFqName()
        mutableToReadOnly[mutableClassId.asSingleFqName().toUnsafe()] = readOnlyFqName
        readOnlyToMutable[readOnlyFqName.toUnsafe()] = mutableFqName
    }

    private fun add(javaClassId: ClassId, kotlinClassId: ClassId) {
        addJavaToKotlin(javaClassId, kotlinClassId)
        addKotlinToJava(kotlinClassId.asSingleFqName(), javaClassId)
    }

    private fun addTopLevel(javaClass: String, kotlinFqName: FqNameUnsafe) {
        addTopLevel(javaClass, kotlinFqName.toSafe())
    }

    private fun addTopLevel(javaClass: String, kotlinFqName: FqName) {
        add(ClassId.topLevel(FqName(javaClass)), ClassId.topLevel(kotlinFqName))
    }

    private fun addJavaToKotlin(javaClassId: ClassId, kotlinClassId: ClassId) {
        javaToKotlin[javaClassId.asSingleFqName().toUnsafe()] = kotlinClassId
    }

    private fun addKotlinToJava(kotlinFqName: FqName, javaClassId: ClassId) {
        mappedKotlinClassFqNames.add(kotlinFqName)
        kotlinToJava[kotlinFqName.toUnsafe()] = javaClassId
    }

    fun isMappedKotlinClass(kotlinClassId: ClassId): Boolean = mappedKotlinClassFqNames.contains(kotlinClassId.asSingleFqName())

    fun isJavaPlatformClass(fqName: FqName): Boolean = mapJavaToKotlin(fqName) != null

    fun mutableToReadOnly(fqNameUnsafe: FqNameUnsafe?): FqName? = mutableToReadOnly[fqNameUnsafe]
    fun readOnlyToMutable(fqNameUnsafe: FqNameUnsafe?): FqName? = readOnlyToMutable[fqNameUnsafe]

    fun mutableToReadOnly(classId: ClassId): ClassId? = mutableToReadOnlyClassId[classId]
    fun readOnlyToMutable(classId: ClassId): ClassId? = readOnlyToMutableClassId[classId]

    fun isMutable(fqNameUnsafe: FqNameUnsafe?): Boolean = mutableToReadOnly.containsKey(fqNameUnsafe)
    fun isReadOnly(fqNameUnsafe: FqNameUnsafe?): Boolean = readOnlyToMutable.containsKey(fqNameUnsafe)

    fun isMutable(classId: ClassId?): Boolean = mutableToReadOnlyClassId.containsKey(classId)
    fun isReadOnly(classId: ClassId?): Boolean = readOnlyToMutableClassId.containsKey(classId)

    fun getReadOnlyAsJava(): Set<FqName> = readOnlyToMutable.keys.mapTo(mutableSetOf()) { kotlinToJava[it]!!.asSingleFqName() }

}
