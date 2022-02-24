package org.crystal.intellij.resolve.symbols

import org.crystal.intellij.psi.CrTypeSource

class CrModuleSym(
    name: String,
    override val namespace: CrModuleLikeSym,
    sources: List<CrTypeSource>,
    override val program: CrProgramSym
) : CrModuleLikeSym(name, sources)