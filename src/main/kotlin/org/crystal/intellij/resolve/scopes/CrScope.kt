package org.crystal.intellij.resolve.scopes

import org.crystal.intellij.resolve.symbols.CrSym

interface CrScope {
    fun getConstant(name: String, isRoot: Boolean = false): CrSym<*>?
}