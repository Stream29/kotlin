/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("PackageDirectoryMismatch", "DEPRECATION", "DEPRECATION_ERROR", "unused", "DuplicatedCode")
@file:OptIn(Argument.RawDelimiter::class)

package org.jetbrains.kotlin.cli.common.arguments

import org.jetbrains.kotlin.cli.common.CompilerSystemProperties
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstanceOrNull

enum class ArgKind {
    BOOLEAN,
    STRING,
    STRING_ARRAY,
}

class NativeArgumentDescriptor(
    val argument: Argument,
    val kind: ArgKind,
    val changesLanguageFeatures: Boolean,
    val legalStringValues: Set<String>,
    val removed: Boolean,
    val setter: (K2NativeCompilerArguments, Any) -> Unit,
)

private const val ADVANCED_ARGUMENT_PREFIX = "-X"
private const val FREE_ARGS_DELIMITER = "--"

internal val allNativeArgumentDescriptors: List<NativeArgumentDescriptor> = listOf(
    NativeArgumentDescriptor(
        argument = Argument(value = "-Werror",
        description = "Report an error if there are any warnings.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.allWarningsAsErrors = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Wextra",
        description = "Enable extra checkers for K2.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.extraWarnings = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-X",
        description = "Print a synopsis of advanced options.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.extraHelp = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-help",
        shortName = "-h",
        description = "Print a synopsis of standard options.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.help = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-nowarn",
        description = "Don't generate any warnings.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.suppressWarnings = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-verbose",
        description = "Enable verbose logging output.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.verbose = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-version",
        description = "Display the compiler version.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.version = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-P",
        valueDescription = "plugin:<pluginId>:<optionName>=<value>",
        description = "Pass an option to a plugin.",),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.pluginOptions = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-XXLanguage",
        valueDescription = "[+-]LanguageFeatureName",
        description = """Enables/disables specified language feature.
Warning: this flag is not intended for production use. If you want to configure the language behaviour use the
-language-version or corresponding experimental feature flags.""",
        delimiter = Argument.Delimiters.none,),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.manuallyConfiguredFeatures = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-XXdebug-level-compiler-checks",
        description = "Enable debug level compiler checks. ATTENTION: these checks can slow compiler down or even crash it.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.debugLevelCompilerChecks = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-XXdump-model",
        valueDescription = "<dir>",
        description = "Dump compilation model to specified directory for use in modularized tests.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.dumpArgumentsDir = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-XXexplicit-return-types",
        valueDescription = "{strict|warning|disable}",
        description = """Force the compiler to report errors on all public API declarations without an explicit return type.
Use the 'warning' level to issue warnings instead of errors.
This flag partially enables functionality of `-Xexplicit-api` flag, so please don't use them altogether""",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.explicitReturnTypes = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-XXlenient-mode",
        description = "Lenient compiler mode. When actuals are missing, placeholder declarations are generated.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.lenientMode = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xallow-any-scripts-in-source-roots",
        description = "Allow compiling scripts along with regular Kotlin sources.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.allowAnyScriptsInSourceRoots = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xallow-condition-implies-returns-contracts",
        description = "Allow contracts that specify a limited conditional returns postcondition.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = true,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.allowConditionImpliesReturnsContracts = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xallow-contracts-on-more-functions",
        description = "Allow contracts on some operators and accessors, and allow checks for erased types.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = true,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.allowContractsOnMoreFunctions = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xallow-holdsin-contract",
        description = "Allow contracts that specify a condition that holds true inside a lambda argument.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = true,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.allowHoldsinContract = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xallow-kotlin-package",
        description = "Allow compiling code in the 'kotlin' package, and allow not requiring 'kotlin.stdlib' in 'module-info'.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.allowKotlinPackage = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xallow-reified-type-in-catch",
        description = "Allow 'catch' parameters to have reified types.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = true,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.allowReifiedTypeInCatch = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xallow-returns-result-of",
        description = "Allows to use `returnsResultOf()` in `contract {}` block of function body. This contract provides additional information for return value checker. Enabling this feature will force compiler to produce pre-release binaries, because this functions with this contract cannot be read correctly by Kotlin 2.3 and lower.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = true,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.allowReturnsResultOf = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xannotation-default-target",
        valueDescription = "first-only|first-only-warn|param-property",
        description = """Change the default annotation targets for constructor properties:
-Xannotation-default-target=first-only:      use the first of the following allowed targets: '@param:', '@property:', '@field:';
-Xannotation-default-target=first-only-warn: same as first-only, and raise warnings when both '@param:' and either '@property:' or '@field:' are allowed;
-Xannotation-default-target=param-property:  use '@param:' target if applicable, and also use the first of either '@property:' or '@field:';
default: 'param-property' in language version 2.4+, 'first-only-warn' in language versions 2.2 & 2.3, 'first-only' in version 2.1 and before.""",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = true,
        legalStringValues = setOf("first-only-warn", "param-property", "first-only", "first-only", "first-only-warn"),
        removed = false,
        setter = { args, v -> args.annotationDefaultTarget = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xannotation-target-all",
        description = "Enable experimental language support for @all: annotation use-site target.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = true,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.annotationTargetAll = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xcheck-phase-conditions",
        description = "Check pre- and postconditions of IR lowering phases.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.checkPhaseConditions = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xcollection-literals",
        description = "Enable experimental language support for collection literals.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = true,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.collectionLiterals = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xcommon-sources",
        valueDescription = "<path>",
        description = """Sources of the common module that need to be compiled together with this module in multiplatform mode.
They should be a subset of sources passed as free arguments.""",),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.commonSources = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xcompanion-blocks-and-extensions",
        description = "Enables companion blocks and extensions.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = true,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.companionBlocksAndExtensions = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xcompiler-plugin",
        valueDescription = "<path1>,<path2>[=<optionName>=<value>,<optionName>=<value>]",
        description = "Register a compiler plugin.",
        delimiter = Argument.Delimiters.none,),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.pluginConfigurations = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xcompiler-plugin-order",
        valueDescription = "<pluginId1>><pluginId2>",
        description = """Specify an execution order constraint for compiler plugins.
Order constraint can be specified using the 'pluginId' of compiler plugins.
The first specified plugin will be executed before the second plugin.
Multiple constraints can be specified by repeating this option. Cycles in constraints will cause an error.""",
        delimiter = Argument.Delimiters.none,),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.pluginOrderConstraints = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xconsistent-data-class-copy-visibility",
        description = "The effect of this compiler flag is the same as applying @ConsistentCopyVisibility annotation to all data classes in the module. See https://youtrack.jetbrains.com/issue/KT-11914",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = true,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.consistentDataClassCopyVisibility = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xcontext-parameters",
        description = "Enable experimental context parameters.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = true,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.contextParameters = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xcontext-receivers",
        description = "Enable experimental context receivers.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = true,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.contextReceivers = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xcontext-sensitive-resolution",
        description = "Enable experimental context-sensitive resolution.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = true,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.contextSensitiveResolution = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xdata-flow-based-exhaustiveness",
        description = "Enable `when` exhaustiveness improvements that rely on data-flow analysis.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = true,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.dataFlowBasedExhaustiveness = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xdetailed-perf",
        description = """Enable more detailed performance statistics (Experimental).
For Native, the performance report includes execution time and lines processed per second for every individual lowering.
For WASM and JS, the performance report includes execution time and lines per second for each lowering of the first stage of compilation.
Additionally enables measurements for User and CPU time for all targets. Note that this could cause performance degradation on Linux
  machines, so use this mode with caution.""",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.detailedPerf = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xdirect-java-actualization",
        description = "Enable experimental direct Java actualization support.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = true,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.directJavaActualization = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xdisable-default-scripting-plugin",
        description = "Don't enable the scripting plugin by default.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.disableDefaultScriptingPlugin = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xdisable-ir-checkers",
        valueDescription = "<checker1>,<checker2>",
        description = """A list of IR checkers to disable, specified by a simple name of the checker class. A name of an annotation can also be used to match all tagged checkers.
Only has effect if '-Xverify-ir' is not 'none'.""",),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.disableIrCheckers = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xdisable-phases",
        description = "Disable backend phases.",),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.disablePhases = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xdont-sort-source-files",
        description = "Disable automatic sorting of source files.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.dontSortSourceFiles = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xdont-warn-on-error-suppression",
        description = "Don't report warnings when errors are suppressed. This only affects K2.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.dontWarnOnErrorSuppression = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xdump-directory",
        description = "Dump the backend state into this directory.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.dumpDirectory = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xdump-fqname",
        description = "Dump the declaration with the given FqName.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.dumpOnlyFqName = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xdump-perf",
        valueDescription = "<path>",
        description = """Dump detailed performance statistics to the specified file in plain text, JSON or markdown format (it's detected by the file's extension).
Also, it supports the placeholder `*` and directory for generating file names based on the module being compiled and the current time stamp.
Example: `path/to/dir/*.log` creates logs like `path/to/dir/my-module_2025-06-20-12-22-32.log` in plain text format, `path/to/dir/` creates logs like `path/to/dir/my-log_2025-06-20-12-22-32.json`.""",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.dumpPerf = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xeager-lambda-analysis",
        description = "Enable eager analysis of lambda bodies to improve overload resolution by the lambda's return type.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = true,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.eagerLambdaAnalysis = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xenable-additional-ir-checkers",
        valueDescription = "<checker1>,<checker2>",
        description = """A list of IR checkers to enable, specified by a simple name of the checker class.
It may only be used with specific checkers that are not enabled by default, and which are prepared to be enabled this way. Only has effect if '-Xverify-ir' is not 'none'.""",),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.enableAdditionalIrCheckers = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xenable-incremental-compilation",
        description = "Enable incremental compilation.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.incrementalCompilation = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xescaping-functions",
        valueDescription = "<+|-><fq.name>",
        description = "Add (+) or remove (-) a callable whose functional arguments are analyzed for escaping mutable variables. Callables are specified by their fully qualified name.",),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.escapingFunctions = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xexpect-actual-classes",
        description = """'expect'/'actual' classes (including interfaces, objects, annotations, enums, and 'actual' typealiases) are in Beta.
Kotlin reports a warning every time you use one of them. You can use this flag to mute the warning.""",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.expectActualClasses = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xexplicit-api",
        valueDescription = "{strict|warning|disable}",
        description = """Force the compiler to report errors on all public API declarations without an explicit visibility or a return type.
Use the 'warning' level to issue warnings instead of errors.""",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.explicitApi = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xexplicit-backing-fields",
        description = "Enable experimental language support for explicit backing fields.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = true,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.explicitBackingFields = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xexplicit-context-arguments",
        description = "Enable explicit passing of context arguments using named argument syntax.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = true,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.explicitContextArguments = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xfir-aggressive-pruning",
        description = "Enable or disable FirAggressivePruningProcessor, which prunes unreachable private members during body resolve.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.firAggressivePruning = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xfragment-dependency",
        valueDescription = "<fragment name>:<path>",
        description = """Declare common klib dependencies for the specific fragment.
This argument is required for any HMPP module except the platform leaf module: it takes dependencies from -cp/-libraries.
The argument should be used only if the new compilation scheme is enabled with -Xseparate-kmp-compilation""",
        delimiter = Argument.Delimiters.none,),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.fragmentDependencies = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xfragment-friend-dependency",
        valueDescription = "<fragment name>:<path>",
        description = """Declare common klib friend dependencies for the specific fragment.
This argument can be specified for any HMPP module except the platform leaf module: it takes dependencies from the platform specific friend module arguments.
The argument should be used only if the new compilation scheme is enabled with -Xseparate-kmp-compilation""",
        delimiter = Argument.Delimiters.none,),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.fragmentFriendDependencies = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xfragment-incremental-classpath",
        valueDescription = "<fragment name>:<path>",
        description = """Declare common klib incremental dependencies (results from the previous compilation) for the specific fragment.    
This argument can be specified for any HMPP module except the platform leaf module: it takes incremental
  dependencies from the platform specific incremental service.""",
        delimiter = Argument.Delimiters.none,),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.fragmentIncrementalClasspath = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xfragment-refines",
        valueDescription = "<fromModuleName>:<onModuleName>",
        description = "Declare that <fromModuleName> refines <onModuleName> with the dependsOn/refines relation.",),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.fragmentRefines = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xfragment-sources",
        valueDescription = "<fragment name>:<path>",
        description = "Add sources to a specific fragment of a multiplatform compilation.",),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.fragmentSources = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xfragments",
        valueDescription = "<fragment name>",
        description = "Declare all known fragments of a multiplatform compilation.",),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.fragments = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xheader-mode",
        description = """Enable header compilation mode.
In this mode, the compiler produces class files that only contain the 'skeleton' of the classes to be
compiled but the method bodies of all the implementations are empty.  This is used to speed up parallel compilation
build systems where header libraries can be used to replace downstream dependencies for which we only need to
see the type names and method signatures required to compile a given translation unit. Inline functions are still kept
with bodies.""",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.headerMode = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xheader-mode-type",
        valueDescription = "{any|compilation}",
        description = """Generates output based on what it is used for:
-Xheader-mode-type=compilation: Skips the IR generation for modules that don't have inline functions.
-Xheader-mode-type=any: Can be used for any downstream dependency which doesn't require linking.""",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.headerModeType = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xignore-const-optimization-errors",
        description = "Ignore all compilation exceptions while optimizing some constant expressions.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.ignoreConstOptimizationErrors = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xinline-classes",
        description = "Enable experimental inline classes.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = true,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.inlineClasses = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xintellij-plugin-root",
        valueDescription = "<path>",
        description = "Path to 'kotlin-compiler.jar' or the directory where the IntelliJ IDEA configuration files can be found.",
        deprecatedVersion = "2.4.20",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.intellijPluginRoot = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xintrinsic-const-evaluation",
        description = "Enables `IntrinsicConstEvaluation` language feature.`",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = true,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.intrinsicConstEvaluation = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xlist-phases",
        description = "List backend phases.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.listPhases = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xlocal-type-aliases",
        description = "Enable experimental language support for local type aliases.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = true,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.localTypeAliases = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xmetadata-klib",
        deprecatedName = "-Xexpect-actual-linker",
        description = "Produce a klib that only contains the metadata of declarations.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.metadataKlib = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xmetadata-version",
        description = "Change the metadata version of the generated binary files.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.metadataVersion = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xmulti-dollar-interpolation",
        description = "Enable experimental multi-dollar interpolation.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = true,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.multiDollarInterpolation = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xmulti-platform",
        description = "Enable language support for multiplatform projects.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = true,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.multiPlatform = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xname-based-destructuring",
        valueDescription = "only-syntax|name-mismatch|complete",
        description = """Enables the following destructuring features:
-Xname-based-destructuring=only-syntax:   Enables syntax for positional destructuring with square brackets and the full form of name-based destructuring with parentheses;
-Xname-based-destructuring=name-mismatch: Reports warnings when short form positional destructuring of data classes uses names that don't match the property names;
-Xname-based-destructuring=complete:      Enables short-form name-based destructuring with parentheses;""",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = true,
        legalStringValues = setOf("only-syntax", "name-mismatch", "complete", "name-mismatch", "complete", "complete", "only-syntax", "only-syntax", "name-mismatch"),
        removed = false,
        setter = { args, v -> args.nameBasedDestructuring = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xnested-type-aliases",
        description = "Enable experimental language support for nested type aliases.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = true,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.nestedTypeAliases = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xnew-inference",
        description = "Enable the new experimental generic type inference algorithm.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = true,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.newInference = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xno-check-actual",
        description = "Do not check for the presence of the 'actual' modifier in multiplatform projects.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.noCheckActual = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xno-inline",
        description = "Disable method inlining.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.noInline = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xnon-local-break-continue",
        description = "Enable experimental non-local break and continue.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = true,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.nonLocalBreakContinue = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xphases-to-dump",
        description = "Dump the backend's state both before and after these phases.",),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.phasesToDump = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xphases-to-dump-after",
        description = "Dump the backend's state after these phases.",),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.phasesToDumpAfter = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xphases-to-dump-before",
        description = "Dump the backend's state before these phases.",),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.phasesToDumpBefore = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xphases-to-validate",
        description = "Validate the backend's state both before and after these phases.",),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.phasesToValidate = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xphases-to-validate-after",
        description = "Validate the backend's state after these phases.",),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.phasesToValidateAfter = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xphases-to-validate-before",
        description = "Validate the backend's state before these phases.",),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.phasesToValidateBefore = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xplugin",
        valueDescription = "<path>",
        description = "Load plugins from the given classpath.",),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.pluginClasspaths = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xprint-configuration",
        description = "Print compiler configuration.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.printConfiguration = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xprofile-phases",
        description = "Profile backend phases.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.profilePhases = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xrender-internal-diagnostic-names",
        description = "Render the internal names of warnings and errors.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.renderInternalDiagnosticNames = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xrepl",
        description = "Run Kotlin REPL (deprecated)",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.repl = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xreport-all-warnings",
        description = "Report all warnings even if errors are found.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.reportAllWarnings = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xreport-output-files",
        description = "Report the source-to-output file mapping.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.reportOutputFiles = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xreport-perf",
        description = "Report detailed performance statistics.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.reportPerf = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xreturn-value-checker",
        valueDescription = "{check|full|disable}",
        description = "Set improved unused return value checker mode. Use 'check' to run checker only and use 'full' to also enable automatic annotation insertion.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.returnValueChecker = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xseparate-kmp-compilation",
        description = "Enables the separated compilation scheme, in which common source sets are analyzed against their own dependencies",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.separateKmpCompilationScheme = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xskip-metadata-version-check",
        description = "Allow loading classes with bad metadata versions and pre-release classes.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.skipMetadataVersionCheck = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xskip-prerelease-check",
        description = "Allow loading pre-release classes.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.skipPrereleaseCheck = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xstdlib-compilation",
        description = "Enables special features which are relevant only for stdlib compilation.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.stdlibCompilation = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xsuppress-api-version-greater-than-language-version-error",
        description = """Suppress error about API version greater than language version.
Warning: This is temporary solution (see KT-63712) intended to be used only for stdlib build.""",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.suppressApiVersionGreaterThanLanguageVersionError = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xsuppress-version-warnings",
        description = "Suppress warnings about outdated, inconsistent, or experimental language or API versions.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.suppressVersionWarnings = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xsuppress-warning",
        valueDescription = "<WARNING_NAME>",
        description = "Suppress specified warning module-wide. This option is deprecated in favor of \"-Xwarning-level\" flag",),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.suppressedDiagnostics = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xunrestricted-builder-inference",
        description = "Eliminate builder inference restrictions, for example by allowing type variables to be returned from builder inference calls.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = true,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.unrestrictedBuilderInference = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xuse-fir-experimental-checkers",
        description = "Enable experimental frontend IR checkers that are not yet ready for production.",
        deprecatedVersion = "2.2.20",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.useFirExperimentalCheckers = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xuse-fir-ic",
        description = """Compile using frontend IR internal incremental compilation.
Warning: This feature is not yet production-ready.""",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.useFirIC = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xuse-fir-lt",
        description = "Compile using the LightTree parser with the frontend IR.",
        deprecatedVersion = "2.4.20",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.useFirLT = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xverbose-phases",
        description = "Be verbose while performing the given backend phases.",),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.verbosePhases = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xverify-ir",
        valueDescription = "{none|warning|error}",
        description = "IR verification mode (no verification by default).",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.verifyIr = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xwarning-level",
        valueDescription = "<WARNING_NAME>:(error|warning|disabled)",
        description = """Set the severity of the given warning.
- `error` level raises the severity of a warning to error level (similar to -Werror but more granular)
- `disabled` level suppresses reporting of a warning (similar to -nowarn but more granular)
- `warning` level overrides -nowarn and -Werror for this specific warning (the warning will be reported/won't be considered as an error)""",),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.warningLevels = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xwhen-guards",
        description = "Enable experimental language support for when guards.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = true,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.whenGuards = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-api-version",
        valueDescription = "<version>",
        description = "Allow using declarations from only the specified version of bundled libraries.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.apiVersion = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-kotlin-home",
        valueDescription = "<path>",
        description = "Path to the Kotlin compiler home directory used for the discovery of runtime libraries.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.kotlinHome = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-language-version",
        valueDescription = "<version>",
        description = "Provide source compatibility with the specified version of Kotlin.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.languageVersion = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-opt-in",
        deprecatedName = "-Xopt-in",
        valueDescription = "<fq.name>",
        description = "Enable API usages that require opt-in with an opt-in requirement marker with the given fully qualified name.",),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.optIn = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-progressive",
        deprecatedName = "-Xprogressive",
        description = """Enable progressive compiler mode.
In this mode, deprecations and bug fixes for unstable code take effect immediately
instead of going through a graceful migration cycle.
Code written in progressive mode is backward compatible; however, code written without
progressive mode enabled may cause compilation errors in progressive mode.""",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.progressiveMode = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-script",
        description = "Evaluate the given Kotlin script (*.kts) file.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.script = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xklib-abi-version",
        valueDescription = "<version>",
        description = """Specify the custom ABI version to be written in KLIB. This option is intended only for tests.
Warning: This option does not affect KLIB ABI. Neither allows it making a KLIB backward-compatible with older ABI versions.
The only observable effect is that a custom ABI version is written to KLIB manifest file.""",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.customKlibAbiVersion = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xklib-duplicated-unique-name-strategy",
        valueDescription = "{deny|allow-all-with-warning|allow-first-with-warning}",
        description = "Klib dependencies usage strategy when multiple KLIBs has same `unique_name` property value.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.duplicatedUniqueNameStrategy = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xklib-enable-signature-clash-checks",
        description = "Enable signature uniqueness checks.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.enableSignatureClashChecks = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xklib-ir-inliner",
        valueDescription = "{intra-module|full|disabled|default}",
        description = """Set the mode of the experimental IR inliner on the first compilation stage.
- `intra-module` mode enforces inlining of the functions only from the compiled module
- `full` mode enforces inlining of all functions (from the compiled module and from all dependencies)
   Warning: This mode will trigger setting the `pre-release` flag for the compiled library.
- `disabled` mode completely disables the IR inliner
- `default` mode lets the IR inliner run in `intra-module`, `full` or `disabled` mode based on the current language version
        """,),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.irInlinerBeforeKlibSerialization = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xklib-normalize-absolute-path",
        description = "Normalize absolute paths in klibs.",
        deprecatedVersion = "2.4.20",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.normalizeAbsolutePath = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xklib-relative-path-base",
        description = """Relativize all the paths stored in a klib using the given path prefixes.
The supplied prefixes should be absolute paths to the directories containing the source code files.
Note: The prefixes are applied in the same order as they are passed in this CLI argument.""",),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.relativePathBases = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xklib-zip-file-accessor-cache-limit",
        description = "Maximum number of klibs that can be cached during compilation. Default is 64.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.klibZipFileAccessorCacheLimit = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xpartial-linkage",
        valueDescription = "{enable|disable}",
        description = "Enables partial linkage mode.",
        deprecatedVersion = "2.4.0",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.partialLinkageMode = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xpartial-linkage-loglevel",
        valueDescription = "{silent|info|warning|error}",
        description = "Define the compile-time log level for partial linkage.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.partialLinkageLogLevel = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xskip-library-special-compatibility-checks",
        description = "Skip library compatibility checks for stdlib and kotlin.test library.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.skipLibrarySpecialCompatibilityChecks = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xadd-cache",
        valueDescription = "<path>",
        description = "Path to a library to be added to the cache.",
        delimiter = Argument.Delimiters.none,),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.libraryToAddToCache = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xadd-light-debug",
        valueDescription = "{disable|enable}",
        description = """Add light debug information for optimized builds. This option is skipped in debug builds.
It's enabled by default on Darwin platforms where collected debug information is stored in a .dSYM file.
Currently this option is disabled by default on other platforms.""",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.lightDebugString = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xallocator",
        valueDescription = "std | mimalloc | custom",
        description = "Allocator used at runtime.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.allocator = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xauto-cache-dir",
        valueDescription = "<path>",
        description = "Path to the directory where caches for auto-cacheable dependencies should be put.",
        delimiter = Argument.Delimiters.none,),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.autoCacheDir = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xauto-cache-from",
        valueDescription = "<path>",
        description = """Path to the root directory from which dependencies are to be cached automatically.
By default caches will be placed into the kotlin-native system cache directory.""",
        delimiter = Argument.Delimiters.none,),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.autoCacheableFrom = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xbackend-threads",
        valueDescription = "<N>",
        description = """Run codegen by file in N parallel threads.
0 means use one thread per processor core.
The default value is 1.""",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.backendThreads = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xbinary",
        valueDescription = "<option=value>",
        description = "Specify a binary option.",),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.binaryOptions = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xbundle-id",
        valueDescription = "<id>",
        description = "Bundle ID to be set in the Info.plist file of the produced framework. This option is deprecated. Please use '-Xbinary=bundleId=<id>'.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.bundleId = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xcache-directory",
        valueDescription = "<path>",
        description = "Path to the directory containing caches.",
        delimiter = Argument.Delimiters.none,),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.cacheDirectories = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xcached-library",
        valueDescription = "<library path>,<cache path>",
        description = "Paths to a library and its cache, separated by a comma.",
        delimiter = Argument.Delimiters.none,),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.cachedLibraries = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xcheck-dependencies",
        deprecatedName = "--check_dependencies",
        description = "Check dependencies and download the missing ones.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.checkDependencies = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xcheck-state-at-external-calls",
        description = "Ensure that all calls of possibly long external functions are done in the native thread state.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.checkExternalCalls = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xcompile-from-bitcode",
        valueDescription = "<path>",
        description = "Continue compilation from the given bitcode file.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.compileFromBitcode = arrayOf(v as String) },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xdebug-info-version",
        description = "Generate debug info of the given version (1, 2).",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.debugInfoFormatVersion = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xdebug-prefix-map",
        valueDescription = "<old1=new1,old2=new2,...>",
        description = "Remap file source directory paths in debug info.",),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.debugPrefixMap = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xdestroy-runtime-mode",
        valueDescription = "<mode>",
        description = "When to destroy the runtime – 'legacy' and 'on-shutdown' are currently supported. Note that 'legacy' mode is deprecated and will be removed.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.destroyRuntimeMode = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xdump-built-caches-to",
        valueDescription = "<path>",
        description = "Path to a file where the list of all cache archives produced by this build should be written.",
        delimiter = Argument.Delimiters.none,),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.dumpBuiltCachesTo = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xdump-tests-to",
        valueDescription = "<path>",
        description = "Path to a file for dumping the list of all available tests.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.testDumpOutputPath = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xemit-lazy-objc-header",
        description = "",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.emitLazyObjCHeader = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xexport-kdoc",
        description = "Export KDoc entries in the framework header.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = true,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.exportKDoc = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xexport-library",
        valueDescription = "<path>",
        description = """A library to be included in the produced framework API.
This library must be one of the ones passed with '-library'.""",
        delimiter = Argument.Delimiters.none,),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.exportedLibraries = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xexternal-dependencies",
        valueDescription = "<path>",
        description = "Path to the file containing external dependencies.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.externalDependencies = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xfake-override-validator",
        description = "Enable the IR fake override validator.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.fakeOverrideValidator = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xfile-to-cache",
        valueDescription = "<path>",
        description = "Path to the file to cache.",
        delimiter = Argument.Delimiters.none,),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.filesToCache = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xframework-import-header",
        valueDescription = "<header>",
        description = "Add an additional header import to the framework header.",),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.frameworkImportHeaders = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xg-generate-debug-trampoline",
        valueDescription = "{disable|enable}",
        description = "Generate trampolines to make debugger breakpoint resolution more accurate (inlines, 'when', etc.).",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.generateDebugTrampolineString = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xgc",
        valueDescription = "<gc>",
        description = "GC to use – 'noop', 'stms', and 'cms' are currently supported. This works only with '-memory-model experimental'.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.gc = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xheader-klib-path",
        description = "Save a klib that only contains the public ABI to the given path.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.headerKlibPath = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xic-cache-dir",
        valueDescription = "<path>",
        description = "Path to the directory where incremental build caches should be put.",
        delimiter = Argument.Delimiters.none,),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.incrementalCacheDir = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xinclude",
        valueDescription = "<path>",
        description = "A path to an intermediate library that should be processed in the same manner as source files.",),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.includes = (v as Array<String>).joinToString(",") },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xir-property-lazy-initialization",
        valueDescription = "{disable|enable}",
        description = "Initialize top level properties lazily per file.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.propertyLazyInitialization = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xkonan-data-dir",
        description = "Custom path to the location of konan distributions.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.konanDataDir = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xllvm-lto-passes",
        description = "Custom set of LLVM passes to run as the LTOOptimizationPipeline.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.llvmLTOPasses = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xllvm-module-passes",
        description = "Custom set of LLVM passes to run as the ModuleOptimizationPipeline.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.llvmModulePasses = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xllvm-variant",
        valueDescription = "{dev|user|absolute path to llvm}",
        description = "Choose the LLVM distribution that will be used during compilation.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.llvmVariant = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xmake-per-file-cache",
        description = "Force the compiler to produce per-file caches.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.makePerFileCache = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xmanifest-native-targets",
        description = "Comma-separated list that will be written as the value of 'native_targets' property in the .klib manifest. Unknown values are discarded.",),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.manifestNativeTargets = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xno-objc-generics",
        description = "Disable generics support for framework header.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.noObjcGenerics = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xomit-framework-binary",
        description = "Omit binary when compiling the framework.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.omitFrameworkBinary = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xoverride-clang-options",
        valueDescription = "<arg1,arg2,...>",
        description = "Explicit list of Clang options.",),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.clangOptions = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xoverride-konan-properties",
        valueDescription = "key1=value1;key2=value2;...",
        description = "Override values from 'konan.properties' with the given ones.",
        delimiter = Argument.Delimiters.semicolon,),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.overrideKonanProperties = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xpre-link-caches",
        valueDescription = "{disable|enable}",
        description = "Perform caches pre-linking.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.preLinkCaches = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xprint-bitcode",
        deprecatedName = "--print_bitcode",
        description = "Print LLVM bitcode.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.printBitCode = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xprint-files",
        description = "Print files.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.printFiles = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xprint-ir",
        deprecatedName = "--print_ir",
        description = "Print IR.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.printIr = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xpurge-user-libs",
        deprecatedName = "--purge_user_libs",
        description = "Don't link unused libraries even if explicitly specified.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.purgeUserLibs = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xread-dependencies-from",
        valueDescription = "<path>",
        description = "Serialized dependencies to use for linking.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.serializedDependencies = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xrefines-paths",
        valueDescription = "<path>",
        description = "Paths to output directories for refined modules (modules whose 'expect' declarations this module can actualize).",),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.refinesPaths = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xruntime",
        deprecatedName = "--runtime",
        valueDescription = "<path>",
        description = "Override the standard 'runtime.bc' location.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.runtimeFile = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xruntime-logs",
        valueDescription = "<tag1=level1,tag2=level2,...>",
        description = "Enable logging of Native runtime internals.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.runtimeLogs = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xsave-llvm-ir-after",
        description = "Save the result of the Kotlin IR to LLVM IR translation to '-Xsave-llvm-ir-directory'.",),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.saveLlvmIrAfter = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xsave-llvm-ir-directory",
        description = "Directory that should contain the results of '-Xsave-llvm-ir-after=<phase>'.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.saveLlvmIrDirectory = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xshort-module-name",
        valueDescription = "<name>",
        description = "A short name used to denote this library in the IDE and in a generated Objective-C header.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.shortModuleName = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xstatic-framework",
        description = "Create a framework with a static library instead of a dynamic one.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.staticFramework = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xtemporary-files-dir",
        deprecatedName = "--temporary_files_dir",
        valueDescription = "<path>",
        description = "Save temporary files to the given directory.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.temporaryFilesDir = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xverify-bitcode",
        deprecatedName = "--verify_bitcode",
        description = "Verify LLVM bitcode after each method.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.verifyBitCode = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xverify-compiler",
        description = "Verify the compiler.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.verifyCompiler = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xworker-exception-handling",
        valueDescription = "<mode>",
        description = "Unhandled exception processing in 'Worker.executeAfter'. Possible values: 'legacy' and 'use-hook'. The default value is 'legacy' and for '-memory-model experimental', the default value is 'use-hook'.",
        deprecatedVersion = "2.4.20",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.workerExceptionHandling = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xwrite-dependencies-of-produced-klib-to",
        valueDescription = "<path>",
        description = "Write file containing the paths of dependencies used during klib compilation to the provided path",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.writeDependenciesOfProducedKlibTo = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xwrite-dependencies-to",
        description = "Path for writing backend dependencies.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.saveDependenciesPath = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-enable-assertions",
        shortName = "-ea",
        deprecatedName = "-enable_assertions",
        description = "Enable runtime assertions in generated code.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.enableAssertions = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-entry",
        shortName = "-e",
        valueDescription = "<name>",
        description = "Qualified entry point name.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.mainPackage = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-friend-modules",
        valueDescription = "<path>",
        description = "Paths to friend modules.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.friendModules = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-g",
        description = "Enable the emission of debug information.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.debug = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-generate-no-exit-test-runner",
        shortName = "-trn",
        description = "Produce a runner for unit tests that doesn't force an exit.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.generateNoExitTestRunner = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-generate-test-runner",
        shortName = "-tr",
        deprecatedName = "-generate_test_runner",
        description = "Produce a runner for unit tests.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.generateTestRunner = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-generate-worker-test-runner",
        shortName = "-trw",
        description = "Produce a worker runner for unit tests.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.generateWorkerTestRunner = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-include-binary",
        shortName = "-ib",
        deprecatedName = "-includeBinary",
        valueDescription = "<path>",
        description = "Pack the given external binary into the klib.",),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.includeBinaries = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-library",
        shortName = "-l",
        valueDescription = "<path>",
        description = "Link with the given library.",
        delimiter = Argument.Delimiters.none,),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.libraries = (v as Array<String>).joinToString(",") },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-linker-option",
        valueDescription = "<arg>",
        description = "Pass the given argument to the linker.",
        delimiter = Argument.Delimiters.none,),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.singleLinkerArguments = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-linker-options",
        deprecatedName = "-linkerOpts",
        valueDescription = "<arg>",
        description = "Pass arguments to the linker.",
        delimiter = Argument.Delimiters.space,),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.linkerArguments = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-list-targets",
        deprecatedName = "-list_targets",
        description = "List available hardware targets.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.listTargets = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-manifest",
        valueDescription = "<path>",
        description = "Provide a manifest addend file.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.manifestFile = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-memory-model",
        valueDescription = "<model>",
        description = "Choose the memory model to be used – 'strict' and 'experimental' are currently supported.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.memoryModel = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-module-name",
        deprecatedName = "-module_name",
        valueDescription = "<name>",
        description = "Specify a name for the compilation module.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.moduleName = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-native-library",
        shortName = "-nl",
        deprecatedName = "-nativelibrary",
        valueDescription = "<path>",
        description = "Include the given native bitcode library.",
        delimiter = Argument.Delimiters.none,),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.nativeLibraries = v as Array<String> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-no-default-libs",
        deprecatedName = "-nodefaultlibs",
        description = "Don't link the libraries from dist/klib automatically.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.nodefaultlibs = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-nomain",
        description = "Assume the 'main' entry point will be provided by external libraries.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.nomain = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-nopack",
        description = "Don't pack the library into a klib file.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.nopack = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-nostdlib",
        description = "Don't link with the stdlib.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.nostdlib = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-opt",
        description = "Enable optimizations during compilation.",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.optimization = v as Boolean },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-output",
        shortName = "-o",
        valueDescription = "<name>",
        description = "Output name.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.outputName = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-produce",
        shortName = "-p",
        valueDescription = "{program|static|dynamic|framework|library|bitcode}",
        description = "Specify the output file kind.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.produce = v as String },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-target",
        valueDescription = "<target>",
        description = "Set the hardware target.",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = false,
        setter = { args, v -> args.target = v as String },
    )
)

