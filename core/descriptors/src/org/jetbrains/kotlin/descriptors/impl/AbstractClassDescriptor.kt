/*
 * Copyright 2010-2016 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.descriptors.impl

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.descriptorUtil.getKotlinTypeRefiner
import org.jetbrains.kotlin.resolve.scopes.InnerClassesScopeWrapper
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.resolve.scopes.SubstitutingScope
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.types.*
import org.jetbrains.kotlin.types.KotlinTypeFactory.computeExpandedType
import org.jetbrains.kotlin.types.checker.KotlinTypeRefiner

// Translated-unverified.
abstract class AbstractClassDescriptor(storageManager: StorageManager, name: Name) : ModuleAwareClassDescriptor() {
    private val className = name
    protected val lazyDefaultType = storageManager.createLazyValue {
        TypeUtils.makeUnsubstitutedType(this, getUnsubstitutedMemberScope(), ::refineDefaultType)
    }
    private fun refineDefaultType(kotlinTypeRefiner: KotlinTypeRefiner): SimpleType {
        val descriptor = kotlinTypeRefiner.refineDescriptor(this) ?: return lazyDefaultType()
        if (descriptor is TypeAliasDescriptor)
            return descriptor.computeExpandedType(TypeUtils.getDefaultTypeProjections(descriptor.getTypeConstructor().getParameters()))
        if (descriptor is ModuleAwareClassDescriptor) {
            val refinedConstructor = descriptor.getTypeConstructor().refine(kotlinTypeRefiner)
            return TypeUtils.makeUnsubstitutedType(refinedConstructor, descriptor.getUnsubstitutedMemberScope(kotlinTypeRefiner), ::refineDefaultType)
        }
        return descriptor.getDefaultType()
    }
    private val unsubstitutedInnerClassesScope = storageManager.createLazyValue { InnerClassesScopeWrapper(getUnsubstitutedMemberScope()) }
    private val thisAsReceiverParameter = storageManager.createLazyValue { LazyClassReceiverParameterDescriptor(this) }
    override fun getName(): Name = className
    override fun getOriginal(): ClassDescriptor = this
    override fun getUnsubstitutedInnerClassesScope(): MemberScope = unsubstitutedInnerClassesScope()
    override fun getThisAsReceiverParameter(): ReceiverParameterDescriptor = thisAsReceiverParameter()
    override fun getContextReceivers(): List<ReceiverParameterDescriptor> = emptyList()
    override fun getMemberScope(typeArguments: List<TypeProjection>, kotlinTypeRefiner: KotlinTypeRefiner): MemberScope {
        assert(typeArguments.size == getTypeConstructor().getParameters().size) {
            "Illegal number of type arguments: expected ${getTypeConstructor().getParameters().size} but was ${typeArguments.size} for ${getTypeConstructor()} ${getTypeConstructor().getParameters()}"
        }
        if (typeArguments.isEmpty()) return getUnsubstitutedMemberScope(kotlinTypeRefiner)
        val substitutor = TypeConstructorSubstitution.create(getTypeConstructor(), typeArguments).buildSubstitutor()
        return SubstitutingScope(getUnsubstitutedMemberScope(kotlinTypeRefiner), substitutor)
    }
    override fun getMemberScope(typeSubstitution: TypeSubstitution, kotlinTypeRefiner: KotlinTypeRefiner): MemberScope {
        if (typeSubstitution.isEmpty()) return getUnsubstitutedMemberScope(kotlinTypeRefiner)
        return SubstitutingScope(getUnsubstitutedMemberScope(kotlinTypeRefiner), TypeSubstitutor.create(typeSubstitution))
    }
    override fun getMemberScope(typeArguments: List<TypeProjection>): MemberScope =
        getMemberScope(typeArguments, DescriptorUtils.getContainingModule(this).getKotlinTypeRefiner())
    override fun getMemberScope(typeSubstitution: TypeSubstitution): MemberScope =
        getMemberScope(typeSubstitution, DescriptorUtils.getContainingModule(this).getKotlinTypeRefiner())
    override fun getUnsubstitutedMemberScope(): MemberScope =
        getUnsubstitutedMemberScope(DescriptorUtils.getContainingModule(this).getKotlinTypeRefiner())
    override fun substitute(substitutor: TypeSubstitutor): ClassDescriptor =
        if (substitutor.isEmpty()) this else LazySubstitutingClassDescriptor(this, substitutor)
    override fun getDefaultType(): SimpleType = lazyDefaultType()
    override fun acceptVoid(visitor: DeclarationDescriptorVisitor<Nothing?, Nothing?>) { visitor.visitClassDescriptor(this, null) }
    override fun <R, D> accept(visitor: DeclarationDescriptorVisitor<R, D>, data: D): R? = visitor.visitClassDescriptor(this, data)
    override fun getDefaultFunctionTypeForSamInterface(): SimpleType? = null
    override fun isDefinitelyNotSamInterface(): Boolean = false
}
