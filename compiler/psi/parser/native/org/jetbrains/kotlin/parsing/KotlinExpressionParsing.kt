/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.parsing

import org.jetbrains.kotlin.psi.entries







import com.intellij.lang.ASTNode
import com.intellij.lang.Language
import com.intellij.lang.PsiBuilder
import com.intellij.openapi.project.Project
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.lang.BinaryOperationPrecedence
import org.jetbrains.kotlin.lexer.KtToken
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.parsing.KotlinParsing.NameParsingMode
import org.jetbrains.kotlin.psi.stubs.elements.KtTokenSets
import org.jetbrains.kotlin.KtNodeTypes.Companion.ANNOTATED_EXPRESSION
import org.jetbrains.kotlin.KtNodeTypes.Companion.ARRAY_ACCESS_EXPRESSION
import org.jetbrains.kotlin.KtNodeTypes.Companion.BINARY_EXPRESSION
import org.jetbrains.kotlin.KtNodeTypes.Companion.BINARY_WITH_TYPE
import org.jetbrains.kotlin.KtNodeTypes.Companion.BLOCK
import org.jetbrains.kotlin.KtNodeTypes.Companion.BODY
import org.jetbrains.kotlin.KtNodeTypes.Companion.BOOLEAN_CONSTANT
import org.jetbrains.kotlin.KtNodeTypes.Companion.BREAK
import org.jetbrains.kotlin.KtNodeTypes.Companion.CALLABLE_REFERENCE_EXPRESSION
import org.jetbrains.kotlin.KtNodeTypes.Companion.CALL_EXPRESSION
import org.jetbrains.kotlin.KtNodeTypes.Companion.CATCH
import org.jetbrains.kotlin.KtNodeTypes.Companion.CHARACTER_CONSTANT
import org.jetbrains.kotlin.KtNodeTypes.Companion.CLASS_LITERAL_EXPRESSION
import org.jetbrains.kotlin.KtNodeTypes.Companion.COLLECTION_LITERAL_EXPRESSION
import org.jetbrains.kotlin.KtNodeTypes.Companion.CONDITION
import org.jetbrains.kotlin.KtNodeTypes.Companion.CONTINUE
import org.jetbrains.kotlin.KtNodeTypes.Companion.CONTRACT_EFFECT
import org.jetbrains.kotlin.KtNodeTypes.Companion.CONTRACT_EFFECT_LIST
import org.jetbrains.kotlin.KtNodeTypes.Companion.DESTRUCTURING_DECLARATION
import org.jetbrains.kotlin.KtNodeTypes.Companion.DOT_QUALIFIED_EXPRESSION
import org.jetbrains.kotlin.KtNodeTypes.Companion.DO_WHILE
import org.jetbrains.kotlin.KtNodeTypes.Companion.ELSE
import org.jetbrains.kotlin.KtNodeTypes.Companion.ESCAPE_STRING_TEMPLATE_ENTRY
import org.jetbrains.kotlin.KtNodeTypes.Companion.FINALLY
import org.jetbrains.kotlin.KtNodeTypes.Companion.FLOAT_CONSTANT
import org.jetbrains.kotlin.KtNodeTypes.Companion.FOR
import org.jetbrains.kotlin.KtNodeTypes.Companion.FUNCTION_LITERAL
import org.jetbrains.kotlin.KtNodeTypes.Companion.IF
import org.jetbrains.kotlin.KtNodeTypes.Companion.INDICES
import org.jetbrains.kotlin.KtNodeTypes.Companion.INTEGER_CONSTANT
import org.jetbrains.kotlin.KtNodeTypes.Companion.IS_EXPRESSION
import org.jetbrains.kotlin.KtNodeTypes.Companion.LABEL
import org.jetbrains.kotlin.KtNodeTypes.Companion.LABELED_EXPRESSION
import org.jetbrains.kotlin.KtNodeTypes.Companion.LABEL_QUALIFIER
import org.jetbrains.kotlin.KtNodeTypes.Companion.LAMBDA_ARGUMENT
import org.jetbrains.kotlin.KtNodeTypes.Companion.LAMBDA_EXPRESSION
import org.jetbrains.kotlin.KtNodeTypes.Companion.LITERAL_STRING_TEMPLATE_ENTRY
import org.jetbrains.kotlin.KtNodeTypes.Companion.LONG_STRING_TEMPLATE_ENTRY
import org.jetbrains.kotlin.KtNodeTypes.Companion.LOOP_RANGE
import org.jetbrains.kotlin.KtNodeTypes.Companion.NULL
import org.jetbrains.kotlin.KtNodeTypes.Companion.OBJECT_DECLARATION
import org.jetbrains.kotlin.KtNodeTypes.Companion.OBJECT_LITERAL
import org.jetbrains.kotlin.KtNodeTypes.Companion.OPERATION_REFERENCE
import org.jetbrains.kotlin.KtNodeTypes.Companion.PARENTHESIZED
import org.jetbrains.kotlin.KtNodeTypes.Companion.POSTFIX_EXPRESSION
import org.jetbrains.kotlin.KtNodeTypes.Companion.PREFIX_EXPRESSION
import org.jetbrains.kotlin.KtNodeTypes.Companion.PROPERTY
import org.jetbrains.kotlin.KtNodeTypes.Companion.REFERENCE_EXPRESSION
import org.jetbrains.kotlin.KtNodeTypes.Companion.RETURN
import org.jetbrains.kotlin.KtNodeTypes.Companion.SAFE_ACCESS_EXPRESSION
import org.jetbrains.kotlin.KtNodeTypes.Companion.SCRIPT_INITIALIZER
import org.jetbrains.kotlin.KtNodeTypes.Companion.SHORT_STRING_TEMPLATE_ENTRY
import org.jetbrains.kotlin.KtNodeTypes.Companion.STRING_INTERPOLATION_PREFIX
import org.jetbrains.kotlin.KtNodeTypes.Companion.STRING_TEMPLATE
import org.jetbrains.kotlin.KtNodeTypes.Companion.SUPER_EXPRESSION
import org.jetbrains.kotlin.KtNodeTypes.Companion.THEN
import org.jetbrains.kotlin.KtNodeTypes.Companion.THIS_EXPRESSION
import org.jetbrains.kotlin.KtNodeTypes.Companion.THROW
import org.jetbrains.kotlin.KtNodeTypes.Companion.TRY
import org.jetbrains.kotlin.KtNodeTypes.Companion.TYPE_ARGUMENT_LIST
import org.jetbrains.kotlin.KtNodeTypes.Companion.VALUE_ARGUMENT
import org.jetbrains.kotlin.KtNodeTypes.Companion.VALUE_ARGUMENT_LIST
import org.jetbrains.kotlin.KtNodeTypes.Companion.VALUE_ARGUMENT_NAME
import org.jetbrains.kotlin.KtNodeTypes.Companion.VALUE_PARAMETER
import org.jetbrains.kotlin.KtNodeTypes.Companion.VALUE_PARAMETER_LIST
import org.jetbrains.kotlin.KtNodeTypes.Companion.WHEN
import org.jetbrains.kotlin.KtNodeTypes.Companion.WHEN_CONDITION_EXPRESSION
import org.jetbrains.kotlin.KtNodeTypes.Companion.WHEN_CONDITION_IN_RANGE
import org.jetbrains.kotlin.KtNodeTypes.Companion.WHEN_CONDITION_IS_PATTERN
import org.jetbrains.kotlin.KtNodeTypes.Companion.WHEN_ENTRY
import org.jetbrains.kotlin.KtNodeTypes.Companion.WHEN_ENTRY_GUARD
import org.jetbrains.kotlin.KtNodeTypes.Companion.WHILE
import org.jetbrains.kotlin.lang.BinaryOperationPrecedence.Companion.TOKEN_TO_BINARY_PRECEDENCE_MAP_WITH_SOFT_IDENTIFIERS
import org.jetbrains.kotlin.lexer.KtTokens.Companion.ANDAND
import org.jetbrains.kotlin.lexer.KtTokens.Companion.ARROW
import org.jetbrains.kotlin.lexer.KtTokens.Companion.ARROW_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.AS_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.AS_KEYWORD_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.AS_SAFE
import org.jetbrains.kotlin.lexer.KtTokens.Companion.AS_SAFE_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.AT
import org.jetbrains.kotlin.lexer.KtTokens.Companion.BREAK_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.BREAK_KEYWORD_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.CATCH_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.CHARACTER_LITERAL
import org.jetbrains.kotlin.lexer.KtTokens.Companion.CHARACTER_LITERAL_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.CLASS_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.CLASS_KEYWORD_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.CLOSING_QUOTE
import org.jetbrains.kotlin.lexer.KtTokens.Companion.COLON
import org.jetbrains.kotlin.lexer.KtTokens.Companion.COLONCOLON
import org.jetbrains.kotlin.lexer.KtTokens.Companion.COMMA
import org.jetbrains.kotlin.lexer.KtTokens.Companion.CONTEXT_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.CONTINUE_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.CONTINUE_KEYWORD_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.CONTRACT_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.DANGLING_NEWLINE
import org.jetbrains.kotlin.lexer.KtTokens.Companion.DIV
import org.jetbrains.kotlin.lexer.KtTokens.Companion.DIVEQ
import org.jetbrains.kotlin.lexer.KtTokens.Companion.DOT
import org.jetbrains.kotlin.lexer.KtTokens.Companion.DOT_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.DO_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.DO_KEYWORD_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.ELSE_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.ELSE_KEYWORD_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.ELVIS
import org.jetbrains.kotlin.lexer.KtTokens.Companion.EOL_OR_SEMICOLON
import org.jetbrains.kotlin.lexer.KtTokens.Companion.EQ
import org.jetbrains.kotlin.lexer.KtTokens.Companion.EQEQ
import org.jetbrains.kotlin.lexer.KtTokens.Companion.EQEQEQ
import org.jetbrains.kotlin.lexer.KtTokens.Companion.ESCAPE_SEQUENCE
import org.jetbrains.kotlin.lexer.KtTokens.Companion.EXCL
import org.jetbrains.kotlin.lexer.KtTokens.Companion.EXCLEQ
import org.jetbrains.kotlin.lexer.KtTokens.Companion.EXCLEQEQEQ
import org.jetbrains.kotlin.lexer.KtTokens.Companion.EXCLEXCL
import org.jetbrains.kotlin.lexer.KtTokens.Companion.FALSE_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.FALSE_KEYWORD_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.FINALLY_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.FLOAT_LITERAL
import org.jetbrains.kotlin.lexer.KtTokens.Companion.FLOAT_LITERAL_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.FOR_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.FOR_KEYWORD_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.FUN_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.FUN_KEYWORD_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.GT
import org.jetbrains.kotlin.lexer.KtTokens.Companion.IDENTIFIER
import org.jetbrains.kotlin.lexer.KtTokens.Companion.IDENTIFIER_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.IF_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.IF_KEYWORD_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.INTEGER_LITERAL
import org.jetbrains.kotlin.lexer.KtTokens.Companion.INTEGER_LITERAL_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.INTERFACE_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.INTERFACE_KEYWORD_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.INTERPOLATION_PREFIX
import org.jetbrains.kotlin.lexer.KtTokens.Companion.INTERPOLATION_PREFIX_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.IN_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.IN_KEYWORD_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.IS_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.IS_KEYWORD_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.KEYWORDS
import org.jetbrains.kotlin.lexer.KtTokens.Companion.LBRACE
import org.jetbrains.kotlin.lexer.KtTokens.Companion.LBRACE_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.LBRACKET
import org.jetbrains.kotlin.lexer.KtTokens.Companion.LBRACKET_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.LONG_TEMPLATE_ENTRY_END
import org.jetbrains.kotlin.lexer.KtTokens.Companion.LONG_TEMPLATE_ENTRY_START
import org.jetbrains.kotlin.lexer.KtTokens.Companion.LPAR
import org.jetbrains.kotlin.lexer.KtTokens.Companion.LPAR_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.LT
import org.jetbrains.kotlin.lexer.KtTokens.Companion.LTEQ
import org.jetbrains.kotlin.lexer.KtTokens.Companion.MINUS
import org.jetbrains.kotlin.lexer.KtTokens.Companion.MINUSEQ
import org.jetbrains.kotlin.lexer.KtTokens.Companion.MINUSMINUS
import org.jetbrains.kotlin.lexer.KtTokens.Companion.MODIFIER_KEYWORDS
import org.jetbrains.kotlin.lexer.KtTokens.Companion.MUL
import org.jetbrains.kotlin.lexer.KtTokens.Companion.MULTEQ
import org.jetbrains.kotlin.lexer.KtTokens.Companion.NOT_IN
import org.jetbrains.kotlin.lexer.KtTokens.Companion.NOT_IN_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.NOT_IS
import org.jetbrains.kotlin.lexer.KtTokens.Companion.NOT_IS_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.NULL_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.NULL_KEYWORD_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.OBJECT_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.OBJECT_KEYWORD_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.OPEN_QUOTE
import org.jetbrains.kotlin.lexer.KtTokens.Companion.OPEN_QUOTE_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.OROR
import org.jetbrains.kotlin.lexer.KtTokens.Companion.PACKAGE_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.PERC
import org.jetbrains.kotlin.lexer.KtTokens.Companion.PERCEQ
import org.jetbrains.kotlin.lexer.KtTokens.Companion.PLUS
import org.jetbrains.kotlin.lexer.KtTokens.Companion.PLUSEQ
import org.jetbrains.kotlin.lexer.KtTokens.Companion.PLUSPLUS
import org.jetbrains.kotlin.lexer.KtTokens.Companion.QUEST
import org.jetbrains.kotlin.lexer.KtTokens.Companion.RANGE
import org.jetbrains.kotlin.lexer.KtTokens.Companion.RANGE_UNTIL
import org.jetbrains.kotlin.lexer.KtTokens.Companion.RBRACE
import org.jetbrains.kotlin.lexer.KtTokens.Companion.RBRACE_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.RBRACKET
import org.jetbrains.kotlin.lexer.KtTokens.Companion.REGULAR_STRING_PART
import org.jetbrains.kotlin.lexer.KtTokens.Companion.RETURN_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.RETURN_KEYWORD_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.RPAR
import org.jetbrains.kotlin.lexer.KtTokens.Companion.SAFE_ACCESS
import org.jetbrains.kotlin.lexer.KtTokens.Companion.SEMICOLON
import org.jetbrains.kotlin.lexer.KtTokens.Companion.SHORT_TEMPLATE_ENTRY_START
import org.jetbrains.kotlin.lexer.KtTokens.Companion.SOFT_KEYWORDS
import org.jetbrains.kotlin.lexer.KtTokens.Companion.SUPER_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.SUPER_KEYWORD_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.THIS_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.THIS_KEYWORD_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.THROW_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.THROW_KEYWORD_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.TRUE_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.TRUE_KEYWORD_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.TRY_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.TRY_KEYWORD_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.TYPE_ALIAS_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.TYPE_ALIAS_KEYWORD_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.VAL_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.VAL_KEYWORD_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.VAR_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.VAR_KEYWORD_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.WHEN_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.WHEN_KEYWORD_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.WHILE_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.WHILE_KEYWORD_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.WHITE_SPACE_OR_COMMENT_BIT_SET
import org.jetbrains.kotlin.parsing.KotlinParsing.DeclarationParsingMode
import org.jetbrains.kotlin.parsing.KotlinParsing.Companion.EXPRESSION_FIRST
import org.jetbrains.kotlin.parsing.KotlinParsing.Companion.EXPRESSION_FOLLOW
import org.jetbrains.kotlin.parsing.KotlinParsing.ModifierDetector
import org.jetbrains.kotlin.parsing.KotlinParsing.MultiDeclarationMode
import org.jetbrains.kotlin.parsing.KotlinParsing.Companion.PARAMETER_NAME_RECOVERY_SET
import org.jetbrains.kotlin.parsing.KotlinParsing.AnnotationParsingMode.DEFAULT
import org.jetbrains.kotlin.parsing.PRECEDING_ALL_COMMENTS_BINDER
import org.jetbrains.kotlin.parsing.TRAILING_ALL_COMMENTS_BINDER



