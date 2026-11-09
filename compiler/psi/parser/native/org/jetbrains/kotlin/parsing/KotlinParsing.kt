/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.parsing







import com.intellij.lang.PsiBuilder
import com.intellij.lang.WhitespacesBinders
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.jetbrains.kotlin.lexer.KtKeywordToken
import org.jetbrains.kotlin.lexer.KtSingleValueToken
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.KtNodeTypes.Companion.ANNOTATION
import org.jetbrains.kotlin.KtNodeTypes.Companion.ANNOTATION_ENTRY
import org.jetbrains.kotlin.KtNodeTypes.Companion.ANNOTATION_TARGET
import org.jetbrains.kotlin.KtNodeTypes.Companion.BACKING_FIELD
import org.jetbrains.kotlin.KtNodeTypes.Companion.BLOCK
import org.jetbrains.kotlin.KtNodeTypes.Companion.BLOCK_CODE_FRAGMENT
import org.jetbrains.kotlin.KtNodeTypes.Companion.CLASS
import org.jetbrains.kotlin.KtNodeTypes.Companion.CLASS_BODY
import org.jetbrains.kotlin.KtNodeTypes.Companion.CLASS_INITIALIZER
import org.jetbrains.kotlin.KtNodeTypes.Companion.COMPANION_BLOCK
import org.jetbrains.kotlin.KtNodeTypes.Companion.CONSTRUCTOR_CALLEE
import org.jetbrains.kotlin.KtNodeTypes.Companion.CONSTRUCTOR_DELEGATION_CALL
import org.jetbrains.kotlin.KtNodeTypes.Companion.CONSTRUCTOR_DELEGATION_REFERENCE
import org.jetbrains.kotlin.KtNodeTypes.Companion.CONTEXT_PARAMETER_LIST
import org.jetbrains.kotlin.KtNodeTypes.Companion.CONTEXT_RECEIVER
import org.jetbrains.kotlin.KtNodeTypes.Companion.DELEGATED_SUPER_TYPE_ENTRY
import org.jetbrains.kotlin.KtNodeTypes.Companion.DESTRUCTURING_DECLARATION
import org.jetbrains.kotlin.KtNodeTypes.Companion.DESTRUCTURING_DECLARATION_ENTRY
import org.jetbrains.kotlin.KtNodeTypes.Companion.DOT_QUALIFIED_EXPRESSION
import org.jetbrains.kotlin.KtNodeTypes.Companion.DYNAMIC_TYPE
import org.jetbrains.kotlin.KtNodeTypes.Companion.ENUM_ENTRY
import org.jetbrains.kotlin.KtNodeTypes.Companion.ENUM_ENTRY_SUPERCLASS_REFERENCE_EXPRESSION
import org.jetbrains.kotlin.KtNodeTypes.Companion.EXPRESSION_CODE_FRAGMENT
import org.jetbrains.kotlin.KtNodeTypes.Companion.FILE_ANNOTATION_LIST
import org.jetbrains.kotlin.KtNodeTypes.Companion.FUN
import org.jetbrains.kotlin.KtNodeTypes.Companion.FUNCTION_TYPE
import org.jetbrains.kotlin.KtNodeTypes.Companion.FUNCTION_TYPE_RECEIVER
import org.jetbrains.kotlin.KtNodeTypes.Companion.IMPORT_ALIAS
import org.jetbrains.kotlin.KtNodeTypes.Companion.IMPORT_DIRECTIVE
import org.jetbrains.kotlin.KtNodeTypes.Companion.IMPORT_LIST
import org.jetbrains.kotlin.KtNodeTypes.Companion.INITIALIZER_LIST
import org.jetbrains.kotlin.KtNodeTypes.Companion.INTERSECTION_TYPE
import org.jetbrains.kotlin.KtNodeTypes.Companion.KT_FILE
import org.jetbrains.kotlin.KtNodeTypes.Companion.MODIFIER_LIST
import org.jetbrains.kotlin.KtNodeTypes.Companion.NULLABLE_TYPE
import org.jetbrains.kotlin.KtNodeTypes.Companion.OBJECT_DECLARATION
import org.jetbrains.kotlin.KtNodeTypes.Companion.PACKAGE_DIRECTIVE
import org.jetbrains.kotlin.KtNodeTypes.Companion.PRIMARY_CONSTRUCTOR
import org.jetbrains.kotlin.KtNodeTypes.Companion.PROPERTY
import org.jetbrains.kotlin.KtNodeTypes.Companion.PROPERTY_ACCESSOR
import org.jetbrains.kotlin.KtNodeTypes.Companion.PROPERTY_DELEGATE
import org.jetbrains.kotlin.KtNodeTypes.Companion.REFERENCE_EXPRESSION
import org.jetbrains.kotlin.KtNodeTypes.Companion.SCRIPT
import org.jetbrains.kotlin.KtNodeTypes.Companion.SECONDARY_CONSTRUCTOR
import org.jetbrains.kotlin.KtNodeTypes.Companion.SUPER_TYPE_CALL_ENTRY
import org.jetbrains.kotlin.KtNodeTypes.Companion.SUPER_TYPE_ENTRY
import org.jetbrains.kotlin.KtNodeTypes.Companion.SUPER_TYPE_LIST
import org.jetbrains.kotlin.KtNodeTypes.Companion.TYPEALIAS
import org.jetbrains.kotlin.KtNodeTypes.Companion.TYPE_ARGUMENT_LIST
import org.jetbrains.kotlin.KtNodeTypes.Companion.TYPE_CODE_FRAGMENT
import org.jetbrains.kotlin.KtNodeTypes.Companion.TYPE_CONSTRAINT
import org.jetbrains.kotlin.KtNodeTypes.Companion.TYPE_CONSTRAINT_LIST
import org.jetbrains.kotlin.KtNodeTypes.Companion.TYPE_PARAMETER
import org.jetbrains.kotlin.KtNodeTypes.Companion.TYPE_PARAMETER_LIST
import org.jetbrains.kotlin.KtNodeTypes.Companion.TYPE_PROJECTION
import org.jetbrains.kotlin.KtNodeTypes.Companion.TYPE_REFERENCE
import org.jetbrains.kotlin.KtNodeTypes.Companion.USER_TYPE
import org.jetbrains.kotlin.KtNodeTypes.Companion.VALUE_PARAMETER
import org.jetbrains.kotlin.KtNodeTypes.Companion.VALUE_PARAMETER_LIST
import org.jetbrains.kotlin.lexer.KtTokens.Companion.ALL_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.AND
import org.jetbrains.kotlin.lexer.KtTokens.Companion.ARROW
import org.jetbrains.kotlin.lexer.KtTokens.Companion.AS_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.AT
import org.jetbrains.kotlin.lexer.KtTokens.Companion.BLOCK_COMMENT
import org.jetbrains.kotlin.lexer.KtTokens.Companion.BREAK_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.BY_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.CHARACTER_LITERAL
import org.jetbrains.kotlin.lexer.KtTokens.Companion.CLASS_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.CLASS_KEYWORD_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.COLON
import org.jetbrains.kotlin.lexer.KtTokens.Companion.COLONCOLON
import org.jetbrains.kotlin.lexer.KtTokens.Companion.COMMA
import org.jetbrains.kotlin.lexer.KtTokens.Companion.COMPANION_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.CONSTRUCTOR_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.CONTEXT_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.CONTINUE_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.CONTRACT_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.DELEGATE_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.DOC_COMMENT
import org.jetbrains.kotlin.lexer.KtTokens.Companion.DOT
import org.jetbrains.kotlin.lexer.KtTokens.Companion.DO_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.DYNAMIC_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.ENUM_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.EOL_OR_SEMICOLON
import org.jetbrains.kotlin.lexer.KtTokens.Companion.EQ
import org.jetbrains.kotlin.lexer.KtTokens.Companion.EXCL
import org.jetbrains.kotlin.lexer.KtTokens.Companion.EXCLEXCL
import org.jetbrains.kotlin.lexer.KtTokens.Companion.FALSE_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.FIELD_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.FILE_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.FLOAT_LITERAL
import org.jetbrains.kotlin.lexer.KtTokens.Companion.FOR_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.FUN_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.FUN_KEYWORD_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.GET_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.GT
import org.jetbrains.kotlin.lexer.KtTokens.Companion.HASH
import org.jetbrains.kotlin.lexer.KtTokens.Companion.IDENTIFIER
import org.jetbrains.kotlin.lexer.KtTokens.Companion.IDENTIFIER_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.IF_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.IMPORT_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.INIT_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.INTEGER_LITERAL
import org.jetbrains.kotlin.lexer.KtTokens.Companion.INTERFACE_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.INTERFACE_KEYWORD_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.INTERPOLATION_PREFIX
import org.jetbrains.kotlin.lexer.KtTokens.Companion.LBRACE
import org.jetbrains.kotlin.lexer.KtTokens.Companion.LBRACKET
import org.jetbrains.kotlin.lexer.KtTokens.Companion.LBRACKET_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.LPAR
import org.jetbrains.kotlin.lexer.KtTokens.Companion.LPAR_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.LT
import org.jetbrains.kotlin.lexer.KtTokens.Companion.MINUS
import org.jetbrains.kotlin.lexer.KtTokens.Companion.MINUSMINUS
import org.jetbrains.kotlin.lexer.KtTokens.Companion.MODIFIER_KEYWORDS
import org.jetbrains.kotlin.lexer.KtTokens.Companion.MUL
import org.jetbrains.kotlin.lexer.KtTokens.Companion.NULL_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.OBJECT_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.OBJECT_KEYWORD_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.OPEN_QUOTE
import org.jetbrains.kotlin.lexer.KtTokens.Companion.PACKAGE_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.PARAM_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.PLUS
import org.jetbrains.kotlin.lexer.KtTokens.Companion.PLUSPLUS
import org.jetbrains.kotlin.lexer.KtTokens.Companion.PROPERTY_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.QUEST
import org.jetbrains.kotlin.lexer.KtTokens.Companion.RBRACE
import org.jetbrains.kotlin.lexer.KtTokens.Companion.RBRACKET
import org.jetbrains.kotlin.lexer.KtTokens.Companion.RECEIVER_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.RESERVED_VALUE_PARAMETER_MODIFIER_KEYWORDS
import org.jetbrains.kotlin.lexer.KtTokens.Companion.RETURN_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.RPAR
import org.jetbrains.kotlin.lexer.KtTokens.Companion.SAFE_ACCESS
import org.jetbrains.kotlin.lexer.KtTokens.Companion.SEMICOLON
import org.jetbrains.kotlin.lexer.KtTokens.Companion.SETPARAM_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.SET_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.SUPER_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.THIS_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.THROW_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.TRUE_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.TRY_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.TYPE_ALIAS_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.TYPE_ALIAS_KEYWORD_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.TYPE_ARGUMENT_MODIFIER_KEYWORDS
import org.jetbrains.kotlin.lexer.KtTokens.Companion.TYPE_MODIFIER_KEYWORDS
import org.jetbrains.kotlin.lexer.KtTokens.Companion.VAL_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.VAL_KEYWORD_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.VAL_VAR
import org.jetbrains.kotlin.lexer.KtTokens.Companion.VAR_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.VAR_KEYWORD_Id
import org.jetbrains.kotlin.lexer.KtTokens.Companion.WHEN_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.WHERE_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.WHILE_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.Companion.WHITE_SPACE_OR_COMMENT_BIT_SET
import org.jetbrains.kotlin.parsing.KotlinParsing.AnnotationParsingMode.*
import org.jetbrains.kotlin.parsing.PRECEDING_ALL_BINDER
import org.jetbrains.kotlin.parsing.TRAILING_ALL_BINDER



