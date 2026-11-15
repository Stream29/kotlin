/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.load.java.descriptors

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.CallableDescriptor.UserDataKey
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor.Kind
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.PropertyDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.PropertyGetterDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.PropertySetterDescriptorImpl
import org.jetbrains.kotlin.load.java.typeEnhancement.hasEnhancedNullability
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.DescriptorFactory
import org.jetbrains.kotlin.types.KotlinType

open class JavaPropertyDescriptor protected constructor(
    containingDeclaration: DeclarationDescriptor, annotations: Annotations, modality: Modality,
    visibility: DescriptorVisibility, isVar: Boolean, name: Name, source: SourceElement,
    original: PropertyDescriptor?, kind: Kind, private val isStaticFinal: Boolean,
    private val singleUserData: Pair<UserDataKey<*>, Any?>?
) : PropertyDescriptorImpl(
    containingDeclaration, original, annotations, modality, visibility, isVar, name, kind, source,
    false, false, false, false, false, false
), JavaCallableMemberDescriptor {
    private var inputType: KotlinType? = null

    override fun createSubstitutedCopy(
        newOwner: DeclarationDescriptor, newModality: Modality, newVisibility: DescriptorVisibility,
        original: PropertyDescriptor?, kind: Kind, newName: Name, source: SourceElement
    ): PropertyDescriptorImpl = JavaPropertyDescriptor(
        newOwner, annotations, newModality, newVisibility, isVar(), newName, source, original, kind,
        isStaticFinal, singleUserData
    )

    override fun hasSynthesizedParameterNames(): Boolean = false

    override fun enhance(
        enhancedReceiverType: KotlinType?, enhancedValueParameterTypes: List<KotlinType>,
        enhancedReturnType: KotlinType, additionalUserData: Pair<UserDataKey<*>, Any?>?
    ): JavaCallableMemberDescriptor {
        val enhancedOriginal = if (getOriginal() === this) null else getOriginal()
        val enhanced = JavaPropertyDescriptor(
            getContainingDeclaration(), annotations, getModality(), getVisibility(), isVar(), getName(),
            getSource(), enhancedOriginal, getKind(), isStaticFinal, additionalUserData
        )
        val getter = getGetter()
        val newGetter = getter?.let {
            PropertyGetterDescriptorImpl(
                enhanced, it.annotations, it.getModality(), it.getVisibility(), it.isDefault(),
                it.isExternal(), it.isInline(), getKind(), enhancedOriginal?.getGetter(), it.getSource()
            ).also { copy ->
                copy.setInitialSignatureDescriptor(it.getInitialSignatureDescriptor())
                copy.initialize(enhancedReturnType)
            }
        }
        val setter = getSetter()
        val newSetter = setter?.let {
            PropertySetterDescriptorImpl(
                enhanced, it.annotations, it.getModality(), it.getVisibility(), it.isDefault(),
                it.isExternal(), it.isInline(), getKind(), enhancedOriginal?.getSetter(), it.getSource()
            ).also { copy ->
                // Preserve the upstream self-read, rather than changing enhancement semantics.
                copy.setInitialSignatureDescriptor(copy.getInitialSignatureDescriptor())
                copy.initialize(it.getValueParameters()[0])
            }
        }
        enhanced.initialize(newGetter, newSetter, getBackingField(), getDelegateField())
        enhanced.setSetterProjectedOut(isSetterProjectedOut())
        if (compileTimeInitializerFactory != null) {
            enhanced.setCompileTimeInitializer(compileTimeInitializer, compileTimeInitializerFactory!!)
        }
        enhanced.setOverriddenDescriptors(getOverriddenDescriptors())
        val enhancedReceiver = enhancedReceiverType?.let {
            DescriptorFactory.createExtensionReceiverParameterForCallable(this, it, Annotations.EMPTY)
        }
        enhanced.setType(enhancedReturnType, getTypeParameters(), getDispatchReceiverParameter(), enhancedReceiver, emptyList())
        return enhanced
    }

    override fun isConst(): Boolean {
        val type = getType()
        return isStaticFinal && ConstUtil.canBeUsedForConstVal(type) &&
                (!type.hasEnhancedNullability() || KotlinBuiltIns.isString(type))
    }

    override fun setInType(inType: KotlinType) {
        inputType = inType
    }

    override fun getInType(): KotlinType? = getSetter()?.getValueParameters()?.get(0)?.getType() ?: inputType

    @Suppress("UNCHECKED_CAST")
    override fun <V> getUserData(key: UserDataKey<V>): V? =
        if (singleUserData != null && singleUserData.first == key) singleUserData.second as V? else null

    companion object {
        fun create(
            containingDeclaration: DeclarationDescriptor, annotations: Annotations, modality: Modality,
            visibility: DescriptorVisibility, isVar: Boolean, name: Name, source: SourceElement, isStaticFinal: Boolean
        ): JavaPropertyDescriptor = JavaPropertyDescriptor(
            containingDeclaration, annotations, modality, visibility, isVar, name, source, null, Kind.DECLARATION, isStaticFinal, null
        )
    }
}
