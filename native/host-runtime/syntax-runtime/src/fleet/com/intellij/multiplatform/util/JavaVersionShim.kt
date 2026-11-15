// Copyright 2000-2025 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package fleet.com.intellij.multiplatform.util

import fleet.com.intellij.multiplatform.util.lang.JavaVersion

/**
 * see implementation [fleet.com.intellij.multiplatform.util.currentJavaVersionPlatformSpecificJvm]
 */
internal fun currentJavaVersionPlatformSpecific(): JavaVersion = currentJavaVersionPlatformSpecificNative()
