/*
 * Copyright 2000-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin.types

import org.jetbrains.kotlin.builtins.StandardNames
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.constants.IntegerLiteralTypeConstructor
import org.jetbrains.kotlin.resolve.constants.IntegerValueTypeConstructor
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.types.checker.KotlinTypeChecker
import org.jetbrains.kotlin.types.checker.KotlinTypeRefiner
import org.jetbrains.kotlin.types.checker.NewTypeVariableConstructor
import org.jetbrains.kotlin.types.error.ErrorTypeKind
import org.jetbrains.kotlin.types.error.ErrorUtils
import org.jetbrains.kotlin.utils.SmartSet

// Translated-unverified from TypeUtils.java. TypeUtils.kt belongs to types.typeUtil, not this class.
open class TypeUtils {
    open class SpecialType(private val name: String) : DelegatingSimpleType() {
        override val delegate: SimpleType get() = throw IllegalStateException(name)
        override fun replaceAttributes(newAttributes: TypeAttributes): SimpleType = throw IllegalStateException(name)
        override fun makeNullableAsSpecified(newNullability: Boolean): SimpleType = throw IllegalStateException(name)
        override fun toString(): String = name
        @TypeRefinement
        override fun replaceDelegate(delegate: SimpleType): DelegatingSimpleType = throw IllegalStateException(name)
        @TypeRefinement
        override fun refine(kotlinTypeRefiner: KotlinTypeRefiner): SpecialType = this
    }
    companion object {
        val DONT_CARE: SimpleType = ErrorUtils.createErrorType(ErrorTypeKind.DONT_CARE)
        val CANNOT_INFER_FUNCTION_PARAM_TYPE: SimpleType = ErrorUtils.createErrorType(ErrorTypeKind.UNINFERRED_LAMBDA_PARAMETER_TYPE)
        val NO_EXPECTED_TYPE: SimpleType = SpecialType("NO_EXPECTED_TYPE")
        val UNIT_EXPECTED_TYPE: SimpleType = SpecialType("UNIT_EXPECTED_TYPE")
        fun noExpectedType(type: KotlinType): Boolean = type === NO_EXPECTED_TYPE || type === UNIT_EXPECTED_TYPE
        fun isDontCarePlaceholder(type: KotlinType?): Boolean = type != null && type.constructor === DONT_CARE.constructor
        fun makeNullable(type: KotlinType): KotlinType = makeNullableAsSpecified(type, true)
        fun makeNotNullable(type: KotlinType): KotlinType = makeNullableAsSpecified(type, false)
        fun makeNullableAsSpecified(type: KotlinType, nullable: Boolean): KotlinType = type.unwrap().makeNullableAsSpecified(nullable)
        fun makeNullableIfNeeded(type: SimpleType, nullable: Boolean): SimpleType = if (nullable) type.makeNullableAsSpecified(true) else type
        fun makeNullableIfNeeded(type: KotlinType, nullable: Boolean): KotlinType = if (nullable) makeNullable(type) else type
        fun canHaveSubtypes(typeChecker: KotlinTypeChecker, type: KotlinType): Boolean {
            if (type.isMarkedNullable || !type.constructor.isFinal()) return true
            val parameters = type.constructor.getParameters()
            val arguments = type.arguments
            for (i in parameters.indices) {
                val parameter = parameters[i]
                val projection = arguments[i]
                if (projection.isStarProjection()) return true
                val kind = projection.getProjectionKind()
                val argument = projection.getType()
                when (parameter.getVariance()) {
                    Variance.INVARIANT -> when (kind) {
                        Variance.INVARIANT -> if (lowerThanBound(typeChecker, argument, parameter) || canHaveSubtypes(typeChecker, argument)) return true
                        Variance.IN_VARIANCE -> if (lowerThanBound(typeChecker, argument, parameter)) return true
                        Variance.OUT_VARIANCE -> if (canHaveSubtypes(typeChecker, argument)) return true
                    }
                    Variance.IN_VARIANCE -> if (kind != Variance.OUT_VARIANCE) {
                        if (lowerThanBound(typeChecker, argument, parameter)) return true
                    } else if (canHaveSubtypes(typeChecker, argument)) return true
                    Variance.OUT_VARIANCE -> if (kind != Variance.IN_VARIANCE) {
                        if (canHaveSubtypes(typeChecker, argument)) return true
                    } else if (lowerThanBound(typeChecker, argument, parameter)) return true
                }
            }
            return false
        }
        private fun lowerThanBound(typeChecker: KotlinTypeChecker, argument: KotlinType, parameterDescriptor: TypeParameterDescriptor): Boolean {
            for (bound in parameterDescriptor.getUpperBounds())
                if (typeChecker.isSubtypeOf(argument, bound) && argument.constructor != bound.constructor) return true
            return false
        }
        fun makeUnsubstitutedType(classifierDescriptor: ClassifierDescriptor, unsubstitutedMemberScope: MemberScope,
                                 refinedTypeFactory: RefinedTypeFactory): SimpleType {
            if (ErrorUtils.isError(classifierDescriptor))
                return ErrorUtils.createErrorType(ErrorTypeKind.UNABLE_TO_SUBSTITUTE_TYPE, classifierDescriptor.toString())
            return makeUnsubstitutedType(classifierDescriptor.getTypeConstructor(), unsubstitutedMemberScope, refinedTypeFactory)
        }
        fun makeUnsubstitutedType(typeConstructor: TypeConstructor, unsubstitutedMemberScope: MemberScope,
                                 refinedTypeFactory: RefinedTypeFactory): SimpleType =
            KotlinTypeFactory.simpleTypeWithNonTrivialMemberScope(TypeAttributes.Empty, typeConstructor,
                getDefaultTypeProjections(typeConstructor.getParameters()), false, unsubstitutedMemberScope, refinedTypeFactory)
        fun getDefaultTypeProjections(parameters: List<TypeParameterDescriptor>): List<TypeProjection> {
            val result = ArrayList<TypeProjection>(parameters.size)
            for (parameter in parameters) result.add(TypeProjectionImpl(parameter.getDefaultType()))
            return result.toList()
        }
        fun getImmediateSupertypes(type: KotlinType): List<KotlinType> {
            val substitutor = TypeSubstitutor.create(type)
            val originalSupertypes = type.constructor.getSupertypes()
            val result = ArrayList<KotlinType>(originalSupertypes.size)
            for (supertype in originalSupertypes) {
                val substituted = createSubstitutedSupertype(type, supertype, substitutor)
                if (substituted != null) result.add(substituted)
            }
            return result
        }
        fun createSubstitutedSupertype(subType: KotlinType, superType: KotlinType, substitutor: TypeSubstitutor): KotlinType? =
            substitutor.substitute(superType, Variance.INVARIANT)?.let { makeNullableIfNeeded(it, subType.isMarkedNullable) }
        private fun collectAllSupertypes(type: KotlinType, result: MutableSet<KotlinType>) {
            val immediate = getImmediateSupertypes(type)
            result.addAll(immediate)
            for (supertype in immediate) collectAllSupertypes(supertype, result)
        }
        fun getAllSupertypes(type: KotlinType): Set<KotlinType> {
            val result = LinkedHashSet<KotlinType>(15)
            collectAllSupertypes(type, result)
            return result
        }
        /** Whether a value of this type can be null; differs from acceptsNullable for type parameters. */
        fun isNullableType(type: KotlinType): Boolean {
            if (type.isMarkedNullable) return true
            if (type.isFlexible() && isNullableType(type.asFlexibleType().upperBound)) return true
            if (type.isDefinitelyNotNullType) return false
            if (isTypeParameter(type)) return hasNullableSuperType(type)
            if (type is AbstractStubType) {
                val constructor = type.originalTypeVariable as NewTypeVariableConstructor
                val parameter = constructor.originalTypeParameter
                return parameter == null || hasNullableSuperType(parameter.getDefaultType())
            }
            val constructor = type.constructor
            if (constructor is IntersectionTypeConstructor)
                for (supertype in constructor.getSupertypes()) if (isNullableType(supertype)) return true
            return false
        }
        fun acceptsNullable(type: KotlinType): Boolean {
            if (type.isMarkedNullable) return true
            if (type.isFlexible() && acceptsNullable(type.asFlexibleType().upperBound)) return true
            return false
        }
        fun hasNullableSuperType(type: KotlinType): Boolean {
            if (type.constructor.getDeclarationDescriptor() is ClassDescriptor) return false
            for (supertype in getImmediateSupertypes(type)) if (isNullableType(supertype)) return true
            return false
        }
        fun getClassDescriptor(type: KotlinType): ClassDescriptor? = type.constructor.getDeclarationDescriptor() as? ClassDescriptor
        fun substituteParameters(clazz: ClassDescriptor, typeArguments: List<KotlinType>): KotlinType =
            substituteProjectionsForParameters(clazz, typeArguments.map { TypeProjectionImpl(it) })
        fun substituteProjectionsForParameters(clazz: ClassDescriptor, projections: List<TypeProjection>): KotlinType {
            val parameters = clazz.getTypeConstructor().getParameters()
            if (parameters.size != projections.size) throw IllegalArgumentException("type parameter counts do not match: $clazz, $projections")
            val substitutions = HashMap<TypeConstructor, TypeProjection>(parameters.size)
            for (i in parameters.indices) substitutions[parameters[i].getTypeConstructor()] = projections[i]
            return TypeSubstitutor.create(substitutions).substitute(clazz.getDefaultType(), Variance.INVARIANT)!!
        }
        fun equalTypes(a: KotlinType, b: KotlinType): Boolean = KotlinTypeChecker.DEFAULT.equalTypes(a, b)
        fun dependsOnTypeParameters(type: KotlinType, typeParameters: Collection<TypeParameterDescriptor>): Boolean =
            dependsOnTypeConstructors(type, typeParameters.map { it.getTypeConstructor() })
        fun dependsOnTypeConstructors(type: KotlinType, typeParameterConstructors: Collection<TypeConstructor>): Boolean {
            if (typeParameterConstructors.contains(type.constructor)) return true
            for (projection in type.arguments)
                if (!projection.isStarProjection() && dependsOnTypeConstructors(projection.getType(), typeParameterConstructors)) return true
            return false
        }
        fun contains(type: KotlinType?, specialType: KotlinType): Boolean = contains(type, { specialType == it })
        fun contains(type: KotlinType?, isSpecialType: (UnwrappedType) -> Boolean): Boolean = contains(type, isSpecialType, null)
        private fun contains(type: KotlinType?, isSpecialType: (UnwrappedType) -> Boolean, visited: SmartSet<KotlinType>?): Boolean {
            if (type == null) return false
            val unwrapped = type.unwrap()
            if (noExpectedType(type)) return isSpecialType(unwrapped)
            if (visited != null && visited.contains(type)) return false
            if (isSpecialType(unwrapped)) return true
            val seen = visited ?: SmartSet.create()
            seen.add(type)
            if (unwrapped is FlexibleType &&
                (contains(unwrapped.lowerBound, isSpecialType, seen) || contains(unwrapped.upperBound, isSpecialType, seen))) return true
            if (unwrapped is DefinitelyNotNullType && contains(unwrapped.original, isSpecialType, seen)) return true
            val constructor = type.constructor
            if (constructor is IntersectionTypeConstructor) {
                for (supertype in constructor.getSupertypes()) if (contains(supertype, isSpecialType, seen)) return true
                return false
            }
            for (projection in type.arguments) {
                if (projection.isStarProjection()) continue
                if (contains(projection.getType(), isSpecialType, seen)) return true
            }
            return false
        }
        fun makeStarProjection(parameterDescriptor: TypeParameterDescriptor): TypeProjection = StarProjectionImpl(parameterDescriptor)
        fun makeStarProjection(parameterDescriptor: TypeParameterDescriptor, attr: ErasureTypeAttributes): TypeProjection =
            if (attr.howThisTypeIsUsed == TypeUsage.SUPERTYPE) TypeProjectionImpl(parameterDescriptor.starProjectionType())
            else StarProjectionImpl(parameterDescriptor)
        fun getDefaultPrimitiveNumberType(numberValueTypeConstructor: IntegerValueTypeConstructor): KotlinType {
            val type = getDefaultPrimitiveNumberType(numberValueTypeConstructor.getSupertypes())
            assert(type != null) {
                "Strange number value type constructor: $numberValueTypeConstructor. Super types doesn't contain double, int or long: ${numberValueTypeConstructor.getSupertypes()}"
            }
            return type!!
        }
        fun getDefaultPrimitiveNumberType(supertypes: Collection<KotlinType>): KotlinType? {
            if (supertypes.isEmpty()) return null
            val builtIns = supertypes.iterator().next().constructor.getBuiltIns()
            val doubleType = builtIns.getDoubleType()
            if (supertypes.contains(doubleType)) return doubleType
            val intType = builtIns.getIntType()
            if (supertypes.contains(intType)) return intType
            val longType = builtIns.getLongType()
            if (supertypes.contains(longType)) return longType
            findByFqName(supertypes, StandardNames.FqNames.uIntFqName)?.let { return it }
            findByFqName(supertypes, StandardNames.FqNames.uLongFqName)?.let { return it }
            return null
        }
        private fun findByFqName(supertypes: Collection<KotlinType>, fqName: FqName): KotlinType? {
            for (supertype in supertypes) {
                val descriptor = supertype.constructor.getDeclarationDescriptor() ?: continue
                if (DescriptorUtils.getFqName(descriptor) == fqName.toUnsafe()) return supertype
            }
            return null
        }
        fun getPrimitiveNumberType(numberValueTypeConstructor: IntegerValueTypeConstructor, expectedType: KotlinType): KotlinType {
            if (noExpectedType(expectedType) || expectedType.isError) return getDefaultPrimitiveNumberType(numberValueTypeConstructor)
            for (primitive in numberValueTypeConstructor.getSupertypes())
                if (KotlinTypeChecker.DEFAULT.isSubtypeOf(primitive, expectedType)) return primitive
            return getDefaultPrimitiveNumberType(numberValueTypeConstructor)
        }
        fun getPrimitiveNumberType(literalTypeConstructor: IntegerLiteralTypeConstructor, expectedType: KotlinType): KotlinType {
            if (noExpectedType(expectedType) || expectedType.isError) return literalTypeConstructor.getApproximatedType()
            val approximated = literalTypeConstructor.getApproximatedType()
            if (KotlinTypeChecker.DEFAULT.isSubtypeOf(approximated, expectedType)) return approximated
            for (primitive in literalTypeConstructor.possibleTypes)
                if (KotlinTypeChecker.DEFAULT.isSubtypeOf(primitive, expectedType)) return primitive
            return literalTypeConstructor.getApproximatedType()
        }
        fun isTypeParameter(type: KotlinType): Boolean = getTypeParameterDescriptorOrNull(type) != null || type.constructor is NewTypeVariableConstructor
        fun isReifiedTypeParameter(type: KotlinType): Boolean = getTypeParameterDescriptorOrNull(type)?.isReified() == true
        fun isNonReifiedTypeParameter(type: KotlinType): Boolean = getTypeParameterDescriptorOrNull(type)?.isReified() == false
        fun getTypeParameterDescriptorOrNull(type: KotlinType): TypeParameterDescriptor? = type.constructor.getDeclarationDescriptor() as? TypeParameterDescriptor
    }
}
