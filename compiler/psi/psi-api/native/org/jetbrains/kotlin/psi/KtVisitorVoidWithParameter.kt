/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi







open class KtVisitorVoidWithParameter<P> : KtVisitor<Nothing?,P>() {
open fun visitKtElementVoid(element: KtElement, data: P): Unit {

super.visitKtElement(element,data)
}

open fun visitDeclarationVoid(dcl: KtDeclaration, data: P): Unit {

super.visitDeclaration(dcl,data)
}

open fun visitClassVoid(klass: KtClass, data: P): Unit {

super.visitClass(klass,data)
}

open fun visitSecondaryConstructorVoid(constructor: KtSecondaryConstructor, data: P): Unit {

super.visitSecondaryConstructor(constructor,data)
}

open fun visitPrimaryConstructorVoid(constructor: KtPrimaryConstructor, data: P): Unit {

super.visitPrimaryConstructor(constructor,data)
}

open fun visitNamedFunctionVoid(function: KtNamedFunction, data: P): Unit {

super.visitNamedFunction(function,data)
}

open fun visitPropertyVoid(property: KtProperty, data: P): Unit {

super.visitProperty(property,data)
}

open fun visitDestructuringDeclarationVoid(multiDeclaration: KtDestructuringDeclaration, data: P): Unit {

super.visitDestructuringDeclaration(multiDeclaration,data)
}

open fun visitDestructuringDeclarationEntryVoid(multiDeclarationEntry: KtDestructuringDeclarationEntry, data: P): Unit {

super.visitDestructuringDeclarationEntry(multiDeclarationEntry,data)
}

open fun visitKtFileVoid(file: KtFile, data: P): Unit {

super.visitKtFile(file,data)
}

open fun visitScriptVoid(script: KtScript, data: P): Unit {

super.visitScript(script,data)
}

open fun visitImportDirectiveVoid(importDirective: KtImportDirective, data: P): Unit {

super.visitImportDirective(importDirective,data)
}

open fun visitImportAliasVoid(importAlias: KtImportAlias, data: P): Unit {

super.visitImportAlias(importAlias,data)
}

open fun visitImportListVoid(importList: KtImportList, data: P): Unit {

super.visitImportList(importList,data)
}

open fun visitClassBodyVoid(classBody: KtClassBody, data: P): Unit {

super.visitClassBody(classBody,data)
}

open fun visitCompanionBlockVoid(companionBlock: KtCompanionBlock, data: P): Unit {

super.visitCompanionBlock(companionBlock,data)
}

open fun visitModifierListVoid(list: KtModifierList, data: P): Unit {

super.visitModifierList(list,data)
}

open fun visitAnnotationVoid(annotation: KtAnnotation, data: P): Unit {

super.visitAnnotation(annotation,data)
}

open fun visitAnnotationEntryVoid(annotationEntry: KtAnnotationEntry, data: P): Unit {

super.visitAnnotationEntry(annotationEntry,data)
}

open fun visitConstructorCalleeExpressionVoid(constructorCalleeExpression: KtConstructorCalleeExpression, data: P): Unit {

super.visitConstructorCalleeExpression(constructorCalleeExpression,data)
}

open fun visitTypeParameterListVoid(list: KtTypeParameterList, data: P): Unit {

super.visitTypeParameterList(list,data)
}

open fun visitTypeParameterVoid(parameter: KtTypeParameter, data: P): Unit {

super.visitTypeParameter(parameter,data)
}

open fun visitEnumEntryVoid(enumEntry: KtEnumEntry, data: P): Unit {

super.visitEnumEntry(enumEntry,data)
}

open fun visitParameterListVoid(list: KtParameterList, data: P): Unit {

super.visitParameterList(list,data)
}

open fun visitParameterVoid(parameter: KtParameter, data: P): Unit {

super.visitParameter(parameter,data)
}

open fun visitSuperTypeListVoid(list: KtSuperTypeList, data: P): Unit {

super.visitSuperTypeList(list,data)
}

open fun visitSuperTypeListEntryVoid(specifier: KtSuperTypeListEntry, data: P): Unit {

super.visitSuperTypeListEntry(specifier,data)
}

open fun visitDelegatedSuperTypeEntryVoid(specifier: KtDelegatedSuperTypeEntry, data: P): Unit {

super.visitDelegatedSuperTypeEntry(specifier,data)
}

open fun visitSuperTypeCallEntryVoid(call: KtSuperTypeCallEntry, data: P): Unit {

super.visitSuperTypeCallEntry(call,data)
}

open fun visitSuperTypeEntryVoid(specifier: KtSuperTypeEntry, data: P): Unit {

super.visitSuperTypeEntry(specifier,data)
}

open fun visitConstructorDelegationCallVoid(call: KtConstructorDelegationCall, data: P): Unit {

super.visitConstructorDelegationCall(call,data)
}

open fun visitPropertyDelegateVoid(delegate: KtPropertyDelegate, data: P): Unit {

super.visitPropertyDelegate(delegate,data)
}

open fun visitTypeReferenceVoid(typeReference: KtTypeReference, data: P): Unit {

super.visitTypeReference(typeReference,data)
}

open fun visitValueArgumentListVoid(list: KtValueArgumentList, data: P): Unit {

super.visitValueArgumentList(list,data)
}

open fun visitArgumentVoid(argument: KtValueArgument, data: P): Unit {

super.visitArgument(argument,data)
}

open fun visitExpressionVoid(expression: KtExpression, data: P): Unit {

super.visitExpression(expression,data)
}

open fun visitLoopExpressionVoid(loopExpression: KtLoopExpression, data: P): Unit {

super.visitLoopExpression(loopExpression,data)
}

open fun visitConstantExpressionVoid(expression: KtConstantExpression, data: P): Unit {

super.visitConstantExpression(expression,data)
}

open fun visitSimpleNameExpressionVoid(expression: KtSimpleNameExpression, data: P): Unit {

super.visitSimpleNameExpression(expression,data)
}

open fun visitReferenceExpressionVoid(expression: KtReferenceExpression, data: P): Unit {

super.visitReferenceExpression(expression,data)
}

open fun visitLabeledExpressionVoid(expression: KtLabeledExpression, data: P): Unit {

super.visitLabeledExpression(expression,data)
}

open fun visitPrefixExpressionVoid(expression: KtPrefixExpression, data: P): Unit {

super.visitPrefixExpression(expression,data)
}

open fun visitPostfixExpressionVoid(expression: KtPostfixExpression, data: P): Unit {

super.visitPostfixExpression(expression,data)
}

open fun visitUnaryExpressionVoid(expression: KtUnaryExpression, data: P): Unit {

super.visitUnaryExpression(expression,data)
}

open fun visitBinaryExpressionVoid(expression: KtBinaryExpression, data: P): Unit {

super.visitBinaryExpression(expression,data)
}

open fun visitReturnExpressionVoid(expression: KtReturnExpression, data: P): Unit {

super.visitReturnExpression(expression,data)
}

open fun visitExpressionWithLabelVoid(expression: KtExpressionWithLabel, data: P): Unit {

super.visitExpressionWithLabel(expression,data)
}

open fun visitThrowExpressionVoid(expression: KtThrowExpression, data: P): Unit {

super.visitThrowExpression(expression,data)
}

open fun visitBreakExpressionVoid(expression: KtBreakExpression, data: P): Unit {

super.visitBreakExpression(expression,data)
}

open fun visitContinueExpressionVoid(expression: KtContinueExpression, data: P): Unit {

super.visitContinueExpression(expression,data)
}

open fun visitIfExpressionVoid(expression: KtIfExpression, data: P): Unit {

super.visitIfExpression(expression,data)
}

open fun visitWhenExpressionVoid(expression: KtWhenExpression, data: P): Unit {

super.visitWhenExpression(expression,data)
}

open fun visitTryExpressionVoid(expression: KtTryExpression, data: P): Unit {

super.visitTryExpression(expression,data)
}

open fun visitForExpressionVoid(expression: KtForExpression, data: P): Unit {

super.visitForExpression(expression,data)
}

open fun visitWhileExpressionVoid(expression: KtWhileExpression, data: P): Unit {

super.visitWhileExpression(expression,data)
}

open fun visitDoWhileExpressionVoid(expression: KtDoWhileExpression, data: P): Unit {

super.visitDoWhileExpression(expression,data)
}

open fun visitLambdaExpressionVoid(expression: KtLambdaExpression, data: P): Unit {

super.visitLambdaExpression(expression,data)
}

open fun visitAnnotatedExpressionVoid(expression: KtAnnotatedExpression, data: P): Unit {

super.visitAnnotatedExpression(expression,data)
}

open fun visitCallExpressionVoid(expression: KtCallExpression, data: P): Unit {

super.visitCallExpression(expression,data)
}

open fun visitArrayAccessExpressionVoid(expression: KtArrayAccessExpression, data: P): Unit {

super.visitArrayAccessExpression(expression,data)
}

open fun visitQualifiedExpressionVoid(expression: KtQualifiedExpression, data: P): Unit {

super.visitQualifiedExpression(expression,data)
}

open fun visitDoubleColonExpressionVoid(expression: KtDoubleColonExpression, data: P): Unit {

super.visitDoubleColonExpression(expression,data)
}

open fun visitCallableReferenceExpressionVoid(expression: KtCallableReferenceExpression, data: P): Unit {

super.visitCallableReferenceExpression(expression,data)
}

open fun visitClassLiteralExpressionVoid(expression: KtClassLiteralExpression, data: P): Unit {

super.visitClassLiteralExpression(expression,data)
}

open fun visitDotQualifiedExpressionVoid(expression: KtDotQualifiedExpression, data: P): Unit {

super.visitDotQualifiedExpression(expression,data)
}

open fun visitSafeQualifiedExpressionVoid(expression: KtSafeQualifiedExpression, data: P): Unit {

super.visitSafeQualifiedExpression(expression,data)
}

open fun visitObjectLiteralExpressionVoid(expression: KtObjectLiteralExpression, data: P): Unit {

super.visitObjectLiteralExpression(expression,data)
}

open fun visitBlockExpressionVoid(expression: KtBlockExpression, data: P): Unit {

super.visitBlockExpression(expression,data)
}

open fun visitCatchSectionVoid(catchClause: KtCatchClause, data: P): Unit {

super.visitCatchSection(catchClause,data)
}

open fun visitFinallySectionVoid(finallySection: KtFinallySection, data: P): Unit {

super.visitFinallySection(finallySection,data)
}

open fun visitTypeArgumentListVoid(typeArgumentList: KtTypeArgumentList, data: P): Unit {

super.visitTypeArgumentList(typeArgumentList,data)
}

open fun visitThisExpressionVoid(expression: KtThisExpression, data: P): Unit {

super.visitThisExpression(expression,data)
}

open fun visitSuperExpressionVoid(expression: KtSuperExpression, data: P): Unit {

super.visitSuperExpression(expression,data)
}

open fun visitParenthesizedExpressionVoid(expression: KtParenthesizedExpression, data: P): Unit {

super.visitParenthesizedExpression(expression,data)
}

open fun visitInitializerListVoid(list: KtInitializerList, data: P): Unit {

super.visitInitializerList(list,data)
}

open fun visitAnonymousInitializerVoid(initializer: KtAnonymousInitializer, data: P): Unit {

super.visitAnonymousInitializer(initializer,data)
}

open fun visitPropertyAccessorVoid(accessor: KtPropertyAccessor, data: P): Unit {

super.visitPropertyAccessor(accessor,data)
}

open fun visitTypeConstraintListVoid(list: KtTypeConstraintList, data: P): Unit {

super.visitTypeConstraintList(list,data)
}

open fun visitTypeConstraintVoid(constraint: KtTypeConstraint, data: P): Unit {

super.visitTypeConstraint(constraint,data)
}

open fun visitUserTypeVoid(type: KtUserType, data: P): Unit {

super.visitUserType(type,data)
}

open fun visitDynamicTypeVoid(type: KtDynamicType, data: P): Unit {

super.visitDynamicType(type,data)
}

open fun visitFunctionTypeVoid(type: KtFunctionType, data: P): Unit {

super.visitFunctionType(type,data)
}

open fun visitSelfTypeVoid(type: KtSelfType, data: P): Unit {

super.visitSelfType(type,data)
}

open fun visitBinaryWithTypeRHSExpressionVoid(expression: KtBinaryExpressionWithTypeRHS, data: P): Unit {

super.visitBinaryWithTypeRHSExpression(expression,data)
}

open fun visitStringTemplateExpressionVoid(expression: KtStringTemplateExpression, data: P): Unit {

super.visitStringTemplateExpression(expression,data)
}

open fun visitNamedDeclarationVoid(declaration: KtNamedDeclaration, data: P): Unit {

super.visitNamedDeclaration(declaration,data)
}

open fun visitNullableTypeVoid(nullableType: KtNullableType, data: P): Unit {

super.visitNullableType(nullableType,data)
}

open fun visitTypeProjectionVoid(typeProjection: KtTypeProjection, data: P): Unit {

super.visitTypeProjection(typeProjection,data)
}

open fun visitWhenEntryVoid(ktWhenEntry: KtWhenEntry, data: P): Unit {

super.visitWhenEntry(ktWhenEntry,data)
}

open fun visitCollectionLiteralExpressionVoid(expression: KtCollectionLiteralExpression, data: P): Unit {

super.visitCollectionLiteralExpression(expression,data)
}

open fun visitIsExpressionVoid(expression: KtIsExpression, data: P): Unit {

super.visitIsExpression(expression,data)
}

open fun visitWhenConditionIsPatternVoid(condition: KtWhenConditionIsPattern, data: P): Unit {

super.visitWhenConditionIsPattern(condition,data)
}

open fun visitWhenConditionInRangeVoid(condition: KtWhenConditionInRange, data: P): Unit {

super.visitWhenConditionInRange(condition,data)
}

open fun visitWhenConditionWithExpressionVoid(condition: KtWhenConditionWithExpression, data: P): Unit {

super.visitWhenConditionWithExpression(condition,data)
}

open fun visitObjectDeclarationVoid(declaration: KtObjectDeclaration, data: P): Unit {

super.visitObjectDeclaration(declaration,data)
}

open fun visitStringTemplateEntryVoid(entry: KtStringTemplateEntry, data: P): Unit {

super.visitStringTemplateEntry(entry,data)
}

open fun visitStringTemplateEntryWithExpressionVoid(entry: KtStringTemplateEntryWithExpression, data: P): Unit {

super.visitStringTemplateEntryWithExpression(entry,data)
}

open fun visitBlockStringTemplateEntryVoid(entry: KtBlockStringTemplateEntry, data: P): Unit {

super.visitBlockStringTemplateEntry(entry,data)
}

open fun visitSimpleNameStringTemplateEntryVoid(entry: KtSimpleNameStringTemplateEntry, data: P): Unit {

super.visitSimpleNameStringTemplateEntry(entry,data)
}

open fun visitLiteralStringTemplateEntryVoid(entry: KtLiteralStringTemplateEntry, data: P): Unit {

super.visitLiteralStringTemplateEntry(entry,data)
}

open fun visitEscapeStringTemplateEntryVoid(entry: KtEscapeStringTemplateEntry, data: P): Unit {

super.visitEscapeStringTemplateEntry(entry,data)
}

final override fun visitKtElement(element: KtElement, data: P): Nothing? {

visitKtElementVoid(element,data)
return null
}

final override fun visitDeclaration(dcl: KtDeclaration, data: P): Nothing? {

visitDeclarationVoid(dcl,data)
return null
}

final override fun visitClass(klass: KtClass, data: P): Nothing? {

visitClassVoid(klass,data)
return null
}

final override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor, data: P): Nothing? {

visitPrimaryConstructorVoid(constructor,data)
return null
}

