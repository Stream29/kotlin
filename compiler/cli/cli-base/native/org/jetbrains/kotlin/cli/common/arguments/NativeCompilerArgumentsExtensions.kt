/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("PackageDirectoryMismatch", "unused")

package org.jetbrains.kotlin.cli.common.arguments

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.config.ExplicitApiMode
import org.jetbrains.kotlin.config.HeaderMode
import org.jetbrains.kotlin.config.KlibIrInlinerMode
import org.jetbrains.kotlin.config.ReturnValueCheckerMode
import org.jetbrains.kotlin.konan.file.File
import org.jetbrains.kotlin.utils.SmartList

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class Argument(
    val value: String,
    val shortName: String = "",
    val deprecatedName: String = "",
    @property:RawDelimiter
    val delimiter: String = Delimiters.default,
    val valueDescription: String = "",
    val description: String,
    val deprecatedVersion: String = "",
    val removedVersion: String = "",
) {
    @RequiresOptIn(
        message = "The raw delimiter value needs to be resolved. See 'resolvedDelimiter'. Using the raw value requires opt-in",
        level = RequiresOptIn.Level.ERROR
    )
    annotation class RawDelimiter

    object Delimiters {
        const val default = ","
        const val none = ""
        const val pathSeparator = "<path_separator>"
        const val space = " "
        const val semicolon = ";"
    }
}

val Argument.isAdvanced: Boolean
    get() = isSpecial(ADVANCED_ARGUMENT_PREFIX)

val Argument.isInternal: Boolean
    get() = isSpecial(INTERNAL_ARGUMENT_PREFIX)

private fun Argument.isSpecial(prefix: String): Boolean =
    value.startsWith(prefix) && value.length > prefix.length

@OptIn(Argument.RawDelimiter::class)
val Argument.resolvedDelimiter: String?
    get() = when (delimiter) {
        Argument.Delimiters.none -> null
        Argument.Delimiters.pathSeparator -> if (Platform.osFamily == OsFamily.WINDOWS) ";" else ":"
        else -> delimiter
    }

val Argument.isAlreadyRemoved: Boolean
    get() = removedVersion.takeIf { it.isNotEmpty() }.let {
        it != null && parseKotlinVersion(removedVersion) <= KotlinVersion.CURRENT
    }

private const val ADVANCED_ARGUMENT_PREFIX = "-X"
internal const val INTERNAL_ARGUMENT_PREFIX = "-XX"

data class ArgumentParseErrors(
    val unknownArgs: MutableList<String> = SmartList(),
    val unknownExtraFlags: MutableList<String> = SmartList(),
    val extraArgumentsPassedInObsoleteForm: MutableList<String> = SmartList(),
    val deprecatedArguments: MutableMap<String, String> = mutableMapOf(),
    var argumentsWithoutValue: MutableList<String> = SmartList(),
    var booleanArgumentsWithIncorrectValue: MutableList<String> = SmartList(),
    var booleanLangFeatureArgumentsWithValue: MutableList<String> = SmartList(),
    val stringLangFeatureArgumentsWithIncorrectValue: MutableList<Pair<String, Set<String>>> = SmartList(),
    val argfileErrors: MutableList<String> = SmartList(),
    val internalArgumentsParsingProblems: MutableList<Pair<CompilerMessageSeverity, String>> = SmartList()
)

fun validateArguments(errors: ArgumentParseErrors?): String? =
    validateArgumentsAllErrors(errors).takeIf { it.isNotEmpty() }?.joinToString("\n")

fun validateArgumentsAllErrors(errors: ArgumentParseErrors?): List<String> {
    if (errors == null) return emptyList()
    return buildList {
        errors.argumentsWithoutValue.forEach {
            add("No value passed for argument $it")
        }
        errors.booleanArgumentsWithIncorrectValue.forEach { arg ->
            add("Incorrect value for boolean argument '${arg.substringBefore('=')}'. Only 'true' and 'false' are allowed.")
        }
        errors.booleanLangFeatureArgumentsWithValue.forEach { arg ->
            add("No value is expected for argument '${arg.substringBefore('=')}'.")
        }
        errors.stringLangFeatureArgumentsWithIncorrectValue.forEach { [arg, allowedValues] ->
            val [argName, argValue] = arg.split('=')
            val allowedValuesString = allowedValues.joinToString(", ") { "'$it'" }
            add("Incorrect value for argument '$argName'. Actual value: '$argValue', but allowed values: $allowedValuesString.")
        }
        errors.unknownArgs.forEach {
            add("Invalid argument: $it")
        }
    }
}

fun K2NativeCompilerArguments.isNativeSecondStage(): Boolean = produce != "library"

internal fun parseKotlinVersion(kotlinReleaseVersion: String): KotlinVersion {
    val components = kotlinReleaseVersion.split('.')
    return KotlinVersion(
        major = components[0].toInt(),
        minor = components[1].toInt(),
        patch = components[2].toInt(),
    )
}

private class ArgumentInstanceKey(val instance: CommonCompilerArguments) {
    override fun equals(other: Any?): Boolean = other is ArgumentInstanceKey && other.instance === instance
    override fun hashCode(): Int = instance.hashCode()
}

