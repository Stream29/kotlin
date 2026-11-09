/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.konan.objcexport

import org.jetbrains.kotlin.K1Deprecation
import org.jetbrains.kotlin.backend.konan.*
import org.jetbrains.kotlin.backend.konan.descriptors.getPackageFragments
import org.jetbrains.kotlin.backend.konan.descriptors.isInterface
import org.jetbrains.kotlin.backend.konan.driver.NativeBackendPhaseContext
import org.jetbrains.kotlin.backend.konan.llvm.CodeGenerator
import org.jetbrains.kotlin.backend.konan.llvm.objcexport.ObjCExportBlockCodeGenerator
import org.jetbrains.kotlin.backend.konan.llvm.objcexport.ObjCExportCodeGenerator
import org.jetbrains.kotlin.builtins.*
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.builtins.StandardNames
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.config.nativeBinaryOptions.BinaryOptions
import org.jetbrains.kotlin.config.nativeBinaryOptions.UnitSuspendFunctionObjCExport
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.konan.config.*
import org.jetbrains.kotlin.konan.exec.Command
import org.jetbrains.kotlin.konan.file.*
import org.jetbrains.kotlin.konan.file.createTempFile
import org.jetbrains.kotlin.konan.target.CompilerOutputKind
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.renderer.DescriptorRenderer

sealed class TypeBridge
object ReferenceBridge : TypeBridge()
data class BlockPointerBridge(val numberOfParameters: Int, val returnsVoid: Boolean) : TypeBridge()
data class ValueTypeBridge(val objCValueType: ObjCValueType) : TypeBridge()

enum class ObjCValueType(val encoding: String) {
    BOOL("c"),
    UNICHAR("S"),
    CHAR("c"),
    SHORT("s"),
    INT("i"),
    LONG_LONG("q"),
    UNSIGNED_CHAR("C"),
    UNSIGNED_SHORT("S"),
    UNSIGNED_INT("I"),
    UNSIGNED_LONG_LONG("Q"),
    FLOAT("f"),
    DOUBLE("d"),
    VECTOR_FLOAT_128(""),
    POINTER("^v")
}

enum class NSNumberKind(val mappedKotlinClassId: ClassId?, val factorySelector: String, val valueSelector: String) {
    BOOL(ClassId.topLevel(StandardNames.FqNames._boolean.toSafe()), "numberWithBool:", "boolValue"),
    CHAR(ClassId.topLevel(StandardNames.FqNames._byte.toSafe()), "numberWithChar:", "charValue"),
    SHORT(ClassId.topLevel(StandardNames.FqNames._short.toSafe()), "numberWithShort:", "shortValue"),
    INT(ClassId.topLevel(StandardNames.FqNames._int.toSafe()), "numberWithInt:", "intValue"),
    LONG_LONG(ClassId.topLevel(StandardNames.FqNames._long.toSafe()), "numberWithLongLong:", "longLongValue"),
    UNSIGNED_CHAR(StandardNames.FqNames.uByte, "numberWithUnsignedChar:", "unsignedCharValue"),
    UNSIGNED_SHORT(StandardNames.FqNames.uShort, "numberWithUnsignedShort:", "unsignedShortValue"),
    UNSIGNED_INT(StandardNames.FqNames.uInt, "numberWithUnsignedInt:", "unsignedIntValue"),
    UNSIGNED_LONG_LONG(StandardNames.FqNames.uLong, "numberWithUnsignedLongLong:", "unsignedLongLongValue"),
    FLOAT(ClassId.topLevel(StandardNames.FqNames._float.toSafe()), "numberWithFloat:", "floatValue"),
    DOUBLE(ClassId.topLevel(StandardNames.FqNames._double.toSafe()), "numberWithDouble:", "doubleValue"),
    INTEGER(null, "numberWithInteger:", "integerValue"),
    UNSIGNED_INTEGER(null, "numberWithUnsignedInteger:", "unsignedIntegerValue")
}

sealed class MethodBridgeParameter
sealed class MethodBridgeReceiver : MethodBridgeParameter() {
    object Static : MethodBridgeReceiver()
    object Factory : MethodBridgeReceiver()
    object Instance : MethodBridgeReceiver()
    companion object {
        val Init: MethodBridgeReceiver
            get() = Instance
    }
}
object MethodBridgeSelector : MethodBridgeParameter()
sealed class MethodBridgeValueParameter : MethodBridgeParameter() {
    data class Mapped(val bridge: TypeBridge) : MethodBridgeValueParameter()
    object ErrorOutParameter : MethodBridgeValueParameter()
    data class SuspendCompletion(val useUnitCompletion: Boolean) : MethodBridgeValueParameter()
}

