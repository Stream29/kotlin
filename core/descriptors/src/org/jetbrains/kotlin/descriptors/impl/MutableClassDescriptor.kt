/*
 * Copyright 2000-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin.descriptors.impl

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.types.*
import org.jetbrains.kotlin.types.checker.KotlinTypeRefiner

// Translated-unverified.
open class MutableClassDescriptor(
    containingDeclaration: DeclarationDescriptor, kind: ClassKind, isInner: Boolean, isExternal: Boolean,
    name: Name, source: SourceElement, private val storageManager: StorageManager
) : ClassDescriptorBase(storageManager, containingDeclaration, name, source, isExternal) {
    private val classKind = kind
    private val inner = isInner
    private var classModality: Modality? = null
    private var classVisibility: DescriptorVisibility? = null
    private var constructor: TypeConstructor? = null
    private var typeParameters: List<TypeParameterDescriptor>? = null
    private val supertypes = ArrayList<KotlinType>()
    init { assert(kind != ClassKind.OBJECT) { "Fix isCompanionObject()" } }
    override fun getCompanionObjectDescriptor(): ClassDescriptor? = null
    override val annotations: Annotations get() = Annotations.EMPTY
    open fun setModality(modality: Modality) {
        assert(modality != Modality.SEALED) { "Implement getSealedSubclasses() for this class: ${this::class}" }
        classModality = modality
    }
    override fun getModality(): Modality = classModality!!
    override fun getKind(): ClassKind = classKind
    open fun setVisibility(visibility: DescriptorVisibility) { classVisibility = visibility }
    override fun getVisibility(): DescriptorVisibility = classVisibility!!
    override fun isInner(): Boolean = inner
    override fun isData(): Boolean = false
    override fun isInline(): Boolean = false
    override fun isFun(): Boolean = false
    override fun isValue(): Boolean = false
    override fun isCompanionObject(): Boolean = false
    override fun isExpect(): Boolean = false
    override fun isActual(): Boolean = false
    override fun getTypeConstructor(): TypeConstructor = constructor!!
    open fun addSupertype(supertype: KotlinType) {
        assert(!supertype.isError) { "Error types must be filtered out in DescriptorResolver" }
        if (TypeUtils.getClassDescriptor(supertype) != null) supertypes.add(supertype)
    }
    override fun getConstructors(): Set<ClassConstructorDescriptor> = emptySet()
    override fun getUnsubstitutedPrimaryConstructor(): ClassConstructorDescriptor? = null
    open fun setTypeParameterDescriptors(typeParameters: List<TypeParameterDescriptor>) {
        if (this.typeParameters != null) throw IllegalStateException("Type parameters are already set for ${getName()}")
        this.typeParameters = ArrayList(typeParameters)
    }
    override fun getDeclaredTypeParameters(): List<TypeParameterDescriptor> = typeParameters!!
    open fun createTypeConstructor() {
        assert(constructor == null) { constructor.toString() }
        constructor = ClassTypeConstructorImpl(this, typeParameters!!, supertypes, storageManager)
        for (function in getConstructors()) (function as ClassConstructorDescriptorImpl).setReturnType(getDefaultType())
    }
    override fun getUnsubstitutedMemberScope(kotlinTypeRefiner: KotlinTypeRefiner): MemberScope = MemberScope.Empty
    override fun getStaticScope(): MemberScope = MemberScope.Empty
    override fun getSealedSubclasses(): Collection<ClassDescriptor> = emptyList()
    override fun getValueClassRepresentation(): ValueClassRepresentation<SimpleType>? = null
    override fun toString(): String = DeclarationDescriptorImpl.toString(this)
}

var MutableClassDescriptor.modality: Modality get() = getModality(); set(value) = setModality(value)
var MutableClassDescriptor.visibility: DescriptorVisibility get() = getVisibility(); set(value) = setVisibility(value)
