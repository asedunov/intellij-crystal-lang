package org.crystal.intellij.resolve.symbols

import org.crystal.intellij.psi.CrSymbolOrdinalHolder

sealed class CrOrdinalSym<Source : CrSymbolOrdinalHolder>(
    name: String,
    sources: List<Source>
) : CrSym<Source>(name, sources) {
    open val ordinal: CrSymbolOrdinal?
        get() = sources.firstOrNull()?.ordinal()
}