final override fun visitNamedFunction(function: KtNamedFunction, data: P): Nothing? {

visitNamedFunctionVoid(function,data)
return null
}

final override fun visitProperty(property: KtProperty, data: P): Nothing? {

visitPropertyVoid(property,data)
return null
}

final override fun visitDestructuringDeclaration(multiDeclaration: KtDestructuringDeclaration, data: P): Nothing? {

visitDestructuringDeclarationVoid(multiDeclaration,data)
return null
}

final override fun visitDestructuringDeclarationEntry(multiDeclarationEntry: KtDestructuringDeclarationEntry, data: P): Nothing? {

visitDestructuringDeclarationEntryVoid(multiDeclarationEntry,data)
return null
}

final override fun visitKtFile(file: KtFile, data: P): Nothing? {

visitKtFileVoid(file,data)
return null
}

final override fun visitScript(script: KtScript, data: P): Nothing? {

visitScriptVoid(script,data)
return null
}

final override fun visitImportDirective(importDirective: KtImportDirective, data: P): Nothing? {

visitImportDirectiveVoid(importDirective,data)
return null
}

final override fun visitImportList(importList: KtImportList, data: P): Nothing? {

visitImportListVoid(importList,data)
return null
}

final override fun visitClassBody(classBody: KtClassBody, data: P): Nothing? {

visitClassBodyVoid(classBody,data)
return null
}

