package org.crystal.intellij.lang.resolve.symbols

import org.crystal.intellij.lang.psi.CrSimpleParameter

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