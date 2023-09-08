package org.crystal.intellij.lang.resolve.symbols

import org.crystal.intellij.lang.psi.CrConstantSource

class CrTypeAliasSym(
    name: String,
    override val namespace: CrModuleLikeSym,
    sources: List<CrConstantSource>,
    override val program: CrProgramSym
) : CrTypeAliasLikeSym(name, sources)