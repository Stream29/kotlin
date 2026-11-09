/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("IncorrectFormatting", "unused")

package org.jetbrains.kotlin.konan.config

import org.jetbrains.kotlin.backend.konan.AllocationMode
import org.jetbrains.kotlin.backend.konan.LlvmVariant
import org.jetbrains.kotlin.backend.konan.TestRunnerKind
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.konan.target.CompilerOutputKind
import org.jetbrains.kotlin.konan.target.KonanTarget

internal object NativeExtraConfigurationKeys {
    val BUNDLE_ID: CompilerConfigurationKey<String> = CompilerConfigurationKey.create<String>("BUNDLE_ID")
    val CHECK_DEPENDENCIES: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create<Boolean>("CHECK_DEPENDENCIES")
    val DEBUG: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create<Boolean>("DEBUG")
    val FAKE_OVERRIDE_VALIDATOR: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create<Boolean>("FAKE_OVERRIDE_VALIDATOR")
    val EMIT_LAZY_OBJC_HEADER_FILE: CompilerConfigurationKey<String> = CompilerConfigurationKey.create<String>("EMIT_LAZY_OBJC_HEADER_FILE")
    val ENABLE_ASSERTIONS: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create<Boolean>("ENABLE_ASSERTIONS")
    val KONAN_ENTRY_POINT: CompilerConfigurationKey<String> = CompilerConfigurationKey.create<String>("KONAN_ENTRY_POINT")
    val EXPORTED_LIBRARIES: CompilerConfigurationKey<List<String>> = CompilerConfigurationKey.create<List<String>>("EXPORTED_LIBRARIES")
    val FULL_EXPORTED_NAME_PREFIX: CompilerConfigurationKey<String> = CompilerConfigurationKey.create<String>("FULL_EXPORTED_NAME_PREFIX")
    val KONAN_LIBRARY_TO_ADD_TO_CACHE: CompilerConfigurationKey<String> = CompilerConfigurationKey.create<String>("KONAN_LIBRARY_TO_ADD_TO_CACHE")
    val CACHE_DIRECTORIES: CompilerConfigurationKey<List<String>> = CompilerConfigurationKey.create<List<String>>("CACHE_DIRECTORIES")
    val AUTO_CACHEABLE_FROM: CompilerConfigurationKey<List<String>> = CompilerConfigurationKey.create<List<String>>("AUTO_CACHEABLE_FROM")
    val AUTO_CACHE_DIR: CompilerConfigurationKey<String> = CompilerConfigurationKey.create<String>("AUTO_CACHE_DIR")
    val INCREMENTAL_CACHE_DIR: CompilerConfigurationKey<String> = CompilerConfigurationKey.create<String>("INCREMENTAL_CACHE_DIR")
    val DUMP_BUILT_CACHES_TO: CompilerConfigurationKey<String> = CompilerConfigurationKey.create<String>("DUMP_BUILT_CACHES_TO")
    val CACHED_LIBRARIES: CompilerConfigurationKey<Map<String, String>> = CompilerConfigurationKey.create<Map<String, String>>("CACHED_LIBRARIES")
    val FILES_TO_CACHE: CompilerConfigurationKey<List<String>> = CompilerConfigurationKey.create<List<String>>("FILES_TO_CACHE")
    val MAKE_PER_FILE_CACHE: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create<Boolean>("MAKE_PER_FILE_CACHE")
    val CACHED_LIBRARY_DEPENDENCIES_FINGERPRINT: CompilerConfigurationKey<String> = CompilerConfigurationKey.create<String>("CACHED_LIBRARY_DEPENDENCIES_FINGERPRINT")
    val FRAMEWORK_IMPORT_HEADERS: CompilerConfigurationKey<List<String>> = CompilerConfigurationKey.create<List<String>>("FRAMEWORK_IMPORT_HEADERS")
    val KONAN_REFINES_MODULES: CompilerConfigurationKey<List<String>> = CompilerConfigurationKey.create<List<String>>("KONAN_REFINES_MODULES")
    val GENERATE_TEST_RUNNER: CompilerConfigurationKey<TestRunnerKind> = CompilerConfigurationKey.create<TestRunnerKind>("GENERATE_TEST_RUNNER")
    val KONAN_INCLUDED_BINARIES: CompilerConfigurationKey<List<String>> = CompilerConfigurationKey.create<List<String>>("KONAN_INCLUDED_BINARIES")
    val LIGHT_DEBUG: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create<Boolean>("LIGHT_DEBUG")
    val GENERATE_DEBUG_TRAMPOLINE: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create<Boolean>("GENERATE_DEBUG_TRAMPOLINE")
    val LINKER_ARGS: CompilerConfigurationKey<List<String>> = CompilerConfigurationKey.create<List<String>>("LINKER_ARGS")
    val LIST_TARGETS: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create<Boolean>("LIST_TARGETS")
    val KONAN_MANIFEST_ADDEND: CompilerConfigurationKey<String> = CompilerConfigurationKey.create<String>("KONAN_MANIFEST_ADDEND")
    val KONAN_GENERATED_HEADER_KLIB_PATH: CompilerConfigurationKey<String> = CompilerConfigurationKey.create<String>("KONAN_GENERATED_HEADER_KLIB_PATH")
    val KONAN_NATIVE_LIBRARIES: CompilerConfigurationKey<List<String>> = CompilerConfigurationKey.create<List<String>>("KONAN_NATIVE_LIBRARIES")
    val NOMAIN: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create<Boolean>("NOMAIN")
    val KONAN_DONT_COMPRESS_KLIB: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create<Boolean>("KONAN_DONT_COMPRESS_KLIB")
    val OPTIMIZATION: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create<Boolean>("OPTIMIZATION")
    val KONAN_OUTPUT_PATH: CompilerConfigurationKey<String> = CompilerConfigurationKey.create<String>("KONAN_OUTPUT_PATH")
    val OVERRIDE_CLANG_OPTIONS: CompilerConfigurationKey<List<String>> = CompilerConfigurationKey.create<List<String>>("OVERRIDE_CLANG_OPTIONS")
    val ALLOCATION_MODE: CompilerConfigurationKey<AllocationMode> = CompilerConfigurationKey.create<AllocationMode>("ALLOCATION_MODE")
    val KONAN_PRINT_BITCODE: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create<Boolean>("KONAN_PRINT_BITCODE")
    val KONAN_PRINT_IR: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create<Boolean>("KONAN_PRINT_IR")
    val KONAN_PRINT_FILES: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create<Boolean>("KONAN_PRINT_FILES")
    val KONAN_PRODUCED_ARTIFACT_KIND: CompilerConfigurationKey<CompilerOutputKind> = CompilerConfigurationKey.create<CompilerOutputKind>("KONAN_PRODUCED_ARTIFACT_KIND")
    val KONAN_PURGE_USER_LIBS: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create<Boolean>("KONAN_PURGE_USER_LIBS")
    val RUNTIME_FILE: CompilerConfigurationKey<String> = CompilerConfigurationKey.create<String>("RUNTIME_FILE")
    val KONAN_SHORT_MODULE_NAME: CompilerConfigurationKey<String> = CompilerConfigurationKey.create<String>("KONAN_SHORT_MODULE_NAME")
    val STATIC_FRAMEWORK: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create<Boolean>("STATIC_FRAMEWORK")
    val KONAN_TARGET: CompilerConfigurationKey<String> = CompilerConfigurationKey.create<String>("KONAN_TARGET")
    val TEMPORARY_FILES_DIR: CompilerConfigurationKey<String> = CompilerConfigurationKey.create<String>("TEMPORARY_FILES_DIR")
    val SAVE_LLVM_IR: CompilerConfigurationKey<List<String>> = CompilerConfigurationKey.create<List<String>>("SAVE_LLVM_IR")
    val VERIFY_BITCODE: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create<Boolean>("VERIFY_BITCODE")
    val VERIFY_COMPILER: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create<Boolean>("VERIFY_COMPILER")
    val KONAN_WRITE_DEPENDENCIES_OF_PRODUCED_KLIB_TO: CompilerConfigurationKey<String> = CompilerConfigurationKey.create<String>("KONAN_WRITE_DEPENDENCIES_OF_PRODUCED_KLIB_TO")
    val DEBUG_INFO_VERSION: CompilerConfigurationKey<Int> = CompilerConfigurationKey.create<Int>("DEBUG_INFO_VERSION")
    val OBJC_GENERICS: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create<Boolean>("OBJC_GENERICS")
    val DEBUG_PREFIX_MAP: CompilerConfigurationKey<Map<String, String>> = CompilerConfigurationKey.create<Map<String, String>>("DEBUG_PREFIX_MAP")
    val PRE_LINK_CACHES: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create<Boolean>("PRE_LINK_CACHES")
    val OVERRIDE_KONAN_PROPERTIES: CompilerConfigurationKey<Map<String, String>> = CompilerConfigurationKey.create<Map<String, String>>("OVERRIDE_KONAN_PROPERTIES")
    val PROPERTY_LAZY_INITIALIZATION: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create<Boolean>("PROPERTY_LAZY_INITIALIZATION")
    val EXTERNAL_DEPENDENCIES: CompilerConfigurationKey<String> = CompilerConfigurationKey.create<String>("EXTERNAL_DEPENDENCIES")
    val LLVM_VARIANT: CompilerConfigurationKey<LlvmVariant> = CompilerConfigurationKey.create<LlvmVariant>("LLVM_VARIANT")
    val RUNTIME_LOGS: CompilerConfigurationKey<String> = CompilerConfigurationKey.create<String>("RUNTIME_LOGS")
    val TEST_DUMP_OUTPUT_PATH: CompilerConfigurationKey<String> = CompilerConfigurationKey.create<String>("TEST_DUMP_OUTPUT_PATH")
    val OMIT_FRAMEWORK_BINARY: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create<Boolean>("OMIT_FRAMEWORK_BINARY")
    val COMPILE_FROM_BITCODE: CompilerConfigurationKey<String> = CompilerConfigurationKey.create<String>("COMPILE_FROM_BITCODE")
    val SERIALIZED_DEPENDENCIES: CompilerConfigurationKey<String> = CompilerConfigurationKey.create<String>("SERIALIZED_DEPENDENCIES")
    val SAVE_DEPENDENCIES_PATH: CompilerConfigurationKey<String> = CompilerConfigurationKey.create<String>("SAVE_DEPENDENCIES_PATH")
    val SAVE_LLVM_IR_DIRECTORY: CompilerConfigurationKey<String> = CompilerConfigurationKey.create<String>("SAVE_LLVM_IR_DIRECTORY")
    val KONAN_DATA_DIR: CompilerConfigurationKey<String> = CompilerConfigurationKey.create<String>("KONAN_DATA_DIR")
    val KONAN_MANIFEST_NATIVE_TARGETS: CompilerConfigurationKey<List<KonanTarget>> = CompilerConfigurationKey.create<List<KonanTarget>>("KONAN_MANIFEST_NATIVE_TARGETS")
    val LLVM_MODULE_PASSES: CompilerConfigurationKey<String> = CompilerConfigurationKey.create<String>("LLVM_MODULE_PASSES")
    val LLVM_LTO_PASSES: CompilerConfigurationKey<String> = CompilerConfigurationKey.create<String>("LLVM_LTO_PASSES")
}

