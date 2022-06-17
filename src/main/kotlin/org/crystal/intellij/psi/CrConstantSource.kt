package org.crystal.intellij.psi

import org.crystal.intellij.resolve.FqName
import org.crystal.intellij.resolve.symbols.CrSym

sealed interface CrConstantSource : CrSymbolOrdinalHolder {
    val fqName: FqName?

    fun resolveSymbol(): CrSym<*>?
}