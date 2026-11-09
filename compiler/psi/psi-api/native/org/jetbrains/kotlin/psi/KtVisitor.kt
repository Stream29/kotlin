/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import org.jetbrains.kotlin.psi.psiUtil.tryFlattenStringConcatenationDescendants



open class KtVisitor<R,D> : PsiElementVisitor() {
open fun visitKtElement(element: KtElement, data: D): R {

visitElement(element)
@Suppress("UNCHECKED_CAST")
return null as R
}

open fun visitDeclaration(dcl: KtDeclaration, data: D): R {

return visitExpression(dcl,data)
}

open fun visitClass(klass: KtClass, data: D): R {

return visitClassOrObject(klass,data)
}

open fun visitObjectDeclaration(declaration: KtObjectDeclaration, data: D): R {

return visitClassOrObject(declaration,data)
}

open fun visitClassOrObject(classOrObject: KtClassOrObject, data: D): R {

return visitNamedDeclaration(classOrObject,data)
}

open fun visitConstructor(constructor: KtConstructor<*>, data: D): R {

return visitNamedDeclaration(constructor,data)
}

open fun visitSecondaryConstructor(constructor: KtSecondaryConstructor, data: D): R {

return visitConstructor(constructor,data)
}

open fun visitPrimaryConstructor(constructor: KtPrimaryConstructor, data: D): R {

return visitConstructor(constructor,data)
}

open fun visitNamedFunction(function: KtNamedFunction, data: D): R {

return visitNamedDeclaration(function,data)
}

open fun visitProperty(property: KtProperty, data: D): R {

return visitNamedDeclaration(property,data)
}

open fun visitDestructuringDeclaration(multiDeclaration: KtDestructuringDeclaration, data: D): R {

return visitDeclaration(multiDeclaration,data)
}

open fun visitDestructuringDeclarationEntry(multiDeclarationEntry: KtDestructuringDeclarationEntry, data: D): R {

return visitNamedDeclaration(multiDeclarationEntry,data)
}

open fun visitTypeAlias(typeAlias: KtTypeAlias, data: D): R {

return visitNamedDeclaration(typeAlias,data)
}

open fun visitKtFile(file: KtFile, data: D): R {

visitFile(file)
@Suppress("UNCHECKED_CAST")
return null as R
}

fun visitKtCommonFile(file: KtCommonFile): R {

visitFile(file)
@Suppress("UNCHECKED_CAST")
return null as R
}

open fun visitScript(script: KtScript, data: D): R {

return visitDeclaration(script,data)
}

open fun visitImportAlias(importAlias: KtImportAlias, data: D): R {

return visitKtElement(importAlias,data)
}

open fun visitImportDirective(importDirective: KtImportDirective, data: D): R {

return visitKtElement(importDirective,data)
}

open fun visitImportList(importList: KtImportList, data: D): R {

return visitKtElement(importList,data)
}

open fun visitFileAnnotationList(fileAnnotationList: KtFileAnnotationList, data: D): R {

return visitKtElement(fileAnnotationList,data)
}

open fun visitClassBody(classBody: KtClassBody, data: D): R {

return visitKtElement(classBody,data)
}

open fun visitCompanionBlock(companionBlock: KtCompanionBlock, data: D): R {

return visitKtElement(companionBlock,data)
}

open fun visitModifierList(list: KtModifierList, data: D): R {

return visitKtElement(list,data)
}

open fun visitAnnotation(annotation: KtAnnotation, data: D): R {

return visitKtElement(annotation,data)
}

open fun visitAnnotationEntry(annotationEntry: KtAnnotationEntry, data: D): R {

return visitKtElement(annotationEntry,data)
}

open fun visitAnnotationUseSiteTarget(annotationTarget: KtAnnotationUseSiteTarget, data: D): R {

return visitKtElement(annotationTarget,data)
}

open fun visitConstructorCalleeExpression(constructorCalleeExpression: KtConstructorCalleeExpression, data: D): R {

return visitKtElement(constructorCalleeExpression,data)
}

open fun visitTypeParameterList(list: KtTypeParameterList, data: D): R {

return visitKtElement(list,data)
}

open fun visitTypeParameter(parameter: KtTypeParameter, data: D): R {

return visitNamedDeclaration(parameter,data)
}

open fun visitEnumEntry(enumEntry: KtEnumEntry, data: D): R {

return visitClass(enumEntry,data)
}

open fun visitParameterList(list: KtParameterList, data: D): R {

return visitKtElement(list,data)
}

open fun visitParameter(parameter: KtParameter, data: D): R {

return visitNamedDeclaration(parameter,data)
}

open fun visitSuperTypeList(list: KtSuperTypeList, data: D): R {

return visitKtElement(list,data)
}

open fun visitSuperTypeListEntry(specifier: KtSuperTypeListEntry, data: D): R {

return visitKtElement(specifier,data)
}

open fun visitDelegatedSuperTypeEntry(specifier: KtDelegatedSuperTypeEntry, data: D): R {

return visitSuperTypeListEntry(specifier,data)
}

open fun visitSuperTypeCallEntry(call: KtSuperTypeCallEntry, data: D): R {

return visitSuperTypeListEntry(call,data)
}

open fun visitSuperTypeEntry(specifier: KtSuperTypeEntry, data: D): R {

return visitSuperTypeListEntry(specifier,data)
}

open fun visitContextReceiverList(contextReceiverList: KtContextReceiverList, data: D): R {

@Suppress("UNCHECKED_CAST")
return null as R
}

open fun visitContextParameterList(contextParameterList: KtContextParameterList, data: D): R {

return visitKtElement(contextParameterList,data)
}

open fun visitContextReceiver(contextReceiver: KtContextReceiver, data: D): R {

return visitKtElement(contextReceiver,data)
}

open fun visitConstructorDelegationCall(call: KtConstructorDelegationCall, data: D): R {

return visitKtElement(call,data)
}

open fun visitPropertyDelegate(delegate: KtPropertyDelegate, data: D): R {

return visitKtElement(delegate,data)
}

open fun visitTypeReference(typeReference: KtTypeReference, data: D): R {

return visitKtElement(typeReference,data)
}

open fun visitValueArgumentList(list: KtValueArgumentList, data: D): R {

return visitKtElement(list,data)
}

open fun visitArgument(argument: KtValueArgument, data: D): R {

return visitKtElement(argument,data)
}

open fun visitExpression(expression: KtExpression, data: D): R {

return visitKtElement(expression,data)
}

open fun visitLoopExpression(loopExpression: KtLoopExpression, data: D): R {

return visitExpression(loopExpression,data)
}

open fun visitConstantExpression(expression: KtConstantExpression, data: D): R {

return visitExpression(expression,data)
}

open fun visitSimpleNameExpression(expression: KtSimpleNameExpression, data: D): R {

return visitReferenceExpression(expression,data)
}

open fun visitReferenceExpression(expression: KtReferenceExpression, data: D): R {

return visitExpression(expression,data)
}

open fun visitLabeledExpression(expression: KtLabeledExpression, data: D): R {

return visitExpressionWithLabel(expression,data)
}

open fun visitPrefixExpression(expression: KtPrefixExpression, data: D): R {

return visitUnaryExpression(expression,data)
}

open fun visitPostfixExpression(expression: KtPostfixExpression, data: D): R {

return visitUnaryExpression(expression,data)
}

open fun visitUnaryExpression(expression: KtUnaryExpression, data: D): R {

return visitExpression(expression,data)
}

open fun visitBinaryExpression(expression: KtBinaryExpression, data: D): R {

var flattenedStringConcatenationChildren: List<PsiElement>? = expression.tryFlattenStringConcatenationDescendants()
if (flattenedStringConcatenationChildren != null) {
for (childElement in flattenedStringConcatenationChildren) {
if (childElement is KtElement) {
((childElement as KtElement)).accept(this,data)
}
else {
childElement.accept(this)
}
}
@Suppress("UNCHECKED_CAST")
return null as R
}
else {
return visitExpression(expression,data)
}
}

open fun visitReturnExpression(expression: KtReturnExpression, data: D): R {

return visitExpressionWithLabel(expression,data)
}

open fun visitExpressionWithLabel(expression: KtExpressionWithLabel, data: D): R {

return visitExpression(expression,data)
}

open fun visitThrowExpression(expression: KtThrowExpression, data: D): R {

return visitExpression(expression,data)
}

open fun visitBreakExpression(expression: KtBreakExpression, data: D): R {

return visitExpressionWithLabel(expression,data)
}

open fun visitContinueExpression(expression: KtContinueExpression, data: D): R {

return visitExpressionWithLabel(expression,data)
}

open fun visitIfExpression(expression: KtIfExpression, data: D): R {

return visitExpression(expression,data)
}

open fun visitWhenExpression(expression: KtWhenExpression, data: D): R {

return visitExpression(expression,data)
}

open fun visitCollectionLiteralExpression(expression: KtCollectionLiteralExpression, data: D): R {

return visitExpression(expression,data)
}

open fun visitTryExpression(expression: KtTryExpression, data: D): R {

return visitExpression(expression,data)
}

open fun visitForExpression(expression: KtForExpression, data: D): R {

return visitLoopExpression(expression,data)
}

open fun visitWhileExpression(expression: KtWhileExpression, data: D): R {

return visitLoopExpression(expression,data)
}

open fun visitDoWhileExpression(expression: KtDoWhileExpression, data: D): R {

return visitLoopExpression(expression,data)
}

open fun visitLambdaExpression(expression: KtLambdaExpression, data: D): R {

return visitExpression(expression,data)
}

open fun visitAnnotatedExpression(expression: KtAnnotatedExpression, data: D): R {

return visitExpression(expression,data)
}

open fun visitCallExpression(expression: KtCallExpression, data: D): R {

return visitReferenceExpression(expression,data)
}

open fun visitArrayAccessExpression(expression: KtArrayAccessExpression, data: D): R {

return visitReferenceExpression(expression,data)
}

open fun visitQualifiedExpression(expression: KtQualifiedExpression, data: D): R {

return visitExpression(expression,data)
}

open fun visitDoubleColonExpression(expression: KtDoubleColonExpression, data: D): R {

return visitExpression(expression,data)
}

open fun visitCallableReferenceExpression(expression: KtCallableReferenceExpression, data: D): R {

return visitDoubleColonExpression(expression,data)
}

open fun visitClassLiteralExpression(expression: KtClassLiteralExpression, data: D): R {

return visitDoubleColonExpression(expression,data)
}

open fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression, data: D): R {

return visitQualifiedExpression(expression,data)
}

