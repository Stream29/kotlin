/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.load.java.components

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.NonReportingOverrideStrategy
import org.jetbrains.kotlin.resolve.OverridingUtil
import org.jetbrains.kotlin.serialization.deserialization.ErrorReporter

object DescriptorResolverUtils {
    fun <D : CallableMemberDescriptor> resolveOverridesForNonStaticMembers(
        name: Name, membersFromSupertypes: Collection<D>, membersFromCurrent: Collection<D>,
        classDescriptor: ClassDescriptor, errorReporter: ErrorReporter, overridingUtil: OverridingUtil
    ): Collection<D> = resolveOverrides(name, membersFromSupertypes, membersFromCurrent, classDescriptor, errorReporter, overridingUtil, false)

    fun <D : CallableMemberDescriptor> resolveOverridesForStaticMembers(
        name: Name, membersFromSupertypes: Collection<D>, membersFromCurrent: Collection<D>,
        classDescriptor: ClassDescriptor, errorReporter: ErrorReporter, overridingUtil: OverridingUtil
    ): Collection<D> = resolveOverrides(name, membersFromSupertypes, membersFromCurrent, classDescriptor, errorReporter, overridingUtil, true)

    private fun <D : CallableMemberDescriptor> resolveOverrides(
        name: Name, membersFromSupertypes: Collection<D>, membersFromCurrent: Collection<D>,
        classDescriptor: ClassDescriptor, errorReporter: ErrorReporter, overridingUtil: OverridingUtil, isStaticContext: Boolean
    ): Collection<D> {
        val result = linkedSetOf<D>()
        overridingUtil.generateOverridesInFunctionGroup(
            name, membersFromSupertypes, membersFromCurrent, classDescriptor, object : NonReportingOverrideStrategy() {
                @Suppress("UNCHECKED_CAST")
                override fun addFakeOverride(fakeOverride: CallableMemberDescriptor) {
                    OverridingUtil.resolveUnknownVisibilityForMember(fakeOverride) { errorReporter.reportCannotInferVisibility(it) }
                    result.add(fakeOverride as D)
                }

                override fun conflict(fromSuper: CallableMemberDescriptor, fromCurrent: CallableMemberDescriptor) {}

                override fun setOverriddenDescriptors(member: CallableMemberDescriptor, overridden: Collection<CallableMemberDescriptor>) {
                    if (isStaticContext && member.getKind() != CallableMemberDescriptor.Kind.FAKE_OVERRIDE) return
                    super.setOverriddenDescriptors(member, overridden)
                }
            }
        )
        return result
    }

    fun getAnnotationParameterByName(name: Name, annotationClass: ClassDescriptor): ValueParameterDescriptor? {
        val constructors = annotationClass.getConstructors()
        if (constructors.size != 1) return null
        for (parameter in constructors.first().getValueParameters()) {
            if (parameter.getName() == name) return parameter
        }
        return null
    }
}
