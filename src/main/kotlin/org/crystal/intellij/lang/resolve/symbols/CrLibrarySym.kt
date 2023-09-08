package org.crystal.intellij.lang.resolve.symbols

import org.crystal.intellij.lang.psi.CrConstantSource

class CrLibrarySym(
    name: String,
    override val namespace: CrModuleLikeSym,
    sources: List<CrConstantSource>,
    override val program: CrProgramSym
) : CrModuleLikeSym(name, sources) {
    override val metaclass: CrModuleLikeSym
        get() = this
}