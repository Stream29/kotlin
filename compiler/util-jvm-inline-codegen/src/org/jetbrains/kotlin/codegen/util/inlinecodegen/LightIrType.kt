/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.util.inlinecodegen

import java.io.Serializable

data class LightIrType(val classifier: Classifier, val arguments: List<TypeArgument>, val nullable: Boolean) : Serializable {
    sealed interface Classifier : Serializable {
        val mapTypeParameterInternalName: String

        data class Clazz(
            val fqName: String,
            val inlineJvmRepr: String?,
            val nullableInlineJvmRepr: String?,
            override val mapTypeParameterInternalName: String,
        ) : Classifier

        data class TypeParameter(
            val name: String,
            val index: Int,
            val variance: Char,
            val isReified: Boolean,
            val specialized: Boolean,
            override val mapTypeParameterInternalName: String,
        ) : Classifier
    }

    sealed interface TypeArgument : Serializable {
        class StarProjection : TypeArgument
        data class TypeProjection(val type: LightIrType, val variance: Char) : TypeArgument
    }

    fun markNullable(): LightIrType = copy(nullable = true)

    fun reify(reificationArgument: ReificationArgument): LightIrType {
        var arrayWrapped = this
        repeat(reificationArgument.arrayDepth) {
            arrayWrapped = LightIrType(
                Classifier.Clazz(
                    "kotlin/Array",
                    null,
                    null,
                    if (arrayWrapped.classifier.mapTypeParameterInternalName.startsWith("[")) {
                        "[" + arrayWrapped.classifier.mapTypeParameterInternalName
                    } else {
                        "[L${arrayWrapped.classifier.mapTypeParameterInternalName};"
                    }
                ),
                listOf(TypeArgument.TypeProjection(arrayWrapped.markNullable(), '-')),
                false,
            )
        }
        return if (reificationArgument.nullable && !arrayWrapped.nullable) {
            arrayWrapped.markNullable()
        } else {
            arrayWrapped
        }
    }

    fun encode(): String {
        val bytes = java.io.ByteArrayOutputStream().use { bos ->
            java.io.ObjectOutputStream(bos).use { it.writeObject(this) }
            bos.toByteArray()
        }
        return java.util.Base64.getEncoder().encodeToString(bytes)
    }

    fun reify(mapping: Map<String, LightIrType>): LightIrType {
        (this.classifier as? Classifier.TypeParameter)?.name?.let { mapping[it] }?.let { parameterValue ->
            return parameterValue
        }

        val reifiedArgs = arguments.map {
            when (it) {
                is TypeArgument.StarProjection -> TypeArgument.StarProjection()
                is TypeArgument.TypeProjection -> TypeArgument.TypeProjection(it.type.reify(mapping), it.variance)
            }
        }

        return LightIrType(classifier, reifiedArgs, nullable)
    }

    companion object {
        fun decode(s: String): LightIrType {
            val bytes = java.util.Base64.getDecoder().decode(s)
            return java.io.ObjectInputStream(java.io.ByteArrayInputStream(bytes)).use { it.readObject() as LightIrType }
        }

        fun decodeTypeParameters(str: String): Map<Int, LightIrType> {
            val map = HashMap<Int, LightIrType>()
            for (line in str.lines()) {
                if (line.isEmpty()) continue
                val eqIdx = line.indexOf('=')
                val key = line.substring(0, eqIdx).toInt()
                val value = line.substring(eqIdx + 1)
                map[key] = decode(value)
            }
            return map
        }

        fun encodeTypeParameters(map: Map<Int, LightIrType>): String {
            return map.entries.joinToString("\n") { (k, v) -> "$k=${v.encode()}" }
        }
    }
}
