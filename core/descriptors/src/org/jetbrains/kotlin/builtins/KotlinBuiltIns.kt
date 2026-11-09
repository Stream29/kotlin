/*
 * Copyright 2010-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin.builtins

import org.jetbrains.kotlin.builtins.PrimitiveType.*
import org.jetbrains.kotlin.builtins.StandardNames.ANNOTATION_PACKAGE_FQ_NAME
import org.jetbrains.kotlin.builtins.StandardNames.BUILT_INS_PACKAGE_FQ_NAME
import org.jetbrains.kotlin.builtins.StandardNames.BUILT_INS_PACKAGE_NAME
import org.jetbrains.kotlin.builtins.StandardNames.COLLECTIONS_PACKAGE_FQ_NAME
import org.jetbrains.kotlin.builtins.StandardNames.COROUTINES_PACKAGE_FQ_NAME
import org.jetbrains.kotlin.builtins.StandardNames.RANGES_PACKAGE_FQ_NAME
import org.jetbrains.kotlin.builtins.StandardNames.FqNames
import org.jetbrains.kotlin.builtins.StandardNames.getFunctionName
import org.jetbrains.kotlin.builtins.StandardNames.getKFunctionFqName
import org.jetbrains.kotlin.builtins.StandardNames.getSuspendFunctionName
import org.jetbrains.kotlin.builtins.functions.BuiltInFictitiousFunctionClassFactory
import org.jetbrains.kotlin.builtins.functions.FunctionTypeKind
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.deserialization.AdditionalClassPartsProvider
import org.jetbrains.kotlin.descriptors.deserialization.ClassDescriptorFactory
import org.jetbrains.kotlin.descriptors.deserialization.PlatformDependentDeclarationFilter
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.name.*
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.descriptorUtil.classId
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.storage.NotNullLazyValue
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.types.*
import org.jetbrains.kotlin.types.checker.KotlinTypeChecker

// Translated-unverified. BuiltInsLoader is still the mandatory loading path, not a substitute registry.
abstract class KotlinBuiltIns protected constructor(private val storageManager: StorageManager) {
    private var builtInsModule: ModuleDescriptorImpl? = null
    private var postponedBuiltInsModule: NotNullLazyValue<ModuleDescriptorImpl>? = null
    private val builtInPackagesImportedByDefault = storageManager.createLazyValue {
        listOf(
            getBuiltInsModule().getPackage(BUILT_INS_PACKAGE_FQ_NAME), getBuiltInsModule().getPackage(COLLECTIONS_PACKAGE_FQ_NAME),
            getBuiltInsModule().getPackage(RANGES_PACKAGE_FQ_NAME), getBuiltInsModule().getPackage(ANNOTATION_PACKAGE_FQ_NAME)
        )
    }
    private val primitives = storageManager.createLazyValue {
        val primitiveTypeToArrayKotlinType = LinkedHashMap<PrimitiveType, SimpleType>()
        val primitiveKotlinTypeToKotlinArrayType = HashMap<KotlinType, SimpleType>()
        val kotlinArrayTypeToPrimitiveKotlinType = HashMap<SimpleType, SimpleType>()
        for (primitive in PrimitiveType.entries) {
            val type = getBuiltInTypeByClassName(primitive.typeName.asString())
            val arrayType = getBuiltInTypeByClassName(primitive.arrayTypeName.asString())
            primitiveTypeToArrayKotlinType[primitive] = arrayType
            primitiveKotlinTypeToKotlinArrayType[type] = arrayType
            kotlinArrayTypeToPrimitiveKotlinType[arrayType] = type
        }
        Primitives(primitiveTypeToArrayKotlinType, primitiveKotlinTypeToKotlinArrayType, kotlinArrayTypeToPrimitiveKotlinType)
    }
    private val builtInClassesByName = storageManager.createMemoizedFunction<Name, ClassDescriptor> { name ->
        val classifier = getBuiltInsPackageScope().getContributedClassifier(name, NoLookupLocation.FROM_BUILTINS)
            ?: throw AssertionError("Built-in class ${BUILT_INS_PACKAGE_FQ_NAME.child(name)} is not found")
        if (classifier !is ClassDescriptor) throw AssertionError("Must be a class descriptor $name, but was $classifier")
        classifier
    }
    protected open fun createBuiltInsModule(isFallback: Boolean) {
        val module = ModuleDescriptorImpl(BUILTINS_MODULE_NAME, storageManager, this, null)
        builtInsModule = module
        module.initialize(BuiltInsLoader.Instance.createPackageFragmentProvider(
            storageManager, module, getClassDescriptorFactories(), getPlatformDependentDeclarationFilter(), getAdditionalClassPartsProvider(), isFallback
        ))
        module.setDependencies(module)
    }
    open fun setBuiltInsModule(module: ModuleDescriptorImpl) {
        storageManager.compute {
            if (builtInsModule != null) throw AssertionError("Built-ins module is already set: $builtInsModule (attempting to reset to $module)")
            builtInsModule = module
        }
    }
    open fun setPostponedBuiltinsModuleComputation(computation: () -> ModuleDescriptorImpl) {
        postponedBuiltInsModule = storageManager.createLazyValue(computation)
    }
    protected open fun getAdditionalClassPartsProvider(): AdditionalClassPartsProvider = AdditionalClassPartsProvider.None
    protected open fun getPlatformDependentDeclarationFilter(): PlatformDependentDeclarationFilter = PlatformDependentDeclarationFilter.NoPlatformDependent
    protected open fun getClassDescriptorFactories(): Iterable<ClassDescriptorFactory> =
        listOf(BuiltInFictitiousFunctionClassFactory(storageManager, getBuiltInsModule()))
    protected open fun getStorageManager(): StorageManager = storageManager
    private class Primitives(
        val primitiveTypeToArrayKotlinType: Map<PrimitiveType, SimpleType>,
        val primitiveKotlinTypeToKotlinArrayType: Map<KotlinType, SimpleType>,
        val kotlinArrayTypeToPrimitiveKotlinType: Map<SimpleType, SimpleType>
    )
    open fun getBuiltInsModule(): ModuleDescriptorImpl {
        assert(builtInsModule != null || postponedBuiltInsModule != null) { "Uninitialized built-ins module" }
        if (builtInsModule == null) builtInsModule = postponedBuiltInsModule!!()
        return builtInsModule!!
    }
    open fun getBuiltInPackagesImportedByDefault(): Collection<PackageViewDescriptor> = builtInPackagesImportedByDefault()
    open fun getBuiltInsPackageScope(): MemberScope = getBuiltInsModule().getPackage(BUILT_INS_PACKAGE_FQ_NAME).memberScope
    open fun getBuiltInClassByFqName(fqName: FqName): ClassDescriptor {
        val descriptor = getBuiltInsModule().resolveClassByFqName(fqName, NoLookupLocation.FROM_BUILTINS)
        assert(descriptor != null) { "Can't find built-in class $fqName" }
        return descriptor!!
    }
    private fun getBuiltInClassByName(simpleName: String): ClassDescriptor = builtInClassesByName(Name.identifier(simpleName))
    open fun getAny(): ClassDescriptor = getBuiltInClassByName("Any")
    open fun getNothing(): ClassDescriptor = getBuiltInClassByName("Nothing")
    private fun getPrimitiveClassDescriptor(type: PrimitiveType): ClassDescriptor = getBuiltInClassByName(type.typeName.asString())
    open fun getByte(): ClassDescriptor = getPrimitiveClassDescriptor(BYTE)
    open fun getShort(): ClassDescriptor = getPrimitiveClassDescriptor(SHORT)
    open fun getInt(): ClassDescriptor = getPrimitiveClassDescriptor(INT)
    open fun getLong(): ClassDescriptor = getPrimitiveClassDescriptor(LONG)
    open fun getFloat(): ClassDescriptor = getPrimitiveClassDescriptor(FLOAT)
    open fun getDouble(): ClassDescriptor = getPrimitiveClassDescriptor(DOUBLE)
    open fun getChar(): ClassDescriptor = getPrimitiveClassDescriptor(CHAR)
    open fun getBoolean(): ClassDescriptor = getPrimitiveClassDescriptor(BOOLEAN)
    open fun getArray(): ClassDescriptor = getBuiltInClassByName("Array")
    open fun getPrimitiveArrayClassDescriptor(type: PrimitiveType): ClassDescriptor = getBuiltInClassByName(type.arrayTypeName.asString())
    open fun getNumber(): ClassDescriptor = getBuiltInClassByName("Number")
    open fun getUnit(): ClassDescriptor = getBuiltInClassByName("Unit")
    open fun getFunction(parameterCount: Int): ClassDescriptor = getBuiltInClassByName(getFunctionName(parameterCount))
    open fun getSuspendFunction(parameterCount: Int): ClassDescriptor =
        getBuiltInClassByFqName(COROUTINES_PACKAGE_FQ_NAME.child(Name.identifier(getSuspendFunctionName(parameterCount))))
    open fun getKFunction(parameterCount: Int): ClassDescriptor = getBuiltInClassByFqName(getKFunctionFqName(parameterCount).toSafe())
    open fun getKSuspendFunction(parameterCount: Int): ClassDescriptor =
        getBuiltInClassByFqName(COROUTINES_PACKAGE_FQ_NAME.child(Name.identifier(FunctionTypeKind.KSuspendFunction.classNamePrefix + parameterCount)))
    open fun getThrowable(): ClassDescriptor = getBuiltInClassByName("Throwable")
    open fun getString(): ClassDescriptor = getBuiltInClassByName("String")
    open fun getCharSequence(): ClassDescriptor = getBuiltInClassByName("CharSequence")
    open fun getComparable(): ClassDescriptor = getBuiltInClassByName("Comparable")
    open fun getEnum(): ClassDescriptor = getBuiltInClassByName("Enum")
    open fun getAnnotation(): ClassDescriptor = getBuiltInClassByName("Annotation")
    open fun getKClass(): ClassDescriptor = getBuiltInClassByFqName(FqNames.kClass.toSafe())
    open fun getKType(): ClassDescriptor = getBuiltInClassByFqName(FqNames.kType.toSafe())
    open fun getKCallable(): ClassDescriptor = getBuiltInClassByFqName(FqNames.kCallable.toSafe())
    open fun getKProperty(): ClassDescriptor = getBuiltInClassByFqName(FqNames.kPropertyFqName.toSafe())
    open fun getKProperty0(): ClassDescriptor = getBuiltInClassByFqName(FqNames.kProperty0.toSafe())
    open fun getKProperty1(): ClassDescriptor = getBuiltInClassByFqName(FqNames.kProperty1.toSafe())
    open fun getKProperty2(): ClassDescriptor = getBuiltInClassByFqName(FqNames.kProperty2.toSafe())
    open fun getKMutableProperty0(): ClassDescriptor = getBuiltInClassByFqName(FqNames.kMutableProperty0.toSafe())
    open fun getKMutableProperty1(): ClassDescriptor = getBuiltInClassByFqName(FqNames.kMutableProperty1.toSafe())
    open fun getKMutableProperty2(): ClassDescriptor = getBuiltInClassByFqName(FqNames.kMutableProperty2.toSafe())
    open fun getIterator(): ClassDescriptor = getBuiltInClassByFqName(FqNames.iterator)
    open fun getIterable(): ClassDescriptor = getBuiltInClassByFqName(FqNames.iterable)
    open fun getMutableIterable(): ClassDescriptor = getBuiltInClassByFqName(FqNames.mutableIterable)
    open fun getMutableIterator(): ClassDescriptor = getBuiltInClassByFqName(FqNames.mutableIterator)
    open fun getCollection(): ClassDescriptor = getBuiltInClassByFqName(FqNames.collection)
    open fun getMutableCollection(): ClassDescriptor = getBuiltInClassByFqName(FqNames.mutableCollection)
    open fun getList(): ClassDescriptor = getBuiltInClassByFqName(FqNames.list)
    open fun getMutableList(): ClassDescriptor = getBuiltInClassByFqName(FqNames.mutableList)
    open fun getSet(): ClassDescriptor = getBuiltInClassByFqName(FqNames.set)
    open fun getMutableSet(): ClassDescriptor = getBuiltInClassByFqName(FqNames.mutableSet)
    open fun getMap(): ClassDescriptor = getBuiltInClassByFqName(FqNames.map)
    open fun getMutableMap(): ClassDescriptor = getBuiltInClassByFqName(FqNames.mutableMap)
    open fun getMapEntry(): ClassDescriptor {
        val descriptor = DescriptorUtils.getInnerClassByName(getMap(), "Entry", NoLookupLocation.FROM_BUILTINS)
        assert(descriptor != null) { "Can't find Map.Entry" }
        return descriptor!!
    }
    open fun getMutableMapEntry(): ClassDescriptor {
        val descriptor = DescriptorUtils.getInnerClassByName(getMutableMap(), "MutableEntry", NoLookupLocation.FROM_BUILTINS)
        assert(descriptor != null) { "Can't find MutableMap.MutableEntry" }
        return descriptor!!
    }
    open fun getListIterator(): ClassDescriptor = getBuiltInClassByFqName(FqNames.listIterator)
    open fun getMutableListIterator(): ClassDescriptor = getBuiltInClassByFqName(FqNames.mutableListIterator)
    private fun getBuiltInTypeByClassName(classSimpleName: String): SimpleType = getBuiltInClassByName(classSimpleName).getDefaultType()
    open fun getNothingType(): SimpleType = getNothing().getDefaultType()
    open fun getNullableNothingType(): SimpleType = getNothingType().makeNullableAsSpecified(true)
    open fun getAnyType(): SimpleType = getAny().getDefaultType()
    open fun getNullableAnyType(): SimpleType = getAnyType().makeNullableAsSpecified(true)
    open fun getDefaultBound(): SimpleType = getNullableAnyType()
    open fun getPrimitiveKotlinType(type: PrimitiveType): SimpleType = getPrimitiveClassDescriptor(type).getDefaultType()
    open fun getNumberType(): SimpleType = getNumber().getDefaultType()
    open fun getByteType(): SimpleType = getPrimitiveKotlinType(BYTE)
    open fun getShortType(): SimpleType = getPrimitiveKotlinType(SHORT)
    open fun getIntType(): SimpleType = getPrimitiveKotlinType(INT)
    open fun getLongType(): SimpleType = getPrimitiveKotlinType(LONG)
    open fun getFloatType(): SimpleType = getPrimitiveKotlinType(FLOAT)
    open fun getDoubleType(): SimpleType = getPrimitiveKotlinType(DOUBLE)
    open fun getCharType(): SimpleType = getPrimitiveKotlinType(CHAR)
    open fun getBooleanType(): SimpleType = getPrimitiveKotlinType(BOOLEAN)
    open fun getUnitType(): SimpleType = getUnit().getDefaultType()
    open fun getStringType(): SimpleType = getString().getDefaultType()
    open fun getIterableType(): KotlinType = getIterable().getDefaultType()
    open fun getArrayElementType(arrayType: KotlinType): KotlinType =
        getArrayElementTypeOrNull(arrayType) ?: throw IllegalStateException("not array: $arrayType")
    open fun getArrayElementTypeOrNull(arrayType: KotlinType): KotlinType? {
        if (isArray(arrayType)) {
            if (arrayType.arguments.size != 1) return null
            return arrayType.arguments[0].getType()
        }
        val notNullArrayType = TypeUtils.makeNotNullable(arrayType)
        val primitiveType = primitives().kotlinArrayTypeToPrimitiveKotlinType[notNullArrayType]
        if (primitiveType != null) return primitiveType
        val module = DescriptorUtils.getContainingModuleOrNull(notNullArrayType)
        if (module != null) {
            val unsignedType = getElementTypeForUnsignedArray(notNullArrayType, module)
            if (unsignedType != null) return unsignedType
        }
        return null
    }
    open fun getPrimitiveArrayKotlinType(primitiveType: PrimitiveType): SimpleType = primitives().primitiveTypeToArrayKotlinType.getValue(primitiveType)
    open fun getPrimitiveArrayKotlinTypeByPrimitiveKotlinType(kotlinType: KotlinType): SimpleType? {
        val primitiveArray = primitives().primitiveKotlinTypeToKotlinArrayType[kotlinType]
        if (primitiveArray != null) return primitiveArray
        if (UnsignedTypes.isUnsignedType(kotlinType)) {
            if (TypeUtils.isNullableType(kotlinType)) return null
            val module = DescriptorUtils.getContainingModuleOrNull(kotlinType) ?: return null
            val unsignedClassId = kotlinType.constructor.getDeclarationDescriptor()?.classId
            assert(unsignedClassId != null) { "unsignedClassId should not be null for unsigned type $kotlinType" }
            val arrayClassId = UnsignedTypes.getUnsignedArrayClassIdByUnsignedClassId(unsignedClassId!!)
            assert(arrayClassId != null) { "arrayClassId should not be null for unsigned type $unsignedClassId" }
            val arrayClassDescriptor = module.findClassAcrossModuleDependencies(arrayClassId!!) ?: return null
            return arrayClassDescriptor.getDefaultType()
        }
        return null
    }
    open fun getArrayType(projectionType: Variance, argument: KotlinType, annotations: Annotations): SimpleType =
        KotlinTypeFactory.simpleNotNullType(annotations.toDefaultAttributes(), getArray(), listOf(TypeProjectionImpl(projectionType, argument)))
    open fun getArrayType(projectionType: Variance, argument: KotlinType): SimpleType = getArrayType(projectionType, argument, Annotations.EMPTY)
    open fun getEnumType(argument: SimpleType): SimpleType =
        KotlinTypeFactory.simpleNotNullType(TypeAttributes.Empty, getEnum(), listOf(TypeProjectionImpl(Variance.INVARIANT, argument)))
    open fun getAnnotationType(): SimpleType = getAnnotation().getDefaultType()
    open fun isBooleanOrSubtype(type: KotlinType): Boolean = KotlinTypeChecker.DEFAULT.isSubtypeOf(type, getBooleanType())
    open fun isMemberOfAny(descriptor: DeclarationDescriptor): Boolean = descriptor.getContainingDeclaration() === getAny()
    companion object {
        val BUILTINS_MODULE_NAME: Name = Name.special("<built-ins module>")
        fun isBuiltIn(descriptor: DeclarationDescriptor): Boolean =
            DescriptorUtils.getParentOfType(descriptor, BuiltInsPackageFragment::class, false) != null
        fun isUnderKotlinPackage(descriptor: DeclarationDescriptor): Boolean {
            var current: DeclarationDescriptor? = descriptor
            while (current != null) {
                if (current is PackageFragmentDescriptor) return current.fqName.startsWith(BUILT_INS_PACKAGE_NAME)
                current = current.getContainingDeclaration()
            }
            return false
        }
        private fun getElementTypeForUnsignedArray(notNullArrayType: KotlinType, module: ModuleDescriptor): KotlinType? {
            val descriptor = notNullArrayType.constructor.getDeclarationDescriptor() ?: return null
            if (!UnsignedTypes.isShortNameOfUnsignedArray(descriptor.getName())) return null
            val arrayClassId = descriptor.classId ?: return null
            val elementClassId = UnsignedTypes.getUnsignedClassIdByArrayClassId(arrayClassId) ?: return null
            val elementClassDescriptor = module.findClassAcrossModuleDependencies(elementClassId) ?: return null
            return elementClassDescriptor.getDefaultType()
        }
        fun getPrimitiveType(descriptor: DeclarationDescriptor): PrimitiveType? =
            if (FqNames.primitiveTypeShortNames.contains(descriptor.getName())) FqNames.fqNameToPrimitiveType[DescriptorUtils.getFqName(descriptor)] else null
        fun getPrimitiveArrayType(descriptor: DeclarationDescriptor): PrimitiveType? =
            if (FqNames.primitiveArrayTypeShortNames.contains(descriptor.getName())) FqNames.arrayClassFqNameToPrimitiveType[DescriptorUtils.getFqName(descriptor)] else null
        fun isArray(type: KotlinType): Boolean = isConstructedFromGivenClass(type, FqNames.array)
        fun isArrayOrPrimitiveArray(descriptor: ClassDescriptor): Boolean = classFqNameEquals(descriptor, FqNames.array) || getPrimitiveArrayType(descriptor) != null
        fun isArrayOrPrimitiveArray(type: KotlinType): Boolean = isArray(type) || isPrimitiveArray(type)
        fun isPrimitiveArray(type: KotlinType): Boolean = type.constructor.getDeclarationDescriptor()?.let { getPrimitiveArrayType(it) != null } ?: false
        fun getPrimitiveArrayElementType(type: KotlinType): PrimitiveType? = type.constructor.getDeclarationDescriptor()?.let { getPrimitiveArrayType(it) }
        fun getPrimitiveType(type: KotlinType): PrimitiveType? = type.constructor.getDeclarationDescriptor()?.let { getPrimitiveType(it) }
        fun isPrimitiveType(type: KotlinType): Boolean = !type.isMarkedNullable && isPrimitiveTypeOrNullablePrimitiveType(type)
        fun isPrimitiveTypeOrNullablePrimitiveType(type: KotlinType): Boolean =
            (type.constructor.getDeclarationDescriptor() as? ClassDescriptor)?.let { isPrimitiveClass(it) } ?: false
        fun isPrimitiveClass(descriptor: ClassDescriptor): Boolean = getPrimitiveType(descriptor) != null
        private fun isConstructedFromGivenClass(type: KotlinType, fqName: FqNameUnsafe): Boolean = isTypeConstructorForGivenClass(type.constructor, fqName)
        fun isConstructedFromGivenClass(type: KotlinType, fqName: FqName): Boolean = isConstructedFromGivenClass(type, fqName.toUnsafe())
        fun isTypeConstructorForGivenClass(typeConstructor: TypeConstructor, fqName: FqNameUnsafe): Boolean =
            (typeConstructor.getDeclarationDescriptor() as? ClassDescriptor)?.let { classFqNameEquals(it, fqName) } ?: false
        private fun classFqNameEquals(descriptor: ClassifierDescriptor, fqName: FqNameUnsafe): Boolean =
            descriptor.getName() == fqName.shortName() && fqName == DescriptorUtils.getFqName(descriptor)
        private fun isNotNullConstructedFromGivenClass(type: KotlinType, fqName: FqNameUnsafe): Boolean =
            !type.isMarkedNullable && isConstructedFromGivenClass(type, fqName)
        fun isSpecialClassWithNoSupertypes(descriptor: ClassDescriptor): Boolean = classFqNameEquals(descriptor, FqNames.any) || classFqNameEquals(descriptor, FqNames.nothing)
        fun isAny(descriptor: ClassDescriptor): Boolean = classFqNameEquals(descriptor, FqNames.any)
        fun isAny(type: KotlinType): Boolean = isConstructedFromGivenClassAndNotNullable(type, FqNames.any)
        fun isBoolean(type: KotlinType): Boolean = isConstructedFromGivenClassAndNotNullable(type, FqNames._boolean)
        fun isBooleanOrNullableBoolean(type: KotlinType): Boolean = isConstructedFromGivenClass(type, FqNames._boolean)
        fun isBoolean(classDescriptor: ClassDescriptor): Boolean = classFqNameEquals(classDescriptor, FqNames._boolean)
        fun isNumber(type: KotlinType): Boolean = isConstructedFromGivenClassAndNotNullable(type, FqNames.number)
        fun isChar(type: KotlinType): Boolean = isConstructedFromGivenClassAndNotNullable(type, FqNames._char)
        fun isCharOrNullableChar(type: KotlinType): Boolean = isConstructedFromGivenClass(type, FqNames._char)
        fun isInt(type: KotlinType): Boolean = isConstructedFromGivenClassAndNotNullable(type, FqNames._int)
        fun isByte(type: KotlinType): Boolean = isConstructedFromGivenClassAndNotNullable(type, FqNames._byte)
        fun isLong(type: KotlinType): Boolean = isConstructedFromGivenClassAndNotNullable(type, FqNames._long)
        fun isLongOrNullableLong(type: KotlinType): Boolean = isConstructedFromGivenClass(type, FqNames._long)
        fun isShort(type: KotlinType): Boolean = isConstructedFromGivenClassAndNotNullable(type, FqNames._short)
        fun isFloat(type: KotlinType): Boolean = isFloatOrNullableFloat(type) && !type.isMarkedNullable
        fun isFloatOrNullableFloat(type: KotlinType): Boolean = isConstructedFromGivenClass(type, FqNames._float)
        fun isDouble(type: KotlinType): Boolean = isDoubleOrNullableDouble(type) && !type.isMarkedNullable
        fun isUByte(type: KotlinType): Boolean = isConstructedFromGivenClassAndNotNullable(type, FqNames.uByteFqName.toUnsafe())
        fun isUShort(type: KotlinType): Boolean = isConstructedFromGivenClassAndNotNullable(type, FqNames.uShortFqName.toUnsafe())
        fun isUInt(type: KotlinType): Boolean = isConstructedFromGivenClassAndNotNullable(type, FqNames.uIntFqName.toUnsafe())
        fun isULong(type: KotlinType): Boolean = isConstructedFromGivenClassAndNotNullable(type, FqNames.uLongFqName.toUnsafe())
        fun isUByteArray(type: KotlinType): Boolean = isConstructedFromGivenClassAndNotNullable(type, FqNames.uByteArrayFqName.toUnsafe())
        fun isUShortArray(type: KotlinType): Boolean = isConstructedFromGivenClassAndNotNullable(type, FqNames.uShortArrayFqName.toUnsafe())
        fun isUIntArray(type: KotlinType): Boolean = isConstructedFromGivenClassAndNotNullable(type, FqNames.uIntArrayFqName.toUnsafe())
        fun isULongArray(type: KotlinType): Boolean = isConstructedFromGivenClassAndNotNullable(type, FqNames.uLongArrayFqName.toUnsafe())
        fun isUnsignedArrayType(type: KotlinType): Boolean = isUByteArray(type) || isUShortArray(type) || isUIntArray(type) || isULongArray(type)
        fun isDoubleOrNullableDouble(type: KotlinType): Boolean = isConstructedFromGivenClass(type, FqNames._double)
        private fun isConstructedFromGivenClassAndNotNullable(type: KotlinType, fqName: FqNameUnsafe): Boolean =
            isConstructedFromGivenClass(type, fqName) && !type.isMarkedNullable
        fun isNothing(type: KotlinType): Boolean = isNothingOrNullableNothing(type) && !TypeUtils.isNullableType(type)
        fun isNullableNothing(type: KotlinType): Boolean = isNothingOrNullableNothing(type) && TypeUtils.isNullableType(type)
        fun isNothingOrNullableNothing(type: KotlinType): Boolean = isConstructedFromGivenClass(type, FqNames.nothing)
        fun isAnyOrNullableAny(type: KotlinType): Boolean = isConstructedFromGivenClass(type, FqNames.any)
        fun isNullableAny(type: KotlinType): Boolean = isAnyOrNullableAny(type) && type.isMarkedNullable
        fun isDefaultBound(type: KotlinType): Boolean = isNullableAny(type)
        fun isUnit(type: KotlinType): Boolean = isNotNullConstructedFromGivenClass(type, FqNames.unit)
        fun mayReturnNonUnitValue(descriptor: FunctionDescriptor): Boolean {
            val returnType = descriptor.getReturnType()
            assert(returnType != null) { "Function return typed type must be resolved." }
            var mayReturn = !isUnit(returnType!!)
            for (overridden in descriptor.getOriginal().getOverriddenDescriptors()) {
                if (mayReturn) break
                val overriddenType = overridden.getReturnType()
                assert(overriddenType != null) { "Function return typed type must be resolved." }
                mayReturn = !isUnit(overriddenType!!)
            }
            return mayReturn
        }
        fun isUnitOrNullableUnit(type: KotlinType): Boolean = isConstructedFromGivenClass(type, FqNames.unit)
        fun isEnum(descriptor: ClassDescriptor): Boolean = classFqNameEquals(descriptor, FqNames._enum)
        fun isEnum(type: KotlinType): Boolean = isConstructedFromGivenClassAndNotNullable(type, FqNames._enum)
        fun isComparable(descriptor: ClassDescriptor): Boolean = classFqNameEquals(descriptor, FqNames.comparable.toUnsafe())
        fun isComparable(type: KotlinType): Boolean = isConstructedFromGivenClassAndNotNullable(type, FqNames.comparable.toUnsafe())
        fun isCharSequence(type: KotlinType?): Boolean = type != null && isNotNullConstructedFromGivenClass(type, FqNames.charSequence)
        fun isString(type: KotlinType?): Boolean = type != null && isNotNullConstructedFromGivenClass(type, FqNames.string)
        fun isUnsignedNumber(type: KotlinType?): Boolean = type != null && (isUByte(type) || isUShort(type) || isUInt(type) || isULong(type))
        fun isCharSequenceOrNullableCharSequence(type: KotlinType?): Boolean = type != null && isConstructedFromGivenClass(type, FqNames.charSequence)
        fun isStringOrNullableString(type: KotlinType?): Boolean = type != null && isConstructedFromGivenClass(type, FqNames.string)
        fun isCollectionOrNullableCollection(type: KotlinType): Boolean = isConstructedFromGivenClass(type, FqNames.collection)
        fun isListOrNullableList(type: KotlinType): Boolean = isConstructedFromGivenClass(type, FqNames.list)
        fun isSetOrNullableSet(type: KotlinType): Boolean = isConstructedFromGivenClass(type, FqNames.set)
        fun isMapOrNullableMap(type: KotlinType): Boolean = isConstructedFromGivenClass(type, FqNames.map)
        fun isIterableOrNullableIterable(type: KotlinType): Boolean = isConstructedFromGivenClass(type, FqNames.iterable)
        fun isThrowableOrNullableThrowable(type: KotlinType): Boolean = isConstructedFromGivenClass(type, FqNames.throwable)
        fun isThrowable(descriptor: ClassDescriptor): Boolean = classFqNameEquals(descriptor, FqNames.throwable.toUnsafe())
        fun isKClass(descriptor: ClassDescriptor): Boolean = classFqNameEquals(descriptor, FqNames.kClass)
        fun isNonPrimitiveArray(descriptor: ClassDescriptor): Boolean = classFqNameEquals(descriptor, FqNames.array)
        // Declaration-site annotation only; @DeprecatedSinceKotlin needs DeprecationResolver.
        fun isDeprecated(declarationDescriptor: DeclarationDescriptor): Boolean {
            if (declarationDescriptor.getOriginal().annotations.hasAnnotation(FqNames.deprecated)) return true
            if (declarationDescriptor is PropertyDescriptor) {
                val getter = declarationDescriptor.getGetter()
                val setter = declarationDescriptor.getSetter()
                return getter != null && isDeprecated(getter) && (!declarationDescriptor.isVar() || setter != null && isDeprecated(setter))
            }
            return false
        }
        fun isNotNullOrNullableFunctionSupertype(type: KotlinType): Boolean = isConstructedFromGivenClass(type, FqNames.functionSupertype)
    }
}