val NativeConfigurationKeys.BUNDLE_ID: CompilerConfigurationKey<String>
    get() = NativeExtraConfigurationKeys.BUNDLE_ID

val NativeConfigurationKeys.CHECK_DEPENDENCIES: CompilerConfigurationKey<Boolean>
    get() = NativeExtraConfigurationKeys.CHECK_DEPENDENCIES

val NativeConfigurationKeys.DEBUG: CompilerConfigurationKey<Boolean>
    get() = NativeExtraConfigurationKeys.DEBUG

val NativeConfigurationKeys.FAKE_OVERRIDE_VALIDATOR: CompilerConfigurationKey<Boolean>
    get() = NativeExtraConfigurationKeys.FAKE_OVERRIDE_VALIDATOR

val NativeConfigurationKeys.EMIT_LAZY_OBJC_HEADER_FILE: CompilerConfigurationKey<String>
    get() = NativeExtraConfigurationKeys.EMIT_LAZY_OBJC_HEADER_FILE

val NativeConfigurationKeys.ENABLE_ASSERTIONS: CompilerConfigurationKey<Boolean>
    get() = NativeExtraConfigurationKeys.ENABLE_ASSERTIONS

val NativeConfigurationKeys.KONAN_ENTRY_POINT: CompilerConfigurationKey<String>
    get() = NativeExtraConfigurationKeys.KONAN_ENTRY_POINT

