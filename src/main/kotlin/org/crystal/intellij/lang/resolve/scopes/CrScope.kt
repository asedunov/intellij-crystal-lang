package org.crystal.intellij.lang.resolve.scopes

import org.crystal.intellij.lang.resolve.CrCall
import org.crystal.intellij.lang.resolve.symbols.CrConstantLikeSym
import org.crystal.intellij.lang.resolve.symbols.CrMacroId
import org.crystal.intellij.lang.resolve.symbols.CrMacroSym

interface CrScope {
    fun getAllConstants(isRoot: Boolean = false): Sequence<CrConstantLikeSym<*>> = emptySequence()

    fun getConstant(name: String, isRoot: Boolean = false): CrConstantLikeSym<*>? = null

    fun getAllMacros(): Sequence<CrMacroSym> = emptySequence()

    fun getAllMacros(id: CrMacroId): List<CrMacroSym> = emptyList()

    fun lookupMacroCall(call: CrCall): CrResolvedMacroCall? = null
}