/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.resolve

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.renderer.AnnotationArgumentsRenderingPolicy
import org.jetbrains.kotlin.renderer.DescriptorRenderer
import org.jetbrains.kotlin.renderer.DescriptorRendererModifier

// Translated-unverified.
class MemberComparator private constructor() : Comparator<DeclarationDescriptor> {
    class NameAndTypeMemberComparator private constructor() : Comparator<DeclarationDescriptor> {
        override fun compare(a: DeclarationDescriptor, b: DeclarationDescriptor): Int = compareInternal(a, b) ?: 0
        companion object {
            val INSTANCE = NameAndTypeMemberComparator()
            private fun getDeclarationPriority(descriptor: DeclarationDescriptor): Int = when {
                DescriptorUtils.isEnumEntry(descriptor) -> 8
                descriptor is ConstructorDescriptor -> 7
                descriptor is PropertyDescriptor -> if (descriptor.getExtensionReceiverParameter() == null) 6 else 5
                descriptor is FunctionDescriptor -> if (descriptor.getExtensionReceiverParameter() == null) 4 else 3
                descriptor is ClassDescriptor -> 2
                descriptor is TypeAliasDescriptor -> 1
                else -> 0
            }
            internal fun compareInternal(a: DeclarationDescriptor, b: DeclarationDescriptor): Int? {
                val priorities = getDeclarationPriority(b) - getDeclarationPriority(a)
                if (priorities != 0) return priorities
                if (DescriptorUtils.isEnumEntry(a) && DescriptorUtils.isEnumEntry(b)) return 0
                val names = a.getName().compareTo(b.getName())
                if (names != 0) return names
                return null
            }
        }
    }
    override fun compare(a: DeclarationDescriptor, b: DeclarationDescriptor): Int {
        NameAndTypeMemberComparator.compareInternal(a, b)?.let { return it }
        if (a is TypeAliasDescriptor && b is TypeAliasDescriptor) {
            val diff = RENDERER.renderType(a.underlyingType).compareTo(RENDERER.renderType(b.underlyingType))
            if (diff != 0) return diff
        } else if (a is CallableDescriptor && b is CallableDescriptor) {
            val aReceiver = a.getExtensionReceiverParameter()
            val bReceiver = b.getExtensionReceiverParameter()
            assert((aReceiver != null) == (bReceiver != null))
            if (aReceiver != null) {
                val diff = RENDERER.renderType(aReceiver.getType()).compareTo(RENDERER.renderType(bReceiver!!.getType()))
                if (diff != 0) return diff
            }
            val aValues = a.getValueParameters()
            val bValues = b.getValueParameters()
            for (i in 0 until minOf(aValues.size, bValues.size)) {
                val diff = RENDERER.renderType(aValues[i].getType()).compareTo(RENDERER.renderType(bValues[i].getType()))
                if (diff != 0) return diff
            }
            if (aValues.size != bValues.size) return aValues.size - bValues.size
            val aTypes = a.getTypeParameters()
            val bTypes = b.getTypeParameters()
            for (i in 0 until minOf(aTypes.size, bTypes.size)) {
                val aBounds = aTypes[i].getUpperBounds()
                val bBounds = bTypes[i].getUpperBounds()
                if (aBounds.size != bBounds.size) return aBounds.size - bBounds.size
                for (j in aBounds.indices) {
                    val diff = RENDERER.renderType(aBounds[j]).compareTo(RENDERER.renderType(bBounds[j]))
                    if (diff != 0) return diff
                }
            }
            if (aTypes.size != bTypes.size) return aTypes.size - bTypes.size
            if (a is CallableMemberDescriptor && b is CallableMemberDescriptor) {
                val diff = a.getKind().ordinal - b.getKind().ordinal
                if (diff != 0) return diff
            }
        } else if (a is ClassDescriptor && b is ClassDescriptor) {
            if (a.getKind().ordinal != b.getKind().ordinal) return a.getKind().ordinal - b.getKind().ordinal
            if (a.isCompanionObject() != b.isCompanionObject()) return if (a.isCompanionObject()) 1 else -1
        } else throw AssertionError("Unsupported pair of descriptors:\n'$a' Class: ${a::class}\n$b' Class: ${b::class}")
        val renderDiff = RENDERER.render(a).compareTo(RENDERER.render(b))
        if (renderDiff != 0) return renderDiff
        return DescriptorUtils.getContainingModule(a).getName().compareTo(DescriptorUtils.getContainingModule(b).getName())
    }
    companion object {
        val INSTANCE = MemberComparator()
        private val RENDERER = DescriptorRenderer.withOptions {
            withDefinedIn = false
            verbose = true
            annotationArgumentsRenderingPolicy = AnnotationArgumentsRenderingPolicy.UNLESS_EMPTY
            modifiers = DescriptorRendererModifier.ALL
        }
    }
}
