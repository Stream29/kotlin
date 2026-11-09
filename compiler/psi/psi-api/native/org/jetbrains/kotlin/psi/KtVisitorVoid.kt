/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi







open class KtVisitorVoid : KtVisitor<Nothing?,Nothing?>() {
open fun visitKtElement(element: KtElement): Unit {

super.visitKtElement(element,null)
}

open fun visitDeclaration(dcl: KtDeclaration): Unit {

super.visitDeclaration(dcl,null)
}

open fun visitClass(klass: KtClass): Unit {

super.visitClass(klass,null)
}

open fun visitClassOrObject(classOrObject: KtClassOrObject): Unit {

super.visitClassOrObject(classOrObject,null)
}

open fun visitConstructor(constructor: KtConstructor<*>): Unit {

super.visitConstructor(constructor,null)
}

open fun visitSecondaryConstructor(constructor: KtSecondaryConstructor): Unit {

super.visitSecondaryConstructor(constructor,null)
}

open fun visitPrimaryConstructor(constructor: KtPrimaryConstructor): Unit {

super.visitPrimaryConstructor(constructor,null)
}

open fun visitNamedFunction(function: KtNamedFunction): Unit {

super.visitNamedFunction(function,null)
}

open fun visitProperty(property: KtProperty): Unit {

super.visitProperty(property,null)
}

open fun visitTypeAlias(typeAlias: KtTypeAlias): Unit {

super.visitTypeAlias(typeAlias,null)
}

open fun visitDestructuringDeclaration(destructuringDeclaration: KtDestructuringDeclaration): Unit {

super.visitDestructuringDeclaration(destructuringDeclaration,null)
}

open fun visitDestructuringDeclarationEntry(multiDeclarationEntry: KtDestructuringDeclarationEntry): Unit {

super.visitDestructuringDeclarationEntry(multiDeclarationEntry,null)
}

open fun visitKtFile(file: KtFile): Unit {

super.visitKtFile(file,null)
}

open fun visitScript(script: KtScript): Unit {

super.visitScript(script,null)
}

open fun visitImportAlias(importAlias: KtImportAlias): Unit {

super.visitImportAlias(importAlias,null)
}

open fun visitImportDirective(importDirective: KtImportDirective): Unit {

super.visitImportDirective(importDirective,null)
}

open fun visitImportList(importList: KtImportList): Unit {

super.visitImportList(importList,null)
}

open fun visitClassBody(classBody: KtClassBody): Unit {

super.visitClassBody(classBody,null)
}

open fun visitCompanionBlock(companionBlock: KtCompanionBlock): Unit {

super.visitCompanionBlock(companionBlock,null)
}

open fun visitModifierList(list: KtModifierList): Unit {

super.visitModifierList(list,null)
}

open fun visitAnnotation(annotation: KtAnnotation): Unit {

super.visitAnnotation(annotation,null)
}

open fun visitAnnotationEntry(annotationEntry: KtAnnotationEntry): Unit {

super.visitAnnotationEntry(annotationEntry,null)
}

open fun visitConstructorCalleeExpression(constructorCalleeExpression: KtConstructorCalleeExpression): Unit {

super.visitConstructorCalleeExpression(constructorCalleeExpression,null)
}

open fun visitTypeParameterList(list: KtTypeParameterList): Unit {

super.visitTypeParameterList(list,null)
}

open fun visitTypeParameter(parameter: KtTypeParameter): Unit {

super.visitTypeParameter(parameter,null)
}

open fun visitEnumEntry(enumEntry: KtEnumEntry): Unit {

super.visitEnumEntry(enumEntry,null)
}

open fun visitParameterList(list: KtParameterList): Unit {

super.visitParameterList(list,null)
}

open fun visitParameter(parameter: KtParameter): Unit {

super.visitParameter(parameter,null)
}

open fun visitSuperTypeList(list: KtSuperTypeList): Unit {

super.visitSuperTypeList(list,null)
}

open fun visitSuperTypeListEntry(specifier: KtSuperTypeListEntry): Unit {

super.visitSuperTypeListEntry(specifier,null)
}

open fun visitDelegatedSuperTypeEntry(specifier: KtDelegatedSuperTypeEntry): Unit {

super.visitDelegatedSuperTypeEntry(specifier,null)
}

open fun visitSuperTypeCallEntry(call: KtSuperTypeCallEntry): Unit {

super.visitSuperTypeCallEntry(call,null)
}

open fun visitSuperTypeEntry(specifier: KtSuperTypeEntry): Unit {

super.visitSuperTypeEntry(specifier,null)
}

open fun visitContextReceiverList(contextReceiverList: KtContextReceiverList): Unit {

super.visitContextReceiverList(contextReceiverList,null)
}

open fun visitContextParameterList(contextParameterList: KtContextParameterList): Unit {

super.visitContextParameterList(contextParameterList,null)
}

open fun visitContextReceiver(contextReceiver: KtContextReceiver): Unit {

super.visitContextReceiver(contextReceiver,null)
}

open fun visitConstructorDelegationCall(call: KtConstructorDelegationCall): Unit {

super.visitConstructorDelegationCall(call,null)
}

open fun visitPropertyDelegate(delegate: KtPropertyDelegate): Unit {

super.visitPropertyDelegate(delegate,null)
}

open fun visitTypeReference(typeReference: KtTypeReference): Unit {

super.visitTypeReference(typeReference,null)
}

open fun visitValueArgumentList(list: KtValueArgumentList): Unit {

super.visitValueArgumentList(list,null)
}

open fun visitArgument(argument: KtValueArgument): Unit {

super.visitArgument(argument,null)
}

open fun visitExpression(expression: KtExpression): Unit {

super.visitExpression(expression,null)
}

open fun visitLoopExpression(loopExpression: KtLoopExpression): Unit {

super.visitLoopExpression(loopExpression,null)
}

open fun visitConstantExpression(expression: KtConstantExpression): Unit {

super.visitConstantExpression(expression,null)
}

open fun visitSimpleNameExpression(expression: KtSimpleNameExpression): Unit {

super.visitSimpleNameExpression(expression,null)
}

open fun visitReferenceExpression(expression: KtReferenceExpression): Unit {

super.visitReferenceExpression(expression,null)
}

open fun visitLabeledExpression(expression: KtLabeledExpression): Unit {

super.visitLabeledExpression(expression,null)
}

open fun visitPrefixExpression(expression: KtPrefixExpression): Unit {

super.visitPrefixExpression(expression,null)
}

open fun visitPostfixExpression(expression: KtPostfixExpression): Unit {

super.visitPostfixExpression(expression,null)
}

open fun visitUnaryExpression(expression: KtUnaryExpression): Unit {

super.visitUnaryExpression(expression,null)
}

open fun visitBinaryExpression(expression: KtBinaryExpression): Unit {

super.visitBinaryExpression(expression,null)
}

open fun visitReturnExpression(expression: KtReturnExpression): Unit {

super.visitReturnExpression(expression,null)
}

open fun visitExpressionWithLabel(expression: KtExpressionWithLabel): Unit {

super.visitExpressionWithLabel(expression,null)
}

open fun visitThrowExpression(expression: KtThrowExpression): Unit {

super.visitThrowExpression(expression,null)
}

open fun visitBreakExpression(expression: KtBreakExpression): Unit {

super.visitBreakExpression(expression,null)
}

open fun visitContinueExpression(expression: KtContinueExpression): Unit {

super.visitContinueExpression(expression,null)
}

open fun visitIfExpression(expression: KtIfExpression): Unit {

super.visitIfExpression(expression,null)
}

open fun visitWhenExpression(expression: KtWhenExpression): Unit {

super.visitWhenExpression(expression,null)
}

open fun visitCollectionLiteralExpression(expression: KtCollectionLiteralExpression): Unit {

super.visitCollectionLiteralExpression(expression,null)
}

open fun visitTryExpression(expression: KtTryExpression): Unit {

super.visitTryExpression(expression,null)
}

open fun visitForExpression(expression: KtForExpression): Unit {

super.visitForExpression(expression,null)
}

open fun visitWhileExpression(expression: KtWhileExpression): Unit {

super.visitWhileExpression(expression,null)
}

open fun visitDoWhileExpression(expression: KtDoWhileExpression): Unit {

super.visitDoWhileExpression(expression,null)
}

open fun visitLambdaExpression(lambdaExpression: KtLambdaExpression): Unit {

super.visitLambdaExpression(lambdaExpression,null)
}

open fun visitAnnotatedExpression(expression: KtAnnotatedExpression): Unit {

super.visitAnnotatedExpression(expression,null)
}

open fun visitCallExpression(expression: KtCallExpression): Unit {

super.visitCallExpression(expression,null)
}

open fun visitArrayAccessExpression(expression: KtArrayAccessExpression): Unit {

super.visitArrayAccessExpression(expression,null)
}

open fun visitQualifiedExpression(expression: KtQualifiedExpression): Unit {

super.visitQualifiedExpression(expression,null)
}

open fun visitDoubleColonExpression(expression: KtDoubleColonExpression): Unit {

super.visitDoubleColonExpression(expression,null)
}

open fun visitCallableReferenceExpression(expression: KtCallableReferenceExpression): Unit {

super.visitCallableReferenceExpression(expression,null)
}

open fun visitClassLiteralExpression(expression: KtClassLiteralExpression): Unit {

super.visitClassLiteralExpression(expression,null)
}

open fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression): Unit {

super.visitDotQualifiedExpression(expression,null)
}

