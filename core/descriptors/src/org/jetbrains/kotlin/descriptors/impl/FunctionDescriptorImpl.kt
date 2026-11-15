/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin.descriptors.impl

import kotlin.concurrent.Volatile
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.CallableDescriptor.UserDataKey
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor.Kind
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.annotations.composeAnnotations
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.DescriptorFactory
import org.jetbrains.kotlin.resolve.scopes.receivers.ExtensionReceiver
import org.jetbrains.kotlin.resolve.scopes.receivers.ImplicitContextReceiver
import org.jetbrains.kotlin.types.*

// Translated-unverified.
abstract class FunctionDescriptorImpl protected constructor(
    containingDeclaration: DeclarationDescriptor, original: FunctionDescriptor?, annotations: Annotations,
    name: Name, kind: Kind, source: SourceElement
) : DeclarationDescriptorNonRootImpl(containingDeclaration, annotations, name, source), FunctionDescriptor {
    private var typeParameters: List<TypeParameterDescriptor>? = null
    private var unsubstitutedValueParameters: List<ValueParameterDescriptor>? = null
    private var unsubstitutedReturnType: KotlinType? = null
    private var contextReceiverParameters: List<ReceiverParameterDescriptor>? = null
    private var extensionReceiverParameter: ReceiverParameterDescriptor? = null
    private var dispatchReceiverParameter: ReceiverParameterDescriptor? = null
    private var modality: Modality? = null
    private var visibility = DescriptorVisibilities.UNKNOWN
    private var operatorFlag = false
    private var infixFlag = false
    private var externalFlag = false
    private var inlineFlag = false
    private var tailrecFlag = false
    private var expectFlag = false
    private var actualFlag = false
    // Signature-clash hiding forbids super calls; resolution hiding propagates to overrides.
    private var hiddenToOvercomeSignatureClash = false
    private var hiddenForResolutionEverywhereBesideSupercalls = false
    private var suspendFlag = false
    private var stableParameterNames = true
    private var synthesizedParameterNames = false
    private var overriddenFunctions: Collection<FunctionDescriptor>? = null
    @Volatile
    private var lazyOverriddenFunctionsTask: (() -> Collection<FunctionDescriptor>)? = null
    private val originalDescriptor = original ?: this
    private val memberKind = kind
    private var initialSignatureDescriptor: FunctionDescriptor? = null
    protected var userDataMap: MutableMap<UserDataKey<*>, Any?>? = null

    open fun initialize(
        extensionReceiverParameter: ReceiverParameterDescriptor?, dispatchReceiverParameter: ReceiverParameterDescriptor?,
        contextReceiverParameters: List<ReceiverParameterDescriptor>, typeParameters: List<TypeParameterDescriptor>,
        unsubstitutedValueParameters: List<ValueParameterDescriptor>, unsubstitutedReturnType: KotlinType?,
        modality: Modality?, visibility: DescriptorVisibility
    ): FunctionDescriptorImpl {
        this.typeParameters = typeParameters.toList()
        this.unsubstitutedValueParameters = unsubstitutedValueParameters.toList()
        this.unsubstitutedReturnType = unsubstitutedReturnType
        this.modality = modality
        this.visibility = visibility
        this.extensionReceiverParameter = extensionReceiverParameter
        this.dispatchReceiverParameter = dispatchReceiverParameter
        this.contextReceiverParameters = contextReceiverParameters
        for (i in typeParameters.indices) {
            val parameter = typeParameters[i]
            if (parameter.getIndex() != i)
                throw IllegalStateException("$parameter index is ${parameter.getIndex()} but position is $i")
        }
        for (i in unsubstitutedValueParameters.indices) {
            val firstValueParameterOffset = 0
            val parameter = unsubstitutedValueParameters[i]
            if (parameter.index != i + firstValueParameterOffset)
                throw IllegalStateException("${parameter}index is ${parameter.index} but position is $i")
        }
        return this
    }

    open fun setVisibility(visibility: DescriptorVisibility) { this.visibility = visibility }
    open fun setOperator(isOperator: Boolean) { operatorFlag = isOperator }
    open fun setInfix(isInfix: Boolean) { infixFlag = isInfix }
    open fun setExternal(isExternal: Boolean) { externalFlag = isExternal }
    open fun setInline(isInline: Boolean) { inlineFlag = isInline }
    open fun setTailrec(isTailrec: Boolean) { tailrecFlag = isTailrec }
    open fun setExpect(isExpect: Boolean) { expectFlag = isExpect }
    open fun setActual(isActual: Boolean) { actualFlag = isActual }
    private fun setHiddenToOvercomeSignatureClash(value: Boolean) { hiddenToOvercomeSignatureClash = value }
    private fun setHiddenForResolutionEverywhereBesideSupercalls(value: Boolean) { hiddenForResolutionEverywhereBesideSupercalls = value }
    open fun setSuspend(suspend: Boolean) { suspendFlag = suspend }
    open fun setReturnType(unsubstitutedReturnType: KotlinType) { this.unsubstitutedReturnType = unsubstitutedReturnType }
    open fun setHasStableParameterNames(hasStableParameterNames: Boolean) { stableParameterNames = hasStableParameterNames }
    open fun setHasSynthesizedParameterNames(hasSynthesizedParameterNames: Boolean) { synthesizedParameterNames = hasSynthesizedParameterNames }
    open fun setExtensionReceiverParameter(extensionReceiverParameter: ReceiverParameterDescriptor) { this.extensionReceiverParameter = extensionReceiverParameter }
    override fun getContextReceiverParameters(): List<ReceiverParameterDescriptor> = contextReceiverParameters!!
    override fun getExtensionReceiverParameter(): ReceiverParameterDescriptor? = extensionReceiverParameter
    override fun getDispatchReceiverParameter(): ReceiverParameterDescriptor? = dispatchReceiverParameter
    override fun getOverriddenDescriptors(): Collection<FunctionDescriptor> {
        performOverriddenLazyCalculationIfNeeded()
        return overriddenFunctions ?: emptyList()
    }
    private fun performOverriddenLazyCalculationIfNeeded() {
        val overriddenTask = lazyOverriddenFunctionsTask
        if (overriddenTask != null) {
            overriddenFunctions = overriddenTask()
            // Publish the collection before the volatile completion marker, as in the Java implementation.
            lazyOverriddenFunctionsTask = null
        }
    }
    override fun getModality(): Modality = modality!!
    override fun getVisibility(): DescriptorVisibility = visibility
    override fun isOperator(): Boolean {
        if (operatorFlag) return true
        for (descriptor in getOriginal().getOverriddenDescriptors()) if (descriptor.isOperator()) return true
        return false
    }
    override fun isInfix(): Boolean {
        if (infixFlag) return true
        for (descriptor in getOriginal().getOverriddenDescriptors()) if (descriptor.isInfix()) return true
        return false
    }
    override fun isExternal(): Boolean = externalFlag
    override fun isInline(): Boolean = inlineFlag
    override fun isTailrec(): Boolean = tailrecFlag
    override fun isSuspend(): Boolean = suspendFlag
    override fun isExpect(): Boolean = expectFlag
    override fun isActual(): Boolean = actualFlag
    @Suppress("UNCHECKED_CAST")
    override fun <V> getUserData(key: UserDataKey<V>): V? = userDataMap?.get(key) as V?
    override fun isHiddenToOvercomeSignatureClash(): Boolean = hiddenToOvercomeSignatureClash
    @Suppress("UNCHECKED_CAST")
    override fun setOverriddenDescriptors(overriddenDescriptors: Collection<CallableMemberDescriptor?>) {
        overriddenFunctions = overriddenDescriptors as Collection<FunctionDescriptor>
        for (function in overriddenFunctions!!) {
            if (function.isHiddenForResolutionEverywhereBesideSupercalls()) {
                hiddenForResolutionEverywhereBesideSupercalls = true
                break
            }
        }
    }
    override fun getTypeParameters(): List<TypeParameterDescriptor> =
        typeParameters ?: throw IllegalStateException("typeParameters == null for $this")
    override fun validate() { getTypeParameters() }
    override fun getValueParameters(): List<ValueParameterDescriptor> = unsubstitutedValueParameters!!
    override fun hasStableParameterNames(): Boolean = stableParameterNames
    override fun hasSynthesizedParameterNames(): Boolean = synthesizedParameterNames
    override fun getReturnType(): KotlinType? = unsubstitutedReturnType
    override fun getOriginal(): FunctionDescriptor = if (originalDescriptor === this) this else originalDescriptor.getOriginal()
    override fun getKind(): Kind = memberKind
    override fun substitute(substitutor: TypeSubstitutor): FunctionDescriptor? {
        if (substitutor.isEmpty()) return this
        return newCopyBuilder(substitutor).setOriginal(getOriginal()).setPreserveSourceElement().setJustForTypeSubstitution(true).build()
    }
    private fun getExtensionReceiverParameterType(): KotlinType? = extensionReceiverParameter?.getType()
    override fun isHiddenForResolutionEverywhereBesideSupercalls(): Boolean = hiddenForResolutionEverywhereBesideSupercalls

    open inner class CopyConfiguration(
        var substitution: TypeSubstitution, var newOwner: DeclarationDescriptor, var newModality: Modality,
        var newVisibility: DescriptorVisibility, var kind: Kind, var newValueParameterDescriptors: List<ValueParameterDescriptor>,
        var newContextReceiverParameters: List<ReceiverParameterDescriptor>, var newExtensionReceiverParameter: ReceiverParameterDescriptor?,
        var newReturnType: KotlinType, var name: Name?
    ) : FunctionDescriptor.CopyBuilder<FunctionDescriptor> {
        var original: FunctionDescriptor? = null
        var dispatchReceiverParameter = this@FunctionDescriptorImpl.dispatchReceiverParameter
        var copyOverrides = true
        var signatureChange = false
        var preserveSourceElement = false
        var dropOriginalInContainingParts = false
        var hiddenToOvercomeSignatureClash = this@FunctionDescriptorImpl.isHiddenToOvercomeSignatureClash()
        var newTypeParameters: List<TypeParameterDescriptor>? = null
        var additionalAnnotations: Annotations? = null
        var hiddenForResolutionEverywhereBesideSupercalls = this@FunctionDescriptorImpl.isHiddenForResolutionEverywhereBesideSupercalls()
        var userDataMap = LinkedHashMap<UserDataKey<*>, Any?>()
        var newHasSynthesizedParameterNames: Boolean? = null
        var justForTypeSubstitution = false

        override fun setOwner(owner: DeclarationDescriptor): CopyConfiguration { newOwner = owner; return this }
        override fun setModality(modality: Modality): CopyConfiguration { newModality = modality; return this }
        override fun setVisibility(visibility: DescriptorVisibility): CopyConfiguration { newVisibility = visibility; return this }
        override fun setKind(kind: Kind): CopyConfiguration { this.kind = kind; return this }
        override fun setCopyOverrides(copyOverrides: Boolean): CopyConfiguration { this.copyOverrides = copyOverrides; return this }
        override fun setName(name: Name): CopyConfiguration { this.name = name; return this }
        override fun setValueParameters(parameters: List<ValueParameterDescriptor>): CopyConfiguration { newValueParameterDescriptors = parameters; return this }
        override fun setTypeParameters(parameters: List<TypeParameterDescriptor>): CopyConfiguration { newTypeParameters = parameters; return this }
        override fun setReturnType(type: KotlinType): CopyConfiguration { newReturnType = type; return this }
        override fun setContextReceiverParameters(contextReceiverParameters: List<ReceiverParameterDescriptor>): FunctionDescriptor.CopyBuilder<FunctionDescriptor> {
            newContextReceiverParameters = contextReceiverParameters
            return this
        }
        override fun setExtensionReceiverParameter(extensionReceiverParameter: ReceiverParameterDescriptor?): CopyConfiguration {
            newExtensionReceiverParameter = extensionReceiverParameter
            return this
        }
        override fun setDispatchReceiverParameter(dispatchReceiverParameter: ReceiverParameterDescriptor?): CopyConfiguration {
            this.dispatchReceiverParameter = dispatchReceiverParameter
            return this
        }
        override fun setOriginal(original: CallableMemberDescriptor?): CopyConfiguration { this.original = original as FunctionDescriptor?; return this }
        override fun setSignatureChange(): CopyConfiguration { signatureChange = true; return this }
        override fun setPreserveSourceElement(): CopyConfiguration { preserveSourceElement = true; return this }
        override fun setDropOriginalInContainingParts(): CopyConfiguration { dropOriginalInContainingParts = true; return this }
        override fun setHiddenToOvercomeSignatureClash(): CopyConfiguration { hiddenToOvercomeSignatureClash = true; return this }
        override fun setHiddenForResolutionEverywhereBesideSupercalls(): CopyConfiguration { hiddenForResolutionEverywhereBesideSupercalls = true; return this }
        override fun setAdditionalAnnotations(additionalAnnotations: Annotations): CopyConfiguration { this.additionalAnnotations = additionalAnnotations; return this }
        open fun setHasSynthesizedParameterNames(value: Boolean): CopyConfiguration { newHasSynthesizedParameterNames = value; return this }
        override fun setSubstitution(substitution: TypeSubstitution): CopyConfiguration { this.substitution = substitution; return this }
        override fun <V> putUserData(userDataKey: UserDataKey<V>, value: V): FunctionDescriptor.CopyBuilder<FunctionDescriptor> {
            userDataMap[userDataKey] = value
            return this
        }
        override fun build(): FunctionDescriptor? = doSubstitute(this)
        open fun getOriginal(): FunctionDescriptor? = original
        open fun getSubstitution(): TypeSubstitution = substitution
        open fun setJustForTypeSubstitution(value: Boolean): CopyConfiguration { justForTypeSubstitution = value; return this }
    }

    override fun newCopyBuilder(): FunctionDescriptor.CopyBuilder<out FunctionDescriptor?> = newCopyBuilder(TypeSubstitutor.EMPTY)
    protected open fun newCopyBuilder(substitutor: TypeSubstitutor): CopyConfiguration =
        CopyConfiguration(substitutor.getSubstitution(), getContainingDeclaration(), getModality(), getVisibility(), getKind(),
                          getValueParameters(), getContextReceiverParameters(), getExtensionReceiverParameter(), getReturnType()!!, null)

    protected open fun doSubstitute(configuration: CopyConfiguration): FunctionDescriptor? {
        val wereChanges = BooleanArray(1)
        val resultAnnotations = configuration.additionalAnnotations?.let { composeAnnotations(annotations, it) } ?: annotations
        val substitutedDescriptor = createSubstitutedCopy(
            configuration.newOwner, configuration.original, configuration.kind, configuration.name, resultAnnotations,
            getSourceToUseForCopy(configuration.preserveSourceElement, configuration.original)
        )
        val unsubstitutedTypeParameters = configuration.newTypeParameters ?: getTypeParameters()
        wereChanges[0] = wereChanges[0].or(unsubstitutedTypeParameters.isNotEmpty())
        val substitutedTypeParameters = ArrayList<TypeParameterDescriptor>(unsubstitutedTypeParameters.size)
        val substitutor = DescriptorSubstitutor.substituteTypeParameters(
            unsubstitutedTypeParameters, configuration.substitution, substitutedDescriptor, substitutedTypeParameters, wereChanges
        ) ?: return null
        val substitutedContextReceiverParameters = ArrayList<ReceiverParameterDescriptor>()
        if (configuration.newContextReceiverParameters.isNotEmpty()) {
            var index = 0
            for (newContextReceiverParameter in configuration.newContextReceiverParameters) {
                val substitutedContextReceiverType = substitutor.substitute(newContextReceiverParameter.getType(), Variance.IN_VARIANCE) ?: return null
                val substitutedContextReceiverParameter = DescriptorFactory.createContextReceiverParameterForCallable(
                    substitutedDescriptor, substitutedContextReceiverType,
                    (newContextReceiverParameter.getValue() as ImplicitContextReceiver).customLabelName,
                    newContextReceiverParameter.annotations, index++
                )
                substitutedContextReceiverParameters.add(substitutedContextReceiverParameter!!)
                wereChanges[0] = wereChanges[0].or(substitutedContextReceiverType !== newContextReceiverParameter.getType())
            }
        }
        var substitutedReceiverParameter: ReceiverParameterDescriptor? = null
        val extensionReceiver = configuration.newExtensionReceiverParameter
        if (extensionReceiver != null) {
            val substitutedExtensionReceiverType = substitutor.substitute(extensionReceiver.getType(), Variance.IN_VARIANCE) ?: return null
            substitutedReceiverParameter = ReceiverParameterDescriptorImpl(
                substitutedDescriptor, ExtensionReceiver(substitutedDescriptor, substitutedExtensionReceiverType, extensionReceiver.getValue()),
                extensionReceiver.annotations
            )
            wereChanges[0] = wereChanges[0].or(substitutedExtensionReceiverType !== extensionReceiver.getType())
        }
        var substitutedExpectedThis: ReceiverParameterDescriptor? = null
        val dispatchReceiver = configuration.dispatchReceiverParameter
        if (dispatchReceiver != null) {
            // A fake override's receiver remains Base, not Derived, to avoid introducing a smart cast.
            substitutedExpectedThis = dispatchReceiver.substitute(substitutor) ?: return null
            wereChanges[0] = wereChanges[0].or(substitutedExpectedThis !== dispatchReceiver)
        }
        val substitutedValueParameters = getSubstitutedValueParameters(
            substitutedDescriptor, configuration.newValueParameterDescriptors, substitutor,
            configuration.dropOriginalInContainingParts, configuration.preserveSourceElement, wereChanges
        ) ?: return null
        val substitutedReturnType = substitutor.substitute(configuration.newReturnType, Variance.OUT_VARIANCE) ?: return null
        wereChanges[0] = wereChanges[0].or(substitutedReturnType !== configuration.newReturnType)
        if (!wereChanges[0] && configuration.justForTypeSubstitution) return this
        substitutedDescriptor.initialize(
            substitutedReceiverParameter, substitutedExpectedThis, substitutedContextReceiverParameters,
            substitutedTypeParameters, substitutedValueParameters, substitutedReturnType, configuration.newModality, configuration.newVisibility
        )
        substitutedDescriptor.setOperator(operatorFlag)
        substitutedDescriptor.setInfix(infixFlag)
        substitutedDescriptor.setExternal(externalFlag)
        substitutedDescriptor.setInline(inlineFlag)
        substitutedDescriptor.setTailrec(tailrecFlag)
        substitutedDescriptor.setSuspend(suspendFlag)
        substitutedDescriptor.setExpect(expectFlag)
        substitutedDescriptor.setActual(actualFlag)
        substitutedDescriptor.setHasStableParameterNames(stableParameterNames)
        substitutedDescriptor.setHiddenToOvercomeSignatureClash(configuration.hiddenToOvercomeSignatureClash)
        substitutedDescriptor.setHiddenForResolutionEverywhereBesideSupercalls(configuration.hiddenForResolutionEverywhereBesideSupercalls)
        substitutedDescriptor.setHasSynthesizedParameterNames(configuration.newHasSynthesizedParameterNames ?: synthesizedParameterNames)
        if (configuration.userDataMap.isNotEmpty() || userDataMap != null) {
            val newMap = configuration.userDataMap
            userDataMap?.forEach { (key, value) -> if (!newMap.containsKey(key)) newMap[key] = value }
            substitutedDescriptor.userDataMap = if (newMap.size == 1)
                singletonUserDataMap(newMap.keys.first(), newMap.values.first()) else newMap
        }
        if (configuration.signatureChange || getInitialSignatureDescriptor() != null) {
            val initialSignature = getInitialSignatureDescriptor() ?: this
            substitutedDescriptor.setInitialSignatureDescriptor(initialSignature.substitute(substitutor))
        }
        if (configuration.copyOverrides && getOriginal().getOverriddenDescriptors().isNotEmpty()) {
            if (configuration.substitution.isEmpty()) {
                val overriddenFunctionsTask = lazyOverriddenFunctionsTask
                if (overriddenFunctionsTask != null) substitutedDescriptor.lazyOverriddenFunctionsTask = overriddenFunctionsTask
                else substitutedDescriptor.setOverriddenDescriptors(getOverriddenDescriptors())
            } else {
                substitutedDescriptor.lazyOverriddenFunctionsTask = {
                    val result = ArrayList<FunctionDescriptor>()
                    for (overriddenFunction in getOverriddenDescriptors()) {
                        result.add(overriddenFunction.substitute(substitutor)!!)
                    }
                    result
                }
            }
        }
        return substitutedDescriptor
    }

    override fun copy(newOwner: DeclarationDescriptor, modality: Modality, visibility: DescriptorVisibility, kind: Kind, copyOverrides: Boolean): FunctionDescriptor =
        newCopyBuilder().setOwner(newOwner).setModality(modality).setVisibility(visibility).setKind(kind).setCopyOverrides(copyOverrides).build()!!
    protected abstract fun createSubstitutedCopy(
        newOwner: DeclarationDescriptor, original: FunctionDescriptor?, kind: Kind, newName: Name?,
        annotations: Annotations, source: SourceElement
    ): FunctionDescriptorImpl
    private fun getSourceToUseForCopy(preserveSource: Boolean, original: FunctionDescriptor?): SourceElement =
        if (preserveSource) (original ?: getOriginal()).getSource() else SourceElement.NO_SOURCE
    override fun <R, D> accept(visitor: DeclarationDescriptorVisitor<R, D>, data: D): R? = visitor.visitFunctionDescriptor(this, data)
    override fun getInitialSignatureDescriptor(): FunctionDescriptor? = initialSignatureDescriptor
    private fun setInitialSignatureDescriptor(initialSignatureDescriptor: FunctionDescriptor?) {
        this.initialSignatureDescriptor = initialSignatureDescriptor
    }
    /** Only for unpublished descriptors. */
    open fun <V> putInUserDataMap(key: UserDataKey<V>, value: Any?) {
        if (userDataMap == null) userDataMap = LinkedHashMap()
        userDataMap!![key] = value
    }

    companion object {
        private fun singletonUserDataMap(key: UserDataKey<*>, value: Any?): MutableMap<UserDataKey<*>, Any?> =
            object : AbstractMutableMap<UserDataKey<*>, Any?>() {
                private val entry = object : MutableMap.MutableEntry<UserDataKey<*>, Any?> {
                    override val key = key
                    override val value = value
                    override fun setValue(newValue: Any?): Any? = throw UnsupportedOperationException()
                    override fun equals(other: Any?): Boolean =
                        other is Map.Entry<*, *> && key == other.key && value == other.value
                    override fun hashCode(): Int = key.hashCode() xor (value?.hashCode() ?: 0)
                    override fun toString(): String = "$key=$value"
                }
                override val entries: MutableSet<MutableMap.MutableEntry<UserDataKey<*>, Any?>> =
                    object : AbstractMutableSet<MutableMap.MutableEntry<UserDataKey<*>, Any?>>() {
                        override val size: Int get() = 1
                        override fun add(element: MutableMap.MutableEntry<UserDataKey<*>, Any?>): Boolean =
                            throw UnsupportedOperationException()
                        override fun iterator(): MutableIterator<MutableMap.MutableEntry<UserDataKey<*>, Any?>> =
                            object : MutableIterator<MutableMap.MutableEntry<UserDataKey<*>, Any?>> {
                                private var available = true
                                override fun hasNext(): Boolean = available
                                override fun next(): MutableMap.MutableEntry<UserDataKey<*>, Any?> {
                                    if (!available) throw NoSuchElementException()
                                    available = false
                                    return entry
                                }
                                override fun remove(): Unit = throw UnsupportedOperationException()
                            }
                    }
                override fun put(key: UserDataKey<*>, value: Any?): Any? = throw UnsupportedOperationException()
            }
        fun getSubstitutedValueParameters(
            substitutedDescriptor: FunctionDescriptor, unsubstitutedValueParameters: List<ValueParameterDescriptor>, substitutor: TypeSubstitutor
        ): List<ValueParameterDescriptor>? =
            getSubstitutedValueParameters(substitutedDescriptor, unsubstitutedValueParameters, substitutor, false, false, null)
        fun getSubstitutedValueParameters(
            substitutedDescriptor: FunctionDescriptor, unsubstitutedValueParameters: List<ValueParameterDescriptor>, substitutor: TypeSubstitutor,
            dropOriginal: Boolean, preserveSourceElement: Boolean, wereChanges: BooleanArray?
        ): List<ValueParameterDescriptor>? {
            val result = ArrayList<ValueParameterDescriptor>(unsubstitutedValueParameters.size)
            for (unsubstitutedValueParameter in unsubstitutedValueParameters) {
                val substitutedType = substitutor.substitute(unsubstitutedValueParameter.getType(), Variance.IN_VARIANCE)
                val varargElementType = unsubstitutedValueParameter.varargElementType
                val substituteVarargElementType = varargElementType?.let { substitutor.substitute(it, Variance.IN_VARIANCE) }
                if (substitutedType == null) return null
                if (substitutedType !== unsubstitutedValueParameter.getType() || varargElementType !== substituteVarargElementType)
                    if (wereChanges != null) wereChanges[0] = true
                var destructuringVariablesAction: (() -> List<VariableDescriptor>)? = null
                if (unsubstitutedValueParameter is ValueParameterDescriptorImpl.WithDestructuringDeclaration) {
                    val destructuringVariables = unsubstitutedValueParameter.destructuringVariables
                    destructuringVariablesAction = { destructuringVariables }
                }
                result.add(ValueParameterDescriptorImpl.createWithDestructuringDeclarations(
                    substitutedDescriptor, if (dropOriginal) null else unsubstitutedValueParameter,
                    unsubstitutedValueParameter.index, unsubstitutedValueParameter.annotations, unsubstitutedValueParameter.getName(),
                    substitutedType, unsubstitutedValueParameter.declaresDefaultValue(), unsubstitutedValueParameter.isCrossinline,
                    unsubstitutedValueParameter.isNoinline, substituteVarargElementType,
                    if (preserveSourceElement) unsubstitutedValueParameter.getSource() else SourceElement.NO_SOURCE,
                    destructuringVariablesAction
                ))
            }
            return result
        }
    }
}

var FunctionDescriptorImpl.isOperator: Boolean get() = isOperator(); set(value) = setOperator(value)
var FunctionDescriptorImpl.isInfix: Boolean get() = isInfix(); set(value) = setInfix(value)
var FunctionDescriptorImpl.isExternal: Boolean get() = isExternal(); set(value) = setExternal(value)
var FunctionDescriptorImpl.isInline: Boolean get() = isInline(); set(value) = setInline(value)
var FunctionDescriptorImpl.isTailrec: Boolean get() = isTailrec(); set(value) = setTailrec(value)
var FunctionDescriptorImpl.isExpect: Boolean get() = isExpect(); set(value) = setExpect(value)
var FunctionDescriptorImpl.isActual: Boolean get() = isActual(); set(value) = setActual(value)
var FunctionDescriptorImpl.isSuspend: Boolean get() = isSuspend(); set(value) = setSuspend(value)
var FunctionDescriptorImpl.visibility: DescriptorVisibility get() = getVisibility(); set(value) = setVisibility(value)
