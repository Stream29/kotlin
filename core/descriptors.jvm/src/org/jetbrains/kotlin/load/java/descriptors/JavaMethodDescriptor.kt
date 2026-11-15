/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.load.java.descriptors

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.CallableDescriptor.UserDataKey
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor.Kind
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.SimpleFunctionDescriptorImpl
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.DescriptorFactory
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.util.OperatorChecks

open class JavaMethodDescriptor protected constructor(
    containingDeclaration: DeclarationDescriptor,
    original: SimpleFunctionDescriptor?,
    annotations: Annotations,
    name: Name,
    kind: Kind,
    source: SourceElement,
    private val forRecordComponent: Boolean
) : SimpleFunctionDescriptorImpl(containingDeclaration, original, annotations, name, kind, source),
    JavaCallableMemberDescriptor {
    private enum class ParameterNamesStatus(val isStable: Boolean, val isSynthesized: Boolean) {
        NON_STABLE_DECLARED(false, false),
        STABLE_DECLARED(true, false),
        NON_STABLE_SYNTHESIZED(false, true),
        STABLE_SYNTHESIZED(true, true);

        companion object {
            fun get(stable: Boolean, synthesized: Boolean): ParameterNamesStatus =
                if (stable) {
                    if (synthesized) STABLE_SYNTHESIZED else STABLE_DECLARED
                } else {
                    if (synthesized) NON_STABLE_SYNTHESIZED else NON_STABLE_DECLARED
                }
        }
    }

    private var parameterNamesStatus: ParameterNamesStatus? = null

    override fun initialize(
        extensionReceiverParameter: ReceiverParameterDescriptor?,
        dispatchReceiverParameter: ReceiverParameterDescriptor?,
        contextReceiverParameters: List<ReceiverParameterDescriptor>,
        typeParameters: List<TypeParameterDescriptor>,
        unsubstitutedValueParameters: List<ValueParameterDescriptor>,
        unsubstitutedReturnType: KotlinType?,
        modality: Modality?,
        visibility: DescriptorVisibility,
        userData: Map<out UserDataKey<*>, Any?>?
    ): SimpleFunctionDescriptorImpl {
        val descriptor = super.initialize(
            extensionReceiverParameter, dispatchReceiverParameter, contextReceiverParameters, typeParameters,
            unsubstitutedValueParameters, unsubstitutedReturnType, modality, visibility, userData
        )
        setOperator(OperatorChecks.check(descriptor).isSuccess)
        return descriptor
    }

    override fun hasStableParameterNames(): Boolean {
        assert(parameterNamesStatus != null) { "Parameter names status was not set: $this" }
        return parameterNamesStatus!!.isStable
    }

    override fun hasSynthesizedParameterNames(): Boolean {
        assert(parameterNamesStatus != null) { "Parameter names status was not set: $this" }
        return parameterNamesStatus!!.isSynthesized
    }

    fun setParameterNamesStatus(hasStableParameterNames: Boolean, hasSynthesizedParameterNames: Boolean) {
        parameterNamesStatus = ParameterNamesStatus.get(hasStableParameterNames, hasSynthesizedParameterNames)
    }

    fun isForRecordComponent(): Boolean = forRecordComponent

    override fun createSubstitutedCopy(
        newOwner: DeclarationDescriptor, original: FunctionDescriptor?, kind: Kind, newName: Name?,
        annotations: Annotations, source: SourceElement
    ): JavaMethodDescriptor {
        val result = JavaMethodDescriptor(
            newOwner, original as SimpleFunctionDescriptor?, annotations, newName ?: getName(), kind, source, forRecordComponent
        )
        result.setParameterNamesStatus(hasStableParameterNames(), hasSynthesizedParameterNames())
        return result
    }

    override fun enhance(
        enhancedReceiverType: KotlinType?, enhancedValueParameterTypes: List<KotlinType>,
        enhancedReturnType: KotlinType, additionalUserData: Pair<UserDataKey<*>, Any?>?
    ): JavaMethodDescriptor {
        val enhancedValueParameters = copyValueParameters(enhancedValueParameterTypes, getValueParameters(), this)
        val enhancedReceiver = enhancedReceiverType?.let {
            DescriptorFactory.createExtensionReceiverParameterForCallable(this, it, Annotations.EMPTY)
        }
        val enhancedMethod = newCopyBuilder()
            .setValueParameters(enhancedValueParameters)
            .setReturnType(enhancedReturnType)
            .setExtensionReceiverParameter(enhancedReceiver)
            .setDropOriginalInContainingParts()
            .setPreserveSourceElement()
            .build() as JavaMethodDescriptor?
        assert(enhancedMethod != null) { "null after substitution while enhancing $this" }
        if (additionalUserData != null) {
            enhancedMethod!!.putInUserDataMap(additionalUserData.first, additionalUserData.second)
        }
        return enhancedMethod!!
    }

    companion object {
        val ORIGINAL_VALUE_PARAMETER_FOR_EXTENSION_RECEIVER = object : UserDataKey<ValueParameterDescriptor> {}
        val HAS_ERASED_VALUE_PARAMETERS = object : UserDataKey<Boolean> {}

        fun createJavaMethod(
            containingDeclaration: DeclarationDescriptor, annotations: Annotations, name: Name,
            source: SourceElement, isForRecordComponent: Boolean
        ): JavaMethodDescriptor = JavaMethodDescriptor(
            containingDeclaration, null, annotations, name, Kind.DECLARATION, source, isForRecordComponent
        )
    }
}

val JavaMethodDescriptor.isForRecordComponent: Boolean get() = isForRecordComponent()