val NativeConfigurationKeys.EXPORTED_LIBRARIES: CompilerConfigurationKey<List<String>>
    get() = NativeExtraConfigurationKeys.EXPORTED_LIBRARIES

val NativeConfigurationKeys.FULL_EXPORTED_NAME_PREFIX: CompilerConfigurationKey<String>
    get() = NativeExtraConfigurationKeys.FULL_EXPORTED_NAME_PREFIX

val NativeConfigurationKeys.KONAN_LIBRARY_TO_ADD_TO_CACHE: CompilerConfigurationKey<String>
    get() = NativeExtraConfigurationKeys.KONAN_LIBRARY_TO_ADD_TO_CACHE

val NativeConfigurationKeys.CACHE_DIRECTORIES: CompilerConfigurationKey<List<String>>
    get() = NativeExtraConfigurationKeys.CACHE_DIRECTORIES

val NativeConfigurationKeys.AUTO_CACHEABLE_FROM: CompilerConfigurationKey<List<String>>
    get() = NativeExtraConfigurationKeys.AUTO_CACHEABLE_FROM

val NativeConfigurationKeys.AUTO_CACHE_DIR: CompilerConfigurationKey<String>
    get() = NativeExtraConfigurationKeys.AUTO_CACHE_DIR

val NativeConfigurationKeys.INCREMENTAL_CACHE_DIR: CompilerConfigurationKey<String>
    get() = NativeExtraConfigurationKeys.INCREMENTAL_CACHE_DIR

val NativeConfigurationKeys.DUMP_BUILT_CACHES_TO: CompilerConfigurationKey<String>
    get() = NativeExtraConfigurationKeys.DUMP_BUILT_CACHES_TO

val NativeConfigurationKeys.CACHED_LIBRARIES: CompilerConfigurationKey<Map<String, String>>
    get() = NativeExtraConfigurationKeys.CACHED_LIBRARIES

val NativeConfigurationKeys.FILES_TO_CACHE: CompilerConfigurationKey<List<String>>
    get() = NativeExtraConfigurationKeys.FILES_TO_CACHE

val NativeConfigurationKeys.MAKE_PER_FILE_CACHE: CompilerConfigurationKey<Boolean>
    get() = NativeExtraConfigurationKeys.MAKE_PER_FILE_CACHE

val NativeConfigurationKeys.CACHED_LIBRARY_DEPENDENCIES_FINGERPRINT: CompilerConfigurationKey<String>
    get() = NativeExtraConfigurationKeys.CACHED_LIBRARY_DEPENDENCIES_FINGERPRINT

val NativeConfigurationKeys.FRAMEWORK_IMPORT_HEADERS: CompilerConfigurationKey<List<String>>
    get() = NativeExtraConfigurationKeys.FRAMEWORK_IMPORT_HEADERS

val NativeConfigurationKeys.KONAN_REFINES_MODULES: CompilerConfigurationKey<List<String>>
    get() = NativeExtraConfigurationKeys.KONAN_REFINES_MODULES

val NativeConfigurationKeys.GENERATE_TEST_RUNNER: CompilerConfigurationKey<TestRunnerKind>
    get() = NativeExtraConfigurationKeys.GENERATE_TEST_RUNNER

