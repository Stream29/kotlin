// Copyright 2000-2025 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package fleet.com.intellij.multiplatform.openapi.util


@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class NlsContext(
  /**
   * Provide a neat property key prefix that unambiguously defines literal usage context.
   * E.g. "button", "button.tooltip" for button text and tooltip correspondingly, "action.text" for action text
   */
  val prefix: String = "",

  /**
   * Provide a neat property key suffix that unambiguously defines literal usage context.
   * E.g. "description" for action/intention description
   */
  val suffix: String = "",
)