/*
 * Copyright 2010-2017 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.load.java.descriptors

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.CallableDescriptor.UserDataKey
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor.Kind
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.ClassConstructorDescriptorImpl
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.DescriptorFactory
import org.jetbrains.kotlin.types.KotlinType

open class JavaClassConstructorDescriptor protected constructor(
    containingDeclaration: ClassDescriptor, original: JavaClassConstructorDescriptor?,
    annotations: Annotations, isPrimary: Boolean, kind: Kind, source: SourceElement
) : ClassConstructorDescriptorImpl(containingDeclaration, original, annotations, isPrimary, kind, source),
    JavaCallableMemberDescriptor {
    private var stableParameterNames: Boolean? = null
    private var synthesizedParameterNames: Boolean? = null

    override fun hasStableParameterNames(): Boolean {
        assert(stableParameterNames != null) { "hasStableParameterNames was not set: $this" }
        return stableParameterNames!!
    }

    override fun setHasStableParameterNames(hasStableParameterNames: Boolean) {
        stableParameterNames = hasStableParameterNames
    }

    override fun hasSynthesizedParameterNames(): Boolean {
        assert(synthesizedParameterNames != null) { "hasSynthesizedParameterNames was not set: $this" }
        return synthesizedParameterNames!!
    }

    override fun setHasSynthesizedParameterNames(hasSynthesizedParameterNames: Boolean) {
        synthesizedParameterNames = hasSynthesizedParameterNames
    }

    override fun createSubstitutedCopy(
        newOwner: DeclarationDescriptor, original: FunctionDescriptor?, kind: Kind, newName: Name?,
        annotations: Annotations, source: SourceElement
    ): JavaClassConstructorDescriptor {
        if (kind != Kind.DECLARATION && kind != Kind.SYNTHESIZED) {
            throw IllegalStateException(
                "Attempt at creating a constructor that is not a declaration: \ncopy from: $this\nnewOwner: $newOwner\nkind: $kind"
            )
        }
        assert(newName == null) { "Attempt to rename constructor: $this" }
        val result = createDescriptor(newOwner as ClassDescriptor, original as JavaClassConstructorDescriptor?, kind, source, annotations)
        result.setHasStableParameterNames(hasStableParameterNames())
        result.setHasSynthesizedParameterNames(hasSynthesizedParameterNames())
        return result
    }

    protected open fun createDescriptor(
        newOwner: ClassDescriptor, original: JavaClassConstructorDescriptor?, kind: Kind,
        sourceElement: SourceElement, annotations: Annotations
    ): JavaClassConstructorDescriptor =
        JavaClassConstructorDescriptor(newOwner, original, annotations, isPrimary(), kind, sourceElement)

    override fun enhance(
        enhancedReceiverType: KotlinType?, enhancedValueParameterTypes: List<KotlinType>,
        enhancedReturnType: KotlinType, additionalUserData: Pair<UserDataKey<*>, Any?>?
    ): JavaClassConstructorDescriptor {
        val enhanced = createSubstitutedCopy(getContainingDeclaration(), null, getKind(), null, annotations, getSource())
        val enhancedReceiver = enhancedReceiverType?.let {
            DescriptorFactory.createExtensionReceiverParameterForCallable(enhanced, it, Annotations.EMPTY)
        }
        enhanced.initialize(
            enhancedReceiver, getDispatchReceiverParameter(), emptyList(), getTypeParameters(),
            copyValueParameters(enhancedValueParameterTypes, getValueParameters(), enhanced),
            enhancedReturnType, getModality(), getVisibility()
        )
        if (additionalUserData != null) enhanced.putInUserDataMap(additionalUserData.first, additionalUserData.second)
        return enhanced
    }

    companion object {
        fun createJavaConstructor(
            containingDeclaration: ClassDescriptor, annotations: Annotations, isPrimary: Boolean, source: SourceElement
        ): JavaClassConstructorDescriptor =
            JavaClassConstructorDescriptor(containingDeclaration, null, annotations, isPrimary, Kind.DECLARATION, source)
    }
}
