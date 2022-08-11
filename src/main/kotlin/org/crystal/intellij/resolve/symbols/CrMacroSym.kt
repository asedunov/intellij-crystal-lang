package org.crystal.intellij.resolve.symbols

import org.crystal.intellij.psi.CrMacro

@Suppress("UnstableApiUsage")
class CrMacroSym(
    name: String,
    override val namespace: CrModuleLikeSym,
    source: CrMacro
) : CrSym<CrMacro>(name, listOf(source)) {
    override val program: CrProgramSym
        get() = namespace.program

    val source: CrMacro
        get() = sources.single()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CrMacroSym) return false

        if (namespace != other.namespace) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = namespace.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}