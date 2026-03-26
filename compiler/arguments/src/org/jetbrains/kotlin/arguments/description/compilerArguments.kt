/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.arguments.description

import org.jetbrains.kotlin.arguments.description.removed.*
import org.jetbrains.kotlin.arguments.dsl.base.Modifier
import org.jetbrains.kotlin.arguments.dsl.base.compilerArguments

val kotlinCompilerArguments = compilerArguments {
    topLevel(
        name = CompilerArgumentsLevelNames.commonToolArguments,
        mergeWith = setOf(actualCommonToolsArguments, removedCommonToolsArguments)
    ) {
        subLevel(
            name = CompilerArgumentsLevelNames.commonCompilerArguments,
            mergeWith = setOf(actualCommonCompilerArguments, removedCommonCompilerArguments)
        ) {
            subLevel(
                name = CompilerArgumentsLevelNames.jvmCompilerArguments,
                mergeWith = setOf(actualJvmCompilerArguments, removedJvmCompilerArguments)
            ) {}
            subLevel(
                name = CompilerArgumentsLevelNames.commonKlibBasedArguments,
                mergeWith = setOf(
                    actualCommonKlibBasedArguments,
                    actualCommonKlibBasedArgumentsKlibStage,
                    actualCommonKlibBasedArgumentsLinkingStage,
                    removedCommonKlibBasedCompilerArguments
                )
            ) {
                subLevel(
                    name = CompilerArgumentsLevelNames.commonJsAndWasmArguments,
                    mergeWith = setOf(
                        actualCommonJsAndWasmArguments,
                        actualCommonJsAndWasmArgumentsKlibStage,
                        actualCommonJsAndWasmArgumentsLinkingStage
                    )
                ) {
                    modifier(Modifier.SEALED)
                    subLevel(
                        name = CompilerArgumentsLevelNames.legacyWasmArguments,
                        mergeWith = setOf(actualWasmArguments, actualWasmArgumentsKlibStage, actualWasmArgumentsLinkingStage, removedWasmArguments)
                    ) {
                        modifier(Modifier.DEPRECATED)
                        modifier(Modifier.SEALED)
                        subLevel(
                            name = CompilerArgumentsLevelNames.jsArguments,
                            mergeWith = setOf(
                                actualJsArguments,
                                actualJsArgumentsKlibStage,
                                actualJsArgumentsLinkingStage,
                                removedJsArguments
                            )
                        ) {}
                    }
                    subLevel(
                        name = CompilerArgumentsLevelNames.wasmArguments,
                        mergeWith = setOf(
                            actualWasmArguments,
                            actualWasmArgumentsKlibStage,
                            actualWasmArgumentsLinkingStage,
                            removedWasmArguments
                        )
                    ) {}
                }
                subLevel(
                    name = CompilerArgumentsLevelNames.nativeArguments,
                    mergeWith = setOf(actualNativeArguments, removedNativeArguments)
                ) {}
            }
            subLevel(
                name = CompilerArgumentsLevelNames.metadataArguments,
                mergeWith = setOf(actualMetadataArguments, removedMetadataArguments)
            ) {}
        }
    }
}