val NativeConfigurationKeys.KONAN_INCLUDED_BINARIES: CompilerConfigurationKey<List<String>>
    get() = NativeExtraConfigurationKeys.KONAN_INCLUDED_BINARIES

val NativeConfigurationKeys.LIGHT_DEBUG: CompilerConfigurationKey<Boolean>
    get() = NativeExtraConfigurationKeys.LIGHT_DEBUG

val NativeConfigurationKeys.GENERATE_DEBUG_TRAMPOLINE: CompilerConfigurationKey<Boolean>
    get() = NativeExtraConfigurationKeys.GENERATE_DEBUG_TRAMPOLINE

val NativeConfigurationKeys.LINKER_ARGS: CompilerConfigurationKey<List<String>>
    get() = NativeExtraConfigurationKeys.LINKER_ARGS

val NativeConfigurationKeys.LIST_TARGETS: CompilerConfigurationKey<Boolean>
    get() = NativeExtraConfigurationKeys.LIST_TARGETS

val NativeConfigurationKeys.KONAN_MANIFEST_ADDEND: CompilerConfigurationKey<String>
    get() = NativeExtraConfigurationKeys.KONAN_MANIFEST_ADDEND

val NativeConfigurationKeys.KONAN_GENERATED_HEADER_KLIB_PATH: CompilerConfigurationKey<String>
    get() = NativeExtraConfigurationKeys.KONAN_GENERATED_HEADER_KLIB_PATH

val NativeConfigurationKeys.KONAN_NATIVE_LIBRARIES: CompilerConfigurationKey<List<String>>
    get() = NativeExtraConfigurationKeys.KONAN_NATIVE_LIBRARIES

val NativeConfigurationKeys.NOMAIN: CompilerConfigurationKey<Boolean>
    get() = NativeExtraConfigurationKeys.NOMAIN

val NativeConfigurationKeys.KONAN_DONT_COMPRESS_KLIB: CompilerConfigurationKey<Boolean>
    get() = NativeExtraConfigurationKeys.KONAN_DONT_COMPRESS_KLIB

val NativeConfigurationKeys.OPTIMIZATION: CompilerConfigurationKey<Boolean>
    get() = NativeExtraConfigurationKeys.OPTIMIZATION

val NativeConfigurationKeys.KONAN_OUTPUT_PATH: CompilerConfigurationKey<String>
    get() = NativeExtraConfigurationKeys.KONAN_OUTPUT_PATH

val NativeConfigurationKeys.OVERRIDE_CLANG_OPTIONS: CompilerConfigurationKey<List<String>>
    get() = NativeExtraConfigurationKeys.OVERRIDE_CLANG_OPTIONS

val NativeConfigurationKeys.ALLOCATION_MODE: CompilerConfigurationKey<AllocationMode>
    get() = NativeExtraConfigurationKeys.ALLOCATION_MODE

val NativeConfigurationKeys.KONAN_PRINT_BITCODE: CompilerConfigurationKey<Boolean>
    get() = NativeExtraConfigurationKeys.KONAN_PRINT_BITCODE

val NativeConfigurationKeys.KONAN_PRINT_IR: CompilerConfigurationKey<Boolean>
    get() = NativeExtraConfigurationKeys.KONAN_PRINT_IR

val NativeConfigurationKeys.KONAN_PRINT_FILES: CompilerConfigurationKey<Boolean>
    get() = NativeExtraConfigurationKeys.KONAN_PRINT_FILES

val NativeConfigurationKeys.KONAN_PRODUCED_ARTIFACT_KIND: CompilerConfigurationKey<CompilerOutputKind>
    get() = NativeExtraConfigurationKeys.KONAN_PRODUCED_ARTIFACT_KIND

val NativeConfigurationKeys.KONAN_PURGE_USER_LIBS: CompilerConfigurationKey<Boolean>
    get() = NativeExtraConfigurationKeys.KONAN_PURGE_USER_LIBS

val NativeConfigurationKeys.RUNTIME_FILE: CompilerConfigurationKey<String>
    get() = NativeExtraConfigurationKeys.RUNTIME_FILE

val NativeConfigurationKeys.KONAN_SHORT_MODULE_NAME: CompilerConfigurationKey<String>
    get() = NativeExtraConfigurationKeys.KONAN_SHORT_MODULE_NAME

val NativeConfigurationKeys.STATIC_FRAMEWORK: CompilerConfigurationKey<Boolean>
    get() = NativeExtraConfigurationKeys.STATIC_FRAMEWORK

val NativeConfigurationKeys.KONAN_TARGET: CompilerConfigurationKey<String>
    get() = NativeExtraConfigurationKeys.KONAN_TARGET

val NativeConfigurationKeys.TEMPORARY_FILES_DIR: CompilerConfigurationKey<String>
    get() = NativeExtraConfigurationKeys.TEMPORARY_FILES_DIR

val NativeConfigurationKeys.SAVE_LLVM_IR: CompilerConfigurationKey<List<String>>
    get() = NativeExtraConfigurationKeys.SAVE_LLVM_IR

val NativeConfigurationKeys.VERIFY_BITCODE: CompilerConfigurationKey<Boolean>
    get() = NativeExtraConfigurationKeys.VERIFY_BITCODE

