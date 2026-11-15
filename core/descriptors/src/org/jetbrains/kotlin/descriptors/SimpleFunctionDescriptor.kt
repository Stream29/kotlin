/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.descriptors

import org.jetbrains.kotlin.mpp.SimpleFunctionSymbolMarker

// Translated-unverified.
interface SimpleFunctionDescriptor : FunctionDescriptor, SimpleFunctionSymbolMarker {
    override fun copy(newOwner: DeclarationDescriptor, modality: Modality, visibility: DescriptorVisibility,
                      kind: CallableMemberDescriptor.Kind, copyOverrides: Boolean): SimpleFunctionDescriptor
    override fun getOriginal(): SimpleFunctionDescriptor
    override fun newCopyBuilder(): FunctionDescriptor.CopyBuilder<out SimpleFunctionDescriptor?>
}

val SimpleFunctionDescriptor.original: SimpleFunctionDescriptor get() = getOriginal()