private class ExtraCompilerArgumentsState {
    var extraWarnings: Boolean = false
    var extraHelp: Boolean = false
    var help: Boolean = false
    var suppressWarnings: Boolean = false
    var verbose: Boolean = false
    var internalArguments: List<ManualLanguageFeatureSetting> = emptyList()
    var errors: ArgumentParseErrors? = null
    var autoAdvanceLanguageVersion: Boolean = true
    var autoAdvanceApiVersion: Boolean = true
    var manuallyConfiguredFeatures: Array<String> = emptyArray()
    var debugLevelCompilerChecks: Boolean = false
    var dumpArgumentsDir: String? = null
    var explicitReturnTypes: String = "disable"
    var lenientMode: Boolean = false
    var allowAnyScriptsInSourceRoots: Boolean = false
    var allowConditionImpliesReturnsContracts: Boolean = false
    var allowContractsOnMoreFunctions: Boolean = false
    var allowHoldsinContract: Boolean = false
    var allowKotlinPackage: Boolean = false
    var allowReifiedTypeInCatch: Boolean = false
    var allowReturnsResultOf: Boolean = false
    var annotationDefaultTarget: String? = null
    var annotationTargetAll: Boolean = false
    var checkPhaseConditions: Boolean = false
    var collectionLiterals: Boolean = false
    var commonSources: Array<String> = emptyArray()
    var companionBlocksAndExtensions: Boolean = false
    var consistentDataClassCopyVisibility: Boolean = false
    var contextParameters: Boolean = false
    var contextReceivers: Boolean = false
    var contextSensitiveResolution: Boolean = false
    var dataFlowBasedExhaustiveness: Boolean = false
    var detailedPerf: Boolean = false
    var directJavaActualization: Boolean = false
    var disableIrCheckers: Array<String> = emptyArray()
    var disablePhases: Array<String> = emptyArray()
    var dontSortSourceFiles: Boolean = false
    var dontWarnOnErrorSuppression: Boolean = false
    var dumpDirectory: String? = null
    var dumpOnlyFqName: String? = null
    var eagerLambdaAnalysis: Boolean = false
    var enableAdditionalIrCheckers: Array<String> = emptyArray()
    var incrementalCompilation: Boolean? = null
    var escapingFunctions: Array<String> = emptyArray()
    var expectActualClasses: Boolean = false
    var explicitApi: String = "disable"
    var explicitBackingFields: Boolean = false
    var explicitContextArguments: Boolean = false
    var firAggressivePruning: Boolean? = null
    var fragmentDependencies: Array<String> = emptyArray()
    var fragmentFriendDependencies: Array<String> = emptyArray()
    var fragmentIncrementalClasspath: Array<String> = emptyArray()
    var fragmentRefines: Array<String> = emptyArray()
    var fragmentSources: Array<String> = emptyArray()
    var fragments: Array<String> = emptyArray()
    var headerMode: Boolean = false
    var headerModeType: String = "any"
    var ignoreConstOptimizationErrors: Boolean = false
    var inlineClasses: Boolean = false
    var intellijPluginRoot: String? = null
    var intrinsicConstEvaluation: Boolean = false
    var localTypeAliases: Boolean = false
    var metadataKlib: Boolean = false
    var metadataVersion: String? = null
    var multiDollarInterpolation: Boolean = false
    var multiPlatform: Boolean = false
    var nameBasedDestructuring: String? = null
    var nestedTypeAliases: Boolean = false
    var newInference: Boolean = false
    var noCheckActual: Boolean = false
    var noInline: Boolean = false
    var nonLocalBreakContinue: Boolean = false
    var phasesToDump: Array<String> = emptyArray()
    var phasesToDumpAfter: Array<String> = emptyArray()
    var phasesToDumpBefore: Array<String> = emptyArray()
    var phasesToValidate: Array<String> = emptyArray()
    var phasesToValidateAfter: Array<String> = emptyArray()
    var phasesToValidateBefore: Array<String> = emptyArray()
    var printConfiguration: Boolean = false
    var profilePhases: Boolean = false
    var renderInternalDiagnosticNames: Boolean = false
    var repl: Boolean = false
    var reportOutputFiles: Boolean = false
    var returnValueChecker: String = "disable"
    var separateKmpCompilationScheme: Boolean = false
    var skipMetadataVersionCheck: Boolean = false
    var skipPrereleaseCheck: Boolean = false
    var stdlibCompilation: Boolean = false
    var suppressApiVersionGreaterThanLanguageVersionError: Boolean = false
    var suppressVersionWarnings: Boolean = false
    var suppressedDiagnostics: Array<String> = emptyArray()
    var unrestrictedBuilderInference: Boolean = false
    var useFirExperimentalCheckers: Boolean = false
    var useFirIC: Boolean = false
    var useFirLT: Boolean = true
    var verbosePhases: Array<String> = emptyArray()
    var verifyIr: String? = null
    var warningLevels: Array<String> = emptyArray()
    var whenGuards: Boolean = false
    var apiVersion: String? = null
    var kotlinHome: String? = null
    var languageVersion: String? = null
    var optIn: Array<String> = emptyArray()
    var progressiveMode: Boolean = false
    var script: Boolean = false
    var customKlibAbiVersion: String? = null
    var duplicatedUniqueNameStrategy: String? = null
    var enableSignatureClashChecks: Boolean = true
    var irInlinerBeforeKlibSerialization: String = "default"
    var normalizeAbsolutePath: Boolean = false
    var relativePathBases: Array<String> = emptyArray()
    var klibZipFileAccessorCacheLimit: String = "64"
    var skipLibrarySpecialCompatibilityChecks: Boolean = false
    var lightDebugString: String? = null
    var allocator: String? = null
    var autoCacheDir: String? = null
    var autoCacheableFrom: Array<String> = emptyArray()
    var backendThreads: String = "1"
    var binaryOptions: Array<String> = emptyArray()
    var bundleId: String? = null
    var cacheDirectories: Array<String> = emptyArray()
    var cachedLibraries: Array<String> = emptyArray()
    var checkExternalCalls: Boolean = false
    var debugInfoFormatVersion: String = "1"
    var debugPrefixMap: Array<String> = emptyArray()
    var destroyRuntimeMode: String? = null
    var dumpBuiltCachesTo: String? = null
    var testDumpOutputPath: String? = null
    var emitLazyObjCHeader: String? = null
    var exportKDoc: Boolean = false
    var externalDependencies: String? = null
    var fakeOverrideValidator: Boolean = false
    var filesToCache: Array<String> = emptyArray()
    var frameworkImportHeaders: Array<String> = emptyArray()
    var generateDebugTrampolineString: String? = null
    var gc: String? = null
    var headerKlibPath: String? = null
    var incrementalCacheDir: String? = null
    var propertyLazyInitialization: String? = null
    var konanDataDir: String? = null
    var llvmLTOPasses: String? = null
    var llvmModulePasses: String? = null
    var llvmVariant: String? = null
    var makePerFileCache: Boolean = false
    var manifestNativeTargets: Array<String> = emptyArray()
    var noObjcGenerics: Boolean = false
    var omitFrameworkBinary: Boolean = false
    var clangOptions: Array<String> = emptyArray()
    var overrideKonanProperties: Array<String> = emptyArray()
    var preLinkCaches: String? = null
    var printBitCode: Boolean = false
    var printFiles: Boolean = false
    var printIr: Boolean = false
    var purgeUserLibs: Boolean = false
    var serializedDependencies: String? = null
    var refinesPaths: Array<String> = emptyArray()
    var runtimeFile: String? = null
    var runtimeLogs: String? = null
    var saveLlvmIrAfter: Array<String> = emptyArray()
    var saveLlvmIrDirectory: String? = null
    var shortModuleName: String? = null
    var staticFramework: Boolean = false
    var temporaryFilesDir: String? = null
    var verifyBitCode: Boolean = false
    var verifyCompiler: String? = null
    var workerExceptionHandling: String? = null
    var writeDependenciesOfProducedKlibTo: String? = null
    var saveDependenciesPath: String? = null
    var enableAssertions: Boolean = false
    var mainPackage: String? = null
    var debug: Boolean = false
    var generateNoExitTestRunner: Boolean = false
    var generateTestRunner: Boolean = false
    var generateWorkerTestRunner: Boolean = false
    var includeBinaries: Array<String> = emptyArray()
    var singleLinkerArguments: Array<String> = emptyArray()
    var linkerArguments: Array<String> = emptyArray()
    var manifestFile: String? = null
    var memoryModel: String? = null
    var moduleName: String? = null
    var nativeLibraries: Array<String> = emptyArray()
    var nodefaultlibs: Boolean = false
    var nomain: Boolean = false
    var nopack: Boolean = false
    var nostdlib: Boolean = false
    var optimization: Boolean = false
    var outputName: String? = null
    var target: String? = null
}

