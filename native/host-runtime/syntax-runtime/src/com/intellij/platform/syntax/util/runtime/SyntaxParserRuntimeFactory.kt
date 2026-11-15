// Copyright 2000-2025 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.platform.syntax.util.runtime

import com.intellij.platform.syntax.parser.SyntaxTreeBuilder

interface SyntaxParserRuntimeFactory {
  fun buildParserRuntime(builder: SyntaxTreeBuilder, extendedState: SyntaxGeneratedParserRuntime.ParserUserState? = null): SyntaxGeneratedParserRuntime
}