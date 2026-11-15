package org.jetbrains.kotlin.psi.stubs.elements

import kotlin.reflect.KClass

internal object KotlinStubFileMethodOwners {
    val entries: Map<KClass<*>, KClass<*>> = mapOf(
        org.jetbrains.kotlin.psi.stubs.elements.KtFileElementType::class to org.jetbrains.kotlin.psi.stubs.elements.KtFileElementType::class,
    )
}
