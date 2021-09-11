package org.crystal.intellij.psi

import org.crystal.intellij.lexer.CR_CONSTANT
import org.crystal.intellij.resolve.*

sealed interface CrDefinitionWithFqName : CrDefinition {
    val fqName: FqName?
        get() {
            if (this is CrPathBasedDefinition && nameElement?.isGlobal == true) return localFqName

            val parentFqName = when (val parent = getParentSkipModifiers()) {
                is CrFile -> null
                is CrBody -> (parent.parent as? CrBodyHolder)?.fqName as? StableFqName
                else -> return null
            }
            val nameElement = nameElement ?: return null
            return when (nameElement) {
                is CrPathNameElement -> nameElement.getLocalFqName(parentFqName)
                is CrSimpleNameElement -> {
                    val isConstant = nameElement.tokenType == CR_CONSTANT
                    val name = name ?: NO_NAME
                    return if (isConstant) StableFqName(name, parentFqName) else MemberFqName(name, parentFqName)
                }
            }
        }
}