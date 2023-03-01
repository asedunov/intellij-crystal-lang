package org.crystal.intellij.stubs.indexes

import org.crystal.intellij.psi.CrTypeDefinition

class CrystalTypeShortNameIndex : CrystalStringStubIndexExtensionBase<CrTypeDefinition>() {
    companion object Helper: HelperBase<CrTypeDefinition>(
        CrystalTypeShortNameIndex::class.java,
        CrTypeDefinition::class.java
    )

    override val helper
        get() = Helper
}