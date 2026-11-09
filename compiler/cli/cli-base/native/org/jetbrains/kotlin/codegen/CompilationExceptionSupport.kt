/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("PackageDirectoryMismatch")

package org.jetbrains.kotlin.codegen

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.utils.KotlinExceptionWithAttachments

class CompilationException(
    message: String,
    cause: Throwable?,
    val element: PsiElement?,
) : KotlinExceptionWithAttachments(message, cause)
