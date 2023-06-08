package org.crystal.intellij.psi

import org.crystal.intellij.resolve.symbols.CrSym

sealed interface CrReferenceElement : CrElement {
    fun resolveSymbol(): CrSym<*>?

    fun resolveCandidates(): List<CrSym<*>> = listOfNotNull(resolveSymbol())
}