/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.types.expressions

import org.jetbrains.kotlin.psi.expressions
import org.jetbrains.kotlin.psi.operationToken

import org.jetbrains.kotlin.util.NativeImmutableBiMap as ImmutableBiMap
import org.jetbrains.kotlin.util.NativeImmutableSet as ImmutableSet
import org.jetbrains.kotlin.util.OperatorNameConventions.AND
import org.jetbrains.kotlin.util.OperatorNameConventions.ASSIGNMENT_OPERATIONS
import org.jetbrains.kotlin.util.OperatorNameConventions.BINARY_OPERATION_NAMES
import org.jetbrains.kotlin.util.OperatorNameConventions.COMPONENT_REGEX
import org.jetbrains.kotlin.util.OperatorNameConventions.NUMBER_CONVERSIONS
import org.jetbrains.kotlin.util.OperatorNameConventions.OR
import org.jetbrains.kotlin.util.OperatorNameConventions.TO_BYTE
import org.jetbrains.kotlin.util.OperatorNameConventions.TO_CHAR
import org.jetbrains.kotlin.util.OperatorNameConventions.TO_DOUBLE
import org.jetbrains.kotlin.util.OperatorNameConventions.TO_FLOAT
import org.jetbrains.kotlin.util.OperatorNameConventions.TO_INT
import org.jetbrains.kotlin.util.OperatorNameConventions.TO_LONG
import org.jetbrains.kotlin.util.OperatorNameConventions.TO_SHORT
import org.jetbrains.kotlin.util.OperatorNameConventions.UNARY_OPERATION_NAMES






import org.jetbrains.kotlin.lexer.KtSingleValueToken
import org.jetbrains.kotlin.lexer.KtToken
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.utils.OperatorTokens



open class OperatorConventions {
private constructor() {


}

companion object {
val DOUBLE: Name = TO_DOUBLE

val FLOAT: Name = TO_FLOAT

val LONG: Name = TO_LONG

val INT: Name = TO_INT

val CHAR: Name = TO_CHAR

val SHORT: Name = TO_SHORT

val BYTE: Name = TO_BYTE

val NUMBER_CONVERSIONS: ImmutableSet<Name> = ImmutableSet.of(DOUBLE,FLOAT,LONG,INT,SHORT,BYTE,CHAR)

val UNARY_OPERATION_NAMES: ImmutableBiMap<KtSingleValueToken,Name> = ImmutableBiMap.copyOf(OperatorTokens.UNARY_OPERATION_NAMES)

val BINARY_OPERATION_NAMES: ImmutableBiMap<KtSingleValueToken,Name> = ImmutableBiMap.copyOf(OperatorTokens.BINARY_OPERATION_NAMES)

val NOT_OVERLOADABLE: ImmutableSet<KtSingleValueToken> = ImmutableSet.of(KtTokens.ANDAND,KtTokens.OROR,KtTokens.ELVIS,KtTokens.EQEQEQ,KtTokens.EXCLEQEQEQ)

val INCREMENT_OPERATIONS: ImmutableSet<KtSingleValueToken> = ImmutableSet.copyOf(OperatorTokens.INCREMENT_OPERATIONS)

val COMPARISON_OPERATIONS: ImmutableSet<KtSingleValueToken> = ImmutableSet.copyOf(OperatorTokens.COMPARISON_OPERATIONS)

val EQUALS_OPERATIONS: ImmutableSet<KtSingleValueToken> = ImmutableSet.copyOf(OperatorTokens.EQUALS_OPERATIONS)

val IDENTITY_EQUALS_OPERATIONS: ImmutableSet<KtSingleValueToken> = ImmutableSet.copyOf(OperatorTokens.IDENTITY_EQUALS_OPERATIONS)

val IN_OPERATIONS: ImmutableSet<KtSingleValueToken> = ImmutableSet.copyOf(OperatorTokens.IN_OPERATIONS)

val ASSIGNMENT_OPERATIONS: ImmutableBiMap<KtSingleValueToken,Name> = ImmutableBiMap.copyOf(OperatorTokens.ASSIGNMENT_OPERATION_NAMES)

val ASSIGNMENT_OPERATION_COUNTERPARTS: ImmutableBiMap<KtSingleValueToken,KtSingleValueToken> = ImmutableBiMap.copyOf(OperatorTokens.OPERATIONS_FOR_ASSIGNMENTS)

val ASSIGN_METHOD: Name = Name.identifier("assign")

val BOOLEAN_OPERATIONS: ImmutableBiMap<KtSingleValueToken,Name> = ImmutableBiMap.builder<KtSingleValueToken,Name>().put(KtTokens.ANDAND,AND).put(KtTokens.OROR,OR).build()

val CONVENTION_NAMES: ImmutableSet<Name> = ImmutableSet.copyOf(OperatorTokens.CONVENTION_NAMES)

fun getNameForOperationSymbol(token: KtToken): Name? {

return OperatorTokens.operationName(token)
}

fun getNameForOperationSymbol(token: KtToken, unaryOperations: Boolean, binaryOperations: Boolean): Name? {

return OperatorTokens.operationName(token,unaryOperations,binaryOperations)
}

fun getOperationSymbolForName(name: Name): KtToken? {

return OperatorTokens.operationToken(name)
}

fun isConventionName(name: Name): Boolean {

return CONVENTION_NAMES.contains(name) || COMPONENT_REGEX.matches(name.asString())
}
}
}
