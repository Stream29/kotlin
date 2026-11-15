/*
 * Copyright 2010-2015 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0; see the adjacent upstream Java source.
 */
package org.jetbrains.kotlin.descriptors.impl

import org.jetbrains.kotlin.descriptors.*

// Translated-unverified. The nullable root result and hierarchical dispatch are upstream behavior.
open class DeclarationDescriptorVisitorEmptyBodies<R, D> : DeclarationDescriptorVisitor<R, D> {
    open fun visitDeclarationDescriptor(descriptor: DeclarationDescriptor, data: D): R? = null
    override fun visitVariableDescriptor(descriptor: VariableDescriptor, data: D): R? = visitDeclarationDescriptor(descriptor, data)
    override fun visitFunctionDescriptor(descriptor: FunctionDescriptor, data: D): R? = visitDeclarationDescriptor(descriptor, data)
    override fun visitTypeParameterDescriptor(descriptor: TypeParameterDescriptor, data: D): R? = visitDeclarationDescriptor(descriptor, data)
    override fun visitPackageFragmentDescriptor(descriptor: PackageFragmentDescriptor, data: D): R? = visitDeclarationDescriptor(descriptor, data)
    override fun visitPackageViewDescriptor(descriptor: PackageViewDescriptor, data: D): R? = visitDeclarationDescriptor(descriptor, data)
    override fun visitClassDescriptor(descriptor: ClassDescriptor, data: D): R? = visitDeclarationDescriptor(descriptor, data)
    override fun visitTypeAliasDescriptor(descriptor: TypeAliasDescriptor, data: D): R? = visitDeclarationDescriptor(descriptor, data)
    override fun visitModuleDeclaration(descriptor: ModuleDescriptor, data: D): R? = visitDeclarationDescriptor(descriptor, data)
    override fun visitConstructorDescriptor(constructorDescriptor: ConstructorDescriptor, data: D): R? =
        visitFunctionDescriptor(constructorDescriptor, data)
    override fun visitScriptDescriptor(scriptDescriptor: ScriptDescriptor, data: D): R? = visitClassDescriptor(scriptDescriptor, data)
    override fun visitPropertyDescriptor(descriptor: PropertyDescriptor, data: D): R? = visitVariableDescriptor(descriptor, data)
    override fun visitValueParameterDescriptor(descriptor: ValueParameterDescriptor, data: D): R? = visitVariableDescriptor(descriptor, data)
    override fun visitPropertyGetterDescriptor(descriptor: PropertyGetterDescriptor, data: D): R? = visitFunctionDescriptor(descriptor, data)
    override fun visitPropertySetterDescriptor(descriptor: PropertySetterDescriptor, data: D): R? = visitFunctionDescriptor(descriptor, data)
    override fun visitReceiverParameterDescriptor(descriptor: ReceiverParameterDescriptor, data: D): R? = visitDeclarationDescriptor(descriptor, data)
}
