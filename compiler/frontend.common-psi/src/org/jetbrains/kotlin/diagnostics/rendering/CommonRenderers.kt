/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:OptIn(LegacyRenderingContextApi::class)

package org.jetbrains.kotlin.diagnostics.rendering







import com.intellij.openapi.util.text.StringUtil
import org.jetbrains.kotlin.config.MavenComparableVersion
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.kotlin.utils.addToStdlib.joinToWithBuffer



object CommonRenderers {

    val EMPTY = Renderer<Any> { "" }

    val STRING = Renderer<String> { it }

    val NAME = Renderer<Name> { it.asString() }

    val MAVEN_VERSION = Renderer<MavenComparableVersion?> {
        it?.toString() ?: "initial version"
    }

    val THROWABLE = Renderer<Throwable> {
        StringUtil.first(it.stackTraceToString(), 2048, true)
    }

    val RENDER_POSITION_VARIANCE = Renderer { variance: Variance ->
        when (variance) {
            Variance.INVARIANT -> "invariant"
            Variance.IN_VARIANCE -> "in"
            Variance.OUT_VARIANCE -> "out"
        }
    }

    val CLASS_KIND = Renderer { classKind: ClassKind ->
        when (classKind) {
            ClassKind.CLASS -> "class"
            ClassKind.INTERFACE -> "interface"
            ClassKind.ENUM_CLASS -> "enum class"
            ClassKind.ENUM_ENTRY -> "enum entry"
            ClassKind.ANNOTATION_CLASS -> "annotation class"
            ClassKind.OBJECT -> "object"
        }
    }

    fun <T> commaSeparated(itemRenderer: DiagnosticParameterRenderer<T>) = ContextDependentRenderer<Collection<T>> { collection, context ->
        buildString {
            val iterator = collection.iterator()
            while (iterator.hasNext()) {
                val next = iterator.next()
                append(itemRenderer.render(next, context))
                if (iterator.hasNext()) {
                    append(", ")
                }
            }
        }
    }

    fun <T> onNextLines(itemRenderer: ContextIndependentParameterRenderer<T>): ContextIndependentParameterRenderer<Collection<T>> =
        Renderer { elements -> elements.joinToString(separator = "\n", prefix = "\n", transform = itemRenderer::render) }

    fun <Declaration, Data> renderConflictingSignatureData(
        signatureKind: String,
        sortUsing: Comparator<Declaration>,
        declarationRenderer: DiagnosticParameterRenderer<Declaration>,
        renderSignature: StringBuilder.(Data) -> Unit,
        declarations: (Data) -> Collection<Declaration>,
        declarationKind: (Data) -> String = { "declarations" },
    ) = Renderer<Data> { data ->
        val sortedDeclarations = declarations(data).sortedWith(sortUsing)
        val renderingContext = RenderingContext.Impl(sortedDeclarations)
        buildString {
            append("The following ")
            append(declarationKind(data))
            append(" have the same ")
            append(signatureKind)
            append(" signature (")
            renderSignature(data)
            appendLine("):")
            sortedDeclarations.joinToWithBuffer(this, separator = "\n") { descriptor ->
                append("    ")
                append(declarationRenderer.render(descriptor, renderingContext))
            }
        }
    }
}
