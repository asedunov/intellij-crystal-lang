package org.crystal.intellij.resolve.scopes

import org.crystal.intellij.resolve.symbols.CrMethodSym
import org.crystal.intellij.resolve.symbols.CrSym

class CrMethodScope(
    val symbol: CrMethodSym
) : CrScope {
    override fun getType(name: String, isRoot: Boolean): CrSym<*>? {
        return symbol.getTypeParameter(name) ?: symbol.namespace.memberScope.getType(name, isRoot)
    }
}