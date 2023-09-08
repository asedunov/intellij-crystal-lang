package org.crystal.intellij.lang.stubs.indexes

import org.crystal.intellij.lang.psi.CrConstantSource

class CrystalTypeBySuperclassNameIndex : CrystalStringStubIndexExtensionBase<CrConstantSource>() {
    companion object Helper: HelperBase<CrConstantSource>(
        CrystalTypeBySuperclassNameIndex::class.java,
        CrConstantSource::class.java
    )

    override val helper
        get() = Helper
}