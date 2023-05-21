package org.crystal.intellij.stubs.indexes

import org.crystal.intellij.psi.CrMacro

class CrystalMacroParentFqNameIndex : CrystalStringStubIndexExtensionBase<CrMacro>() {
    companion object Helper: HelperBase<CrMacro>(
        CrystalMacroParentFqNameIndex::class.java,
        CrMacro::class.java
    )

    override val helper
        get() = Helper
}