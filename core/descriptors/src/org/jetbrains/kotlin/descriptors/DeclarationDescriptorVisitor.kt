/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.descriptors

// Translated-unverified.
interface DeclarationDescriptorVisitor<R, D> {
    fun visitPackageFragmentDescriptor(descriptor: PackageFragmentDescriptor, data: D): R?
    fun visitPackageViewDescriptor(descriptor: PackageViewDescriptor, data: D): R?
    fun visitVariableDescriptor(descriptor: VariableDescriptor, data: D): R?
    fun visitFunctionDescriptor(descriptor: FunctionDescriptor, data: D): R?
    fun visitTypeParameterDescriptor(descriptor: TypeParameterDescriptor, data: D): R?
    fun visitClassDescriptor(descriptor: ClassDescriptor, data: D): R?
    fun visitTypeAliasDescriptor(descriptor: TypeAliasDescriptor, data: D): R?
    fun visitModuleDeclaration(descriptor: ModuleDescriptor, data: D): R?
    fun visitConstructorDescriptor(constructorDescriptor: ConstructorDescriptor, data: D): R?
    fun visitScriptDescriptor(scriptDescriptor: ScriptDescriptor, data: D): R?
    fun visitPropertyDescriptor(descriptor: PropertyDescriptor, data: D): R?
    fun visitValueParameterDescriptor(descriptor: ValueParameterDescriptor, data: D): R?
    fun visitPropertyGetterDescriptor(descriptor: PropertyGetterDescriptor, data: D): R?
    fun visitPropertySetterDescriptor(descriptor: PropertySetterDescriptor, data: D): R?
    fun visitReceiverParameterDescriptor(descriptor: ReceiverParameterDescriptor, data: D): R?
}
