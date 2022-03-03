package org.crystal.intellij.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import org.crystal.intellij.lexer.CR_ABSTRACT
import org.crystal.intellij.resolve.*

sealed interface CrDefinitionWithFqName : CrDefinition, CrVisibilityHolder {
    val fqName: FqName?
        get() {
            if (this is CrPathBasedDefinition && nameElement?.isGlobal == true) return localFqName

            val parentFqName = when (val parent = parentStubOrPsi()) {
                is CrFile -> null
                is CrBody -> (parent.parent as? CrDefinitionWithBody)?.fqName as? StableFqName
                else -> return null
            }
            val nameElement = nameElement ?: return null
            return when (nameElement) {
                is CrPathNameElement -> nameElement.getLocalFqName(parentFqName)
                is CrSimpleNameElement -> {
                    val isConstant = nameElement.kind == CrNameKind.CONSTANT
                    val name = name ?: NO_NAME
                    return if (isConstant) StableFqName(name, parentFqName) else MemberFqName(name, parentFqName)
                }
            }
        }

    val isLocal: Boolean
        get() {
            val p = parentStubOrPsi()
            return !(p is CrFile || p is CrBody)
        }

    override val visibility: CrVisibility?
        get() {
            val visibility = super.visibility
            if (visibility != null) return visibility
            return if (isLocal) null else CrVisibility.PUBLIC
        }

    val abstractModifier: PsiElement?
        get() = allChildren().firstOrNull { it.elementType == CR_ABSTRACT }

    val isAbstract: Boolean
        get() = abstractModifier != null
}