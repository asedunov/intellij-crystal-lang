package org.crystal.intellij.psi

import org.crystal.intellij.resolve.FqName
import org.crystal.intellij.resolve.symbols.CrSym

sealed interface CrTypeDefinition : CrDefinitionWithFqName, CrPathNameElementHolder, CrConstantSource {
    override val fqName: FqName?
        get() = super.fqName

    override fun resolveSymbol(): CrSym<*>? = nameElement?.resolveSymbol()
}