open class KotlinExpressionParsing : AbstractKotlinParsing {
private val myKotlinParsing: KotlinParsing

constructor(builder: SemanticWhitespaceAwarePsiBuilder, kotlinParsing: KotlinParsing) : this(builder,kotlinParsing,true) {


}

constructor(builder: SemanticWhitespaceAwarePsiBuilder, kotlinParsing: KotlinParsing, isLazy: Boolean) : super(builder,isLazy) {

myKotlinParsing = kotlinParsing
}

open fun parseExpression(): Unit {

parseExpression("Expecting an expression")
}

private fun parseExpression(messageIfNotExpressionFirst: String): Unit {

if (! atSet(EXPRESSION_FIRST)) {
error(messageIfNotExpressionFirst)
return 
}
parseBinaryExpression(MAX_BINARY_OPERATION_PRECEDENCE)
}

private fun parseBinaryExpression(maxPrecedence: BinaryOperationPrecedence?): BinaryOperationPrecedence {

if (maxPrecedence == null) {
throw IllegalArgumentException("Shouldn't be here")
}
var expression: PsiBuilder.Marker = mark()
parsePrefixExpression()
var minPrecedence: BinaryOperationPrecedence = MIN_BINARY_OPERATION_PRECEDENCE
while (! interruptedWithNewLine()) {
var token: IElementType? = tt()
var operation: KtToken? = (if (token is KtToken) (token as KtToken) else null)
if (operation == null) break
var nextPrecedence: BinaryOperationPrecedence? = TOKEN_TO_BINARY_PRECEDENCE_MAP_WITH_SOFT_IDENTIFIERS.get(token)
if (nextPrecedence == null || nextPrecedence.ordinal > maxPrecedence.ordinal || nextPrecedence.ordinal < minPrecedence.ordinal) {
break
}
if (nextPrecedence == BinaryOperationPrecedence.INFIX && SOFT_KEYWORDS.contains(operation)) {
myBuilder.remapCurrentToken(KtTokens.IDENTIFIER)
}
parseOperationReference()
var resultType: IElementType?
switch1@ do {
when (operation.tokenId) {
AS_KEYWORD_Id -> {
myKotlinParsing.parseTypeRefWithoutIntersections()
minPrecedence = BinaryOperationPrecedence.AS
resultType = KtNodeTypes.BINARY_WITH_TYPE
break@switch1
}
AS_SAFE_Id -> {
myKotlinParsing.parseTypeRefWithoutIntersections()
minPrecedence = BinaryOperationPrecedence.AS
resultType = KtNodeTypes.BINARY_WITH_TYPE
break@switch1
}
IS_KEYWORD_Id -> {
myKotlinParsing.parseTypeRefWithoutIntersections()
minPrecedence = BinaryOperationPrecedence.IN_OR_IS
resultType = KtNodeTypes.IS_EXPRESSION
break@switch1
}
NOT_IS_Id -> {
myKotlinParsing.parseTypeRefWithoutIntersections()
minPrecedence = BinaryOperationPrecedence.IN_OR_IS
resultType = KtNodeTypes.IS_EXPRESSION
break@switch1
}
else -> {
minPrecedence = parseBinaryExpression(nextPrecedence.higherPriority)
resultType = KtNodeTypes.BINARY_EXPRESSION
break@switch1
}
}
} while (false)
expression.done(resultType)
expression = expression.precede()
}
expression.drop()
return minPrecedence
}

private fun parseLabeledExpression(): Unit {

var expression: PsiBuilder.Marker = mark()
parseLabelDefinition()
parsePrefixExpression()
expression.done(LABELED_EXPRESSION)
}

private fun parsePrefixExpression(): Unit {

if (at(AT)) {
if (! parseLocalDeclaration(false,false)) {
var expression: PsiBuilder.Marker = mark()
myKotlinParsing.parseAnnotations(DEFAULT)
parsePrefixExpression()
expression.done(ANNOTATED_EXPRESSION)
}
}
else {
myBuilder.disableJoiningComplexTokens()
if (isAtLabelDefinitionOrMissingIdentifier()) {
myBuilder.restoreJoiningComplexTokensState()
parseLabeledExpression()
}
else if (atSet(KtTokenSets.PREFIX_OPERATIONS)) {
var expression: PsiBuilder.Marker = mark()
parseOperationReference()
myBuilder.restoreJoiningComplexTokensState()
parsePrefixExpression()
expression.done(PREFIX_EXPRESSION)
}
else {
myBuilder.restoreJoiningComplexTokensState()
parsePostfixExpression()
}
}
}

private fun parseDoubleColonSuffix(expression: PsiBuilder.Marker): Boolean {

if (! at(COLONCOLON)) return false
advance()
if (at(CLASS_KEYWORD)) {
advance()
expression.done(CLASS_LITERAL_EXPRESSION)
return true
}
parseSimpleNameExpression()
if (at(LT)) {
var typeArgumentList: PsiBuilder.Marker = mark()
if (myKotlinParsing.tryParseTypeArgumentList(TYPE_ARGUMENT_LIST_STOPPERS)) {
typeArgumentList.error("Type arguments are not allowed")
}
else {
typeArgumentList.rollbackTo()
}
}
if (at(LPAR) && ! myBuilder.newlineBeforeCurrentToken()) {
var lpar: PsiBuilder.Marker = mark()
parseCallSuffix()
lpar.error("This syntax is reserved for future use; to call a reference, enclose it in parentheses: (foo::bar)(args)")
}
expression.done(CALLABLE_REFERENCE_EXPRESSION)
return true
}

private fun skipQuestionMarksBeforeDoubleColon(): Unit {

if (at(QUEST)) {
var k: Int = 1
while (lookahead(k) == QUEST) k ++
if (lookahead(k) == COLONCOLON) {
while (k > 0) {
advance()
k --
}
}
}
}

private fun parsePostfixExpression(): Unit {

var expression: PsiBuilder.Marker = mark()
var firstExpressionParsed: Boolean = (if (at(COLONCOLON)) parseDoubleColonSuffix(mark()) else parseAtomicExpression())
while (true) {
if (interruptedWithNewLine()) {
break
}
else if (at(LBRACKET)) {
parseArrayAccess()
expression.done(ARRAY_ACCESS_EXPRESSION)
}
else if (parseCallSuffix()) {
expression.done(CALL_EXPRESSION)
}
else if (at(DOT) || at(SAFE_ACCESS)) {
var expressionType: IElementType? = (if (at(DOT)) DOT_QUALIFIED_EXPRESSION else SAFE_ACCESS_EXPRESSION)
advance()
if (! firstExpressionParsed) {
expression.drop()
expression = mark()
firstExpressionParsed = parseAtomicExpression()
continue
}
parseSelectorCallExpression()
expression.done(expressionType!!)
}
else if (atSet(KtTokenSets.POSTFIX_OPERATIONS)) {
parseOperationReference()
expression.done(POSTFIX_EXPRESSION)
}
else {
skipQuestionMarksBeforeDoubleColon()
if (! parseDoubleColonSuffix(expression)) {
break
}
}
expression = expression.precede()
}
expression.drop()
}

private fun parseCallSuffix(): Boolean {

if (parseCallWithClosure()) {

}
else if (at(LPAR)) {
parseValueArgumentList()
parseCallWithClosure()
}
else if (at(LT)) {
var typeArgumentList: PsiBuilder.Marker = mark()
if (myKotlinParsing.tryParseTypeArgumentList(TYPE_ARGUMENT_LIST_STOPPERS)) {
typeArgumentList.done(TYPE_ARGUMENT_LIST)
if (! myBuilder.newlineBeforeCurrentToken() && at(LPAR)) parseValueArgumentList()
parseCallWithClosure()
}
else {
typeArgumentList.rollbackTo()
return false
}
}
else {
return false
}
return true
}

private fun parseSelectorCallExpression(): Unit {

var mark: PsiBuilder.Marker = mark()
parseAtomicExpression()
if (! myBuilder.newlineBeforeCurrentToken() && parseCallSuffix()) {
mark.done(CALL_EXPRESSION)
}
else {
mark.drop()
}
}

private fun parseOperationReference(): Unit {

var operationReference: PsiBuilder.Marker = mark()
advance()
operationReference.done(OPERATION_REFERENCE)
}

protected open fun parseCallWithClosure(): Boolean {

var success: Boolean = false
while (true) {
var argument: PsiBuilder.Marker = mark()
if (! parseAnnotatedLambda(false)) {
argument.drop()
break
}
argument.done(LAMBDA_ARGUMENT)
success = true
}
return success
}

private fun parseAnnotatedLambda(preferBlock: Boolean): Boolean {

var annotated: PsiBuilder.Marker = mark()
var wereAnnotations: Boolean = myKotlinParsing.parseAnnotations(DEFAULT)
var labeled: PsiBuilder.Marker = mark()
var wasLabel: Boolean = isAtLabelDefinitionOrMissingIdentifier()
if (wasLabel) {
parseLabelDefinition()
}
if (! at(LBRACE)) {
annotated.rollbackTo()
return false
}
parseFunctionLiteral(preferBlock,true)
doneOrDrop(labeled,LABELED_EXPRESSION,wasLabel)
doneOrDrop(annotated,ANNOTATED_EXPRESSION,wereAnnotations)
return true
}

open fun isAtLabelDefinitionOrMissingIdentifier(): Boolean {

return (at(IDENTIFIER) && myBuilder.rawLookup(1) == AT) || at(AT)
}

private fun parseAtomicExpression(): Boolean {

var ok: Boolean = true
switch2@ do {
when (getTokenId()) {
LPAR_Id -> {
parseParenthesizedExpression()
break@switch2
}
LBRACKET_Id -> {
parseCollectionLiteralExpression()
break@switch2
}
THIS_KEYWORD_Id -> {
parseThisExpression()
break@switch2
}
SUPER_KEYWORD_Id -> {
parseSuperExpression()
break@switch2
}
OBJECT_KEYWORD_Id -> {
parseObjectLiteral()
break@switch2
}
THROW_KEYWORD_Id -> {
parseThrow()
break@switch2
}
RETURN_KEYWORD_Id -> {
parseReturn()
break@switch2
}
CONTINUE_KEYWORD_Id -> {
parseJump(CONTINUE)
break@switch2
}
BREAK_KEYWORD_Id -> {
parseJump(BREAK)
break@switch2
}
IF_KEYWORD_Id -> {
parseIf()
break@switch2
}
WHEN_KEYWORD_Id -> {
parseWhen()
break@switch2
}
TRY_KEYWORD_Id -> {
parseTry()
break@switch2
}
FOR_KEYWORD_Id -> {
parseFor()
break@switch2
}
WHILE_KEYWORD_Id -> {
parseWhile()
break@switch2
}
DO_KEYWORD_Id -> {
parseDoWhile()
break@switch2
}
IDENTIFIER_Id -> {
if (at(CONTEXT_KEYWORD) && lookahead(1) == LPAR) {
if (parseLocalDeclaration(true,false)) {
break@switch2
}
else {
at(IDENTIFIER)
}
}
parseSimpleNameExpression()
break@switch2
}
LBRACE_Id -> {
parseFunctionLiteral()
break@switch2
}
INTERPOLATION_PREFIX_Id -> {
parseStringTemplate()
break@switch2
}
OPEN_QUOTE_Id -> {
parseStringTemplate()
break@switch2
}
TRUE_KEYWORD_Id -> {
parseOneTokenExpression(BOOLEAN_CONSTANT)
break@switch2
}
FALSE_KEYWORD_Id -> {
parseOneTokenExpression(BOOLEAN_CONSTANT)
break@switch2
}
INTEGER_LITERAL_Id -> {
parseOneTokenExpression(INTEGER_CONSTANT)
break@switch2
}
CHARACTER_LITERAL_Id -> {
parseOneTokenExpression(CHARACTER_CONSTANT)
break@switch2
}
FLOAT_LITERAL_Id -> {
parseOneTokenExpression(FLOAT_CONSTANT)
break@switch2
}
NULL_KEYWORD_Id -> {
parseOneTokenExpression(NULL)
break@switch2
}
CLASS_KEYWORD_Id -> {
if (! parseLocalDeclaration(myBuilder.newlineBeforeCurrentToken(),false)) {
ok = false
}
break@switch2
}
INTERFACE_KEYWORD_Id -> {
if (! parseLocalDeclaration(myBuilder.newlineBeforeCurrentToken(),false)) {
ok = false
}
break@switch2
}
FUN_KEYWORD_Id -> {
if (! parseLocalDeclaration(myBuilder.newlineBeforeCurrentToken(),false)) {
ok = false
}
break@switch2
}
VAL_KEYWORD_Id -> {
if (! parseLocalDeclaration(myBuilder.newlineBeforeCurrentToken(),false)) {
ok = false
}
break@switch2
}
VAR_KEYWORD_Id -> {
if (! parseLocalDeclaration(myBuilder.newlineBeforeCurrentToken(),false)) {
ok = false
}
break@switch2
}
TYPE_ALIAS_KEYWORD_Id -> {
if (! parseLocalDeclaration(myBuilder.newlineBeforeCurrentToken(),false)) {
ok = false
}
break@switch2
}
else -> {
ok = false
}
}
} while (false)
if (! ok) {
errorWithRecovery("Expecting an element",TokenSet.orSet(EXPRESSION_FOLLOW,TokenSet.create(LONG_TEMPLATE_ENTRY_END)))
}
return ok
}

private fun parseStringTemplate(): Unit {

assert(_at(INTERPOLATION_PREFIX) || _at(OPEN_QUOTE))
var template: PsiBuilder.Marker = mark()
if (at(INTERPOLATION_PREFIX)) {
var mark: PsiBuilder.Marker = mark()
advance()
mark.done(STRING_INTERPOLATION_PREFIX)
}
assert(_at(OPEN_QUOTE))
advance()
while (! eof()) {
if (at(CLOSING_QUOTE) || at(DANGLING_NEWLINE)) {
break
}
parseStringTemplateElement()
}
if (at(DANGLING_NEWLINE)) {
errorAndAdvance("Expecting '\"'")
}
else {
expect(CLOSING_QUOTE,"Expecting '\"'")
}
template.done(STRING_TEMPLATE)
}

private fun parseStringTemplateElement(): Unit {

if (at(REGULAR_STRING_PART)) {
var mark: PsiBuilder.Marker = mark()
advance()
mark.done(LITERAL_STRING_TEMPLATE_ENTRY)
}
else if (at(ESCAPE_SEQUENCE)) {
var mark: PsiBuilder.Marker = mark()
advance()
mark.done(ESCAPE_STRING_TEMPLATE_ENTRY)
}
else if (at(SHORT_TEMPLATE_ENTRY_START)) {
var entry: PsiBuilder.Marker = mark()
advance()
if (at(THIS_KEYWORD)) {
var thisExpression: PsiBuilder.Marker = mark()
var reference: PsiBuilder.Marker = mark()
advance()
reference.done(REFERENCE_EXPRESSION)
thisExpression.done(THIS_EXPRESSION)
}
else {
var keyword: KtToken? = KEYWORD_TEXTS.get(myBuilder.tokenText)
if (keyword != null) {
myBuilder.remapCurrentToken(keyword)
errorAndAdvance("Keyword cannot be used as a reference")
}
else {
var reference: PsiBuilder.Marker = mark()
expect(IDENTIFIER,"Expecting a name")
reference.done(REFERENCE_EXPRESSION)
}
}
entry.done(SHORT_STRING_TEMPLATE_ENTRY)
}
else if (at(LONG_TEMPLATE_ENTRY_START)) {
var longTemplateEntry: PsiBuilder.Marker = mark()
advance()
while (! eof()) {
var offset: Int = myBuilder.currentOffset
parseExpression()
if (_at(LONG_TEMPLATE_ENTRY_END)) {
advance()
break
}
else {
error("Expecting '}'")
if (offset == myBuilder.currentOffset) {
advance()
}
}
}
longTemplateEntry.done(LONG_STRING_TEMPLATE_ENTRY)
}
else {
errorAndAdvance("Unexpected token in a string template")
}
}

private fun parseWhen(): Unit {

assert(_at(WHEN_KEYWORD))
var `when`: PsiBuilder.Marker = mark()
advance()
myBuilder.disableNewlines()
if (at(LPAR)) {
advanceAt(LPAR)
var atWhenStart: PsiBuilder.Marker = mark()
myKotlinParsing.parseAnnotationsList(EQ_RPAR_SET)
if (at(VAL_KEYWORD) || at(VAR_KEYWORD)) {
var declType: IElementType? = myKotlinParsing.parseProperty(KotlinParsing.DeclarationParsingMode.LOCAL)
atWhenStart.done(declType!!)
atWhenStart.setCustomEdgeTokenBinders(PrecedingDocCommentsBinder,TrailingCommentsBinder)
}
else {
atWhenStart.rollbackTo()
parseExpression()
}
expect(RPAR,"Expecting ')'")
}
myBuilder.restoreNewlinesState()
myBuilder.enableNewlines()
if (expect(LBRACE,"Expecting '{'")) {
while (! eof() && ! at(RBRACE)) {
parseWhenEntry()
}
expect(RBRACE,"Expecting '}'")
}
myBuilder.restoreNewlinesState()
`when`.done(WHEN)
}

private fun parseWhenEntry(): Unit {

var entry: PsiBuilder.Marker = mark()
if (at(ELSE_KEYWORD)) {
advance()
parseWhenEntryGuardOrSuggest()
if (! at(ARROW)) {
errorUntil("Expecting '->'",TokenSet.create(ARROW,LBRACE,RBRACE,EOL_OR_SEMICOLON))
}
if (at(ARROW)) {
advance()
if (atSet(WHEN_CONDITION_RECOVERY_SET)) {
error("Expecting an element")
}
else {
parseControlStructureBody()
}
}
else if (at(LBRACE)) {
parseControlStructureBody()
}
else if (! atSet(WHEN_CONDITION_RECOVERY_SET)) {
errorAndAdvance("Expecting '->'")
}
}
else {
parseWhenEntryNotElse()
}
entry.done(WHEN_ENTRY)
consumeIf(SEMICOLON)
}

private fun parseWhenEntryNotElse(): Unit {

while (true) {
while (at(COMMA)) errorAndAdvance("Expecting a when-condition")
parseWhenCondition()
if (! at(COMMA)) break
advance()
if (at(ARROW)) {
break
}
}
parseWhenEntryGuardOrSuggest()
expect(ARROW,"Expecting '->'",WHEN_CONDITION_RECOVERY_SET)
if (atSet(WHEN_CONDITION_RECOVERY_SET)) {
error("Expecting an element")
}
else {
parseControlStructureBody()
}
}

private fun parseWhenCondition(): Unit {

var condition: PsiBuilder.Marker = mark()
myBuilder.disableNewlines()
switch3@ do {
when (getTokenId()) {
IN_KEYWORD_Id -> {
var mark: PsiBuilder.Marker = mark()
advance()
mark.done(OPERATION_REFERENCE)
if (atSet(WHEN_CONDITION_RECOVERY_SET_WITH_ARROW)) {
error("Expecting an element")
}
else {
parseExpression()
}
condition.done(WHEN_CONDITION_IN_RANGE)
break@switch3
}
NOT_IN_Id -> {
var mark: PsiBuilder.Marker = mark()
advance()
mark.done(OPERATION_REFERENCE)
if (atSet(WHEN_CONDITION_RECOVERY_SET_WITH_ARROW)) {
error("Expecting an element")
}
else {
parseExpression()
}
condition.done(WHEN_CONDITION_IN_RANGE)
break@switch3
}
IS_KEYWORD_Id -> {
advance()
if (atSet(WHEN_CONDITION_RECOVERY_SET_WITH_ARROW)) {
error("Expecting a type")
}
else {
myKotlinParsing.parseTypeRef()
}
condition.done(WHEN_CONDITION_IS_PATTERN)
break@switch3
}
NOT_IS_Id -> {
advance()
if (atSet(WHEN_CONDITION_RECOVERY_SET_WITH_ARROW)) {
error("Expecting a type")
}
else {
myKotlinParsing.parseTypeRef()
}
condition.done(WHEN_CONDITION_IS_PATTERN)
break@switch3
}
RBRACE_Id -> {
error("Expecting an expression, is-condition or in-condition")
condition.done(WHEN_CONDITION_EXPRESSION)
break@switch3
}
ELSE_KEYWORD_Id -> {
error("Expecting an expression, is-condition or in-condition")
condition.done(WHEN_CONDITION_EXPRESSION)
break@switch3
}
ARROW_Id -> {
error("Expecting an expression, is-condition or in-condition")
condition.done(WHEN_CONDITION_EXPRESSION)
break@switch3
}
DOT_Id -> {
error("Expecting an expression, is-condition or in-condition")
condition.done(WHEN_CONDITION_EXPRESSION)
break@switch3
}
else -> {
parseExpression()
condition.done(WHEN_CONDITION_EXPRESSION)
break@switch3
}
}
} while (false)
myBuilder.restoreNewlinesState()
}

private fun parseWhenEntryGuardOrSuggest(): Unit {

if (at(ANDAND)) {
errorUntil("Unexpected '&&', use 'if' to introduce additional conditions; see https://kotl.in/guards-in-when",TokenSet.create(LBRACE,RBRACE,ARROW))
}
else if (at(IF_KEYWORD)) {
parseWhenEntryGuard()
}
}

private fun parseWhenEntryGuard(): Unit {

assert(_at(IF_KEYWORD))
var guard: PsiBuilder.Marker = mark()
advance()
parseExpression()
guard.done(WHEN_ENTRY_GUARD)
}

private fun parseArrayAccess(): Unit {

parseAsCollectionLiteralExpression(INDICES,false,"Expecting an index element")
}

private fun parseCollectionLiteralExpression(): Unit {

parseAsCollectionLiteralExpression(COLLECTION_LITERAL_EXPRESSION,true,"Expecting an element")
}

private fun parseAsCollectionLiteralExpression(nodeType: IElementType, canBeEmpty: Boolean, missingElementErrorMessage: String): Unit {

assert(_at(LBRACKET))
var innerExpressions: PsiBuilder.Marker = mark()
myBuilder.disableNewlines()
advance()
if (! canBeEmpty && at(RBRACKET)) {
error(missingElementErrorMessage)
}
else {
parseInnerExpressions(missingElementErrorMessage)
}
expect(RBRACKET,"Expecting ']'")
myBuilder.restoreNewlinesState()
innerExpressions.done(nodeType)
}

private fun parseInnerExpressions(missingElementErrorMessage: String): Unit {

while (true) {
if (at(RBRACKET)) {
break
}
parseExpression(missingElementErrorMessage)
if (! at(COMMA)) break
advance()
}
}

open fun parseContractDescriptionBlock(): Unit {

assert(_at(CONTRACT_KEYWORD))
advance()
parseContractEffectList()
}

private fun parseContractEffectList(): Unit {

var block: PsiBuilder.Marker = mark()
expect(LBRACKET,"Expecting '['")
myBuilder.enableNewlines()
parseContractEffects()
expect(RBRACKET,"Expecting ']'")
myBuilder.restoreNewlinesState()
block.done(CONTRACT_EFFECT_LIST)
}

private fun parseContractEffects(): Unit {

while (true) {
if (at(COMMA)) errorAndAdvance("Expecting a contract effect")
if (at(RBRACKET)) {
break
}
var effect: PsiBuilder.Marker = mark()
parseExpression()
effect.done(CONTRACT_EFFECT)
if (! at(COMMA)) break
advance()
}
}

open fun parseSimpleNameExpression(): Unit {

var simpleName: PsiBuilder.Marker = mark()
expect(IDENTIFIER,"Expecting an identifier")
simpleName.done(REFERENCE_EXPRESSION)
}

private fun parseLocalDeclaration(rollbackIfDefinitelyNotExpression: Boolean, isScriptTopLevel: Boolean): Boolean {

var decl: PsiBuilder.Marker = mark()
var detector: KotlinParsing.ModifierDetector = KotlinParsing.ModifierDetector()
myKotlinParsing.parseModifierList(detector,TokenSet.EMPTY,true)
var declType: IElementType? = parseLocalDeclarationRest(detector,rollbackIfDefinitelyNotExpression,isScriptTopLevel)
if (declType != null) {
closeDeclarationWithCommentBinders(decl,declType,declType != KtNodeTypes.PROPERTY && declType != KtNodeTypes.DESTRUCTURING_DECLARATION)
return true
}
else {
decl.rollbackTo()
return false
}
}

private fun parseFunctionLiteral(): Unit {

parseFunctionLiteral(false,true)
}

open fun parseFunctionLiteral(preferBlock: Boolean, collapse: Boolean): Unit {

assert(_at(LBRACE))
var literalExpression: PsiBuilder.Marker = mark()
var literal: PsiBuilder.Marker = mark()
myBuilder.enableNewlines()
advance()
var paramsFound: Boolean = false
var token: IElementType? = tt()
if (token == ARROW) {
mark().done(VALUE_PARAMETER_LIST)
advance()
paramsFound = true
}
else if (token == IDENTIFIER || token == COLON || token == LPAR || token == LBRACKET) {
var rollbackMarker: PsiBuilder.Marker = mark()
var nextToken: IElementType? = lookahead(1)
var preferParamsToExpressions: Boolean = (nextToken == COMMA || nextToken == COLON)
parseFunctionLiteralParameterList()
paramsFound = (if (preferParamsToExpressions) rollbackOrDrop(rollbackMarker,ARROW,"An -> is expected",RBRACE) else rollbackOrDropAt(rollbackMarker,ARROW))
}
if (! paramsFound && preferBlock) {
literal.drop()
parseStatements()
expect(RBRACE,"Expecting '}'")
literalExpression.done(BLOCK)
myBuilder.restoreNewlinesState()
return 
}
if (collapse && isLazy) {
myKotlinParsing.advanceBalancedBlock()
literal.done(FUNCTION_LITERAL)
literalExpression.collapse(LAMBDA_EXPRESSION)
}
else {
var body: PsiBuilder.Marker = mark()
parseStatements()
body.done(BLOCK)
body.setCustomEdgeTokenBinders(PRECEDING_ALL_COMMENTS_BINDER,TRAILING_ALL_COMMENTS_BINDER)
expect(RBRACE,"Expecting '}'")
literal.done(FUNCTION_LITERAL)
literalExpression.done(LAMBDA_EXPRESSION)
}
myBuilder.restoreNewlinesState()
}

private fun rollbackOrDropAt(rollbackMarker: PsiBuilder.Marker, dropAt: IElementType): Boolean {

if (at(dropAt)) {
advance()
rollbackMarker.drop()
return true
}
rollbackMarker.rollbackTo()
return false
}

private fun rollbackOrDrop(rollbackMarker: PsiBuilder.Marker, expected: KtToken, expectMessage: String, validForDrop: IElementType): Boolean {

if (at(expected)) {
advance()
rollbackMarker.drop()
return true
}
else if (at(validForDrop)) {
rollbackMarker.drop()
expect(expected,expectMessage)
return true
}
rollbackMarker.rollbackTo()
return false
}

private fun parseFunctionLiteralParameterList(): Unit {

var parameterList: PsiBuilder.Marker = mark()
while (! eof()) {
if (at(ARROW)) {
break
}
var parameter: PsiBuilder.Marker = mark()
if (at(COLON)) {
error("Expecting parameter name")
}
else if (at(LPAR) || at(LBRACKET)) {
var destructuringDeclaration: PsiBuilder.Marker = mark()
myKotlinParsing.parseMultiDeclarationEntry(TOKEN_SET_TO_FOLLOW_AFTER_DESTRUCTURING_DECLARATION_IN_LAMBDA,TOKEN_SET_TO_FOLLOW_AFTER_DESTRUCTURING_DECLARATION_IN_LAMBDA_RECOVERY,(if(lookahead(1) == VAL_KEYWORD) MultiDeclarationMode.FULL_VAL_ONLY else MultiDeclarationMode.SHORT))
destructuringDeclaration.done(DESTRUCTURING_DECLARATION)
}
else {
expect(IDENTIFIER,"Expecting parameter name",ARROW_SET)
}
if (at(COLON)) {
advance()
myKotlinParsing.parseTypeRef(ARROW_COMMA_SET)
}
parameter.done(VALUE_PARAMETER)
if (at(ARROW)) {
break
}
else if (at(COMMA)) {
advance()
}
else {
error("Expecting '->' or ','")
break
}
}
parameterList.done(VALUE_PARAMETER_LIST)
}

open fun parseStatements(): Unit {

parseStatements(false)
}

open fun parseStatements(isScriptTopLevel: Boolean): Unit {

while (at(SEMICOLON)) advance()
while (! eof() && ! at(RBRACE)) {
if (! atSet(STATEMENT_FIRST)) {
errorAndAdvance("Expecting an element")
}
if (atSet(STATEMENT_FIRST)) {
parseStatement(isScriptTopLevel)
}
if (at(SEMICOLON)) {
while (at(SEMICOLON)) advance()
}
else if (at(RBRACE)) {
break
}
else if (! isScriptTopLevel && ! myBuilder.newlineBeforeCurrentToken()) {
var severalStatementsError: String = "Unexpected tokens (use ';' to separate expressions on the same line)"
if (atSet(STATEMENT_NEW_LINE_QUICK_RECOVERY_SET)) {
error(severalStatementsError)
}
else {
errorUntil(severalStatementsError,TokenSet.create(EOL_OR_SEMICOLON,LBRACE,RBRACE))
}
}
}
}

private fun parseStatement(isScriptTopLevel: Boolean): Unit {

if (! parseLocalDeclaration(false,isScriptTopLevel)) {
if (! atSet(EXPRESSION_FIRST)) {
errorAndAdvance("Expecting a statement")
}
else if (isScriptTopLevel) {
var scriptInitializer: PsiBuilder.Marker = mark()
parseBlockLevelExpression()
scriptInitializer.done(SCRIPT_INITIALIZER)
}
else {
parseBlockLevelExpression()
}
}
}

private fun parseBlockLevelExpression(): Unit {

if (at(AT)) {
var expression: PsiBuilder.Marker = mark()
myKotlinParsing.parseAnnotations(DEFAULT)
if (! myBuilder.newlineBeforeCurrentToken()) {
expression.rollbackTo()
parseExpression()
return 
}
parseBlockLevelExpression()
expression.done(ANNOTATED_EXPRESSION)
return 
}
parseExpression()
}

private fun parseLocalDeclarationRest(modifierDetector: KotlinParsing.ModifierDetector, failIfDefinitelyNotExpression: Boolean, isScriptTopLevel: Boolean): IElementType? {

var keywordToken: IElementType? = tt()
if (failIfDefinitelyNotExpression) {
if (keywordToken != FUN_KEYWORD) return null
return myKotlinParsing.parseFunction(true)
}
if (keywordToken == OBJECT_KEYWORD) {
var lookahead: IElementType? = lookahead(1)
if (lookahead == COLON || lookahead == LBRACE) {
return null
}
}
return myKotlinParsing.parseCommonDeclaration(modifierDetector,NameParsingMode.REQUIRED,(if(isScriptTopLevel) KotlinParsing.DeclarationParsingMode.SCRIPT_TOPLEVEL else KotlinParsing.DeclarationParsingMode.LOCAL))
}

private fun parseDoWhile(): Unit {

assert(_at(DO_KEYWORD))
var loop: PsiBuilder.Marker = mark()
advance()
if (! at(WHILE_KEYWORD)) {
parseLoopBody()
}
if (expect(WHILE_KEYWORD,"Expecting 'while' followed by a post-condition")) {
parseCondition()
}
loop.done(DO_WHILE)
}

private fun parseWhile(): Unit {

assert(_at(WHILE_KEYWORD))
var loop: PsiBuilder.Marker = mark()
advance()
parseCondition()
parseLoopBody()
loop.done(WHILE)
}

private fun parseFor(): Unit {

assert(_at(FOR_KEYWORD))
var loop: PsiBuilder.Marker = mark()
advance()
if (expect(LPAR,"Expecting '(' to open a loop range",EXPRESSION_FIRST)) {
myBuilder.disableNewlines()
if (! at(RPAR)) {
var parameter: PsiBuilder.Marker = mark()
if (! at(IN_KEYWORD)) {
myKotlinParsing.parseModifierList(IN_KEYWORD_R_PAR_COLON_SET)
}
if (at(VAL_KEYWORD) || at(VAR_KEYWORD)) advance()
if (at(LPAR) || at(LBRACKET)) {
var destructuringDeclaration: PsiBuilder.Marker = mark()
myKotlinParsing.parseMultiDeclarationEntry(IN_KEYWORD_L_BRACE_SET,IN_KEYWORD_L_BRACE_RECOVERY_SET,(if(lookahead(1) == VAL_KEYWORD) MultiDeclarationMode.FULL_VAL_ONLY else MultiDeclarationMode.SHORT))
destructuringDeclaration.done(DESTRUCTURING_DECLARATION)
}
else {
expect(IDENTIFIER,"Expecting a variable name",COLON_IN_KEYWORD_SET)
if (at(COLON)) {
advance()
myKotlinParsing.parseTypeRef(IN_KEYWORD_SET)
}
}
parameter.done(VALUE_PARAMETER)
if (expect(IN_KEYWORD,"Expecting 'in'",L_PAR_L_BRACE_R_PAR_SET)) {
var range: PsiBuilder.Marker = mark()
parseExpression()
range.done(LOOP_RANGE)
}
}
else {
error("Expecting a variable name")
}
expectNoAdvance(RPAR,"Expecting ')'")
myBuilder.restoreNewlinesState()
}
parseLoopBody()
loop.done(FOR)
}

private fun parseControlStructureBody(): Unit {

if (! parseAnnotatedLambda(true)) {
parseBlockLevelExpression()
}
}

private fun parseLoopBody(): Unit {

var body: PsiBuilder.Marker = mark()
if (! at(SEMICOLON)) {
parseControlStructureBody()
}
body.done(BODY)
}

private fun parseTry(): Unit {

assert(_at(TRY_KEYWORD))
var tryExpression: PsiBuilder.Marker = mark()
advance()
myKotlinParsing.parseBlock()
var catchOrFinally: Boolean = false
while (at(CATCH_KEYWORD)) {
catchOrFinally = true
var catchBlock: PsiBuilder.Marker = mark()
advance()
if (atSet(TRY_CATCH_RECOVERY_TOKEN_SET)) {
error("Expecting exception variable declaration")
}
else {
var parameters: PsiBuilder.Marker = mark()
expect(LPAR,"Expecting '('",TRY_CATCH_RECOVERY_TOKEN_SET)
if (! atSet(TRY_CATCH_RECOVERY_TOKEN_SET)) {
myKotlinParsing.parseValueParameter(true)
if (at(COMMA)) {
advance()
}
expect(RPAR,"Expecting ')'",TRY_CATCH_RECOVERY_TOKEN_SET)
}
else {
error("Expecting exception variable declaration")
}
parameters.done(VALUE_PARAMETER_LIST)
}
if (at(LBRACE)) {
myKotlinParsing.parseBlock()
}
else {
error("Expecting a block: { ... }")
}
catchBlock.done(CATCH)
}
if (at(FINALLY_KEYWORD)) {
catchOrFinally = true
var finallyBlock: PsiBuilder.Marker = mark()
advance()
myKotlinParsing.parseBlock()
finallyBlock.done(FINALLY)
}
if (! catchOrFinally) {
error("Expecting 'catch' or 'finally'")
}
tryExpression.done(TRY)
}

private fun parseIf(): Unit {

assert(_at(IF_KEYWORD))
var marker: PsiBuilder.Marker = mark()
advance()
parseCondition()
var thenBranch: PsiBuilder.Marker = mark()
if (! at(ELSE_KEYWORD) && ! at(SEMICOLON)) {
parseControlStructureBody()
}
if (at(SEMICOLON) && lookahead(1) == ELSE_KEYWORD) {
advance()
}
thenBranch.done(THEN)
if (at(ELSE_KEYWORD) && lookahead(1) != ARROW) {
advance()
var elseBranch: PsiBuilder.Marker = mark()
if (! at(SEMICOLON)) {
parseControlStructureBody()
}
elseBranch.done(ELSE)
}
marker.done(IF)
}

private fun parseCondition(): Unit {

myBuilder.disableNewlines()
if (expect(LPAR,"Expecting a condition in parentheses '(...)'",EXPRESSION_FIRST)) {
var condition: PsiBuilder.Marker = mark()
parseExpression()
condition.done(CONDITION)
expect(RPAR,"Expecting ')")
}
myBuilder.restoreNewlinesState()
}

private fun parseJump(type: IElementType): Unit {

assert(_at(BREAK_KEYWORD) || _at(CONTINUE_KEYWORD))
var marker: PsiBuilder.Marker = mark()
advance()
parseLabelReferenceWithNoWhitespace()
marker.done(type)
}

private fun parseReturn(): Unit {

assert(_at(RETURN_KEYWORD))
var returnExpression: PsiBuilder.Marker = mark()
advance()
parseLabelReferenceWithNoWhitespace()
if (atSet(EXPRESSION_FIRST) && ! at(EOL_OR_SEMICOLON)) parseExpression()
returnExpression.done(RETURN)
}

private fun parseLabelReferenceWithNoWhitespace(): Unit {

if (at(AT) && ! myBuilder.newlineBeforeCurrentToken()) {
if (WHITE_SPACE_OR_COMMENT_BIT_SET.contains(myBuilder.rawLookup(- 1))) {
error("There should be no space or comments before '@' in label reference")
}
parseLabelReference()
}
}

open fun parseLabelDefinition(): Unit {

assert(isAtLabelDefinitionOrMissingIdentifier()) { "Callers must check that current token is IDENTIFIER followed with '@'" }
var labelWrap: PsiBuilder.Marker = mark()
var mark: PsiBuilder.Marker = mark()
if (at(AT)) {
errorAndAdvance("Expecting identifier before '@' in label definition")
labelWrap.drop()
mark.drop()
return 
}
advance()
advance()
mark.done(LABEL)
labelWrap.done(LABEL_QUALIFIER)
}

private fun parseLabelReference(): Unit {

assert(_at(AT))
var labelWrap: PsiBuilder.Marker = mark()
var mark: PsiBuilder.Marker = mark()
if (myBuilder.rawLookup(1) != IDENTIFIER) {
errorAndAdvance("Label must be named")
labelWrap.drop()
mark.drop()
return 
}
advance()
advance()
mark.done(LABEL)
labelWrap.done(LABEL_QUALIFIER)
}

private fun parseThrow(): Unit {

assert(_at(THROW_KEYWORD))
var marker: PsiBuilder.Marker = mark()
advance()
parseExpression()
marker.done(THROW)
}

private fun parseParenthesizedExpression(): Unit {

assert(_at(LPAR))
var mark: PsiBuilder.Marker = mark()
myBuilder.disableNewlines()
advance()
if (at(RPAR)) {
error("Expecting an expression")
}
else {
parseExpression()
}
expect(RPAR,"Expecting ')'")
myBuilder.restoreNewlinesState()
mark.done(PARENTHESIZED)
}

private fun parseThisExpression(): Unit {

assert(_at(THIS_KEYWORD))
var mark: PsiBuilder.Marker = mark()
var thisReference: PsiBuilder.Marker = mark()
advance()
thisReference.done(REFERENCE_EXPRESSION)
parseLabelReferenceWithNoWhitespace()
mark.done(THIS_EXPRESSION)
}

private fun parseSuperExpression(): Unit {

assert(_at(SUPER_KEYWORD))
var mark: PsiBuilder.Marker = mark()
var superReference: PsiBuilder.Marker = mark()
advance()
superReference.done(REFERENCE_EXPRESSION)
if (at(LT)) {
var supertype: PsiBuilder.Marker = mark()
myBuilder.disableNewlines()
advance()
myKotlinParsing.parseTypeRef()
if (at(GT)) {
advance()
supertype.drop()
}
else {
supertype.rollbackTo()
}
myBuilder.restoreNewlinesState()
}
parseLabelReferenceWithNoWhitespace()
mark.done(SUPER_EXPRESSION)
}

open fun parseValueArgumentList(): Unit {

var list: PsiBuilder.Marker = mark()
myBuilder.disableNewlines()
if (expect(LPAR,"Expecting an argument list",EXPRESSION_FOLLOW)) {
if (! at(RPAR)) {
while (true) {
parseValueArgument()
if (at(COLON) && lookahead(1) == IDENTIFIER) {
errorAndAdvance("Unexpected type specification",2)
}
if (! at(COMMA)) {
if (atSet(EXPRESSION_FIRST)) {
error("Expecting ','")
continue
}
else {
break
}
}
advance()
if (at(RPAR)) {
break
}
}
}
expect(RPAR,"Expecting ')'",EXPRESSION_FOLLOW)
}
myBuilder.restoreNewlinesState()
list.done(VALUE_ARGUMENT_LIST)
}

private fun parseValueArgument(): Unit {

var argument: PsiBuilder.Marker = mark()
if (at(IDENTIFIER) && lookahead(1) == EQ) {
var argName: PsiBuilder.Marker = mark()
var reference: PsiBuilder.Marker = mark()
advance()
reference.done(REFERENCE_EXPRESSION)
argName.done(VALUE_ARGUMENT_NAME)
advance()
}
if (at(MUL)) {
advance()
}
parseExpression("Expecting an argument")
argument.done(VALUE_ARGUMENT)
}

open fun parseObjectLiteral(): Unit {

var literal: PsiBuilder.Marker = mark()
var declaration: PsiBuilder.Marker = mark()
myKotlinParsing.parseObject(NameParsingMode.PROHIBITED,false)
declaration.done(OBJECT_DECLARATION)
literal.done(OBJECT_LITERAL)
}

private fun parseOneTokenExpression(type: IElementType): Unit {

var mark: PsiBuilder.Marker = mark()
advance()
mark.done(type)
}

protected override fun create(builder: SemanticWhitespaceAwarePsiBuilder): KotlinParsing {

return myKotlinParsing.createForSibling(builder)
}

private fun interruptedWithNewLine(): Boolean {

return ! ALLOW_NEWLINE_OPERATIONS.contains(tt()) && myBuilder.newlineBeforeCurrentToken()
}

companion object {
private val WHEN_CONDITION_RECOVERY_SET: TokenSet = TokenSet.create(RBRACE,IN_KEYWORD,NOT_IN,IS_KEYWORD,NOT_IS,ELSE_KEYWORD)

private val WHEN_CONDITION_RECOVERY_SET_WITH_ARROW: TokenSet = TokenSet.create(RBRACE,IN_KEYWORD,NOT_IN,IS_KEYWORD,NOT_IS,ELSE_KEYWORD,ARROW,DOT)

private val KEYWORD_TEXTS: Map<String,KtToken> = tokenSetToMap(KEYWORDS)

private val TOKEN_SET_TO_FOLLOW_AFTER_DESTRUCTURING_DECLARATION_IN_LAMBDA: TokenSet = TokenSet.create(ARROW,COMMA,COLON)

private val TOKEN_SET_TO_FOLLOW_AFTER_DESTRUCTURING_DECLARATION_IN_LAMBDA_RECOVERY: TokenSet = TokenSet.orSet(TOKEN_SET_TO_FOLLOW_AFTER_DESTRUCTURING_DECLARATION_IN_LAMBDA,PARAMETER_NAME_RECOVERY_SET)

private val EQ_RPAR_SET: TokenSet = TokenSet.create(EQ,RPAR)

private val ARROW_SET: TokenSet = TokenSet.create(ARROW)

private val ARROW_COMMA_SET: TokenSet = TokenSet.create(ARROW,COMMA)

private val IN_KEYWORD_R_PAR_COLON_SET: TokenSet = TokenSet.create(IN_KEYWORD,RPAR,COLON)

private val IN_KEYWORD_L_BRACE_SET: TokenSet = TokenSet.create(IN_KEYWORD,LBRACE)

private val IN_KEYWORD_L_BRACE_RECOVERY_SET: TokenSet = TokenSet.orSet(IN_KEYWORD_L_BRACE_SET,PARAMETER_NAME_RECOVERY_SET)

private val COLON_IN_KEYWORD_SET: TokenSet = TokenSet.create(COLON,IN_KEYWORD)

private val L_PAR_L_BRACE_R_PAR_SET: TokenSet = TokenSet.create(LPAR,LBRACE,RPAR)

private val IN_KEYWORD_SET: TokenSet = TokenSet.create(IN_KEYWORD)

private val TRY_CATCH_RECOVERY_TOKEN_SET: TokenSet = TokenSet.create(LBRACE,RBRACE,FINALLY_KEYWORD,CATCH_KEYWORD)

private fun tokenSetToMap(tokens: TokenSet): Map<String,KtToken> {
    val result = linkedMapOf<String, KtToken>()
    for (token in tokens.types) {
        val key = token.toString()
        require(!result.containsKey(key)) { "Multiple entries with same key: $key" }
        result[key] = token as KtToken
    }
    return result.toMap()
}

private val TYPE_ARGUMENT_LIST_STOPPERS: TokenSet = TokenSet.create(INTEGER_LITERAL,FLOAT_LITERAL,CHARACTER_LITERAL,INTERPOLATION_PREFIX,OPEN_QUOTE,PACKAGE_KEYWORD,AS_KEYWORD,TYPE_ALIAS_KEYWORD,INTERFACE_KEYWORD,CLASS_KEYWORD,THIS_KEYWORD,VAL_KEYWORD,VAR_KEYWORD,FUN_KEYWORD,FOR_KEYWORD,NULL_KEYWORD,TRUE_KEYWORD,FALSE_KEYWORD,IS_KEYWORD,THROW_KEYWORD,RETURN_KEYWORD,BREAK_KEYWORD,CONTINUE_KEYWORD,OBJECT_KEYWORD,IF_KEYWORD,TRY_KEYWORD,ELSE_KEYWORD,WHILE_KEYWORD,DO_KEYWORD,WHEN_KEYWORD,RBRACKET,RBRACE,RPAR,PLUSPLUS,MINUSMINUS,EXCLEXCL,PLUS,MINUS,EXCL,DIV,PERC,LTEQ,EQEQEQ,EXCLEQEQEQ,EQEQ,EXCLEQ,ANDAND,OROR,SAFE_ACCESS,ELVIS,SEMICOLON,RANGE,RANGE_UNTIL,EQ,MULTEQ,DIVEQ,PERCEQ,PLUSEQ,MINUSEQ,NOT_IN,NOT_IS,COLONCOLON,COLON)

val STATEMENT_FIRST: TokenSet = TokenSet.orSet(EXPRESSION_FIRST,TokenSet.create(FUN_KEYWORD,VAL_KEYWORD,VAR_KEYWORD,INTERFACE_KEYWORD,CLASS_KEYWORD,TYPE_ALIAS_KEYWORD),MODIFIER_KEYWORDS)

private val STATEMENT_NEW_LINE_QUICK_RECOVERY_SET: TokenSet = TokenSet.orSet(TokenSet.andSet(STATEMENT_FIRST,TokenSet.andNot(KEYWORDS,TokenSet.create(IN_KEYWORD))),TokenSet.create(EOL_OR_SEMICOLON))

private val MIN_BINARY_OPERATION_PRECEDENCE: BinaryOperationPrecedence = BinaryOperationPrecedence.entries.get(0)

private val MAX_BINARY_OPERATION_PRECEDENCE: BinaryOperationPrecedence = BinaryOperationPrecedence.entries.last()

private val ALLOW_NEWLINE_OPERATIONS: TokenSet = TokenSet.create(DOT,SAFE_ACCESS,COLON,AS_KEYWORD,AS_SAFE,ELVIS,ANDAND,OROR)

private fun doneOrDrop(marker: PsiBuilder.Marker, type: IElementType, condition: Boolean): Unit {

if (condition) {
marker.done(type)
}
else {
marker.drop()
}
}
}
}
