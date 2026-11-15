/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.psi.*
import com.intellij.psi.impl.PsiManagerEx
import com.intellij.psi.impl.source.tree.FileElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.tree.IElementType
import com.intellij.testFramework.LightVirtualFile
import com.intellij.util.messages.Topic
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtCodeFragment.Companion.IMPORT_SEPARATOR
import org.jetbrains.kotlin.psi.psiUtil.getElementTextWithContext



abstract class KtCodeFragment : KtFile, KtCodeFragmentBase {

    private var originalContext: PsiElement?
    private var fragmentViewProvider: SingleRootFileViewProvider
    private var importDirectiveStrings: LinkedHashSet<String>
    private var fakeContextForJavaFileDelegate: Lazy<PsiElement?>
    private val fakeContextForJavaFile: PsiElement? get() = fakeContextForJavaFileDelegate.value
    private var forcedResolveScopeValue: GlobalSearchScope?
    private var thisTypeValue: Any?
    private var superTypeValue: Any?
    private var exceptionHandlerValue: Any?

    constructor(viewProvider: FileViewProvider, imports: String?, elementType: IElementType, context: PsiElement?) :
        super(viewProvider, false) {
        originalContext = context
        fragmentViewProvider = super.viewProvider as SingleRootFileViewProvider
        importDirectiveStrings = LinkedHashSet()
        fakeContextForJavaFileDelegate = lazy { getCopyableUserData(FAKE_CONTEXT_FOR_JAVA_FILE)?.invoke() }
        @Suppress("LeakingThis")
        fragmentViewProvider.forceCachedPsi(this)
        init(TokenType.CODE_FRAGMENT, elementType)
        if (imports != null) appendImports(imports)
        forcedResolveScopeValue = null
        thisTypeValue = null
        superTypeValue = null
        exceptionHandlerValue = null
    }

    protected fun copyKtCodeFragmentStateTo(clone: KtCodeFragment) {
        copyKtCommonFileStateTo(clone)
        clone.originalContext = originalContext
        clone.fragmentViewProvider = fragmentViewProvider
        clone.importDirectiveStrings = importDirectiveStrings
        clone.fakeContextForJavaFileDelegate = fakeContextForJavaFileDelegate
        clone.forcedResolveScopeValue = forcedResolveScopeValue
        clone.thisTypeValue = thisTypeValue
        clone.superTypeValue = superTypeValue
        clone.exceptionHandlerValue = exceptionHandlerValue
    }

    constructor(
        project: Project,
        name: String,
        text: CharSequence,
        imports: String?,
        elementType: IElementType,
        context: PsiElement?
    ) : this(
        createFileViewProviderForLightFile(project, name, text),
        imports,
        elementType,
        context,
    )






    final override fun init(elementType: IElementType, contentElementType: IElementType?) {
        super.init(elementType, contentElementType)
    }


    abstract fun getContentElement(): KtElement?

    open fun forceResolveScope(scope: GlobalSearchScope?) {
        forcedResolveScopeValue = scope
    }

    open fun getForcedResolveScope() = forcedResolveScopeValue

    override val isPhysical: Boolean get() = fragmentViewProvider.isEventSystemEnabled

    override val isValid: Boolean get() = true

    override val context: PsiElement?
    get() {
        if (fakeContextForJavaFile != null) return fakeContextForJavaFile
        val originalContext = this.originalContext
        if (originalContext != null && originalContext !is KtElement) {
            val logInfoForContextElement = (originalContext as? PsiFile)?.virtualFile?.path ?: originalContext.getElementTextWithContext()
            LOG.warn("CodeFragment with non-kotlin context should have fakeContextForJavaFile set: \noriginalContext = $logInfoForContextElement")
            return null
        }

        return originalContext
    }

    override val resolveScope: GlobalSearchScope get() = originalContext?.resolveScope ?: super.resolveScope

    override fun clone(): KtCodeFragment {
        val elementClone = calcTreeElement().clone() as FileElement

        return (cloneImpl(elementClone) as KtCodeFragment).apply {
            myOriginalFile = this@KtCodeFragment
            importDirectiveStrings = LinkedHashSet(this@KtCodeFragment.importDirectiveStrings)
            fragmentViewProvider = SingleRootFileViewProvider(
                PsiManager.getInstance(project),
                LightVirtualFile(name, KotlinFileType.INSTANCE, text),
                false
            )
            fragmentViewProvider.forceCachedPsi(this)
        }
    }

