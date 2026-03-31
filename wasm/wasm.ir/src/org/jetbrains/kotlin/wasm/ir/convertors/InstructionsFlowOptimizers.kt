/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.wasm.ir.convertors

import org.jetbrains.kotlin.wasm.ir.*
import org.jetbrains.kotlin.wasm.ir.source.location.SourceLocation

private fun WasmOp.pureStacklessInstruction() = when (this) {
    WasmOp.REF_NULL, WasmOp.I32_CONST, WasmOp.I64_CONST, WasmOp.F32_CONST, WasmOp.F64_CONST, WasmOp.LOCAL_GET, WasmOp.GLOBAL_GET, WasmOp.CALL_PURE -> true
    else -> false
}

private fun WasmOp.isOutCfgNode() = when (this) {
    WasmOp.UNREACHABLE, WasmOp.RETURN, WasmOp.THROW, WasmOp.THROW_REF, WasmOp.RETHROW, WasmOp.BR, WasmOp.BR_TABLE -> true
    else -> false
}

private fun WasmOp.isInCfgNode() = when (this) {
    WasmOp.ELSE, WasmOp.CATCH, WasmOp.CATCH_ALL -> true
    else -> false
}

// Annotation pseudo-ops are "attached" to the next real instruction: if that instruction is dropped
// by an optimizer, the annotation must be dropped too. This differs from comment pseudo-ops, which
// are always passed through immediately.
private fun WasmOp.isAnnotationPseudoOp() = when (this) {
    WasmOp.PSEUDO_ANNOTATION_BRANCH_HINT,
    WasmOp.PSEUDO_ANNOTATION_TRACE_INST,
    WasmOp.PSEUDO_ANNOTATION_JS_CALLED -> true
    else -> false
}

internal abstract class OptimizeFlow {
    abstract fun push(instruction: WasmInstr)
    abstract fun complete()
}

private abstract class OptimizeFlowBase(protected val output: OptimizeFlow) : OptimizeFlow() {
    final override fun complete() {
        flash()
        output.complete()
    }

    protected open fun flash() {}
}

private class RemoveUnreachableInstructions(output: OptimizeFlow) : OptimizeFlowBase(output) {
    private var eatEverythingUntilLevel: Int? = null
    private var numberOfNestedBlocks = 0

    private fun getCurrentEatLevel(op: WasmOp): Int? {
        val eatLevel = eatEverythingUntilLevel ?: return null
        if (numberOfNestedBlocks == eatLevel && op.isInCfgNode()) {
            eatEverythingUntilLevel = null
            return null
        }
        if (numberOfNestedBlocks < eatLevel) {
            eatEverythingUntilLevel = null
            return null
        }
        return eatLevel
    }

    override fun push(instruction: WasmInstr) {
        val op = instruction.operator

        if (op.isBlockStart()) {
            numberOfNestedBlocks++
        } else if (op.isBlockEnd()) {
            numberOfNestedBlocks--
        }

        val currentEatUntil = getCurrentEatLevel(op)
        if (currentEatUntil != null) {
            if (currentEatUntil <= numberOfNestedBlocks) {
                return
            }
        } else {
            if (op.isOutCfgNode()) {
                eatEverythingUntilLevel = numberOfNestedBlocks
            }
        }
        output.push(instruction)
    }
}

private class RemoveInstructionPriorUnreachable(output: OptimizeFlow) : OptimizeFlowBase(output) {
    private var firstInstruction: WasmInstr? = null
    private var firstAnnotations: List<WasmInstr> = emptyList()
    private val pendingAnnotations = mutableListOf<WasmInstr>()

    override fun push(instruction: WasmInstr) {
        if (instruction.operator.opcode == WASM_OP_PSEUDO_OPCODE) {
            if (instruction.operator.isAnnotationPseudoOp()) {
                // Buffer annotation which must travel with the next real instruction.
                pendingAnnotations.add(instruction)
            } else {
                flash()
                output.push(instruction)
            }
            return
        }

        val first = firstInstruction
        val firstAnn = firstAnnotations

        // Claim any pending annotations for this incoming instruction.
        firstAnnotations = pendingAnnotations.toList()
        pendingAnnotations.clear()
        firstInstruction = instruction

        if (first == null) {
            return
        }

        if (instruction.operator == WasmOp.UNREACHABLE && (first.operator.pureStacklessInstruction() || first.operator == WasmOp.NOP)) {
            if (first.operator != WasmOp.NOP) {
                val firstLocation = first.location as? SourceLocation.DefinedLocation
                if (firstLocation != null) {
                    //replace first instruction to NOP; its annotations are dropped (they annotated the removed instruction)
                    output.push(wasmInstrWithLocation(WasmOp.NOP, firstLocation))
                }
                // else: drop first and its annotations entirely
            }
            // else: NOP is dropped; firstAnn is empty (NOPs don't carry annotations)
        } else {
            firstAnn.forEach { output.push(it) }
            output.push(first)
        }
    }

    override fun flash() {
        val first = firstInstruction ?: return
        firstAnnotations.forEach { output.push(it) }
        output.push(first)
        firstInstruction = null
        firstAnnotations = emptyList()
        // Pending annotations with no following instruction are dropped.
        pendingAnnotations.clear()
    }
}

