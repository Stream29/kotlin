/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import org.jetbrains.kotlin.resolution.KtResolvableCall



interface KtCallElement : KtElement, KtResolvableCall {
fun getCalleeExpression(): KtExpression?

fun getValueArgumentList(): KtValueArgumentList?

fun getValueArguments(): List<out ValueArgument>

fun getLambdaArguments(): List<KtLambdaArgument>

fun getTypeArguments(): List<KtTypeProjection>

fun getTypeArgumentList(): KtTypeArgumentList?
}