final override fun visitCompanionBlock(companionBlock: KtCompanionBlock, data: P): Nothing? {

visitCompanionBlockVoid(companionBlock,data)
return null
}

final override fun visitModifierList(list: KtModifierList, data: P): Nothing? {

visitModifierListVoid(list,data)
return null
}

final override fun visitAnnotation(annotation: KtAnnotation, data: P): Nothing? {

visitAnnotationVoid(annotation,data)
return null
}

final override fun visitAnnotationEntry(annotationEntry: KtAnnotationEntry, data: P): Nothing? {

visitAnnotationEntryVoid(annotationEntry,data)
return null
}

final override fun visitTypeParameterList(list: KtTypeParameterList, data: P): Nothing? {

visitTypeParameterListVoid(list,data)
return null
}

final override fun visitTypeParameter(parameter: KtTypeParameter, data: P): Nothing? {

visitTypeParameterVoid(parameter,data)
return null
}

final override fun visitEnumEntry(enumEntry: KtEnumEntry, data: P): Nothing? {

visitEnumEntryVoid(enumEntry,data)
return null
}

final override fun visitParameterList(list: KtParameterList, data: P): Nothing? {

visitParameterListVoid(list,data)
return null
}

final override fun visitParameter(parameter: KtParameter, data: P): Nothing? {

visitParameterVoid(parameter,data)
return null
}

