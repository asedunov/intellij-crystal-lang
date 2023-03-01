package org.crystal.intellij.stubs.indexes

import org.crystal.intellij.psi.CrConstantSource

class CrystalConstantFqNameIndex : CrystalStringStubIndexExtensionBase<CrConstantSource>() {
    companion object Helper: HelperBase<CrConstantSource>(
        CrystalConstantFqNameIndex::class.java,
        CrConstantSource::class.java
    )

    override val helper
        get() = Helper
}