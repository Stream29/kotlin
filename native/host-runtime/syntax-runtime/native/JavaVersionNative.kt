// Copyright 2000-2025 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package fleet.com.intellij.multiplatform.util

import fleet.com.intellij.multiplatform.util.lang.JavaVersion

/** A Native process has no current JVM; installed JDKs do not change the host runtime. */
internal fun currentJavaVersionPlatformSpecificNative(): JavaVersion =
    throw UnsupportedOperationException("JavaVersion.current() is not defined on a Kotlin/Native host")