open fun visitSafeQualifiedExpression(expression: KtSafeQualifiedExpression): Unit {

super.visitSafeQualifiedExpression(expression,null)
}

open fun visitObjectLiteralExpression(expression: KtObjectLiteralExpression): Unit {

super.visitObjectLiteralExpression(expression,null)
}

open fun visitBlockExpression(expression: KtBlockExpression): Unit {

super.visitBlockExpression(expression,null)
}

open fun visitCatchSection(catchClause: KtCatchClause): Unit {

super.visitCatchSection(catchClause,null)
}

open fun visitFinallySection(finallySection: KtFinallySection): Unit {

super.visitFinallySection(finallySection,null)
}

open fun visitTypeArgumentList(typeArgumentList: KtTypeArgumentList): Unit {

super.visitTypeArgumentList(typeArgumentList,null)
}

open fun visitThisExpression(expression: KtThisExpression): Unit {

super.visitThisExpression(expression,null)
}

open fun visitSuperExpression(expression: KtSuperExpression): Unit {

super.visitSuperExpression(expression,null)
}

open fun visitParenthesizedExpression(expression: KtParenthesizedExpression): Unit {

super.visitParenthesizedExpression(expression,null)
}

open fun visitInitializerList(list: KtInitializerList): Unit {

super.visitInitializerList(list,null)
}

