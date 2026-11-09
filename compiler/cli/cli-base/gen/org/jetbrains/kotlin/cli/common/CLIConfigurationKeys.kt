/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("IncorrectFormatting", "unused")

package org.jetbrains.kotlin.cli.common

import nativekotlin.host.files.NativeFile as File
import org.jetbrains.kotlin.cli.common.config.ContentRoot
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.modules.ModuleChunk
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.config.MessageCollectorAccess
import org.jetbrains.kotlin.utils.KotlinPaths

internal object CLIExtraConfigurationKeys {
    val CONTENT_ROOTS = CompilerConfigurationKey.create<List<ContentRoot>>("CONTENT_ROOTS")

    @OptIn(MessageCollectorAccess::class)
    val MESSAGE_COLLECTOR_KEY = CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY

    val ORIGINAL_MESSAGE_COLLECTOR_KEY = CompilerConfigurationKey.create<MessageCollector>("ORIGINAL_MESSAGE_COLLECTOR_KEY")

    val RENDER_DIAGNOSTIC_INTERNAL_NAME = CompilerConfigurationKey.create<Boolean>("RENDER_DIAGNOSTIC_INTERNAL_NAME")

    val TREAT_WARNINGS_AS_ERRORS = CompilerConfigurationKey.create<Boolean>("TREAT_WARNINGS_AS_ERRORS")

    val ALLOW_KOTLIN_PACKAGE = CompilerConfigurationKey.create<Boolean>("ALLOW_KOTLIN_PACKAGE")

    val METADATA_DESTINATION_DIRECTORY = CompilerConfigurationKey.create<File>("METADATA_DESTINATION_DIRECTORY")

    val PATH_TO_KOTLIN_COMPILER_JAR = CompilerConfigurationKey.create<File>("PATH_TO_KOTLIN_COMPILER_JAR")

    val PRINT_VERSION = CompilerConfigurationKey.create<Boolean>("PRINT_VERSION")

    val SCRIPT_MODE = CompilerConfigurationKey.create<Boolean>("SCRIPT_MODE")

    val REPL_MODE = CompilerConfigurationKey.create<Boolean>("REPL_MODE")

    val KOTLIN_PATHS = CompilerConfigurationKey.create<KotlinPaths>("KOTLIN_PATHS")

    val ALLOW_NO_SOURCE_FILES = CompilerConfigurationKey.create<Boolean>("ALLOW_NO_SOURCE_FILES")

    val MODULE_CHUNK = CompilerConfigurationKey.create<ModuleChunk>("MODULE_CHUNK")

    val BUILD_FILE = CompilerConfigurationKey.create<File>("BUILD_FILE")

    val FREE_ARGS_FOR_SCRIPT = CompilerConfigurationKey.create<List<String>>("FREE_ARGS_FOR_SCRIPT")

    val DEFAULT_EXTENSION_FOR_SCRIPTS = CompilerConfigurationKey.create<String>("DEFAULT_EXTENSION_FOR_SCRIPTS")
}

val CLIConfigurationKeys.CONTENT_ROOTS: CompilerConfigurationKey<List<ContentRoot>>
    get() = CLIExtraConfigurationKeys.CONTENT_ROOTS

@Deprecated(
    "Please use CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY instead",
    ReplaceWith("CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY", "org.jetbrains.kotlin.config.CommonConfigurationKeys"),
    DeprecationLevel.ERROR,
)
@MessageCollectorAccess
val CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY: CompilerConfigurationKey<MessageCollector>
    get() = CLIExtraConfigurationKeys.MESSAGE_COLLECTOR_KEY

val CLIConfigurationKeys.ORIGINAL_MESSAGE_COLLECTOR_KEY: CompilerConfigurationKey<MessageCollector>
    get() = CLIExtraConfigurationKeys.ORIGINAL_MESSAGE_COLLECTOR_KEY

val CLIConfigurationKeys.RENDER_DIAGNOSTIC_INTERNAL_NAME: CompilerConfigurationKey<Boolean>
    get() = CLIExtraConfigurationKeys.RENDER_DIAGNOSTIC_INTERNAL_NAME

val CLIConfigurationKeys.TREAT_WARNINGS_AS_ERRORS: CompilerConfigurationKey<Boolean>
    get() = CLIExtraConfigurationKeys.TREAT_WARNINGS_AS_ERRORS

val CLIConfigurationKeys.ALLOW_KOTLIN_PACKAGE: CompilerConfigurationKey<Boolean>
    get() = CLIExtraConfigurationKeys.ALLOW_KOTLIN_PACKAGE

val CLIConfigurationKeys.METADATA_DESTINATION_DIRECTORY: CompilerConfigurationKey<File>
    get() = CLIExtraConfigurationKeys.METADATA_DESTINATION_DIRECTORY

val CLIConfigurationKeys.PATH_TO_KOTLIN_COMPILER_JAR: CompilerConfigurationKey<File>
    get() = CLIExtraConfigurationKeys.PATH_TO_KOTLIN_COMPILER_JAR

val CLIConfigurationKeys.PRINT_VERSION: CompilerConfigurationKey<Boolean>
    get() = CLIExtraConfigurationKeys.PRINT_VERSION

val CLIConfigurationKeys.SCRIPT_MODE: CompilerConfigurationKey<Boolean>
    get() = CLIExtraConfigurationKeys.SCRIPT_MODE

