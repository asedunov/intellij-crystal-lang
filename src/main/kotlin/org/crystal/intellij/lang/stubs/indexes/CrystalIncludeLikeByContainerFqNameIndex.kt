package org.crystal.intellij.lang.stubs.indexes

import org.crystal.intellij.lang.psi.CrIncludeLikeExpression

class CrystalIncludeLikeByContainerFqNameIndex : CrystalStringStubIndexExtensionBase<CrIncludeLikeExpression>() {
    companion object Helper: HelperBase<CrIncludeLikeExpression>(
        CrystalIncludeLikeByContainerFqNameIndex::class.java,
        CrIncludeLikeExpression::class.java
    )

    override val helper
        get() = Helper
}