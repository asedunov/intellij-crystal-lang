package org.crystal.intellij.resolve.symbols

import org.crystal.intellij.psi.CrSimpleParameter

class CrMacroParameterSym(
    source: CrSimpleParameter,
    val macro: CrMacroSym
) : CrSym<CrSimpleParameter>(source.name ?: "", listOf(source)) {
    override val program: CrProgramSym
        get() = macro.program

    override val namespace: CrSym<*>
        get() = macro

    val source: CrSimpleParameter
        get() = sources.single()

    val hasDefaultValue: Boolean
        get() = source.hasInitializer

    val externalName: String
        get() = source.externalName ?: name
}