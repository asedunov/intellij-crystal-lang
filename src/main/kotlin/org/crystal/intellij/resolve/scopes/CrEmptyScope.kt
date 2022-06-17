package org.crystal.intellij.resolve.scopes

import org.crystal.intellij.resolve.symbols.CrSym

object CrEmptyScope : CrScope {
    override fun getConstant(name: String, isRoot: Boolean): CrSym<*>? = null
}