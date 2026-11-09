/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.descriptors.impl

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValue
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.shouldBeUpdated

// Translated-unverified.
open class ReceiverParameterDescriptorImpl(
    containingDeclaration: DeclarationDescriptor, value: ReceiverValue, annotations: Annotations, name: Name
) : AbstractReceiverParameterDescriptor(annotations, name) {
    private val containingDescriptor = containingDeclaration
    private var receiverValue = value
    constructor(containingDeclaration: DeclarationDescriptor, value: ReceiverValue, annotations: Annotations) :
        this(containingDeclaration, value, annotations, SpecialNames.THIS)
    override fun getValue(): ReceiverValue = receiverValue
    override fun getContainingDeclaration(): DeclarationDescriptor = containingDescriptor
    override fun copy(newOwner: DeclarationDescriptor): ReceiverParameterDescriptor =
        ReceiverParameterDescriptorImpl(newOwner, receiverValue, annotations)
    open fun setOutType(outType: KotlinType) {
        assert(receiverValue.getType().shouldBeUpdated())
        receiverValue = receiverValue.replaceType(outType)
    }
}
