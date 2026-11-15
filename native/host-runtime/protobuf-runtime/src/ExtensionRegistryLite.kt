/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Copyright 2008 Google Inc.
 * Use of this source code is governed by the Apache 2.0 and Protobuf BSD licenses.
 */

package org.jetbrains.kotlin.protobuf

class ExtensionRegistryLite private constructor(
    private val isReadOnly: Boolean,
    private val extensionsByNumber: MutableMap<ObjectIntPair, GeneratedMessageLite.GeneratedExtension<*, *>> = HashMap()
) {
    private class ObjectIntPair(val obj: Any, val number: Int) {
        override fun hashCode(): Int = obj.hashCode() * 65535 + number
        override fun equals(other: Any?): Boolean {
            if (other !is ObjectIntPair) return false
            return obj === other.obj && number == other.number
        }
    }

    val unmodifiable: ExtensionRegistryLite get() = ExtensionRegistryLite(true, extensionsByNumber)
    fun getUnmodifiable(): ExtensionRegistryLite = unmodifiable

    fun add(extension: GeneratedMessageLite.GeneratedExtension<*, *>) {
        if (isReadOnly) throw UnsupportedOperationException("Extension registry is read-only")
        extensionsByNumber[ObjectIntPair(extension.containingTypeDefaultInstance, extension.number)] = extension
    }

    fun findLiteExtensionByNumber(
        containingTypeDefaultInstance: MessageLite,
        fieldNumber: Int
    ): GeneratedMessageLite.GeneratedExtension<*, *>? {
        if (extensionsByNumber.isEmpty()) return null
        return extensionsByNumber[ObjectIntPair(containingTypeDefaultInstance, fieldNumber)]
    }

    companion object {
        private val EMPTY = ExtensionRegistryLite(true)

        fun newInstance(): ExtensionRegistryLite = ExtensionRegistryLite(false)

        fun getEmptyRegistry(): ExtensionRegistryLite = EMPTY
    }
}
