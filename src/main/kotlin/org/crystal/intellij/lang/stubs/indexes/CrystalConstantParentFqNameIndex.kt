package org.crystal.intellij.lang.stubs.indexes

import org.crystal.intellij.lang.psi.CrConstantSource

class CrystalConstantParentFqNameIndex : CrystalStringStubIndexExtensionBase<CrConstantSource>() {
    companion object Helper: HelperBase<CrConstantSource>(
        CrystalConstantParentFqNameIndex::class.java,
        CrConstantSource::class.java
    )

    override val helper
        get() = Helper
}