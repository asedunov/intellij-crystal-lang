package org.crystal.intellij.stubs.indexes

import org.crystal.intellij.psi.CrMacro

class CrystalMacroFqNameIndex : CrystalStringStubIndexExtensionBase<CrMacro>() {
    companion object Helper: HelperBase<CrMacro>(
        CrystalMacroFqNameIndex::class.java,
        CrMacro::class.java
    )

    override val helper
        get() = Helper
}