/*
 * Copyright 2010-2025 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.jvm.compiler

import com.intellij.core.CoreProjectScopeBuilder
import com.intellij.lang.Language
import com.intellij.mock.MockFileDocumentManagerImpl
import com.intellij.mock.MockFileIndexFacade
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ComponentManager
import com.intellij.openapi.components.NativeServiceManager
import com.intellij.openapi.editor.impl.DocumentImpl
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileTypes.BinaryFileDecompiler
import com.intellij.openapi.fileTypes.BinaryFileTypeDecompilers
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.FileIndexFacade
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.ModificationTracker
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileSystem
import com.intellij.openapi.vfs.encoding.EncodingRegistry
import com.intellij.openapi.vfs.local.CoreLocalFileSystem
import com.intellij.platform.native.NativeCharset
import com.intellij.pom.PomModel
import com.intellij.pom.core.impl.PomModelImpl
import com.intellij.pom.tree.TreeAspect
import com.intellij.psi.*
import com.intellij.psi.impl.PsiDocumentManagerBase
import com.intellij.psi.impl.PsiManagerImpl
import com.intellij.psi.impl.PsiTreeChangeEventImpl
import com.intellij.psi.impl.PsiTreeChangePreprocessor
import com.intellij.psi.impl.smartPointers.SmartPointerManagerImpl
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.search.ProjectScopeBuilder
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.testFramework.LightVirtualFile
import com.intellij.util.Function
import com.intellij.util.KeyedLazyInstance
import com.intellij.util.LocalTimeCounter
import org.jetbrains.kotlin.CoreEnvironmentDeprecation
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.config.kotlinSourceRoots
import org.jetbrains.kotlin.cli.common.getSourceFilesFromSourceRoots
import org.jetbrains.kotlin.cli.extensionsStorage
import org.jetbrains.kotlin.compiler.plugin.TEST_ONLY_PROJECT_CONFIGURATION_CALLBACK
import org.jetbrains.kotlin.compiler.plugin.registerInProject
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImplementationDetail
import org.jetbrains.kotlin.psi.hmppModuleName
import org.jetbrains.kotlin.psi.impl.KotlinPsiBootstrap
import org.jetbrains.kotlin.psi.isCommonSource

enum class EnvironmentConfigFiles {
    JVM_CONFIG_FILES,
    JS_CONFIG_FILES,
    METADATA_CONFIG_FILES,
    WASM_CONFIG_FILES,
    NATIVE_CONFIG_FILES,
}

class VfsBasedProjectEnvironment(
    val project: Project,
    val localFileSystem: VirtualFileSystem,
)

class NativeCompilerProject(
    parent: ComponentManager,
    override val name: String = "KotlinNativeCompilerProject",
) : NativeServiceManager(parent), Project {
    override val baseDir: VirtualFile? get() = null
    override val basePath: String? get() = null
    override val projectFile: VirtualFile? get() = null
    override val projectFilePath: String? get() = null
    override val workspaceFile: VirtualFile? get() = null
    override val locationHash: String get() = name
    override fun save() {}
    override val isOpen: Boolean get() = !isDisposed
    override val isInitialized: Boolean get() = !isDisposed
}

private class NativeEncodingRegistry : EncodingRegistry() {
    override fun isNative2Ascii(virtualFile: VirtualFile): Boolean = false
    override val isNative2AsciiForPropertiesFiles: Boolean get() = false
    override val defaultCharset: NativeCharset get() = NativeCharset.UTF_8
    override fun getEncoding(virtualFile: VirtualFile?, useParentDefaults: Boolean): NativeCharset? = NativeCharset.UTF_8
    override fun setEncoding(virtualFileOrDir: VirtualFile?, charset: NativeCharset?) {}
    override val defaultConsoleEncoding: NativeCharset get() = NativeCharset.UTF_8
}

private class NativePsiModificationTracker : PsiModificationTracker, PsiTreeChangePreprocessor {
    private var count: Long = 0L

    override val modificationCount: Long
        get() = count

    @Suppress("DEPRECATION")
    override val javaStructureModificationTracker: ModificationTracker
        get() = this

    override fun forLanguage(language: Language): ModificationTracker = this

    override fun forLanguages(condition: (Language) -> Boolean): ModificationTracker = this

    override fun treeChanged(event: PsiTreeChangeEventImpl) {
        count++
    }
}

private class NativePsiDocumentManager(project: Project) : PsiDocumentManagerBase(project)

private class NativePsiFileFactory(private val project: Project) : PsiFileFactory() {
    @Suppress("OverridingDeprecatedMember")
    override fun createFileFromText(name: String, text: String): PsiFile {
        val fileType = FileTypeRegistry.getInstance().getFileTypeByFileName(name)
        return createFileFromText(name, fileType, text)
    }

    override fun createFileFromText(fileName: String, fileType: FileType, text: CharSequence): PsiFile =
        createFileFromText(fileName, fileType, text, LocalTimeCounter.currentTime(), false)

    override fun createFileFromText(
        name: String,
        fileType: FileType,
        text: CharSequence,
        modificationStamp: Long,
        eventSystemEnabled: Boolean,
    ): PsiFile = createFileFromText(name, fileType, text, modificationStamp, eventSystemEnabled, true)

    override fun createFileFromText(
        name: String,
        fileType: FileType,
        text: CharSequence,
        modificationStamp: Long,
        eventSystemEnabled: Boolean,
        markAsCopy: Boolean,
    ): PsiFile {
        val virtualFile = LightVirtualFile(name, fileType, text, modificationStamp)
        val viewProvider = SingleRootFileViewProvider(PsiManager.getInstance(project), virtualFile, eventSystemEnabled, fileType)
        return viewProvider.getPsi(viewProvider.baseLanguage)
            ?: error("Could not create PsiFile for $name ($fileType)")
    }

    override fun createFileFromText(name: String, language: Language, text: CharSequence): PsiFile? =
        createFileFromText(name, language, text, true, true)

    override fun createFileFromText(
        name: String,
        language: Language,
        text: CharSequence,
        eventSystemEnabled: Boolean,
        markAsCopy: Boolean,
    ): PsiFile? = createFileFromText(name, language, text, eventSystemEnabled, markAsCopy, false)

    override fun createFileFromText(
        name: String,
        language: Language,
        text: CharSequence,
        eventSystemEnabled: Boolean,
        markAsCopy: Boolean,
        noSizeLimit: Boolean,
    ): PsiFile? = createFileFromText(name, language, text, eventSystemEnabled, markAsCopy, noSizeLimit, null)

    override fun createFileFromText(
        name: String,
        language: Language,
        text: CharSequence,
        eventSystemEnabled: Boolean,
        markAsCopy: Boolean,
        noSizeLimit: Boolean,
        original: VirtualFile?,
    ): PsiFile? {
        val virtualFile = LightVirtualFile(name, language, text)
        if (noSizeLimit) {
            SingleRootFileViewProvider.doNotCheckFileSizeLimit(virtualFile)
        }
        val viewProvider = SingleRootFileViewProvider(PsiManager.getInstance(project), virtualFile, eventSystemEnabled)
        return viewProvider.getPsi(language)
    }

    override fun createFileFromText(
        fileType: FileType,
        fileName: String,
        chars: CharSequence,
        startOffset: Int,
        endOffset: Int,
    ): PsiFile? = createFileFromText(fileName, fileType, chars.subSequence(startOffset, endOffset))

    override fun createFileFromText(chars: CharSequence, original: PsiFile): PsiFile? =
        createFileFromText(original.name, original.fileType, chars)
}

class KotlinCoreEnvironment private constructor(
    val project: Project,
    val configuration: CompilerConfiguration,
    val localFileSystem: VirtualFileSystem,
) {
    private val sourceFiles: List<KtFile> by lazy {
        createSourceFilesFromSourceRoots(configuration, project, localFileSystem)
    }

    fun getSourceFiles(): List<KtFile> = sourceFiles

    fun countLinesOfCode(sourceFiles: List<KtFile>): Int =
        sourceFiles.sumOf { sourceFile ->
            val text = sourceFile.text
            text.count { it == '\n' } + if (text.isNotEmpty() && !text.endsWith('\n')) 1 else 0
        }

    companion object {
        @CoreEnvironmentDeprecation
        fun createForProduction(
            projectDisposable: Disposable,
            configuration: CompilerConfiguration,
            configFiles: EnvironmentConfigFiles,
        ): KotlinCoreEnvironment {
            val app = ApplicationManager.getApplicationOrNull() ?: run {
                val runtime = KotlinPsiBootstrap.install(PluginId.getId("org.jetbrains.kotlin.native.klib.cli"))
                Disposer.register(projectDisposable, runtime)
                runtime.application
            }
            if (app is NativeServiceManager) {
                if (!app.extensionArea.hasExtensionPoint(BinaryFileTypeDecompilers.EP_NAME.name)) {
                    app.extensionArea.declarePoint<KeyedLazyInstance<BinaryFileDecompiler>>(BinaryFileTypeDecompilers.EP_NAME.name)
                }
                if (!app.extensionArea.hasExtensionPoint("com.intellij.lang.substitutor")) {
                    app.extensionArea.declarePoint<KeyedLazyInstance<LanguageSubstitutor>>("com.intellij.lang.substitutor")
                }
                if (!app.hasComponent(BinaryFileTypeDecompilers::class)) {
                    app.registerService(BinaryFileTypeDecompilers::class) { BinaryFileTypeDecompilers() }
                }
                if (!app.hasComponent(EncodingRegistry::class)) {
                    app.registerService(EncodingRegistry::class) { NativeEncodingRegistry() }
                }
                if (!app.hasComponent(LanguageSubstitutors::class)) {
                    app.registerService(LanguageSubstitutors::class) { LanguageSubstitutors() }
                }
                if (!app.hasComponent(FileDocumentManager::class)) {
                    app.registerService(FileDocumentManager::class) {
                        MockFileDocumentManagerImpl(null, Function { text -> DocumentImpl(text) })
                    }
                }
            }

            val project = NativeCompilerProject(app)
            Disposer.register(projectDisposable, project)

            project.extensionArea.declarePoint<PsiTreeChangePreprocessor>(PsiTreeChangePreprocessor.EP.name)
            project.extensionArea.declarePoint<PsiTreeChangeListener>(PsiTreeChangeListener.EP.name)
            project.extensionArea.declarePoint<FirExtensionRegistrarAdapter>(FirExtensionRegistrarAdapter.name)
            project.extensionArea.declarePoint<IrGenerationExtension>(IrGenerationExtension.name)

            project.registerService(PsiModificationTracker::class) { NativePsiModificationTracker() }
            project.registerService(PomModel::class) { PomModelImpl(project) }
            project.registerService(TreeAspect::class) { TreeAspect() }
            project.registerService(PsiManager::class) { PsiManagerImpl(project) }
            project.registerService(PsiFileFactory::class) { NativePsiFileFactory(project) }
            project.registerService(PsiDocumentManager::class) { NativePsiDocumentManager(project) }
            project.registerService(SmartPointerManager::class) { SmartPointerManagerImpl(project) }
            project.registerService(ResolveCache::class) { ResolveCache(project) }
            project.registerService(FileIndexFacade::class) {
                object : MockFileIndexFacade(project) {
                    override fun isInLibrarySource(file: VirtualFile): Boolean = true
                }
            }
            project.registerService(ProjectScopeBuilder::class) {
                CoreProjectScopeBuilder(project, FileIndexFacade.getInstance(project))
            }

            configuration.extensionsStorage?.registerInProject(project)
            configuration.get(TEST_ONLY_PROJECT_CONFIGURATION_CALLBACK)?.invoke(project)

            val localFileSystem = CoreLocalFileSystem()
            return KotlinCoreEnvironment(project, configuration, localFileSystem)
        }

        @OptIn(KtImplementationDetail::class)
        private fun createSourceFilesFromSourceRoots(
            configuration: CompilerConfiguration,
            project: Project,
            localFileSystem: VirtualFileSystem,
        ): List<KtFile> {
            val psiManager = PsiManager.getInstance(project)
            val result = mutableListOf<KtFile>()
            val vfsEnv = VfsBasedProjectEnvironment(project, localFileSystem)
            getSourceFilesFromSourceRoots(configuration, vfsEnv, configuration.kotlinSourceRoots) { virtualFile, isCommon, moduleName ->
                val psiFile = psiManager.findFile(virtualFile)
                if (psiFile is KtFile) {
                    psiFile.isCommonSource = isCommon
                    if (moduleName != null) {
                        psiFile.hmppModuleName = moduleName
                    }
                    result.add(psiFile)
                }
            }
            return result
        }
    }
}

fun KotlinCoreEnvironment.toVfsBasedProjectEnvironment(): VfsBasedProjectEnvironment {
    return VfsBasedProjectEnvironment(project, localFileSystem)
}
