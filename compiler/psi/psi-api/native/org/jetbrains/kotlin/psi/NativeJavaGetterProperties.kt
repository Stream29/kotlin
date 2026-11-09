/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license in the license/LICENSE.txt file.
 */
package org.jetbrains.kotlin.psi

import com.intellij.psi.*

// Generated property views of retained original Java getter methods.
val KtAnnotated.annotations: List<KtAnnotation>
    get() = getAnnotations()

val KtAnnotated.annotationEntries: List<KtAnnotationEntry>
    get() = getAnnotationEntries()

val KtAnnotatedExpression.baseExpression: KtExpression?
    get() = getBaseExpression()

val KtAnnotatedExpression.annotations: List<KtAnnotation>
    get() = getAnnotations()

val KtAnnotatedExpression.annotationEntries: List<KtAnnotationEntry>
    get() = getAnnotationEntries()

val KtAnnotation.entries: List<KtAnnotationEntry>
    get() = getEntries()

val KtAnnotationEntry.typeReference: KtTypeReference?
    get() = getTypeReference()

val KtAnnotationEntry.calleeExpression: KtConstructorCalleeExpression?
    get() = getCalleeExpression()

val KtAnnotationEntry.valueArgumentList: KtValueArgumentList?
    get() = getValueArgumentList()

val KtAnnotationEntry.valueArguments: List<out ValueArgument>
    get() = getValueArguments()

val KtAnnotationEntry.lambdaArguments: List<KtLambdaArgument>
    get() = getLambdaArguments()

val KtAnnotationEntry.typeArguments: List<KtTypeProjection>
    get() = getTypeArguments()

val KtAnnotationEntry.typeArgumentList: KtTypeArgumentList?
    get() = getTypeArgumentList()

val KtAnnotationEntry.atSymbol: com.intellij.psi.PsiElement?
    get() = getAtSymbol()

val KtAnnotationsContainer.annotations: List<KtAnnotation>
    get() = getAnnotations()

val KtAnnotationsContainer.annotationEntries: List<KtAnnotationEntry>
    get() = getAnnotationEntries()

val KtArrayAccessExpression.arrayExpression: KtExpression?
    get() = getArrayExpression()

val KtArrayAccessExpression.indexExpressions: List<KtExpression>
    get() = getIndexExpressions()

val KtArrayAccessExpression.indicesNode: KtContainerNode
    get() = getIndicesNode()

val KtArrayAccessExpression.bracketRanges: List<com.intellij.openapi.util.TextRange>
    get() = getBracketRanges()

val KtArrayAccessExpression.leftBracket: com.intellij.psi.PsiElement?
    get() = getLeftBracket()

val KtArrayAccessExpression.rightBracket: com.intellij.psi.PsiElement?
    get() = getRightBracket()

val KtArrayAccessExpression.trailingComma: com.intellij.psi.PsiElement?
    get() = getTrailingComma()

val KtBinaryExpression.left: KtExpression?
    get() = getLeft()

val KtBinaryExpression.right: KtExpression?
    get() = getRight()

val KtBinaryExpression.operationReference: KtOperationReferenceExpression
    get() = getOperationReference()

val KtBinaryExpression.operationToken: com.intellij.psi.tree.IElementType
    get() = getOperationToken()

val KtBinaryExpressionWithTypeRHS.left: KtExpression
    get() = getLeft()

val KtBinaryExpressionWithTypeRHS.right: KtTypeReference?
    get() = getRight()

val KtBinaryExpressionWithTypeRHS.operationReference: KtSimpleNameExpression
    get() = getOperationReference()

val KtBlockExpression.containingKtFile: KtFile
    get() = getContainingKtFile()

val KtBlockExpression.psiOrParent: KtElement
    get() = getPsiOrParent()

val KtBlockExpression.firstStatement: KtExpression?
    get() = getFirstStatement()

val KtBlockExpression.statements: List<KtExpression>
    get() = getStatements()

val KtBlockExpression.lastBracketRange: com.intellij.openapi.util.TextRange?
    get() = getLastBracketRange()

val KtBlockExpression.rBrace: com.intellij.psi.PsiElement?
    get() = getRBrace()

val KtBlockExpression.lBrace: com.intellij.psi.PsiElement?
    get() = getLBrace()

val KtBlockStringTemplateEntry.expressions: List<KtExpression>
    get() = getExpressions()

val KtCallElement.calleeExpression: KtExpression?
    get() = getCalleeExpression()

val KtCallElement.valueArgumentList: KtValueArgumentList?
    get() = getValueArgumentList()

val KtCallElement.valueArguments: List<out ValueArgument>
    get() = getValueArguments()

val KtCallElement.lambdaArguments: List<KtLambdaArgument>
    get() = getLambdaArguments()

val KtCallElement.typeArguments: List<KtTypeProjection>
    get() = getTypeArguments()

val KtCallElement.typeArgumentList: KtTypeArgumentList?
    get() = getTypeArgumentList()

val KtCallableDeclaration.valueParameterList: KtParameterList?
    get() = getValueParameterList()

val KtCallableDeclaration.valueParameters: List<KtParameter>
    get() = getValueParameters()

val KtCallableDeclaration.receiverTypeReference: KtTypeReference?
    get() = getReceiverTypeReference()

