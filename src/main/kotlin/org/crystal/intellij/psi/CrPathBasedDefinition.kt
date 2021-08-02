package org.crystal.intellij.psi

import org.crystal.intellij.resolve.StableFqName

sealed interface CrPathBasedDefinition : CrDefinitionWithFqName, CrPathNameElementHolder {
    val localFqName: StableFqName?
        get() = nameElement?.localFqName
}