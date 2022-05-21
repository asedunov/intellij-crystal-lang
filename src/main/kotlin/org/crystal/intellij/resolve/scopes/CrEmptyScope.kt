package org.crystal.intellij.resolve.scopes

import org.crystal.intellij.resolve.symbols.CrSym

object CrEmptyScope : CrScope {
    override fun getType(name: String, isRoot: Boolean): CrSym<*>? = null
}