private class RemoveInstructionPriorDrop(output: OptimizeFlow) : OptimizeFlowBase(output) {
    private var firstInstruction: WasmInstr? = null
    private var firstAnnotations: List<WasmInstr> = emptyList()
    private var secondInstruction: WasmInstr? = null
    private var secondAnnotations: List<WasmInstr> = emptyList()
    private val pendingAnnotations = mutableListOf<WasmInstr>()

    override fun push(instruction: WasmInstr) {
        if (instruction.operator.opcode == WASM_OP_PSEUDO_OPCODE) {
            if (instruction.operator.isAnnotationPseudoOp()) {
                // Buffer annotation which must travel with the next real instruction.
                pendingAnnotations.add(instruction)
            } else {
                flash()
                output.push(instruction)
            }
            return
        }

        val first = firstInstruction
        val second = secondInstruction

        // Claim any pending annotations for this incoming instruction.
        val annotations = pendingAnnotations.toList()
        pendingAnnotations.clear()

        if (first == null) {
            firstInstruction = instruction
            firstAnnotations = annotations
            return
        }
        if (second == null) {
            secondInstruction = instruction
            secondAnnotations = annotations
            return
        }

        if (second.operator == WasmOp.DROP && first.operator.pureStacklessInstruction()) {
            val firstLocation = first.location as? SourceLocation.DefinedLocation
            if (firstLocation != null) {
                //replace first instruction with NOP; drop its annotations and the DROP's annotations
                firstInstruction = wasmInstrWithLocation(WasmOp.NOP, firstLocation)
                firstAnnotations = emptyList()
                secondInstruction = instruction
                secondAnnotations = annotations
            } else {
                //eat both instructions and their annotations
                firstInstruction = instruction
                firstAnnotations = annotations
                secondInstruction = null
                secondAnnotations = emptyList()
            }
        } else {
            firstAnnotations.forEach { output.push(it) }
            output.push(first)
            firstInstruction = second
            firstAnnotations = secondAnnotations
            secondInstruction = instruction
            secondAnnotations = annotations
        }
    }

    override fun flash() {
        firstInstruction?.let {
            firstAnnotations.forEach { output.push(it) }
            output.push(it)
            firstInstruction = null
            firstAnnotations = emptyList()
        }

        secondInstruction?.let {
            secondAnnotations.forEach { output.push(it) }
            output.push(it)
            secondInstruction = null
            secondAnnotations = emptyList()
        }

        // Pending annotations with no following instruction are dropped.
        pendingAnnotations.clear()
    }
}


private class MergeSetAndGetIntoTee(output: OptimizeFlow) : OptimizeFlowBase(output) {
    private var firstInstruction: WasmInstr? = null
    private var firstAnnotations: List<WasmInstr> = emptyList()
    private val pendingAnnotations = mutableListOf<WasmInstr>()

    override fun push(instruction: WasmInstr) {
        if (instruction.operator.opcode == WASM_OP_PSEUDO_OPCODE) {
            if (instruction.operator.isAnnotationPseudoOp()) {
                // Buffer annotation which must travel with the next real instruction.
                pendingAnnotations.add(instruction)
            } else {
                flash()
                output.push(instruction)
            }
            return
        }

        val first = firstInstruction
        val firstAnn = firstAnnotations

        // Claim any pending annotations for this incoming instruction.
        val annotations = pendingAnnotations.toList()
        pendingAnnotations.clear()

        if (first == null) {
            firstInstruction = instruction
            firstAnnotations = annotations
            return
        }

        if (first.operator == WasmOp.LOCAL_SET && instruction.operator == WasmOp.LOCAL_GET) {
            check(first.immediatesCount == 1 && instruction.immediatesCount == 1)
            val firstImmediate = first.firstImmediateOrNull()
            val secondImmediate = instruction.firstImmediateOrNull()
            val setNumber = (firstImmediate as? WasmImmediate.LocalIdx)?.value
            val getNumber = (secondImmediate as? WasmImmediate.LocalIdx)?.value
            check(setNumber != null && getNumber != null)

            if (getNumber == setNumber) {
                val location = instruction.location
                firstInstruction = if (location != null) {
                    wasmInstrWithLocation(WasmOp.LOCAL_TEE, location, firstImmediate)
                } else {
                    wasmInstrWithoutLocation(WasmOp.LOCAL_TEE, firstImmediate)
                }
                // Annotations of LOCAL_SET carry over to LOCAL_TEE (firstAnnotations stays as firstAnn).
                // Annotations of LOCAL_GET (annotations) are dropped since LOCAL_GET is consumed.
                return
            }
        }

        firstAnn.forEach { output.push(it) }
        output.push(first)
        firstInstruction = instruction
        firstAnnotations = annotations
    }

    override fun flash() {
        firstInstruction?.let { first ->
            firstAnnotations.forEach { output.push(it) }
            output.push(first)
            firstInstruction = null
            firstAnnotations = emptyList()
        }
        // Pending annotations with no following instruction are dropped.
        pendingAnnotations.clear()
    }
}

internal fun createInstructionsFlow(output: OptimizeFlow): OptimizeFlow {
    val mergedWithTee = MergeSetAndGetIntoTee(output)
    val mergedWithUnreachable = RemoveInstructionPriorUnreachable(mergedWithTee)
    val mergedWithDrop = RemoveInstructionPriorDrop(mergedWithUnreachable)
    val removedUnreachableCode = RemoveUnreachableInstructions(mergedWithDrop)
    return removedUnreachableCode
}