package org.crystal.intellij.stubs.indexes

import org.crystal.intellij.psi.CrDefinitionWithFqName

class CrystalVariableShortNameIndex : CrystalStringStubIndexExtensionBase<CrDefinitionWithFqName>() {
    companion object Helper: HelperBase<CrDefinitionWithFqName>(
        CrystalVariableShortNameIndex::class.java,
        CrDefinitionWithFqName::class.java
    )

    override val helper
        get() = Helper
}