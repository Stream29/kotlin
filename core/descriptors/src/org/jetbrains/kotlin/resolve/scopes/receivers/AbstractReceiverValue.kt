/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.resolve.scopes.receivers

import org.jetbrains.kotlin.types.KotlinType

// Translated-unverified.
abstract class AbstractReceiverValue(protected val receiverType: KotlinType, original: ReceiverValue?) : ReceiverValue {
    private val originalReceiver = original ?: this
    override fun getType(): KotlinType = receiverType
    override fun getOriginal(): ReceiverValue = originalReceiver
}
