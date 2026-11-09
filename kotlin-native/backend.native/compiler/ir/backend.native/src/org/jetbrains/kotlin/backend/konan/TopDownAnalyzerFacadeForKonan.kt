/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

package org.jetbrains.kotlin.backend.konan

import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.backend.konan.driver.phases.FrontendContext
import org.jetbrains.kotlin.builtins.*
import org.jetbrains.kotlin.builtins.functions.functionInterfacePackageFragmentProvider
import org.jetbrains.kotlin.builtins.konan.KonanBuiltIns
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.impl.ModuleDependenciesImpl
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl
import org.jetbrains.kotlin.descriptors.name
import org.jetbrains.kotlin.konan.file.File as KlibFile
import org.jetbrains.kotlin.konan.file.canonicalPath
import org.jetbrains.kotlin.library.metadata.CurrentKlibModuleOrigin
import org.jetbrains.kotlin.library.metadata.KlibMetadataFactories
import org.jetbrains.kotlin.library.metadata.NullFlexibleTypeDeserializer
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.storage.LockBasedStorageManager
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.K1Deprecation

interface BindingContext {
    companion object {
        val EMPTY: CleanableBindingContext = object : CleanableBindingContext {
            override fun clear() {}
        }
    }
}

interface CleanableBindingContext : BindingContext {
    fun clear()
}

interface BindingTrace {
    val bindingContext: BindingContext
}

class BindingTraceContext(@Suppress("UNUSED_PARAMETER") val project: Project? = null) : BindingTrace {
    override val bindingContext: CleanableBindingContext = object : CleanableBindingContext {
        override fun clear() {}
    }
}

open class AnalysisResult protected constructor(
        val bindingContext: BindingContext,
        val moduleDescriptor: ModuleDescriptor,
        val shouldGenerateCode: Boolean = true
) {
    open fun isError(): Boolean = false

    class RetryWithAdditionalRoots(
            bindingContext: BindingContext,
            moduleDescriptor: ModuleDescriptor,
            val additionalKotlinRoots: List<KlibFile>
    ) : AnalysisResult(bindingContext, moduleDescriptor, true)

    companion object {
        fun success(
                bindingContext: BindingContext,
                module: ModuleDescriptor,
                shouldGenerateCode: Boolean = true
        ): AnalysisResult = AnalysisResult(bindingContext, module, shouldGenerateCode)
    }
}

class ProjectContext(val project: Project, debugName: String) {
    val storageManager: StorageManager = LockBasedStorageManager(debugName)
}

interface ModuleContext {
    val module: ModuleDescriptor
    val project: Project
    val storageManager: StorageManager
}

class MutableModuleContextImpl(
        override val module: ModuleDescriptorImpl,
        val projectContext: ProjectContext
) : ModuleContext {
    override val project: Project
        get() = projectContext.project
    override val storageManager: StorageManager
        get() = projectContext.storageManager

    fun setDependencies(vararg dependencies: ModuleDescriptorImpl) {
        module.setDependencies(*dependencies)
    }
}

private const val KONAN_STDLIB_NAME = "stdlib"

fun ModuleDescriptor.isNativeStdlib(): Boolean = name == Name.special("<$KONAN_STDLIB_NAME>")

val CallableDescriptor.explicitParameters: List<ParameterDescriptor>
    get() = if (dispatchReceiverParameter != null) {
        val result = ArrayList<ParameterDescriptor>(valueParameters.size + 2)
        dispatchReceiverParameter?.let { result.add(it) }
        extensionReceiverParameter?.let { result.add(it) }
        result.addAll(valueParameters)
        result
    } else {
        extensionReceiverParameter?.let { listOf(it) + valueParameters } ?: valueParameters
    }

val CallableDescriptor.allParameters: List<ParameterDescriptor>
    get() = if (this is ConstructorDescriptor) {
        listOf(this.constructedClass.thisAsReceiverParameter) + explicitParameters
    } else {
        explicitParameters
    }

@OptIn(K1Deprecation::class)
internal object TopDownAnalyzerFacadeForKonan {

    private val nativeFactories = KlibMetadataFactories(::KonanBuiltIns, NullFlexibleTypeDeserializer)

    fun analyzeFiles(files: Collection<KtFile>, context: FrontendContext): AnalysisResult {
        val config = context.config
        val moduleName = Name.special("<${config.moduleId}>")

        val projectContext = ProjectContext(config.project, "TopDownAnalyzer for Konan")

        val module = nativeFactories.DefaultDescriptorFactory.createDescriptorAndNewBuiltIns(
                moduleName, projectContext.storageManager, origin = CurrentKlibModuleOrigin)
        val moduleContext = MutableModuleContextImpl(module, projectContext)

        val resolvedModuleDescriptors = nativeFactories.DefaultResolvedDescriptorsFactory.createResolved(
                config.resolvedLibraries, projectContext.storageManager, module.builtIns, config.languageVersionSettings,
                config.friendModuleFiles, config.refinesModuleFiles,
                config.includedLibraries.map { KlibFile(it.path) }.toSet(), listOf(module),
                isForMetadataCompilation = config.metadataKlib)

        val additionalPackages = mutableListOf<PackageFragmentProvider>()
        if (!module.isNativeStdlib()) {
            module.setDependencies(ModuleDependenciesImpl(
                    allDependencies =
                    listOf(module) + resolvedModuleDescriptors.resolvedDescriptors + resolvedModuleDescriptors.forwardDeclarationsModule,
                    modulesWhoseInternalsAreVisible = resolvedModuleDescriptors.friendModules,
                    directExpectedByDependencies = resolvedModuleDescriptors.refinesModules.toList(),
                    allExpectedByDependencies = resolvedModuleDescriptors.refinesModules
            ))
        } else {
            assert(resolvedModuleDescriptors.resolvedDescriptors.isEmpty())
            moduleContext.setDependencies(module)
            // [K][Suspend]FunctionN belong to stdlib.
            additionalPackages += functionInterfacePackageFragmentProvider(projectContext.storageManager, module)
        }

        return analyzeFilesWithGivenTrace(files, BindingTraceContext(projectContext.project), moduleContext, context, projectContext, additionalPackages)
    }

    fun analyzeFilesWithGivenTrace(
            files: Collection<KtFile>,
            trace: BindingTrace,
            moduleContext: ModuleContext,
            context: FrontendContext,
            @Suppress("UNUSED_PARAMETER") projectContext: ProjectContext,
            additionalPackages: List<PackageFragmentProvider> = emptyList()
    ): AnalysisResult {

        // we print out each file we compile if frontend phase is verbose
        files.takeIf {
            context.shouldPrintFiles()
        }?.forEach(::println)

        createTopDownAnalyzerProviderForKonan(
                moduleContext, trace,
                context.config.configuration.get(CommonConfigurationKeys.LANGUAGE_VERSION_SETTINGS)!!,
                additionalPackages,
                context.config
        ).apply {
            postprocessComponents(context, files)
        }

        return AnalysisResult.success(trace.bindingContext, moduleContext.module)
    }

    fun checkForErrors(@Suppress("UNUSED_PARAMETER") files: Collection<KtFile>, @Suppress("UNUSED_PARAMETER") bindingContext: BindingContext) {
    }
}