open fun visitSafeQualifiedExpression(expression: KtSafeQualifiedExpression, data: D): R {

return visitQualifiedExpression(expression,data)
}

open fun visitObjectLiteralExpression(expression: KtObjectLiteralExpression, data: D): R {

return visitExpression(expression,data)
}

open fun visitBlockExpression(expression: KtBlockExpression, data: D): R {

return visitExpression(expression,data)
}

open fun visitCatchSection(catchClause: KtCatchClause, data: D): R {

return visitKtElement(catchClause,data)
}

open fun visitFinallySection(finallySection: KtFinallySection, data: D): R {

return visitKtElement(finallySection,data)
}

open fun visitTypeArgumentList(typeArgumentList: KtTypeArgumentList, data: D): R {

return visitKtElement(typeArgumentList,data)
}

open fun visitThisExpression(expression: KtThisExpression, data: D): R {

return visitExpressionWithLabel(expression,data)
}

open fun visitSuperExpression(expression: KtSuperExpression, data: D): R {

return visitExpressionWithLabel(expression,data)
}

open fun visitParenthesizedExpression(expression: KtParenthesizedExpression, data: D): R {

return visitExpression(expression,data)
}

open fun visitInitializerList(list: KtInitializerList, data: D): R {

return visitKtElement(list,data)
}