val KtCallableDeclaration.contextReceivers: List<KtContextReceiver>
    get() = getContextReceivers()

val KtCallableDeclaration.contextParameters: List<KtParameter>
    get() = getContextParameters()

val KtCallableDeclaration.typeReference: KtTypeReference?
    get() = getTypeReference()

val KtCallableDeclaration.colon: com.intellij.psi.PsiElement?
    get() = getColon()

val KtCallableReferenceExpression.callableReference: KtSimpleNameExpression
    get() = getCallableReference()

val KtCatchClause.parameterList: KtParameterList?
    get() = getParameterList()

val KtCatchClause.catchParameter: KtParameter?
    get() = getCatchParameter()

val KtCatchClause.catchBody: KtExpression?
    get() = getCatchBody()

val KtCollectionLiteralExpression.leftBracket: com.intellij.psi.PsiElement?
    get() = getLeftBracket()

val KtCollectionLiteralExpression.rightBracket: com.intellij.psi.PsiElement?
    get() = getRightBracket()

val KtCollectionLiteralExpression.trailingComma: com.intellij.psi.PsiElement?
    get() = getTrailingComma()

val KtCollectionLiteralExpression.innerExpressions: List<KtExpression>
    get() = getInnerExpressions()

val KtConstructorCalleeExpression.typeReference: KtTypeReference?
    get() = getTypeReference()

val KtConstructorCalleeExpression.constructorReferenceExpression: KtSimpleNameExpression?
    get() = getConstructorReferenceExpression()

val KtConstructorDelegationCall.valueArgumentList: KtValueArgumentList?
    get() = getValueArgumentList()

val KtConstructorDelegationCall.valueArguments: List<out ValueArgument>
    get() = getValueArguments()

val KtConstructorDelegationCall.lambdaArguments: List<KtLambdaArgument>
    get() = getLambdaArguments()

val KtConstructorDelegationCall.typeArguments: List<KtTypeProjection>
    get() = getTypeArguments()

val KtConstructorDelegationCall.typeArgumentList: KtTypeArgumentList?
    get() = getTypeArgumentList()

val KtConstructorDelegationCall.calleeExpression: KtConstructorDelegationReferenceExpression?
    get() = getCalleeExpression()

val KtConstructorDelegationCall.isImplicit: Boolean
    get() = isImplicit()

val KtConstructorDelegationCall.isCallToThis: Boolean
    get() = isCallToThis()

val KtConstructorDelegationReferenceExpression.isThis: Boolean
    get() = isThis()

val KtDeclaration.docComment: org.jetbrains.kotlin.kdoc.psi.api.KDoc?
    get() = getDocComment()

val KtDeclarationContainer.declarations: List<KtDeclaration>
    get() = getDeclarations()

val KtDeclarationImpl.modifierList: KtModifierList?
    get() = getModifierList()

val KtDeclarationImpl.annotationEntries: List<KtAnnotationEntry>
    get() = getAnnotationEntries()

val KtDeclarationImpl.annotations: List<KtAnnotation>
    get() = getAnnotations()

val KtDeclarationImpl.docComment: org.jetbrains.kotlin.kdoc.psi.api.KDoc?
    get() = getDocComment()

val <T : com.intellij.psi.stubs.StubElement<*>> KtDeclarationStub<T>.docComment: org.jetbrains.kotlin.kdoc.psi.api.KDoc?
    get() = getDocComment()

val KtDeclarationWithBody.bodyExpression: KtExpression?
    get() = getBodyExpression()

val KtDeclarationWithBody.equalsToken: com.intellij.psi.PsiElement?
    get() = getEqualsToken()

val KtDeclarationWithBody.contractDescription: KtContractEffectList?
    get() = getContractDescription()

val KtDeclarationWithBody.valueParameters: List<KtParameter>
    get() = getValueParameters()

val KtDeclarationWithBody.bodyBlockExpression: KtBlockExpression?
    get() = getBodyBlockExpression()

val KtDeclarationWithInitializer.initializer: KtExpression?
    get() = getInitializer()

val KtDeclarationWithReturnType.typeReference: KtTypeReference?
    get() = getTypeReference()

val KtDelegatedSuperTypeEntry.delegateExpression: KtExpression?
    get() = getDelegateExpression()

val KtDelegatedSuperTypeEntry.byKeywordNode: com.intellij.lang.ASTNode?
    get() = getByKeywordNode()

val KtDestructuringDeclaration.entries: List<KtDestructuringDeclarationEntry>
    get() = getEntries()

val KtDestructuringDeclaration.initializer: KtExpression?
    get() = getInitializer()

val KtDestructuringDeclaration.valOrVarKeyword: com.intellij.psi.PsiElement?
    get() = getValOrVarKeyword()

val KtDestructuringDeclaration.isFullForm: Boolean
    get() = isFullForm()

val KtDestructuringDeclaration.rPar: com.intellij.psi.PsiElement?
    get() = getRPar()

val KtDestructuringDeclaration.lPar: com.intellij.psi.PsiElement?
    get() = getLPar()

val KtDestructuringDeclaration.trailingComma: com.intellij.psi.PsiElement?
    get() = getTrailingComma()

val KtDestructuringDeclaration.typeReference: KtTypeReference?
    get() = getTypeReference()

