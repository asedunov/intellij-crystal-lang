package org.crystal.intellij.stubs.indexes

import org.crystal.intellij.psi.CrDefinitionWithFqName

class CrystalStrictConstantShortNameIndex : CrystalStringStubIndexExtensionBase<CrDefinitionWithFqName>() {
    companion object Helper: HelperBase<CrDefinitionWithFqName>(
        CrystalStrictConstantShortNameIndex::class.java,
        CrDefinitionWithFqName::class.java
    )

    override val helper
        get() = Helper
}