final override fun visitSuperTypeList(list: KtSuperTypeList, data: P): Nothing? {

visitSuperTypeListVoid(list,data)
return null
}

final override fun visitSuperTypeListEntry(specifier: KtSuperTypeListEntry, data: P): Nothing? {

visitSuperTypeListEntryVoid(specifier,data)
return null
}

final override fun visitDelegatedSuperTypeEntry(specifier: KtDelegatedSuperTypeEntry, data: P): Nothing? {

visitDelegatedSuperTypeEntryVoid(specifier,data)
return null
}

final override fun visitSuperTypeCallEntry(call: KtSuperTypeCallEntry, data: P): Nothing? {

visitSuperTypeCallEntryVoid(call,data)
return null
}

final override fun visitSuperTypeEntry(specifier: KtSuperTypeEntry, data: P): Nothing? {

visitSuperTypeEntryVoid(specifier,data)
return null
}

final override fun visitConstructorDelegationCall(call: KtConstructorDelegationCall, data: P): Nothing? {

visitConstructorDelegationCallVoid(call,data)
return null
}

final override fun visitPropertyDelegate(delegate: KtPropertyDelegate, data: P): Nothing? {

visitPropertyDelegateVoid(delegate,data)
return null
}

final override fun visitTypeReference(typeReference: KtTypeReference, data: P): Nothing? {

visitTypeReferenceVoid(typeReference,data)
return null
}

