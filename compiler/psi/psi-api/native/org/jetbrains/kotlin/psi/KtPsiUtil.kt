/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import org.jetbrains.kotlin.psi.psiUtil.getQualifiedElement as p1kOriginal_getQualifiedElement
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedElementSelector as p1kOriginal_getQualifiedElementSelector
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForSelector as p1kOriginal_getQualifiedExpressionForSelector
import org.jetbrains.kotlin.resolve.getLastStatementInABlock as p1kOriginal_getLastStatementInABlock

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.codeInsight.CommentUtilCore
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.builtins.StandardNames
import org.jetbrains.kotlin.kdoc.psi.api.KDocElement
import org.jetbrains.kotlin.lang.BinaryOperationPrecedence
import org.jetbrains.kotlin.lexer.KtToken
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import org.jetbrains.kotlin.resolve.StatementFilter



open class KtPsiUtil {
private constructor() {


}

interface KtExpressionWrapper {
fun getBaseExpression(): KtExpression
}

companion object {
fun <D> visitChildren(element: KtElement, visitor: KtVisitor<Nothing?,D>, data: D): Unit {

var child: PsiElement? = element.firstChild
while (child != null) {
if (child is KtElement) {
((child as KtElement)).accept(visitor,data)
}
child = child.nextSibling
}
}

fun safeDeparenthesize(expression: KtExpression): KtExpression {

return safeDeparenthesize(expression,false)
}

fun safeDeparenthesize(expression: KtExpression, keepAnnotations: Boolean): KtExpression {

var deparenthesized: KtExpression? = deparenthesize(expression,keepAnnotations)
return (if (deparenthesized != null) deparenthesized else expression)
}

fun deparenthesize(expression: KtExpression?): KtExpression? {

return deparenthesize(expression,false)
}

fun deparenthesize(expression: KtExpression?, keepAnnotations: Boolean): KtExpression? {
var expression = expression
while (true) {
var baseExpression: KtExpression? = deparenthesizeOnce(expression,keepAnnotations)
if (baseExpression === expression) return baseExpression
expression = baseExpression
}
}

fun deparenthesizeOnce(expression: KtExpression?): KtExpression? {

return deparenthesizeOnce(expression,false)
}

fun deparenthesizeOnce(expression: KtExpression?, keepAnnotations: Boolean): KtExpression? {

if (expression is KtAnnotatedExpression && ! keepAnnotations) {
return ((expression as KtAnnotatedExpression)).getBaseExpression()
}
else if (expression is KtLabeledExpression) {
return ((expression as KtLabeledExpression)).baseExpression
}
else if (expression is KtExpressionWrapper) {
return ((expression as KtExpressionWrapper)).getBaseExpression()
}
else if (expression is KtParenthesizedExpression) {
return ((expression as KtParenthesizedExpression)).getExpression()
}
return expression
}

fun safeName(name: String?): Name {

return (if (name == null) SpecialNames.NO_NAME_PROVIDED else Name.identifier(name))
}

fun findRootExpressions(unreachableElements: Collection<KtElement>): Set<KtElement> {

var rootElements: MutableSet<KtElement> = HashSet()
var shadowedElements: MutableSet<KtElement> = HashSet()
var shadowAllChildren: KtVisitorVoid = object : KtVisitorVoid() {
override fun visitKtElement(element: KtElement): Unit {

if(shadowedElements.add(element)) {
element.acceptChildren(this)
}
}
}
for (element in unreachableElements) {
if (shadowedElements.contains(element)) continue
element.acceptChildren(shadowAllChildren)
rootElements.removeAll(shadowedElements)
rootElements.add(element)
}
return rootElements
}

fun unquoteIdentifier(quoted: String): String {

if (quoted.indexOf('`') < 0) {
return quoted
}
if (quoted.startsWith("`") && quoted.endsWith("`") && quoted.length >= 2) {
return quoted.substring(1,quoted.length - 1)
}
else {
return quoted
}
}

fun unquoteIdentifierOrFieldReference(quoted: String): String {

if (quoted.indexOf('`') < 0) {
return quoted
}
if (quoted.startsWith("\$")) {
return "\$" + unquoteIdentifier(quoted.substring(1))
}
else {
return unquoteIdentifier(quoted)
}
}

fun isDeprecated(owner: KtModifierListOwner): Boolean {

var modifierList: KtModifierList? = owner.getModifierList()
if (modifierList != null) {
var annotationEntries: List<KtAnnotationEntry> = modifierList.getAnnotationEntries()
for (annotation in annotationEntries) {
var shortName: Name? = annotation.shortName
if (StandardNames.FqNames.deprecated.shortName().equals(shortName)) {
return true
}
}
}
return false
}

fun <T : PsiElement> getDirectParentOfTypeForBlock(block: KtBlockExpression, aClass: kotlin.reflect.KClass<T>): T? {

var parent: T? = PsiTreeUtil.getParentOfType(block,aClass)
if (parent is KtIfExpression) {
var ifExpression: KtIfExpression = (parent as KtIfExpression)
if (ifExpression.getElse() == block || ifExpression.getThen() == block) {
return parent
}
}
if (parent is KtWhenExpression) {
var whenExpression: KtWhenExpression = (parent as KtWhenExpression)
for (whenEntry in whenExpression.getEntries()) {
if (whenEntry.getExpression() == block) {
return parent
}
}
}
if (parent is KtFunctionLiteral) {
var functionLiteral: KtFunctionLiteral = (parent as KtFunctionLiteral)
if (functionLiteral.getBodyExpression() == block) {
return parent
}
}
if (parent is KtTryExpression) {
var tryExpression: KtTryExpression = (parent as KtTryExpression)
if (tryExpression.getTryBlock() == block) {
return parent
}
for (clause in tryExpression.getCatchClauses()) {
if (clause.getCatchBody() == block) {
return parent
}
}
}
return null
}

fun getLastReference(importedReference: KtExpression): KtSimpleNameExpression? {

var selector: KtElement? = (importedReference).p1kOriginal_getQualifiedElementSelector()
return (if (selector is KtSimpleNameExpression) (selector as KtSimpleNameExpression) else null)
}

fun isSelectorInQualified(nameExpression: KtSimpleNameExpression): Boolean {

var qualifiedElement: KtElement? = (nameExpression).p1kOriginal_getQualifiedElement()
return qualifiedElement is KtQualifiedExpression || ((qualifiedElement is KtUserType) &&((qualifiedElement as KtUserType)).getQualifier() != null)
}

fun isLHSOfDot(expression: KtExpression): Boolean {

var parent: PsiElement? = expression.parent
if (!(parent is KtQualifiedExpression)) return false
var qualifiedParent: KtQualifiedExpression = (parent as KtQualifiedExpression)
return qualifiedParent.receiverExpression == expression || isLHSOfDot(qualifiedParent)
}

fun isScriptDeclaration(namedDeclaration: KtDeclaration): Boolean {

return getScript(namedDeclaration) != null
}

fun getScript(namedDeclaration: KtDeclaration): KtScript? {

var parent: PsiElement? = namedDeclaration.parent
if (parent != null && parent.parent is KtScript) {
return (parent.parent as KtScript)
}
else {
return null
}
}

fun isRemovableVariableDeclaration(declaration: KtDeclaration): Boolean {

if (!(declaration is KtVariableDeclaration)) return false
if (declaration is KtProperty) return true
assert(declaration is KtDestructuringDeclarationEntry)
return true
}

fun getTopmostParentOfTypes(element: PsiElement?, vararg parentTypes: kotlin.reflect.KClass<out PsiElement>): PsiElement? {

if (element is PsiFile) return null
var answer: PsiElement? = PsiTreeUtil.getParentOfType(element,*parentTypes)
if (answer is PsiFile) return answer
do {
var next: PsiElement? = PsiTreeUtil.getParentOfType(answer,*parentTypes)
if (next == null) break
answer = next
} while (true)
return answer
}

fun isNullConstant(expression: KtExpression): Boolean {

var deparenthesized: KtExpression? = deparenthesize(expression)
return deparenthesized is KtConstantExpression && deparenthesized.node.elementType == KtNodeTypes.NULL
}

fun isTrueConstant(condition: KtExpression?): Boolean {

return isBooleanConstant(condition) && condition!!.node!!.findChildByType(KtTokens.TRUE_KEYWORD) != null
}

fun isFalseConstant(condition: KtExpression?): Boolean {

return isBooleanConstant(condition) && condition!!.node!!.findChildByType(KtTokens.FALSE_KEYWORD) != null
}

fun isBooleanConstant(condition: KtExpression?): Boolean {

return condition != null && condition.node!!.elementType == KtNodeTypes.BOOLEAN_CONSTANT
}

fun isAbstract(declaration: KtDeclarationWithBody): Boolean {

return declaration.getBodyExpression() == null
}

fun getExpressionOrLastStatementInBlock(expression: KtExpression?): KtExpression? {

if (expression is KtBlockExpression) {
return getLastStatementInABlock((expression as KtBlockExpression))
}
return expression
}

fun getLastStatementInABlock(blockExpression: KtBlockExpression?): KtExpression? {

if (blockExpression == null) return null
var statements: List<KtExpression> = blockExpression.getStatements()
return (if (statements.isEmpty()) null else statements.get(statements.size - 1))
}

fun isTrait(classOrObject: KtClassOrObject): Boolean {

return classOrObject is KtClass && ((classOrObject as KtClass)).isInterface
}

fun getOutermostClassOrObject(classOrObject: KtClassOrObject): KtClassOrObject? {

var current: KtClassOrObject? = classOrObject
while (true) {
var parent: PsiElement? = current!!.parent
assert(parent != null) { "Class with no parent: " + current!!.text }
if (parent is PsiFile) {
return current
}
if (!(parent is KtClassBody)) {
return current
}
current = (parent.parent as KtClassOrObject?)
}
}

fun getClassIfParameterIsProperty(ktParameter: KtParameter): KtClassOrObject? {

if (ktParameter.hasValOrVar()) {
var grandParent: PsiElement? = ktParameter.parent!!.parent
if (grandParent is KtPrimaryConstructor) {
return ((grandParent as KtPrimaryConstructor)).getContainingClassOrObject()
}
}
return null
}

private fun getOperation(expression: KtExpression): IElementType? {

if (expression is KtQualifiedExpression) {
return ((expression as KtQualifiedExpression)).operationSign
}
else if (expression is KtOperationExpression) {
return ((expression as KtOperationExpression)).getOperationReference().getReferencedNameElementType()
}
return null
}

val MAX_PRIORITY: Int = BinaryOperationPrecedence.entries.size + 3

private fun getPriority(expression: KtExpression): Int {

if (expression is KtSuperExpression) {
return MAX_PRIORITY
}
if (expression is KtPostfixExpression || expression is KtQualifiedExpression || expression is KtCallExpression || expression is KtArrayAccessExpression || expression is KtDoubleColonExpression) {
return MAX_PRIORITY - 1
}
if (expression is KtPrefixExpression || expression is KtLabeledExpression || expression is KtIfExpression) {
return MAX_PRIORITY - 2
}
var operation: IElementType? = getOperation(expression)
if (operation is KtToken) {
var binaryPrecedence: BinaryOperationPrecedence? = BinaryOperationPrecedence.TOKEN_TO_BINARY_PRECEDENCE_MAP.get((operation as KtToken))
if (binaryPrecedence != null) {
return (MAX_PRIORITY - 3) - binaryPrecedence.ordinal
}
}
if (expression is KtDeclaration || expression is KtStatementExpression) {
return 0
}
return MAX_PRIORITY
}

fun areParenthesesUseless(expression: KtParenthesizedExpression): Boolean {

var innerExpression: KtExpression? = expression.getExpression()
if (innerExpression == null) return true
var parent: PsiElement? = expression.parent
if (!(parent is KtElement)) return true
return ! areParenthesesNecessary(innerExpression,expression,(parent as KtElement))
}

fun areParenthesesNecessary(innerExpression: KtExpression, currentInner: KtExpression, parentElement: KtElement): Boolean {

if (parentElement is KtDelegatedSuperTypeEntry) return true
if (parentElement is KtParenthesizedExpression || innerExpression is KtParenthesizedExpression) {
return false
}
if (parentElement is KtPackageDirective) return false
if (parentElement is KtWhenExpression || innerExpression is KtWhenExpression) {
return false
}
if (parentElement is KtCollectionLiteralExpression) return false
if (innerExpression is KtIfExpression) {
if (parentElement is KtQualifiedExpression) return true
var current: PsiElement? = parentElement
while (!(current is KtBlockExpression || current is KtDeclaration || current is KtStatementExpression || current is KtFile)) {
if (current!!.textRange!!.endOffset != currentInner.textRange!!.endOffset) {
return !(current is KtParenthesizedExpression) && !(current is KtValueArgumentList)
}
current = current!!.parent
}
}
if (innerExpression is KtLambdaExpression) {
var prevSibling: PsiElement? = PsiTreeUtil.skipWhitespacesAndCommentsBackward(currentInner)
if (endWithParenthesisOrCallExpression(prevSibling)) return true
}
if (parentElement is KtCallExpression && currentInner == ((parentElement as KtCallExpression)).getCalleeExpression()) {
var parentCall: KtCallExpression = (parentElement as KtCallExpression)
var targetInnerExpression: KtExpression? = innerExpression
if (targetInnerExpression is KtDotQualifiedExpression) {
var selector: KtExpression? = ((targetInnerExpression as KtDotQualifiedExpression)).selectorExpression
if (selector != null) {
targetInnerExpression = selector
}
}
if (targetInnerExpression is KtSimpleNameExpression) return false
if ((parentElement).p1kOriginal_getQualifiedExpressionForSelector() != null) return true
if (targetInnerExpression is KtCallExpression && parentCall.getValueArgumentList() == null) return true
return !(targetInnerExpression is KtThisExpression || targetInnerExpression is KtArrayAccessExpression || targetInnerExpression is KtConstantExpression || targetInnerExpression is KtStringTemplateExpression || targetInnerExpression is KtCallExpression)
}
if (parentElement is KtValueArgument) {
var nextArg: KtValueArgument? = PsiTreeUtil.getNextSiblingOfType(parentElement,KtValueArgument ::class)
var nextExpression: PsiElement? = (if (nextArg != null) nextArg.getArgumentExpression() else null)
if (innerExpression is KtBinaryExpression && ((innerExpression as KtBinaryExpression)).getOperationToken() == KtTokens.LT && nextExpression is KtBinaryExpression && ((nextExpression as KtBinaryExpression)).getOperationToken() == KtTokens.GT) return true
}
var innerOperation: IElementType? = getOperation(innerExpression)
if (innerExpression is KtBinaryExpression) {
if (parentElement is KtBinaryExpression) {
var innerBinary: KtBinaryExpression = (innerExpression as KtBinaryExpression)
if (innerBinary.getRight() is KtReturnExpression) {
return true
}
}
if (innerOperation != KtTokens.ELVIS && !(parentElement is KtValueArgument) && !(parentElement is KtParameter) && !(parentElement is KtBlockStringTemplateEntry) && !(parentElement is KtContainerNode && !(parentElement is KtContainerNodeForControlStructureBody)) && isKeepBinaryExpressionParenthesized((innerExpression as KtBinaryExpression))) {
return true
}
}
if (!(parentElement is KtExpression)) return false
var parentOperation: IElementType? = getOperation((parentElement as KtExpression))
if (parentElement is KtReturnExpression && (innerExpression is KtLabeledExpression || innerExpression is KtAnnotatedExpression)) return true
if (innerExpression is KtBinaryExpressionWithTypeRHS && parentOperation == KtTokens.LT) {
return true
}
if (parentElement is KtLabeledExpression) return false
if (parentElement is KtBinaryExpression && parentOperation == KtTokens.ELVIS && !(innerExpression is KtBinaryExpression) && currentInner == ((parentElement as KtBinaryExpression)).getRight()) {
return false
}
if (parentElement is KtBinaryExpression && parentOperation == KtTokens.EQ && innerExpression is KtNamedFunction && currentInner == ((parentElement as KtBinaryExpression)).getRight()) {
return false
}
var innerPriority: Int = getPriority(innerExpression)
var parentPriority: Int = getPriority((parentElement as KtExpression))
if (innerPriority == parentPriority) {
if (parentElement is KtBinaryExpression) {
if (innerOperation == KtTokens.ANDAND || innerOperation == KtTokens.OROR) {
return false
}
return ((parentElement as KtBinaryExpression)).getRight() == currentInner
}
if (parentElement is KtPrefixExpression && innerExpression is KtPrefixExpression) {
if (parentOperation == KtTokens.PLUS) {
return innerOperation == KtTokens.PLUS || innerOperation == KtTokens.PLUSPLUS
}
if (parentOperation == KtTokens.MINUS) {
return innerOperation == KtTokens.MINUS || innerOperation == KtTokens.MINUSMINUS
}
}
return false
}
return innerPriority < parentPriority
}

private fun endWithParenthesisOrCallExpression(element: PsiElement?): Boolean {

if (element == null) return false
if (element.text.endsWith(KtTokens.RPAR.value) || element is KtCallExpression) return true
var children: Array<PsiElement> = element.children
var length: Int = children.size
if (length == 0) return false
return endWithParenthesisOrCallExpression(children[length - 1])
}

private fun isKeepBinaryExpressionParenthesized(expression: KtBinaryExpression): Boolean {

var expr: PsiElement? = expression.firstChild
while (expr != null) {
if (expr is PsiWhiteSpace && expr.textContains('\n')) {
return true
}
if (expr is KtOperationReferenceExpression) {
break
}
expr = expr.nextSibling
}
return (expression.getRight() is KtBinaryExpression && isKeepBinaryExpressionParenthesized((expression.getRight() as KtBinaryExpression))) || (expression.getLeft() is KtBinaryExpression && isKeepBinaryExpressionParenthesized((expression.getLeft() as KtBinaryExpression)))
}

fun isAssignment(element: PsiElement): Boolean {

return element is KtBinaryExpression && KtTokens.ALL_ASSIGNMENTS.contains(((element as KtBinaryExpression)).getOperationToken())
}

fun isOrdinaryAssignment(element: PsiElement): Boolean {

return element is KtBinaryExpression && ((element as KtBinaryExpression)).getOperationToken().equals(KtTokens.EQ)
}

fun isCast(expression: KtBinaryExpressionWithTypeRHS): Boolean {

return isSafeCast(expression) || isUnsafeCast(expression)
}

fun isSafeCast(expression: KtBinaryExpressionWithTypeRHS): Boolean {

return expression.getOperationReference().getReferencedNameElementType() == KtTokens.AS_SAFE
}

fun isUnsafeCast(expression: KtBinaryExpressionWithTypeRHS): Boolean {

return expression.getOperationReference().getReferencedNameElementType() == KtTokens.AS_KEYWORD
}

fun checkVariableDeclarationInBlock(block: KtBlockExpression, varName: String): Boolean {

for (element in block.getStatements()) {
if (element is KtVariableDeclaration) {
if (((element as KtVariableDeclaration)).getNameAsSafeName().asString().equals(varName)) {
return true
}
}
}
return false
}

fun checkWhenExpressionHasSingleElse(whenExpression: KtWhenExpression): Boolean {

var elseCount: Int = 0
for (entry in whenExpression.getEntries()) {
if (entry.isElse()) {
elseCount ++
}
}
return (elseCount == 1)
}

fun skipTrailingWhitespacesAndComments(element: PsiElement?): PsiElement? {

return PsiTreeUtil.skipSiblingsForward(element,PsiWhiteSpace ::class,PsiComment ::class)
}

fun prevLeafIgnoringWhitespaceAndComments(element: PsiElement): PsiElement? {

var prev: PsiElement? = PsiTreeUtil.prevLeaf(element,true)
while (prev != null && KtTokens.WHITE_SPACE_OR_COMMENT_BIT_SET.contains(prev.node!!.elementType)) {
prev = PsiTreeUtil.prevLeaf(prev,true)
}
return prev
}

fun getPreviousWord(element: PsiElement, word: String): PsiElement? {

var prev: PsiElement? = prevLeafIgnoringWhitespaceAndComments(element)
if (prev != null && prev.node!!.elementType == KtTokens.IDENTIFIER && word.equals(prev.text)) {
return prev
}
return null
}

fun getText(element: PsiElement?): String {

return (if (element != null) element.text else "")
}

fun getNullableText(element: PsiElement?): String? {

return (if (element != null) element.text else null)
}

fun isInComment(element: PsiElement): Boolean {

return CommentUtilCore.isComment(element) || element is KDocElement
}

fun getOutermostParent(element: PsiElement, upperBound: PsiElement, strict: Boolean): PsiElement? {

var parent: PsiElement? = (if (strict) element.parent else element)
while (parent != null && parent.parent != upperBound) {
parent = parent.parent
}
return parent
}

fun <T : PsiElement> getLastChildByType(root: PsiElement, vararg elementTypes: kotlin.reflect.KClass<out T>): T? {

var children: Array<PsiElement> = root.children
run {
var i: Int = children.size - 1
forLoop1@ while (i >= 0) {
forBody2@ do {
if (PsiTreeUtil.instanceOf(children[i],*elementTypes)) {
return (children[i] as T)
}
} while (false)
i --
}
}
return null
}

fun getOutermostDescendantElement(root: PsiElement?, first: Boolean, predicate: (KtElement) -> Boolean): KtElement? {

if (!(root is KtElement)) return null
var results: MutableList<KtElement> = ArrayList()
root.accept(object : KtVisitorVoid() {
override fun visitKtElement(element: KtElement): Unit {

if(predicate(element)) {
results.add(element)
}
else {
element.acceptChildren(this)
}
}
})
if (results.isEmpty()) return null
return (if (first) results.get(0) else results.get(results.size - 1))
}

fun findChildByType(element: PsiElement, type: IElementType): PsiElement? {

var node: ASTNode? = element.node!!.findChildByType(type)
return (if (node == null) null else node.psi)
}

fun skipSiblingsBackwardByPredicate(element: PsiElement?, elementsToSkip: (PsiElement) -> Boolean): PsiElement? {

if (element == null) return null
run {
var e: PsiElement? = element.prevSibling
forLoop3@ while (e != null) {
forBody4@ do {
if (elementsToSkip(e)) break@forBody4
return e
} while (false)
e = e.prevSibling
}
}
return null
}

fun ascendIfPropertyAccessor(element: PsiElement): PsiElement {

if (element is KtPropertyAccessor) {
return element.parent!!
}
return element
}

fun replaceModifierList(owner: KtModifierListOwner, modifierList: KtModifierList?): KtModifierList? {

return KtPsiMutationService.getInstance().replaceModifierList(owner,modifierList)
}

fun getPackageName(element: KtElement): String? {

var file: KtFile = element.getContainingKtFile()
var header: KtPackageDirective? = PsiTreeUtil.findChildOfType(file,KtPackageDirective ::class)
return (if (header != null) header.getQualifiedName() else null)
}

fun getEnclosingElementForLocalDeclaration(declaration: KtDeclaration): KtElement? {

return getEnclosingElementForLocalDeclaration(declaration,true)
}

private fun isMemberOfObjectExpression(propertyOrFunction: KtCallableDeclaration): Boolean {

var parent: PsiElement? = PsiTreeUtil.getStubOrPsiParent(propertyOrFunction)
if (!(parent is KtClassBody)) return false
var grandparent: PsiElement? = PsiTreeUtil.getStubOrPsiParent(parent)
if (!(grandparent is KtObjectDeclaration)) return false
return PsiTreeUtil.getStubOrPsiParent(grandparent) is KtObjectLiteralExpression
}

private fun isNonLocalCallable(declaration: KtDeclaration?): Boolean {

if (declaration is KtProperty) {
return !((declaration as KtProperty)).isLocal
}
else if (declaration is KtFunction) {
return !((declaration as KtFunction)).isLocal
}
return false
}

fun getEnclosingElementForLocalDeclaration(declaration: KtDeclaration, skipParameters: Boolean): KtElement? {
var declaration: KtDeclaration? = declaration
if (declaration is KtTypeParameter && skipParameters) {
declaration = PsiTreeUtil.getParentOfType(declaration,KtNamedDeclaration ::class)
}
else if (declaration is KtParameter) {
var functionType: KtFunctionType? = PsiTreeUtil.getParentOfType(declaration,KtFunctionType ::class)
if (functionType != null) {
return functionType
}
var parent: PsiElement? = declaration.parent
if (((declaration as KtParameter)).hasValOrVar() && parent != null && parent.parent is KtPrimaryConstructor) {
return getEnclosingElementForLocalDeclaration(((parent.parent as KtPrimaryConstructor)).getContainingClassOrObject(),skipParameters)
}
else if (skipParameters && parent != null && !(parent is KtForExpression) && parent.parent is KtNamedFunction) {
declaration = (parent.parent as KtNamedFunction)
}
}
if (declaration is PsiFile) {
return declaration
}
var current: PsiElement? = PsiTreeUtil.getStubOrPsiParent(declaration)
var isNonLocalCallable: Boolean = isNonLocalCallable(declaration)
while (current != null) {
if (current is PsiFile) {
return null
}
var parent: PsiElement? = PsiTreeUtil.getStubOrPsiParent(current)
if (parent is KtScript) return null
if (current is KtAnonymousInitializer) {
return ((current as KtAnonymousInitializer)).body
}
if (current is KtProperty || current is KtFunction) {
if (parent is KtFile) {
return (current as KtElement)
}
else if (parent is KtClassBody && ! isMemberOfObjectExpression((current as KtCallableDeclaration))) {
return (parent as KtElement)
}
else if (parent is KtBlockExpression) {
var grandParent: PsiElement? = parent.parent
if (grandParent is KtScript) {
return (parent as KtElement)
}
}
}
if (current is KtParameter) {
return (current as KtElement)
}
if (current is KtValueArgument) {
if (! isNonLocalCallable) {
return (current as KtElement)
}
}
if (current is KtBlockExpression) {
if (! isNonLocalCallable || !(current.parent is KtFunctionLiteral)) {
return (current as KtElement)
}
}
if (current is KtDelegatedSuperTypeEntry || current is KtSuperTypeCallEntry) {
var grandParent: PsiElement? = current.parent!!.parent
if (grandParent is KtClassOrObject && !(grandParent.parent is KtObjectLiteralExpression)) {
return (grandParent as KtElement)
}
}
current = parent
}
return null
}

fun isLocal(declaration: KtDeclaration): Boolean {

return getEnclosingElementForLocalDeclaration(declaration) != null
}

fun getOperationToken(expression: KtOperationExpression): KtToken? {

var operationExpression: KtSimpleNameExpression = expression.getOperationReference()
var elementType: IElementType? = operationExpression.getReferencedNameElementType()
assert(elementType == null || elementType is KtToken) { "KtOperationExpression should have operation token of type KtToken: " + expression }
return (elementType as KtToken?)
}

fun isLabelIdentifierExpression(element: PsiElement): Boolean {

return element is KtLabelReferenceExpression
}

fun getParentCallIfPresent(expression: KtExpression): KtExpression? {

var parent: PsiElement? = expression.parent
while (parent != null) {
if (parent is KtBinaryExpression || parent is KtUnaryExpression || parent is KtLabeledExpression || parent is KtDotQualifiedExpression || parent is KtCallExpression || parent is KtArrayAccessExpression || parent is KtDestructuringDeclaration) {
if (parent is KtLabeledExpression) {
parent = parent.parent
continue
}
return (parent as KtExpression)
}
else if (parent is KtParenthesizedExpression || parent is KtBinaryExpressionWithTypeRHS) {
parent = parent.parent
}
else if (parent is KtValueArgument || parent is KtValueArgumentList) {
parent = parent.parent
}
else if (parent is KtLambdaExpression || parent is KtAnnotatedExpression) {
parent = parent.parent
}
else {
return null
}
}
return null
}

fun isLabeledFunctionLiteral(functionLiteral: KtFunctionLiteral): Boolean {

return functionLiteral.parent!!.parent is KtLabeledExpression
}

fun getLastElementDeparenthesized(expression: KtExpression?, statementFilter: StatementFilter): KtExpression? {

var deparenthesizedExpression: KtExpression? = deparenthesize(expression)
if (deparenthesizedExpression is KtBlockExpression) {
var blockExpression: KtBlockExpression = (deparenthesizedExpression as KtBlockExpression)
var lastStatementInABlock: KtExpression? = (statementFilter).p1kOriginal_getLastStatementInABlock(blockExpression)
if (lastStatementInABlock != null) {
return getLastElementDeparenthesized(lastStatementInABlock,statementFilter)
}
}
return deparenthesizedExpression
}

fun isStatementContainer(container: PsiElement?): Boolean {

return container is KtBlockExpression || container is KtContainerNodeForControlStructureBody || container is KtWhenEntry
}

fun isStatement(element: PsiElement): Boolean {

return isStatementContainer(element.parent)
}

fun isInOrNotInOperation(binaryExpression: KtBinaryExpression): Boolean {

return isInOperation(binaryExpression) || isNotInOperation(binaryExpression)
}

fun isNotInOperation(binaryExpression: KtBinaryExpression): Boolean {

return (binaryExpression.getOperationToken() == KtTokens.NOT_IN)
}

private fun isInOperation(binaryExpression: KtBinaryExpression): Boolean {

return (binaryExpression.getOperationToken() == KtTokens.IN_KEYWORD)
}
}
}
