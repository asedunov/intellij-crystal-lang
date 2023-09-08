package org.crystal.intellij.lang.stubs.indexes

import org.crystal.intellij.lang.psi.CrTypeDefinition

class CrystalTypeShortNameIndex : CrystalStringStubIndexExtensionBase<CrTypeDefinition>() {
    companion object Helper: HelperBase<CrTypeDefinition>(
        CrystalTypeShortNameIndex::class.java,
        CrTypeDefinition::class.java
    )

    override val helper
        get() = Helper
}