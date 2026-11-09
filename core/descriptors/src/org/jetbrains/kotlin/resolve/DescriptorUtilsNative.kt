/*
 * Copyright 2010-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin.resolve

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.builtins.UnsignedTypes
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor.Kind.*
import org.jetbrains.kotlin.descriptors.annotations.Annotated
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.descriptors.impl.PackageFragmentDescriptorImpl
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.name.*
import org.jetbrains.kotlin.resolve.constants.StringValue
import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.types.*
import org.jetbrains.kotlin.types.checker.KotlinTypeChecker
import org.jetbrains.kotlin.types.error.ErrorUtils
import kotlin.reflect.KClass

// Translated-unverified from DescriptorUtils.java; the adjacent .kt is in resolve.descriptorUtil.
class DescriptorUtils private constructor() {
    companion object {
        val JVM_NAME = FqName("kotlin.jvm.JvmName")
        fun getDispatchReceiverParameterIfNeeded(containingDeclaration: DeclarationDescriptor): ReceiverParameterDescriptor? =
            (containingDeclaration as? ClassDescriptor)?.getThisAsReceiverParameter()
        fun isLocal(descriptor: DeclarationDescriptor): Boolean {
            var current: DeclarationDescriptor? = descriptor
            while (current != null) {
                if (isAnonymousObject(current) || isDescriptorWithLocalVisibility(current)) return true
                current = current.getContainingDeclaration()
            }
            return false
        }
        fun isDescriptorWithLocalVisibility(current: DeclarationDescriptor?): Boolean =
            current is DeclarationDescriptorWithVisibility && current.getVisibility() === DescriptorVisibilities.LOCAL
        fun getFqName(descriptor: DeclarationDescriptor): FqNameUnsafe =
            getFqNameSafeIfPossible(descriptor)?.toUnsafe() ?: getFqNameUnsafe(descriptor)
        fun getFqNameSafe(descriptor: DeclarationDescriptor): FqName =
            getFqNameSafeIfPossible(descriptor) ?: getFqNameUnsafe(descriptor).toSafe()
        private fun getFqNameSafeIfPossible(descriptor: DeclarationDescriptor): FqName? = when {
            descriptor is ModuleDescriptor || ErrorUtils.isError(descriptor) -> FqName.ROOT
            descriptor is PackageViewDescriptor -> descriptor.fqName
            descriptor is PackageFragmentDescriptor -> descriptor.fqName
            else -> null
        }
        private fun getFqNameUnsafe(descriptor: DeclarationDescriptor): FqNameUnsafe {
            val containing = descriptor.getContainingDeclaration()
            assert(containing != null) { "Not package/module descriptor doesn't have containing declaration: $descriptor" }
            return getFqName(containing!!).child(descriptor.getName())
        }
        fun getFqNameFromTopLevelClass(descriptor: DeclarationDescriptor): FqName {
            val containing = descriptor.getContainingDeclaration()
            val name = descriptor.getName()
            if (containing !is ClassDescriptor) return FqName.topLevel(name)
            return getFqNameFromTopLevelClass(containing).child(name)
        }
        fun getClassIdForNonLocalClass(descriptor: DeclarationDescriptor): ClassId {
            val containing = descriptor.getContainingDeclaration()
            val name = descriptor.getName()
            if (containing is PackageFragmentDescriptorImpl) return ClassId(containing.fqName, name)
            if (containing !is ClassDescriptor) return ClassId(FqName.ROOT, name)
            return getClassIdForNonLocalClass(containing).createNestedClassId(name)
        }
        fun isTopLevelDeclaration(descriptor: DeclarationDescriptor?): Boolean = descriptor?.getContainingDeclaration() is PackageFragmentDescriptor
        fun isExtension(descriptor: CallableDescriptor): Boolean = descriptor.getExtensionReceiverParameter() != null
        fun isOverride(descriptor: CallableMemberDescriptor): Boolean = descriptor.getOverriddenDescriptors().isNotEmpty()
        fun isStaticDeclaration(descriptor: CallableDescriptor): Boolean {
            if (descriptor is ConstructorDescriptor) return false
            val container = descriptor.getContainingDeclaration()
            return container is PackageFragmentDescriptor || container is ClassDescriptor && descriptor.getDispatchReceiverParameter() == null
        }
        fun areInSameModule(first: DeclarationDescriptor, second: DeclarationDescriptor): Boolean =
            getContainingModule(first) == getContainingModule(second)
        fun <D : DeclarationDescriptor> getParentOfType(descriptor: DeclarationDescriptor?, aClass: KClass<D>): D? =
            getParentOfType(descriptor, aClass, true)
        @Suppress("UNCHECKED_CAST")
        fun <D : DeclarationDescriptor> getParentOfType(descriptor: DeclarationDescriptor?, aClass: KClass<D>, strict: Boolean): D? {
            if (descriptor == null) return null
            var current = if (strict) descriptor.getContainingDeclaration() else descriptor
            while (current != null) {
                if (aClass.isInstance(current)) return current as D
                current = current.getContainingDeclaration()
            }
            return null
        }
        fun getContainingModuleOrNull(kotlinType: KotlinType): ModuleDescriptor? =
            kotlinType.constructor.getDeclarationDescriptor()?.let { getContainingModuleOrNull(it) }
        fun getContainingModule(descriptor: DeclarationDescriptor): ModuleDescriptor {
            val module = getContainingModuleOrNull(descriptor)
            assert(module != null) { "Descriptor without a containing module: $descriptor" }
            return module!!
        }
        fun getContainingModuleOrNull(descriptor: DeclarationDescriptor): ModuleDescriptor? {
            var current: DeclarationDescriptor? = descriptor
            while (current != null) {
                if (current is ModuleDescriptor) return current
                if (current is PackageViewDescriptor) return current.module
                current = current.getContainingDeclaration()
            }
            return null
        }
        fun getContainingClass(descriptor: DeclarationDescriptor): ClassDescriptor? {
            var containing = descriptor.getContainingDeclaration()
            while (containing != null) {
                if (containing is ClassDescriptor && !isCompanionObject(containing)) return containing
                containing = containing.getContainingDeclaration()
            }
            return null
        }
        fun isAncestor(ancestor: DeclarationDescriptor?, declarationDescriptor: DeclarationDescriptor, strict: Boolean): Boolean {
            if (ancestor == null) return false
            var descriptor = if (strict) declarationDescriptor.getContainingDeclaration() else declarationDescriptor
            while (descriptor != null) {
                if (ancestor === descriptor) return true
                descriptor = descriptor.getContainingDeclaration()
            }
            return false
        }
        fun isDirectSubclass(subClass: ClassDescriptor, superClass: ClassDescriptor): Boolean {
            for (type in subClass.getTypeConstructor().getSupertypes()) if (isSameClass(type, superClass.getOriginal())) return true
            return false
        }
        fun isSubclass(subClass: ClassDescriptor, superClass: ClassDescriptor): Boolean = isSubtypeOfClass(subClass.getDefaultType(), superClass.getOriginal())
        private fun isSameClass(type: KotlinType, other: DeclarationDescriptor): Boolean {
            val original = type.constructor.getDeclarationDescriptor()?.getOriginal()
            return original is ClassifierDescriptor && other is ClassifierDescriptor && other.getTypeConstructor() == original.getTypeConstructor()
        }
        fun isSubtypeOfClass(type: KotlinType, superClass: DeclarationDescriptor): Boolean {
            if (isSameClass(type, superClass)) return true
            for (superType in type.constructor.getSupertypes()) if (isSubtypeOfClass(superType, superClass)) return true
            return false
        }
        fun isCompanionObject(descriptor: DeclarationDescriptor?): Boolean =
            isKindOf(descriptor, ClassKind.OBJECT) && (descriptor as ClassDescriptor).isCompanionObject()
        fun isSealedClass(descriptor: DeclarationDescriptor?): Boolean =
            (isKindOf(descriptor, ClassKind.CLASS) || isKindOf(descriptor, ClassKind.INTERFACE)) && (descriptor as ClassDescriptor).getModality() == Modality.SEALED
        fun isAnonymousObject(descriptor: DeclarationDescriptor): Boolean = isClass(descriptor) && descriptor.getName() == SpecialNames.NO_NAME_PROVIDED
        fun isAnonymousFunction(descriptor: DeclarationDescriptor): Boolean = descriptor is SimpleFunctionDescriptor && descriptor.getName() == SpecialNames.ANONYMOUS
        fun isNonCompanionObject(descriptor: DeclarationDescriptor?): Boolean =
            isKindOf(descriptor, ClassKind.OBJECT) && !(descriptor as ClassDescriptor).isCompanionObject()
        fun isObject(descriptor: DeclarationDescriptor?): Boolean = isKindOf(descriptor, ClassKind.OBJECT)
        fun isEnumEntry(descriptor: DeclarationDescriptor): Boolean = isKindOf(descriptor, ClassKind.ENUM_ENTRY)
        fun isEnumClass(descriptor: DeclarationDescriptor?): Boolean = isKindOf(descriptor, ClassKind.ENUM_CLASS)
        fun isAnnotationClass(descriptor: DeclarationDescriptor?): Boolean = isKindOf(descriptor, ClassKind.ANNOTATION_CLASS)
        fun isInterface(descriptor: DeclarationDescriptor?): Boolean = isKindOf(descriptor, ClassKind.INTERFACE)
        fun isClass(descriptor: DeclarationDescriptor?): Boolean = isKindOf(descriptor, ClassKind.CLASS)
        fun isClassOrEnumClass(descriptor: DeclarationDescriptor?): Boolean = isClass(descriptor) || isEnumClass(descriptor)
        private fun isKindOf(descriptor: DeclarationDescriptor?, classKind: ClassKind): Boolean = descriptor is ClassDescriptor && descriptor.getKind() == classKind
        fun hasAbstractMembers(classDescriptor: ClassDescriptor): Boolean {
            for (member in getAllDescriptors(classDescriptor.getDefaultType().memberScope))
                if (member is CallableMemberDescriptor && member.getModality() == Modality.ABSTRACT) return true
            return false
        }
        fun getSuperclassDescriptors(classDescriptor: ClassDescriptor): List<ClassDescriptor> {
            val result = ArrayList<ClassDescriptor>()
            for (type in classDescriptor.getTypeConstructor().getSupertypes()) {
                val descriptor = getClassDescriptorForType(type)
                if (!KotlinBuiltIns.isAny(descriptor)) result.add(descriptor)
            }
            return result
        }
        fun getSuperClassType(classDescriptor: ClassDescriptor): KotlinType {
            for (type in classDescriptor.getTypeConstructor().getSupertypes())
                if (getClassDescriptorForType(type).getKind() != ClassKind.INTERFACE) return type
            return classDescriptor.builtIns.getAnyType()
        }
        fun getSuperClassDescriptor(classDescriptor: ClassDescriptor): ClassDescriptor? {
            for (type in classDescriptor.getTypeConstructor().getSupertypes()) {
                val descriptor = getClassDescriptorForType(type)
                if (descriptor.getKind() != ClassKind.INTERFACE) return descriptor
            }
            return null
        }
        fun getClassDescriptorForType(type: KotlinType): ClassDescriptor = getClassDescriptorForTypeConstructor(type.constructor)
        fun getClassDescriptorForTypeConstructor(typeConstructor: TypeConstructor): ClassDescriptor {
            val descriptor = typeConstructor.getDeclarationDescriptor()
            assert(descriptor is ClassDescriptor) { "Classifier descriptor of a type should be of type ClassDescriptor: $typeConstructor" }
            return descriptor as ClassDescriptor
        }
        fun getDefaultConstructorVisibility(classDescriptor: ClassDescriptor, freedomForSealedInterfacesSupported: Boolean): DescriptorVisibility {
            val kind = classDescriptor.getKind()
            if (kind == ClassKind.ENUM_CLASS || kind.isSingleton) return DescriptorVisibilities.PRIVATE
            if (isSealedClass(classDescriptor)) return if (freedomForSealedInterfacesSupported) DescriptorVisibilities.PROTECTED else DescriptorVisibilities.PRIVATE
            if (isAnonymousObject(classDescriptor)) return DescriptorVisibilities.DEFAULT_VISIBILITY
            assert(kind == ClassKind.CLASS || kind == ClassKind.INTERFACE || kind == ClassKind.ANNOTATION_CLASS)
            return DescriptorVisibilities.PUBLIC
        }
        fun getInnerClassByName(classDescriptor: ClassDescriptor, innerClassName: String, location: LookupLocation): ClassDescriptor? {
            val classifier = classDescriptor.getDefaultType().memberScope.getContributedClassifier(Name.identifier(innerClassName), location)
            assert(classifier is ClassDescriptor) {
                "Inner class $innerClassName in $classDescriptor should be instance of ClassDescriptor, but was: ${classifier?.let { it::class }}"
            }
            return classifier as ClassDescriptor?
        }
        fun getReceiverParameterType(receiverParameterDescriptor: ReceiverParameterDescriptor?): KotlinType? = receiverParameterDescriptor?.getType()
        fun isStaticNestedClass(descriptor: DeclarationDescriptor): Boolean =
            descriptor is ClassDescriptor && descriptor.getContainingDeclaration() is ClassDescriptor && !descriptor.isInner()
        @Suppress("UNCHECKED_CAST")
        fun <D : CallableMemberDescriptor> unwrapFakeOverride(descriptor: D): D {
            var current = descriptor
            while (current.getKind() == FAKE_OVERRIDE) {
                val overridden = current.getOverriddenDescriptors()
                if (overridden.isEmpty()) throw IllegalStateException("Fake override should have at least one overridden descriptor: $current")
                current = overridden.iterator().next() as D
            }
            return current
        }
        @Suppress("UNCHECKED_CAST")
        fun <D : CallableMemberDescriptor> unwrapSubstitutionOverride(descriptor: D): D {
            var current = descriptor
            while (current.getKind() == FAKE_OVERRIDE) {
                val overridden = current.getOverriddenDescriptors()
                if (overridden.isEmpty()) throw IllegalStateException("Fake override should have at least one overridden descriptor: $current")
                if (overridden.size > 1) return current
                current = overridden.iterator().next() as D
            }
            return current
        }
        @Suppress("UNCHECKED_CAST")
        fun <D : DeclarationDescriptorWithVisibility> unwrapFakeOverrideToAnyDeclaration(descriptor: D): D =
            if (descriptor is CallableMemberDescriptor) unwrapFakeOverride(descriptor) as D else descriptor
        fun shouldRecordInitializerForProperty(variable: VariableDescriptor, type: KotlinType): Boolean {
            if (variable.isVar() || type.isError) return false
            if (TypeUtils.acceptsNullable(type)) return true
            val builtIns = variable.builtIns
            return KotlinBuiltIns.isPrimitiveType(type) || KotlinTypeChecker.DEFAULT.equalTypes(builtIns.getStringType(), type) ||
                KotlinTypeChecker.DEFAULT.equalTypes(builtIns.getNumber().getDefaultType(), type) ||
                KotlinTypeChecker.DEFAULT.equalTypes(builtIns.getAnyType(), type) || UnsignedTypes.isUnsignedType(type)
        }
        fun classCanHaveAbstractFakeOverride(classDescriptor: ClassDescriptor): Boolean = classCanHaveAbstractDeclaration(classDescriptor) || classDescriptor.isExpect()
        fun classCanHaveAbstractDeclaration(classDescriptor: ClassDescriptor): Boolean =
            classDescriptor.getModality() == Modality.ABSTRACT || isSealedClass(classDescriptor) || classDescriptor.getKind() == ClassKind.ENUM_CLASS
        fun classCanHaveOpenMembers(classDescriptor: ClassDescriptor): Boolean =
            classDescriptor.getModality() != Modality.FINAL || classDescriptor.getKind() == ClassKind.ENUM_CLASS
        @Suppress("UNCHECKED_CAST")
        fun <D : CallableDescriptor> getAllOverriddenDescriptors(f: D): Set<D> {
            val result = LinkedHashSet<D>()
            collectAllOverriddenDescriptors(f.getOriginal() as D, result)
            return result
        }
        @Suppress("UNCHECKED_CAST")
        private fun <D : CallableDescriptor> collectAllOverriddenDescriptors(current: D, result: MutableSet<D>) {
            if (result.contains(current)) return
            for (callable in current.getOriginal().getOverriddenDescriptors()) {
                val descriptor = callable.getOriginal() as D
                collectAllOverriddenDescriptors(descriptor, result)
                result.add(descriptor)
            }
        }
        @Suppress("UNCHECKED_CAST")
        fun <D : CallableMemberDescriptor> getAllOverriddenDeclarations(memberDescriptor: D): Set<D> {
            val result = HashSet<D>()
            for (overridden in memberDescriptor.getOverriddenDescriptors()) {
                when (overridden.getKind()) {
                    DECLARATION -> result.add(overridden as D)
                    DELEGATION, FAKE_OVERRIDE, SYNTHESIZED -> Unit
                }
                result.addAll(getAllOverriddenDeclarations(overridden as D))
            }
            return result
        }
        fun isSingletonOrAnonymousObject(classDescriptor: ClassDescriptor): Boolean = classDescriptor.getKind().isSingleton || isAnonymousObject(classDescriptor)
        fun canHaveDeclaredConstructors(classDescriptor: ClassDescriptor): Boolean = !isSingletonOrAnonymousObject(classDescriptor) && !isInterface(classDescriptor)
        fun getJvmName(annotated: Annotated): String? = getJvmName(findJvmNameAnnotation(annotated))
        private fun getJvmName(jvmNameAnnotation: AnnotationDescriptor?): String? {
            if (jvmNameAnnotation == null) return null
            val arguments = jvmNameAnnotation.allValueArguments
            if (arguments.isEmpty()) return null
            return (arguments.values.iterator().next() as? StringValue)?.value
        }
        fun findJvmNameAnnotation(annotated: Annotated): AnnotationDescriptor? = annotated.annotations.findAnnotation(JVM_NAME)
        fun hasJvmNameAnnotation(annotated: Annotated): Boolean = findJvmNameAnnotation(annotated) != null
        fun getContainingSourceFile(descriptor: DeclarationDescriptor): SourceFile {
            val declaration = if (descriptor is PropertySetterDescriptor) descriptor.getCorrespondingProperty() else descriptor
            return if (declaration is DeclarationDescriptorWithSource) declaration.getSource().containingFile else SourceFile.NO_SOURCE_FILE
        }
        fun getAllDescriptors(scope: MemberScope): Collection<DeclarationDescriptor> = scope.getContributedDescriptors(DescriptorKindFilter.ALL, MemberScope.ALL_NAME_FILTER)
        fun getFunctionByName(scope: MemberScope, name: Name): FunctionDescriptor = getFunctionByNameOrNull(scope, name) ?: throw IllegalStateException("Function not found")
        fun getFunctionByNameOrNull(scope: MemberScope, name: Name): FunctionDescriptor? {
            for (descriptor in scope.getContributedFunctions(name, NoLookupLocation.FROM_BACKEND))
                if (name == descriptor.getOriginal().getName()) return descriptor
            return null
        }
        fun getPropertyByName(scope: MemberScope, name: Name): PropertyDescriptor {
            for (descriptor in scope.getContributedVariables(name, NoLookupLocation.FROM_BACKEND))
                if (name == descriptor.getOriginal().getName()) return descriptor
            throw IllegalStateException("Property not found")
        }
        fun getDirectMember(descriptor: CallableMemberDescriptor): CallableMemberDescriptor =
            if (descriptor is PropertyAccessorDescriptor) descriptor.getCorrespondingProperty() else descriptor
    }
}