open fun visitAnonymousInitializer(initializer: KtAnonymousInitializer): Unit {

super.visitAnonymousInitializer(initializer,null)
}

open fun visitScriptInitializer(initializer: KtScriptInitializer): Unit {

super.visitScriptInitializer(initializer,null)
}

open fun visitClassInitializer(initializer: KtClassInitializer): Unit {

super.visitClassInitializer(initializer,null)
}

open fun visitPropertyAccessor(accessor: KtPropertyAccessor): Unit {

super.visitPropertyAccessor(accessor,null)
}

open fun visitTypeConstraintList(list: KtTypeConstraintList): Unit {

super.visitTypeConstraintList(list,null)
}

open fun visitTypeConstraint(constraint: KtTypeConstraint): Unit {

super.visitTypeConstraint(constraint,null)
}

open fun visitUserType(type: KtUserType): Unit {

super.visitUserType(type,null)
}

open fun visitDynamicType(type: KtDynamicType): Unit {

super.visitDynamicType(type,null)
}

open fun visitFunctionType(type: KtFunctionType): Unit {

super.visitFunctionType(type,null)
}

open fun visitSelfType(type: KtSelfType): Unit {

super.visitSelfType(type,null)
}

open fun visitBinaryWithTypeRHSExpression(expression: KtBinaryExpressionWithTypeRHS): Unit {

super.visitBinaryWithTypeRHSExpression(expression,null)
}

