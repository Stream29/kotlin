/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.load.java

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.java.JavaVisibilities
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValue

object JavaDescriptorVisibilities {
    val PACKAGE_VISIBILITY: DescriptorVisibility = object : DelegatedDescriptorVisibility(JavaVisibilities.PackageVisibility) {
        override fun isVisible(
            receiver: ReceiverValue?, what: DeclarationDescriptorWithVisibility, from: DeclarationDescriptor,
            useSpecialRulesForPrivateSealedConstructors: Boolean
        ): Boolean = areInSamePackage(what, from)
        override fun visibleFromPackage(fromPackage: FqName, myPackage: FqName): Boolean = fromPackage == myPackage
    }
    val PROTECTED_STATIC_VISIBILITY: DescriptorVisibility = object : DelegatedDescriptorVisibility(JavaVisibilities.ProtectedStaticVisibility) {
        override fun isVisible(
            receiver: ReceiverValue?, what: DeclarationDescriptorWithVisibility, from: DeclarationDescriptor,
            useSpecialRulesForPrivateSealedConstructors: Boolean
        ): Boolean = isVisibleForProtectedAndPackage(receiver, what, from)
    }
    val PROTECTED_AND_PACKAGE: DescriptorVisibility = object : DelegatedDescriptorVisibility(JavaVisibilities.ProtectedAndPackage) {
        override fun isVisible(
            receiver: ReceiverValue?, what: DeclarationDescriptorWithVisibility, from: DeclarationDescriptor,
            useSpecialRulesForPrivateSealedConstructors: Boolean
        ): Boolean = isVisibleForProtectedAndPackage(receiver, what, from)
    }

    private fun isVisibleForProtectedAndPackage(
        receiver: ReceiverValue?, what: DeclarationDescriptorWithVisibility, from: DeclarationDescriptor
    ): Boolean {
        if (areInSamePackage(DescriptorUtils.unwrapFakeOverrideToAnyDeclaration(what), from)) return true
        return DescriptorVisibilities.PROTECTED.isVisible(receiver, what, from, false)
    }

    private fun areInSamePackage(first: DeclarationDescriptor, second: DeclarationDescriptor): Boolean {
        val whatPackage = DescriptorUtils.getParentOfType(first, PackageFragmentDescriptor::class, false)
        val fromPackage = DescriptorUtils.getParentOfType(second, PackageFragmentDescriptor::class, false)
        return fromPackage != null && whatPackage != null && whatPackage.getFqName() == fromPackage.getFqName()
    }

    private val visibilitiesMapping = listOf(PACKAGE_VISIBILITY, PROTECTED_STATIC_VISIBILITY, PROTECTED_AND_PACKAGE).associateBy { it.delegate }

    fun toDescriptorVisibility(visibility: Visibility): DescriptorVisibility =
        visibilitiesMapping[visibility] ?: DescriptorVisibilities.toDescriptorVisibility(visibility)
}
