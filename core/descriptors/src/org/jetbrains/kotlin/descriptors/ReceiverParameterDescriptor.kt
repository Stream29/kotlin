/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.descriptors

import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValue
import org.jetbrains.kotlin.types.TypeSubstitutor

// Translated-unverified.
interface ReceiverParameterDescriptor : ParameterDescriptor {
    fun getValue(): ReceiverValue
    override fun substitute(substitutor: TypeSubstitutor): ReceiverParameterDescriptor?
    fun copy(newOwner: DeclarationDescriptor): ReceiverParameterDescriptor
}

val ReceiverParameterDescriptor.value: ReceiverValue get() = getValue()