open fun visitStringTemplateExpression(expression: KtStringTemplateExpression): Unit {

super.visitStringTemplateExpression(expression,null)
}

open fun visitNamedDeclaration(declaration: KtNamedDeclaration): Unit {

super.visitNamedDeclaration(declaration,null)
}

open fun visitNullableType(nullableType: KtNullableType): Unit {

super.visitNullableType(nullableType,null)
}

open fun visitIntersectionType(intersectionType: KtIntersectionType): Unit {

super.visitIntersectionType(intersectionType,null)
}

open fun visitTypeProjection(typeProjection: KtTypeProjection): Unit {

super.visitTypeProjection(typeProjection,null)
}

open fun visitWhenEntry(ktWhenEntry: KtWhenEntry): Unit {

super.visitWhenEntry(ktWhenEntry,null)
}

open fun visitIsExpression(expression: KtIsExpression): Unit {

super.visitIsExpression(expression,null)
}

open fun visitWhenConditionIsPattern(condition: KtWhenConditionIsPattern): Unit {

super.visitWhenConditionIsPattern(condition,null)
}

open fun visitWhenConditionInRange(condition: KtWhenConditionInRange): Unit {

super.visitWhenConditionInRange(condition,null)
}

open fun visitWhenConditionWithExpression(condition: KtWhenConditionWithExpression): Unit {

super.visitWhenConditionWithExpression(condition,null)
}

open fun visitObjectDeclaration(declaration: KtObjectDeclaration): Unit {

super.visitObjectDeclaration(declaration,null)
}

open fun visitStringTemplateEntry(entry: KtStringTemplateEntry): Unit {

super.visitStringTemplateEntry(entry,null)
}

open fun visitStringTemplateEntryWithExpression(entry: KtStringTemplateEntryWithExpression): Unit {

super.visitStringTemplateEntryWithExpression(entry,null)
}

open fun visitBlockStringTemplateEntry(entry: KtBlockStringTemplateEntry): Unit {

super.visitBlockStringTemplateEntry(entry,null)
}

open fun visitSimpleNameStringTemplateEntry(entry: KtSimpleNameStringTemplateEntry): Unit {

super.visitSimpleNameStringTemplateEntry(entry,null)
}

open fun visitLiteralStringTemplateEntry(entry: KtLiteralStringTemplateEntry): Unit {

super.visitLiteralStringTemplateEntry(entry,null)
}

open fun visitEscapeStringTemplateEntry(entry: KtEscapeStringTemplateEntry): Unit {

super.visitEscapeStringTemplateEntry(entry,null)
}

open fun visitPackageDirective(directive: KtPackageDirective): Unit {

super.visitPackageDirective(directive,null)
}

final override fun visitKtElement(element: KtElement, data: Nothing?): Nothing? {

visitKtElement(element)
return null
}

final override fun visitDeclaration(dcl: KtDeclaration, data: Nothing?): Nothing? {

visitDeclaration(dcl)
return null
}

final override fun visitClass(klass: KtClass, data: Nothing?): Nothing? {

visitClass(klass)
return null
}

final override fun visitClassOrObject(classOrObject: KtClassOrObject, data: Nothing?): Nothing? {

visitClassOrObject(classOrObject)
return null
}

final override fun visitConstructor(constructor: KtConstructor<*>, data: Nothing?): Nothing? {

visitConstructor(constructor)
return null
}

final override fun visitSecondaryConstructor(constructor: KtSecondaryConstructor, data: Nothing?): Nothing? {

visitSecondaryConstructor(constructor)
return null
}

final override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor, data: Nothing?): Nothing? {

visitPrimaryConstructor(constructor)
return null
}

final override fun visitNamedFunction(function: KtNamedFunction, data: Nothing?): Nothing? {

visitNamedFunction(function)
return null
}

