package org.crystal.intellij.lang.stubs.indexes

import org.crystal.intellij.lang.psi.CrMacro

class CrystalMacroSignatureIndex : CrystalStringStubIndexExtensionBase<CrMacro>() {
    companion object Helper: HelperBase<CrMacro>(
        CrystalMacroSignatureIndex::class.java,
        CrMacro::class.java
    )

    override val helper
        get() = Helper
}