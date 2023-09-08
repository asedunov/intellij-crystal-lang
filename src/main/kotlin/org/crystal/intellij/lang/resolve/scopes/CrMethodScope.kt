package org.crystal.intellij.lang.resolve.scopes

import org.crystal.intellij.lang.resolve.symbols.CrConstantLikeSym
import org.crystal.intellij.lang.resolve.symbols.CrMethodSym

class CrMethodScope(
    val symbol: CrMethodSym
) : CrScope {
    override fun getAllConstants(isRoot: Boolean) = symbol.typeParameters.asSequence()

    override fun getConstant(name: String, isRoot: Boolean): CrConstantLikeSym<*>? {
        return symbol.getTypeParameter(name) ?: symbol.namespace.memberScope.getConstant(name, isRoot)
    }
}