/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.load.java.descriptors

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.mpp.MppJavaImplicitActualizatorMarker

interface JavaClassDescriptor : ClassDescriptor, MppJavaImplicitActualizatorMarker {
    fun isRecord(): Boolean
}

val JavaClassDescriptor.isRecord: Boolean get() = isRecord()
