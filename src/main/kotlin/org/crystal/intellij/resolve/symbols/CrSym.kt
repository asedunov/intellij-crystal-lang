@file:Suppress("UnstableApiUsage")

package org.crystal.intellij.resolve.symbols

import com.intellij.model.Pointer
import com.intellij.model.Symbol
import org.crystal.intellij.psi.CrSymbolOrdinalHolder

sealed class CrSym<Source: CrSymbolOrdinalHolder>(
    val name: String,
    val sources: List<Source>
) : Symbol, Pointer<Symbol> {
    override fun createPointer() = this

    override fun dereference() = this

    val isValid: Boolean
        get() = sources.all { it.isValid }

    val ordinal: CrSymbolOrdinal?
        get() = sources.firstOrNull()?.ordinal()

    abstract val program: CrProgramSym

    abstract val namespace: CrModuleLikeSym
}