data class MethodBridge(
        val returnBridge: ReturnValue,
        val receiver: MethodBridgeReceiver,
        val valueParameters: List<MethodBridgeValueParameter>
) {
    sealed class ReturnValue {
        object Void : ReturnValue()
        object HashCode : ReturnValue()
        data class Mapped(val bridge: TypeBridge) : ReturnValue()
        sealed class Instance : ReturnValue() {
            object InitResult : Instance()
            object FactoryResult : Instance()
        }
        sealed class WithError : ReturnValue() {
            object Success : WithError()
            data class ZeroForError(val successBridge: ReturnValue, val successMayBeZero: Boolean) : WithError()
        }
        object Suspend : ReturnValue()
    }

    val paramBridges: List<MethodBridgeParameter> = listOf(receiver, MethodBridgeSelector) + valueParameters

    val isInstance: Boolean
        get() = when (receiver) {
            MethodBridgeReceiver.Static, MethodBridgeReceiver.Factory -> false
            MethodBridgeReceiver.Instance -> true
        }

    val returnsError: Boolean
        get() = valueParameters.isNotEmpty() && valueParameters.last() is MethodBridgeValueParameter.ErrorOutParameter
}

interface ObjCExportProblemCollector {
    fun reportWarning(text: String)
    fun reportWarning(declaration: DeclarationDescriptor, text: String)
    fun reportError(text: String)
    fun reportError(declaration: DeclarationDescriptor, text: String)
    fun reportException(throwable: Throwable)

    companion object {
        val SILENT = object : ObjCExportProblemCollector {
            override fun reportWarning(text: String) {}
            override fun reportWarning(declaration: DeclarationDescriptor, text: String) {}
            override fun reportError(text: String) {}
            override fun reportError(declaration: DeclarationDescriptor, text: String) {}
            override fun reportException(throwable: Throwable) {
                throw throwable
            }
        }
    }
}

enum class ObjCExportNameCollisionMode {
    NONE,
    WARNING,
    ERROR
}

interface ObjCEntryPoints {
    companion object {
        val ALL: ObjCEntryPoints = object : ObjCEntryPoints {}
        fun create(@Suppress("UNUSED_PARAMETER") closure: Any): ObjCEntryPoints = ALL
    }
}

fun File.readObjCEntryPoints(): ObjCEntryPoints = ObjCEntryPoints.ALL

fun computeDownwardClosure(
        entryPoints: ObjCEntryPoints,
        @Suppress("UNUSED_PARAMETER") moduleDescriptors: List<ModuleDescriptor>
): ObjCEntryPoints = entryPoints

fun abbreviate(name: String): String {
    val normalized = name
            .replaceFirstChar { if (it.isLowerCase()) it.titlecaseChar() else it }
            .replace("[^A-Za-z0-9]".toRegex(), "_")
    val uppers = normalized.filter { it.isUpperCase() }
    return if (uppers.length >= 3) uppers else normalized
}

interface ObjCExportNamer {
    data class ClassOrProtocolName(
            val swiftName: String,
            val objCName: String,
            val binaryName: String = objCName
    )

    val topLevelNamePrefix: String
    val kotlinAnyName: ClassOrProtocolName
    val kotlinNumberName: ClassOrProtocolName
    val mutableSetName: ClassOrProtocolName
    val mutableMapName: ClassOrProtocolName
    fun numberBoxName(classId: ClassId): ClassOrProtocolName
    fun getFileClassName(file: SourceFile): ClassOrProtocolName
    fun getClassOrProtocolName(descriptor: ClassDescriptor): ClassOrProtocolName
    fun getSelector(method: FunctionDescriptor): String
    fun getObjectInstanceSelector(descriptor: ClassDescriptor): String
    fun getObjectPropertySelector(descriptor: ClassDescriptor): String
    fun getCompanionObjectPropertySelector(descriptor: ClassDescriptor): String
    fun getEnumEntrySelector(descriptor: ClassDescriptor): String
    fun getEnumStaticMemberSelector(descriptor: CallableMemberDescriptor): String
    fun getNSEnumTypeName(descriptor: ClassDescriptor): ClassOrProtocolName?

