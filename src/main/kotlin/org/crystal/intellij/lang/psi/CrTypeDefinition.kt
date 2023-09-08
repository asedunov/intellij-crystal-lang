package org.crystal.intellij.lang.psi

import org.crystal.intellij.lang.resolve.FqName
import org.crystal.intellij.lang.resolve.symbols.CrConstantLikeSym

sealed interface CrTypeDefinition : CrDefinitionWithFqName, CrPathNameElementHolder, CrConstantSource {
    override val fqName: FqName?
        get() = super.fqName

    override fun resolveSymbol(): CrConstantLikeSym<*>? = nameElement?.resolveSymbol()
}