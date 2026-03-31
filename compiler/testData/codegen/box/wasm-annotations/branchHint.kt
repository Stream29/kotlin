// TARGET_BACKEND: WASM
// RUN_THIRD_PARTY_OPTIMIZER

import kotlin.random.Random
import kotlin.wasm.internal.*

fun box(): String {
    val r = Random.nextInt(100)
    var result = ""

    // Explicit function-based hint for the branch condition
    if (likely(r > 48)) {
        result += "a"
    } else {
        result += "b"
    }

    // Non-taken branch with unlikely hint
    if (unlikely(r > 51)) {
        result += "c"
    } else {
        result += "d"
    }

    if (result == "bc") return "Fail"

    return "OK"
}
