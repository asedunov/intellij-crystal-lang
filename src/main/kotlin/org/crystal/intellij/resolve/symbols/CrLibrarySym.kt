package org.crystal.intellij.resolve.symbols

import org.crystal.intellij.psi.CrConstantSource

class CrLibrarySym(
    name: String,
    override val namespace: CrModuleLikeSym,
    sources: List<CrConstantSource>,
    override val program: CrProgramSym
) : CrModuleLikeSym(name, sources) {
    override val metaclass: CrModuleLikeSym
        get() = this
}