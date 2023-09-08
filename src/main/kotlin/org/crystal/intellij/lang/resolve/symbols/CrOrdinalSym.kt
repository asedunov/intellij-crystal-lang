package org.crystal.intellij.lang.resolve.symbols

import org.crystal.intellij.lang.psi.CrSymbolOrdinalHolder

sealed class CrOrdinalSym<Source : CrSymbolOrdinalHolder>(
    name: String,
    sources: List<Source>
) : CrSym<Source>(name, sources) {
    open val ordinal: CrSymbolOrdinal?
        get() = sources.firstOrNull()?.ordinal()
}