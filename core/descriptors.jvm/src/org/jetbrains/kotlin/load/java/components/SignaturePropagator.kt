/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.load.java.components

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.load.java.structure.JavaMethod
import org.jetbrains.kotlin.types.KotlinType

interface SignaturePropagator {
    class PropagatedSignature(
        val returnType: KotlinType, val receiverType: KotlinType?, val valueParameters: List<ValueParameterDescriptor>,
        val typeParameters: List<TypeParameterDescriptor>, val errors: List<String>, private val stableParameterNames: Boolean
    ) {
        fun hasStableParameterNames(): Boolean = stableParameterNames
    }

    fun resolvePropagatedSignature(
        method: JavaMethod, owner: ClassDescriptor, returnType: KotlinType, receiverType: KotlinType?,
        valueParameters: List<ValueParameterDescriptor>, typeParameters: List<TypeParameterDescriptor>
    ): PropagatedSignature

    fun reportSignatureErrors(descriptor: CallableMemberDescriptor, signatureErrors: List<String>)

    companion object {
        // This policy is present in upstream; it is not a replacement for an overridability provider.
        val DO_NOTHING: SignaturePropagator = object : SignaturePropagator {
            override fun resolvePropagatedSignature(
                method: JavaMethod, owner: ClassDescriptor, returnType: KotlinType, receiverType: KotlinType?,
                valueParameters: List<ValueParameterDescriptor>, typeParameters: List<TypeParameterDescriptor>
            ): PropagatedSignature =
                PropagatedSignature(returnType, receiverType, valueParameters, typeParameters, emptyList(), false)

            override fun reportSignatureErrors(descriptor: CallableMemberDescriptor, signatureErrors: List<String>) {
                throw UnsupportedOperationException("Should not be called")
            }
        }
    }
}
