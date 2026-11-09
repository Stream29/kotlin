/*
 * Copyright 2010-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.psi

import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.resolve.ImportPath



open class KtImportsFactory {
private val project: Project

private val importsCache: MutableMap<ImportPath,KtImportDirective> = HashMap()

constructor(project: Project) {

this.project = project
}

private fun createImportDirective(importPath: ImportPath): KtImportDirective {

var directive: KtImportDirective? = importsCache.get(importPath)
if (directive != null) {
return directive
}
var createdDirective: KtImportDirective = KtPsiFactory(project,false).createImportDirective(importPath)
importsCache.put(importPath,createdDirective)
return createdDirective
}

open fun createImportDirectives(importPaths: MutableCollection<ImportPath>): Collection<KtImportDirective> {

return importPaths.map(this::createImportDirective)
}

open fun createImportDirectivesNotCached(importPaths: MutableCollection<ImportPath>): Collection<KtImportDirective> {

return KtPsiFactory(project,false).createImportDirectives(importPaths)
}
}