val NativeConfigurationKeys.VERIFY_COMPILER: CompilerConfigurationKey<Boolean>
    get() = NativeExtraConfigurationKeys.VERIFY_COMPILER

val NativeConfigurationKeys.KONAN_WRITE_DEPENDENCIES_OF_PRODUCED_KLIB_TO: CompilerConfigurationKey<String>
    get() = NativeExtraConfigurationKeys.KONAN_WRITE_DEPENDENCIES_OF_PRODUCED_KLIB_TO

val NativeConfigurationKeys.DEBUG_INFO_VERSION: CompilerConfigurationKey<Int>
    get() = NativeExtraConfigurationKeys.DEBUG_INFO_VERSION

val NativeConfigurationKeys.OBJC_GENERICS: CompilerConfigurationKey<Boolean>
    get() = NativeExtraConfigurationKeys.OBJC_GENERICS

val NativeConfigurationKeys.DEBUG_PREFIX_MAP: CompilerConfigurationKey<Map<String, String>>
    get() = NativeExtraConfigurationKeys.DEBUG_PREFIX_MAP

val NativeConfigurationKeys.PRE_LINK_CACHES: CompilerConfigurationKey<Boolean>
    get() = NativeExtraConfigurationKeys.PRE_LINK_CACHES

val NativeConfigurationKeys.OVERRIDE_KONAN_PROPERTIES: CompilerConfigurationKey<Map<String, String>>
    get() = NativeExtraConfigurationKeys.OVERRIDE_KONAN_PROPERTIES

val NativeConfigurationKeys.PROPERTY_LAZY_INITIALIZATION: CompilerConfigurationKey<Boolean>
    get() = NativeExtraConfigurationKeys.PROPERTY_LAZY_INITIALIZATION

val NativeConfigurationKeys.EXTERNAL_DEPENDENCIES: CompilerConfigurationKey<String>
    get() = NativeExtraConfigurationKeys.EXTERNAL_DEPENDENCIES

val NativeConfigurationKeys.LLVM_VARIANT: CompilerConfigurationKey<LlvmVariant>
    get() = NativeExtraConfigurationKeys.LLVM_VARIANT

val NativeConfigurationKeys.RUNTIME_LOGS: CompilerConfigurationKey<String>
    get() = NativeExtraConfigurationKeys.RUNTIME_LOGS

val NativeConfigurationKeys.TEST_DUMP_OUTPUT_PATH: CompilerConfigurationKey<String>
    get() = NativeExtraConfigurationKeys.TEST_DUMP_OUTPUT_PATH

val NativeConfigurationKeys.OMIT_FRAMEWORK_BINARY: CompilerConfigurationKey<Boolean>
    get() = NativeExtraConfigurationKeys.OMIT_FRAMEWORK_BINARY

val NativeConfigurationKeys.COMPILE_FROM_BITCODE: CompilerConfigurationKey<String>
    get() = NativeExtraConfigurationKeys.COMPILE_FROM_BITCODE

val NativeConfigurationKeys.SERIALIZED_DEPENDENCIES: CompilerConfigurationKey<String>
    get() = NativeExtraConfigurationKeys.SERIALIZED_DEPENDENCIES

val NativeConfigurationKeys.SAVE_DEPENDENCIES_PATH: CompilerConfigurationKey<String>
    get() = NativeExtraConfigurationKeys.SAVE_DEPENDENCIES_PATH

val NativeConfigurationKeys.SAVE_LLVM_IR_DIRECTORY: CompilerConfigurationKey<String>
    get() = NativeExtraConfigurationKeys.SAVE_LLVM_IR_DIRECTORY

val NativeConfigurationKeys.KONAN_DATA_DIR: CompilerConfigurationKey<String>
    get() = NativeExtraConfigurationKeys.KONAN_DATA_DIR

val NativeConfigurationKeys.KONAN_MANIFEST_NATIVE_TARGETS: CompilerConfigurationKey<List<KonanTarget>>
    get() = NativeExtraConfigurationKeys.KONAN_MANIFEST_NATIVE_TARGETS

val NativeConfigurationKeys.LLVM_MODULE_PASSES: CompilerConfigurationKey<String>
    get() = NativeExtraConfigurationKeys.LLVM_MODULE_PASSES

val NativeConfigurationKeys.LLVM_LTO_PASSES: CompilerConfigurationKey<String>
    get() = NativeExtraConfigurationKeys.LLVM_LTO_PASSES

