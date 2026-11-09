/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.descriptors

import org.jetbrains.kotlin.mpp.CallableSymbolMarker
import org.jetbrains.kotlin.types.KotlinType

// Translated-unverified.
interface CallableDescriptor : DeclarationDescriptorWithVisibility, DeclarationDescriptorNonRoot,
    Substitutable<CallableDescriptor>, CallableSymbolMarker {
    fun getContextReceiverParameters(): List<ReceiverParameterDescriptor>
    fun getExtensionReceiverParameter(): ReceiverParameterDescriptor?
    fun getDispatchReceiverParameter(): ReceiverParameterDescriptor?
    fun getTypeParameters(): List<TypeParameterDescriptor>
    /** Null for an incompletely initialized descriptor or after an error. */
    fun getReturnType(): KotlinType?
    override fun getOriginal(): CallableDescriptor
    fun getValueParameters(): List<ValueParameterDescriptor>
    /** Platform definitions may have names unsuitable for named arguments. */
    fun hasStableParameterNames(): Boolean
    /** Missing platform names may be synthesized as p0, p1, etc. */
    fun hasSynthesizedParameterNames(): Boolean
    fun getOverriddenDescriptors(): Collection<CallableDescriptor>
    interface UserDataKey<V>
    fun <V> getUserData(key: UserDataKey<V>): V?
}

val CallableDescriptor.contextReceiverParameters: List<ReceiverParameterDescriptor> get() = getContextReceiverParameters()
val CallableDescriptor.extensionReceiverParameter: ReceiverParameterDescriptor? get() = getExtensionReceiverParameter()
val CallableDescriptor.dispatchReceiverParameter: ReceiverParameterDescriptor? get() = getDispatchReceiverParameter()
val CallableDescriptor.typeParameters: List<TypeParameterDescriptor> get() = getTypeParameters()
val CallableDescriptor.returnType: KotlinType? get() = getReturnType()
val CallableDescriptor.original: CallableDescriptor get() = getOriginal()
val CallableDescriptor.valueParameters: List<ValueParameterDescriptor> get() = getValueParameters()
val CallableDescriptor.overriddenDescriptors: Collection<CallableDescriptor> get() = getOverriddenDescriptors()