val KtDestructuringDeclarationEntry.typeReference: KtTypeReference?
    get() = getTypeReference()

val KtDestructuringDeclarationEntry.colon: com.intellij.psi.PsiElement?
    get() = getColon()

val KtDestructuringDeclarationEntry.valueParameterList: KtParameterList?
    get() = getValueParameterList()

val KtDestructuringDeclarationEntry.valueParameters: List<KtParameter>
    get() = getValueParameters()

val KtDestructuringDeclarationEntry.receiverTypeReference: KtTypeReference?
    get() = getReceiverTypeReference()

val KtDestructuringDeclarationEntry.typeParameterList: KtTypeParameterList?
    get() = getTypeParameterList()

val KtDestructuringDeclarationEntry.typeConstraintList: KtTypeConstraintList?
    get() = getTypeConstraintList()

val KtDestructuringDeclarationEntry.typeConstraints: List<KtTypeConstraint>
    get() = getTypeConstraints()

val KtDestructuringDeclarationEntry.typeParameters: List<KtTypeParameter>
    get() = getTypeParameters()

val KtDestructuringDeclarationEntry.initializer: KtNameReferenceExpression?
    get() = getInitializer()

val KtDestructuringDeclarationEntry.equalsToken: com.intellij.psi.PsiElement?
    get() = getEqualsToken()

val KtDestructuringDeclarationEntry.valOrVarKeyword: com.intellij.psi.PsiElement?
    get() = getValOrVarKeyword()

val KtDestructuringDeclarationEntry.ownValOrVarKeyword: com.intellij.psi.PsiElement?
    get() = getOwnValOrVarKeyword()

val KtDestructuringDeclarationEntry.fqName: org.jetbrains.kotlin.name.FqName?
    get() = getFqName()

val KtDoWhileExpression.whileKeyword: com.intellij.psi.PsiElement?
    get() = getWhileKeyword()

val KtDynamicType.typeArgumentsAsTypes: List<KtTypeReference>
    get() = getTypeArgumentsAsTypes()

val KtElementImpl.containingKtFile: KtFile
    get() = getContainingKtFile()

val KtElementImpl.psiOrParent: KtElement
    get() = getPsiOrParent()

val <T : com.intellij.psi.stubs.StubElement<*>> KtElementImplStub<T>.containingKtFile: KtFile
    get() = getContainingKtFile()

val <T : com.intellij.psi.stubs.StubElement<*>> KtElementImplStub<T>.psiOrParent: KtElement
    get() = getPsiOrParent()

val KtEnumEntry.superTypeListEntries: List<KtSuperTypeListEntry>
    get() = getSuperTypeListEntries()

val KtEnumEntry.classId: org.jetbrains.kotlin.name.ClassId?
    get() = getClassId()

val KtEnumEntry.initializerList: KtInitializerList?
    get() = getInitializerList()

val KtEnumEntry.comma: com.intellij.psi.PsiElement?
    get() = getComma()

val KtEnumEntry.semicolon: com.intellij.psi.PsiElement?
    get() = getSemicolon()

val KtEnumEntry.typeReference: KtTypeReference?
    get() = getTypeReference()

val KtEscapeStringTemplateEntry.unescapedValue: String
    get() = getUnescapedValue()

val KtFileAnnotationList.annotations: List<KtAnnotation>
    get() = getAnnotations()

val KtFileAnnotationList.annotationEntries: List<KtAnnotationEntry>
    get() = getAnnotationEntries()

val KtFinallySection.finalExpression: KtBlockExpression?
    get() = getFinalExpression()

val KtForExpression.loopParameter: KtParameter?
    get() = getLoopParameter()

val KtForExpression.destructuringDeclaration: KtDestructuringDeclaration?
    get() = getDestructuringDeclaration()

val KtForExpression.loopRange: KtExpression?
    get() = getLoopRange()

val KtForExpression.inKeyword: com.intellij.psi.PsiElement?
    get() = getInKeyword()

val KtForExpression.forKeyword: com.intellij.psi.PsiElement
    get() = getForKeyword()

val KtFunction.isLocal: Boolean
    get() = isLocal()

val KtFunctionLiteral.bodyExpression: KtBlockExpression
    get() = getBodyExpression()

val KtFunctionLiteral.equalsToken: com.intellij.psi.PsiElement?
    get() = getEqualsToken()

val KtFunctionLiteral.lBrace: com.intellij.psi.PsiElement
    get() = getLBrace()

val KtFunctionLiteral.rBrace: com.intellij.psi.PsiElement?
    get() = getRBrace()

val KtFunctionLiteral.arrow: com.intellij.psi.PsiElement?
    get() = getArrow()

val KtFunctionLiteral.fqName: org.jetbrains.kotlin.name.FqName?
    get() = getFqName()

val KtFunctionNotStubbed.valueParameterList: KtParameterList?
    get() = getValueParameterList()

val KtFunctionNotStubbed.valueParameters: List<KtParameter>
    get() = getValueParameters()

val KtFunctionNotStubbed.bodyExpression: KtExpression?
    get() = getBodyExpression()

val KtFunctionNotStubbed.receiverTypeReference: KtTypeReference?
    get() = getReceiverTypeReference()

val KtFunctionNotStubbed.typeReference: KtTypeReference?
    get() = getTypeReference()