private val removedNativeArgumentDescriptors: List<NativeArgumentDescriptor> = listOf(
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xuse-k2",
        description = "Compile using the K2 compiler pipeline.",
        deprecatedVersion = "1.9.0",
        removedVersion = "2.2.0",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = true,
        setter = { _, _ -> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xverify-ir-nested-offsets",
        description = "Check that offsets of nested IR elements conform to offsets of their containers. Only has effect if '-Xverify-ir' is not 'none'.",
        removedVersion = "2.4.20",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = true,
        setter = { _, _ -> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xverify-ir-visibility",
        description = "Check for visibility violations in IR when validating it before running any lowerings. Only has effect if '-Xverify-ir' is not 'none'.",
        removedVersion = "2.4.20",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = true,
        setter = { _, _ -> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xcompile-builtins-as-part-of-stdlib",
        description = "Enable behaviour needed to compile builtins as part of JVM stdlib",
        removedVersion = "2.3.20",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = true,
        setter = { _, _ -> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xcompile-java",
        description = "Reuse 'javac' analysis and compile Java source files.",
        removedVersion = "2.4.0",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = true,
        setter = { _, _ -> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xir-inliner",
        description = "Inline functions using the IR inliner instead of the bytecode inliner.",
        removedVersion = "2.3.0",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = true,
        setter = { _, _ -> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xjavac-arguments",
        valueDescription = "<option[,]>",
        description = "Java compiler arguments.",
        removedVersion = "2.4.0",),
        kind = ArgKind.STRING_ARRAY,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = true,
        setter = { _, _ -> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xserialize-ir",
        valueDescription = "{none|inline|all}",
        description = "Save the IR to metadata (Experimental).",
        removedVersion = "2.4.0",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = true,
        setter = { _, _ -> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xuse-javac",
        description = "Use javac for Java source and class file analysis.",
        removedVersion = "2.4.0",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = true,
        setter = { _, _ -> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xuse-k2-kapt",
        description = "Enable the experimental support for K2 KAPT.",
        removedVersion = "2.3.0",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = true,
        setter = { _, _ -> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xvalue-classes",
        description = "Enable experimental value classes.",
        removedVersion = "2.4.20",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = true,
        setter = { _, _ -> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xtyped-arrays",
        description = "This option does nothing and is left for compatibility with the legacy backend.",
        deprecatedVersion = "2.1.0",
        removedVersion = "2.3.0",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = true,
        setter = { _, _ -> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-output",
        valueDescription = "<filepath>",
        description = "",
        deprecatedVersion = "2.1.0",
        removedVersion = "2.2.0",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = true,
        setter = { _, _ -> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-Xg0",
        description = "Add light debug information.",
        deprecatedVersion = "1.5.20",
        removedVersion = "2.4.20",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = true,
        setter = { _, _ -> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-library-version",
        shortName = "-lv",
        valueDescription = "<version>",
        description = "The library version.",
        deprecatedVersion = "2.0.20",
        removedVersion = "2.4.20",),
        kind = ArgKind.STRING,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = true,
        setter = { _, _ -> },
    ),
    NativeArgumentDescriptor(
        argument = Argument(value = "-no-endorsed-libs",
        description = "Don't link endorsed libraries from the dist automatically.",
        deprecatedVersion = "1.9.20",
        removedVersion = "2.4.20",),
        kind = ArgKind.BOOLEAN,
        changesLanguageFeatures = false,
        legalStringValues = emptySet(),
        removed = true,
        setter = { _, _ -> },
    )
)

internal val activeCliArgMap: Map<String, NativeArgumentDescriptor> = buildMap {
    for (desc in allNativeArgumentDescriptors) {
        val a = desc.argument
        for (key in listOf(a.value, a.shortName, a.deprecatedName)) {
            if (key.isNotEmpty()) put(key, desc)
        }
    }
}

private val removedCliArgMap: Map<String, NativeArgumentDescriptor> = buildMap {
    for (desc in removedNativeArgumentDescriptors) {
        val a = desc.argument
        for (key in listOf(a.value, a.shortName, a.deprecatedName)) {
            if (key.isNotEmpty()) put(key, desc)
        }
    }
}

private class ExplicitArgsKey(val instance: CommonCompilerArguments) {
    override fun equals(other: Any?): Boolean = other is ExplicitArgsKey && other.instance === instance
    override fun hashCode(): Int = instance.hashCode()
}

private val explicitArgumentsStore = LinkedHashMap<ExplicitArgsKey, MutableMap<NativeArgumentDescriptor, MutableList<Any>>>()

internal var CommonCompilerArguments.explicitNativeArguments: Map<NativeArgumentDescriptor, List<Any>>
    get() = explicitArgumentsStore[ExplicitArgsKey(this)].orEmpty()
    set(value) {
        val mutable = LinkedHashMap<NativeArgumentDescriptor, MutableList<Any>>()
        for ((k, v) in value) {
            mutable[k] = v.toMutableList()
        }
        explicitArgumentsStore[ExplicitArgsKey(this)] = mutable
    }

fun <A : CommonCompilerArguments> parseCommandLineArguments(
    args: List<String>,
    result: A,
    overrideArguments: Boolean = false,
) {
    val errors = lazy { result.errors ?: ArgumentParseErrors().also { result.errors = it } }
    val preprocessed = preprocessCommandLineArguments(args, errors)
    parsePreprocessedCommandLineArguments(preprocessed, result, errors, overrideArguments)
}

fun <A : CommonCompilerArguments> parseCommandLineArgumentsFromEnvironment(arguments: A) {
    val settingsFromEnvironment = CompilerSystemProperties.LANGUAGE_VERSION_SETTINGS.value?.takeIf { it.isNotEmpty() }
        ?.split(Regex("""\\s"""))
        ?.filterNot { it.isBlank() }
        ?: return
    parseCommandLineArguments(settingsFromEnvironment, arguments, overrideArguments = true)
}

inline fun <reified T : CommonCompilerArguments> parseCommandLineArguments(args: List<String>): T {
    val instance = when (T::class) {
        K2NativeCompilerArguments::class -> K2NativeCompilerArguments() as T
        else -> error("Unsupported CommonCompilerArguments type: ${T::class}")
    }
    parseCommandLineArguments(args, instance)
    return instance
}

private fun <A : CommonCompilerArguments> parsePreprocessedCommandLineArguments(
    args: List<String>,
    result: A,
    errors: Lazy<ArgumentParseErrors>,
    overrideArguments: Boolean,
) {
    val nativeResult = result as? K2NativeCompilerArguments
        ?: error("Only K2NativeCompilerArguments is supported in Native CLI parser, got ${result::class}")

    var freeArgsStarted = false
    val freeArgs = ArrayList<String>()
    val explicitArgs = LinkedHashMap<NativeArgumentDescriptor, MutableList<Any>>()

    var i = 0
    loop@ while (i < args.size) {
        val arg = args[i++]

        if (freeArgsStarted) {
            freeArgs.add(arg)
            continue
        }
        if (arg == FREE_ARGS_DELIMITER) {
            freeArgsStarted = true
            continue
        }

        val delimiter = when {
            arg.startsWith("-XXLanguage") -> ':'
            else -> '='
        }
        val key = arg.substringBefore(delimiter)
        var descriptor = activeCliArgMap[key]
        var removedArg = false

        if (descriptor == null) {
            descriptor = removedCliArgMap[key]
            removedArg = true
        }

        if (descriptor == null) {
            when {
                arg.startsWith(ADVANCED_ARGUMENT_PREFIX) -> errors.value.unknownExtraFlags.add(arg)
                arg.startsWith("-") -> errors.value.unknownArgs.add(arg)
                else -> freeArgs.add(arg)
            }
            continue
        }

        val argument = descriptor.argument

        // Disallow -shortName=value
        if (key != arg && key == argument.shortName) {
            errors.value.unknownArgs.add(arg)
            continue
        }

        val deprecatedName = argument.deprecatedName
        if (deprecatedName == key) {
            errors.value.deprecatedArguments[deprecatedName] = argument.value
        }

        if (argument.value == arg) {
            if (argument.isAdvanced && descriptor.kind != ArgKind.BOOLEAN) {
                errors.value.extraArgumentsPassedInObsoleteForm.add(arg)
            }
        }

        val existingValues by lazy(LazyThreadSafetyMode.NONE) {
            explicitArgs.getOrPut(descriptor) { mutableListOf() }
        }

        val newValue: Any = if (descriptor.kind == ArgKind.BOOLEAN) {
            parseBooleanValue(arg, descriptor, delimiter, errors).also { existingValues.add(it) }
        } else {
            val stringValue: String = when {
                arg.startsWith(argument.value + delimiter) -> {
                    val legalValues = descriptor.legalStringValues
                    arg.substring(argument.value.length + 1).also {
                        if (legalValues.isNotEmpty() && !legalValues.contains(it)) {
                            errors.value.stringLangFeatureArgumentsWithIncorrectValue.add(arg to legalValues)
                        }
                    }
                }
                argument.deprecatedName.isNotEmpty() && arg.startsWith(argument.deprecatedName + delimiter) -> {
                    arg.substring(argument.deprecatedName.length + 1)
                }
                i == args.size -> {
                    errors.value.argumentsWithoutValue.add(arg)
                    break@loop
                }
                else -> {
                    args[i++]
                }
            }

            when (descriptor.kind) {
                ArgKind.STRING -> stringValue.also { existingValues.add(it) }
                ArgKind.STRING_ARRAY -> convertArrayOfStrings(argument, stringValue, overrideArguments, existingValues)
                ArgKind.BOOLEAN -> error("Unreachable")
            }
        }

        if (!removedArg) {
            descriptor.setter(nativeResult, newValue)
        }
    }

    result.freeArgs += freeArgs
    result.explicitNativeArguments = explicitArgs

    val internalArguments = ArrayList<ManualLanguageFeatureSetting>()
    for (arg in nativeResult.manuallyConfiguredFeatures) {
        val featureSetting = LanguageSettingsParser.parseLanguageFeature(arg, "-XXLanguage:$arg", errors.value) ?: continue
        internalArguments.removeAll {
            it.languageFeature == featureSetting.languageFeature
        }
        internalArguments.add(featureSetting)
    }
    result.updateInternalArguments(internalArguments, overrideArguments)
}

private fun parseBooleanValue(
    arg: String,
    descriptor: NativeArgumentDescriptor,
    delimiter: Char,
    errors: Lazy<ArgumentParseErrors>,
): Boolean {
    val argumentValue = descriptor.argument.value
    return if (arg.startsWith(argumentValue + delimiter)) {
        val changesLangFeatures = descriptor.changesLanguageFeatures
        when (arg.substring(argumentValue.length + 1)) {
            "true" -> true
            "false" -> false
            else -> true.also {
                if (!changesLangFeatures) {
                    errors.value.booleanArgumentsWithIncorrectValue.add(arg)
                }
            }
        }.also {
            if (changesLangFeatures) {
                errors.value.booleanLangFeatureArgumentsWithValue.add(arg)
            }
        }
    } else {
        true
    }
}

private fun convertArrayOfStrings(
    argument: Argument,
    stringValue: String,
    overrideArguments: Boolean,
    existingValues: MutableList<Any>,
): Array<String> {
    val resolvedDelimiter = argument.resolvedDelimiter

    val newElements: List<String> = if (resolvedDelimiter.isNullOrEmpty()) {
        listOf(stringValue)
    } else {
        stringValue.split(resolvedDelimiter)
    }

    val oldValue: MutableList<String>? = if (!overrideArguments) {
        existingValues.firstIsInstanceOrNull<MutableList<String>>()
    } else {
        null
    }

    val resultElements: MutableList<String> = oldValue?.also { it.addAll(newElements) }
        ?: newElements.toMutableList().also { existingValues.add(it) }

    return resultElements.toTypedArray()
}

private fun <A : CommonCompilerArguments> A.updateInternalArguments(
    newInternalArguments: ArrayList<ManualLanguageFeatureSetting>,
    overrideArguments: Boolean,
) {
    val filteredExistingArguments = if (overrideArguments) {
        internalArguments.filter { existingArgument ->
            newInternalArguments.none { it.languageFeature == existingArgument.languageFeature }
        }
    } else internalArguments

    internalArguments = filteredExistingArguments + newInternalArguments
}

fun MessageCollector.reportArgumentParseProblems(arguments: CommonCompilerArguments) {
    for ((key, values) in arguments.explicitNativeArguments) {
        if (values.size <= 1 || values.distinct().size == 1) continue

        val argName = key.argument.value
        val valuesString = values.joinToString("', '")
        val message = "Argument '$argName' is passed multiple times: '$valuesString'. The last value will be used."
        report(CompilerMessageSeverity.STRONG_WARNING, message)
    }

    val errors = arguments.errors ?: return
    for (flag in errors.unknownExtraFlags) {
        report(CompilerMessageSeverity.STRONG_WARNING, "Flag is not supported by this version of the compiler: $flag")
    }
    for (argument in errors.extraArgumentsPassedInObsoleteForm) {
        report(
            CompilerMessageSeverity.STRONG_WARNING,
            "Advanced option value is passed in an obsolete form. Please use the '=' character to specify the value: $argument=..."
        )
    }
    for ((deprecatedName, newName) in errors.deprecatedArguments) {
        report(CompilerMessageSeverity.STRONG_WARNING, "Argument $deprecatedName is deprecated. Please use $newName instead")
    }
    for (argfileError in errors.argfileErrors) {
        report(CompilerMessageSeverity.STRONG_WARNING, argfileError)
    }

    reportUnsafeInternalArgumentsIfAny(arguments)

    for ((severity, internalArgumentsProblem) in errors.internalArgumentsParsingProblems) {
        report(severity, internalArgumentsProblem)
    }
}

private fun MessageCollector.reportUnsafeInternalArgumentsIfAny(arguments: CommonCompilerArguments) {
    val unsafeArguments = arguments.internalArguments.filterNot {
        it.languageFeature.actuallyEnabledInProgressiveMode && it.state == LanguageFeature.State.ENABLED
    }

    if (unsafeArguments.isNotEmpty()) {
        val unsafeArgumentsString = unsafeArguments.joinToString(prefix = "\n", postfix = "\n\n", separator = "\n") {
            it.stringRepresentation
        }

        report(
            CompilerMessageSeverity.STRONG_WARNING,
            "ATTENTION!\n" +
                    "This build uses unsafe internal compiler arguments:\n" +
                    unsafeArgumentsString +
                    "This mode is not recommended for production use,\n" +
                    "as no stability/compatibility guarantees are given on\n" +
                    "compiler or generated code. Use it at your own risk!\n"
        )
    }
}

fun copyCommonToolArguments(from: CommonCompilerArguments, to: CommonCompilerArguments): CommonCompilerArguments {
    to.allWarningsAsErrors = from.allWarningsAsErrors
    to.extraHelp = from.extraHelp
    to.extraWarnings = from.extraWarnings
    to.freeArgs = from.freeArgs
    to.help = from.help
    to.internalArguments = from.internalArguments
    to.suppressWarnings = from.suppressWarnings
    to.verbose = from.verbose
    to.version = from.version

    return to
}

fun copyCommonCompilerArguments(from: CommonCompilerArguments, to: CommonCompilerArguments): CommonCompilerArguments {
    copyCommonToolArguments(from, to)

    to.allowAnyScriptsInSourceRoots = from.allowAnyScriptsInSourceRoots
    to.allowConditionImpliesReturnsContracts = from.allowConditionImpliesReturnsContracts
    to.allowContractsOnMoreFunctions = from.allowContractsOnMoreFunctions
    to.allowHoldsinContract = from.allowHoldsinContract
    to.allowKotlinPackage = from.allowKotlinPackage
    to.allowReifiedTypeInCatch = from.allowReifiedTypeInCatch
    to.allowReturnsResultOf = from.allowReturnsResultOf
    to.annotationDefaultTarget = from.annotationDefaultTarget
    to.annotationTargetAll = from.annotationTargetAll
    to.apiVersion = from.apiVersion
    to.autoAdvanceApiVersion = from.autoAdvanceApiVersion
    to.autoAdvanceLanguageVersion = from.autoAdvanceLanguageVersion
    to.checkPhaseConditions = from.checkPhaseConditions
    to.collectionLiterals = from.collectionLiterals
    to.commonSources = from.commonSources.copyOf()
    to.companionBlocksAndExtensions = from.companionBlocksAndExtensions
    to.consistentDataClassCopyVisibility = from.consistentDataClassCopyVisibility
    to.contextParameters = from.contextParameters
    to.contextReceivers = from.contextReceivers
    to.contextSensitiveResolution = from.contextSensitiveResolution
    to.dataFlowBasedExhaustiveness = from.dataFlowBasedExhaustiveness
    to.debugLevelCompilerChecks = from.debugLevelCompilerChecks
    to.detailedPerf = from.detailedPerf
    to.directJavaActualization = from.directJavaActualization
    to.disableDefaultScriptingPlugin = from.disableDefaultScriptingPlugin
    to.disableIrCheckers = from.disableIrCheckers.copyOf()
    to.disablePhases = from.disablePhases.copyOf()
    to.dontSortSourceFiles = from.dontSortSourceFiles
    to.dontWarnOnErrorSuppression = from.dontWarnOnErrorSuppression
    to.dumpArgumentsDir = from.dumpArgumentsDir
    to.dumpDirectory = from.dumpDirectory
    to.dumpOnlyFqName = from.dumpOnlyFqName
    to.dumpPerf = from.dumpPerf
    to.eagerLambdaAnalysis = from.eagerLambdaAnalysis
    to.enableAdditionalIrCheckers = from.enableAdditionalIrCheckers.copyOf()
    to.escapingFunctions = from.escapingFunctions.copyOf()
    to.expectActualClasses = from.expectActualClasses
    to.explicitApi = from.explicitApi
    to.explicitBackingFields = from.explicitBackingFields
    to.explicitContextArguments = from.explicitContextArguments
    to.explicitReturnTypes = from.explicitReturnTypes
    to.firAggressivePruning = from.firAggressivePruning
    to.fragmentDependencies = from.fragmentDependencies.copyOf()
    to.fragmentFriendDependencies = from.fragmentFriendDependencies.copyOf()
    to.fragmentIncrementalClasspath = from.fragmentIncrementalClasspath.copyOf()
    to.fragmentRefines = from.fragmentRefines.copyOf()
    to.fragmentSources = from.fragmentSources.copyOf()
    to.fragments = from.fragments.copyOf()
    to.headerMode = from.headerMode
    to.headerModeType = from.headerModeType
    to.ignoreConstOptimizationErrors = from.ignoreConstOptimizationErrors
    to.incrementalCompilation = from.incrementalCompilation
    to.inlineClasses = from.inlineClasses
    @Suppress("DEPRECATION")
    to.intellijPluginRoot = from.intellijPluginRoot
    to.intrinsicConstEvaluation = from.intrinsicConstEvaluation
    to.kotlinHome = from.kotlinHome
    to.languageVersion = from.languageVersion
    to.lenientMode = from.lenientMode
    to.listPhases = from.listPhases
    to.localTypeAliases = from.localTypeAliases
    to.manuallyConfiguredFeatures = from.manuallyConfiguredFeatures.copyOf()
    to.metadataKlib = from.metadataKlib
    to.metadataVersion = from.metadataVersion
    to.multiDollarInterpolation = from.multiDollarInterpolation
    to.multiPlatform = from.multiPlatform
    to.nameBasedDestructuring = from.nameBasedDestructuring
    to.nestedTypeAliases = from.nestedTypeAliases
    to.newInference = from.newInference
    to.noCheckActual = from.noCheckActual
    to.noInline = from.noInline
    to.nonLocalBreakContinue = from.nonLocalBreakContinue
    to.optIn = from.optIn.copyOf()
    to.phasesToDump = from.phasesToDump.copyOf()
    to.phasesToDumpAfter = from.phasesToDumpAfter.copyOf()
    to.phasesToDumpBefore = from.phasesToDumpBefore.copyOf()
    to.phasesToValidate = from.phasesToValidate.copyOf()
    to.phasesToValidateAfter = from.phasesToValidateAfter.copyOf()
    to.phasesToValidateBefore = from.phasesToValidateBefore.copyOf()
    to.pluginClasspaths = from.pluginClasspaths?.copyOf()
    to.pluginConfigurations = from.pluginConfigurations?.copyOf()
    to.pluginOptions = from.pluginOptions?.copyOf()
    to.pluginOrderConstraints = from.pluginOrderConstraints?.copyOf()
    to.printConfiguration = from.printConfiguration
    to.profilePhases = from.profilePhases
    to.progressiveMode = from.progressiveMode
    to.renderInternalDiagnosticNames = from.renderInternalDiagnosticNames
    to.repl = from.repl
    to.reportAllWarnings = from.reportAllWarnings
    to.reportOutputFiles = from.reportOutputFiles
    to.reportPerf = from.reportPerf
    to.returnValueChecker = from.returnValueChecker
    to.script = from.script
    to.separateKmpCompilationScheme = from.separateKmpCompilationScheme
    to.skipMetadataVersionCheck = from.skipMetadataVersionCheck
    to.skipPrereleaseCheck = from.skipPrereleaseCheck
    to.stdlibCompilation = from.stdlibCompilation
    to.suppressApiVersionGreaterThanLanguageVersionError = from.suppressApiVersionGreaterThanLanguageVersionError
    to.suppressVersionWarnings = from.suppressVersionWarnings
    to.suppressedDiagnostics = from.suppressedDiagnostics.copyOf()
    to.unrestrictedBuilderInference = from.unrestrictedBuilderInference
    @Suppress("DEPRECATION")
    to.useFirExperimentalCheckers = from.useFirExperimentalCheckers
    to.useFirIC = from.useFirIC
    @Suppress("DEPRECATION")
    to.useFirLT = from.useFirLT
    to.verbosePhases = from.verbosePhases.copyOf()
    to.verifyIr = from.verifyIr
    to.warningLevels = from.warningLevels.copyOf()
    to.whenGuards = from.whenGuards

    return to
}

fun copyCommonKlibBasedCompilerArguments(from: CommonKlibBasedCompilerArguments, to: CommonKlibBasedCompilerArguments): CommonKlibBasedCompilerArguments {
    copyCommonCompilerArguments(from, to)

    to.customKlibAbiVersion = from.customKlibAbiVersion
    to.duplicatedUniqueNameStrategy = from.duplicatedUniqueNameStrategy
    to.enableSignatureClashChecks = from.enableSignatureClashChecks
    to.irInlinerBeforeKlibSerialization = from.irInlinerBeforeKlibSerialization
    to.klibZipFileAccessorCacheLimit = from.klibZipFileAccessorCacheLimit
    @Suppress("DEPRECATION")
    to.normalizeAbsolutePath = from.normalizeAbsolutePath
    to.partialLinkageLogLevel = from.partialLinkageLogLevel
    @Suppress("DEPRECATION")
    to.partialLinkageMode = from.partialLinkageMode
    to.relativePathBases = from.relativePathBases.copyOf()
    to.skipLibrarySpecialCompatibilityChecks = from.skipLibrarySpecialCompatibilityChecks

    return to
}

fun copyK2NativeCompilerArguments(from: K2NativeCompilerArguments, to: K2NativeCompilerArguments): K2NativeCompilerArguments {
    copyCommonKlibBasedCompilerArguments(from, to)

    to.allocator = from.allocator
    to.autoCacheDir = from.autoCacheDir
    to.autoCacheableFrom = from.autoCacheableFrom.copyOf()
    to.backendThreads = from.backendThreads
    to.binaryOptions = from.binaryOptions.copyOf()
    to.bundleId = from.bundleId
    to.cacheDirectories = from.cacheDirectories.copyOf()
    to.cachedLibraries = from.cachedLibraries.copyOf()
    to.checkDependencies = from.checkDependencies
    to.checkExternalCalls = from.checkExternalCalls
    to.clangOptions = from.clangOptions.copyOf()
    to.compileFromBitcode = from.compileFromBitcode?.copyOf()
    to.debug = from.debug
    to.debugInfoFormatVersion = from.debugInfoFormatVersion
    to.debugPrefixMap = from.debugPrefixMap.copyOf()
    to.destroyRuntimeMode = from.destroyRuntimeMode
    to.dumpBuiltCachesTo = from.dumpBuiltCachesTo
    to.emitLazyObjCHeader = from.emitLazyObjCHeader
    to.enableAssertions = from.enableAssertions
    to.exportKDoc = from.exportKDoc
    to.exportedLibraries = from.exportedLibraries?.copyOf()
    to.externalDependencies = from.externalDependencies
    to.fakeOverrideValidator = from.fakeOverrideValidator
    to.filesToCache = from.filesToCache.copyOf()
    to.frameworkImportHeaders = from.frameworkImportHeaders.copyOf()
    to.friendModules = from.friendModules
    to.gc = from.gc
    to.generateDebugTrampolineString = from.generateDebugTrampolineString
    to.generateNoExitTestRunner = from.generateNoExitTestRunner
    to.generateTestRunner = from.generateTestRunner
    to.generateWorkerTestRunner = from.generateWorkerTestRunner
    to.headerKlibPath = from.headerKlibPath
    to.includeBinaries = from.includeBinaries.copyOf()
    to.includes = from.includes
    to.incrementalCacheDir = from.incrementalCacheDir
    to.konanDataDir = from.konanDataDir
    to.libraries = from.libraries
    to.libraryToAddToCache = from.libraryToAddToCache
    to.lightDebugString = from.lightDebugString
    to.linkerArguments = from.linkerArguments.copyOf()
    to.listTargets = from.listTargets
    to.llvmLTOPasses = from.llvmLTOPasses
    to.llvmModulePasses = from.llvmModulePasses
    to.llvmVariant = from.llvmVariant
    to.mainPackage = from.mainPackage
    to.makePerFileCache = from.makePerFileCache
    to.manifestFile = from.manifestFile
    to.manifestNativeTargets = from.manifestNativeTargets.copyOf()
    to.memoryModel = from.memoryModel
    to.moduleName = from.moduleName
    to.nativeLibraries = from.nativeLibraries.copyOf()
    to.noObjcGenerics = from.noObjcGenerics
    to.nodefaultlibs = from.nodefaultlibs
    to.nomain = from.nomain
    to.nopack = from.nopack
    to.nostdlib = from.nostdlib
    to.omitFrameworkBinary = from.omitFrameworkBinary
    to.optimization = from.optimization
    to.outputName = from.outputName
    to.overrideKonanProperties = from.overrideKonanProperties.copyOf()
    to.preLinkCaches = from.preLinkCaches
    to.printBitCode = from.printBitCode
    to.printFiles = from.printFiles
    to.printIr = from.printIr
    to.produce = from.produce
    to.propertyLazyInitialization = from.propertyLazyInitialization
    to.purgeUserLibs = from.purgeUserLibs
    to.refinesPaths = from.refinesPaths.copyOf()
    to.runtimeFile = from.runtimeFile
    to.runtimeLogs = from.runtimeLogs
    to.saveDependenciesPath = from.saveDependenciesPath
    to.saveLlvmIrAfter = from.saveLlvmIrAfter.copyOf()
    to.saveLlvmIrDirectory = from.saveLlvmIrDirectory
    to.serializedDependencies = from.serializedDependencies
    to.shortModuleName = from.shortModuleName
    to.singleLinkerArguments = from.singleLinkerArguments.copyOf()
    to.staticFramework = from.staticFramework
    to.target = from.target
    to.temporaryFilesDir = from.temporaryFilesDir
    to.testDumpOutputPath = from.testDumpOutputPath
    to.verifyBitCode = from.verifyBitCode
    to.verifyCompiler = from.verifyCompiler
    @Suppress("DEPRECATION")
    to.workerExceptionHandling = from.workerExceptionHandling
    to.writeDependenciesOfProducedKlibTo = from.writeDependenciesOfProducedKlibTo

    return to
}

fun K2NativeCompilerArguments.copyOf(): K2NativeCompilerArguments =
    copyK2NativeCompilerArguments(this, K2NativeCompilerArguments())