private val extraArgsStateMap = HashMap<ArgumentInstanceKey, ExtraCompilerArgumentsState>()
private fun CommonCompilerArguments.extraState(): ExtraCompilerArgumentsState =
    extraArgsStateMap.getOrPut(ArgumentInstanceKey(this)) { ExtraCompilerArgumentsState() }

val CommonCompilerArguments.configurator: CommonCompilerArgumentsConfigurator
    get() = when (this) {
        is K2NativeCompilerArguments -> K2NativeCompilerArgumentsConfigurator()
        else -> CommonCompilerArgumentsConfigurator()
    }

var CommonCompilerArguments.extraWarnings: Boolean
    get() = extraState().extraWarnings
    set(value) { extraState().extraWarnings = value }

var CommonCompilerArguments.extraHelp: Boolean
    get() = extraState().extraHelp
    set(value) { extraState().extraHelp = value }

var CommonCompilerArguments.help: Boolean
    get() = extraState().help
    set(value) { extraState().help = value }

var CommonCompilerArguments.suppressWarnings: Boolean
    get() = extraState().suppressWarnings
    set(value) { extraState().suppressWarnings = value }

var CommonCompilerArguments.verbose: Boolean
    get() = extraState().verbose
    set(value) { extraState().verbose = value }

var CommonCompilerArguments.internalArguments: List<ManualLanguageFeatureSetting>
    get() = extraState().internalArguments
    set(value) { extraState().internalArguments = value }

var CommonCompilerArguments.errors: ArgumentParseErrors?
    get() = extraState().errors
    set(value) { extraState().errors = value }

var CommonCompilerArguments.autoAdvanceLanguageVersion: Boolean
    get() = extraState().autoAdvanceLanguageVersion
    set(value) { extraState().autoAdvanceLanguageVersion = value }

var CommonCompilerArguments.autoAdvanceApiVersion: Boolean
    get() = extraState().autoAdvanceApiVersion
    set(value) { extraState().autoAdvanceApiVersion = value }

var CommonCompilerArguments.manuallyConfiguredFeatures: Array<String>
    get() = extraState().manuallyConfiguredFeatures
    set(value) { extraState().manuallyConfiguredFeatures = value }

var CommonCompilerArguments.debugLevelCompilerChecks: Boolean
    get() = extraState().debugLevelCompilerChecks
    set(value) { extraState().debugLevelCompilerChecks = value }

var CommonCompilerArguments.dumpArgumentsDir: String?
    get() = extraState().dumpArgumentsDir
    set(value) { extraState().dumpArgumentsDir = if (value.isNullOrEmpty()) null else value }

var CommonCompilerArguments.explicitReturnTypes: String
    get() = extraState().explicitReturnTypes
    set(value) { extraState().explicitReturnTypes = value }

var CommonCompilerArguments.lenientMode: Boolean
    get() = extraState().lenientMode
    set(value) { extraState().lenientMode = value }

var CommonCompilerArguments.allowAnyScriptsInSourceRoots: Boolean
    get() = extraState().allowAnyScriptsInSourceRoots
    set(value) { extraState().allowAnyScriptsInSourceRoots = value }

var CommonCompilerArguments.allowConditionImpliesReturnsContracts: Boolean
    get() = extraState().allowConditionImpliesReturnsContracts
    set(value) { extraState().allowConditionImpliesReturnsContracts = value }

var CommonCompilerArguments.allowContractsOnMoreFunctions: Boolean
    get() = extraState().allowContractsOnMoreFunctions
    set(value) { extraState().allowContractsOnMoreFunctions = value }

var CommonCompilerArguments.allowHoldsinContract: Boolean
    get() = extraState().allowHoldsinContract
    set(value) { extraState().allowHoldsinContract = value }

