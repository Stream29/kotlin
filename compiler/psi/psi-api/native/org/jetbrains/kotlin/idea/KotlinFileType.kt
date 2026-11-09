/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea

import com.intellij.openapi.fileTypes.LanguageFileType



open class KotlinFileType : LanguageFileType {

private constructor() : super(KotlinLanguage.INSTANCE) {


}

override val name: String
get() {

return KotlinLanguage.NAME
}

override val description: String
get() {

return name
}

override val defaultExtension: String
get() {

return EXTENSION
}



companion object {
const val EXTENSION: String = "kt"

const val DOT_DEFAULT_EXTENSION: String = "." + EXTENSION

const val SCRIPT_EXTENSION: String = "kts"

const val DOT_SCRIPT_EXTENSION: String = "." + SCRIPT_EXTENSION

val INSTANCE: KotlinFileType = KotlinFileType()
}
}
