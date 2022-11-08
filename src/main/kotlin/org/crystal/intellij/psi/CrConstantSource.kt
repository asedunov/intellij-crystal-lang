package org.crystal.intellij.psi

import org.crystal.intellij.resolve.FqName
import org.crystal.intellij.resolve.symbols.CrConstantLikeSym

sealed interface CrConstantSource : CrSymbolOrdinalHolder {
    val fqName: FqName?

    fun resolveSymbol(): CrConstantLikeSym<*>?
}