var CommonCompilerArguments.allowKotlinPackage: Boolean
    get() = extraState().allowKotlinPackage
    set(value) { extraState().allowKotlinPackage = value }

var CommonCompilerArguments.allowReifiedTypeInCatch: Boolean
    get() = extraState().allowReifiedTypeInCatch
    set(value) { extraState().allowReifiedTypeInCatch = value }

var CommonCompilerArguments.allowReturnsResultOf: Boolean
    get() = extraState().allowReturnsResultOf
    set(value) { extraState().allowReturnsResultOf = value }

var CommonCompilerArguments.annotationDefaultTarget: String?
    get() = extraState().annotationDefaultTarget
    set(value) { extraState().annotationDefaultTarget = if (value.isNullOrEmpty()) null else value }

var CommonCompilerArguments.annotationTargetAll: Boolean
    get() = extraState().annotationTargetAll
    set(value) { extraState().annotationTargetAll = value }

var CommonCompilerArguments.checkPhaseConditions: Boolean
    get() = extraState().checkPhaseConditions
    set(value) { extraState().checkPhaseConditions = value }

var CommonCompilerArguments.collectionLiterals: Boolean
    get() = extraState().collectionLiterals
    set(value) { extraState().collectionLiterals = value }

var CommonCompilerArguments.commonSources: Array<String>
    get() = extraState().commonSources
    set(value) { extraState().commonSources = value }

var CommonCompilerArguments.companionBlocksAndExtensions: Boolean
    get() = extraState().companionBlocksAndExtensions
    set(value) { extraState().companionBlocksAndExtensions = value }

var CommonCompilerArguments.consistentDataClassCopyVisibility: Boolean
    get() = extraState().consistentDataClassCopyVisibility
    set(value) { extraState().consistentDataClassCopyVisibility = value }

var CommonCompilerArguments.contextParameters: Boolean
    get() = extraState().contextParameters
    set(value) { extraState().contextParameters = value }

var CommonCompilerArguments.contextReceivers: Boolean
    get() = extraState().contextReceivers
    set(value) { extraState().contextReceivers = value }

var CommonCompilerArguments.contextSensitiveResolution: Boolean
    get() = extraState().contextSensitiveResolution
    set(value) { extraState().contextSensitiveResolution = value }

var CommonCompilerArguments.dataFlowBasedExhaustiveness: Boolean
    get() = extraState().dataFlowBasedExhaustiveness
    set(value) { extraState().dataFlowBasedExhaustiveness = value }

var CommonCompilerArguments.detailedPerf: Boolean
    get() = extraState().detailedPerf
    set(value) { extraState().detailedPerf = value }

var CommonCompilerArguments.directJavaActualization: Boolean
    get() = extraState().directJavaActualization
    set(value) { extraState().directJavaActualization = value }

var CommonCompilerArguments.disableIrCheckers: Array<String>
    get() = extraState().disableIrCheckers
    set(value) { extraState().disableIrCheckers = value }

var CommonCompilerArguments.disablePhases: Array<String>
    get() = extraState().disablePhases
    set(value) { extraState().disablePhases = value }

var CommonCompilerArguments.dontSortSourceFiles: Boolean
    get() = extraState().dontSortSourceFiles
    set(value) { extraState().dontSortSourceFiles = value }

var CommonCompilerArguments.dontWarnOnErrorSuppression: Boolean
    get() = extraState().dontWarnOnErrorSuppression
    set(value) { extraState().dontWarnOnErrorSuppression = value }

var CommonCompilerArguments.dumpDirectory: String?
    get() = extraState().dumpDirectory
    set(value) { extraState().dumpDirectory = if (value.isNullOrEmpty()) null else value }

var CommonCompilerArguments.dumpOnlyFqName: String?
    get() = extraState().dumpOnlyFqName
    set(value) { extraState().dumpOnlyFqName = if (value.isNullOrEmpty()) null else value }

var CommonCompilerArguments.eagerLambdaAnalysis: Boolean
    get() = extraState().eagerLambdaAnalysis
    set(value) { extraState().eagerLambdaAnalysis = value }

var CommonCompilerArguments.enableAdditionalIrCheckers: Array<String>
    get() = extraState().enableAdditionalIrCheckers
    set(value) { extraState().enableAdditionalIrCheckers = value }

var CommonCompilerArguments.incrementalCompilation: Boolean?
    get() = extraState().incrementalCompilation
    set(value) { extraState().incrementalCompilation = value }

var CommonCompilerArguments.escapingFunctions: Array<String>
    get() = extraState().escapingFunctions
    set(value) { extraState().escapingFunctions = value }

var CommonCompilerArguments.expectActualClasses: Boolean
    get() = extraState().expectActualClasses
    set(value) { extraState().expectActualClasses = value }

var CommonCompilerArguments.explicitApi: String
    get() = extraState().explicitApi
    set(value) { extraState().explicitApi = value }

var CommonCompilerArguments.explicitBackingFields: Boolean
    get() = extraState().explicitBackingFields
    set(value) { extraState().explicitBackingFields = value }

var CommonCompilerArguments.explicitContextArguments: Boolean
    get() = extraState().explicitContextArguments
    set(value) { extraState().explicitContextArguments = value }

var CommonCompilerArguments.firAggressivePruning: Boolean?
    get() = extraState().firAggressivePruning
    set(value) { extraState().firAggressivePruning = value }

var CommonCompilerArguments.fragmentDependencies: Array<String>
    get() = extraState().fragmentDependencies
    set(value) { extraState().fragmentDependencies = value }

var CommonCompilerArguments.fragmentFriendDependencies: Array<String>
    get() = extraState().fragmentFriendDependencies
    set(value) { extraState().fragmentFriendDependencies = value }

var CommonCompilerArguments.fragmentIncrementalClasspath: Array<String>
    get() = extraState().fragmentIncrementalClasspath
    set(value) { extraState().fragmentIncrementalClasspath = value }

var CommonCompilerArguments.fragmentRefines: Array<String>
    get() = extraState().fragmentRefines
    set(value) { extraState().fragmentRefines = value }

