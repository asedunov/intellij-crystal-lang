package org.crystal.intellij.resolve.symbols

import org.crystal.intellij.psi.CrConstantSource

class CrStructSym(
    name: String,
    override val namespace: CrModuleLikeSym,
    sources: List<CrConstantSource>,
    override val program: CrProgramSym
) : CrClassLikeSym(name, sources)