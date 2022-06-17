package org.crystal.intellij.resolve.symbols

import org.crystal.intellij.psi.CrConstantSource

class CrCUnionSym(
    name: String,
    override val namespace: CrModuleLikeSym,
    sources: List<CrConstantSource>,
    override val program: CrProgramSym
) : CrClassLikeSym(name, sources) {
    override val isAbstract: Boolean
        get() = false
}