/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.resolve.scopes.receivers

import org.jetbrains.kotlin.types.KotlinType

// Translated-unverified. The implicit iterator of a for-loop has no expression receiver.
open class TransientReceiver private constructor(type: KotlinType, original: ReceiverValue?) : AbstractReceiverValue(type, original) {
    constructor(type: KotlinType) : this(type, null)
    override fun toString(): String = "{Transient} : ${getType()}"
    override fun replaceType(newType: KotlinType): ReceiverValue = TransientReceiver(newType, getOriginal())
}
