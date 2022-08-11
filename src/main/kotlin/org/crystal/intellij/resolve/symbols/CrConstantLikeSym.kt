package org.crystal.intellij.resolve.symbols

import org.crystal.intellij.psi.CrSymbolOrdinalHolder
import org.crystal.intellij.resolve.StableFqName

sealed class CrConstantLikeSym<Source : CrSymbolOrdinalHolder>(
    name: String,
    sources: List<Source>
) : CrOrdinalSym<Source>(name, sources) {
    open val fqName: StableFqName? by lazy {
        val namespace = namespace as? CrModuleLikeSym
        if (namespace != null) StableFqName(name, namespace.fqName) else null
    }
}