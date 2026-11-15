// Copyright 2000-2025 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.platform.syntax.util.runtime

import com.intellij.platform.syntax.i18n.ResourceBundle

object SyntaxRuntimeBundle {
  private const val BUNDLE: String = "messages.SyntaxRuntimeBundle"
    
  private val bundle = ResourceBundle("com.intellij.platform.syntax.util.runtime.SyntaxRuntimeBundle", BUNDLE, this, emptyMap())
  
  fun message(key: String, vararg params: Any): String {
    return bundle.message(key, *params)
  }
  
  fun messagePointer(key: String, vararg params: Any): () -> String {
    return bundle.messagePointer(key, *params)
  }
}