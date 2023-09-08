package org.crystal.intellij.lang.stubs.indexes

import org.crystal.intellij.lang.psi.CrDefinitionWithFqName

class CrystalVariableShortNameIndex : CrystalStringStubIndexExtensionBase<CrDefinitionWithFqName>() {
    companion object Helper: HelperBase<CrDefinitionWithFqName>(
        CrystalVariableShortNameIndex::class.java,
        CrDefinitionWithFqName::class.java
    )

    override val helper
        get() = Helper
}