final override fun visitValueArgumentList(list: KtValueArgumentList, data: P): Nothing? {

visitValueArgumentListVoid(list,data)
return null
}

final override fun visitArgument(argument: KtValueArgument, data: P): Nothing? {

visitArgumentVoid(argument,data)
return null
}

final override fun visitExpression(expression: KtExpression, data: P): Nothing? {

visitExpressionVoid(expression,data)
return null
}

final override fun visitLoopExpression(loopExpression: KtLoopExpression, data: P): Nothing? {

visitLoopExpressionVoid(loopExpression,data)
return null
}

final override fun visitConstantExpression(expression: KtConstantExpression, data: P): Nothing? {

visitConstantExpressionVoid(expression,data)
return null
}

final override fun visitSimpleNameExpression(expression: KtSimpleNameExpression, data: P): Nothing? {

visitSimpleNameExpressionVoid(expression,data)
return null
}

final override fun visitReferenceExpression(expression: KtReferenceExpression, data: P): Nothing? {

visitReferenceExpressionVoid(expression,data)
return null
}

final override fun visitLabeledExpression(expression: KtLabeledExpression, data: P): Nothing? {

visitLabeledExpressionVoid(expression,data)
return null
}

final override fun visitPrefixExpression(expression: KtPrefixExpression, data: P): Nothing? {

visitPrefixExpressionVoid(expression,data)
return null
}

