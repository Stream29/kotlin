/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("PackageDirectoryMismatch")

package org.jetbrains.kotlin.serialization

import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptorNonRoot
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.extensions.ProjectExtensionDescriptor
import org.jetbrains.kotlin.metadata.ProtoBuf
import org.jetbrains.kotlin.metadata.deserialization.VersionRequirement
import org.jetbrains.kotlin.metadata.serialization.MutableVersionRequirementTable
import org.jetbrains.kotlin.types.DefaultTypeAttributeTranslator
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeAttributeTranslator
import org.jetbrains.kotlin.types.TypeConstructor
import org.jetbrains.kotlin.types.TypeProjection
import org.jetbrains.kotlin.types.extensions.TypeAttributeTranslators

internal val DeclarationDescriptorNonRoot.containingDeclaration: DeclarationDescriptor
    get() = getContainingDeclaration()

internal val TypeProjection.type: KotlinType
    get() = getType()

internal val TypeConstructor.declarationDescriptor: ClassifierDescriptor?
    get() = getDeclarationDescriptor()

object VersionRequirementUtils {
    fun writeVersionRequirement(
        major: Int,
        minor: Int,
        patch: Int,
        versionKind: ProtoBuf.VersionRequirement.VersionKind,
        versionRequirementTable: MutableVersionRequirementTable,
    ): Int {
        val requirement = ProtoBuf.VersionRequirement.newBuilder().apply {
            VersionRequirement.Version(major, minor, patch).encode(
                writeVersion = { version = it },
                writeVersionFull = { versionFull = it }
            )
            if (versionKind != defaultInstanceForType.versionKind) {
                this.versionKind = versionKind
            }
        }
        return versionRequirementTable[requirement]
    }
}
