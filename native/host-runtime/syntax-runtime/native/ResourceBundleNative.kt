// Copyright 2000-2025 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.platform.syntax.i18n

/**
 * Native implementation of [ResourceBundle].
 * Uses upstream [BaseResourceBundle] mapping and message formatting algorithm.
 */
internal fun ResourceBundleNative(
  bundleClass: String,
  pathToBundle: String,
  self: Any,
  defaultMapping: Map<String, String>,
): ResourceBundle = BaseResourceBundle(defaultMapping)