var CommonCompilerArguments.fragmentSources: Array<String>
    get() = extraState().fragmentSources
    set(value) { extraState().fragmentSources = value }

var CommonCompilerArguments.fragments: Array<String>
    get() = extraState().fragments
    set(value) { extraState().fragments = value }

var CommonCompilerArguments.headerMode: Boolean
    get() = extraState().headerMode
    set(value) { extraState().headerMode = value }

var CommonCompilerArguments.headerModeType: String
    get() = extraState().headerModeType
    set(value) { extraState().headerModeType = value }

var CommonCompilerArguments.ignoreConstOptimizationErrors: Boolean
    get() = extraState().ignoreConstOptimizationErrors
    set(value) { extraState().ignoreConstOptimizationErrors = value }

var CommonCompilerArguments.inlineClasses: Boolean
    get() = extraState().inlineClasses
    set(value) { extraState().inlineClasses = value }

var CommonCompilerArguments.intellijPluginRoot: String?
    get() = extraState().intellijPluginRoot
    set(value) { extraState().intellijPluginRoot = if (value.isNullOrEmpty()) null else value }

var CommonCompilerArguments.intrinsicConstEvaluation: Boolean
    get() = extraState().intrinsicConstEvaluation
    set(value) { extraState().intrinsicConstEvaluation = value }

var CommonCompilerArguments.localTypeAliases: Boolean
    get() = extraState().localTypeAliases
    set(value) { extraState().localTypeAliases = value }

var CommonCompilerArguments.metadataKlib: Boolean
    get() = extraState().metadataKlib
    set(value) { extraState().metadataKlib = value }

var CommonCompilerArguments.metadataVersion: String?
    get() = extraState().metadataVersion
    set(value) { extraState().metadataVersion = if (value.isNullOrEmpty()) null else value }

var CommonCompilerArguments.multiDollarInterpolation: Boolean
    get() = extraState().multiDollarInterpolation
    set(value) { extraState().multiDollarInterpolation = value }

var CommonCompilerArguments.multiPlatform: Boolean
    get() = extraState().multiPlatform
    set(value) { extraState().multiPlatform = value }

var CommonCompilerArguments.nameBasedDestructuring: String?
    get() = extraState().nameBasedDestructuring
    set(value) { extraState().nameBasedDestructuring = if (value.isNullOrEmpty()) null else value }

var CommonCompilerArguments.nestedTypeAliases: Boolean
    get() = extraState().nestedTypeAliases
    set(value) { extraState().nestedTypeAliases = value }

var CommonCompilerArguments.newInference: Boolean
    get() = extraState().newInference
    set(value) { extraState().newInference = value }

var CommonCompilerArguments.noCheckActual: Boolean
    get() = extraState().noCheckActual
    set(value) { extraState().noCheckActual = value }

var CommonCompilerArguments.noInline: Boolean
    get() = extraState().noInline
    set(value) { extraState().noInline = value }

var CommonCompilerArguments.nonLocalBreakContinue: Boolean
    get() = extraState().nonLocalBreakContinue
    set(value) { extraState().nonLocalBreakContinue = value }

var CommonCompilerArguments.phasesToDump: Array<String>
    get() = extraState().phasesToDump
    set(value) { extraState().phasesToDump = value }

var CommonCompilerArguments.phasesToDumpAfter: Array<String>
    get() = extraState().phasesToDumpAfter
    set(value) { extraState().phasesToDumpAfter = value }

var CommonCompilerArguments.phasesToDumpBefore: Array<String>
    get() = extraState().phasesToDumpBefore
    set(value) { extraState().phasesToDumpBefore = value }

var CommonCompilerArguments.phasesToValidate: Array<String>
    get() = extraState().phasesToValidate
    set(value) { extraState().phasesToValidate = value }

var CommonCompilerArguments.phasesToValidateAfter: Array<String>
    get() = extraState().phasesToValidateAfter
    set(value) { extraState().phasesToValidateAfter = value }

var CommonCompilerArguments.phasesToValidateBefore: Array<String>
    get() = extraState().phasesToValidateBefore
    set(value) { extraState().phasesToValidateBefore = value }

var CommonCompilerArguments.printConfiguration: Boolean
    get() = extraState().printConfiguration
    set(value) { extraState().printConfiguration = value }

var CommonCompilerArguments.profilePhases: Boolean
    get() = extraState().profilePhases
    set(value) { extraState().profilePhases = value }

var CommonCompilerArguments.renderInternalDiagnosticNames: Boolean
    get() = extraState().renderInternalDiagnosticNames
    set(value) { extraState().renderInternalDiagnosticNames = value }

var CommonCompilerArguments.repl: Boolean
    get() = extraState().repl
    set(value) { extraState().repl = value }

var CommonCompilerArguments.reportOutputFiles: Boolean
    get() = extraState().reportOutputFiles
    set(value) { extraState().reportOutputFiles = value }

var CommonCompilerArguments.returnValueChecker: String
    get() = extraState().returnValueChecker
    set(value) { extraState().returnValueChecker = value }

var CommonCompilerArguments.separateKmpCompilationScheme: Boolean
    get() = extraState().separateKmpCompilationScheme
    set(value) { extraState().separateKmpCompilationScheme = value }

var CommonCompilerArguments.skipMetadataVersionCheck: Boolean
    get() = extraState().skipMetadataVersionCheck
    set(value) { extraState().skipMetadataVersionCheck = value }

var CommonCompilerArguments.skipPrereleaseCheck: Boolean
    get() = extraState().skipPrereleaseCheck
    set(value) { extraState().skipPrereleaseCheck = value }

var CommonCompilerArguments.stdlibCompilation: Boolean
    get() = extraState().stdlibCompilation
    set(value) { extraState().stdlibCompilation = value }

var CommonCompilerArguments.suppressApiVersionGreaterThanLanguageVersionError: Boolean
    get() = extraState().suppressApiVersionGreaterThanLanguageVersionError
    set(value) { extraState().suppressApiVersionGreaterThanLanguageVersionError = value }

