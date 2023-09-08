package org.crystal.intellij.lang.resolve.symbols

import org.crystal.intellij.lang.psi.CrConstantSource

sealed class CrTypeAliasLikeSym(
    name: String,
    sources: List<CrConstantSource>
) : CrProperTypeSym(name, sources)