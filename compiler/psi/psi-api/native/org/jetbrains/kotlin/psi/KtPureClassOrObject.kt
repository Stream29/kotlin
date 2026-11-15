/*
 * Copyright 2010-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.psi




interface KtPureClassOrObject : KtPureElement, KtDeclarationContainer {
val name: String?

fun isLocal(): Boolean

fun getSuperTypeListEntries(): List<KtSuperTypeListEntry>

fun getCompanionObjects(): List<KtObjectDeclaration>

fun hasExplicitPrimaryConstructor(): Boolean

fun hasPrimaryConstructor(): Boolean

fun getPrimaryConstructor(): KtPrimaryConstructor?

fun getPrimaryConstructorModifierList(): KtModifierList?

fun getPrimaryConstructorParameters(): List<KtParameter>

fun getSecondaryConstructors(): List<KtSecondaryConstructor>

fun getContextReceivers(): List<KtContextReceiver>

fun getBody(): KtClassBody?
}
