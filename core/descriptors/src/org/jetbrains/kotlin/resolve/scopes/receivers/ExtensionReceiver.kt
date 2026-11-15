/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.resolve.scopes.receivers

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.types.KotlinType

// Translated-unverified. Receiver of a pure `this` expression in a lambda with receiver.
open class ExtensionReceiver(
    callableDescriptor: CallableDescriptor, receiverType: KotlinType, original: ReceiverValue?
) : AbstractReceiverValue(receiverType, original), ImplicitReceiver {
    private val descriptor = callableDescriptor
    override val declarationDescriptor: CallableDescriptor get() = descriptor
    override fun replaceType(newType: KotlinType): ReceiverValue = ExtensionReceiver(descriptor, newType, getOriginal())
    override fun toString(): String = "${getType()}: Ext {$descriptor}"
}
