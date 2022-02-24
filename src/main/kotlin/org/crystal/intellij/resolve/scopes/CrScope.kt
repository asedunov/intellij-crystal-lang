package org.crystal.intellij.resolve.scopes

import org.crystal.intellij.resolve.symbols.CrSym

interface CrScope {
    fun getType(name: String, isRoot: Boolean = false): CrSym<*>?
}