open class KotlinParsing : AbstractKotlinParsing {
private val myExpressionParsing: KotlinExpressionParsing

private val lastDotAfterReceiverLParPattern: FirstBefore = FirstBefore(AtSet(RECEIVER_TYPE_TERMINATORS),object : AbstractTokenStreamPredicate() {
override fun matching(topLevel: Boolean): Boolean {

if(topLevel && atSet(definitelyOutOfReceiverSet)) {
return true
}
return topLevel && ! at(QUEST) && ! at(LPAR) && ! at(RPAR)
}
})

private val lastDotAfterReceiverNotLParPattern: LastBefore = LastBefore(AtSet(RECEIVER_TYPE_TERMINATORS),object : AbstractTokenStreamPredicate() {
override fun matching(topLevel: Boolean): Boolean {

if(topLevel &&(atSet(definitelyOutOfReceiverSet) || at(LPAR))) return true
if(topLevel && at(IDENTIFIER)) {
var lookahead: IElementType? = lookahead(1)
return lookahead != LT && lookahead != DOT && lookahead != SAFE_ACCESS && lookahead != QUEST
}
return false
}
})

private constructor(builder: SemanticWhitespaceAwarePsiBuilder, isTopLevel: Boolean, isLazy: Boolean) : super(builder,isLazy) {

myExpressionParsing = (if (isTopLevel) KotlinExpressionParsing(builder,this,isLazy) else object : KotlinExpressionParsing(builder,this@KotlinParsing,isLazy) {
protected override fun parseCallWithClosure(): Boolean {

if(((builder as SemanticWhitespaceAwarePsiBuilderForByClause)).getStackSize() > 0) {
return super.parseCallWithClosure()
}
return false
}

protected override fun create(builder: SemanticWhitespaceAwarePsiBuilder): KotlinParsing {

return createForByClause(builder,isLazy)
}
})
}

open fun parseFile(): Unit {

var fileMarker: PsiBuilder.Marker = mark()
parsePreamble()
while (! eof()) {
parseTopLevelDeclaration()
}
checkUnclosedBlockComment()
fileMarker.done(KT_FILE)
}

private fun checkUnclosedBlockComment(): Unit {

if (BLOCK_DOC_COMMENT_SET.contains(myBuilder.rawLookup(- 1))) {
var startOffset: Int = myBuilder.rawTokenTypeStart(- 1)
var endOffset: Int = myBuilder.rawTokenTypeStart(0)
var tokenChars: CharSequence = myBuilder.originalText.subSequence(startOffset,endOffset)
if (!(tokenChars.length > 2 && tokenChars.subSequence(tokenChars.length - 2,tokenChars.length).toString().equals("*/"))) {
var marker: PsiBuilder.Marker = myBuilder.mark()
marker.error("Unclosed comment")
marker.setCustomEdgeTokenBinders(WhitespacesBinders.GREEDY_RIGHT_BINDER,null)
}
}
}

open fun parseTypeCodeFragment(): Unit {

var marker: PsiBuilder.Marker = mark()
parseTypeRef()
checkForUnexpectedSymbols()
marker.done(TYPE_CODE_FRAGMENT)
}

open fun parseExpressionCodeFragment(): Unit {

var marker: PsiBuilder.Marker = mark()
myExpressionParsing.parseExpression()
checkForUnexpectedSymbols()
marker.done(EXPRESSION_CODE_FRAGMENT)
}

open fun parseBlockCodeFragment(): Unit {

var marker: PsiBuilder.Marker = mark()
var blockMarker: PsiBuilder.Marker = mark()
if (at(PACKAGE_KEYWORD) || at(IMPORT_KEYWORD)) {
var err: PsiBuilder.Marker = mark()
parsePreamble()
err.error("Package directive and imports are forbidden in code fragments")
}
myExpressionParsing.parseStatements()
checkForUnexpectedSymbols()
blockMarker.done(BLOCK)
marker.done(BLOCK_CODE_FRAGMENT)
}

open fun parseLambdaExpression(): Unit {

myExpressionParsing.parseFunctionLiteral(false,false)
}

open fun parseBlockExpression(): Unit {

parseBlock(false)
}

open fun parseScript(): Unit {

var fileMarker: PsiBuilder.Marker = mark()
parsePreamble()
var scriptMarker: PsiBuilder.Marker = mark()
var blockMarker: PsiBuilder.Marker = mark()
myExpressionParsing.parseStatements(true)
checkForUnexpectedSymbols()
blockMarker.done(BLOCK)
blockMarker.setCustomEdgeTokenBinders(PRECEDING_ALL_BINDER,TRAILING_ALL_BINDER)
scriptMarker.done(SCRIPT)
scriptMarker.setCustomEdgeTokenBinders(PRECEDING_ALL_BINDER,TRAILING_ALL_BINDER)
fileMarker.done(KT_FILE)
}

private fun checkForUnexpectedSymbols(): Unit {

while (! eof()) {
errorAndAdvance("Unexpected symbol")
}
}

private fun parsePreamble(): Unit {

var firstEntry: PsiBuilder.Marker = mark()
parseFileAnnotationList(FILE_ANNOTATIONS_BEFORE_PACKAGE)
var packageDirective: PsiBuilder.Marker = mark()
parseModifierList(TokenSet.EMPTY)
if (at(PACKAGE_KEYWORD)) {
advance()
parsePackageName()
firstEntry.drop()
consumeIf(SEMICOLON)
packageDirective.done(PACKAGE_DIRECTIVE)
}
else {
firstEntry.rollbackTo()
parseFileAnnotationList(FILE_ANNOTATIONS_WHEN_PACKAGE_OMITTED)
packageDirective = mark()
packageDirective.done(PACKAGE_DIRECTIVE)
packageDirective.setCustomEdgeTokenBinders(BindFirstShebangWithWhitespaceOnly,null)
}
parseImportDirectives()
}

private fun parsePackageName(): Unit {

var qualifiedExpression: PsiBuilder.Marker = mark()
var simpleName: Boolean = true
while (true) {
if (myBuilder.newlineBeforeCurrentToken()) {
errorWithRecovery("Package name must be a '.'-separated identifier list placed on a single line",PACKAGE_NAME_RECOVERY_SET)
break
}
if (at(DOT)) {
advance()
qualifiedExpression.error("Package name must be a '.'-separated identifier list")
qualifiedExpression = mark()
continue
}
var nsName: PsiBuilder.Marker = mark()
var simpleNameFound: Boolean = expect(IDENTIFIER,"Package name must be a '.'-separated identifier list",PACKAGE_NAME_RECOVERY_SET)
if (simpleNameFound) {
nsName.done(REFERENCE_EXPRESSION)
}
else {
nsName.drop()
}
if (! simpleName) {
var precedingMarker: PsiBuilder.Marker = qualifiedExpression.precede()
qualifiedExpression.done(DOT_QUALIFIED_EXPRESSION)
qualifiedExpression = precedingMarker
}
if (at(DOT)) {
advance()
if (simpleName && ! simpleNameFound) {
qualifiedExpression.drop()
qualifiedExpression = mark()
}
else {
simpleName = false
}
}
else {
break
}
}
qualifiedExpression.drop()
}

private fun parseImportDirective(): Unit {

assert(_at(IMPORT_KEYWORD))
var importDirective: PsiBuilder.Marker = mark()
advance()
if (closeImportWithErrorIfNewline(importDirective,null,"Expecting qualified name")) {
return 
}
if (! at(IDENTIFIER)) {
var error: PsiBuilder.Marker = mark()
skipUntil(TokenSet.create(EOL_OR_SEMICOLON))
error.error("Expecting qualified name")
importDirective.done(IMPORT_DIRECTIVE)
consumeIf(SEMICOLON)
return 
}
var qualifiedName: PsiBuilder.Marker = mark()
var reference: PsiBuilder.Marker = mark()
advance()
reference.done(REFERENCE_EXPRESSION)
while (at(DOT) && lookahead(1) != MUL) {
advance()
if (closeImportWithErrorIfNewline(importDirective,null,"Import must be placed on a single line")) {
qualifiedName.drop()
return 
}
reference = mark()
if (expect(IDENTIFIER,"Qualified name must be a '.'-separated identifier list",IMPORT_RECOVERY_SET)) {
reference.done(REFERENCE_EXPRESSION)
}
else {
reference.drop()
}
var precede: PsiBuilder.Marker = qualifiedName.precede()
qualifiedName.done(DOT_QUALIFIED_EXPRESSION)
qualifiedName = precede
}
qualifiedName.drop()
if (at(DOT)) {
advance()
assert(_at(MUL))
advance()
if (at(AS_KEYWORD)) {
var `as`: PsiBuilder.Marker = mark()
advance()
if (closeImportWithErrorIfNewline(importDirective,null,"Expecting identifier")) {
`as`.drop()
return 
}
consumeIf(IDENTIFIER)
`as`.done(IMPORT_ALIAS)
`as`.precede().error("Cannot rename all imported items to one identifier")
}
}
if (at(AS_KEYWORD)) {
var alias: PsiBuilder.Marker = mark()
advance()
if (closeImportWithErrorIfNewline(importDirective,alias,"Expecting identifier")) {
return 
}
expect(IDENTIFIER,"Expecting identifier",SEMICOLON_SET)
alias.done(IMPORT_ALIAS)
}
consumeIf(SEMICOLON)
importDirective.done(IMPORT_DIRECTIVE)
importDirective.setCustomEdgeTokenBinders(null,TrailingCommentsBinder)
}

private fun closeImportWithErrorIfNewline(importDirective: PsiBuilder.Marker, importAlias: PsiBuilder.Marker?, errorMessage: String): Boolean {

if (myBuilder.newlineBeforeCurrentToken()) {
if (importAlias != null) {
importAlias.done(IMPORT_ALIAS)
}
error(errorMessage)
importDirective.done(IMPORT_DIRECTIVE)
return true
}
return false
}

private fun parseImportDirectives(): Unit {

var importList: PsiBuilder.Marker = mark()
if (! at(IMPORT_KEYWORD)) {
importList.setCustomEdgeTokenBinders(DoNotBindAnything,null)
}
while (at(IMPORT_KEYWORD)) {
parseImportDirective()
}
importList.done(IMPORT_LIST)
}

private fun parseTopLevelDeclaration(): Unit {

if (at(SEMICOLON)) {
advance()
return 
}
var decl: PsiBuilder.Marker = mark()
var detector: ModifierDetector = ModifierDetector()
parseModifierList(detector,TokenSet.EMPTY,false)
var declType: IElementType? = parseCommonDeclaration(detector,NameParsingMode.REQUIRED,DeclarationParsingMode.MEMBER_OR_TOPLEVEL)
if (declType == null && at(LBRACE)) {
error("Expecting a top level declaration")
parseBlock()
declType = FUN
}
if (declType == null && at(IMPORT_KEYWORD)) {
error("imports are only allowed in the beginning of file")
parseImportDirectives()
decl.drop()
}
else if (declType == null) {
errorAndAdvance("Expecting a top level declaration")
decl.drop()
}
else {
closeDeclarationWithCommentBinders(decl,declType,true)
}
}

open fun parseCommonDeclaration(detector: ModifierDetector, nameParsingModeForObject: NameParsingMode, declarationParsingMode: DeclarationParsingMode): IElementType? {

switch1@ do {
when (getTokenId()) {
CLASS_KEYWORD_Id -> {
return parseClass(detector.isEnumDetected(),true)
}
INTERFACE_KEYWORD_Id -> {
return parseClass(detector.isEnumDetected(),true)
}
FUN_KEYWORD_Id -> {
return parseFunction()
}
VAL_KEYWORD_Id -> {
return parseProperty(declarationParsingMode)
}
VAR_KEYWORD_Id -> {
return parseProperty(declarationParsingMode)
}
LPAR_Id -> {
var lookahead: IElementType? = lookahead(1)
return (if (lookahead == VAL_KEYWORD || lookahead == VAR_KEYWORD) parseProperty(declarationParsingMode) else null)
return parseTypeAlias()
parseObject(nameParsingModeForObject,true)
return OBJECT_DECLARATION
if (detector.isEnumDetected() && declarationParsingMode.canBeEnumUsedAsSoftKeyword) {
return parseClass(true,false)
}
}
LBRACKET_Id -> {
var lookahead: IElementType? = lookahead(1)
return (if (lookahead == VAL_KEYWORD || lookahead == VAR_KEYWORD) parseProperty(declarationParsingMode) else null)
return parseTypeAlias()
parseObject(nameParsingModeForObject,true)
return OBJECT_DECLARATION
if (detector.isEnumDetected() && declarationParsingMode.canBeEnumUsedAsSoftKeyword) {
return parseClass(true,false)
}
}
TYPE_ALIAS_KEYWORD_Id -> {
return parseTypeAlias()
}
OBJECT_KEYWORD_Id -> {
parseObject(nameParsingModeForObject,true)
return OBJECT_DECLARATION
if (detector.isEnumDetected() && declarationParsingMode.canBeEnumUsedAsSoftKeyword) {
return parseClass(true,false)
}
}
IDENTIFIER_Id -> {
if (detector.isEnumDetected() && declarationParsingMode.canBeEnumUsedAsSoftKeyword) {
return parseClass(true,false)
}
}
}
} while (false)
return null
}

open fun parseModifierList(noModifiersBefore: TokenSet): Boolean {

return parseModifierList(null,noModifiersBefore,false)
}

open fun parseAnnotationsList(noModifiersBefore: TokenSet): Unit {

doParseModifierList(null,TokenSet.EMPTY,AnnotationParsingMode.DEFAULT,noModifiersBefore,false)
}

open fun parseModifierList(tokenConsumer: Consumer<IElementType>?, noModifiersBefore: TokenSet, localDeclaration: Boolean): Boolean {

return doParseModifierList(tokenConsumer,MODIFIER_KEYWORDS,AnnotationParsingMode.DEFAULT,noModifiersBefore,localDeclaration)
}

private fun parseFunctionTypeValueParameterModifierList(): Unit {

doParseModifierList(null,RESERVED_VALUE_PARAMETER_MODIFIER_KEYWORDS,NO_ANNOTATIONS_NO_CONTEXT,NO_MODIFIER_BEFORE_FOR_VALUE_PARAMETER,false)
}

private fun parseTypeModifierList(): Unit {

doParseModifierList(null,TYPE_MODIFIER_KEYWORDS,TYPE_CONTEXT,TokenSet.EMPTY,false)
}

private fun parseTypeArgumentModifierList(): Unit {

doParseModifierList(null,TYPE_ARGUMENT_MODIFIER_KEYWORDS,NO_ANNOTATIONS_NO_CONTEXT,COMMA_COLON_GT_SET,false)
}

private fun doParseModifierListBody(tokenConsumer: Consumer<IElementType>?, modifierKeywords: TokenSet, annotationParsingMode: AnnotationParsingMode, noModifiersBefore: TokenSet, localDeclaration: Boolean): Boolean {

var empty: Boolean = true
var beforeAnnotationMarker: PsiBuilder.Marker
while (! eof()) {
if (at(AT) && annotationParsingMode.allowAnnotations) {
beforeAnnotationMarker = mark()
var isAnnotationParsed: Boolean = parseAnnotationOrList(annotationParsingMode)
if (! isAnnotationParsed && ! annotationParsingMode.withSignificantWhitespaceBeforeArguments) {
beforeAnnotationMarker.rollbackTo()
var newMode: AnnotationParsingMode = (if (annotationParsingMode.allowContextList) WITH_SIGNIFICANT_WHITESPACE_BEFORE_ARGUMENTS else WITH_SIGNIFICANT_WHITESPACE_BEFORE_ARGUMENTS_NO_CONTEXT)
doParseModifierListBody(tokenConsumer,modifierKeywords,newMode,noModifiersBefore,localDeclaration)
empty = false
break
}
else {
beforeAnnotationMarker.drop()
}
}
else if (at(CONTEXT_KEYWORD) && annotationParsingMode.allowContextList && lookahead(1) == LPAR) {
var contextMarker: PsiBuilder.Marker = mark()
if (! parseContextParameterOrReceiverList(false) && localDeclaration) {
contextMarker.rollbackTo()
break
}
else {
contextMarker.drop()
}
}
else if (tryParseModifier(tokenConsumer,noModifiersBefore,modifierKeywords)) {

}
else {
break
}
empty = false
}
return empty
}

private fun doParseModifierList(tokenConsumer: Consumer<IElementType>?, modifierKeywords: TokenSet, annotationParsingMode: AnnotationParsingMode, noModifiersBefore: TokenSet, localDeclaration: Boolean): Boolean {

var list: PsiBuilder.Marker = mark()
var empty: Boolean = doParseModifierListBody(tokenConsumer,modifierKeywords,annotationParsingMode,noModifiersBefore,localDeclaration)
if (empty) {
list.drop()
}
else {
list.done(MODIFIER_LIST)
}
return ! empty
}

private fun tryParseModifier(tokenConsumer: Consumer<IElementType>?, noModifiersBefore: TokenSet, modifierKeywords: TokenSet): Boolean {

var marker: PsiBuilder.Marker = mark()
if (atSet(modifierKeywords)) {
var lookahead: IElementType? = lookahead(1)
if (at(FUN_KEYWORD) && lookahead != INTERFACE_KEYWORD) {
marker.rollbackTo()
return false
}
if (lookahead != null && ! noModifiersBefore.contains(lookahead)) {
var tt: IElementType? = tt()
if (tokenConsumer != null) {
tokenConsumer.consume(tt!!)
}
advance()
marker.collapse(tt!!)
return true
}
}
marker.rollbackTo()
return false
}

private fun parseContextParameterOrReceiverList(inFunctionType: Boolean): Boolean {

assert(_at(CONTEXT_KEYWORD))
var valueParameterOrTypeRefList: PsiBuilder.Marker = mark()
advance()
assert(_at(LPAR))
var noError: Boolean
if (lookahead(1) == RPAR) {
advance()
error("Empty context parameter list")
advance()
noError = false
}
else {
noError = valueParameterLoop(inFunctionType,CONTEXT_PARAMETERS_FOLLOW_SET,lambda2@{ parseValueParameterOrTypeRef(inFunctionType) })
}
valueParameterOrTypeRefList.done(CONTEXT_PARAMETER_LIST)
return noError
}

private fun parseValueParameterOrTypeRef(inFunctionType: Boolean): Boolean {

if (tryParseValueParameter(true)) {
return true
}
var contextReceiver: PsiBuilder.Marker = mark()
if (! inFunctionType && myExpressionParsing.isAtLabelDefinitionOrMissingIdentifier()) {
myExpressionParsing.parseLabelDefinition()
}
parseTypeRef()
contextReceiver.done(CONTEXT_RECEIVER)
return inFunctionType
}

private fun parseFileAnnotationList(mode: AnnotationParsingMode): Unit {

if (! mode.isFileAnnotationParsingMode) {
LOG.error("expected file annotation parsing mode, but:" + mode)
}
var fileAnnotationsList: PsiBuilder.Marker = mark()
if (parseAnnotations(mode)) {
fileAnnotationsList.done(FILE_ANNOTATION_LIST)
}
else {
fileAnnotationsList.drop()
}
}

open fun parseAnnotations(mode: AnnotationParsingMode): Boolean {

if (! parseAnnotationOrList(mode)) return false
while (parseAnnotationOrList(mode)) {

}
return true
}

private fun parseAnnotationOrList(mode: AnnotationParsingMode): Boolean {

if (at(AT)) {
var nextRawToken: IElementType? = myBuilder.rawLookup(1)
var tokenToMatch: IElementType? = nextRawToken
var isTargetedAnnotation: Boolean = false
if ((nextRawToken == IDENTIFIER || ANNOTATION_TARGETS.contains(nextRawToken)) && lookahead(2) == COLON) {
tokenToMatch = lookahead(3)
isTargetedAnnotation = true
}
else if (lookahead(1) == COLON) {
isTargetedAnnotation = true
tokenToMatch = lookahead(2)
}
if (tokenToMatch == IDENTIFIER) {
return parseAnnotation(mode)
}
else if (tokenToMatch == LBRACKET) {
return parseAnnotationList(mode)
}
else {
if (isTargetedAnnotation) {
if (lookahead(1) == COLON) {
errorAndAdvance("Expected annotation identifier after ':'",2)
}
else {
errorAndAdvance("Expected annotation identifier after ':'",3)
}
}
else {
errorAndAdvance("Expected annotation identifier after '@'",1)
}
}
return true
}
return false
}

private fun parseAnnotationList(mode: AnnotationParsingMode): Boolean {

assert(_at(AT))
var annotation: PsiBuilder.Marker = mark()
myBuilder.disableNewlines()
advance()
if (! parseAnnotationTargetIfNeeded(mode)) {
annotation.rollbackTo()
myBuilder.restoreNewlinesState()
return false
}
assert(_at(LBRACKET))
advance()
if (! at(IDENTIFIER) && ! at(AT)) {
error("Expecting a list of annotations")
}
else {
while (at(IDENTIFIER) || at(AT)) {
if (at(AT)) {
errorAndAdvance("No '@' needed in annotation list")
continue
}
parseAnnotation(DEFAULT)
while (at(COMMA)) {
errorAndAdvance("No commas needed to separate annotations")
}
}
}
expect(RBRACKET,"Expecting ']' to close the annotation list")
myBuilder.restoreNewlinesState()
annotation.done(ANNOTATION)
return true
}

private fun parseAnnotationTargetIfNeeded(mode: AnnotationParsingMode): Boolean {

var expectedAnnotationTargetBeforeColon: String = "Expected annotation target before ':'"
if (at(COLON)) {
errorAndAdvance(expectedAnnotationTargetBeforeColon)
return true
}
var targetKeyword: KtKeywordToken? = (if (atSet(ANNOTATION_TARGETS)) (myBuilder.tokenType as KtKeywordToken) else null)
if (mode == FILE_ANNOTATIONS_WHEN_PACKAGE_OMITTED && !(targetKeyword == FILE_KEYWORD && lookahead(1) == COLON)) {
return false
}
if (lookahead(1) == COLON && targetKeyword == null && at(IDENTIFIER)) {
errorAndAdvance(expectedAnnotationTargetBeforeColon)
advance()
return true
}
if (targetKeyword == null && mode.isFileAnnotationParsingMode) {
parseAnnotationTarget(FILE_KEYWORD)
}
else if (targetKeyword != null) {
parseAnnotationTarget(targetKeyword)
}
return true
}

private fun parseAnnotationTarget(keyword: KtKeywordToken): Unit {

var marker: PsiBuilder.Marker = mark()
if (! expect(keyword)) {
error(generateAnnotationTargetErrorMessage(keyword))
marker.drop()
}
else {
marker.done(ANNOTATION_TARGET)
}
if (! expect(KtTokens.COLON)) {
errorWithRecovery(generateAnnotationTargetErrorMessage(keyword),IDENTIFIER_RBRACKET_LBRACKET_SET)
}
}

private fun parseAnnotation(mode: AnnotationParsingMode): Boolean {

assert(_at(IDENTIFIER) || (_at(AT) &&(! isNextRawTokenCommentOrWhitespace() || lookahead(1) == COLON))) { "Invalid annotation prefix" }
var annotation: PsiBuilder.Marker = mark()
var atAt: Boolean = at(AT)
if (atAt) {
advance()
}
if (atAt && ! parseAnnotationTargetIfNeeded(mode)) {
annotation.rollbackTo()
return false
}
var reference: PsiBuilder.Marker = mark()
var typeReference: PsiBuilder.Marker = mark()
parseUserType()
typeReference.done(TYPE_REFERENCE)
reference.done(CONSTRUCTOR_CALLEE)
parseTypeArgumentList()
if (at(LPAR) && ! VAL_VAR.contains(lookahead(1)) && !(WHITE_SPACE_OR_COMMENT_BIT_SET.contains(myBuilder.rawLookup(- 1)) && mode.withSignificantWhitespaceBeforeArguments)) {
myExpressionParsing.parseValueArgumentList()
if (mode.typeContext && (getLastToken() != RPAR || at(ARROW))) {
annotation.done(ANNOTATION_ENTRY)
return false
}
}
annotation.done(ANNOTATION_ENTRY)
return true
}

private fun isNextRawTokenCommentOrWhitespace(): Boolean {

return WHITE_SPACE_OR_COMMENT_BIT_SET.contains(myBuilder.rawLookup(1))
}

enum class NameParsingMode {
REQUIRED,
ALLOWED,
PROHIBITED;


}

private fun parseClassOrObject(`object`: Boolean, nameParsingMode: NameParsingMode, optionalBody: Boolean, enumClass: Boolean, expectKindKeyword: Boolean): IElementType {

if (expectKindKeyword) {
if (`object`) {
assert(_at(OBJECT_KEYWORD))
}
else {
assert(_atSet(CLASS_INTERFACE_SET))
}
advance()
}
else {
assert(enumClass) { "Currently classifiers without class/interface/object are only allowed for enums" }
error("'class' keyword is expected after 'enum'")
}
if (nameParsingMode == NameParsingMode.REQUIRED) {
expect(IDENTIFIER,"Name expected",CLASS_NAME_RECOVERY_SET)
}
else {
assert(`object`) { "Must be an object to be nameless" }
if (at(IDENTIFIER)) {
if (nameParsingMode == NameParsingMode.PROHIBITED) {
errorAndAdvance("An object expression cannot bind a name")
}
else {
assert(nameParsingMode == NameParsingMode.ALLOWED)
advance()
}
}
}
var typeParametersDeclared: Boolean = parseTypeParameterList(TYPE_PARAMETER_GT_RECOVERY_SET)
var beforeConstructorModifiers: PsiBuilder.Marker = mark()
var primaryConstructorMarker: PsiBuilder.Marker = mark()
var hasConstructorModifiers: Boolean = parseModifierList(TokenSet.EMPTY)
if (hasConstructorModifiers && ! atSet(LPAR_LBRACE_COLON_CONSTRUCTOR_KEYWORD_SET)) {
beforeConstructorModifiers.rollbackTo()
return (if (`object`) OBJECT_DECLARATION else CLASS)
}
beforeConstructorModifiers.drop()
var hasConstructorKeyword: Boolean = at(CONSTRUCTOR_KEYWORD)
if (hasConstructorKeyword) {
advance()
}
if (at(LPAR)) {
parseValueParameterList(false,true,LBRACE_RBRACE_SET)
primaryConstructorMarker.done(PRIMARY_CONSTRUCTOR)
}
else if (hasConstructorModifiers || hasConstructorKeyword) {
primaryConstructorMarker.done(PRIMARY_CONSTRUCTOR)
if (hasConstructorKeyword) {
error("Expecting primary constructor parameter list")
}
else {
error("Expecting 'constructor' keyword")
}
}
else {
primaryConstructorMarker.drop()
}
if (at(COLON)) {
advance()
parseDelegationSpecifierList()
}
var whereMarker: OptionalMarker = OptionalMarker(`object`)
parseTypeConstraintsGuarded(typeParametersDeclared)
whereMarker.error("Where clause is not allowed for objects")
if (at(LBRACE)) {
if (enumClass) {
parseEnumClassBody()
}
else {
parseClassBody()
}
}
else if (! optionalBody) {
var fakeBody: PsiBuilder.Marker = mark()
error("Expecting a class body")
fakeBody.done(CLASS_BODY)
}
return (if (`object`) OBJECT_DECLARATION else CLASS)
}

private fun parseClass(enumClass: Boolean, expectKindKeyword: Boolean): IElementType {

return parseClassOrObject(false,NameParsingMode.REQUIRED,true,enumClass,expectKindKeyword)
}

open fun parseObject(nameParsingMode: NameParsingMode, optionalBody: Boolean): Unit {

parseClassOrObject(true,nameParsingMode,optionalBody,false,true)
}

private fun parseEnumClassBody(): Unit {

if (! at(LBRACE)) return 
var body: PsiBuilder.Marker = mark()
myBuilder.enableNewlines()
advance()
if (! parseEnumEntries() && ! at(RBRACE)) {
error("Expecting ';' after the last enum entry or '}' to close enum class body")
}
parseMembers()
expect(RBRACE,"Expecting '}' to close enum class body")
myBuilder.restoreNewlinesState()
body.done(CLASS_BODY)
}

private fun parseEnumEntries(): Boolean {

while (! eof() && ! at(RBRACE)) {
switch3@ do {
when (parseEnumEntry()) {
ParseEnumEntryResult.FAILED -> {
if (at(SEMICOLON)) {
advance()
return true
}
else {
return false
}
return false
break@switch3
}
ParseEnumEntryResult.NO_DELIMITER -> {
return false
}
ParseEnumEntryResult.COMMA_DELIMITER -> {
break@switch3
}
ParseEnumEntryResult.SEMICOLON_DELIMITER -> {
return true
}
}
} while (false)
}
return false
}

private enum class ParseEnumEntryResult {
FAILED,
NO_DELIMITER,
COMMA_DELIMITER,
SEMICOLON_DELIMITER;


}

private fun parseEnumEntry(): ParseEnumEntryResult {

var entry: PsiBuilder.Marker = mark()
parseModifierList(COMMA_SEMICOLON_RBRACE_SET)
if (! atSet(SOFT_KEYWORDS_AT_MEMBER_START) && at(IDENTIFIER)) {
advance()
if (at(LPAR)) {
var initializerList: PsiBuilder.Marker = mark()
var delegatorSuperCall: PsiBuilder.Marker = mark()
var callee: PsiBuilder.Marker = mark()
var typeReference: PsiBuilder.Marker = mark()
var type: PsiBuilder.Marker = mark()
var referenceExpr: PsiBuilder.Marker = mark()
referenceExpr.done(ENUM_ENTRY_SUPERCLASS_REFERENCE_EXPRESSION)
type.done(USER_TYPE)
typeReference.done(TYPE_REFERENCE)
callee.done(CONSTRUCTOR_CALLEE)
myExpressionParsing.parseValueArgumentList()
delegatorSuperCall.done(SUPER_TYPE_CALL_ENTRY)
initializerList.done(INITIALIZER_LIST)
}
if (at(LBRACE)) {
parseClassBody()
}
var commaFound: Boolean = at(COMMA)
if (commaFound) {
advance()
}
var semicolonFound: Boolean = at(SEMICOLON)
if (semicolonFound) {
advance()
}
closeDeclarationWithCommentBinders(entry,ENUM_ENTRY,true)
return (if (semicolonFound) ParseEnumEntryResult.SEMICOLON_DELIMITER else ((if(commaFound) ParseEnumEntryResult.COMMA_DELIMITER else ParseEnumEntryResult.NO_DELIMITER)))
}
else {
entry.rollbackTo()
return ParseEnumEntryResult.FAILED
}
}

private fun parseClassBody(): Unit {

var body: PsiBuilder.Marker = mark()
myBuilder.enableNewlines()
if (expect(LBRACE,"Expecting a class body")) {
parseMembers()
expect(RBRACE,"Missing '}")
}
myBuilder.restoreNewlinesState()
body.done(CLASS_BODY)
}

private fun parseMembers(): Unit {

while (! eof() && ! at(RBRACE)) {
parseMemberDeclaration()
}
}

private fun parseMemberDeclaration(): Unit {

if (at(SEMICOLON)) {
advance()
return 
}
var decl: PsiBuilder.Marker = mark()
var isCompanionBlock: Boolean = at(COMPANION_KEYWORD) && lookahead(1) == LBRACE
var detector: ModifierDetector = ModifierDetector()
parseModifierList(detector,TokenSet.EMPTY,false)
var declType: IElementType? = (if (isCompanionBlock) parseCompanionBlock() else parseMemberDeclarationRest(detector))
if (declType == null) {
errorWithRecovery("Expecting member declaration",TokenSet.EMPTY)
decl.drop()
}
else {
closeDeclarationWithCommentBinders(decl,declType,true)
}
}

private fun parseMemberDeclarationRest(modifierDetector: ModifierDetector): IElementType? {

var declType: IElementType? = parseCommonDeclaration(modifierDetector,(if(modifierDetector.isCompanionDetected()) NameParsingMode.ALLOWED else NameParsingMode.REQUIRED),DeclarationParsingMode.MEMBER_OR_TOPLEVEL)
if (declType != null) return declType
if (at(INIT_KEYWORD)) {
advance()
if (at(LBRACE)) {
parseBlock()
}
else {
mark().error("Expecting '{' after 'init'")
}
declType = CLASS_INITIALIZER
}
else if (at(CONSTRUCTOR_KEYWORD)) {
parseSecondaryConstructor()
declType = SECONDARY_CONSTRUCTOR
}
else if (at(LBRACE)) {
error("Expecting member declaration")
parseBlock()
declType = FUN
}
return declType
}

private fun parseCompanionBlock(): IElementType {

parseClassBody()
return COMPANION_BLOCK
}

private fun parseSecondaryConstructor(): Unit {

assert(_at(CONSTRUCTOR_KEYWORD))
advance()
if (at(LPAR)) {
parseValueParameterList(false,true,VALUE_ARGS_RECOVERY_SET)
}
else {
errorWithRecovery("Expecting '('",TokenSet.orSet(VALUE_ARGS_RECOVERY_SET,TokenSet.create(COLON)))
}
if (at(COLON)) {
advance()
var delegationCall: PsiBuilder.Marker = mark()
if (at(THIS_KEYWORD) || at(SUPER_KEYWORD)) {
parseThisOrSuper()
myExpressionParsing.parseValueArgumentList()
}
else {
error("Expecting a 'this' or 'super' constructor call")
var beforeWrongDelegationCallee: PsiBuilder.Marker? = null
if (! at(LPAR)) {
beforeWrongDelegationCallee = mark()
advance()
}
myExpressionParsing.parseValueArgumentList()
if (beforeWrongDelegationCallee != null) {
if (at(LBRACE)) {
beforeWrongDelegationCallee.drop()
}
else {
beforeWrongDelegationCallee.rollbackTo()
}
}
}
delegationCall.done(CONSTRUCTOR_DELEGATION_CALL)
}
else {
var emptyDelegationCall: PsiBuilder.Marker = mark()
mark().done(CONSTRUCTOR_DELEGATION_REFERENCE)
emptyDelegationCall.done(CONSTRUCTOR_DELEGATION_CALL)
}
if (at(LBRACE)) {
parseBlock()
}
}

private fun parseThisOrSuper(): Unit {

assert(_at(THIS_KEYWORD) || _at(SUPER_KEYWORD))
var mark: PsiBuilder.Marker = mark()
advance()
mark.done(CONSTRUCTOR_DELEGATION_REFERENCE)
}

private fun parseTypeAlias(): IElementType {

assert(_at(TYPE_ALIAS_KEYWORD))
advance()
expect(IDENTIFIER,"Type name expected",LT_EQ_SEMICOLON_TOP_LEVEL_DECLARATION_FIRST_SET)
parseTypeParameterList(TYPE_PARAMETER_GT_RECOVERY_SET)
if (at(WHERE_KEYWORD)) {
var error: PsiBuilder.Marker = mark()
parseTypeConstraints()
error.error("Type alias parameters can't have bounds")
}
expect(EQ,"Expecting '='",TOP_LEVEL_DECLARATION_FIRST_SEMICOLON_SET)
parseTypeRef()
consumeIf(SEMICOLON)
return TYPEALIAS
}

enum class DeclarationParsingMode {
MEMBER_OR_TOPLEVEL(false,true,true),
LOCAL(true,false,false),
SCRIPT_TOPLEVEL(true,true,false);

val destructuringAllowed: Boolean

val accessorsAllowed: Boolean

val canBeEnumUsedAsSoftKeyword: Boolean

private constructor(destructuringAllowed: Boolean, accessorsAllowed: Boolean, canBeEnumUsedAsSoftKeyword: Boolean) {

this.destructuringAllowed = destructuringAllowed
this.accessorsAllowed = accessorsAllowed
this.canBeEnumUsedAsSoftKeyword = canBeEnumUsedAsSoftKeyword
}
}

open fun parseProperty(mode: DeclarationParsingMode): IElementType {

var isShortForm: Boolean = at(VAL_KEYWORD) || at(VAR_KEYWORD)
if (isShortForm) {
advance()
}
var typeParametersDeclared: Boolean = at(LT) && parseTypeParameterList(IDENTIFIER_EQ_COLON_SEMICOLON_SET)
myBuilder.disableJoiningComplexTokens()
var receiver: PsiBuilder.Marker = mark()
var receiverTypeDeclared: Boolean = parseReceiverType("property",PROPERTY_NAME_FOLLOW_SET)
var multiDeclaration: Boolean = at(LPAR) || at(LBRACKET)
errorIf(receiver,multiDeclaration && receiverTypeDeclared,"Receiver type is not allowed on a destructuring declaration")
var isNameOnTheNextLine: Boolean = eol()
var beforeName: PsiBuilder.Marker = mark()
if (multiDeclaration) {
var multiDecl: PsiBuilder.Marker = mark()
parseMultiDeclarationEntry((if(isShortForm) PROPERTY_NAME_FOLLOW_SET else DESTRUCTURING_PROPERTY_NAME_FOLLOW_SET),PROPERTY_NAME_FOLLOW_MULTI_DECLARATION_RECOVERY_SET,(if(isShortForm) MultiDeclarationMode.SHORT else MultiDeclarationMode.FULL))
errorIf(multiDecl,! mode.destructuringAllowed,"Destructuring declarations are only allowed for local variables/values")
}
else {
parseFunctionOrPropertyName(receiverTypeDeclared,"property",PROPERTY_NAME_FOLLOW_SET,PROPERTY_NAME_FOLLOW_FUNCTION_OR_PROPERTY_RECOVERY_SET,true)
}
myBuilder.restoreJoiningComplexTokensState()
var noTypeReference: Boolean = true
if (at(COLON)) {
noTypeReference = false
var type: PsiBuilder.Marker = mark()
advance()
parseTypeRef()
errorIf(type,multiDeclaration,"Type annotations are not allowed on destructuring declarations")
}
parseTypeConstraintsGuarded(typeParametersDeclared)
if (! parsePropertyDelegateOrAssignment() && isNameOnTheNextLine && noTypeReference && ! receiverTypeDeclared) {
if (! multiDeclaration || isShortForm) {
beforeName.rollbackTo()
error("Expecting property name or receiver type")
}
else {
beforeName.drop()
}
return (if (multiDeclaration) DESTRUCTURING_DECLARATION else PROPERTY)
}
beforeName.drop()
if (mode.accessorsAllowed) {
myBuilder.enableNewlines()
var hasNewLineWithSemicolon: Boolean = consumeIf(SEMICOLON) && myBuilder.newlineBeforeCurrentToken()
myBuilder.restoreNewlinesState()
if (! hasNewLineWithSemicolon) {
var alreadyRead: PropertyComponentKind.Collector = PropertyComponentKind.Collector()
var propertyComponentKind: PropertyComponentKind? = parsePropertyComponent(alreadyRead)
while (propertyComponentKind != null) {
alreadyRead.collect(propertyComponentKind)
propertyComponentKind = parsePropertyComponent(alreadyRead)
}
if (! atSet(EOL_OR_SEMICOLON_RBRACE_SET)) {
if (getLastToken() != SEMICOLON) {
errorUntil("Property getter or setter expected",TokenSet.orSet(DECLARATION_FIRST,TokenSet.create(EOL_OR_SEMICOLON,LBRACE,RBRACE)))
}
}
else {
consumeIf(SEMICOLON)
}
}
}
return (if (multiDeclaration) DESTRUCTURING_DECLARATION else PROPERTY)
}

private fun parsePropertyDelegateOrAssignment(): Boolean {

if (at(BY_KEYWORD)) {
parsePropertyDelegate()
return true
}
else if (at(EQ)) {
advance()
myExpressionParsing.parseExpression()
return true
}
return false
}

private fun parsePropertyDelegate(): Unit {

assert(_at(BY_KEYWORD))
var delegate: PsiBuilder.Marker = mark()
advance()
myExpressionParsing.parseExpression()
delegate.done(PROPERTY_DELEGATE)
}

enum class MultiDeclarationMode {
SHORT,
FULL,
FULL_VAL_ONLY;


}

open fun parseMultiDeclarationEntry(follow: TokenSet, recoverySet: TokenSet?, mode: MultiDeclarationMode): Unit {

myBuilder.disableNewlines()
var isParentheses: Boolean = at(LPAR)
var closingBrace: KtSingleValueToken = (if (isParentheses) RPAR else RBRACKET)
advance()
if (! atSet(follow)) {
while (true) {
if (at(COMMA)) {
errorAndAdvance("Expecting a name")
}
else if (at(closingBrace)) {
error("Expecting a name")
break
}
var property: PsiBuilder.Marker = mark()
if (mode == MultiDeclarationMode.FULL) {
if (at(VAL_KEYWORD) || at(VAR_KEYWORD)) {
advance()
}
else {
errorWithRecovery("Expecting val or var keyword",recoverySet)
}
}
else if (mode == MultiDeclarationMode.FULL_VAL_ONLY) {
if (at(VAL_KEYWORD)) {
advance()
}
else {
errorWithRecovery("Expecting val keyword",recoverySet)
}
}
else {
parseModifierList(COMMA_RPAR_RBRACKET_COLON_EQ_SET)
}
expect(IDENTIFIER,"Expecting a name",recoverySet)
if (at(COLON)) {
advance()
parseTypeRef(follow)
}
if (at(EQ) && closingBrace == RPAR) {
advance()
myExpressionParsing.parseSimpleNameExpression()
}
property.done(DESTRUCTURING_DECLARATION_ENTRY)
if (! at(COMMA)) break
advance()
if (at(closingBrace)) break
}
}
expect(closingBrace,(if(isParentheses) "Expecting ')'" else "Expecting ']'"),follow)
myBuilder.restoreNewlinesState()
}

private enum class PropertyComponentKind {
GET,
SET,
FIELD;

open class Collector {
private val collected: BooleanArray = booleanArrayOf(false,false,false)

open fun collect(kind: PropertyComponentKind): Unit {

collected[kind.ordinal] = true
}

open fun contains(kind: PropertyComponentKind): Boolean {

return collected[kind.ordinal]
}
}
}

private fun parsePropertyComponent(notAllowedKind: PropertyComponentKind.Collector): PropertyComponentKind? {

var propertyComponent: PsiBuilder.Marker = mark()
parseModifierList(TokenSet.EMPTY)
var propertyComponentKind: PropertyComponentKind
if (at(GET_KEYWORD)) {
propertyComponentKind = PropertyComponentKind.GET
}
else if (at(SET_KEYWORD)) {
propertyComponentKind = PropertyComponentKind.SET
}
else if (at(FIELD_KEYWORD)) {
propertyComponentKind = PropertyComponentKind.FIELD
}
else {
propertyComponent.rollbackTo()
return null
}
if (notAllowedKind.contains(propertyComponentKind)) {
propertyComponent.rollbackTo()
return null
}
advance()
if (! at(LPAR) && propertyComponentKind != PropertyComponentKind.FIELD) {
if (! atSet(ACCESSOR_FIRST_OR_PROPERTY_END)) {
errorUntil("Accessor body expected",TokenSet.orSet(ACCESSOR_FIRST_OR_PROPERTY_END,TokenSet.create(LBRACE,LPAR,EQ)))
}
else {
closeDeclarationWithCommentBinders(propertyComponent,PROPERTY_ACCESSOR,true)
return propertyComponentKind
}
}
myBuilder.disableNewlines()
if (propertyComponentKind != PropertyComponentKind.FIELD) {
var parameterList: PsiBuilder.Marker = mark()
expect(LPAR,"Expecting '('",RPAR_IDENTIFIER_COLON_LBRACE_EQ_SET)
if (propertyComponentKind == PropertyComponentKind.SET) {
var setterParameter: PsiBuilder.Marker = mark()
parseModifierList(COMMA_COLON_RPAR_SET)
expect(IDENTIFIER,"Expecting parameter name",RPAR_COLON_LBRACE_EQ_SET)
if (at(COLON)) {
advance()
parseTypeRef()
}
setterParameter.done(VALUE_PARAMETER)
if (at(COMMA)) {
advance()
}
}
if (! at(RPAR)) {
errorUntil("Expecting ')'",TokenSet.create(RPAR,COLON,LBRACE,RBRACE,EQ,EOL_OR_SEMICOLON))
}
if (at(RPAR)) {
advance()
}
parameterList.done(VALUE_PARAMETER_LIST)
}
myBuilder.restoreNewlinesState()
if (at(COLON)) {
advance()
parseTypeRef()
}
if (propertyComponentKind != PropertyComponentKind.FIELD) {
parseFunctionContract()
parseFunctionBody()
}
else if (at(EQ)) {
advance()
myExpressionParsing.parseExpression()
consumeIf(SEMICOLON)
}
if (propertyComponentKind == PropertyComponentKind.FIELD) {
closeDeclarationWithCommentBinders(propertyComponent,BACKING_FIELD,true)
}
else {
closeDeclarationWithCommentBinders(propertyComponent,PROPERTY_ACCESSOR,true)
}
return propertyComponentKind
}

private fun parseFunction(): IElementType? {

return parseFunction(false)
}

open fun parseFunction(failIfIdentifierExists: Boolean): IElementType? {

assert(_at(FUN_KEYWORD))
advance()
if (at(RBRACE)) {
error("Function body expected")
return FUN
}
var typeParameterListOccurred: Boolean = false
if (at(LT)) {
parseTypeParameterList(LBRACKET_LBRACE_RBRACE_LPAR_SET)
typeParameterListOccurred = true
}
myBuilder.disableJoiningComplexTokens()
var receiverFound: Boolean = parseReceiverType("function",FUNCTION_NAME_FOLLOW_SET)
if (at(IDENTIFIER) && failIfIdentifierExists) {
myBuilder.restoreJoiningComplexTokensState()
return null
}
parseFunctionOrPropertyName(receiverFound,"function",FUNCTION_NAME_FOLLOW_SET,FUNCTION_NAME_RECOVERY_SET,false)
myBuilder.restoreJoiningComplexTokensState()
if (at(LT)) {
var error: PsiBuilder.Marker = mark()
parseTypeParameterList(LPAR_VALUE_PARAMETERS_FOLLOW_SET)
if (typeParameterListOccurred) {
var finishIndex: Int = myBuilder.rawTokenIndex()
error.rollbackTo()
error = mark()
advance(finishIndex - myBuilder.rawTokenIndex())
error.error("Only one type parameter list is allowed for a function")
}
else {
error.drop()
}
typeParameterListOccurred = true
}
if (at(LPAR)) {
parseValueParameterList(false,false,VALUE_PARAMETERS_FOLLOW_SET)
}
else {
error("Expecting '('")
}
if (at(COLON)) {
advance()
parseTypeRef()
}
var functionContractOccurred: Boolean = parseFunctionContract()
parseTypeConstraintsGuarded(typeParameterListOccurred)
if (! functionContractOccurred) {
parseFunctionContract()
}
if (at(SEMICOLON)) {
advance()
}
else if (at(EQ) || at(LBRACE)) {
parseFunctionBody()
}
return FUN
}

private fun parseReceiverType(title: String, nameFollow: TokenSet): Boolean {

var annotations: PsiBuilder.Marker = mark()
var annotationsPresent: Boolean = parseAnnotations(DEFAULT)
var lastDot: Int = lastDotAfterReceiver()
var receiverPresent: Boolean = lastDot != - 1
if (annotationsPresent) {
if (receiverPresent) {
annotations.rollbackTo()
}
else {
annotations.error("Annotations are not allowed in this position")
}
}
else {
annotations.drop()
}
if (! receiverPresent) return false
createTruncatedBuilder(lastDot).parseTypeRefWithoutIntersections()
if (atSet(RECEIVER_TYPE_TERMINATORS)) {
advance()
}
else {
errorWithRecovery("Expecting '.' before a " + title + " name",nameFollow)
}
return true
}

private fun lastDotAfterReceiver(): Int {

var pattern: AbstractTokenStreamPattern = (if (at(LPAR)) lastDotAfterReceiverLParPattern else lastDotAfterReceiverNotLParPattern)
pattern.reset()
return matchTokenStreamPredicate(pattern)
}

private fun parseFunctionOrPropertyName(receiverFound: Boolean, title: String, nameFollow: TokenSet, recoverySet: TokenSet?, nameRequired: Boolean): Unit {

if (! nameRequired && atSet(nameFollow)) return 
if (expect(IDENTIFIER)) {
return 
}
errorWithRecovery("Expecting " + title + " name" +((if(! receiverFound) " or receiver type" else "")),recoverySet)
}

private fun parseFunctionBody(): Unit {

if (at(LBRACE)) {
parseBlock()
}
else if (at(EQ)) {
advance()
myExpressionParsing.parseExpression()
consumeIf(SEMICOLON)
}
else {
error("Expecting function body")
}
}

open fun parseBlock(): Unit {

parseBlock(true)
}

private fun parseBlock(collapse: Boolean): Unit {

var lazyBlock: PsiBuilder.Marker = mark()
myBuilder.enableNewlines()
var hasOpeningBrace: Boolean = expect(LBRACE,"Expecting '{' to open a block")
var canCollapse: Boolean = collapse && hasOpeningBrace && isLazy
if (canCollapse) {
advanceBalancedBlock()
}
else {
myExpressionParsing.parseStatements()
expect(RBRACE,"Expecting '}'")
}
myBuilder.restoreNewlinesState()
if (canCollapse) {
lazyBlock.collapse(BLOCK)
}
else {
lazyBlock.done(BLOCK)
}
}

open fun advanceBalancedBlock(): Unit {

var braceCount: Int = 1
while (! eof()) {
if (_at(LBRACE)) {
braceCount ++
}
else if (_at(RBRACE)) {
braceCount --
}
advance()
if (braceCount == 0) {
break
}
}
}

private fun parseDelegationSpecifierList(): Unit {

var list: PsiBuilder.Marker = mark()
while (true) {
if (at(COMMA)) {
errorAndAdvance("Expecting a delegation specifier")
continue
}
parseDelegationSpecifier()
if (! at(COMMA)) break
advance()
}
list.done(SUPER_TYPE_LIST)
}

private fun parseDelegationSpecifier(): Unit {

var delegator: PsiBuilder.Marker = mark()
var reference: PsiBuilder.Marker = mark()
parseTypeRef()
if (at(BY_KEYWORD)) {
reference.drop()
advance()
createForByClause(myBuilder,isLazy).myExpressionParsing.parseExpression()
delegator.done(DELEGATED_SUPER_TYPE_ENTRY)
}
else if (at(LPAR)) {
reference.done(CONSTRUCTOR_CALLEE)
myExpressionParsing.parseValueArgumentList()
delegator.done(SUPER_TYPE_CALL_ENTRY)
}
else {
reference.drop()
delegator.done(SUPER_TYPE_ENTRY)
}
}

private fun parseTypeParameterList(recoverySet: TokenSet?): Boolean {

var result: Boolean = false
if (at(LT)) {
var list: PsiBuilder.Marker = mark()
myBuilder.disableNewlines()
advance()
while (true) {
if (at(COMMA)) errorAndAdvance("Expecting type parameter declaration")
parseTypeParameter()
if (! at(COMMA)) break
advance()
if (at(GT)) {
break
}
}
expect(GT,"Missing '>'",recoverySet)
myBuilder.restoreNewlinesState()
result = true
list.done(TYPE_PARAMETER_LIST)
}
return result
}

private fun parseTypeConstraintsGuarded(typeParameterListOccurred: Boolean): Unit {

var error: PsiBuilder.Marker = mark()
var constraints: Boolean = parseTypeConstraints()
errorIf(error,constraints && ! typeParameterListOccurred,"Type constraints are not allowed when no type parameters declared")
}

private fun parseTypeConstraints(): Boolean {

if (at(WHERE_KEYWORD)) {
parseTypeConstraintList()
return true
}
return false
}

private fun parseTypeConstraintList(): Unit {

assert(_at(WHERE_KEYWORD))
advance()
var list: PsiBuilder.Marker = mark()
while (true) {
if (at(COMMA)) errorAndAdvance("Type constraint expected")
parseTypeConstraint()
if (! at(COMMA)) break
advance()
}
list.done(TYPE_CONSTRAINT_LIST)
}

private fun parseTypeConstraint(): Unit {

var constraint: PsiBuilder.Marker = mark()
parseAnnotations(DEFAULT)
var reference: PsiBuilder.Marker = mark()
if (expect(IDENTIFIER,"Expecting type parameter name",COLON_COMMA_LBRACE_RBRACE_TYPE_REF_FIRST_SET)) {
reference.done(REFERENCE_EXPRESSION)
}
else {
reference.drop()
}
expect(COLON,"Expecting ':' before the upper bound",LBRACE_RBRACE_TYPE_REF_FIRST_SET)
parseTypeRef()
constraint.done(TYPE_CONSTRAINT)
}

private fun parseFunctionContract(): Boolean {

if (at(CONTRACT_KEYWORD)) {
myExpressionParsing.parseContractDescriptionBlock()
return true
}
return false
}

private fun parseTypeParameter(): Unit {

if (atSet(TYPE_PARAMETER_GT_RECOVERY_SET)) {
error("Type parameter declaration expected")
return 
}
var mark: PsiBuilder.Marker = mark()
parseModifierList(GT_COMMA_COLON_SET)
expect(IDENTIFIER,"Type parameter name expected",TokenSet.EMPTY)
if (at(COLON)) {
advance()
parseTypeRef()
}
mark.done(TYPE_PARAMETER)
}

open fun parseTypeRef(): Unit {

parseTypeRef(TokenSet.EMPTY)
}

open fun parseTypeRefWithoutIntersections(): Unit {

parseTypeRef(TokenSet.EMPTY,false)
}

open fun parseTypeRef(extraRecoverySet: TokenSet): Unit {

parseTypeRef(extraRecoverySet,true)
}

private fun parseTypeRef(extraRecoverySet: TokenSet, allowSimpleIntersectionTypes: Boolean): Unit {

var typeRefMarker: PsiBuilder.Marker = parseTypeRefContents(extraRecoverySet,allowSimpleIntersectionTypes)
typeRefMarker.done(TYPE_REFERENCE)
}

private fun parseTypeRefContents(extraRecoverySet: TokenSet, allowSimpleIntersectionTypes: Boolean): PsiBuilder.Marker {

var typeRefMarker: PsiBuilder.Marker = mark()
parseTypeModifierList()
var lookahead: IElementType? = lookahead(1)
var lookahead2: IElementType? = lookahead(2)
var typeBeforeDot: Boolean = true
var withContextReceiver: Boolean = at(CONTEXT_KEYWORD) && lookahead == LPAR
var wasFunctionTypeParsed: Boolean = false
var contextReceiversStart: PsiBuilder.Marker = mark()
if (withContextReceiver) {
parseContextParameterOrReceiverList(true)
}
var typeElementMarker: PsiBuilder.Marker = mark()
if (at(IDENTIFIER) && !(lookahead == DOT && lookahead2 == IDENTIFIER) && lookahead != LT && at(DYNAMIC_KEYWORD)) {
var dynamicType: PsiBuilder.Marker = mark()
advance()
dynamicType.done(DYNAMIC_TYPE)
}
else if (at(IDENTIFIER) || at(PACKAGE_KEYWORD) || atParenthesizedMutableForPlatformTypes(0)) {
parseUserType()
}
else if (at(LPAR)) {
var functionOrParenthesizedType: PsiBuilder.Marker = mark()
advance()
parseTypeRefContents(TokenSet.EMPTY,true).drop()
if (at(RPAR) && lookahead(1) != ARROW) {
advance()
functionOrParenthesizedType.drop()
}
else {
functionOrParenthesizedType.rollbackTo()
parseFunctionType(contextReceiversStart.precede())
wasFunctionTypeParsed = true
}
}
else {
errorWithRecovery("Type expected",TokenSet.orSet(TOP_LEVEL_DECLARATION_FIRST,TokenSet.create(EQ,COMMA,GT,RBRACKET,DOT,RPAR,RBRACE,LBRACE,SEMICOLON),extraRecoverySet))
typeBeforeDot = false
}
myBuilder.disableJoiningComplexTokens()
typeElementMarker = parseNullableTypeSuffix(typeElementMarker)
myBuilder.restoreJoiningComplexTokensState()
var wasIntersection: Boolean = false
if (allowSimpleIntersectionTypes && at(AND)) {
var leftTypeRef: PsiBuilder.Marker = typeElementMarker
typeElementMarker = typeElementMarker.precede()
var intersectionType: PsiBuilder.Marker = leftTypeRef.precede()
leftTypeRef.done(TYPE_REFERENCE)
advance()
parseTypeRef(extraRecoverySet,true)
intersectionType.done(INTERSECTION_TYPE)
wasIntersection = true
}
if (typeBeforeDot && at(DOT) && ! wasIntersection && ! wasFunctionTypeParsed) {
var functionType: PsiBuilder.Marker = contextReceiversStart.precede()
var receiverTypeRef: PsiBuilder.Marker = typeElementMarker.precede()
var receiverType: PsiBuilder.Marker = receiverTypeRef.precede()
receiverTypeRef.done(TYPE_REFERENCE)
receiverType.done(FUNCTION_TYPE_RECEIVER)
advance()
if (at(LPAR)) {
parseFunctionType(functionType)
}
else {
functionType.drop()
error("Expecting function type")
}
wasFunctionTypeParsed = true
}
if (withContextReceiver && ! wasFunctionTypeParsed) {
errorWithRecovery("Function type expected expected",TokenSet.orSet(TOP_LEVEL_DECLARATION_FIRST,TokenSet.create(EQ,COMMA,GT,RBRACKET,DOT,RPAR,RBRACE,LBRACE,SEMICOLON),extraRecoverySet))
}
typeElementMarker.drop()
contextReceiversStart.drop()
return typeRefMarker
}

private fun parseNullableTypeSuffix(typeElementMarker: PsiBuilder.Marker): PsiBuilder.Marker {
var typeElementMarker = typeElementMarker
while (at(QUEST) && myBuilder.rawLookup(1) != COLON) {
var precede: PsiBuilder.Marker = typeElementMarker.precede()
advance()
typeElementMarker.done(NULLABLE_TYPE)
typeElementMarker = precede
}
return typeElementMarker
}

private fun parseUserType(): Unit {

var userType: PsiBuilder.Marker = mark()
if (at(PACKAGE_KEYWORD)) {
var keyword: PsiBuilder.Marker = mark()
advance()
keyword.error("Expecting an element")
expect(DOT,"Expecting '.'",TokenSet.create(IDENTIFIER,LBRACE,RBRACE))
}
var reference: PsiBuilder.Marker = mark()
while (true) {
recoverOnParenthesizedWordForPlatformTypes(0,"Mutable",true)
if (expect(IDENTIFIER,"Expecting type name",USER_TYPE_NAME_RECOVERY_SET)) {
reference.done(REFERENCE_EXPRESSION)
}
else {
reference.drop()
break
}
parseTypeArgumentList()
recoverOnPlatformTypeSuffix()
if (! at(DOT)) {
break
}
if (lookahead(1) == LPAR && ! atParenthesizedMutableForPlatformTypes(1)) {
break
}
var precede: PsiBuilder.Marker = userType.precede()
userType.done(USER_TYPE)
userType = precede
advance()
reference = mark()
}
userType.done(USER_TYPE)
}

private fun atParenthesizedMutableForPlatformTypes(offset: Int): Boolean {

return recoverOnParenthesizedWordForPlatformTypes(offset,"Mutable",false)
}

private fun recoverOnParenthesizedWordForPlatformTypes(offset: Int, word: String, consume: Boolean): Boolean {

if (lookahead(offset) == LPAR && lookahead(offset + 1) == IDENTIFIER && lookahead(offset + 2) == RPAR && lookahead(offset + 3) == IDENTIFIER) {
var error: PsiBuilder.Marker = mark()
advance(offset)
advance()
if (! word.equals(myBuilder.tokenText)) {
error.rollbackTo()
return false
}
else {
advance()
advance()
if (consume) {
error.error("Unexpected tokens")
}
else {
error.rollbackTo()
}
return true
}
}
return false
}

private fun recoverOnPlatformTypeSuffix(): Unit {

if (at(EXCL)) {
var error: PsiBuilder.Marker = mark()
advance()
error.error("Unexpected token")
}
}

private fun parseTypeArgumentList(): Unit {

if (! at(LT)) return 
var list: PsiBuilder.Marker = mark()
tryParseTypeArgumentList(TokenSet.EMPTY)
list.done(TYPE_ARGUMENT_LIST)
}

open fun tryParseTypeArgumentList(extraRecoverySet: TokenSet): Boolean {

myBuilder.disableNewlines()
advance()
while (true) {
var projection: PsiBuilder.Marker = mark()
recoverOnParenthesizedWordForPlatformTypes(0,"out",true)
parseTypeArgumentModifierList()
if (at(MUL)) {
advance()
}
else {
parseTypeRef(extraRecoverySet)
}
projection.done(TYPE_PROJECTION)
if (! at(COMMA)) break
advance()
if (at(GT)) {
break
}
}
var atGT: Boolean = at(GT)
if (! atGT) {
error("Expecting a '>'")
}
else {
advance()
}
myBuilder.restoreNewlinesState()
return atGT
}

private fun parseFunctionType(functionType: PsiBuilder.Marker): Unit {

parseFunctionTypeContents(functionType).done(FUNCTION_TYPE)
}

private fun parseFunctionTypeContents(functionType: PsiBuilder.Marker): PsiBuilder.Marker {

assert(_at(LPAR)) { tt().toString() }
parseValueParameterList(true,true,TokenSet.EMPTY)
expect(ARROW,"Expecting '->' to specify return type of a function type",TYPE_REF_FIRST)
parseTypeRef()
return functionType
}

private fun parseValueParameterList(isFunctionTypeContents: Boolean, typeRequired: Boolean, recoverySet: TokenSet?): Unit {

assert(_at(LPAR))
var parameters: PsiBuilder.Marker = mark()
myBuilder.disableNewlines()
valueParameterLoop(isFunctionTypeContents,recoverySet,lambda4@{ if(isFunctionTypeContents) {
if(! tryParseValueParameter(typeRequired)) {
var valueParameter: PsiBuilder.Marker = mark()
parseFunctionTypeValueParameterModifierList()
parseTypeRef()
closeDeclarationWithCommentBinders(valueParameter,VALUE_PARAMETER,false)
}
}
else {
parseValueParameter(typeRequired)
}
return@lambda4 true })
myBuilder.restoreNewlinesState()
parameters.done(VALUE_PARAMETER_LIST)
}

private fun valueParameterLoop(inFunctionTypeContext: Boolean, recoverySet: TokenSet?, parseParameter: () -> Boolean): Boolean {

advance()
var noError: Boolean = true
if (! at(RPAR) && ! atSet(recoverySet!!)) {
while (true) {
var offsetBefore: Int = myBuilder.currentOffset
if (at(RPAR)) {
break
}
noError = parseParameter() && noError
if (at(COMMA)) {
advance()
}
else if (at(COLON)) {
continue
}
else {
if (! at(RPAR)) {
error("Expecting comma or ')'")
noError = false
}
if (! atSet((if(inFunctionTypeContext) LAMBDA_VALUE_PARAMETER_FIRST else VALUE_PARAMETER_FIRST))) break
if (offsetBefore == myBuilder.currentOffset) break
}
}
}
return expect(RPAR,"Expecting ')'",recoverySet) && noError
}

private fun tryParseValueParameter(typeRequired: Boolean): Boolean {

return parseValueParameter(true,typeRequired)
}

open fun parseValueParameter(typeRequired: Boolean): Unit {

parseValueParameter(false,typeRequired)
}

private fun parseValueParameter(rollbackOnFailure: Boolean, typeRequired: Boolean): Boolean {

var parameter: PsiBuilder.Marker = mark()
parseModifierList(NO_MODIFIER_BEFORE_FOR_VALUE_PARAMETER)
if (at(VAR_KEYWORD) || at(VAL_KEYWORD)) {
advance()
}
if (! parseFunctionParameterRest(typeRequired) && rollbackOnFailure) {
parameter.rollbackTo()
return false
}
closeDeclarationWithCommentBinders(parameter,VALUE_PARAMETER,false)
return true
}

private fun parseFunctionParameterRest(typeRequired: Boolean): Boolean {

var noErrors: Boolean = true
if (at(COMMA) || at(RPAR)) {
error("Expecting a parameter declaration")
noErrors = false
}
else if (at(IDENTIFIER) && lookahead(1) == LT) {
error("Parameter name expected")
noErrors = false
parseTypeRef()
}
else if (at(COLON)) {
error("Parameter name expected")
advance()
parseTypeRef()
}
else {
expect(IDENTIFIER,"Parameter name expected",PARAMETER_NAME_RECOVERY_SET)
if (at(COLON)) {
advance()
if (at(IDENTIFIER) && lookahead(1) == COLON) {
error("Type reference expected")
return false
}
parseTypeRef()
}
else if (typeRequired) {
errorWithRecovery("Parameters must have type annotation",PARAMETER_NAME_RECOVERY_SET)
noErrors = false
}
}
if (at(EQ)) {
advance()
myExpressionParsing.parseExpression()
}
return noErrors
}

protected override fun create(builder: SemanticWhitespaceAwarePsiBuilder): KotlinParsing {

return createForTopLevel(builder)
}

open class ModifierDetector : Consumer<IElementType> {
private var enumDetected: Boolean = false

private var companionDetected: Boolean = false

override fun consume(item: IElementType): Unit {

if (item == KtTokens.ENUM_KEYWORD) {
enumDetected = true
}
else if (item == KtTokens.COMPANION_KEYWORD) {
companionDetected = true
}
}

open fun isEnumDetected(): Boolean {

return enumDetected
}

open fun isCompanionDetected(): Boolean {

return companionDetected
}
}

enum class AnnotationParsingMode {
DEFAULT(false,true,true,false,false),
FILE_ANNOTATIONS_BEFORE_PACKAGE(true,true,false,false,false),
FILE_ANNOTATIONS_WHEN_PACKAGE_OMITTED(true,true,false,false,false),
TYPE_CONTEXT(false,true,false,true,false),
WITH_SIGNIFICANT_WHITESPACE_BEFORE_ARGUMENTS(false,true,true,true,true),
WITH_SIGNIFICANT_WHITESPACE_BEFORE_ARGUMENTS_NO_CONTEXT(false,true,false,true,true),
NO_ANNOTATIONS_NO_CONTEXT(false,false,false,false,false);

val isFileAnnotationParsingMode: Boolean

val allowAnnotations: Boolean

val allowContextList: Boolean

val withSignificantWhitespaceBeforeArguments: Boolean

val typeContext: Boolean

private constructor(isFileAnnotationParsingMode: Boolean, allowAnnotations: Boolean, allowContextList: Boolean, typeContext: Boolean, withSignificantWhitespaceBeforeArguments: Boolean) {

this.isFileAnnotationParsingMode = isFileAnnotationParsingMode
this.allowAnnotations = allowAnnotations
this.allowContextList = allowContextList
this.typeContext = typeContext
this.withSignificantWhitespaceBeforeArguments = withSignificantWhitespaceBeforeArguments
}
}

companion object {
private val GT_COMMA_COLON_SET: TokenSet = TokenSet.create(GT,COMMA,COLON)

private val LOG: Logger = Logger.getInstance(KotlinParsing ::class)

private val TOP_LEVEL_DECLARATION_FIRST: TokenSet = TokenSet.create(TYPE_ALIAS_KEYWORD,INTERFACE_KEYWORD,CLASS_KEYWORD,OBJECT_KEYWORD,FUN_KEYWORD,VAL_KEYWORD,VAR_KEYWORD,PACKAGE_KEYWORD)

private val TOP_LEVEL_DECLARATION_FIRST_SEMICOLON_SET: TokenSet = TokenSet.orSet(TOP_LEVEL_DECLARATION_FIRST,TokenSet.create(SEMICOLON))

private val LT_EQ_SEMICOLON_TOP_LEVEL_DECLARATION_FIRST_SET: TokenSet = TokenSet.orSet(TokenSet.create(LT,EQ,SEMICOLON),TOP_LEVEL_DECLARATION_FIRST)

private val DECLARATION_FIRST: TokenSet = TokenSet.orSet(TOP_LEVEL_DECLARATION_FIRST,TokenSet.create(INIT_KEYWORD,GET_KEYWORD,SET_KEYWORD,CONSTRUCTOR_KEYWORD))

private val CLASS_NAME_RECOVERY_SET: TokenSet = TokenSet.orSet(TokenSet.create(LT,LPAR,COLON,LBRACE),TOP_LEVEL_DECLARATION_FIRST)

private val TYPE_PARAMETER_GT_RECOVERY_SET: TokenSet = TokenSet.create(WHERE_KEYWORD,LPAR,COLON,LBRACE,GT)

val PARAMETER_NAME_RECOVERY_SET: TokenSet = TokenSet.create(COLON,EQ,COMMA,RPAR,VAL_KEYWORD,VAR_KEYWORD)

private val PACKAGE_NAME_RECOVERY_SET: TokenSet = TokenSet.create(DOT,EOL_OR_SEMICOLON)

private val IMPORT_RECOVERY_SET: TokenSet = TokenSet.create(AS_KEYWORD,DOT,EOL_OR_SEMICOLON)

private val TYPE_REF_FIRST: TokenSet = TokenSet.create(LBRACKET,IDENTIFIER,LPAR,HASH,DYNAMIC_KEYWORD)

private val LBRACE_RBRACE_TYPE_REF_FIRST_SET: TokenSet = TokenSet.orSet(TokenSet.create(LBRACE,RBRACE),TYPE_REF_FIRST)

private val COLON_COMMA_LBRACE_RBRACE_TYPE_REF_FIRST_SET: TokenSet = TokenSet.orSet(TokenSet.create(COLON,COMMA,LBRACE,RBRACE),TYPE_REF_FIRST)

private val RECEIVER_TYPE_TERMINATORS: TokenSet = TokenSet.create(DOT,SAFE_ACCESS)

private val VALUE_PARAMETER_FIRST: TokenSet = TokenSet.orSet(TokenSet.create(IDENTIFIER,LBRACKET,VAL_KEYWORD,VAR_KEYWORD),TokenSet.andNot(MODIFIER_KEYWORDS,TokenSet.create(FUN_KEYWORD)))

private val LAMBDA_VALUE_PARAMETER_FIRST: TokenSet = TokenSet.orSet(TokenSet.create(IDENTIFIER,LBRACKET),TokenSet.andNot(MODIFIER_KEYWORDS,TokenSet.create(FUN_KEYWORD)))

private val SOFT_KEYWORDS_AT_MEMBER_START: TokenSet = TokenSet.create(CONSTRUCTOR_KEYWORD,INIT_KEYWORD)

private val ANNOTATION_TARGETS: TokenSet = TokenSet.create(ALL_KEYWORD,FILE_KEYWORD,FIELD_KEYWORD,GET_KEYWORD,SET_KEYWORD,PROPERTY_KEYWORD,RECEIVER_KEYWORD,PARAM_KEYWORD,SETPARAM_KEYWORD,DELEGATE_KEYWORD)

private val BLOCK_DOC_COMMENT_SET: TokenSet = TokenSet.create(BLOCK_COMMENT,DOC_COMMENT)

private val SEMICOLON_SET: TokenSet = TokenSet.create(SEMICOLON)

private val COMMA_COLON_GT_SET: TokenSet = TokenSet.create(COMMA,COLON,GT)

private val IDENTIFIER_RBRACKET_LBRACKET_SET: TokenSet = TokenSet.create(IDENTIFIER,RBRACKET,LBRACKET)

private val LBRACE_RBRACE_SET: TokenSet = TokenSet.create(LBRACE,RBRACE)

private val COMMA_SEMICOLON_RBRACE_SET: TokenSet = TokenSet.create(COMMA,SEMICOLON,RBRACE)

private val VALUE_ARGS_RECOVERY_SET: TokenSet = TokenSet.create(LBRACE,SEMICOLON,RPAR,EOL_OR_SEMICOLON,RBRACE)

private val PROPERTY_NAME_FOLLOW_SET: TokenSet = TokenSet.create(COLON,EQ,LBRACE,RBRACE,SEMICOLON,VAL_KEYWORD,VAR_KEYWORD,FUN_KEYWORD,CLASS_KEYWORD)

private val DESTRUCTURING_PROPERTY_NAME_FOLLOW_SET: TokenSet = TokenSet.andNot(PROPERTY_NAME_FOLLOW_SET,VAL_VAR)

private val PROPERTY_NAME_FOLLOW_MULTI_DECLARATION_RECOVERY_SET: TokenSet = TokenSet.orSet(PROPERTY_NAME_FOLLOW_SET,PARAMETER_NAME_RECOVERY_SET)

private val PROPERTY_NAME_FOLLOW_FUNCTION_OR_PROPERTY_RECOVERY_SET: TokenSet = TokenSet.orSet(PROPERTY_NAME_FOLLOW_SET,LBRACE_RBRACE_SET,TOP_LEVEL_DECLARATION_FIRST)

private val IDENTIFIER_EQ_COLON_SEMICOLON_SET: TokenSet = TokenSet.create(IDENTIFIER,EQ,COLON,SEMICOLON)

private val COMMA_RPAR_RBRACKET_COLON_EQ_SET: TokenSet = TokenSet.create(COMMA,RPAR,RBRACKET,COLON,EQ)

private val ACCESSOR_FIRST_OR_PROPERTY_END: TokenSet = TokenSet.orSet(MODIFIER_KEYWORDS,TokenSet.create(AT,GET_KEYWORD,SET_KEYWORD,FIELD_KEYWORD,EOL_OR_SEMICOLON,RBRACE))

private val RPAR_IDENTIFIER_COLON_LBRACE_EQ_SET: TokenSet = TokenSet.create(RPAR,IDENTIFIER,COLON,LBRACE,EQ)

private val COMMA_COLON_RPAR_SET: TokenSet = TokenSet.create(COMMA,COLON,RPAR)

private val RPAR_COLON_LBRACE_EQ_SET: TokenSet = TokenSet.create(RPAR,COLON,LBRACE,EQ)

private val LBRACKET_LBRACE_RBRACE_LPAR_SET: TokenSet = TokenSet.create(LBRACKET,LBRACE,RBRACE,LPAR)

private val FUNCTION_NAME_FOLLOW_SET: TokenSet = TokenSet.create(LT,LPAR,RPAR,COLON,EQ)

private val FUNCTION_NAME_RECOVERY_SET: TokenSet = TokenSet.orSet(TokenSet.create(LT,LPAR,RPAR,COLON,EQ),LBRACE_RBRACE_SET,TOP_LEVEL_DECLARATION_FIRST)

private val VALUE_PARAMETERS_FOLLOW_SET: TokenSet = TokenSet.create(EQ,LBRACE,RBRACE,SEMICOLON,RPAR)

private val CONTEXT_PARAMETERS_FOLLOW_SET: TokenSet = TokenSet.create(CLASS_KEYWORD,OBJECT_KEYWORD,FUN_KEYWORD,VAL_KEYWORD,VAR_KEYWORD)

private val LPAR_VALUE_PARAMETERS_FOLLOW_SET: TokenSet = TokenSet.orSet(TokenSet.create(LPAR),VALUE_PARAMETERS_FOLLOW_SET)

private val LPAR_LBRACE_COLON_CONSTRUCTOR_KEYWORD_SET: TokenSet = TokenSet.create(LPAR,LBRACE,COLON,CONSTRUCTOR_KEYWORD)

private val definitelyOutOfReceiverSet: TokenSet = TokenSet.orSet(TokenSet.create(EQ,COLON,LBRACE,RBRACE,BY_KEYWORD),TOP_LEVEL_DECLARATION_FIRST)

private val EOL_OR_SEMICOLON_RBRACE_SET: TokenSet = TokenSet.create(EOL_OR_SEMICOLON,RBRACE)

private val CLASS_INTERFACE_SET: TokenSet = TokenSet.create(CLASS_KEYWORD,INTERFACE_KEYWORD)

fun createForTopLevel(builder: SemanticWhitespaceAwarePsiBuilder): KotlinParsing {

return KotlinParsing(builder,true,true)
}

fun createForTopLevelNonLazy(builder: SemanticWhitespaceAwarePsiBuilder): KotlinParsing {

return KotlinParsing(builder,true,false)
}

private fun createForByClause(builder: SemanticWhitespaceAwarePsiBuilder, isLazy: Boolean): KotlinParsing {

return KotlinParsing(SemanticWhitespaceAwarePsiBuilderForByClause(builder),false,isLazy)
}

private fun generateAnnotationTargetErrorMessage(keyword: KtKeywordToken): String {

return "Expecting \"" + keyword + KtTokens.COLON.value + "\" prefix for " + keyword + " annotations"
}

private val NO_MODIFIER_BEFORE_FOR_VALUE_PARAMETER: TokenSet = TokenSet.create(COMMA,COLON,EQ,RPAR)

val EXPRESSION_FIRST: TokenSet = TokenSet.create(MINUS,PLUS,MINUSMINUS,PLUSPLUS,EXCL,EXCLEXCL,COLONCOLON,LPAR,TRUE_KEYWORD,FALSE_KEYWORD,INTERPOLATION_PREFIX,OPEN_QUOTE,INTEGER_LITERAL,CHARACTER_LITERAL,FLOAT_LITERAL,NULL_KEYWORD,LBRACE,FUN_KEYWORD,THIS_KEYWORD,SUPER_KEYWORD,IF_KEYWORD,WHEN_KEYWORD,TRY_KEYWORD,OBJECT_KEYWORD,THROW_KEYWORD,RETURN_KEYWORD,CONTINUE_KEYWORD,BREAK_KEYWORD,FOR_KEYWORD,WHILE_KEYWORD,DO_KEYWORD,IDENTIFIER,AT,LBRACKET)

val EXPRESSION_FOLLOW: TokenSet = TokenSet.create(EOL_OR_SEMICOLON,ARROW,COMMA,RBRACE,RPAR,RBRACKET)

private val USER_TYPE_NAME_RECOVERY_SET: TokenSet = TokenSet.orSet(EXPRESSION_FIRST,EXPRESSION_FOLLOW,DECLARATION_FIRST)
}
}
