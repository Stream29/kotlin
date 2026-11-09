/*
 * Copyright 2010-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

package org.jetbrains.kotlin.backend.konan

import org.jetbrains.kotlin.analyzer.ModuleInfo
import org.jetbrains.kotlin.backend.konan.driver.phases.FrontendContext
import org.jetbrains.kotlin.backend.konan.objcexport.ObjCExportProblemCollector
import org.jetbrains.kotlin.config.nativeBinaryOptions.BinaryOptions
import org.jetbrains.kotlin.config.nativeBinaryOptions.UnitSuspendFunctionObjCExport
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.konan.config.emitLazyObjcHeaderFile
import org.jetbrains.kotlin.konan.config.objcGenerics
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.K1Deprecation

class DeprecationResolver {
    fun getDeprecations(@Suppress("UNUSED_PARAMETER") descriptor: CallableMemberDescriptor): List<Any> = emptyList()
}

interface ObjCExportLazy {
    interface Configuration {
        val frameworkName: String
        fun isIncluded(moduleInfo: ModuleInfo): Boolean
        fun getCompilerModuleName(moduleInfo: ModuleInfo): String
        val objcGenerics: Boolean
        val objcExportBlockExplicitParameterNames: Boolean
        val disableSwiftMemberNameMangling: Boolean
        val unitSuspendFunctionExport: UnitSuspendFunctionObjCExport
        val ignoreInterfaceMethodCollisions: Boolean
    }

    fun dumpObjCHeader(files: Collection<KtFile>, outputFile: String, shouldExportKDoc: Boolean)
}

@OptIn(K1Deprecation::class)
internal fun KonanComponentProvider.postprocessComponents(context: FrontendContext, @Suppress("UNUSED_PARAMETER") files: Collection<KtFile>) {
    context.frontendServices = this.frontendServices
}

@OptIn(K1Deprecation::class)
class FrontendServices(val deprecationResolver: DeprecationResolver)