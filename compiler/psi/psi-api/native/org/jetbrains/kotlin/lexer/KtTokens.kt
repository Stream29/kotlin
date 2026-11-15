/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.lexer

import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.jetbrains.kotlin.kdoc.lexer.KDocTokens
import org.jetbrains.kotlin.psi.KtPsiUtil
import org.jetbrains.kotlin.KtNodeTypes.Companion.DOT_QUALIFIED_EXPRESSION
import org.jetbrains.kotlin.KtNodeTypes.Companion.SAFE_ACCESS_EXPRESSION
import org.jetbrains.kotlin.KtNodeTypes.Companion.ERROR_SAFE_ACCESS_EXPRESSION

interface KtTokens {
companion object {
const val INVALID_Id: Int = 0

const val EOF_Id: Int = KDocTokens.CODE_SPAN_TEXT_Id + 1

const val RESERVED_Id: Int = EOF_Id + 1

const val BLOCK_COMMENT_Id: Int = RESERVED_Id + 1

const val EOL_COMMENT_Id: Int = BLOCK_COMMENT_Id + 1

const val SHEBANG_COMMENT_Id: Int = EOL_COMMENT_Id + 1

const val INTEGER_LITERAL_Id: Int = SHEBANG_COMMENT_Id + 1

const val FLOAT_LITERAL_Id: Int = INTEGER_LITERAL_Id + 1

const val CHARACTER_LITERAL_Id: Int = FLOAT_LITERAL_Id + 1

const val CLOSING_QUOTE_Id: Int = CHARACTER_LITERAL_Id + 1

const val OPEN_QUOTE_Id: Int = CLOSING_QUOTE_Id + 1

const val REGULAR_STRING_PART_Id: Int = OPEN_QUOTE_Id + 1

const val ESCAPE_SEQUENCE_Id: Int = REGULAR_STRING_PART_Id + 1

const val SHORT_TEMPLATE_ENTRY_START_Id: Int = ESCAPE_SEQUENCE_Id + 1

const val LONG_TEMPLATE_ENTRY_START_Id: Int = SHORT_TEMPLATE_ENTRY_START_Id + 1

const val LONG_TEMPLATE_ENTRY_END_Id: Int = LONG_TEMPLATE_ENTRY_START_Id + 1

const val DANGLING_NEWLINE_Id: Int = LONG_TEMPLATE_ENTRY_END_Id + 1

const val PACKAGE_KEYWORD_Id: Int = DANGLING_NEWLINE_Id + 1

const val AS_KEYWORD_Id: Int = PACKAGE_KEYWORD_Id + 1

const val TYPE_ALIAS_KEYWORD_Id: Int = AS_KEYWORD_Id + 1

const val CLASS_KEYWORD_Id: Int = TYPE_ALIAS_KEYWORD_Id + 1

const val THIS_KEYWORD_Id: Int = CLASS_KEYWORD_Id + 1

const val SUPER_KEYWORD_Id: Int = THIS_KEYWORD_Id + 1

const val VAL_KEYWORD_Id: Int = SUPER_KEYWORD_Id + 1

const val VAR_KEYWORD_Id: Int = VAL_KEYWORD_Id + 1

const val FUN_KEYWORD_Id: Int = VAR_KEYWORD_Id + 1

const val FOR_KEYWORD_Id: Int = FUN_KEYWORD_Id + 1

const val NULL_KEYWORD_Id: Int = FOR_KEYWORD_Id + 1

const val TRUE_KEYWORD_Id: Int = NULL_KEYWORD_Id + 1

const val FALSE_KEYWORD_Id: Int = TRUE_KEYWORD_Id + 1

const val IS_KEYWORD_Id: Int = FALSE_KEYWORD_Id + 1

const val IN_KEYWORD_Id: Int = IS_KEYWORD_Id + 1

const val THROW_KEYWORD_Id: Int = IN_KEYWORD_Id + 1

const val RETURN_KEYWORD_Id: Int = THROW_KEYWORD_Id + 1

const val BREAK_KEYWORD_Id: Int = RETURN_KEYWORD_Id + 1

const val CONTINUE_KEYWORD_Id: Int = BREAK_KEYWORD_Id + 1

const val OBJECT_KEYWORD_Id: Int = CONTINUE_KEYWORD_Id + 1

const val IF_KEYWORD_Id: Int = OBJECT_KEYWORD_Id + 1

const val TRY_KEYWORD_Id: Int = IF_KEYWORD_Id + 1

const val ELSE_KEYWORD_Id: Int = TRY_KEYWORD_Id + 1

const val WHILE_KEYWORD_Id: Int = ELSE_KEYWORD_Id + 1

const val DO_KEYWORD_Id: Int = WHILE_KEYWORD_Id + 1

const val WHEN_KEYWORD_Id: Int = DO_KEYWORD_Id + 1

const val INTERFACE_KEYWORD_Id: Int = WHEN_KEYWORD_Id + 1

const val TYPEOF_KEYWORD_Id: Int = INTERFACE_KEYWORD_Id + 1

const val AS_SAFE_Id: Int = TYPEOF_KEYWORD_Id + 1

const val IDENTIFIER_Id: Int = AS_SAFE_Id + 1

const val FIELD_IDENTIFIER_Id: Int = IDENTIFIER_Id + 1

const val LBRACKET_Id: Int = FIELD_IDENTIFIER_Id + 1

const val RBRACKET_Id: Int = LBRACKET_Id + 1

const val LBRACE_Id: Int = RBRACKET_Id + 1

const val RBRACE_Id: Int = LBRACE_Id + 1

const val LPAR_Id: Int = RBRACE_Id + 1

const val RPAR_Id: Int = LPAR_Id + 1

const val DOT_Id: Int = RPAR_Id + 1

const val PLUSPLUS_Id: Int = DOT_Id + 1

const val MINUSMINUS_Id: Int = PLUSPLUS_Id + 1

const val MUL_Id: Int = MINUSMINUS_Id + 1

const val PLUS_Id: Int = MUL_Id + 1

const val MINUS_Id: Int = PLUS_Id + 1

const val EXCL_Id: Int = MINUS_Id + 1

const val DIV_Id: Int = EXCL_Id + 1

const val PERC_Id: Int = DIV_Id + 1

const val LT_Id: Int = PERC_Id + 1

const val GT_Id: Int = LT_Id + 1

const val LTEQ_Id: Int = GT_Id + 1

const val GTEQ_Id: Int = LTEQ_Id + 1

const val EQEQEQ_Id: Int = GTEQ_Id + 1

const val ARROW_Id: Int = EQEQEQ_Id + 1

const val DOUBLE_ARROW_Id: Int = ARROW_Id + 1

const val EXCLEQEQEQ_Id: Int = DOUBLE_ARROW_Id + 1

const val EQEQ_Id: Int = EXCLEQEQEQ_Id + 1

const val EXCLEQ_Id: Int = EQEQ_Id + 1

const val EXCLEXCL_Id: Int = EXCLEQ_Id + 1

const val ANDAND_Id: Int = EXCLEXCL_Id + 1

const val AND_Id: Int = ANDAND_Id + 1

const val OROR_Id: Int = AND_Id + 1

const val SAFE_ACCESS_Id: Int = OROR_Id + 1

const val ELVIS_Id: Int = SAFE_ACCESS_Id + 1

const val QUEST_Id: Int = ELVIS_Id + 1

const val COLONCOLON_Id: Int = QUEST_Id + 1

const val COLON_Id: Int = COLONCOLON_Id + 1

const val SEMICOLON_Id: Int = COLON_Id + 1

const val DOUBLE_SEMICOLON_Id: Int = SEMICOLON_Id + 1

const val RANGE_Id: Int = DOUBLE_SEMICOLON_Id + 1

const val RANGE_UNTIL_Id: Int = RANGE_Id + 1

const val EQ_Id: Int = RANGE_UNTIL_Id + 1

const val MULTEQ_Id: Int = EQ_Id + 1

const val DIVEQ_Id: Int = MULTEQ_Id + 1

const val PERCEQ_Id: Int = DIVEQ_Id + 1

const val PLUSEQ_Id: Int = PERCEQ_Id + 1

const val MINUSEQ_Id: Int = PLUSEQ_Id + 1

const val NOT_IN_Id: Int = MINUSEQ_Id + 1

const val NOT_IS_Id: Int = NOT_IN_Id + 1

const val HASH_Id: Int = NOT_IS_Id + 1

const val AT_Id: Int = HASH_Id + 1

const val COMMA_Id: Int = AT_Id + 1

const val EOL_OR_SEMICOLON_Id: Int = COMMA_Id + 1

const val FILE_KEYWORD_Id: Int = EOL_OR_SEMICOLON_Id + 1

const val FIELD_KEYWORD_Id: Int = FILE_KEYWORD_Id + 1

const val PROPERTY_KEYWORD_Id: Int = FIELD_KEYWORD_Id + 1

const val RECEIVER_KEYWORD_Id: Int = PROPERTY_KEYWORD_Id + 1

const val PARAM_KEYWORD_Id: Int = RECEIVER_KEYWORD_Id + 1

const val SETPARAM_KEYWORD_Id: Int = PARAM_KEYWORD_Id + 1

const val DELEGATE_KEYWORD_Id: Int = SETPARAM_KEYWORD_Id + 1

const val IMPORT_KEYWORD_Id: Int = DELEGATE_KEYWORD_Id + 1

const val WHERE_KEYWORD_Id: Int = IMPORT_KEYWORD_Id + 1

const val BY_KEYWORD_Id: Int = WHERE_KEYWORD_Id + 1

const val GET_KEYWORD_Id: Int = BY_KEYWORD_Id + 1

const val SET_KEYWORD_Id: Int = GET_KEYWORD_Id + 1

const val CONSTRUCTOR_KEYWORD_Id: Int = SET_KEYWORD_Id + 1

const val INIT_KEYWORD_Id: Int = CONSTRUCTOR_KEYWORD_Id + 1

const val CONTEXT_KEYWORD_Id: Int = INIT_KEYWORD_Id + 1

const val CATCH_KEYWORD_Id: Int = CONTEXT_KEYWORD_Id + 1

const val DYNAMIC_KEYWORD_Id: Int = CATCH_KEYWORD_Id + 1

const val FINALLY_KEYWORD_Id: Int = DYNAMIC_KEYWORD_Id + 1

const val ABSTRACT_KEYWORD_Id: Int = FINALLY_KEYWORD_Id + 1

const val ENUM_KEYWORD_Id: Int = ABSTRACT_KEYWORD_Id + 1

const val CONTRACT_KEYWORD_Id: Int = ENUM_KEYWORD_Id + 1

const val OPEN_KEYWORD_Id: Int = CONTRACT_KEYWORD_Id + 1

const val INNER_KEYWORD_Id: Int = OPEN_KEYWORD_Id + 1

const val OVERRIDE_KEYWORD_Id: Int = INNER_KEYWORD_Id + 1

const val PRIVATE_KEYWORD_Id: Int = OVERRIDE_KEYWORD_Id + 1

const val PUBLIC_KEYWORD_Id: Int = PRIVATE_KEYWORD_Id + 1

const val INTERNAL_KEYWORD_Id: Int = PUBLIC_KEYWORD_Id + 1

const val PROTECTED_KEYWORD_Id: Int = INTERNAL_KEYWORD_Id + 1

const val OUT_KEYWORD_Id: Int = PROTECTED_KEYWORD_Id + 1

const val VARARG_KEYWORD_Id: Int = OUT_KEYWORD_Id + 1

const val REIFIED_KEYWORD_Id: Int = VARARG_KEYWORD_Id + 1

const val COMPANION_KEYWORD_Id: Int = REIFIED_KEYWORD_Id + 1

const val SEALED_KEYWORD_Id: Int = COMPANION_KEYWORD_Id + 1

const val FINAL_KEYWORD_Id: Int = SEALED_KEYWORD_Id + 1

const val LATEINIT_KEYWORD_Id: Int = FINAL_KEYWORD_Id + 1

const val DATA_KEYWORD_Id: Int = LATEINIT_KEYWORD_Id + 1

const val VALUE_KEYWORD_Id: Int = DATA_KEYWORD_Id + 1

const val INLINE_KEYWORD_Id: Int = VALUE_KEYWORD_Id + 1

const val NOINLINE_KEYWORD_Id: Int = INLINE_KEYWORD_Id + 1

const val TAILREC_KEYWORD_Id: Int = NOINLINE_KEYWORD_Id + 1

const val EXTERNAL_KEYWORD_Id: Int = TAILREC_KEYWORD_Id + 1

const val ANNOTATION_KEYWORD_Id: Int = EXTERNAL_KEYWORD_Id + 1

const val CROSSINLINE_KEYWORD_Id: Int = ANNOTATION_KEYWORD_Id + 1

const val OPERATOR_KEYWORD_Id: Int = CROSSINLINE_KEYWORD_Id + 1

const val INFIX_KEYWORD_Id: Int = OPERATOR_KEYWORD_Id + 1

const val CONST_KEYWORD_Id: Int = INFIX_KEYWORD_Id + 1

const val SUSPEND_KEYWORD_Id: Int = CONST_KEYWORD_Id + 1

const val EXPECT_KEYWORD_Id: Int = SUSPEND_KEYWORD_Id + 1

const val ACTUAL_KEYWORD_Id: Int = EXPECT_KEYWORD_Id + 1

const val INTERPOLATION_PREFIX_Id: Int = ACTUAL_KEYWORD_Id + 1

const val ALL_KEYWORD_Id: Int = INTERPOLATION_PREFIX_Id + 1

const val ERROR_KEYWORD_Id: Int = ALL_KEYWORD_Id + 1

const val OR_Id: Int = ERROR_KEYWORD_Id + 1

const val ERROR_SAFE_ACCESS_Id: Int = OR_Id + 1

val EOF: KtToken = KtToken("EOF",EOF_Id)

val RESERVED: KtToken = KtToken("RESERVED",RESERVED_Id)

val BLOCK_COMMENT: KtToken = KtToken("BLOCK_COMMENT",BLOCK_COMMENT_Id)

val EOL_COMMENT: KtToken = KtToken("EOL_COMMENT",EOL_COMMENT_Id)

val SHEBANG_COMMENT: KtToken = KtToken("SHEBANG_COMMENT",SHEBANG_COMMENT_Id)

val DOC_COMMENT: IElementType = KDocTokens.KDOC

val WHITE_SPACE: IElementType = TokenType.WHITE_SPACE

val INTEGER_LITERAL: KtToken = KtToken("INTEGER_LITERAL",INTEGER_LITERAL_Id)

val FLOAT_LITERAL: KtToken = KtToken("FLOAT_CONSTANT",FLOAT_LITERAL_Id)

val CHARACTER_LITERAL: KtToken = KtToken("CHARACTER_LITERAL",CHARACTER_LITERAL_Id)

val INTERPOLATION_PREFIX: KtToken = KtToken("INTERPOLATION_PREFIX",INTERPOLATION_PREFIX_Id)

val CLOSING_QUOTE: KtToken = KtToken("CLOSING_QUOTE",CLOSING_QUOTE_Id)

val OPEN_QUOTE: KtToken = KtToken("OPEN_QUOTE",OPEN_QUOTE_Id)

val REGULAR_STRING_PART: KtToken = KtToken("REGULAR_STRING_PART",REGULAR_STRING_PART_Id)

val ESCAPE_SEQUENCE: KtToken = KtToken("ESCAPE_SEQUENCE",ESCAPE_SEQUENCE_Id)

val SHORT_TEMPLATE_ENTRY_START: KtToken = KtToken("SHORT_TEMPLATE_ENTRY_START",SHORT_TEMPLATE_ENTRY_START_Id)

val LONG_TEMPLATE_ENTRY_START: KtToken = KtToken("LONG_TEMPLATE_ENTRY_START",LONG_TEMPLATE_ENTRY_START_Id)

val LONG_TEMPLATE_ENTRY_END: KtToken = KtToken("LONG_TEMPLATE_ENTRY_END",LONG_TEMPLATE_ENTRY_END_Id)

val DANGLING_NEWLINE: KtToken = KtToken("DANGLING_NEWLINE",DANGLING_NEWLINE_Id)

val PACKAGE_KEYWORD: KtKeywordToken = KtKeywordToken.keyword("package",PACKAGE_KEYWORD_Id)

val AS_KEYWORD: KtKeywordToken = KtKeywordToken.keyword("as",AS_KEYWORD_Id)

val TYPE_ALIAS_KEYWORD: KtKeywordToken = KtKeywordToken.keyword("typealias",TYPE_ALIAS_KEYWORD_Id)

val CLASS_KEYWORD: KtKeywordToken = KtKeywordToken.keyword("class",CLASS_KEYWORD_Id)

val THIS_KEYWORD: KtKeywordToken = KtKeywordToken.keyword("this",THIS_KEYWORD_Id)

val SUPER_KEYWORD: KtKeywordToken = KtKeywordToken.keyword("super",SUPER_KEYWORD_Id)

val VAL_KEYWORD: KtKeywordToken = KtKeywordToken.keyword("val",VAL_KEYWORD_Id)

val VAR_KEYWORD: KtKeywordToken = KtKeywordToken.keyword("var",VAR_KEYWORD_Id)

val FUN_KEYWORD: KtModifierKeywordToken = KtModifierKeywordToken.keywordModifier("fun",FUN_KEYWORD_Id)

val FOR_KEYWORD: KtKeywordToken = KtKeywordToken.keyword("for",FOR_KEYWORD_Id)

val NULL_KEYWORD: KtKeywordToken = KtKeywordToken.keyword("null",NULL_KEYWORD_Id)

val TRUE_KEYWORD: KtKeywordToken = KtKeywordToken.keyword("true",TRUE_KEYWORD_Id)

val FALSE_KEYWORD: KtKeywordToken = KtKeywordToken.keyword("false",FALSE_KEYWORD_Id)

val IS_KEYWORD: KtKeywordToken = KtKeywordToken.keyword("is",IS_KEYWORD_Id)

val IN_KEYWORD: KtModifierKeywordToken = KtModifierKeywordToken.keywordModifier("in",IN_KEYWORD_Id)

val THROW_KEYWORD: KtKeywordToken = KtKeywordToken.keyword("throw",THROW_KEYWORD_Id)

val RETURN_KEYWORD: KtKeywordToken = KtKeywordToken.keyword("return",RETURN_KEYWORD_Id)

val BREAK_KEYWORD: KtKeywordToken = KtKeywordToken.keyword("break",BREAK_KEYWORD_Id)

val CONTINUE_KEYWORD: KtKeywordToken = KtKeywordToken.keyword("continue",CONTINUE_KEYWORD_Id)

val OBJECT_KEYWORD: KtKeywordToken = KtKeywordToken.keyword("object",OBJECT_KEYWORD_Id)

val IF_KEYWORD: KtKeywordToken = KtKeywordToken.keyword("if",IF_KEYWORD_Id)

val TRY_KEYWORD: KtKeywordToken = KtKeywordToken.keyword("try",TRY_KEYWORD_Id)

val ELSE_KEYWORD: KtKeywordToken = KtKeywordToken.keyword("else",ELSE_KEYWORD_Id)

val WHILE_KEYWORD: KtKeywordToken = KtKeywordToken.keyword("while",WHILE_KEYWORD_Id)

val DO_KEYWORD: KtKeywordToken = KtKeywordToken.keyword("do",DO_KEYWORD_Id)

val WHEN_KEYWORD: KtKeywordToken = KtKeywordToken.keyword("when",WHEN_KEYWORD_Id)

val INTERFACE_KEYWORD: KtKeywordToken = KtKeywordToken.keyword("interface",INTERFACE_KEYWORD_Id)

val TYPEOF_KEYWORD: KtKeywordToken = KtKeywordToken.keyword("typeof",TYPEOF_KEYWORD_Id)

val AS_SAFE: KtToken = KtKeywordToken.keyword("AS_SAFE","as?",AS_SAFE_Id)

val IDENTIFIER: KtToken = KtToken("IDENTIFIER",IDENTIFIER_Id)

val FIELD_IDENTIFIER: KtToken = KtToken("FIELD_IDENTIFIER",FIELD_IDENTIFIER_Id)

val LBRACKET: KtSingleValueToken = KtSingleValueToken("LBRACKET","[",LBRACKET_Id)

val RBRACKET: KtSingleValueToken = KtSingleValueToken("RBRACKET","]",RBRACKET_Id)

val LBRACE: KtSingleValueToken = KtSingleValueToken("LBRACE","{",LBRACE_Id)

val RBRACE: KtSingleValueToken = KtSingleValueToken("RBRACE","}",RBRACE_Id)

val LPAR: KtSingleValueToken = KtSingleValueToken("LPAR","(",LPAR_Id)

val RPAR: KtSingleValueToken = KtSingleValueToken("RPAR",")",RPAR_Id)

val DOT: KtSingleValueToken = KtSingleValueToken("DOT",".",DOT_Id)

val PLUSPLUS: KtSingleValueToken = KtSingleValueToken("PLUSPLUS","++",PLUSPLUS_Id)

val MINUSMINUS: KtSingleValueToken = KtSingleValueToken("MINUSMINUS","--",MINUSMINUS_Id)

val MUL: KtSingleValueToken = KtSingleValueToken("MUL","*",MUL_Id)

val PLUS: KtSingleValueToken = KtSingleValueToken("PLUS","+",PLUS_Id)

val MINUS: KtSingleValueToken = KtSingleValueToken("MINUS","-",MINUS_Id)

val EXCL: KtSingleValueToken = KtSingleValueToken("EXCL","!",EXCL_Id)

val DIV: KtSingleValueToken = KtSingleValueToken("DIV","/",DIV_Id)

val PERC: KtSingleValueToken = KtSingleValueToken("PERC","%",PERC_Id)

val LT: KtSingleValueToken = KtSingleValueToken("LT","<",LT_Id)

val GT: KtSingleValueToken = KtSingleValueToken("GT",">",GT_Id)

val LTEQ: KtSingleValueToken = KtSingleValueToken("LTEQ","<=",LTEQ_Id)

val GTEQ: KtSingleValueToken = KtSingleValueToken("GTEQ",">=",GTEQ_Id)

val EQEQEQ: KtSingleValueToken = KtSingleValueToken("EQEQEQ","===",EQEQEQ_Id)

val ARROW: KtSingleValueToken = KtSingleValueToken("ARROW","->",ARROW_Id)

val DOUBLE_ARROW: KtSingleValueToken = KtSingleValueToken("DOUBLE_ARROW","=>",DOUBLE_ARROW_Id)

val EXCLEQEQEQ: KtSingleValueToken = KtSingleValueToken("EXCLEQEQEQ","!==",EXCLEQEQEQ_Id)

val EQEQ: KtSingleValueToken = KtSingleValueToken("EQEQ","==",EQEQ_Id)

val EXCLEQ: KtSingleValueToken = KtSingleValueToken("EXCLEQ","!=",EXCLEQ_Id)

val EXCLEXCL: KtSingleValueToken = KtSingleValueToken("EXCLEXCL","!!",EXCLEXCL_Id)

val ANDAND: KtSingleValueToken = KtSingleValueToken("ANDAND","&&",ANDAND_Id)

val AND: KtSingleValueToken = KtSingleValueToken("AND","&",AND_Id)

val OROR: KtSingleValueToken = KtSingleValueToken("OROR","||",OROR_Id)

val OR: KtSingleValueToken = KtSingleValueToken("OR","|",OR_Id)

val SAFE_ACCESS: KtSingleValueToken = KtSingleValueToken("SAFE_ACCESS","?.",SAFE_ACCESS_Id)

val ERROR_SAFE_ACCESS: KtSingleValueToken = KtSingleValueToken("ERROR_SAFE_ACCESS","|.",ERROR_SAFE_ACCESS_Id)

val ELVIS: KtSingleValueToken = KtSingleValueToken("ELVIS","?:",ELVIS_Id)

val QUEST: KtSingleValueToken = KtSingleValueToken("QUEST","?",QUEST_Id)

val COLONCOLON: KtSingleValueToken = KtSingleValueToken("COLONCOLON","::",COLONCOLON_Id)

val COLON: KtSingleValueToken = KtSingleValueToken("COLON",":",COLON_Id)

val SEMICOLON: KtSingleValueToken = KtSingleValueToken("SEMICOLON",";",SEMICOLON_Id)

val DOUBLE_SEMICOLON: KtSingleValueToken = KtSingleValueToken("DOUBLE_SEMICOLON",";;",DOUBLE_SEMICOLON_Id)

val RANGE: KtSingleValueToken = KtSingleValueToken("RANGE","..",RANGE_Id)

val RANGE_UNTIL: KtSingleValueToken = KtSingleValueToken("RANGE_UNTIL","..<",RANGE_UNTIL_Id)

val EQ: KtSingleValueToken = KtSingleValueToken("EQ","=",EQ_Id)

val MULTEQ: KtSingleValueToken = KtSingleValueToken("MULTEQ","*=",MULTEQ_Id)

val DIVEQ: KtSingleValueToken = KtSingleValueToken("DIVEQ","/=",DIVEQ_Id)

val PERCEQ: KtSingleValueToken = KtSingleValueToken("PERCEQ","%=",PERCEQ_Id)

val PLUSEQ: KtSingleValueToken = KtSingleValueToken("PLUSEQ","+=",PLUSEQ_Id)

val MINUSEQ: KtSingleValueToken = KtSingleValueToken("MINUSEQ","-=",MINUSEQ_Id)

val NOT_IN: KtKeywordToken = KtKeywordToken.keyword("NOT_IN","!in",NOT_IN_Id)

val NOT_IS: KtKeywordToken = KtKeywordToken.keyword("NOT_IS","!is",NOT_IS_Id)

val HASH: KtSingleValueToken = KtSingleValueToken("HASH","#",HASH_Id)

val AT: KtSingleValueToken = KtSingleValueToken("AT","@",AT_Id)

val COMMA: KtSingleValueToken = KtSingleValueToken("COMMA",",",COMMA_Id)

val EOL_OR_SEMICOLON: KtToken = KtToken("EOL_OR_SEMICOLON",EOL_OR_SEMICOLON_Id)

val ALL_KEYWORD: KtKeywordToken = KtKeywordToken.softKeyword("all",ALL_KEYWORD_Id)

val FILE_KEYWORD: KtKeywordToken = KtKeywordToken.softKeyword("file",FILE_KEYWORD_Id)

val FIELD_KEYWORD: KtKeywordToken = KtKeywordToken.softKeyword("field",FIELD_KEYWORD_Id)

val PROPERTY_KEYWORD: KtKeywordToken = KtKeywordToken.softKeyword("property",PROPERTY_KEYWORD_Id)

val RECEIVER_KEYWORD: KtKeywordToken = KtKeywordToken.softKeyword("receiver",RECEIVER_KEYWORD_Id)

val PARAM_KEYWORD: KtKeywordToken = KtKeywordToken.softKeyword("param",PARAM_KEYWORD_Id)

val SETPARAM_KEYWORD: KtKeywordToken = KtKeywordToken.softKeyword("setparam",SETPARAM_KEYWORD_Id)

val DELEGATE_KEYWORD: KtKeywordToken = KtKeywordToken.softKeyword("delegate",DELEGATE_KEYWORD_Id)

val IMPORT_KEYWORD: KtKeywordToken = KtKeywordToken.softKeyword("import",IMPORT_KEYWORD_Id)

val WHERE_KEYWORD: KtKeywordToken = KtKeywordToken.softKeyword("where",WHERE_KEYWORD_Id)

val BY_KEYWORD: KtKeywordToken = KtKeywordToken.softKeyword("by",BY_KEYWORD_Id)

val GET_KEYWORD: KtKeywordToken = KtKeywordToken.softKeyword("get",GET_KEYWORD_Id)

val SET_KEYWORD: KtKeywordToken = KtKeywordToken.softKeyword("set",SET_KEYWORD_Id)

val CONSTRUCTOR_KEYWORD: KtKeywordToken = KtKeywordToken.softKeyword("constructor",CONSTRUCTOR_KEYWORD_Id)

val INIT_KEYWORD: KtKeywordToken = KtKeywordToken.softKeyword("init",INIT_KEYWORD_Id)

val CONTEXT_KEYWORD: KtKeywordToken = KtKeywordToken.softKeyword("context",CONTEXT_KEYWORD_Id)

val CATCH_KEYWORD: KtKeywordToken = KtKeywordToken.softKeyword("catch",CATCH_KEYWORD_Id)

val DYNAMIC_KEYWORD: KtKeywordToken = KtKeywordToken.softKeyword("dynamic",DYNAMIC_KEYWORD_Id)

val FINALLY_KEYWORD: KtKeywordToken = KtKeywordToken.softKeyword("finally",FINALLY_KEYWORD_Id)

val ABSTRACT_KEYWORD: KtModifierKeywordToken = KtModifierKeywordToken.softKeywordModifier("abstract",ABSTRACT_KEYWORD_Id)

val ENUM_KEYWORD: KtModifierKeywordToken = KtModifierKeywordToken.softKeywordModifier("enum",ENUM_KEYWORD_Id)

val CONTRACT_KEYWORD: KtModifierKeywordToken = KtModifierKeywordToken.softKeywordModifier("contract",CONTRACT_KEYWORD_Id)

val OPEN_KEYWORD: KtModifierKeywordToken = KtModifierKeywordToken.softKeywordModifier("open",OPEN_KEYWORD_Id)

val INNER_KEYWORD: KtModifierKeywordToken = KtModifierKeywordToken.softKeywordModifier("inner",INNER_KEYWORD_Id)

val OVERRIDE_KEYWORD: KtModifierKeywordToken = KtModifierKeywordToken.softKeywordModifier("override",OVERRIDE_KEYWORD_Id)

val PRIVATE_KEYWORD: KtModifierKeywordToken = KtModifierKeywordToken.softKeywordModifier("private",PRIVATE_KEYWORD_Id)

val PUBLIC_KEYWORD: KtModifierKeywordToken = KtModifierKeywordToken.softKeywordModifier("public",PUBLIC_KEYWORD_Id)

val INTERNAL_KEYWORD: KtModifierKeywordToken = KtModifierKeywordToken.softKeywordModifier("internal",INTERNAL_KEYWORD_Id)

val PROTECTED_KEYWORD: KtModifierKeywordToken = KtModifierKeywordToken.softKeywordModifier("protected",PROTECTED_KEYWORD_Id)

val OUT_KEYWORD: KtModifierKeywordToken = KtModifierKeywordToken.softKeywordModifier("out",OUT_KEYWORD_Id)

val VARARG_KEYWORD: KtModifierKeywordToken = KtModifierKeywordToken.softKeywordModifier("vararg",VARARG_KEYWORD_Id)

val REIFIED_KEYWORD: KtModifierKeywordToken = KtModifierKeywordToken.softKeywordModifier("reified",REIFIED_KEYWORD_Id)

val COMPANION_KEYWORD: KtModifierKeywordToken = KtModifierKeywordToken.softKeywordModifier("companion",COMPANION_KEYWORD_Id)

val SEALED_KEYWORD: KtModifierKeywordToken = KtModifierKeywordToken.softKeywordModifier("sealed",SEALED_KEYWORD_Id)

val ERROR_KEYWORD: KtModifierKeywordToken = KtModifierKeywordToken.softKeywordModifier("error",ERROR_KEYWORD_Id)

val DEFAULT_VISIBILITY_KEYWORD: KtModifierKeywordToken = PUBLIC_KEYWORD

val FINAL_KEYWORD: KtModifierKeywordToken = KtModifierKeywordToken.softKeywordModifier("final",FINAL_KEYWORD_Id)

val LATEINIT_KEYWORD: KtModifierKeywordToken = KtModifierKeywordToken.softKeywordModifier("lateinit",LATEINIT_KEYWORD_Id)

val DATA_KEYWORD: KtModifierKeywordToken = KtModifierKeywordToken.softKeywordModifier("data",DATA_KEYWORD_Id)

val VALUE_KEYWORD: KtModifierKeywordToken = KtModifierKeywordToken.softKeywordModifier("value",VALUE_KEYWORD_Id)

val INLINE_KEYWORD: KtModifierKeywordToken = KtModifierKeywordToken.softKeywordModifier("inline",INLINE_KEYWORD_Id)

val NOINLINE_KEYWORD: KtModifierKeywordToken = KtModifierKeywordToken.softKeywordModifier("noinline",NOINLINE_KEYWORD_Id)

val TAILREC_KEYWORD: KtModifierKeywordToken = KtModifierKeywordToken.softKeywordModifier("tailrec",TAILREC_KEYWORD_Id)

val EXTERNAL_KEYWORD: KtModifierKeywordToken = KtModifierKeywordToken.softKeywordModifier("external",EXTERNAL_KEYWORD_Id)

val ANNOTATION_KEYWORD: KtModifierKeywordToken = KtModifierKeywordToken.softKeywordModifier("annotation",ANNOTATION_KEYWORD_Id)

val CROSSINLINE_KEYWORD: KtModifierKeywordToken = KtModifierKeywordToken.softKeywordModifier("crossinline",CROSSINLINE_KEYWORD_Id)

val OPERATOR_KEYWORD: KtModifierKeywordToken = KtModifierKeywordToken.softKeywordModifier("operator",OPERATOR_KEYWORD_Id)

val INFIX_KEYWORD: KtModifierKeywordToken = KtModifierKeywordToken.softKeywordModifier("infix",INFIX_KEYWORD_Id)

val CONST_KEYWORD: KtModifierKeywordToken = KtModifierKeywordToken.softKeywordModifier("const",CONST_KEYWORD_Id)

val SUSPEND_KEYWORD: KtModifierKeywordToken = KtModifierKeywordToken.softKeywordModifier("suspend",SUSPEND_KEYWORD_Id)

val EXPECT_KEYWORD: KtModifierKeywordToken = KtModifierKeywordToken.softKeywordModifier("expect",EXPECT_KEYWORD_Id)

val ACTUAL_KEYWORD: KtModifierKeywordToken = KtModifierKeywordToken.softKeywordModifier("actual",ACTUAL_KEYWORD_Id)

val KEYWORDS: TokenSet = TokenSet.create(PACKAGE_KEYWORD,AS_KEYWORD,TYPE_ALIAS_KEYWORD,CLASS_KEYWORD,INTERFACE_KEYWORD,THIS_KEYWORD,SUPER_KEYWORD,VAL_KEYWORD,VAR_KEYWORD,FUN_KEYWORD,FOR_KEYWORD,NULL_KEYWORD,TRUE_KEYWORD,FALSE_KEYWORD,IS_KEYWORD,IN_KEYWORD,THROW_KEYWORD,RETURN_KEYWORD,BREAK_KEYWORD,CONTINUE_KEYWORD,OBJECT_KEYWORD,IF_KEYWORD,ELSE_KEYWORD,WHILE_KEYWORD,DO_KEYWORD,TRY_KEYWORD,WHEN_KEYWORD,NOT_IN,NOT_IS,AS_SAFE,TYPEOF_KEYWORD)

val SOFT_KEYWORDS: TokenSet = TokenSet.create(FILE_KEYWORD,IMPORT_KEYWORD,WHERE_KEYWORD,BY_KEYWORD,GET_KEYWORD,SET_KEYWORD,ABSTRACT_KEYWORD,ENUM_KEYWORD,CONTRACT_KEYWORD,OPEN_KEYWORD,INNER_KEYWORD,OVERRIDE_KEYWORD,PRIVATE_KEYWORD,PUBLIC_KEYWORD,INTERNAL_KEYWORD,PROTECTED_KEYWORD,CATCH_KEYWORD,FINALLY_KEYWORD,OUT_KEYWORD,FINAL_KEYWORD,VARARG_KEYWORD,REIFIED_KEYWORD,DYNAMIC_KEYWORD,COMPANION_KEYWORD,CONSTRUCTOR_KEYWORD,INIT_KEYWORD,SEALED_KEYWORD,FIELD_KEYWORD,PROPERTY_KEYWORD,RECEIVER_KEYWORD,PARAM_KEYWORD,SETPARAM_KEYWORD,DELEGATE_KEYWORD,ALL_KEYWORD,LATEINIT_KEYWORD,DATA_KEYWORD,INLINE_KEYWORD,NOINLINE_KEYWORD,TAILREC_KEYWORD,EXTERNAL_KEYWORD,ANNOTATION_KEYWORD,CROSSINLINE_KEYWORD,CONST_KEYWORD,OPERATOR_KEYWORD,INFIX_KEYWORD,SUSPEND_KEYWORD,EXPECT_KEYWORD,ACTUAL_KEYWORD,VALUE_KEYWORD,CONTEXT_KEYWORD,ERROR_KEYWORD)

val MODIFIER_KEYWORDS_ARRAY: Array<KtModifierKeywordToken> = arrayOf<KtModifierKeywordToken>(PUBLIC_KEYWORD,PROTECTED_KEYWORD,PRIVATE_KEYWORD,INTERNAL_KEYWORD,EXPECT_KEYWORD,ACTUAL_KEYWORD,FINAL_KEYWORD,OPEN_KEYWORD,ABSTRACT_KEYWORD,SEALED_KEYWORD,CONST_KEYWORD,EXTERNAL_KEYWORD,OVERRIDE_KEYWORD,LATEINIT_KEYWORD,TAILREC_KEYWORD,VARARG_KEYWORD,SUSPEND_KEYWORD,INNER_KEYWORD,ENUM_KEYWORD,ANNOTATION_KEYWORD,FUN_KEYWORD,COMPANION_KEYWORD,INLINE_KEYWORD,VALUE_KEYWORD,INFIX_KEYWORD,OPERATOR_KEYWORD,DATA_KEYWORD,ERROR_KEYWORD,OUT_KEYWORD,IN_KEYWORD,REIFIED_KEYWORD,NOINLINE_KEYWORD,CROSSINLINE_KEYWORD,)

val MODIFIER_KEYWORDS: TokenSet = TokenSet.create(*MODIFIER_KEYWORDS_ARRAY)

val TYPE_MODIFIER_KEYWORDS: TokenSet = TokenSet.create(SUSPEND_KEYWORD)

val TYPE_ARGUMENT_MODIFIER_KEYWORDS: TokenSet = TokenSet.create(IN_KEYWORD,OUT_KEYWORD)

val RESERVED_VALUE_PARAMETER_MODIFIER_KEYWORDS: TokenSet = TokenSet.create(OUT_KEYWORD,VARARG_KEYWORD)

val VISIBILITY_MODIFIERS: TokenSet = TokenSet.create(PRIVATE_KEYWORD,PUBLIC_KEYWORD,INTERNAL_KEYWORD,PROTECTED_KEYWORD)

val MODALITY_MODIFIERS: TokenSet = TokenSet.create(ABSTRACT_KEYWORD,FINAL_KEYWORD,SEALED_KEYWORD,OPEN_KEYWORD)

val WHITESPACES: TokenSet = TokenSet.create(TokenType.WHITE_SPACE)

val COMMENTS: TokenSet = TokenSet.create(EOL_COMMENT,BLOCK_COMMENT,DOC_COMMENT,SHEBANG_COMMENT)

val WHITE_SPACE_OR_COMMENT_BIT_SET: TokenSet = TokenSet.orSet(COMMENTS,WHITESPACES)

val STRINGS: TokenSet = TokenSet.create(CHARACTER_LITERAL,REGULAR_STRING_PART)

val OPERATIONS: TokenSet = TokenSet.create(AS_KEYWORD,AS_SAFE,IS_KEYWORD,IN_KEYWORD,DOT,PLUSPLUS,MINUSMINUS,EXCLEXCL,MUL,PLUS,MINUS,EXCL,DIV,PERC,LT,GT,LTEQ,GTEQ,EQEQEQ,EXCLEQEQEQ,EQEQ,EXCLEQ,ANDAND,OROR,SAFE_ACCESS,ERROR_SAFE_ACCESS,ELVIS,RANGE,RANGE_UNTIL,EQ,MULTEQ,DIVEQ,PERCEQ,PLUSEQ,MINUSEQ,NOT_IN,NOT_IS,IDENTIFIER)

val AUGMENTED_ASSIGNMENTS: TokenSet = TokenSet.create(PLUSEQ,MINUSEQ,MULTEQ,PERCEQ,DIVEQ)

val ALL_ASSIGNMENTS: TokenSet = TokenSet.create(EQ,PLUSEQ,MINUSEQ,MULTEQ,PERCEQ,DIVEQ)

val INCREMENT_AND_DECREMENT: TokenSet = TokenSet.create(PLUSPLUS,MINUSMINUS)

val QUALIFIED_ACCESS: TokenSet = TokenSet.create(DOT_QUALIFIED_EXPRESSION,SAFE_ACCESS_EXPRESSION,ERROR_SAFE_ACCESS_EXPRESSION)

val VAL_VAR: TokenSet = TokenSet.create(VAL_KEYWORD,VAR_KEYWORD)

val ALL_SAFE_ACCESSES: TokenSet = TokenSet.create(SAFE_ACCESS,ERROR_SAFE_ACCESS)
}
}
