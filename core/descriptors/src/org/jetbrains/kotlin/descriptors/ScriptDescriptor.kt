/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.descriptors

// Translated-unverified.
interface ScriptDescriptor : ClassDescriptor {
    fun getPriority(): Int
    override fun getUnsubstitutedPrimaryConstructor(): ClassConstructorDescriptor
    fun getImplicitReceivers(): List<ClassDescriptor>
    fun getScriptProvidedProperties(): List<PropertyDescriptor>
    fun getResultValue(): PropertyDescriptor?
    fun getEarlierScriptsConstructorParameter(): ValueParameterDescriptor?
    fun getExplicitConstructorParameters(): List<ValueParameterDescriptor>
    fun getScriptProvidedPropertiesParameters(): List<ValueParameterDescriptor>
}

val ScriptDescriptor.priority: Int get() = getPriority()
val ScriptDescriptor.unsubstitutedPrimaryConstructor: ClassConstructorDescriptor get() = getUnsubstitutedPrimaryConstructor()
val ScriptDescriptor.implicitReceivers: List<ClassDescriptor> get() = getImplicitReceivers()
val ScriptDescriptor.scriptProvidedProperties: List<PropertyDescriptor> get() = getScriptProvidedProperties()
val ScriptDescriptor.resultValue: PropertyDescriptor? get() = getResultValue()
val ScriptDescriptor.earlierScriptsConstructorParameter: ValueParameterDescriptor? get() = getEarlierScriptsConstructorParameter()
val ScriptDescriptor.explicitConstructorParameters: List<ValueParameterDescriptor> get() = getExplicitConstructorParameters()
val ScriptDescriptor.scriptProvidedPropertiesParameters: List<ValueParameterDescriptor> get() = getScriptProvidedPropertiesParameters()