var CommonCompilerArguments.suppressVersionWarnings: Boolean
    get() = extraState().suppressVersionWarnings
    set(value) { extraState().suppressVersionWarnings = value }

var CommonCompilerArguments.suppressedDiagnostics: Array<String>
    get() = extraState().suppressedDiagnostics
    set(value) { extraState().suppressedDiagnostics = value }

var CommonCompilerArguments.unrestrictedBuilderInference: Boolean
    get() = extraState().unrestrictedBuilderInference
    set(value) { extraState().unrestrictedBuilderInference = value }

var CommonCompilerArguments.useFirExperimentalCheckers: Boolean
    get() = extraState().useFirExperimentalCheckers
    set(value) { extraState().useFirExperimentalCheckers = value }

var CommonCompilerArguments.useFirIC: Boolean
    get() = extraState().useFirIC
    set(value) { extraState().useFirIC = value }

var CommonCompilerArguments.useFirLT: Boolean
    get() = extraState().useFirLT
    set(value) { extraState().useFirLT = value }

var CommonCompilerArguments.verbosePhases: Array<String>
    get() = extraState().verbosePhases
    set(value) { extraState().verbosePhases = value }

var CommonCompilerArguments.verifyIr: String?
    get() = extraState().verifyIr
    set(value) { extraState().verifyIr = if (value.isNullOrEmpty()) null else value }

var CommonCompilerArguments.warningLevels: Array<String>
    get() = extraState().warningLevels
    set(value) { extraState().warningLevels = value }

var CommonCompilerArguments.whenGuards: Boolean
    get() = extraState().whenGuards
    set(value) { extraState().whenGuards = value }

var CommonCompilerArguments.apiVersion: String?
    get() = extraState().apiVersion
    set(value) { extraState().apiVersion = if (value.isNullOrEmpty()) null else value }

var CommonCompilerArguments.kotlinHome: String?
    get() = extraState().kotlinHome
    set(value) { extraState().kotlinHome = if (value.isNullOrEmpty()) null else value }

var CommonCompilerArguments.languageVersion: String?
    get() = extraState().languageVersion
    set(value) { extraState().languageVersion = if (value.isNullOrEmpty()) null else value }

var CommonCompilerArguments.optIn: Array<String>
    get() = extraState().optIn
    set(value) { extraState().optIn = value }

var CommonCompilerArguments.progressiveMode: Boolean
    get() = extraState().progressiveMode
    set(value) { extraState().progressiveMode = value }

var CommonCompilerArguments.script: Boolean
    get() = extraState().script
    set(value) { extraState().script = value }

var CommonKlibBasedCompilerArguments.customKlibAbiVersion: String?
    get() = extraState().customKlibAbiVersion
    set(value) { extraState().customKlibAbiVersion = if (value.isNullOrEmpty()) null else value }

var CommonKlibBasedCompilerArguments.duplicatedUniqueNameStrategy: String?
    get() = extraState().duplicatedUniqueNameStrategy
    set(value) { extraState().duplicatedUniqueNameStrategy = if (value.isNullOrEmpty()) null else value }

var CommonKlibBasedCompilerArguments.enableSignatureClashChecks: Boolean
    get() = extraState().enableSignatureClashChecks
    set(value) { extraState().enableSignatureClashChecks = value }

var CommonKlibBasedCompilerArguments.irInlinerBeforeKlibSerialization: String
    get() = extraState().irInlinerBeforeKlibSerialization
    set(value) { extraState().irInlinerBeforeKlibSerialization = value }

var CommonKlibBasedCompilerArguments.normalizeAbsolutePath: Boolean
    get() = extraState().normalizeAbsolutePath
    set(value) { extraState().normalizeAbsolutePath = value }

var CommonKlibBasedCompilerArguments.relativePathBases: Array<String>
    get() = extraState().relativePathBases
    set(value) { extraState().relativePathBases = value }

var CommonKlibBasedCompilerArguments.klibZipFileAccessorCacheLimit: String
    get() = extraState().klibZipFileAccessorCacheLimit
    set(value) { extraState().klibZipFileAccessorCacheLimit = value }

var CommonKlibBasedCompilerArguments.skipLibrarySpecialCompatibilityChecks: Boolean
    get() = extraState().skipLibrarySpecialCompatibilityChecks
    set(value) { extraState().skipLibrarySpecialCompatibilityChecks = value }

var K2NativeCompilerArguments.lightDebugString: String?
    get() = extraState().lightDebugString
    set(value) { extraState().lightDebugString = if (value.isNullOrEmpty()) null else value }

var K2NativeCompilerArguments.allocator: String?
    get() = extraState().allocator
    set(value) { extraState().allocator = if (value.isNullOrEmpty()) null else value }

var K2NativeCompilerArguments.autoCacheDir: String?
    get() = extraState().autoCacheDir
    set(value) { extraState().autoCacheDir = if (value.isNullOrEmpty()) null else value }

var K2NativeCompilerArguments.autoCacheableFrom: Array<String>
    get() = extraState().autoCacheableFrom
    set(value) { extraState().autoCacheableFrom = value }

var K2NativeCompilerArguments.backendThreads: String
    get() = extraState().backendThreads
    set(value) { extraState().backendThreads = value }

var K2NativeCompilerArguments.binaryOptions: Array<String>
    get() = extraState().binaryOptions
    set(value) { extraState().binaryOptions = value }

var K2NativeCompilerArguments.bundleId: String?
    get() = extraState().bundleId
    set(value) { extraState().bundleId = if (value.isNullOrEmpty()) null else value }

var K2NativeCompilerArguments.cacheDirectories: Array<String>
    get() = extraState().cacheDirectories
    set(value) { extraState().cacheDirectories = value }

var K2NativeCompilerArguments.cachedLibraries: Array<String>
    get() = extraState().cachedLibraries
    set(value) { extraState().cachedLibraries = value }

