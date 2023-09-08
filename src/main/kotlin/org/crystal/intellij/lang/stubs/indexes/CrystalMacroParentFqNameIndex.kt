package org.crystal.intellij.lang.stubs.indexes

import org.crystal.intellij.lang.psi.CrMacro

class CrystalMacroParentFqNameIndex : CrystalStringStubIndexExtensionBase<CrMacro>() {
    companion object Helper: HelperBase<CrMacro>(
        CrystalMacroParentFqNameIndex::class.java,
        CrMacro::class.java
    )

    override val helper
        get() = Helper
}