val KtFunctionNotStubbed.colon: com.intellij.psi.PsiElement?
    get() = getColon()

val KtFunctionNotStubbed.isLocal: Boolean
    get() = isLocal()

val KtFunctionType.typeArgumentsAsTypes: List<KtTypeReference?>
    get() = getTypeArgumentsAsTypes()

val KtFunctionType.parameterList: KtParameterList?
    get() = getParameterList()

val KtFunctionType.parameters: List<KtParameter>
    get() = getParameters()

val KtFunctionType.receiver: KtFunctionTypeReceiver?
    get() = getReceiver()

val KtFunctionType.receiverTypeReference: KtTypeReference?
    get() = getReceiverTypeReference()

val KtFunctionType.contextReceiverList: KtContextReceiverList?
    get() = getContextReceiverList()

val KtFunctionType.contextParameterList: KtContextParameterList?
    get() = getContextParameterList()

val KtFunctionType.contextReceiversTypeReferences: List<KtTypeReference>
    get() = getContextReceiversTypeReferences()

val KtFunctionType.returnTypeReference: KtTypeReference?
    get() = getReturnTypeReference()

val KtFunctionType.totalParameterCount: Int
    get() = getTotalParameterCount()

val KtFunctionTypeReceiver.typeReference: KtTypeReference
    get() = getTypeReference()

val KtIfExpression.condition: KtExpression?
    get() = getCondition()

val KtIfExpression.leftParenthesis: com.intellij.psi.PsiElement?
    get() = getLeftParenthesis()

val KtIfExpression.rightParenthesis: com.intellij.psi.PsiElement?
    get() = getRightParenthesis()

val KtIfExpression.then: KtExpression?
    get() = getThen()

val KtIfExpression.`else`: KtExpression?
    get() = getElse()

val KtIfExpression.elseKeyword: com.intellij.psi.PsiElement?
    get() = getElseKeyword()

val KtIfExpression.ifKeyword: com.intellij.psi.PsiElement
    get() = getIfKeyword()

val KtImportDirective.importedReference: KtExpression?
    get() = getImportedReference()

val KtImportDirective.alias: KtImportAlias?
    get() = getAlias()

val KtImportDirective.importPath: org.jetbrains.kotlin.resolve.ImportPath?
    get() = getImportPath()

val KtImportDirective.isValidImport: Boolean
    get() = isValidImport()

val KtImportList.imports: List<KtImportDirective>
    get() = getImports()

val KtInitializerList.initializers: List<KtSuperTypeListEntry>
    get() = getInitializers()

val KtInstanceExpressionWithLabel.instanceReference: KtReferenceExpression
    get() = getInstanceReference()

val KtIsExpression.leftHandSide: KtExpression
    get() = getLeftHandSide()

val KtIsExpression.typeReference: KtTypeReference?
    get() = getTypeReference()

val KtIsExpression.operationReference: KtSimpleNameExpression
    get() = getOperationReference()

val KtIsExpression.isNegated: Boolean
    get() = isNegated()

val KtLambdaExpression.functionLiteral: KtFunctionLiteral
    get() = getFunctionLiteral()

val KtLambdaExpression.valueParameters: List<KtParameter>
    get() = getValueParameters()

val KtLambdaExpression.bodyExpression: KtBlockExpression?
    get() = getBodyExpression()

val KtLambdaExpression.leftCurlyBrace: com.intellij.lang.ASTNode
    get() = getLeftCurlyBrace()

val KtLambdaExpression.rightCurlyBrace: com.intellij.lang.ASTNode?
    get() = getRightCurlyBrace()

val KtLambdaExpression.containingKtFile: KtFile
    get() = getContainingKtFile()

val KtLambdaExpression.psiOrParent: KtElement
    get() = getPsiOrParent()

val KtLambdaExpression.isTrailingLambdaOnNewLine: Boolean
    get() = isTrailingLambdaOnNewLine()

val KtLoopExpression.body: KtExpression?
    get() = getBody()

val KtLoopExpression.leftParenthesis: com.intellij.psi.PsiElement?
    get() = getLeftParenthesis()

val KtLoopExpression.rightParenthesis: com.intellij.psi.PsiElement?
    get() = getRightParenthesis()

val KtModifierList.annotations: List<KtAnnotation>
    get() = getAnnotations()

val KtModifierList.contextParameterList: KtContextParameterList?
    get() = getContextParameterList()

val KtModifierList.contextReceiverList: KtContextReceiverList?
    get() = getContextReceiverList()

val KtModifierList.contextParameterLists: List<KtContextParameterList>
    get() = getContextParameterLists()

val KtModifierList.contextReceiverLists: List<KtContextReceiverList>
    get() = getContextReceiverLists()

val KtModifierList.annotationEntries: List<KtAnnotationEntry>
    get() = getAnnotationEntries()

val KtModifierList.owner: com.intellij.psi.PsiElement?
    get() = getOwner()

val KtModifierListOwner.modifierList: KtModifierList?
    get() = getModifierList()

val <T : com.intellij.psi.stubs.StubElement<*>> KtModifierListOwnerStub<T>.modifierList: KtModifierList?
    get() = getModifierList()

val <T : com.intellij.psi.stubs.StubElement<*>> KtModifierListOwnerStub<T>.annotationEntries: List<KtAnnotationEntry>
    get() = getAnnotationEntries()

