/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.konan

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeConstructor

@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.FILE
)
@Retention(AnnotationRetention.BINARY)
annotation class JvmName(val name: String)

internal val TypeConstructor.declarationDescriptor: ClassifierDescriptor?
    get() = getDeclarationDescriptor()

internal val TypeConstructor.supertypes: Collection<KotlinType>
    get() = getSupertypes()

internal val ClassDescriptor.unsubstitutedPrimaryConstructor: ClassConstructorDescriptor?
    get() = getUnsubstitutedPrimaryConstructor()

internal val CallableDescriptor.valueParameters: List<ValueParameterDescriptor>
    get() = getValueParameters()

internal val VariableDescriptor.type: KotlinType
    get() = getType()

internal val DeclarationDescriptor.name: Name
    get() = getName()

internal val DeclarationDescriptor.containingDeclaration: DeclarationDescriptor?
    get() = getContainingDeclaration()
