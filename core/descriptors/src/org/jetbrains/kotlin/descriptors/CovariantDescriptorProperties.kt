/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin.descriptors

// Kotlin declarations refining a former Java getter need the refined property type too.
val DeclarationDescriptorNonRoot.containingDeclaration get() = getContainingDeclaration()
val DeclarationDescriptorWithSource.original get() = getOriginal()
val ParameterDescriptor.original get() = getOriginal()
val ValueParameterDescriptor.original get() = getOriginal()
val ValueParameterDescriptor.containingDeclaration get() = getContainingDeclaration()
val ValueParameterDescriptor.overriddenDescriptors get() = getOverriddenDescriptors()
val ClassConstructorDescriptor.original get() = getOriginal()
val ClassConstructorDescriptor.containingDeclaration get() = getContainingDeclaration()
val TypeAliasDescriptor.original get() = getOriginal()
val PackageFragmentDescriptor.containingDeclaration get() = getContainingDeclaration()
val PackageViewDescriptor.containingDeclaration get() = getContainingDeclaration()
