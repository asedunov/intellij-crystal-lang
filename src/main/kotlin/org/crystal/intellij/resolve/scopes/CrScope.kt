package org.crystal.intellij.resolve.scopes

import org.crystal.intellij.resolve.symbols.CrMacroSym
import org.crystal.intellij.resolve.symbols.CrSym

interface CrScope {
    fun getConstant(name: String, isRoot: Boolean = false): CrSym<*>? = null

    fun getOwnMacros(name: String): Collection<CrMacroSym> = emptyList()

    fun getMacros(name: String): Collection<CrMacroSym> = emptyList()
}