    final override val viewProvider: SingleRootFileViewProvider get() = fragmentViewProvider

    open fun importsToString(): String {
        return importDirectiveStrings.joinToString(IMPORT_SEPARATOR)
    }

    open fun addImportsFromString(imports: String?) {
        val notifyChanged = fragmentViewProvider.isEventSystemEnabled

        if (imports != null && appendImports(imports)) {
            if (notifyChanged) {
                // This forces the code fragment to be re-highlighted
                add(KtPsiFactory(project).createColon()).delete()
            }

            // Increment the modification stamp
            clearCaches()
        }
    }

    @Deprecated("Use 'addImportsFromString()w' instead", ReplaceWith("addImportsFromString(import)"), level = DeprecationLevel.WARNING)
    fun addImport(import: String) {
        addImportsFromString(import)
    }

    /**
     * Parses raw [rawImports] and appends them to the list of code fragment imports.
     *
     * Import strings must be separated by the [IMPORT_SEPARATOR].
     * Each import must be either a qualified name to import (e.g., 'foo.bar'), or a complete text representation of an import directive
     * (e.g., 'import foo.bar as baz').
     *
     * Note that already present import directives will be ignored.
     *
     * @return `true` if new import directives were added.
     */
    private fun appendImports(rawImports: String): Boolean {
        if (rawImports.isEmpty()) {
            return false
        }

        var hasNewImports = false

        for (rawImport in rawImports.split(IMPORT_SEPARATOR)) {
            val importDirectiveString = if (rawImport.startsWith("import ")) rawImport else "import $rawImport"
            if (importDirectiveStrings.add(importDirectiveString) && !hasNewImports) {
                hasNewImports = true
            }
        }

        return hasNewImports
    }

    fun importsAsImportList(): KtImportList? {
        val originalContext = this.originalContext
        if (importDirectiveStrings.isNotEmpty() && originalContext != null) {
            val ktPsiFactory = KtPsiFactory.contextual(originalContext)
            val fileText = importDirectiveStrings.joinToString("\n")
            return ktPsiFactory.createFile("imports_for_codeFragment.kt", fileText).importList
        }

        return null
    }

    override val importList: KtImportList?
        get() = importsAsImportList()

    override val importLists: List<KtImportList>
        get() = listOfNotNull(importsAsImportList())

    override val importDirectives: List<KtImportDirective>
        get() = importsAsImportList()?.imports ?: emptyList()

    fun getContextContainingFile(): KtFile? {
        return getOriginalContext()?.takeIf { it.isValid }?.containingKtFile
    }

    fun getOriginalContext(): KtElement? {
        val contextElement = originalContext as? KtElement
        val contextFile = contextElement?.containingFile as? KtFile
        if (contextFile is KtCodeFragment) {
            return contextFile.getOriginalContext()
        }
        return contextElement
    }

    companion object {
        const val IMPORT_SEPARATOR: String = ","

        @KtImplementationDetail
        val IMPORT_MODIFICATION: Topic<KotlinCodeFragmentImportModificationListener> =
            Topic(KotlinCodeFragmentImportModificationListener::class, Topic.BroadcastDirection.TO_CHILDREN, true)

        val FAKE_CONTEXT_FOR_JAVA_FILE: Key<Function0<KtElement>> = Key.create("FAKE_CONTEXT_FOR_JAVA_FILE")

        internal fun createFileViewProviderForLightFile(project: Project, name: String, text: CharSequence): FileViewProvider {
            val psiManager = PsiManager.getInstance(project) as PsiManagerEx
            return psiManager.fileManager.createFileViewProvider(
                LightVirtualFile(name, KotlinFileType.INSTANCE, text),
                /* eventSystemEnabled = */true
            )
        }

        private val LOG = Logger.getInstance(KtCodeFragment::class)
    }
}

fun interface KotlinCodeFragmentImportModificationListener {
    fun onCodeFragmentImportsModification(codeFragment: KtCodeFragment)
}
