package org.crystal.intellij.psi

import org.crystal.intellij.resolve.symbols.CrSym

sealed interface CrTypeDefinition : CrDefinitionWithFqName, CrPathNameElementHolder, CrTypeSource {
    fun resolveSymbol(): CrSym<*>? = nameElement?.resolveSymbol()
}