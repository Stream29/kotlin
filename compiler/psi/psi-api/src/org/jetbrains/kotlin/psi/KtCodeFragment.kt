/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.mock.MockProject
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.psi.*
import com.intellij.psi.JavaCodeFragment.VisibilityChecker
import com.intellij.psi.impl.PsiManagerEx
import com.intellij.psi.impl.source.tree.FileElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.tree.IElementType
import com.intellij.testFramework.LightVirtualFile
import com.intellij.util.messages.Topic
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.psiUtil.getElementTextWithContext

@SubclassOptInRequired(KtImplementationDetail::class)
abstract class KtCodeFragment : KtFile, KtCodeFragmentBase {
    private var originalContext: PsiElement?
    private var fragmentViewProvider: SingleRootFileViewProvider
    private var importDirectiveStrings: LinkedHashSet<String>
    private var fakeContextForJavaFileDelegate: Lazy<PsiElement?>
    private val fakeContextForJavaFile: PsiElement? get() = fakeContextForJavaFileDelegate.value
    private var forcedResolveScopeValue: GlobalSearchScope?
    private var thisTypeValue: PsiType?
    private var superTypeValue: PsiType?
    private var exceptionHandlerValue: JavaCodeFragment.ExceptionHandler?
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






    final override fun init(elementType: IElementType, contentElementType: IElementType) {
        super.init(elementType, contentElementType)
    }


    /**
     * Returns the meaningful content of this fragment (for example, the parsed expression, block, or type reference), or `null` if it could
     * not be parsed.
     */
    abstract fun getContentElement(): KtElement?

    override fun forceResolveScope(scope: GlobalSearchScope?) {
        forcedResolveScopeValue = scope
    }

    override fun getForcedResolveScope() = forcedResolveScopeValue

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

    override fun getThisType() = thisTypeValue

    override fun setThisType(psiType: PsiType?) {
        thisTypeValue = psiType
    }

    override fun getSuperType() = superTypeValue

    override fun setSuperType(superType: PsiType?) {
        this.superTypeValue = superType
    }

    override fun importsToString(): String {
        return importDirectiveStrings.joinToString(IMPORT_SEPARATOR)
    }

    override fun addImportsFromString(imports: String?) {
        val notifyChanged = fragmentViewProvider.isEventSystemEnabled && project !is MockProject

        if (imports != null && appendImports(imports)) {
            if (notifyChanged) {
                // This forces the code fragment to be re-highlighted
                add(KtPsiFactory(project).createColon()).delete()
            }

            // Increment the modification stamp
            clearCaches()

            if (notifyChanged) {
                @OptIn(KtImplementationDetail::class)
                project.messageBus.syncPublisher(IMPORT_MODIFICATION).onCodeFragmentImportsModification(this)
            }
        }
    }

    @Deprecated(
        message = "Use 'addImportsFromString()w' instead",
        replaceWith = ReplaceWith("addImportsFromString(import)"),
        level = DeprecationLevel.ERROR,
    )
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

    /**
     * Builds a [KtImportList] from the imports registered on this fragment, or `null` if the fragment has no imports or no context.
     */
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

    override fun setVisibilityChecker(checker: VisibilityChecker?) {}

    override fun getVisibilityChecker(): VisibilityChecker = VisibilityChecker.EVERYTHING_VISIBLE

    override fun setExceptionHandler(checker: JavaCodeFragment.ExceptionHandler?) {
        exceptionHandlerValue = checker
    }

    override fun getExceptionHandler() = exceptionHandlerValue

    /**
     * Returns the [KtFile] that ultimately contains this fragment's context (see [getOriginalContext]), or `null` if the context is invalid
     * or not a Kotlin element.
     */
    fun getContextContainingFile(): KtFile? {
        return getOriginalContext()?.takeIf { it.isValid }?.containingKtFile
    }

    /**
     * Returns the original context element, unwrapping nested code fragments so that the result is a real source element rather than
     * another fragment's context, or `null` if there is no Kotlin context.
     */
    fun getOriginalContext(): KtElement? {
        val contextElement = originalContext as? KtElement
        val contextFile = contextElement?.containingFile as? KtFile
        if (contextFile is KtCodeFragment) {
            return contextFile.getOriginalContext()
        }
        return contextElement
    }

    companion object {
        /**
         * The separator between individual imports in the raw import string accepted by a code fragment.
         */
        const val IMPORT_SEPARATOR: String = ","

        @KtImplementationDetail
        val IMPORT_MODIFICATION: Topic<KotlinCodeFragmentImportModificationListener> =
            Topic(KotlinCodeFragmentImportModificationListener::class, Topic.BroadcastDirection.TO_CHILDREN, true)

        /**
         * A copyable user-data key holding a provider of a fake Kotlin context element. It is used when a fragment's real context is a
         * non-Kotlin (for example, Java) file, so that references can still be resolved as Kotlin.
         */
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

/**
 * A listener notified when the imports of a [KtCodeFragment] are modified.
 */
fun interface KotlinCodeFragmentImportModificationListener {
    /**
     * Called after the imports of [codeFragment] have changed.
     */
    fun onCodeFragmentImportsModification(codeFragment: KtCodeFragment)
}