final override fun visitProperty(property: KtProperty, data: Nothing?): Nothing? {

visitProperty(property)
return null
}

final override fun visitTypeAlias(typeAlias: KtTypeAlias, data: Nothing?): Nothing? {

visitTypeAlias(typeAlias)
return null
}

final override fun visitDestructuringDeclaration(multiDeclaration: KtDestructuringDeclaration, data: Nothing?): Nothing? {

visitDestructuringDeclaration(multiDeclaration)
return null
}

final override fun visitDestructuringDeclarationEntry(multiDeclarationEntry: KtDestructuringDeclarationEntry, data: Nothing?): Nothing? {

visitDestructuringDeclarationEntry(multiDeclarationEntry)
return null
}

final override fun visitKtFile(file: KtFile, data: Nothing?): Nothing? {

visitKtFile(file)
return null
}

final override fun visitScript(script: KtScript, data: Nothing?): Nothing? {

visitScript(script)
return null
}

final override fun visitImportDirective(importDirective: KtImportDirective, data: Nothing?): Nothing? {

visitImportDirective(importDirective)
return null
}

final override fun visitImportList(importList: KtImportList, data: Nothing?): Nothing? {

visitImportList(importList)
return null
}

final override fun visitClassBody(classBody: KtClassBody, data: Nothing?): Nothing? {

visitClassBody(classBody)
return null
}

final override fun visitCompanionBlock(companionBlock: KtCompanionBlock, data: Nothing?): Nothing? {

visitCompanionBlock(companionBlock)
return null
}

final override fun visitModifierList(list: KtModifierList, data: Nothing?): Nothing? {

visitModifierList(list)
return null
}

final override fun visitAnnotation(annotation: KtAnnotation, data: Nothing?): Nothing? {

visitAnnotation(annotation)
return null
}

final override fun visitAnnotationEntry(annotationEntry: KtAnnotationEntry, data: Nothing?): Nothing? {

visitAnnotationEntry(annotationEntry)
return null
}

final override fun visitConstructorCalleeExpression(constructorCalleeExpression: KtConstructorCalleeExpression, data: Nothing?): Nothing? {

visitConstructorCalleeExpression(constructorCalleeExpression)
return null
}

final override fun visitTypeParameterList(list: KtTypeParameterList, data: Nothing?): Nothing? {

visitTypeParameterList(list)
return null
}

final override fun visitTypeParameter(parameter: KtTypeParameter, data: Nothing?): Nothing? {

visitTypeParameter(parameter)
return null
}

final override fun visitEnumEntry(enumEntry: KtEnumEntry, data: Nothing?): Nothing? {

visitEnumEntry(enumEntry)
return null
}

final override fun visitParameterList(list: KtParameterList, data: Nothing?): Nothing? {

visitParameterList(list)
return null
}

final override fun visitParameter(parameter: KtParameter, data: Nothing?): Nothing? {

visitParameter(parameter)
return null
}

final override fun visitSuperTypeList(list: KtSuperTypeList, data: Nothing?): Nothing? {

visitSuperTypeList(list)
return null
}

final override fun visitSuperTypeListEntry(specifier: KtSuperTypeListEntry, data: Nothing?): Nothing? {

visitSuperTypeListEntry(specifier)
return null
}

final override fun visitDelegatedSuperTypeEntry(specifier: KtDelegatedSuperTypeEntry, data: Nothing?): Nothing? {

visitDelegatedSuperTypeEntry(specifier)
return null
}

final override fun visitSuperTypeCallEntry(call: KtSuperTypeCallEntry, data: Nothing?): Nothing? {

visitSuperTypeCallEntry(call)
return null
}

final override fun visitSuperTypeEntry(specifier: KtSuperTypeEntry, data: Nothing?): Nothing? {

visitSuperTypeEntry(specifier)
return null
}

final override fun visitContextReceiverList(contextReceiverList: KtContextReceiverList, data: Nothing?): Nothing? {

visitContextReceiverList(contextReceiverList)
return null
}

final override fun visitContextParameterList(contextParameterList: KtContextParameterList, data: Nothing?): Nothing? {

visitContextParameterList(contextParameterList)
return null
}

