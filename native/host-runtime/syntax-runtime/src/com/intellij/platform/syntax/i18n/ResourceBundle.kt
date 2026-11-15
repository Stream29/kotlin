// Copyright 2000-2025 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package com.intellij.platform.syntax.i18n


interface ResourceBundle {
  fun message(key: String, vararg params: Any): String

  fun messagePointer(key: String, vararg params: Any): () -> String
}

/**
 * see [com.intellij.platform.syntax.i18n.JvmResourceBundleKt.ResourceBundleJvm]
 *
 * @param bundleClass       the class of bundle, usually an object class where the bundle is declared
 * @param pathToBundle      usually, something like `messages.MyBundle`
 * @param self              pass `this` please
 * @param defaultMapping    default mapping to use. Can be generated from your .properties file with generateBundleMapping.kt
 */
@Suppress("unused")
fun ResourceBundle(
  bundleClass: String,
  pathToBundle: String,
  self: Any,
  defaultMapping: Map<String, String>,
): ResourceBundle = ResourceBundleNative(bundleClass, pathToBundle, self, defaultMapping)