@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package org.jetbrains.kotlin.konan.host

import kotlinx.cinterop.*
import nativekotlin.c1i.posix.c1i_spawn
import platform.posix.strerror

fun runNativeProcess(
    command: List<String>, workingDirectory: NativeFile?, input: NativeFile?,
    output: NativeFile?, errorOutput: NativeFile?, mergeErrors: Boolean
): Int = memScoped {
    require(command.isNotEmpty())
    require(command.all { '\u0000' !in it })
    val argv = allocArray<CPointerVar<ByteVar>>(command.size + 1)
    command.forEachIndexed { i, argument -> argv[i] = argument.cstr.ptr }
    argv[command.size] = null
    val status = c1i_spawn(argv, workingDirectory?.path, input?.path, output?.path, errorOutput?.path, if (mergeErrors) 1 else 0)
    check(status >= 0) { "Cannot execute ${command.first()}: ${strerror(-status)?.toKString()} (errno=${-status})" }
    status
}