var CompilerConfiguration.bundleId: String?
    get() = get(NativeConfigurationKeys.BUNDLE_ID)
    set(value) { put(NativeConfigurationKeys.BUNDLE_ID, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.checkDependencies: Boolean
    get() = getBoolean(NativeConfigurationKeys.CHECK_DEPENDENCIES)
    set(value) { put(NativeConfigurationKeys.CHECK_DEPENDENCIES, value) }

var CompilerConfiguration.debug: Boolean
    get() = getBoolean(NativeConfigurationKeys.DEBUG)
    set(value) { put(NativeConfigurationKeys.DEBUG, value) }

var CompilerConfiguration.fakeOverrideValidator: Boolean
    get() = getBoolean(NativeConfigurationKeys.FAKE_OVERRIDE_VALIDATOR)
    set(value) { put(NativeConfigurationKeys.FAKE_OVERRIDE_VALIDATOR, value) }

var CompilerConfiguration.emitLazyObjcHeaderFile: String?
    get() = get(NativeConfigurationKeys.EMIT_LAZY_OBJC_HEADER_FILE)
    set(value) { put(NativeConfigurationKeys.EMIT_LAZY_OBJC_HEADER_FILE, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.enableAssertions: Boolean
    get() = getBoolean(NativeConfigurationKeys.ENABLE_ASSERTIONS)
    set(value) { put(NativeConfigurationKeys.ENABLE_ASSERTIONS, value) }

var CompilerConfiguration.konanEntryPoint: String?
    get() = get(NativeConfigurationKeys.KONAN_ENTRY_POINT)
    set(value) { put(NativeConfigurationKeys.KONAN_ENTRY_POINT, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.exportedLibraries: List<String>
    get() = getList(NativeConfigurationKeys.EXPORTED_LIBRARIES)
    set(value) { put(NativeConfigurationKeys.EXPORTED_LIBRARIES, value) }

var CompilerConfiguration.fullExportedNamePrefix: String?
    get() = get(NativeConfigurationKeys.FULL_EXPORTED_NAME_PREFIX)
    set(value) { put(NativeConfigurationKeys.FULL_EXPORTED_NAME_PREFIX, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.konanLibraryToAddToCache: String?
    get() = get(NativeConfigurationKeys.KONAN_LIBRARY_TO_ADD_TO_CACHE)
    set(value) { put(NativeConfigurationKeys.KONAN_LIBRARY_TO_ADD_TO_CACHE, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.cacheDirectories: List<String>
    get() = getList(NativeConfigurationKeys.CACHE_DIRECTORIES)
    set(value) { put(NativeConfigurationKeys.CACHE_DIRECTORIES, value) }

var CompilerConfiguration.autoCacheableFrom: List<String>
    get() = getList(NativeConfigurationKeys.AUTO_CACHEABLE_FROM)
    set(value) { put(NativeConfigurationKeys.AUTO_CACHEABLE_FROM, value) }

var CompilerConfiguration.autoCacheDir: String?
    get() = get(NativeConfigurationKeys.AUTO_CACHE_DIR)
    set(value) { put(NativeConfigurationKeys.AUTO_CACHE_DIR, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.incrementalCacheDir: String?
    get() = get(NativeConfigurationKeys.INCREMENTAL_CACHE_DIR)
    set(value) { put(NativeConfigurationKeys.INCREMENTAL_CACHE_DIR, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.dumpBuiltCachesTo: String?
    get() = get(NativeConfigurationKeys.DUMP_BUILT_CACHES_TO)
    set(value) { put(NativeConfigurationKeys.DUMP_BUILT_CACHES_TO, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.cachedLibraries: Map<String, String>
    get() = getMap(NativeConfigurationKeys.CACHED_LIBRARIES)
    set(value) { put(NativeConfigurationKeys.CACHED_LIBRARIES, value) }

var CompilerConfiguration.filesToCache: List<String>
    get() = getList(NativeConfigurationKeys.FILES_TO_CACHE)
    set(value) { put(NativeConfigurationKeys.FILES_TO_CACHE, value) }

var CompilerConfiguration.makePerFileCache: Boolean
    get() = getBoolean(NativeConfigurationKeys.MAKE_PER_FILE_CACHE)
    set(value) { put(NativeConfigurationKeys.MAKE_PER_FILE_CACHE, value) }

var CompilerConfiguration.cachedLibraryDependenciesFingerprint: String?
    get() = get(NativeConfigurationKeys.CACHED_LIBRARY_DEPENDENCIES_FINGERPRINT)
    set(value) { put(NativeConfigurationKeys.CACHED_LIBRARY_DEPENDENCIES_FINGERPRINT, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.frameworkImportHeaders: List<String>
    get() = getList(NativeConfigurationKeys.FRAMEWORK_IMPORT_HEADERS)
    set(value) { put(NativeConfigurationKeys.FRAMEWORK_IMPORT_HEADERS, value) }

var CompilerConfiguration.konanRefinesModules: List<String>
    get() = getList(NativeConfigurationKeys.KONAN_REFINES_MODULES)
    set(value) { put(NativeConfigurationKeys.KONAN_REFINES_MODULES, value) }

var CompilerConfiguration.generateTestRunner: TestRunnerKind?
    get() = get(NativeConfigurationKeys.GENERATE_TEST_RUNNER)
    set(value) { put(NativeConfigurationKeys.GENERATE_TEST_RUNNER, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.konanIncludedBinaries: List<String>
    get() = getList(NativeConfigurationKeys.KONAN_INCLUDED_BINARIES)
    set(value) { put(NativeConfigurationKeys.KONAN_INCLUDED_BINARIES, value) }

var CompilerConfiguration.lightDebug: Boolean
    get() = getBoolean(NativeConfigurationKeys.LIGHT_DEBUG)
    set(value) { put(NativeConfigurationKeys.LIGHT_DEBUG, value) }

var CompilerConfiguration.generateDebugTrampoline: Boolean
    get() = getBoolean(NativeConfigurationKeys.GENERATE_DEBUG_TRAMPOLINE)
    set(value) { put(NativeConfigurationKeys.GENERATE_DEBUG_TRAMPOLINE, value) }

var CompilerConfiguration.linkerArgs: List<String>
    get() = getList(NativeConfigurationKeys.LINKER_ARGS)
    set(value) { put(NativeConfigurationKeys.LINKER_ARGS, value) }

var CompilerConfiguration.listTargets: Boolean
    get() = getBoolean(NativeConfigurationKeys.LIST_TARGETS)
    set(value) { put(NativeConfigurationKeys.LIST_TARGETS, value) }

var CompilerConfiguration.konanManifestAddend: String?
    get() = get(NativeConfigurationKeys.KONAN_MANIFEST_ADDEND)
    set(value) { put(NativeConfigurationKeys.KONAN_MANIFEST_ADDEND, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.konanGeneratedHeaderKlibPath: String?
    get() = get(NativeConfigurationKeys.KONAN_GENERATED_HEADER_KLIB_PATH)
    set(value) { put(NativeConfigurationKeys.KONAN_GENERATED_HEADER_KLIB_PATH, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.konanNativeLibraries: List<String>
    get() = getList(NativeConfigurationKeys.KONAN_NATIVE_LIBRARIES)
    set(value) { put(NativeConfigurationKeys.KONAN_NATIVE_LIBRARIES, value) }

var CompilerConfiguration.nomain: Boolean
    get() = getBoolean(NativeConfigurationKeys.NOMAIN)
    set(value) { put(NativeConfigurationKeys.NOMAIN, value) }

var CompilerConfiguration.konanDontCompressKlib: Boolean
    get() = getBoolean(NativeConfigurationKeys.KONAN_DONT_COMPRESS_KLIB)
    set(value) { put(NativeConfigurationKeys.KONAN_DONT_COMPRESS_KLIB, value) }

var CompilerConfiguration.optimization: Boolean
    get() = getBoolean(NativeConfigurationKeys.OPTIMIZATION)
    set(value) { put(NativeConfigurationKeys.OPTIMIZATION, value) }

var CompilerConfiguration.konanOutputPath: String?
    get() = get(NativeConfigurationKeys.KONAN_OUTPUT_PATH)
    set(value) { put(NativeConfigurationKeys.KONAN_OUTPUT_PATH, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.overrideClangOptions: List<String>
    get() = getList(NativeConfigurationKeys.OVERRIDE_CLANG_OPTIONS)
    set(value) { put(NativeConfigurationKeys.OVERRIDE_CLANG_OPTIONS, value) }

var CompilerConfiguration.allocationMode: AllocationMode?
    get() = get(NativeConfigurationKeys.ALLOCATION_MODE)
    set(value) { put(NativeConfigurationKeys.ALLOCATION_MODE, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.konanPrintBitcode: Boolean
    get() = getBoolean(NativeConfigurationKeys.KONAN_PRINT_BITCODE)
    set(value) { put(NativeConfigurationKeys.KONAN_PRINT_BITCODE, value) }

var CompilerConfiguration.konanPrintIr: Boolean
    get() = getBoolean(NativeConfigurationKeys.KONAN_PRINT_IR)
    set(value) { put(NativeConfigurationKeys.KONAN_PRINT_IR, value) }

var CompilerConfiguration.konanPrintFiles: Boolean
    get() = getBoolean(NativeConfigurationKeys.KONAN_PRINT_FILES)
    set(value) { put(NativeConfigurationKeys.KONAN_PRINT_FILES, value) }

var CompilerConfiguration.konanProducedArtifactKind: CompilerOutputKind?
    get() = get(NativeConfigurationKeys.KONAN_PRODUCED_ARTIFACT_KIND)
    set(value) { put(NativeConfigurationKeys.KONAN_PRODUCED_ARTIFACT_KIND, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.konanPurgeUserLibs: Boolean
    get() = getBoolean(NativeConfigurationKeys.KONAN_PURGE_USER_LIBS)
    set(value) { put(NativeConfigurationKeys.KONAN_PURGE_USER_LIBS, value) }

var CompilerConfiguration.runtimeFile: String?
    get() = get(NativeConfigurationKeys.RUNTIME_FILE)
    set(value) { put(NativeConfigurationKeys.RUNTIME_FILE, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.konanShortModuleName: String?
    get() = get(NativeConfigurationKeys.KONAN_SHORT_MODULE_NAME)
    set(value) { put(NativeConfigurationKeys.KONAN_SHORT_MODULE_NAME, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.staticFramework: Boolean
    get() = getBoolean(NativeConfigurationKeys.STATIC_FRAMEWORK)
    set(value) { put(NativeConfigurationKeys.STATIC_FRAMEWORK, value) }

var CompilerConfiguration.konanTarget: String?
    get() = get(NativeConfigurationKeys.KONAN_TARGET)
    set(value) { put(NativeConfigurationKeys.KONAN_TARGET, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.temporaryFilesDir: String?
    get() = get(NativeConfigurationKeys.TEMPORARY_FILES_DIR)
    set(value) { put(NativeConfigurationKeys.TEMPORARY_FILES_DIR, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.saveLlvmIr: List<String>
    get() = getList(NativeConfigurationKeys.SAVE_LLVM_IR)
    set(value) { put(NativeConfigurationKeys.SAVE_LLVM_IR, value) }

var CompilerConfiguration.verifyBitcode: Boolean
    get() = getBoolean(NativeConfigurationKeys.VERIFY_BITCODE)
    set(value) { put(NativeConfigurationKeys.VERIFY_BITCODE, value) }

var CompilerConfiguration.verifyCompiler: Boolean
    get() = getBoolean(NativeConfigurationKeys.VERIFY_COMPILER)
    set(value) { put(NativeConfigurationKeys.VERIFY_COMPILER, value) }

var CompilerConfiguration.konanWriteDependenciesOfProducedKlibTo: String?
    get() = get(NativeConfigurationKeys.KONAN_WRITE_DEPENDENCIES_OF_PRODUCED_KLIB_TO)
    set(value) { put(NativeConfigurationKeys.KONAN_WRITE_DEPENDENCIES_OF_PRODUCED_KLIB_TO, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.debugInfoVersion: Int?
    get() = get(NativeConfigurationKeys.DEBUG_INFO_VERSION)
    set(value) { put(NativeConfigurationKeys.DEBUG_INFO_VERSION, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.objcGenerics: Boolean
    get() = getBoolean(NativeConfigurationKeys.OBJC_GENERICS)
    set(value) { put(NativeConfigurationKeys.OBJC_GENERICS, value) }

var CompilerConfiguration.debugPrefixMap: Map<String, String>
    get() = getMap(NativeConfigurationKeys.DEBUG_PREFIX_MAP)
    set(value) { put(NativeConfigurationKeys.DEBUG_PREFIX_MAP, value) }

var CompilerConfiguration.preLinkCaches: Boolean
    get() = getBoolean(NativeConfigurationKeys.PRE_LINK_CACHES)
    set(value) { put(NativeConfigurationKeys.PRE_LINK_CACHES, value) }

var CompilerConfiguration.overrideKonanProperties: Map<String, String>
    get() = getMap(NativeConfigurationKeys.OVERRIDE_KONAN_PROPERTIES)
    set(value) { put(NativeConfigurationKeys.OVERRIDE_KONAN_PROPERTIES, value) }

var CompilerConfiguration.propertyLazyInitialization: Boolean
    get() = getBoolean(NativeConfigurationKeys.PROPERTY_LAZY_INITIALIZATION)
    set(value) { put(NativeConfigurationKeys.PROPERTY_LAZY_INITIALIZATION, value) }

var CompilerConfiguration.externalDependencies: String?
    get() = get(NativeConfigurationKeys.EXTERNAL_DEPENDENCIES)
    set(value) { put(NativeConfigurationKeys.EXTERNAL_DEPENDENCIES, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.llvmVariant: LlvmVariant?
    get() = get(NativeConfigurationKeys.LLVM_VARIANT)
    set(value) { put(NativeConfigurationKeys.LLVM_VARIANT, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.runtimeLogs: String?
    get() = get(NativeConfigurationKeys.RUNTIME_LOGS)
    set(value) { put(NativeConfigurationKeys.RUNTIME_LOGS, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.testDumpOutputPath: String?
    get() = get(NativeConfigurationKeys.TEST_DUMP_OUTPUT_PATH)
    set(value) { put(NativeConfigurationKeys.TEST_DUMP_OUTPUT_PATH, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.omitFrameworkBinary: Boolean
    get() = getBoolean(NativeConfigurationKeys.OMIT_FRAMEWORK_BINARY)
    set(value) { put(NativeConfigurationKeys.OMIT_FRAMEWORK_BINARY, value) }

var CompilerConfiguration.compileFromBitcode: String?
    get() = get(NativeConfigurationKeys.COMPILE_FROM_BITCODE)
    set(value) { put(NativeConfigurationKeys.COMPILE_FROM_BITCODE, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.serializedDependencies: String?
    get() = get(NativeConfigurationKeys.SERIALIZED_DEPENDENCIES)
    set(value) { put(NativeConfigurationKeys.SERIALIZED_DEPENDENCIES, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.saveDependenciesPath: String?
    get() = get(NativeConfigurationKeys.SAVE_DEPENDENCIES_PATH)
    set(value) { put(NativeConfigurationKeys.SAVE_DEPENDENCIES_PATH, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.saveLlvmIrDirectory: String?
    get() = get(NativeConfigurationKeys.SAVE_LLVM_IR_DIRECTORY)
    set(value) { put(NativeConfigurationKeys.SAVE_LLVM_IR_DIRECTORY, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.konanDataDir: String?
    get() = get(NativeConfigurationKeys.KONAN_DATA_DIR)
    set(value) { put(NativeConfigurationKeys.KONAN_DATA_DIR, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.konanManifestNativeTargets: List<KonanTarget>
    get() = getList(NativeConfigurationKeys.KONAN_MANIFEST_NATIVE_TARGETS)
    set(value) { put(NativeConfigurationKeys.KONAN_MANIFEST_NATIVE_TARGETS, value) }

var CompilerConfiguration.llvmModulePasses: String?
    get() = get(NativeConfigurationKeys.LLVM_MODULE_PASSES)
    set(value) { put(NativeConfigurationKeys.LLVM_MODULE_PASSES, requireNotNull(value) { "nullable values are not allowed" }) }

var CompilerConfiguration.llvmLtoPasses: String?
    get() = get(NativeConfigurationKeys.LLVM_LTO_PASSES)
    set(value) { put(NativeConfigurationKeys.LLVM_LTO_PASSES, requireNotNull(value) { "nullable values are not allowed" }) }