var K2NativeCompilerArguments.checkExternalCalls: Boolean
    get() = extraState().checkExternalCalls
    set(value) { extraState().checkExternalCalls = value }

var K2NativeCompilerArguments.debugInfoFormatVersion: String
    get() = extraState().debugInfoFormatVersion
    set(value) { extraState().debugInfoFormatVersion = value }

var K2NativeCompilerArguments.debugPrefixMap: Array<String>
    get() = extraState().debugPrefixMap
    set(value) { extraState().debugPrefixMap = value }

var K2NativeCompilerArguments.destroyRuntimeMode: String?
    get() = extraState().destroyRuntimeMode
    set(value) { extraState().destroyRuntimeMode = if (value.isNullOrEmpty()) null else value }

var K2NativeCompilerArguments.dumpBuiltCachesTo: String?
    get() = extraState().dumpBuiltCachesTo
    set(value) { extraState().dumpBuiltCachesTo = if (value.isNullOrEmpty()) null else value }

var K2NativeCompilerArguments.testDumpOutputPath: String?
    get() = extraState().testDumpOutputPath
    set(value) { extraState().testDumpOutputPath = if (value.isNullOrEmpty()) null else value }

var K2NativeCompilerArguments.emitLazyObjCHeader: String?
    get() = extraState().emitLazyObjCHeader
    set(value) { extraState().emitLazyObjCHeader = if (value.isNullOrEmpty()) null else value }

var K2NativeCompilerArguments.exportKDoc: Boolean
    get() = extraState().exportKDoc
    set(value) { extraState().exportKDoc = value }

var K2NativeCompilerArguments.externalDependencies: String?
    get() = extraState().externalDependencies
    set(value) { extraState().externalDependencies = if (value.isNullOrEmpty()) null else value }

var K2NativeCompilerArguments.fakeOverrideValidator: Boolean
    get() = extraState().fakeOverrideValidator
    set(value) { extraState().fakeOverrideValidator = value }

var K2NativeCompilerArguments.filesToCache: Array<String>
    get() = extraState().filesToCache
    set(value) { extraState().filesToCache = value }

var K2NativeCompilerArguments.frameworkImportHeaders: Array<String>
    get() = extraState().frameworkImportHeaders
    set(value) { extraState().frameworkImportHeaders = value }

var K2NativeCompilerArguments.generateDebugTrampolineString: String?
    get() = extraState().generateDebugTrampolineString
    set(value) { extraState().generateDebugTrampolineString = if (value.isNullOrEmpty()) null else value }

var K2NativeCompilerArguments.gc: String?
    get() = extraState().gc
    set(value) { extraState().gc = if (value.isNullOrEmpty()) null else value }

var K2NativeCompilerArguments.headerKlibPath: String?
    get() = extraState().headerKlibPath
    set(value) { extraState().headerKlibPath = if (value.isNullOrEmpty()) null else value }

var K2NativeCompilerArguments.incrementalCacheDir: String?
    get() = extraState().incrementalCacheDir
    set(value) { extraState().incrementalCacheDir = if (value.isNullOrEmpty()) null else value }

var K2NativeCompilerArguments.propertyLazyInitialization: String?
    get() = extraState().propertyLazyInitialization
    set(value) { extraState().propertyLazyInitialization = if (value.isNullOrEmpty()) null else value }

var K2NativeCompilerArguments.konanDataDir: String?
    get() = extraState().konanDataDir
    set(value) { extraState().konanDataDir = if (value.isNullOrEmpty()) null else value }

var K2NativeCompilerArguments.llvmLTOPasses: String?
    get() = extraState().llvmLTOPasses
    set(value) { extraState().llvmLTOPasses = if (value.isNullOrEmpty()) null else value }

var K2NativeCompilerArguments.llvmModulePasses: String?
    get() = extraState().llvmModulePasses
    set(value) { extraState().llvmModulePasses = if (value.isNullOrEmpty()) null else value }

var K2NativeCompilerArguments.llvmVariant: String?
    get() = extraState().llvmVariant
    set(value) { extraState().llvmVariant = if (value.isNullOrEmpty()) null else value }

var K2NativeCompilerArguments.makePerFileCache: Boolean
    get() = extraState().makePerFileCache
    set(value) { extraState().makePerFileCache = value }

var K2NativeCompilerArguments.manifestNativeTargets: Array<String>
    get() = extraState().manifestNativeTargets
    set(value) { extraState().manifestNativeTargets = value }

var K2NativeCompilerArguments.noObjcGenerics: Boolean
    get() = extraState().noObjcGenerics
    set(value) { extraState().noObjcGenerics = value }

var K2NativeCompilerArguments.omitFrameworkBinary: Boolean
    get() = extraState().omitFrameworkBinary
    set(value) { extraState().omitFrameworkBinary = value }

var K2NativeCompilerArguments.clangOptions: Array<String>
    get() = extraState().clangOptions
    set(value) { extraState().clangOptions = value }

var K2NativeCompilerArguments.overrideKonanProperties: Array<String>
    get() = extraState().overrideKonanProperties
    set(value) { extraState().overrideKonanProperties = value }

var K2NativeCompilerArguments.preLinkCaches: String?
    get() = extraState().preLinkCaches
    set(value) { extraState().preLinkCaches = if (value.isNullOrEmpty()) null else value }

var K2NativeCompilerArguments.printBitCode: Boolean
    get() = extraState().printBitCode
    set(value) { extraState().printBitCode = value }

var K2NativeCompilerArguments.printFiles: Boolean
    get() = extraState().printFiles
    set(value) { extraState().printFiles = value }

var K2NativeCompilerArguments.printIr: Boolean
    get() = extraState().printIr
    set(value) { extraState().printIr = value }

var K2NativeCompilerArguments.purgeUserLibs: Boolean
    get() = extraState().purgeUserLibs
    set(value) { extraState().purgeUserLibs = value }

