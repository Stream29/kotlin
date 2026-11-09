/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.load.java.components

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.load.java.structure.*
import org.jetbrains.kotlin.name.FqName

interface JavaResolverCache {
    fun getClassResolvedFromSource(fqName: FqName): ClassDescriptor?
    fun recordMethod(member: JavaMember, descriptor: SimpleFunctionDescriptor)
    fun recordConstructor(element: JavaElement, descriptor: ConstructorDescriptor)
    fun recordField(field: JavaField, descriptor: PropertyDescriptor)
    fun recordClass(javaClass: JavaClass, descriptor: ClassDescriptor)

    companion object {
        // Upstream optional cache policy, not a Native service fallback.
        val EMPTY: JavaResolverCache = object : JavaResolverCache {
            override fun getClassResolvedFromSource(fqName: FqName): ClassDescriptor? = null
            override fun recordMethod(member: JavaMember, descriptor: SimpleFunctionDescriptor) {}
            override fun recordConstructor(element: JavaElement, descriptor: ConstructorDescriptor) {}
            override fun recordField(field: JavaField, descriptor: PropertyDescriptor) {}
            override fun recordClass(javaClass: JavaClass, descriptor: ClassDescriptor) {}
        }
    }
}
