/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.descriptors.annotations

// Translated-unverified. Annotated is a Kotlin-origin property interface.
open class AnnotatedImpl(annotations: Annotations) : Annotated {
    private val storedAnnotations = annotations
    override val annotations: Annotations get() = storedAnnotations
}