final override fun visitPostfixExpression(expression: KtPostfixExpression, data: P): Nothing? {

visitPostfixExpressionVoid(expression,data)
return null
}

final override fun visitUnaryExpression(expression: KtUnaryExpression, data: P): Nothing? {

visitUnaryExpressionVoid(expression,data)
return null
}

final override fun visitBinaryExpression(expression: KtBinaryExpression, data: P): Nothing? {

visitBinaryExpressionVoid(expression,data)
return null
}

final override fun visitReturnExpression(expression: KtReturnExpression, data: P): Nothing? {

visitReturnExpressionVoid(expression,data)
return null
}

final override fun visitExpressionWithLabel(expression: KtExpressionWithLabel, data: P): Nothing? {

visitExpressionWithLabelVoid(expression,data)
return null
}

final override fun visitThrowExpression(expression: KtThrowExpression, data: P): Nothing? {

visitThrowExpressionVoid(expression,data)
return null
}

final override fun visitBreakExpression(expression: KtBreakExpression, data: P): Nothing? {

visitBreakExpressionVoid(expression,data)
return null
}

final override fun visitContinueExpression(expression: KtContinueExpression, data: P): Nothing? {

visitContinueExpressionVoid(expression,data)
return null
}

final override fun visitIfExpression(expression: KtIfExpression, data: P): Nothing? {

visitIfExpressionVoid(expression,data)
return null
}

final override fun visitWhenExpression(expression: KtWhenExpression, data: P): Nothing? {

visitWhenExpressionVoid(expression,data)
return null
}

final override fun visitCollectionLiteralExpression(expression: KtCollectionLiteralExpression, data: P): Nothing? {

visitCollectionLiteralExpressionVoid(expression,data)
return null
}

final override fun visitTryExpression(expression: KtTryExpression, data: P): Nothing? {

visitTryExpressionVoid(expression,data)
return null
}