val <T : com.intellij.psi.stubs.StubElement<*>> KtModifierListOwnerStub<T>.annotations: List<KtAnnotation>
    get() = getAnnotations()

val KtNamed.nameAsName: org.jetbrains.kotlin.name.Name?
    get() = getNameAsName()

val KtNamedDeclaration.nameAsSafeName: org.jetbrains.kotlin.name.Name
    get() = getNameAsSafeName()

val KtNamedDeclaration.fqName: org.jetbrains.kotlin.name.FqName?
    get() = getFqName()

val KtNamedDeclarationNotStubbed.nameAsName: org.jetbrains.kotlin.name.Name?
    get() = getNameAsName()

val KtNamedDeclarationNotStubbed.nameAsSafeName: org.jetbrains.kotlin.name.Name
    get() = getNameAsSafeName()

val <T : org.jetbrains.kotlin.psi.stubs.KotlinStubWithFqName<*>> KtNamedDeclarationStub<T>.nameAsName: org.jetbrains.kotlin.name.Name?
    get() = getNameAsName()

val <T : org.jetbrains.kotlin.psi.stubs.KotlinStubWithFqName<*>> KtNamedDeclarationStub<T>.nameAsSafeName: org.jetbrains.kotlin.name.Name
    get() = getNameAsSafeName()

val <T : org.jetbrains.kotlin.psi.stubs.KotlinStubWithFqName<*>> KtNamedDeclarationStub<T>.fqName: org.jetbrains.kotlin.name.FqName?
    get() = getFqName()

val KtNullableType.questionMarkNode: com.intellij.lang.ASTNode
    get() = getQuestionMarkNode()

val KtNullableType.typeArgumentsAsTypes: List<KtTypeReference?>
    get() = getTypeArgumentsAsTypes()

val KtNullableType.innerType: KtTypeElement?
    get() = getInnerType()

val KtNullableType.modifierList: KtModifierList?
    get() = getModifierList()

val KtNullableType.annotationEntries: List<KtAnnotationEntry>
    get() = getAnnotationEntries()

val KtObjectLiteralExpression.objectDeclaration: KtObjectDeclaration
    get() = getObjectDeclaration()

val KtOperationExpression.operationReference: KtSimpleNameExpression
    get() = getOperationReference()

val KtPackageDirective.packageNameExpression: KtExpression?
    get() = getPackageNameExpression()

val KtPackageDirective.packageNames: List<KtSimpleNameExpression>
    get() = getPackageNames()

val KtPackageDirective.lastReferenceExpression: KtSimpleNameExpression?
    get() = getLastReferenceExpression()

val KtPackageDirective.nameAsName: org.jetbrains.kotlin.name.Name
    get() = getNameAsName()

val KtPackageDirective.isRoot: Boolean
    get() = isRoot()

val KtPackageDirective.fqName: org.jetbrains.kotlin.name.FqName
    get() = getFqName()

val KtPackageDirective.qualifiedName: String
    get() = getQualifiedName()

val KtPackageDirective.packageKeyword: com.intellij.psi.PsiElement?
    get() = getPackageKeyword()

val KtParameter.typeReference: KtTypeReference?
    get() = getTypeReference()

val KtParameter.colon: com.intellij.psi.PsiElement?
    get() = getColon()

val KtParameter.equalsToken: com.intellij.psi.PsiElement?
    get() = getEqualsToken()

val KtParameter.defaultValue: KtExpression?
    get() = getDefaultValue()

val KtParameter.isVarArg: Boolean
    get() = isVarArg()

val KtParameter.valOrVarKeyword: com.intellij.psi.PsiElement?
    get() = getValOrVarKeyword()

val KtParameter.destructuringDeclaration: KtDestructuringDeclaration?
    get() = getDestructuringDeclaration()

val KtParameter.isLoopParameter: Boolean
    get() = isLoopParameter()

val KtParameter.isCatchParameter: Boolean
    get() = isCatchParameter()

val KtParameter.isContextParameter: Boolean
    get() = isContextParameter()

val KtParameter.isLambdaParameter: Boolean
    get() = isLambdaParameter()

val KtParameter.isFunctionTypeParameter: Boolean
    get() = isFunctionTypeParameter()

val KtParameter.valueParameterList: KtParameterList?
    get() = getValueParameterList()

val KtParameter.valueParameters: List<KtParameter>
    get() = getValueParameters()

val KtParameter.receiverTypeReference: KtTypeReference?
    get() = getReceiverTypeReference()

val KtParameter.typeParameterList: KtTypeParameterList?
    get() = getTypeParameterList()

val KtParameter.typeConstraintList: KtTypeConstraintList?
    get() = getTypeConstraintList()

val KtParameter.typeConstraints: List<KtTypeConstraint>
    get() = getTypeConstraints()

val KtParameter.typeParameters: List<KtTypeParameter>
    get() = getTypeParameters()

val KtParameter.ownerFunction: KtDeclarationWithBody?
    get() = getOwnerFunction()

val KtParameter.ownerDeclaration: KtDeclaration?
    get() = getOwnerDeclaration()

val KtParameterList.parameters: List<KtParameter>
    get() = getParameters()

