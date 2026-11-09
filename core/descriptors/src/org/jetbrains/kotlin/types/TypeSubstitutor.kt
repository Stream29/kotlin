/*
 * Copyright 2010-2016 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.types

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.builtins.StandardNames
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.annotations.CompositeAnnotations
import org.jetbrains.kotlin.descriptors.annotations.FilteredAnnotations
import org.jetbrains.kotlin.resolve.calls.inference.isCaptured
import org.jetbrains.kotlin.types.checker.NewCapturedTypeConstructor
import org.jetbrains.kotlin.types.error.ErrorTypeKind
import org.jetbrains.kotlin.types.error.ErrorUtils
import org.jetbrains.kotlin.types.model.TypeSubstitutorMarker
import org.jetbrains.kotlin.types.typeUtil.replaceAnnotations
import org.jetbrains.kotlin.types.typesApproximation.approximateCapturedTypesIfNecessary
import org.jetbrains.kotlin.utils.isProcessCanceledException

// Translated-unverified. Keeps the original recursive substitution and approximation branches.
open class TypeSubstitutor protected constructor(private val typeSubstitution: TypeSubstitution) : TypeSubstitutorMarker {
    private class SubstitutionException(message: String) : Exception(message)

    open fun replaceWithNonApproximatingSubstitution(): TypeSubstitutor {
        val substitution = typeSubstitution
        if (substitution !is IndexedParametersSubstitution || !substitution.approximateContravariantCapturedTypes()) return this
        return TypeSubstitutor(IndexedParametersSubstitution(substitution.parameters, substitution.arguments, false))
    }

    open fun replaceWithContravariantApproximatingSubstitution(): TypeSubstitutor {
        val substitution = typeSubstitution
        if (substitution is SubstitutionWithCapturedTypeApproximation)
            return TypeSubstitutor(SubstitutionWithContravariantCapturedTypeApproximation(substitution.substitution))
        if (substitution is IndexedParametersSubstitution && !substitution.approximateContravariantCapturedTypes())
            return TypeSubstitutor(IndexedParametersSubstitution(substitution.parameters, substitution.arguments, true))
        return this
    }

    open fun isEmpty(): Boolean = typeSubstitution.isEmpty()
    open fun getSubstitution(): TypeSubstitution = typeSubstitution

    open fun safeSubstitute(type: KotlinType, howThisTypeIsUsed: Variance): KotlinType {
        if (isEmpty()) return type
        return try {
            unsafeSubstitute(TypeProjectionImpl(howThisTypeIsUsed, type), null, 0).getType()
        } catch (e: SubstitutionException) {
            ErrorUtils.createErrorType(ErrorTypeKind.UNABLE_TO_SUBSTITUTE_TYPE, e.message!!)
        }
    }

    open fun substitute(type: KotlinType, howThisTypeIsUsed: Variance): KotlinType? {
        val projection = substitute(TypeProjectionImpl(howThisTypeIsUsed, getSubstitution().prepareTopLevelType(type, howThisTypeIsUsed)))
        return projection?.getType()
    }

    open fun substitute(typeProjection: TypeProjection): TypeProjection? {
        val substitutedTypeProjection = substituteWithoutApproximation(typeProjection)
        if (!typeSubstitution.approximateCapturedTypes() && !typeSubstitution.approximateContravariantCapturedTypes())
            return substitutedTypeProjection
        return approximateCapturedTypesIfNecessary(substitutedTypeProjection, typeSubstitution.approximateContravariantCapturedTypes())
    }

    open fun substituteWithoutApproximation(typeProjection: TypeProjection): TypeProjection? {
        if (isEmpty()) return typeProjection
        return try {
            unsafeSubstitute(typeProjection, null, 0)
        } catch (e: SubstitutionException) {
            null
        }
    }

    private fun unsafeSubstitute(
        originalProjection: TypeProjection, typeParameter: TypeParameterDescriptor?, recursionDepth: Int
    ): TypeProjection {
        assertRecursionDepth(recursionDepth, originalProjection, typeSubstitution)
        if (originalProjection.isStarProjection()) return originalProjection
        val type = originalProjection.getType()
        if (type is TypeWithEnhancement) {
            val substitution = unsafeSubstitute(
                TypeProjectionImpl(originalProjection.getProjectionKind(), type.origin), typeParameter, recursionDepth + 1
            )
            if (substitution.isStarProjection()) return substitution
            val substitutedEnhancement = substitute(type.enhancement, originalProjection.getProjectionKind())
            val resultingType = substitution.getType().unwrap().wrapEnhancement(substitutedEnhancement)
            return TypeProjectionImpl(substitution.getProjectionKind(), resultingType)
        }
        if (type.isDynamic() || type.unwrap() is RawType) return originalProjection
        val substituted = typeSubstitution[type]
        val replacement = if (substituted != null)
            projectedTypeForConflictedTypeWithUnsafeVariance(type, substituted, typeParameter, originalProjection) else null
        val originalProjectionKind = originalProjection.getProjectionKind()
        if (replacement == null && type.isFlexible() && !type.isCustomTypeParameter()) {
            val flexibleType = type.asFlexibleType()
            val substitutedLower = unsafeSubstitute(
                TypeProjectionImpl(originalProjectionKind, flexibleType.lowerBound), typeParameter, recursionDepth + 1
            )
            val substitutedUpper = unsafeSubstitute(
                TypeProjectionImpl(originalProjectionKind, flexibleType.upperBound), typeParameter, recursionDepth + 1
            )
            val substitutedProjectionKind = substitutedLower.getProjectionKind()
            assert(
                substitutedProjectionKind == substitutedUpper.getProjectionKind() && originalProjectionKind == Variance.INVARIANT ||
                    originalProjectionKind == substitutedProjectionKind
            ) { "Unexpected substituted projection kind: $substitutedProjectionKind; original: $originalProjectionKind" }
            if (substitutedLower.getType() === flexibleType.lowerBound && substitutedUpper.getType() === flexibleType.upperBound)
                return originalProjection
            val substitutedFlexibleType =
                KotlinTypeFactory.flexibleType(substitutedLower.getType().asSimpleType(), substitutedUpper.getType().asSimpleType())
            return TypeProjectionImpl(substitutedProjectionKind, substitutedFlexibleType)
        }
        if (KotlinBuiltIns.isNothing(type) || type.isError) return originalProjection
        if (replacement != null) {
            val varianceConflict = conflictType(originalProjectionKind, replacement.getProjectionKind())
            // Captured types permit substitution in the opposite projection.
            if (!type.isCaptured()) {
                when (varianceConflict) {
                    VarianceConflictType.OUT_IN_IN_POSITION -> throw SubstitutionException("Out-projection in in-position")
                    VarianceConflictType.IN_IN_OUT_POSITION ->
                        return TypeProjectionImpl(Variance.OUT_VARIANCE, type.constructor.getBuiltIns().getNullableAnyType())
                    VarianceConflictType.NO_CONFLICT -> Unit
                }
            }
            val customTypeParameter = type.getCustomTypeParameter()
            var substitutedType: KotlinType
            if (replacement.isStarProjection()) return replacement
            else if (customTypeParameter != null) substitutedType = customTypeParameter.substitutionResult(replacement.getType())
            else substitutedType = TypeUtils.makeNullableIfNeeded(replacement.getType(), type.isMarkedNullable)
            if (!type.annotations.isEmpty()) {
                val typeAnnotations = filterOutUnsafeVariance(typeSubstitution.filterAnnotations(type.annotations))
                substitutedType = substitutedType.replaceAnnotations(CompositeAnnotations(substitutedType.annotations, typeAnnotations))
            }
            val resultingProjectionKind = if (varianceConflict == VarianceConflictType.NO_CONFLICT)
                combine(originalProjectionKind, replacement.getProjectionKind()) else originalProjectionKind
            return TypeProjectionImpl(resultingProjectionKind, substitutedType)
        }
        return substituteCompoundType(originalProjection, recursionDepth)
    }

    private fun substituteCompoundType(originalProjection: TypeProjection, recursionDepth: Int): TypeProjection {
        val type = originalProjection.getType()
        val projectionKind = originalProjection.getProjectionKind()
        if (type.constructor.getDeclarationDescriptor() is TypeParameterDescriptor) return originalProjection
        var substitutedAbbreviation: KotlinType? = null
        val abbreviation = type.getAbbreviation()
        if (abbreviation != null) {
            substitutedAbbreviation = replaceWithNonApproximatingSubstitution().substitute(abbreviation, Variance.INVARIANT)
        }
        val substitutedArguments = substituteTypeArguments(type.constructor.getParameters(), type.arguments, recursionDepth)
        var substitutedType = type.replace(substitutedArguments, typeSubstitution.filterAnnotations(type.annotations))
        if (substitutedType is SimpleType && substitutedAbbreviation is SimpleType)
            substitutedType = substitutedType.withAbbreviation(substitutedAbbreviation)
        return TypeProjectionImpl(projectionKind, substitutedType)
    }

    private fun substituteTypeArguments(
        typeParameters: List<TypeParameterDescriptor>, typeArguments: List<TypeProjection>, recursionDepth: Int
    ): List<TypeProjection> {
        val substitutedArguments = ArrayList<TypeProjection>(typeParameters.size)
        var wereChanges = false
        for (i in typeParameters.indices) {
            val typeParameter = typeParameters[i]
            val typeArgument = typeArguments[i]
            var substitutedTypeArgument = unsafeSubstitute(typeArgument, typeParameter, recursionDepth + 1)
            when (conflictType(typeParameter.getVariance(), substitutedTypeArgument.getProjectionKind())) {
                VarianceConflictType.NO_CONFLICT ->
                    if (typeParameter.getVariance() != Variance.INVARIANT && !substitutedTypeArgument.isStarProjection())
                        substitutedTypeArgument = TypeProjectionImpl(Variance.INVARIANT, substitutedTypeArgument.getType())
                VarianceConflictType.OUT_IN_IN_POSITION, VarianceConflictType.IN_IN_OUT_POSITION ->
                    substitutedTypeArgument = TypeUtils.makeStarProjection(typeParameter)
            }
            if (substitutedTypeArgument !== typeArgument) wereChanges = true
            substitutedArguments.add(substitutedTypeArgument)
        }
        if (!wereChanges) return typeArguments
        return substitutedArguments
    }

    private enum class VarianceConflictType { NO_CONFLICT, IN_IN_OUT_POSITION, OUT_IN_IN_POSITION }

    companion object {
        private const val MAX_RECURSION_DEPTH = 100
        val EMPTY: TypeSubstitutor = create(TypeSubstitution.EMPTY)
        fun create(substitution: TypeSubstitution): TypeSubstitutor = TypeSubstitutor(substitution)
        fun createChainedSubstitutor(first: TypeSubstitution, second: TypeSubstitution): TypeSubstitutor =
            create(DisjointKeysUnionTypeSubstitution.create(first, second))
        fun create(substitutionContext: Map<TypeConstructor, TypeProjection>): TypeSubstitutor =
            create(TypeConstructorSubstitution.createByConstructorsMap(substitutionContext))
        fun create(context: KotlinType): TypeSubstitutor = create(TypeConstructorSubstitution.create(context.constructor, context.arguments))

        private fun projectedTypeForConflictedTypeWithUnsafeVariance(
            originalType: KotlinType, substituted: TypeProjection, typeParameter: TypeParameterDescriptor?,
            originalProjection: TypeProjection
        ): TypeProjection {
            if (!originalType.annotations.hasAnnotation(StandardNames.FqNames.unsafeVariance)) return substituted
            val constructor = substituted.getType().constructor
            if (constructor !is NewCapturedTypeConstructor) return substituted
            val capturedTypeProjection = constructor.projection
            val varianceOfCapturedType = capturedTypeProjection.getProjectionKind()
            if (conflictType(originalProjection.getProjectionKind(), varianceOfCapturedType) == VarianceConflictType.OUT_IN_IN_POSITION)
                return TypeProjectionImpl(capturedTypeProjection.getType())
            if (typeParameter == null) return substituted
            if (conflictType(typeParameter.getVariance(), varianceOfCapturedType) == VarianceConflictType.OUT_IN_IN_POSITION)
                return TypeProjectionImpl(capturedTypeProjection.getType())
            return substituted
        }

        private fun filterOutUnsafeVariance(annotations: Annotations): Annotations {
            if (!annotations.hasAnnotation(StandardNames.FqNames.unsafeVariance)) return annotations
            return FilteredAnnotations(annotations) { name -> name != StandardNames.FqNames.unsafeVariance }
        }

        fun combine(typeParameterVariance: Variance, typeProjection: TypeProjection): Variance {
            if (typeProjection.isStarProjection()) return Variance.OUT_VARIANCE
            return combine(typeParameterVariance, typeProjection.getProjectionKind())
        }

        fun combine(typeParameterVariance: Variance, projectionKind: Variance): Variance {
            if (typeParameterVariance == Variance.INVARIANT) return projectionKind
            if (projectionKind == Variance.INVARIANT) return typeParameterVariance
            if (typeParameterVariance == projectionKind) return projectionKind
            throw AssertionError("Variance conflict: type parameter variance '$typeParameterVariance' and projection kind '$projectionKind' cannot be combined")
        }

        private fun conflictType(position: Variance, argument: Variance): VarianceConflictType {
            if (position == Variance.IN_VARIANCE && argument == Variance.OUT_VARIANCE) return VarianceConflictType.OUT_IN_IN_POSITION
            if (position == Variance.OUT_VARIANCE && argument == Variance.IN_VARIANCE) return VarianceConflictType.IN_IN_OUT_POSITION
            return VarianceConflictType.NO_CONFLICT
        }

        private fun assertRecursionDepth(recursionDepth: Int, projection: TypeProjection, substitution: TypeSubstitution) {
            if (recursionDepth > MAX_RECURSION_DEPTH)
                throw IllegalStateException("Recursion too deep. Most likely infinite loop while substituting ${safeToString(projection)}; substitution: ${safeToString(substitution)}")
        }

        private fun safeToString(value: Any): String = try {
            value.toString()
        } catch (e: Throwable) {
            if (e.isProcessCanceledException()) throw e
            "[Exception while computing toString(): $e]"
        }
    }
}

val TypeSubstitutor.isEmpty: Boolean get() = isEmpty()
val TypeSubstitutor.substitution: TypeSubstitution get() = getSubstitution()