final override fun visitForExpression(expression: KtForExpression, data: P): Nothing? {

visitForExpressionVoid(expression,data)
return null
}

final override fun visitWhileExpression(expression: KtWhileExpression, data: P): Nothing? {

visitWhileExpressionVoid(expression,data)
return null
}

final override fun visitDoWhileExpression(expression: KtDoWhileExpression, data: P): Nothing? {

visitDoWhileExpressionVoid(expression,data)
return null
}

final override fun visitLambdaExpression(expression: KtLambdaExpression, data: P): Nothing? {

visitLambdaExpressionVoid(expression,data)
return null
}

final override fun visitAnnotatedExpression(expression: KtAnnotatedExpression, data: P): Nothing? {

visitAnnotatedExpressionVoid(expression,data)
return null
}

final override fun visitCallExpression(expression: KtCallExpression, data: P): Nothing? {

visitCallExpressionVoid(expression,data)
return null
}

final override fun visitArrayAccessExpression(expression: KtArrayAccessExpression, data: P): Nothing? {

visitArrayAccessExpressionVoid(expression,data)
return null
}

final override fun visitQualifiedExpression(expression: KtQualifiedExpression, data: P): Nothing? {

visitQualifiedExpressionVoid(expression,data)
return null
}

final override fun visitDoubleColonExpression(expression: KtDoubleColonExpression, data: P): Nothing? {

visitDoubleColonExpressionVoid(expression,data)
return null
}

final override fun visitCallableReferenceExpression(expression: KtCallableReferenceExpression, data: P): Nothing? {

visitCallableReferenceExpressionVoid(expression,data)
return null
}

final override fun visitClassLiteralExpression(expression: KtClassLiteralExpression, data: P): Nothing? {

visitClassLiteralExpressionVoid(expression,data)
return null
}

final override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression, data: P): Nothing? {

visitDotQualifiedExpressionVoid(expression,data)
return null
}

final override fun visitSafeQualifiedExpression(expression: KtSafeQualifiedExpression, data: P): Nothing? {

visitSafeQualifiedExpressionVoid(expression,data)
return null
}

final override fun visitObjectLiteralExpression(expression: KtObjectLiteralExpression, data: P): Nothing? {

visitObjectLiteralExpressionVoid(expression,data)
return null
}

final override fun visitBlockExpression(expression: KtBlockExpression, data: P): Nothing? {

visitBlockExpressionVoid(expression,data)
return null
}

final override fun visitCatchSection(catchClause: KtCatchClause, data: P): Nothing? {

visitCatchSectionVoid(catchClause,data)
return null
}

final override fun visitFinallySection(finallySection: KtFinallySection, data: P): Nothing? {

visitFinallySectionVoid(finallySection,data)
return null
}

final override fun visitTypeArgumentList(typeArgumentList: KtTypeArgumentList, data: P): Nothing? {

visitTypeArgumentListVoid(typeArgumentList,data)
return null
}

final override fun visitThisExpression(expression: KtThisExpression, data: P): Nothing? {

visitThisExpressionVoid(expression,data)
return null
}

final override fun visitSuperExpression(expression: KtSuperExpression, data: P): Nothing? {

visitSuperExpressionVoid(expression,data)
return null
}

final override fun visitParenthesizedExpression(expression: KtParenthesizedExpression, data: P): Nothing? {

visitParenthesizedExpressionVoid(expression,data)
return null
}

final override fun visitInitializerList(list: KtInitializerList, data: P): Nothing? {

visitInitializerListVoid(list,data)
return null
}

final override fun visitAnonymousInitializer(initializer: KtAnonymousInitializer, data: P): Nothing? {

visitAnonymousInitializerVoid(initializer,data)
return null
}

final override fun visitPropertyAccessor(accessor: KtPropertyAccessor, data: P): Nothing? {

visitPropertyAccessorVoid(accessor,data)
return null
}