    companion object {
        const val kotlinThrowableAsErrorMethodName: String = "asError"
        const val nsEnumPropertyName: String = "nsEnum"
    }
}

class ObjCExportMapper(
        @Suppress("UNUSED_PARAMETER") val deprecationResolver: DeprecationResolver? = null,
        val local: Boolean = false,
        val unitSuspendFunctionExport: UnitSuspendFunctionObjCExport = UnitSuspendFunctionObjCExport.DEFAULT,
        val entryPoints: ObjCEntryPoints = ObjCEntryPoints.ALL
) {
    fun shouldBeExposed(descriptor: CallableMemberDescriptor): Boolean =
            descriptor.visibility.isPublicAPI

    fun shouldBeExposed(descriptor: ClassDescriptor): Boolean =
            descriptor.visibility.isPublicAPI

    fun isBaseMethod(descriptor: FunctionDescriptor): Boolean =
            descriptor.overriddenDescriptors.none { shouldBeExposed(it) }

    fun getBaseMethods(descriptor: FunctionDescriptor): List<FunctionDescriptor> =
            if (isBaseMethod(descriptor)) listOf(descriptor)
            else descriptor.overriddenDescriptors.filter { shouldBeExposed(it) }.flatMap { getBaseMethods(it.original) }.distinct()

    private fun bridgeType(kotlinType: org.jetbrains.kotlin.types.KotlinType): TypeBridge = when {
        KotlinBuiltIns.isBoolean(kotlinType) -> ValueTypeBridge(ObjCValueType.BOOL)
        KotlinBuiltIns.isChar(kotlinType) -> ValueTypeBridge(ObjCValueType.UNICHAR)
        KotlinBuiltIns.isByte(kotlinType) -> ValueTypeBridge(ObjCValueType.CHAR)
        KotlinBuiltIns.isShort(kotlinType) -> ValueTypeBridge(ObjCValueType.SHORT)
        KotlinBuiltIns.isInt(kotlinType) -> ValueTypeBridge(ObjCValueType.INT)
        KotlinBuiltIns.isLong(kotlinType) -> ValueTypeBridge(ObjCValueType.LONG_LONG)
        KotlinBuiltIns.isFloat(kotlinType) -> ValueTypeBridge(ObjCValueType.FLOAT)
        KotlinBuiltIns.isDouble(kotlinType) -> ValueTypeBridge(ObjCValueType.DOUBLE)
        KotlinBuiltIns.isUByte(kotlinType) -> ValueTypeBridge(ObjCValueType.UNSIGNED_CHAR)
        KotlinBuiltIns.isUShort(kotlinType) -> ValueTypeBridge(ObjCValueType.UNSIGNED_SHORT)
        KotlinBuiltIns.isUInt(kotlinType) -> ValueTypeBridge(ObjCValueType.UNSIGNED_INT)
        KotlinBuiltIns.isULong(kotlinType) -> ValueTypeBridge(ObjCValueType.UNSIGNED_LONG_LONG)
        else -> ReferenceBridge
    }

    fun bridgeMethod(descriptor: FunctionDescriptor): MethodBridge {
        val receiver = when {
            descriptor is ConstructorDescriptor -> MethodBridgeReceiver.Init
            descriptor.dispatchReceiverParameter != null || descriptor.extensionReceiverParameter != null -> MethodBridgeReceiver.Instance
            else -> MethodBridgeReceiver.Static
        }
        val actualReceiver = if (receiver == MethodBridgeReceiver.Init) MethodBridgeReceiver.Instance else receiver
        val valueParams = descriptor.valueParameters.map { MethodBridgeValueParameter.Mapped(bridgeType(it.type)) }
        val returnType = descriptor.returnType ?: descriptor.module.builtIns.unitType
        val returnBridge = when {
            descriptor is ConstructorDescriptor -> MethodBridge.ReturnValue.Instance.InitResult
            descriptor.isSuspend -> MethodBridge.ReturnValue.Suspend
            descriptor.containingDeclaration.let { it is ClassDescriptor && KotlinBuiltIns.isAny(it) } &&
                descriptor.name.asString() == "hashCode" -> MethodBridge.ReturnValue.HashCode
            KotlinBuiltIns.isUnit(returnType) || KotlinBuiltIns.isNothing(returnType) -> MethodBridge.ReturnValue.Void
            else -> MethodBridge.ReturnValue.Mapped(bridgeType(returnType))
        }
        return MethodBridge(returnBridge, actualReceiver, valueParams)
    }

    private val FunctionDescriptor.module: ModuleDescriptor
        get() {
            var cur: DeclarationDescriptor = this
            while (cur !is ModuleDescriptor) {
                cur = cur.containingDeclaration ?: error("No module for $this")
            }
            return cur
        }
}

