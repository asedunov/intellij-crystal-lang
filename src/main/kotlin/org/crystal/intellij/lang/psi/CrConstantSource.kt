package org.crystal.intellij.lang.psi

import org.crystal.intellij.lang.resolve.FqName
import org.crystal.intellij.lang.resolve.symbols.CrConstantLikeSym

sealed interface CrConstantSource : CrSymbolOrdinalHolder {
    val fqName: FqName?

    fun resolveSymbol(): CrConstantLikeSym<*>?
}