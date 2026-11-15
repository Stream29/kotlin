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
open class ClassDescriptorImpl(
    containingDeclaration: DeclarationDescriptor, name: Name, modality: Modality, kind: ClassKind,
    supertypes: Collection<KotlinType>, source: SourceElement, isExternal: Boolean, storageManager: StorageManager
) : ClassDescriptorBase(storageManager, containingDeclaration, name, source, isExternal) {
    init { assert(modality != Modality.SEALED) { "Implement getSealedSubclasses() for this class: ${this::class}" } }
    private val classModality = modality
    private val classKind = kind
    private val constructor = ClassTypeConstructorImpl(this, emptyList(), supertypes, storageManager)
    private var memberScope: MemberScope? = null
    private var constructors: Set<ClassConstructorDescriptor>? = null
    private var primaryConstructor: ClassConstructorDescriptor? = null
    fun initialize(unsubstitutedMemberScope: MemberScope, constructors: Set<ClassConstructorDescriptor>, primaryConstructor: ClassConstructorDescriptor?) {
        this.memberScope = unsubstitutedMemberScope
        this.constructors = constructors
        this.primaryConstructor = primaryConstructor
    }
    override val annotations: Annotations get() = Annotations.EMPTY
    override fun getTypeConstructor(): TypeConstructor = constructor
    override fun getConstructors(): Collection<ClassConstructorDescriptor> = constructors!!
    override fun getUnsubstitutedMemberScope(kotlinTypeRefiner: KotlinTypeRefiner): MemberScope = memberScope!!
    override fun getStaticScope(): MemberScope = MemberScope.Empty
    override fun getCompanionObjectDescriptor(): ClassDescriptor? = null
    override fun getKind(): ClassKind = classKind
    override fun isCompanionObject(): Boolean = false
    override fun isExpect(): Boolean = false
    override fun isActual(): Boolean = false
    override fun getUnsubstitutedPrimaryConstructor(): ClassConstructorDescriptor? = primaryConstructor
    override fun getModality(): Modality = classModality
    override fun getVisibility(): DescriptorVisibility = DescriptorVisibilities.PUBLIC
    override fun isData(): Boolean = false
    override fun isInline(): Boolean = false
    override fun isFun(): Boolean = false
    override fun isValue(): Boolean = false
    override fun isInner(): Boolean = false
    override fun toString(): String = "class ${getName()}"
    override fun getDeclaredTypeParameters(): List<TypeParameterDescriptor> = emptyList()
    override fun getSealedSubclasses(): Collection<ClassDescriptor> = emptyList()
    override fun getValueClassRepresentation(): ValueClassRepresentation<SimpleType>? = null
}