class ObjCExportNamerImpl(
        val moduleDescriptors: Set<ModuleDescriptor>,
        val builtIns: KotlinBuiltIns,
        val mapper: ObjCExportMapper,
        val problemCollector: ObjCExportProblemCollector,
        override val topLevelNamePrefix: String,
        val local: Boolean,
        val objcGenerics: Boolean = false,
        val disableSwiftMemberNameMangling: Boolean = false,
        val ignoreInterfaceMethodCollisions: Boolean = false,
        val nameCollisionMode: ObjCExportNameCollisionMode = ObjCExportNameCollisionMode.NONE,
        val explicitMethodFamily: Boolean = false
) : ObjCExportNamer {
    override val kotlinAnyName = ObjCExportNamer.ClassOrProtocolName("KotlinBase", "${topLevelNamePrefix}Base")
    override val kotlinNumberName = ObjCExportNamer.ClassOrProtocolName("KotlinNumber", "${topLevelNamePrefix}Number")
    override val mutableSetName = ObjCExportNamer.ClassOrProtocolName("KotlinMutableSet", "${topLevelNamePrefix}MutableSet")
    override val mutableMapName = ObjCExportNamer.ClassOrProtocolName("KotlinMutableDictionary", "${topLevelNamePrefix}MutableDictionary")

    override fun numberBoxName(classId: ClassId): ObjCExportNamer.ClassOrProtocolName {
        val short = classId.shortClassName.asString()
        return ObjCExportNamer.ClassOrProtocolName("Kotlin$short", "$topLevelNamePrefix$short")
    }

    override fun getFileClassName(file: SourceFile): ObjCExportNamer.ClassOrProtocolName {
        val name = (file.name ?: "File").substringBeforeLast('.')
        return ObjCExportNamer.ClassOrProtocolName(name, "$topLevelNamePrefix$name")
    }

    override fun getClassOrProtocolName(descriptor: ClassDescriptor): ObjCExportNamer.ClassOrProtocolName {
        val name = descriptor.name.asString()
        return ObjCExportNamer.ClassOrProtocolName(name, "$topLevelNamePrefix$name")
    }

    override fun getSelector(method: FunctionDescriptor): String = when {
        method.containingDeclaration.let { it is ClassDescriptor && KotlinBuiltIns.isAny(it) } -> when (method.name.asString()) {
            "hashCode" -> "hash"
            "toString" -> "description"
            "equals" -> "isEqual:"
            else -> method.name.asString()
        }
        else -> method.name.asString()
    }
    override fun getObjectInstanceSelector(descriptor: ClassDescriptor): String = descriptor.name.asString().replaceFirstChar { it.lowercaseChar() }
    override fun getObjectPropertySelector(descriptor: ClassDescriptor): String = "shared"
    override fun getCompanionObjectPropertySelector(descriptor: ClassDescriptor): String = "companion"
    override fun getEnumEntrySelector(descriptor: ClassDescriptor): String = descriptor.name.asString().replaceFirstChar { it.lowercaseChar() }
    override fun getEnumStaticMemberSelector(descriptor: CallableMemberDescriptor): String = descriptor.name.asString()
    override fun getNSEnumTypeName(descriptor: ClassDescriptor): ObjCExportNamer.ClassOrProtocolName? = null
}

