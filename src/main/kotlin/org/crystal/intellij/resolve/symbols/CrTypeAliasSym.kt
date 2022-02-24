package org.crystal.intellij.resolve.symbols

import org.crystal.intellij.psi.CrTypeSource

class CrTypeAliasSym(
    name: String,
    override val namespace: CrModuleLikeSym,
    sources: List<CrTypeSource>,
    override val program: CrProgramSym
) : CrTypeAliasLikeSym(name, sources)