open fun visitAnonymousInitializer(initializer: KtAnonymousInitializer, data: D): R {

return visitDeclaration(initializer,data)
}

open fun visitScriptInitializer(initializer: KtScriptInitializer, data: D): R {

return visitAnonymousInitializer(initializer,data)
}

open fun visitClassInitializer(initializer: KtClassInitializer, data: D): R {

return visitAnonymousInitializer(initializer,data)
}

open fun visitPropertyAccessor(accessor: KtPropertyAccessor, data: D): R {

return visitDeclaration(accessor,data)
}

open fun visitBackingField(accessor: KtBackingField, data: D): R {

return visitDeclaration(accessor,data)
}

open fun visitTypeConstraintList(list: KtTypeConstraintList, data: D): R {

return visitKtElement(list,data)
}

open fun visitTypeConstraint(constraint: KtTypeConstraint, data: D): R {

return visitKtElement(constraint,data)
}

private fun visitTypeElement(type: KtTypeElement, data: D): R {

return visitKtElement(type,data)
}

open fun visitUserType(type: KtUserType, data: D): R {

return visitTypeElement(type,data)
}

open fun visitDynamicType(type: KtDynamicType, data: D): R {

return visitTypeElement(type,data)
}

open fun visitFunctionType(type: KtFunctionType, data: D): R {

return visitTypeElement(type,data)
}