internal class ObjCExportHeaderGenerator private constructor(
        private val namer: ObjCExportNamer,
        private val mapper: ObjCExportMapper
) {
    fun translateModule() {}

    fun buildInterface(): ObjCExportedInterface = ObjCExportedInterface(
            emptySet(),
            emptyMap(),
            emptyMap(),
            emptyList(),
            namer,
            mapper
    )

    companion object {
        fun createInstance(
                @Suppress("UNUSED_PARAMETER") moduleDescriptors: List<ModuleDescriptor>,
                mapper: ObjCExportMapper,
                namer: ObjCExportNamer,
                @Suppress("UNUSED_PARAMETER") problemCollector: ObjCExportProblemCollector,
                @Suppress("UNUSED_PARAMETER") objcGenerics: Boolean,
                @Suppress("UNUSED_PARAMETER") objcExportBlockExplicitParameterNames: Boolean,
                @Suppress("UNUSED_PARAMETER") shouldExportKDoc: Boolean,
                @Suppress("UNUSED_PARAMETER") additionalImports: List<String>
        ): ObjCExportHeaderGenerator = ObjCExportHeaderGenerator(namer, mapper)
    }
}

internal class MainPackageGuesser {
    fun guess(
            @Suppress("UNUSED_PARAMETER") moduleDescriptor: ModuleDescriptor,
            @Suppress("UNUSED_PARAMETER") includedLibraries: List<ModuleDescriptor>,
            @Suppress("UNUSED_PARAMETER") exportedDependencies: List<ModuleDescriptor>
    ): FqName = FqName.ROOT
}

internal class ModuleMapBuilder {
    fun build(frameworkName: String, moduleDependencies: Set<String>): String = buildString {
        appendLine("framework module $frameworkName {")
        appendLine("    umbrella header \"$frameworkName.h\"")
        appendLine("    export *")
        appendLine("    module * { export * }")
        moduleDependencies.forEach { appendLine("    use $it") }
        appendLine("}")
    }
}

internal class ObjCHeaderWriter {
    fun write(headerFileName: String, headerLines: List<String>, headersDirectory: File) {
        headersDirectory.child(headerFileName).writeLines(headerLines)
    }
}

internal fun ClassDescriptor.needCompanionObjectProperty(
        @Suppress("UNUSED_PARAMETER") namer: ObjCExportNamer,
        mapper: ObjCExportMapper
): Boolean = companionObjectDescriptor?.let { mapper.shouldBeExposed(it) } == true

internal fun ClassDescriptor.getEnumValuesFunctionDescriptor(): SimpleFunctionDescriptor? =
        staticScope.getContributedFunctions(StandardNames.ENUM_VALUES, org.jetbrains.kotlin.incremental.components.NoLookupLocation.FROM_BACKEND).singleOrNull()

internal fun ClassDescriptor.getEnumEntriesPropertyDescriptor(): PropertyDescriptor? =
        staticScope.getContributedVariables(StandardNames.ENUM_ENTRIES, org.jetbrains.kotlin.incremental.components.NoLookupLocation.FROM_BACKEND).singleOrNull()

internal class ObjCExportedInterface(
        val generatedClasses: Set<ClassDescriptor>,
        val categoryMembers: Map<ClassDescriptor, List<CallableMemberDescriptor>>,
        val topLevel: Map<SourceFile, List<CallableMemberDescriptor>>,
        val headerLines: List<String>,
        val namer: ObjCExportNamer,
        val mapper: ObjCExportMapper
)

