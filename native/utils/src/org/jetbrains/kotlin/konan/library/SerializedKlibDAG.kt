/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.konan.library

import org.jetbrains.kotlin.library.KlibPath as Path
import org.jetbrains.kotlin.library.KotlinLibrary

data class SerializedKlibDAG(val dag: Map<Path, Set<Path>>) {
    init {
        for ((libraryPath: Path, directDependencyPaths: Set<Path>) in dag.entries) {
            for (directDependency in directDependencyPaths) {
                check(directDependency in dag) {
                    "There is a direct dependency $directDependency of library $libraryPath that is not in DAG"
                }
            }
        }
    }
}
