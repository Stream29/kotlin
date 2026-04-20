/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.jvm.specialization

import org.jetbrains.kotlin.codegen.util.inlinecodegen.LightIrType
import org.jetbrains.kotlin.codegen.util.inlinecodegen.LightTypeIntrinsics
import org.jetbrains.kotlin.codegen.util.inlinecodegen.ReifiedOperationKind
import org.jetbrains.kotlin.codegen.util.inlinecodegen.reificationArgument
import org.jetbrains.kotlin.codegen.util.inlinecodegen.reifiedOperationKind
import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.tree.InsnList
import org.jetbrains.org.objectweb.asm.tree.InsnNode
import org.jetbrains.org.objectweb.asm.tree.MethodInsnNode
import org.jetbrains.org.objectweb.asm.tree.TypeInsnNode

internal fun reify(
    instructions: InsnList,
    markerInsn: MethodInsnNode,
    typeParametersNames: List<String>,
    specializedTypeParameters: Map<Int, LightIrType>
) {
    val prev = markerInsn.previous ?: error("no previous instruction")
    val prev2 = prev.previous ?: error("no previous instruction")

    val operationKind = markerInsn.reifiedOperationKind ?: error("invalid reified operation kind")
    val reificationArgument = markerInsn.reificationArgument ?: error("invalid reification argument")

    val typeParameterIndex = typeParametersNames.indexOf(reificationArgument.parameterName).takeIf { it != -1 }
        ?: error("type parameter name not found in generic signature: ${reificationArgument.parameterName}")
    val rawTypeParameterValue = specializedTypeParameters[typeParameterIndex]
        ?: error("reified type parameter must be specialized: ${reificationArgument.parameterName}")
    val typeParameterValue = rawTypeParameterValue.reify(reificationArgument)

    instructions.set(prev, InsnNode(Opcodes.NOP))
    instructions.set(prev2, InsnNode(Opcodes.NOP))

    when (operationKind) {
        ReifiedOperationKind.NEW_ARRAY -> {
            (markerInsn.next as TypeInsnNode).desc = typeParameterValue.classifier.mapTypeParameterInternalName
            instructions.set(markerInsn, InsnNode(Opcodes.NOP))
        }
        ReifiedOperationKind.AS -> processAs(instructions, markerInsn, typeParameterValue, safe = false)
        ReifiedOperationKind.SAFE_AS -> processAs(instructions, markerInsn, typeParameterValue, safe = true)
        ReifiedOperationKind.IS -> processIs(instructions, markerInsn, typeParameterValue)
        ReifiedOperationKind.JAVA_CLASS -> TODO()
        ReifiedOperationKind.ENUM_REIFIED -> TODO()
        ReifiedOperationKind.TYPE_OF -> TODO()
        ReifiedOperationKind.CATCH -> TODO()
    }
}

private fun processAs(
    instructions: InsnList,
    markerInsn: MethodInsnNode,
    typeParameterValue: LightIrType,
    safe: Boolean,
) {
    val classifier = typeParameterValue.classifier as? LightIrType.Classifier.Clazz
        ?: error("trying to reify type parameter which is itself a type parameter")

    LightTypeIntrinsics.asCast(
        classifier.fqName,
        typeParameterValue.nullable,
        classifier.mapTypeParameterInternalName,
        safe,
        true, // TODO: support non-unified null checks?
    ) { instructions.insertBefore(markerInsn, it) }

    // Keep stubCheckcast to avoid VerifyErrors on 1.8+ bytecode,
    // it's safe to remove cast to Object as FrameMap will use it as default value for merged branches
    if ((markerInsn.next as TypeInsnNode).desc == "java/lang/Object") {
        instructions.set(markerInsn.next, InsnNode(Opcodes.NOP))
    }

    instructions.set(markerInsn, InsnNode(Opcodes.NOP))
}

private fun processIs(
    instructions: InsnList,
    markerInsn: MethodInsnNode,
    typeParameterValue: LightIrType,
) {
    val classifier = typeParameterValue.classifier as? LightIrType.Classifier.Clazz
        ?: error("trying to reify type parameter which is itself a type parameter")

    LightTypeIntrinsics.isCheck(
        classifier.fqName,
        typeParameterValue.nullable,
        classifier.mapTypeParameterInternalName,
    ) { instructions.insertBefore(markerInsn, it) }

    instructions.set(markerInsn.next, InsnNode(Opcodes.NOP))
    instructions.set(markerInsn, InsnNode(Opcodes.NOP))
}