final override fun visitContextReceiver(contextReceiver: KtContextReceiver, data: Nothing?): Nothing? {

visitContextReceiver(contextReceiver)
return null
}

final override fun visitConstructorDelegationCall(call: KtConstructorDelegationCall, data: Nothing?): Nothing? {

visitConstructorDelegationCall(call)
return null
}

final override fun visitPropertyDelegate(delegate: KtPropertyDelegate, data: Nothing?): Nothing? {

visitPropertyDelegate(delegate)
return null
}

final override fun visitTypeReference(typeReference: KtTypeReference, data: Nothing?): Nothing? {

visitTypeReference(typeReference)
return null
}

final override fun visitValueArgumentList(list: KtValueArgumentList, data: Nothing?): Nothing? {

visitValueArgumentList(list)
return null
}

final override fun visitArgument(argument: KtValueArgument, data: Nothing?): Nothing? {

visitArgument(argument)
return null
}

final override fun visitExpression(expression: KtExpression, data: Nothing?): Nothing? {

visitExpression(expression)
return null
}

final override fun visitLoopExpression(loopExpression: KtLoopExpression, data: Nothing?): Nothing? {

visitLoopExpression(loopExpression)
return null
}

final override fun visitConstantExpression(expression: KtConstantExpression, data: Nothing?): Nothing? {

visitConstantExpression(expression)
return null
}

final override fun visitSimpleNameExpression(expression: KtSimpleNameExpression, data: Nothing?): Nothing? {

visitSimpleNameExpression(expression)
return null
}

final override fun visitReferenceExpression(expression: KtReferenceExpression, data: Nothing?): Nothing? {

visitReferenceExpression(expression)
return null
}

final override fun visitLabeledExpression(expression: KtLabeledExpression, data: Nothing?): Nothing? {

visitLabeledExpression(expression)
return null
}

final override fun visitPrefixExpression(expression: KtPrefixExpression, data: Nothing?): Nothing? {

visitPrefixExpression(expression)
return null
}

final override fun visitPostfixExpression(expression: KtPostfixExpression, data: Nothing?): Nothing? {

visitPostfixExpression(expression)
return null
}

final override fun visitUnaryExpression(expression: KtUnaryExpression, data: Nothing?): Nothing? {

visitUnaryExpression(expression)
return null
}

final override fun visitBinaryExpression(expression: KtBinaryExpression, data: Nothing?): Nothing? {

visitBinaryExpression(expression)
return null
}

final override fun visitReturnExpression(expression: KtReturnExpression, data: Nothing?): Nothing? {

visitReturnExpression(expression)
return null
}

final override fun visitExpressionWithLabel(expression: KtExpressionWithLabel, data: Nothing?): Nothing? {

visitExpressionWithLabel(expression)
return null
}

final override fun visitThrowExpression(expression: KtThrowExpression, data: Nothing?): Nothing? {

visitThrowExpression(expression)
return null
}

final override fun visitBreakExpression(expression: KtBreakExpression, data: Nothing?): Nothing? {

visitBreakExpression(expression)
return null
}

final override fun visitContinueExpression(expression: KtContinueExpression, data: Nothing?): Nothing? {

visitContinueExpression(expression)
return null
}

final override fun visitIfExpression(expression: KtIfExpression, data: Nothing?): Nothing? {

visitIfExpression(expression)
return null
}

final override fun visitWhenExpression(expression: KtWhenExpression, data: Nothing?): Nothing? {

visitWhenExpression(expression)
return null
}

final override fun visitCollectionLiteralExpression(expression: KtCollectionLiteralExpression, data: Nothing?): Nothing? {

visitCollectionLiteralExpression(expression)
return null
}

final override fun visitTryExpression(expression: KtTryExpression, data: Nothing?): Nothing? {

visitTryExpression(expression)
return null
}

final override fun visitForExpression(expression: KtForExpression, data: Nothing?): Nothing? {

visitForExpression(expression)
return null
}

