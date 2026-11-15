/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin.psi.stubs.impl

import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.stubs.NamedStub
import com.intellij.psi.stubs.StubBase
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.psi.KtElementImplStub
import org.jetbrains.kotlin.psi.KtImplementationDetail
import org.jetbrains.kotlin.psi.stubs.*
import kotlin.reflect.KClass

@KtImplementationDetail
val STUB_TO_STRING_PREFIX = "KotlinStub$"

private val IGNORED_NULL_VALUES: Map<KClass<*>, Set<String>> = buildMap {
    put(KotlinFunctionStub::class, setOf(KotlinCallableStubBase<*>::kdocText.name))
    put(KotlinPropertyStub::class, setOf(KotlinCallableStubBase<*>::kdocText.name))
    put(KotlinConstructorStub::class, setOf(KotlinCallableStubBase<*>::kdocText.name))
    put(KotlinClassStub::class, setOf(KotlinClassOrObjectStub<*>::kdocText.name))
    put(KotlinObjectStub::class, setOf(KotlinClassOrObjectStub<*>::kdocText.name))
}

@KtImplementationDetail
abstract class KotlinStubBaseImpl<T : KtElementImplStub<*>>(parent: StubElement<*>?, elementType: IElementType) :
    StubBase<T>(parent, elementType), KotlinStubElement<T> {

    @KtImplementationDetail
    abstract override fun copyInto(newParent: StubElement<*>?): KotlinStubBaseImpl<T>

    override fun toString(): String {
        val stubInterface = NativeStubRenderMetadata.get(this::class)
        val propertiesValues = renderPropertyValues(stubInterface)
        if (propertiesValues.isEmpty()) {
            return "$STUB_TO_STRING_PREFIX$elementType"
        }

        val properties = propertiesValues.joinToString(separator = ", ", prefix = "[", postfix = "]")
        return "$STUB_TO_STRING_PREFIX$elementType$properties"
    }

    private fun renderPropertyValues(stubInterface: NativeStubInterface): List<String> =
        stubInterface.properties.mapNotNull { property -> renderProperty(stubInterface.type, property) }.sorted()

    private fun renderProperty(stubInterface: KClass<*>, property: NativeStubProperty): String? {
        return try {
            val value = try {
                property.read(this)
            } catch (target: Throwable) {
                throw NativeStubPropertyInvocationFailure(target)
            }
            val name = property.name
            if (value == null) {
                val ignoredNamesForClass = IGNORED_NULL_VALUES[stubInterface].orEmpty()
                if (name in ignoredNamesForClass) return null
            }
            "$name=$value"
        } catch (e: Exception) {
            LOGGER.error(e)
            null
        }
    }

    companion object {
        private val LOGGER = Logger.getInstance(KotlinStubBaseImpl::class)
    }
}

private class NativeStubPropertyInvocationFailure(target: Throwable) : Exception(target)
