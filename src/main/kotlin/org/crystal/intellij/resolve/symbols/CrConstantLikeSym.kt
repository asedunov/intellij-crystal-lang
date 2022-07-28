package org.crystal.intellij.resolve.symbols

import org.crystal.intellij.psi.CrSymbolOrdinalHolder
import org.crystal.intellij.psi.CrTypeDefinition
import org.crystal.intellij.psi.CrVisibility
import org.crystal.intellij.resolve.StableFqName

sealed class CrConstantLikeSym<Source : CrSymbolOrdinalHolder>(
    name: String,
    sources: List<Source>
) : CrOrdinalSym<Source>(name, sources) {
    val visibility: CrVisibility? by lazy {
        val typeDef = sources.firstOrNull { it is CrTypeDefinition } as? CrTypeDefinition
        typeDef?.visibility ?: CrVisibility.PUBLIC
    }

    open val fqName: StableFqName? by lazy {
        val namespace = namespace as? CrModuleLikeSym
        if (namespace != null) StableFqName(name, namespace.fqName) else null
    }
}