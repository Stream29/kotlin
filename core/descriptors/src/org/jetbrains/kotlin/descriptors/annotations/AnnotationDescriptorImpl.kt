/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.descriptors.annotations

import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.renderer.DescriptorRenderer
import org.jetbrains.kotlin.resolve.constants.ConstantValue
import org.jetbrains.kotlin.types.KotlinType

// Translated-unverified.
open class AnnotationDescriptorImpl(
    private val annotationType: KotlinType,
    private val valueArguments: Map<Name, ConstantValue<*>>,
    source: SourceElement
) : AnnotationDescriptor {
    private val sourceElement = source
    override val type: KotlinType get() = annotationType
    override val fqName: FqName? get() = super<AnnotationDescriptor>.fqName
    override val allValueArguments: Map<Name, ConstantValue<*>> get() = valueArguments
    override val source: SourceElement get() = sourceElement
    override fun toString(): String = DescriptorRenderer.FQ_NAMES_IN_TYPES.renderAnnotation(this, null)
}