val KtParameterList.ownerFunction: KtDeclarationWithBody?
    get() = getOwnerFunction()

val KtParameterList.rightParenthesis: com.intellij.psi.PsiElement?
    get() = getRightParenthesis()

val KtParameterList.leftParenthesis: com.intellij.psi.PsiElement?
    get() = getLeftParenthesis()

val KtParameterList.firstComma: com.intellij.psi.PsiElement?
    get() = getFirstComma()

val KtParameterList.trailingComma: com.intellij.psi.PsiElement?
    get() = getTrailingComma()

val KtParenthesizedExpression.expression: KtExpression?
    get() = getExpression()

val KtPostfixExpression.baseExpression: KtExpression?
    get() = getBaseExpression()

val KtPrefixExpression.baseExpression: KtExpression?
    get() = getBaseExpression()

val KtProperty.isLocal: Boolean
    get() = isLocal()

val KtProperty.isMember: Boolean
    get() = isMember()

val KtProperty.valueParameterList: KtParameterList?
    get() = getValueParameterList()

val KtProperty.valueParameters: List<KtParameter>
    get() = getValueParameters()

val KtProperty.receiverTypeReference: KtTypeReference?
    get() = getReceiverTypeReference()

val KtProperty.typeReference: KtTypeReference?
    get() = getTypeReference()

val KtProperty.colon: com.intellij.psi.PsiElement?
    get() = getColon()

val KtProperty.equalsToken: com.intellij.psi.PsiElement?
    get() = getEqualsToken()

val KtProperty.accessors: List<KtPropertyAccessor>
    get() = getAccessors()

val KtProperty.getter: KtPropertyAccessor?
    get() = getGetter()

val KtProperty.setter: KtPropertyAccessor?
    get() = getSetter()

val KtProperty.fieldDeclaration: KtBackingField?
    get() = getFieldDeclaration()

val KtProperty.delegateExpression: KtExpression?
    get() = getDelegateExpression()

val KtProperty.initializer: KtExpression?
    get() = getInitializer()

val KtProperty.delegateExpressionOrInitializer: KtExpression?
    get() = getDelegateExpressionOrInitializer()

val KtProperty.valOrVarKeyword: com.intellij.psi.PsiElement
    get() = getValOrVarKeyword()

val KtPropertyDelegate.expression: KtExpression?
    get() = getExpression()

val KtPropertyDelegate.byKeywordNode: com.intellij.lang.ASTNode
    get() = getByKeywordNode()

val KtPureClassOrObject.isLocal: Boolean
    get() = isLocal()

val KtPureClassOrObject.superTypeListEntries: List<KtSuperTypeListEntry>
    get() = getSuperTypeListEntries()

val KtPureClassOrObject.companionObjects: List<KtObjectDeclaration>
    get() = getCompanionObjects()

val KtPureClassOrObject.primaryConstructor: KtPrimaryConstructor?
    get() = getPrimaryConstructor()

val KtPureClassOrObject.primaryConstructorModifierList: KtModifierList?
    get() = getPrimaryConstructorModifierList()

val KtPureClassOrObject.primaryConstructorParameters: List<KtParameter>
    get() = getPrimaryConstructorParameters()

val KtPureClassOrObject.secondaryConstructors: List<KtSecondaryConstructor>
    get() = getSecondaryConstructors()

val KtPureClassOrObject.contextReceivers: List<KtContextReceiver>
    get() = getContextReceivers()

val KtPureClassOrObject.body: KtClassBody?
    get() = getBody()

val KtPureElement.psiOrParent: KtElement
    get() = getPsiOrParent()

val KtPureElement.containingKtFile: KtFile
    get() = getContainingKtFile()

val KtReturnExpression.returnedExpression: KtExpression?
    get() = getReturnedExpression()

val KtReturnExpression.returnKeyword: com.intellij.psi.PsiElement
    get() = getReturnKeyword()

val KtReturnExpression.labeledExpression: com.intellij.psi.PsiElement?
    get() = getLabeledExpression()

val KtSelfType.typeArgumentsAsTypes: List<KtTypeReference>
    get() = getTypeArgumentsAsTypes()

val KtStringTemplateEntry.expression: KtExpression?
    get() = getExpression()

val KtStringTemplateEntryWithExpression.expressions: List<KtExpression>
    get() = getExpressions()

val KtStringTemplateExpression.interpolationPrefix: KtStringInterpolationPrefix?
    get() = getInterpolationPrefix()

val KtStringTemplateExpression.entries: Array<KtStringTemplateEntry>
    get() = getEntries()

val KtSuperExpression.superTypeQualifier: KtTypeReference?
    get() = getSuperTypeQualifier()

val KtSuperTypeCallEntry.calleeExpression: KtConstructorCalleeExpression
    get() = getCalleeExpression()

val KtSuperTypeCallEntry.valueArgumentList: KtValueArgumentList?
    get() = getValueArgumentList()

val KtSuperTypeCallEntry.valueArguments: List<out ValueArgument>
    get() = getValueArguments()

val KtSuperTypeCallEntry.lambdaArguments: List<KtLambdaArgument>
    get() = getLambdaArguments()

val KtSuperTypeCallEntry.typeReference: KtTypeReference?
    get() = getTypeReference()

val KtSuperTypeCallEntry.typeArguments: List<KtTypeProjection>
    get() = getTypeArguments()

