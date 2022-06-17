package org.crystal.intellij.resolve.symbols

import org.crystal.intellij.psi.CrConstantSource

sealed class CrTypeAliasLikeSym(
    name: String,
    sources: List<CrConstantSource>
) : CrTypeSym(name, sources)