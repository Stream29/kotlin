/*
 * Copyright 2010-2017 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.descriptors

import org.jetbrains.kotlin.descriptors.impl.TypeAliasConstructorDescriptor
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.scopes.receivers.*
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.isDynamic
import org.jetbrains.kotlin.util.ModuleVisibilityHelper
import org.jetbrains.kotlin.util.NativeDescriptorServices

// JVM discovery is replaced by required Native host installation; visibility rules are unchanged.
open class DescriptorVisibilities {
    companion object {
        val PRIVATE: DescriptorVisibility = object : DelegatedDescriptorVisibility(Visibilities.Private) {
            private fun hasContainingSourceFile(descriptor: DeclarationDescriptor): Boolean =
                DescriptorUtils.getContainingSourceFile(descriptor) !== SourceFile.NO_SOURCE_FILE
            override fun isVisible(receiver: ReceiverValue?, what: DeclarationDescriptorWithVisibility, from: DeclarationDescriptor,
                                   useSpecialRulesForPrivateSealedConstructors: Boolean): Boolean {
                if (DescriptorUtils.isTopLevelDeclaration(what) && hasContainingSourceFile(from)) return inSameFile(what, from)
                if (what is ConstructorDescriptor) {
                    val classDescriptor = what.getContainingDeclaration()
                    if (useSpecialRulesForPrivateSealedConstructors && DescriptorUtils.isSealedClass(classDescriptor) &&
                        DescriptorUtils.isTopLevelDeclaration(classDescriptor) && from is ConstructorDescriptor &&
                        DescriptorUtils.isTopLevelDeclaration(from.getContainingDeclaration()) && inSameFile(what, from)) return true
                }
                var parent: DeclarationDescriptor? = what
                while (parent != null) {
                    parent = parent.getContainingDeclaration()
                    if (parent is ClassDescriptor && !DescriptorUtils.isCompanionObject(parent) || parent is PackageFragmentDescriptor) break
                }
                if (parent == null) return false
                var fromParent: DeclarationDescriptor? = from
                while (fromParent != null) {
                    if (parent === fromParent) return true
                    if (fromParent is PackageFragmentDescriptor)
                        return parent is PackageFragmentDescriptor && parent.fqName == fromParent.fqName &&
                            DescriptorUtils.areInSameModule(fromParent, parent)
                    fromParent = fromParent.getContainingDeclaration()
                }
                return false
            }
        }
        val PRIVATE_TO_THIS: DescriptorVisibility = object : DelegatedDescriptorVisibility(Visibilities.PrivateToThis) {
            override fun isVisible(receiver: ReceiverValue?, what: DeclarationDescriptorWithVisibility, from: DeclarationDescriptor,
                                   useSpecialRulesForPrivateSealedConstructors: Boolean): Boolean {
                if (PRIVATE.isVisible(receiver, what, from, useSpecialRulesForPrivateSealedConstructors)) {
                    if (receiver === ALWAYS_SUITABLE_RECEIVER) return true
                    if (receiver === IRRELEVANT_RECEIVER) return false
                    val classDescriptor = DescriptorUtils.getParentOfType(what, ClassDescriptor::class)
                    if (classDescriptor != null && receiver is ThisClassReceiver)
                        return receiver.classDescriptor.getOriginal() == classDescriptor.getOriginal()
                }
                return false
            }
        }
        val PROTECTED: DescriptorVisibility = object : DelegatedDescriptorVisibility(Visibilities.Protected) {
            override fun isVisible(receiver: ReceiverValue?, what: DeclarationDescriptorWithVisibility, from: DeclarationDescriptor,
                                   useSpecialRulesForPrivateSealedConstructors: Boolean): Boolean {
                val givenClass = DescriptorUtils.getParentOfType(what, ClassDescriptor::class)
                val fromClass = DescriptorUtils.getParentOfType(from, ClassDescriptor::class, false) ?: return false
                if (givenClass != null && DescriptorUtils.isCompanionObject(givenClass)) {
                    val owner = DescriptorUtils.getParentOfType(givenClass, ClassDescriptor::class)
                    if (owner != null && DescriptorUtils.isSubclass(fromClass, owner)) return true
                }
                val whatDeclaration = DescriptorUtils.unwrapFakeOverrideToAnyDeclaration(what)
                val classDescriptor = DescriptorUtils.getParentOfType(whatDeclaration, ClassDescriptor::class) ?: return false
                if (DescriptorUtils.isSubclass(fromClass, classDescriptor) && doesReceiverFitForProtectedVisibility(receiver, whatDeclaration, fromClass))
                    return true
                return isVisible(receiver, what, fromClass.getContainingDeclaration(), useSpecialRulesForPrivateSealedConstructors)
            }
            @Suppress("DEPRECATION")
            private fun doesReceiverFitForProtectedVisibility(receiver: ReceiverValue?, whatDeclaration: DeclarationDescriptorWithVisibility,
                                                             fromClass: ClassDescriptor): Boolean {
                if (receiver === FALSE_IF_PROTECTED) return false
                if (whatDeclaration !is CallableMemberDescriptor || whatDeclaration is ConstructorDescriptor) return true
                if (receiver === ALWAYS_SUITABLE_RECEIVER) return true
                if (receiver === IRRELEVANT_RECEIVER || receiver == null) return false
                val type = if (receiver is SuperCallReceiverValue) receiver.thisType else receiver.getType()
                return DescriptorUtils.isSubtypeOfClass(type, fromClass) || type.isDynamic()
            }
        }
        val INTERNAL: DescriptorVisibility = object : DelegatedDescriptorVisibility(Visibilities.Internal) {
            override fun isVisible(receiver: ReceiverValue?, what: DeclarationDescriptorWithVisibility, from: DeclarationDescriptor,
                                   useSpecialRulesForPrivateSealedConstructors: Boolean): Boolean {
                val whatModule = DescriptorUtils.getContainingModule(what)
                val fromModule = DescriptorUtils.getContainingModule(from)
                if (!fromModule.shouldSeeInternalsOf(whatModule)) return false
                return MODULE_VISIBILITY_HELPER.isInFriendModule(what, from)
            }
        }
        val PUBLIC: DescriptorVisibility = object : DelegatedDescriptorVisibility(Visibilities.Public) {
            override fun isVisible(receiver: ReceiverValue?, what: DeclarationDescriptorWithVisibility, from: DeclarationDescriptor,
                                   useSpecialRulesForPrivateSealedConstructors: Boolean): Boolean = true
        }
        val LOCAL: DescriptorVisibility = object : DelegatedDescriptorVisibility(Visibilities.Local) {
            override fun isVisible(receiver: ReceiverValue?, what: DeclarationDescriptorWithVisibility, from: DeclarationDescriptor,
                                   useSpecialRulesForPrivateSealedConstructors: Boolean): Boolean =
                throw IllegalStateException("This method shouldn't be invoked for LOCAL visibility")
        }
        val INHERITED: DescriptorVisibility = object : DelegatedDescriptorVisibility(Visibilities.Inherited) {
            override fun isVisible(receiver: ReceiverValue?, what: DeclarationDescriptorWithVisibility, from: DeclarationDescriptor,
                                   useSpecialRulesForPrivateSealedConstructors: Boolean): Boolean = throw IllegalStateException("Visibility is unknown yet")
        }
        val INVISIBLE_FAKE: DescriptorVisibility = object : DelegatedDescriptorVisibility(Visibilities.InvisibleFake) {
            override fun isVisible(receiver: ReceiverValue?, what: DeclarationDescriptorWithVisibility, from: DeclarationDescriptor,
                                   useSpecialRulesForPrivateSealedConstructors: Boolean): Boolean = false
        }
        val UNKNOWN: DescriptorVisibility = object : DelegatedDescriptorVisibility(Visibilities.Unknown) {
            override fun isVisible(receiver: ReceiverValue?, what: DeclarationDescriptorWithVisibility, from: DeclarationDescriptor,
                                   useSpecialRulesForPrivateSealedConstructors: Boolean): Boolean = false
        }
        val INVISIBLE_FROM_OTHER_MODULES: Set<DescriptorVisibility> = object : AbstractSet<DescriptorVisibility>() {
            private val values = setOf(PRIVATE, PRIVATE_TO_THIS, INTERNAL, LOCAL)
            override val size: Int get() = values.size
            override fun iterator(): Iterator<DescriptorVisibility> = object : Iterator<DescriptorVisibility> {
                private val iterator = values.iterator()
                override fun hasNext(): Boolean = iterator.hasNext()
                override fun next(): DescriptorVisibility = iterator.next()
            }
        }
        fun isVisible(receiver: ReceiverValue?, what: DeclarationDescriptorWithVisibility, from: DeclarationDescriptor,
                      useSpecialRulesForPrivateSealedConstructors: Boolean): Boolean =
            findInvisibleMember(receiver, what, from, useSpecialRulesForPrivateSealedConstructors) == null
        fun isVisibleIgnoringReceiver(what: DeclarationDescriptorWithVisibility, from: DeclarationDescriptor,
                                      useSpecialRulesForPrivateSealedConstructors: Boolean): Boolean =
            findInvisibleMember(ALWAYS_SUITABLE_RECEIVER, what, from, useSpecialRulesForPrivateSealedConstructors) == null
        fun isVisibleWithAnyReceiver(what: DeclarationDescriptorWithVisibility, from: DeclarationDescriptor,
                                    useSpecialRulesForPrivateSealedConstructors: Boolean): Boolean =
            findInvisibleMember(IRRELEVANT_RECEIVER, what, from, useSpecialRulesForPrivateSealedConstructors) == null
        fun inSameFile(what: DeclarationDescriptor, from: DeclarationDescriptor): Boolean {
            val file = DescriptorUtils.getContainingSourceFile(from)
            if (file !== SourceFile.NO_SOURCE_FILE) return file == DescriptorUtils.getContainingSourceFile(what)
            return false
        }
        fun findInvisibleMember(receiver: ReceiverValue?, what: DeclarationDescriptorWithVisibility, from: DeclarationDescriptor,
                                useSpecialRulesForPrivateSealedConstructors: Boolean): DeclarationDescriptorWithVisibility? {
            var parent: DeclarationDescriptorWithVisibility? = what.getOriginal() as DeclarationDescriptorWithVisibility
            while (parent != null && parent.getVisibility() !== LOCAL) {
                if (!parent.getVisibility().isVisible(receiver, parent, from, useSpecialRulesForPrivateSealedConstructors)) return parent
                parent = DescriptorUtils.getParentOfType(parent, DeclarationDescriptorWithVisibility::class)
            }
            if (what is TypeAliasConstructorDescriptor) {
                val invisible = findInvisibleMember(receiver, what.underlyingConstructorDescriptor, from, useSpecialRulesForPrivateSealedConstructors)
                if (invisible != null) return invisible
            }
            return null
        }
        private val ORDERED_VISIBILITIES = mapOf(PRIVATE_TO_THIS to 0, PRIVATE to 0, INTERNAL to 1, PROTECTED to 1, PUBLIC to 2)
        internal fun compareLocal(first: DescriptorVisibility, second: DescriptorVisibility): Int? {
            if (first === second) return 0
            val firstIndex = ORDERED_VISIBILITIES[first]
            val secondIndex = ORDERED_VISIBILITIES[second]
            if (firstIndex == null || secondIndex == null || firstIndex == secondIndex) return null
            return firstIndex - secondIndex
        }
        fun compare(first: DescriptorVisibility, second: DescriptorVisibility): Int? {
            val result = first.compareTo(second)
            if (result != null) return result
            val opposite = second.compareTo(first)
            if (opposite != null) return -opposite
            return null
        }
        val DEFAULT_VISIBILITY: DescriptorVisibility = PUBLIC
        private val IRRELEVANT_RECEIVER = object : ReceiverValue {
            override fun getType(): KotlinType = throw IllegalStateException("This method should not be called")
            override fun replaceType(newType: KotlinType): ReceiverValue = throw IllegalStateException("This method should not be called")
            override fun getOriginal(): ReceiverValue = this
        }
        val ALWAYS_SUITABLE_RECEIVER: ReceiverValue = object : ReceiverValue {
            override fun getType(): KotlinType = throw IllegalStateException("This method should not be called")
            override fun replaceType(newType: KotlinType): ReceiverValue = throw IllegalStateException("This method should not be called")
            override fun getOriginal(): ReceiverValue = this
        }
        @Deprecated("Not intended for use outside protected visibility checks")
        val FALSE_IF_PROTECTED: ReceiverValue = object : ReceiverValue {
            override fun getType(): KotlinType = throw IllegalStateException("This method should not be called")
            override fun replaceType(newType: KotlinType): ReceiverValue = throw IllegalStateException("This method should not be called")
            override fun getOriginal(): ReceiverValue = this
        }
        fun isPrivate(visibility: DescriptorVisibility): Boolean = visibility === PRIVATE || visibility === PRIVATE_TO_THIS
        private val MODULE_VISIBILITY_HELPER: ModuleVisibilityHelper
            get() = NativeDescriptorServices.moduleVisibilityHelper
        private val visibilitiesMapping = HashMap<Visibility, DescriptorVisibility>()
        private fun recordVisibilityMapping(visibility: DescriptorVisibility) { visibilitiesMapping[visibility.delegate] = visibility }
        init {
            recordVisibilityMapping(PRIVATE)
            recordVisibilityMapping(PRIVATE_TO_THIS)
            recordVisibilityMapping(PROTECTED)
            recordVisibilityMapping(INTERNAL)
            recordVisibilityMapping(PUBLIC)
            recordVisibilityMapping(LOCAL)
            recordVisibilityMapping(INHERITED)
            recordVisibilityMapping(INVISIBLE_FAKE)
            recordVisibilityMapping(UNKNOWN)
        }
        fun toDescriptorVisibility(visibility: Visibility): DescriptorVisibility =
            visibilitiesMapping[visibility] ?: throw IllegalArgumentException("Inapplicable visibility: $visibility")
    }
}
