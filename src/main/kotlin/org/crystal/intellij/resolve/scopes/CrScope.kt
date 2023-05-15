package org.crystal.intellij.resolve.scopes

import org.crystal.intellij.resolve.CrCall
import org.crystal.intellij.resolve.symbols.CrConstantLikeSym
import org.crystal.intellij.resolve.symbols.CrMacroSignature
import org.crystal.intellij.resolve.symbols.CrMacroSym

interface CrScope {
    fun getAllConstants(isRoot: Boolean = false): Sequence<CrConstantLikeSym<*>> = emptySequence()

    fun getConstant(name: String, isRoot: Boolean = false): CrConstantLikeSym<*>? = null

    fun getAllMacros(signature: CrMacroSignature): List<CrMacroSym> = emptyList()

    fun lookupMacroCall(call: CrCall): CrResolvedMacroCall? = null
}