/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

// STATUS: 机械转译未验证 (Translated-Unverified) - 尚未经 Native 编译与差分验证

package org.jetbrains.kotlin.descriptors

interface ValidateableDescriptor {
    fun validate() {}
}