internal fun produceObjCExportInterface(
    context: NativeBackendPhaseContext,
    moduleDescriptor: ModuleDescriptor,
    frontendServices: FrontendServices,
): ObjCExportedInterface {
    val config = context.config
    require(config.target.family.isAppleFamily)
    require(config.produce == CompilerOutputKind.FRAMEWORK)

    val topLevelNamePrefix = context.objCExportTopLevelNamePrefix

    // TODO: emit RTTI to the same modules as classes belong to.
    //   Not possible yet, since ObjCExport translates the entire "world" API at once
    //   and can't do this per-module, e.g. due to global name conflict resolution.

    val unitSuspendFunctionExport = config.unitSuspendFunctionObjCExport
    val moduleDescriptors = listOf(moduleDescriptor) + moduleDescriptor.getExportedDependencies(config)
    val entryPoints = config.objcEntryPoints
    val expandEntryPoints = config.configuration.getBoolean(BinaryOptions.objcExportExpandEntryPoints)
    val effectiveEntryPoints = if (entryPoints != ObjCEntryPoints.ALL && expandEntryPoints) {
        ObjCEntryPoints.create(computeDownwardClosure(entryPoints, moduleDescriptors))
    } else {
        entryPoints
    }

    @OptIn(K1Deprecation::class)
    val mapper = ObjCExportMapper(
            frontendServices.deprecationResolver,
            unitSuspendFunctionExport = unitSuspendFunctionExport,
            entryPoints = effectiveEntryPoints)
    val objcGenerics = config.configuration.objcGenerics
    val disableSwiftMemberNameMangling = config.configuration.getBoolean(BinaryOptions.objcExportDisableSwiftMemberNameMangling)
    val ignoreInterfaceMethodCollisions = config.configuration.getBoolean(BinaryOptions.objcExportIgnoreInterfaceMethodCollisions)
    val reportNameCollisions = config.configuration.getBoolean(BinaryOptions.objcExportReportNameCollisions)
    val errorOnNameCollisions = config.configuration.getBoolean(BinaryOptions.objcExportErrorOnNameCollisions)
    val explicitMethodFamily = config.configuration.getBoolean(BinaryOptions.objcExportExplicitMethodFamily)
    val objcExportBlockExplicitParameterNames = config.configuration.get(BinaryOptions.objcExportBlockExplicitParameterNames, false)

    val problemCollector = ObjCExportCompilerProblemCollector(context)

    val namer = ObjCExportNamerImpl(
            moduleDescriptors.toSet(),
            moduleDescriptor.builtIns,
            mapper,
            problemCollector,
            topLevelNamePrefix,
            local = false,
            objcGenerics = objcGenerics,
            disableSwiftMemberNameMangling = disableSwiftMemberNameMangling,
            ignoreInterfaceMethodCollisions = ignoreInterfaceMethodCollisions,
            nameCollisionMode = when {
                errorOnNameCollisions -> ObjCExportNameCollisionMode.ERROR
                reportNameCollisions -> ObjCExportNameCollisionMode.WARNING
                else -> ObjCExportNameCollisionMode.NONE
            },
            explicitMethodFamily = explicitMethodFamily,
    )
    val shouldExportKDoc = context.shouldExportKDoc()
    val additionalImports = context.config.configuration.getNotNull(NativeConfigurationKeys.FRAMEWORK_IMPORT_HEADERS)
    val headerGenerator = ObjCExportHeaderGenerator.createInstance(
            moduleDescriptors, mapper, namer, problemCollector, objcGenerics, objcExportBlockExplicitParameterNames, shouldExportKDoc = shouldExportKDoc,
            additionalImports = additionalImports)
    headerGenerator.translateModule()
    return headerGenerator.buildInterface()
}

private class ObjCExportCompilerProblemCollector(val context: NativeBackendPhaseContext) : ObjCExportProblemCollector {
    private val DeclarationDescriptor.psiLocation: CompilerMessageLocation?
        get() = null

    override fun reportWarning(text: String) {
        context.diagnosticReporter.report(NativeBackendDiagnostics.OBJC_EXPORT_WARNING, text)
    }

    override fun reportWarning(declaration: DeclarationDescriptor, text: String) {
        val location = declaration.psiLocation ?: return reportWarning(
                "$text\n    (at ${DescriptorRenderer.COMPACT_WITH_SHORT_TYPES.render(declaration)})"
        )

        context.diagnosticReporter.report(NativeBackendDiagnostics.OBJC_EXPORT_WARNING, text, location)
    }

    override fun reportError(text: String) {
        context.diagnosticReporter.report(NativeBackendDiagnostics.NATIVE_BACKEND_ERROR, text, null)
    }

    override fun reportError(declaration: DeclarationDescriptor, text: String) {
        val location = declaration.psiLocation ?: return reportError(
                "$text\n    (at ${DescriptorRenderer.COMPACT_WITH_SHORT_TYPES.render(declaration)})"
        )

        context.diagnosticReporter.report(NativeBackendDiagnostics.NATIVE_BACKEND_ERROR, text, location)
    }

    override fun reportException(throwable: Throwable) {
        throw throwable
    }
}

/**
 * Populate framework directory with headers, module and info.plist.
 */
