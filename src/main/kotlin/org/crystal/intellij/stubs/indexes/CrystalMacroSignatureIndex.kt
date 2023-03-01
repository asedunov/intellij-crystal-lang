package org.crystal.intellij.stubs.indexes

import org.crystal.intellij.psi.CrMacro

class CrystalMacroSignatureIndex : CrystalStringStubIndexExtensionBase<CrMacro>() {
    companion object Helper: HelperBase<CrMacro>(
        CrystalMacroSignatureIndex::class.java,
        CrMacro::class.java
    )

    override val helper
        get() = Helper
}