package org.crystal.intellij.stubs.indexes

import org.crystal.intellij.psi.CrFunctionLikeDefinition

class CrystalFunctionShortNameIndex : CrystalStringStubIndexExtensionBase<CrFunctionLikeDefinition>() {
    companion object Helper: HelperBase<CrFunctionLikeDefinition>(
        CrystalFunctionShortNameIndex::class.java,
        CrFunctionLikeDefinition::class.java
    )

    override val helper
        get() = Helper
}