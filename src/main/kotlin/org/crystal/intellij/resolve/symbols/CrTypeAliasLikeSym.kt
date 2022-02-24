package org.crystal.intellij.resolve.symbols

import org.crystal.intellij.psi.CrTypeSource

sealed class CrTypeAliasLikeSym(
    name: String,
    sources: List<CrTypeSource>
) : CrTypeSym(name, sources)