open fun visitSelfType(type: KtSelfType, data: D): R {

return visitTypeElement(type,data)
}

open fun visitBinaryWithTypeRHSExpression(expression: KtBinaryExpressionWithTypeRHS, data: D): R {

return visitExpression(expression,data)
}

open fun visitStringTemplateExpression(expression: KtStringTemplateExpression, data: D): R {

return visitExpression(expression,data)
}

open fun visitStringInterpolationPrefix(prefix: KtStringInterpolationPrefix, data: D): R {

return visitKtElement(prefix,data)
}

open fun visitNamedDeclaration(declaration: KtNamedDeclaration, data: D): R {

return visitDeclaration(declaration,data)
}

open fun visitNullableType(nullableType: KtNullableType, data: D): R {

return visitTypeElement(nullableType,data)
}

open fun visitIntersectionType(definitelyNotNullType: KtIntersectionType, data: D): R {

return visitTypeElement(definitelyNotNullType,data)
}

open fun visitTypeProjection(typeProjection: KtTypeProjection, data: D): R {

return visitKtElement(typeProjection,data)
}

open fun visitWhenEntry(ktWhenEntry: KtWhenEntry, data: D): R {

return visitKtElement(ktWhenEntry,data)
}

open fun visitIsExpression(expression: KtIsExpression, data: D): R {

return visitExpression(expression,data)
}

open fun visitWhenConditionIsPattern(condition: KtWhenConditionIsPattern, data: D): R {

return visitKtElement(condition,data)
}

open fun visitWhenConditionInRange(condition: KtWhenConditionInRange, data: D): R {

return visitKtElement(condition,data)
}

open fun visitWhenConditionWithExpression(condition: KtWhenConditionWithExpression, data: D): R {

return visitKtElement(condition,data)
}

open fun visitStringTemplateEntry(entry: KtStringTemplateEntry, data: D): R {

return visitKtElement(entry,data)
}

open fun visitStringTemplateEntryWithExpression(entry: KtStringTemplateEntryWithExpression, data: D): R {

return visitStringTemplateEntry(entry,data)
}

open fun visitBlockStringTemplateEntry(entry: KtBlockStringTemplateEntry, data: D): R {

return visitStringTemplateEntryWithExpression(entry,data)
}

open fun visitSimpleNameStringTemplateEntry(entry: KtSimpleNameStringTemplateEntry, data: D): R {

return visitStringTemplateEntryWithExpression(entry,data)
}

open fun visitLiteralStringTemplateEntry(entry: KtLiteralStringTemplateEntry, data: D): R {

return visitStringTemplateEntry(entry,data)
}

open fun visitEscapeStringTemplateEntry(entry: KtEscapeStringTemplateEntry, data: D): R {

return visitStringTemplateEntry(entry,data)
}

open fun visitPackageDirective(directive: KtPackageDirective, data: D): R {

return visitKtElement(directive,data)
}
}
