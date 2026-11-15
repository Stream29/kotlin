/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.checkers

import org.jetbrains.kotlin.fir.analysis.checkers.*
import org.jetbrains.kotlin.fir.analysis.diagnostics.CliFrontendDiagnostics
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirErrors
import org.jetbrains.kotlin.fir.analysis.diagnostics.native.FirNativeErrors
import org.jetbrains.kotlin.fir.analysis.native.checkers.NativeDeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.native.checkers.NativeDeclarationExtraCheckers
import org.jetbrains.kotlin.fir.analysis.native.checkers.NativeExpressionCheckers
import org.jetbrains.kotlin.fir.analysis.native.checkers.NativeTypeCheckers
import org.jetbrains.kotlin.fir.builder.FirSyntaxErrors
import org.jetbrains.kotlin.fir.session.FirSessionConfigurator

fun FirSessionConfigurator.registerCommonCheckers() {
    useCheckers(CommonDeclarationCheckers)
    useCheckers(CommonExpressionCheckers)
    useCheckers(CommonTypeCheckers)
    useCheckers(CommonLanguageVersionSettingsCheckers)
    registerDiagnosticContainers(FirErrors, FirSyntaxErrors, CliFrontendDiagnostics)
}

fun FirSessionConfigurator.registerExtraCommonCheckers() {
    useCheckers(ExtraExpressionCheckers)
    useCheckers(ExtraDeclarationCheckers)
    useCheckers(ExtraLanguageVersionSettingsCheckers)
    registerDiagnosticContainers(FirErrors)
}

fun FirSessionConfigurator.registerExperimentalCheckers() {
    useCheckers(ExperimentalExpressionCheckers)
    useCheckers(ExperimentalTypeCheckers)
    useCheckers(ExperimentalLanguageVersionSettingsCheckers)
    registerDiagnosticContainers(FirErrors)
}

fun FirSessionConfigurator.registerNativeCheckers() {
    useCheckers(NativeDeclarationCheckers)
    useCheckers(NativeExpressionCheckers)
    useCheckers(NativeTypeCheckers)
    registerDiagnosticContainers(FirNativeErrors)
}

fun FirSessionConfigurator.registerExtraNativeCheckers() {
    useCheckers(NativeDeclarationExtraCheckers)
    registerDiagnosticContainers(FirNativeErrors)
}
