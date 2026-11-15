/*
 * Copyright 2010-2015 JetBrains s.r.o.
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

// STATUS: 机械转译未验证 (Translated-Unverified) - 尚未经 Native 编译与差分验证

package org.jetbrains.kotlin.descriptors

interface DeclarationDescriptorWithVisibility : DeclarationDescriptor {
    fun getVisibility(): DescriptorVisibility
}

val DeclarationDescriptorWithVisibility.visibility: DescriptorVisibility
    get() = getVisibility()