final override fun visitWhileExpression(expression: KtWhileExpression, data: Nothing?): Nothing? {

visitWhileExpression(expression)
return null
}

final override fun visitDoWhileExpression(expression: KtDoWhileExpression, data: Nothing?): Nothing? {

visitDoWhileExpression(expression)
return null
}

final override fun visitLambdaExpression(expression: KtLambdaExpression, data: Nothing?): Nothing? {

visitLambdaExpression(expression)
return null
}

final override fun visitAnnotatedExpression(expression: KtAnnotatedExpression, data: Nothing?): Nothing? {

visitAnnotatedExpression(expression)
return null
}

final override fun visitCallExpression(expression: KtCallExpression, data: Nothing?): Nothing? {

visitCallExpression(expression)
return null
}

final override fun visitArrayAccessExpression(expression: KtArrayAccessExpression, data: Nothing?): Nothing? {

visitArrayAccessExpression(expression)
return null
}

final override fun visitQualifiedExpression(expression: KtQualifiedExpression, data: Nothing?): Nothing? {

visitQualifiedExpression(expression)
return null
}

final override fun visitDoubleColonExpression(expression: KtDoubleColonExpression, data: Nothing?): Nothing? {

visitDoubleColonExpression(expression)
return null
}

final override fun visitCallableReferenceExpression(expression: KtCallableReferenceExpression, data: Nothing?): Nothing? {

visitCallableReferenceExpression(expression)
return null
}

final override fun visitClassLiteralExpression(expression: KtClassLiteralExpression, data: Nothing?): Nothing? {

visitClassLiteralExpression(expression)
return null
}

final override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression, data: Nothing?): Nothing? {

visitDotQualifiedExpression(expression)
return null
}

final override fun visitSafeQualifiedExpression(expression: KtSafeQualifiedExpression, data: Nothing?): Nothing? {

visitSafeQualifiedExpression(expression)
return null
}

final override fun visitObjectLiteralExpression(expression: KtObjectLiteralExpression, data: Nothing?): Nothing? {

visitObjectLiteralExpression(expression)
return null
}

final override fun visitBlockExpression(expression: KtBlockExpression, data: Nothing?): Nothing? {

visitBlockExpression(expression)
return null
}

final override fun visitCatchSection(catchClause: KtCatchClause, data: Nothing?): Nothing? {

visitCatchSection(catchClause)
return null
}

final override fun visitFinallySection(finallySection: KtFinallySection, data: Nothing?): Nothing? {

visitFinallySection(finallySection)
return null
}

final override fun visitTypeArgumentList(typeArgumentList: KtTypeArgumentList, data: Nothing?): Nothing? {

visitTypeArgumentList(typeArgumentList)
return null
}

final override fun visitThisExpression(expression: KtThisExpression, data: Nothing?): Nothing? {

visitThisExpression(expression)
return null
}

final override fun visitSuperExpression(expression: KtSuperExpression, data: Nothing?): Nothing? {

visitSuperExpression(expression)
return null
}

final override fun visitParenthesizedExpression(expression: KtParenthesizedExpression, data: Nothing?): Nothing? {

visitParenthesizedExpression(expression)
return null
}

final override fun visitInitializerList(list: KtInitializerList, data: Nothing?): Nothing? {

visitInitializerList(list)
return null
}

final override fun visitAnonymousInitializer(initializer: KtAnonymousInitializer, data: Nothing?): Nothing? {

visitAnonymousInitializer(initializer)
return null
}

final override fun visitPropertyAccessor(accessor: KtPropertyAccessor, data: Nothing?): Nothing? {

visitPropertyAccessor(accessor)
return null
}

final override fun visitTypeConstraintList(list: KtTypeConstraintList, data: Nothing?): Nothing? {

visitTypeConstraintList(list)
return null
}

final override fun visitTypeConstraint(constraint: KtTypeConstraint, data: Nothing?): Nothing? {

visitTypeConstraint(constraint)
return null
}

final override fun visitUserType(type: KtUserType, data: Nothing?): Nothing? {

visitUserType(type)
return null
}

