package org.crystal.intellij.resolve.symbols

import org.crystal.intellij.psi.CrConstantSource

@Suppress("UnstableApiUsage")
class CrConstantSym(
    name: String,
    sources: List<CrConstantSource>,
    override val namespace: CrModuleLikeSym
) : CrOrdinalSym<CrConstantSource>(name, sources) {
    override val program: CrProgramSym
        get() = namespace.program

    override fun equals(other: Any?): Boolean {
        return other is CrConstantSym
                && namespace == other.namespace
                && name == other.name
    }

    override fun hashCode(): Int {
        var result = namespace.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}