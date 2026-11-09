/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea

import com.intellij.openapi.application.ApplicationManager
import javax.swing.*



abstract class KotlinIconProviderService {
abstract fun getFileIcon(): Icon

abstract fun getBuiltInFileIcon(): Icon

open class CompilerKotlinFileIconProviderService : KotlinIconProviderService {
override fun getFileIcon(): Icon? {

return null
}

override fun getBuiltInFileIcon(): Icon? {

return null
}
}

companion object {
fun getInstance(): KotlinIconProviderService {

var service: KotlinIconProviderService = ApplicationManager.getApplication().getService(KotlinIconProviderService ::class)
return (if (service != null) service else CompilerKotlinFileIconProviderService())
}
}
}