val KtSuperTypeCallEntry.typeArgumentList: KtTypeArgumentList?
    get() = getTypeArgumentList()

val KtSuperTypeList.entries: List<KtSuperTypeListEntry>
    get() = getEntries()

val KtSuperTypeListEntry.typeReference: KtTypeReference?
    get() = getTypeReference()

val KtSuperTypeListEntry.typeAsUserType: KtUserType?
    get() = getTypeAsUserType()

val KtThrowExpression.thrownExpression: KtExpression?
    get() = getThrownExpression()

val KtTryExpression.tryBlock: KtBlockExpression
    get() = getTryBlock()

val KtTryExpression.catchClauses: List<KtCatchClause>
    get() = getCatchClauses()

val KtTryExpression.finallyBlock: KtFinallySection?
    get() = getFinallyBlock()

val KtTryExpression.tryKeyword: com.intellij.psi.PsiElement?
    get() = getTryKeyword()

val KtTypeArgumentList.arguments: List<KtTypeProjection>
    get() = getArguments()

val KtTypeArgumentList.trailingComma: com.intellij.psi.PsiElement?
    get() = getTrailingComma()

val KtTypeConstraint.subjectTypeParameterName: KtSimpleNameExpression?
    get() = getSubjectTypeParameterName()

val KtTypeConstraint.boundTypeReference: KtTypeReference?
    get() = getBoundTypeReference()

val KtTypeConstraint.annotations: List<KtAnnotation>
    get() = getAnnotations()

val KtTypeConstraint.annotationEntries: List<KtAnnotationEntry>
    get() = getAnnotationEntries()

val KtTypeConstraintList.constraints: List<KtTypeConstraint>
    get() = getConstraints()

val KtTypeElement.typeArgumentsAsTypes: List<KtTypeReference?>
    get() = getTypeArgumentsAsTypes()

val KtTypeParameter.variance: org.jetbrains.kotlin.types.Variance
    get() = getVariance()

val KtTypeParameter.extendsBound: KtTypeReference?
    get() = getExtendsBound()

val KtTypeParameterList.parameters: List<KtTypeParameter>
    get() = getParameters()

val KtTypeParameterList.trailingComma: com.intellij.psi.PsiElement?
    get() = getTrailingComma()

val KtTypeParameterListOwner.typeParameterList: KtTypeParameterList?
    get() = getTypeParameterList()

val KtTypeParameterListOwner.typeConstraintList: KtTypeConstraintList?
    get() = getTypeConstraintList()

val KtTypeParameterListOwner.typeConstraints: List<KtTypeConstraint>
    get() = getTypeConstraints()

val KtTypeParameterListOwner.typeParameters: List<KtTypeParameter>
    get() = getTypeParameters()

val KtTypeParameterListOwnerNotStubbed.typeParameterList: KtTypeParameterList?
    get() = getTypeParameterList()

val KtTypeParameterListOwnerNotStubbed.typeConstraintList: KtTypeConstraintList?
    get() = getTypeConstraintList()

val KtTypeParameterListOwnerNotStubbed.typeConstraints: List<KtTypeConstraint>
    get() = getTypeConstraints()

val KtTypeParameterListOwnerNotStubbed.typeParameters: List<KtTypeParameter>
    get() = getTypeParameters()

val <T : org.jetbrains.kotlin.psi.stubs.KotlinStubWithFqName<*>> KtTypeParameterListOwnerStub<T>.typeParameterList: KtTypeParameterList?
    get() = getTypeParameterList()

val <T : org.jetbrains.kotlin.psi.stubs.KotlinStubWithFqName<*>> KtTypeParameterListOwnerStub<T>.typeConstraintList: KtTypeConstraintList?
    get() = getTypeConstraintList()

val <T : org.jetbrains.kotlin.psi.stubs.KotlinStubWithFqName<*>> KtTypeParameterListOwnerStub<T>.typeConstraints: List<KtTypeConstraint>
    get() = getTypeConstraints()

val <T : org.jetbrains.kotlin.psi.stubs.KotlinStubWithFqName<*>> KtTypeParameterListOwnerStub<T>.typeParameters: List<KtTypeParameter>
    get() = getTypeParameters()

val <T : org.jetbrains.kotlin.psi.stubs.KotlinStubWithFqName<*>> KtTypeParameterListOwnerStub<T>.contextReceiverList: KtContextReceiverList?
    get() = getContextReceiverList()

val <T : org.jetbrains.kotlin.psi.stubs.KotlinStubWithFqName<*>> KtTypeParameterListOwnerStub<T>.contextReceiverLists: List<KtContextReceiverList>
    get() = getContextReceiverLists()

val KtTypeProjection.typeReference: KtTypeReference?
    get() = getTypeReference()

val KtTypeProjection.projectionToken: com.intellij.psi.PsiElement?
    get() = getProjectionToken()

val KtUnaryExpression.baseExpression: KtExpression?
    get() = getBaseExpression()

val KtUnaryExpression.operationReference: KtSimpleNameExpression
    get() = getOperationReference()

val KtUnaryExpression.operationToken: com.intellij.psi.tree.IElementType
    get() = getOperationToken()

val KtUserType.typeArgumentList: KtTypeArgumentList?
    get() = getTypeArgumentList()

