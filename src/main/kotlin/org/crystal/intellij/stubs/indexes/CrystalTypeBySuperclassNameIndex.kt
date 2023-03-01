package org.crystal.intellij.stubs.indexes

import org.crystal.intellij.psi.CrConstantSource

class CrystalTypeBySuperclassNameIndex : CrystalStringStubIndexExtensionBase<CrConstantSource>() {
    companion object Helper: HelperBase<CrConstantSource>(
        CrystalTypeBySuperclassNameIndex::class.java,
        CrConstantSource::class.java
    )

    override val helper
        get() = Helper
}