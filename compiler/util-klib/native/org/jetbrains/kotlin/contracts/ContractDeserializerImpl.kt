/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.contracts

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ContractProvider
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.metadata.ProtoBuf
import org.jetbrains.kotlin.metadata.deserialization.TypeTable
import org.jetbrains.kotlin.serialization.deserialization.ContractDeserializer
import org.jetbrains.kotlin.serialization.deserialization.DeserializationConfiguration
import org.jetbrains.kotlin.serialization.deserialization.TypeDeserializer
import org.jetbrains.kotlin.storage.StorageManager

class ContractDeserializerImpl(
    val configuration: DeserializationConfiguration,
    val storageManager: StorageManager,
) : ContractDeserializer {
    override fun deserializeContractFromFunction(
        proto: ProtoBuf.Function,
        ownerFunction: FunctionDescriptor,
        typeTable: TypeTable,
        typeDeserializer: TypeDeserializer,
    ): Pair<CallableDescriptor.UserDataKey<*>, ContractProvider>? = null
}
