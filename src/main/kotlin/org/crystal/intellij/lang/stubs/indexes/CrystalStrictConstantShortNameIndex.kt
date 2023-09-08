package org.crystal.intellij.lang.stubs.indexes

import org.crystal.intellij.lang.psi.CrDefinitionWithFqName

class CrystalStrictConstantShortNameIndex : CrystalStringStubIndexExtensionBase<CrDefinitionWithFqName>() {
    companion object Helper: HelperBase<CrDefinitionWithFqName>(
        CrystalStrictConstantShortNameIndex::class.java,
        CrDefinitionWithFqName::class.java
    )

    override val helper
        get() = Helper
}