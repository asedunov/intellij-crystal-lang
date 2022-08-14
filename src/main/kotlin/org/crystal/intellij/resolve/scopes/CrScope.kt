package org.crystal.intellij.resolve.scopes

import org.crystal.intellij.resolve.CrCall
import org.crystal.intellij.resolve.symbols.CrMacroSignature
import org.crystal.intellij.resolve.symbols.CrMacroSym
import org.crystal.intellij.resolve.symbols.CrSym

interface CrScope {
    fun getConstant(name: String, isRoot: Boolean = false): CrSym<*>? = null

    fun getAllMacros(signature: CrMacroSignature): List<CrMacroSym> = emptyList()

    fun getMacro(call: CrCall): CrMacroSym? = null
}