val CLIConfigurationKeys.REPL_MODE: CompilerConfigurationKey<Boolean>
    get() = CLIExtraConfigurationKeys.REPL_MODE

val CLIConfigurationKeys.KOTLIN_PATHS: CompilerConfigurationKey<KotlinPaths>
    get() = CLIExtraConfigurationKeys.KOTLIN_PATHS

val CLIConfigurationKeys.ALLOW_NO_SOURCE_FILES: CompilerConfigurationKey<Boolean>
    get() = CLIExtraConfigurationKeys.ALLOW_NO_SOURCE_FILES

val CLIConfigurationKeys.MODULE_CHUNK: CompilerConfigurationKey<ModuleChunk>
    get() = CLIExtraConfigurationKeys.MODULE_CHUNK

val CLIConfigurationKeys.BUILD_FILE: CompilerConfigurationKey<File>
    get() = CLIExtraConfigurationKeys.BUILD_FILE

val CLIConfigurationKeys.FREE_ARGS_FOR_SCRIPT: CompilerConfigurationKey<List<String>>
    get() = CLIExtraConfigurationKeys.FREE_ARGS_FOR_SCRIPT

val CLIConfigurationKeys.DEFAULT_EXTENSION_FOR_SCRIPTS: CompilerConfigurationKey<String>
    get() = CLIExtraConfigurationKeys.DEFAULT_EXTENSION_FOR_SCRIPTS

var CompilerConfiguration.contentRoots: List<ContentRoot>
    get() = getList(CLIConfigurationKeys.CONTENT_ROOTS)
    set(value) { put(CLIConfigurationKeys.CONTENT_ROOTS, value) }

var CompilerConfiguration.originalMessageCollectorKey: MessageCollector?
    get() = get(CLIConfigurationKeys.ORIGINAL_MESSAGE_COLLECTOR_KEY)
    set(value) { put(CLIConfigurationKeys.ORIGINAL_MESSAGE_COLLECTOR_KEY, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.renderDiagnosticInternalName: Boolean
    get() = getBoolean(CLIConfigurationKeys.RENDER_DIAGNOSTIC_INTERNAL_NAME)
    set(value) { put(CLIConfigurationKeys.RENDER_DIAGNOSTIC_INTERNAL_NAME, value) }

var CompilerConfiguration.treatWarningsAsErrors: Boolean
    get() = getBoolean(CLIConfigurationKeys.TREAT_WARNINGS_AS_ERRORS)
    set(value) { put(CLIConfigurationKeys.TREAT_WARNINGS_AS_ERRORS, value) }

var CompilerConfiguration.allowKotlinPackage: Boolean
    get() = getBoolean(CLIConfigurationKeys.ALLOW_KOTLIN_PACKAGE)
    set(value) { put(CLIConfigurationKeys.ALLOW_KOTLIN_PACKAGE, value) }

var CompilerConfiguration.metadataDestinationDirectory: File?
    get() = get(CLIConfigurationKeys.METADATA_DESTINATION_DIRECTORY)
    set(value) { put(CLIConfigurationKeys.METADATA_DESTINATION_DIRECTORY, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.pathToKotlinCompilerJar: File?
    get() = get(CLIConfigurationKeys.PATH_TO_KOTLIN_COMPILER_JAR)
    set(value) { put(CLIConfigurationKeys.PATH_TO_KOTLIN_COMPILER_JAR, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.printVersion: Boolean
    get() = getBoolean(CLIConfigurationKeys.PRINT_VERSION)
    set(value) { put(CLIConfigurationKeys.PRINT_VERSION, value) }

var CompilerConfiguration.scriptMode: Boolean
    get() = getBoolean(CLIConfigurationKeys.SCRIPT_MODE)
    set(value) { put(CLIConfigurationKeys.SCRIPT_MODE, value) }

var CompilerConfiguration.replMode: Boolean
    get() = getBoolean(CLIConfigurationKeys.REPL_MODE)
    set(value) { put(CLIConfigurationKeys.REPL_MODE, value) }

var CompilerConfiguration.kotlinPaths: KotlinPaths?
    get() = get(CLIConfigurationKeys.KOTLIN_PATHS)
    set(value) { put(CLIConfigurationKeys.KOTLIN_PATHS, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.allowNoSourceFiles: Boolean
    get() = getBoolean(CLIConfigurationKeys.ALLOW_NO_SOURCE_FILES)
    set(value) { put(CLIConfigurationKeys.ALLOW_NO_SOURCE_FILES, value) }

var CompilerConfiguration.moduleChunk: ModuleChunk?
    get() = get(CLIConfigurationKeys.MODULE_CHUNK)
    set(value) { put(CLIConfigurationKeys.MODULE_CHUNK, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.buildFile: File?
    get() = get(CLIConfigurationKeys.BUILD_FILE)
    set(value) { put(CLIConfigurationKeys.BUILD_FILE, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.freeArgsForScript: List<String>
    get() = getList(CLIConfigurationKeys.FREE_ARGS_FOR_SCRIPT)
    set(value) { put(CLIConfigurationKeys.FREE_ARGS_FOR_SCRIPT, value) }

var CompilerConfiguration.defaultExtensionForScripts: String?
    get() = get(CLIConfigurationKeys.DEFAULT_EXTENSION_FOR_SCRIPTS)
    set(value) { putIfNotNull(CLIConfigurationKeys.DEFAULT_EXTENSION_FOR_SCRIPTS, value) }
