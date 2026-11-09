/*
 * Copyright 2010-2017 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.resolve

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.impl.FunctionDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.PropertyAccessorDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.PropertyDescriptorImpl
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.OverridingUtil.OverrideCompatibilityInfo.Result.*
import org.jetbrains.kotlin.resolve.descriptorUtil.isTypeRefinementEnabled
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.types.*
import org.jetbrains.kotlin.types.checker.KotlinTypeChecker
import org.jetbrains.kotlin.types.checker.KotlinTypePreparator
import org.jetbrains.kotlin.types.checker.KotlinTypeRefiner
import org.jetbrains.kotlin.types.model.KotlinTypeMarker
import org.jetbrains.kotlin.utils.SmartSet
import org.jetbrains.kotlin.util.NativeDescriptorServices

// JVM discovery is replaced by required Native host installation; override rules are unchanged.
class OverridingUtil private constructor(
    private val equalityAxioms: KotlinTypeChecker.TypeConstructorEquality,
    private val kotlinTypeRefiner: KotlinTypeRefiner, private val kotlinTypePreparator: KotlinTypePreparator,
    private val customSubtype: ((KotlinType, KotlinType) -> Boolean?)?
) {
    fun isOverridableBy(superDescriptor: CallableDescriptor, subDescriptor: CallableDescriptor, subClassDescriptor: ClassDescriptor?): OverrideCompatibilityInfo =
        isOverridableBy(superDescriptor, subDescriptor, subClassDescriptor, false)
    fun isOverridableBy(superDescriptor: CallableDescriptor, subDescriptor: CallableDescriptor, subClassDescriptor: ClassDescriptor?,
                        checkReturnType: Boolean): OverrideCompatibilityInfo {
        val basicResult = isOverridableByWithoutExternalConditions(superDescriptor, subDescriptor, checkReturnType)
        var wasSuccess = basicResult.getResult() == OVERRIDABLE
        for (condition in EXTERNAL_CONDITIONS) {
            if (condition.getContract() == ExternalOverridabilityCondition.Contract.CONFLICTS_ONLY) continue
            if (wasSuccess && condition.getContract() == ExternalOverridabilityCondition.Contract.SUCCESS_ONLY) continue
            when (condition.isOverridable(superDescriptor, subDescriptor, subClassDescriptor)) {
                ExternalOverridabilityCondition.Result.OVERRIDABLE -> wasSuccess = true
                ExternalOverridabilityCondition.Result.INCOMPATIBLE -> return OverrideCompatibilityInfo.incompatible("External condition")
                ExternalOverridabilityCondition.Result.UNKNOWN -> Unit
            }
        }
        if (!wasSuccess) return basicResult
        for (condition in EXTERNAL_CONDITIONS) {
            if (condition.getContract() != ExternalOverridabilityCondition.Contract.CONFLICTS_ONLY) continue
            when (condition.isOverridable(superDescriptor, subDescriptor, subClassDescriptor)) {
                ExternalOverridabilityCondition.Result.INCOMPATIBLE -> return OverrideCompatibilityInfo.incompatible("External condition")
                ExternalOverridabilityCondition.Result.OVERRIDABLE ->
                    throw IllegalStateException("Contract violation in ${condition::class.qualifiedName} condition. It's not supposed to end with success")
                ExternalOverridabilityCondition.Result.UNKNOWN -> Unit
            }
        }
        return OverrideCompatibilityInfo.success()
    }
    fun isOverridableByWithoutExternalConditions(
        superDescriptor: CallableDescriptor, subDescriptor: CallableDescriptor, checkReturnType: Boolean
    ): OverrideCompatibilityInfo {
        getBasicOverridabilityProblem(superDescriptor, subDescriptor)?.let { return it }
        val superValueParameters = compiledValueParameters(superDescriptor)
        val subValueParameters = compiledValueParameters(subDescriptor)
        val superTypeParameters = superDescriptor.getTypeParameters()
        val subTypeParameters = subDescriptor.getTypeParameters()
        if (superTypeParameters.size != subTypeParameters.size) {
            for (i in superValueParameters.indices) {
                if (!KotlinTypeChecker.DEFAULT.equalTypes(superValueParameters[i], subValueParameters[i]))
                    return OverrideCompatibilityInfo.incompatible("Type parameter number mismatch")
            }
            return OverrideCompatibilityInfo.conflict("Type parameter number mismatch")
        }
        val state = createTypeCheckerState(superTypeParameters, subTypeParameters)
        for (i in superTypeParameters.indices) {
            if (!areTypeParametersEquivalent(superTypeParameters[i], subTypeParameters[i], state))
                return OverrideCompatibilityInfo.incompatible("Type parameter bounds mismatch")
        }
        for (i in superValueParameters.indices) {
            if (!areTypesEquivalent(superValueParameters[i], subValueParameters[i], state))
                return OverrideCompatibilityInfo.incompatible("Value parameter type mismatch")
        }
        if (superDescriptor is FunctionDescriptor && subDescriptor is FunctionDescriptor && superDescriptor.isSuspend() != subDescriptor.isSuspend())
            return OverrideCompatibilityInfo.conflict("Incompatible suspendability")
        if (checkReturnType) {
            val superReturnType = superDescriptor.getReturnType()
            val subReturnType = subDescriptor.getReturnType()
            if (superReturnType != null && subReturnType != null) {
                val bothErrors = subReturnType.isError && superReturnType.isError
                if (!bothErrors && !AbstractTypeChecker.isSubtypeOf(state, subReturnType.unwrap(), superReturnType.unwrap()))
                    return OverrideCompatibilityInfo.conflict("Return type mismatch")
            }
        }
        return OverrideCompatibilityInfo.success()
    }
    private fun createTypeCheckerState(firstParameters: List<TypeParameterDescriptor>, secondParameters: List<TypeParameterDescriptor>): TypeCheckerState {
        assert(firstParameters.size == secondParameters.size) { "Should be the same number of type parameters: $firstParameters vs $secondParameters" }
        val customSubtypingCallback: ((KotlinTypeMarker, KotlinTypeMarker) -> Boolean?)? =
            customSubtype?.let { callback -> { a, b -> callback(a as KotlinType, b as KotlinType) } }
        if (firstParameters.isEmpty())
            return OverridingUtilTypeSystemContext(null, equalityAxioms, kotlinTypeRefiner, kotlinTypePreparator, customSubtypingCallback)
                .newTypeCheckerState(true, true, false)
        val matching = HashMap<TypeConstructor, TypeConstructor>()
        for (i in firstParameters.indices) matching[firstParameters[i].getTypeConstructor()] = secondParameters[i].getTypeConstructor()
        return OverridingUtilTypeSystemContext(matching, equalityAxioms, kotlinTypeRefiner, kotlinTypePreparator, customSubtypingCallback)
            .newTypeCheckerState(true, true, false)
    }
    fun generateOverridesInFunctionGroup(
        name: Name, membersFromSupertypes: Collection<CallableMemberDescriptor>, membersFromCurrent: Collection<CallableMemberDescriptor>,
        current: ClassDescriptor, strategy: OverridingStrategy
    ) {
        val notOverridden = LinkedHashSet(membersFromSupertypes)
        for (fromCurrent in membersFromCurrent) {
            val bound = extractAndBindOverridesForMember(fromCurrent, membersFromSupertypes, current, strategy)
            notOverridden.removeAll(bound)
        }
        createAndBindFakeOverrides(current, notOverridden, strategy)
    }
    private fun extractAndBindOverridesForMember(
        fromCurrent: CallableMemberDescriptor, descriptorsFromSuper: Collection<CallableMemberDescriptor>,
        current: ClassDescriptor, strategy: OverridingStrategy
    ): Collection<CallableMemberDescriptor> {
        val bound = ArrayList<CallableMemberDescriptor>(descriptorsFromSuper.size)
        val overridden = SmartSet.create<CallableMemberDescriptor>()
        for (fromSupertype in descriptorsFromSuper) {
            val result = isOverridableBy(fromSupertype, fromCurrent, current).getResult()
            val visible = isVisibleForOverride(fromCurrent, fromSupertype, false)
            when (result) {
                OVERRIDABLE -> {
                    if (visible) overridden.add(fromSupertype)
                    bound.add(fromSupertype)
                }
                CONFLICT -> {
                    if (visible) strategy.overrideConflict(fromSupertype, fromCurrent)
                    bound.add(fromSupertype)
                }
                INCOMPATIBLE -> Unit
            }
        }
        strategy.setOverriddenDescriptors(fromCurrent, overridden)
        return bound
    }

    open class OverrideCompatibilityInfo(success: Result, private val debugMessage: String) {
        enum class Result { OVERRIDABLE, INCOMPATIBLE, CONFLICT }
        private val overridable = success
        open fun getResult(): Result = overridable
        open fun getDebugMessage(): String = debugMessage
        override fun toString(): String = "$overridable: $debugMessage"
        companion object {
            private val SUCCESS = OverrideCompatibilityInfo(OVERRIDABLE, "SUCCESS")
            fun success(): OverrideCompatibilityInfo = SUCCESS
            fun incompatible(debugMessage: String): OverrideCompatibilityInfo = OverrideCompatibilityInfo(INCOMPATIBLE, debugMessage)
            fun conflict(debugMessage: String): OverrideCompatibilityInfo = OverrideCompatibilityInfo(CONFLICT, debugMessage)
        }
    }
    companion object {
        private val EXTERNAL_CONDITIONS: List<ExternalOverridabilityCondition>
            get() = NativeDescriptorServices.externalConditions
        private val DEFAULT_TYPE_CONSTRUCTOR_EQUALITY = KotlinTypeChecker.TypeConstructorEquality { a, b -> a == b }
        val DEFAULT = OverridingUtil(DEFAULT_TYPE_CONSTRUCTOR_EQUALITY, KotlinTypeRefiner.Default, KotlinTypePreparator.Default, null)
        fun createWithTypeRefiner(kotlinTypeRefiner: KotlinTypeRefiner): OverridingUtil =
            OverridingUtil(DEFAULT_TYPE_CONSTRUCTOR_EQUALITY, kotlinTypeRefiner, KotlinTypePreparator.Default, null)
        fun createWithTypePreparatorAndCustomSubtype(kotlinTypePreparator: KotlinTypePreparator, customSubtype: (KotlinType, KotlinType) -> Boolean?): OverridingUtil =
            OverridingUtil(DEFAULT_TYPE_CONSTRUCTOR_EQUALITY, KotlinTypeRefiner.Default, kotlinTypePreparator, customSubtype)
        fun create(kotlinTypeRefiner: KotlinTypeRefiner, equalityAxioms: KotlinTypeChecker.TypeConstructorEquality): OverridingUtil =
            OverridingUtil(equalityAxioms, kotlinTypeRefiner, KotlinTypePreparator.Default, null)
        fun <D : CallableDescriptor> filterOutOverridden(candidateSet: Set<D>): Set<D> {
            val allowCopies = candidateSet.isNotEmpty() && candidateSet.first().module.isTypeRefinementEnabled()
            return filterOverrides(candidateSet, allowCopies, null) { a, b -> Pair(a, b) }
        }
        fun <D> filterOverrides(
            candidateSet: Set<D>, allowDescriptorCopies: Boolean, cancellationCallback: (() -> Any?)?,
            transformFirst: (D, D) -> Pair<CallableDescriptor, CallableDescriptor>
        ): Set<D> {
            if (candidateSet.size <= 1) return candidateSet
            val result = LinkedHashSet<D>()
            outerLoop@ for (meD in candidateSet) {
                cancellationCallback?.invoke()
                val iterator = result.iterator()
                while (iterator.hasNext()) {
                    val otherD = iterator.next()
                    val (me, other) = transformFirst(meD, otherD)
                    if (overrides(me, other, allowDescriptorCopies, true)) iterator.remove()
                    else if (overrides(other, me, allowDescriptorCopies, true)) continue@outerLoop
                }
                result.add(meD)
            }
            assert(result.isNotEmpty()) { "All candidates filtered out from $candidateSet" }
            return result
        }
        fun <D : CallableDescriptor> overrides(f: D, g: D, allowDeclarationCopies: Boolean, distinguishExpectsAndNonExpects: Boolean): Boolean {
            if (f != g && DescriptorEquivalenceForOverrides.areEquivalent(f.getOriginal(), g.getOriginal(), allowDeclarationCopies, distinguishExpectsAndNonExpects))
                return true
            val originalG = g.getOriginal()
            for (overridden in DescriptorUtils.getAllOverriddenDescriptors(f)) {
                if (DescriptorEquivalenceForOverrides.areEquivalent(originalG, overridden, allowDeclarationCopies, distinguishExpectsAndNonExpects))
                    return true
            }
            return false
        }
        fun getOverriddenDeclarations(descriptor: CallableMemberDescriptor): Set<CallableMemberDescriptor> {
            val result = LinkedHashSet<CallableMemberDescriptor>()
            collectOverriddenDeclarations(descriptor, result)
            return result
        }
        private fun collectOverriddenDeclarations(descriptor: CallableMemberDescriptor, result: MutableSet<CallableMemberDescriptor>) {
            if (descriptor.getKind().isReal()) result.add(descriptor)
            else {
                if (descriptor.getOverriddenDescriptors().isEmpty()) throw IllegalStateException("No overridden descriptors found for (fake override) $descriptor")
                for (overridden in descriptor.getOverriddenDescriptors()) collectOverriddenDeclarations(overridden, result)
            }
        }
        fun getBasicOverridabilityProblem(superDescriptor: CallableDescriptor, subDescriptor: CallableDescriptor): OverrideCompatibilityInfo? {
            if (superDescriptor is FunctionDescriptor && subDescriptor !is FunctionDescriptor ||
                superDescriptor is PropertyDescriptor && subDescriptor !is PropertyDescriptor)
                return OverrideCompatibilityInfo.incompatible("Member kind mismatch")
            if (superDescriptor !is FunctionDescriptor && superDescriptor !is PropertyDescriptor)
                throw IllegalArgumentException("This type of CallableDescriptor cannot be checked for overridability: $superDescriptor")
            if (superDescriptor.getName() != subDescriptor.getName()) return OverrideCompatibilityInfo.incompatible("Name mismatch")
            return checkReceiverAndParameterCount(superDescriptor, subDescriptor)
        }
        private fun checkReceiverAndParameterCount(superDescriptor: CallableDescriptor, subDescriptor: CallableDescriptor): OverrideCompatibilityInfo? {
            if ((superDescriptor.getExtensionReceiverParameter() == null) != (subDescriptor.getExtensionReceiverParameter() == null))
                return OverrideCompatibilityInfo.incompatible("Receiver presence mismatch")
            if (superDescriptor.getValueParameters().size != subDescriptor.getValueParameters().size)
                return OverrideCompatibilityInfo.incompatible("Value parameter number mismatch")
            return null
        }
        private fun areTypesEquivalent(typeInSuper: KotlinType, typeInSub: KotlinType, typeCheckerState: TypeCheckerState): Boolean {
            if (typeInSuper.isError && typeInSub.isError) return true
            return AbstractTypeChecker.equalTypes(typeCheckerState, typeInSuper.unwrap(), typeInSub.unwrap())
        }
        private fun areTypeParametersEquivalent(superTypeParameter: TypeParameterDescriptor, subTypeParameter: TypeParameterDescriptor,
                                               typeCheckerState: TypeCheckerState): Boolean {
            val superBounds = superTypeParameter.getUpperBounds()
            val subBounds = ArrayList(subTypeParameter.getUpperBounds())
            if (superBounds.size != subBounds.size) return false
            outer@ for (superBound in superBounds) {
                val iterator = subBounds.listIterator()
                while (iterator.hasNext()) {
                    if (areTypesEquivalent(superBound, iterator.next(), typeCheckerState)) {
                        iterator.remove()
                        continue@outer
                    }
                }
                return false
            }
            return true
        }
        private fun compiledValueParameters(callableDescriptor: CallableDescriptor): List<KotlinType> {
            val parameters = ArrayList<KotlinType>()
            callableDescriptor.getExtensionReceiverParameter()?.let { parameters.add(it.getType()) }
            for (parameter in callableDescriptor.getValueParameters()) parameters.add(parameter.getType())
            return parameters
        }
        fun isVisibleForOverride(overriding: MemberDescriptor, fromSuper: MemberDescriptor, useSpecialRulesForPrivateSealedConstructors: Boolean): Boolean =
            !DescriptorVisibilities.isPrivate(fromSuper.getVisibility()) &&
                DescriptorVisibilities.isVisibleIgnoringReceiver(fromSuper, overriding, useSpecialRulesForPrivateSealedConstructors)
        private fun allHasSameContainingDeclaration(notOverridden: Collection<CallableMemberDescriptor>): Boolean {
            if (notOverridden.size < 2) return true
            val containing = notOverridden.first().getContainingDeclaration()
            return notOverridden.all { it.getContainingDeclaration() === containing }
        }
        private fun createAndBindFakeOverrides(current: ClassDescriptor, notOverridden: Collection<CallableMemberDescriptor>, strategy: OverridingStrategy) {
            if (allHasSameContainingDeclaration(notOverridden)) {
                for (descriptor in notOverridden) createAndBindFakeOverride(setOf(descriptor), current, strategy)
                return
            }
            val queue = ArrayList(notOverridden)
            while (queue.isNotEmpty()) {
                val fromSuper = findMemberWithMaxVisibility(queue)
                val overridables = extractMembersOverridableInBothWays(fromSuper, queue, strategy)
                createAndBindFakeOverride(overridables, current, strategy)
            }
        }
        fun isMoreSpecific(a: CallableDescriptor, b: CallableDescriptor): Boolean {
            val aReturnType = a.getReturnType()
            val bReturnType = b.getReturnType()
            assert(aReturnType != null) { "Return type of $a is null" }
            assert(bReturnType != null) { "Return type of $b is null" }
            if (!isVisibilityMoreSpecific(a, b)) return false
            val state = DEFAULT.createTypeCheckerState(a.getTypeParameters(), b.getTypeParameters())
            if (a is FunctionDescriptor) {
                assert(b is FunctionDescriptor) { "b is ${b::class}" }
                return isReturnTypeMoreSpecific(a, aReturnType!!, b, bReturnType!!, state)
            }
            if (a is PropertyDescriptor) {
                assert(b is PropertyDescriptor) { "b is ${b::class}" }
                b as PropertyDescriptor
                if (!isAccessorMoreSpecific(a.getSetter(), b.getSetter())) return false
                return if (a.isVar() && b.isVar()) AbstractTypeChecker.equalTypes(state, aReturnType!!.unwrap(), bReturnType!!.unwrap())
                else !(!a.isVar() && b.isVar()) && isReturnTypeMoreSpecific(a, aReturnType!!, b, bReturnType!!, state)
            }
            throw IllegalArgumentException("Unexpected callable: ${a::class}")
        }
        private fun isVisibilityMoreSpecific(a: DeclarationDescriptorWithVisibility, b: DeclarationDescriptorWithVisibility): Boolean {
            val result = DescriptorVisibilities.compare(a.getVisibility(), b.getVisibility())
            return result == null || result >= 0
        }
        private fun isAccessorMoreSpecific(a: PropertyAccessorDescriptor?, b: PropertyAccessorDescriptor?): Boolean =
            if (a == null || b == null) true else isVisibilityMoreSpecific(a, b)
        private fun isMoreSpecificThenAllOf(candidate: CallableDescriptor, descriptors: Collection<CallableDescriptor>): Boolean {
            for (descriptor in descriptors) if (!isMoreSpecific(candidate, descriptor)) return false
            return true
        }
        private fun isReturnTypeMoreSpecific(a: CallableDescriptor, aReturnType: KotlinType, b: CallableDescriptor,
                                            bReturnType: KotlinType, typeCheckerState: TypeCheckerState): Boolean =
            AbstractTypeChecker.isSubtypeOf(typeCheckerState, aReturnType.unwrap(), bReturnType.unwrap())
        fun <H> selectMostSpecificMember(overridables: Collection<H>, descriptorByHandle: (H) -> CallableDescriptor): H {
            assert(overridables.isNotEmpty()) { "Should have at least one overridable descriptor" }
            if (overridables.size == 1) return overridables.first()
            val candidates = ArrayList<H>(2)
            val descriptors = overridables.map(descriptorByHandle)
            var transitivelyMostSpecific = overridables.first()
            // Intentionally fixed to the initial descriptor, matching the upstream algorithm.
            val transitivelyMostSpecificDescriptor = descriptorByHandle(transitivelyMostSpecific)
            for (overridable in overridables) {
                val descriptor = descriptorByHandle(overridable)
                if (isMoreSpecificThenAllOf(descriptor, descriptors)) candidates.add(overridable)
                if (isMoreSpecific(descriptor, transitivelyMostSpecificDescriptor) && !isMoreSpecific(transitivelyMostSpecificDescriptor, descriptor))
                    transitivelyMostSpecific = overridable
            }
            if (candidates.isEmpty()) return transitivelyMostSpecific
            if (candidates.size == 1) return candidates.first()
            var firstNonFlexible: H? = null
            for (candidate in candidates) {
                if (!descriptorByHandle(candidate).getReturnType()!!.isFlexible()) {
                    firstNonFlexible = candidate
                    break
                }
            }
            if (firstNonFlexible != null) return firstNonFlexible
            return candidates.first()
        }
        private fun createAndBindFakeOverride(overridables: Collection<CallableMemberDescriptor>, current: ClassDescriptor, strategy: OverridingStrategy) {
            val visible = filterVisibleFakeOverrides(current, overridables)
            val allInvisible = visible.isEmpty()
            val effective = if (allInvisible) overridables else visible
            val modality = determineModalityForFakeOverride(effective, current)
            val visibility = if (allInvisible) DescriptorVisibilities.INVISIBLE_FAKE else DescriptorVisibilities.INHERITED
            val mostSpecific = selectMostSpecificMember(effective) { it }
            val fakeOverride = mostSpecific.copy(current, modality, visibility, CallableMemberDescriptor.Kind.FAKE_OVERRIDE, false)
            strategy.setOverriddenDescriptors(fakeOverride, effective)
            assert(fakeOverride.getOverriddenDescriptors().isNotEmpty()) { "Overridden descriptors should be set for ${CallableMemberDescriptor.Kind.FAKE_OVERRIDE}" }
            strategy.addFakeOverride(fakeOverride)
        }
        private fun determineModalityForFakeOverride(descriptors: Collection<CallableMemberDescriptor>, current: ClassDescriptor): Modality {
            var hasOpen = false
            var hasAbstract = false
            for (descriptor in descriptors) when (descriptor.getModality()) {
                Modality.FINAL -> return Modality.FINAL
                Modality.SEALED -> throw IllegalStateException("Member cannot have SEALED modality: $descriptor")
                Modality.OPEN -> hasOpen = true
                Modality.ABSTRACT -> hasAbstract = true
            }
            val transform = current.isExpect() && current.getModality() != Modality.ABSTRACT && current.getModality() != Modality.SEALED
            if (hasOpen && !hasAbstract) return Modality.OPEN
            if (!hasOpen && hasAbstract) return if (transform) current.getModality() else Modality.ABSTRACT
            val all = HashSet<CallableMemberDescriptor>()
            for (descriptor in descriptors) all.addAll(getOverriddenDeclarations(descriptor))
            return getMinimalModality(filterOutOverridden(all), transform, current.getModality())
        }
        private fun getMinimalModality(descriptors: Collection<CallableMemberDescriptor>, transformAbstractToClassModality: Boolean,
                                       classModality: Modality): Modality {
            var result = Modality.ABSTRACT
            for (descriptor in descriptors) {
                val effective = if (transformAbstractToClassModality && descriptor.getModality() == Modality.ABSTRACT) classModality else descriptor.getModality()
                if (effective < result) result = effective
            }
            return result
        }
        fun filterVisibleFakeOverrides(current: ClassDescriptor, toFilter: Collection<CallableMemberDescriptor>): Collection<CallableMemberDescriptor> =
            toFilter.filter { !DescriptorVisibilities.isPrivate(it.getVisibility()) && DescriptorVisibilities.isVisibleIgnoringReceiver(it, current, false) }
        fun <H> extractMembersOverridableInBothWays(overrider: H, extractFrom: MutableCollection<H>,
                                                  descriptorByHandle: (H) -> CallableDescriptor, onConflict: (H) -> Unit): Collection<H> {
            val overridable = arrayListOf(overrider)
            val descriptor = descriptorByHandle(overrider)
            val iterator = extractFrom.iterator()
            while (iterator.hasNext()) {
                val candidate = iterator.next()
                val candidateDescriptor = descriptorByHandle(candidate)
                if (overrider === candidate) {
                    iterator.remove()
                    continue
                }
                val result = getBothWaysOverridability(descriptor, candidateDescriptor)
                if (result == OVERRIDABLE) {
                    overridable.add(candidate)
                    iterator.remove()
                } else if (result == CONFLICT) {
                    onConflict(candidate)
                    iterator.remove()
                }
            }
            return overridable
        }
        fun getBothWaysOverridability(overriderDescriptor: CallableDescriptor, candidateDescriptor: CallableDescriptor): OverrideCompatibilityInfo.Result? {
            val first = DEFAULT.isOverridableBy(candidateDescriptor, overriderDescriptor, null).getResult()
            val second = DEFAULT.isOverridableBy(overriderDescriptor, candidateDescriptor, null).getResult()
            return if (first == OVERRIDABLE && second == OVERRIDABLE) OVERRIDABLE
            else if (first == CONFLICT || second == CONFLICT) CONFLICT else INCOMPATIBLE
        }
        private fun extractMembersOverridableInBothWays(overrider: CallableMemberDescriptor, extractFrom: MutableCollection<CallableMemberDescriptor>,
                                                       strategy: OverridingStrategy): Collection<CallableMemberDescriptor> =
            extractMembersOverridableInBothWays(overrider, extractFrom, { it }) { strategy.inheritanceConflict(overrider, it) }
        fun resolveUnknownVisibilityForMember(memberDescriptor: CallableMemberDescriptor, cannotInferVisibility: ((CallableMemberDescriptor) -> Unit)?) {
            for (descriptor in memberDescriptor.getOverriddenDescriptors())
                if (descriptor.getVisibility() === DescriptorVisibilities.INHERITED) resolveUnknownVisibilityForMember(descriptor, cannotInferVisibility)
            if (memberDescriptor.getVisibility() !== DescriptorVisibilities.INHERITED) return
            val maxVisibility = computeVisibilityToInherit(memberDescriptor)
            val visibility = if (maxVisibility == null) {
                cannotInferVisibility?.invoke(memberDescriptor)
                DescriptorVisibilities.PUBLIC
            } else maxVisibility
            if (memberDescriptor is PropertyDescriptorImpl) {
                memberDescriptor.setVisibility(visibility)
                for (accessor in memberDescriptor.getAccessors()) resolveUnknownVisibilityForMember(accessor, if (maxVisibility == null) null else cannotInferVisibility)
            } else if (memberDescriptor is FunctionDescriptorImpl) memberDescriptor.setVisibility(visibility)
            else {
                assert(memberDescriptor is PropertyAccessorDescriptorImpl)
                memberDescriptor as PropertyAccessorDescriptorImpl
                memberDescriptor.setVisibility(visibility)
                if (visibility !== memberDescriptor.getCorrespondingProperty().getVisibility()) memberDescriptor.setDefault(false)
            }
        }
        private fun computeVisibilityToInherit(memberDescriptor: CallableMemberDescriptor): DescriptorVisibility? {
            val overridden = memberDescriptor.getOverriddenDescriptors()
            val maxVisibility = findMaxVisibility(overridden) ?: return null
            if (memberDescriptor.getKind() == CallableMemberDescriptor.Kind.FAKE_OVERRIDE) {
                for (descriptor in overridden)
                    if (descriptor.getModality() != Modality.ABSTRACT && descriptor.getVisibility() != maxVisibility) return null
                return maxVisibility
            }
            return maxVisibility.normalize()
        }
        fun findMaxVisibility(descriptors: Collection<CallableMemberDescriptor>): DescriptorVisibility? {
            if (descriptors.isEmpty()) return DescriptorVisibilities.DEFAULT_VISIBILITY
            var maxVisibility: DescriptorVisibility? = null
            for (descriptor in descriptors) {
                val visibility = descriptor.getVisibility()
                assert(visibility !== DescriptorVisibilities.INHERITED) { "Visibility should have been computed for $descriptor" }
                if (maxVisibility == null) { maxVisibility = visibility; continue }
                val result = DescriptorVisibilities.compare(visibility, maxVisibility)
                if (result == null) maxVisibility = null
                else if (result > 0) maxVisibility = visibility
            }
            if (maxVisibility == null) return null
            for (descriptor in descriptors) {
                val result = DescriptorVisibilities.compare(maxVisibility, descriptor.getVisibility())
                if (result == null || result < 0) return null
            }
            return maxVisibility
        }
    }
}

val OverridingUtil.OverrideCompatibilityInfo.result: OverridingUtil.OverrideCompatibilityInfo.Result get() = getResult()
val OverridingUtil.OverrideCompatibilityInfo.debugMessage: String get() = getDebugMessage()
