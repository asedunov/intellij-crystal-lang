package org.crystal.intellij.lang.psi

import org.crystal.intellij.lang.resolve.symbols.CrSym

sealed interface CrReferenceElement : CrElement {
    fun resolveSymbol(): CrSym<*>?

    fun resolveCandidates(): List<CrSym<*>> = listOfNotNull(resolveSymbol())
}