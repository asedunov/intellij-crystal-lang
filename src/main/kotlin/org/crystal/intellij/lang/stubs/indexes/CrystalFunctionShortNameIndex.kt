package org.crystal.intellij.lang.stubs.indexes

import org.crystal.intellij.lang.psi.CrFunctionLikeDefinition

class CrystalFunctionShortNameIndex : CrystalStringStubIndexExtensionBase<CrFunctionLikeDefinition>() {
    companion object Helper: HelperBase<CrFunctionLikeDefinition>(
        CrystalFunctionShortNameIndex::class.java,
        CrFunctionLikeDefinition::class.java
    )

    override val helper
        get() = Helper
}