final override fun visitDynamicType(type: KtDynamicType, data: Nothing?): Nothing? {

visitDynamicType(type)
return null
}

final override fun visitFunctionType(type: KtFunctionType, data: Nothing?): Nothing? {

visitFunctionType(type)
return null
}

final override fun visitSelfType(type: KtSelfType, data: Nothing?): Nothing? {

visitSelfType(type)
return null
}

final override fun visitBinaryWithTypeRHSExpression(expression: KtBinaryExpressionWithTypeRHS, data: Nothing?): Nothing? {

visitBinaryWithTypeRHSExpression(expression)
return null
}

final override fun visitStringTemplateExpression(expression: KtStringTemplateExpression, data: Nothing?): Nothing? {

visitStringTemplateExpression(expression)
return null
}

final override fun visitNamedDeclaration(declaration: KtNamedDeclaration, data: Nothing?): Nothing? {

visitNamedDeclaration(declaration)
return null
}

final override fun visitNullableType(nullableType: KtNullableType, data: Nothing?): Nothing? {

visitNullableType(nullableType)
return null
}

final override fun visitIntersectionType(intersectionType: KtIntersectionType, data: Nothing?): Nothing? {

visitIntersectionType(intersectionType)
return null
}

final override fun visitTypeProjection(typeProjection: KtTypeProjection, data: Nothing?): Nothing? {

visitTypeProjection(typeProjection)
return null
}

final override fun visitWhenEntry(ktWhenEntry: KtWhenEntry, data: Nothing?): Nothing? {

visitWhenEntry(ktWhenEntry)
return null
}

final override fun visitIsExpression(expression: KtIsExpression, data: Nothing?): Nothing? {

visitIsExpression(expression)
return null
}

final override fun visitWhenConditionIsPattern(condition: KtWhenConditionIsPattern, data: Nothing?): Nothing? {

visitWhenConditionIsPattern(condition)
return null
}

final override fun visitWhenConditionInRange(condition: KtWhenConditionInRange, data: Nothing?): Nothing? {

visitWhenConditionInRange(condition)
return null
}

final override fun visitWhenConditionWithExpression(condition: KtWhenConditionWithExpression, data: Nothing?): Nothing? {

visitWhenConditionWithExpression(condition)
return null
}

final override fun visitObjectDeclaration(declaration: KtObjectDeclaration, data: Nothing?): Nothing? {

visitObjectDeclaration(declaration)
return null
}

final override fun visitStringTemplateEntry(entry: KtStringTemplateEntry, data: Nothing?): Nothing? {

visitStringTemplateEntry(entry)
return null
}

final override fun visitStringTemplateEntryWithExpression(entry: KtStringTemplateEntryWithExpression, data: Nothing?): Nothing? {

visitStringTemplateEntryWithExpression(entry)
return null
}

final override fun visitBlockStringTemplateEntry(entry: KtBlockStringTemplateEntry, data: Nothing?): Nothing? {

visitBlockStringTemplateEntry(entry)
return null
}

final override fun visitSimpleNameStringTemplateEntry(entry: KtSimpleNameStringTemplateEntry, data: Nothing?): Nothing? {

visitSimpleNameStringTemplateEntry(entry)
return null
}

final override fun visitLiteralStringTemplateEntry(entry: KtLiteralStringTemplateEntry, data: Nothing?): Nothing? {

visitLiteralStringTemplateEntry(entry)
return null
}

final override fun visitEscapeStringTemplateEntry(entry: KtEscapeStringTemplateEntry, data: Nothing?): Nothing? {

visitEscapeStringTemplateEntry(entry)
return null
}

final override fun visitPackageDirective(directive: KtPackageDirective, data: Nothing?): Nothing? {

visitPackageDirective(directive)
return null
}

final override fun visitScriptInitializer(initializer: KtScriptInitializer, data: Nothing?): Nothing? {

visitScriptInitializer(initializer)
return null
}

final override fun visitClassInitializer(initializer: KtClassInitializer, data: Nothing?): Nothing? {

visitClassInitializer(initializer)
return null
}
}
