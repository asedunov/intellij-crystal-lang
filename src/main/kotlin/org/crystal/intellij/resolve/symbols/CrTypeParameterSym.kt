package org.crystal.intellij.resolve.symbols

import org.crystal.intellij.psi.CrTypeParameter

@Suppress("UnstableApiUsage")
class CrTypeParameterSym(
    name: String,
    sources: List<CrTypeParameter>,
    override val namespace: CrSym<*>
) : CrTypeSym<CrTypeParameter>(name, sources) {
    override val program: CrProgramSym
        get() = namespace.program

    override fun equals(other: Any?): Boolean {
        return other is CrTypeParameterSym
                && namespace == other.namespace
                && name == other.name
    }

    override fun hashCode(): Int {
        var result = namespace.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}