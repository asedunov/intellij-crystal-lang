package org.crystal.intellij.psi

import org.crystal.intellij.resolve.StableFqName

sealed interface CrPathBasedDefinition : CrDefinitionWithFqName, CrPathNameElementHolder {
    override val nameElement: CrPathNameElement?
        get() = childOfType()

    val localFqName: StableFqName?
        get() = nameElement?.localFqName
}