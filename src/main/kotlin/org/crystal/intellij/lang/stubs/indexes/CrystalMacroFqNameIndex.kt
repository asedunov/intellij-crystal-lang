package org.crystal.intellij.lang.stubs.indexes

import org.crystal.intellij.lang.psi.CrMacro

class CrystalMacroFqNameIndex : CrystalStringStubIndexExtensionBase<CrMacro>() {
    companion object Helper: HelperBase<CrMacro>(
        CrystalMacroFqNameIndex::class.java,
        CrMacro::class.java
    )

    override val helper
        get() = Helper
}