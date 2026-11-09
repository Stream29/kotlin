/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.session

import org.jetbrains.kotlin.library.components.KlibMetadataComponent

class KlibIcData(
    private val fragments: Map<String, Map<String, ByteArray>> = emptyMap(),
) : KlibMetadataComponent {

    val packageFragmentNameList: Collection<String>
        get() = fragments.keys

    override val moduleHeaderData: Nothing
        get() = error("moduleHeaderData is not implemented")

    override fun getPackageFragmentNames(packageFqName: String): Set<String> {
        return fragments[packageFqName]?.keys ?: emptySet()
    }

    override fun getPackageFragment(packageFqName: String, fragmentName: String): ByteArray {
        return fragments[packageFqName]?.get(fragmentName) ?: error("Metadata not found for package $packageFqName part $fragmentName")
    }
}