var K2NativeCompilerArguments.serializedDependencies: String?
    get() = extraState().serializedDependencies
    set(value) { extraState().serializedDependencies = if (value.isNullOrEmpty()) null else value }

var K2NativeCompilerArguments.refinesPaths: Array<String>
    get() = extraState().refinesPaths
    set(value) { extraState().refinesPaths = value }

var K2NativeCompilerArguments.runtimeFile: String?
    get() = extraState().runtimeFile
    set(value) { extraState().runtimeFile = if (value.isNullOrEmpty()) null else value }

var K2NativeCompilerArguments.runtimeLogs: String?
    get() = extraState().runtimeLogs
    set(value) { extraState().runtimeLogs = if (value.isNullOrEmpty()) null else value }

var K2NativeCompilerArguments.saveLlvmIrAfter: Array<String>
    get() = extraState().saveLlvmIrAfter
    set(value) { extraState().saveLlvmIrAfter = value }

var K2NativeCompilerArguments.saveLlvmIrDirectory: String?
    get() = extraState().saveLlvmIrDirectory
    set(value) { extraState().saveLlvmIrDirectory = if (value.isNullOrEmpty()) null else value }

var K2NativeCompilerArguments.shortModuleName: String?
    get() = extraState().shortModuleName
    set(value) { extraState().shortModuleName = if (value.isNullOrEmpty()) null else value }

var K2NativeCompilerArguments.staticFramework: Boolean
    get() = extraState().staticFramework
    set(value) { extraState().staticFramework = value }

var K2NativeCompilerArguments.temporaryFilesDir: String?
    get() = extraState().temporaryFilesDir
    set(value) { extraState().temporaryFilesDir = if (value.isNullOrEmpty()) null else value }

var K2NativeCompilerArguments.verifyBitCode: Boolean
    get() = extraState().verifyBitCode
    set(value) { extraState().verifyBitCode = value }

var K2NativeCompilerArguments.verifyCompiler: String?
    get() = extraState().verifyCompiler
    set(value) { extraState().verifyCompiler = if (value.isNullOrEmpty()) null else value }

var K2NativeCompilerArguments.workerExceptionHandling: String?
    get() = extraState().workerExceptionHandling
    set(value) { extraState().workerExceptionHandling = if (value.isNullOrEmpty()) null else value }

var K2NativeCompilerArguments.writeDependenciesOfProducedKlibTo: String?
    get() = extraState().writeDependenciesOfProducedKlibTo
    set(value) { extraState().writeDependenciesOfProducedKlibTo = if (value.isNullOrEmpty()) null else value }

var K2NativeCompilerArguments.saveDependenciesPath: String?
    get() = extraState().saveDependenciesPath
    set(value) { extraState().saveDependenciesPath = if (value.isNullOrEmpty()) null else value }

var K2NativeCompilerArguments.enableAssertions: Boolean
    get() = extraState().enableAssertions
    set(value) { extraState().enableAssertions = value }

var K2NativeCompilerArguments.mainPackage: String?
    get() = extraState().mainPackage
    set(value) { extraState().mainPackage = if (value.isNullOrEmpty()) null else value }

var K2NativeCompilerArguments.debug: Boolean
    get() = extraState().debug
    set(value) { extraState().debug = value }

var K2NativeCompilerArguments.generateNoExitTestRunner: Boolean
    get() = extraState().generateNoExitTestRunner
    set(value) { extraState().generateNoExitTestRunner = value }

var K2NativeCompilerArguments.generateTestRunner: Boolean
    get() = extraState().generateTestRunner
    set(value) { extraState().generateTestRunner = value }

var K2NativeCompilerArguments.generateWorkerTestRunner: Boolean
    get() = extraState().generateWorkerTestRunner
    set(value) { extraState().generateWorkerTestRunner = value }

var K2NativeCompilerArguments.includeBinaries: Array<String>
    get() = extraState().includeBinaries
    set(value) { extraState().includeBinaries = value }

var K2NativeCompilerArguments.singleLinkerArguments: Array<String>
    get() = extraState().singleLinkerArguments
    set(value) { extraState().singleLinkerArguments = value }

var K2NativeCompilerArguments.linkerArguments: Array<String>
    get() = extraState().linkerArguments
    set(value) { extraState().linkerArguments = value }

var K2NativeCompilerArguments.manifestFile: String?
    get() = extraState().manifestFile
    set(value) { extraState().manifestFile = if (value.isNullOrEmpty()) null else value }

var K2NativeCompilerArguments.memoryModel: String?
    get() = extraState().memoryModel
    set(value) { extraState().memoryModel = if (value.isNullOrEmpty()) null else value }

var K2NativeCompilerArguments.moduleName: String?
    get() = extraState().moduleName
    set(value) { extraState().moduleName = if (value.isNullOrEmpty()) null else value }

var K2NativeCompilerArguments.nativeLibraries: Array<String>
    get() = extraState().nativeLibraries
    set(value) { extraState().nativeLibraries = value }

var K2NativeCompilerArguments.nodefaultlibs: Boolean
    get() = extraState().nodefaultlibs
    set(value) { extraState().nodefaultlibs = value }

var K2NativeCompilerArguments.nomain: Boolean
    get() = extraState().nomain
    set(value) { extraState().nomain = value }

var K2NativeCompilerArguments.nopack: Boolean
    get() = extraState().nopack
    set(value) { extraState().nopack = value }

var K2NativeCompilerArguments.nostdlib: Boolean
    get() = extraState().nostdlib
    set(value) { extraState().nostdlib = value }

var K2NativeCompilerArguments.optimization: Boolean
    get() = extraState().optimization
    set(value) { extraState().optimization = value }

var K2NativeCompilerArguments.outputName: String?
    get() = extraState().outputName
    set(value) { extraState().outputName = if (value.isNullOrEmpty()) null else value }

var K2NativeCompilerArguments.target: String?
    get() = extraState().target
    set(value) { extraState().target = if (value.isNullOrEmpty()) null else value }
