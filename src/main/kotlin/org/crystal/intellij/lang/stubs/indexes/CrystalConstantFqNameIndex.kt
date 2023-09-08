package org.crystal.intellij.lang.stubs.indexes

import org.crystal.intellij.lang.psi.CrConstantSource

class CrystalConstantFqNameIndex : CrystalStringStubIndexExtensionBase<CrConstantSource>() {
    companion object Helper: HelperBase<CrConstantSource>(
        CrystalConstantFqNameIndex::class.java,
        CrConstantSource::class.java
    )

    override val helper
        get() = Helper
}