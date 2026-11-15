package org.jetbrains.kotlin.ir.descriptors

// Java's platform generic result could be null even with an unconstrained R return.
// Casting to the erased, nullable upper bound retains that value; !! would not.
@Suppress("UNCHECKED_CAST")
internal fun <R> platformVisitorResult(value: R?): R = value as R