final override fun visitTypeConstraintList(list: KtTypeConstraintList, data: P): Nothing? {

visitTypeConstraintListVoid(list,data)
return null
}

final override fun visitTypeConstraint(constraint: KtTypeConstraint, data: P): Nothing? {

visitTypeConstraintVoid(constraint,data)
return null
}

final override fun visitUserType(type: KtUserType, data: P): Nothing? {

visitUserTypeVoid(type,data)
return null
}

final override fun visitDynamicType(type: KtDynamicType, data: P): Nothing? {

visitDynamicTypeVoid(type,data)
return null
}

final override fun visitFunctionType(type: KtFunctionType, data: P): Nothing? {

visitFunctionTypeVoid(type,data)
return null
}

final override fun visitSelfType(type: KtSelfType, data: P): Nothing? {

visitSelfTypeVoid(type,data)
return null
}

final override fun visitBinaryWithTypeRHSExpression(expression: KtBinaryExpressionWithTypeRHS, data: P): Nothing? {

visitBinaryWithTypeRHSExpressionVoid(expression,data)
return null
}

final override fun visitStringTemplateExpression(expression: KtStringTemplateExpression, data: P): Nothing? {

visitStringTemplateExpressionVoid(expression,data)
return null
}

final override fun visitNamedDeclaration(declaration: KtNamedDeclaration, data: P): Nothing? {

visitNamedDeclarationVoid(declaration,data)
return null
}

final override fun visitNullableType(nullableType: KtNullableType, data: P): Nothing? {

visitNullableTypeVoid(nullableType,data)
return null
}

final override fun visitTypeProjection(typeProjection: KtTypeProjection, data: P): Nothing? {

visitTypeProjectionVoid(typeProjection,data)
return null
}

final override fun visitWhenEntry(ktWhenEntry: KtWhenEntry, data: P): Nothing? {

visitWhenEntryVoid(ktWhenEntry,data)
return null
}

final override fun visitIsExpression(expression: KtIsExpression, data: P): Nothing? {

visitIsExpressionVoid(expression,data)
return null
}

final override fun visitWhenConditionIsPattern(condition: KtWhenConditionIsPattern, data: P): Nothing? {

visitWhenConditionIsPatternVoid(condition,data)
return null
}

final override fun visitWhenConditionInRange(condition: KtWhenConditionInRange, data: P): Nothing? {

visitWhenConditionInRangeVoid(condition,data)
return null
}

final override fun visitWhenConditionWithExpression(condition: KtWhenConditionWithExpression, data: P): Nothing? {

visitWhenConditionWithExpressionVoid(condition,data)
return null
}

final override fun visitObjectDeclaration(declaration: KtObjectDeclaration, data: P): Nothing? {

visitObjectDeclarationVoid(declaration,data)
return null
}

final override fun visitStringTemplateEntry(entry: KtStringTemplateEntry, data: P): Nothing? {

visitStringTemplateEntryVoid(entry,data)
return null
}

final override fun visitStringTemplateEntryWithExpression(entry: KtStringTemplateEntryWithExpression, data: P): Nothing? {

visitStringTemplateEntryWithExpressionVoid(entry,data)
return null
}

final override fun visitBlockStringTemplateEntry(entry: KtBlockStringTemplateEntry, data: P): Nothing? {

visitBlockStringTemplateEntryVoid(entry,data)
return null
}

final override fun visitSimpleNameStringTemplateEntry(entry: KtSimpleNameStringTemplateEntry, data: P): Nothing? {

visitSimpleNameStringTemplateEntryVoid(entry,data)
return null
}

final override fun visitLiteralStringTemplateEntry(entry: KtLiteralStringTemplateEntry, data: P): Nothing? {

visitLiteralStringTemplateEntryVoid(entry,data)
return null
}

final override fun visitEscapeStringTemplateEntry(entry: KtEscapeStringTemplateEntry, data: P): Nothing? {

visitEscapeStringTemplateEntryVoid(entry,data)
return null
}
}
