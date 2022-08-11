package org.crystal.intellij.resolve.symbols

import org.crystal.intellij.psi.CrSymbolOrdinalHolder
import org.crystal.intellij.resolve.CrStdFqNames
import org.crystal.intellij.resolve.scopes.CrModuleLikeScope
import org.crystal.intellij.resolve.scopes.getTypeAs

sealed class CrTypeSym<Source : CrSymbolOrdinalHolder>(
    name: String,
    sources: List<Source>
) : CrConstantLikeSym<Source>(name, sources) {
    open val metaclass: CrModuleLikeSym by lazy {
        if (fqName != CrStdFqNames.OBJECT) {
            CrMetaclassSym(this)
        } else {
            program.memberScope.getTypeAs(CrStdFqNames.CLASS)!!
        }
    }

    open val parents: CrModuleLikeScope.ParentList?
        get() = null
}