val KtUserType.typeArguments: List<KtTypeProjection>
    get() = getTypeArguments()

val KtUserType.typeArgumentsAsTypes: List<KtTypeReference?>
    get() = getTypeArgumentsAsTypes()

val KtUserType.referenceExpression: KtSimpleNameExpression?
    get() = getReferenceExpression()

val KtUserType.qualifier: KtUserType?
    get() = getQualifier()

val KtUserType.referencedName: String?
    get() = getReferencedName()

val KtValVarKeywordOwner.valOrVarKeyword: com.intellij.psi.PsiElement?
    get() = getValOrVarKeyword()

val KtValueArgument.argumentExpression: KtExpression?
    get() = getArgumentExpression()

val KtValueArgument.stringTemplateExpression: KtStringTemplateExpression?
    get() = getStringTemplateExpression()

val KtValueArgument.argumentName: KtValueArgumentName?
    get() = getArgumentName()

val KtValueArgument.equalsToken: com.intellij.psi.PsiElement?
    get() = getEqualsToken()

val KtValueArgument.isNamed: Boolean
    get() = isNamed()

val KtValueArgument.spreadElement: com.intellij.psi.impl.source.tree.LeafPsiElement?
    get() = getSpreadElement()

val KtValueArgument.isExternal: Boolean
    get() = isExternal()

val KtValueArgumentList.arguments: List<KtValueArgument>
    get() = getArguments()

val KtValueArgumentList.rightParenthesis: com.intellij.psi.PsiElement?
    get() = getRightParenthesis()

val KtValueArgumentList.leftParenthesis: com.intellij.psi.PsiElement?
    get() = getLeftParenthesis()

val KtValueArgumentList.trailingComma: com.intellij.psi.PsiElement?
    get() = getTrailingComma()

val KtWhenConditionInRange.isNegated: Boolean
    get() = isNegated()

val KtWhenConditionInRange.rangeExpression: KtExpression?
    get() = getRangeExpression()

val KtWhenConditionInRange.operationReference: KtOperationReferenceExpression
    get() = getOperationReference()

val KtWhenConditionIsPattern.isNegated: Boolean
    get() = isNegated()

val KtWhenConditionIsPattern.typeReference: KtTypeReference?
    get() = getTypeReference()

val KtWhenConditionWithExpression.expression: KtExpression?
    get() = getExpression()

val KtWhenEntry.isElse: Boolean
    get() = isElse()

val KtWhenEntry.elseKeyword: com.intellij.psi.PsiElement?
    get() = getElseKeyword()

val KtWhenEntry.expression: KtExpression?
    get() = getExpression()

val KtWhenEntry.conditions: Array<KtWhenCondition>
    get() = getConditions()

val KtWhenEntry.guard: KtWhenEntryGuard?
    get() = getGuard()

val KtWhenEntry.trailingComma: com.intellij.psi.PsiElement?
    get() = getTrailingComma()

val KtWhenEntry.arrow: com.intellij.psi.PsiElement?
    get() = getArrow()

val KtWhenExpression.entries: List<KtWhenEntry>
    get() = getEntries()

val KtWhenExpression.subjectVariable: KtProperty?
    get() = getSubjectVariable()

val KtWhenExpression.subjectExpression: KtExpression?
    get() = getSubjectExpression()

val KtWhenExpression.whenKeyword: com.intellij.psi.PsiElement
    get() = getWhenKeyword()

val KtWhenExpression.closeBrace: com.intellij.psi.PsiElement?
    get() = getCloseBrace()

val KtWhenExpression.openBrace: com.intellij.psi.PsiElement?
    get() = getOpenBrace()

val KtWhenExpression.leftParenthesis: com.intellij.psi.PsiElement?
    get() = getLeftParenthesis()

val KtWhenExpression.rightParenthesis: com.intellij.psi.PsiElement?
    get() = getRightParenthesis()

val KtWhenExpression.elseExpression: KtExpression?
    get() = getElseExpression()

val KtWhileExpressionBase.condition: KtExpression?
    get() = getCondition()

val KtClass.isInterface: Boolean
    get() = isInterface()

val KtClassLikeDeclaration.classId: org.jetbrains.kotlin.name.ClassId?
    get() = getClassId()

val KtClassOrObject.isTopLevel: Boolean
    get() = isTopLevel()

val KtClassOrObject.classId: org.jetbrains.kotlin.name.ClassId?
    get() = getClassId()

val KtClassOrObject.isLocal: Boolean
    get() = isLocal()

val <T : KtConstructor<T>> KtConstructor<T>.isDelegatedCallToThis: Boolean
    get() = isDelegatedCallToThis()

val <T : KtConstructor<T>> KtConstructor<T>.isExplicitDelegationCall: Boolean
    get() = isExplicitDelegationCall()

val KtObjectDeclaration.isObjectLiteral: Boolean
    get() = isObjectLiteral()

val KtScript.fqName: org.jetbrains.kotlin.name.FqName
    get() = getFqName()

val KtSimpleNameExpression.referencedName: String
    get() = getReferencedName()

val KtTypeAlias.isTopLevel: Boolean
    get() = isTopLevel()

val KtTypeAlias.classId: org.jetbrains.kotlin.name.ClassId?
    get() = getClassId()
