package org.crystal.intellij.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import org.crystal.intellij.lexer.CR_ABSTRACT
import org.crystal.intellij.resolve.*

sealed interface CrDefinitionWithFqName : CrDefinition, CrVisibilityHolder {
    val fqName: FqName?
        get() {
            return when (val nameElement = nameElement) {
                is CrPathNameElement -> nameElement.getFqName(::parentFqName)
                is CrSimpleNameElement -> {
                    val isConstant = nameElement.kind == CrNameKind.CONSTANT
                    val name = name ?: NO_NAME
                    return if (isConstant) StableFqName(name, parentFqName()) else MemberFqName(name, parentFqName())
                }
                else -> null
            }
        }

    private fun parentFqName() = when (val parent = parentStubOrPsi()) {
        is CrDefinitionWithBody -> parent.fqName as? StableFqName
        is CrBody -> (parent.parent as? CrDefinitionWithBody)?.fqName as? StableFqName
        else -> null
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