internal fun createObjCFramework(
        config: NativeSecondStageCompilationConfig,
        moduleDescriptor: ModuleDescriptor,
        exportedInterface: ObjCExportedInterface,
        frameworkDirectory: File
) {
    val frameworkName = frameworkDirectory.name.removeSuffix(CompilerOutputKind.FRAMEWORK.suffix())
    val frameworkBuilder = FrameworkBuilder(
        config,
        infoPListBuilder = InfoPListBuilder(config),
        moduleMapBuilder = ModuleMapBuilder(),
        objCHeaderWriter = ObjCHeaderWriter(),
        mainPackageGuesser = MainPackageGuesser(),
    )
    frameworkBuilder.build(
            moduleDescriptor,
            frameworkDirectory,
            frameworkName,
            exportedInterface.headerLines,
            moduleDependencies = setOf("Foundation")
    )
}

internal fun createTestBundle(
        config: NativeSecondStageCompilationConfig,
        moduleDescriptor: ModuleDescriptor,
        bundleDirectory: File
) {
    val name = bundleDirectory.name.removeSuffix(CompilerOutputKind.TEST_BUNDLE.suffix())
    BundleBuilder(
            config = config,
            infoPListBuilder = InfoPListBuilder(config, BundleType.XCTEST),
            mainPackageGuesser = MainPackageGuesser()
    ).build(moduleDescriptor, bundleDirectory, name)
}

// TODO: No need for such class in dynamic driver.
internal class ObjCExport(
    private val generationState: NativeGenerationState,
    private val moduleDescriptor: ModuleDescriptor,
    private val exportedInterface: ObjCExportedInterface?,
    private val codeSpec: ObjCExportCodeSpec?
) {
    private val config = generationState.config
    private val target get() = config.target
    private val topLevelNamePrefix get() = generationState.objCExportTopLevelNamePrefix

    lateinit var namer: ObjCExportNamer

    internal fun generate(codegen: CodeGenerator) {
        if (!target.family.isAppleFamily) return

        if (generationState.shouldDefineFunctionClasses) {
            ObjCExportBlockCodeGenerator(codegen).generate()
        }

        if (!config.isFinalBinary) return // TODO: emit RTTI to the same modules as classes belong to.

        @OptIn(K1Deprecation::class)
        val mapper = exportedInterface?.mapper ?: ObjCExportMapper(unitSuspendFunctionExport = config.unitSuspendFunctionObjCExport)
        namer = exportedInterface?.namer ?: ObjCExportNamerImpl(
                setOf(moduleDescriptor),
                moduleDescriptor.builtIns,
                mapper,
                ObjCExportProblemCollector.SILENT,
                topLevelNamePrefix,
                local = false
        )

        val objCCodeGenerator = ObjCExportCodeGenerator(codegen, namer, mapper)

        exportedInterface?.generateWorkaroundForSwiftSR10177(generationState)

        objCCodeGenerator.generate(codeSpec)
        objCCodeGenerator.dispose()
    }
}

// See https://bugs.swift.org/browse/SR-10177
private fun ObjCExportedInterface.generateWorkaroundForSwiftSR10177(generationState: NativeGenerationState) {
    // Code for all protocols from the header should get into the binary.
    // Objective-C protocols ABI is complicated (consider e.g. undocumented extended type encoding),
    // so the easiest way to achieve this (quickly) is to compile a stub by clang.

    val protocolsStub = listOf(
            "__attribute__((used)) static void __workaroundSwiftSR10177() {",
            buildString {
                append("    ")
                generatedClasses.forEach {
                    if (it.isInterface) {
                        val protocolName = namer.getClassOrProtocolName(it).objCName
                        append("@protocol($protocolName); ")
                    }
                }
            },
            "}"
    )

    val source = createTempFile("protocols", ".m").deleteOnExit()
    source.writeLines(headerLines + protocolsStub)

    val bitcode = createTempFile("protocols", ".bc").deleteOnExit()

    val clangCommand = generationState.config.clang.clangC(
            source.absolutePath,
            "-O2",
            "-emit-llvm",
            "-c", "-o", bitcode.absolutePath
    )

    val result = Command(clangCommand).getResult(withErrors = true)

    if (result.exitCode == 0) {
        generationState.llvm.additionalProducedBitcodeFiles += bitcode.absolutePath
    } else {
        // Note: ignoring compile errors intentionally.
        // In this case resulting framework will likely be unusable due to compile errors when importing it.
    }
}

internal val NativeBackendPhaseContext.objCExportTopLevelNamePrefix: String
    get() = abbreviate(config.fullExportedNamePrefix)


