package org.crystal.intellij.resolve.symbols

import org.crystal.intellij.psi.CrSymbolOrdinalHolder

sealed class CrTypeSym<Source : CrSymbolOrdinalHolder>(
    name: String,
    sources: List<Source>
) : CrConstantLikeSym<Source>(name, sources)